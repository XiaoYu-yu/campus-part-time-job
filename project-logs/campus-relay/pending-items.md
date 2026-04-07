# 校园代送待处理事项

## Step 03F 最高优先级

1. 为 admin 新增最小售后处理能力，避免 `AFTER_SALE_OPEN` 长期停留
2. 为 courier 新增最小异常上报入口
3. 为 courier 新增低频位置上报接口，并复用现有 `campus_location_report`
4. 为 admin 新增结算分页查询能力，便于查看 `campus_settlement_record`
5. 评估并决定 `courier/profile` 与 `courier/review-status` 的双 token bridge 何时收口

## 已完成但仍需继续扩展的部分

- `campus_*` 数据表已建，初始化数据可用于 H2 和本地联调
- `CampusRelayOrderService` 已支持：
  - admin 订单分页/详情
  - admin 订单时间线
  - customer 创建单/模拟支付/详情/列表/确认送达/取消/售后
  - courier 可接单列表/详情/接单/取餐/配送推进
- `CampusPublicController` 已开放 `pickup-points` 和 `delivery-rules`
- `CampusAdminRelayOrderController` 已支持分页、详情与时间线，但还没有异常备注和售后处理能力
- `CampusAdminCourierController` 已支持分页与审核，但还没有详情页或批量审核能力
- `CampusCourierProfileController` 已开放资料提交、资料查看、审核状态查看，并继续通过 bridge 方案接入现有 `user` token
- `CampusCourierAuthController` 已开放正式 courier token 发行入口，但仅允许已审核通过且启用的 courier 获取 token
- campus 订单在 customer confirm 进入 `COMPLETED` 时，已会自动生成或更新 `campus_settlement_record`

## 已锁定的默认处理策略

### 1. 表命名与包命名

- 继续使用 `campus_` 表前缀
- 继续使用 `com.cangqiong.takeaway.campus` 包根
- 继续避免直接改旧外卖实体命名

### 2. 账号体系

- 普通用户继续复用 `user`
- 校园普通用户资料继续放在 `campus_customer_profile`
- 兼职配送员资料继续放在 `campus_courier_profile`
- 后台管理员继续复用 `employee`

### 3. 鉴权策略

- `/api/campus/public/**` 继续公开访问
- `/api/campus/admin/**` 继续只允许 employee token
- `/api/campus/customer/**` 继续只允许 customer token
- `/api/campus/courier/auth/token` 公开访问，但 service 层继续校验 `APPROVED + enabled = 1`
- `/api/campus/courier/orders/**` 继续只允许 courier token
- `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 当前仍允许 `customer/courier` 双通道桥接

### 4. 上传与附件

- 认证照片、课程表、取餐凭证继续复用现有上传和受控文件读取链路
- 字段内只保存受控路径或文件引用
- 继续不新增第二套文件服务

### 5. 支付与金额

- 继续只支持模拟支付
- 金额字段继续使用 `BigDecimal` 并统一保留两位小数
- 打赏金额继续限制在 `1` 到 `10` 元之间

### 6. 审核状态流转

- courier 提交或重提资料时统一回到 `PENDING`
- admin 审核只允许推进到 `APPROVED / REJECTED / DISABLED`
- admin 审核接口不允许直接把状态设置为 `PENDING`

### 7. 接单与履约状态流转

- 已支付非宿舍单进入 `WAITING_ACCEPT`
- 已支付宿舍单进入 `BUILDING_PRIORITY_PENDING`
- 接单成功统一推进到 `ACCEPTED`
- 取餐成功推进到 `PICKED_UP`
- 配送推进接口按顺序推进：
  - `PICKED_UP -> DELIVERING`
  - `DELIVERING -> AWAITING_CONFIRMATION`
- customer 确认送达后推进到 `COMPLETED`
- customer 在未取餐前可推进到 `CANCELLED`
- customer 在 `AWAITING_CONFIRMATION / COMPLETED` 可发起售后并推进到 `AFTER_SALE_OPEN`
- 已取餐后 customer 仍不允许直接取消
- 订单进入 `COMPLETED` 后自动插入或更新待结算记录

## 当前阻塞点

### 1. courier onboarding 仍存在 bridge 过渡态

- 影响：`profile/review-status` 当前既能用 customer token 也能用 courier token
- 默认处理：继续保留到前端与 onboarding 入口稳定，再统一收口

### 2. admin 还没有售后处理和异常备注

- 影响：customer 发起售后后只能进入 `AFTER_SALE_OPEN`，后台仍缺少最小处理动作
- 默认处理：Step 03F 优先补 admin 售后处理或异常备注

### 3. courier 位置、异常反馈还没开

- 影响：最小履约闭环已完成，但异常态和运营态仍缺少基础支撑
- 默认处理：Step 03F 优先补低频位置上报和异常上报

### 4. rules 仍是代码常量

- 影响：校园规则变更需要改代码
- 默认处理：先保留在 `CampusRuleCatalog`，后续再结构化到后台配置

### 5. 前端还没接 campus 接口

- 影响：当前 campus 新接口主要通过测试或后端联调演示
- 默认处理：继续保持前端不动，避免范围膨胀

### 6. 结算只有自动生成，还没有 admin 查询和处理

- 影响：`campus_settlement_record` 已能自动生成，但后台仍无法分页查看或结算确认
- 默认处理：Step 03F 或 Step 04 补 admin settlement 读接口

## 当前明确没做的事情

- 没有改 `frontend/`
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有接第三方支付
- 没有实现 customer 退款、完整售后处理结果
- 没有实现 courier 位置上报和异常反馈
- 没有新增第二套返回体
- 没有引入 Flyway、Liquibase 等新迁移工具

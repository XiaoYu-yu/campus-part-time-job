# 校园代送待处理事项

## Step 03D 最高优先级

1. 为 courier 新增取餐接口
2. 为 courier 新增取餐凭证上传与落库
3. 为 courier 新增配送中/送达状态推进接口
4. 为 customer 新增确认送达接口或最小确认链路
5. 为 customer 预留取消/售后边界，但仍先不开放完整售后

## 已完成但仍需继续扩展的部分

- `campus_*` 数据表已建，初始化数据可用于 H2 和本地联调
- `CampusRelayOrderService` 已支持 admin 分页/详情、customer 创建单/模拟支付/详情/列表、courier 可接单列表/详情/接单，但还没有 courier 取餐和 customer 取消
- `CampusPublicController` 已开放 `pickup-points` 和 `delivery-rules`
- `CampusAdminRelayOrderController` 已支持分页与详情，但还没有更多人工干预接口
- `CampusAdminCourierController` 已支持分页与审核，但还没有详情页或批量审核能力
- `CampusCourierProfileController` 已开放资料提交、资料查看、审核状态查看，并已通过 bridge 方案接入现有 `user` token
- `CampusCourierAuthController` 已开放正式 courier token 发行入口，但仅允许已审核通过且启用的 courier 获取 token

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
- `/api/campus/customer/**` 已按 customer token 生效
- `/api/campus/courier/auth/token` 公开访问，但会在 service 层校验 `APPROVED + enabled = 1`
- `/api/campus/courier/orders/**` 只允许 courier token
- `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 当前允许 `customer/courier` 双通道桥接，后续再决定是否收口

### 4. 上传与附件

- 认证照片、课程表、取餐凭证继续复用现有上传和受控文件读取链路
- 字段内只保存受控路径或文件引用
- Step 03D 也不新增第二套文件服务

### 5. 支付与金额

- 继续只支持模拟支付
- 金额字段继续使用 `BigDecimal` 并统一保留两位小数
- 打赏金额继续限制在 `1` 到 `10` 元之间

### 6. 审核状态流转

- courier 提交或重提资料时统一回到 `PENDING`
- admin 审核只允许推进到 `APPROVED / REJECTED / DISABLED`
- admin 审核接口不允许直接把状态设置为 `PENDING`

### 7. 接单状态流转

- 已支付非宿舍单进入 `WAITING_ACCEPT`
- 已支付宿舍单进入 `BUILDING_PRIORITY_PENDING`
- 接单成功统一推进到 `ACCEPTED`
- 同一订单只允许一个 courier 接单，依赖条件更新保证并发安全

## 当前阻塞点

### 1. courier 取餐后的状态链路还没开

- 影响：当前只到 `ACCEPTED`，无法继续进入配送闭环
- 默认处理：Step 03D 先补取餐与取餐凭证

### 2. courier onboarding 仍存在 bridge 过渡态

- 影响：`profile/review-status` 当前既能用 customer token 也能用 courier token，规则尚未最终收口
- 默认处理：等 Step 03D 完成后再决定是否将 profile 链路独立到 customer 侧或保留 bridge

### 3. customer profile 与注册流程还没接起来

- 影响：customer 创建代送单依赖 `campus_customer_profile` 已存在，缺少资料补录入口
- 默认处理：短期先沿用初始化数据和数据库直写资料；后续再决定是否给普通用户开放资料补录接口

### 4. rules 仍是代码常量

- 影响：校园规则变更需要改代码
- 默认处理：先保留在 `CampusRuleCatalog`，等后台配置需求稳定再表结构化

### 5. 前端还没接 campus 接口

- 影响：当前 campus 新接口只能通过测试或后端联调看到
- 默认处理：继续保持前端不动，避免 Step 03 范围膨胀

### 6. customer 订单还缺取消与详情扩展

- 影响：当前只能创建、模拟支付、查详情、查列表，无法完成更多用户态操作
- 默认处理：优先完成 courier 取餐/配送链路，再补 customer 取消与售后

### 7. H2 与 MySQL 中文测试数据在接口断言中不稳定

- 影响：数据库种子里的中文字段在 MockMvc/H2 链路下不适合作为强断言主字段
- 默认处理：继续以订单号、编码、状态、手机号、金额、文件路径等稳定字段为主做回归断言

## 当前明确没做的事情

- 没有改 `frontend/`
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有实现 courier 取餐、配送中、送达、完成等写接口
- 没有实现 customer 取消、售后、退款等接口
- 没有新增第二套返回体
- 没有引入 Flyway、Liquibase 等新迁移工具

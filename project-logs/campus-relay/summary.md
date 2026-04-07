# 校园代送改造总览

## 当前轮次

- 当前已完成：`Step 01 - 领域模型重构规划`
- 当前已完成：`Step 02A - 数据库与后端骨架`
- 当前已完成：`Step 02B - 只读接口完善`
- 当前已完成：`Step 03A - customer 下单与模拟支付`
- 当前已完成：`Step 03B - courier 资料链路与 admin 最小审核/详情`
- 当前已完成：`Step 03C - courier token 与最小接单链路`
- 当前日期：`2026-04-07`
- 当前范围：在 Step 03B 基础上，补齐 courier token 发行、可接订单列表、订单详情和最小接单能力

## 当前状态

### 后端

- 现有外卖后端仍保留原有 `category`、`dish`、`setmeal`、`cart`、`address`、`orders` 等链路
- 已并行新增 `com.cangqiong.takeaway.campus` 包结构
- 已并行新增 `campus_customer_profile`、`campus_courier_profile`、`campus_pickup_point`、`campus_relay_order`、`campus_location_report`、`campus_settlement_record`
- 已开放 campus 最小接口：
  - `/api/campus/public/pickup-points`
  - `/api/campus/public/delivery-rules`
  - `/api/campus/admin/orders`
  - `/api/campus/admin/orders/{id}`
  - `/api/campus/admin/couriers`
  - `/api/campus/admin/couriers/{id}/review`
  - `/api/campus/customer/orders`
  - `/api/campus/customer/orders/{id}`
  - `/api/campus/customer/orders/{id}/mock-pay`
  - `/api/campus/courier/auth/token`
  - `/api/campus/courier/profile`
  - `/api/campus/courier/review-status`
  - `/api/campus/courier/orders/available`
  - `/api/campus/courier/orders/{id}`
  - `/api/campus/courier/orders/{id}/accept`
- customer 侧已具备：
  - 普通用户复用旧 `user` 身份登录
  - 校园资料继续读取 `campus_customer_profile`
  - 创建代送单、模拟支付、订单详情、订单列表
- courier 侧已具备：
  - 基于旧 `user` 账号的 courier token 发行入口
  - 资料提交、资料详情查看、审核状态查看
  - onboarding 过渡桥接：资料接口允许 `customer` 或 `courier` token
  - 已审核通过且启用的 courier 才能进入接单链路
  - 可接订单列表
  - courier 订单详情
  - 最小接单
  - 提交/重新提交时统一回到 `PENDING`
- admin 侧已具备：
  - campus 订单分页
  - campus 订单详情
  - campus 配送员分页
  - campus 配送员审核
- 仍未实现 courier 取餐、上传取餐凭证、配送中、送达、完成、售后、结算等写接口

### Web 管理端

- `frontend/` 本轮没有改动
- 现有后台菜单和页面仍是外卖语义
- campus 后台接口可供后续挂接，但本轮仍未接入页面

### Web 用户端

- `frontend/src/views/user/*` 本轮没有改动
- 用户 Web 仍是旧点餐、购物车、地址、结算链路
- campus customer/courier 接口已可通过后端联调或测试演示，但尚未接入页面

### 移动端

- 当前仓库仍没有 `uni-app/`
- 本轮仍未创建移动端工程

## Step 02A 实际完成事项

1. 新增 campus 迁移脚本 `backend/db/migrations/V2__campus_relay_schema.sql`
2. 扩展 `backend/db/init.sql`
3. 扩展 H2 `schema-h2.sql` 与 `data-h2.sql`
4. 更新 dev/test/prod MyBatis type aliases 配置，纳入 campus 实体包
5. 更新 `JwtInterceptor`，放行 `/api/campus/public/**`，并将 `/api/campus/admin/**` 固定为 employee 权限
6. 新增 `com.cangqiong.takeaway.campus` 下的 entity、enums、support、query、vo、mapper、service、service/impl、controller 骨架
7. 实现最小只读 public/admin Controller
8. 新增 `CampusSkeletonIntegrationTest`
9. 执行 `mvnw.cmd -DskipTests compile` 和 `mvnw.cmd test`，全部通过

## Step 02B 实际完成事项

1. 扩展 `CampusDeliveryRuleVO` 与 `CampusRuleCatalog`，将规则文案、楼栋清单、费用区间、优先窗口、位置刷新频率集中化
2. 扩展 `CampusRelayOrderQuery` 和 `CampusCourierQuery`，补充只读分页筛选字段
3. 重写 `CampusRelayOrderMapper`、`CampusCourierProfileMapper` 的查询 SQL，补齐关联字段和分页统计
4. 完善 `CampusRelayOrderServiceImpl`、`CampusCourierProfileServiceImpl` 的查询参数归一化和 `size/pageSize` 兼容
5. 为 `CampusPublicController`、`CampusAdminRelayOrderController`、`CampusAdminCourierController` 增加明确日志
6. 扩展 `CampusSkeletonIntegrationTest`，覆盖公开接口结构化返回、admin 鉴权、admin 分页筛选与分页元数据
7. 执行 `.\mvnw.cmd test`，`14` 个测试全部通过

## Step 03A 实际完成事项

1. 新增 `CampusCustomerOrderCreateDTO`、`CampusCustomerOrderQuery`、`CampusCustomerOrderVO`
2. 新增 `CampusCustomerOrderController`，开放 4 个 customer 侧接口：
   - `POST /api/campus/customer/orders`
   - `POST /api/campus/customer/orders/{id}/mock-pay`
   - `GET /api/campus/customer/orders/{id}`
   - `GET /api/campus/customer/orders`
3. 扩展 `CampusRelayOrderService`、`CampusRelayOrderMapper`、`CampusRelayOrderServiceImpl`，补齐 customer 侧创建单、模拟支付、详情、列表能力
4. 创建单时完成最小校验：
   - 校验 `campus_customer_profile` 是否存在
   - 校验取餐点必须存在且启用
   - 校验目标类型与楼栋范围
   - 校验打赏金额范围、加急标记合法性
   - 金额统一按两位小数处理，继续使用 `BigDecimal`
5. 模拟支付时完成最小状态流转：
   - `UNPAID + PENDING_PAYMENT`
   - 宿舍单支付后进入 `PAID + BUILDING_PRIORITY_PENDING`
   - 非宿舍单支付后进入 `PAID + WAITING_ACCEPT`
6. 将“已取餐后不可直接取消”的规则提前落在 service 层校验和注释中，为后续取消接口预留约束
7. 新增 `CampusCustomerOrderIntegrationTest`
8. 执行 `.\mvnw.cmd test`，`18` 个测试全部通过

## Step 03B 实际完成事项

1. 新增 `CampusCourierProfileSubmitDTO`、`CampusCourierReviewDTO`、`CampusCourierReviewStatusVO`
2. 新增 `CampusCourierProfileController`，开放：
   - `POST /api/campus/courier/profile`
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
3. 扩展 `CampusCourierProfileMapper`、`CampusCourierProfileService`、`CampusCourierProfileServiceImpl`
4. 新增 `admin` 最小审核与详情接口：
   - `GET /api/campus/admin/orders/{id}`
   - `POST /api/campus/admin/couriers/{id}/review`
5. courier 资料提交/重提时统一重置为：
   - `reviewStatus = PENDING`
   - `enabled = 0`
   - 清空审核人和审核时间
6. admin 审核时集中在 service 层推进状态：
   - `APPROVED -> enabled = 1`
   - `REJECTED -> enabled = 0`
   - `DISABLED -> enabled = 0`
7. 文件字段继续只保存 `/api/files/*` 受控路径，不引入第二套文件方案
8. 新增 `CampusCourierProfileIntegrationTest`
9. 执行 `.\mvnw.cmd test`，`23` 个测试全部通过

## Step 03C 实际完成事项

1. 新增 `CampusCourierAuthController`，开放 `POST /api/campus/courier/auth/token`
2. 新增 `CampusCourierOrderController`，开放：
   - `GET /api/campus/courier/orders/available`
   - `GET /api/campus/courier/orders/{id}`
   - `POST /api/campus/courier/orders/{id}/accept`
3. 新增 `CampusCourierAvailableOrderQuery`、`CampusCourierOrderVO`
4. 扩展 `CampusRelayOrderService`、`CampusCourierProfileService`
5. 扩展 `CampusRelayOrderMapper`、`CampusCourierProfileMapper`
6. 扩展 `CampusRelayOrderServiceImpl`、`CampusCourierProfileServiceImpl`
7. 更新 `JwtInterceptor`：
   - 放行 `/api/campus/courier/auth/token`
   - `courier/orders/**` 继续只允许 `courier` token
   - `courier/profile`、`courier/review-status` 支持 `customer/courier` 双通道桥接
8. 将可接单资格、宿舍优先窗口判断、重复接单判断集中到 service 层
9. 接单写入采用条件更新，避免同一订单被重复接单
10. 新增 `CampusCourierAcceptIntegrationTest`，并调整 `CampusCourierProfileIntegrationTest` 走真实 token/桥接链路
11. 执行 `.\mvnw.cmd test`，`28` 个测试全部通过

## 当前锁定的技术事实

1. 仓库后端真实风格是注解式 MyBatis，不使用 XML Mapper
2. H2 测试环境真实依赖 `schema-h2.sql` 和 `data-h2.sql`，不能只改 MySQL 初始化脚本
3. `employee` 继续承担后台管理员登录和 `/api/campus/admin/**` 权限入口
4. `user` 继续作为普通用户基础身份，校园普通用户资料通过 `campus_customer_profile` 承载
5. customer 下单阶段继续只允许模拟支付，不接第三方支付
6. courier 资料附件字段继续保存受控文件路径，不引入新文件系统方案
7. courier token 继续复用现有 `user` 账号密码校验，token 内部只新增 `userType=courier` 声明，不新建第二套 courier 账号表
8. `courier/profile` 与 `courier/review-status` 当前采用 `customer/courier` 双通道桥接，避免未审核通过用户无法完成资料提交
9. 本轮没有引入新迁移工具，仍沿用 `init.sql + migrations + H2 schema/data`

## 当前风险与未完成项

- `courier/profile` 和 `courier/review-status` 当前仍是 bridge 方案，后续是否保留双 token 通道需要在 Step 03D 之后收口
- courier 已有最小接单，但仍没有取餐、上传取餐凭证、配送中、送达、完成接口
- customer 订单当前只有创建、模拟支付、详情、列表，没有取消、修改、售后接口
- admin 当前只有订单分页/详情和 courier 分页/审核，没有更多人工干预能力
- campus 规则文本目前集中在 `CampusRuleCatalog`，后续如果学校规则变更，仍需要后台配置化
- `frontend/` 还未隐藏旧菜单，也未接入 campus 新接口

## 下一轮建议

- 进入 `Step 03D`
- 重点补 courier 取餐、上传取餐凭证与订单状态继续流转
- 推荐顺序：
  1. `POST /api/campus/courier/orders/{id}/pickup`
  2. 取餐凭证上传与受控文件引用落库
  3. `POST /api/campus/courier/orders/{id}/deliver`
  4. `POST /api/campus/customer/orders/{id}/confirm`
  5. customer 取消/售后边界设计
- 保持 `frontend/` 继续不动，先把 courier 端状态闭环继续向前推进

## 日志索引

- [领域模型重构规划](../../docs/campus-relay/domain-refactor-plan.md)
- [旧模块到校园代送模块映射](../../docs/campus-relay/legacy-to-campus-mapping.md)
- [Step 01 日志](step-01-domain-model-planning.md)
- [Step 02A 日志](step-02a-db-and-backend-skeleton.md)
- [Step 02B 日志](step-02b-readonly-api.md)
- [Step 03A 日志](step-03a-customer-order-create-and-pay.md)
- [Step 03B 日志](step-03b-courier-profile-and-admin-review.md)
- [Step 03C 日志](step-03c-courier-token-and-accept.md)
- [待处理事项](pending-items.md)
- [文件改动清单](file-change-list.md)

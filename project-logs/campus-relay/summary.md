# 校园代送改造总览

## 当前轮次

- 当前已完成：`Step 01 - 领域模型重构规划`
- 当前已完成：`Step 02A - 数据库与后端骨架`
- 当前已完成：`Step 02B - 只读接口完善`
- 当前已完成：`Step 03A - customer 下单与模拟支付`
- 当前已完成：`Step 03B - courier 资料链路与 admin 最小审核/详情`
- 当前已完成：`Step 03C - courier token 与最小接单链路`
- 当前已完成：`Step 03D - courier 取餐 / 配送推进 / customer 确认送达`
- 当前日期：`2026-04-07`
- 当前范围：在不改 `frontend/`、不删旧外卖模块的前提下，已打通校园代送最小后端状态闭环

## 当前状态

### 后端

- 旧外卖后端仍保留 `category`、`dish`、`setmeal`、`cart`、`address`、`orders` 等链路
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
  - `/api/campus/customer/orders/{id}/confirm`
  - `/api/campus/courier/auth/token`
  - `/api/campus/courier/profile`
  - `/api/campus/courier/review-status`
  - `/api/campus/courier/orders/available`
  - `/api/campus/courier/orders/{id}`
  - `/api/campus/courier/orders/{id}/accept`
  - `/api/campus/courier/orders/{id}/pickup`
  - `/api/campus/courier/orders/{id}/deliver`
- customer 侧已具备：
  - 复用旧 `user` 身份登录
  - 校园资料继续读取 `campus_customer_profile`
  - 创建代送单、模拟支付、订单详情、订单列表、确认送达
- courier 侧已具备：
  - 基于旧 `user` 账号的 courier token 发行入口
  - 资料提交、资料详情查看、审核状态查看
  - onboarding 过渡桥接：资料接口允许 `customer` 或 `courier` token
  - 已审核通过且启用的 courier 才能进入接单链路
  - 可接订单列表、订单详情、最小接单
  - 取餐凭证受控路径校验、取餐、配送中推进、送达待确认
- admin 侧已具备：
  - campus 订单分页与详情
  - campus 配送员分页与审核
- 当前最小订单状态闭环已打通：
  - `PENDING_PAYMENT`
  - `BUILDING_PRIORITY_PENDING / WAITING_ACCEPT`
  - `ACCEPTED`
  - `PICKED_UP`
  - `DELIVERING`
  - `AWAITING_CONFIRMATION`
  - `COMPLETED`

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

## Step 01 实际完成事项

1. 完成校园代送领域规划文档和旧模块映射文档落档
2. 固定并行新增 `campus_*` 领域、不重建后端、不改前端的总体策略
3. 为后续 Step 02/03 建立日志索引和待办基线

## Step 02A 实际完成事项

1. 新增 campus 迁移脚本 `backend/db/migrations/V2__campus_relay_schema.sql`
2. 扩展 `backend/db/init.sql`
3. 扩展 H2 `schema-h2.sql` 与 `data-h2.sql`
4. 更新 dev/test/prod MyBatis type aliases 配置，纳入 campus 实体包
5. 更新 `JwtInterceptor`，放行 `/api/campus/public/**`，并将 `/api/campus/admin/**` 固定为 employee 权限
6. 新增 `com.cangqiong.takeaway.campus` 下的 entity、enums、support、query、vo、mapper、service、service/impl、controller 骨架
7. 实现最小只读 public/admin Controller
8. 新增 `CampusSkeletonIntegrationTest`
9. 执行 `.\mvnw.cmd test`，全部通过

## Step 02B 实际完成事项

1. 扩展 `CampusDeliveryRuleVO` 与 `CampusRuleCatalog`，集中规则文案、楼栋清单、费用区间、优先窗口和位置刷新策略
2. 扩展 `CampusRelayOrderQuery` 和 `CampusCourierQuery`
3. 完善 `CampusRelayOrderMapper`、`CampusCourierProfileMapper` 查询 SQL
4. 完善 `CampusRelayOrderServiceImpl`、`CampusCourierProfileServiceImpl` 的只读分页逻辑
5. 完善 public/admin 只读 controller
6. 扩展 `CampusSkeletonIntegrationTest`
7. 执行 `.\mvnw.cmd test`，累计 `14` 个测试通过

## Step 03A 实际完成事项

1. 新增 `CampusCustomerOrderCreateDTO`、`CampusCustomerOrderQuery`、`CampusCustomerOrderVO`
2. 新增 `CampusCustomerOrderController`
3. 实现 customer 创建单、模拟支付、详情、列表能力
4. 将金额统一按两位小数处理，继续复用 `BigDecimal`
5. 将“已取餐后不可直接取消”规则提前落到 service 层
6. 新增 `CampusCustomerOrderIntegrationTest`
7. 执行 `.\mvnw.cmd test`，累计 `18` 个测试通过

## Step 03B 实际完成事项

1. 新增 `CampusCourierProfileSubmitDTO`、`CampusCourierReviewDTO`、`CampusCourierReviewStatusVO`
2. 新增 `CampusCourierProfileController`
3. 实现 courier 资料提交、资料详情、审核状态查询
4. 实现 admin 订单详情与 courier 审核
5. courier 提交/重提时统一回到 `PENDING`
6. 文件字段继续只保存 `/api/files/*` 受控路径
7. 新增 `CampusCourierProfileIntegrationTest`
8. 执行 `.\mvnw.cmd test`，累计 `23` 个测试通过

## Step 03C 实际完成事项

1. 新增 `CampusCourierAuthController`
2. 新增 `CampusCourierOrderController`
3. 新增 `CampusCourierAvailableOrderQuery`、`CampusCourierOrderVO`
4. 实现 courier token 发行、可接单列表、订单详情、最小接单
5. 将宿舍优先窗口、资格判断和重复接单判断集中到 service 层
6. 接单写入采用条件更新，避免同一订单被重复接单
7. 新增 `CampusCourierAcceptIntegrationTest`
8. 执行 `.\mvnw.cmd test`，累计 `28` 个测试通过

## Step 03D 实际完成事项

1. 新增 `CampusCourierPickupDTO`、`CampusCourierDeliverDTO`
2. 扩展 `CampusRelayOrderService`、`CampusRelayOrderMapper`、`CampusRelayOrderServiceImpl`
3. 扩展 `CampusCourierOrderController`：
   - `POST /api/campus/courier/orders/{id}/pickup`
   - `POST /api/campus/courier/orders/{id}/deliver`
4. 扩展 `CampusCustomerOrderController`：
   - `POST /api/campus/customer/orders/{id}/confirm`
5. pickup 时新增受控文件路径校验：
   - 只允许保存 `/api/files/*`
   - 不允许写入本地磁盘绝对路径
6. pickup 成功后：
   - `orderStatus -> PICKED_UP`
   - 写入 `pickedUpAt`
   - 写入 `pickupProofImageUrl`
7. deliver 接口采用单路径双阶段推进：
   - `PICKED_UP -> DELIVERING`
   - `DELIVERING -> AWAITING_CONFIRMATION`
   - 第二次推进时写入 `deliveredAt`
8. customer confirm 只允许订单所属用户调用：
   - `AWAITING_CONFIRMATION -> COMPLETED`
   - 写入 `autoCompleteAt`
9. 继续把权限、状态流转、取餐凭证校验集中在 service 层
10. 扩展 customer/courier 订单 VO 和 mapper 字段投影，补齐 `pickupProofImageUrl`、`courierRemark`、`autoCompleteAt`
11. 新增 `CampusCourierDeliveryIntegrationTest`
12. 调整 `CampusSkeletonIntegrationTest` 的 admin 订单筛选断言，避免全量回归下的脆弱匹配
13. 执行 `.\mvnw.cmd test`，累计 `31` 个测试通过

## 当前锁定的技术事实

1. 仓库后端真实风格是注解式 MyBatis，不使用 XML Mapper
2. H2 测试环境真实依赖 `schema-h2.sql` 和 `data-h2.sql`
3. `employee` 继续承担后台管理员登录和 `/api/campus/admin/**` 权限入口
4. `user` 继续作为普通用户基础身份
5. customer 下单阶段继续只允许模拟支付，不接第三方支付
6. courier 资料附件和取餐凭证继续保存受控文件路径，不引入新文件系统方案
7. courier token 继续复用现有 `user` 账号密码校验，不新建第二套 courier 账号表
8. `courier/profile` 与 `courier/review-status` 当前仍采用 `customer/courier` 双通道桥接
9. 本轮没有引入新迁移工具，仍沿用 `init.sql + migrations + H2 schema/data`

## 当前风险与未完成项

- `courier/profile` 和 `courier/review-status` 仍是 bridge 方案，后续是否收口需要明确
- customer 仍没有取消、退款、售后接口
- courier 仍没有异常上报、位置上报、结算联动
- admin 仍没有订单异常处理、强制改派、售后处理能力
- `CampusRuleCatalog` 仍是代码常量，后续若学校规则变更仍需配置化
- `frontend/` 尚未隐藏旧菜单，也未接入 campus 新接口

## 下一轮建议

- 进入 `Step 03E`
- 重点补 customer 取消/售后边界、admin 异常处理与结算准备
- 推荐顺序：
  1. `POST /api/campus/customer/orders/{id}/cancel`
  2. `POST /api/campus/customer/orders/{id}/after-sale`
  3. `GET /api/campus/admin/orders/{id}/timeline` 或等价详情扩展
  4. campus 订单完成后自动或半自动生成 `campus_settlement_record`
  5. 评估是否收口 `courier/profile` 的双 token bridge
- 保持 `frontend/` 继续不动，先把后端异常链路和结算链路补稳

## 日志索引

- [领域模型重构规划](../../docs/campus-relay/domain-refactor-plan.md)
- [旧模块到校园代送模块映射](../../docs/campus-relay/legacy-to-campus-mapping.md)
- [Step 01 日志](step-01-domain-model-planning.md)
- [Step 02A 日志](step-02a-db-and-backend-skeleton.md)
- [Step 02B 日志](step-02b-readonly-api.md)
- [Step 03A 日志](step-03a-customer-order-create-and-pay.md)
- [Step 03B 日志](step-03b-courier-profile-and-admin-review.md)
- [Step 03C 日志](step-03c-courier-token-and-accept.md)
- [Step 03D 日志](step-03d-pickup-deliver-confirm.md)
- [待处理事项](pending-items.md)
- [文件改动清单](file-change-list.md)

# 校园代送改造总览

## 当前轮次

- 当前已完成：`Step 01 - 领域模型重构规划`
- 当前已完成：`Step 02A - 数据库与后端骨架`
- 当前已完成：`Step 02B - 只读接口完善`
- 当前已完成：`Step 03A - customer 下单与模拟支付`
- 当前已完成：`Step 03B - courier 资料链路与 admin 最小审核/详情`
- 当前已完成：`Step 03C - courier token 与最小接单链路`
- 当前已完成：`Step 03D - courier 取餐 / 配送推进 / customer 确认送达`
- 当前已完成：`Step 03E - customer 取消 / 售后 / admin 时间线 / 结算联动`
- 当前已完成：`Step 03F - admin 售后处理 / courier 异常上报 / courier 位置上报 / admin 结算分页`
- 当前已完成：`Step 04 - 售后决策 / 结算确认 / admin 运营只读接口`
- 当前已完成：`Step 05 - 售后执行记录 / 结算打款运营 / 订单级运营查询`
- 当前日期：`2026-04-08`
- 当前范围：后端最小闭环已扩展到运营记录层，`frontend/` 仍未改动，旧外卖模块仍保留可运行

## 当前状态

### 数据与领域

- 已并行新增表：
  - `campus_customer_profile`
  - `campus_courier_profile`
  - `campus_pickup_point`
  - `campus_relay_order`
  - `campus_location_report`
  - `campus_settlement_record`
- 普通用户继续复用 `user`
- 管理员继续复用 `employee`
- 配送员继续通过 `campus_courier_profile.user_id` 关联 `user`
- 旧外卖表与旧控制器未删除，旧 `orders/cart/address` 语义未改

### 当前已开放的 campus 接口

- public：
  - `/api/campus/public/pickup-points`
  - `/api/campus/public/delivery-rules`
- customer：
  - `/api/campus/customer/orders`
  - `/api/campus/customer/orders/{id}`
  - `/api/campus/customer/orders/{id}/mock-pay`
  - `/api/campus/customer/orders/{id}/confirm`
  - `/api/campus/customer/orders/{id}/cancel`
  - `/api/campus/customer/orders/{id}/after-sale`
- courier：
  - `/api/campus/courier/auth/token`
  - `/api/campus/courier/profile`
  - `/api/campus/courier/review-status`
  - `/api/campus/courier/orders/available`
  - `/api/campus/courier/orders/{id}`
  - `/api/campus/courier/orders/{id}/accept`
  - `/api/campus/courier/orders/{id}/pickup`
  - `/api/campus/courier/orders/{id}/deliver`
  - `/api/campus/courier/orders/{id}/exception-report`
  - `/api/campus/courier/location-reports`
- admin：
  - `/api/campus/admin/orders`
  - `/api/campus/admin/orders/{id}`
  - `/api/campus/admin/orders/{id}/timeline`
  - `/api/campus/admin/orders/after-sale`
  - `/api/campus/admin/orders/{id}/after-sale-handle`
  - `/api/campus/admin/orders/{id}/after-sale-decision`
  - `/api/campus/admin/orders/{id}/after-sale-result`
  - `/api/campus/admin/orders/{id}/after-sale-execution`
  - `/api/campus/admin/orders/{id}/location-reports`
  - `/api/campus/admin/orders/{id}/exception-summary`
  - `/api/campus/admin/couriers`
  - `/api/campus/admin/couriers/{id}/review`
  - `/api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
  - `/api/campus/admin/couriers/{courierProfileId}/location-reports`
  - `/api/campus/admin/settlements`
  - `/api/campus/admin/settlements/{id}`
  - `/api/campus/admin/settlements/{id}/confirm`
  - `/api/campus/admin/settlements/{id}/payout-result`
  - `/api/campus/admin/settlements/batch-payout-result`
  - `/api/campus/admin/settlements/reconcile-summary`

### 状态与运营能力

- 订单主状态已覆盖：
  - `PENDING_PAYMENT`
  - `BUILDING_PRIORITY_PENDING`
  - `WAITING_ACCEPT`
  - `ACCEPTED`
  - `PICKED_UP`
  - `DELIVERING`
  - `AWAITING_CONFIRMATION`
  - `COMPLETED`
  - `CANCELLED`
  - `AFTER_SALE_OPEN`
  - `AFTER_SALE_RESOLVED`
  - `AFTER_SALE_REJECTED`
- `after_sale_execution_status` 独立于 `order_status`
  - `PENDING`
  - `SUCCESS`
  - `FAILED`
  - `NOT_REQUIRED`
- `payout_status` 独立于 `settlement_status`
  - `UNPAID`
  - `PAID`
  - `FAILED`
- `decisionType = REFUND / COMPENSATE` 记录后会自动初始化 `after_sale_execution_status = PENDING`
- `decisionType = NONE` 记录后会自动初始化 `after_sale_execution_status = NOT_REQUIRED`
- settlement confirm 后会自动初始化 `payout_status = UNPAID`
- courier 异常上报仍只保留订单上的最新一次异常，不改 `order_status`
- courier 位置上报仍只写 `campus_location_report`，不进入 timeline

## Step 05 实际完成事项

1. 为 `campus_relay_order` 新增售后执行字段：
   - `after_sale_execution_status`
   - `after_sale_execution_remark`
   - `after_sale_execution_reference_no`
   - `after_sale_executed_by_employee_id`
   - `after_sale_executed_at`
2. 扩展 `GET /api/campus/admin/orders/after-sale`
   - 新增 `afterSaleDecisionType`
   - 新增 `afterSaleExecutionStatus`
   - 默认排序继续 `after_sale_applied_at DESC, created_at DESC`
3. 新增 `GET /api/campus/admin/orders/{id}/after-sale-result`
4. 新增 `POST /api/campus/admin/orders/{id}/after-sale-execution`
5. 将售后执行结果规则集中在 service 层：
   - 仅 `AFTER_SALE_RESOLVED` 且已记录决策的订单可写
   - `REFUND + SUCCESS` 要求 `executionReferenceNo` 非空
   - `executionReferenceNo` 统一 trim，纯空白不落库
   - `decisionType = NONE` 不允许再通过执行接口写 `SUCCESS / FAILED`
   - 允许 `FAILED -> SUCCESS` 一次人工纠正
   - 拒绝 `SUCCESS -> FAILED` 和终态重复写入
6. admin 时间线新增“售后执行结果已记录”节点
7. 为 `campus_settlement_record` 新增打款字段：
   - `payout_status`
   - `payout_remark`
   - `payout_reference_no`
   - `payout_recorded_by_employee_id`
   - `payout_recorded_at`
8. 扩展 `GET /api/campus/admin/settlements`
   - 新增 `payoutStatus`
   - 继续兼容 `pageSize / size`
   - 默认排序继续 `createdAt DESC`
9. 新增 `POST /api/campus/admin/settlements/{id}/payout-result`
10. 新增 `POST /api/campus/admin/settlements/batch-payout-result`
11. 新增 `GET /api/campus/admin/settlements/reconcile-summary`
12. 将打款规则集中在 service 层：
   - 仅 `SETTLED` 记录可写
   - `PAID` 时 `payoutReferenceNo` 必填
   - `FAILED` 可无参考号
   - 批量处理会先对 `settlementIds` 去重
   - `totalRequested` 按去重后的请求数统计
   - 允许 `FAILED -> PAID` 一次人工纠正
13. 新增 `GET /api/campus/admin/orders/{id}/location-reports`
14. 新增 `GET /api/campus/admin/orders/{id}/exception-summary`
15. 订单级运营只读规则：
   - `location-reports` 订单不存在返回 `404`
   - 默认按 `reportedAt DESC`
   - `pageSize` 最大值 `100`
   - `exception-summary` 只返回当前订单最新一次异常摘要
   - 无异常时返回空异常字段
   - 无位置记录时 `locationReportCount = 0`
16. 新增 `CampusStep05IntegrationTest`
17. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `.\mvnw.cmd "-Dtest=CampusStep05IntegrationTest" test`
   - `.\mvnw.cmd test`
18. 当前累计 `50` 个测试通过

## 当前锁定的技术事实

1. 继续使用注解式 MyBatis，不改 XML
2. 继续沿用 `init.sql + migrations + schema-h2.sql + data-h2.sql`
3. 第一版支付仍只允许模拟支付，不接第三方支付
4. 退款/补偿/打款都仍然只是后台运营记录，不是真实支付执行
5. 所有金额处理继续统一为 `BigDecimal`、两位小数、`HALF_UP`
6. `courier/profile` 与 `courier/review-status` 的 bridge 继续保留
7. 保留 bridge 的原因：
   - 未审核通过用户拿不到 `courier` token`
   - 但资料提交与审核状态查询必须发生在拿 token 之前
8. 收口 bridge 的前提：
   - 先有稳定的 onboarding 替代链路
   - 或把资料提交与审核查询统一改为不依赖 `courier` token 的入口

## 当前未解决的问题

- customer 仍没有售后结果查询和自助退款反馈接口
- admin 仍没有售后执行审计列表和执行结果修正视图
- settlement 仍没有真实打款、撤回打款、批次审计详情和复杂对账
- 异常仍只保留最新一次，没有历史记录和处理结果流
- 位置记录仍是低频明细，没有地图、轨迹聚合和频控
- `frontend/` 仍未接入 campus 接口
- `CampusRuleCatalog` 仍是代码常量

## 下一轮建议

- 进入 `Step 06`
- 推荐顺序：
  1. 为 customer 增加售后结果查询与最小结果回执
  2. 为 admin 增加售后执行结果列表、按配送员/按状态筛选和人工修正只读视图
  3. 为 settlement 增加批次审计明细和更稳定的运营查询
  4. 结合 onboarding 方案继续评估 `courier/profile` bridge 的收口时机
  5. 在后端接口稳定后再决定 `frontend/` 的接入节奏

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
- [Step 03E 日志](step-03e-cancel-after-sale-timeline-settlement.md)
- [Step 03F 日志](step-03f-admin-after-sale-exception-location-settlement.md)
- [Step 04 日志](step-04-after-sale-decision-settlement-admin-ops.md)
- [Step 05 日志](step-05-after-sale-execution-payout-and-order-ops.md)
- [待处理事项](pending-items.md)
- [文件改动清单](file-change-list.md)

# 数据库总览

本文只记录当前关键表和迁移路径，真实字段以 SQL 文件为准。

## 初始化与迁移

- 本地完整初始化：`backend/db/init.sql`
- 版本迁移：`backend/db/migrations/`
- H2 schema：`backend/src/main/resources/db/schema-h2.sql`
- H2 seed：`backend/src/main/resources/db/data-h2.sql`

## 旧外卖基础表

旧外卖模块保留使用，包括：

- `employee`
- `user`
- `category`
- `dish`
- `setmeal`
- `orders`
- `order_detail`
- `cart`
- `address`

这些表仍是旧模块和复用身份模型的基础。

## campus 核心表

### courier

- `campus_courier_profile`

用于独立建模兼职配送员，通过 `user_id` 关联普通用户。

### order

- `campus_relay_order`
- campus timeline / location / exception 相关扩展表

承载校园代送订单、状态推进、异常摘要、位置记录等能力。

### exception

- `campus_exception_record`

记录 courier 每次异常上报的历史。订单表上的 latest exception 字段继续作为兼容摘要。

### after-sale

- campus 售后决策字段
- `campus_after_sale_execution_record`

记录 admin 每次售后执行结果，订单表上的 after-sale execution 字段继续作为当前摘要。

### settlement

- `campus_settlement_record`
- `campus_settlement_batch_operation_record`
- `campus_settlement_reconcile_difference_record`

当前单笔结算与打款摘要主表。批次由 `payout_batch_no` 逻辑聚合。
批次操作审计表记录 admin 对逻辑打款批次的复核、撤回观察类操作，只做审计留痕，不改变单笔 payout 摘要。
对账差异记录表记录 admin 或模拟对账发现的单笔 settlement 差异，作为审计主数据，不改变 settlement payout 摘要。

## 迁移脚本现状

- `V1__baseline_schema.sql`：旧外卖基线
- `V2__campus_relay_schema.sql`：校园代送核心 schema
- `V3__campus_relay_order_timeline_columns.sql`
- `V4__campus_relay_ops_and_settlement.sql`
- `V5__campus_after_sale_decision_and_settlement_ops.sql`
- `V6__campus_after_sale_execution_and_settlement_payout.sql`
- `V7__campus_customer_receipt_and_settlement_audit.sql`
- `V8__campus_courier_onboarding_bridge_replacement.sql`
- `V9__campus_exception_record.sql`
- `V10__campus_after_sale_execution_record.sql`
- `V11__campus_settlement_batch_operation_record.sql`
- `V12__campus_settlement_reconcile_difference_record.sql`

## 设计约束

1. 新 campus 表统一使用 `campus_` 前缀。
2. 普通用户复用 `user`。
3. 管理员复用 `employee`。
4. 配送员独立建模为 `campus_courier_profile`。
5. 模拟支付和模拟打款不等价于真实财务系统。
6. 新增表时必须同步 MySQL init、migration、H2 schema，必要时同步 H2 seed。

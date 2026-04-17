# Database Migrations

这里存放面向部署和审计的数据库迁移脚本，和 `backend/db/init.sql` 的用途不同：

- `init.sql`：快速初始化本地演示或一次性手工建库。
- `migrations/`：按版本演进数据库结构，便于部署、审计和回滚。

## 当前迁移

- `V1__baseline_schema.sql`：旧外卖基线表结构。
- `V2__campus_relay_schema.sql`：校园代送核心 schema。
- `V3__campus_relay_order_timeline_columns.sql`：订单时间线字段。
- `V4__campus_relay_ops_and_settlement.sql`：运营与结算基础能力。
- `V5__campus_after_sale_decision_and_settlement_ops.sql`：售后决策与 settlement ops。
- `V6__campus_after_sale_execution_and_settlement_payout.sql`：售后执行与打款结果。
- `V7__campus_customer_receipt_and_settlement_audit.sql`：customer 回执与 settlement 审计。
- `V8__campus_courier_onboarding_bridge_replacement.sql`：courier onboarding 新入口与 bridge 并行。
- `V9__campus_exception_record.sql`：异常历史记录。
- `V10__campus_after_sale_execution_record.sql`：售后执行历史记录。
- `V11__campus_settlement_batch_operation_record.sql`：结算批次操作审计记录。

## 维护规则

1. 新增数据库结构时只追加新版本脚本，不覆盖历史版本。
2. 同步维护 `backend/db/init.sql`。
3. 同步维护 `backend/src/main/resources/db/schema-h2.sql`。
4. 如需 H2 样本数据，同步维护 `backend/src/main/resources/db/data-h2.sql`。
5. 不要把演示数据误写成生产初始化要求。

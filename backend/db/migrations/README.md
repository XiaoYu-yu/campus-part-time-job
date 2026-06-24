# Database Migrations

这里存放面向部署和审计的 Flyway 数据库迁移脚本，和 `backend/db/init.sql`、`backend/db/seed` 的用途不同：

- `migrations/`：按版本演进 MySQL 数据库结构，已通过 Maven resources 打包到 `classpath:db/migration`。
- `init.sql`：历史快速初始化 / 人工参考脚本，不作为正式部署入口。
- `seed/`：可选演示数据，不参与生产结构迁移。

生产 profile 默认启用 Flyway。测试 profile 继续使用 H2 的 `schema-h2.sql` / `data-h2.sql`，并禁用 Flyway。

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
- `V12__campus_settlement_reconcile_difference_record.sql`：结算对账差异记录。
- `V13__campus_feedback.sql`：用户端与兼职端反馈记录。
- `V14__campus_feedback_admin_processing.sql`：反馈处理管理员、时间与备注字段。

## 维护规则

1. 新增数据库结构时只追加新版本脚本，不覆盖历史版本。
2. 新增脚本命名使用 Flyway 规则：`V{版本号}__{说明}.sql`。
3. 同步维护 `backend/src/main/resources/db/schema-h2.sql`，保证 H2 测试仍覆盖最新结构。
4. 如需 H2 样本数据，同步维护 `backend/src/main/resources/db/data-h2.sql`。
5. 如需 MySQL 内测样本数据，同步维护 `backend/db/seed/internal-trial-data.sql`。
6. 不要把演示数据误写成生产初始化要求。
7. 已经进入 Flyway 历史的脚本不要再回写修改；如需调整，追加新版本脚本。

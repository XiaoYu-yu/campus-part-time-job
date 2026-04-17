# Step 57 - settlement 批次操作审计最小实现

## 本轮目标

1. 基于 Step 56 的 go / no-go 结论，落地 `campus_settlement_batch_operation_record`。
2. 复用现有 settlement 批次聚合能力，新增批次操作审计最小后端闭环。
3. review / withdraw 只写批次操作审计，不改变现有 payout 摘要。
4. 不实现对账差异记录，不新增前端页面，不触 bridge。

## 实际落地内容

### 数据表

新增 `campus_settlement_batch_operation_record`。

最终字段：

1. `id`
2. `payout_batch_no`
3. `operation_type`
4. `operation_result`
5. `operation_remark`
6. `operated_by_employee_id`
7. `operated_at`
8. `created_at`
9. `updated_at`

最小索引：

1. `payout_batch_no`
2. `operation_type`
3. `operation_result`
4. `operated_at`

数据库路径同步：

1. `backend/db/init.sql`
2. `backend/db/migrations/V11__campus_settlement_batch_operation_record.sql`
3. `backend/src/main/resources/db/schema-h2.sql`
4. `backend/db/migrations/README.md`

H2 data：

1. 本轮没有预置批次操作样本。
2. 原因：批次操作记录必须基于真实存在的 `payout_batch_no` 写入，预置样本容易和当前结算种子状态不一致。
3. Step 58 建议通过 H2/test 运行态先生成或使用真实批次，再调用 review / withdraw 写入审计。

### 后端模型

新增：

1. `CampusSettlementBatchOperationRecord`
2. `CampusAdminSettlementBatchOperationDTO`
3. `CampusSettlementBatchOperationQuery`
4. `CampusSettlementBatchOperationRecordVO`
5. `CampusSettlementBatchOperationRecordMapper`
6. `CampusSettlementBatchOperationRecordService`
7. `CampusSettlementBatchOperationRecordServiceImpl`
8. `CampusAdminSettlementBatchOperationController`

### 新增接口

1. `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
2. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
3. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`

列表接口：

1. 默认按 `operated_at desc, id desc` 返回。
2. 支持最小分页：`page / pageSize / size`。
3. 支持最小筛选：`operationType / operationResult`。

review 接口：

1. `operation_type = REVIEW`
2. 只允许 `operation_result = PASSED / REJECTED`
3. 必须提供 `operationRemark`

withdraw 接口：

1. `operation_type = WITHDRAW`
2. 只允许 `operation_result = REQUESTED / RECORDED`
3. 必须提供 `operationRemark`

## 批次存在性校验

1. 写入 review / withdraw 前会调用现有 `CampusSettlementRecordMapper.selectPayoutBatchSummaryByBatchNo(batchNo)`。
2. 只有当前 `campus_settlement_record.payout_batch_no` 能聚合出真实批次时，才允许写入操作审计。
3. 批次不存在时返回 `打款批次不存在`。

## 兼容策略如何落地

本轮只新增批次操作审计表，不修改 `campus_settlement_record`。

继续保持不变的字段：

1. `payout_status`
2. `payout_batch_no`
3. `payout_remark`
4. `payout_reference_no`
5. `payout_recorded_by_employee_id`
6. `payout_recorded_at`
7. `payout_verified`
8. `payout_verified_by_employee_id`
9. `payout_verified_at`
10. `payout_verify_remark`

继续保持不变的读取路径：

1. settlement 单笔详情继续读 `campus_settlement_record`。
2. settlement 批次列表/详情继续按 `payout_batch_no` 聚合。
3. `reconcile-summary` 继续按现有 payout 摘要字段聚合。
4. 新增操作审计只通过 `/operations` 读取。

这样做的原因：

1. review / withdraw 当前只是运营观察和留痕，不是真实财务动作。
2. 不把“撤回”误做成打款状态逆转。
3. 不破坏已验证的 settlement 批次列表和详情页。
4. 后续如需前端承接，可以只在现有批次详情中新增只读操作历史区。

## 明确没有做

1. 没有新增 settlement 前端页面。
2. 没有修改现有 settlement 批次页。
3. 没有实现对账差异记录表。
4. 没有修改 `payout_status`。
5. 没有清空 `payout_batch_no`。
6. 没有做真实财务撤回。
7. 没有修改订单主状态。
8. 没有修改 bridge。
9. 没有修改 `request.js`、token 附着、鉴权或路由。
10. 没有新增第五个 admin 页。

## 验证结果

已完成：

1. `.\mvnw.cmd -DskipTests compile` 通过。
2. `npm run build` 通过。

未完成：

1. 本轮未启动 H2/test profile 做接口运行态验证。
2. 未真实调用 review / withdraw。
3. 未验证原批次详情接口在运行态返回不变。

原因：

1. 本轮主要完成后端最小实现和构建验证。
2. 运行态需要先准备真实 `payout_batch_no`，建议单独作为 Step 58 验证轮处理，避免把未执行结果写成已通过。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 58 优先做 settlement 批次操作审计运行态验证。
2. H2/test 下准备一个真实 `payout_batch_no`。
3. 调用 review 写入 `PASSED / REJECTED` 审计。
4. 调用 withdraw 写入 `REQUESTED / RECORDED` 审计。
5. 验证 `/operations` 可查到历史，且原 settlement record payout 摘要未变化。
6. 运行态验证通过后，再评估是否需要前端最小只读承接或进入对账差异记录方案。

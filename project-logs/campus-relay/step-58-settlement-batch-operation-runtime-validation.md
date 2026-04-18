# Step 58 - settlement 批次操作审计运行态验证

## 本轮目标

1. 基于 Step 57 已落地的 `campus_settlement_batch_operation_record`，补 H2/test 运行态验证。
2. 在本地 test profile 下准备真实 `payout_batch_no`。
3. 调用 review / withdraw 写入批次操作审计。
4. 调用 `/operations` 查询操作历史。
5. 验证原 settlement 批次详情和单笔 payout 摘要未被 review / withdraw 改写。

## 运行环境

1. 后端：test profile + H2。
2. 启动方式：`spring-boot:run`，覆盖 `server.port=8080`。
3. admin 登录账号：`13800138000 / 123456`。
4. 结算记录：`campus_settlement_record.id = 1`。
5. 关联订单：`CR202604060001`。
6. 固定批次号：`PBSTEP58VALIDATION`。
7. 运行证据：`project-logs/campus-relay/runtime/step-58/settlement-batch-operation-api-validation.json`。

## 实际执行步骤

1. 调用 `POST /api/employees/login` 获取 admin employee token。
2. 调用 `POST /api/campus/admin/settlements/1/confirm`，把 H2 种子中的 `PENDING` 结算记录确认成可打款状态。
3. 调用 `POST /api/campus/admin/settlements/batch-payout-result`，使用固定 `batchNo = PBSTEP58VALIDATION` 记录批量打款成功。
4. 调用 `GET /api/campus/admin/settlements/payout-batches/PBSTEP58VALIDATION` 获取批次详情基线。
5. 调用 `GET /api/campus/admin/settlements/1` 获取单笔结算基线。
6. 调用 `POST /api/campus/admin/settlements/payout-batches/PBSTEP58VALIDATION/review` 写入：
   - `operationType = REVIEW`
   - `operationResult = PASSED`
   - `operationRemark = Step58 review passed audit only`
7. 调用 `POST /api/campus/admin/settlements/payout-batches/PBSTEP58VALIDATION/withdraw` 写入：
   - `operationType = WITHDRAW`
   - `operationResult = REQUESTED`
   - `operationRemark = Step58 withdraw requested audit only`
8. 调用 `GET /api/campus/admin/settlements/payout-batches/PBSTEP58VALIDATION/operations?page=1&pageSize=10` 查询批次操作历史。
9. 再次调用批次详情和单笔结算详情，验证 review / withdraw 只写操作审计、不改 payout 摘要。

## 验证结果

已通过：

1. `/operations` 返回 2 条批次操作审计记录。
2. 操作历史包含 `REVIEW / PASSED`。
3. 操作历史包含 `WITHDRAW / REQUESTED`。
4. 单笔结算 `payoutStatus` 在操作前后保持 `PAID`。
5. 单笔结算 `payoutBatchNo` 在操作前后保持 `PBSTEP58VALIDATION`。
6. 单笔结算 `payoutVerified` 在操作前后保持不变。
7. 批次详情 `totalCount` 在操作前后保持不变。
8. 批次详情 `paidCount` 在操作前后保持不变。
9. 批次详情记录中的 `payoutStatus` 在操作前后保持不变。

运行证据摘要：

```json
{
  "generatedPayoutBatchNo": "PBSTEP58VALIDATION",
  "settlementId": 1,
  "relayOrderId": "CR202604060001",
  "totalOperationRecords": 2,
  "containsReviewPassed": true,
  "containsWithdrawRequested": true,
  "singlePayoutStatusStable": true,
  "singlePayoutBatchNoStable": true,
  "singlePayoutVerifiedStable": true,
  "passed": true
}
```

## 兼容策略验证结论

1. review / withdraw 已验证为纯操作审计写入。
2. `campus_settlement_record.payout_status` 未被 review / withdraw 改写。
3. `campus_settlement_record.payout_batch_no` 未被 review / withdraw 清空或替换。
4. `payout_verified*` 核对字段未被 review / withdraw 改写。
5. 原 settlement 批次详情和单笔 payout 摘要仍继续作为兼容读取基础。
6. 新增 `/operations` 是批次操作历史的唯一读取入口。

## 明确没有做

1. 没有修改 Java / SQL / Vue 业务代码。
2. 没有新增前端页面。
3. 没有实现对账差异记录。
4. 没有接真实打款。
5. 没有修改 `payout_status`。
6. 没有清空 `payout_batch_no`。
7. 没有修改订单主状态。
8. 没有修改 bridge。
9. 没有修改 `request.js`、token 附着、鉴权或路由。
10. 没有新增第五个 admin 页。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 59 可以进入 go / no-go 评估：
   - 方向 A：在现有 settlement 批次详情前端中做操作审计只读承接。
   - 方向 B：进入 settlement 对账差异记录最小方案设计。
2. 默认不要新增独立第五个 admin 页。
3. 如果做前端承接，优先在现有批次详情页增加只读操作历史区，不新增写操作。
4. 如果进入对账差异设计，先做方案边界，不并发实现前端页面。

## Step 59 go / no-go 回填

Step 59 已完成 go / no-go 评估，并选择方向 A：

1. 在现有 `CampusSettlementBatchDetail.vue` 中增加 operations 只读承接。
2. 不新增独立第五个 admin 页。
3. 不调用 review / withdraw 写接口。
4. 不进入对账差异记录方案设计。
5. Step 60 建议只改 `frontend/src/api/campus-admin.js` 与 `frontend/src/views/CampusSettlementBatchDetail.vue`。

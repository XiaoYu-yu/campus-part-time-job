# Step 59 - settlement 批次操作审计前端承接 go / no-go

## 本轮目标

1. 基于 Step 58 已通过的 H2/test 运行态验证，判断 settlement 批次操作审计下一步是否需要前端承接。
2. 在“现有批次详情页只读承接”和“进入对账差异记录方案设计”之间做 go / no-go 选择。
3. 不写代码，不新增页面，不改接口，不改 bridge，不改鉴权。

## 当前起点

1. Step 57 已落地 `campus_settlement_batch_operation_record` 和三个 admin 接口：
   - `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
   - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
   - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`
2. Step 58 已在 H2/test 下验证：
   - 固定批次 `PBSTEP58VALIDATION` 可写入 `REVIEW / PASSED`。
   - 固定批次 `PBSTEP58VALIDATION` 可写入 `WITHDRAW / REQUESTED`。
   - `/operations` 可查询 2 条操作历史。
   - review / withdraw 不改 `payout_status`、`payout_batch_no`、`payout_verified`。
3. 当前前端已有批次详情页：
   - `frontend/src/views/CampusSettlementBatchDetail.vue`
   - 路由：`/campus/settlement-batches/:batchNo`
4. 当前前端 API 封装已有：
   - `getCampusSettlementPayoutBatches`
   - `getCampusSettlementPayoutBatchDetail`
5. 当前前端 API 封装尚未包含：
   - `getCampusSettlementBatchOperations`

## 方向 A：现有批次详情页只读承接

### 最小范围

1. 在 `frontend/src/api/campus-admin.js` 新增 operations 只读封装。
2. 在 `CampusSettlementBatchDetail.vue` 中增加“批次操作历史”只读区。
3. 只读取 `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`。
4. 不调用 review / withdraw 写接口。
5. 不新增路由。
6. 不新增独立 admin 页面。

### 演示收益

1. Step 57 / Step 58 的后端审计能力可以直接在已有批次详情页被看见。
2. settlement 批次页形成“汇总 -> 明细 -> 操作历史”的最小闭环。
3. 不需要新增第五个 admin 页，也不会制造新的后台导航负担。
4. 能直接证明 review / withdraw 是操作审计，而不是 payout 状态改写。

### 真实使用收益

1. admin 可在批次详情上下文中查看该批次所有 review / withdraw 留痕。
2. 与现有批次详情页的 `batchNo` 上下文天然一致。
3. 后续若需要写操作按钮，也可在同一页继续评估，但当前不做。

### 风险

1. 风险集中在一个现有 Vue 页面和一个 API 封装。
2. 只读查询不影响 settlement 业务状态。
3. 不改后端，不改接口语义，不改路由。
4. 若接口无数据，只需展示空态。

### 结论

方向 A 收益明确、改动小、风险低，适合作为 Step 60 的最小实现候选。

## 方向 B：进入 settlement 对账差异记录方案设计

### 最小范围

1. 设计 `campus_settlement_reconcile_difference_record` 或等价对账差异记录模型。
2. 明确差异来源、差异状态、处理状态、兼容策略和接口边界。
3. 本轮不写代码。

### 演示收益

1. 能继续补 settlement 财务运营深度。
2. 对“对账差异与处理留痕”有长期价值。

### 风险

1. 复杂度高于批次操作历史前端承接。
2. 容易从“只读运营”扩大到完整财务对账系统。
3. 当前还没有先把 Step 57 / Step 58 的批次操作审计前端可见化，直接进入差异记录会跳过一个已验证能力的展示闭环。
4. 需要新表、迁移、接口和新的兼容策略设计，不能作为小步承接。

### 结论

方向 B 有后续价值，但不应抢在批次操作历史前端只读承接之前进入。建议继续后置到 Step 61 或更后，等 Step 60 先完成 operations 只读承接后再评估。

## 最终选择

选择方向 A：Step 60 进入 settlement 批次操作审计前端最小只读承接。

理由：

1. Step 58 已证明后端 operations 能真实返回审计历史。
2. 现有 `CampusSettlementBatchDetail.vue` 正好具备 `batchNo` 上下文。
3. 只读承接能补齐当前最小演示闭环。
4. 不新增第五个 admin 页。
5. 不改后端、不改状态、不改 bridge。
6. 改动范围可以严格限制在一个 API 封装和一个现有页面。

## Step 60 建议边界

允许做：

1. `frontend/src/api/campus-admin.js` 新增 `getCampusSettlementBatchOperations(batchNo, params)`。
2. `frontend/src/views/CampusSettlementBatchDetail.vue` 增加只读操作历史区。
3. 支持最小分页或固定第一页读取。
4. 展示字段：
   - `operationType`
   - `operationResult`
   - `operationRemark`
   - `operatedByEmployeeId`
   - `operatedAt`
5. 空态展示“暂无批次操作记录”。
6. loading / error 态保持最小清晰。

明确不做：

1. 不新增页面。
2. 不新增路由。
3. 不调用 review / withdraw 写接口。
4. 不新增按钮动作。
5. 不改 `payout_status`。
6. 不改 `payout_batch_no`。
7. 不实现对账差异记录。
8. 不接真实财务。
9. 不改 bridge、鉴权、token 附着或旧外卖模块。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 60 进入 settlement 批次操作审计前端最小只读承接。
2. 优先只改 `frontend/src/api/campus-admin.js` 与 `frontend/src/views/CampusSettlementBatchDetail.vue`。
3. 完成后运行：
   - `npm run build`
   - `.\mvnw.cmd -DskipTests compile`
   - `git diff --check`
4. 如果实现过程中发现现有接口字段不足，先停在问题记录，不要顺手改后端。

## Step 60 实现回填

Step 60 已按方向 A 完成前端最小只读承接：

1. `frontend/src/api/campus-admin.js` 新增 `getCampusSettlementBatchOperations(batchNo, params)`。
2. `frontend/src/views/CampusSettlementBatchDetail.vue` 新增“批次操作历史”只读区。
3. 只调用 `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`。
4. 未调用 review / withdraw 写接口。
5. 未新增页面、路由、后端接口、bridge 改动或真实财务动作。

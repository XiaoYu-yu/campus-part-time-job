# Step 56 - settlement 批次操作审计 go / no-go

## 本轮目标

1. 回到 Step 54 的 settlement 批次复核、撤回和对账方案。
2. 判断下一步是否进入批次操作审计最小实现。
3. 明确本轮是否需要并发做对账差异记录。
4. 继续保持 bridge、展示 polish 和媒体线冻结口径。

## 真实代码基线

1. 当前 settlement 批次仍不是独立批次主表。
2. 当前批次由 `campus_settlement_record.payout_batch_no` 聚合得到。
3. 现有后端已经有：
   - `GET /api/campus/admin/settlements`
   - `GET /api/campus/admin/settlements/{id}`
   - `GET /api/campus/admin/settlements/payout-batches`
   - `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
   - `POST /api/campus/admin/settlements/{id}/verify-payout`
   - `POST /api/campus/admin/settlements/batch-payout-result`
   - `GET /api/campus/admin/settlements/reconcile-summary`
4. 现有前端已经有 settlement 批次列表、批次详情和 settlement 只读运营页。
5. 当前缺口不是“能不能看批次”，而是“批次级复核、撤回、观察类操作没有审计留痕”。

## 候选方向 A：进入批次操作审计最小实现

### 建议实现范围

新增 `campus_settlement_batch_operation_record`，只承接批次级操作审计。

建议最小字段：

1. `id`
2. `payout_batch_no`
3. `operation_type`
4. `operation_result`
5. `operation_remark`
6. `operated_by_employee_id`
7. `operated_at`
8. `created_at`
9. `updated_at`

建议最小 `operation_type`：

1. `REVIEW`
2. `WITHDRAW`

建议最小 `operation_result`：

1. `PASSED`
2. `REJECTED`
3. `REQUESTED`
4. `RECORDED`

说明：

1. Step 54 曾列出 `REVIEW_PASSED / REVIEW_REJECTED / WITHDRAW_REQUESTED / WITHDRAW_RECORDED` 作为操作类型草案。
2. Step 56 建议收敛为 `operation_type + operation_result` 两段式字段。
3. 这样可以避免后续操作类型枚举膨胀，同时保持查询和展示更清楚。

### 建议最小接口

Step 57 若进入实现，只建议新增：

1. `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
2. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
3. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`

接口边界：

1. `review` 只写一条批次操作记录。
2. `withdraw` 只写一条批次操作记录。
3. 写入前需要校验 `batchNo` 在现有 settlement 批次聚合中真实存在。
4. 操作人使用当前 admin employee 上下文。
5. 列表接口默认按 `operated_at desc` 返回。

### 明确不修改

1. 不修改 `campus_settlement_record.payout_status`。
2. 不修改 `campus_settlement_record.payout_batch_no`。
3. 不修改 `campus_settlement_record.payout_verified*`。
4. 不修改 settlement 单笔详情、批次详情、reconcile-summary 的现有读取语义。
5. 不做真实财务撤回。
6. 不触发打款逆操作。
7. 不新增前端页面。

### 收益

1. 补齐批次级操作审计缺口。
2. 风险小于直接新增 settlement 批次主表。
3. 可以复用现有 `payout_batch_no` 聚合批次，不迁移已有数据。
4. 不破坏 Step 08 / Step 11 已有 admin settlement 演示页。
5. 后续可在现有批次详情 drawer 或详情页中只读承接操作历史。

### 风险

1. 如果后续把 withdraw 误做成真实撤回，会破坏当前模拟财务边界。
2. 如果在同一轮并发做对账差异记录，范围会扩大。
3. 如果接口命名不清，容易让使用者误以为 review / withdraw 会改变 payout 状态。

### 风险控制

1. 接口文档和日志明确写“只写审计，不改 payout”。
2. Service 层只写 `campus_settlement_batch_operation_record`。
3. 不在本阶段修改 settlement record。
4. Step 57 验证重点放在“操作审计新增”和“原批次详情不变”。

## 候选方向 B：继续停留在方案设计

### 收益

1. 零实现风险。
2. 可以继续打磨对账差异记录方案。

### 缺点

1. 批次级复核/撤回审计缺口仍然存在。
2. Step 54 已经把批次操作和对账差异拆清楚，继续设计收益有限。
3. 当前进入最小实现的边界已经足够明确。

## Go / no-go 结论

本轮结论：选择方向 A，建议 Step 57 进入 `campus_settlement_batch_operation_record` 最小实现。

理由：

1. 批次操作审计是当前 settlement 线最小且最清晰的缺口。
2. 实现可以完全避开真实财务、payout 状态改写和批次主表迁移。
3. 对账差异记录仍可后置，不需要和批次操作审计并发实现。
4. 该动作可单独验证、单独回滚，不会破坏已有 customer / courier / admin 主演示链路。

## Step 57 实现边界建议

Step 57 只允许实现：

1. `campus_settlement_batch_operation_record` 表。
2. MySQL init、migration、H2 schema 同步。
3. entity / DTO / VO / mapper / service / controller 的最小实现。
4. `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`。
5. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`。
6. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`。

Step 57 明确不做：

1. 不新增 settlement 前端页面。
2. 不修改现有 settlement 批次页。
3. 不做对账差异记录表。
4. 不修改 `payout_status`。
5. 不清空 `payout_batch_no`。
6. 不做真实财务撤回。
7. 不改 bridge、鉴权、token 附着或路由。

## 验证建议

Step 57 至少验证：

1. `.\mvnw.cmd -DskipTests compile` 通过。
2. `npm run build` 通过。
3. H2/test profile 启动成功。
4. 针对存在的 `payoutBatchNo` 写入 review 记录成功。
5. 针对存在的 `payoutBatchNo` 写入 withdraw 记录成功。
6. 操作历史列表可按 `operated_at desc` 查询。
7. 原 settlement 批次详情接口返回不变。
8. 原 settlement 单笔 payout 字段未被 review / withdraw 改写。
9. `git diff --check` 通过。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 本轮明确没有做

1. 没有写 Java 业务代码。
2. 没有写 SQL migration。
3. 没有改 Vue 页面。
4. 没有新增接口实现。
5. 没有修改旧外卖模块。
6. 没有改 bridge、鉴权、token 附着或路由。
7. 没有新增第五个 admin 页。

## 下一轮建议

1. Step 57 进入 settlement 批次操作审计最小实现。
2. 只落 `campus_settlement_batch_operation_record` 和三个最小 admin 接口。
3. 对账差异记录继续后置到 Step 58 或之后单独评估。
4. 如果 Step 57 实现中发现会触碰 payout 状态或真实财务语义，应立即停下并回到方案评估。

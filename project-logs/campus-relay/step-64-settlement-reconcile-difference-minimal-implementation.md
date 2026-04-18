# Step 64 - settlement 对账差异记录最小后端实现

## 本轮目标

1. 基于 Step 63 的 go / no-go 结论，落地 settlement 对账差异最小后端闭环。
2. 新增 `campus_settlement_reconcile_difference_record`，并同步 MySQL init、migration、H2 schema。
3. 新增 admin 对账差异列表、详情、创建、resolve 四个最小接口。
4. 保持 settlement payout 摘要、`reconcile-summary`、订单主状态和 bridge 行为不变。

## 已完成项

### 1. 数据库落地

已新增 `campus_settlement_reconcile_difference_record`：

- `id`
- `payout_batch_no`
- `settlement_record_id`
- `relay_order_id`
- `courier_profile_id`
- `difference_type`
- `expected_amount`
- `actual_amount`
- `difference_remark`
- `process_status`
- `process_result`
- `process_remark`
- `reported_by_employee_id`
- `reported_at`
- `processed_by_employee_id`
- `processed_at`
- `source`
- `created_at`
- `updated_at`

已同步：

- `backend/db/init.sql`
- `backend/db/migrations/V12__campus_settlement_reconcile_difference_record.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/db/migrations/README.md`

H2 seed 未新增固定差异样本，原因是本轮运行态验证通过真实 admin 创建接口生成对账差异，避免把演示用差异误写成默认初始化数据。

### 2. admin 最小接口

新增：

- `GET /api/campus/admin/settlements/reconcile-differences`
- `GET /api/campus/admin/settlements/reconcile-differences/{id}`
- `POST /api/campus/admin/settlements/reconcile-differences`
- `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`

列表支持最小筛选：

- `payoutBatchNo`
- `settlementRecordId`
- `relayOrderId`
- `differenceType`
- `processStatus`
- `page / pageSize / size`

创建校验：

- `settlementRecordId` 必须存在。
- `payoutBatchNo` 必须匹配 settlement 当前 `payoutBatchNo`。
- `differenceType` 仅支持 `AMOUNT_MISMATCH / STATUS_MISMATCH / UNVERIFIED_PAID / FAILED_NEEDS_RETRY / MANUAL_NOTE`。
- `source` 仅支持 `MANUAL_ADMIN / SIMULATED_RECONCILE`，未传默认 `MANUAL_ADMIN`。
- `AMOUNT_MISMATCH` 必须传 `expectedAmount` 和 `actualAmount`。
- 初始 `processStatus = OPEN`。

resolve 规则：

- 只允许 `OPEN -> RESOLVED`。
- `processResult` 仅支持 `CONFIRMED / MARKED_INVALID / FOLLOWED_UP`。
- 重复 resolve 返回明确业务错误。
- 只更新差异记录自身处理字段，不改 settlement payout 摘要。

### 3. 兼容策略

本轮明确落地如下兼容策略：

1. `campus_settlement_record` 继续作为 settlement 单笔、批次和 `reconcile-summary` 的兼容摘要来源。
2. 对账差异记录只作为审计主数据，不改 `payout_status`、`payout_batch_no`、`pending_amount`、`payout_verified*`。
3. 现有 settlement 单笔页、批次页、batch operation 页和 `reconcile-summary` 语义不变。
4. admin 想查看对账差异处理状态，读取新增 `reconcile-differences` 接口。

## 运行态验证

H2/test 验证证据：

- `project-logs/campus-relay/runtime/step-64/settlement-reconcile-difference-validation.json`

验证链路：

1. 使用 employee `13800138000 / 123456` 登录。
2. 确认 settlement `id = 1`。
3. 对 settlement `id = 1` 记录批次打款失败，固定批次号 `PBSTEP64RECON`。
4. 创建 `AMOUNT_MISMATCH` 对账差异。
5. 列表接口按 `processStatus=OPEN` 查到记录。
6. 详情接口回读 `processStatus = OPEN`。
7. resolve 后详情回读 `processStatus = RESOLVED`、`processResult = CONFIRMED`。
8. 再次 resolve 返回业务错误：`对账差异记录已处理，不能重复处理`。
9. resolve 前后 settlement `payoutStatus` 仍为 `FAILED`。
10. resolve 前后 settlement `payoutBatchNo` 仍为 `PBSTEP64RECON`。
11. resolve 前后 `reconcile-summary` 统计未变化。

验证结果摘要：

- backend 启动：通过
- 创建差异：通过
- 列表：通过
- 详情：通过
- resolve：通过
- 重复 resolve 失败保护：通过
- settlement payout 摘要不变：通过
- reconcile-summary 不变：通过

## 测试结果

- `.\mvnw.cmd -DskipTests compile`：通过
- `npm run build`：通过
- H2/test 运行态接口验证：通过
- `git diff --check`：待最终提交前执行

## 本轮明确没有做

1. 没有新增前端页面。
2. 没有新增第五个 admin 页。
3. 没有改 settlement payout 摘要。
4. 没有改 `reconcile-summary` 语义。
5. 没有接真实财务、银行流水或第三方支付。
6. 没有自动改金额、自动重算 settlement 或触发打款。
7. 没有改订单主状态。
8. 没有改 bridge、鉴权、token 附着、路由或旧外卖模块。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、`request.js`、`campus-courier.js`、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 65 先做 settlement 对账差异前端承接 go / no-go。
2. 默认不直接新增第五个 admin 页。
3. 优先评估是否在现有 settlement 只读运营页或批次详情页中做最小只读承接。
4. 如果前端承接收益不足，也可以收住 settlement 对账差异线，转入下一条非 bridge 后端能力。

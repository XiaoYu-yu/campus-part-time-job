# Step 67 - settlement 对账差异前端运行态验证

## 本轮目标

1. 在 H2/test 后端与 Vite 前端下，验证 Step 66 新增的 settlement 对账差异只读区真实可用。
2. 准备固定 settlement 与对账差异样本，确保 `/campus/settlements` 详情 drawer 可读到真实差异记录。
3. 验证页面只读承接不改变 settlement payout 摘要、`reconcile-summary`、bridge、鉴权、路由或旧外卖链路。

## 运行环境

- 后端：`test profile + H2`，`http://127.0.0.1:18080`
- 前端：Vite，`http://127.0.0.1:5173`
- 页面入口：`/campus/settlements`
- 管理员账号：`13800138000`

## 样本准备

本轮通过真实 admin API 准备样本：

1. 登录 admin。
2. 将 settlement `id=1` 确认为已结算。
3. 写入批次打款结果：
   - `batchNo = PBSTEP67UI`
   - `payoutStatus = FAILED`
   - `payoutRemark = Step67 failed payout for frontend drawer validation`
4. 创建对账差异：
   - `settlementRecordId = 1`
   - `payoutBatchNo = PBSTEP67UI`
   - `differenceType = AMOUNT_MISMATCH`
   - `expectedAmount = 6.00`
   - `actualAmount = 4.50`
   - `differenceRemark = Step67 UI drawer amount mismatch validation`
   - `source = SIMULATED_RECONCILE`

证据文件：

- `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-ui-api-prep.json`

说明：准备文件已脱敏，不保留 admin token。

## 页面验证

Playwright 验证步骤：

1. 使用 admin 登录并设置本地 admin 会话。
2. 打开 `/campus/settlements`。
3. 验证列表中出现 settlement `id=1` / `CR202604060001`：
   - `settlementStatus = SETTLED`
   - `payoutStatus = FAILED`
   - `payoutBatchNo = PBSTEP67UI`
4. 点击“查看详情”打开详情 drawer。
5. 验证 drawer 内“对账差异记录”区展示：
   - `PBSTEP67UI`
   - `金额不一致`
   - `¥6.00`
   - `¥4.50`
   - `Step67 UI drawer amount mismatch validation`
   - `待处理`
   - `模拟对账`
6. 浏览器内再次调用：
   - `GET /api/campus/admin/settlements/reconcile-differences?settlementRecordId=1&page=1&pageSize=10`
7. 确认返回：
   - `code = 200`
   - `total = 1`
   - 第一条记录 `processStatus = OPEN`

证据文件：

- `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-page-validation.json`
- `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-drawer.png`

## 兼容验证

本轮确认：

1. settlement payout 摘要仍保留为 `FAILED / PBSTEP67UI`。
2. `pendingAmount`、`payoutStatus`、`payoutBatchNo`、`payoutVerified*` 没有因只读差异展示被改写。
3. `CampusSettlementOpsView.vue` 只调用只读接口，不接入 create / resolve 写操作。
4. 空态和错误态仍局限在详情 drawer 的对账差异区，不影响 settlement 主详情。

## 明确没有做

1. 没有修改 Java、SQL、Vue 业务代码。
2. 没有新增页面或路由。
3. 没有新增后端接口。
4. 没有接入 `POST /api/campus/admin/settlements/reconcile-differences`。
5. 没有接入 `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`。
6. 没有修改 settlement payout 摘要、批次操作审计、`reconcile-summary` 或订单主状态。
7. 没有修改 bridge、`request.js`、token 附着、鉴权或旧外卖模块。

## 验证结果

- API 样本准备：通过。
- Playwright 页面验证：通过。
- `git diff --check`：通过。
- `npm run build`：通过；仍有既有 Sass `@import` deprecation 和 chunk size warning。
- 本轮未改后端源码，未重新运行 `.\\mvnw.cmd -DskipTests compile`。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、`request.js`、`campus-courier.js`、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 68 先评估 settlement 对账差异前端线是否收住。
2. 若不收住，再做“是否接入对账差异 resolve 前端动作”的 go / no-go，不要默认直接写代码。
3. 若收住，则进入 settlement P3 主线阶段复盘，或转入下一条非 bridge 后端能力评估。

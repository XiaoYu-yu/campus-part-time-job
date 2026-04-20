# Step 66 - settlement 对账差异前端最小只读承接

## 本轮目标

1. 基于 Step 65 的 go / no-go 结论，把 settlement 对账差异记录接入现有 admin settlement 只读运营页。
2. 只在 `CampusSettlementOpsView.vue` 详情 drawer 中增加只读区，不新增页面、不新增路由。
3. 只接入列表读取接口，不接入 create / resolve 写操作。
4. 保持 bridge、鉴权、token 附着、settlement payout 摘要和后端接口不变。

## 实际完成项

### 1. admin API 封装

`frontend/src/api/campus-admin.js` 新增：

- `getCampusSettlementReconcileDifferences(params)`

接口路径：

- `GET /api/campus/admin/settlements/reconcile-differences`

本轮只封装列表读取；没有封装 detail / create / resolve，因为当前页面只做 settlement 详情 drawer 内的最小只读承接。

### 2. settlement 详情 drawer 增加对账差异只读区

`frontend/src/views/CampusSettlementOpsView.vue` 已新增“对账差异记录”区：

1. 打开 settlement 详情时，继续读取：
   - `GET /api/campus/admin/settlements/{id}`
2. 同时按当前 settlement id 读取：
   - `GET /api/campus/admin/settlements/reconcile-differences?settlementRecordId={id}&page=1&pageSize=10`
3. 只读展示：
   - `id`
   - `payoutBatchNo`
   - `differenceType`
   - `expectedAmount`
   - `actualAmount`
   - `differenceRemark`
   - `processStatus`
   - `processResult`
   - `source`
   - `reportedAt`
   - `processedAt`
4. 空态文案：
   - “暂无对账差异记录，当前 settlement 仍按原 payout 摘要展示”
5. 错误态：
   - 只在 drawer 内提示差异记录加载失败，不影响 settlement 详情主信息展示。

### 3. 展示与兼容口径

本轮明确保持以下兼容策略：

1. `CampusSettlementOpsView.vue` 原 settlement 摘要、列表、详情读取逻辑不变。
2. 对账差异记录只作为审计只读信息展示，不改变 `payoutStatus / payoutBatchNo / pendingAmount / payoutVerified*`。
3. `reconcile-summary`、批次页、批次操作审计页不受影响。
4. 当前页面不提供 create / resolve 操作，避免把只读运营页变成完整财务处理台。

## 明确没有做

1. 没有新增第五个 admin 页。
2. 没有新增路由。
3. 没有新增后端接口。
4. 没有接入 `POST /api/campus/admin/settlements/reconcile-differences`。
5. 没有接入 `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`。
6. 没有修改 settlement 查询、分页、筛选语义。
7. 没有修改 settlement payout 摘要、批次操作审计或 `reconcile-summary`。
8. 没有修改 bridge、`request.js`、token 附着、鉴权、路由或旧外卖模块。

## 验证结果

- `npm run build`：通过。
- `git diff --check`：通过。
- 本轮未改后端代码，未运行后端 compile。

## Step 67 运行态验证补充

- H2/test 下已准备 `PBSTEP67UI` / settlement `id=1` 的对账差异样本。
- `/campus/settlements` 列表可显示 `CR202604060001`，详情 drawer 可展示“对账差异记录”只读区。
- 浏览器内验证 `GET /api/campus/admin/settlements/reconcile-differences?settlementRecordId=1&page=1&pageSize=10` 返回 `total = 1`。
- 验证证据：
  - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-ui-api-prep.json`
  - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-page-validation.json`
  - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-drawer.png`

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、`request.js`、`campus-courier.js`、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 67 建议做 settlement 对账差异前端运行态验证。
2. 优先在 H2/test 下复用或重新生成 `PBSTEP64RECON` 类样本。
3. 验证 `/campus/settlements` 打开 settlement 详情 drawer 后能看到对账差异记录。
4. 验证空态、错误态和 settlement payout 摘要不变。

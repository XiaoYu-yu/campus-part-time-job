# Step 65 - settlement 对账差异前端承接 go / no-go

## 本轮目标

1. 基于 Step 64 已完成的 settlement 对账差异最小后端闭环，评估是否需要前端最小承接。
2. 不直接写页面，不新增第五个 admin 页。
3. 只做 go / no-go 判断，并明确下一轮边界。

## 当前起点

Step 64 已完成：

1. `campus_settlement_reconcile_difference_record` 已落地。
2. admin 已有对账差异列表、详情、创建、resolve 四个接口。
3. H2/test 已验证 `PBSTEP64RECON` 下创建、查询、resolve、重复 resolve 失败。
4. settlement payout 摘要、`reconcile-summary` 和现有批次操作审计语义未被改写。

当前前端状态：

1. `CampusSettlementOpsView.vue` 已提供 settlement 单笔列表、摘要和详情 drawer。
2. `CampusSettlementBatchDetail.vue` 已提供批次汇总、批次明细和批次操作历史只读区。
3. `campus-admin.js` 尚未封装 reconcile-differences 相关接口。
4. 当前没有 settlement 对账差异前端承接。

## 方向 A：在现有 settlement 只读运营页做最小承接

建议落点：

- `frontend/src/views/CampusSettlementOpsView.vue`

建议形式：

1. 在现有 settlement 详情 drawer 内增加“对账差异记录”只读区。
2. 打开单笔 settlement 详情时，按 `settlementRecordId` 调用：
   - `GET /api/campus/admin/settlements/reconcile-differences?settlementRecordId={id}`
3. 展示最小字段：
   - `id`
   - `payoutBatchNo`
   - `differenceType`
   - `expectedAmount`
   - `actualAmount`
   - `differenceRemark`
   - `processStatus`
   - `processResult`
   - `reportedAt`
   - `processedAt`
   - `source`
4. 只读展示，不提供创建或 resolve 操作。
5. 新增最小 API 封装：
   - `getCampusSettlementReconcileDifferences`
   - 如详情 drawer 暂不需要单条差异详情，则先不封装 detail / create / resolve。

### 收益

1. 能把 Step 64 后端能力变成 admin 可见的最小展示闭环。
2. 不新增页面、不新增路由，符合“第五个 admin 页继续后置”。
3. `CampusSettlementOpsView.vue` 天然具备 `settlementRecordId` 和 settlement 当前 payout 摘要，最适合解释“差异记录不改 payout 摘要”的兼容策略。
4. 只读承接不会引入财务写操作、状态处理或误操作风险。

### 风险

1. drawer 信息量会增加，需要控制展示层级，避免把 settlement 详情变成复杂财务后台。
2. 如果后续立刻加入 create / resolve 按钮，容易突破只读运营页边界。
3. 需要避免与 `CampusSettlementBatchDetail.vue` 的批次操作历史概念混淆。

### 风险控制

1. Step 66 若实现，只做只读区。
2. 不接入 `POST /reconcile-differences`。
3. 不接入 `POST /reconcile-differences/{id}/resolve`。
4. 不新增路由，不新增页面。
5. 不修改 settlement payout、batch operation 或 `reconcile-summary` 逻辑。

## 方向 B：暂不做前端承接，收住 settlement 对账差异线

### 收益

1. 避免继续扩 settlement 页面信息量。
2. 保持当前已验证后端能力为 API 层交付，不增加前端维护成本。
3. 可以更快转向下一条非 bridge 后端能力。

### 缺点

1. Step 64 新增的对账差异能力只能通过 API 验证和日志解释，演示时不够直观。
2. admin 用户无法在现有页面看到差异记录，会让“对账差异已落地”的运营价值偏弱。
3. settlement 只读运营页已具备详情 drawer，上线一个只读区的边际风险较低，完全不承接会浪费已有上下文。

## 方向 C：放到批次详情页

不作为 Step 66 优先落点。

原因：

1. `CampusSettlementBatchDetail.vue` 当前已经承接批次操作历史，主要讲批次汇总与操作审计。
2. 对账差异虽然带 `payoutBatchNo`，但最小解释粒度是单笔 settlement 记录。
3. 先在 `CampusSettlementOpsView.vue` 的单笔详情里展示更容易保持“差异不改 payout 摘要”的语义清楚。
4. 后续如有需要，再评估批次详情页是否增加 batch-level 差异汇总。

## Go / no-go 结论

本轮选择方向 A：建议 Step 66 进入现有 `CampusSettlementOpsView.vue` 的 settlement 对账差异只读承接实现。

选择原因：

1. 后端闭环已通过 Step 64 运行态验证。
2. 没有前端承接会让对账差异只能停留在 API 层，不利于试运营演示和交接。
3. 现有 settlement 详情 drawer 是最小、低风险、上下文最准确的承接位置。
4. 只读区实现不需要新增页面、路由或写操作，也不触 bridge。

## Step 66 建议边界

Step 66 只允许：

1. `frontend/src/api/campus-admin.js` 新增 `getCampusSettlementReconcileDifferences(params)`。
2. `frontend/src/views/CampusSettlementOpsView.vue` 的详情 drawer 增加“对账差异记录”只读区。
3. 打开详情时按 `settlementRecordId` 加载差异记录。
4. 展示 `OPEN / RESOLVED`、差异类型、金额、来源、处理结果和时间字段。
5. 空态明确提示“暂无对账差异记录”。

Step 66 明确不做：

1. 不新增第五个 admin 页。
2. 不新增路由。
3. 不接入 create / resolve 写操作。
4. 不改 settlement 查询、分页或筛选语义。
5. 不改后端接口。
6. 不改 bridge、鉴权、token 附着或旧外卖模块。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、`request.js`、`campus-courier.js`、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 66 已按本轮边界完成 settlement 对账差异只读前端承接。
2. 承接位置为 `CampusSettlementOpsView.vue` 详情 drawer。
3. 已新增 `getCampusSettlementReconcileDifferences(params)` API 封装。
4. 已明确没有接入 create / resolve 写操作。
5. Step 67 建议继续做 H2/test 下的页面运行态验证，确认差异记录能在 drawer 中真实可见。

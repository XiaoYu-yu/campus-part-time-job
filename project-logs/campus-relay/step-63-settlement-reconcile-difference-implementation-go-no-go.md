# Step 63 - settlement 对账差异记录实现 go / no-go

## 本轮目标

1. 基于 Step 62 的 settlement 对账差异记录最小方案，判断是否进入最小后端实现。
2. 只做 go / no-go，不写 SQL、Java、Vue 或页面。
3. 明确 Step 64 如果进入实现，边界必须控制在表、后端最小接口和兼容策略内。
4. 继续保持 bridge 主线冻结、展示 polish 线冻结、媒体线收住。

## 真实起点

Step 62 已明确：

1. 建议新增独立 `campus_settlement_reconcile_difference_record`。
2. 差异来源第一版只做 `MANUAL_ADMIN / SIMULATED_RECONCILE`。
3. 最小处理状态只做 `OPEN / RESOLVED`。
4. 差异 resolve 不改 settlement payout 摘要、不改订单主状态、不触发真实财务。
5. 现有 `campus_settlement_record` 继续作为单笔 settlement、批次列表、批次详情和 `reconcile-summary` 的兼容读取基础。

## 候选方向 A：进入对账差异记录最小后端实现

### 建议实现范围

1. 新增 `campus_settlement_reconcile_difference_record`。
2. 同步 MySQL init、migration、H2 schema。
3. 新增 admin 差异记录最小接口：
   - `GET /api/campus/admin/settlements/reconcile-differences`
   - `GET /api/campus/admin/settlements/reconcile-differences/{id}`
   - `POST /api/campus/admin/settlements/reconcile-differences`
   - `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`
4. 列表支持最小筛选：
   - `payoutBatchNo`
   - `settlementRecordId`
   - `relayOrderId`
   - `differenceType`
   - `processStatus`
   - `page`
   - `pageSize`
5. 创建接口只允许 admin 手工登记或模拟对账差异，不自动从银行流水生成。
6. resolve 只允许 `OPEN -> RESOLVED`。

### 必须保持的兼容边界

1. 不修改 `campus_settlement_record.payout_status`。
2. 不修改 `campus_settlement_record.payout_batch_no`。
3. 不修改 `campus_settlement_record.pending_amount / gross_amount / platform_commission`。
4. 不修改 `payout_verified` 与 `payout_verify_remark`。
5. 不修改订单主状态。
6. 不修改 settlement 批次操作审计表。
7. 不修改 `reconcile-summary` 的现有聚合语义。

### 收益

1. 补齐 settlement 对账从“聚合摘要”到“可追踪差异项”的最小缺口。
2. 与 Step 57 到 Step 61 的批次操作审计形成互补：
   - 批次操作审计记录 review / withdraw。
   - 对账差异记录具体差异、处理状态和处理备注。
3. 不需要真实支付、银行流水或自动打款重试。
4. 可以先以后端接口验证，不急于新增前端页面。
5. 风险可以通过“不改 payout 摘要”控制在独立审计模型内。

### 风险

1. 创建接口如果放得过宽，可能产生无法关联 settlement 的自由文本记录。
2. resolve 如果误改 payout 摘要，会破坏现有 settlement 演示页。
3. 如果 Step 64 同时做前端页面，会扩大实现范围。
4. 如果自动生成差异，会把范围扩大成对账引擎。

### 风险控制

1. Step 64 创建接口必须校验 `settlementRecordId` 真实存在。
2. 若请求携带 `payoutBatchNo`，必须与 settlement 当前 `payoutBatchNo` 一致。
3. resolve update 只写差异记录自己的处理字段。
4. Service 层禁止调用 settlement payout 结果更新逻辑。
5. Step 64 不新增前端页面。

## 候选方向 B：继续补方案，不进入实现

### 收益

1. 零实现风险。
2. 可以继续打磨字段和枚举细节。

### 缺点

1. Step 62 已经明确了最小表、状态、接口和兼容边界，继续设计的边际收益较低。
2. 当前 settlement 线最后一个明显后端缺口仍无法进入验证。
3. 批次操作审计已经完成后端、前端和运行态验证，对账差异再停留设计会让 P3 线不完整。

## Go / no-go 结论

本轮选择方向 A：建议 Step 64 进入 `campus_settlement_reconcile_difference_record` 最小后端实现。

选择原因：

1. Step 62 已把数据模型、状态、接口和兼容策略收敛到足够小。
2. 实现可以完全独立于现有 settlement payout 摘要，不必修改已验证链路。
3. Step 64 可限定为后端最小闭环，不新增页面，不接真实财务。
4. 风险点已有明确控制方式：校验 settlement 存在、校验 batchNo、只更新差异记录自身字段。

## Step 64 实现边界

Step 64 只允许实现：

1. `campus_settlement_reconcile_difference_record` 表。
2. MySQL init、migration、H2 schema 同步。
3. entity / DTO / query / VO / mapper / service / controller 最小代码。
4. admin 差异列表、详情、创建、resolve 四个最小接口。
5. `OPEN -> RESOLVED` 状态保护。
6. H2/test 下通过接口制造并处理一条差异记录。

Step 64 明确不做：

1. 不新增前端页面。
2. 不新增第五个 admin 页。
3. 不修改 settlement payout 摘要。
4. 不修改 `reconcile-summary` 语义。
5. 不自动改金额。
6. 不接真实财务或银行流水。
7. 不修改订单主状态。
8. 不修改 bridge、鉴权、token 附着或路由。

## Step 64 验证建议

至少验证：

1. `.\mvnw.cmd -DskipTests compile` 通过。
2. `npm run build` 通过。
3. H2/test profile 启动成功。
4. 使用现有 settlement 记录创建一条 `OPEN` 差异。
5. 列表接口能查到该差异。
6. 详情接口能回读该差异。
7. resolve 后状态变为 `RESOLVED`。
8. 重复 resolve 必须失败。
9. 原 settlement 详情、批次详情和 `reconcile-summary` 不被改写。
10. `git diff --check` 通过。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 本轮明确没有做

1. 没有写 SQL migration。
2. 没有写 Java 业务代码。
3. 没有改 Vue 页面。
4. 没有新增接口实现。
5. 没有新增前端页面。
6. 没有接真实财务。
7. 没有修改 settlement payout 摘要。
8. 没有修改 bridge、鉴权、token 附着或路由。
9. 没有修改旧外卖模块。

## 下一轮建议

1. Step 64 进入 settlement 对账差异记录最小后端实现。
2. 只落表和 admin 最小接口，不并发做前端承接。
3. 实现完成后先做 H2/test 运行态验证，再决定是否前端承接。

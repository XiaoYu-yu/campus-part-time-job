# Step 76 - 模拟资金链路产品化边界说明

## 本轮目标

1. 基于 Step 75 的试运营 preflight 手册，补一份独立的模拟资金链路产品化边界说明。
2. 明确模拟支付、模拟退款、模拟打款、settlement、批次操作审计和对账差异的边界。
3. 保持 bridge、展示 polish、媒体线、地图线和非 bridge 后端三线的既有冻结 / 收住口径。
4. 本轮不改业务代码、不新增接口、不改路由、不改鉴权、不新增页面。

## 为什么需要独立成文

Step 75 已经说明试运营如何启动和检查，但资金链路容易被误解为“已经具备真实支付 / 退款 / 打款能力”。本轮将资金相关能力独立成文，目的不是扩功能，而是把当前能力的产品化边界写清：

1. 当前资金能力是试运营模拟链路。
2. 当前资金能力服务演示、联调和运营审计。
3. 当前资金能力不是生产级财务系统。
4. 后续如果接入真实公司能力，需要重新进入独立产品化阶段。

## 实际完成内容

### 新增模拟资金边界文档

新增：

`docs/simulated-funds-boundary.md`

该文档覆盖：

1. 模拟支付边界。
2. 售后执行与模拟退款边界。
3. settlement 与模拟打款边界。
4. 批次操作审计边界。
5. 对账差异边界。
6. 与现有 latest / payout / after-sale 摘要字段的兼容关系。
7. customer / courier / admin 的角色边界。
8. 试运营前检查项。
9. 未来接真实资金能力前的必要前置条件。

### 更新文档入口

更新：

1. `README.md`
2. `docs/README.md`
3. `docs/delivery-guide.md`
4. `docs/trial-operation-preflight.md`

更新目的：

1. 将模拟资金边界文档挂到当前交付入口。
2. 让试运营 preflight、交付说明和 README 中的资金口径保持一致。
3. 避免后续演示时把 mock-pay、售后执行或 settlement payout 误讲成真实资金能力。

## 当前资金链路边界

### 模拟支付

当前入口：

`POST /api/campus/customer/orders/{id}/mock-pay`

当前语义：

1. 只推进校园代送订单业务状态。
2. 不调用第三方支付。
3. 不生成真实支付流水。
4. 不涉及收银台、钱包、银行卡或支付回调。

### 售后执行 / 模拟退款

当前入口：

1. `POST /api/campus/admin/orders/{id}/after-sale-execution`
2. `GET /api/campus/admin/orders/after-sale-executions`
3. `GET /api/campus/admin/after-sale-execution-records`

当前语义：

1. 售后决策和执行结果是运营记录。
2. `decisionType = REFUND / COMPENSATE` 不代表真实退款到账。
3. `campus_after_sale_execution_record` 是审计历史，不是资金流水。
4. 当前不接真实退款通道，不接退款回调。

### settlement / 模拟打款

当前入口：

1. `GET /api/campus/admin/settlements`
2. `GET /api/campus/admin/settlements/{id}`
3. `POST /api/campus/admin/settlements/{id}/payout-result`
4. `POST /api/campus/admin/settlements/batch-payout-result`
5. `GET /api/campus/admin/settlements/payout-batches`
6. `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
7. `GET /api/campus/admin/settlements/reconcile-summary`
8. `POST /api/campus/admin/settlements/{id}/verify-payout`

当前语义：

1. `payout_status` 是模拟 payout 状态。
2. `payout_batch_no` 是模拟批次归集号。
3. `payout_reference_no` 是运营参考号，不是银行或第三方代付凭证。
4. 当前不接真实打款、银行账户、代付通道或真实财务对账。

### 批次操作审计

当前入口：

1. `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
2. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
3. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`

当前语义：

1. review / withdraw 只写操作审计。
2. review / withdraw 不改 `payout_status`。
3. withdraw 不清空 `payout_batch_no`。
4. withdraw 不触发真实财务撤回。

### 对账差异

当前入口：

1. `GET /api/campus/admin/settlements/reconcile-differences`
2. `GET /api/campus/admin/settlements/reconcile-differences/{id}`
3. `POST /api/campus/admin/settlements/reconcile-differences`
4. `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`

当前语义：

1. 差异记录是模拟对账审计。
2. resolve 只更新差异记录自身处理字段。
3. resolve 不改 settlement 金额。
4. resolve 不改 payout 摘要。
5. resolve 不触发真实财务处理。

## 兼容策略

1. `campus_relay_order` 上的售后、异常和执行摘要字段继续作为现有页面和接口的兼容读取来源。
2. `campus_settlement_record` 上的 payout 摘要继续服务 settlement 单笔页、批次页和 `reconcile-summary`。
3. 历史表和审计表作为审计主数据，不反向破坏旧摘要字段语义。
4. 本轮不要求修改任何前端页面、API 封装、路由或 token 附着逻辑。

## 明确没做

1. 没有修改 Vue 页面。
2. 没有修改 Java / SQL 业务代码。
3. 没有新增后端接口。
4. 没有新增路由。
5. 没有改 bridge、鉴权、token 附着、`request.js` 或 `campus-*` API 运行时行为。
6. 没有提交真实地图 key。
7. 没有接真实支付。
8. 没有接真实退款。
9. 没有接真实打款。
10. 没有接银行、支付网关、第三方清结算或真实财务系统。

## 验证结果

本轮是文档与口径整理轮，没有业务代码变更。

已执行：

1. `git diff --check` 通过。
2. 仓库 diff 中未发现腾讯地图 key 明文。

未执行：

1. `.\mvnw.cmd -DskipTests compile`，原因是本轮未修改 Java / SQL。
2. `npm run build`，原因是本轮未修改 Vue / JS / 路由 / API 封装。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮资金边界文档不涉及 bridge 行为变化，也不构成 bridge 恢复推进触发条件。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 77 建议进入“试运营交付文档一致性复核 / preflight 跑通验证”二选一：

1. 如果要继续文档线，优先检查 README、delivery-guide、trial-operation-preflight、simulated-funds-boundary 和 summary 的口径是否一致。
2. 如果要继续验证线，按 `docs/trial-operation-preflight.md` 跑一轮关键页面和关键接口 preflight。
3. 不建议直接接真实支付、真实退款、真实打款。
4. 不建议重开 bridge、展示 polish、媒体线或地图第二页。

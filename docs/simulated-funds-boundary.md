# 模拟资金链路产品化边界说明

## 定位

当前项目是“校园代送试运营版”，资金相关能力只用于本地试运营、演示、联调和运营留痕。本文用于明确模拟支付、模拟退款、模拟打款、settlement、售后执行和对账差异的产品化边界，避免后续把“模拟资金记录”误理解为真实财务系统。

当前结论：

1. 不接真实支付。
2. 不接真实退款。
3. 不接真实打款。
4. 不接银行、支付网关、第三方清结算或真实财务系统。
5. 资金相关字段只表达业务状态、运营记录和审计留痕。
6. 旧外卖模块的支付、订单、地址、购物车语义不在本文范围内修改。

## 总原则

1. 模拟支付是订单流程推进动作，不是真实扣款。
2. 售后执行是运营记录和审计动作，不是真实退款。
3. settlement 打款记录是模拟 payout 留痕，不是真实转账。
4. 批次复核、撤回和对账差异是运营审计，不是财务清分。
5. 所有金额字段仍保留两位小数和业务计算语义，但不代表真实账户余额。
6. 若未来接入真实支付、退款或打款，必须作为独立阶段重新设计凭证、回调、幂等、对账、安全和合规边界。

## customer 模拟支付

### 当前入口

- `POST /api/campus/customer/orders/{id}/mock-pay`

### 当前语义

1. customer 在订单创建后通过 mock-pay 推进订单支付状态。
2. mock-pay 只更新校园代送订单的业务状态和 payment 摘要。
3. mock-pay 不调用第三方支付网关。
4. mock-pay 不生成真实支付流水。
5. mock-pay 不涉及银行卡、钱包、余额、收银台或支付回调。

### 产品化边界

mock-pay 只能用于试运营链路闭环，目的是让订单从待支付进入可接单或楼栋优先等后续流程。它不能作为真实收款凭证，也不能用于生产财务结算。

## 售后执行与模拟退款

### 当前入口

- `POST /api/campus/admin/orders/{id}/after-sale-execution`
- `GET /api/campus/admin/orders/after-sale-executions`
- `GET /api/campus/admin/after-sale-execution-records`

### 当前语义

1. admin 售后决策后，可以记录售后执行结果。
2. 售后执行结果支持当前摘要和执行历史。
3. `decisionType = REFUND / COMPENSATE` 表示运营决策类型，不代表真实退款已经发生。
4. `after_sale_execution_status` 表示模拟执行记录状态，不代表支付渠道退款状态。
5. `campus_after_sale_execution_record` 是审计历史主数据，不是资金流水表。

### 产品化边界

售后执行记录只用于说明“运营上如何处理该售后”。当前不做真实退款，不接支付通道退款接口，不接回调，也不做退款到账确认。

若未来接入真实退款，至少需要新增：

1. 真实退款单号。
2. 支付渠道退款流水。
3. 退款申请、受理、成功、失败、回调状态。
4. 幂等控制。
5. 失败重试和人工复核。
6. 与售后执行记录之间的映射关系。

## settlement 与模拟打款

### 当前入口

- `GET /api/campus/admin/settlements`
- `GET /api/campus/admin/settlements/{id}`
- `POST /api/campus/admin/settlements/{id}/confirm`
- `POST /api/campus/admin/settlements/{id}/payout-result`
- `POST /api/campus/admin/settlements/batch-payout-result`
- `GET /api/campus/admin/settlements/reconcile-summary`
- `GET /api/campus/admin/settlements/payout-batches`
- `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
- `POST /api/campus/admin/settlements/{id}/verify-payout`

### 当前语义

1. `campus_settlement_record` 表达单笔结算记录和模拟 payout 摘要。
2. `payout_status` 表达模拟打款记录状态，当前最小值包括 `UNPAID / PAID / FAILED`。
3. `payout_batch_no` 表示模拟批次归集号，不代表真实付款批次。
4. `payout_reference_no` 表示运营记录中的参考号，不代表银行或第三方支付渠道凭证。
5. `payout_verified` 表示二次核对结果，不代表真实财务复核签章。

### 产品化边界

settlement 当前只用于演示“订单完成后如何形成配送员结算、如何记录模拟打款、如何做批次查看和核对”。它不是完整财务后台。

当前不做：

1. 真实打款。
2. 银行账户绑定。
3. 支付机构代付。
4. 财务凭证生成。
5. 真实对账文件导入。
6. 资金余额管理。
7. 税务、发票或合规审计。

## 批次操作审计

### 当前入口

- `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
- `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
- `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`

### 当前语义

1. `campus_settlement_batch_operation_record` 记录批次 review / withdraw 等运营操作。
2. review / withdraw 只写操作审计。
3. review / withdraw 不修改 settlement 的 `payout_status`。
4. withdraw 不清空 `payout_batch_no`。
5. withdraw 不触发真实撤回或资金退回。

### 产品化边界

批次操作审计只解释“管理员做过什么运营判断”。它不是资金回滚机制，也不是财务审批流。

## 对账差异

### 当前入口

- `GET /api/campus/admin/settlements/reconcile-differences`
- `GET /api/campus/admin/settlements/reconcile-differences/{id}`
- `POST /api/campus/admin/settlements/reconcile-differences`
- `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`

### 当前语义

1. `campus_settlement_reconcile_difference_record` 记录模拟对账差异。
2. 差异来源当前只用于 admin 手工或模拟对账场景。
3. 差异处理只更新差异记录自身状态。
4. resolve 不修改 settlement 金额。
5. resolve 不修改 `payout_status`。
6. resolve 不触发真实财务处理。

### 产品化边界

对账差异记录只用于试运营说明“发现差异、记录差异、处理差异”的运营闭环。它不是支付网关对账系统，也不是银行流水对账系统。

## 兼容关系

### 订单与售后摘要

订单表上的售后、异常和执行摘要字段继续作为现有页面与接口的兼容读取来源。新增历史表或审计表作为主审计数据，不反向破坏旧摘要语义。

### settlement payout 摘要

`campus_settlement_record` 上的 payout 摘要字段继续服务：

1. settlement 单笔页。
2. settlement 批次页。
3. reconcile-summary。
4. 只读运营展示。

批次操作审计和对账差异记录不会替代这些摘要字段。

### 前端展示

现有 customer / courier / admin 页面继续按当前接口读取模拟资金状态。本文不要求修改任何页面、路由或 token 附着逻辑。

## 角色边界

### customer

1. 可以触发 mock-pay。
2. 可以查看订单状态和 completed 结果。
3. 不直接参与 settlement、payout、批次复核或对账差异。

### courier

1. 可以通过 workbench 完成接单、取餐、deliver、异常上报。
2. 当前不操作 payout。
3. 当前不查看完整财务结算后台。

### admin

1. 可以记录售后执行结果。
2. 可以查看 settlement、批次、操作审计和对账差异。
3. 可以记录模拟 payout 结果和核对结果。
4. 当前 admin 的资金相关能力仍是运营记录，不是真实财务执行。

## 试运营前检查项

1. mock-pay 能推进订单进入后续流程。
2. 售后执行记录能写入当前摘要和历史记录。
3. settlement 单笔页能展示 payout 摘要。
4. settlement 批次页能展示批次汇总和明细。
5. 批次操作历史能只读展示 review / withdraw 审计。
6. 对账差异能创建、查看和 resolve，但不改 payout 摘要。
7. customer 结果页不展示任何真实付款凭证。
8. 文档和演示口径明确说明“模拟资金链路不等于真实财务系统”。

## 未来真实资金接入前置条件

未来如果需要进入真实资金产品化阶段，至少需要重新设计：

1. 支付渠道与退款渠道选型。
2. 支付、退款、打款真实流水表。
3. 第三方回调验签和幂等。
4. 真实对账文件或账单导入。
5. 财务审批、复核、撤回和重试流程。
6. 敏感配置与密钥管理。
7. 资金相关权限控制。
8. 生产审计日志。
9. 异常补偿和回滚预案。
10. 数据脱敏与合规边界。

在这些前置条件完成前，当前项目继续保持模拟资金链路，不对外承诺真实支付、退款或打款能力。

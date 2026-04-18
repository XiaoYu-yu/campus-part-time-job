# Step 61 - settlement 批次操作审计前端运行态验证

## 本轮目标

1. 基于 Step 60 的前端只读承接，做 H2/test + Vite + Playwright 运行态验证。
2. 准备真实 `payout_batch_no` 和两条批次操作审计记录。
3. 打开现有批次详情页，验证“批次操作历史”只读区真实渲染。
4. 验证原 settlement 明细表仍正常展示。

## 运行环境

1. 后端：test profile + H2，端口 `8080`。
2. 前端：Vite dev server，端口 `5173`。
3. 浏览器验证：Playwright。
4. admin 登录账号：`13800138000 / 123456`。
5. 结算记录：`campus_settlement_record.id = 1`。
6. 关联订单：`CR202604060001`。
7. 固定批次号：`PBSTEP61UI`。

## 实际执行步骤

### API 数据准备

1. 调用 `POST /api/employees/login` 获取 admin employee token。
2. 调用 `POST /api/campus/admin/settlements/1/confirm` 确认 H2 种子结算记录。
3. 调用 `POST /api/campus/admin/settlements/batch-payout-result`，写入固定批次号 `PBSTEP61UI`。
4. 调用 `POST /api/campus/admin/settlements/payout-batches/PBSTEP61UI/review` 写入：
   - `operationType = REVIEW`
   - `operationResult = PASSED`
   - `operationRemark = Step61 page review audit only`
5. 调用 `POST /api/campus/admin/settlements/payout-batches/PBSTEP61UI/withdraw` 写入：
   - `operationType = WITHDRAW`
   - `operationResult = REQUESTED`
   - `operationRemark = Step61 page withdraw audit only`
6. 调用 `/operations` 验证后端返回 2 条操作历史。

API 准备证据：

`project-logs/campus-relay/runtime/step-61/settlement-batch-operation-ui-api-prep.json`

### 页面验证

1. 打开 `http://127.0.0.1:5173/campus/settlement-batches/PBSTEP61UI`。
2. 注入 admin token 后进入现有批次详情页。
3. 验证页面包含固定批次号 `PBSTEP61UI`。
4. 验证页面包含 settlement 明细订单 `CR202604060001`。
5. 验证页面出现“操作历史”区域。
6. 验证页面显示共 2 条操作记录。
7. 验证页面可见 review 通过结果。
8. 验证页面可见 withdraw 已发起结果。
9. 验证页面没有显示操作历史加载失败。
10. 通过浏览器内 `fetch` 再次确认 `/operations` 返回：
    - `total = 2`
    - 包含 `REVIEW / PASSED`
    - 包含 `WITHDRAW / REQUESTED`

页面验证证据：

`project-logs/campus-relay/runtime/step-61/settlement-batch-operation-page-validation.json`

## 验证结果

已通过：

1. API 准备成功生成固定批次 `PBSTEP61UI`。
2. API 准备成功写入 `REVIEW / PASSED` 和 `WITHDRAW / REQUESTED`。
3. 页面 `/campus/settlement-batches/PBSTEP61UI` 能正常打开。
4. 页面能展示批次汇总。
5. 页面能展示 settlement 明细 `CR202604060001`。
6. 页面能展示“批次操作历史”只读区。
7. 页面能展示 2 条操作审计记录。
8. 页面未出现操作历史加载失败。

已额外确认：

1. 后端 `/operations` 在浏览器上下文中返回 `code = 200`。
2. 浏览器上下文中 `/operations` 返回 `total = 2`。
3. 浏览器上下文中 `/operations` 包含 `REVIEW / PASSED`。
4. 浏览器上下文中 `/operations` 包含 `WITHDRAW / REQUESTED`。

## 明确没有做

1. 没有修改 Java、SQL、Vue 业务代码。
2. 没有新增页面。
3. 没有新增路由。
4. 没有调用 review / withdraw 写接口之外的额外写操作。
5. 没有实现对账差异记录。
6. 没有接真实打款。
7. 没有修改 bridge、`request.js`、token 附着、鉴权或旧外卖模块。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 62 可以进入 settlement 对账差异记录最小方案设计。
2. 设计前先明确：
   - 对账差异是否需要独立表。
   - 差异来源是否只来自人工录入或模拟数据。
   - 是否保留当前 `campus_settlement_record` payout 摘要兼容读取。
   - 是否只做 admin 只读/最小处理接口，不接真实财务。
3. 不默认新增第五个 admin 页。
4. 不接真实支付、真实退款、真实打款。
5. 不重开 bridge 主线。

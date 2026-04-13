# Step 51A - 售后执行历史最小实现

## 本轮目标

1. 基于 Step 50 的售后执行历史表方案，落地最小后端实现。
2. 只实现：
   - `campus_after_sale_execution_record` 表。
   - 现有 admin 售后执行接口同事务写入历史。
   - admin 售后执行历史只读分页查询接口。
3. 不补前端页面，不做真实退款，不做 settlement 联动，不扩完整售后工单系统。
4. bridge 主线继续冻结，展示 polish 线继续冻结，媒体线继续收住。

## 实际落地内容

### 1. 新增售后执行历史表

新增表：`campus_after_sale_execution_record`

最终字段：

1. `id`
2. `relay_order_id`
3. `decision_type`
4. `decision_amount`
5. `previous_execution_status`
6. `execution_status`
7. `execution_remark`
8. `execution_reference_no`
9. `executed_by_employee_id`
10. `executed_at`
11. `corrected`
12. `created_at`
13. `updated_at`

索引：

1. `relay_order_id`
2. `execution_status`
3. `executed_at`
4. `corrected`

外键：

1. `relay_order_id -> campus_relay_order(id)`
2. `executed_by_employee_id -> employee(id)`

### 2. 数据库路径同步情况

已同步：

1. MySQL init：`backend/db/init.sql`
2. migration：`backend/db/migrations/V10__campus_after_sale_execution_record.sql`
3. H2 schema：`backend/src/main/resources/db/schema-h2.sql`

H2 data：

1. 本轮未在 `backend/src/main/resources/db/data-h2.sql` 预置售后执行历史样本。
2. 原因：售后执行历史应由真实 admin 执行动作生成，直接 seed 历史容易让“当前订单摘要”和“历史审计记录”在演示数据里脱节。
3. 本轮运行态验证已通过真实售后链路生成历史记录。

### 3. 现有售后执行接口接入历史写入

复用现有接口：

`POST /api/campus/admin/orders/{id}/after-sale-execution`

接入方式：

1. 保持接口路径不变。
2. 保持请求体 `CampusAdminAfterSaleExecutionDTO` 不变。
3. 保持现有状态转换校验不变。
4. 在现有订单摘要更新成功后，同一事务内插入一条 `campus_after_sale_execution_record`。
5. 插入记录保留：
   - 当前决策类型和金额快照。
   - 写入前执行状态。
   - 本次执行状态、备注、参考号。
   - 执行管理员和执行时间。
   - 是否为 `FAILED -> SUCCESS` 纠正。

同事务语义：

1. 如果订单摘要更新失败，历史不会写入。
2. 如果历史写入失败，订单摘要更新会随事务回滚。
3. 每次成功执行都新增历史，不覆盖旧历史。

### 4. 新增 admin 只读分页接口

新增接口：

`GET /api/campus/admin/after-sale-execution-records`

查询参数：

1. `relayOrderId`
2. `executionStatus`
3. `corrected`
4. `page`
5. `pageSize`
6. `size`

行为：

1. 只读分页查询。
2. `executionStatus` 仅支持 `SUCCESS / FAILED`。
3. `corrected=true` 可查询纠正记录。
4. 默认按 `executed_at desc, id desc` 排序。

返回字段：

1. 历史记录字段。
2. 关联订单的 `orderStatus`、`customerUserId`、`courierProfileId`。
3. 当前订单售后执行摘要字段：
   - `currentExecutionStatus`
   - `currentExecutionRemark`
   - `currentExecutionReferenceNo`
   - `currentExecutionCorrected`
   - `currentExecutionCorrectedAt`

## 兼容策略如何落地

继续保留订单表当前摘要字段：

1. `after_sale_execution_status`
2. `after_sale_execution_remark`
3. `after_sale_execution_reference_no`
4. `after_sale_executed_by_employee_id`
5. `after_sale_executed_at`
6. `after_sale_execution_corrected`
7. `after_sale_execution_corrected_by_employee_id`
8. `after_sale_execution_corrected_at`

旧读取路径继续读当前摘要：

1. `GET /api/campus/admin/orders/after-sale-executions`
2. `GET /api/campus/admin/orders/{id}/after-sale-result`
3. `GET /api/campus/customer/orders/{id}/after-sale-result`
4. 现有 admin 售后执行页和售后结果 drawer。

新读取路径读历史表：

1. `GET /api/campus/admin/after-sale-execution-records`

这样做的原因：

1. 不改变现有页面和接口响应语义。
2. 不改变售后执行状态机。
3. 历史表只追加审计能力。
4. 当前摘要仍然是前端和结果页的兼容读取面。

## 明确没做

1. 没有新增前端页面。
2. 没有新增售后执行历史详情页。
3. 没有新增售后执行历史修改、删除、回滚接口。
4. 没有记录失败校验请求。
5. 没有做真实退款。
6. 没有改 settlement。
7. 没有改订单主状态机。
8. 没有改 bridge。
9. 没有改 `request.js`。
10. 没有改 token 附着逻辑。
11. 没有改路由。
12. 没有补第五个 admin 页。

## 运行态验证

运行环境：

1. 后端：H2/test profile，端口 `8082`。
2. 验证订单：`CR202604060001`。
3. admin 账号：`13800138000 / 123456`。
4. customer 账号：`13900139000 / 123456`。

验证链路：

1. customer 对 `CR202604060001` 发起售后。
2. admin 处理售后为 `RESOLVED`。
3. admin 记录决策 `REFUND`。
4. admin 第一次记录执行 `FAILED`。
5. admin 查询售后执行历史，得到 1 条历史：
   - `executionStatus = FAILED`
   - `previousExecutionStatus = PENDING`
   - `corrected = 0`
6. admin 第二次记录执行 `SUCCESS`，参考号 `STEP51A-REFUND-001`。
7. admin 再次查询售后执行历史，得到 2 条历史：
   - 最新一条 `SUCCESS`
   - `previousExecutionStatus = FAILED`
   - `corrected = 1`
   - 旧一条 `FAILED`
8. admin 使用 `executionStatus=SUCCESS&corrected=true` 可筛出纠正成功记录。
9. 旧售后执行分页接口仍返回当前摘要：
   - `afterSaleExecutionStatus = SUCCESS`
   - `afterSaleExecutionCorrected = 1`
10. customer 售后结果回读仍可读取当前执行状态：
   - `afterSaleExecutionStatus = SUCCESS`

证据文件：

1. `project-logs/campus-relay/runtime/step-51a/after-sale-execution-history-validation.json`

## 验证结果

1. `.\mvnw.cmd -DskipTests compile`
   - 通过。
2. `.\mvnw.cmd -DskipTests package`
   - 通过。
3. `npm run build`
   - 通过。
   - 仍保留既有 Sass `@import` deprecation 与 chunk size warning。
4. H2/test 运行态验证：
   - 售后执行历史写入：通过。
   - `PENDING -> FAILED` 历史：通过。
   - `FAILED -> SUCCESS` corrected 历史：通过。
   - admin 历史分页查询：通过。
   - corrected 筛选：通过。
   - 旧售后执行摘要兼容：通过。
   - customer 售后结果兼容：通过。
5. `git diff --check`
   - 通过，仅有 Windows 工作区 LF/CRLF 提示。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未触碰 bridge、鉴权、token 附着逻辑、前端路由或旧前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 51B 可进入售后执行历史前端承接 go / no-go 评估轮。
2. 先判断 admin 是否需要最小前端承接：
   - 只读历史列表。
   - 详情 drawer。
   - 或仅保留 API 层用于联调。
3. 如果前端承接收益不足，可转向 P3 settlement 批次复核、撤回和对账方案设计。
4. 不要直接补页面，不要补第五个 admin 页。
5. bridge 主线继续冻结。
6. 展示 polish 线继续冻结。
7. 媒体线继续收住。

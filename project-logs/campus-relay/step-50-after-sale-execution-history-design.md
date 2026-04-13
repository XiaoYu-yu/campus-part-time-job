# Step 50 - 售后执行历史表最小方案设计

## 本轮目标

1. 基于 Step 49 后异常历史 / resolve / admin 异常页已稳定的事实，进入 P2 售后执行历史表方案设计。
2. 只设计售后执行历史的最小数据模型、写入时机、状态边界、接口边界和兼容策略。
3. 不写 Java 代码，不写 SQL migration，不新增前端页面，不修改现有售后执行接口语义。
4. bridge 主线继续保持 `Phase A no-op` 冻结态，展示 polish 线继续冻结，媒体线继续收住。

## 为什么 Step 50 选择售后执行历史表

1. 异常历史已经完成：
   - `campus_exception_record` 已落地。
   - courier 异常上报已写入历史。
   - admin 异常只读与最小 resolve 已落地。
   - `/campus/exceptions` 已完成运行态验证。
2. settlement 批次复核、撤回和对账属于更重的财务运营能力，风险和设计复杂度更高。
3. 售后执行当前已有 admin 执行接口和分页页，但只保留订单表上的当前执行结果与一次纠正标记，缺少完整执行历史。
4. 因此 P2 售后执行历史表是当前最合适的非 bridge 后端主线：收益明确，边界可控，并且能复用现有售后执行状态与接口。

## 当前真实代码现状

### 现有接口

1. `GET /api/campus/admin/orders/after-sale-executions`
   - 当前用于售后执行分页演示页。
   - 支持 `afterSaleExecutionStatus`、`decisionType`、`correctedOnly` 等查询条件。
   - 返回的是订单表上的当前售后执行摘要。
2. `POST /api/campus/admin/orders/{id}/after-sale-execution`
   - 当前用于 admin 记录售后执行结果。
   - 请求 DTO 为 `CampusAdminAfterSaleExecutionDTO`：
     - `executionStatus`
     - `executionRemark`
     - `executionReferenceNo`

### 现有执行状态

当前枚举 `CampusAfterSaleExecutionStatus` 包含：

1. `PENDING`
2. `SUCCESS`
3. `FAILED`
4. `NOT_REQUIRED`

当前执行接口只允许提交：

1. `SUCCESS`
2. `FAILED`

### 现有订单摘要字段

当前 `campus_relay_order` 已保存售后执行当前摘要：

1. `after_sale_execution_status`
2. `after_sale_execution_remark`
3. `after_sale_execution_reference_no`
4. `after_sale_executed_by_employee_id`
5. `after_sale_executed_at`
6. `after_sale_execution_corrected`
7. `after_sale_execution_corrected_by_employee_id`
8. `after_sale_execution_corrected_at`

这些字段目前支撑：

1. admin 售后执行分页页。
2. 售后结果 drawer。
3. customer / admin 侧售后结果回读。

### 现有状态转换

当前 service 已限制：

1. `NONE` 决策不需要执行。
2. `NOT_REQUIRED` 不允许记录执行结果。
3. 执行结果只能是 `SUCCESS` 或 `FAILED`。
4. `SUCCESS` 后不允许再修改。
5. `FAILED -> FAILED` 不允许重复记录失败。
6. `FAILED -> SUCCESS` 允许作为人工纠正，并写入 `after_sale_execution_corrected*` 字段。
7. `PENDING -> SUCCESS / FAILED` 是当前正常执行路径。

## 当前痛点

1. 当前订单表只能表达“最新执行结果”，无法保留完整执行轨迹。
2. `FAILED -> SUCCESS` 虽然有 corrected 标记，但无法看到失败记录本身的完整历史。
3. admin 售后执行分页页只能展示当前态，不能回答“之前执行过几次、谁执行、何时失败、后来如何纠正”。
4. 如果未来需要答辩说明售后执行审计，当前只能展示当前字段和纠正标记，审计证据不够完整。

## 最小目标

售后执行历史最小闭环只覆盖：

1. 每次 admin 成功记录售后执行结果时，新增一条执行历史。
2. 同一事务内继续更新订单表当前售后执行摘要。
3. `FAILED -> SUCCESS` 时，历史记录明确标记这是纠正记录。
4. admin 可通过最小只读接口查询执行历史。
5. 现有售后执行分页页、customer 结果回读和订单详情继续读取订单当前摘要，不被新历史表破坏。

本轮不覆盖：

1. 失败校验请求的安全审计。
2. 完整售后工单系统。
3. 多级审批。
4. 真实退款。
5. 前端新页面。
6. payment / settlement 状态联动。

## 数据模型边界

建议新增表：`campus_after_sale_execution_record`

### 必需字段

1. `id`
   - 主键。
2. `relay_order_id`
   - 对应 `campus_relay_order.id`。
3. `decision_type`
   - 执行时订单上的售后决策类型快照。
4. `decision_amount`
   - 执行时订单上的售后决策金额快照。
5. `previous_execution_status`
   - 写入前的执行状态。
6. `execution_status`
   - 本次写入的执行结果，最小只允许 `SUCCESS / FAILED`。
7. `execution_remark`
   - 本次执行备注。
8. `execution_reference_no`
   - 本次执行参考号。
9. `executed_by_employee_id`
   - 执行员工。
10. `executed_at`
   - 执行时间。
11. `corrected`
   - 是否为 `FAILED -> SUCCESS` 的纠正记录。
12. `created_at`
   - 记录创建时间。
13. `updated_at`
   - 记录更新时间。

### 当前不建议放入首版的字段

1. `process_status`
   - 售后执行历史是执行审计，不是异常处理工单；首版不再引入额外处理状态。
2. `admin_note`
   - 当前已有 `execution_remark`，首版不重复建处理备注。
3. `source / channel`
   - 当前写入来源只有 admin 售后执行接口，首版不需要。
4. `failure_reason_code`
   - 当前失败已由 `executionStatus = FAILED` 和 `executionRemark` 表达。
5. `request_payload`
   - 容易扩大为操作审计系统，首版不做。

### 最小索引建议

1. `relay_order_id`
2. `execution_status`
3. `executed_at`
4. `corrected`

如果 Step 51A 实现时需要分页查询，建议默认按 `executed_at desc, id desc` 排序。

## 写入时机

建议复用现有接口：

`POST /api/campus/admin/orders/{id}/after-sale-execution`

最小写入策略：

1. 先沿用现有 service 校验：
   - 决策存在。
   - 决策不是 `NONE`。
   - 执行结果为 `SUCCESS / FAILED`。
   - `REFUND + SUCCESS` 时参考号必填。
   - 状态转换合法。
2. 校验通过后，在同一事务内：
   - 新增 `campus_after_sale_execution_record`。
   - 更新 `campus_relay_order` 当前售后执行摘要字段。
3. 如果任一步失败，历史记录和订单摘要都不能只写一半。
4. 每次成功执行都新增历史，不覆盖旧历史。

首版不记录失败校验请求，原因：

1. 失败校验更接近系统操作审计，而不是售后执行业务历史。
2. 如果把失败请求也放进业务历史，会混淆“真实执行结果”和“非法请求尝试”。
3. 当前最小闭环优先保证成功执行轨迹可追溯。

## 状态边界

售后执行历史表首版不新增独立状态机，只复用当前执行状态：

1. `SUCCESS`
2. `FAILED`

`PENDING / NOT_REQUIRED` 仍然只作为订单当前摘要状态存在，不写入执行历史。

### 允许记录的历史序列

1. `PENDING -> SUCCESS`
   - 记录一条 `SUCCESS` 历史。
   - `corrected = 0`。
2. `PENDING -> FAILED`
   - 记录一条 `FAILED` 历史。
   - `corrected = 0`。
3. `FAILED -> SUCCESS`
   - 记录一条 `SUCCESS` 历史。
   - `previous_execution_status = FAILED`。
   - `corrected = 1`。

### 不允许扩展的状态流

1. 不允许 `SUCCESS -> FAILED`。
2. 不允许 `SUCCESS -> SUCCESS`。
3. 不允许 `FAILED -> FAILED`。
4. 不引入 `REOPENED`。
5. 不引入 `CANCELLED`。
6. 不引入多次纠正审批流。

这些限制继续由现有 service 状态校验负责，不因为新增历史表而改变。

## 接口边界

### 复用现有写接口

继续使用：

`POST /api/campus/admin/orders/{id}/after-sale-execution`

Step 51A 如果进入实现，只改变后端持久化策略：

1. 请求体不变。
2. 路径不变。
3. 前端调用方式不变。
4. 业务校验不变。
5. 同事务追加历史写入。

### 新增只读查询接口建议

建议首版新增 admin 只读接口：

1. `GET /api/campus/admin/after-sale-execution-records`
2. `GET /api/campus/admin/orders/{id}/after-sale-execution-records`

二者可以择一先做，建议 Step 51A 优先做全局分页接口：

`GET /api/campus/admin/after-sale-execution-records`

最小查询条件：

1. `relayOrderId`
2. `executionStatus`
3. `corrected`
4. `page`
5. `pageSize`

默认排序：

1. `executedAt desc`
2. `id desc`

返回字段建议：

1. `id`
2. `relayOrderId`
3. `decisionType`
4. `decisionAmount`
5. `previousExecutionStatus`
6. `executionStatus`
7. `executionRemark`
8. `executionReferenceNo`
9. `executedByEmployeeId`
10. `executedAt`
11. `corrected`
12. `createdAt`
13. `updatedAt`

### 当前明确不做的接口

1. 不新增售后执行历史修改接口。
2. 不新增删除历史接口。
3. 不新增 replay / rollback 接口。
4. 不新增 customer 售后执行历史接口。
5. 不新增 courier 售后执行接口。
6. 不新增前端页面接口聚合层。

## 兼容策略

### 保留订单当前摘要字段

继续保留并维护：

1. `campus_relay_order.after_sale_execution_status`
2. `campus_relay_order.after_sale_execution_remark`
3. `campus_relay_order.after_sale_execution_reference_no`
4. `campus_relay_order.after_sale_executed_by_employee_id`
5. `campus_relay_order.after_sale_executed_at`
6. `campus_relay_order.after_sale_execution_corrected`
7. `campus_relay_order.after_sale_execution_corrected_by_employee_id`
8. `campus_relay_order.after_sale_execution_corrected_at`

### 新旧读取分工

1. 现有 admin 售后执行分页页继续读订单当前摘要。
2. 现有售后结果 drawer 继续读订单当前摘要。
3. customer 结果回读继续读订单当前摘要。
4. 新增 admin 售后执行历史查询接口读 `campus_after_sale_execution_record`。
5. 后续如果要做 admin 历史页面，应明确展示“历史审计”，不替代当前售后执行分页页。

### 为什么这样风险最低

1. 不破坏已有前端页面。
2. 不改变已有接口响应语义。
3. 不改变售后执行状态机。
4. 历史表只追加审计能力，订单表继续承担当前状态摘要。
5. 与异常历史的兼容策略一致：新表作为历史主数据，旧字段作为 latest/current summary。

## 明确不做的范围

1. 不做真实退款。
2. 不做支付网关对接。
3. 不做售后完整工单系统。
4. 不做 customer 售后执行历史页面。
5. 不做 courier 售后参与流程。
6. 不做售后执行历史删除、回滚、重放。
7. 不做多角色审批。
8. 不做 settlement 联动。
9. 不做 admin 前端新页面。
10. 不做第五个 admin 页。
11. 不改 bridge。
12. 不改鉴权。
13. 不改路由。
14. 不改旧外卖模块。

## 风险点

1. 同事务一致性：
   - 如果历史写入和订单摘要更新不在同一事务，可能产生审计与当前摘要不一致。
2. 重复历史：
   - 必须依赖现有状态转换校验，避免 `FAILED -> FAILED` 或 `SUCCESS -> SUCCESS` 产生无意义历史。
3. H2 / MySQL 路径一致：
   - 如果 Step 51A 实现，必须同步维护 MySQL init、migration、H2 schema。
4. 前端误读：
   - 不能把历史接口误当成当前售后执行分页接口。
5. 纠正语义：
   - `corrected = 1` 应只表达 `FAILED -> SUCCESS`，不泛化成所有修改。

## 推荐 Step 51A 进入方式

如果进入实现，建议 Step 51A 只做后端最小实现：

1. 新增 `campus_after_sale_execution_record` 表。
2. 新增实体、Mapper、Service、VO、Query。
3. 复用现有 `POST /api/campus/admin/orders/{id}/after-sale-execution`，在同事务内追加历史写入。
4. 新增 admin 只读分页接口 `GET /api/campus/admin/after-sale-execution-records`。
5. 不做前端页面。
6. 不做修改 / 删除 / 回滚接口。
7. 不改订单主状态、settlement、bridge、路由或鉴权。

建议 Step 51A 验证至少覆盖：

1. `PENDING -> FAILED` 写入一条历史。
2. `FAILED -> SUCCESS` 再写入一条 corrected 历史。
3. 订单当前摘要为 `SUCCESS`。
4. 历史表有两条记录。
5. 现有售后执行分页页接口仍返回当前摘要。
6. 新历史分页接口能查到两条记录。
7. `.\mvnw.cmd -DskipTests compile` 通过。
8. `npm run build` 通过。
9. `git diff --check` 通过。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未触碰 bridge、鉴权、token 附着逻辑、前端路由或旧前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 51A 已完成售后执行历史最小实现。
2. 已新增 `campus_after_sale_execution_record`，并同步 MySQL init、V10 migration、H2 schema。
3. 已复用现有 `POST /api/campus/admin/orders/{id}/after-sale-execution`，在同事务内追加历史写入。
4. 已新增 admin 只读分页接口 `GET /api/campus/admin/after-sale-execution-records`。
5. 已通过 H2/test 运行态验证 `PENDING -> FAILED -> SUCCESS` 产生 2 条历史，且当前售后执行摘要保持兼容。
6. Step 51B 建议进入售后执行历史前端承接 go / no-go 评估轮，不默认补页面。
7. 不补第五个 admin 页。
8. bridge 主线继续冻结。
9. 展示 polish 线继续冻结。
10. 媒体线继续收住。

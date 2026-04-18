# Step 62 - settlement 对账差异记录最小方案设计

## 本轮目标

1. 基于 Step 61 已验证的 settlement 批次操作审计前端承接，进入 settlement 对账差异记录最小方案设计。
2. 只做方案设计，不写 SQL、Java、Vue 或页面。
3. 明确对账差异是否需要独立表、差异来源、处理状态、接口边界和兼容策略。
4. 继续保持 bridge 主线冻结、展示 polish 线冻结、媒体线收住。
5. 不接真实打款、不做真实撤回、不新增第五个 admin 页。

## 为什么 Step 62 进入对账差异设计

1. Step 57 到 Step 61 已完成 settlement 批次操作审计的后端实现、运行态验证、前端只读承接和页面运行态验证。
2. 当前 settlement 线已经具备：
   - 单笔 settlement 分页与详情。
   - 批次列表与批次详情。
   - 批次 review / withdraw 操作审计。
   - 批次操作历史只读展示。
3. Step 54 留下的 P3 方向中，尚未收敛的是“对账差异记录”。
4. 当前 `reconcile-summary` 只能展示聚合摘要，无法留存某一次具体差异、处理状态、处理备注和处理人。
5. 对账差异最容易被误扩成真实财务系统，所以本轮先定最小方案，不直接实现。

## 当前真实 settlement 基线

当前已有接口与模型：

1. `GET /api/campus/admin/settlements`
2. `GET /api/campus/admin/settlements/{id}`
3. `POST /api/campus/admin/settlements/{id}/confirm`
4. `POST /api/campus/admin/settlements/{id}/payout-result`
5. `POST /api/campus/admin/settlements/batch-payout-result`
6. `GET /api/campus/admin/settlements/reconcile-summary`
7. `GET /api/campus/admin/settlements/payout-batches`
8. `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
9. `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
10. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
11. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`

当前批次仍不是独立主表，而是通过 `campus_settlement_record.payout_batch_no` 聚合出来的逻辑批次。

## 当前痛点

1. `reconcile-summary` 是聚合摘要，不能记录具体差异项。
2. 单笔 `payout_verified` 可以表达二次核对结果，但无法表达“发现差异、原因、处理结果、处理备注”。
3. 批次操作历史可以记录 review / withdraw，但不是对账差异主数据。
4. 直接把差异处理写回 `campus_settlement_record` 会污染 settlement 当前摘要语义。
5. 如果对账差异直接改金额、改打款状态或重跑打款，会越界到真实财务或复杂财务后台。

## 最小目标

1. 新增独立对账差异记录作为审计主数据。
2. 差异记录只服务 admin 运营核查，不直接影响 customer / courier 主链路。
3. 差异处理只记录“已处理”和处理结果，不自动修改 settlement 金额、打款状态或批次号。
4. 继续保留 `campus_settlement_record` payout 摘要作为现有页面和接口的兼容读取基础。
5. 后续如果实现，先做最小后端接口，再判断是否需要前端只读承接。

## 数据模型边界

建议后续新增独立表：`campus_settlement_reconcile_difference_record`。

建议最小字段：

1. `id`
2. `payout_batch_no`
3. `settlement_record_id`
4. `relay_order_id`
5. `courier_profile_id`
6. `difference_type`
7. `expected_amount`
8. `actual_amount`
9. `difference_remark`
10. `process_status`
11. `process_result`
12. `process_remark`
13. `reported_by_employee_id`
14. `reported_at`
15. `processed_by_employee_id`
16. `processed_at`
17. `created_at`
18. `updated_at`

### 必需字段

1. `payout_batch_no`：用于批次维度对账和后续从批次详情回看差异。
2. `settlement_record_id`：用于回到单笔 settlement。
3. `relay_order_id`：用于运营人员直接识别订单。
4. `difference_type`：表达差异类型。
5. `process_status`：表达差异处理状态。
6. `reported_by_employee_id / reported_at`：形成最小审计。
7. `created_at / updated_at`：保持项目现有审计字段风格。

### 可为空字段

1. `expected_amount / actual_amount`：金额差异时使用，状态差异或备注类差异可为空。
2. `courier_profile_id`：建议从 settlement 记录冗余一份，便于 admin 过滤；如果实现时判断冗余过多，也可只通过 `settlement_record_id` 关联读取。
3. `process_result / process_remark / processed_by_employee_id / processed_at`：初始 `OPEN` 时为空。

### 不建议加入的字段

1. 不加入真实银行流水号。
2. 不加入真实打款渠道响应。
3. 不加入复杂审批人链路。
4. 不加入自动重试次数。
5. 不加入退款或撤回金额。

## 差异来源边界

当前最小方案只支持 admin 手工记录或模拟对账发现，不接真实财务系统。

建议最小来源：

1. `MANUAL_ADMIN`：admin 手工发现和登记。
2. `SIMULATED_RECONCILE`：后续若需要本地模拟对账脚本，可用该来源。

本轮不建议新增真实 `BANK_STATEMENT`、`PAYMENT_GATEWAY` 等来源，避免误导为真实财务接入。

## 差异类型边界

建议最小 `difference_type`：

1. `AMOUNT_MISMATCH`：应付金额与实际记录金额不一致。
2. `STATUS_MISMATCH`：结算/打款状态与人工核查结果不一致。
3. `UNVERIFIED_PAID`：已标记打款但未完成二次核对。
4. `FAILED_NEEDS_RETRY`：打款失败需要后续人工重试或观察。
5. `MANUAL_NOTE`：不适合归入金额/状态差异的人工备注类差异。

设计原因：

1. 覆盖当前模拟财务和试运营演示场景。
2. 不引入真实银行流水差异分类。
3. 不把 settlement 处理扩成完整财务对账平台。

## 状态边界

建议最小 `process_status`：

1. `OPEN`
2. `RESOLVED`

### OPEN

表示差异已登记，但尚未完成运营处理。

### RESOLVED

表示运营人员已经给出处理结果和备注。

### 当前不引入的状态

1. 不引入 `ACKNOWLEDGED`。
2. 不引入 `REOPENED`。
3. 不引入 `REJECTED` 主状态。
4. 不引入多级审批状态。

原因：

1. 当前目标是最小处理闭环，不是工单平台。
2. `OPEN / RESOLVED` 已足够表达“待处理 / 已处理”。
3. 如果要表达“无效差异”，建议通过 `process_result = MARKED_INVALID` 表达，而不是新增主状态。

## 处理结果边界

建议最小 `process_result`：

1. `CONFIRMED`
2. `MARKED_INVALID`
3. `FOLLOWED_UP`

说明：

1. `CONFIRMED`：确认差异属实，并已完成运营记录。
2. `MARKED_INVALID`：确认是误报或无需处理。
3. `FOLLOWED_UP`：已经人工跟进，但不在系统内自动改 settlement 摘要。

## 接口边界

如果 Step 63 进入实现评估，建议最小接口为：

1. `GET /api/campus/admin/settlements/reconcile-differences`
2. `GET /api/campus/admin/settlements/reconcile-differences/{id}`
3. `POST /api/campus/admin/settlements/reconcile-differences`
4. `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`

### 列表接口

最小筛选建议：

1. `payoutBatchNo`
2. `settlementRecordId`
3. `relayOrderId`
4. `differenceType`
5. `processStatus`
6. `page`
7. `pageSize`

默认排序：`reported_at desc`。

### 详情接口

至少返回：

1. 差异记录自身字段。
2. 关联 settlement 基础信息。
3. 关联订单号。
4. 当前 payout 摘要字段。
5. 处理状态与处理结果。

### 创建接口

最小请求字段：

1. `payoutBatchNo`
2. `settlementRecordId`
3. `differenceType`
4. `expectedAmount`
5. `actualAmount`
6. `differenceRemark`

创建时：

1. 校验 settlement 记录存在。
2. 校验 `payoutBatchNo` 与 settlement 当前批次一致；如允许手工备注类差异，可允许为空，但不建议第一版放开。
3. 初始写入 `process_status = OPEN`。
4. 记录当前 employee 作为 `reported_by_employee_id`。

### resolve 接口

最小请求字段：

1. `processResult`
2. `processRemark`

resolve 时：

1. 只允许 `OPEN -> RESOLVED`。
2. 已 `RESOLVED` 的差异不能重复处理。
3. 写入处理人、处理时间、处理结果和处理备注。
4. 不改 settlement 当前摘要。
5. 不改订单主状态。
6. 不触发真实财务动作。

## 与现有 payout 摘要兼容策略

继续保留并维持现有 `campus_settlement_record` 字段语义：

1. `payout_status`
2. `payout_batch_no`
3. `payout_remark`
4. `payout_reference_no`
5. `payout_recorded_by_employee_id`
6. `payout_recorded_at`
7. `payout_verified`
8. `payout_verified_by_employee_id`
9. `payout_verified_at`
10. `payout_verify_remark`

### 新旧读取关系

1. 现有 settlement 单笔页继续读 `campus_settlement_record`。
2. 现有 settlement 批次列表/详情继续按 `payout_batch_no` 聚合。
3. 现有 `reconcile-summary` 继续返回基于 settlement record 的聚合摘要。
4. 新增差异列表/详情才读 `campus_settlement_reconcile_difference_record`。
5. 差异 resolve 后不清空、不改写 settlement 的 payout 摘要字段。

### 为什么这样风险最低

1. 不破坏 Step 08 / Step 11 / Step 60 已有 settlement 前端展示。
2. 不破坏 Step 57 到 Step 61 已验证的批次操作审计链路。
3. 不把“对账差异已处理”误表达成“真实资金已调整”。
4. 允许后续独立展示差异处理状态，而不是污染 payout 状态。

## 明确不做的范围

1. 不接真实支付、真实退款、真实打款。
2. 不接银行流水。
3. 不做 settlement 金额自动调整。
4. 不做打款自动重试。
5. 不做真实财务撤回。
6. 不修改 `payout_status`。
7. 不清空 `payout_batch_no`。
8. 不修改订单主状态。
9. 不新增 customer / courier 接口。
10. 不新增前端页面。
11. 不新增第五个 admin 页。
12. 不改 bridge、`request.js`、token 附着或鉴权。
13. 不改旧外卖模块。

## 风险点

1. 如果差异 resolve 直接改 settlement payout 摘要，会和真实财务语义混淆。
2. 如果允许不关联 settlement 的自由文本差异，后续查询和回溯会变差。
3. 如果第一版就做自动生成差异，容易扩大到完整对账引擎。
4. 如果和前端页面并发实现，容易把方案轮变成财务后台开发轮。

## 推荐 Step 63 进入方式

建议 Step 63 做 go / no-go 评估，二选一：

1. 方向 A：进入 `campus_settlement_reconcile_difference_record` 最小后端实现。
2. 方向 B：继续停留在方案设计，补充差异创建和 resolve 的字段校验细节。

若选择方向 A，Step 63 / Step 64 应只允许实现：

1. 差异记录表。
2. MySQL init、migration、H2 schema 同步。
3. admin 差异列表、详情、创建、resolve 最小接口。
4. 不新增前端页面。
5. 不修改 settlement payout 摘要。
6. 不接真实财务。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 本轮明确没有做

1. 没有写 SQL migration。
2. 没有写 Java 业务代码。
3. 没有改 Vue 页面。
4. 没有新增后端接口。
5. 没有新增前端页面。
6. 没有接真实财务。
7. 没有修改 settlement payout 摘要。
8. 没有修改 bridge、鉴权、token 附着或路由。
9. 没有修改旧外卖模块。

## 下一轮建议

1. Step 63 先做 settlement 对账差异记录实现 go / no-go。
2. 如果确认实现，先落后端最小闭环，不并发做前端页面。
3. 继续保持不接真实财务、不改 payout 摘要、不新增第五个 admin 页。

## Step 63 回填

1. Step 63 已完成 settlement 对账差异记录实现 go / no-go。
2. 最终选择进入 `campus_settlement_reconcile_difference_record` 最小后端实现。
3. Step 64 边界已收敛为：
   - 表与数据库路径同步。
   - admin 列表、详情、创建、resolve 四个最小接口。
   - `OPEN -> RESOLVED` 状态保护。
4. Step 64 仍不允许新增前端页面、不接真实财务、不改 settlement payout 摘要、不改 bridge。

# Step 54 - settlement 批次复核、撤回和对账最小方案设计

## 本轮目标

1. 基于 Step 53 已验证的售后执行历史前端承接，进入 P3 settlement 批次复核、撤回和对账方案设计。
2. 只做方案设计，不写代码、不改库、不新增页面。
3. 明确批次复核、撤回、对账差异记录的最小边界。
4. 明确现有 `campus_settlement_record` 摘要字段、批次聚合能力和后续历史/审计模型之间的兼容关系。
5. 继续保持 bridge 主线冻结、展示 polish 线冻结、媒体线收住。

## 为什么 Step 54 选择 settlement P3

1. 异常历史与处理闭环已完成后端、前端和运行态验证。
2. 售后执行历史已完成后端、前端承接和运行态验证。
3. 当前剩余非 bridge 后端方向中，settlement 的批次复核、撤回和对账仍只有单笔/批次结果记录与只读展示，没有批次级审计边界。
4. settlement 与试运营交付关系更直接，但也更容易误伤“真实财务”语义，所以本轮先做方案而不是直接实现。

## 当前真实 settlement 能力

基于当前代码核查，已有能力如下：

1. 后端入口：`CampusAdminSettlementController`。
2. 单笔结算分页与详情：
   - `GET /api/campus/admin/settlements`
   - `GET /api/campus/admin/settlements/{id}`
3. 结算确认：
   - `POST /api/campus/admin/settlements/{id}/confirm`
4. 单笔打款结果记录：
   - `POST /api/campus/admin/settlements/{id}/payout-result`
5. 批量打款结果记录：
   - `POST /api/campus/admin/settlements/batch-payout-result`
6. 打款批次聚合查询：
   - `GET /api/campus/admin/settlements/payout-batches`
   - `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
7. 对账摘要：
   - `GET /api/campus/admin/settlements/reconcile-summary`
8. 二次核对：
   - `POST /api/campus/admin/settlements/{id}/verify-payout`

当前批次不是独立表，而是由 `campus_settlement_record.payout_batch_no` 聚合得到的逻辑批次。

## 当前痛点

1. 批次没有一等元数据表，无法记录批次级复核状态。
2. 批次撤回没有审计入口，不能区分“撤回申请”“撤回确认”“仅备注观察”。
3. 对账摘要只能给出聚合结果，不能记录具体差异项、处理状态和处理备注。
4. 单笔 `payout_verified` 可以表达二次核对，但不足以表达批次级复核结论。
5. 当前 `payout_status / payout_batch_no / payout_verified` 仍要作为演示页和旧接口的兼容摘要，不能因为新增审计模型就改掉语义。

## 最小目标

1. 先引入批次级审计/操作记录，而不是真实资金撤回。
2. 先引入对账差异记录，而不是银行流水对接。
3. 继续保留现有单笔 settlement 摘要字段。
4. 不清空、不删除、不重算已有打款批次。
5. 所有新能力优先服务 admin 运营审计，不影响 customer/courier 主链路。

## 数据模型草案

### 1. 批次操作审计表

建议后续新增 `campus_settlement_batch_operation_record`。

最小字段：

1. `id`
2. `payout_batch_no`
3. `operation_type`
4. `operation_result`
5. `operation_remark`
6. `operated_by_employee_id`
7. `operated_at`
8. `created_at`
9. `updated_at`

建议最小 `operation_type`：

1. `REVIEW_PASSED`
2. `REVIEW_REJECTED`
3. `WITHDRAW_REQUESTED`
4. `WITHDRAW_RECORDED`

建议最小 `operation_result`：

1. `SUCCESS`
2. `FAILED`
3. `OBSERVED`

设计原因：

1. 当前批次是逻辑批次，没有批次主表。
2. 操作审计表可以先记录批次级复核和撤回观察，不要求立即建立完整批次状态机。
3. 对已有批次列表/详情兼容风险最低。

### 2. 对账差异记录表

建议后续新增 `campus_settlement_reconcile_difference_record`。

最小字段：

1. `id`
2. `payout_batch_no`
3. `settlement_record_id`
4. `relay_order_id`
5. `difference_type`
6. `expected_amount`
7. `actual_amount`
8. `process_status`
9. `process_result`
10. `process_remark`
11. `reported_by_employee_id`
12. `reported_at`
13. `processed_by_employee_id`
14. `processed_at`
15. `created_at`
16. `updated_at`

建议最小 `difference_type`：

1. `AMOUNT_MISMATCH`
2. `STATUS_MISMATCH`
3. `UNVERIFIED_PAID`
4. `FAILED_NEEDS_RETRY`
5. `MANUAL_NOTE`

建议最小 `process_status`：

1. `OPEN`
2. `RESOLVED`

设计原因：

1. `reconcile-summary` 继续负责聚合摘要。
2. 差异记录表负责可追踪的差异项与处理结果。
3. 不把对账差异强行塞回 `campus_settlement_record`，避免污染单笔结算摘要。

## 状态边界草案

### 批次复核

1. 当前不建议新增复杂批次状态机。
2. 批次复核先以操作记录表达结果。
3. `REVIEW_PASSED` 表示本批次运营复核通过。
4. `REVIEW_REJECTED` 表示本批次运营复核未通过，需要人工备注原因。
5. 不因为复核通过自动改 `payout_verified`。
6. 单笔 `payout_verified` 仍由现有 `verify-payout` 维护。

### 批次撤回

1. 当前不做真实资金撤回。
2. 当前不自动把 `PAID` 改回 `UNPAID`。
3. 当前不删除 `payout_batch_no`。
4. 撤回先作为批次操作审计记录存在。
5. 若后续确需模拟撤回，只允许在明确未核对、非真实打款场景下单独设计。

### 对账差异

1. 差异项初始为 `OPEN`。
2. 人工处理后变为 `RESOLVED`。
3. 不引入 `ACKNOWLEDGED`、`REOPENED` 或多级审批。
4. 差异处理不影响订单主状态。
5. 差异处理不直接改 settlement 单笔打款结果。

## 接口边界草案

### 批次操作只读/写入

建议后续最小接口：

1. `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
2. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
3. `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`

边界：

1. review / withdraw 只写批次操作记录。
2. 不修改订单主状态。
3. 不修改 `payout_status`。
4. 不清空 `payout_batch_no`。
5. 不触发真实财务。

### 对账差异只读/写入

建议后续最小接口：

1. `GET /api/campus/admin/settlements/reconcile-differences`
2. `GET /api/campus/admin/settlements/reconcile-differences/{id}`
3. `POST /api/campus/admin/settlements/reconcile-differences`
4. `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`

边界：

1. 只记录差异和处理结果。
2. 不自动改结算金额。
3. 不自动重跑打款。
4. 不接真实银行流水。
5. 不新增 customer/courier 接口。

## 兼容策略

### 继续保留的现有摘要字段

`campus_settlement_record` 继续保留并维持现有语义：

1. `settlement_status`
2. `payout_status`
3. `payout_batch_no`
4. `payout_remark`
5. `payout_reference_no`
6. `payout_recorded_by_employee_id`
7. `payout_recorded_at`
8. `payout_verified`
9. `payout_verified_by_employee_id`
10. `payout_verified_at`
11. `payout_verify_remark`

### 新旧读取关系

1. 现有 settlement 单笔页继续读 `campus_settlement_record`。
2. 现有 settlement 批次列表/详情继续按 `payout_batch_no` 聚合。
3. 现有 `reconcile-summary` 继续返回聚合摘要。
4. 新的批次操作接口读 `campus_settlement_batch_operation_record`。
5. 新的对账差异接口读 `campus_settlement_reconcile_difference_record`。
6. 前端现有 settlement admin 页面不需要立即改动。

### 为什么这样风险最低

1. 不破坏 Step 08 和 Step 11 已有 admin settlement 演示页。
2. 不改变已经验证过的单笔/批量打款结果记录语义。
3. 不让“撤回”误解为真实财务撤回。
4. 不把对账差异和结算摘要混在同一字段里。
5. 可以分两步落地：先批次操作审计，再对账差异记录。

## 明确不做的范围

1. 不做真实打款。
2. 不做真实撤回。
3. 不接银行流水。
4. 不做完整财务后台。
5. 不做 settlement 自动重算。
6. 不修改订单主状态。
7. 不修改 customer/courier 前台页面。
8. 不新增第五个 admin 页面。
9. 不改 bridge。
10. 不改 `request.js`。
11. 不改 token 附着逻辑。
12. 不改旧外卖模块。

## 风险点

1. 如果直接新增批次主表，需要迁移已有逻辑批次，风险较高。
2. 如果撤回直接改 `payout_status`，容易破坏已验证的打款摘要和批次列表。
3. 如果对账差异直接修改 settlement 金额，可能引入真实财务语义，本阶段不适合。
4. 如果同时做批次操作和差异处理实现，范围容易扩大，建议拆分。

## 推荐 Step 55 进入方式

建议 Step 55 做 go / no-go 评估，二选一：

1. 方向 A：先实现 `campus_settlement_batch_operation_record`，只做批次复核/撤回操作审计。
2. 方向 B：先继续补对账差异记录方案细节，暂不写代码。

默认更推荐方向 A，前提是：

1. 不改现有 `campus_settlement_record` 摘要语义。
2. 不改批次列表/详情聚合查询。
3. review / withdraw 只写操作审计。
4. 不做真实财务撤回。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## Step 55 回填

1. Step 55 因用户要求先处理旧外卖文档清理，暂未进入 settlement 批次操作审计实现。
2. Step 55 已完成旧外卖文档归档和当前文档入口重写。
3. Step 56 可回到本设计建议，继续评估 `campus_settlement_batch_operation_record` 是否进入最小实现。

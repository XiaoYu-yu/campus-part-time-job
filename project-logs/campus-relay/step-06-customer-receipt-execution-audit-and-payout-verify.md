# Step 06 - customer 售后结果回执 / admin 执行纠正审计 / settlement 批次核对

## 本次目标

1. 为 customer 增加售后结果查询与最小回执
2. 为 admin 增加售后执行结果分页与人工纠正审计查询
3. 为 settlement 增加批次列表、批次详情和二次核对
4. 保持旧系统与现有 campus 状态机稳定，不改 `frontend/`

## 已完成项

1. 新增 `GET /api/campus/customer/orders/{id}/after-sale-result`
2. customer 售后回执使用 VO 计算字段输出：
   - `customerReceiptStatus`
   - `customerReceiptTitle`
   - `customerReceiptMessage`
   - `lastUpdatedAt`
3. 新增 `GET /api/campus/admin/orders/after-sale-executions`
4. 为订单新增售后执行纠正审计字段：
   - `after_sale_execution_corrected`
   - `after_sale_execution_corrected_at`
   - `after_sale_execution_corrected_by_employee_id`
5. 将 `FAILED -> SUCCESS` 一次人工纠正规则收口到 service 层：
   - 初次 `SUCCESS` 不算 corrected
   - 仅这一次人工纠正会把 corrected 置为 `1`
   - 不允许 `SUCCESS -> FAILED`
6. 扩展 `GET /api/campus/admin/orders/{id}/after-sale-result`
   - 回显 corrected 标记与纠正审计字段
7. 为 `campus_settlement_record` 新增：
   - `payout_batch_no`
   - `payout_verified`
   - `payout_verified_by_employee_id`
   - `payout_verified_at`
   - `payout_verify_remark`
8. 扩展 `GET /api/campus/admin/settlements`
   - 支持 `payoutVerified`
   - 支持 `payoutBatchNo`
9. 新增 `GET /api/campus/admin/settlements/payout-batches`
10. 新增 `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
11. 新增 `POST /api/campus/admin/settlements/{id}/verify-payout`
12. batch payout 未传 `batchNo` 时，service 层会自动生成统一批次号，并且只写入成功处理的 settlement 记录
13. `GET /api/campus/admin/settlements/{id}` 与批次详情接口均可查看：
   - `payoutVerified`
   - `payoutVerifiedAt`
   - `payoutVerifyRemark`

## 主要修改文件

- `backend/db/init.sql`
- `backend/db/migrations/V7__campus_customer_receipt_and_settlement_audit.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusStep06IntegrationTest.java`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 新增的主要类型

- `CampusCustomerAfterSaleResultVO`
- `CampusAdminAfterSaleExecutionQuery`
- `CampusAdminAfterSaleExecutionVO`
- `CampusSettlementPayoutBatchVO`
- `CampusSettlementPayoutBatchDetailVO`
- `CampusAdminSettlementVerifyPayoutDTO`

## 测试与验证

1. 执行 `.\mvnw.cmd -DskipTests compile`，通过
2. 执行 `.\mvnw.cmd "-Dtest=CampusStep06IntegrationTest" test`，通过
3. 执行 `.\mvnw.cmd test`，通过
4. 当前累计 `53` 个测试通过

## Bridge 结论

- `courier/profile` 与 `courier/review-status` 的双 token bridge 继续保留
- 保留原因：
  - 未审核通过用户拿不到 `courier` token`
  - 但资料提交与审核状态查询必须发生在拿 token 之前
- 收口前提：
  - 先有稳定的 onboarding 替代链路
  - 或把资料提交与审核查询统一改为不依赖 `courier` token 的入口

## 遗留问题

1. customer 仍没有自助退款申请和执行结果确认动作，只能查看回执
2. 售后执行和异常上报仍没有完整历史表
3. settlement 仍没有真实打款、撤回和复杂对账
4. `frontend/` 仍未接 campus 新接口
5. `CampusRuleCatalog` 仍是代码常量

## 下一步建议

1. 进入 Step 07，优先评估 onboarding 替代链路，为 bridge 收口做准备
2. 规划 `frontend/` 对 admin 运营查询和 customer 售后结果页的最小接入
3. 视业务需要补售后执行历史、异常历史与更细粒度财务审计

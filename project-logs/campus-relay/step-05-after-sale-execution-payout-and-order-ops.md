# Step 05 - 售后执行记录 / 结算打款运营 / 订单级运营查询

## 本次目标

- 为 admin 增加售后执行结果记录与结果查询能力
- 为 settlement 增加单笔打款结果、批量打款结果和最小对账摘要能力
- 为 admin 增加按订单维度查看 location reports 与 exception summary 的只读入口
- 继续保持 `frontend/` 不动、旧外卖模块不删、旧 `orders/cart/address` 语义不变

## 已完成项

1. 扩展 `campus_relay_order`，新增售后执行结果字段：
   - `after_sale_execution_status`
   - `after_sale_execution_remark`
   - `after_sale_execution_reference_no`
   - `after_sale_executed_by_employee_id`
   - `after_sale_executed_at`
2. 扩展 `GET /api/campus/admin/orders/after-sale`
   - 新增 `afterSaleDecisionType`
   - 新增 `afterSaleExecutionStatus`
   - 默认排序保持 `after_sale_applied_at DESC, created_at DESC`
3. 新增 `GET /api/campus/admin/orders/{id}/after-sale-result`
4. 新增 `POST /api/campus/admin/orders/{id}/after-sale-execution`
5. service 层收口售后执行规则：
   - 仅 `AFTER_SALE_RESOLVED` 且已记录售后决策的订单可写
   - `decisionType = NONE` 的订单自动初始化 `after_sale_execution_status = NOT_REQUIRED`
   - `decisionType = NONE` 不允许再通过执行接口写 `SUCCESS / FAILED`
   - `REFUND + SUCCESS` 强制要求 `executionReferenceNo` 非空
   - `executionReferenceNo` 统一 trim，纯空白不落库
   - 允许 `FAILED -> SUCCESS` 一次人工纠正
   - 拒绝 `SUCCESS -> FAILED` 和终态重复写入
6. admin timeline 新增“售后执行结果已记录”节点
7. 扩展 `campus_settlement_record`，新增打款运营字段：
   - `payout_status`
   - `payout_remark`
   - `payout_reference_no`
   - `payout_recorded_by_employee_id`
   - `payout_recorded_at`
8. settlement confirm 后自动初始化 `payout_status = UNPAID`
9. 扩展 `GET /api/campus/admin/settlements`
   - 新增 `payoutStatus`
   - 新增打款结果字段返回
   - 继续兼容 `pageSize / size`
   - 默认排序保持 `createdAt DESC`
10. 新增 `POST /api/campus/admin/settlements/{id}/payout-result`
11. 新增 `POST /api/campus/admin/settlements/batch-payout-result`
12. 新增 `GET /api/campus/admin/settlements/reconcile-summary`
13. service 层收口打款规则：
   - 仅 `SETTLED` 记录可写打款结果
   - `PAID` 时 `payoutReferenceNo` 必填
   - `payoutReferenceNo` 统一 trim，纯空白不落库
   - `FAILED -> PAID` 允许一次人工纠正
   - 批量处理前先对 `settlementIds` 去重
   - `totalRequested` 按去重后的请求数统计
   - 对账摘要金额口径统一使用 `pending_amount`
14. 新增 `GET /api/campus/admin/orders/{id}/location-reports`
15. 新增 `GET /api/campus/admin/orders/{id}/exception-summary`
16. 按订单维度运营只读规则：
   - `location-reports` 订单不存在返回 `404`
   - 默认按 `reportedAt DESC`
   - `pageSize` 最大 `100`
   - `exception-summary` 只返回当前订单最新一次异常摘要
   - 无异常时返回空异常字段
   - 无位置记录时 `locationReportCount = 0`

## 主要修改文件

- `backend/db/init.sql`
- `backend/db/migrations/V6__campus_after_sale_execution_and_settlement_payout.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleExecutionStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusPayoutStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleExecutionDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementPayoutResultDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementBatchPayoutDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleOrderQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminOrderLocationReportQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleResultVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementBatchPayoutResultVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementReconcileSummaryVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderExceptionSummaryVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusStep05IntegrationTest.java`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 测试执行

1. 执行 `.\mvnw.cmd -DskipTests compile`
2. 执行 `.\mvnw.cmd "-Dtest=CampusStep05IntegrationTest" test`
3. 执行 `.\mvnw.cmd test`

## 测试结果

- Step 05 新增测试覆盖：
  - after-sale 结果分页查询
  - 单笔 after-sale result 汇总查询
  - `REFUND` 执行成功
  - `COMPENSATE` 执行失败后人工纠正
  - `decisionType = NONE` 自动初始化 `NOT_REQUIRED`
  - 非 admin 执行售后记录被拒绝
  - settlement 单笔打款记录
  - settlement 批量打款记录
  - reconcile summary 汇总
  - 按订单查看 location reports
  - 按订单查看 exception summary
- 全量测试通过，当前累计 `50` 个测试通过

## bridge 保留结论

- 本轮继续保留 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 的双 token bridge
- 保留原因：
  - 未审核通过用户仍拿不到 `courier` token`
  - 但资料提交和审核状态查询必须发生在拿 token 之前
- 收口前提：
  - 先有稳定的 onboarding 替代链路
  - 或将资料提交与审核查询改造成不依赖 `courier` token 的统一入口

## 遗留问题

- customer 仍没有售后结果查询和最小退款回执
- admin 仍缺售后执行结果的专门列表与修正视图
- settlement 仍没有真实打款、批次审计详情和复杂对账
- 异常仍只保留最新一次，没有历史流
- `frontend/` 仍未接 campus 接口
- `CampusRuleCatalog` 仍是代码常量

## 下一步建议

1. 进入 `Step 06`
2. 优先为 customer 增加售后结果查询与回执接口
3. 为 admin 增加售后执行结果列表和修正运营查询
4. 为 settlement 增加批次审计与更稳定的运营查询
5. 继续评估 bridge 的收口时机，但在替代链路稳定前不强行收口

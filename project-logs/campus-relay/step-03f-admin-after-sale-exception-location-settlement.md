# Step 03F - admin 售后处理 / courier 异常上报 / courier 位置上报 / admin 结算分页

## 本次目标

1. 为 admin 新增最小售后处理接口，避免 `AFTER_SALE_OPEN` 长期停留
2. 为 courier 新增最小异常上报接口
3. 为 courier 新增低频位置上报接口，并复用 `campus_location_report`
4. 为 admin 新增 `campus_settlement_record` 分页查询能力
5. 明确 `courier/profile` 与 `courier/review-status` 的双 token bridge 是否保留

## 本次实际扫描的代码

- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/java/com/cangqiong/takeaway/utils/JwtUtil.java`
- `backend/src/main/java/com/cangqiong/takeaway/context/BaseContext.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/db/init.sql`

## 本次已完成项

1. 为 `campus_relay_order` 最小增量补齐售后处理与异常上报字段
2. 在 `CampusRelayOrderStatus` 中新增：
   - `AFTER_SALE_RESOLVED`
   - `AFTER_SALE_REJECTED`
3. 新增 `CampusAdminAfterSaleHandleDTO`，并要求 `handleRemark` 必填且不能为空白
4. 新增 `CampusCourierExceptionReportDTO`，并要求 `exceptionType`、`exceptionRemark` 必填
5. 新增 `CampusCourierLocationReportDTO`，支持最小低频位置上报
6. 新增 `POST /api/campus/admin/orders/{id}/after-sale-handle`
7. 新增 `POST /api/campus/courier/orders/{id}/exception-report`
8. 新增 `POST /api/campus/courier/location-reports`
9. 新增 `GET /api/campus/admin/settlements`
10. 在 service 层集中实现：
   - 售后处理动作校验与状态推进
   - courier 异常上报权限与履约状态校验
   - courier 位置上报归属校验、履约状态校验、坐标最小校验
   - settlement 分页的 `pageSize / size` 兼容逻辑
11. admin 时间线新增：
   - `EXCEPTION_REPORTED`
   - `AFTER_SALE_RESOLVED`
   - `AFTER_SALE_REJECTED`
12. 明确保留 bridge：
   - `courier/profile`
   - `courier/review-status`
   - 原因是未审核通过用户在拿不到 `courier` token 前，仍需提交资料和查询审核状态

## 本次修改文件

- `backend/pom.xml`
- `backend/db/init.sql`
- `backend/db/migrations/V4__campus_relay_ops_and_settlement.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/java/com/cangqiong/takeaway/exception/GlobalExceptionHandler.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleHandleAction.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleHandleDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierExceptionReportDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierLocationReportDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierLocationController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusStep03FIntegrationTest.java`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 本次只做了骨架或最小能力的部分

- courier 异常上报只保留订单上的最新一次异常信息，不保存多次历史
- 异常上报不会改变 `order_status`
- 位置上报只做低频记录，不做实时轨迹、频控和地图展示
- admin settlement 只做分页查询，不做确认结算、打款、对账
- admin 售后处理只支持最小动作：
  - `RESOLVED`
  - `REJECTED`

## 测试执行结果

1. 执行 `.\mvnw.cmd -DskipTests compile`，通过
2. 执行 `.\mvnw.cmd "-Dtest=CampusStep03FIntegrationTest" test`，通过
3. 执行 `.\mvnw.cmd test`，通过
4. 当前累计测试结果：`40` 个测试通过，`0` 失败，`0` 错误，`0` 跳过

## 当前遗留问题

1. `courier/profile` 与 `courier/review-status` 的双 token bridge 仍保留，尚未收口
2. customer 还没有退款、售后结果查询能力
3. admin 还没有售后补偿、退款、改派等后续动作
4. settlement 还没有确认结算和打款备注能力
5. 位置上报还没有后台查看入口
6. `frontend/` 仍未接入 campus 新接口

## Step 04 下一步建议

1. 为 admin 增加售后结果查询和最小退款/补偿决策能力
2. 为 settlement 增加详情、确认结算和打款备注
3. 为 admin 增加查看 courier 最近异常与位置记录的只读接口
4. 在 onboarding 方案稳定后，再决定是否收口 `courier/profile` 的双 token bridge
5. 在后端接口稳定后，再评估管理端与用户端页面接入节奏

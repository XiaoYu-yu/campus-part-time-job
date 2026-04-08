# Step 04 - 售后决策 / 结算确认 / admin 运营只读接口

## 本次目标

1. 为 admin 增加售后结果分页查询与最小退款/补偿决策记录能力
2. 为 settlement 增加详情查询与确认结算能力
3. 为 admin 增加查看 courier 最近异常与低频位置记录的只读接口
4. 继续保持 `courier/profile` 与 `courier/review-status` 的双 token bridge，不破坏 onboarding 过渡链路

## 已完成项

### 1. 售后结果查询与最小决策

- 新增 `GET /api/campus/admin/orders/after-sale`
- 新增 `POST /api/campus/admin/orders/{id}/after-sale-decision`
- 为 `campus_relay_order` 补齐售后决策字段：
  - `after_sale_decision_type`
  - `after_sale_decision_amount`
  - `after_sale_decision_remark`
  - `after_sale_decided_by_employee_id`
  - `after_sale_decided_at`
- `orderStatus` 过滤在 service 层强校验，只允许：
  - `AFTER_SALE_OPEN`
  - `AFTER_SALE_RESOLVED`
  - `AFTER_SALE_REJECTED`
- `afterSaleHandleAction` 过滤同样在 service 层做白名单校验
- `decisionType` 在 service 层统一转大写后再枚举解析，兼容 `refund` / `Refund`
- `decisionAmount` 在 service 层统一按 `BigDecimal.setScale(2, HALF_UP)` 规范化
- `REFUND` 金额不得超过订单 `totalAmount`
- `NONE` 决策金额必须为空，最终落库为 `NULL`
- 售后决策不改变订单状态，订单继续保持 `AFTER_SALE_RESOLVED`
- admin timeline 新增“售后决策已记录”节点

### 2. settlement 详情与确认

- 新增 `GET /api/campus/admin/settlements/{id}`
- 新增 `POST /api/campus/admin/settlements/{id}/confirm`
- settlement 详情不存在时返回 `404` 语义
- confirm 仅允许 `PENDING -> SETTLED`
- `settleRemark` 去空格后为空时直接拒绝
- confirm 复用 `campus_settlement_record.remark`：
  - 原 remark 为空：直接写 `settleRemark`
  - 原 remark 非空：按 `原remark | settleRemark` 追加

### 3. admin 运营只读接口

- 新增 `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
  - `limit` 为空默认 `10`
  - 超过 `50` 按 `50` 处理
  - 无数据返回空列表
- 新增 `GET /api/campus/admin/couriers/{courierProfileId}/location-reports`
  - 兼容 `pageSize / size`
  - 默认 `reportedAt DESC`
  - `pageSize` 最大 `100`
- courier 异常仍按“订单上最新一次异常”读取，不补异常历史表
- location report 继续只落 `campus_location_report`，不进入 timeline

### 4. 兼容性修正

- 修复 campus 分页兼容回归：
  - 查询对象默认 `pageSize=10` 时，不再覆盖旧接口传入的 `size`
  - 目前统一按“传了 `size` 则优先生效，否则再看 `pageSize`”处理

## 实际修改文件

- `backend/db/init.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java`
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
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 实际新增文件

- `backend/db/migrations/V5__campus_after_sale_decision_and_settlement_ops.sql`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleDecisionType.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleDecisionDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementConfirmDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleOrderQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierRecentExceptionQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminCourierLocationReportQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierRecentExceptionVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusLocationReportVO.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusStep04IntegrationTest.java`

## 测试执行

- 执行 `.\mvnw.cmd -DskipTests compile`：通过
- 执行 `.\mvnw.cmd "-Dtest=CampusStep04IntegrationTest" test`：通过
- 执行 `.\mvnw.cmd test`：通过
- 当前累计 `45` 个测试通过，`0` 失败，`0` 错误，`0` 跳过

## bridge 保留结论

- 本轮继续保留 `courier/profile` 与 `courier/review-status` 的双 token bridge
- 保留原因：
  - 未审核通过用户还拿不到 `courier` token
  - 但资料提交和审核状态查询必须发生在拿 token 之前
- 收口前提：
  - 先提供不依赖 `courier` token 的稳定 onboarding 替代链路
  - 或者明确把资料提交/审核查询统一并到 customer 侧入口

## 遗留问题

1. 售后决策仍只是后台记录，不触发真实退款或补偿支付
2. settlement confirm 仍只是后台记录，不触发真实打款
3. courier 异常仍只保留最新一次，没有历史和处理结果流转
4. admin 目前还不能按订单维度查看位置记录
5. `frontend/` 仍未接入 campus 新接口

## 下一步建议

1. 进入 Step 05，优先补售后决策后的结果查询和真实执行记录
2. 为 settlement 增加打款结果、批量处理和最小对账能力
3. 为 admin 增加按订单维度查看位置记录与异常摘要的只读接口
4. 结合前端 onboarding 方案，再决定 bridge 的收口时机

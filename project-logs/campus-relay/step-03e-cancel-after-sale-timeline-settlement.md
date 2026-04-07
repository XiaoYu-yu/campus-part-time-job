# Step 03E - customer 取消 / 售后 / admin 时间线 / 结算联动

## 本次目标

1. 为 customer 新增最小取消接口
2. 为 customer 新增最小售后/异常申诉接口
3. 为 admin 新增 campus 订单时间线查看能力
4. 为已完成订单补齐 `campus_settlement_record` 自动生成或更新逻辑
5. 对 `courier/profile` 与 `courier/review-status` 的双 token bridge 给出代码内明确结论
6. 在不改 `frontend/`、不删旧外卖模块的前提下保持 H2 和旧链路测试通过

## 本次实际扫描的文件

- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/db/init.sql`
- `backend/db/migrations/V2__campus_relay_schema.sql`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCustomerOrderIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCourierDeliveryIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCourierProfileIntegrationTest.java`

## 本次实际修改的文件

- `backend/db/init.sql`
- `backend/db/migrations/V2__campus_relay_schema.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusOrderExceptionIntegrationTest.java`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 本次实际新增的文件

- `backend/db/migrations/V3__campus_relay_order_timeline_columns.sql`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderCancelDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderAfterSaleDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderTimelineItemVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderTimelineVO.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusOrderExceptionIntegrationTest.java`
- `project-logs/campus-relay/step-03e-cancel-after-sale-timeline-settlement.md`

## 本次实际完成项

1. 新增 `POST /api/campus/customer/orders/{id}/cancel`
2. 新增 `POST /api/campus/customer/orders/{id}/after-sale`
3. 新增 `GET /api/campus/admin/orders/{id}/timeline`
4. 为 `campus_relay_order` 最小补齐：
   - `paid_at`
   - `cancelled_at`
   - `after_sale_applied_at`
   - `cancel_reason`
5. customer 取消与售后状态流转已集中到 `CampusRelayOrderServiceImpl`
6. admin 时间线改为由 service 基于订单时间字段和 `campus_settlement_record` 组装
7. customer confirm 进入 `COMPLETED` 时自动生成或更新待结算记录
8. `courier/profile` 与 `courier/review-status` 的 bridge 继续保留，并在 `JwtInterceptor` 中补了保留原因注释

## 本次状态流转与规则

### 取消规则

- 只有订单所属 customer 可以取消
- 只允许 `PENDING_PAYMENT / BUILDING_PRIORITY_PENDING / WAITING_ACCEPT / ACCEPTED`
- 一旦已取餐或进入之后阶段，直接拒绝取消
- 取消成功后：
  - `order_status -> CANCELLED`
  - 写入 `cancel_reason`
  - 写入 `cancelled_at`

### 售后规则

- 只有订单所属 customer 可以发起
- 当前最小实现只接受文本原因，不做附件流
- 只允许在：
  - `AWAITING_CONFIRMATION`
  - `COMPLETED`
- 发起成功后：
  - `order_status -> AFTER_SALE_OPEN`
  - 写入 `after_sale_reason`
  - 写入 `after_sale_applied_at`

### 结算联动

- customer confirm 成功后：
  - `order_status -> COMPLETED`
  - 写入 `auto_complete_at`
  - 自动查询 `campus_settlement_record`
- 如果不存在结算记录：
  - 插入一条 `PENDING` 待结算记录
- 如果已存在结算记录：
  - 更新订单金额快照和配送员信息
  - 已结算记录不会被重置回未结算

## 哪些点只做了最小实现

1. admin 侧本轮只补了时间线查看，没有补异常备注或售后处理动作
2. 售后只是一段最小文本申诉，没有扩成独立工单系统
3. 取消后不做退款流转，支付状态保持当前值
4. timeline 仍基于订单表时间字段组装，不是完整审计日志表
5. settlement 只做到自动生成/更新，不包含 admin 分页查看和人工结算确认

## bridge 结论

- 本轮明确结论：**继续保留 bridge**
- 保留原因：
  1. 未审核通过的用户拿不到 courier token
  2. 但资料提交和审核状态查看又是拿到 courier token 之前必须完成的动作
  3. 因此前端未接入前，`/api/campus/courier/profile` 和 `/api/campus/courier/review-status` 继续允许 `customer/courier` 双通道
- 代码落点：
  - `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`

## 测试与结果

- 执行：`.\mvnw.cmd -DskipTests compile`
- 结果：通过
- 执行：`.\mvnw.cmd "-Dtest=CampusCustomerOrderIntegrationTest,CampusCourierDeliveryIntegrationTest,CampusOrderExceptionIntegrationTest,CampusCourierProfileIntegrationTest" test`
- 结果：通过
- 执行：`.\mvnw.cmd test`
- 结果：通过
- 当前累计：`34` 个测试通过，`0` 失败，`0` 错误，`0` 跳过

## 当前仍未解决的问题

1. admin 还没有售后处理动作，`AFTER_SALE_OPEN` 目前只能查看
2. customer 还没有退款、售后结果查询能力
3. courier 还没有位置上报和异常上报
4. settlement 还没有 admin 查询和结算确认接口
5. `CampusRuleCatalog` 仍是代码常量，尚未配置化
6. `frontend/` 仍未接入 campus 新接口

## Step 03F 下一步建议

1. 新增 admin 最小售后处理接口
2. 新增 courier 异常上报接口
3. 新增 courier 低频位置上报接口
4. 新增 admin settlement 分页查询接口
5. 结合前端接入节奏再决定 bridge 的收口窗口

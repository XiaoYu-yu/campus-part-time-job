# Step 02A - 数据库与后端骨架

## 本次目标

在不改 `frontend/`、不删除旧外卖模块、不改变旧 `orders/cart/address` 语义的前提下，完成 campus 领域的第一批后端落地：

1. 新增 `campus_*` SQL 结构
2. 新增 H2 schema 和 H2 data
3. 新增 `com.cangqiong.takeaway.campus` 包结构
4. 新增 entity、enums、support、query、vo、mapper 骨架
5. 新增 service 接口和 service impl 骨架
6. 新增只读 public/admin controller 的最小实现
7. 更新必要配置与鉴权边界
8. 补最小测试
9. 更新 campus 专用日志

## 实际扫描的文件

### 规划与规则文档

- `docs/README.md`
- `docs/campus-relay/domain-refactor-plan.md`
- `docs/campus-relay/legacy-to-campus-mapping.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-01-domain-model-planning.md`
- `AGENTS.md`

### 后端结构与旧模块

- `backend/db/init.sql`
- `backend/db/migrations/README.md`
- `backend/db/migrations/V1__baseline_schema.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/resources/application-dev.properties`
- `backend/src/main/resources/application-test.properties`
- `backend/src/main/resources/application-prod.properties`
- `backend/src/main/java/com/cangqiong/takeaway/TakeawayApplication.java`
- `backend/src/main/java/com/cangqiong/takeaway/config/WebMvcConfig.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/java/com/cangqiong/takeaway/utils/JwtUtil.java`
- `backend/src/main/java/com/cangqiong/takeaway/utils/Result.java`
- `backend/src/main/java/com/cangqiong/takeaway/vo/PageResult.java`
- `backend/src/main/java/com/cangqiong/takeaway/entity/User.java`
- `backend/src/main/java/com/cangqiong/takeaway/entity/Employee.java`
- `backend/src/main/java/com/cangqiong/takeaway/entity/Order.java`
- `backend/src/main/java/com/cangqiong/takeaway/mapper/UserMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/mapper/EmployeeMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/mapper/OrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/mapper/OrderItemMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/mapper/AddressMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/service/UserService.java`
- `backend/src/main/java/com/cangqiong/takeaway/service/OrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/service/impl/UserServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/service/impl/OrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/controller/UserController.java`
- `backend/src/main/java/com/cangqiong/takeaway/controller/EmployeeController.java`
- `backend/src/main/java/com/cangqiong/takeaway/controller/OrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/controller/UserOrderController.java`
- `backend/src/test/java/com/cangqiong/takeaway/CriticalApiIntegrationTest.java`

### 前端结构确认

- `frontend/src/router/index.js`
- `frontend/src/layout/MainLayout.vue`
- `frontend/src/layout/UserLayout.vue`
- `frontend/src/api/order.js`
- `frontend/src/api/customer-order.js`
- `frontend/src/api/customer.js`

## 实际新增的表

1. `campus_customer_profile`
2. `campus_courier_profile`
3. `campus_pickup_point`
4. `campus_relay_order`
5. `campus_location_report`
6. `campus_settlement_record`

## 实际新增的 Java 类

### entity

- `CampusCustomerProfile`
- `CampusCourierProfile`
- `CampusPickupPoint`
- `CampusRelayOrder`
- `CampusLocationReport`
- `CampusSettlementRecord`

### enums

- `CampusRelayOrderStatus`
- `CampusPaymentStatus`
- `CampusCourierReviewStatus`
- `CampusDeliveryTargetType`
- `CampusSettlementStatus`

### support

- `CampusRuleCatalog`

### query

- `CampusRelayOrderQuery`
- `CampusCourierQuery`

### vo

- `CampusPickupPointVO`
- `CampusDeliveryRuleVO`
- `CampusRelayOrderVO`
- `CampusCourierProfileVO`

### mapper

- `CampusCustomerProfileMapper`
- `CampusCourierProfileMapper`
- `CampusPickupPointMapper`
- `CampusRelayOrderMapper`
- `CampusLocationReportMapper`
- `CampusSettlementRecordMapper`

### service

- `CampusCustomerProfileService`
- `CampusPickupPointService`
- `CampusRelayOrderService`
- `CampusCourierProfileService`

### service impl

- `CampusCustomerProfileServiceImpl`
- `CampusPickupPointServiceImpl`
- `CampusRelayOrderServiceImpl`
- `CampusCourierProfileServiceImpl`

### controller

- `CampusPublicController`
- `CampusAdminRelayOrderController`
- `CampusAdminCourierController`

## 实际修改的文件

- `backend/db/init.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/resources/application-dev.properties`
- `backend/src/main/resources/application-test.properties`
- `backend/src/main/resources/application-prod.properties`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 哪些点只做了骨架

1. `CampusCustomerProfileService` 只提供按 `userId` 查询
2. `CampusRelayOrderService` 只支持后台只读分页
3. `CampusCourierProfileService` 只支持后台只读分页
4. `CampusPublicController` 只返回取餐点和规则说明
5. `CampusAdminRelayOrderController` 只开放 `GET /api/campus/admin/orders`
6. `CampusAdminCourierController` 只开放 `GET /api/campus/admin/couriers`
7. `CampusLocationReportMapper` 和 `CampusSettlementRecordMapper` 目前只有最小读写骨架，没有挂到 Controller

## 本次实际开放的接口

- `GET /api/campus/public/pickup-points`
- `GET /api/campus/public/delivery-rules`
- `GET /api/campus/admin/orders`
- `GET /api/campus/admin/couriers`

## 本次实际执行的验证

### 编译

- `backend`: `.\mvnw.cmd -DskipTests compile`
- 结果：通过

### 测试

- `backend`: `.\mvnw.cmd test`
- 结果：通过
- 总结：`12` 个测试全部通过，包含：
  - 旧登录、旧订单、旧统计、旧上传链路
  - 新增 `CampusSkeletonIntegrationTest`

### 新增测试覆盖点

1. H2 启动后 `campus_pickup_point`、`campus_relay_order`、`campus_courier_profile` 有种子数据
2. `/api/campus/public/pickup-points` 无需登录即可访问
3. `/api/campus/public/delivery-rules` 无需登录即可访问
4. `/api/campus/admin/orders` 未登录返回 `401`
5. `/api/campus/admin/couriers` 使用 customer token 返回 `403`
6. `/api/campus/admin/orders` 与 `/api/campus/admin/couriers` 使用 employee token 返回 `200`

## 当前仍未解决的风险

1. `/api/campus/courier/**` 虽然在 `JwtInterceptor` 中预留了 `courier` 用户类型，但当前没有真正的 courier 登录与 token 发行
2. customer profile 仍然只是独立表和查询骨架，尚未接到注册/资料完善流程
3. campus 规则目前由 `CampusRuleCatalog` 固定返回，尚未后台配置化
4. 前端完全未接入 campus 接口，当前只能通过测试或手工接口调试验证
5. `CampusRelayOrder` 还没有订单详情、状态流转和写接口

## Step 02B 下一步建议

1. 新增 customer 代送订单创建接口与 DTO
2. 新增 customer 模拟支付接口
3. 新增 courier 资料提交和资料查看接口
4. 新增 admin 审核 courier 接口
5. 新增 campus 订单详情接口
6. 继续保持 `frontend/` 不动，避免本阶段扩散到页面替换

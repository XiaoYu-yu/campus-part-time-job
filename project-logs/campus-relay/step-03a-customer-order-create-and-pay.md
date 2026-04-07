# Step 03A - customer 下单与模拟支付

## 本次目标

在 Step 02A/02B 已完成的 campus 数据结构与只读接口基础上，只补 customer 侧最小写接口，不进入 courier 接单和前端改造：

1. 新增 customer 创建代送单接口
2. 新增 customer 模拟支付接口
3. 新增 customer 订单详情接口
4. 新增 customer 订单列表接口
5. 将最小状态流转校验集中落在 service 层
6. 补充测试
7. 更新 campus 日志总览

## 本次实际扫描的文件

### 规划与日志

- `docs/campus-relay/domain-refactor-plan.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-02a-db-and-backend-skeleton.md`
- `project-logs/campus-relay/step-02b-readonly-api.md`

### campus 现有骨架

- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCustomerProfile.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusPickupPoint.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusPaymentStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusDeliveryTargetType.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusPickupPointMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCustomerProfileService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusPublicController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`

### 现有基础设施与风格参考

- `backend/src/main/java/com/cangqiong/takeaway/utils/Result.java`
- `backend/src/main/java/com/cangqiong/takeaway/vo/PageResult.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/BaseContext.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CriticalApiIntegrationTest.java`

## 本次实际改动

### DTO / Query / VO

- 新增 `CampusCustomerOrderCreateDTO`
- 新增 `CampusCustomerOrderQuery`
- 新增 `CampusCustomerOrderVO`

### Controller

- 新增 `CampusCustomerOrderController`
- 开放以下 4 个 customer 接口：
  - `POST /api/campus/customer/orders`
  - `POST /api/campus/customer/orders/{id}/mock-pay`
  - `GET /api/campus/customer/orders/{id}`
  - `GET /api/campus/customer/orders`

### Service / Mapper

- 扩展 `CampusRelayOrderService`
- 扩展 `CampusRelayOrderMapper`
- 扩展 `CampusRelayOrderServiceImpl`

### 核心实现点

1. customer 继续复用旧 `user` 登录身份
2. customer profile 继续从 `campus_customer_profile` 读取
3. 创建代送单时校验：
   - `pickupPointId`
   - `targetType`
   - `deliveryBuilding`
   - `deliveryDetail`
   - `contactName`
   - `contactPhone`
   - `foodDescription`
   - `externalPlatformName`
   - `externalOrderRef`
   - `pickupCode`
4. 取餐点必须存在且启用
5. 配送楼栋校验按 `CampusRuleCatalog` 执行：
   - 宿舍只能选支持楼栋
   - 教学楼只能选支持楼栋
   - 图书馆目标必须固定为 `图书馆`
6. 金额处理统一使用 `BigDecimal` 两位小数：
   - 基础费固定取 `CampusRuleCatalog.BASE_FEE`
   - `urgentFlag = 1` 时加急费取 `CampusRuleCatalog.PRIORITY_FEE_MIN`
   - `tipFee` 允许为空，非空时限定在 `1` 到 `10` 元
7. customer 详情与列表只能查看自己的订单
8. 支付继续只允许模拟支付，不接第三方支付

## 本次实际开放的接口

### 1. 创建代送单

- `POST /api/campus/customer/orders`
- 鉴权：需要 customer token
- 返回：`Result<String>`
- 语义：返回新建订单号

### 2. 模拟支付

- `POST /api/campus/customer/orders/{id}/mock-pay`
- 鉴权：需要 customer token
- 返回：`Result<Void>`
- 语义：将当前用户自己的订单从待支付推进到待接单阶段

### 3. 查看订单详情

- `GET /api/campus/customer/orders/{id}`
- 鉴权：需要 customer token
- 返回：`Result<CampusCustomerOrderVO>`
- 语义：只返回当前用户自己的订单详情

### 4. 查询订单列表

- `GET /api/campus/customer/orders`
- 鉴权：需要 customer token
- 返回：`Result<PageResult<CampusCustomerOrderVO>>`
- 支持筛选：
  - `orderNo`
  - `status`
  - `paymentStatus`
  - `deliveryTargetType`
  - `page`
  - `pageSize`
  - `size`

## 状态流转

### 创建单

- 创建成功后：
  - `paymentStatus = UNPAID`
  - `orderStatus = PENDING_PAYMENT`

### 模拟支付

- 宿舍单支付后：
  - `paymentStatus = PAID`
  - `orderStatus = BUILDING_PRIORITY_PENDING`
  - `priorityDormitoryBuilding = deliveryBuilding`
  - `priorityWindowDeadline = 当前时间 + 5 分钟`

- 非宿舍单支付后：
  - `paymentStatus = PAID`
  - `orderStatus = WAITING_ACCEPT`

### 已取餐后的限制

- 本轮还没有开放 customer 取消接口
- 但 service 已提前写入规则：
  - 一旦进入 `PICKED_UP`、`DELIVERING`、`AWAITING_CONFIRMATION`、`COMPLETED`
  - 或 `pickedUpAt` 已存在
  - customer 侧不允许再直接取消或修改

## 本次没有做的事情

- 没有改 `frontend/`
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有做 courier 接单、取餐、完成等写接口
- 没有做 customer 取消、售后、退款接口
- 没有做 admin 订单详情和 courier 审核接口
- 没有新增第二套返回体

## 本次测试与结果

### 编译

- 在 `backend/` 执行：`.\mvnw.cmd -DskipTests compile`
- 结果：通过

### 测试

- 在 `backend/` 执行：`.\mvnw.cmd test`
- 结果：通过
- 汇总：`18` 个测试全部通过，`0` 失败，`0` 错误，`0` 跳过

### 本次新增覆盖点

1. `/api/campus/customer/orders` 未登录返回 `401`
2. `/api/campus/customer/orders` 使用 employee token 返回 `403`
3. customer 创建代送单后数据库状态为 `UNPAID + PENDING_PAYMENT`
4. customer 模拟支付后宿舍单状态进入 `PAID + BUILDING_PRIORITY_PENDING`
5. customer 可查看自己创建订单的详情
6. customer 可按订单号、支付状态分页查询自己的订单列表

## 当前仍未解决的风险

1. customer 创建单依赖 `campus_customer_profile` 已存在，当前还没有资料补录接口
2. courier 登录、token、资料提交流程仍未实现
3. customer 订单仍没有取消、售后、退款、轨迹能力
4. admin 仍无法查看 campus 订单详情、审核 courier
5. 规则仍集中在 `CampusRuleCatalog`，尚未配置化
6. 当前只完成后端联调能力，前端页面未接入

## Step 03B 建议

1. 补 `courier` 资料提交接口
2. 补 `courier` 资料详情与审核状态查询接口
3. 补 `admin` 订单详情接口
4. 补 `admin` 审核 courier 接口
5. 继续保持 `frontend/` 不动，先把后端角色边界和审核流转收口

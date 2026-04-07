# Step 03C - courier token 与最小接单链路

## 本次目标

在 Step 03B 已完成 courier 资料链路与 admin 审核能力的基础上，只补 courier 最小接单链路，不进入取餐、送达、完成和前端改造：

1. 为 courier 补正式 token 发行入口，或提供可测可跑的过渡身份桥接
2. 新增 courier 可接订单列表接口
3. 新增 courier 订单详情接口
4. 新增 courier 最小接单接口
5. 补充 query / vo / service / mapper / controller 类型
6. 补充测试
7. 更新 campus 日志总览

## 本次实际扫描的文件

### 规划与日志

- `docs/campus-relay/domain-refactor-plan.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-03b-courier-profile-and-admin-review.md`

### 鉴权与上下文

- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/BaseContext.java`
- `backend/src/main/java/com/cangqiong/takeaway/utils/JwtUtil.java`
- `backend/src/main/java/com/cangqiong/takeaway/controller/UserController.java`
- `backend/src/main/java/com/cangqiong/takeaway/service/impl/UserServiceImpl.java`

### campus 现有实现

- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCourierProfile.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusPaymentStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/resources/db/data-h2.sql`

### 现有测试

- `backend/src/test/java/com/cangqiong/takeaway/CampusCourierProfileIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCustomerOrderIntegrationTest.java`

## 本次实际改动

### 新增类型

- 新增 `CampusCourierAvailableOrderQuery`
- 新增 `CampusCourierOrderVO`

### 新增 Controller

- 新增 `CampusCourierAuthController`
- 新增 `CampusCourierOrderController`

### 扩展 Service / Mapper / Interceptor

- 扩展 `CampusCourierProfileService`
- 扩展 `CampusRelayOrderService`
- 扩展 `CampusCourierProfileMapper`
- 扩展 `CampusRelayOrderMapper`
- 扩展 `CampusCourierProfileServiceImpl`
- 扩展 `CampusRelayOrderServiceImpl`
- 扩展 `JwtInterceptor`

### 测试与文档

- 调整 `CampusCourierProfileIntegrationTest`，让 profile/review-status 走真实 bridge 链路
- 新增 `CampusCourierAcceptIntegrationTest`
- 更新 `summary.md`
- 更新 `pending-items.md`
- 更新 `file-change-list.md`
- 更新 `docs/README.md`

## 本次实际开放的接口

### courier auth

- `POST /api/campus/courier/auth/token`

### courier orders

- `GET /api/campus/courier/orders/available`
- `GET /api/campus/courier/orders/{id}`
- `POST /api/campus/courier/orders/{id}/accept`

## courier token / 身份桥接方案

### 正式 token 发行

- 继续复用现有 `user` 登录凭证，不新建 courier 账号表
- `POST /api/campus/courier/auth/token` 接收手机号和密码
- 先复用 `UserService.login` 完成账号密码校验
- 再通过 `CampusCourierProfileService.requireApprovedEnabledProfile` 校验：
  - `campus_courier_profile` 必须存在
  - `reviewStatus = APPROVED`
  - `enabled = 1`
- 校验通过后签发 `userType = courier` 的 JWT

### 过渡 bridge

- 为避免未审核通过用户无法完成资料提交，`/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 暂时允许：
  - `customer` token
  - `courier` token
- 这两个接口本质上还是 user 维度的资料链路，不进入接单业务权限
- 真正的接单业务接口 `/api/campus/courier/orders/**` 仍然只允许 courier token

## 接单状态流转

### 已有前置状态

- `PENDING_PAYMENT`
- `BUILDING_PRIORITY_PENDING`
- `WAITING_ACCEPT`

### Step 03C 新增推进

- courier 成功接单后：
  - `orderStatus = ACCEPTED`
  - 写入 `courierProfileId`
  - 写入 `acceptedAt`

### 本次明确不做

- 不做 `PICKED_UP`
- 不做 `DELIVERING`
- 不做 `AWAITING_CONFIRMATION`
- 不做 `COMPLETED`

## 宿舍优先窗口命中逻辑

1. 宿舍单支付后进入 `BUILDING_PRIORITY_PENDING`
2. `priorityWindowDeadline` 在支付时写入，默认 `当前时间 + 5 分钟`
3. 在优先窗口内：
   - 只有 `courierProfile.dormitoryBuilding` 与 `order.priorityDormitoryBuilding` 相同的 courier 可查看和接单
4. 超过优先窗口后：
   - 合规 courier 都可查看和接单
5. 非宿舍单直接走 `WAITING_ACCEPT`，不进入楼栋优先窗口

## 并发安全处理

1. 接单资格判断集中在 `CampusRelayOrderServiceImpl`
2. 接单写入使用条件更新：
   - 订单必须 `payment_status = PAID`
   - `courier_profile_id IS NULL`
   - `order_status IN ('WAITING_ACCEPT', 'BUILDING_PRIORITY_PENDING')`
3. 如果两个 courier 同时接单：
   - 只有第一个满足条件更新的请求会成功
   - 后续请求会命中 `updated = 0`
   - service 层统一抛出“订单已被其他配送员接单或状态已变化”
4. 可接订单列表的资格过滤回收到 service 层，避免把宿舍优先窗口规则散落在 SQL 和 controller 中

## 本次没有做的事情

- 没有改 `frontend/`
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有实现 courier 取餐、配送中、送达、完成接口
- 没有实现 customer 取消、售后、退款接口
- 没有新增第二套返回体
- 没有新增第二套文件服务

## 本次测试与结果

### 编译

- 在 `backend/` 执行：`.\mvnw.cmd -DskipTests compile`
- 结果：通过

### 定向测试

- 在 `backend/` 执行：`.\mvnw.cmd "-Dtest=CampusCourierProfileIntegrationTest,CampusCourierAcceptIntegrationTest" test`
- 结果：通过

### 全量测试

- 在 `backend/` 执行：`.\mvnw.cmd test`
- 结果：通过
- 汇总：`28` 个测试全部通过，`0` 失败，`0` 错误，`0` 跳过

### 本次新增覆盖点

1. pending courier 不能获取 courier token
2. approved + enabled courier 可以获取 courier token
3. `/api/campus/courier/orders/**` 未登录返回 `401`
4. `/api/campus/courier/orders/**` 使用 customer token 返回 `403`
5. 同楼栋 courier 能查看并接宿舍优先单
6. 非同楼栋 courier 在优先窗口内不能查看详情、不能接单
7. 同一订单被第一个 courier 接单后，第二个 courier 不能再次抢单
8. profile/review-status bridge 链路继续可用

## 当前仍未解决的风险

1. `courier/profile` 与 `courier/review-status` 当前仍是 bridge 方案，后续需要决定是否收口
2. 订单状态目前只推进到 `ACCEPTED`，还没有取餐、配送、完成闭环
3. customer 仍没有取消、售后、退款接口
4. 规则仍集中在 `CampusRuleCatalog`，尚未配置化
5. 当前仍主要依赖后端联调和测试，前端页面未接 campus 新接口

## Step 03D 建议

1. 新增 `POST /api/campus/courier/orders/{id}/pickup`
2. 为取餐动作补取餐凭证字段写入与文件引用校验
3. 新增 `POST /api/campus/courier/orders/{id}/deliver`
4. 新增 `POST /api/campus/customer/orders/{id}/confirm`
5. 在 Step 03D 结束后评估是否收口 `customer/courier` 双 token bridge

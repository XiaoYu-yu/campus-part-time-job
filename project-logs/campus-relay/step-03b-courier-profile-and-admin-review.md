# Step 03B - courier 资料链路与 admin 最小审核/详情

## 本次目标

在 Step 03A 已完成的 customer 下单与模拟支付基础上，只补 courier 资料链路和 admin 最小审核/详情能力，不进入接单、取餐和前端改造：

1. 新增 courier 资料提交接口
2. 新增 courier 资料详情接口
3. 新增 courier 审核状态查询接口
4. 新增 admin 查看 campus 订单详情接口
5. 新增 admin 审核 courier 接口
6. 补充 DTO / VO / service / mapper / controller 类型
7. 补充测试
8. 更新 campus 日志总览

## 本次实际扫描的文件

### 规划与日志

- `docs/campus-relay/domain-refactor-plan.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-03a-customer-order-create-and-pay.md`

### courier 与 admin 现有骨架

- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCourierProfile.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusCourierReviewStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/java/com/cangqiong/takeaway/utils/JwtUtil.java`
- `backend/src/main/resources/db/data-h2.sql`

### 现有基础设施与风格参考

- `backend/src/main/java/com/cangqiong/takeaway/utils/Result.java`
- `backend/src/main/java/com/cangqiong/takeaway/vo/PageResult.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/BaseContext.java`
- `backend/src/main/java/com/cangqiong/takeaway/mapper/UserMapper.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCustomerOrderIntegrationTest.java`

## 本次实际改动

### DTO / VO

- 新增 `CampusCourierProfileSubmitDTO`
- 新增 `CampusCourierReviewDTO`
- 新增 `CampusCourierReviewStatusVO`
- 扩展 `CampusCourierProfileVO`
- 扩展 `CampusRelayOrderVO`

### Controller

- 新增 `CampusCourierProfileController`
- 扩展 `CampusAdminCourierController`
- 扩展 `CampusAdminRelayOrderController`

### Service / Mapper

- 扩展 `CampusCourierProfileService`
- 扩展 `CampusRelayOrderService`
- 扩展 `CampusCourierProfileMapper`
- 扩展 `CampusRelayOrderMapper`
- 扩展 `CampusCourierProfileServiceImpl`
- 扩展 `CampusRelayOrderServiceImpl`

## 本次实际开放的接口

### courier

- `POST /api/campus/courier/profile`
- `GET /api/campus/courier/profile`
- `GET /api/campus/courier/review-status`

### admin

- `GET /api/campus/admin/orders/{id}`
- `POST /api/campus/admin/couriers/{id}/review`

## 核心实现点

1. courier 资料继续落在 `campus_courier_profile`
2. 资料附件字段继续只保存受控路径：
   - `verificationPhotoUrl`
   - `scheduleAttachmentUrl`
3. 资料提交时校验：
   - 真实姓名、学号、学院、专业、班级
   - 宿舍楼栋与房间
   - 身份证后四位
   - 紧急联系人与电话
   - 认证照片与课程表附件路径
4. 宿舍楼栋范围继续以 `CampusRuleCatalog.DORMITORY_BUILDINGS` 为准
5. 认证照片和课程表附件必须为 `/api/files/*` 受控路径
6. admin 订单详情继续复用 `CampusRelayOrderVO`，补齐：
   - `pickupProofImageUrl`
   - `customerRemark`
   - `courierRemark`
   - `afterSaleReason`
   - `cancelLockedUntil`
   - `autoCompleteAt`

## 审核状态流转

### courier 提交或重提资料

- 资料首次提交或重新提交后统一重置为：
  - `reviewStatus = PENDING`
  - `enabled = 0`
  - `reviewComment = 待人工审核`
  - `reviewedByEmployeeId = null`
  - `reviewedAt = null`

### admin 审核

- `APPROVED`
  - `reviewStatus = APPROVED`
  - `enabled = 1`
- `REJECTED`
  - `reviewStatus = REJECTED`
  - `enabled = 0`
  - 必须填写审核说明
- `DISABLED`
  - `reviewStatus = DISABLED`
  - `enabled = 0`
  - 必须填写审核说明

### 明确限制

- admin 审核接口不允许直接把状态设置为 `PENDING`
- 所有审核状态流转都集中在 service 层，不在 controller 中散落判断

## 本次没有做的事情

- 没有改 `frontend/`
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有实现 courier 接单、取餐、完成等写接口
- 没有实现 customer 取消、售后、退款接口
- 没有新增第二套返回体
- 没有新增第二套文件服务
- 没有补正式 courier 登录/token 发行接口

## 本次测试与结果

### 编译

- 在 `backend/` 执行：`.\mvnw.cmd -DskipTests compile`
- 结果：通过

### 测试

- 在 `backend/` 执行：`.\mvnw.cmd -Dtest=CampusCourierProfileIntegrationTest test`
- 结果：通过
- 在 `backend/` 执行：`.\mvnw.cmd test`
- 结果：通过
- 汇总：`23` 个测试全部通过，`0` 失败，`0` 错误，`0` 跳过

### 本次新增覆盖点

1. `/api/campus/courier/profile` 未登录返回 `401`
2. `/api/campus/courier/profile` 使用 customer token 返回 `403`
3. courier 可查看自己的资料详情
4. courier 重新提交资料后状态会重置为 `PENDING`
5. admin 可审核 courier 资料
6. courier 可查看自己的最新审核状态
7. `/api/campus/admin/orders/{id}` 未登录 `401`、customer token `403`、employee token `200`
8. admin 订单详情返回稳定字段和受控文件路径

## 当前仍未解决的风险

1. `/api/campus/courier/**` 已可通过 token 调用，但当前没有正式登录入口
2. courier 仍没有接单、取餐、送达、完成接口
3. customer 仍没有取消、售后、退款接口
4. admin 仍只有最小审核与详情能力，缺少更多人工干预能力
5. 规则仍集中在 `CampusRuleCatalog`，尚未配置化
6. 当前只完成后端联调能力，前端页面未接入

## Step 03C 建议

1. 补 courier token 正式发行或过渡身份桥接
2. 补 `GET /api/campus/courier/orders/available`
3. 补 `POST /api/campus/courier/orders/{id}/accept`
4. 补 `GET /api/campus/courier/orders/{id}`
5. 继续保持 `frontend/` 不动，先把 courier 最小业务闭环跑通

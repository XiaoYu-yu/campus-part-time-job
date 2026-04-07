# Step 03D - courier 取餐 / 配送推进 / customer 确认送达

## 本次目标

1. 新增 courier 取餐接口
2. 为取餐动作补取餐凭证受控文件引用校验与落库
3. 新增 courier 配送中 / 送达状态推进接口
4. 新增 customer 确认送达接口
5. 补充 DTO / VO / service / mapper / controller 所需类型
6. 补充测试
7. 更新总览和日志

## 实际扫描的文件

- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java`
- `backend/src/main/java/com/cangqiong/takeaway/utils/JwtUtil.java`
- `backend/src/main/java/com/cangqiong/takeaway/interceptor/BaseContext.java`
- `backend/src/main/java/com/cangqiong/takeaway/controller/FileController.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCourierAcceptIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCustomerOrderIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java`

## 实际修改的文件

- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 实际新增的文件

- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierPickupDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierDeliverDTO.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCourierDeliveryIntegrationTest.java`
- `project-logs/campus-relay/step-03d-pickup-deliver-confirm.md`

## 实际完成项

1. 新增 `POST /api/campus/courier/orders/{id}/pickup`
2. 新增 `POST /api/campus/courier/orders/{id}/deliver`
3. 新增 `POST /api/campus/customer/orders/{id}/confirm`
4. pickup 阶段新增 `/api/files/*` 受控路径校验，拒绝本地磁盘绝对路径
5. pickup 成功后写入：
   - `order_status = PICKED_UP`
   - `picked_up_at`
   - `pickup_proof_image_url`
6. deliver 接口按当前状态双阶段推进：
   - `PICKED_UP -> DELIVERING`
   - `DELIVERING -> AWAITING_CONFIRMATION`
   - 第二次推进时写入 `delivered_at`
7. customer confirm 成功后写入：
   - `order_status = COMPLETED`
   - `auto_complete_at`
8. 补齐 customer / courier 详情 VO 字段：
   - `pickupProofImageUrl`
   - `courierRemark`
   - `autoCompleteAt`
9. 调整 admin 只读测试断言，避免全量回归下的脆弱计数匹配

## 本次状态流转落地

- 接单后：`ACCEPTED`
- 取餐后：`PICKED_UP`
- 第一次配送推进：`DELIVERING`
- 第二次配送推进：`AWAITING_CONFIRMATION`
- customer 确认后：`COMPLETED`

所有状态流转都集中在 `CampusRelayOrderServiceImpl`，没有散落到 controller。

## 哪些点只做了最小实现

- `deliver` 接口采用单路径双阶段推进，没有拆成多个更细接口
- customer confirm 使用现有字段 `auto_complete_at` 记录完成确认时间，没有新增 `confirmed_at`
- 取餐凭证只校验路径前缀和落库，没有新增独立文件元数据表
- 暂未接入 settlement / location / after-sale 联动

## 测试与结果

- 执行：`.\mvnw.cmd -DskipTests compile`
- 结果：通过
- 执行：`.\mvnw.cmd "-Dtest=CampusCourierAcceptIntegrationTest,CampusCourierDeliveryIntegrationTest,CampusCustomerOrderIntegrationTest" test`
- 结果：通过
- 执行：`.\mvnw.cmd test`
- 结果：通过
- 当前累计：`31` 个测试通过，`0` 失败，`0` 错误，`0` 跳过

## 当前仍未解决的问题

1. customer 还没有取消、退款、售后接口
2. campus 订单完成后还没有自动生成 `campus_settlement_record`
3. courier 还没有位置上报、异常上报、改派协作能力
4. `courier/profile` 与 `courier/review-status` 仍是 bridge 过渡态
5. `frontend/` 仍未接入 campus 新接口

## Step 03E 下一步建议

1. 新增 `POST /api/campus/customer/orders/{id}/cancel`
2. 新增 `POST /api/campus/customer/orders/{id}/after-sale`
3. 为 admin 新增 campus 订单异常处理或时间线查看
4. 在订单完成后生成或补齐 `campus_settlement_record`
5. 评估是否收口 `courier/profile` 的双 token bridge

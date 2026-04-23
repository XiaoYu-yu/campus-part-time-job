# 校园代送改造文件改动清单

## Step 01 - 领域模型重构规划

- [docs/README.md](../../docs/README.md)
- [docs/campus-relay/domain-refactor-plan.md](../../docs/campus-relay/domain-refactor-plan.md)
- [docs/campus-relay/legacy-to-campus-mapping.md](../../docs/campus-relay/legacy-to-campus-mapping.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-01-domain-model-planning.md](step-01-domain-model-planning.md)

## Step 02A - 数据库与后端骨架

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V2__campus_relay_schema.sql](../../backend/db/migrations/V2__campus_relay_schema.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/resources/db/data-h2.sql](../../backend/src/main/resources/db/data-h2.sql)
- [backend/src/main/resources/application-dev.properties](../../backend/src/main/resources/application-dev.properties)
- [backend/src/main/resources/application-test.properties](../../backend/src/main/resources/application-test.properties)
- [backend/src/main/resources/application-prod.properties](../../backend/src/main/resources/application-prod.properties)
- [backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java](../../backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCustomerProfile.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCustomerProfile.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCourierProfile.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCourierProfile.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusPickupPoint.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusPickupPoint.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusLocationReport.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusLocationReport.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusPaymentStatus.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusPaymentStatus.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusCourierReviewStatus.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusCourierReviewStatus.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusDeliveryTargetType.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusDeliveryTargetType.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusSettlementStatus.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusSettlementStatus.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusRelayOrderQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusRelayOrderQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusPickupPointVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusPickupPointVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusDeliveryRuleVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusDeliveryRuleVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCustomerProfileMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCustomerProfileMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusPickupPointMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusPickupPointMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCustomerProfileService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCustomerProfileService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusPickupPointService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusPickupPointService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCustomerProfileServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCustomerProfileServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusPickupPointServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusPickupPointServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusPublicController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusPublicController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-02a-db-and-backend-skeleton.md](step-02a-db-and-backend-skeleton.md)

## Step 02B - 只读接口完善

- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusRelayOrderQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusRelayOrderQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusDeliveryRuleVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusDeliveryRuleVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusPublicController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusPublicController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-02b-readonly-api.md](step-02b-readonly-api.md)

## Step 03A - customer 订单创建与模拟支付

- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderCreateDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderCreateDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCustomerOrderQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCustomerOrderQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusCustomerOrderIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusCustomerOrderIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-03a-customer-order-create-and-pay.md](step-03a-customer-order-create-and-pay.md)

## Step 03B - courier 资料链路与 admin 最小审核/详情

- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierProfileSubmitDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierProfileSubmitDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierReviewDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierReviewDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierReviewStatusVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierReviewStatusVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierProfileController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierProfileController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusCourierProfileIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusCourierProfileIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-03b-courier-profile-and-admin-review.md](step-03b-courier-profile-and-admin-review.md)

## Step 03C - courier token 与最小接单链路

- [backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java](../../backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierAvailableOrderQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierAvailableOrderQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierAuthController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierAuthController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusCourierProfileIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusCourierProfileIntegrationTest.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusCourierAcceptIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusCourierAcceptIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-03c-courier-token-and-accept.md](step-03c-courier-token-and-accept.md)

## Step 03D - courier 取餐 / 配送推进 / customer 确认送达

- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierPickupDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierPickupDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierDeliverDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierDeliverDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusCourierDeliveryIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusCourierDeliveryIntegrationTest.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-03d-pickup-deliver-confirm.md](step-03d-pickup-deliver-confirm.md)

## Step 03E - customer 取消 / 售后 / admin 时间线 / 结算联动

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V2__campus_relay_schema.sql](../../backend/db/migrations/V2__campus_relay_schema.sql)
- [backend/db/migrations/V3__campus_relay_order_timeline_columns.sql](../../backend/db/migrations/V3__campus_relay_order_timeline_columns.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/resources/db/data-h2.sql](../../backend/src/main/resources/db/data-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java](../../backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderCancelDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderCancelDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderAfterSaleDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerOrderAfterSaleDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderTimelineItemVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderTimelineItemVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderTimelineVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderTimelineVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusOrderExceptionIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusOrderExceptionIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-03e-cancel-after-sale-timeline-settlement.md](step-03e-cancel-after-sale-timeline-settlement.md)

## Step 03F - admin 售后处理 / courier 异常上报 / courier 位置上报 / admin 结算分页

- [backend/pom.xml](../../backend/pom.xml)
- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V4__campus_relay_ops_and_settlement.sql](../../backend/db/migrations/V4__campus_relay_ops_and_settlement.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/resources/db/data-h2.sql](../../backend/src/main/resources/db/data-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/exception/GlobalExceptionHandler.java](../../backend/src/main/java/com/cangqiong/takeaway/exception/GlobalExceptionHandler.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusRelayOrderStatus.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleHandleAction.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleHandleAction.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleHandleDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleHandleDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierExceptionReportDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierExceptionReportDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierLocationReportDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCourierLocationReportDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierLocationController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCourierLocationController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusStep03FIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusStep03FIntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-03f-admin-after-sale-exception-location-settlement.md](step-03f-admin-after-sale-exception-location-settlement.md)

## Step 04 - 售后决策 / 结算确认 / admin 运营只读接口

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V5__campus_after_sale_decision_and_settlement_ops.sql](../../backend/db/migrations/V5__campus_after_sale_decision_and_settlement_ops.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/resources/db/data-h2.sql](../../backend/src/main/resources/db/data-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleDecisionType.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleDecisionType.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleDecisionDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleDecisionDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementConfirmDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementConfirmDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleOrderQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleOrderQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierRecentExceptionQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierRecentExceptionQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminCourierLocationReportQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminCourierLocationReportQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierRecentExceptionVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierRecentExceptionVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusLocationReportVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusLocationReportVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusStep04IntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusStep04IntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-04-after-sale-decision-settlement-admin-ops.md](step-04-after-sale-decision-settlement-admin-ops.md)

## Step 05 - 售后执行记录 / 结算打款运营 / 订单级运营查询

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V6__campus_after_sale_execution_and_settlement_payout.sql](../../backend/db/migrations/V6__campus_after_sale_execution_and_settlement_payout.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/resources/db/data-h2.sql](../../backend/src/main/resources/db/data-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleExecutionStatus.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusAfterSaleExecutionStatus.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusPayoutStatus.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/enums/CampusPayoutStatus.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleExecutionDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminAfterSaleExecutionDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementPayoutResultDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementPayoutResultDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementBatchPayoutDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementBatchPayoutDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleOrderQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleOrderQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminOrderLocationReportQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminOrderLocationReportQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleResultVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleResultVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementBatchPayoutResultVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementBatchPayoutResultVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementReconcileSummaryVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementReconcileSummaryVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderExceptionSummaryVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusOrderExceptionSummaryVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusLocationReportService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusLocationReportServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusLocationReportMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusStep05IntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusStep05IntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-05-after-sale-execution-payout-and-order-ops.md](step-05-after-sale-execution-payout-and-order-ops.md)

## Step 06 - customer 售后结果回执 / admin 执行纠正审计 / settlement 批次核对

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V7__campus_customer_receipt_and_settlement_audit.sql](../../backend/db/migrations/V7__campus_customer_receipt_and_settlement_audit.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusRelayOrder.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementRecord.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementVerifyPayoutDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementVerifyPayoutDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleExecutionQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAdminAfterSaleExecutionQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusRelayOrderService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerAfterSaleResultVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerAfterSaleResultVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleExecutionVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleExecutionVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleResultVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAdminAfterSaleResultVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementRecordVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementPayoutBatchVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementPayoutBatchVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementPayoutBatchDetailVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementPayoutBatchDetailVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusStep06IntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusStep06IntegrationTest.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-06-customer-receipt-execution-audit-and-payout-verify.md](step-06-customer-receipt-execution-audit-and-payout-verify.md)

## Step 07 - customer onboarding 替代链路 / frontend 最小接入

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V8__campus_courier_onboarding_bridge_replacement.sql](../../backend/db/migrations/V8__campus_courier_onboarding_bridge_replacement.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/resources/db/data-h2.sql](../../backend/src/main/resources/db/data-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCourierProfile.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCourierProfile.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerCourierOnboardingController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerCourierOnboardingController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerCourierOnboardingSubmitDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusCustomerCourierOnboardingSubmitDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerCourierOnboardingProfileVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerCourierOnboardingProfileVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerCourierOnboardingReviewStatusVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCustomerCourierOnboardingReviewStatusVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierTokenEligibilityVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierTokenEligibilityVO.java)
- [backend/src/test/java/com/cangqiong/takeaway/CampusCourierOnboardingIntegrationTest.java](../../backend/src/test/java/com/cangqiong/takeaway/CampusCourierOnboardingIntegrationTest.java)
- [frontend/src/utils/request.js](../../frontend/src/utils/request.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/api/campus-customer.js](../../frontend/src/api/campus-customer.js)
- [frontend/src/views/user/Profile.vue](../../frontend/src/views/user/Profile.vue)
- [frontend/src/views/user/AfterSaleResult.vue](../../frontend/src/views/user/AfterSaleResult.vue)
- [frontend/src/views/user/CourierOnboarding.vue](../../frontend/src/views/user/CourierOnboarding.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-07-customer-onboarding-and-frontend-entry.md](step-07-customer-onboarding-and-frontend-entry.md)

## Step 08 - admin settlement 批次演示页 / frontend 最小只读运营接入

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/CampusSettlementBatchList.vue](../../frontend/src/views/CampusSettlementBatchList.vue)
- [frontend/src/views/CampusSettlementBatchDetail.vue](../../frontend/src/views/CampusSettlementBatchDetail.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-08-admin-settlement-batch-demo-page.md](step-08-admin-settlement-batch-demo-page.md)

## Step 09 - admin 售后执行演示页 / frontend 第二个只读运营入口

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/CampusAfterSaleExecutionList.vue](../../frontend/src/views/CampusAfterSaleExecutionList.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-09-admin-after-sale-execution-demo-page.md](step-09-admin-after-sale-execution-demo-page.md)

## Step 10 - admin courier 异常/位置联动演示页 / frontend 第三个只读运营入口

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/CampusCourierOpsView.vue](../../frontend/src/views/CampusCourierOpsView.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-10-admin-courier-ops-demo-page.md](step-10-admin-courier-ops-demo-page.md)

## Step 11 - admin settlement 只读运营页 / frontend 第四个只读运营入口

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/CampusSettlementOpsView.vue](../../frontend/src/views/CampusSettlementOpsView.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-11-admin-settlement-ops-demo-page.md](step-11-admin-settlement-ops-demo-page.md)

## Step 12 - onboarding token 申请衔接 / bridge 收口条件细化 / admin 演示页小修

- [frontend/src/api/campus-customer.js](../../frontend/src/api/campus-customer.js)
- [frontend/src/utils/request.js](../../frontend/src/utils/request.js)
- [frontend/src/stores/customer.js](../../frontend/src/stores/customer.js)
- [frontend/src/views/user/CourierOnboarding.vue](../../frontend/src/views/user/CourierOnboarding.vue)
- [frontend/src/views/CampusSettlementOpsView.vue](../../frontend/src/views/CampusSettlementOpsView.vue)
- [frontend/src/views/CampusAfterSaleExecutionList.vue](../../frontend/src/views/CampusAfterSaleExecutionList.vue)
- [frontend/src/views/CampusCourierOpsView.vue](../../frontend/src/views/CampusCourierOpsView.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-12-onboarding-token-bridge-and-demo-polish.md](step-12-onboarding-token-bridge-and-demo-polish.md)

## Step 13 - courier workbench 最小承接页 / bridge 收口证据链细化

- [frontend/src/api/campus-courier.js](../../frontend/src/api/campus-courier.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/utils/request.js](../../frontend/src/utils/request.js)
- [frontend/src/views/user/Profile.vue](../../frontend/src/views/user/Profile.vue)
- [frontend/src/views/user/CourierOnboarding.vue](../../frontend/src/views/user/CourierOnboarding.vue)
- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-13-courier-workbench-and-bridge-evidence.md](step-13-courier-workbench-and-bridge-evidence.md)

## Step 14 - bridge 真实调用盘点 / courier workbench 最小接单承接

- [frontend/src/api/campus-courier.js](../../frontend/src/api/campus-courier.js)
- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-14-bridge-audit-and-workbench-accept.md](step-14-bridge-audit-and-workbench-accept.md)

## Step 15 - bridge 依赖评估细化 / courier workbench 订单详情承接

- [frontend/src/api/campus-courier.js](../../frontend/src/api/campus-courier.js)
- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-15-bridge-evidence-and-workbench-detail.md](step-15-bridge-evidence-and-workbench-detail.md)

## Step 16 - bridge 收口计划评估 / courier workbench 最小取餐承接

- [frontend/src/api/campus-courier.js](../../frontend/src/api/campus-courier.js)
- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-16-bridge-plan-and-workbench-pickup.md](step-16-bridge-plan-and-workbench-pickup.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)

## Step 17 - bridge 执行前评估 / courier workbench 最小 deliver 承接

- [frontend/src/api/campus-courier.js](../../frontend/src/api/campus-courier.js)
- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/step-17-bridge-readiness-and-workbench-deliver.md](step-17-bridge-readiness-and-workbench-deliver.md)

## Step 18 - bridge 执行准备清单 / courier workbench 最小异常上报承接

- [frontend/src/api/campus-courier.js](../../frontend/src/api/campus-courier.js)
- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/step-18-bridge-readiness-checklist-and-workbench-exception.md](step-18-bridge-readiness-checklist-and-workbench-exception.md)

## Step 19 - bridge 模板文档 / courier workbench confirm 前可视化

- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/bridge-regression-template.md](bridge-regression-template.md)
- [project-logs/campus-relay/step-19-bridge-templates-and-workbench-confirm-visual.md](step-19-bridge-templates-and-workbench-confirm-visual.md)

## Step 20 - bridge 模板可执行化 / courier workbench completed 后最小承接

- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/bridge-regression-template.md](bridge-regression-template.md)
- [project-logs/campus-relay/step-20-bridge-template-hardening-and-workbench-completed-visual.md](step-20-bridge-template-hardening-and-workbench-completed-visual.md)

## Step 21 - bridge 局部真实验证 / courier workbench completed 结果回读

- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/bridge-regression-template.md](bridge-regression-template.md)
- [project-logs/campus-relay/step-21-real-verification-and-workbench-completed-readback.md](step-21-real-verification-and-workbench-completed-readback.md)

## Step 22 - H2 可接单数据补齐 / 本地完整链路真实联调

- [backend/src/main/resources/db/data-h2.sql](../../backend/src/main/resources/db/data-h2.sql)
- [frontend/src/layout/UserLayout.vue](../../frontend/src/layout/UserLayout.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/bridge-regression-template.md](bridge-regression-template.md)
- [project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md](step-22-real-local-chain-and-h2-seed.md)

## Step 23 - 共享回归留痕 / customer completed 结果回看

- [frontend/src/api/campus-customer.js](../../frontend/src/api/campus-customer.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/views/user/Profile.vue](../../frontend/src/views/user/Profile.vue)
- [frontend/src/views/user/CampusOrderResult.vue](../../frontend/src/views/user/CampusOrderResult.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/bridge-regression-template.md](bridge-regression-template.md)
- [project-logs/campus-relay/step-23-shared-regression-evidence.md](step-23-shared-regression-evidence.md)
- [project-logs/campus-relay/step-23-shared-regression-and-customer-result-readback.md](step-23-shared-regression-and-customer-result-readback.md)

## Step 24 - bridge 核实准备增强 / customer 结果页体验增强

- [frontend/src/views/user/CampusOrderResult.vue](../../frontend/src/views/user/CampusOrderResult.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/bridge-regression-template.md](bridge-regression-template.md)
- [project-logs/campus-relay/step-24-bridge-readiness-and-customer-result-polish.md](step-24-bridge-readiness-and-customer-result-polish.md)

## Step 25 - repo 外 bridge 依赖核实与证据回填

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/bridge-regression-template.md](bridge-regression-template.md)
- [project-logs/campus-relay/step-25-repo-external-bridge-verification.md](step-25-repo-external-bridge-verification.md)

## Step 26 - repo 外真实资产追补与阻塞项重新评估

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-25-repo-external-bridge-verification.md](step-25-repo-external-bridge-verification.md)
- [project-logs/campus-relay/step-26-repo-external-evidence-closure.md](step-26-repo-external-evidence-closure.md)

## Step 27 - 关键外部资产追补与阻塞项继续关闭

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-25-repo-external-bridge-verification.md](step-25-repo-external-bridge-verification.md)
- [project-logs/campus-relay/step-26-repo-external-evidence-closure.md](step-26-repo-external-evidence-closure.md)
- [project-logs/campus-relay/step-27-key-external-asset-verification.md](step-27-key-external-asset-verification.md)

## Step 28 - 关键业务资产追补与阻塞项继续关闭

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-25-repo-external-bridge-verification.md](step-25-repo-external-bridge-verification.md)
- [project-logs/campus-relay/step-26-repo-external-evidence-closure.md](step-26-repo-external-evidence-closure.md)
- [project-logs/campus-relay/step-27-key-external-asset-verification.md](step-27-key-external-asset-verification.md)
- [project-logs/campus-relay/step-28-critical-business-asset-followup.md](step-28-critical-business-asset-followup.md)

## Step 29 - owner 确认回填 / bridge repo 外阻塞关闭与阶段重评估

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-29-owner-confirmation-and-bridge-reassessment.md](step-29-owner-confirmation-and-bridge-reassessment.md)

## Step 30 - Phase A 执行准备重新评估

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-29-owner-confirmation-and-bridge-reassessment.md](step-29-owner-confirmation-and-bridge-reassessment.md)
- [project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md](step-30-phase-a-readiness-reassessment.md)

## Step 31 - 最小 Phase A 动作候选评估 / 执行前最小回归复核

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md](step-30-phase-a-readiness-reassessment.md)
- [project-logs/campus-relay/step-31-minimal-phase-a-candidate-and-preflight.md](step-31-minimal-phase-a-candidate-and-preflight.md)

## Step 32 - Phase A 候选池扩展与 go / no-go 决策

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-31-minimal-phase-a-candidate-and-preflight.md](step-31-minimal-phase-a-candidate-and-preflight.md)
- [project-logs/campus-relay/step-32-phase-a-candidate-expansion-and-go-no-go.md](step-32-phase-a-candidate-expansion-and-go-no-go.md)

## Step 33 - Phase A no-op 冻结态 / 恢复推进条件

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)
- [project-logs/campus-relay/bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
- [project-logs/campus-relay/step-32-phase-a-candidate-expansion-and-go-no-go.md](step-32-phase-a-candidate-expansion-and-go-no-go.md)
- [project-logs/campus-relay/step-33-phase-a-no-op-freeze-and-resume-criteria.md](step-33-phase-a-no-op-freeze-and-resume-criteria.md)

## Step 34 - 非 bridge 方向收束 / 展示级优化候选评估

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-33-phase-a-no-op-freeze-and-resume-criteria.md](step-33-phase-a-no-op-freeze-and-resume-criteria.md)
- [project-logs/campus-relay/step-34-non-bridge-direction-and-display-polish-assessment.md](step-34-non-bridge-direction-and-display-polish-assessment.md)

## Step 35 - 展示级优化执行轮 1

- [frontend/src/views/user/CampusOrderResult.vue](../../frontend/src/views/user/CampusOrderResult.vue)
- [frontend/src/views/courier/CourierWorkbench.vue](../../frontend/src/views/courier/CourierWorkbench.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-34-non-bridge-direction-and-display-polish-assessment.md](step-34-non-bridge-direction-and-display-polish-assessment.md)
- [project-logs/campus-relay/step-35-display-polish-execution-round-1.md](step-35-display-polish-execution-round-1.md)

本轮没有修改 bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 36 - 展示级优化执行轮 2

- [frontend/src/views/user/CourierOnboarding.vue](../../frontend/src/views/user/CourierOnboarding.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-35-display-polish-execution-round-1.md](step-35-display-polish-execution-round-1.md)
- [project-logs/campus-relay/step-36-display-polish-execution-round-2.md](step-36-display-polish-execution-round-2.md)

本轮选择方案 A，只处理 `CourierOnboarding.vue`；没有修改 bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 37 - 展示级优化执行轮 3

- [frontend/src/views/CampusSettlementOpsView.vue](../../frontend/src/views/CampusSettlementOpsView.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-36-display-polish-execution-round-2.md](step-36-display-polish-execution-round-2.md)
- [project-logs/campus-relay/step-37-display-polish-execution-round-3.md](step-37-display-polish-execution-round-3.md)

本轮选择 settlement，只处理 `CampusSettlementOpsView.vue`；没有修改 bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 38 - 展示级优化执行轮 4

- [frontend/src/views/CampusAfterSaleExecutionList.vue](../../frontend/src/views/CampusAfterSaleExecutionList.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-37-display-polish-execution-round-3.md](step-37-display-polish-execution-round-3.md)
- [project-logs/campus-relay/step-38-display-polish-execution-round-4.md](step-38-display-polish-execution-round-4.md)

本轮只处理 `CampusAfterSaleExecutionList.vue`；没有修改 bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 39 - 展示 polish 复盘与冻结判断

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-38-display-polish-execution-round-4.md](step-38-display-polish-execution-round-4.md)
- [project-logs/campus-relay/step-39-display-polish-review-and-freeze-decision.md](step-39-display-polish-review-and-freeze-decision.md)

本轮是展示 polish 复盘与冻结判断轮，仅修改日志和评估文档；没有修改业务代码、bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 40 - 交付整理与演示脚本固化

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-39-display-polish-review-and-freeze-decision.md](step-39-display-polish-review-and-freeze-decision.md)
- [project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md](step-40-delivery-packaging-and-demo-script.md)

本轮是交付整理轮，仅修改日志和交付说明文档；没有修改业务代码、bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 41 - 交付材料补完 / 截图清单 / 录屏顺序 / 演示前检查

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md](step-40-delivery-packaging-and-demo-script.md)
- [project-logs/campus-relay/step-41-delivery-assets-checklist-and-recording-plan.md](step-41-delivery-assets-checklist-and-recording-plan.md)

本轮是交付材料补完轮，仅修改日志和交付说明文档；没有修改业务代码、bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 42 - 真实媒体采集与归档

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-41-delivery-assets-checklist-and-recording-plan.md](step-41-delivery-assets-checklist-and-recording-plan.md)
- [project-logs/campus-relay/step-42-real-media-capture-and-archive.md](step-42-real-media-capture-and-archive.md)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/01-customer-onboarding-entry.png](runtime/step-42-media/screenshots/01-customer-onboarding-entry.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/02-onboarding-review-status-token-eligibility.png](runtime/step-42-media/screenshots/02-onboarding-review-status-token-eligibility.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/03-courier-token-issued.png](runtime/step-42-media/screenshots/03-courier-token-issued.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/04-courier-workbench-home.png](runtime/step-42-media/screenshots/04-courier-workbench-home.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/05-available-order-list.png](runtime/step-42-media/screenshots/05-available-order-list.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/06-courier-order-detail-drawer.png](runtime/step-42-media/screenshots/06-courier-order-detail-drawer.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/07-pickup-state.png](runtime/step-42-media/screenshots/07-pickup-state.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/08-deliver-awaiting-confirmation.png](runtime/step-42-media/screenshots/08-deliver-awaiting-confirmation.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/09-exception-report-result.png](runtime/step-42-media/screenshots/09-exception-report-result.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/10-customer-confirm-awaiting.png](runtime/step-42-media/screenshots/10-customer-confirm-awaiting.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/11-customer-completed-result.png](runtime/step-42-media/screenshots/11-customer-completed-result.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/12-admin-settlement-ops.png](runtime/step-42-media/screenshots/12-admin-settlement-ops.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/13-admin-after-sale-execution.png](runtime/step-42-media/screenshots/13-admin-after-sale-execution.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/10a-customer-confirm-blocked-after-exception.png](runtime/step-42-media/screenshots/10a-customer-confirm-blocked-after-exception.png)
- [project-logs/campus-relay/runtime/step-42-media/screenshots/11a-customer-result-after-failed-confirm.png](runtime/step-42-media/screenshots/11a-customer-result-after-failed-confirm.png)
- [project-logs/campus-relay/runtime/step-42-media/videos/01-onboarding-token.webm](runtime/step-42-media/videos/01-onboarding-token.webm)
- [project-logs/campus-relay/runtime/step-42-media/videos/02-courier-workbench-actions.webm](runtime/step-42-media/videos/02-courier-workbench-actions.webm)
- [project-logs/campus-relay/runtime/step-42-media/videos/03-customer-confirm-completed-readback.webm](runtime/step-42-media/videos/03-customer-confirm-completed-readback.webm)
- [project-logs/campus-relay/runtime/step-42-media/videos/04-admin-readonly-ops.webm](runtime/step-42-media/videos/04-admin-readonly-ops.webm)
- [project-logs/campus-relay/runtime/step-42-media/videos/03a-customer-confirm-blocked-after-exception.webm](runtime/step-42-media/videos/03a-customer-confirm-blocked-after-exception.webm)

本轮是真实媒体采集轮，新增截图与录屏文件并回填媒体索引；没有修改业务代码、bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 43 - 媒体缺口分叉判断 / 非 bridge 后端评估入口

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-42-real-media-capture-and-archive.md](step-42-real-media-capture-and-archive.md)
- [project-logs/campus-relay/step-43-media-gap-gate-or-non-bridge-backend-assessment.md](step-43-media-gap-gate-or-non-bridge-backend-assessment.md)

本轮选择路径 B，是判断/评估轮：不补固定 after-sale 样本，不继续补媒体，不写代码；只完成非 bridge 后端方向评估。没有修改业务代码、bridge、接口、鉴权、路由或后端代码，也没有新增页面。

## Step 44 - 异常历史与处理闭环最小方案设计

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-43-media-gap-gate-or-non-bridge-backend-assessment.md](step-43-media-gap-gate-or-non-bridge-backend-assessment.md)
- [project-logs/campus-relay/step-44-exception-history-minimal-solution-design.md](step-44-exception-history-minimal-solution-design.md)

本轮是异常历史与处理闭环方案设计轮：只设计数据模型、状态边界、接口边界和 latest exception 兼容策略；没有修改业务代码、SQL、bridge、接口实现、鉴权、路由或前端页面。

## Step 45A - 异常历史最小实现

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V9__campus_exception_record.sql](../../backend/db/migrations/V9__campus_exception_record.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusExceptionRecord.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusExceptionRecord.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusExceptionRecordQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusExceptionRecordQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusExceptionRecordVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusExceptionRecordVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusExceptionRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusExceptionRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusExceptionRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusExceptionRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusExceptionRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusExceptionRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminExceptionController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminExceptionController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-44-exception-history-minimal-solution-design.md](step-44-exception-history-minimal-solution-design.md)
- [project-logs/campus-relay/step-45a-exception-history-minimal-implementation.md](step-45a-exception-history-minimal-implementation.md)

本轮是异常历史最小后端实现轮：新增异常历史表、写入链路和 admin 只读查询接口；没有修改 bridge、前端页面、路由、鉴权，也没有新增 resolve 处理接口。

## Step 45B - 异常最小处理动作设计

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-45a-exception-history-minimal-implementation.md](step-45a-exception-history-minimal-implementation.md)
- [project-logs/campus-relay/step-45b-exception-resolve-minimal-action-design.md](step-45b-exception-resolve-minimal-action-design.md)

本轮是异常最小处理动作方案评估轮：只设计 `REPORTED -> RESOLVED` 的 resolve 边界；没有修改业务代码、SQL、前端页面、bridge、接口实现、鉴权或路由。

## Step 46 - 异常 resolve 最小实现

- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminExceptionResolveDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminExceptionResolveDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminExceptionController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminExceptionController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusExceptionRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusExceptionRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusExceptionRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusExceptionRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusExceptionRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusExceptionRecordMapper.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-45b-exception-resolve-minimal-action-design.md](step-45b-exception-resolve-minimal-action-design.md)
- [project-logs/campus-relay/step-46-exception-resolve-minimal-implementation.md](step-46-exception-resolve-minimal-implementation.md)

本轮是异常 resolve 最小后端实现轮：新增 `POST /api/campus/admin/exceptions/{id}/resolve` 和最小请求 DTO，只更新异常历史处理字段；没有修改 bridge、前端页面、路由、鉴权、订单主状态或 settlement。

## Step 47 - admin 异常前端承接 go / no-go

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-46-exception-resolve-minimal-implementation.md](step-46-exception-resolve-minimal-implementation.md)
- [project-logs/campus-relay/step-47-admin-exception-frontend-go-no-go.md](step-47-admin-exception-frontend-go-no-go.md)

本轮是 admin 异常前端承接 go / no-go 评估轮：最终选择方向 A，建议 Step 48 进入 admin 异常历史 / resolve 最小前端承接方案与实现准备；没有修改业务代码、前端页面、后端接口、bridge、鉴权、路由、订单主状态或 settlement。

## Step 48 - admin 异常历史 / resolve 最小前端承接

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/router/index.js](../../frontend/src/router/index.js)
- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/CampusExceptionOpsView.vue](../../frontend/src/views/CampusExceptionOpsView.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-47-admin-exception-frontend-go-no-go.md](step-47-admin-exception-frontend-go-no-go.md)
- [project-logs/campus-relay/step-48-admin-exception-frontend-minimal-handoff.md](step-48-admin-exception-frontend-minimal-handoff.md)

本轮新增 admin 异常处理最小前端承接：新增 `/campus/exceptions`、异常历史列表、详情 drawer 和 `REPORTED -> RESOLVED` 最小 resolve 区；没有修改后端接口、bridge、鉴权、订单主状态、settlement、latest exception 摘要或旧前端主链路。

## Step 49 - admin 异常处理页运行态验证

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-48-admin-exception-frontend-minimal-handoff.md](step-48-admin-exception-frontend-minimal-handoff.md)
- [project-logs/campus-relay/step-49-admin-exception-runtime-validation.md](step-49-admin-exception-runtime-validation.md)
- [project-logs/campus-relay/runtime/step-49/admin-exception-api-validation.json](runtime/step-49/admin-exception-api-validation.json)
- [project-logs/campus-relay/runtime/step-49/admin-exception-page-validation.json](runtime/step-49/admin-exception-page-validation.json)

本轮是 admin 异常处理页 H2/test 运行态验证轮：真实验证异常历史列表、详情、resolve、重复 resolve 失败、latest exception 兼容回读和前端页面加载；没有修改业务代码、bridge、接口、鉴权、路由、订单主状态或 settlement，也没有新增页面。

## Step 50 - 售后执行历史表最小方案设计

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-49-admin-exception-runtime-validation.md](step-49-admin-exception-runtime-validation.md)
- [project-logs/campus-relay/step-50-after-sale-execution-history-design.md](step-50-after-sale-execution-history-design.md)

本轮是 P2 售后执行历史表方案设计轮：只设计 `campus_after_sale_execution_record` 的数据边界、写入时机、状态边界、只读接口边界和当前摘要兼容策略；没有修改业务代码、SQL、前端页面、bridge、接口实现、鉴权或路由。

## Step 51A - 售后执行历史最小实现

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/V10__campus_after_sale_execution_record.sql](../../backend/db/migrations/V10__campus_after_sale_execution_record.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusAfterSaleExecutionRecord.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusAfterSaleExecutionRecord.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAfterSaleExecutionRecordQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusAfterSaleExecutionRecordQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAfterSaleExecutionRecordVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusAfterSaleExecutionRecordVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusAfterSaleExecutionRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusAfterSaleExecutionRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusAfterSaleExecutionRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusAfterSaleExecutionRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusAfterSaleExecutionRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusAfterSaleExecutionRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminAfterSaleExecutionRecordController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminAfterSaleExecutionRecordController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-50-after-sale-execution-history-design.md](step-50-after-sale-execution-history-design.md)
- [project-logs/campus-relay/step-51a-after-sale-execution-history-minimal-implementation.md](step-51a-after-sale-execution-history-minimal-implementation.md)
- [project-logs/campus-relay/runtime/step-51a/after-sale-execution-history-validation.json](runtime/step-51a/after-sale-execution-history-validation.json)

本轮是售后执行历史最小后端实现轮：新增历史表、现有售后执行接口同事务追加历史写入、admin 只读分页查询接口，并完成 H2/test 运行态验证；没有新增前端页面，没有修改 bridge、鉴权、路由、订单主状态、settlement 或旧外卖模块。

## Step 51B - 售后执行历史前端承接 go / no-go

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-51a-after-sale-execution-history-minimal-implementation.md](step-51a-after-sale-execution-history-minimal-implementation.md)
- [project-logs/campus-relay/step-51b-after-sale-execution-history-frontend-go-no-go.md](step-51b-after-sale-execution-history-frontend-go-no-go.md)

本轮是售后执行历史前端承接 go / no-go 评估轮：最终选择方向 A，建议 Step 52 在现有 `CampusAfterSaleExecutionList.vue` 详情 drawer 内增加只读执行历史区；没有修改业务代码、前端页面、后端接口、SQL、bridge、鉴权或路由。

## Step 52 - 售后执行历史前端最小承接

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/views/CampusAfterSaleExecutionList.vue](../../frontend/src/views/CampusAfterSaleExecutionList.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-51b-after-sale-execution-history-frontend-go-no-go.md](step-51b-after-sale-execution-history-frontend-go-no-go.md)
- [project-logs/campus-relay/step-52-after-sale-execution-history-frontend-minimal-handoff.md](step-52-after-sale-execution-history-frontend-minimal-handoff.md)

本轮是售后执行历史前端最小承接实现轮：只在现有售后执行页详情 drawer 内新增只读执行历史区，并新增 admin API 封装；没有新增页面、路由、后端接口、写操作，也没有修改 bridge、鉴权、订单主状态、settlement 或旧外卖模块。

## Step 53 - 售后执行历史前端运行态验证

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-52-after-sale-execution-history-frontend-minimal-handoff.md](step-52-after-sale-execution-history-frontend-minimal-handoff.md)
- [project-logs/campus-relay/step-53-after-sale-execution-history-frontend-runtime-validation.md](step-53-after-sale-execution-history-frontend-runtime-validation.md)
- [project-logs/campus-relay/runtime/step-53/after-sale-execution-history-api-validation.json](runtime/step-53/after-sale-execution-history-api-validation.json)
- [project-logs/campus-relay/runtime/step-53/after-sale-execution-history-page-validation.json](runtime/step-53/after-sale-execution-history-page-validation.json)

本轮是售后执行历史前端运行态验证轮：H2/test 下真实生成售后执行历史样本，并用 Playwright 验证 `/campus/after-sale-executions` 详情 drawer 当前摘要和执行历史区；没有修改业务代码、前端页面、后端接口、SQL、bridge、鉴权或路由。

## Step 54 - settlement 批次复核、撤回和对账最小方案设计

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-53-after-sale-execution-history-frontend-runtime-validation.md](step-53-after-sale-execution-history-frontend-runtime-validation.md)
- [project-logs/campus-relay/step-54-settlement-batch-review-withdraw-reconcile-design.md](step-54-settlement-batch-review-withdraw-reconcile-design.md)

本轮是 P3 settlement 批次复核、撤回和对账方案设计轮：只设计批次操作审计、对账差异记录和现有 settlement 摘要兼容策略；没有修改业务代码、SQL、前端页面、bridge、接口实现、鉴权或路由。

## Step 55 - 旧外卖文档清理与目录归档

- [README.md](../../README.md)
- [CHANGELOG.md](../../CHANGELOG.md)
- [CONTRIBUTING.md](../../CONTRIBUTING.md)
- [SECURITY.md](../../SECURITY.md)
- [docs/README.md](../../docs/README.md)
- [docs/delivery-guide.md](../../docs/delivery-guide.md)
- [docs/project-status-review.md](../../docs/project-status-review.md)
- [docs/api-overview.md](../../docs/api-overview.md)
- [docs/db-overview.md](../../docs/db-overview.md)
- [docs/campus-relay/legacy-to-campus-mapping.md](../../docs/campus-relay/legacy-to-campus-mapping.md)
- [docs/deployment/production-deploy.md](../../docs/deployment/production-deploy.md)
- [docs/deployment/backup-and-rollback.md](../../docs/deployment/backup-and-rollback.md)
- [docs/legacy-takeaway/README.md](../../docs/legacy-takeaway/README.md)
- [docs/legacy-takeaway/api-design.md](../../docs/legacy-takeaway/api-design.md)
- [docs/legacy-takeaway/db-design.md](../../docs/legacy-takeaway/db-design.md)
- [frontend/README.md](../../frontend/README.md)
- [backend/db/migrations/README.md](../../backend/db/migrations/README.md)
- [project-logs/README.md](../README.md)
- [project-logs/legacy-takeaway/](../legacy-takeaway/)
- [project-logs/legacy-takeaway/README.md](../legacy-takeaway/README.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-55-docs-cleanup-and-legacy-archive.md](step-55-docs-cleanup-and-legacy-archive.md)

本轮是旧外卖文档清理与目录归档轮：将旧外卖文档和旧修复日志归档到 legacy 目录，并把当前入口文档改为校园代送试运营口径；没有修改 Java、SQL、Vue 业务代码、接口、bridge、鉴权、路由或旧外卖模块。

## Step 56 - settlement 批次操作审计 go / no-go

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-55-docs-cleanup-and-legacy-archive.md](step-55-docs-cleanup-and-legacy-archive.md)
- [project-logs/campus-relay/step-56-settlement-batch-operation-audit-go-no-go.md](step-56-settlement-batch-operation-audit-go-no-go.md)

本轮是 settlement 批次操作审计 go / no-go 决策轮：选择 Step 57 进入 `campus_settlement_batch_operation_record` 最小实现，并明确 review / withdraw 只写批次操作审计，不改 `payout_status`、不清空 `payout_batch_no`、不做真实财务撤回；本轮没有修改 Java、SQL、Vue 业务代码、接口实现、bridge、鉴权、路由或旧外卖模块。

## Step 57 - settlement 批次操作审计最小实现

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/README.md](../../backend/db/migrations/README.md)
- [backend/db/migrations/V11__campus_settlement_batch_operation_record.sql](../../backend/db/migrations/V11__campus_settlement_batch_operation_record.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementBatchOperationController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementBatchOperationController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementBatchOperationDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementBatchOperationDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementBatchOperationRecord.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementBatchOperationRecord.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementBatchOperationRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementBatchOperationRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementBatchOperationQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementBatchOperationQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementBatchOperationRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementBatchOperationRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementBatchOperationRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementBatchOperationRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementBatchOperationRecordVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementBatchOperationRecordVO.java)
- [docs/api-overview.md](../../docs/api-overview.md)
- [docs/db-overview.md](../../docs/db-overview.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-56-settlement-batch-operation-audit-go-no-go.md](step-56-settlement-batch-operation-audit-go-no-go.md)
- [project-logs/campus-relay/step-57-settlement-batch-operation-audit-minimal-implementation.md](step-57-settlement-batch-operation-audit-minimal-implementation.md)

本轮是 settlement 批次操作审计最小后端实现轮：新增批次操作审计表、三个 admin 最小接口和当前 API/DB 总览同步；review / withdraw 只写操作审计，不改 `payout_status`、不清空 `payout_batch_no`、不做真实财务撤回；本轮没有新增前端页面，没有改 bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 58 - settlement 批次操作审计运行态验证

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-57-settlement-batch-operation-audit-minimal-implementation.md](step-57-settlement-batch-operation-audit-minimal-implementation.md)
- [project-logs/campus-relay/step-58-settlement-batch-operation-runtime-validation.md](step-58-settlement-batch-operation-runtime-validation.md)
- [project-logs/campus-relay/runtime/step-58/settlement-batch-operation-api-validation.json](runtime/step-58/settlement-batch-operation-api-validation.json)

本轮是 settlement 批次操作审计 H2/test 运行态验证轮：通过 API 生成固定批次 `PBSTEP58VALIDATION`，写入 `REVIEW / PASSED` 与 `WITHDRAW / REQUESTED` 两条操作审计，并验证原 settlement 批次详情与单笔 payout 摘要未被改写；本轮没有修改 Java、SQL、Vue 业务代码，没有新增前端页面，没有改 bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 59 - settlement 批次操作审计前端承接 go / no-go

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-58-settlement-batch-operation-runtime-validation.md](step-58-settlement-batch-operation-runtime-validation.md)
- [project-logs/campus-relay/step-59-settlement-batch-operation-frontend-go-no-go.md](step-59-settlement-batch-operation-frontend-go-no-go.md)

本轮是 settlement 批次操作审计前端承接 go / no-go 评估轮：最终选择方向 A，建议 Step 60 在现有 `CampusSettlementBatchDetail.vue` 中增加 operations 只读承接；本轮没有修改业务代码、前端页面、后端接口、SQL、bridge、鉴权、路由或旧外卖模块。

## Step 60 - settlement 批次操作审计前端最小只读承接

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/views/CampusSettlementBatchDetail.vue](../../frontend/src/views/CampusSettlementBatchDetail.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-59-settlement-batch-operation-frontend-go-no-go.md](step-59-settlement-batch-operation-frontend-go-no-go.md)
- [project-logs/campus-relay/step-60-settlement-batch-operation-frontend-minimal-handoff.md](step-60-settlement-batch-operation-frontend-minimal-handoff.md)

本轮是 settlement 批次操作审计前端最小只读承接实现轮：只在现有批次详情页增加“批次操作历史”只读区，并新增 admin API 封装；没有新增页面、路由、后端接口、写操作，也没有修改 bridge、鉴权、token 附着、订单主状态、settlement payout 摘要或旧外卖模块。

## Step 61 - settlement 批次操作审计前端运行态验证

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-60-settlement-batch-operation-frontend-minimal-handoff.md](step-60-settlement-batch-operation-frontend-minimal-handoff.md)
- [project-logs/campus-relay/step-61-settlement-batch-operation-frontend-runtime-validation.md](step-61-settlement-batch-operation-frontend-runtime-validation.md)
- [project-logs/campus-relay/runtime/step-61/settlement-batch-operation-ui-api-prep.json](runtime/step-61/settlement-batch-operation-ui-api-prep.json)
- [project-logs/campus-relay/runtime/step-61/settlement-batch-operation-page-validation.json](runtime/step-61/settlement-batch-operation-page-validation.json)

本轮是 settlement 批次操作审计前端运行态验证轮：H2/test 下准备固定批次 `PBSTEP61UI` 和两条操作审计记录，并通过 Playwright 验证现有批次详情页可以展示“批次操作历史”只读区；本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面，没有改 bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 62 - settlement 对账差异记录最小方案设计

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-61-settlement-batch-operation-frontend-runtime-validation.md](step-61-settlement-batch-operation-frontend-runtime-validation.md)
- [project-logs/campus-relay/step-62-settlement-reconcile-difference-minimal-solution-design.md](step-62-settlement-reconcile-difference-minimal-solution-design.md)

本轮是 settlement 对账差异记录最小方案设计轮：建议后续新增独立 `campus_settlement_reconcile_difference_record`，明确差异来源、差异类型、`OPEN / RESOLVED` 最小处理状态、admin 最小接口边界和与 `campus_settlement_record` payout 摘要的兼容策略；本轮没有修改 SQL、Java、Vue、接口实现、bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 63 - settlement 对账差异记录实现 go / no-go

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-62-settlement-reconcile-difference-minimal-solution-design.md](step-62-settlement-reconcile-difference-minimal-solution-design.md)
- [project-logs/campus-relay/step-63-settlement-reconcile-difference-implementation-go-no-go.md](step-63-settlement-reconcile-difference-implementation-go-no-go.md)

本轮是 settlement 对账差异记录实现 go / no-go 评估轮：最终选择 Step 64 进入 `campus_settlement_reconcile_difference_record` 最小后端实现，并明确只允许落表、数据库路径同步、admin 列表/详情/创建/resolve 四个最小接口；本轮没有修改 SQL、Java、Vue、接口实现、bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 64 - settlement 对账差异记录最小后端实现

- [backend/db/init.sql](../../backend/db/init.sql)
- [backend/db/migrations/README.md](../../backend/db/migrations/README.md)
- [backend/db/migrations/V12__campus_settlement_reconcile_difference_record.sql](../../backend/db/migrations/V12__campus_settlement_reconcile_difference_record.sql)
- [backend/src/main/resources/db/schema-h2.sql](../../backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementReconcileDifferenceController.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminSettlementReconcileDifferenceController.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementReconcileDifferenceCreateDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementReconcileDifferenceCreateDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementReconcileDifferenceResolveDTO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminSettlementReconcileDifferenceResolveDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementReconcileDifferenceRecord.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusSettlementReconcileDifferenceRecord.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementReconcileDifferenceRecordMapper.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusSettlementReconcileDifferenceRecordMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementReconcileDifferenceQuery.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusSettlementReconcileDifferenceQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementReconcileDifferenceRecordService.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusSettlementReconcileDifferenceRecordService.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementReconcileDifferenceRecordServiceImpl.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusSettlementReconcileDifferenceRecordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementReconcileDifferenceRecordVO.java](../../backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusSettlementReconcileDifferenceRecordVO.java)
- [docs/api-overview.md](../../docs/api-overview.md)
- [docs/db-overview.md](../../docs/db-overview.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-63-settlement-reconcile-difference-implementation-go-no-go.md](step-63-settlement-reconcile-difference-implementation-go-no-go.md)
- [project-logs/campus-relay/step-64-settlement-reconcile-difference-minimal-implementation.md](step-64-settlement-reconcile-difference-minimal-implementation.md)
- [project-logs/campus-relay/runtime/step-64/settlement-reconcile-difference-validation.json](runtime/step-64/settlement-reconcile-difference-validation.json)

本轮是 settlement 对账差异记录最小后端实现轮：新增对账差异审计表、四个 admin 最小接口和 MySQL/H2 数据库路径同步；创建差异必须校验 settlement 存在且批次号匹配，resolve 只允许 `OPEN -> RESOLVED`，不改 settlement payout 摘要、不改 `reconcile-summary`、不接真实财务；本轮没有新增前端页面，没有改 bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 65 - settlement 对账差异前端承接 go / no-go

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-64-settlement-reconcile-difference-minimal-implementation.md](step-64-settlement-reconcile-difference-minimal-implementation.md)
- [project-logs/campus-relay/step-65-settlement-reconcile-difference-frontend-go-no-go.md](step-65-settlement-reconcile-difference-frontend-go-no-go.md)

本轮是 settlement 对账差异前端承接 go / no-go 评估轮：最终选择方向 A，建议 Step 66 在现有 `CampusSettlementOpsView.vue` 详情 drawer 中增加对账差异只读区；本轮没有修改 Vue 页面、API 封装、后端接口、SQL、bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 66 - settlement 对账差异前端最小只读承接

- [frontend/src/api/campus-admin.js](../../frontend/src/api/campus-admin.js)
- [frontend/src/views/CampusSettlementOpsView.vue](../../frontend/src/views/CampusSettlementOpsView.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-65-settlement-reconcile-difference-frontend-go-no-go.md](step-65-settlement-reconcile-difference-frontend-go-no-go.md)
- [project-logs/campus-relay/step-66-settlement-reconcile-difference-frontend-minimal-handoff.md](step-66-settlement-reconcile-difference-frontend-minimal-handoff.md)

本轮是 settlement 对账差异前端最小只读承接实现轮：只在现有 `CampusSettlementOpsView.vue` 详情 drawer 中增加“对账差异记录”只读区，并新增 admin API 封装；没有新增页面、路由、后端接口或写操作，也没有修改 bridge、鉴权、token 附着、settlement payout 摘要、`reconcile-summary` 或旧外卖模块。

## Step 67 - settlement 对账差异前端运行态验证

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-66-settlement-reconcile-difference-frontend-minimal-handoff.md](step-66-settlement-reconcile-difference-frontend-minimal-handoff.md)
- [project-logs/campus-relay/step-67-settlement-reconcile-difference-frontend-runtime-validation.md](step-67-settlement-reconcile-difference-frontend-runtime-validation.md)
- [project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-ui-api-prep.json](runtime/step-67/settlement-reconcile-difference-ui-api-prep.json)
- [project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-page-validation.json](runtime/step-67/settlement-reconcile-difference-page-validation.json)
- [project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-drawer.png](runtime/step-67/settlement-reconcile-difference-drawer.png)

本轮是 settlement 对账差异前端运行态验证轮：H2/test 下准备固定批次 `PBSTEP67UI`、settlement `id=1` 和一条 `AMOUNT_MISMATCH` 差异记录，并通过 Playwright 验证 `/campus/settlements` 详情 drawer 可展示“对账差异记录”只读区；本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面，没有改 bridge、鉴权、路由、token 附着、settlement payout 摘要或旧外卖模块。

## Step 68 - settlement 对账差异前端线收口评估

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-67-settlement-reconcile-difference-frontend-runtime-validation.md](step-67-settlement-reconcile-difference-frontend-runtime-validation.md)
- [project-logs/campus-relay/step-68-settlement-reconcile-difference-frontend-freeze-decision.md](step-68-settlement-reconcile-difference-frontend-freeze-decision.md)

本轮是 settlement 对账差异前端线收口评估轮：基于 Step 64 到 Step 67 已完成的后端闭环、前端只读承接和运行态验证，正式判断当前不继续为 `CampusSettlementOpsView.vue` 接入 resolve 写操作；本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、路由、token 附着、settlement payout 摘要或旧外卖模块。

## Step 69 - settlement P3 主线阶段复盘

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-68-settlement-reconcile-difference-frontend-freeze-decision.md](step-68-settlement-reconcile-difference-frontend-freeze-decision.md)
- [project-logs/campus-relay/step-69-settlement-p3-freeze-review-and-maintenance-decision.md](step-69-settlement-p3-freeze-review-and-maintenance-decision.md)

本轮是 settlement P3 主线阶段复盘轮：正式确认“批次操作审计线”和“对账差异线”都已在后端实现、前端只读承接和运行态验证后收住，当前 settlement P3 主线整体进入冻结/维护态；本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、路由、token 附着、settlement payout 摘要或旧外卖模块。

## Step 70 - 非 bridge 后端三线整体复盘

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-69-settlement-p3-freeze-review-and-maintenance-decision.md](step-69-settlement-p3-freeze-review-and-maintenance-decision.md)
- [project-logs/campus-relay/step-70-non-bridge-backend-lines-overall-review.md](step-70-non-bridge-backend-lines-overall-review.md)

本轮是非 bridge 后端三线整体复盘轮：统一确认异常历史与最小 resolve 线、售后执行历史线、settlement P3 线都已达到“最小闭环 + 前端承接/验证”状态，当前不再默认继续扩单点能力，主线转入整体维护/交付口径复盘；本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 71 - 整体维护 / 交付口径复盘

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-70-non-bridge-backend-lines-overall-review.md](step-70-non-bridge-backend-lines-overall-review.md)
- [project-logs/campus-relay/step-71-overall-maintenance-and-delivery-readiness-review.md](step-71-overall-maintenance-and-delivery-readiness-review.md)

本轮是整体维护 / 交付口径复盘轮：正式确认当前最小闭环已经达到“可试运营、可演示、可交接”的稳定状态，bridge 冻结态、展示 polish 冻结态、媒体线收住、非 bridge 后端三线收住四条主线当前都没有必须继续补的高优先级缺口；本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、路由、token 附着或旧外卖模块。

## Step 72 - 腾讯地图最小产品化试点 / 园区要素运营地图预览

- [frontend/src/views/CampusCourierOpsView.vue](../../frontend/src/views/CampusCourierOpsView.vue)
- [frontend/src/utils/tencentMap.js](../../frontend/src/utils/tencentMap.js)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-71-overall-maintenance-and-delivery-readiness-review.md](step-71-overall-maintenance-and-delivery-readiness-review.md)
- [project-logs/campus-relay/step-72-tencent-map-admin-ops-minimal-pilot.md](step-72-tencent-map-admin-ops-minimal-pilot.md)
- [project-logs/campus-relay/runtime/step-72/tencent-map-sdk-probe.json](runtime/step-72/tencent-map-sdk-probe.json)
- [project-logs/campus-relay/runtime/step-72/campus-courier-ops-map-validation.json](runtime/step-72/campus-courier-ops-map-validation.json)
- [project-logs/campus-relay/runtime/step-72/campus-courier-ops-tencent-map.png](runtime/step-72/campus-courier-ops-tencent-map.png)
- [project-logs/campus-relay/runtime/step-72/campus-courier-ops-map-panel.png](runtime/step-72/campus-courier-ops-map-panel.png)

本轮是腾讯地图最小产品化试点轮：只在现有 `CampusCourierOpsView.vue` 中接入腾讯地图 JS SDK 最小地图预览，复用现有配送员位置上报数据展示真实点位；运行态验证确认 `/campus/courier-ops` 能展示地图比例尺与版权信息。当前没有新增页面、路由、后端接口或写操作，没有改 bridge、鉴权、token 附着、request.js、旧外卖模块，也没有提交真实地图 key 到仓库。

## Step 73 - 腾讯地图产品化线 go / no-go 与试点收口

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-72-tencent-map-admin-ops-minimal-pilot.md](step-72-tencent-map-admin-ops-minimal-pilot.md)
- [project-logs/campus-relay/step-73-tencent-map-productization-go-no-go.md](step-73-tencent-map-productization-go-no-go.md)

本轮是腾讯地图产品化线 go / no-go 评估轮：基于 Step 72 已完成的真实地图试点，正式判断当前不继续扩到第二个既有页面；地图能力收住为 `CampusCourierOpsView.vue` 单页 admin 只读点位预览，保留 JS SDK 试点成果，但不做轨迹、路线、调度、导航、地图写操作或第二个地图页面。本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、token 附着、`request.js` 或旧外卖模块。

## Step 74 - 试运营版产品化下一阶段规划

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-73-tencent-map-productization-go-no-go.md](step-73-tencent-map-productization-go-no-go.md)
- [project-logs/campus-relay/step-74-trial-operation-productization-roadmap.md](step-74-trial-operation-productization-roadmap.md)

本轮是试运营版产品化下一阶段规划轮：评估地图扩展、模拟资金链路、运行配置 / 环境 / 密钥、admin 运营处理动作和真实部署上线等方向，最终选择 Step 75 优先进入“试运营运行配置与 preflight 手册”；本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、token 附着、`request.js`、地图代码或旧外卖模块。

## Step 75 - 试运营运行配置与 Preflight 手册

- [README.md](../../README.md)
- [docs/README.md](../../docs/README.md)
- [docs/delivery-guide.md](../../docs/delivery-guide.md)
- [docs/trial-operation-preflight.md](../../docs/trial-operation-preflight.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-74-trial-operation-productization-roadmap.md](step-74-trial-operation-productization-roadmap.md)
- [project-logs/campus-relay/step-75-trial-operation-preflight-handbook.md](step-75-trial-operation-preflight-handbook.md)

本轮是试运营运行配置与 preflight 手册整理轮：新增 `docs/trial-operation-preflight.md`，明确 backend / frontend 启动方式、H2/test 与 MySQL/dev 边界、腾讯地图 key 本地配置、演示账号、样本订单、模拟资金链路口径、关键页面和关键接口检查；同步修正交付说明中的地图能力口径。本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、token 附着、`request.js`、地图代码或旧外卖模块，也没有提交真实地图 key。

## Step 76 - 模拟资金链路产品化边界说明

- [README.md](../../README.md)
- [docs/README.md](../../docs/README.md)
- [docs/delivery-guide.md](../../docs/delivery-guide.md)
- [docs/trial-operation-preflight.md](../../docs/trial-operation-preflight.md)
- [docs/simulated-funds-boundary.md](../../docs/simulated-funds-boundary.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-75-trial-operation-preflight-handbook.md](step-75-trial-operation-preflight-handbook.md)
- [project-logs/campus-relay/step-76-simulated-funds-productization-boundary.md](step-76-simulated-funds-productization-boundary.md)

本轮是模拟资金链路产品化边界说明轮：新增 `docs/simulated-funds-boundary.md`，明确 mock-pay、售后模拟退款 / 执行、settlement 模拟打款、批次操作审计和对账差异都只是试运营状态推进与运营审计，不代表真实支付、真实退款、真实打款、银行流水或第三方清结算；同步更新 README、docs index、交付说明、preflight 手册和 Step 75 回填口径。本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、token 附着、`request.js`、地图代码或旧外卖模块，也没有提交真实地图 key。

## Step 77 - 试运营交付文档一致性复核 / 最小 preflight 验证

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-76-simulated-funds-productization-boundary.md](step-76-simulated-funds-productization-boundary.md)
- [project-logs/campus-relay/step-77-delivery-doc-consistency-and-minimal-preflight.md](step-77-delivery-doc-consistency-and-minimal-preflight.md)

本轮是试运营交付文档一致性复核 / 最小 preflight 验证轮：完成 README、delivery-guide、trial-operation-preflight、simulated-funds-boundary 和 summary 的口径复核，并确认 docs 范围内没有“地图 SDK 未接入 / 真实支付已接入 / 真实退款已接入 / 真实打款已接入 / bridge 已可删除”等过期表述；同时完成 backend compile、frontend build，以及 customer / courier / admin 关键页面最小 preflight 验证。本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、token 附着、`request.js`、地图代码或旧外卖模块。

## Step 78 - 试运营交付包 RC 收口复盘 / 最小 smoke 复核

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-77-delivery-doc-consistency-and-minimal-preflight.md](step-77-delivery-doc-consistency-and-minimal-preflight.md)
- [project-logs/campus-relay/step-78-trial-operation-rc-review-and-minimal-smoke.md](step-78-trial-operation-rc-review-and-minimal-smoke.md)

本轮是试运营交付包 RC 收口复盘 / 最小 smoke 复核轮：基于 Step 40 到 Step 42 的交付文档、样本索引、截图与录屏，以及 Step 77 的文档一致性复核和最小 preflight 结果，确认当前交付包已经达到“可演示、可移交、可答辩、可复盘”的试运营 RC 状态；同时额外确认媒体目录、截图/录屏数量和本地 frontend/backend 端口可访问。本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面、路由或后端接口，没有改 bridge、鉴权、token 附着、`request.js`、地图代码或旧外卖模块。

## Step 79 - 试运营 RC 运行配置与构建告警减噪

- [backend/src/main/resources/application-test.properties](../../backend/src/main/resources/application-test.properties)
- [frontend/src/styles/global.scss](../../frontend/src/styles/global.scss)
- [frontend/src/styles/element-plus.scss](../../frontend/src/styles/element-plus.scss)
- [docs/trial-operation-preflight.md](../../docs/trial-operation-preflight.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-78-trial-operation-rc-review-and-minimal-smoke.md](step-78-trial-operation-rc-review-and-minimal-smoke.md)
- [project-logs/campus-relay/step-79-rc-runtime-config-and-build-warning-hardening.md](step-79-rc-runtime-config-and-build-warning-hardening.md)

本轮是试运营 RC 运行配置与构建告警减噪轮：将 `application-test.properties` 默认端口调整为 `SERVER_PORT:8080`，使 `test profile + H2` 可直接用于浏览器联调；同时把前端两个样式入口从 Sass `@import` 迁移到 `@use`，消除 build 中的弃用告警，并同步更新 preflight 手册。本轮没有修改 bridge、鉴权、接口、路由、token 附着、地图代码或业务页面语义。

## Step 80 - 前端打包告警与分包 go / no-go 评估

- [frontend/src/utils/echarts.js](../../frontend/src/utils/echarts.js)
- [frontend/src/views/Dashboard.vue](../../frontend/src/views/Dashboard.vue)
- [frontend/src/views/Statistics.vue](../../frontend/src/views/Statistics.vue)
- [frontend/vite.config.js](../../frontend/vite.config.js)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-79-rc-runtime-config-and-build-warning-hardening.md](step-79-rc-runtime-config-and-build-warning-hardening.md)
- [project-logs/campus-relay/step-80-frontend-bundle-warning-go-no-go.md](step-80-frontend-bundle-warning-go-no-go.md)

本轮先完成前端打包告警与分包 go / no-go 评估，再执行一个低风险最小动作：把 `Dashboard.vue` 与 `Statistics.vue` 从整包 `echarts` 引用切到共享按需注册入口，图表共享 chunk 从约 `1.11 MB` 收敛到约 `545 KB`。剩余大包主要来自当前全局 `ElementPlus` vendor 基线，不适合在试运营 RC 阶段做高风险全量按需改造，因此只在 `frontend/vite.config.js` 中把 `build.chunkSizeWarningLimit` 调整到 `1100`，以匹配当前接受的基线并消除无效 build 噪音。本轮没有修改 bridge、鉴权、接口、路由、token 附着或业务页面语义。

## Step 81 - 前端打包线 freeze / no-op 复盘

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-80-frontend-bundle-warning-go-no-go.md](step-80-frontend-bundle-warning-go-no-go.md)
- [project-logs/campus-relay/step-81-frontend-bundle-freeze-and-no-op-review.md](step-81-frontend-bundle-freeze-and-no-op-review.md)

本轮是前端打包线 freeze / no-op 复盘轮：基于 Step 80 已完成的 `echarts` 最小按需优化和 Vite warning 基线校准，确认当前剩余大包主要来自全局 `ElementPlus` vendor 基线，而不是新的误打包问题。由于继续推进全局按需拆分会显著扩大影响面且当前没有真实性能或交付压力，因此前端打包优化线正式冻结为 no-op，仅在出现明确性能信号、交付压力或全局组件装配重构时才重开。本轮没有业务代码改动。

## Step 82 - 试运营 RC 下一阶段主线重排 / go-no-go 评估

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-81-frontend-bundle-freeze-and-no-op-review.md](step-81-frontend-bundle-freeze-and-no-op-review.md)
- [project-logs/campus-relay/step-82-trial-operation-next-mainline-go-no-go.md](step-82-trial-operation-next-mainline-go-no-go.md)

本轮是试运营 RC 下一阶段主线重排 / go-no-go 评估轮：基于 bridge、展示 polish、媒体、地图和前端打包优化五条主线都已冻结或收住的事实，对下一阶段候选方向做了重新排序。最终选择“试运营运维化最小能力”作为下一条真实主线，理由是它最能直接提升当前试运营版的可启动、可重置、可预检、可复跑能力，而不会把项目重新拉回 bridge、页面 polish 或复杂后端扩张。本轮没有业务代码改动。

## Step 83 - 试运营运维化最小能力边界收敛 / preflight 脚本入口

- [scripts/trial-operation/preflight.ps1](../../scripts/trial-operation/preflight.ps1)
- [scripts/trial-operation/README.md](../../scripts/trial-operation/README.md)
- [docs/trial-operation-preflight.md](../../docs/trial-operation-preflight.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-83-trial-operation-ops-entrypoint-boundary-and-preflight-script.md](step-83-trial-operation-ops-entrypoint-boundary-and-preflight-script.md)

本轮是试运营运维化最小能力边界收敛轮：新增 `scripts/trial-operation/preflight.ps1` 作为本地试运营 preflight 脚本入口，并新增脚本说明文档；脚本只做关键文件、本地地图 key 变量、`.env.local` git 跟踪、可选端口、可选 backend compile 和 frontend build 检查，不自动重置 H2、不启动长驻进程、不打印真实地图 key。本轮没有改 bridge、接口、路由、鉴权、token 附着、前端页面或后端业务语义。

## Step 84 - 试运营样本状态校验脚本

- [scripts/trial-operation/validate-samples.ps1](../../scripts/trial-operation/validate-samples.ps1)
- [scripts/trial-operation/preflight.ps1](../../scripts/trial-operation/preflight.ps1)
- [scripts/trial-operation/README.md](../../scripts/trial-operation/README.md)
- [docs/trial-operation-preflight.md](../../docs/trial-operation-preflight.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-84-trial-operation-sample-validation-script.md](step-84-trial-operation-sample-validation-script.md)

本轮是试运营样本状态校验脚本轮：新增 `validate-samples.ps1`，只读校验 H2 seed / schema 中的账号、订单、位置、settlement 与历史表 schema 锚点；`preflight.ps1` 新增 `-RunSampleValidation` 参数并能把样本校验 warning 作为 warning 处理。本轮没有连接数据库、没有写入样本、没有自动重置 H2、没有改 bridge、接口、路由、鉴权、token 附着、前端页面或后端业务语义。

## Step 85 - 试运营命令索引与手动 H2 reset 指南

- [scripts/trial-operation/commands.ps1](../../scripts/trial-operation/commands.ps1)
- [scripts/trial-operation/README.md](../../scripts/trial-operation/README.md)
- [docs/trial-operation-preflight.md](../../docs/trial-operation-preflight.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-84-trial-operation-sample-validation-script.md](step-84-trial-operation-sample-validation-script.md)
- [project-logs/campus-relay/step-85-trial-operation-command-index-and-manual-h2-reset-guide.md](step-85-trial-operation-command-index-and-manual-h2-reset-guide.md)

本轮是试运营命令索引与手动 H2 reset 指南轮：新增 `commands.ps1`，只打印本地试运营常用命令、浏览器入口和手动 H2 reset 说明，不启动长驻进程、不 kill 进程、不自动重置 H2。本轮没有改 bridge、接口、路由、鉴权、token 附着、前端页面或后端业务语义。

## Step 86 - 试运营脚本线收口 / no-op 冻结判断

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-85-trial-operation-command-index-and-manual-h2-reset-guide.md](step-85-trial-operation-command-index-and-manual-h2-reset-guide.md)
- [project-logs/campus-relay/step-86-trial-operation-ops-line-freeze-and-next-mainline.md](step-86-trial-operation-ops-line-freeze-and-next-mainline.md)

本轮是试运营脚本线收口 / no-op 冻结判断轮：基于 Step 83 到 Step 85 已完成的 preflight、样本校验和命令索引，确认当前脚本线已经足够支撑本地试运营前检查、样本锚点校验、命令索引和手动 H2 reset 说明；继续扩到 runtime smoke、自动 reset 或长驻进程管理的收益低于环境耦合风险，因此脚本线进入维护态。本轮没有改业务代码、bridge、接口、路由、鉴权、token 附着、前端页面或后端业务语义。

## Step 87 - 产品级试运营前剩余差距清单 / go-no-go 评估

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-87-product-trial-readiness-gap-and-next-mainline.md](step-87-product-trial-readiness-gap-and-next-mainline.md)

本轮是产品级试运营前剩余差距清单 / go-no-go 评估轮：确认当前本地答辩 / 交付 RC 已足够，但产品级试运营仍缺环境变量样例、密钥注入边界、部署前 preflight、CI、监控、备份和外部服务降级策略；下一主线建议进入试运营环境与密钥配置硬化。本轮没有改业务代码、bridge、接口、路由、鉴权、token 附着、前端页面或后端业务语义。

## Step 88 - 试运营环境与密钥配置硬化 / deployment preflight 准备

- [.gitignore](../../.gitignore)
- [.env.example](../../.env.example)
- [backend/.env.example](../../backend/.env.example)
- [frontend/.env.example](../../frontend/.env.example)
- [docs/deployment/env-and-secret-checklist.md](../../docs/deployment/env-and-secret-checklist.md)
- [docs/deployment/production-deploy.md](../../docs/deployment/production-deploy.md)
- [docs/trial-operation-preflight.md](../../docs/trial-operation-preflight.md)
- [docs/README.md](../../docs/README.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-88-env-secret-hardening-and-deployment-preflight.md](step-88-env-secret-hardening-and-deployment-preflight.md)

本轮是试运营环境与密钥配置硬化 / deployment preflight 准备轮：新增安全占位 env 样例，明确后端 / 前端环境变量、腾讯地图 key、数据库密码、JWT secret、CORS 和上传目录的配置边界，并更新部署说明和 preflight 文档。本轮没有提交真实 key、没有改运行时默认值、没有改业务代码、bridge、接口、路由、鉴权、token 附着或前端页面。

## Step 89 - 部署后 smoke checklist / deployment preflight 文档

- [docs/deployment/post-deploy-smoke-checklist.md](../../docs/deployment/post-deploy-smoke-checklist.md)
- [docs/deployment/production-deploy.md](../../docs/deployment/production-deploy.md)
- [docs/deployment/env-and-secret-checklist.md](../../docs/deployment/env-and-secret-checklist.md)
- [docs/README.md](../../docs/README.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-88-env-secret-hardening-and-deployment-preflight.md](step-88-env-secret-hardening-and-deployment-preflight.md)
- [project-logs/campus-relay/step-89-post-deploy-smoke-checklist.md](step-89-post-deploy-smoke-checklist.md)

本轮是部署后 smoke checklist / deployment preflight 文档轮：新增 `docs/deployment/post-deploy-smoke-checklist.md`，明确 prod-like 试运营部署后的最小验证范围、角色 smoke、模拟资金口径、bridge 冻结口径和回滚触发。本轮没有新增脚本、没有改业务代码、bridge、接口、路由、鉴权、token 附着或前端页面。

## Step 90 - 最小 CI 检查边界设计 / go-no-go

- [docs/deployment/ci-check-boundary.md](../../docs/deployment/ci-check-boundary.md)
- [docs/deployment/post-deploy-smoke-checklist.md](../../docs/deployment/post-deploy-smoke-checklist.md)
- [docs/README.md](../../docs/README.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-90-minimal-ci-check-boundary-go-no-go.md](step-90-minimal-ci-check-boundary-go-no-go.md)

本轮是最小 CI 检查边界设计 / go-no-go 轮：确认当前仓库已有 GitHub issue/PR 模板但没有 workflow，新增 `docs/deployment/ci-check-boundary.md` 定义最小 CI 只覆盖 backend compile、frontend build 和 sample validation；建议 Step 91 再新增 workflow。本轮没有写 CI、没有改业务代码、bridge、接口、路由、鉴权、token 附着或前端页面。

## Step 91 - 最小 GitHub Actions CI 实现

- [.github/workflows/trial-operation-ci.yml](../../.github/workflows/trial-operation-ci.yml)
- [docs/deployment/ci-check-boundary.md](../../docs/deployment/ci-check-boundary.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-91-minimal-github-actions-ci.md](step-91-minimal-github-actions-ci.md)

本轮是最小 GitHub Actions CI 实现轮：新增 `trial-operation-ci.yml`，只覆盖 backend compile、frontend build 和 trial sample validation；sample validation 的 warning exit code `2` 在 CI 中按 warning 处理，不使 CI 失败。本轮没有做部署、没有注入真实密钥、没有跑 E2E、没有改业务代码、bridge、接口、路由、鉴权、token 附着或前端页面。

## Step 92 - CI 首轮运行结果跟踪 / 本地与远端一致性复核

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-91-minimal-github-actions-ci.md](step-91-minimal-github-actions-ci.md)
- [project-logs/campus-relay/step-92-ci-first-run-followup.md](step-92-ci-first-run-followup.md)

本轮是 CI 首轮运行结果跟踪 / 本地与远端一致性复核轮：确认 GitHub Actions `Trial Operation CI #1` 已由 `450e823ec35f22dba9463c71e1b85b854b1aa6e5` 的 push 触发，`Backend compile`、`Frontend build`、`Trial sample validation` 三个 job 均成功；sample validation warning exit code `2` 在远端按 warning 处理。本轮没有改 workflow、业务代码、bridge、接口、路由、鉴权、token 附着或前端页面。

## Step 93 - GitHub Actions runtime warning 处理 go / no-go

- [.github/workflows/trial-operation-ci.yml](../../.github/workflows/trial-operation-ci.yml)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-92-ci-first-run-followup.md](step-92-ci-first-run-followup.md)
- [project-logs/campus-relay/step-93-github-actions-runtime-warning-go-no-go.md](step-93-github-actions-runtime-warning-go-no-go.md)

本轮是 GitHub Actions runtime warning 处理 go / no-go 轮：基于官方 README 核查，将 `actions/checkout` 升级到 `v6`、`actions/setup-java` 升级到 `v5`、`actions/setup-node` 升级到 `v6`；CI job、触发条件、命令、Java 17 / Node 20 构建版本、sample validation warning-only 策略均保持不变。本轮没有改业务代码、bridge、接口、路由、鉴权、token 附着或前端页面。

## Step 94 - CI action 版本升级后远端运行结果跟踪

- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-93-github-actions-runtime-warning-go-no-go.md](step-93-github-actions-runtime-warning-go-no-go.md)
- [project-logs/campus-relay/step-94-ci-runtime-upgrade-followup.md](step-94-ci-runtime-upgrade-followup.md)

本轮是 CI action 版本升级后远端运行结果跟踪轮：已确认 `2406c1b2586996a1e0fdea1946394022894b3b0e` 对应的 `Trial Operation CI #2` 远端成功，三个 job 继续全绿，且最新 run 摘要页未再观察到显性 runtime deprecation warning 文本。本轮没有改业务代码、bridge、接口、路由、鉴权、token 附着或前端页面。

## Step 95 - 内测型试运营 Compose 部署包最小实现

- [.dockerignore](../../.dockerignore)
- [deploy/internal-trial/.env.example](../../deploy/internal-trial/.env.example)
- [deploy/internal-trial/backend.Dockerfile](../../deploy/internal-trial/backend.Dockerfile)
- [deploy/internal-trial/frontend.Dockerfile](../../deploy/internal-trial/frontend.Dockerfile)
- [deploy/internal-trial/nginx.conf](../../deploy/internal-trial/nginx.conf)
- [deploy/internal-trial/docker-compose.yml](../../deploy/internal-trial/docker-compose.yml)
- [docs/deployment/internal-trial-compose.md](../../docs/deployment/internal-trial-compose.md)
- [README.md](../../README.md)
- [docs/README.md](../../docs/README.md)
- [docs/delivery-guide.md](../../docs/delivery-guide.md)
- [docs/deployment/production-deploy.md](../../docs/deployment/production-deploy.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-94-ci-runtime-upgrade-followup.md](step-94-ci-runtime-upgrade-followup.md)
- [project-logs/campus-relay/step-95-internal-trial-compose-package.md](step-95-internal-trial-compose-package.md)

本轮是内测型试运营 Compose 部署包最小实现轮：新增单机服务器内测部署所需的 MySQL + backend(prod) + frontend(Nginx) compose 工件、环境变量样例和部署说明，并把当前 README / 交付文档统一到这套部署入口。验证上已通过 backend package、frontend build、sample validation 和 `git diff --check`；当前机器未安装 Docker，因此容器级启动验证后置到服务器首轮部署。本轮没有改业务代码、bridge、接口、路由、鉴权或前端页面。

## Step 96 - 单机服务器首轮内测部署与 smoke 验证

- [deploy/internal-trial/docker-compose.yml](../../deploy/internal-trial/docker-compose.yml)
- [backend/db/init.sql](../../backend/db/init.sql)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-96-single-server-internal-trial-deploy-and-smoke.md](step-96-single-server-internal-trial-deploy-and-smoke.md)

本轮是单机服务器首轮内测部署与 smoke 验证轮：在公网单机服务器上真实拉起 MySQL + backend(prod) + frontend(Nginx) Compose 栈，并修正两个部署层阻塞：一是移除 MySQL 8.4 不兼容的 `mysql_native_password` 启动参数，二是修正 `backend/db/init.sql` 中 `campus_relay_order` 种子列清单与值数漂移。当前远端三容器均正常运行，首页 `HTTP 200`，且 admin / customer / courier 三类最小 smoke 已通过公网验证。本轮没有改业务语义、bridge、鉴权、接口、路由或前端页面。

## Step 97 - 全局工作记忆日志建立

- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-97-global-working-memory-bootstrap.md](step-97-global-working-memory-bootstrap.md)

本轮是全局工作记忆日志建立轮：新增 `global-working-memory.md`，把 bridge 冻结态、展示 polish 冻结态、媒体线收住、单机服务器部署现状和当前下一主线从 `summary.md` 的全量历史里抽离出来，作为后续上下文压缩后的快速恢复入口。本轮没有改业务代码、部署逻辑、bridge、鉴权、接口、路由或前端页面。

## Step 98 - 单机服务器内测运维加固 / 最小回滚与备份准备

- [.gitignore](../../.gitignore)
- [.dockerignore](../../.dockerignore)
- [deploy/internal-trial/backup-stack.sh](../../deploy/internal-trial/backup-stack.sh)
- [docs/deployment/backup-and-rollback.md](../../docs/deployment/backup-and-rollback.md)
- [docs/deployment/internal-trial-compose.md](../../docs/deployment/internal-trial-compose.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-98-internal-trial-ops-hardening-and-backup-prep.md](step-98-internal-trial-ops-hardening-and-backup-prep.md)

本轮是单机服务器内测运维加固 / 最小回滚与备份准备轮：新增 `backup-stack.sh`，补齐单机服务器 Compose 部署的最小备份入口，并同步更新备份/回滚文档与 compose 说明；随后已在真实服务器上成功执行一轮备份，真实产出 MySQL dump、uploads 归档、`.env` 备份和 manifest。本轮没有改业务代码、bridge、接口、路由、鉴权或前端页面。

## Step 99 - 单机服务器最小恢复演练 / 运维交付加固

- [deploy/internal-trial/restore-drill.sh](../../deploy/internal-trial/restore-drill.sh)
- [docs/deployment/backup-and-rollback.md](../../docs/deployment/backup-and-rollback.md)
- [docs/deployment/internal-trial-compose.md](../../docs/deployment/internal-trial-compose.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-99-minimal-restore-drill-and-ops-handover.md](step-99-minimal-restore-drill-and-ops-handover.md)

本轮是单机服务器最小恢复演练 / 运维交付加固轮：新增 `restore-drill.sh`，并在真实服务器上完成一轮非破坏性 restore drill，确认最新 dump 可恢复到临时 MySQL 容器、关键样本订单可回读、uploads 归档可解压，且现有 compose 栈不受影响。本轮没有改业务代码、bridge、接口、路由、鉴权或前端页面。

## Step 100 - 单机服务器运维交接与正式入口 go/no-go

- [README.md](../../README.md)
- [docs/README.md](../../docs/README.md)
- [docs/deployment/internal-trial-ops-runbook.md](../../docs/deployment/internal-trial-ops-runbook.md)
- [docs/deployment/internal-trial-compose.md](../../docs/deployment/internal-trial-compose.md)
- [docs/deployment/backup-and-rollback.md](../../docs/deployment/backup-and-rollback.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-100-internal-trial-ops-runbook-and-entry-go-no-go.md](step-100-internal-trial-ops-runbook-and-entry-go-no-go.md)

本轮是单机服务器运维交接与正式入口 go/no-go 轮：新增 `internal-trial-ops-runbook.md`，把启动、停机、日志、更新、备份、restore drill、smoke、回滚触发和 HTTPS / 域名 go/no-go 统一到一份可交接文档；本轮复核服务器 compose 仍为 `Up`、HTTP 返回 `200`、最新 backup manifest 存在。结论是当前内测阶段暂不强制进入 HTTPS / 域名 / 正式反向代理准备。本轮没有改业务代码、bridge、接口、路由、鉴权或前端页面。

## Step 101 - admin 文本乱码修复与前端可读性加固

- [backend/src/main/resources/application-test.properties](../../backend/src/main/resources/application-test.properties)
- [frontend/src/utils/text.js](../../frontend/src/utils/text.js)
- [frontend/src/utils/text.spec.js](../../frontend/src/utils/text.spec.js)
- [frontend/src/utils/request.js](../../frontend/src/utils/request.js)
- [frontend/src/stores/user.js](../../frontend/src/stores/user.js)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-101-admin-text-mojibake-fix-and-frontend-readability.md](step-101-admin-text-mojibake-fix-and-frontend-readability.md)

本轮是 admin 文本乱码修复与前端可读性加固轮：先在 `test profile + H2` SQL 初始化中显式声明 UTF-8，从源头修复本地 seed 数据乱码；再新增前端文本规范化工具，兜底修复接口数据或 `localStorage` 中 UTF-8 中文被错误解码为 Latin-1 / Windows-1252 后的 mojibake；成功响应和 admin 用户 store 已接入该规范化逻辑，并新增单测覆盖典型乱码样例。本轮没有改 bridge、接口路径、token 附着、路由或业务语义。

## Step 102 - 校园兼职视觉体系与 admin 外壳刷新

- [frontend/src/styles/variables.scss](../../frontend/src/styles/variables.scss)
- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/Dashboard.vue](../../frontend/src/views/Dashboard.vue)
- [frontend/src/views/Employee.vue](../../frontend/src/views/Employee.vue)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-102-campus-visual-system-admin-shell-refresh.md](step-102-campus-visual-system-admin-shell-refresh.md)

本轮是校园兼职视觉体系与 admin 外壳刷新轮：将全局主题色、admin 主框架、仪表盘和运营人员页从旧外卖后台视觉调整为校园兼职运营风格；旧外卖模块仍保留，但在菜单中明确作为兼容模块呈现。本轮只改展示层，没有改 bridge、接口、鉴权、路由、API 调用顺序、后端业务或数据库。

## Step 103 - admin 玻璃拟态视觉重基线与残留乱码兜底

- [frontend/src/views/Login.vue](../../frontend/src/views/Login.vue)
- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/Dashboard.vue](../../frontend/src/views/Dashboard.vue)
- [frontend/src/views/Employee.vue](../../frontend/src/views/Employee.vue)
- [frontend/src/stores/user.js](../../frontend/src/stores/user.js)
- [frontend/src/styles/variables.scss](../../frontend/src/styles/variables.scss)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-102-campus-visual-system-admin-shell-refresh.md](step-102-campus-visual-system-admin-shell-refresh.md)
- [project-logs/campus-relay/step-103-admin-glass-visual-rebaseline-and-text-hardening.md](step-103-admin-glass-visual-rebaseline-and-text-hardening.md)

本轮是 admin 玻璃拟态视觉重基线与残留乱码兜底轮：登录页、admin 主框架和 dashboard 继续按用户选择的深色玻璃拟态参考方向调整；同时对 admin localStorage / in-memory 用户信息和 employee 列表高曝光字段增加文本归一化兜底。后端接口原始响应已复核为正常中文。本轮没有改 bridge、接口、鉴权、路由、后端业务、数据库或新增页面。

## Step 104 - 浅色校园视觉回调与 courier ops 窄屏裁切修复

- [frontend/src/layout/MainLayout.vue](../../frontend/src/layout/MainLayout.vue)
- [frontend/src/views/Dashboard.vue](../../frontend/src/views/Dashboard.vue)
- [frontend/src/views/Login.vue](../../frontend/src/views/Login.vue)
- [frontend/src/views/Employee.vue](../../frontend/src/views/Employee.vue)
- [frontend/src/views/CampusCourierOpsView.vue](../../frontend/src/views/CampusCourierOpsView.vue)
- [frontend/src/styles/element-plus.scss](../../frontend/src/styles/element-plus.scss)
- [project-logs/campus-relay/summary.md](summary.md)
- [project-logs/campus-relay/pending-items.md](pending-items.md)
- [project-logs/campus-relay/global-working-memory.md](global-working-memory.md)
- [project-logs/campus-relay/file-change-list.md](file-change-list.md)
- [project-logs/campus-relay/step-104-light-admin-visual-and-courier-ops-mobile-fix.md](step-104-light-admin-visual-and-courier-ops-mobile-fix.md)

本轮是浅色校园视觉回调与 courier ops 窄屏裁切修复轮：将 Step 103 的深色玻璃方向回调为浅色校园玻璃风格，并修正 Element Plus `light-*` 变量映射；同时为 `/campus/courier-ops` 左侧配送员列表增加横向滚动容器和最小宽度，解决窄屏下“审核状态”列被裁切的问题。本轮没有改 bridge、接口、鉴权、路由、token 附着、后端业务或新增页面。

# Step 07 - customer onboarding 替代链路 / frontend 最小接入

## 本次目标

1. 为 courier onboarding 建立 customer 侧稳定替代入口，但先并行保留旧 bridge
2. 为 frontend 增加最小、可演示、可回退的 customer 页面落点
3. 保持旧系统可运行，不切旧前端主链路，不破坏现有 courier token 与接单链路

## 已完成项

1. 新增 customer onboarding 替代入口：
   - `POST /api/campus/customer/courier-onboarding/profile`
   - `GET /api/campus/customer/courier-onboarding/profile`
   - `GET /api/campus/customer/courier-onboarding/review-status`
   - `GET /api/campus/customer/courier-onboarding/token-eligibility`
2. onboarding 新入口只允许 `customer` token，不替代 `/api/campus/courier/auth/token`
3. 新入口与旧 `/api/campus/courier/profile` 提交流程复用同一套 service 写路径，没有复制两套资料提交流程
4. customer onboarding 提交或重提后，仍统一回到：
   - `reviewStatus = PENDING`
   - `enabled = 0`
   - 清空审核人和审核时间
5. onboarding 读取接口支持“未提交资料”默认态，便于前端首屏展示与联调
6. `canApplyCourierToken` 与 `token-eligibility.message` 全部继续作为 VO 计算字段，不新增数据库字段
7. `campus_courier_profile` 最小补齐了 onboarding 所需字段：
   - `gender`
   - `campus_zone`
   - `enabled_work_in_own_building`
   - `applicant_remark`
8. 在不新增 onboarding 表的前提下，放宽了旧 bridge 资料模型的部分强制字段要求，让 onboarding 可以稳定复用 `campus_courier_profile`
9. 新增前端最小 customer API 封装：
   - `frontend/src/api/campus-customer.js`
10. 新增前端 customer 最小页面：
   - `frontend/src/views/user/AfterSaleResult.vue`
   - `frontend/src/views/user/CourierOnboarding.vue`
11. 新增前端路由：
   - `/user/campus/after-sale-result`
   - `/user/campus/courier-onboarding`
12. 在 `frontend/src/views/user/Profile.vue` 追加“校园代送”入口区块，作为最小演示入口
13. `frontend/src/utils/request.js` 已补 customer 对 `/campus/customer/**` 的 token 附着逻辑
14. 新增 `CampusCourierOnboardingIntegrationTest`
15. 旧 bridge 入口兼容性仍保持可用，没有切断：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`

## 主要修改文件

- `backend/db/init.sql`
- `backend/db/migrations/V8__campus_courier_onboarding_bridge_replacement.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusCourierProfile.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusCourierProfileService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusCustomerCourierOnboardingController.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCourierOnboardingIntegrationTest.java`
- `frontend/src/utils/request.js`
- `frontend/src/router/index.js`
- `frontend/src/api/campus-customer.js`
- `frontend/src/views/user/Profile.vue`
- `frontend/src/views/user/AfterSaleResult.vue`
- `frontend/src/views/user/CourierOnboarding.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 新增的主要类型

- `CampusCustomerCourierOnboardingSubmitDTO`
- `CampusCustomerCourierOnboardingProfileVO`
- `CampusCustomerCourierOnboardingReviewStatusVO`
- `CampusCourierTokenEligibilityVO`

## 测试与验证

1. 执行 `.\mvnw.cmd -DskipTests compile`，通过
2. 执行 `.\mvnw.cmd "-Dtest=CampusCourierOnboardingIntegrationTest,CampusCourierProfileIntegrationTest" test`，通过
3. 执行 `.\mvnw.cmd test`，通过
4. 当前累计 `57` 个后端测试通过
5. 执行 `npm run build`，通过
6. 前端最小联调说明：
   - 新页面已挂到现有 customer 路由体系
   - 新页面通过 `campus-customer.js` 调用后端接口
   - `request.js` 已能为 `/campus/customer/**` 自动附着 customer token
   - 本轮以前端构建通过和接口联调代码就绪作为最小演示验证，不替换旧主链路

## Bridge 结论

- `courier/profile` 与 `courier/review-status` 的双 token bridge 本轮继续保留
- 新增 customer onboarding 入口解决了：
  - 未拿到 courier token 前的资料提交入口不再只能依赖旧 bridge
  - 前端现在有一条明确、稳定、面向 onboarding 的 customer 侧调用链
- 旧 bridge 暂不删除的原因：
  - 新入口刚建立，尚未经过更长时间的前端联调与运行验证
  - 旧 bridge 仍承载更完整的资料提交流程与现有兼容入口
  - 贸然切断会增加现有 onboarding 过渡链路风险
- 收口前提：
  - customer onboarding 新入口稳定运行
  - 前端 onboarding 页面完成一轮实际演示与联调验证
  - 团队确认旧 bridge 已无必须保留的历史调用方

## 遗留问题

1. customer onboarding 目前还没有直接打通“去申请 courier token”的按钮流转，只提供资格判断
2. frontend 目前只接入了 customer 售后结果页和 onboarding 页，admin 运营页仍未接入
3. 旧 bridge 仍然保留，需要后续再评估收口时机
4. `CampusRuleCatalog` 仍是代码常量
5. 真实支付、真实退款、真实打款仍然不做

## 下一步建议

1. 进入 Step 08，优先为 admin 增加一个真正可演示的最小只读运营页
2. 继续观察 customer onboarding 新入口与旧 bridge 的并行表现，再决定是否逐步收口 bridge
3. 视业务需要补售后执行历史、异常历史和更细粒度运营审计

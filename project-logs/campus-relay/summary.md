# 校园代送改造总览

## 当前轮次

- 当前已完成：`Step 01 - 领域模型重构规划`
- 当前已完成：`Step 02A - 数据库与后端骨架`
- 当前已完成：`Step 02B - 只读接口完善`
- 当前已完成：`Step 03A - customer 下单与模拟支付`
- 当前已完成：`Step 03B - courier 资料链路与 admin 最小审核/详情`
- 当前已完成：`Step 03C - courier token 与最小接单链路`
- 当前已完成：`Step 03D - courier 取餐 / 配送推进 / customer 确认送达`
- 当前已完成：`Step 03E - customer 取消 / 售后 / admin 时间线 / 结算联动`
- 当前已完成：`Step 03F - admin 售后处理 / courier 异常上报 / courier 位置上报 / admin 结算分页`
- 当前已完成：`Step 04 - 售后决策 / 结算确认 / admin 运营只读接口`
- 当前已完成：`Step 05 - 售后执行记录 / 结算打款运营 / 订单级运营查询`
- 当前已完成：`Step 06 - customer 售后结果回执 / admin 执行纠正审计 / settlement 批次核对`
- 当前已完成：`Step 07 - customer onboarding 替代链路 / frontend 最小接入`
- 当前已完成：`Step 08 - admin settlement 批次演示页 / frontend 最小只读运营接入`
- 当前已完成：`Step 09 - admin 售后执行演示页 / frontend 第二个只读运营入口`
- 当前已完成：`Step 10 - admin courier 异常/位置联动演示页 / frontend 第三个只读运营入口`
- 当前日期：`2026-04-08`
- 当前范围：后端最小闭环已扩展到 customer onboarding 替代链路、admin settlement 批次演示页、admin 售后执行演示页和 admin courier 异常/位置联动演示页，旧外卖模块仍保留可运行，旧前端主链路未被替换

## 当前状态

### 数据与领域

- 已并行新增表：
  - `campus_customer_profile`
  - `campus_courier_profile`
  - `campus_pickup_point`
  - `campus_relay_order`
  - `campus_location_report`
  - `campus_settlement_record`
- 普通用户继续复用 `user`
- 管理员继续复用 `employee`
- 配送员继续通过 `campus_courier_profile.user_id` 关联 `user`
- 旧外卖表与旧控制器未删除，旧 `orders/cart/address` 语义未改

### 当前已开放的 campus 接口

- public：
  - `/api/campus/public/pickup-points`
  - `/api/campus/public/delivery-rules`
- customer：
  - `/api/campus/customer/orders`
  - `/api/campus/customer/orders/{id}`
  - `/api/campus/customer/orders/{id}/mock-pay`
  - `/api/campus/customer/orders/{id}/confirm`
  - `/api/campus/customer/orders/{id}/cancel`
  - `/api/campus/customer/orders/{id}/after-sale`
  - `/api/campus/customer/orders/{id}/after-sale-result`
  - `/api/campus/customer/courier-onboarding/profile`
  - `/api/campus/customer/courier-onboarding/review-status`
  - `/api/campus/customer/courier-onboarding/token-eligibility`
- courier：
  - `/api/campus/courier/auth/token`
  - `/api/campus/courier/profile`
  - `/api/campus/courier/review-status`
  - `/api/campus/courier/orders/available`
  - `/api/campus/courier/orders/{id}`
  - `/api/campus/courier/orders/{id}/accept`
  - `/api/campus/courier/orders/{id}/pickup`
  - `/api/campus/courier/orders/{id}/deliver`
  - `/api/campus/courier/orders/{id}/exception-report`
  - `/api/campus/courier/location-reports`
- admin：
  - `/api/campus/admin/orders`
  - `/api/campus/admin/orders/{id}`
  - `/api/campus/admin/orders/{id}/timeline`
  - `/api/campus/admin/orders/after-sale`
  - `/api/campus/admin/orders/{id}/after-sale-handle`
  - `/api/campus/admin/orders/{id}/after-sale-decision`
  - `/api/campus/admin/orders/{id}/after-sale-result`
  - `/api/campus/admin/orders/{id}/after-sale-execution`
  - `/api/campus/admin/orders/after-sale-executions`
  - `/api/campus/admin/orders/{id}/location-reports`
  - `/api/campus/admin/orders/{id}/exception-summary`
  - `/api/campus/admin/couriers`
  - `/api/campus/admin/couriers/{id}/review`
  - `/api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
  - `/api/campus/admin/couriers/{courierProfileId}/location-reports`
  - `/api/campus/admin/settlements`
  - `/api/campus/admin/settlements/{id}`
  - `/api/campus/admin/settlements/{id}/confirm`
  - `/api/campus/admin/settlements/{id}/payout-result`
  - `/api/campus/admin/settlements/batch-payout-result`
  - `/api/campus/admin/settlements/reconcile-summary`
  - `/api/campus/admin/settlements/payout-batches`
  - `/api/campus/admin/settlements/payout-batches/{batchNo}`
  - `/api/campus/admin/settlements/{id}/verify-payout`

### 状态与运营能力

- 订单主状态已覆盖：
  - `PENDING_PAYMENT`
  - `BUILDING_PRIORITY_PENDING`
  - `WAITING_ACCEPT`
  - `ACCEPTED`
  - `PICKED_UP`
  - `DELIVERING`
  - `AWAITING_CONFIRMATION`
  - `COMPLETED`
  - `CANCELLED`
  - `AFTER_SALE_OPEN`
  - `AFTER_SALE_RESOLVED`
  - `AFTER_SALE_REJECTED`
- `after_sale_execution_status` 独立于 `order_status`
  - `PENDING`
  - `SUCCESS`
  - `FAILED`
  - `NOT_REQUIRED`
- `payout_status` 独立于 `settlement_status`
  - `UNPAID`
  - `PAID`
  - `FAILED`
- `customerReceiptStatus` 继续只作为 customer 结果 VO 计算字段，不落数据库主状态
- `decisionType = REFUND / COMPENSATE` 记录后会自动初始化 `after_sale_execution_status = PENDING`
- `decisionType = NONE` 记录后会自动初始化 `after_sale_execution_status = NOT_REQUIRED`
- settlement confirm 后会自动初始化 `payout_status = UNPAID`
- `FAILED -> SUCCESS` 的售后执行人工纠正会写入 `after_sale_execution_corrected*` 审计字段，初次 `SUCCESS` 不算纠正
- batch payout 未传 `batchNo` 时会在 service 层自动生成统一批次号，只写入本次成功处理的 settlement 记录
- `payout_verified*` 字段独立表达二次核对结果，不复用 `settlement_status`
- courier 异常上报仍只保留订单上的最新一次异常，不改 `order_status`
- courier 位置上报仍只写 `campus_location_report`，不进入 timeline

### frontend 最小接入

- 已新增 customer 侧最小演示入口：
  - `/user/campus/after-sale-result`
  - `/user/campus/courier-onboarding`
- 已新增 admin 侧最小演示入口：
  - `/campus/settlement-batches`
  - `/campus/settlement-batches/:batchNo`
  - `/campus/after-sale-executions`
  - `/campus/courier-ops`
- 已新增 customer 侧 API 封装：
  - `frontend/src/api/campus-customer.js`
- 已新增 admin 侧 API 封装：
  - `frontend/src/api/campus-admin.js`
- 已在 `frontend/src/utils/request.js` 放通 `/campus/customer/**` 的 customer token 附着
- 已在 admin 现有平铺路由体系中新增“校园结算批次”只读入口，没有另起第二套路由体系
- 已在 `frontend/src/views/user/Profile.vue` 追加 customer 轻量入口，不替换旧页面、不改旧登录主入口
- 当前前端仍然只是最小联调与演示接入，不是完整校园代送前台，也不是完整校园代送后台

## Step 07 实际完成事项

1. 新增 customer onboarding 替代入口：
   - `POST /api/campus/customer/courier-onboarding/profile`
   - `GET /api/campus/customer/courier-onboarding/profile`
   - `GET /api/campus/customer/courier-onboarding/review-status`
   - `GET /api/campus/customer/courier-onboarding/token-eligibility`
2. onboarding 新入口只允许 `customer` token，不替代现有 `/api/campus/courier/auth/token`
3. onboarding 资料提交与旧 bridge 复用同一套 service 提交流程，没有复制两套资料提交流程
4. onboarding 首次提交或重提后，仍统一走现有资料状态机：
   - `reviewStatus = PENDING`
   - `enabled = 0`
   - 清空审核人和审核时间
5. customer onboarding 资料读取支持“未提交资料”默认态，便于前端页面首屏回填与联调
6. `canApplyCourierToken` 继续只作为 VO 计算字段：
   - `APPROVED` 且 `enabled = 1` 时为 `true`
   - 其他情况为 `false`
7. `token-eligibility.message` 继续只作为 VO 计算字段，不新增数据库字段
8. 为 `campus_courier_profile` 最小补齐 onboarding 所需字段：
   - `gender`
   - `campus_zone`
   - `enabled_work_in_own_building`
   - `applicant_remark`
9. 同步放宽旧桥接链路依赖的部分资料字段必填约束，保证 onboarding 新入口可在不引入新表的前提下稳定落到 `campus_courier_profile`
10. 新增 `CampusCourierOnboardingIntegrationTest`
11. 旧 bridge 兼容性测试继续保留并通过，确保 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 未被破坏
12. frontend 最小接入已落地：
   - 新增 customer 售后结果页 `frontend/src/views/user/AfterSaleResult.vue`
   - 新增 courier onboarding 页 `frontend/src/views/user/CourierOnboarding.vue`
   - 新增 customer campus API 封装 `frontend/src/api/campus-customer.js`
   - 新增用户侧路由 `/user/campus/after-sale-result` 与 `/user/campus/courier-onboarding`
   - 在 `frontend/src/views/user/Profile.vue` 追加“校园代送”入口区块
13. 前端接入保持“新增页面 + 新增轻量脚本”方式，没有重写旧页面，没有切旧主链路
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `.\mvnw.cmd "-Dtest=CampusCourierOnboardingIntegrationTest,CampusCourierProfileIntegrationTest" test`
   - `.\mvnw.cmd test`
   - `npm run build`
15. 当前累计 `57` 个后端测试通过，前端生产构建通过

## Step 08 实际完成事项

1. 本轮优先选择了 admin settlement 批次列表页与批次详情页，而不是继续扩 customer 页面
2. 选择 settlement 批次页的原因：
   - 批次字段更聚合，演示信号更强
   - 与 Step 06 的批次审计能力直接衔接
   - 风险低于继续扩 customer 页面或直接上售后执行分页页
   - 更适合作为首个 admin 只读运营演示页
3. 因现有 admin 页面采用顶层平铺视图与顶层路由风格，本轮继续沿用该风格：
   - `frontend/src/views/CampusSettlementBatchList.vue`
   - `frontend/src/views/CampusSettlementBatchDetail.vue`
   - 路由使用 `/campus/settlement-batches` 与 `/campus/settlement-batches/:batchNo`
4. 新增 admin 侧 API 封装：
   - `getCampusSettlementPayoutBatches`
   - `getCampusSettlementPayoutBatchDetail`
5. settlement 批次列表页能力：
   - 调用 `GET /api/campus/admin/settlements/payout-batches`
   - 支持 `payoutStatus` 与 `payoutVerified` 两个最小筛选
   - 展示 `payoutBatchNo`、`totalCount`、`paidCount`、`failedCount`、`verifiedCount`、`unverifiedCount`、`totalPendingAmount`、`firstRecordedAt`、`lastRecordedAt`
   - 支持点击批次进入详情页
6. settlement 批次详情页能力：
   - 调用 `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
   - 展示批次汇总卡片
   - 展示 records 明细表
   - 明细默认只读，不新增写操作
7. 为 admin 现有侧边栏新增“校园结算批次”入口，并补齐 breadcrumb 与高亮逻辑
8. 本轮没有改任何 campus 后端接口，也没有新增写接口，页面直接消费现有后端返回
9. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 现在承担未拿到 courier token 前的稳定前台 onboarding 入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容与旧桥接入口
10. Step 08 的 bridge 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
11. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
12. 本轮不扩 customer 业务页面，不扩 admin 写操作，不动旧前端主链路

## Step 09 实际完成事项

1. 本轮优先选择了 admin 售后执行分页演示页，而不是继续扩 customer 页面
2. 选择售后执行分页页的原因：
   - Step 08 已经有 settlement 批次页，继续补第二个 admin 只读运营页，能更快形成演示闭环
   - `GET /api/campus/admin/orders/after-sale-executions` 与 `GET /api/campus/admin/orders/{id}/after-sale-result` 已存在，前端可直接复用，不需要新增写接口或改状态机
   - 当前重点是 admin 运营只读可视化，不是继续扩 customer 页面
3. 延续现有 admin 顶层平铺视图与顶层路由风格，没有另起第二套路由体系：
   - `frontend/src/views/CampusAfterSaleExecutionList.vue`
   - 路由 `/campus/after-sale-executions`
4. 新增 admin 侧 API 封装：
   - `getCampusAfterSaleExecutions`
   - `getCampusAdminAfterSaleResult`
5. 售后执行页能力：
   - 调用 `GET /api/campus/admin/orders/after-sale-executions`
   - 支持 `afterSaleExecutionStatus`、`decisionType`、`correctedOnly` 三个最小筛选
   - 列表展示订单号、订单状态、用户 ID、配送员 ID、决策类型、决策金额、执行状态、执行备注、人工纠正标记、执行时间、纠正时间
   - 通过 drawer 复用 `GET /api/campus/admin/orders/{id}/after-sale-result` 查看单笔售后结果汇总
6. 为 admin 现有侧边栏新增“校园售后执行”入口，并补齐 breadcrumb 与菜单高亮逻辑
7. 本轮没有改任何 campus 后端接口，没有新增写接口，没有改数据库
8. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 现在承担未拿到 courier token 前的稳定 customer onboarding 入口与前端首选入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容与 bridge 入口
9. Step 09 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
10. 达到以下条件后，才进入“逐步收口 bridge”评估：
   - onboarding 新入口完成稳定联调与演示
   - 历史调用方完成迁移盘点
   - 未拿 courier token 前的主要资料提交与状态查询场景已被 customer onboarding 覆盖
11. 执行：
   - `npm run build`
12. 本轮继续只做只读 admin 演示页，不扩 customer 主链路，不切旧前端主入口

## Step 10 实际完成事项

1. 本轮优先选择了 admin courier 异常/位置联动视图，而不是单独做 after-sale result 汇总页
2. 选择 courier 异常/位置联动视图的原因：
   - Step 09 已经在售后执行页通过 drawer 复用了 `after-sale-result` 接口，再单独做汇总页收益较低
   - 当前更需要补齐第三个 admin 只读运营页，扩展示范面，而不是继续堆叠售后视图
   - `/api/campus/admin/couriers`、`/api/campus/admin/couriers/{courierProfileId}/exceptions/recent`、`/api/campus/admin/couriers/{courierProfileId}/location-reports` 已存在，足够支撑前端最小联动页
3. 延续现有 admin 顶层平铺视图与顶层路由风格，没有另起第二套路由体系：
   - `frontend/src/views/CampusCourierOpsView.vue`
   - 路由 `/campus/courier-ops`
4. 新增 admin API 封装：
   - `getCampusCouriers`
   - `getCampusCourierRecentExceptions`
   - `getCampusCourierLocationReports`
5. 页面布局采用左侧 courier 列表、右侧异常列表与位置记录两块联动区：
   - 左侧 courier 列表调用 `GET /api/campus/admin/couriers`
   - 右上异常区调用 `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
   - 右下位置区调用 `GET /api/campus/admin/couriers/{courierProfileId}/location-reports`
6. 页面行为：
   - 默认自动选中当前 courier 列表页第一条记录
   - courier 列表为空时，右侧统一展示空态
   - 不接地图 SDK，不画轨迹，不做实时刷新
   - 位置记录按后端现有字段只读展示，不在前端重建定位语义
7. courier 列表页最小筛选使用后端现有真实参数：
   - `realName`
   - `reviewStatus`
   - `enabled`
8. 位置记录列表分页继续沿用后端现有 `page/pageSize` 风格
9. 为 admin 现有侧边栏新增“校园配送运营”入口，并补齐 breadcrumb 与菜单高亮
10. 本轮没有改任何后端接口、VO、数据库和状态机
11. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 继续承担未拿 courier token 前的稳定 onboarding 新入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容入口
12. Step 10 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
13. 达到以下条件后，才进入“逐步收口 bridge”评估：
   - onboarding 新入口完成稳定联调与演示
   - 历史调用方完成迁移盘点
   - customer onboarding 页面已覆盖未拿 courier token 前的主要资料提交和状态查询场景
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
15. 本轮继续只做只读 admin 演示页，不扩 customer 主链路，不切旧前端主入口

## 当前锁定的技术事实

1. 继续使用注解式 MyBatis，不改 XML
2. 继续沿用 `init.sql + migrations + schema-h2.sql + data-h2.sql`
3. 第一版支付仍只允许模拟支付，不接第三方支付
4. 退款/补偿/打款都仍然只是后台运营记录，不是真实支付执行
5. 所有金额处理继续统一为 `BigDecimal`、两位小数、`HALF_UP`
6. `courier/profile` 与 `courier/review-status` 的 bridge 继续保留
7. `customer/courier-onboarding/*` 只是 onboarding 新入口，不替代 `/api/campus/courier/auth/token`
8. 保留 bridge 的原因：
   - 未审核通过用户拿不到 `courier` token`
   - 但资料提交与审核状态查询必须发生在拿 token 之前
9. 收口 bridge 的前提：
   - 先有稳定的 onboarding 替代链路
   - 新入口完成一轮实际前端联调与演示验证
   - 或把资料提交与审核查询统一改为不依赖 `courier` token 的入口

## 当前未解决的问题

- customer 仍没有自助退款申请和结果确认交互，只能查看售后结果回执
- admin 仍没有独立售后执行历史表，只保留订单上的当前执行结果和一次纠正审计
- settlement 仍没有真实打款、撤回打款和复杂对账
- 异常仍只保留最新一次，没有历史记录和处理结果流
- 位置记录仍是低频明细，没有地图、轨迹聚合和频控
- frontend 目前只接入了 customer 两个演示页与 admin settlement 批次演示页，admin 其他运营页仍未接入
- frontend 目前只接入了 customer 两个演示页与 admin 三个只读运营演示页，更多 admin 运营页仍未接入
- `CampusRuleCatalog` 仍是代码常量

## 下一轮建议

- 进入 `Step 11`
- 推荐顺序：
  1. 继续观察 onboarding 新入口与旧 bridge 的并行表现，再决定是否逐步收口旧 bridge
  2. 视业务需要补第四个 admin 最小只读运营页，优先 after-sale result 汇总页或更细的 settlement 只读页
  3. 视业务需要补售后执行历史、异常历史和更细粒度运营审计
  4. 视业务需要补 settlement 更完整的对账、撤回和财务复核能力

## 日志索引

- [领域模型重构规划](../../docs/campus-relay/domain-refactor-plan.md)
- [旧模块到校园代送模块映射](../../docs/campus-relay/legacy-to-campus-mapping.md)
- [Step 01 日志](step-01-domain-model-planning.md)
- [Step 02A 日志](step-02a-db-and-backend-skeleton.md)
- [Step 02B 日志](step-02b-readonly-api.md)
- [Step 03A 日志](step-03a-customer-order-create-and-pay.md)
- [Step 03B 日志](step-03b-courier-profile-and-admin-review.md)
- [Step 03C 日志](step-03c-courier-token-and-accept.md)
- [Step 03D 日志](step-03d-pickup-deliver-confirm.md)
- [Step 03E 日志](step-03e-cancel-after-sale-timeline-settlement.md)
- [Step 03F 日志](step-03f-admin-after-sale-exception-location-settlement.md)
- [Step 04 日志](step-04-after-sale-decision-settlement-admin-ops.md)
- [Step 05 日志](step-05-after-sale-execution-payout-and-order-ops.md)
- [Step 06 日志](step-06-customer-receipt-execution-audit-and-payout-verify.md)
- [Step 07 日志](step-07-customer-onboarding-and-frontend-entry.md)
- [Step 08 日志](step-08-admin-settlement-batch-demo-page.md)
- [Step 09 日志](step-09-admin-after-sale-execution-demo-page.md)
- [Step 10 日志](step-10-admin-courier-ops-demo-page.md)
- [待处理事项](pending-items.md)
- [文件改动清单](file-change-list.md)

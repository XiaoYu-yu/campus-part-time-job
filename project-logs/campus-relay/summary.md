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
- 当前已完成：`Step 11 - admin settlement 只读运营页 / frontend 第四个只读运营入口`
- 当前已完成：`Step 12 - onboarding token 申请衔接 / bridge 收口条件细化 / admin 演示页小修`
- 当前已完成：`Step 13 - courier workbench 最小承接页 / bridge 收口证据链细化`
- 当前已完成：`Step 14 - bridge 真实调用盘点 / courier workbench 最小接单承接`
- 当前已完成：`Step 15 - bridge 依赖评估细化 / courier workbench 订单详情承接`
- 当前已完成：`Step 16 - bridge 收口计划评估 / courier workbench 最小取餐承接`
- 当前已完成：`Step 17 - bridge 执行前评估 / courier workbench 最小 deliver 承接`
- 当前已完成：`Step 18 - bridge 执行准备清单 / courier workbench 最小异常上报承接`
- 当前已完成：`Step 19 - bridge 执行准备模板 / courier workbench confirm 前可视化`
- 当前已完成：`Step 20 - bridge 模板可执行化 / courier workbench completed 后最小承接`
- 当前已完成：`Step 21 - bridge 局部真实验证 / courier workbench completed 结果回读`
- 当前已完成：`Step 22 - H2 可接单数据补齐 / 本地完整链路真实联调`
- 当前已完成：`Step 23 - 共享回归留痕整理 / customer completed 结果回看`
- 当前已完成：`Step 24 - repo 外核实准备增强 / customer 结果页体验增强 / onboarding 请求体类型核查`
- 当前日期：`2026-04-09`
- 当前范围：后端最小闭环已扩展到 customer onboarding 替代链路、customer 侧 courier token 申请衔接、customer completed 结果回看页、courier workbench 最小承接页、最小接单动作、订单详情承接、最小取餐承接、最小 deliver 承接、最小异常上报承接、confirm 前可视化、completed 后最小只读承接与按订单号结果回读，并已在本地 `test profile + H2 + frontend vite` 下真实跑通 `onboarding -> 审核 -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报 -> customer confirm -> completed 回读` 一轮链路，且已整理成可共享回归留痕；Step 24 又补了 repo 外人工核实动作的可执行说明、customer 结果页的无订单号/查无订单/已完成态真实验证，以及 onboarding 提交体 `enabledWorkInOwnBuilding` 的真实类型核查；admin settlement 批次演示页、admin 售后执行演示页、admin courier 异常/位置联动演示页和 admin settlement 只读运营页继续可用，旧外卖模块仍保留可运行，旧前端主链路未被替换

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
  - `/user/campus/order-result`
  - `/user/campus/courier-onboarding`
- 已新增 courier 侧最小承接入口：
  - `/courier/workbench`
- 已新增 admin 侧最小演示入口：
  - `/campus/settlement-batches`
  - `/campus/settlement-batches/:batchNo`
  - `/campus/after-sale-executions`
  - `/campus/courier-ops`
  - `/campus/settlements`
- 已新增 customer 侧 API 封装：
  - `frontend/src/api/campus-customer.js`
- 已新增 courier 侧 API 封装：
  - `frontend/src/api/campus-courier.js`
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

## Step 11 实际完成事项

1. 本轮优先选择了 admin settlement 只读运营页，而不是单独做 after-sale result 汇总页
2. 选择 settlement 只读运营页的原因：
   - Step 09 已经在售后执行页通过 drawer 复用了 `GET /api/campus/admin/orders/{id}/after-sale-result`
   - 单独再做 after-sale result 汇总页会和 Step 09 的能力高度重叠
   - 当前更适合补齐第四个 admin 只读运营页，形成更完整的 settlement 运营演示面
   - `GET /api/campus/admin/settlements`、`GET /api/campus/admin/settlements/reconcile-summary`、`GET /api/campus/admin/settlements/{id}` 已存在，足够支撑页面，不需要扩后端
3. 延续现有 admin 顶层平铺视图与顶层路由风格，没有另起第二套路由体系：
   - `frontend/src/views/CampusSettlementOpsView.vue`
   - 路由 `/campus/settlements`
4. 新增 admin 侧 API 封装：
   - `getCampusSettlements`
   - `getCampusSettlementReconcileSummary`
   - `getCampusSettlementDetail`
5. 页面布局采用“顶部摘要卡片 + 下方结算表格 + 详情 drawer”：
   - 摘要区调用 `GET /api/campus/admin/settlements/reconcile-summary`
   - 列表区调用 `GET /api/campus/admin/settlements`
   - 详情 drawer 调用 `GET /api/campus/admin/settlements/{id}`
6. 列表页最小真实筛选使用后端现有参数：
   - `settlementStatus`
   - `payoutStatus`
   - `courierProfileId`
   - `relayOrderId`
7. 页面行为：
   - 默认加载摘要和列表
   - 点击单条记录后打开详情 drawer
   - 列表为空时摘要区仍正常显示，表格区展示空态
   - 全部保持只读，不新增写操作按钮
8. 为 admin 现有侧边栏新增“校园结算运营”入口，并补齐 breadcrumb 与菜单高亮
9. 本轮没有改任何后端接口、VO、数据库和状态机
10. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 继续承担未拿 courier token 前的稳定 onboarding 新入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容入口
11. Step 11 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
12. 达到以下条件后，才进入“逐步收口 bridge”评估：
   - onboarding 新入口完成稳定联调与演示
   - 历史调用方完成迁移盘点
   - customer onboarding 页面已覆盖未拿 courier token 前的主要资料提交和状态查询场景
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
14. 本轮继续只做只读 admin 演示页，不扩 customer 主链路，不切旧前端主入口

## Step 12 实际完成事项

1. 本轮优先做“收口型增强”，没有机械新增第五个 admin 页。
2. customer onboarding 页面已从“只能看审核状态与 token 资格”升级为“可直接申请 courier token”：
   - 继续复用 `GET /api/campus/customer/courier-onboarding/token-eligibility`
   - 直接接入现有 `POST /api/campus/courier/auth/token`
3. onboarding 页面行为已补齐：
   - `eligible = true` 时展示“申请 courier token”按钮
   - 用户输入当前登录账号密码后即可申请
   - 成功后显示申请成功提示、返回 token、返回的 `courierProfile` 摘要
   - 失败时继续直接展示后端返回错误信息
4. 本轮没有新建后端认证接口，也没有改状态机；前端沿用现有 `phone + password` 调用方式。
5. 为避免 token 只展示不生效，本轮最小补齐了 courier token 的前端会话能力：
   - 申请成功后写入 `localStorage.courier_token`
   - 同时写入 `localStorage.courier_profile`
   - `request.js` 已开始为 `/api/campus/courier/orders/**` 与 `/api/campus/courier/location-reports` 附加 `courier_token`
   - customer logout 或 courier 相关 401 时会清理本地 courier token
6. 本轮没有改 `/api/campus/courier/profile`、`/api/campus/courier/review-status`、`/api/campus/courier/auth/token`，bridge 仍完整保留。
7. 本轮对现有 admin 演示页做了最小细化，而不是新加第五页：
   - `CampusSettlementOpsView.vue`
   - `CampusAfterSaleExecutionList.vue`
   - `CampusCourierOpsView.vue`
8. 细化点固定为：
   - 顶部说明 alert 更明确
   - 空态文案更清晰
   - “暂无”字段展示统一
   - 非金额型处理显示为“无金额型处理”
9. 本轮不补第五个 admin 页的原因：
   - 当前更高优先级是把 onboarding 新入口真正闭环到 token 申请
   - 现有四个 admin 演示页已经足够支撑本阶段演示
   - 继续机械加页的收益低于先把 onboarding 与 bridge 观察项做实
10. Step 12 后 bridge 观察项比之前更具体：
   - 新 onboarding 页面已覆盖资料提交、资料读取、审核状态查询、资格判断、token 申请五个前置场景
   - 旧 bridge 继续覆盖历史调用方和双 token 兼容读取
   - 进入“逐步收口 bridge”前，仍需补足历史调用盘点与一轮稳定联调证据
11. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
12. 本轮没有改 backend 接口、数据库和测试集，只做前端接入与演示细化。

## Step 13 实际完成事项

1. 本轮优先补的是 courier token 申请成功后的最小前台承接页，而不是继续机械新增第五个 admin 页。
2. 新增 courier workbench 页面：
   - `frontend/src/views/courier/CourierWorkbench.vue`
   - 路由 `/courier/workbench`
3. courier workbench 的定位固定为“token 获取后的最小承接页”，不扩成完整 courier 端，不补地图、不补大屏、不补完整接单页群。
4. 页面结构保持最小三段：
   - 身份状态卡：展示 `courierProfileId`、`realName`、`reviewStatus`、`enabled`、token 状态
   - 可接单预览：调用 `GET /api/campus/courier/orders/available`，只展示前 5 条
   - 快捷入口区：刷新工作台、返回 onboarding 页面、返回个人中心
5. 页面行为固定为：
   - 本地存在 `courier_token` 时，自动读取 `GET /api/campus/courier/profile`、`GET /api/campus/courier/review-status`、`GET /api/campus/courier/orders/available`
   - 本地不存在 `courier_token` 时，不调用 courier 业务接口，只展示明确空态和回退入口
   - available orders 为空时，只读展示空态，不做异常处理分叉
6. 为支撑 workbench，本轮新增 courier 前端 API 封装：
   - `getCourierProfile`
   - `getCourierReviewStatus`
   - `getCourierAvailableOrders`
7. `request.js` 已最小修正 courier token 附着范围：
   - `/api/campus/courier/orders/**`
   - `/api/campus/courier/location-reports`
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
8. 对 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留 bridge 兼容策略：
   - 优先使用 `courier_token`
   - 若没有 `courier_token`，回退使用 `customer_token`
9. onboarding 页面已继续打通到 workbench：
   - 申请 courier token 成功后，页面展示“前往 courier 工作台”按钮
10. customer 个人中心已新增“配送员工作台”轻量入口，但没有改旧主链路，没有替换旧页面。
11. Step 13 后 bridge 收口证据链更具体了：
   - 新入口已覆盖资料提交、资料读取、审核状态、资格判断、token 申请、token 后最小承接六个前端场景
   - 旧 bridge 仍承担历史兼容读取与双 token 过渡场景
   - 仍缺历史调用盘点与一轮稳定联调回归证据，因此本轮结论仍是“继续保留，但已接近具备收口评估条件”
12. 本轮没有补第五个 admin 页。
13. 不补第五页的原因：
   - 当前更高优先级是让 onboarding 页面不再停在“拿到 token 即结束”
   - workbench 比新增展示页更直接决定 onboarding 替代链路是否成立
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
15. 本轮没有改 backend 接口、数据库和状态机，只做前端最小承接与 bridge 证据链细化。

## Step 14 实际完成事项

1. 本轮优先做的是两件事：
   - 盘点旧 bridge 的真实调用方
   - 给 courier workbench 补一个最小可接单动作
2. 当前仓库内对旧 bridge 的前端直接调用已经盘点完成：
   - `frontend/src/api/campus-courier.js`
     - `GET /api/campus/courier/profile`
     - `GET /api/campus/courier/review-status`
   - 这两个 API 在当前 frontend 代码里只被 `frontend/src/views/courier/CourierWorkbench.vue` 调用
3. 当前仓库内没有发现其他前端页面继续直接调用上述两个旧 bridge 接口。
4. `request.js` 中这两类请求的 token 附着策略已再次确认：
   - 优先 `courier_token`
   - 若不存在 `courier_token`，才回退到 `customer_token`
5. 因此，本轮已能明确区分：
   - 当前仍在仓库内依赖旧 bridge endpoint 的调用：`CourierWorkbench.vue`
   - 但该调用运行时应走 `courier_token`
   - customer 侧 onboarding 前置场景已由 `customer/courier-onboarding/*` 覆盖，不再依赖旧 bridge 做 customer 读取
6. bridge 证据链本轮已更具体：
   - 当前仓库内前端没有发现“仍必须通过 customer_token 调用 `/api/campus/courier/profile` 或 `/api/campus/courier/review-status` 才能完成”的页面场景
   - 但是否存在仓库外历史调用、手工调用或其他客户端依赖，当前仍未完成盘点
7. courier workbench 已从“只读承接页”升级为“最小接单承接页”：
   - 新增 `acceptCourierOrder`
   - workbench 可直接调用 `POST /api/campus/courier/orders/{id}/accept`
   - 接单成功后立即刷新可接单预览列表
   - 接单失败时继续展示后端原错误信息
   - 无 `courier_token` 时不调用 courier 业务接口
8. 本轮没有改 backend 接口、数据库和状态机。
9. 本轮没有补第五个 admin 页。
10. 不补第五页的原因：
   - 当前更高优先级是把 courier onboarding 替代链路的“token 获取后承接动作”补成最小闭环
   - 继续机械补展示页的收益低于把 workbench 做到“看得到也能接第一单”
11. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 15 实际完成事项

1. 本轮继续优先做两件事：
   - 把 bridge 收口证据链从“repo 内已盘点”推进到“repo 外依赖边界已说明”
   - 给 courier workbench 补一个接单后的最小详情承接点
2. 旧 bridge 依赖评估进一步具体化：
   - repo 内已确认调用方仍只有 `frontend/src/views/courier/CourierWorkbench.vue`
   - 这两个旧 bridge endpoint 在 repo 内只通过 `frontend/src/api/campus-courier.js` 暴露
   - 当前 repo 内已经没有发现 customer 页面继续依赖 `customer_token` 调用 `/api/campus/courier/profile` 或 `/api/campus/courier/review-status`
3. 对 repo 外依赖的结论本轮明确为：
   - 由于当前仓库内没有其他调用方，repo 外依赖无法从代码中继续穷举
   - 现阶段只能将 repo 外依赖范围定义为：历史客户端、手工联调脚本、未纳入本仓库的旧页面或外部调用方
   - 这部分不能编造，只能作为待核实边界保留
4. 因此，本轮 bridge 结论更明确：
   - 已经具备进入“逐步收口评估”的基础
   - 真正收口前还差两类证据：
     - repo 外依赖确认
     - 一轮稳定联调与回归记录
5. courier workbench 已补“订单详情承接点”：
   - 采用 drawer 方案，没有新增独立页面
   - 在可接单预览表格中增加“详情”按钮
   - 调用 `GET /api/campus/courier/orders/{id}`
6. 详情 drawer 最小展示字段：
   - 订单号
   - 订单状态
   - 取餐点
   - 配送楼栋
   - 配送详情
   - 订单金额
   - 取餐码
   - customer 备注
   - 接单时间
   - 创建时间
   - 更新时间
   - 当前 courierProfileId
7. 接单成功后，workbench 现在会：
   - 先调用 `POST /api/campus/courier/orders/{id}/accept`
   - 刷新可接单列表
   - 自动打开该订单详情 drawer
8. 无 `courier_token` 时：
   - 页面不调用详情接口
   - 继续只展示空态和回退入口
9. 本轮没有改 backend 接口、数据库和状态机
10. 本轮没有补第五个 admin 页
11. 不补第五页的原因：
   - 当前更高优先级仍是把 onboarding 替代链路做成“token 前 + token 后”连续闭环
   - workbench 详情承接比新增一个展示页更直接支撑 bridge 收口评估
12. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 16 实际完成事项

1. 本轮新增 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 文档中已经明确拆分：
   - repo 内已确认调用
   - repo 内可明确排除的依赖
   - repo 外暂未确认依赖范围
   - 进入逐步收口计划前仍缺的证据
   - Phase A / B / C 分阶段建议
   - 回滚与兼容保留策略
3. 本轮 bridge 结论已推进为：
   - 已可以进入“逐步收口计划设计阶段”
   - 但仍不能进入实际删除阶段
4. courier workbench 已补最小取餐承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入 `POST /api/campus/courier/orders/{id}/pickup`
5. workbench 没有新建页面，直接在订单详情 drawer 中新增最小取餐区：
   - `pickupProofImageUrl`
   - `courierRemark`
   - “确认取餐”
6. 本轮按后端真实 DTO 接入，没有臆造取餐码或新接口。
7. 接单成功后：
   - 仍会刷新可接单列表
   - 自动打开订单详情 drawer
   - 用户可以继续在 drawer 中尝试取餐
8. 取餐成功后：
   - 页面显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
9. 取餐失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页
12. 不补第五页的原因：
   - 当前更高优先级是把 bridge 收口评估补成“可进入计划设计阶段”
   - workbench 的最小取餐承接比继续补展示页更直接支撑主链路闭环
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 17 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮把 bridge 评估重点从“能否做计划设计”推进为“能否进入 Phase A 执行准备”。
3. 当前文档结论已明确：
   - repo 内证据已经稳定
   - repo 外依赖仍只能列为待确认边界
   - 当前还不具备进入 Phase A 执行准备的完整条件
4. courier workbench 已补最小 deliver 承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入 `POST /api/campus/courier/orders/{id}/deliver`
5. deliver 没有新建页面，直接放在订单详情 drawer 中。
6. 本轮按后端真实 DTO 接入，没有臆造 deliveredAt、proofImageUrl 等字段：
   - `courierRemark`
7. deliver 区按真实状态机变化显示动作：
   - `PICKED_UP -> 开始配送`
   - `DELIVERING -> 确认送达`
8. 取餐成功后，当前详情刷新为 `PICKED_UP` 时，用户可继续在同一 drawer 中操作 deliver。
9. deliver 成功后：
   - 显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
10. deliver 失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
11. 本轮没有改 backend 接口、数据库和状态机
12. 本轮没有补第五个 admin 页
13. 不补第五页的原因：
   - 当前更高优先级是把 bridge 评估推进到“执行前评估”层
   - workbench 的最小 deliver 承接比继续补展示页更直接支撑主链路闭环
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 18 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮把 bridge 评估重点从“能否进入执行前评估”推进为“能否进入执行准备”。
3. 文档新增了 `Step 18 执行准备清单`，把以下事项明确列为“待人工核实项”：
   - 是否还有仓库外旧页面直接调用 `/api/campus/courier/profile`
   - 是否还有仓库外旧页面直接调用 `/api/campus/courier/review-status`
   - 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge
   - 是否已有一轮 `customer onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的稳定联调记录
   - workbench 在纯 `courier_token` 路径下是否稳定
4. 本轮 bridge 结论已进一步具体化：
   - repo 内证据已经稳定
   - repo 外依赖仍只能列为待人工核实边界
   - 当前还不具备进入 `Phase A` 执行准备的完整条件
   - 缺口已经收敛到执行准备清单，而不是泛化观察
5. courier workbench 已补最小异常上报承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入 `POST /api/campus/courier/orders/{id}/exception-report`
6. exception-report 没有新建页面，直接放在订单详情 drawer 中。
7. 本轮按后端真实 DTO 接入，没有臆造 proofImageUrl 等字段：
   - `exceptionType`
   - `exceptionRemark`
8. 异常上报区只在后端真实允许的状态下可用：
   - `ACCEPTED`
   - `PICKED_UP`
   - `DELIVERING`
   - `AWAITING_CONFIRMATION`
9. 异常上报成功后：
   - 显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
10. 异常上报失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
11. 本轮没有改 backend 接口、数据库和状态机
12. 本轮没有补第五个 admin 页
13. 不补第五页的原因：
   - 当前更高优先级是把 bridge 执行准备证据写实
   - workbench 的最小异常上报承接比继续补展示页更直接支撑 courier 主链路
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 19 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮没有伪造 repo 外依赖结果，而是把执行准备缺口继续改造成可人工关闭的模板文档。
3. 新增 bridge 人工核实 checklist：
   - `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
4. checklist 中为每个待人工核实项提供了统一结构：
   - 核实方法
   - 记录位置
   - 核实结果
   - 负责人
   - 日期
   - 是否通过
5. 新增 bridge 联调/回归模板：
   - `project-logs/campus-relay/bridge-regression-template.md`
6. 回归模板已按完整链路拆分：
   - customer onboarding 提交资料
   - customer 查看审核状态
   - customer 申请 courier token
   - courier workbench 加载 profile/review-status
   - courier 接单
   - courier 取餐
   - courier deliver
   - courier 异常上报
   - customer 侧确认前状态可视化或结果查看
7. courier workbench 本轮没有新增页面，继续复用订单详情 drawer。
8. 本轮新增了“送达后 / confirm 前”状态区：
   - 当订单状态为 `AWAITING_CONFIRMATION` 时，显示“已送达，等待用户确认”
   - 当订单状态为 `COMPLETED` 时，显示“订单已完成”的最小只读状态
9. confirm 前状态区继续只读复用现有详情字段：
   - `status`
   - `deliveredAt`
   - `exceptionType`
   - `exceptionRemark`
   - `acceptedAt`
   - `pickedUpAt`
   - `deliveredAt`
   - `autoCompleteAt`
   - `updatedAt`
10. deliver 成功后刷新详情时，如果订单进入 `AWAITING_CONFIRMATION`，可直接在同一 drawer 中看到等待确认区，不需要新页面。
11. 本轮再次确认 `request.js` 的 courier token 附着范围已足够覆盖：
   - `/api/campus/courier/orders/**`
   - `/api/campus/courier/location-reports`
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
12. 本轮没有补第五个 admin 页。
13. 不补第五页的原因：
   - 当前更高优先级是把 bridge 执行准备缺口变成可落地模板
   - confirm 前可视化比继续补一个展示页更直接支撑 courier 主链路闭环
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 20 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮没有伪造任何 repo 外依赖结果，也没有代填任何人工核实结论。
3. 本轮把 bridge 模板继续补成“可真正执行和可真正填写”的状态：
   - `bridge-execution-readiness-checklist.md`
   - `bridge-regression-template.md`
4. checklist 本轮新增了每个核实项的执行辅助字段：
   - 推荐执行入口
   - 推荐证据类型
   - 失败时如何记录
   - 是否影响进入 `Phase A`
5. regression template 本轮新增了每一步的联调辅助字段：
   - 接口观察点
   - 关键状态字段
   - 失败记录建议
   - 是否为阻塞项
6. 本轮结论继续保持真实边界：
   - 模板已经可执行
   - 但真实人工核实结果仍待补齐
   - 当前仍不具备进入 `Phase A` 执行准备的完整条件
7. courier workbench 本轮没有新增页面，继续复用 `CourierWorkbench.vue` 的订单详情 drawer。
8. 本轮新增了 completed 后最小只读承接：
   - `AWAITING_CONFIRMATION` 时继续展示等待用户确认态
   - `COMPLETED` 时展示明确的已完成摘要态
9. completed / confirm 后状态区继续只读复用现有详情字段：
   - `status`
   - `pickupPointName`
   - `deliveryBuilding`
   - `deliveryDetail`
   - `totalAmount`
   - `customerRemark`
   - `deliveredAt`
   - `autoCompleteAt`
   - `exceptionType`
   - `exceptionRemark`
   - `updatedAt`
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页
12. 不补第五页的原因：
   - 当前更高优先级是把 bridge 模板从“存在”推进到“可真正执行”
   - courier workbench 的 completed 后最小承接比继续机械补展示页更直接支撑主链路闭环
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 21 实际完成事项

1. 本轮继续更新 bridge 评估与执行准备文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
   - `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
   - `project-logs/campus-relay/bridge-regression-template.md`
2. 本轮没有伪造 repo 外依赖结果，也没有把待执行项写成“已通过”。
3. 本轮补齐了 repo 内真实可执行验证：
   - 真实调用 `POST /api/campus/courier/auth/token` 成功取得 `courier_token`
   - 真实打开 `/courier/workbench`
   - 真实抓取 `/api/campus/courier/profile`、`/api/campus/courier/review-status`、`/api/campus/courier/orders/available` 请求
   - 真实确认 `profile` / `review-status` 优先走 `courier_token`
   - 真实确认无 `courier_token` 时页面停在空态，且不再请求 courier 业务接口
4. checklist 本轮已填写一项真实通过项：
   - workbench 的 `profile / review-status` 请求是否全程只走 `courier_token`
5. checklist 本轮也明确留下了一项真实但未完成的结果：
   - 当前 H2 种子下 `available orders = 0`
   - 因此还没有形成完整的 `接单 -> 取餐 -> deliver -> 异常上报` 真实联调记录
6. regression template 本轮已补入真实执行结果：
   - token 申请接口层验证
   - workbench 加载验证
   - completed 结果回读验证
   - 未执行步骤保持为未执行，没有代填成功
7. courier workbench 本轮继续复用 `CourierWorkbench.vue`，没有新建页面群。
8. 本轮新增了最小“按订单号查看详情”入口：
   - 用于在当前可接单列表为空时，仍可读取已完成订单详情
   - 直接复用 `GET /api/campus/courier/orders/{id}`
9. 已真实验证 completed 结果回读：
   - 通过订单号 `CR202604060001`
   - drawer 成功展示 `COMPLETED` 状态、`deliveredAt`、`autoCompleteAt` 和完成后最小只读承接区
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页
12. 不补第五页的原因：
   - 当前更高优先级是把 bridge 评估推进到“有真实局部留痕”
   - completed 结果回读比继续机械补展示页更直接支撑 courier 主链路与收口评估
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 22 实际完成事项

1. 本轮先解决了本地 `available orders = 0` 的真实联调阻塞，而不是继续堆前端展示页。
2. 在 `backend/src/main/resources/db/data-h2.sql` 新增了一笔 H2 联调样本：
   - 订单号：`CR202604070002`
   - customer：`13900139001`
   - pickup point：`2 / NORTH_GATE_LOCKER`
   - delivery building：`梅园`
   - 状态：`WAITING_ACCEPT`
   - paymentStatus：`PAID`
3. 这笔数据使 `GET /api/campus/courier/orders/available?page=1&pageSize=10` 在本地 `test profile + H2` 下真实返回至少一条可接单记录。
4. 本轮同时修正了一个 repo 内真实阻塞：
   - `/courier/workbench` 在只有 `courier_token` 时会被 `UserLayout` 的 customer 购物车请求拉回 `/user/login`
   - 现在 `frontend/src/layout/UserLayout.vue` 在无 `customer_token` 时不再请求 customer 购物车接口，且 `/courier/**` 不再渲染 customer 导航
5. 本轮真实跑通了一轮完整本地链路：
   - customer onboarding 提交资料
   - admin 审核通过
   - customer 查看 token eligibility
   - customer 申请 courier token
   - workbench 加载 profile / review-status / available orders
   - courier 接单
   - courier 取餐
   - courier deliver 到 `DELIVERING`
   - courier deliver 到 `AWAITING_CONFIRMATION`
   - courier 异常上报
   - customer 确认送达
   - courier completed 结果回读
6. 本轮真实联调样本账号：
   - courier candidate / onboarding：`13900139000 / 123456`
   - customer 订单所属人：`13900139001 / 123456`
   - admin：`13800138000 / 123456`
7. 本轮真实样本订单：
   - `CR202604070002`
   - 状态推进：`WAITING_ACCEPT -> ACCEPTED -> PICKED_UP -> DELIVERING -> AWAITING_CONFIRMATION -> COMPLETED`
8. bridge checklist 已补入真实结果：
   - workbench 的 `profile / review-status` 已在纯 `courier_token` 路径下真实验证通过
   - `onboarding -> token -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报 -> customer confirm -> completed 回读` 已在本地真实跑通
9. bridge regression template 已不再只是模板空位，已经补入第一轮真实留痕。
10. 当前 bridge 仍不删除，原因已收敛到 repo 外依赖人工核实，而不是 repo 内页面或 H2 数据阻塞。
11. 本轮没有补 customer confirm 结果回看新页面，也没有补第五个 admin 页。
12. 不补这些页面的原因：
   - 当前最高优先级是先把本地完整链路真实跑通，并把 bridge 评估从“可执行模板”推进到“有真实本地留痕”
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 23 实际完成事项

1. 本轮没有伪造新的联调结果，而是把 Step 22 已真实跑通的本地链路整理成可共享、可复用、可答辩说明的证据文档：
   - `project-logs/campus-relay/step-23-shared-regression-evidence.md`
2. 共享回归证据文档已真实整理以下信息：
   - customer 账号 `13900139001 / 123456`
   - courier onboarding / token 申请账号 `13900139000 / 123456`
   - admin 账号 `13800138000 / 123456`
   - 样本订单 `CR202604070002`
   - 状态流转 `WAITING_ACCEPT -> ACCEPTED -> PICKED_UP -> DELIVERING -> AWAITING_CONFIRMATION -> COMPLETED`
   - 对应接口、页面入口、日志位置、现有证据和待补截图项
3. 本轮新增 customer 侧最小 completed 结果回看页：
   - 路由 `/user/campus/order-result`
   - 页面文件 `frontend/src/views/user/CampusOrderResult.vue`
4. completed 结果回看页复用现有读取接口：
   - `GET /api/campus/customer/orders/{id}`
   - 没有新增后端写接口，没有改状态机
5. 页面按真实字段展示：
   - `status`
   - `deliveredAt`
   - `autoCompleteAt`
   - `pickupPointName`
   - `deliveryBuilding`
   - `deliveryDetail`
   - `totalAmount`
   - `remark`
   - `exceptionType`
   - `exceptionRemark`
   - `updatedAt`
6. 页面按真实状态做最小只读提示：
   - `AWAITING_CONFIRMATION` 时显示等待确认文案
   - `COMPLETED` 时显示已完成结果回看文案
7. 在 `frontend/src/views/user/Profile.vue` 新增“代送结果回看”入口，不改旧 `orders/cart/address` 主链路。
8. 在 `frontend/src/api/campus-customer.js` 补充：
   - `getCampusCustomerOrderDetail`
9. 本轮继续更新 bridge 相关文档，但没有把 repo 外人工核实项写成已完成：
   - `bridge-phaseout-evaluation.md`
   - `bridge-execution-readiness-checklist.md`
   - `bridge-regression-template.md`
10. `bridge-regression-template.md` 本轮明确补入：
    - Step 22 已跑通完整链路的共享证据引用
    - Step 23 customer completed 结果回看页的真实验证结果
11. 本轮没有补第五个 admin 页。
12. 不补第五页的原因：
    - 当前更高优先级是把 Step 22 的真实链路沉淀成可共享留痕
    - 以及补 customer confirm 后的最小结果回看
13. 执行：
    - `.\mvnw.cmd -DskipTests compile`
    - `npm run build`

## Step 24 实际完成事项

1. 本轮没有新增 repo 外人工核实结果，也没有把任何待人工核实项改写成“已通过”。
2. 继续增强了 repo 外人工核实准备：
   - `bridge-execution-readiness-checklist.md` 对未关闭项补齐了：
     - 去哪里核
     - 看什么证据
     - 成功时如何留痕
     - 失败时如何留痕
     - 是否阻塞 `Phase A`
3. 继续更新了：
   - `bridge-phaseout-evaluation.md`
   - `bridge-regression-template.md`
   其中明确写清了：repo 内证据稳定，但 repo 外依赖仍待人工核实。
4. customer 结果回看页 `frontend/src/views/user/CampusOrderResult.vue` 做了最小体验增强：
   - 查询前显示“等待输入订单号”初始提示
   - 查询时清空旧结果，避免上一笔结果残留
   - 无订单号时清空 URL 中旧 `orderId`
   - 查询不存在订单时显示明确错误态
   - `AWAITING_CONFIRMATION / COMPLETED` 状态文案与摘要分组更清楚
5. 本轮对 customer 结果页做了真实页面级验证：
   - 无 `orderId` 时能看到初始提示
   - 查询 `CR404` 时显示“订单不存在”
   - 查询 `CR202604070002` 时显示 completed 结果摘要
6. 本轮真实核查了 `enabledWorkInOwnBuilding` 提交类型：
   - 检查文件：
     - `frontend/src/views/user/CourierOnboarding.vue`
     - `frontend/src/api/campus-customer.js`
   - 通过 Playwright 抓取 `POST /api/campus/customer/courier-onboarding/profile` 的真实请求体，确认 `enabledWorkInOwnBuilding` 当前发送的是整数 `1/0`，不会再发送 boolean。
7. 本轮没有补第五个 admin 页。
8. 不补第五页的原因：
   - 当前更高优先级是继续推进 bridge 收口评估和 customer 结果页真实使用体验
   - 继续补 admin 页会稀释 repo 外人工核实准备的优先级
9. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

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
10. 当前 bridge 收口阶段结论：
   - repo 内证据已足够支持进入“逐步收口计划设计阶段”
   - 但还不具备进入 `Phase A` 执行准备的完整条件
   - 当前缺口已收敛为 repo 外依赖人工核实和一轮可共享的稳定联调/回归证据
   - Step 22 已在本地 `test profile + H2` 下真实跑通一轮完整链路，repo 内阻塞已基本清掉
   - Step 23 已把这轮真实链路整理成共享回归留痕，并补了 customer completed 结果回看页
   - 因此当前不能直接删除旧 bridge，也不能启动执行准备

## 当前未解决的问题

- customer 仍没有自助退款申请和结果确认交互，只能查看售后结果回执
- admin 仍没有独立售后执行历史表，只保留订单上的当前执行结果和一次纠正审计
- settlement 仍没有真实打款、撤回打款和复杂对账
- 异常仍只保留最新一次，没有历史记录和处理结果流
- 位置记录仍是低频明细，没有地图、轨迹聚合和频控
- frontend 目前已接入 customer 三个演示页与 admin 四个只读运营演示页，但仍不是完整校园代送后台
- `CampusRuleCatalog` 仍是代码常量

## 下一轮建议

- 进入 `Step 25`
- 推荐顺序：
  1. 按 checklist 继续补 repo 外旧页面、历史客户端和手工脚本依赖的人工核实结果，判断 bridge 是否可以进入 `Phase A` 执行准备
  2. 若继续扩 customer/courier 前端，优先评估 customer completed 结果页的状态衔接是否还需补更明确摘要
  3. 视 bridge 评估推进情况，再决定是否补第五个 admin 最小只读页，避免稀释当前优先级
  4. 视业务需要补售后执行历史、异常历史和更细粒度运营审计

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
- [Step 11 日志](step-11-admin-settlement-ops-demo-page.md)
- [Step 12 日志](step-12-onboarding-token-bridge-and-demo-polish.md)
- [Step 13 日志](step-13-courier-workbench-and-bridge-evidence.md)
- [Step 14 日志](step-14-bridge-audit-and-workbench-accept.md)
- [Step 15 日志](step-15-bridge-evidence-and-workbench-detail.md)
- [Step 16 日志](step-16-bridge-plan-and-workbench-pickup.md)
- [Step 17 日志](step-17-bridge-readiness-and-workbench-deliver.md)
- [Step 18 日志](step-18-bridge-readiness-checklist-and-workbench-exception.md)
- [Step 19 日志](step-19-bridge-templates-and-workbench-confirm-visual.md)
- [Step 20 日志](step-20-bridge-template-hardening-and-workbench-completed-visual.md)
- [Step 21 日志](step-21-real-verification-and-workbench-completed-readback.md)
- [Step 22 日志](step-22-real-local-chain-and-h2-seed.md)
- [Step 23 日志](step-23-shared-regression-and-customer-result-readback.md)
- [Step 23 共享回归留痕](step-23-shared-regression-evidence.md)
- [Step 24 日志](step-24-bridge-readiness-and-customer-result-polish.md)
- [bridge 收口评估](bridge-phaseout-evaluation.md)
- [bridge 执行准备 checklist](bridge-execution-readiness-checklist.md)
- [bridge 联调/回归模板](bridge-regression-template.md)
- [待处理事项](pending-items.md)
- [文件改动清单](file-change-list.md)

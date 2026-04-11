# Step 36 - 展示级优化执行轮 2

## 本轮目标

1. 基于 Step 35 已完成的第一轮 P0 页面 polish，进入第二轮展示级优化决策。
2. 本轮只能二选一：
   - 方案 A：只处理 `frontend/src/views/user/CourierOnboarding.vue`
   - 方案 B：只处理一个 admin 只读运营页
3. 本轮不铺开多个页面，不新增页面，不改 bridge，不改接口，不改路由，不改后端。

## 本轮选择

结论：选择方案 A，只处理 `CourierOnboarding.vue`。

## 为什么选 A 不选 B

### 演示收益

1. `CourierOnboarding.vue` 是 customer 到 courier token 的前置入口。
2. Step 35 已 polish 了 `CourierWorkbench.vue` 和 `CampusOrderResult.vue`，本轮补 onboarding 能把演示链路前半段补齐。
3. onboarding 页面承载资料提交、审核状态、token 资格和 token 申请四类信息，解释成本高于单个 admin 只读运营页。

### 风险

1. `CourierOnboarding.vue` 的 polish 可以限制在模板和样式层，不需要改提交逻辑。
2. 本轮没有改 script，因此不会影响：
   - `enabledWorkInOwnBuilding` 的 1 / 0 提交
   - token 申请逻辑
   - 接口调用顺序
   - 路由跳转
3. admin 单页 polish 虽然风险也低，但当前收益更偏后台一致性，不如 onboarding 对主演示链路直接。

### 改动范围

1. 本轮只改一个 Vue 页面。
2. 未改 API 封装。
3. 未改路由。
4. 未改后端。

### 当前展示目标贴合度

1. 当前最适合展示的链路是 onboarding -> token -> workbench -> completed 结果回看。
2. Step 35 已覆盖 workbench 与 completed 结果回看。
3. Step 36 选择 onboarding，能把前置入口讲解补齐。

## `CourierOnboarding.vue` 展示级优化

本轮只做展示层调整：

1. 顶部新增“customer 前置入口”标记，明确该页定位。
2. 顶部下方新增三段式流程说明：
   - 提交资料
   - 等待审核
   - 申请 token
3. 审核与资格概览区新增标题和说明，明确是只读展示当前入驻状态。
4. token 申请区新增三项说明：
   - 开放条件
   - 申请凭证
   - 成功后承接
5. token 申请成功结果区增加更明确的结果面板样式。
6. 资料表单区新增说明标题，明确本轮不改变提交体和按钮行为。
7. 表单内部新增分组说明：
   - 基础与校区信息
   - 接单偏好与备注
8. 接单偏好说明中明确 `enabledWorkInOwnBuilding` 仍按后端 DTO 期望转为 `1 / 0`。
9. 增加对应 CSS，用于统一流程条、标记、说明块、结果面板和表单分组层级。

## 明确没动什么

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 `frontend/src/api/campus-courier.js`。
4. 未改 `frontend/src/api/campus-customer.js`。
5. 未改 token 附着逻辑。
6. 未改 API 调用顺序。
7. 未改 `enabledWorkInOwnBuilding` 的整数提交逻辑。
8. 未改 token 申请逻辑。
9. 未改按钮行为。
10. 未改路由。
11. 未改后端代码。
12. 未处理任何 admin 只读运营页。
13. 未新增第五个 admin 页。

## 验证结果

1. `.\mvnw.cmd -DskipTests compile`：通过。
2. `npm run build`：通过。
3. 静态范围确认：
   - 本轮没有修改 `request.js`
   - 本轮没有修改 `campus-courier.js`
   - 本轮没有修改 `campus-customer.js`
   - 本轮没有修改路由
   - 本轮没有修改后端代码
4. 页面行为层面：
   - 资料读取仍通过现有 `getCourierOnboardingProfile`
   - 审核状态仍通过现有 `getCourierOnboardingReviewStatus`
   - token 资格仍通过现有 `getCourierTokenEligibility`
   - token 申请仍通过现有 `applyCourierToken`
   - 资料提交 payload 中 `enabledWorkInOwnBuilding` 仍由 switch 转换为 `1 / 0`

## 是否影响 bridge

不影响。

本轮没有出现 Step 33 定义的恢复推进触发条件，bridge 继续保持 `Phase A no-op` 冻结态。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. bridge 完全保留。
3. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
4. 本轮最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `frontend/src/views/user/CourierOnboarding.vue`
2. `project-logs/campus-relay/summary.md`
3. `project-logs/campus-relay/pending-items.md`
4. `project-logs/campus-relay/file-change-list.md`
5. `project-logs/campus-relay/step-35-display-polish-execution-round-1.md`
6. `project-logs/campus-relay/step-36-display-polish-execution-round-2.md`

## 下一轮建议

1. Step 37 先回看 Step 35 / Step 36 三个 customer/courier 页面 polish 效果。
2. 若继续展示级优化，建议只选择一个 admin 只读运营页做单页 polish。
3. 推荐优先从 `CampusSettlementOpsView.vue` 或 `CampusAfterSaleExecutionList.vue` 二选一。
4. 仍然不要自动铺开 admin 四页。
5. 第五个 admin 页继续后置。
6. bridge 主线继续冻结，除非出现 Step 33 定义的恢复推进触发条件。

## Step 37 执行回填

1. Step 37 已按本建议进入单页 admin 只读运营页 polish。
2. 本轮选择 settlement，只处理 `frontend/src/views/CampusSettlementOpsView.vue`，没有处理 `CampusAfterSaleExecutionList.vue`。
3. 选择原因：
   - settlement 页已有摘要、筛选、列表和详情 drawer，后台只读运营展示形态更完整。
   - settlement 可承接 completed 链路后的结算回看，与 Step 35 / Step 36 主演示链路互补性更强。
   - after-sale 页涉及执行状态和纠正信息，解释成本更高，适合后续单独处理。
4. 展示级优化摘要：
   - 增加“只读运营”标记。
   - 增加三段式运营引导。
   - 增加筛选区说明和表格只读提示。
   - 优化空态文案。
   - 将 settlement / payout 状态 tag 改为中文展示文案。
   - 优化详情 drawer 顶部摘要和字段来源说明。
5. Step 37 明确未改：
   - bridge
   - `request.js`
   - `campus-*` API 文件运行时行为
   - token 附着逻辑
   - API 调用顺序
   - 路由
   - 后端代码
   - 新页面
6. Step 38 建议：
   - 先回看 Step 37 settlement 单页 polish 效果。
   - 若继续 admin 展示级优化，优先只评估 `CampusAfterSaleExecutionList.vue`。
   - 不要自动铺开剩余 admin 页面。
   - bridge 继续保持 `Phase A no-op` 冻结态。

# Step 34 - 非 bridge 方向收束与展示级优化候选评估

## 本轮目标

1. 基于 Step 33 已进入 `Phase A no-op` 冻结态，不再默认推进 bridge 主线。
2. 收束不触 bridge 的下一阶段工作方向。
3. 评估现有页面的纯展示级优化候选，不改业务语义、不改接口、不新增页面。
4. 整理当前最适合演示的真实链路、页面入口、账号和样本订单。
5. 梳理与 bridge 无关的后端后续能力空间，只做判断，不扩开发。

## 为什么 Step 34 不再默认推进 bridge

1. Step 29 已按 owner 事实关闭 repo 外阻塞项。
2. Step 30 已固化 `Phase A` 边界、bridge 保留范围、回滚策略和最小回归清单。
3. Step 31 已完成真实执行前复核，链路稳定，但没有找到值得执行的最小收口动作。
4. Step 32 扩展候选池后仍然 `no-go`。
5. Step 33 已正式进入 `Phase A no-op` 冻结态。
6. 因此本轮继续寻找 bridge 收口动作的收益低，风险是把已稳定链路重新拉回高变更区。

本轮不更新 `bridge-phaseout-evaluation.md` 和 `bridge-execution-readiness-checklist.md`，因为没有出现恢复推进触发条件。

## 本轮收束出的非 bridge 优先方向

1. 第一优先级：现有页面纯展示级优化候选评估。
   - 目的：找到零 bridge 风险、零接口风险、改动可控的 polish 切入点。
   - 约束：不改业务跳转、不改路由结构、不改接口语义、不新增页面。
2. 第二优先级：演示资料整理。
   - 目的：把已跑通的真实链路整理成可展示、可答辩、可复用的入口清单。
   - 约束：围绕真实链路，不写空泛项目说明。
3. 第三优先级：非 bridge 后端能力梳理。
   - 目的：为后续脱离 bridge 主线后的售后、异常、settlement 能力规划做准备。
   - 约束：只梳理，不新增表、不新增接口、不改状态机。

## 页面展示级优化候选池

### P0 - `frontend/src/views/courier/CourierWorkbench.vue`

收益：

1. 这是当前演示链路中最关键的 courier 前台承接页。
2. 页面承载 token 状态、profile、review-status、可接单、详情 drawer、接单、取餐、deliver、异常上报、confirm 前/后状态展示，信息量最大。
3. 纯展示级优化能显著提升演示可读性，尤其是 drawer 内分区、动作区说明、状态区层级和空态。

风险：

1. 页面动作多，容易误碰业务逻辑。
2. 如果一次改太多，容易把展示 polish 变成工作流重构。

优先级结论：

1. 建议放进下一轮小范围执行。
2. 执行边界必须限制为文案、区块层级、空态、loading、状态 tag 和 drawer 分区一致性。
3. 不改 API 调用、不改 token 判断、不改按钮可用条件、不改状态机判断。

### P0 - `frontend/src/views/user/CampusOrderResult.vue`

收益：

1. 这是 completed 结果回看和 customer confirm 后结果承接的最终展示点。
2. 页面已具备等待输入、loading、错误态、`AWAITING_CONFIRMATION` 和 `COMPLETED` 结果展示基础。
3. 继续做纯展示级 polish 可以提升演示收尾阶段的清晰度。

风险：

1. 风险低于 workbench，因为页面是只读读取。
2. 主要风险是过度重写状态文案，导致与真实字段不一致。

优先级结论：

1. 建议与 workbench 组成下一轮第一批 polish，但范围要更小。
2. 重点只做结果摘要层级、状态提示、无订单号/查无订单/接口失败文案一致性。
3. 不新增动作按钮，不新增接口。

### P1 - `frontend/src/views/user/CourierOnboarding.vue`

收益：

1. onboarding 是 customer 到 courier token 的前置入口，演示时会频繁出现。
2. 当前信息区、token 申请区和资料表单都已存在，适合做说明层级与表单区视觉一致性。

风险：

1. 页面涉及资料提交和 token 申请动作，不适合在展示 polish 轮里调整行为。
2. 需要避免误改 `enabledWorkInOwnBuilding` 的整数提交语义。

优先级结论：

1. 暂列第二批候选。
2. 下一轮如果只做一个小执行轮，建议先不动 onboarding。

### P1 - admin 四个只读运营页

范围：

1. `frontend/src/views/CampusSettlementBatchList.vue`
2. `frontend/src/views/CampusAfterSaleExecutionList.vue`
3. `frontend/src/views/CampusCourierOpsView.vue`
4. `frontend/src/views/CampusSettlementOpsView.vue`

收益：

1. 四个页面已形成 admin 最小只读运营演示面。
2. 可通过统一空态、loading、表格字段展示、状态 tag 和页面说明文案提升演示一致性。

风险：

1. 均为只读页面，业务风险低。
2. 但四个页面一起改会导致范围过大，不适合下一轮第一批执行。

优先级结论：

1. 暂列第二批候选。
2. 若下一轮不选 workbench/customer result，可选择其中一个只读 admin 页做单页 polish。

### P2 - `frontend/src/views/user/Profile.vue`

收益：

1. 个人中心是 customer 侧 campus 演示入口集合位置。
2. 可优化“校园代送”入口区的说明和入口排序。

风险：

1. 页面仍承载旧外卖个人中心入口，不能打乱旧主链路。
2. 展示收益低于 workbench、order result 和 onboarding。

优先级结论：

1. 暂不建议作为下一轮优先执行对象。
2. 只在后续做 customer 入口统一 polish 时再处理。

## 是否建议下一轮进入展示级优化执行

结论：建议进入一个小范围展示级优化执行轮。

建议 Step 35 只选以下一组：

1. `CourierWorkbench.vue`
2. `CampusOrderResult.vue`

执行边界：

1. 只做展示层 polish。
2. 不新增页面。
3. 不改路由。
4. 不改接口。
5. 不改 `request.js`。
6. 不改 token 附着逻辑。
7. 不改按钮动作语义。
8. 不改后端状态机。

建议先做这组的原因：

1. 它们覆盖真实演示链路的中段和收尾。
2. 它们比继续补 admin 页更贴近当前已跑通闭环的展示价值。
3. 它们不需要重新打开 bridge 主线。

## 演示资料整理摘要

### 当前最适合演示的真实链路

1. customer 登录。
2. 进入 `/user/campus/courier-onboarding`。
3. 提交或查看 courier onboarding 资料。
4. admin 审核通过 courier 资料。
5. customer 在 onboarding 页申请 courier token。
6. 进入 `/courier/workbench`。
7. 查看 profile / review-status / available orders。
8. 对样本订单执行接单。
9. 在订单详情 drawer 中执行取餐。
10. 在订单详情 drawer 中执行 deliver。
11. 在订单详情 drawer 中执行异常上报。
12. customer confirm。
13. courier workbench 通过订单详情回读 `COMPLETED`。
14. customer 进入 `/user/campus/order-result?orderId=CR202604070002` 回看 completed 结果。

### 页面入口

1. customer onboarding：`/user/campus/courier-onboarding`
2. courier workbench：`/courier/workbench`
3. customer 结果回看：`/user/campus/order-result?orderId=CR202604070002`
4. admin settlement 批次：`/campus/settlement-batches`
5. admin 售后执行：`/campus/after-sale-executions`
6. admin courier 运营：`/campus/courier-ops`
7. admin settlement 运营：`/campus/settlements`

### 演示账号与样本订单

1. customer / courier candidate：`13900139000 / 123456`
2. customer order owner：`13900139001 / 123456`
3. admin：`13800138000 / 123456`
4. 样本订单：`CR202604070002`

### 演示前适合 polish 的页面

1. `/courier/workbench`
2. `/user/campus/order-result`
3. `/user/campus/courier-onboarding`

### 当前不要动的页面或能力

1. 不动 bridge 接口。
2. 不动 `request.js` token 附着逻辑。
3. 不动旧外卖个人中心主语义。
4. 不新增第五个 admin 页。
5. 不做 admin 后台框架重构。

## 非 bridge 后端能力梳理

### 值得后续评估

1. 售后执行历史审计。
   - 价值：从“当前执行结果 + 一次纠正”升级为可追踪的处理过程。
   - 风险：需要新历史表，当前不适合在展示 polish 前做。
2. 异常上报历史与处理结果。
   - 价值：从“最新一次异常”升级为更完整运营审计。
   - 风险：会影响订单异常模型与 admin 运营页。
3. settlement 批次复核、撤回和对账。
   - 价值：更贴近运营后台。
   - 风险：容易滑向真实财务系统，需继续保持模拟与只读边界。

### 暂不值得碰

1. 真实支付、真实退款、真实打款。
2. 地图、轨迹回放、实时调度。
3. 完整后台系统重构。
4. 大规模订单状态机调整。
5. 第五个 admin 页，除非后续非 bridge 优先级明确指向它。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 继续保留。
3. `/api/campus/courier/review-status` 继续保留。
4. `customer_token -> bridge -> courier 前置读取` 继续保留观察态。
5. `/courier/workbench` 继续维持现有优先 `courier_token` 的策略。
6. 本轮没有出现恢复推进触发条件，因此不重新打开 bridge 主线。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-33-phase-a-no-op-freeze-and-resume-criteria.md`
5. `project-logs/campus-relay/step-34-non-bridge-direction-and-display-polish-assessment.md`

## 下一轮建议

1. Step 35 建议进入小范围展示级优化执行轮。
2. 第一优先级建议只处理：
   - `frontend/src/views/courier/CourierWorkbench.vue`
   - `frontend/src/views/user/CampusOrderResult.vue`
3. 严格限制为展示 polish：
   - 状态提示
   - 空态 / loading / 错误态文案
   - drawer 分区层级
   - 表格和卡片一致性
4. 不触 bridge、不改鉴权、不新增接口、不新增页面。

## Step 35 执行回填

1. Step 35 已按本轮建议执行，只处理 P0 两个页面：
   - `frontend/src/views/user/CampusOrderResult.vue`
   - `frontend/src/views/courier/CourierWorkbench.vue`
2. Step 35 实际改动保持在展示层：
   - `CampusOrderResult.vue` 增加只读回看标记、三段式引导、初始/loading/错误态层级和结果状态提示层级。
   - `CourierWorkbench.vue` 增加最小承接标记、四段式演示链路、可接单区提示、订单详情状态摘要和基本信息分组说明。
3. Step 35 未改：
   - bridge
   - `request.js`
   - `campus-courier.js`
   - token 附着逻辑
   - API 调用顺序
   - 按钮动作语义
   - 路由
   - 后端代码
4. Step 35 验证：
   - `.\mvnw.cmd -DskipTests compile` 通过
   - `npm run build` 通过
5. Step 35 后续建议：
   - Step 36 先回看两个 P0 页面 polish 效果
   - 如继续展示级优化，不要自动铺开所有页面，可只选 onboarding 或单个 admin 只读页

# Step 40 - 交付整理与演示脚本固化

## 本轮目标

1. 基于 Step 39 已将 bridge 主线和展示 polish 主线都收成冻结态，本轮进入交付整理与演示脚本固化。
2. 把当前项目整理成可演示、可交接、可答辩、可复盘的稳定交付包。
3. 本轮不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由、不新增页面。
4. 本轮不进入非 bridge 后端能力开发，只整理已有成果。

## 为什么 Step 40 转入交付整理

1. 主业务最小闭环已经真实跑通：
   - onboarding
   - admin 审核
   - courier token
   - workbench
   - 接单
   - 取餐
   - deliver
   - 异常上报
   - customer confirm
   - completed 回读
2. bridge 主线已经在 Step 33 进入 `Phase A no-op` 冻结态。
3. 展示 polish 线已经在 Step 39 进入冻结/维护态。
4. 继续补页面或继续 polish 的收益已经低于把现有成果整理成可讲、可交接、可复盘的交付材料。

## 当前交付边界

### 已交付的核心能力

1. customer 侧：
   - campus 订单创建、模拟支付、取消、售后申请、确认送达。
   - courier onboarding 资料提交、资料读取、审核状态查看、token 资格查看、courier token 申请。
   - completed / awaiting confirmation 订单结果回看页。
2. courier 侧：
   - courier profile / review-status 读取。
   - courier token 后的 workbench。
   - available orders 预览。
   - 接单。
   - 订单详情 drawer。
   - 取餐。
   - deliver。
   - 异常上报。
   - completed 结果回读。
3. admin 侧：
   - courier 审核。
   - 订单分页、详情、时间线。
   - 售后处理、售后决策、售后执行记录。
   - 售后执行分页只读运营页。
   - settlement 分页、详情、对账摘要、批次列表、批次详情、打款记录、二次核对。
   - settlement 只读运营页。
   - courier 异常 / 位置联动只读页。
4. 数据与验证：
   - `test profile + H2` 已有可接单样本订单 `CR202604070002`。
   - Step 22 已真实跑通完整本地链路。
   - Step 23 已整理共享回归留痕。

### 当前明确未交付的能力

1. 当前不是最终产品版，也不是完整商业后台。
2. 不包含真实支付、真实退款、真实打款。
3. 不包含完整售后工单系统。
4. 不包含售后执行历史表。
5. 不包含异常历史表和异常处理闭环。
6. 不包含地图、实时轨迹、位置频控和调度台。
7. 不包含 settlement 撤回、复杂复核、财务对接和完整对账系统。
8. 不包含第五个 admin 页面。
9. 不包含对旧外卖模块的替换或删除。

### bridge 为什么冻结

1. repo 外阻塞项已按 owner 事实关闭，但 bridge 仍是兼容入口。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留 `customer/courier` 双 token 兼容。
3. customer onboarding 新入口已经覆盖未拿 courier token 前的主要场景。
4. courier workbench 已覆盖拿到 courier token 后的前台承接。
5. Step 31 / Step 32 连续评估后没有找到高收益、低风险、单提交可回滚的最小收口动作。
6. 因此当前最优策略是 `Phase A no-op` 冻结，而不是为了“收口”强行删 bridge。

### 展示 polish 为什么冻结

1. Step 35 到 Step 38 已覆盖 5 个关键展示面：
   - `CourierOnboarding.vue`
   - `CourierWorkbench.vue`
   - `CampusOrderResult.vue`
   - `CampusSettlementOpsView.vue`
   - `CampusAfterSaleExecutionList.vue`
2. customer / courier 主演示链路和 admin 两个核心只读运营页已经具备可讲解、可演示、可交接的展示质量。
3. 继续 polish 剩余页面的收益已经下降，容易变成机械美化。
4. 后续只有出现真实演示反馈、明显展示缺陷或交付材料要求时，才重新打开展示 polish。

### 后续候选但不属于当前交付范围

1. 售后执行历史表。
2. 异常历史表和异常处理结果。
3. settlement 复杂对账、撤回和真实打款。
4. 位置轨迹聚合、地图和调度视图。
5. 更完整的 admin 后台布局和第五个 admin 页。
6. 旧外卖主链路迁移或替换。

## 主演示脚本

### 0. 演示前准备

1. 后端建议使用 `test profile + H2`。
2. H2 重启后 `CR202604070002` 会回到 `WAITING_ACCEPT`，适合重新演示完整链路。
3. 前端使用现有 Vite 页面入口。
4. 演示前准备账号：
   - customer / courier candidate：`13900139000 / 123456`
   - customer 订单所属账号：`13900139001 / 123456`
   - admin：`13800138000 / 123456`

### 1. customer onboarding 提交资料

1. 演示入口页面：`/user/campus/courier-onboarding`
2. 使用账号：`13900139000 / 123456`
3. 操作动作：填写或确认 courier 入驻资料并提交。
4. 预期结果：资料进入待审核状态，页面显示审核状态和 token 资格提示。
5. 重点讲解：
   - 这是 customer 未拿 courier token 前的稳定 onboarding 入口。
   - 资料提交仍落到 `campus_courier_profile`，没有新建第二套资料表。
6. 演示类型：动作演示。

### 2. admin 审核

1. 演示入口页面：admin 侧 courier 审核相关入口或接口能力。
2. 使用账号：`13800138000 / 123456`
3. 操作动作：审核通过 courier profile。
4. 预期结果：courier profile 进入 `APPROVED` 且 `enabled = 1`。
5. 重点讲解：
   - 管理员复用 `employee`。
   - courier 作为独立 `CourierProfile`，通过 `userId` 关联普通用户。
6. 演示类型：动作演示。

### 3. customer 申请 courier token

1. 演示入口页面：`/user/campus/courier-onboarding`
2. 使用账号：`13900139000 / 123456`
3. 操作动作：在 token 资格满足后点击申请 courier token。
4. 预期结果：页面显示 token 申请成功，并可进入 courier workbench。
5. 重点讲解：
   - onboarding 只负责申请资格与 token 衔接。
   - 真正 courier 业务动作由 courier token 路径承接。
6. 演示类型：动作演示。

### 4. courier workbench 加载

1. 演示入口页面：`/courier/workbench`
2. 使用账号：使用第 3 步拿到的 courier token。
3. 操作动作：进入 workbench，加载 profile / review-status / available orders。
4. 预期结果：页面展示 courier 身份状态和可接单列表。
5. 重点讲解：
   - workbench 是 courier token 获取后的最小承接页。
   - 无 courier token 时不会调用 courier 业务接口。
6. 演示类型：只读演示。

### 5. 接单

1. 演示入口页面：`/courier/workbench`
2. 使用账号：courier token。
3. 操作动作：对订单 `CR202604070002` 点击接单。
4. 预期结果：订单从 `WAITING_ACCEPT` 推进到 `ACCEPTED`，可进入详情 drawer。
5. 重点讲解：
   - 只补最小接单动作。
   - 不扩完整 courier 页面群。
6. 演示类型：动作演示。

### 6. 取餐

1. 演示入口页面：`/courier/workbench` 订单详情 drawer。
2. 使用账号：courier token。
3. 操作动作：在详情 drawer 中执行取餐。
4. 预期结果：订单进入 `PICKED_UP`。
5. 重点讲解：
   - pickup 调用现有后端接口。
   - 本轮没有新增写接口，没有改状态机。
6. 演示类型：动作演示。

### 7. deliver

1. 演示入口页面：`/courier/workbench` 订单详情 drawer。
2. 使用账号：courier token。
3. 操作动作：执行 deliver。
4. 预期结果：订单可进入 `DELIVERING`，再进入 `AWAITING_CONFIRMATION`。
5. 重点讲解：
   - 送达后进入等待用户确认状态。
   - 页面有 confirm 前状态可视化。
6. 演示类型：动作演示。

### 8. 异常上报

1. 演示入口页面：`/courier/workbench` 订单详情 drawer。
2. 使用账号：courier token。
3. 操作动作：填写异常类型和备注并提交异常上报。
4. 预期结果：订单记录最新一次异常信息。
5. 重点讲解：
   - 当前只保留最新一次异常。
   - 没有异常历史表，这是当前交付边界。
6. 演示类型：动作演示。

### 9. customer confirm

1. 演示入口页面：customer 订单确认能力。
2. 使用账号：`13900139001 / 123456`
3. 操作动作：确认 `CR202604070002` 已送达。
4. 预期结果：订单进入 `COMPLETED`。
5. 重点讲解：
   - customer confirm 是主链路闭环终点。
   - settlement 会在 completed 后自动生成或更新。
6. 演示类型：动作演示。

### 10. completed 回读

1. 演示入口页面：`/courier/workbench`
2. 使用账号：courier token。
3. 操作动作：通过订单号查看 `CR202604070002` 详情。
4. 预期结果：可回读 `COMPLETED` 状态。
5. 重点讲解：
   - 接单、取餐、deliver、confirm 后仍可回读结果。
   - workbench 不只是动作入口，也能承接完成后只读结果。
6. 演示类型：只读演示。

### 11. customer 结果页

1. 演示入口页面：`/user/campus/order-result?orderId=CR202604070002`
2. 使用账号：`13900139001 / 123456`
3. 操作动作：打开结果回看页并查询订单。
4. 预期结果：页面展示 `COMPLETED` 结果摘要。
5. 重点讲解：
   - customer 侧可以按订单号回看完成结果。
   - 这是只读结果回看，不新增写动作。
6. 演示类型：只读演示。

### 12. admin settlement 只读页

1. 演示入口页面：`/campus/settlements`
2. 使用账号：`13800138000 / 123456`
3. 操作动作：查看对账摘要、结算列表和详情 drawer。
4. 预期结果：可查看 settlement 只读运营数据。
5. 重点讲解：
   - 本页只读，不提供确认结算、打款、撤回或核对写操作。
   - 适合展示 completed 后的后台结算回看。
6. 演示类型：只读演示。

### 13. admin after-sale 只读页

1. 演示入口页面：`/campus/after-sale-executions`
2. 使用账号：`13800138000 / 123456`
3. 操作动作：查看售后执行分页、筛选条件和详情 drawer。
4. 预期结果：如果已有售后执行记录，可查看执行状态、决策类型、人工纠正和 after-sale-result 详情；如果 H2 当前没有足够售后样本，则展示只读页结构和空态。
5. 重点讲解：
   - 本页是只读运营页。
   - 当前 H2 稳定主链路样本以 completed / settlement 为主，after-sale 样本不足是已知交付边界，不应脑补数据。
6. 演示类型：只读演示。

## 演示账号与样本数据索引

### 账号

1. customer / courier candidate：
   - 手机号：`13900139000`
   - 密码：`123456`
   - 用途：onboarding、token eligibility、courier token 申请。
2. customer 订单所属账号：
   - 手机号：`13900139001`
   - 密码：`123456`
   - 用途：订单 `CR202604070002` 所属 customer、customer confirm、customer completed 结果回看。
3. admin：
   - 手机号：`13800138000`
   - 密码：`123456`
   - 用途：courier 审核、admin 只读运营页演示。

### 样本订单

1. 完整主链路样本：
   - 订单号：`CR202604070002`
   - H2 初始状态：`WAITING_ACCEPT`
   - 适合演示：接单、取餐、deliver、异常上报、customer confirm、completed 回读。
2. completed 回读样本：
   - 订单号：`CR202604060001`
   - H2 初始状态：`COMPLETED`
   - 适合演示：completed 只读回读、settlement 记录查看。
3. settlement 样本：
   - 订单号：`CR202604060001`
   - 关联 settlement record：`id = 1`
   - 初始状态：`PENDING`
   - 适合演示：settlement 只读运营页和详情 drawer。
4. after-sale 样本：
   - 当前 H2 稳定种子里没有单独准备足够完整的 after-sale 执行样本。
   - 如果要完整演示 after-sale 数据，应先通过现有售后申请、admin 决策和执行记录能力准备样本。
   - 当前交付中 after-sale 页面可演示页面结构、筛选、空态和已有记录读取能力，但不伪造固定样本。

## 演示页面清单

### customer 页面

1. `/user/campus/courier-onboarding`
   - 用途：资料提交、审核状态、token 资格、courier token 申请。
2. `/user/campus/order-result?orderId=CR202604070002`
   - 用途：customer completed / awaiting confirmation 结果回看。
3. `/user/campus/after-sale-result`
   - 用途：customer 售后结果回执查询。

### courier 页面

1. `/courier/workbench`
   - 用途：courier token 后的最小承接、可接单列表、接单、取餐、deliver、异常上报、详情回读。

### admin 页面

1. `/campus/settlement-batches`
   - 用途：settlement 批次列表只读演示。
2. `/campus/settlement-batches/:batchNo`
   - 用途：settlement 批次详情只读演示。
3. `/campus/after-sale-executions`
   - 用途：售后执行分页、纠正审计和 after-sale-result drawer。
4. `/campus/courier-ops`
   - 用途：courier 最近异常和低频位置记录联动查看。
5. `/campus/settlements`
   - 用途：settlement 对账摘要、列表、详情 drawer。

### 只读运营页

1. settlement 批次页。
2. 售后执行页。
3. courier 异常 / 位置联动页。
4. settlement 只读运营页。

### 当前不建议展示的内容

1. 不建议把旧外卖主链路作为 campus 主演示重点。
2. 不建议展示“第五个 admin 页”，因为当前没有必要为页数补页面。
3. 不建议展示未准备固定样本的 after-sale 完整数据闭环，除非演示前已先准备样本。
4. 不建议展示 bridge 收口动作，因为 bridge 当前是冻结保留状态。

### 当前仍冻结的主线

1. bridge 主线：`Phase A no-op` 冻结态。
2. 展示 polish 线：冻结/维护态。

## 风险与答辩口径

### 为什么 bridge 还保留

bridge 仍承担兼容入口作用。虽然 customer onboarding 已覆盖未拿 courier token 前的主要场景，courier workbench 已覆盖拿到 token 后的承接，但当前没有高收益、低风险、可回滚的删除动作，因此保留更稳。

### 为什么不继续删 bridge

Step 31 / Step 32 已评估最小收口候选，结论是收益不足或风险偏高。删除 bridge 不是当前交付目标，当前目标是稳定闭环、可演示和可交接。

### 为什么前端不继续全面 polish

Step 35 到 Step 38 已覆盖 5 个关键展示面。继续 polish 剩余页面的收益下降，容易变成机械美化。当前更需要交付整理，而不是无限美化页面。

### 为什么没有第 5 个 admin 页

当前已有 4 个 admin 只读运营页，覆盖 settlement 批次、售后执行、courier 异常/位置和 settlement 只读运营。继续补第 5 页缺少明确业务收益，不应以“页数”为目标。

### 为什么当前先交付稳定闭环而不是继续扩能力

当前项目已经具备最小闭环和可演示链路。继续扩能力会引入新的状态、测试和文档成本。先稳定交付可以避免项目持续扩散。

### 当前明确未完成项

1. 真实支付、真实退款、真实打款。
2. 售后执行历史表。
3. 异常历史表。
4. 地图、轨迹和调度台。
5. settlement 复杂对账和撤回。
6. 完整后台系统。

### 如果继续开发，下一优先方向

1. 若面向答辩：优先补截图、录屏脚本和演示 checklist。
2. 若面向业务能力：优先梳理非 bridge 后端能力，例如售后历史、异常历史或 settlement 复核。
3. 若面向前端体验：只有在真实演示反馈指出缺陷后，再重开展示 polish。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. bridge 完全保留。
3. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
4. 本轮最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-39-display-polish-review-and-freeze-decision.md`
5. `project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md`

## 下一轮建议

1. Step 41 若继续推进，优先补交付材料缺口：
   - 截图清单
   - 录屏顺序
   - 演示前检查 checklist
2. 如果交付材料已足够，再评估是否进入非 bridge 后端能力梳理。
3. bridge 与展示 polish 继续保持双冻结。
4. 第五个 admin 页继续后置，不以补页数为目标。

## Step 41 执行回填

1. Step 41 已按本建议继续补交付材料缺口。
2. 本轮没有进入非 bridge 后端能力开发，也没有继续 polish 页面。
3. 本轮新增 `step-41-delivery-assets-checklist-and-recording-plan.md`，集中整理：
   - 截图清单。
   - 录屏顺序。
   - 演示前检查 checklist。
4. 截图清单覆盖 customer onboarding、token 资格、courier workbench、可接单列表、订单详情 drawer、取餐 / deliver / 异常上报、customer completed 结果页、settlement 只读运营页和 after-sale 只读运营页。
5. 录屏顺序建议拆为 4 个短片段：
   - customer onboarding 与 token 申请。
   - courier workbench 动作链路。
   - customer confirm 与 completed 结果回读。
   - admin 只读运营页。
6. 演示前检查 checklist 覆盖环境、账号、数据、页面和失败处理。
7. 当前交付材料已具备“可截图、可录屏、可检查”的执行基础；真实截图和录屏文件仍需按清单实际采集。
8. Step 42 建议：
   - 若需要答辩归档，优先按 Step 41 清单采集真实截图和录屏，并记录文件位置。
   - 若不需要立即采集媒体，可评估是否进入非 bridge 后端能力梳理。

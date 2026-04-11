# Step 41 - 交付材料补完：截图清单、录屏顺序、演示前检查

## 本轮目标

1. 基于 Step 40 已完成的交付整理与演示脚本固化，继续补齐实际展示前需要的交付材料。
2. 本轮只整理三类内容：
   - 截图清单
   - 录屏顺序
   - 演示前检查 checklist
3. 本轮不进入非 bridge 后端能力梳理。
4. 本轮不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由、不新增页面。

## 为什么 Step 41 继续停留在交付材料补完

1. Step 40 已经把“讲什么”和“按什么链路演示”整理清楚。
2. 但如果要真正答辩、录屏或交接，还需要把“截哪些图、按什么顺序录、开始前检查什么”写成可执行清单。
3. 当前 bridge 主线已经是 `Phase A no-op` 冻结态，展示 polish 线也已经是冻结/维护态。
4. 因此本轮继续补交付材料，而不是重新开发功能、继续 polish 页面或补第五个 admin 页。

## 截图清单

### 1. customer onboarding 首页

1. 截图名称：`01-customer-onboarding-entry`
2. 页面入口：`/user/campus/courier-onboarding`
3. 使用账号：`13900139000 / 123456`
4. 前置状态：customer 已登录；如需展示资料提交前状态，可重置 H2 或使用未提交资料账号。
5. 核心区域：顶部说明区、三段式流程说明、资料表单入口。
6. 截图目的：说明 customer 未拿 courier token 前的稳定前置入口。
7. 是否必须截图：必须。

### 2. 审核状态与 token 资格

1. 截图名称：`02-onboarding-review-status-token-eligibility`
2. 页面入口：`/user/campus/courier-onboarding`
3. 使用账号：`13900139000 / 123456`
4. 前置状态：courier profile 已提交；根据演示需要可准备 `PENDING` 或 `APPROVED + enabled = 1`。
5. 核心区域：审核状态卡片、token 资格提示、token 申请区。
6. 截图目的：说明 onboarding 页面如何承接审核状态与 token 申请资格。
7. 是否必须截图：必须。

### 3. courier token 申请成功

1. 截图名称：`03-courier-token-issued`
2. 页面入口：`/user/campus/courier-onboarding`
3. 使用账号：`13900139000 / 123456`
4. 前置状态：`reviewStatus = APPROVED` 且 `enabled = 1`。
5. 核心区域：token 申请成功提示、token 或成功状态展示、进入 workbench 的提示。
6. 截图目的：证明 customer onboarding 已能衔接 courier token 申请。
7. 是否必须截图：必须。

### 4. courier workbench 首页

1. 截图名称：`04-courier-workbench-home`
2. 页面入口：`/courier/workbench`
3. 使用账号：第 3 步申请到的 courier token。
4. 前置状态：浏览器已保存 `courier_token`。
5. 核心区域：token 状态区、profile / review-status 摘要、workbench 演示链路说明。
6. 截图目的：证明拿到 courier token 后有最小前台承接页。
7. 是否必须截图：必须。

### 5. 可接单列表

1. 截图名称：`05-available-order-list`
2. 页面入口：`/courier/workbench`
3. 使用账号：courier token。
4. 前置状态：H2 中订单 `CR202604070002` 处于 `WAITING_ACCEPT`。
5. 核心区域：可接单表格、订单号、取货点、配送楼栋、奖励金额、接单按钮。
6. 截图目的：证明 workbench 能真实读取可接单数据。
7. 是否必须截图：必须。

### 6. 订单详情 drawer

1. 截图名称：`06-courier-order-detail-drawer`
2. 页面入口：`/courier/workbench`
3. 使用账号：courier token。
4. 前置状态：订单已接单或通过订单号回读详情。
5. 核心区域：订单基本信息、当前状态摘要、动作区、只读字段来源说明。
6. 截图目的：说明接单后不止能看列表，还能进入后续动作承接点。
7. 是否必须截图：必须。

### 7. 取餐关键状态

1. 截图名称：`07-pickup-state`
2. 页面入口：`/courier/workbench` 详情 drawer。
3. 使用账号：courier token。
4. 前置状态：订单处于 `ACCEPTED`，执行取餐后进入 `PICKED_UP`。
5. 核心区域：订单状态 tag、取餐相关动作区、更新时间字段。
6. 截图目的：证明接单后可以继续推进到取餐。
7. 是否必须截图：建议必须。

### 8. deliver / 等待确认状态

1. 截图名称：`08-deliver-awaiting-confirmation`
2. 页面入口：`/courier/workbench` 详情 drawer。
3. 使用账号：courier token。
4. 前置状态：订单已取餐并执行 deliver，进入 `AWAITING_CONFIRMATION`。
5. 核心区域：等待用户确认提示区、送达时间、状态摘要。
6. 截图目的：证明送达后有 confirm 前可视化承接。
7. 是否必须截图：必须。

### 9. 异常上报结果

1. 截图名称：`09-exception-report-result`
2. 页面入口：`/courier/workbench` 详情 drawer。
3. 使用账号：courier token。
4. 前置状态：订单在配送中或送达后可按当前状态执行异常上报。
5. 核心区域：异常类型、异常备注、最新异常信息只读展示。
6. 截图目的：说明当前异常模型只保留最新一次异常，并能在 workbench 中回读。
7. 是否必须截图：建议必须。

### 10. customer confirm

1. 截图名称：`10-customer-confirm`
2. 页面入口：customer 订单确认送达入口。
3. 使用账号：`13900139001 / 123456`
4. 前置状态：订单 `CR202604070002` 处于 `AWAITING_CONFIRMATION`。
5. 核心区域：确认送达操作入口、确认后状态变化提示。
6. 截图目的：证明 customer 侧完成主链路闭环。
7. 是否必须截图：建议必须；如果确认入口不适合截图，可用 completed 回读页替代。

### 11. customer completed 结果页

1. 截图名称：`11-customer-completed-result`
2. 页面入口：`/user/campus/order-result?orderId=CR202604070002`
3. 使用账号：`13900139001 / 123456`
4. 前置状态：订单已 `COMPLETED`。
5. 核心区域：订单号、当前状态、送达/完成时间、配送结果摘要、异常摘要。
6. 截图目的：证明 customer confirm 后可以回看 completed 结果。
7. 是否必须截图：必须。

### 12. settlement 只读运营页

1. 截图名称：`12-admin-settlement-ops`
2. 页面入口：`/campus/settlements`
3. 使用账号：`13800138000 / 123456`
4. 前置状态：存在 settlement 样本，建议使用 `CR202604060001` 或完成后的 `CR202604070002`。
5. 核心区域：对账摘要卡片、筛选区、列表、详情 drawer。
6. 截图目的：展示 completed 后的后台结算只读运营能力。
7. 是否必须截图：必须。

### 13. after-sale 只读运营页

1. 截图名称：`13-admin-after-sale-execution`
2. 页面入口：`/campus/after-sale-executions`
3. 使用账号：`13800138000 / 123456`
4. 前置状态：若 H2 中没有足够售后执行样本，允许截图空态、筛选区和只读说明。
5. 核心区域：页面导览、筛选区、表格只读提示、详情 drawer 或空态。
6. 截图目的：展示 admin 售后执行只读运营页结构，并明确 after-sale 固定样本缺口。
7. 是否必须截图：必须。

### 14. bridge / polish 冻结说明页

1. 截图名称：`14-doc-bridge-display-freeze`
2. 页面入口：`project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md`
3. 使用账号：不需要。
4. 前置状态：打开文档。
5. 核心区域：bridge 冻结原因、展示 polish 冻结原因、当前交付边界。
6. 截图目的：答辩或交接时解释“为什么不继续删 bridge、不继续无限 polish”。
7. 是否必须截图：可选。

## 录屏顺序

### 推荐拆分方式

1. 不建议一镜到底。
2. 建议拆为 4 个短片段：
   - 片段 A：customer onboarding 与 token 申请。
   - 片段 B：courier workbench 动作链路。
   - 片段 C：customer confirm 与 completed 结果回读。
   - 片段 D：admin 只读运营页。
3. 这样可以降低 H2 状态重置、登录切换和动作失败对整段录屏的影响。

### 片段 A：customer onboarding 与 token 申请

1. 入口：`/user/campus/courier-onboarding`
2. 账号：`13900139000 / 123456`
3. 建议实操录：
   - 打开 onboarding 页面。
   - 展示资料提交区。
   - 展示审核状态和 token 资格。
   - 在已审核通过状态下申请 courier token。
4. 建议讲解：
   - customer 未拿 courier token 前走 onboarding 新入口。
   - token 资格只由审核通过且启用决定。
   - 旧 bridge 保留但不是主演示入口。
5. 建议提前准备：
   - 审核通过的 courier profile。
   - 如果要展示提交动作，可先准备一个可重提或未提交资料账号。

### 片段 B：courier workbench 动作链路

1. 入口：`/courier/workbench`
2. 账号：courier token。
3. 建议实操录：
   - workbench 首屏加载 profile / review-status。
   - 可接单列表出现 `CR202604070002`。
   - 接单。
   - 打开订单详情 drawer。
   - 取餐。
   - deliver。
   - 异常上报。
4. 建议讲解：
   - workbench 是 courier token 后的最小承接页。
   - 本页覆盖接单、详情、取餐、deliver、异常上报和回读。
   - 当前异常模型只保留最新一次异常。
5. 建议提前准备：
   - H2 重置后订单 `CR202604070002` 处于 `WAITING_ACCEPT`。
   - 浏览器中已有有效 `courier_token`。
   - 如录屏中动作失败，应停止重置数据，不建议临场修状态。

### 片段 C：customer confirm 与 completed 结果回读

1. 入口：
   - customer confirm 入口。
   - `/user/campus/order-result?orderId=CR202604070002`
2. 账号：`13900139001 / 123456`
3. 建议实操录：
   - customer 确认送达。
   - 打开结果页。
   - 展示 `COMPLETED` 结果摘要。
4. 建议只读展示：
   - 如果确认操作已经在上一轮数据中完成，可直接打开结果页展示 completed 回读。
5. 建议讲解：
   - customer confirm 是主链路闭环终点。
   - completed 后 customer 与 courier 都能回读结果。
6. 建议提前准备：
   - 如果要录确认动作，确保订单处于 `AWAITING_CONFIRMATION`。
   - 如果只录结果页，确保订单已 `COMPLETED`。

### 片段 D：admin 只读运营页

1. 入口：
   - `/campus/settlements`
   - `/campus/after-sale-executions`
2. 账号：`13800138000 / 123456`
3. 建议实操录：
   - 打开 settlement 只读运营页。
   - 展示对账摘要、筛选区、列表和详情 drawer。
   - 打开 after-sale 只读执行页。
   - 展示筛选区、只读说明、表格或空态、详情 drawer。
4. 建议讲解：
   - settlement 页承接 completed 订单后的后台结算回看。
   - after-sale 页展示售后执行只读运营视角；若无固定样本，应说明当前 H2 稳定样本缺口。
5. 建议提前准备：
   - settlement 样本建议使用 `CR202604060001` 或完成后的 `CR202604070002`。
   - after-sale 若要展示真实记录，应演示前先通过售后申请和 admin 决策准备样本。

## 演示前检查 checklist

### 环境检查

| 检查项 | 建议检查方式 | 必须通过 | 失败处理 |
| --- | --- | --- | --- |
| 后端是否启动 | 访问后端健康接口或打开任一 campus API | 是 | 停止演示，先启动后端 |
| 后端 profile 是否正确 | 确认使用 `test profile + H2` | 是 | 切换 profile 后重启 |
| H2 是否处于合适初始状态 | 确认 `CR202604070002` 初始为 `WAITING_ACCEPT` | 是 | 重启 test 环境或重置 H2 |
| 前端是否启动 | 打开 Vite 本地页面 | 是 | 启动前端 |
| 浏览器缩放 | 建议 90% 到 100% | 否 | 录屏前调整即可 |
| 录屏分辨率 | 建议 1920x1080 或固定窗口尺寸 | 否 | 录屏前调整即可 |

### 账号检查

| 检查项 | 建议检查方式 | 必须通过 | 失败处理 |
| --- | --- | --- | --- |
| customer / courier candidate 可登录 | `13900139000 / 123456` 登录 | 是 | 检查 H2 seed 或登录状态 |
| customer 订单所属账号可登录 | `13900139001 / 123456` 登录 | 是 | 检查 H2 seed 或登录状态 |
| admin 可登录 | `13800138000 / 123456` 登录 | 是 | 检查 employee seed |
| customer token 正常 | 打开 customer onboarding 或结果页 | 是 | 重新登录 customer |
| courier token 正常 | 打开 `/courier/workbench` | 是 | 重新申请 token 或清理旧 token |
| admin token 正常 | 打开 `/campus/settlements` | 是 | 重新登录 admin |

### 数据检查

| 检查项 | 建议检查方式 | 必须通过 | 失败处理 |
| --- | --- | --- | --- |
| `CR202604070002` 存在 | workbench 可接单列表或接口回读 | 是 | 重置 H2 |
| `CR202604070002` 可接单 | 状态为 `WAITING_ACCEPT` | 是 | 重置 H2，不建议手工改状态 |
| `CR202604060001` 可用于 completed 回读 | customer result 或 settlement 页回读 | 建议 | 如缺失，改用已完成的 `CR202604070002` |
| settlement 样本存在 | `/campus/settlements` 列表可见 | 是 | 完成订单后刷新 settlement |
| after-sale 样本是否存在 | `/campus/after-sale-executions` 列表检查 | 否 | 若没有样本，按空态和页面结构讲解 |

### 页面检查

| 页面 | 入口 | 必须通过 | 失败处理 |
| --- | --- | --- | --- |
| customer onboarding | `/user/campus/courier-onboarding` | 是 | 不继续录片段 A |
| courier workbench | `/courier/workbench` | 是 | 不继续录片段 B |
| customer result | `/user/campus/order-result?orderId=CR202604070002` | 是 | 先确认订单是否已完成 |
| settlement 只读运营页 | `/campus/settlements` | 是 | 检查 admin token |
| after-sale 只读运营页 | `/campus/after-sale-executions` | 建议 | 若无数据可按空态讲解 |

### 演示失败处理

| 场景 | 是否可跳过 | 处理口径 |
| --- | --- | --- |
| onboarding 页面打不开 | 否 | 停止演示，修复前端或 token |
| token 申请失败 | 否 | 停止演示，检查审核状态和 enabled |
| workbench 无法加载 | 否 | 停止演示，检查 courier token |
| 可接单列表为空 | 否 | 重置 H2 或检查 `CR202604070002` 状态 |
| 接单失败 | 否 | 停止演示，重置数据后重录片段 B |
| 取餐或 deliver 失败 | 否 | 停止演示，检查订单状态是否被提前推进 |
| 异常上报失败 | 可跳过 | 主链路仍可完成，但需说明异常上报未录成功 |
| customer confirm 失败 | 否 | 停止演示，检查订单是否 `AWAITING_CONFIRMATION` |
| after-sale 页面无样本 | 可跳过数据 | 展示页面结构和空态，并说明固定样本缺口 |
| settlement 页面无样本 | 否 | 完成订单后刷新或检查 seed |

## 当前交付材料是否已经足够

结论：当前已具备可截图、可录屏、可检查的交付材料。

说明：

1. Step 40 已固化交付边界、主演示脚本、账号与样本数据、页面清单和答辩口径。
2. Step 41 已补齐截图清单、录屏顺序和演示前检查 checklist。
3. 当前仍未产出真实截图文件和录屏文件，但已有可执行清单，可以直接按清单采集。
4. 如果下一轮仍留在交付整理，可优先做真实截图/录屏采集与文件归档；如果不采集媒体，则可以评估进入非 bridge 后端能力梳理。

## Step 42 真实采集回填

Step 42 已按本清单完成一版真实媒体采集，并归档到：

`project-logs/campus-relay/runtime/step-42-media/`

采集结果：

1. 截图：
   - 主交付截图 13 张。
   - 异常后 customer confirm 被拒绝的失败留痕 2 张。
2. 录屏：
   - 主交付录屏 4 段。
   - 异常后 customer confirm 被拒绝的失败留痕 1 段。
3. 已覆盖：
   - customer onboarding。
   - token 资格与 token 申请。
   - courier workbench。
   - 可接单、详情、取餐、deliver、异常上报。
   - customer confirm 前结果页与 completed 回读。
   - admin settlement 与 after-sale 只读运营页。

非阻塞缺口：

1. after-sale 固定真实样本仍不是本轮新增重点；当前已采集页面结构与只读运营页展示，若答辩要求真实售后执行记录，需要演示前单独准备样本。
2. customer confirm 动作通过真实接口完成并录屏记录，当前没有新增独立 customer confirm UI 按钮截图。

详细媒体索引见：

`project-logs/campus-relay/step-42-real-media-capture-and-archive.md`

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有改 bridge、没有改鉴权、没有改 token 附着逻辑。
4. 当前最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md`
5. `project-logs/campus-relay/step-41-delivery-assets-checklist-and-recording-plan.md`

## 下一轮建议

1. 如果要直接答辩或交接，Step 42 可以按本清单采集真实截图和录屏，并归档文件位置。
2. 如果不需要立刻采集媒体，Step 42 可以评估是否进入非 bridge 后端能力梳理。
3. bridge 主线继续冻结。
4. 展示 polish 线继续冻结。
5. 第五个 admin 页继续后置，不以补页数为目标。

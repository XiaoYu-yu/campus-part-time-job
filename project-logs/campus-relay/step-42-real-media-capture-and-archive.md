# Step 42 - 真实媒体采集与归档

## 本轮目标

1. 基于 Step 41 的截图清单、录屏顺序和演示前检查 checklist，采集一版真实页面截图和录屏片段。
2. 形成可交接、可答辩、可复查的媒体文件索引。
3. 只做媒体采集与归档，不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由、不新增页面。

## 为什么 Step 42 进入真实媒体采集

Step 40 已完成交付边界、主演示脚本、账号与样本数据索引、页面展示清单和答辩口径。Step 41 已把截图、录屏和演示前检查项整理成可执行清单。

因此本轮不再继续写功能或 polish 页面，而是把已有可演示链路落成真实文件，便于答辩、录屏和后续交接。

## 采集环境

| 项目 | 实际值 |
| --- | --- |
| 后端 | `test profile + H2`，本地 `http://localhost:8080` |
| 前端 | Vite，本地 `http://127.0.0.1:5173` |
| 截图目录 | `project-logs/campus-relay/runtime/step-42-media/screenshots/` |
| 录屏目录 | `project-logs/campus-relay/runtime/step-42-media/videos/` |
| 运行日志目录 | `project-logs/campus-relay/runtime/step-42-media/logs/` |
| 录屏工具 | Playwright video，执行前通过 `npx playwright install ffmpeg` 补齐本机 ffmpeg 依赖 |

说明：

1. `npx playwright install ffmpeg` 只安装本机 Playwright 录屏依赖，没有修改仓库业务代码。
2. 采集中途发现 `CR202604070002` 在异常上报后不能再 customer confirm，后端返回 `当前订单状态不可确认送达`；该失败被保留为真实留痕。
3. 为采集 confirm 成功与 completed 回读，本轮重启了本地 test/H2 后端，重新按真实接口把 `CR202604070002` 推进到 `AWAITING_CONFIRMATION` 后完成 customer confirm。

## 使用账号与样本数据

| 用途 | 账号 / 数据 |
| --- | --- |
| customer / courier candidate | `13900139000 / 123456` |
| customer 订单所属账号 | `13900139001 / 123456` |
| admin | `13800138000 / 123456` |
| 主链路订单 | `CR202604070002` |
| completed / settlement 固定回读样本 | `CR202604060001` |

## 已采集截图

| 文件名 | 页面入口 | 使用账号 | 前置状态 | 核心区域 | 目的 | 必须项 |
| --- | --- | --- | --- | --- | --- | --- |
| `01-customer-onboarding-entry.png` | `/user/campus/courier-onboarding` | `13900139000` | customer 已登录 | onboarding 首页说明与资料入口 | 展示 customer 到 courier 前置入口 | 是 |
| `02-onboarding-review-status-token-eligibility.png` | `/user/campus/courier-onboarding` | `13900139000` | profile 已 admin 审核通过 | 审核状态与 token 资格 | 展示 token 申请前置判断 | 是 |
| `03-courier-token-issued.png` | `/user/campus/courier-onboarding` | `13900139000` | 输入当前账号密码并申请 token | token 申请成功区 | 展示 courier_token 写入闭环 | 是 |
| `04-courier-workbench-home.png` | `/courier/workbench` | `13900139000` courier token | courier_token 已存在 | workbench 身份状态与说明 | 展示 token 后承接页 | 是 |
| `05-available-order-list.png` | `/courier/workbench` | `13900139000` courier token | `CR202604070002 = WAITING_ACCEPT` | 可接单列表 | 展示可接单预览 | 是 |
| `06-courier-order-detail-drawer.png` | `/courier/workbench` | `13900139000` courier token | 已接单 | 订单详情 drawer | 展示接单后详情承接 | 是 |
| `07-pickup-state.png` | `/courier/workbench` | `13900139000` courier token | 已确认取餐 | 取餐后状态与动作区 | 展示 pickup 承接 | 是 |
| `08-deliver-awaiting-confirmation.png` | `/courier/workbench` | `13900139000` courier token | 已送达，等待确认 | 送达后状态区 | 展示 awaiting confirmation | 是 |
| `09-exception-report-result.png` | `/courier/workbench` | `13900139000` courier token | 已送达后上报异常 | 异常摘要与上报结果 | 展示异常上报承接 | 是 |
| `10-customer-confirm-awaiting.png` | `/user/campus/order-result?orderId=CR202604070002` | `13900139001` | 订单等待 customer confirm | customer 等待确认结果页 | 展示确认前结果回看 | 是 |
| `11-customer-completed-result.png` | `/user/campus/order-result?orderId=CR202604070002` | `13900139001` | customer confirm 成功后 | completed 结果页 | 展示完成后回读 | 是 |
| `12-admin-settlement-ops.png` | `/campus/settlements` | `13800138000` | admin 已登录 | settlement 只读运营页 | 展示结算运营页 | 是 |
| `13-admin-after-sale-execution.png` | `/campus/after-sale-executions` | `13800138000` | admin 已登录 | 售后执行只读页 | 展示售后执行运营页 | 是 |
| `10a-customer-confirm-blocked-after-exception.png` | `/user/campus/order-result?orderId=CR202604070002` | `13900139001` | 异常上报后尝试 confirm | confirm 被拒绝前页面 | 保留异常后无法 confirm 的真实留痕 | 否 |
| `11a-customer-result-after-failed-confirm.png` | `/user/campus/order-result?orderId=CR202604070002` | `13900139001` | confirm 被拒绝后刷新 | 失败后结果页 | 保留失败场景回看 | 否 |

截图保存目录：

`project-logs/campus-relay/runtime/step-42-media/screenshots/`

## 已采集录屏

| 文件名 | 内容 | 是否完整成功 | 保存路径 | 说明 |
| --- | --- | --- | --- | --- |
| `01-onboarding-token.webm` | customer onboarding、审核状态、token 资格与 token 申请成功 | 是 | `project-logs/campus-relay/runtime/step-42-media/videos/01-onboarding-token.webm` | 动作演示 |
| `02-courier-workbench-actions.webm` | workbench 加载、接单、详情、取餐、deliver、异常上报 | 是 | `project-logs/campus-relay/runtime/step-42-media/videos/02-courier-workbench-actions.webm` | 动作演示 |
| `03-customer-confirm-completed-readback.webm` | customer confirm 前结果页、真实 confirm 接口、completed 回读 | 是 | `project-logs/campus-relay/runtime/step-42-media/videos/03-customer-confirm-completed-readback.webm` | 动作 + 只读回看演示 |
| `04-admin-readonly-ops.webm` | settlement 只读运营页与 after-sale 只读运营页 | 是 | `project-logs/campus-relay/runtime/step-42-media/videos/04-admin-readonly-ops.webm` | 只读运营演示 |
| `03a-customer-confirm-blocked-after-exception.webm` | 异常上报后 customer confirm 被后端拒绝 | 失败留痕 | `project-logs/campus-relay/runtime/step-42-media/videos/03a-customer-confirm-blocked-after-exception.webm` | 证明未伪造 confirm 结果 |

录屏保存目录：

`project-logs/campus-relay/runtime/step-42-media/videos/`

## 媒体缺失项

| 缺失项 | 缺失原因 | 是否影响答辩 / 交接 |
| --- | --- | --- |
| 独立的 customer confirm 按钮 UI 截图 | 当前 customer completed 结果页是只读回看页，confirm 操作通过真实 `POST /api/campus/customer/orders/{id}/confirm` 接口完成并被录屏记录，没有新增 UI 动作按钮 | 不影响；主链路 confirm 已真实执行并录屏 |
| after-sale 固定数据样本截图 | 当前 H2 稳定样本仍以页面结构、筛选、只读说明和空态/现有数据为主，未为了媒体采集新增售后样本 | 不阻塞；若答辩要求展示真实售后执行记录，需演示前单独准备售后样本 |
| bridge / polish 冻结说明文档截图 | 本轮优先采集真实业务页面和录屏，文档截图不是主演示必需项 | 不影响 |

## 采集后复核

| 复核项 | 结果 |
| --- | --- |
| customer 账号 | 已使用 `13900139000` 与 `13900139001` |
| courier token | 已通过 `13900139000` 真实申请并写入本地浏览器 |
| admin 账号 | 已使用 `13800138000` |
| 主链路订单 | 已使用 `CR202604070002` |
| completed 回读 | 已采集 `CR202604070002` confirm 后 completed 回读 |
| settlement 回看 | 已采集 `/campus/settlements` |
| after-sale 说明 | 已采集 `/campus/after-sale-executions`，固定真实售后样本仍按缺口说明处理 |
| 是否误录旧外卖页面 | 未发现 |
| 是否误录 bridge 收口内容 | 未录制 bridge 删除或收口动作；bridge 仍冻结 |

## 当前交付材料是否已足够

结论：媒体材料已基本足够用于答辩和交接。

理由：

1. 主链路截图已覆盖 onboarding、token、workbench、接单、详情、取餐、deliver、异常上报、customer confirm、completed 回读。
2. admin 只读运营截图已覆盖 settlement 与 after-sale 两个核心演示页。
3. 录屏已按 Step 41 的 4 段式顺序拆成短片段。
4. 异常后 confirm 被拒绝的真实失败场景已单独保留，没有被伪造成通过。

仍需注意：

1. 如果答辩要求 after-sale 必须展示真实记录，演示前还需要准备一笔固定售后样本。
2. 如果答辩要求全程纯 UI 点击 confirm，需要后续评估 customer 侧 confirm UI，而不是在本轮补写接口或新页面。

## 验证结果

| 验证项 | 结果 | 说明 |
| --- | --- | --- |
| `git diff --check` | 通过 | 仅有 Windows 行尾转换提示，无空白错误 |
| `npm run build` | 通过 | Vite 构建通过；保留既有 Sass `@import` deprecation 与 chunk size warning |
| `.\\mvnw.cmd -DskipTests compile` | 通过 | 后端 compile 成功 |
| 页面采集 | 通过 | customer、courier、admin 页面均能打开并输出真实媒体文件 |
| 业务代码变更 | 无 | 本轮没有修改 Vue、API、后端、路由或鉴权代码 |

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有改 bridge、没有改鉴权、没有改 token 附着逻辑、没有改接口语义。
4. 当前最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-41-delivery-assets-checklist-and-recording-plan.md`
5. `project-logs/campus-relay/step-42-real-media-capture-and-archive.md`
6. `project-logs/campus-relay/runtime/step-42-media/screenshots/*.png`
7. `project-logs/campus-relay/runtime/step-42-media/videos/*.webm`

## 下一轮建议

1. 如果要继续交付整理，Step 43 优先补 after-sale 固定样本或补一轮媒体复核，不要转开发。
2. 如果媒体材料已满足答辩，Step 43 可评估是否进入非 bridge 后端能力梳理。
3. bridge 主线继续冻结。
4. 展示 polish 线继续冻结。
5. 第五个 admin 页继续后置，不以补页数为目标。

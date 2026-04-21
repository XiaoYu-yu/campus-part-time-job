# Step 78 - 试运营交付包 RC 收口复盘 / 最小 smoke 复核

## 本轮目标

1. 基于 Step 40 到 Step 42 的交付整理、媒体归档与 Step 77 的 preflight 结果，判断当前项目是否已达到试运营 RC 状态。
2. 做一轮最小 smoke 复核，确认当前交付包不是只停留在文档层面。
3. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由、不改前端页面。

## 为什么进入 RC 收口复盘

Step 77 已确认交付文档口径一致、backend compile 和 frontend build 可通过、customer / courier / admin 关键页面最小 preflight 已跑通。  
因此本轮不再继续补功能，而是判断当前交付包是否已经达到可冻结、可移交、可答辩、可复盘的试运营 RC 状态。

## RC 复盘范围

1. Step 40 的交付边界、主演示脚本、账号与样本索引、页面展示清单、风险与答辩口径。
2. Step 41 的截图清单、录屏顺序、演示前检查 checklist。
3. Step 42 的真实截图、真实录屏与媒体索引。
4. Step 77 的文档一致性复核与最小 preflight 验证结果。

## 最小 smoke 复核

### 交付材料存在性

已确认以下关键交付材料存在：

1. `docs/trial-operation-preflight.md`
2. `docs/simulated-funds-boundary.md`
3. `project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md`
4. `project-logs/campus-relay/step-41-delivery-assets-checklist-and-recording-plan.md`
5. `project-logs/campus-relay/step-42-real-media-capture-and-archive.md`
6. `project-logs/campus-relay/step-77-delivery-doc-consistency-and-minimal-preflight.md`

### 媒体目录存在性

已确认以下目录存在：

1. `project-logs/campus-relay/runtime/step-42-media/screenshots`
2. `project-logs/campus-relay/runtime/step-42-media/videos`
3. `project-logs/campus-relay/runtime/step-42-media/logs`

### 媒体数量

当前已归档：

1. 截图 15 张
2. 录屏 5 段

其中包含：

1. customer onboarding / token 申请
2. courier workbench 主动作链路
3. customer confirm 与 completed 回读
4. admin settlement 只读页
5. admin after-sale 只读页
6. 异常后 confirm 失败留痕

### 本地运行可达性

最小 smoke 复核结果：

1. frontend `127.0.0.1:5173` 端口可访问
2. backend `127.0.0.1:8080` 端口可访问

说明：

1. Step 77 已完成 compile、build 和关键页面 preflight。
2. 本轮不重跑整轮浏览器链路，只补充交付包 RC 所需的最小可达性复核。

## RC 结论

结论：当前项目已达到“试运营 RC 状态”。

具体含义：

1. 可演示：主链路、admin 只读页、地图单页试点和异常/售后/settlement 只读能力都已有稳定演示材料。
2. 可移交：交付边界、启动方式、账号、样本订单、媒体目录、演示脚本和 preflight 手册都已整理。
3. 可答辩：已有主链路截图、录屏、答辩口径和模拟资金边界说明。
4. 可复盘：Step 40 到 Step 42、Step 77 已形成完整交付留痕。

## 当前仍保留的非阻塞遗留项

1. `application-test.properties` 默认 `server.port=0`，浏览器验证仍需本地环境变量临时覆盖到 `8080`。
2. frontend build 仍有既有 Sass `@import` 弃用告警和 Vite chunk size 告警。
3. after-sale 固定真实样本仍不是当前交付必需项；若未来答辩明确要求真实售后记录，可再做 targeted 补样本。

这些问题当前不阻塞 RC 收口。

## 已完成项

1. 完成 RC 收口复盘。
2. 完成交付材料存在性检查。
3. 完成媒体目录与媒体数量核对。
4. 完成 frontend / backend 本地最小 smoke 可达性复核。
5. 明确当前项目已进入试运营 RC 状态。

## 明确没做

1. 没有修改 Java、SQL、Vue 业务代码。
2. 没有新增页面、路由或后端接口。
3. 没有改 bridge、鉴权、token 附着、`request.js` 或地图代码。
4. 没有接真实支付、真实退款、真实打款。
5. 没有重开展示 polish、媒体线或地图第二页。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-77-delivery-doc-consistency-and-minimal-preflight.md`
5. `project-logs/campus-relay/step-78-trial-operation-rc-review-and-minimal-smoke.md`

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. 本轮只做 RC 收口复盘和最小 smoke 复核，不构成 bridge 恢复推进触发条件。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 79 建议进入“试运营 RC 归档 / 后续主线分层评估”：

1. 如果目标是收口交付，优先把当前 RC 状态、冻结线和非阻塞遗留项整理成更短的 release note / handoff 口径。
2. 如果目标是继续开发，不默认重开 bridge、展示 polish、媒体线或地图线，而是先重新分层评估 backlog，再决定是否开启新的非 bridge 主线。
3. 当前不建议直接进入真实支付、真实退款、真实打款或地图扩张。

## Step 79 回填

1. 已将 `application-test.properties` 默认端口从随机端口收口为 `SERVER_PORT:8080`，不再需要为了浏览器联调临时覆盖端口。
2. 已将 `frontend/src/styles/global.scss` 与 `frontend/src/styles/element-plus.scss` 从 Sass `@import` 迁移到 `@use`。
3. 已同步更新 `docs/trial-operation-preflight.md` 的 test profile 启动说明，补充默认端口和 `SERVER_PORT` 覆盖方式。
4. backend compile、frontend build 和 H2/test 启动验证已完成，frontend build 不再出现 Sass `@import` 弃用告警。

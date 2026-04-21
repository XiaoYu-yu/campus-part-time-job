# Step 77 - 试运营交付文档一致性复核 / 最小 Preflight 验证

## 本轮目标

1. 复核试运营交付文档口径是否一致。
2. 按 `docs/trial-operation-preflight.md` 完成一轮最小 preflight 验证。
3. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由、不改前端页面。

## 复核范围

1. `README.md`
2. `docs/delivery-guide.md`
3. `docs/trial-operation-preflight.md`
4. `docs/simulated-funds-boundary.md`
5. `project-logs/campus-relay/summary.md`

## 文档一致性结论

1. docs 范围内未发现“地图 SDK 未接入”“真实支付已接入”“真实退款已接入”“真实打款已接入”或“bridge 已可删除”等过期交付口径。
2. 当前交付文档对地图能力的描述一致：
   - 腾讯地图已在 `CampusCourierOpsView.vue` 完成单页 admin 只读点位预览。
   - 轨迹、调度、第二个地图页面仍未接入。
3. 当前交付文档对资金能力的描述一致：
   - mock-pay 只推进订单业务状态。
   - 售后执行只代表模拟退款 / 执行审计。
   - settlement payout、批次操作审计和对账差异都只表达模拟打款与运营审计。
4. 当前交付文档对 bridge 的描述一致：
   - bridge 继续保持 `Phase A no-op` 冻结态。
   - `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
5. 本轮命中的“bridge 已可删除”措辞只存在于历史日志里的防误写约束，不属于当前交付文档矛盾。

## 最小 Preflight 验证

### 构建验证

1. backend：`.\mvnw.cmd -DskipTests compile` 通过。
2. frontend：`npm run build` 通过。
3. frontend build 仅保留既有非阻塞告警：
   - Sass `@import` 弃用告警
   - Vite chunk size 告警

### 运行态验证

1. frontend 通过 Vite 本地启动，供浏览器验证使用。
2. backend 使用 `test profile` 本地启动；由于 `application-test.properties` 中 `server.port=0`，本轮仅通过本地环境变量临时覆盖到 `8080` 供浏览器验证，未修改配置文件。
3. customer 侧：
   - 使用测试账号 `13900139000 / 123456` 登录成功。
   - `/user/campus/courier-onboarding` 可正常打开并展示 onboarding 信息区、审核状态区与 token 申请区。
   - `/user/campus/order-result?orderId=CR202604060001` 可正常打开并展示 `COMPLETED` 结果摘要。
4. courier 侧：
   - `/courier/workbench` 可正常打开。
   - 在无 `courier_token` 场景下，可稳定展示无 token 提示态。
5. admin 侧：
   - `/campus/settlements` 可正常打开。
   - `/campus/after-sale-executions` 可正常打开。
   - `/campus/courier-ops` 可正常打开。
   - `/campus/exceptions` 可正常打开。

## 已完成项

1. 完成交付文档一致性复核。
2. 完成 backend / frontend 最小 preflight 构建验证。
3. 完成 customer / courier / admin 关键页面的最小运行态验证。
4. 确认本轮不需要修改任何业务代码或交付文档正文。

## 明确没做

1. 没有修改 Java、SQL、Vue 业务代码。
2. 没有新增页面、路由或后端接口。
3. 没有改 bridge、鉴权、token 附着、`request.js` 或地图代码。
4. 没有接真实支付、真实退款、真实打款。
5. 没有重开展示 polish 或地图第二页。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-76-simulated-funds-productization-boundary.md`
5. `project-logs/campus-relay/step-77-delivery-doc-consistency-and-minimal-preflight.md`

## 遗留问题

1. `application-test.properties` 默认 `server.port=0`，对浏览器 preflight 不够直接；当前仅通过本地环境变量临时覆盖，不改项目配置。
2. frontend build 仍有既有 Sass `@import` 弃用告警和 Vite chunk size 告警，但本轮不处理。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. 本轮只做文档一致性和 preflight 验证，不构成 bridge 恢复推进触发条件。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 78 建议进入“试运营交付包 RC 收口复盘 / 最小 smoke 复核”：

1. 以当前 preflight 手册和 Step 77 验证结果为基线，确认交付包是否已达到可冻结、可移交、可答辩的 RC 状态。
2. 若无新问题，不默认新增功能、不重开 bridge、不重开展示 polish，也不扩地图第二页。
3. 若仍有残缺，只补交付材料或 smoke 留痕，不转入真实支付、真实退款、真实打款。

## Step 78 回填

1. 已按 Step 77 的建议完成试运营交付包 RC 收口复盘。
2. 已复核 Step 40 到 Step 42 的交付文档、样本索引、截图和录屏归档，确认主交付材料链路完整。
3. 已确认 `project-logs/campus-relay/runtime/step-42-media/screenshots`、`videos`、`logs` 三个目录存在。
4. 已确认当前媒体资产数量为：
   - 截图 15 张
   - 录屏 5 段
5. 已完成最小 smoke 复核：
   - frontend `5173` 端口可访问
   - backend `8080` 端口可访问
6. 当前交付结论已收口为“试运营 RC 状态”，但仍保留非阻塞遗留项：
   - `application-test.properties` 默认 `server.port=0`
   - frontend build 仍有 Sass `@import` 和 Vite chunk size 告警
   - after-sale 固定真实样本仍不是当前交付必需项

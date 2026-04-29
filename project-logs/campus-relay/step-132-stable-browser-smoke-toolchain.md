# Step 132 - 稳定浏览器 smoke 工具链

## 本轮目标

在 Step 131 已完成本地/内测型试运营 smoke 后，本轮只补齐可重复执行的浏览器级 smoke 工具链：

1. 复用当前本地 `test` profile 后端与 frontend dev server。
2. 通过真实登录接口获取 admin / customer / parttime token。
3. 使用浏览器自动化写入 localStorage，访问关键页面并保存截图。
4. 生成可复用的 smoke 报告与截图索引。
5. 不改业务代码、接口语义、bridge、鉴权、路由或 token 附着逻辑。

## 实际新增工具

新增脚本：

```powershell
scripts/trial-operation/browser-smoke.ps1
```

运行方式：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\browser-smoke.ps1
```

前置条件：

1. backend `test` profile 已在 `127.0.0.1:8080` 运行。
2. frontend dev server 已在 `127.0.0.1:5173` 运行。
3. H2 样本账号仍使用既有固定账号。

脚本行为：

1. 调用真实登录接口获取 token。
2. 不在报告中输出 token。
3. 使用 `@playwright/cli` 正确调用方式：`npx --yes --package @playwright/cli -- playwright-cli ...`。
4. 对每个页面创建独立浏览器 session。
5. 写入对应 localStorage 后 reload，使 Pinia/router guard 从 token 状态初始化。
6. 访问目标页面，校验未跳回 login、存在 `#app`、页面已渲染。
7. 保存 full-page 截图。

## 覆盖页面

本轮浏览器 smoke 覆盖 7 个入口：

1. admin dashboard：`/dashboard`
2. admin employee：`/employee`
3. admin settlement 只读运营页：`/campus/settlements`
4. admin after-sale 执行页：`/campus/after-sale-executions`
5. admin exception 处理页：`/campus/exceptions`
6. customer completed 结果页：`/user/campus/order-result?orderId=CR202604060001`
7. parttime workbench：`/parttime/workbench`

## 运行结果

报告位置：

```text
project-logs/campus-relay/runtime/step-132-browser-smoke/browser-smoke-report.json
```

结果：

1. 7 项通过。
2. 0 项失败。
3. tokens 已脱敏，仅记录页面路径、最终 URL、截图路径与状态。

截图文件：

```text
project-logs/campus-relay/runtime/step-132-browser-smoke/admin-dashboard.png
project-logs/campus-relay/runtime/step-132-browser-smoke/admin-employee.png
project-logs/campus-relay/runtime/step-132-browser-smoke/admin-settlements.png
project-logs/campus-relay/runtime/step-132-browser-smoke/admin-after-sale-executions.png
project-logs/campus-relay/runtime/step-132-browser-smoke/admin-exceptions.png
project-logs/campus-relay/runtime/step-132-browser-smoke/customer-order-result.png
project-logs/campus-relay/runtime/step-132-browser-smoke/parttime-workbench.png
```

## 本轮文档同步

1. `scripts/trial-operation/README.md` 已新增本地浏览器 smoke 命令。
2. `scripts/trial-operation/commands.ps1` 已新增本地浏览器 smoke 命令索引。
3. `.gitignore` 已忽略 `.playwright-cli/`，避免本地浏览器 CLI 快照缓存进入版本管理。

## 明确未改

1. 未改 bridge。
2. 未改 `request.js`。
3. 未改 token 附着逻辑。
4. 未改 `campus-*` API 文件运行时行为。
5. 未改路由。
6. 未改后端接口、数据库、状态机或鉴权。
7. 未新增页面。
8. 未删除旧兼容模块。
9. 未提交真实密钥、公网 IP、服务器密码、GitHub token 或腾讯地图 key。

## 当前判断

本地/内测型试运营验证现在具备两层可重复工具链：

1. Step 131 的 API smoke：验证三类身份和关键接口。
2. Step 132 的浏览器 smoke：验证关键页面能以真实 token 状态渲染，并保留截图证据。

这比单纯人工点击更稳定，也能在后续 UI 修补或内测前快速复核关键入口。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

## 下一轮建议

Step 133 建议先做“本地内测 RC 状态复盘”：

1. 汇总 Step 131 API smoke 与 Step 132 浏览器 smoke 的通过范围。
2. 判断本地/内测型试运营是否已具备“可反复验证”的基线。
3. 如果继续开发，优先只处理真实用户指出的前端 bug 或试运营阻塞，不再凭感觉继续大改样式。

## Step 133 跟进结果

已完成本地内测 RC 状态复盘：

1. Step 131 API smoke 与 Step 132 浏览器 smoke 已形成两层可重复验证基线。
2. 当前本地/内测型试运营具备可反复验证能力。
3. 后续若修前端 bug 或做小功能，建议先跑 preflight、API smoke、browser smoke 和 `git diff --check`。
4. 服务器内测仍需单独做远端 smoke，不用本地结论直接替代。

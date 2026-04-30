# Step 134 - 远端内测 smoke 准备

## 本轮目标

基于 Step 133 已完成的本地 RC 复盘，本轮进入服务器内测 smoke 准备：

1. 补一个可参数化的远端 API / SPA shell smoke 脚本。
2. 明确远端 smoke 的执行命令、覆盖范围和脱敏边界。
3. 同步部署文档和试运营脚本入口。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 为什么做远端 smoke 准备

Step 131 和 Step 132 已经证明本地 test profile + H2 + frontend dev server 可以稳定通过 API 与浏览器 smoke。

但服务器内测不能直接复用本地结论。远端环境至少多出这些风险：

1. API base、前端静态资源路径和反向代理路径可能不一致。
2. 服务器数据库 seed 或 profile 可能与本地 H2 不一致。
3. CORS、端口暴露和静态资源 fallback 可能有环境差异。
4. 真实公网地址、密码、地图 key、JWT secret 不能写入仓库日志。

因此本轮只补“可带参数执行、默认脱敏”的远端 smoke 工具与说明。

## 实际新增脚本

新增：

```text
scripts/trial-operation/remote-smoke.ps1
```

推荐命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host:8080/api -FrontendBase http://your-host/
```

特点：

1. `ApiBase` 必须以 `/api` 结尾。
2. `FrontendBase` 可选；不传时只跑 API smoke。
3. 默认报告路径：

```text
project-logs/campus-relay/runtime/step-134-remote-smoke/remote-smoke-report.json
```

4. 默认脱敏 host，不写真实公网地址。
5. 报告不输出 token。
6. 不修改业务数据结构，不重置 H2，不启动长驻进程。

## 覆盖范围

### API smoke

脚本复用固定内测账号，覆盖：

1. admin login。
2. customer login。
3. parttime token。
4. admin employee list。
5. admin settlements list。
6. admin settlement summary。
7. admin after-sale executions。
8. admin couriers list。
9. admin exception history list。
10. customer onboarding profile。
11. customer token eligibility。
12. customer completed order detail `CR202604060001`。
13. parttime profile。
14. parttime review status。
15. parttime available orders。
16. parttime completed order detail `CR202604060001`。

### frontend SPA shell smoke

传入 `-FrontendBase` 后，脚本检查：

1. `/`
2. `/login`
3. `/dashboard`
4. `/campus/settlements`
5. `/campus/after-sale-executions`
6. `/campus/exceptions`
7. `/user/campus/order-result?orderId=CR202604060001`
8. `/parttime/workbench`

它只确认远端静态资源能返回 SPA 壳子，不替代 Step 132 的浏览器截图级 smoke。

## 文档同步

新增：

```text
docs/deployment/remote-internal-trial-smoke.md
```

更新：

1. `docs/deployment/post-deploy-smoke-checklist.md`
2. `docs/deployment/internal-trial-ops-runbook.md`
3. `scripts/trial-operation/README.md`
4. `scripts/trial-operation/commands.ps1`

文档均只使用 `your-host` 占位，不写真实服务器 IP、密码、token 或地图 key。

## 明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改 token 附着逻辑。
4. 没有改后端接口、数据库、状态机或鉴权。
5. 没有改任何 Vue 页面。
6. 没有新增页面或路由。
7. 没有删除旧兼容模块。
8. 没有接真实支付、真实退款或真实打款。
9. 没有提交真实公网 IP、服务器密码、GitHub token 或腾讯地图 key。

## 验证结果

本轮已执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://127.0.0.1:8080/api -FrontendBase http://127.0.0.1:5173 -OutputPath project-logs\campus-relay\runtime\step-134-remote-smoke\local-remote-smoke-report.json
git diff --check
```

结果：

1. remote-smoke 本地等价验证：24 项通过，0 项失败。
2. 报告路径：

```text
project-logs/campus-relay/runtime/step-134-remote-smoke/local-remote-smoke-report.json
```

3. 报告中 `apiBase`、`frontendBase` 和每个 endpoint 均已脱敏为 `<redacted>`。
4. `npm run build` 通过。
5. `.\mvnw.cmd -DskipTests compile` 通过。
6. `git diff --check` 通过；仅输出 Windows 换行转换 warning，无空白错误。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

远端 smoke 只是验证当前内测部署是否可用，不代表 bridge 可以删除。

## Step 135 建议

如果服务器已经完成部署并启动：

1. 用真实服务器地址手工执行 `remote-smoke.ps1`，但不要把未脱敏报告提交到仓库。
2. 若 remote smoke 失败，优先修部署、端口、profile、CORS、seed 或静态资源 fallback。
3. 若 remote smoke 通过，再考虑补一轮远端浏览器截图 smoke。

如果服务器尚未稳定部署：

1. 先按 `docs/deployment/internal-trial-ops-runbook.md` 完成服务启动、备份和状态检查。
2. 不继续堆业务功能。

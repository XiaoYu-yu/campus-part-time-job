# Step 135 - 远端内测 smoke 真实验证

## 本轮目标

基于 Step 134 已新增的 `remote-smoke.ps1`，本轮对 owner 提供的内测服务器做一次真实远端 smoke：

1. 验证远端 API base 可用。
2. 验证远端 frontend SPA shell 可访问。
3. 确认报告默认脱敏，不提交真实公网地址、密码、token 或地图 key。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 执行入口

本轮使用：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase <redacted-api-base> -FrontendBase <redacted-frontend-base> -OutputPath project-logs\campus-relay\runtime\step-135-remote-smoke\remote-smoke-report.json
```

真实 host 只作为本地命令参数使用，不写入仓库文档。

## 验证结果

报告路径：

```text
project-logs/campus-relay/runtime/step-135-remote-smoke/remote-smoke-report.json
```

结果：

1. API + frontend shell smoke：24 项通过。
2. 失败项：0。
3. 跳过项：0。
4. 报告中 `apiBase`、`frontendBase` 与 endpoint 均为 `<redacted>`。
5. 报告未输出登录 token。

## 覆盖范围

### API

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

### frontend SPA shell

1. `/`
2. `/login`
3. `/dashboard`
4. `/campus/settlements`
5. `/campus/after-sale-executions`
6. `/campus/exceptions`
7. `/user/campus/order-result?orderId=CR202604060001`
8. `/parttime/workbench`

## 当前结论

当前 owner 提供的内测服务器在远端 smoke 范围内可用：

1. 三类身份登录可用。
2. admin / customer / parttime 关键接口可读。
3. 前端主要路由能返回 SPA 壳子。
4. 当前验证仍不是完整生产验收，不覆盖 HTTPS、域名、证书、压测、监控、真实支付、真实退款或真实打款。

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

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

远端 smoke 通过只说明当前内测部署可用，不代表 bridge 可以删除。

## Step 136 建议

建议二选一：

1. 如果继续验证服务器体验：做远端浏览器截图 smoke，只保存脱敏或未跟踪证据，不提交真实 host。
2. 如果开始内测运维加固：检查服务器 compose 状态、备份脚本、restore drill 和日志查看流程是否可执行。

不建议下一轮继续扩业务功能或继续大范围改前端样式。

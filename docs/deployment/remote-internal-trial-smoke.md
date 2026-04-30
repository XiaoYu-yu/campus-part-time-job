# 远端内测 Smoke 执行说明

## 目标

这份文档用于服务器部署完成后的最小远端 smoke。

它不是正式生产验收，也不替代真实监控、备份、压测或安全审计。它只确认：

1. 远端 backend API 能用固定内测账号登录。
2. admin / customer / parttime 关键只读接口可访问。
3. 远端 frontend SPA 壳子能访问关键路由。
4. 报告默认不写入真实公网 host、token、密码或地图 key。
5. bridge、真实支付、真实退款、真实打款边界没有被误改。

## 前提

执行前先确认：

1. 服务器已启动 backend、frontend 和数据库。
2. 目标 API base 以 `/api` 结尾。
3. 后端已包含当前内测样本账号和样本订单。
4. 真实服务器 IP、域名、密码、JWT secret、腾讯地图 key 不写入仓库。
5. bridge 仍处于 `Phase A no-op` 冻结态。

## 推荐命令

在本地仓库根目录执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host:8080/api -FrontendBase http://your-host/
```

只检查 API 时：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host:8080/api
```

输出报告默认写入：

```text
project-logs/campus-relay/runtime/step-134-remote-smoke/remote-smoke-report.json
```

报告默认脱敏 host：

```json
"apiBase": "http://<redacted>:8080/api"
```

不要在正常提交中使用 `-NoRedact`。如果临时需要完整 URL 留痕，把报告保存到未跟踪目录。

## 覆盖范围

### API smoke

脚本会使用固定内测账号调用：

1. admin login
2. customer login
3. parttime token
4. admin employee list
5. admin settlements list
6. admin settlement summary
7. admin after-sale executions
8. admin couriers list
9. admin exception history list
10. customer onboarding profile
11. customer token eligibility
12. customer completed order detail `CR202604060001`
13. parttime profile
14. parttime review status
15. parttime available orders
16. parttime completed order detail `CR202604060001`

### frontend SPA shell smoke

如果传入 `-FrontendBase`，脚本会检查以下路由返回 SPA 壳子：

1. `/`
2. `/login`
3. `/dashboard`
4. `/campus/settlements`
5. `/campus/after-sale-executions`
6. `/campus/exceptions`
7. `/user/campus/order-result?orderId=CR202604060001`
8. `/parttime/workbench`

这不是浏览器截图级验证。若需要截图级验证，使用：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\browser-smoke.ps1 -ApiBase http://your-host:8080/api -FrontendBase http://your-host/ -RuntimeDir .\tmp\remote-browser-smoke
```

注意：`browser-smoke.ps1` 的报告会写 currentUrl，因此远端截图报告建议保存在未跟踪目录。

## 不覆盖的范围

1. 不执行真实支付。
2. 不执行真实退款。
3. 不执行真实打款。
4. 不删除 bridge。
5. 不改鉴权。
6. 不做压测。
7. 不检查 HTTPS、域名或证书有效性。
8. 不替代备份与恢复演练。

## 失败处理

如果 remote smoke 失败：

1. 先看 `remote-smoke-report.json` 中第一条 `FAIL`。
2. 如果登录失败，优先检查数据库 seed、profile、JWT secret 和 CORS。
3. 如果受保护接口失败，优先检查 token 附着、API base、反向代理和后端日志。
4. 如果 frontend shell 失败，优先检查静态资源部署、前端构建产物和前端路由 fallback。
5. 不要在 smoke 失败时顺手改 bridge、删旧接口或接真实支付。

## 证据记录模板

| 项目 | 结果 | 报告路径 | 备注 |
| --- | --- | --- | --- |
| remote API smoke | 待填写 | `project-logs/campus-relay/runtime/step-134-remote-smoke/remote-smoke-report.json` | host 默认脱敏 |
| remote frontend shell smoke | 待填写 | 同上 | 仅检查 SPA 壳子 |
| remote browser screenshot smoke | 可选 | 未跟踪目录 | 避免提交真实 host |

## 当前冻结主线

远端 smoke 不改变以下结论：

1. bridge 主线继续 `Phase A no-op` 冻结。
2. 展示 polish 线继续维护态。
3. 媒体线继续收住。
4. 旧兼容模块继续保留。

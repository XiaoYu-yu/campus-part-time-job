# Step 139 - 单机内测安全边界固化

## 本轮目标

基于 Step 138 已完成的端口收敛，本轮进入单机内测安全边界固化：

1. 明确当前内测服务器的公网入口边界。
2. 把 `80 / 22 / 8080 / 3306` 的策略写成可执行文档。
3. 将 runbook、smoke checklist 和命令索引统一到同一口径。
4. 复核当前服务器监听与公网探测结果。
5. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

本日志不记录真实公网 IP、服务器密码、数据库密码、JWT secret、腾讯地图 key、SSH 私钥或 token。

## 本轮选择

Step 139 选择“单机内测安全组 / 防火墙说明固化”，暂不新增 backend health endpoint。

原因：

1. Step 138 刚完成端口边界收敛，本轮继续固化运维口径风险最低。
2. backend health endpoint 属于新增后端接口，虽然风险低，但仍应先做 go / no-go。
3. 当前远端 smoke 已能覆盖关键业务入口，health endpoint 不阻塞当前内测。
4. 当前最容易出问题的是后续误把 `8080 / 3306` 重新暴露，因此优先把边界写清。

## 安全边界文档

新增：

```text
docs/deployment/internal-trial-security-boundary.md
```

该文档明确：

1. 业务公网入口只使用 frontend `80`。
2. API 公网访问统一走 frontend nginx `/api`。
3. backend `8080` 只绑定服务器本机 `127.0.0.1`。
4. MySQL `3306` 只绑定服务器本机 `127.0.0.1`。
5. SSH `22` 仅作为运维入口，建议在云厂商安全组限制来源 IP。
6. 云安全组不应放行 `8080 / 3306`。
7. 如后续启用 HTTPS，再新增 `443`，但本阶段不做 HTTPS / 域名 / 证书。

## 文档同步

已同步更新：

1. `docs/deployment/internal-trial-ops-runbook.md`
   - 增加安全边界文档入口。
   - 增加本地公网端口探测命令。
2. `docs/deployment/post-deploy-smoke-checklist.md`
   - 增加单机内测端口边界检查项。
3. `docs/deployment/remote-internal-trial-smoke.md`
   - 增加安全边界文档链接。
4. `scripts/trial-operation/commands.ps1`
   - 增加端口边界探测命令模板。

## 实际服务器复核

已通过 SSH 做服务器本机监听检查，脱敏结论：

1. SSH `22` 监听公网。
2. frontend `80` 监听公网。
3. backend `8080` 监听 `127.0.0.1`。
4. MySQL `3306` 监听 `127.0.0.1`。
5. `ufw` 当前 inactive。
6. `firewalld` 当前不可用。

已在本机做公网端口探测，脱敏结论：

1. `22` 可达。
2. `80` 可达。
3. `8080` 不可达。
4. `3306` 不可达。

当前判断：

1. 业务公网入口已收敛到 frontend `80`。
2. backend 与 MySQL 没有继续公网暴露。
3. 服务器当前主要依靠 Docker 端口绑定和云安全组承担边界。
4. 如果进入更长期内测，下一步应在云安全组限制 SSH `22` 来源 IP。

## 明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改 token 附着逻辑。
4. 没有改后端接口、数据库 schema、状态机或鉴权。
5. 没有改任何 Vue 页面。
6. 没有新增页面或路由。
7. 没有新增 backend health endpoint。
8. 没有配置 HTTPS、域名或证书。
9. 没有改云厂商安全组。
10. 没有提交真实公网 IP、服务器密码、GitHub token、SSH 私钥或腾讯地图 key。

## 验证结果

1. 服务器监听检查：通过。
2. 本地公网端口探测：通过。
3. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1`：通过。
4. `git diff --check`：通过，仅保留 Windows CRLF 提示。

本轮未改前端/后端业务代码，因此未重复执行 `npm run build` 和 `.\mvnw.cmd -DskipTests compile`。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

## Step 140 建议

建议进入“backend health endpoint go / no-go”：

1. 先评估是否新增一个无业务副作用的最小健康检查接口。
2. 如果新增，建议只返回应用存活状态，不读取用户、订单或资金数据。
3. 如果不新增，则继续明确只能用 remote smoke 作为部署后健康证据。
4. 继续保持 bridge、展示 polish、媒体线、旧兼容模块冻结口径不变。

# Step 138 - 内测服务器端口边界与备份告警加固

## 本轮目标

基于 Step 137 已完成 GitHub / 服务器同步与远端 smoke，本轮进入内测服务器安全边界最小加固：

1. 收紧公网端口暴露。
2. 保留 frontend 80 作为唯一默认公网入口。
3. backend 8080 与 MySQL 3306 仅绑定服务器本机 `127.0.0.1`。
4. 远端 API smoke 改为通过 frontend nginx `/api` 反向代理访问。
5. 修正 MySQL 8 `mysqldump` tablespace 权限 warning。
6. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

本日志不记录真实公网 IP、服务器密码、数据库密码、JWT secret、腾讯地图 key、SSH 私钥或 token。

## 配置改动

### compose 端口边界

文件：

```text
deploy/internal-trial/docker-compose.yml
```

改动：

1. `mysql` 端口从默认公网绑定改为：
   - `127.0.0.1:${MYSQL_PORT:-3306}:3306`
2. `backend` 端口从默认公网绑定改为：
   - `127.0.0.1:${BACKEND_PORT:-8080}:8080`
3. `frontend` 继续保留：
   - `${FRONTEND_PORT:-80}:80`

原因：

1. frontend nginx 已经代理 `/api/` 到 compose 内部 `backend:8080`。
2. backend 8080 不需要直接公网访问。
3. MySQL 3306 不应长期公网暴露。
4. 保留 `127.0.0.1` 绑定可以支持服务器本机诊断或 SSH tunnel，不破坏运维调试。

### env 示例说明

文件：

```text
deploy/internal-trial/.env.example
```

补充说明：

1. `MYSQL_PORT` 与 `BACKEND_PORT` 只绑定服务器本机。
2. 这两个端口用于本机诊断或 SSH tunnel，不作为公网入口。

### 备份脚本

文件：

```text
deploy/internal-trial/backup-stack.sh
```

改动：

1. `mysqldump` 增加 `--no-tablespaces`。
2. 目标是消除 MySQL 8 下普通备份账号缺少 `PROCESS` 权限导致的 tablespace warning。

边界：

1. 不修改数据库账号权限。
2. 不改变备份内容的业务数据范围。
3. 后续仍通过 restore drill 验证备份可恢复。

## 文档改动

已同步更新：

1. `docs/deployment/internal-trial-ops-runbook.md`
2. `docs/deployment/remote-internal-trial-smoke.md`
3. `docs/deployment/post-deploy-smoke-checklist.md`
4. `scripts/trial-operation/commands.ps1`

新的远端 smoke 推荐入口：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host/api -FrontendBase http://your-host/
```

旧的 `http://your-host:8080/api` 只保留为“故意开放 8080 时的 legacy 形式”，不再作为默认推荐。

## 本地验证

已完成：

1. `npm run build` 通过。
2. `.\mvnw.cmd -DskipTests compile` 通过。

本机未完成：

1. `docker compose config` 未执行成功，原因是当前本机环境没有可用的 `docker` CLI。
2. compose 配置展开与真实端口验证放到服务器执行。

## 服务器验证结果

已完成：

1. 本地配置已提交并推送到 GitHub，服务器已 `git pull --ff-only origin main` 更新到 Step 138 提交。
2. 服务器已执行 compose 配置展开检查，确认：
   - `backend` 绑定 `127.0.0.1:8080->8080/tcp`。
   - `mysql` 绑定 `127.0.0.1:3306->3306/tcp`。
   - `frontend` 继续绑定公网 `80`。
3. 服务器已重建 compose，`backend / frontend / mysql` 均为 running。
4. 本地公网端口探测结果：
   - `80` 可访问。
   - `8080` 不可访问。
   - `3306` 不可访问。
5. 已通过 nginx `/api` 入口重新跑远端 smoke：
   - 报告路径：`project-logs/campus-relay/runtime/step-138-remote-smoke/remote-smoke-report.json`。
   - PASS：24。
   - FAIL：0。
   - SKIP：0。
   - 报告已脱敏 host、endpoint 和 token。
6. 已执行 `backup-stack.sh`，`mysqldump` 未再出现 tablespace `PROCESS` 权限 warning；仅保留 MySQL CLI 使用密码的通用 warning。
7. 已执行非破坏性 restore drill：
   - 最新 dump 可恢复。
   - `restored_order_count=7`。
   - `CR202604070002` 存在。
   - `CR202604060001` 存在。
   - uploads 归档可解压，当前文件数为 `0`。

补充说明：

1. 第一次 restore drill 曾短暂出现临时 MySQL root 登录失败，复查时临时 MySQL 认证单独验证通过。
2. 复跑同一最新备份的 `restore-drill.sh` 已通过，未改业务代码或数据库结构。
3. 后续如果再次出现同类失败，再考虑加固 restore drill 的 SQL readiness 检测；本轮不扩大脚本改动。

## 明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改 token 附着逻辑。
4. 没有改后端接口、数据库 schema、状态机或鉴权。
5. 没有改任何 Vue 页面。
6. 没有新增页面或路由。
7. 没有删除旧兼容模块。
8. 没有接真实支付、真实退款或真实打款。
9. 没有提交真实公网 IP、服务器密码、GitHub token、SSH 私钥或腾讯地图 key。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

## Step 139 建议

Step 138 端口收敛、远端 smoke、backup 和 restore drill 均已通过。

Step 139 建议进入：

1. 单机内测安全组 / 防火墙说明固化。
2. 或补最小 backend health endpoint go / no-go。
3. 或继续做服务器监控 / 日志留存最小方案。

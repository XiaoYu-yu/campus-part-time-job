# Step 136 - 服务器内测运维检查与恢复演练

## 本轮目标

基于 Step 135 远端 smoke 已通过，本轮不继续扩业务功能，而是检查单机内测服务器是否具备基本维护能力：

1. 检查部署目录和 git 状态。
2. 检查 compose 容器状态。
3. 检查本机 HTTP / backend 基础访问。
4. 执行备份脚本。
5. 执行非破坏性 restore drill。
6. 验证日志查看流程。
7. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 执行入口

服务器部署目录：

```text
/opt/campus-part-time-job
```

实际执行命令来自：

```text
docs/deployment/internal-trial-ops-runbook.md
```

本日志不记录真实公网 IP、服务器密码、数据库密码、JWT secret、腾讯地图 key 或 token。

## 部署目录检查

结果：

1. `/opt/campus-part-time-job` 存在。
2. `git status --short --branch` 显示服务器工作区干净。
3. 服务器当前 git short hash：`1a2329e`。
4. 本地当前 `main` 比 `origin/main` 领先多个提交，且本地最新 Step 134 / Step 135 尚未推送到远端，因此服务器代码不是本地最新状态。

影响：

1. 当前远端 smoke 通过证明服务器现有部署可用。
2. 但它不能证明本地最新提交已经部署。
3. 后续如果要部署本地最新内容，必须先推送 GitHub，再按 runbook 备份、拉取、重建、smoke。

## compose 状态检查

执行：

```bash
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml ps
```

结果：

1. `backend`：Up。
2. `frontend`：Up。
3. `mysql`：Up。
4. 当前端口暴露：
   - frontend 暴露 80。
   - backend 暴露 8080。
   - mysql 暴露 3306。

风险备注：

1. 当前是内测服务器，公网暴露 8080 和 3306 可以用于调试，但不适合作为正式生产入口。
2. 若进入长期外部试用，建议收口为 80 / 443 对外，backend / mysql 只走内网或反向代理。

## 基础访问检查

结果：

1. 服务器本机 `curl -I http://127.0.0.1/` 返回 200。
2. backend 入口可响应。
3. Step 135 已完成远端 API + frontend shell smoke：24 项通过，0 项失败。

注意：

1. 本轮曾用 GET 方式探测登录路径，backend log 出现一次 `NumberFormatException: For input string: "login"` 噪音。
2. 这是探测方式导致的日志噪音，不是本轮业务代码改动。
3. 后续健康检查应优先使用已有 smoke 脚本或明确方法的业务接口，避免用 GET 探测 POST 登录路径。

## 备份脚本验证

执行：

```bash
bash deploy/internal-trial/backup-stack.sh
```

结果：

1. MySQL dump 已生成。
2. uploads volume 归档已生成。
3. `.env` 备份已生成。
4. manifest 已生成。
5. 备份产物位于 `deploy/internal-trial/backups/`，该目录按仓库规则不应提交。

本轮没有把 `.env` 备份、数据库 dump 或 uploads 归档拉回本地，也没有提交。

## 非破坏性 restore drill

执行：

```bash
bash deploy/internal-trial/restore-drill.sh
```

结果：

1. 临时 MySQL 容器启动成功。
2. 最近一次 SQL dump 恢复成功。
3. schema 与样本数据校验成功。
4. 恢复后订单数：7。
5. `CR202604070002` 存在。
6. `CR202604060001` 存在。
7. uploads 归档可解压检查。
8. restore drill 结束后未发现遗留临时容器。

结论：

当前服务器具备“可备份 + 可非破坏性恢复演练”的最低内测运维能力。

## 日志查看流程

已验证以下命令可执行：

```bash
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs backend --tail=20
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs frontend --tail=20
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs mysql --tail=20
```

观察到：

1. backend 日志可读取；本轮 GET 探测登录路径带来一次非业务验证噪音。
2. frontend nginx 日志可读取；公网 80 暴露后会收到外部扫描请求，这是内测公网入口的预期风险之一。
3. mysql 日志可读取；存在 `sha256_password` deprecated warning，当前不阻塞运行。

## 当前未解决问题

1. 服务器当前部署不是本地最新提交。
2. 当前公网暴露 backend 8080 和 mysql 3306，不适合长期正式外部试用。
3. backend 当前没有独立健康检查接口；仍依赖业务 smoke。
4. frontend 日志中可见公网扫描流量，说明后续需要更清晰的安全边界。
5. 目前没有真实监控、告警、HTTPS、域名和证书配置。

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

## Step 137 建议

建议进入“服务器部署同步与安全边界评估”：

1. 先把本地领先提交推送到 GitHub。
2. 服务器按 runbook 先备份，再 `git pull --ff-only origin main`，再重建 compose。
3. 重建后重新跑 `remote-smoke.ps1`。
4. 评估是否立即收紧公网端口，至少优先考虑不再公网暴露 MySQL 3306。

如果暂时不更新服务器代码，下一轮至少应记录“服务器当前部署 hash 与本地最新提交不同”的风险，并暂停继续扩功能。

## Step 137 后续结果回填

Step 137 已按本建议执行：

1. 本地 `main` 已推送到 GitHub。
2. 服务器已从 `1a2329e` 更新到 `3bf59cb`。
3. 更新前已执行备份。
4. 更新后 compose 已重建，backend / frontend / mysql 均为 running。
5. 远端 smoke 24 项通过、0 项失败、0 项跳过。
6. 最新备份已通过非破坏性 restore drill。

仍需后续处理：

1. 公网 backend 8080 和 MySQL 3306 暴露边界。
2. MySQL 8 dump tablespace 权限 warning。
3. 独立 backend health endpoint 或等价健康检查能力。

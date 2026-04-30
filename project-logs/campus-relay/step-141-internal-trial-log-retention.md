# Step 141 - 单机内测日志留存与轮转策略

## 本轮目标

1. 固化单机内测服务器的最小日志留存 / 轮转策略。
2. 避免 `mysql / backend / frontend` 容器日志长期无限增长。
3. 保留最近一段可用于排障的运行日志。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 为什么选择日志轮转

Step 140 已补齐 backend health endpoint，并通过服务器远端 smoke 验证。

当前内测服务器已经具备：

1. 80 作为公网业务入口。
2. backend 8080 与 MySQL 3306 仅本机绑定。
3. 备份与 restore drill。
4. health endpoint 与 remote smoke。

剩余较直接的运维风险是：容器日志如果不设上限，长时间内测后可能占用磁盘，影响后续重建、备份或排障。

因此 Step 141 先做日志轮转，比继续扩业务功能更贴近当前“内测型试运营稳定性”目标。

## 实际完成项

### 1. Compose 日志轮转落地

`deploy/internal-trial/docker-compose.yml` 新增统一 logging 配置：

```yaml
x-default-logging: &default-logging
  driver: json-file
  options:
    max-size: "${DOCKER_LOG_MAX_SIZE:-20m}"
    max-file: "${DOCKER_LOG_MAX_FILE:-5}"
```

并应用到：

1. `mysql`
2. `backend`
3. `frontend`

默认效果：

1. 单个容器日志文件最大 `20m`。
2. 每个容器最多保留 `5` 个日志文件。
3. 单个容器理论日志上限约 `100m`。
4. 三个核心容器合计理论上限约 `300m`。

### 2. `.env.example` 增加可调参数

`deploy/internal-trial/.env.example` 新增：

```env
DOCKER_LOG_MAX_SIZE=20m
DOCKER_LOG_MAX_FILE=5
```

真实服务器 `.env` 可按磁盘和排障需要调整，但不应关闭日志。

### 3. 新增日志留存文档

新增：

- `docs/deployment/internal-trial-log-retention.md`

文档明确：

1. 当前日志轮转策略。
2. 可调参数。
3. 日常查看命令。
4. 部署后检查命令。
5. 当前不做集中式日志、告警、远端归档。
6. 容器运行日志当前不纳入 backup 包。

### 4. 同步运维入口文档

已同步：

1. `docs/deployment/internal-trial-ops-runbook.md`
2. `docs/deployment/internal-trial-compose.md`
3. `docs/deployment/post-deploy-smoke-checklist.md`
4. `scripts/trial-operation/commands.ps1`

新增或补充了日志轮转查看与检查入口。

## 明确没有改动

本轮没有修改：

1. Java 业务代码。
2. Vue 页面。
3. `request.js`。
4. token 附着逻辑。
5. bridge 相关接口。
6. 后端鉴权。
7. 路由。
8. 数据库表结构。
9. 旧兼容模块。

## 验证结果

本轮完成以下本地验证：

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1` 通过。
2. `.\mvnw.cmd -DskipTests compile` 通过。
3. `npm run build` 通过。
4. `git diff --check` 通过。
5. 敏感信息扫描无命中。

本机当前没有 Docker CLI，因此本轮没有在本地执行 `docker compose config`。Compose 日志轮转的实际生效验证放到 Step 142 的服务器部署验证中完成。

Step 142 已完成服务器部署验证：

1. 服务器已拉取包含日志轮转配置的最新提交并重建 compose。
2. `mysql / backend / frontend` 三个容器的 `LogConfig` 均为 `json-file max-size=20m max-file=5`。
3. health endpoint 在重建 warm-up 后返回 `UP`。
4. 远端 smoke 25 项通过、0 项失败、0 项跳过。
5. 详见 `project-logs/campus-relay/step-142-server-log-rotation-deploy-validation.md`。

服务器应用本次 compose 日志轮转配置后，建议执行：

```bash
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-backend-1
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-frontend-1
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-mysql-1
```

期望看到 `json-file`，以及 `max-size / max-file`。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 142 建议二选一：

1. 将本次日志轮转配置部署到服务器，重建 compose，并验证 `LogConfig` 生效，再跑 health + remote smoke。
2. 如果暂不动服务器，先固化 SSH `22` 安全组来源限制操作清单，由 owner 在云控制台手动执行。

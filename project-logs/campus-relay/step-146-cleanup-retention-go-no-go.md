# Step 146 - 内测服务器清理与留存策略 go / no-go

## 本轮目标

1. 基于 Step 145 的真实服务器磁盘与 Docker 读数，判断是否需要执行清理。
2. 固化 Docker / 备份 / 容器日志的清理触发条件。
3. 不执行任何 prune、删除或服务器写操作。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 当前真实基线

来自 Step 145：

1. 根分区约 `40G`，已用约 `9.3G`，使用率约 `25%`。
2. backend 容器日志约 `16K`。
3. frontend 容器日志约 `28K`。
4. mysql 容器日志约 `4.0K`。
5. 备份目录约 `184K`。
6. Docker image / build cache 有可回收空间，但未影响部署。

## go / no-go 结论

本轮结论：`no-go`，当前不执行清理。

理由：

1. 磁盘空间压力低。
2. 容器日志量极小。
3. 备份目录量极小。
4. 当前可回收 Docker 空间没有形成部署阻塞。
5. 清理动作可能降低快速回滚和排障能力。

## 新增文档

新增：

- `docs/deployment/internal-trial-cleanup-retention.md`

文档明确：

1. 当前默认只观察，不自动清理。
2. 清理触发条件。
3. Docker 清理边界。
4. 备份留存边界。
5. 容器日志边界。
6. 当前 no-go 结论。

## 当前明确不做

本轮不执行：

1. `docker system prune`
2. `docker system prune -a`
3. `docker builder prune`
4. `docker image prune`
5. 删除备份目录。
6. 删除容器日志。
7. 修改 Docker log config。
8. 重启容器。

## 验证结果

1. `scripts/trial-operation/commands.ps1` 通过。
2. `git diff --check` 通过。
3. 敏感信息扫描无真实 secret / host 命中。

本轮只新增清理策略文档，不改业务代码，因此没有重新执行 backend compile 或 frontend build。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 147 建议回到“试运营可用性”层面，做一次内测入口状态复盘：

1. 本地仓库状态。
2. GitHub 状态。
3. 服务器运行状态。
4. 远端 smoke 状态。
5. 运维脚本状态。
6. 当前还差什么才能让少量真实用户试用。

如果继续运维线，也可以下一轮补“内测运行日常检查表”，但不要继续执行清理动作。

Step 147 已完成补充：

1. 本地与 GitHub 均为最新 `main`。
2. 服务器运行态 remote smoke 25 项通过、0 项失败、0 项跳过。
3. 当前结论为“本地 / 内测型试运营可用，但不是完整产品级正式上线”。

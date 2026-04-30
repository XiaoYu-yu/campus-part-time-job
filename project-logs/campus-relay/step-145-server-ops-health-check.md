# Step 145 - 内测服务器运维健康检查

## 本轮目标

1. 给单机内测服务器补一个只读运维健康检查入口。
2. 检查磁盘、Docker 占用、容器日志大小和备份目录占用。
3. 不引入完整监控系统，不接告警平台。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 实际完成项

### 1. 新增只读运维健康脚本

新增：

- `scripts/trial-operation/server-ops-health.ps1`

脚本通过 key-based SSH 只读执行：

1. `df -h`
2. `docker system df`
3. `docker compose ps`
4. 三个核心容器当前日志文件大小和 LogConfig
5. 备份目录整体占用和最近备份目录概览

脚本不执行：

1. `docker prune`
2. 删除日志
3. 删除备份
4. 重启容器
5. 修改服务器配置

### 2. 同步命令索引

已更新：

1. `scripts/trial-operation/README.md`
2. `scripts/trial-operation/commands.ps1`

新增 `server-ops-health.ps1` 的只读执行示例。

## 真实运行结果

报告路径：

1. `project-logs/campus-relay/runtime/step-145-server-ops-health/server-ops-health-report.json`
2. `project-logs/campus-relay/runtime/step-145-server-ops-health/server-ops-health.raw.txt`

结果：

```text
server-ops-health: PASS
```

关键读数：

1. 根分区：约 `40G`，已用约 `9.3G`，使用率约 `25%`。
2. backend 容器日志：约 `16K`。
3. frontend 容器日志：约 `28K`。
4. mysql 容器日志：约 `4.0K`。
5. 备份目录：约 `184K`。
6. Docker 镜像与 build cache 有可回收空间，但本轮不执行清理。

结论：

1. 当前磁盘压力低。
2. 日志轮转已生效，当前日志量很小。
3. 备份目录当前不构成空间风险。
4. Docker 可回收空间可留到后续“清理策略”轮次单独处理，不能在健康检查中自动 prune。

## 脱敏与安全

本轮报告没有提交：

1. 真实公网 IP。
2. 服务器密码。
3. 私钥内容。
4. GitHub token。
5. 腾讯地图 key。

报告中的 host 使用 `<redacted-host>`。

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
10. 服务器 `sshd_config`。
11. Docker 容器状态。
12. 备份文件。

## 验证结果

1. `server-ops-health.ps1` 真实运行通过。
2. `scripts/trial-operation/commands.ps1` 通过。
3. `git diff --check` 通过。
4. 敏感信息扫描无真实 secret / host 命中。

本轮只新增脚本和文档，不改业务代码，因此没有重新执行 backend compile 或 frontend build。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 146 建议做“内测服务器清理与留存策略 go / no-go”：

1. 先评估 Docker image / build cache 是否需要手动清理。
2. 先评估备份目录是否需要保留最近 N 份。
3. 不自动执行 prune。
4. 不自动删除备份。
5. 如果风险和收益不匹配，则继续保持只读健康检查，不做清理动作。

Step 146 已完成补充：

1. 新增 `docs/deployment/internal-trial-cleanup-retention.md`。
2. 结论为 no-go：当前不执行 Docker prune、不删除备份、不删除日志。
3. 清理触发条件和回滚边界已固化。

# 单机内测清理与留存策略

## 目标

本策略用于单机内测服务器的磁盘清理判断，避免两类问题：

1. Docker 镜像、build cache、容器日志或备份无限增长。
2. 在磁盘压力不高时误执行破坏性清理，影响回滚和排障。

当前原则：默认只观察，不自动清理。

## 当前基线

Step 145 只读运维健康检查显示：

1. 根分区使用率约 `25%`。
2. backend / frontend / mysql 容器日志均为 KB 级。
3. 备份目录约 `184K`。
4. Docker image / build cache 存在可回收空间，但当前不构成磁盘风险。

因此当前结论是：

1. 不执行 `docker system prune`。
2. 不删除备份。
3. 不删除容器日志。
4. 保留只读健康检查作为后续判断依据。

## 清理触发条件

只有出现以下任一情况，才进入清理评估：

1. 根分区使用率持续超过 `80%`。
2. Docker build cache 或 image 占用明显影响后续部署。
3. 备份目录持续增长且超过预设保留目标。
4. Docker 日志虽然有轮转，但排障确认不再需要旧日志。
5. 服务器部署或备份因磁盘空间失败。

没有上述信号时，不建议为了“看起来更干净”而清理。

## Docker 清理边界

优先级从低风险到高风险：

1. 只查看：
   ```bash
   docker system df
   docker builder du
   ```
2. 清理未使用 build cache：
   ```bash
   docker builder prune
   ```
3. 清理未使用 dangling image：
   ```bash
   docker image prune
   ```
4. 不建议直接执行：
   ```bash
   docker system prune -a
   ```

原因：

1. `docker system prune -a` 影响范围大。
2. 可能删除后续快速回滚需要的镜像。
3. 当前磁盘压力低，不值得承担该风险。

## 备份留存边界

当前备份目录很小，不需要清理。

后续如需清理，应遵循：

1. 至少保留最近 `3` 份完整备份。
2. 清理前先列出将删除的目录。
3. 不删除最近一次已通过 restore drill 的备份。
4. 删除后记录清理时间、删除对象和剩余备份。

建议先做 dry-run：

```bash
find /opt/campus-part-time-job/deploy/internal-trial/backups -maxdepth 1 -mindepth 1 -type d | sort
```

## 容器日志边界

当前日志轮转已启用：

1. driver: `json-file`
2. max-size: `20m`
3. max-file: `5`

因此当前不手工删除容器日志。

如果必须释放日志空间：

1. 先确认容器日志已经轮转。
2. 先保存必要排障片段。
3. 优先通过重建容器或调整 `DOCKER_LOG_MAX_SIZE / DOCKER_LOG_MAX_FILE` 控制后续增长。
4. 不直接编辑正在使用的 Docker log 文件。

## 当前 no-go 结论

基于 Step 145 的真实读数，当前不执行清理动作。

原因：

1. 根分区使用率低。
2. 容器日志体量极小。
3. 备份目录体量极小。
4. Docker 可回收空间尚未影响部署。
5. 清理动作收益低于误删和降低回滚能力的风险。

## 下一次复核建议

建议在以下场景复核：

1. 每次较大版本部署后。
2. 备份目录明显增长后。
3. Docker build 失败或镜像拉取失败后。
4. 服务器磁盘使用率超过 `70%` 后。
5. 内测运行超过一周且持续有真实访问后。

# 单机内测日志留存与轮转策略

## 当前定位

这份文档面向当前单机内测型试运营环境。

目标是解决两个问题：

1. Docker 容器日志不能无限增长。
2. 出现问题时仍能保留最近一段可排查日志。

它不是正式生产级日志平台方案，不包含集中式日志、告警、链路追踪或长期审计归档。

## 当前落地策略

当前 Compose 已对 `mysql`、`backend`、`frontend` 三个服务统一启用 Docker `json-file` 日志轮转：

```yaml
logging:
  driver: json-file
  options:
    max-size: "${DOCKER_LOG_MAX_SIZE:-20m}"
    max-file: "${DOCKER_LOG_MAX_FILE:-5}"
```

默认含义：

1. 单个日志文件最大 `20m`。
2. 每个容器最多保留 `5` 个日志文件。
3. 单个容器理论日志上限约为 `100m`。
4. 三个核心容器合计理论上限约为 `300m`。

这能覆盖当前内测排障，同时避免长期运行后 Docker 日志占满磁盘。

## 可调参数

在服务器 `deploy/internal-trial/.env` 中可调整：

```env
DOCKER_LOG_MAX_SIZE=20m
DOCKER_LOG_MAX_FILE=5
```

调整建议：

1. owner 自测或少量内测：保持默认即可。
2. 如果排障时日志不够：临时调大 `DOCKER_LOG_MAX_FILE`，排障后改回。
3. 如果磁盘紧张：优先降为 `10m / 3`，不要直接关闭日志。

修改后需要重建或重启 compose：

```bash
cd /opt/campus-part-time-job
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml up -d
```

## 日常查看命令

查看 backend 最近日志：

```bash
cd /opt/campus-part-time-job
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs backend --tail=200
```

查看 frontend 最近日志：

```bash
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs frontend --tail=200
```

查看 MySQL 最近日志：

```bash
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs mysql --tail=200
```

查看 Docker 实际日志文件大小：

```bash
docker inspect --format='{{.LogPath}}' campus-trial-backend-1
sudo du -h "$(docker inspect --format='{{.LogPath}}' campus-trial-backend-1)"
```

如果 compose project name 被修改，容器名也会随之变化。先用以下命令确认真实容器名：

```bash
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml ps
```

## 部署后检查

每次更新部署后至少确认：

```bash
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-backend-1
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-frontend-1
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-mysql-1
```

期望看到：

```json
{"Type":"json-file","Config":{"max-file":"5","max-size":"20m"}}
```

如果容器名不一致，先用 `docker compose ps` 获取真实名称。

## 当前明确不做

当前不做：

1. 不接 ELK / Loki / Prometheus。
2. 不做正式监控告警。
3. 不做日志远端归档。
4. 不把 `.env`、token、密码或真实公网 IP 写入日志文档。
5. 不通过日志策略修改业务代码、bridge、鉴权或接口语义。

## 与备份的关系

当前 `backup-stack.sh` 仍只备份：

1. MySQL dump。
2. uploads volume。
3. `.env`。
4. manifest。

容器运行日志不纳入备份包。原因是当前日志主要服务短期排障，不作为业务审计主数据。

如果后续需要长期审计，应另开“正式日志归档 / 监控告警”主线，不在当前单机内测策略中硬塞。

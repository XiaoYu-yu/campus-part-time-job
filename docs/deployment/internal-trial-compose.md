# 内测型试运营 Compose 部署说明

## 适用范围

这套部署包面向：

1. 单机服务器上的内测型试运营。
2. 本项目当前“模拟支付 / 模拟退款 / 模拟打款”的稳定演示与试运营闭环。
3. 不要求真实支付、真实地图 WebService、真实消息推送或完整生产监控的场景。

它不是正式生产部署方案，但比单纯手工启动更接近可交付的内测环境。

## 部署工件

当前仓库新增了以下内测部署工件：

1. `deploy/internal-trial/docker-compose.yml`
2. `deploy/internal-trial/backend.Dockerfile`
3. `deploy/internal-trial/frontend.Dockerfile`
4. `deploy/internal-trial/nginx.conf`
5. `deploy/internal-trial/.env.example`

## 前置条件

目标服务器至少需要：

1. Docker Engine
2. Docker Compose Plugin
3. 可用的 80 / 8080 / 3306 端口，或你自己在 `.env` 中改端口映射
4. 一个真实但未提交到仓库的腾讯地图 JS SDK key

如果你暂时不演示地图，仍然建议保留 `VITE_TENCENT_MAP_KEY` 配置位，但可以在演示口径中明确该能力属于只读预览。

## 首次准备

1. 复制环境样例：

```bash
cp deploy/internal-trial/.env.example deploy/internal-trial/.env
```

2. 至少修改这些值：
   - `DB_PASSWORD`
   - `MYSQL_ROOT_PASSWORD`
   - `JWT_SECRET`
   - `APP_CORS_ALLOWED_ORIGINS`
   - `VITE_TENCENT_MAP_KEY`
3. 如果前端直接通过服务器 IP 访问，把服务器访问地址补进 `APP_CORS_ALLOWED_ORIGINS`。

## 启动命令

在项目根目录执行：

```bash
docker compose \
  --env-file deploy/internal-trial/.env \
  -f deploy/internal-trial/docker-compose.yml \
  up -d --build
```

## 组件说明

### MySQL

1. 使用 `mysql:8.4`
2. 首次启动时自动执行 `backend/db/init.sql`
3. 数据卷：
   - `mysql-data`

### Backend

1. 使用 `deploy/internal-trial/backend.Dockerfile`
2. 容器内固定 `SPRING_PROFILES_ACTIVE=prod`
3. 通过环境变量注入数据库、JWT、CORS 和上传目录
4. 上传目录挂到数据卷：
   - `uploads-data`

### Frontend

1. 使用 `deploy/internal-trial/frontend.Dockerfile`
2. 构建时注入：
   - `VITE_API_BASE_URL=/api`
   - `VITE_TENCENT_MAP_KEY`
3. 运行时由 Nginx 提供静态资源，并把 `/api/` 代理到 backend 容器

## 首次启动后检查

建议按顺序确认：

1. `docker compose ps`
2. `docker compose logs backend --tail=200`
3. `docker compose logs frontend --tail=200`
4. `docker compose logs mysql --tail=200`
5. 打开前端首页
6. 按 [部署后 Smoke Checklist](post-deploy-smoke-checklist.md) 做最小 smoke

日常启动、停止、备份、恢复演练、smoke 和正式入口 go/no-go 统一见：

- [单机内测试运营运维 Runbook](internal-trial-ops-runbook.md)

## 备份与回滚

当前单机内测部署的最小备份入口：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/backup-stack.sh
```

该脚本会同时备份：

1. MySQL 数据
2. uploads volume
3. 当前 `.env`
4. manifest

当前推荐的非破坏性恢复演练入口：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/restore-drill.sh
```

它会把最近一次备份恢复到临时 MySQL 容器和临时目录，用于验证 dump 与 uploads 归档是否可恢复，不会改动正在运行的 compose 栈。

详细口径见：

- [备份与回滚说明](backup-and-rollback.md)

## 访问入口

默认端口映射：

1. frontend: `http://<server-ip>:${FRONTEND_PORT}`
2. backend: `http://<server-ip>:${BACKEND_PORT}`
3. mysql: `<server-ip>:${MYSQL_PORT}`

如不希望暴露 backend / mysql，可在内网环境里自行改 compose 端口映射。

## 回滚与重置

停止：

```bash
docker compose \
  --env-file deploy/internal-trial/.env \
  -f deploy/internal-trial/docker-compose.yml \
  down
```

删除容器并保留数据卷：

```bash
docker compose \
  --env-file deploy/internal-trial/.env \
  -f deploy/internal-trial/docker-compose.yml \
  down
```

删除容器和数据卷，回到首次初始化状态：

```bash
docker compose \
  --env-file deploy/internal-trial/.env \
  -f deploy/internal-trial/docker-compose.yml \
  down -v
```

## 当前明确不覆盖的能力

1. 真实支付
2. 真实退款
3. 真实打款
4. 完整生产监控
5. 自动备份与恢复编排
6. HTTPS / 域名 / CDN 自动配置
7. 多节点扩容
8. bridge 收口动作

## 建议口径

当前这套 compose 部署包适合作为“本地 / 单机服务器 / 内测型试运营”的交付包，而不是正式商业生产部署包。

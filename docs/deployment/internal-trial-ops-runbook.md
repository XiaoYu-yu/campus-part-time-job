# 单机内测试运营运维 Runbook

## 当前定位

这份 runbook 面向当前单机服务器内测环境。

它解决的问题是：

1. 日常怎么查看服务状态
2. 怎么启动 / 停止 / 重建
3. 怎么备份
4. 怎么做非破坏性恢复演练
5. 怎么做最小 smoke
6. 什么时候才需要进入 HTTPS / 域名 / 反向代理正式入口准备

它不是正式生产 SRE 手册，也不替代真实生产监控、审计、备份策略。

## 不写入仓库的内容

以下内容只允许放在服务器 `.env`、本地环境变量或安全凭据管理处，不允许提交到仓库：

1. 服务器密码
2. 数据库密码
3. JWT secret
4. 腾讯地图 key
5. 任何访问 token

## 当前服务器约定

当前单机内测部署默认约定：

1. 仓库路径：`/opt/campus-part-time-job`
2. Compose 文件：`deploy/internal-trial/docker-compose.yml`
3. 真实环境变量：`deploy/internal-trial/.env`
4. 前端访问：`http://<server-ip>/`
5. backend 访问：`http://<server-ip>:8080/`

注意：

1. GitHub `main` 是源码真相来源。
2. 服务器工作区只作为部署目录，不作为开发源。
3. 若服务器工作区出现 dirty 状态，先确认是否来自部署热修或上传脚本，不要直接把服务器改动反向当成产品需求。

## 日常状态检查

在服务器执行：

```bash
cd /opt/campus-part-time-job
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml ps
```

至少确认：

1. `mysql` 为 `Up`
2. `backend` 为 `Up`
3. `frontend` 为 `Up`

基础 HTTP 检查：

```bash
curl -I http://127.0.0.1/
```

期望：

1. 返回 `HTTP/1.1 200`
2. 或等价 `200` 响应

## 查看日志

查看 backend：

```bash
cd /opt/campus-part-time-job
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs backend --tail=200
```

查看 frontend：

```bash
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs frontend --tail=200
```

查看 MySQL：

```bash
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml logs mysql --tail=200
```

## 启动或重建

常规启动 / 重建：

```bash
cd /opt/campus-part-time-job
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml up -d --build
```

适用场景：

1. 首次部署
2. 代码更新后重新构建
3. 容器异常退出后重建

执行后必须至少做：

1. `docker compose ps`
2. `curl -I http://127.0.0.1/`
3. 按 [部署后 Smoke Checklist](post-deploy-smoke-checklist.md) 做最小 smoke

## 停止服务

停止容器但保留数据卷：

```bash
cd /opt/campus-part-time-job
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml down
```

注意：

1. 内测环境日常维护优先使用此命令。
2. 不要随手执行 `down -v`。

## 重置环境

删除容器和数据卷，回到首次初始化状态：

```bash
cd /opt/campus-part-time-job
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml down -v
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml up -d --build
```

只有在明确需要重置全部试运营数据时才允许执行。

执行前必须：

1. 先跑一次 `backup-stack.sh`
2. 确认备份产物已生成
3. 明确本次重置会清空当前 MySQL 数据卷

## 备份

推荐命令：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/backup-stack.sh
```

备份内容：

1. MySQL dump
2. uploads volume
3. `.env`
4. manifest

备份后至少确认：

```bash
ls -lh deploy/internal-trial/backups/mysql
ls -lh deploy/internal-trial/backups/uploads
ls -lh deploy/internal-trial/backups/meta
```

## 非破坏性恢复演练

推荐命令：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/restore-drill.sh
```

它会：

1. 启一个临时 MySQL 容器
2. 恢复最近一次 SQL dump
3. 校验关键样本订单
4. 解压 uploads 归档
5. 自动清理临时容器

它不会：

1. 覆盖当前 MySQL 数据卷
2. 停止当前 compose 栈
3. 修改当前运行中的业务数据

## 最小 smoke

部署后至少检查：

1. 前端首页能打开
2. admin 登录可用
3. customer 登录可用
4. courier token 可申请
5. admin settlement / after-sale / exception 页面对应接口可读
6. customer completed 结果回读可用
7. courier profile / review-status / available orders 可读

详细清单：

- [部署后 Smoke Checklist](post-deploy-smoke-checklist.md)

## 更新部署

推荐顺序：

1. 确认 GitHub `main` 已包含目标提交。
2. 在服务器进入 `/opt/campus-part-time-job`。
3. 先备份：

```bash
bash deploy/internal-trial/backup-stack.sh
```

4. 拉取代码前确认服务器工作区状态：

```bash
git status --short
```

5. 若存在本地 dirty 状态，先判断来源：
   - 如果是 `deploy/internal-trial/backups/`，它应被忽略，不应提交。
   - 如果是部署热修文件，先和 GitHub `main` 比对，不要直接覆盖。
   - 如果无法确认，不要继续更新，先保留现场。
6. 确认安全后再更新代码并重建：

```bash
git pull --ff-only origin main
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml up -d --build
```

7. 更新后必须跑 smoke。

## 回滚优先级

优先级 1：只回滚代码 / 镜像，不回滚数据。

适用：

1. 前端资源异常
2. backend 新版本启动失败
3. 非数据层的容器异常

优先级 2：只恢复 `.env`。

适用：

1. 配置误改
2. CORS / JWT / 地图 key 配置错误

优先级 3：数据恢复。

适用：

1. 数据被误删
2. schema 迁移导致数据不可用
3. 试运营数据被破坏

当前不提供一键数据恢复脚本。需要恢复数据时，先保留现场，再按 [备份与回滚说明](backup-and-rollback.md) 做保守恢复。

## 回滚触发条件

出现以下任一情况，应停止继续操作并考虑回滚：

1. `backend` 无法稳定启动
2. `frontend` 首页不可访问
3. admin / customer / courier 任一角色完全无法登录
4. onboarding 或 courier workbench 主入口不可用
5. bridge 接口被误删或误收紧
6. 页面出现真实支付、真实退款、真实打款误导性行为
7. 数据库出现明显 schema / seed 漂移

## HTTPS / 域名 / 反向代理 go-no-go

### 当前结论

当前仍是单机服务器内测型试运营阶段。

如果只是 owner 自测或少量内测访问：

1. 暂不强制进入 HTTPS / 域名 / 正式反向代理配置。
2. 可以继续使用服务器 IP 访问。
3. 更高优先级是保证备份、restore drill、smoke 和回滚边界稳定。

### 需要进入正式入口准备的触发条件

出现以下情况，再进入 HTTPS / 域名 / 反向代理准备：

1. 开始邀请非开发者长期访问
2. 需要微信 / 移动端真实环境稳定访问
3. 浏览器安全策略开始影响地图、登录或 token 行为
4. 需要把 backend / mysql 从公网端口收回到内网
5. 需要更正式的访问日志、证书续期和反向代理规则

### 进入正式入口前必须先做

1. 确认域名已备案或可用于当前服务器
2. 准备 HTTPS 证书申请与续期方案
3. 收紧 compose 对外端口，优先只暴露 80 / 443
4. 更新 `APP_CORS_ALLOWED_ORIGINS`
5. 重新构建 frontend，确认 `VITE_API_BASE_URL=/api`
6. 跑完整部署后 smoke

## 当前冻结主线

继续保持：

1. bridge 主线 `Phase A no-op` 冻结态
2. 展示 polish 线冻结
3. 媒体线收住

本 runbook 不允许被解读成：

1. 可以删除 bridge
2. 可以接真实支付
3. 可以重开页面 polish
4. 可以把单机内测直接宣称为正式生产上线

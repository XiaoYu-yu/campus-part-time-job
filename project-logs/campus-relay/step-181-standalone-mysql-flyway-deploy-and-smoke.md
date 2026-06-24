# Step 181 - 138 standalone MySQL + Flyway 部署与 smoke 验证

## 本次目标

继续 Step 180 的数据库正式化路线，在 owner 授权的 `192.168.121.138 / master` 上完成隔离 standalone MySQL 部署验证：

1. 不复用、不修改 138 宿主机 Hive metastore MySQL。
2. 不启动、停止或重启 Hadoop / Hive / HBase / ZooKeeper 等集群服务。
3. 使用独立 `campus-standalone-*` 容器、网络和数据卷验证 MySQL + Flyway。
4. 解决实际部署中暴露的镜像 / 依赖 / 数据库迁移兼容问题。
5. 通过 API、前端 SPA shell、Flyway 表和种子数据检查证明部署可用。

## 已完成项

### 1. 解决 MySQL Flyway 运行时适配问题

`backend/pom.xml` 在 `flyway-core` 基础上新增：

- `org.flywaydb:flyway-mysql`

原因：Spring Boot 当前依赖栈下，仅引入 `flyway-core` 时后端在 MySQL 8.x 启动阶段报 `Unsupported Database: MySQL 8.0` / `Unsupported Database: MySQL 8.4`，无法执行迁移；加入 MySQL database support 模块后，Flyway 可识别 MySQL 8.0 并执行 V1..V14。

### 2. 修正 standalone 前端镜像默认基线

将默认 Node 构建镜像从 Alpine 切到 Debian slim：

- `node:20-alpine` -> `node:20-bookworm-slim`

原因：138 上 frontend 容器构建时，Alpine 下 `lightningcss` 的 musl optional native package 没有按 lockfile 稳定落入 `node_modules`，导致 `npm run build` 失败。Debian slim 走 glibc native package，`npm ci` 保持 lockfile，不引入额外依赖漂移，构建通过。

涉及文件：

- `deploy/internal-trial/frontend.Dockerfile`
- `deploy/standalone-podman/deploy.sh`
- `deploy/standalone-podman/deploy-h2-smoke.sh`
- `deploy/standalone-podman/README.md`

### 3. 固定 standalone MySQL 默认镜像到 8.0

`deploy/standalone-podman/deploy.sh` 默认 MySQL 镜像从：

- `docker.m.daocloud.io/library/mysql:8.4`

调整为：

- `docker.m.daocloud.io/library/mysql:8.0`

原因：当前 Flyway 版本对 MySQL 8.4 兼容性不稳，standalone 内测部署优先选择 MySQL 8.0 作为更稳的持久化基线。

### 4. 同步代码到 138 并完成隔离部署

已同步当前代码到：

- `/opt/campus-part-time-job-standalone`

部署前确认：

- 仅存在 `campus-standalone-uploads-data` 上传卷。
- 无残留 `campus-standalone-*` 容器。
- 无残留 standalone MySQL 数据卷。

执行：

```bash
cd /opt/campus-part-time-job-standalone
bash deploy/standalone-podman/deploy.sh
```

部署结果：

```text
Standalone deployment is ready.
Frontend: http://0.0.0.0:18080
Backend host binding: 127.0.0.1:18081
MySQL host binding: 127.0.0.1:13306
```

当前容器：

```text
campus-standalone-mysql     Up  127.0.0.1:13306->3306/tcp
campus-standalone-backend   Up  127.0.0.1:18081->8080/tcp
campus-standalone-frontend  Up  0.0.0.0:18080->80/tcp
```

### 5. Flyway 与 seed 数据验证

在 standalone MySQL 容器内检查：

```text
flyway_rows    successful_rows    max_rank
14             14                 14

version    description                         success
14         campus feedback admin processing     1
```

关键 campus 表和 seed 数据已存在：

```text
campus_courier_profile      2
campus_customer_profile     2
campus_feedback             2
campus_location_report      1
campus_pickup_point         2
campus_relay_order          3
campus_settlement_record    1
```

结论：后端 jar 中的 `db/migration/V1..V14` 已在真实 MySQL 上完整执行，内测 seed 已导入。

### 6. 健康检查与远程 smoke

后端直连健康检查：

```bash
curl http://127.0.0.1:18081/api/campus/public/health
```

前端 Nginx 代理健康检查：

```bash
curl http://127.0.0.1:18080/api/campus/public/health
```

均返回 `code=200` / `status=UP`。

远程 smoke：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 `
  -ApiBase http://192.168.121.138:18080/api `
  -FrontendBase http://192.168.121.138:18080/ `
  -OutputPath project-logs\campus-relay\runtime\step-181-standalone-mysql-flyway-smoke\remote-smoke-report.json
```

结果：

```text
27 PASS / 0 FAIL / 0 SKIP
```

报告文件：

- `project-logs/campus-relay/runtime/step-181-standalone-mysql-flyway-smoke/remote-smoke-report.json`

覆盖范围：

- public health
- admin 登录和运营接口
- customer 登录和校园代送接口
- parttime token、资料、可接单、已完成订单详情
- 前端首页、admin 登录 / dashboard / settlements / after-sale / exceptions / feedback
- 用户端订单结果页
- 兼职端工作台

## 修改文件

- `backend/pom.xml`
- `deploy/internal-trial/frontend.Dockerfile`
- `deploy/standalone-podman/deploy.sh`
- `deploy/standalone-podman/deploy-h2-smoke.sh`
- `deploy/standalone-podman/README.md`
- `project-logs/campus-relay/runtime/step-181-standalone-mysql-flyway-smoke/remote-smoke-report.json`
- `project-logs/campus-relay/step-181-standalone-mysql-flyway-deploy-and-smoke.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 验证结果

1. 本地后端测试：

   ```text
   Tests run: 58, Failures: 0, Errors: 0, Skipped: 0
   BUILD SUCCESS
   ```

2. 138 standalone 部署：

   - MySQL：启动成功。
   - Backend：启动成功。
   - Frontend：启动成功。

3. Flyway：

   - `flyway_schema_history` 共 14 条。
   - 14 条全部 `success=1`。
   - 最新迁移为 V14。

4. 数据：

   - seed 账号、订单、反馈、结算样本已导入。

5. 远程 smoke：

   - 27 PASS / 0 FAIL / 0 SKIP。

## 本轮明确未改动

- 未使用 138 宿主机 MySQL。
- 未修改 Hive metastore 数据库。
- 未启动、停止或重启 Hadoop / Hive / HBase / ZooKeeper / 宿主机 MySQL。
- 未删除 HDFS、Hive、ZooKeeper、MySQL 或 HBase 数据目录。
- 未提交真实 `.env`、密码、证书私钥、release keystore、GitHub token、腾讯地图 key 或服务器凭据。

## 遗留问题

1. 138 standalone MySQL 当前绑定为 `127.0.0.1:13306`，Windows Navicat 不能直接连这个端口；推荐通过 SSH tunnel 连接，或在明确需要时再放开为 `0.0.0.0` 并配防火墙。
2. 当前仍是局域网内测部署，不是正式公网发布；域名、HTTPS、证书、正式 release 签名 APK / AAB 仍待 owner 最终材料。
3. `backend/db/init.sql` 仍保留为历史人工参考，后续应标注 deprecated 或迁入文档说明，避免误当正式部署入口。
4. 后续如新增迁移，需在当前 standalone MySQL 数据卷上验证 Flyway 增量迁移，而不是每次清空数据卷。

## 下一步建议

1. 将本轮修正提交并推送到 GitHub，确保 138 后续可 `git pull`。
2. 如果 owner 要用 Navicat 看 standalone MySQL，优先使用 SSH tunnel：

   ```bash
   ssh -L 13306:127.0.0.1:13306 root@192.168.121.138
   ```

   Navicat 连接本机 `127.0.0.1:13306` 即可。

3. 补一份 standalone MySQL 备份 / 恢复 runbook。
4. 后续上线前继续处理正式签名包、HTTPS / 域名、生产 secrets 管理和发布回滚脚本。

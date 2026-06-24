# Step 180 - MySQL Flyway 正式化数据库改造

## 本次目标

owner 要求继续推进项目数据库方向，前置问题是当前部署主要依赖 H2 smoke，正式上线前需要更适合持久化部署的数据库方案。

本轮目标：

1. 保留 H2 作为测试 / smoke fallback，不把 H2 当正式数据库。
2. 将正式 `prod` profile 切到 MySQL + Flyway 自动迁移。
3. 让 `backend/db/migrations/V1..V14` 真正进入后端 jar classpath。
4. 修正现有迁移链中会导致顺序执行失败的重复字段问题。
5. 将 standalone MySQL 部署从“挂载整包 init.sql + 手工补 V14”改为“空库 + 后端 Flyway + 可选内测 seed”。

## 已完成项

### 1. 后端接入 Flyway

- `backend/pom.xml` 新增 `org.flywaydb:flyway-core`。
- Maven resources 新增：
  - 源目录：`backend/db/migrations`
  - 打包目标：`classpath:db/migration`
- `application-prod.properties` 默认启用 Flyway：
  - `spring.flyway.enabled=${DB_MIGRATION_ENABLED:true}`
  - `spring.flyway.locations=classpath:db/migration`
  - `spring.flyway.validate-on-migrate=true`
  - `spring.flyway.clean-disabled=true`
- `application-test.properties` 明确禁用 Flyway，继续使用 H2 `schema-h2.sql` / `data-h2.sql`。
- `application-dev.properties` 默认不自动迁移，保留 `DB_MIGRATION_ENABLED=true` 手动开启能力，避免误碰已有本地 MySQL。

### 2. 迁移链修正

已修正 `backend/db/migrations/V2__campus_relay_schema.sql`：

- 移除 V2 中已经由 V3 负责追加的字段：
  - `paid_at`
  - `cancelled_at`
  - `after_sale_applied_at`
  - `cancel_reason`

修正后，V1 -> V14 在结构层面不再出现已知重复 `ADD COLUMN` 问题。

### 3. prod MySQL 参数上线化

`application-prod.properties` 支持通过环境变量控制 MySQL SSL / 公钥获取：

- `DB_USE_SSL`，默认 `true`。
- `DB_ALLOW_PUBLIC_KEY_RETRIEVAL`，默认 `false`。

standalone 内网 MySQL 默认使用：

- `DB_USE_SSL=false`
- `DB_ALLOW_PUBLIC_KEY_RETRIEVAL=true`

正式外部数据库仍建议按实际证书和网络边界开启 SSL。

### 4. standalone MySQL 部署链路改造

`deploy/standalone-podman/deploy.sh` 已改为：

1. 启动空 MySQL 容器。
2. 启动后端 `prod` profile。
3. 后端启动时由 Flyway 自动执行 `V1..V14`。
4. 后端健康检查通过后，可选导入幂等内测 seed。
5. 再启动前端。

已移除旧逻辑：

- 不再将 `backend/db/init.sql` 挂入 `/docker-entrypoint-initdb.d/01-init.sql`。
- 不再手工检测 `campus_feedback.admin_note` 并单独执行 V14。

### 5. 新增 MySQL 内测 seed

新增：

- `backend/db/seed/README.md`
- `backend/db/seed/internal-trial-data.sql`

`internal-trial-data.sql` 用于 isolated standalone / LAN smoke，不参与生产结构迁移，包含：

- 管理员账号：`13800138000 / 123456`
- 用户账号：`13900139000 / 123456`
- 兼职账号：`13900139001 / 123456`
- pickup point 示例
- 可接订单 `CR202604070002`
- 已完成订单 `CR202604060001`
- location / settlement / feedback 示例

脚本使用 `ON DUPLICATE KEY UPDATE`，重复部署时不会因主键冲突失败。

### 6. 文档和环境变量更新

已更新：

- `backend/.env.example`
- `deploy/standalone-podman/.env.example`
- `deploy/standalone-podman/init-env.sh`
- `backend/db/migrations/README.md`
- `deploy/standalone-podman/README.md`

新增 / 说明的关键变量：

- `DB_MIGRATION_ENABLED`
- `DB_FLYWAY_BASELINE_ON_MIGRATE`
- `DB_FLYWAY_BASELINE_VERSION`
- `DB_USE_SSL`
- `DB_ALLOW_PUBLIC_KEY_RETRIEVAL`
- `APP_SEED_INTERNAL_TRIAL`

## 修改文件

- `backend/pom.xml`
- `backend/.env.example`
- `backend/db/migrations/README.md`
- `backend/db/migrations/V2__campus_relay_schema.sql`
- `backend/db/seed/README.md`
- `backend/db/seed/internal-trial-data.sql`
- `backend/src/main/resources/application-dev.properties`
- `backend/src/main/resources/application-prod.properties`
- `backend/src/main/resources/application-test.properties`
- `deploy/internal-trial/backend.Dockerfile`
- `deploy/standalone-podman/.env.example`
- `deploy/standalone-podman/README.md`
- `deploy/standalone-podman/deploy.sh`
- `deploy/standalone-podman/init-env.sh`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 验证结果

### 1. 后端全量测试

```bash
cd backend
./mvnw test
```

结果：

```text
Tests run: 58, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

结论：H2 test profile 未被 Flyway 误伤，现有后端业务测试全绿。

### 2. 后端打包

```bash
cd backend
./mvnw -q -DskipTests package
```

结果：通过。

### 3. jar 内容检查

已确认 `target/takeaway-0.0.1-SNAPSHOT.jar` 包含：

- `BOOT-INF/lib/flyway-core-9.22.3.jar`
- `BOOT-INF/classes/db/migration/V1__baseline_schema.sql`
- `BOOT-INF/classes/db/migration/V2__campus_relay_schema.sql`
- ...
- `BOOT-INF/classes/db/migration/V14__campus_feedback_admin_processing.sql`

### 4. 迁移静态重复字段检查

对 `backend/db/migrations/V*.sql` 做了简单静态扫描，未发现本轮修正后仍存在的重复 `ADD COLUMN` 输出。

### 5. 样本校验

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1
```

结果：

- 0 hard failure
- 3 warning

三个 warning 是既有可选固定样本未预置：

- `campus_after_sale_execution_record`
- `campus_exception_record`
- `campus_settlement_reconcile_difference_record`

本轮未改变该策略，仍通过运行态操作生成这些样本。

### 6. diff 检查

```bash
git diff --check
```

结果：通过。

### 7. 未完成的验证

本轮没有在真实 MySQL 实例上执行 Flyway 迁移：

- 没有使用 138 上的 Hive metastore MySQL。
- 没有创建、删除或修改 138 宿主机 MySQL 数据库。
- 没有启动或停止 138 的 MySQL / Hive / Hadoop / HBase / ZooKeeper。

原因：138 上 MySQL 属于 Hive 上课环境相关组件，本轮优先避免误碰；standalone MySQL 镜像链路可在下一轮窗口单独验证。

## 遗留问题

1. 需要在隔离 MySQL 上跑一次真实 Flyway 启动验证：
   - 优先使用 standalone Podman MySQL；
   - 或在 138 上创建完全独立的新 MySQL 容器 / 数据卷；
   - 不复用 Hive metastore MySQL。
2. 旧 `backend/db/init.sql` 仍保留为历史人工参考脚本，后续可单独评估是否拆分或标记 deprecated。
3. 若已有手工初始化的 MySQL schema，需要人工确认结构完整后才可开启：
   - `DB_FLYWAY_BASELINE_ON_MIGRATE=true`
   - `DB_FLYWAY_BASELINE_VERSION=14`
4. H2 仍用于 test profile 和 H2 smoke fallback，不应作为正式持久化部署数据库。

## 下一步建议

1. 在 138 的 isolated standalone 环境跑一次 MySQL + Flyway 部署：

   ```bash
   cd /opt/campus-part-time-job-standalone
   git pull --ff-only
   bash deploy/standalone-podman/deploy.sh
   ```

2. 部署成功后执行：

   ```bash
   podman exec campus-standalone-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" campus_standalone -e "select version, success from flyway_schema_history order by installed_rank;"
   ```

3. 复跑 remote smoke，确认 MySQL 持久化栈具备登录、下单、模拟支付、接单和详情读取能力。
4. MySQL 栈稳定后，再决定是否停止 138 上的 H2 smoke-only 栈。

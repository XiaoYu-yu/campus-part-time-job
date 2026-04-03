# Database Migrations

这里存放面向部署的数据库迁移脚本，和 `backend/db/init.sql` 的用途不同：

- `init.sql`：用于快速初始化本地演示或一次性手工建库
- `migrations/`：用于按版本演进数据库结构，便于生产部署、审计和回滚

当前提供：

- `V1__baseline_schema.sql`：基线表结构

后续每次变更数据库结构时，新增一个版本脚本，保持只追加、不覆盖历史版本。

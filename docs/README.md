# 项目文档索引

这个目录只保留当前“校内兼职平台 / 校园代送试运营版”需要直接阅读的文档。旧苍穹外卖阶段材料已归档到 `legacy-takeaway/`，不再作为当前主线入口。

## 当前交付文档

- [交付与启动说明](delivery-guide.md)
- [试运营运行配置与 Preflight 手册](trial-operation-preflight.md)
- [模拟资金链路产品化边界](simulated-funds-boundary.md)
- [用户端 / 兼职端 Android 壳 scaffold 方案](mobile/android-shell-scaffold-plan.md)
- [Android 壳工程说明](../mobile/README.md)
- [项目状态检查](project-status-review.md)
- [API 总览](api-overview.md)
- [数据库总览](db-overview.md)

## 校园代送规划与映射

- [领域模型重构规划](campus-relay/domain-refactor-plan.md)
- [旧模块到校园代送模块映射](campus-relay/legacy-to-campus-mapping.md)

## 部署与运维

- [内测型试运营 Compose 部署说明](deployment/internal-trial-compose.md)
- [生产部署说明](deployment/production-deploy.md)
- [环境变量与密钥配置清单](deployment/env-and-secret-checklist.md)
- [部署后 Smoke Checklist](deployment/post-deploy-smoke-checklist.md)
- [最小 CI 检查边界](deployment/ci-check-boundary.md)
- [备份与回滚说明](deployment/backup-and-rollback.md)
- [单机内测试运营运维 Runbook](deployment/internal-trial-ops-runbook.md)
- [数据库迁移说明](../backend/db/migrations/README.md)

## 当前主线日志

- [校园代送总览](../project-logs/campus-relay/summary.md)
- [待处理事项](../project-logs/campus-relay/pending-items.md)
- [文件改动清单](../project-logs/campus-relay/file-change-list.md)
- [bridge 收口评估](../project-logs/campus-relay/bridge-phaseout-evaluation.md)
- [交付整理与演示脚本](../project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md)
- [媒体采集与归档](../project-logs/campus-relay/step-42-real-media-capture-and-archive.md)

## 旧外卖阶段归档

- [旧外卖文档归档说明](legacy-takeaway/README.md)
- [旧 API 设计](legacy-takeaway/api-design.md)
- [旧数据库设计](legacy-takeaway/db-design.md)
- [旧修复日志归档](../project-logs/legacy-takeaway/summary.md)

## 维护规则

1. 当前 campus 主线文档优先放在 `docs/` 或 `project-logs/campus-relay/`。
2. 旧外卖材料只做参考，不再挂在 README 主入口。
3. 新增数据库结构必须同步维护 `backend/db/init.sql`、`backend/db/migrations/` 与 H2 schema。
4. 新增阶段任务必须同步更新 `project-logs/campus-relay/summary.md`、`pending-items.md`、`file-change-list.md`。

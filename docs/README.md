# Docs

这个目录存放项目文档入口、部署说明和维护材料索引。文档内容以当前仓库真实代码状态为准。

## Navigation

### 项目入口

- [仓库首页说明](../README.md)
- [项目交付说明](../项目交付说明.md)
- [项目检查说明](../项目检查说明.md)

### 工程与交付

- [变更记录](../CHANGELOG.md)
- [贡献说明](../CONTRIBUTING.md)
- [安全说明](../SECURITY.md)
- [前端说明](../frontend/README.md)

### 校园代送改造规划

- [领域模型重构规划](campus-relay/domain-refactor-plan.md)
- [旧模块到校园代送模块映射](campus-relay/legacy-to-campus-mapping.md)
- [校园代送改造总览](../project-logs/campus-relay/summary.md)
- [校园代送 Step 01 日志](../project-logs/campus-relay/step-01-domain-model-planning.md)
- [校园代送 Step 02A 日志](../project-logs/campus-relay/step-02a-db-and-backend-skeleton.md)
- [校园代送 Step 02B 日志](../project-logs/campus-relay/step-02b-readonly-api.md)
- [校园代送 Step 03A 日志](../project-logs/campus-relay/step-03a-customer-order-create-and-pay.md)
- [校园代送 Step 03B 日志](../project-logs/campus-relay/step-03b-courier-profile-and-admin-review.md)
- [校园代送 Step 03C 日志](../project-logs/campus-relay/step-03c-courier-token-and-accept.md)

### 部署与数据库

- [数据库设计](db-design.md)
- [接口设计](api-design.md)
- [生产部署说明](deployment/production-deploy.md)
- [备份与回滚](deployment/backup-and-rollback.md)

### 修复与发布日志

- [修复总览](../project-logs/summary.md)
- [生产准备日志](../project-logs/production-readiness.md)
- [交付整理日志](../project-logs/delivery-handover.md)
- [GitHub 发布日志](../project-logs/github-release.md)
- [环境搭建日志](../project-logs/runtime-setup.md)
- [文件改动清单](../project-logs/file-change-list.md)
- [待处理事项](../project-logs/pending-items.md)

## Current Notes

- 仓库首页 README 已按 GitHub 开源仓库入口方式重构
- `docs/screenshots/` 目前仍不存在，因此不引用失效截图
- 当前文档重点是启动、交付、部署、修复记录和发布说明
- `campus-relay/` 已进入后端接口闭环阶段，当前已具备 public/admin/customer/courier 的最小后端能力
- 若后续补 API 详细文档、ER 图或演示素材，建议继续按子目录扩展

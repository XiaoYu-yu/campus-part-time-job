# Step 184 - 仓库介绍去旧项目化与 campus-first 口径整理

## 本次目标

owner 要求修改仓库介绍，把“苍穹外卖”等旧项目来源表述删掉，使仓库首页和主要文档入口更像一个独立的“校内兼职平台 / 校园代送”项目。

本轮只做文案和索引整理：

- 不删除旧代码。
- 不修改数据库。
- 不移动目录。
- 不修改 AGENTS 协作约束。
- 不连接服务器。

## 已完成项

### 1. README 改为 campus-first 介绍

更新 `README.md`：

- 删除“由苍穹外卖风格项目增量改造而来”的仓库介绍。
- 改为“面向校园场景的校内兼职与代送试运营平台”。
- 删除仓库首页中“旧外卖后台基础页面仍保留”等展示性表述。
- 将 `bridge` 主线描述改为“兼容接口保持冻结态”。
- 从根 README 的目录树和文档入口中移除旧归档入口。

### 2. docs 索引改为当前主线入口

更新 `docs/README.md`：

- 删除“旧苍穹外卖阶段材料”入口描述。
- 移除旧阶段归档 section。
- 将“旧模块到校园代送模块映射”改为“历史兼容能力到校园代送模块映射”。
- 维护规则改为“历史参考材料不再挂在 README 主入口”。

### 3. 主要公开文档去旧项目化

更新：

- `docs/api-overview.md`
- `docs/db-overview.md`
- `docs/project-status-review.md`
- `docs/deployment/launch-readiness-acquisition-guide.md`

调整内容：

- “旧外卖公开能力”改为“基础运行检查能力”。
- “旧外卖后台”改为“基础管理能力”。
- “旧外卖基础表”改为“基础复用表”。
- “旧外卖模块”改为“历史兼容模块 / 后端兼容能力”。
- “bridge 说明”改为“兼容接口说明”。

### 4. 保留真实历史与协作约束

本轮没有修改：

- `AGENTS.md`
- `docs/legacy-takeaway/`
- `project-logs/legacy-takeaway/`
- Flyway 迁移脚本文件名

原因：

- `AGENTS.md` 是协作约束，不是仓库介绍。
- 历史归档文件可以保留，但不挂在仓库主入口。
- Flyway 文件名不能为了文案好看随意改名，否则会影响迁移历史。

## 修改文件

- `README.md`
- `docs/README.md`
- `docs/api-overview.md`
- `docs/db-overview.md`
- `docs/project-status-review.md`
- `docs/deployment/launch-readiness-acquisition-guide.md`
- `project-logs/campus-relay/step-184-repository-introduction-campus-first.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 验证结果

已检查以下公开入口中不再出现“苍穹外卖 / 旧外卖”：

- `README.md`
- `docs/README.md`
- `docs/api-overview.md`
- `docs/db-overview.md`
- `docs/project-status-review.md`
- `docs/deployment/launch-readiness-acquisition-guide.md`

保留项：

- 迁移脚本文件名中仍有 `bridge`，这是历史文件名，未修改。
- 深层历史归档文档仍保留原始上下文，未作为仓库介绍入口展示。

## 遗留问题

1. 如果 owner 还想改 GitHub 仓库右侧 About 描述，需要单独使用 GitHub 页面或 `gh repo edit` 修改。
2. 如果后续要彻底清理历史归档，需要单独评估，不应和仓库介绍文案混在一轮做。

## 下一步建议

如果继续整理仓库门面，下一步可以：

1. 更新 GitHub About 描述为“校内兼职平台 / 校园代送试运营系统”。
2. 增加 README 截图或演示动图。
3. 补一段“适合课程设计 / 毕设 / 校园服务原型”的项目亮点。

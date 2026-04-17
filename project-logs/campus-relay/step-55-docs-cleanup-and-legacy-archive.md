# Step 55 - 旧外卖文档清理与目录归档

## 本轮目标

1. 清理根目录和 docs 中仍然按旧苍穹外卖项目表达的文档。
2. 将旧外卖阶段文档归档到 legacy 目录，不再作为当前项目主入口。
3. 将当前入口文档统一改成“校内兼职平台 / 校园代送试运营版”口径。
4. 不修改旧外卖业务代码，不删除旧外卖模块。
5. 不修改 bridge、鉴权、接口、路由或前端业务行为。

## 处理原则

1. 旧外卖模块仍保留可运行，所以旧文档不做无脑删除。
2. 明显过时、会误导当前开发入口的文档从主入口移走。
3. 仍有历史参考价值的内容归档到 `legacy-takeaway`。
4. 当前 README、docs index、frontend README 和交付说明必须反映当前 campus 主线。

## 实际整理结果

### 旧外卖文档归档

1. `docs/api-design.md` 移动到 `docs/legacy-takeaway/api-design.md`。
2. `docs/db-design.md` 移动到 `docs/legacy-takeaway/db-design.md`。
3. 根 `project-logs/` 下旧外卖修复日志移动到 `project-logs/legacy-takeaway/`。
4. 删除未纳入 Git 的旧临时运行日志目录 `project-logs/runtime/`；正式证据目录 `project-logs/campus-relay/runtime/` 未动。

### 当前文档重写

1. 根 `README.md` 改为校园代送试运营版入口。
2. `docs/README.md` 改为当前文档索引。
3. `docs/delivery-guide.md` 替代原根目录中文交付说明。
4. `docs/project-status-review.md` 替代原根目录中文检查说明。
5. `frontend/README.md` 改为当前 customer / courier / admin 路由与 token 约束说明。
6. `docs/deployment/production-deploy.md` 改为当前试运营到生产部署边界。
7. `docs/deployment/backup-and-rollback.md` 改为当前回滚与 bridge 注意事项。
8. `CONTRIBUTING.md`、`SECURITY.md`、`CHANGELOG.md` 改为当前 campus 口径。
9. `backend/db/migrations/README.md` 更新到 V1-V10 迁移状态。
10. `docs/campus-relay/legacy-to-campus-mapping.md` 中旧文档路径同步改为归档路径和当前入口路径。

### 新增当前主线文档

1. `docs/api-overview.md`：当前 API 分组总览。
2. `docs/db-overview.md`：当前数据库与 campus 表总览。
3. `docs/legacy-takeaway/README.md`：旧外卖文档归档说明。
4. `project-logs/README.md`：当前日志目录索引。

## 明确没有做

1. 没有删除旧外卖业务代码。
2. 没有删除旧外卖数据库表。
3. 没有改订单、购物车、地址等旧外卖语义。
4. 没有改 bridge。
5. 没有改 `request.js`。
6. 没有改 token 附着逻辑。
7. 没有新增页面。
8. 没有新增后端接口。
9. 没有改 SQL、Java 或 Vue 业务实现。

## 当前目录口径

1. 当前项目入口：
   - `README.md`
   - `docs/README.md`
   - `project-logs/campus-relay/summary.md`
2. 当前交付文档：
   - `docs/delivery-guide.md`
   - `docs/project-status-review.md`
   - `docs/api-overview.md`
   - `docs/db-overview.md`
3. 旧外卖参考：
   - `docs/legacy-takeaway/`
   - `project-logs/legacy-takeaway/`

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮只是文档目录整理，不构成 bridge 收口动作。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. 若继续推进业务能力，回到 Step 54 已建议的 settlement 批次操作审计 go / no-go。
2. 如果进入实现，优先只落 `campus_settlement_batch_operation_record`。
3. 不要把文档清理轮扩成业务开发轮。
4. 继续保持 bridge、展示 polish、媒体线冻结口径。

## Step 56 回填

1. Step 56 已完成 settlement 批次操作审计 go / no-go。
2. 最终选择进入 `campus_settlement_batch_operation_record` 最小实现。
3. Step 57 实现边界已收紧为批次操作审计表和三个最小 admin 接口。
4. review / withdraw 继续只写操作审计，不改 `payout_status`，不清空 `payout_batch_no`，不做真实财务撤回。
5. 对账差异记录继续后置，避免和批次操作审计并发扩范围。

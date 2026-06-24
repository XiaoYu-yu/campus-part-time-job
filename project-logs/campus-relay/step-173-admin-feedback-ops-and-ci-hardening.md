# Step 173 - admin 反馈运营闭环与 CI 回归加固

## 本次目标

把 Step 170 已上线的 App 内反馈提交能力从“只能入库”补成可运营闭环，并把上线前回归从人工命令提升到 CI 强制检查。

## 已完成项

1. 新增 admin 反馈 API：
   - `GET /api/campus/admin/feedback`
   - `GET /api/campus/admin/feedback/{id}`
   - `POST /api/campus/admin/feedback/{id}/process`
2. 反馈支持按来源角色、类型、状态和关联订单分页筛选。
3. 反馈处理状态固定为：
   - `PENDING`
   - `IN_PROGRESS`
   - `RESOLVED`
4. 处理动作保留管理员 ID、处理时间和处理备注；已完成反馈不可重复覆盖。
5. 新增管理后台 `/campus/feedback` 页面，支持列表、详情、筛选和处理进度更新。
6. 新增 MySQL migration `V14__campus_feedback_admin_processing.sql`，并同步 MySQL init、H2 schema、H2 seed。
7. 新增 `CampusFeedbackIntegrationTest`，覆盖公开提交、角色鉴权、admin 分页/详情、处理中、已完成和重复处理拒绝。
8. 修复 `CampusCourierAcceptIntegrationTest` 对共享 H2 动态数据总数的脆弱断言：查询显式携带目标订单号。
9. CI 从后端只编译、前端只构建升级为：
   - 后端全量测试。
   - 前端 lint。
   - 前端 Vitest。
   - 前端生产构建。

## 修改文件

### 后端与数据库

- `backend/db/init.sql`
- `backend/db/migrations/README.md`
- `backend/db/migrations/V14__campus_feedback_admin_processing.sql`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminFeedbackController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/dto/CampusAdminFeedbackProcessDTO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/entity/CampusFeedback.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusFeedbackMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusFeedbackQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/CampusFeedbackService.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusFeedbackServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusFeedbackVO.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusFeedbackIntegrationTest.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusCourierAcceptIntegrationTest.java`

### 前端

- `frontend/src/api/campus-admin.js`
- `frontend/src/layout/MainLayout.vue`
- `frontend/src/router/index.js`
- `frontend/src/views/CampusFeedbackOpsView.vue`

### CI 与文档

- `.github/workflows/trial-operation-ci.yml`
- `README.md`
- `docs/api-overview.md`
- `docs/db-overview.md`
- `docs/deployment/ci-check-boundary.md`
- `docs/deployment/public-beta-release-gap-closure.md`
- `scripts/trial-operation/validate-samples.ps1`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 验证结果

- 后端 `.\mvnw.cmd test`：58 tests，0 failures，0 errors。
- 前端 `npm run lint`：通过。
- 前端 `npm test`：2 files / 5 tests 全部通过。
- 前端 `npm run build`：通过。
- 前端 `npm run build:android:user:public`：通过。
- 前端 `npm run build:android:parttime:public`：通过。
- 试运营样本校验：0 hard failures，3 个既有可选样本 warning；新增 feedback schema / seed 检查通过。
- `git diff --check`：通过，仅仓库既有 CRLF 转换提示。

## 遗留问题

1. 真实 release keystore 与正式签名 APK 仍需 owner 在私有环境生成，不能提交到仓库。
2. 服务器部署前必须执行 `V14__campus_feedback_admin_processing.sql`。
3. 新 admin 反馈页面尚未进入远端 browser smoke 截图矩阵。
4. 公共反馈提交接口当前没有专用应用层限流；扩大公开流量前建议在 Nginx 或应用层增加防刷策略。
5. 旧后端外卖模块仍需按模块独立审计，不能批量删除。

## 下一步建议

1. owner 生成双端 release keystore 和正式签名 APK。
2. 部署 V14 migration，重建服务器并运行远端 API / SPA smoke。
3. 将 `/campus/feedback` 加入 admin browser smoke。
4. 做正式 release 包真机主链路回归。
5. release 收口后，再评估反馈接口限流和旧后端模块逐项去旧。

# Step 95 - 内测型试运营 Compose 部署包最小实现

## 本轮目标

1. 把当前项目从“本地可演示”推进到“单机服务器可起的内测型试运营部署包”。
2. 只补部署工件与部署说明，不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。
3. 为你后续买服务器后的首轮内测部署提供可直接执行的 compose 方案。

## 为什么选这条线

当前主链路、交付材料、CI 和试运营脚本都已经收口。

相比继续扩业务，本轮更高价值的是：

1. 给单机服务器准备真实可用的部署工件。
2. 降低你拿到服务器后的手工拼环境成本。
3. 保持当前“模拟支付 / 模拟退款 / 模拟打款”的试运营边界不变。

## 实际新增的部署工件

### 1. Docker / Compose

新增：

1. `deploy/internal-trial/docker-compose.yml`
2. `deploy/internal-trial/backend.Dockerfile`
3. `deploy/internal-trial/frontend.Dockerfile`
4. `deploy/internal-trial/nginx.conf`
5. `deploy/internal-trial/.env.example`
6. 根目录 `.dockerignore`

部署模型：

1. `mysql:8.4`
2. Spring Boot backend（`prod` profile）
3. Nginx 承载的 frontend 静态资源，并代理 `/api`

### 2. 部署文档

新增：

1. `docs/deployment/internal-trial-compose.md`

同步更新：

1. `README.md`
2. `docs/README.md`
3. `docs/delivery-guide.md`
4. `docs/deployment/production-deploy.md`

## 关键边界

本轮部署包保持这些边界不变：

1. 不接真实支付。
2. 不接真实退款。
3. 不接真实打款。
4. 不改 bridge。
5. 不改 `request.js`。
6. 不改任何 `campus-*` API 运行时行为。
7. 不改前端页面与路由。

## Compose 方案摘要

### MySQL

1. 使用 `mysql:8.4`
2. 首次启动自动执行 `backend/db/init.sql`
3. 数据卷：`mysql-data`

### Backend

1. 使用 `deploy/internal-trial/backend.Dockerfile`
2. 容器内固定 `SPRING_PROFILES_ACTIVE=prod`
3. 数据库、JWT、CORS、上传路径通过环境变量注入
4. 上传目录数据卷：`uploads-data`

### Frontend

1. 使用 `deploy/internal-trial/frontend.Dockerfile`
2. 构建时注入 `VITE_API_BASE_URL=/api`
3. 构建时注入 `VITE_TENCENT_MAP_KEY`
4. Nginx 反向代理 `/api/` 到 backend 容器

## 当前明确不覆盖

1. HTTPS / 域名自动化
2. 多节点扩容
3. 生产级监控
4. 自动备份编排
5. bridge 收口动作
6. 真实外部公司能力接入

## 验证结果

本轮已完成：

1. `backend\\.\\mvnw.cmd -DskipTests package` 通过
2. `frontend\\npm run build` 通过
3. `scripts\\trial-operation\\validate-samples.ps1` 通过硬校验，仅保留 3 条既有 warning
4. `git diff --check` 通过

本轮未完成：

1. `docker compose up` 运行态验证

原因：

1. 当前机器未安装 Docker / Docker Compose。
2. 因此本轮只能完成部署工件静态交付与文档固化，不能在本机直接做容器级 smoke。

## 当前 bridge 结论

1. bridge 继续保持 `Phase A no-op` 冻结态。
2. 本轮部署包实现不改变 bridge 保留范围。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 96 建议进入“单机服务器首轮内测部署与 smoke 验证”：

1. 在新服务器上安装 Docker / Docker Compose。
2. 按 `deploy/internal-trial/.env.example` 生成真实 `.env`。
3. 执行 compose 启动。
4. 按部署后 smoke checklist 跑一轮最小验证。
5. 若容器级启动成功，再评估是否需要补日志、回滚或备份的最小自动化。

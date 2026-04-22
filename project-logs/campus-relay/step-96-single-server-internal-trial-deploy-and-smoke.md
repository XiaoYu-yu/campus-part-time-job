# Step 96 - 单机服务器首轮内测部署与 smoke 验证

## 本轮目标

1. 在单机公网服务器上真实拉起内测型试运营 Compose 栈。
2. 解决部署包在真实环境中的首轮运行阻塞。
3. 按最小 smoke 范围验证 admin / customer / courier 三条试运营入口可用。
4. 不改 bridge、不改鉴权、不改业务语义，只修部署层和初始化层阻塞。

## 为什么进入这一轮

Step 95 已经补齐了内测型试运营 Compose 部署包，但当时只完成了本地静态交付：

1. 当前机器未安装 Docker，无法做容器级运行验证。
2. 因此 Step 96 的价值是把部署包真正落到服务器上，验证其不是“只看起来能部署”，而是“真的能起来”。

## 实际完成项

### 1. 单机服务器 Compose 栈真实拉起

已在公网单机服务器上完成：

1. 安装 Docker / Docker Compose。
2. 克隆仓库到 `/opt/campus-part-time-job`。
3. 按 `deploy/internal-trial/.env.example` 生成真实 `.env`。
4. 执行 `docker compose up -d --build`。

当前远端 Compose 状态：

1. `campus-trial-mysql-1` 已正常运行。
2. `campus-trial-backend-1` 已正常运行。
3. `campus-trial-frontend-1` 已正常运行。

### 2. 真实部署阻塞 1：MySQL 8.4 启动参数不兼容

首轮启动发现 `mysql:8.4` 无法接受：

1. `--default-authentication-plugin=mysql_native_password`

因此本轮已从：

- `deploy/internal-trial/docker-compose.yml`

中移除该参数，保持 MySQL 8.4 默认认证行为。

### 3. 真实部署阻塞 2：MySQL init 种子与 H2 已存在漂移

MySQL 首轮初始化执行：

- `backend/db/init.sql`

时，`campus_relay_order` seed 因列数与值数不一致失败。

本轮已修正：

1. `backend/db/init.sql` 中 `campus_relay_order` 的列清单。
2. 使其与当前 H2 / 后端真实字段保持一致。
3. 同步补齐试运营主链路样本：
   - `CR202604070001`
   - `CR202604070002`
   - `CR202604060001`

修正后：

1. MySQL 初始化成功。
2. backend 能在 `prod` profile 下连上 MySQL 正常启动。

## 公开端口与可达性

本轮确认：

1. `http://<server-ip>/` 返回 `200`。
2. `http://<server-ip>:8080` 的 backend 对外可访问。
3. `80 / 8080 / 3306` 已按 compose 暴露。

## 最小 smoke 验证结果

### 1. 角色登录

通过公网入口验证以下登录成功：

1. admin 登录：`POST /api/employees/login`
2. customer 登录：`POST /api/users/login`
3. courier token 申请：`POST /api/campus/courier/auth/token`

### 2. admin 侧 smoke

以下接口已返回 `code = 200`：

1. `GET /api/campus/admin/settlements?page=1&pageSize=2`
2. `GET /api/campus/admin/orders/after-sale-executions?page=1&pageSize=2`
3. `GET /api/campus/admin/exceptions?page=1&pageSize=2`

### 3. customer 侧 smoke

以下接口已返回 `code = 200`：

1. `GET /api/campus/customer/orders/CR202604060001`
2. `GET /api/campus/customer/courier-onboarding/token-eligibility`

### 4. courier 侧 smoke

以下接口已返回 `code = 200`：

1. `GET /api/campus/courier/profile`
2. `GET /api/campus/courier/review-status`
3. `GET /api/campus/courier/orders/available?page=1&pageSize=5`

### 5. 关键结论

这说明当前项目已经从：

1. 本地可演示

推进到：

2. 单机服务器可启动、可访问、可进行内测型试运营 smoke

## 本轮明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改任何前端页面、路由或接口语义。
4. 没有改后端业务状态机。
5. 没有接真实支付、真实退款、真实打款。
6. 没有做 HTTPS、域名、监控、备份或自动回滚。

## 修改文件

1. `deploy/internal-trial/docker-compose.yml`
2. `backend/db/init.sql`
3. `project-logs/campus-relay/summary.md`
4. `project-logs/campus-relay/pending-items.md`
5. `project-logs/campus-relay/file-change-list.md`
6. `project-logs/campus-relay/step-96-single-server-internal-trial-deploy-and-smoke.md`

## 验证结果

1. 服务器上 `docker compose ps` 三个服务均为 `Up`。
2. `curl -I http://<server-ip>/` 返回 `HTTP 200`。
3. backend 在 MySQL 初始化修正后正常启动。
4. admin / customer / courier 三类最小 smoke 接口均返回 `code = 200`。
5. 本地 `backend\.\mvnw.cmd -DskipTests compile` 通过。
6. 本地 `frontend\npm run build` 通过。
7. `git diff --check` 通过。

## 当前 bridge 结论

1. bridge 继续保持 `Phase A no-op` 冻结态。
2. 本轮部署验证没有修改 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 的保留策略。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 97 建议进入“单机服务器内测运维加固 / 最小回滚与备份准备”：

1. 为当前 Compose 部署补最小回滚步骤。
2. 补 MySQL 数据卷与 `.env` 的最小备份说明。
3. 视服务器条件决定是否补 HTTPS / 域名 / 反向代理正式入口。
4. 在不扩大业务范围的前提下，把当前内测型试运营进一步收敛成更稳的运维入口。

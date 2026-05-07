# Step 157 - 当前本地工作树服务器部署与远端 smoke

## 本轮目标

把当前本地项目部署到公网服务器，复用现有单机内测 Docker Compose 方案，并完成部署后远端 smoke 验证。

本轮不是功能开发轮，不修改业务代码；目标是确认当前工作树可在服务器上运行，并留下可复核的部署证据。

## 部署范围

- 服务器应用目录：`/opt/campus-part-time-job`
- 部署方式：本地打包当前工作树，上传到服务器后通过 `deploy/internal-trial/docker-compose.yml` 重建 `mysql / backend / frontend`
- 公网入口：frontend `80`
- 内部端口边界：
  - backend `8080` 继续仅绑定服务器本机
  - MySQL `3306` 继续仅绑定服务器本机
- CORS：已更新为当前公网入口和本地调试入口

## 部署前备份

部署前已在服务器执行现有备份脚本：

- MySQL 备份：`deploy/internal-trial/backups/mysql/campus-trial-cangqiong_takeaway-20260507-110943.sql.gz`
- uploads 备份：`deploy/internal-trial/backups/uploads/campus-trial-uploads-20260507-110943.tar.gz`
- env 备份：`deploy/internal-trial/backups/meta/campus-trial-env-20260507-110943.bak`
- manifest：`deploy/internal-trial/backups/meta/campus-trial-backup-20260507-110943.txt`

说明：备份文件随旧部署目录一起保留在服务器上一轮源码备份目录中，未提交到仓库。

## 实际部署动作

1. 使用本机 SSH key 完成服务器免密登录验证。
2. 打包当前本地工作树为 `target/deploy/campus-current.tar.gz`。
3. 上传到服务器 `/tmp/campus-current.tar.gz`。
4. 保留原 `/opt/campus-part-time-job` 为带时间戳的 `.prev-*` 目录。
5. 解压新包到 `/opt/campus-part-time-job`。
6. 复用原 `.env`，仅修正 CORS 入口到当前公网地址与本地调试地址。
7. 执行 Docker Compose `up -d --build` 重建并启动。

## 容器状态

部署后 `mysql / backend / frontend` 均为 `Up`：

- `campus-trial-mysql-1`
- `campus-trial-backend-1`
- `campus-trial-frontend-1`

backend 使用 `prod` profile 启动，Tomcat 正常监听 `8080`；frontend Nginx 正常监听公网 `80`。

## Smoke 验证

### 基础访问

- `GET /`：HTTP 200，SPA shell 正常
- `GET /api/campus/public/health`：`code=200`，`status=UP`

### 远端完整 smoke

已执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\trial-operation\remote-smoke.ps1 `
  -ApiBase http://<server>/api `
  -FrontendBase http://<server> `
  -OutputPath .\project-logs\campus-relay\runtime\step-157-server-deploy\remote-smoke-report-rerun.json `
  -TimeoutSec 30
```

结果：

- PASS：25
- FAIL：0
- SKIP：0

覆盖项包括：

- public health
- admin 登录与员工列表
- customer 登录、onboarding profile、token eligibility、completed order detail
- parttime token、profile、review-status、available orders、completed order detail
- admin settlement、after-sale、courier、exception history 查询
- frontend SPA shell：`/`、`/login`、`/dashboard`、`/campus/settlements`、`/campus/after-sale-executions`、`/campus/exceptions`、`/user/campus/order-result`、`/parttime/workbench`

报告文件：

- `project-logs/campus-relay/runtime/step-157-server-deploy/remote-smoke-report-rerun.json`

### 瞬时 502 说明

第一次远端 smoke 中 `customer token eligibility` 曾出现一次 502。随后使用同一 customer token 单独重试该接口返回 `code=200`，再次完整执行远端 smoke 后 25 项全部通过。当前判断为刚部署后短时连接 / 网关瞬时问题，不作为稳定失败项。

## 明确未改动

- 未改业务代码
- 未改 bridge
- 未改 `request.js`
- 未改 token 附着逻辑
- 未改后端鉴权
- 未改路由
- 未改数据库结构
- 未删除旧外卖兼容模块
- 未提交或记录真实密钥、服务器密码、GitHub token、腾讯地图 key

## 当前风险

- 当前部署来自本地工作树，不等同于“远端 main 已完全同步”的代码状态。
- 仍未配置 HTTPS、域名和正式证书。
- 当前为 owner-controlled 单机内测部署，不是完整产品级生产上线。
- 如后续继续交给他人测试，仍建议先补一次人工页面巡检和 Android 双端公网 API smoke。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

- 不删除 `/api/campus/courier/profile`
- 不删除 `/api/campus/courier/review-status`
- 不修改双 token bridge 鉴权
- 不重开 bridge 收口动作

## 下一轮建议

1. 先在浏览器和 Android 双端用公网入口做一次人工巡检，确认样式与核心交互是否符合预期。
2. 若要给他人内测，补一份“公网内测访问说明 + 测试账号发放边界”。
3. 若要继续工程推进，优先补 Android 双端公网 API smoke 与 APK 重打包，而不是继续新增后台页面。

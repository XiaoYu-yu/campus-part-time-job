# Step 162 - GitHub 与内测服务器同步确认

## 本轮目标

1. 确认本地最新改动已经提交并推送到 `origin/main`。
2. 将当前主线版本同步到内测服务器。
3. 保留服务器部署前备份。
4. 复跑远端 API 与前端 shell smoke，确认服务器不是“只传上去”，而是实际可运行。
5. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由、不新增页面。

## 本轮实际完成

1. 本地提交并推送：
   - 已提交：`1d68c534d091c533d82ded13d3b3b924eb00db91`
   - 提交信息：`feat: stabilize mobile apps for internal trial`
   - 已推送到：`origin/main`
2. 服务器同步：
   - 使用 `git archive` 从本地 HEAD 生成源码归档。
   - 上传归档到内测服务器临时目录。
   - 保留服务器已有 `.env`，未写入仓库。
   - 将旧部署目录移动为上一版目录：`/opt/campus-part-time-job.prev-20260513-090427`
   - 将新归档展开为当前部署目录：`/opt/campus-part-time-job`
   - 执行 Docker Compose 重建并启动 `mysql / backend / frontend`。
3. 服务器备份：
   - 部署前已运行 `deploy/internal-trial/backup-stack.sh`。
   - 备份 manifest：`/opt/campus-part-time-job/deploy/internal-trial/backups/meta/campus-trial-backup-20260513-090204.txt`
   - MySQL 备份：`/opt/campus-part-time-job/deploy/internal-trial/backups/mysql/campus-trial-cangqiong_takeaway-20260513-090204.sql.gz`
   - uploads 备份：`/opt/campus-part-time-job/deploy/internal-trial/backups/uploads/campus-trial-uploads-20260513-090204.tar.gz`
   - `.env` 备份：`/opt/campus-part-time-job/deploy/internal-trial/backups/meta/campus-trial-env-20260513-090204.bak`
4. 远端 smoke：
   - 执行脚本：`scripts/trial-operation/remote-smoke.ps1`
   - 报告位置：`project-logs/campus-relay/runtime/step-162-server-sync/remote-smoke-report.json`
   - 结果：25 PASS / 0 FAIL / 0 SKIP

## 远端 smoke 覆盖范围

1. Public health：
   - `GET /api/campus/public/health`
2. 登录：
   - admin 登录
   - 用户端登录
   - 兼职端 token 登录
3. 后台 API：
   - employee list
   - settlement list
   - settlement reconcile summary
   - after-sale executions
   - courier ops list
   - exception history list
4. 用户端 API：
   - courier onboarding profile
   - token eligibility
   - completed order detail
5. 兼职端 API：
   - profile
   - review-status
   - available orders
   - completed order detail
6. 前端 shell：
   - 首页
   - 管理端登录
   - 管理端 dashboard
   - settlement 页面
   - after-sale 页面
   - exception 页面
   - 用户端结果页
   - 兼职端 workbench

## 安全与提交边界

1. 本轮没有把公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容写入仓库。
2. `remote-smoke-report.json` 中的 URL 已脱敏。
3. 本轮部署复用了服务器已有 `.env`，没有把本地 `.env` 上传覆盖。
4. 根目录临时截图已通过 `.gitignore` 忽略，不进入提交。

## 明确未改动

1. 未改后端业务代码。
2. 未改前端业务页面。
3. 未改 bridge。
4. 未改 `request.js`。
5. 未改 token 附着逻辑。
6. 未改接口路径、路由或鉴权。
7. 未删除旧外卖兼容模块。
8. 未新增第五个 admin 页。

## 当前判断

1. GitHub `origin/main` 已包含当前本地最新主线提交。
2. 内测服务器已经同步到该提交对应代码。
3. 服务器 Docker Compose 运行正常。
4. 远端 smoke 全部通过，当前可继续作为 owner-controlled 内测环境使用。
5. 仍不建议公开公测；公开公测前还需要 release 签名包、安装说明、反馈模板、弱网 / 后台切回 / 多账号回归、HTTPS / 域名 / 证书等收口。

## 下一轮建议

1. 优先补内测分发材料：
   - APK 安装说明
   - 测试账号说明
   - 已知限制
   - 反馈模板
2. 继续做 Android 双端真机小回归：
   - 用户端登录、发单、查单、确认收货
   - 兼职端登录、接单、取餐、送达、异常上报
   - 后台查看订单、异常、售后、结算
3. 若要扩大到非 owner 用户，再补 release 签名包和最低限度隐私 / 安全说明。

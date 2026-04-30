# Step 137 - GitHub / 服务器同步与远端 smoke 复核

## 本轮目标

基于 Step 136 已完成服务器内测运维检查与恢复演练，本轮把本地最新提交同步到 GitHub 和单机内测服务器，并完成更新后的远端 smoke：

1. 推送本地 `main` 到 GitHub。
2. 服务器先备份，再 `git pull --ff-only origin main`。
3. 重建 compose 中 backend / frontend。
4. 使用远端 smoke 脚本复核 API 与 frontend shell。
5. 继续不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

本日志不记录真实公网 IP、服务器密码、数据库密码、JWT secret、腾讯地图 key 或任何 token。

## GitHub 同步

执行结果：

1. 首次普通 `git push origin main` 失败，原因是连接被 reset。
2. 后续按 owner 指定的本地代理端口执行推送成功。
3. GitHub `main` 已从 `e7cf965` 更新到 `3bf59cb`。
4. 本地 `main` 与 `origin/main` 已一致。

结论：

1. GitHub 已包含 Step 134 到 Step 136 的本地领先提交。
2. 服务器可以从 GitHub 拉取最新主线。

## SSH 免密配置

本轮为 owner 提供的内测服务器配置了本机专用 SSH key：

1. 本地生成专用 ed25519 key。
2. 通过一次密码登录把公钥追加到服务器 `authorized_keys`。
3. 已验证 `BatchMode=yes` 的免密登录可用。
4. 私钥只保存在本机用户目录，未写入仓库。
5. 未提交服务器密码、公网 IP、私钥或其它凭据。

验证结果：

1. 免密 SSH 可登录服务器。
2. 可在服务器部署目录读取 git hash。
3. 可通过免密 SSH 查看 compose 状态。

## 服务器同步与重建

服务器部署目录：

```text
/opt/campus-part-time-job
```

执行顺序：

1. 进入部署目录。
2. 确认更新前服务器 hash 为 `1a2329e`。
3. 执行 `backup-stack.sh`。
4. 执行 `git pull --ff-only origin main`。
5. 确认更新后服务器 hash 为 `3bf59cb`。
6. 执行 compose 重建。
7. 检查 compose 状态。
8. 检查服务器本机 frontend HTTP。

结果：

1. `git pull --ff-only` 成功。
2. backend image 构建成功。
3. frontend image 构建成功。
4. backend / frontend 容器重建成功。
5. mysql 容器保持运行。
6. `curl -I http://127.0.0.1/` 返回 200。

compose 结果：

1. `backend`：running。
2. `frontend`：running。
3. `mysql`：running。

## 备份与 restore drill

更新前执行了备份脚本：

```bash
bash deploy/internal-trial/backup-stack.sh
```

结果：

1. MySQL dump 已生成。
2. uploads 归档已生成。
3. `.env` 备份已生成。
4. manifest 已生成。

备份过程中仍观察到 MySQL 8 的 tablespace 权限 warning：

```text
Access denied; you need (at least one of) the PROCESS privilege(s) for this operation when trying to dump tablespaces
```

处理方式：

1. 本轮没有修改数据库账号权限。
2. 本轮立即执行非破坏性 restore drill 验证备份可恢复。
3. restore drill 通过。

restore drill 结果：

1. 最近一次 dump 可恢复到临时 MySQL。
2. 恢复后订单数：7。
3. `CR202604070002` 存在。
4. `CR202604060001` 存在。
5. uploads 归档可解压检查。
6. 未破坏当前运行中的 MySQL 数据卷。

后续建议：

1. Step 138 可评估在 `backup-stack.sh` 中增加 `--no-tablespaces`，或为备份账号补充最小必要权限。
2. 在修正前，每次关键备份后继续用 restore drill 兜底验证。

## 远端 smoke 结果

执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase <redacted-api-base> -FrontendBase <redacted-frontend-base> -OutputPath project-logs\campus-relay\runtime\step-137-remote-smoke\remote-smoke-report.json
```

报告路径：

```text
project-logs/campus-relay/runtime/step-137-remote-smoke/remote-smoke-report.json
```

结果：

1. PASS：24。
2. FAIL：0。
3. SKIP：0。
4. 报告已脱敏 host 和 endpoint。
5. 报告不包含 token、服务器密码、数据库密码、JWT secret 或腾讯地图 key。

覆盖范围：

1. admin login。
2. customer login。
3. parttime token。
4. admin employee / settlement / after-sale / courier / exception 关键只读接口。
5. customer onboarding / token eligibility / completed order detail。
6. parttime profile / review-status / available orders / completed order detail。
7. frontend shell：首页、登录页、dashboard、settlement、after-sale、exception、customer result、parttime workbench。

## 当前未解决问题

1. 服务器公网仍暴露 backend `8080` 和 MySQL `3306`，这适合临时调试，不适合长期外部试用。
2. backend 暂无独立健康检查接口，仍依赖业务 smoke。
3. backup 脚本在 MySQL 8 下仍出现 tablespace 权限 warning，虽然本轮 restore drill 已验证可恢复。
4. 当前没有 HTTPS、域名、证书、正式反向代理、安全组收敛或监控告警。

## 明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改 token 附着逻辑。
4. 没有改后端接口、数据库、状态机或鉴权。
5. 没有改任何 Vue 页面。
6. 没有新增页面或路由。
7. 没有删除旧兼容模块。
8. 没有接真实支付、真实退款或真实打款。
9. 没有提交真实公网 IP、服务器密码、GitHub token、私钥或腾讯地图 key。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

## Step 138 建议

建议进入“内测服务器安全边界最小加固评估”：

1. 优先评估是否关闭公网 MySQL `3306`，只允许容器内网访问。
2. 评估 backend `8080` 是否继续公网暴露，或只保留 frontend `80` 对外并通过 nginx 代理 `/api`。
3. 评估备份脚本 MySQL 8 tablespace warning 的处理方式：`--no-tablespaces` 或权限最小补齐。
4. 继续保持 bridge 冻结，不借运维加固改业务。

## Step 138 后续结果回填

Step 138 已开始执行本建议：

1. compose 中 backend 8080 与 MySQL 3306 已改为服务器本机 `127.0.0.1` 绑定。
2. frontend 80 继续作为默认公网入口。
3. 远端 smoke 推荐入口已切换为 `http://your-host/api`。
4. `backup-stack.sh` 已增加 `--no-tablespaces`。
5. 服务器侧重建、端口收敛验证和远端 smoke 待 Step 138 完成。

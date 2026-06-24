# Step 174 - 反馈防刷、部署隔离预案与 MuMu 回归

## 本次目标

在 Step 173 admin 反馈闭环基础上补齐公开反馈防刷与部署入口安全基线，并使用 owner 已有的 MuMu 模拟器完成最新 Android APK 的安装、启动和真实 WebView API 闭环验证。

## 已完成项

1. 公开反馈提交增加应用层基础防刷：
   - 内容少于 5 个字符时拒绝。
   - 同一角色、类型、内容、联系方式、页面和订单在 60 秒内重复提交时返回业务码 `429`。
   - 集成测试覆盖重复提交拒绝，确认数据库只保留一条记录。
2. Nginx 增加公开反馈接口专用限流：
   - `limit_req_zone` 基线为每分钟 6 次。
   - 单客户端突发量为 3。
   - 超限返回 HTTP `429` JSON。
3. HTTPS / Nginx 基线补充：
   - 仅允许 TLS 1.2 / 1.3。
   - 增加 HSTS、`nosniff`、`SAMEORIGIN` 和 Referrer Policy。
   - 隐藏 Nginx 版本号。
4. Docker / Podman 镜像引用改为完整 registry 名称，降低 Podman 短名称解析歧义。
5. 新增 `deploy/standalone-podman/`：
   - 使用独立 `campus-standalone-*` 前缀。
   - frontend 使用独立端口。
   - backend 和 MySQL 仅绑定回环地址。
   - 不依赖、不修改既有集群网络、容器、卷或服务。
6. smoke 与样例校验补充 admin 反馈页面、API、Nginx 限流和独立部署隔离边界。
7. MuMu Android 12 回归：
   - 重新构建用户端和兼职端 emulator Debug APK。
   - 两个 APK 均安装成功、可启动、可截图。
   - 用户端首屏和兼职端首屏无白屏、崩溃或错误页。
8. 修复 Android WebView smoke 脚本：
   - 登录前自动勾选用户协议 / 隐私政策。
   - 修复组合包装器未透传 `ApiBase`、adb 路径和设备 ID 的 PowerShell 参数冲突。
9. MuMu 真实 WebView 闭环通过：
   - 用户端：登录、取餐点、配送规则、订单列表、创建代送单、模拟支付、详情回读。
   - 兼职端：登录、资料、审核状态、可接订单列表。
10. Linux 路线已按 owner 指令停止：
   - 停止后未再连接授权的局域网机器。
   - 停止前仅在一台独立备用机安装了 Podman 并暂存源码。
   - 未拉取镜像、未创建容器/网络/卷、未启动服务、未占用业务端口，未修改集群环境。

## 修改文件

### 后端

- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusFeedbackMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusFeedbackServiceImpl.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusFeedbackIntegrationTest.java`

### 部署与安全

- `deploy/internal-trial/backend.Dockerfile`
- `deploy/internal-trial/frontend.Dockerfile`
- `deploy/internal-trial/docker-compose.yml`
- `deploy/internal-trial/nginx-xiaoyu.xin.conf`
- `deploy/internal-trial/nginx.conf`
- `deploy/standalone-podman/.env.example`
- `deploy/standalone-podman/init-env.sh`
- `deploy/standalone-podman/deploy.sh`
- `deploy/standalone-podman/stop.sh`
- `deploy/standalone-podman/status.sh`
- `deploy/standalone-podman/README.md`

### smoke 与文档

- `scripts/trial-operation/android-webview-user-public-smoke.ps1`
- `scripts/trial-operation/android-webview-parttime-public-smoke.ps1`
- `scripts/trial-operation/android-webview-public-smoke.ps1`
- `scripts/trial-operation/browser-smoke.ps1`
- `scripts/trial-operation/remote-smoke.ps1`
- `scripts/trial-operation/commands.ps1`
- `scripts/trial-operation/validate-samples.ps1`
- `mobile/README.md`
- `docs/README.md`
- `docs/deployment/internal-trial-compose.md`
- `docs/deployment/internal-trial-security-boundary.md`
- `docs/deployment/post-deploy-smoke-checklist.md`
- `docs/deployment/public-beta-release-gap-closure.md`
- `docs/deployment/xiaoyu-xin-https-runbook.md`

### 回归留痕

- `project-logs/campus-relay/runtime/android-qa-apks/step-174-mumu/android-qa-apk-manifest.json`
- `project-logs/campus-relay/runtime/android-mumu-webview-local/android-public-webview-readiness-summary.json`
- `project-logs/campus-relay/runtime/android-mumu-webview-local/user-public-webview-smoke.json`
- `project-logs/campus-relay/runtime/android-mumu-webview-local/parttime-public-webview-smoke.json`
- `project-logs/campus-relay/runtime/android-mumu-webview-local/user-campus-orders-public-webview.png`
- `project-logs/campus-relay/runtime/android-mumu-webview-local/parttime-workbench-public-webview.png`
- `project-logs/campus-relay/runtime/android-smoke/20260623-223022-user-app-launch.png`
- `project-logs/campus-relay/runtime/android-smoke/20260623-223033-parttime-app-launch.png`

## 验证结果

- 后端定向反馈测试：通过。
- 后端全量测试：58 tests，0 failures，0 errors。
- 前端 lint：通过。
- 前端 Vitest：2 files / 5 tests 全部通过。
- 前端生产构建：通过。
- Android emulator Debug APK：
  - user SHA-256：`6FBAD76B859ABD8B31D15B9B26A7CA17AB96191A99805CCF1838F7AAF4A84B35`
  - parttime SHA-256：`3C671FDF1BFFB1B7014B443A9C6768D27FC10FD4C5AF24A825DBDBD94A37013F`
- MuMu 安装 / 启动 / 截图：双端通过。
- MuMu WebView readiness：双端通过。
  - 用户端生成本地 H2 测试订单并模拟支付成功。
  - 兼职端 profile ID `2`、审核状态 `APPROVED`、可接订单 2 条。
- sample validation：0 hard failures，3 个运行时可选样本 warning。
- Android API base check：0 hard failures，2 个 ignored LAN env 分类 warning。
- `git diff --check`：通过，仅 CRLF 转换提示。

## 遗留问题

1. 正式上线仍缺 owner 私有的真实 release keystore 和正式签名 APK。
2. 服务器现网仍需在 owner 再次明确授权后执行 V14 migration、部署和远端 smoke；当前不再操作 Linux。
3. Android release 包还需在 HTTPS public 模式下做最终安装、登录和主链路回归。
4. 反馈防刷目前是单实例数据库时间窗 + Nginx 单机限流；未来多实例或大流量场景应使用共享限流存储。
5. 旧后端外卖模块仍需逐项依赖审计，不能批量删除。
6. 3 个可选运营样本仍需在运行态通过真实操作生成。

## 下一步建议

1. owner 在私有环境生成 release keystore，构建双端正式签名 APK。
2. 使用 MuMu 或真机安装正式 release 包，跑 HTTPS 主链路回归。
3. owner 明确恢复服务器操作后，再执行 V14 migration 和远端 smoke。
4. release 收口后进入旧后端模块逐项依赖审计。

# 公开公测前发布缺口收口清单

本清单用于判断当前项目从 owner-controlled 小范围内测进入公开公测前，还需要关闭哪些发布、安全和合规缺口。

当前结论：项目已经具备 owner-controlled 小范围内测和 Android release candidate 验证基础，但仍不建议直接公开公测。

## 当前已收口

### 1. Android release 签名配置入口

已完成：

- 用户端和兼职端 Gradle 均支持从本地 `android/key.properties` 读取 release 签名配置。
- 用户端和兼职端 Gradle 均支持通过 `CAMPUS_VERSION_CODE` / `CAMPUS_VERSION_NAME` 注入正式版本号。
- 用户端和兼职端 Gradle 均支持 `CAMPUS_REQUIRE_RELEASE_SIGNING=true` 强制要求 release 签名。
- 如果 `android/key.properties` 存在但缺少 `storeFile / storePassword / keyAlias / keyPassword`，构建会直接失败。
- 如果 `storeFile` 指向的 keystore 不存在，构建会直接失败。
- 已提供示例文件：
  - `mobile/user-app/android/key.properties.example`
  - `mobile/parttime-app/android/key.properties.example`
- `.gitignore` 已忽略：
  - `mobile/**/android/key.properties`
  - `mobile/**/android/keystore/`
  - `mobile/**/android/*.jks`
  - `mobile/**/android/*.keystore`
- 没有提交真实 keystore、密码或别名。

仍需 owner 本地完成或在 GitHub Secrets 中完成：

```powershell
keytool -genkeypair -v -keystore mobile\user-app\android\keystore\campus-user-release.jks -alias campus-user -keyalg RSA -keysize 2048 -validity 10000
keytool -genkeypair -v -keystore mobile\parttime-app\android\keystore\campus-parttime-release.jks -alias campus-parttime -keyalg RSA -keysize 2048 -validity 10000
```

然后分别复制 `key.properties.example` 为 `key.properties`，填入真实密码。真实文件必须保留在仓库外或本地私有目录，不允许提交。

### 2. Android release candidate 构建与验证管线

已完成：

- 新增本地 release 构建脚本：
  - `scripts/trial-operation/build-android-release-apks.ps1`
- 脚本会同时构建用户端和兼职端 release APK / AAB。
- release API base 必须使用 HTTPS 且以 `/api` 结尾。
- release 构建会校验：
  - 包名：`com.xiaoyu.campus.user` / `com.xiaoyu.campus.parttime`
  - 显示名：`用户端` / `兼职端`
  - `versionCode` / `versionName`
  - Capacitor release 配置为 `androidScheme=https`、`cleartext=false`
  - 合并后的 release manifest 为 `usesCleartextTraffic=false`
  - 合并后的 release manifest 为 `allowBackup=false`
  - 如果传入 `-RequireSigning`，APK 必须可通过 `apksigner verify`
- 新增 release 只读启动 smoke：
  - `scripts/trial-operation/android-release-webview-smoke.ps1`
- 新增手动 GitHub Actions workflow：
  - `.github/workflows/android-release.yml`
- 新增最小 CI release candidate job：
  - `.github/workflows/trial-operation-ci.yml`
  - 默认只构建 unsigned release candidate 并上传 manifest，不需要真实签名密钥。

本地验证结果：

- unsigned release candidate 构建通过。
- 使用临时 QA keystore 的 signed release candidate 构建通过。
- 双端 release APK 已安装到 MuMu Android 12。
- MuMu release smoke 通过：双端均可启动、获得焦点、截图非空，未发现 `ERR_CLEARTEXT` 或 `net::ERR_*`。
- 临时 QA keystore 和本地 `key.properties` 已在验证后删除。

注意：

- 本地临时 QA keystore 只用于验证签名链路，不是生产签名材料。
- 正式公开包必须由 owner 生产 keystore 或 GitHub Secrets 构建。
- 当前 `https://xiaoyu.xin/api/campus/public/health` 从本机访问超时，release smoke 将该项记录为可选远端可用性风险，不把它写成公网接口已通过。

### 3. Android cleartext release 收口

已完成：

- 用户端和兼职端 Manifest 已改为使用 `usesCleartextTraffic` placeholder。
- Debug 构建保留 `usesCleartextTraffic=true`，继续支持当前 HTTP 内测环境。
- Release 构建设置 `usesCleartextTraffic=false`。
- 已添加 debug/release 分离的 `network_security_config.xml`。
- 仓库默认提交的 Capacitor 配置为 `androidScheme=https`、`cleartext=false`。
- `cap:sync` / `cap:sync:emulator` / `cap:sync:lan` 会在本地同步前切到 HTTP cleartext 模式，`cap:sync:public` 会切回 HTTPS-only 模式。
- 已通过 Gradle 验证：
  - 用户端 debug / release 构建通过。
  - 兼职端 debug / release 构建通过。
  - 合并后的 debug manifest 为 `usesCleartextTraffic=true`。
  - 合并后的 release manifest 为 `usesCleartextTraffic=false`。

注意：

- 当前 HTTPS 入口与 release candidate 管线已就绪，但正式公开包仍需要 owner 生产 keystore 签名。
- 当前 public Debug QA 包仍只适合内测，不是正式 release 包。

### 4. HTTPS / 域名 / 证书

已完成：

- 域名 `xiaoyu.xin` 已确认解析到当前服务器。
- Docker 前端端口已收口为宿主机本机端口：
  - `127.0.0.1:18080`
- 宿主机 Nginx 已接入：
  - `/` 反代 frontend。
  - `/api/` 反代 backend。
- 已通过 Certbot 为 `xiaoyu.xin` 签发真实证书。
- 证书只保存在服务器宿主机 Nginx 层，未进入 Docker 容器，未提交到 Git。
- HTTP 80 已跳转 HTTPS。
- 已提供仓库侧 Nginx 443 模板：
  - `deploy/internal-trial/nginx-xiaoyu.xin.conf`
- 已提供执行与回滚说明：
  - `docs/deployment/xiaoyu-xin-https-runbook.md`
- Android public env 示例已切换为 `https://xiaoyu.xin/api`。
- 已完成远端 smoke：
  - 25 PASS / 0 FAIL / 0 SKIP。
- 已重新生成 Android public QA 包，并完成真机轻量安装 / 启动复核。

注意：

- 不要提交证书、证书私钥、服务器 `.env` 或服务器登录凭据。
- 当前本机访问 `https://xiaoyu.xin` 出现超时；在 owner 重新授权服务器操作前，不再默认连接服务器或改动部署。

### 5. 隐私说明与用户协议入口

已完成：

- 新增公共页面：
  - `/legal/privacy`
  - `/legal/terms`
- 用户端登录页和兼职端登录页已增加协议勾选。
- 未勾选时不允许继续登录。
- 当前静态文本已覆盖：收集信息、使用目的、保存期限、用户权利和反馈渠道。

仍需 owner 补充：

- 正式公开前建议补齐真实运营主体、联系邮箱或其他正式联系方式。
- 如果后续上架应用商店，仍需按应用商店要求补隐私政策公开 URL 和首次启动弹窗。

### 6. App 内反馈入口

已完成：

- 新增公共反馈页：`/feedback`。
- 用户端个人中心新增“问题反馈”入口。
- 兼职端资料页新增“问题反馈”入口。
- 后端新增 `campus_feedback` 表。
- 新增提交接口：`POST /api/campus/public/feedback`。
- H2/test profile 已验证反馈提交返回 `code=200` 并生成记录 ID。

已完成运营承接：

- 管理后台新增 `/campus/feedback`。
- 支持按来源角色、反馈类型、处理状态和关联订单筛选。
- 支持反馈详情回读。
- 支持 `PENDING -> IN_PROGRESS -> RESOLVED` 处理进度与管理员备注留痕。

### 7. 匿名反馈防刷

已完成：

- 应用层拒绝 60 秒内内容与上下文完全相同的重复反馈，返回业务码 `429`。
- 宿主机与容器 Nginx 均对 `/api/campus/public/feedback` 配置独立按 IP 限流。
- 默认每个来源 IP 每分钟 6 个请求，允许 3 个突发请求。
- 网关超限返回 HTTP `429` JSON，不影响其它 API。

## 当前仍未收口

### 1. owner 生产 release keystore 与正式签名 APK / AAB

当前状态：

- 仓库已有 release 签名配置入口和 `key.properties.example`。
- 仓库已有本地 release APK / AAB 构建脚本和 GitHub Actions 手动签名 workflow。
- 本地已用临时 QA keystore 验证 signed release candidate 构建链路。
- 真实生产 keystore、真实 `key.properties` 或 GitHub Secrets 仍未由 owner 配置。
- 正式生产签名 APK / AAB 仍未由 owner 生成并验收。

这是进入公开公测前最后的核心阻断项之一。Debug QA 包和临时 QA 签名包仍只适合 owner-controlled 内测或构建链路验证，不应作为公开公测包分发。

### 2. 最终服务器部署与公网 smoke

当前状态：

- owner 已明确要求停止 Linux 操作。
- 停止后未再连接任何服务器，未修改集群环境。
- `https://xiaoyu.xin` 当前从本机访问超时，尚未对 Step 173/174/175 的最终版本做服务器部署和远端 smoke。

公开公测前仍需 owner 重新明确授权后完成：

1. 执行 `V14__campus_feedback_admin_processing.sql`。
2. 部署当前版本。
3. 运行远端 API / SPA / admin 反馈 / Android release 包 smoke。

## 当前禁止项

- 不提交真实 keystore。
- 不提交 `key.properties`。
- 不提交公网 IP、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。
- 不把 Debug QA 包当正式 release 包发给公开用户。
- 不重开 bridge 主线。
- 不删除旧外卖兼容模块。

## 推荐下一步

下一步建议：

1. 生成用户端 / 兼职端真实 release keystore。
2. 填写本地 `key.properties` 或配置 GitHub Actions signing secrets，确认不提交真实密钥。
3. 构建双端正式 signed release APK / AAB。
4. 基于 `https://xiaoyu.xin/api` 做 release 包 MuMu / 真机安装和主链路 smoke。
5. owner 重新授权服务器操作后，部署 `V14__campus_feedback_admin_processing.sql` 和当前版本，对 admin 反馈页面与公网 API 做一次服务器 smoke。

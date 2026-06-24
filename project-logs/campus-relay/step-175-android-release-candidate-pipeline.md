# Step 175 - Android release candidate 管线与 MuMu release 验证

## 本次目标

在不触碰 Linux / 集群环境的前提下，把 Android 双端从 Debug QA 包推进到可重复构建、可校验、可在 MuMu 启动验证的 release candidate 状态，为 owner 后续生成生产签名包做准备。

## 已完成项

1. Android Capacitor 配置分层
   - 新增 `mobile/configure-capacitor.mjs`。
   - 仓库提交态的双端 `capacitor.config.json` 默认为 `androidScheme=https`、`cleartext=false`。
   - `cap:sync` / `cap:sync:emulator` / `cap:sync:lan` 会在本地同步前切换到 HTTP cleartext 调试模式。
   - `cap:sync:public` 会恢复 HTTPS-only release / public 模式。

2. Android release 构建加固
   - 双端 `build.gradle` 支持：
     - `CAMPUS_VERSION_CODE`
     - `CAMPUS_VERSION_NAME`
     - `CAMPUS_REQUIRE_RELEASE_SIGNING`
   - 如果 `android/key.properties` 存在但不完整，构建直接失败。
   - 如果 keystore 文件不存在，构建直接失败。
   - release 默认 `usesCleartextTraffic=false`。
   - 双端 Android manifest 明确 `android:allowBackup="false"`。

3. 本地 release 构建脚本
   - 新增 `scripts/trial-operation/build-android-release-apks.ps1`。
   - 同时构建用户端和兼职端 release APK / AAB。
   - 强制 release API base 使用 HTTPS 且以 `/api` 结尾。
   - 校验包名、显示名、版本号、HTTPS-only Capacitor 配置、no-cleartext、no-backup。
   - `-RequireSigning` 模式下强制 `apksigner verify` 通过。
   - 输出 `android-release-manifest.json`，记录 APK / AAB hash。

4. GitHub Actions release 边界
   - `.github/workflows/trial-operation-ci.yml` 新增 `android-release-candidate` job。
   - CI 只构建 unsigned release candidate，只上传 manifest，不上传 APK / AAB。
   - 新增 `.github/workflows/android-release.yml` 手动 signed release workflow。
   - 手动 workflow 依赖 owner 配置 GitHub Secrets，构建结束后清理临时 keystore 和 `key.properties`。

5. MuMu release smoke
   - 新增 `scripts/trial-operation/android-release-webview-smoke.ps1`。
   - release 包不依赖 WebView DevTools，改用启动、焦点、截图、logcat 错误和可选公网 health 检查。
   - 使用临时 QA keystore 构建的双端 signed release candidate 已安装到 MuMu Android 12。
   - 双端 `versionCode=175`、`versionName=0.9.0-rc.175`。
   - 双端均可启动、获得焦点、截图非空，未发现 `ERR_CLEARTEXT` 或 `net::ERR_*`。
   - 临时 QA keystore、本地 `key.properties` 已在验证后删除。

## 修改文件

- `.gitignore`
- `.github/workflows/trial-operation-ci.yml`
- `.github/workflows/android-release.yml`
- `docs/deployment/ci-check-boundary.md`
- `docs/deployment/public-beta-release-gap-closure.md`
- `mobile/README.md`
- `mobile/configure-capacitor.mjs`
- `mobile/user-app/package.json`
- `mobile/parttime-app/package.json`
- `mobile/user-app/capacitor.config.json`
- `mobile/parttime-app/capacitor.config.json`
- `mobile/user-app/android/app/build.gradle`
- `mobile/parttime-app/android/app/build.gradle`
- `mobile/user-app/android/app/src/main/AndroidManifest.xml`
- `mobile/parttime-app/android/app/src/main/AndroidManifest.xml`
- `scripts/trial-operation/build-android-release-apks.ps1`
- `scripts/trial-operation/android-release-webview-smoke.ps1`
- `project-logs/campus-relay/runtime/android-release-validation/step-175/android-release-manifest.json`
- `project-logs/campus-relay/runtime/android-release-validation/step-175/android-release-webview-smoke.json`
- `project-logs/campus-relay/runtime/android-release-validation/step-175/user-release-launch.png`
- `project-logs/campus-relay/runtime/android-release-validation/step-175/parttime-release-launch.png`
- `project-logs/campus-relay/step-175-android-release-candidate-pipeline.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 验证结果

- unsigned release candidate 构建通过。
- 临时 QA keystore signed release candidate 构建通过。
- build manifest 确认：
  - 用户端包名：`com.xiaoyu.campus.user`
  - 兼职端包名：`com.xiaoyu.campus.parttime`
  - 双端 `androidScheme=https`
  - 双端 `cleartext=false`
  - 双端 `manifestAllowsCleartext=false`
  - 双端 `androidBackupAllowed=false`
  - 双端 signed APK 通过签名校验。
- MuMu release smoke 通过：
  - 用户端启动截图：`runtime/android-release-validation/step-175/user-release-launch.png`
  - 兼职端启动截图：`runtime/android-release-validation/step-175/parttime-release-launch.png`
  - 报告：`runtime/android-release-validation/step-175/android-release-webview-smoke.json`
- PowerShell 脚本语法检查通过：
  - `scripts/trial-operation/build-android-release-apks.ps1`
  - `scripts/trial-operation/android-release-webview-smoke.ps1`
- `node --check mobile/configure-capacitor.mjs` 通过。
- backend `.\mvnw.cmd test` 通过：58 tests / 0 failures / 0 errors。
- frontend `npm run lint` 通过。
- frontend `npm test` 通过：2 files / 5 tests。
- frontend `npm run build` 通过。
- sample validation CI wrapper 通过：0 hard failures / 3 expected optional warnings。
- `git diff --check` 通过，仅 CRLF 提示。
- 已复核双端 `key.properties` 和临时 QA keystore 目录不存在。
- 本轮未连接 Linux，未部署服务器，未修改集群环境。
- 本轮未提交真实 keystore、真实 `key.properties`、密码、证书私钥、服务器 `.env`、服务器凭据、GitHub token、腾讯地图 key 或真实设备 ID。

## 遗留问题

1. 本轮 signed release candidate 使用的是临时 QA keystore，只验证签名链路，不是生产签名材料。
2. owner 仍需生成并妥善保管用户端、兼职端生产 release keystore。
3. owner 仍需配置本地 `key.properties` 或 GitHub Actions signing secrets 后构建正式 signed APK / AAB。
4. `https://xiaoyu.xin/api/campus/public/health` 从本机访问超时，公网服务需要在 owner 重新授权服务器操作后再复核。
5. Step 173/174/175 尚未部署到服务器，`V14__campus_feedback_admin_processing.sql` 也未在服务器执行。

## 下一步建议

1. owner 私下生成生产 release keystore，或配置 GitHub Actions signing secrets。
2. 使用生产签名材料跑一次 `.github/workflows/android-release.yml` 或本地 `build-android-release-apks.ps1 -RequireSigning`。
3. 用 MuMu / 真机安装生产 signed release 包，验证登录、创建代送单、模拟支付、接单、取餐、配送、完成等主链路。
4. owner 重新明确授权服务器操作后，再部署当前版本并执行远端 API / SPA / admin 反馈 / Android release 包 smoke。

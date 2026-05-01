# Step 152 - Android 双端 QA APK 打包与安装启动 smoke

## 本轮目标

1. 承接 Step 151 的 Android 双端稳定性复核，把用户端和兼职端从“能构建 / 能 WebView smoke”推进到“能重复生成可安装 QA Debug APK”。
2. 新增统一脚本，生成双端 APK、复制到本地归档目录并生成 SHA256 manifest。
3. 使用模拟器验证两个 APK 可安装、可启动并保存首屏截图。
4. 不做 release 签名包、不对外分发、不做 HTTPS / 域名 / 证书收口。
5. 不改 bridge、不改鉴权、不改接口、不改路由、不改业务页面。

## 实际新增能力

### 1. Android QA APK 打包脚本

新增：

```text
scripts/trial-operation/build-android-qa-apks.ps1
```

脚本能力：

1. 支持 `default / emulator / lan / public` 四种构建模式。
2. 默认 `public` 模式，用于 owner-controlled 内测 APK 准备。
3. 自动查找 JDK 21，优先使用：
   - 显式 `-Jdk21Home`
   - 当前 `JAVA_HOME`
   - `D:\software\jdk-21-temurin`
   - 常见 `C:\Program Files` JDK 21 目录
4. 依次执行用户端和兼职端：
   - `npm run cap:sync:<mode>`
   - `gradlew.bat assembleDebug`
5. 复制 APK 到 git 忽略目录：
   - `project-logs/campus-relay/runtime/android-qa-apks/<timestamp>/`
6. 写入 `android-qa-apk-manifest.json`，记录：
   - commit
   - mode
   - JDK 21 路径
   - packageName
   - APK 路径
   - 文件大小
   - SHA256

该目录已加入 `.gitignore`，避免提交 APK 二进制文件。

### 2. Android QA APK 交付说明

新增：

```text
docs/mobile/android-qa-apk-handoff.md
```

说明覆盖：

1. 用户端与兼职端包名和入口。
2. QA Debug APK 生成命令。
3. JDK 21 要求。
4. 模拟器 / 真机安装命令。
5. 当前验证边界。
6. 不做事项：不提交 APK、不提交签名密钥、不把 debug APK 当 release 包。

### 3. 命令索引更新

已更新：

1. `scripts/trial-operation/README.md`
2. `scripts/trial-operation/commands.ps1`

新增可执行入口：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\build-android-qa-apks.ps1 -Mode public
```

## 本轮真实验证

### 1. QA APK 打包验证

执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\build-android-qa-apks.ps1 -Mode public -OutputDirectory "project-logs\campus-relay\runtime\android-qa-apks\step-152-public"
```

结果：通过。

实际生成文件：

1. 用户端 APK：
   - `project-logs/campus-relay/runtime/android-qa-apks/step-152-public/campus-user-public-debug.apk`
   - size：`5138258`
   - SHA256：`88615F6501598677D994DDFEE427DCCFAB93A1CF0E6228BF3FBA9D6EDCB92FC0`
2. 兼职端 APK：
   - `project-logs/campus-relay/runtime/android-qa-apks/step-152-public/campus-parttime-public-debug.apk`
   - size：`5138270`
   - SHA256：`726CBB549B15344618E9EDD060A528B58FCB339128313CD0B3404AD2267F4EFE`
3. manifest：
   - `project-logs/campus-relay/runtime/android-qa-apks/step-152-public/android-qa-apk-manifest.json`

注意：APK 和 manifest 位于 git 忽略目录，仅作为本地交付产物，不提交仓库。

### 2. 模拟器安装启动 smoke

执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator -ClearData -LaunchWaitSeconds 12
```

结果：通过。

验证内容：

1. 启动 `campus_api35` 模拟器。
2. 安装用户端 `com.xiaoyu.campus.user` 成功。
3. 安装兼职端 `com.xiaoyu.campus.parttime` 成功。
4. 清理用户端数据后启动成功。
5. 清理兼职端数据后启动成功。
6. 保存两个首屏截图。

截图证据：

1. `project-logs/campus-relay/runtime/android-smoke/20260501-182338-user-app-launch.png`
2. `project-logs/campus-relay/runtime/android-smoke/20260501-182353-parttime-app-launch.png`

## 当前 Android 双端结论

当前可以确认：

1. 用户端和兼职端 public Web 构建可重复执行。
2. 用户端和兼职端 Capacitor sync 可重复执行。
3. 用户端和兼职端 Debug APK 可在 JDK 21 下构建。
4. 已有统一 QA APK 打包脚本。
5. 已有本地 manifest / SHA256 校验信息。
6. 模拟器安装和首屏启动通过。

当前仍不能宣称：

1. 真机长期稳定。
2. release 签名包稳定。
3. 外部分发可用。
4. HTTPS / 域名 / 证书完成。
5. Android cleartext 已收口。
6. 真机网络、权限、WebView 兼容性已覆盖。

因此本轮结论是：

```text
Android 用户端和兼职端已完成 owner-controlled QA Debug APK 准备与模拟器安装启动验证。
仍不等于正式 release 包或外部真机长期内测稳定。
```

## 明确没有改动

本轮没有修改：

1. Vue 页面业务行为。
2. Java 后端业务代码。
3. 数据库。
4. 后端接口。
5. 鉴权。
6. `request.js`。
7. token 附着逻辑。
8. bridge。
9. 路由。
10. 旧兼容模块。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 153 建议不要再继续无反馈地扩 Android 功能。建议二选一：

1. **真机手工 smoke 轮**：把本轮生成的 QA Debug APK 安装到真实 Android 手机，验证登录、页面加载、网络、返回键、滚动、输入框和关键动作。
2. **release 签名准备轮**：仅整理签名、版本号、包名、输出命名和 release 边界，不提交 keystore，不生成对外分发包。

如果暂时没有真机条件，建议回到 Web / 管理端 owner-controlled 内测，Android 仅作为模拟器 QA APK 基线保留。

# Android 壳工程

当前移动端策略是：一份 `frontend/` 业务源码，两个 Capacitor Android 壳。

## 壳工程

1. `mobile/user-app`
   - App 名称：校内兼职
   - 包名：`com.xiaoyu.campus.user`
   - 前端构建产物：`frontend/dist-android-user`
   - 默认入口：`/user/login`
2. `mobile/parttime-app`
   - App 名称：校内兼职端
   - 包名：`com.xiaoyu.campus.parttime`
   - 前端构建产物：`frontend/dist-android-parttime`
   - 默认入口：`/parttime/login`

## 常用命令

### 前端产物与 Capacitor 同步

先在 `frontend/` 生成对应移动端构建产物：

```bash
npm run build:android:user
npm run build:android:parttime
```

用户端壳：

```bash
cd mobile/user-app
npm install
npm run cap:sync
```

兼职端壳：

```bash
cd mobile/parttime-app
npm install
npm run cap:sync
```

仓库中提交的 `capacitor.config.json` 默认为 release-safe：`androidScheme=https`、`cleartext=false`。本地同步脚本会按场景临时改写：

1. `cap:sync` / `cap:sync:emulator` / `cap:sync:lan`：HTTP + cleartext，用于本地模拟器或局域网调试。
2. `cap:sync:public`：HTTPS + no-cleartext，用于公网内测和 release candidate。

`cap:sync` 默认仍使用模拟器 API base。局域网真机或公网内测不要直接用默认 `cap:sync`，否则会把已生成的 LAN/Public 构建覆盖回 `10.0.2.2`。对应场景使用：

```bash
cd mobile/user-app
npm run cap:sync:lan
npm run cap:sync:public

cd ../parttime-app
npm run cap:sync:lan
npm run cap:sync:public
```

### Debug APK 构建

Android 本地构建需要：

1. JDK 21。
2. Android SDK `platform-tools`、`platforms;android-36`、`build-tools;36.0.0`。
3. 每个壳工程自己的 `android/local.properties` 指向本机 SDK，例如：

```properties
sdk.dir=C:/Users/20278/AppData/Local/Android/Sdk
```

`local.properties`、`android/app/build/`、`android/app/src/main/assets/` 和 `android/app/src/main/res/xml/config.xml` 都是本地生成内容，不提交到仓库。

用户端 APK：

```bash
cd mobile/user-app
npm run cap:sync
cd android
./gradlew assembleDebug --no-daemon
```

兼职端 APK：

```bash
cd mobile/parttime-app
npm run cap:sync
cd android
./gradlew assembleDebug --no-daemon
```

Debug APK 输出位置：

1. `mobile/user-app/android/app/build/outputs/apk/debug/app-debug.apk`
2. `mobile/parttime-app/android/app/build/outputs/apk/debug/app-debug.apk`

当前 Android 构建已优先使用国内镜像：

1. Gradle wrapper 分发包走腾讯 Gradle 镜像。
2. Android Gradle 依赖优先走阿里云 Maven 镜像，官方 `google()` / `mavenCentral()` 保留为兜底。

### Release APK / AAB 构建

正式发布构建统一使用：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\build-android-release-apks.ps1 `
  -VersionCode 175 `
  -VersionName 0.9.0-rc.175 `
  -ApiBase https://xiaoyu.xin/api `
  -Jdk21Home 'D:\software\jdk-21-temurin' `
  -OutputDirectory target\android-release-candidate
```

脚本会同时构建用户端和兼职端 release APK / AAB，并校验：

1. API base 必须是 HTTPS 且以 `/api` 结尾。
2. 包名、显示名、`versionCode`、`versionName` 正确。
3. 生成后的 Capacitor 配置为 `androidScheme=https`、`cleartext=false`。
4. release manifest 为 `usesCleartextTraffic=false`。
5. release manifest 为 `allowBackup=false`。
6. 如果传入 `-RequireSigning`，APK 必须通过 `apksigner verify`。

生成正式签名包前，owner 需要分别在两个壳工程下准备私有 `android/key.properties` 和 keystore。真实文件必须继续保持 ignored，不允许提交：

```powershell
Copy-Item mobile\user-app\android\key.properties.example mobile\user-app\android\key.properties
Copy-Item mobile\parttime-app\android\key.properties.example mobile\parttime-app\android\key.properties
```

然后执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\build-android-release-apks.ps1 `
  -VersionCode 175 `
  -VersionName 0.9.0-rc.175 `
  -ApiBase https://xiaoyu.xin/api `
  -Jdk21Home 'D:\software\jdk-21-temurin' `
  -OutputDirectory project-logs\campus-relay\runtime\android-release-validation\step-175 `
  -RequireSigning
```

如果没有真实 keystore，脚本可以构建 unsigned release candidate，但 unsigned APK 只能用于 CI 构建边界验证，不能公开分发。

GitHub Actions 手动 signed release workflow：

```text
.github/workflows/android-release.yml
```

需要 owner 配置以下 GitHub Secrets：

1. `ANDROID_USER_KEYSTORE_BASE64`
2. `ANDROID_USER_STORE_PASSWORD`
3. `ANDROID_USER_KEY_ALIAS`
4. `ANDROID_USER_KEY_PASSWORD`
5. `ANDROID_PARTTIME_KEYSTORE_BASE64`
6. `ANDROID_PARTTIME_STORE_PASSWORD`
7. `ANDROID_PARTTIME_KEY_ALIAS`
8. `ANDROID_PARTTIME_KEY_PASSWORD`

workflow 会在构建时临时生成 keystore 和 `key.properties`，上传 APK / AAB / manifest，并在结束时清理私有签名文件。

## 真机 / 模拟器 smoke

当前本机已准备过的模拟器基础：

1. Android Emulator：`emulator`
2. 系统镜像：`system-images;android-35;google_apis;x86_64`
3. AVD 名称：`campus_api35`

如果模拟器无法上线并提示 hypervisor driver 缺失，需要用管理员权限运行：

```powershell
C:\Users\20278\AppData\Local\Android\Sdk\extras\google\Android_Emulator_Hypervisor_Driver\silent_install.bat
```

安装后重新启动模拟器，再执行 smoke 脚本。没有 hypervisor 加速时，当前 x86_64 模拟器无法稳定进入 `adb devices` 在线状态。

当前本机已验证：

1. `campus_api35` 可进入 `adb devices -l` 在线状态。
2. 用户端与兼职端 Debug APK 可安装、启动和截图。
3. 兼职端 WebView 可通过 `http://10.0.2.2:8080/api` 调用本机 test profile 后端。
4. 用户端 WebView 可完成 `POST /api/users/login`。

### MuMu 模拟器

MuMu 12 也可作为本地 Android smoke 设备。不要同时启动 Android Studio AVD，避免 adb 把两台模拟器混在一起。

本机已验证的连接方式：

```powershell
$adb = 'D:\Program Files\Netease\MuMu\nx_device\12.0\shell\adb.exe'
& $adb connect 127.0.0.1:5555
& $adb devices -l
```

安装、启动和截图：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 `
  -AdbPath 'D:\Program Files\Netease\MuMu\nx_device\12.0\shell\adb.exe' `
  -DeviceId '127.0.0.1:5555' `
  -All -ClearData
```

当前 MuMu 网络中 `10.0.2.2` 可访问 Windows 宿主机，因此可复用 emulator 模式 API base。更换 MuMu 版本或网络模式后，应先在模拟器中重新验证宿主机可达性。

#### MuMu release smoke

release 包默认不暴露 WebView DevTools，因此 release smoke 只做安装后的启动、焦点、截图、logcat 错误和可选公网 health 检查：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-release-webview-smoke.ps1 `
  -ApiBase https://xiaoyu.xin/api `
  -AdbPath 'D:\Program Files\Netease\MuMu\nx_device\12.0\shell\adb.exe' `
  -DeviceId '127.0.0.1:5555' `
  -OutputDirectory project-logs\campus-relay\runtime\android-release-validation\step-175 `
  -ClearData
```

当前本机已验证：

1. 双端临时 QA 签名 release APK 可安装到 MuMu Android 12。
2. 双端包版本为 `versionCode=175`、`versionName=0.9.0-rc.175`。
3. 双端均可启动并获得焦点。
4. 双端首屏截图非空。
5. 未发现 `ERR_CLEARTEXT` 或 `net::ERR_*`。
6. `https://xiaoyu.xin/api/campus/public/health` 从本机访问超时，记录为可选远端可用性风险；在 owner 重新授权服务器操作前，不默认连接服务器处理。

### Android API base 分层

Android 构建必须按运行场景选择 API base，不要把模拟器、局域网真机和公网内测混用同一份配置。

#### 1. 本地模拟器

Android 模拟器访问宿主机后端时不能使用 `127.0.0.1`，需要使用 `10.0.2.2`。当前默认命令仍保留为模拟器安全配置：

```text
frontend/.env.android-user
frontend/.env.android-parttime
frontend/.env.android-user-emulator
frontend/.env.android-parttime-emulator
```

默认内容为：

```properties
VITE_API_BASE_URL=http://10.0.2.2:8080/api
VITE_USE_MOCK=false
```

构建命令：

```bash
npm run build:android:user
npm run build:android:parttime
npm run build:android:user:emulator
npm run build:android:parttime:emulator
```

#### 2. 局域网真机

真机访问开发机后端时必须使用开发机在同一局域网中的 IP，例如 `http://192.168.1.100:8080/api`。不要使用 `10.0.2.2`，它只适用于 Android 模拟器。

先复制 example，再填入真实局域网 IP：

```powershell
Copy-Item frontend\.env.android-user-lan.example frontend\.env.android-user-lan
Copy-Item frontend\.env.android-parttime-lan.example frontend\.env.android-parttime-lan
```

构建命令：

```bash
npm run build:android:user:lan
npm run build:android:parttime:lan
```

#### 3. 公网 / 服务器内测

公网内测应优先使用 HTTPS 域名或反向代理后的公网 API base，例如 `https://your-domain.example.com/api`。如果临时使用公网 IP + HTTP，只能作为短期内测 smoke，不能作为长期发布口径。

先复制 example，再填入真实公网地址：

```powershell
Copy-Item frontend\.env.android-user-public.example frontend\.env.android-user-public
Copy-Item frontend\.env.android-parttime-public.example frontend\.env.android-parttime-public
```

构建命令：

```bash
npm run build:android:user:public
npm run build:android:parttime:public
```

同步到 Android 壳时使用对应的 public sync 命令，避免默认 `cap:sync` 重新生成模拟器构建：

```bash
cd mobile/user-app
npm run cap:sync:public

cd ../parttime-app
npm run cap:sync:public
```

在打包前建议先检查公网 API base 上的只读依赖是否可用。该检查只访问用户端代送入口依赖的 public API，不会创建订单或写入数据：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-public-api-smoke.ps1 -ApiBase https://your-domain.example.com/api
```

如果返回失败，只能说明“该 API base 当前不适合做 Android WebView 真实接口 smoke”；仍可继续验证构建模式，但不要把失败写成真机 / 公网 smoke 通过。

#### 4. API base 检查

运行脚本检查当前 Android API base 分层：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-api-base-check.ps1
```

本脚本会把未创建的 LAN / public 本地 env 视为 warning；只有默认模拟器配置缺失或类型错误时才视为 hard failure。使用 `-Strict` 时，LAN / public 本地 env 缺失也会视为 hard failure。

后端 test/dev profile 已放行 Android WebView 本地 origin：`http://localhost`、`https://localhost`、`capacitor://localhost`。

### 本地 cleartext 边界

两个 Android 壳的提交态默认为 release-safe：

1. `server.androidScheme = "https"`
2. `server.cleartext = false`
3. release AndroidManifest `usesCleartextTraffic = false`
4. release AndroidManifest `allowBackup = false`

本地 / 内测 smoke 使用脚本临时切换：

1. `cap:sync` / `cap:sync:emulator` / `cap:sync:lan` 会设置 `server.androidScheme = "http"`、`server.cleartext = true`。
2. Debug manifest 继续允许 `usesCleartextTraffic=true`。
3. `cap:sync:public` 会恢复 `server.androidScheme = "https"`、`server.cleartext = false`。

原因是 Capacitor 默认 `https://localhost` 会拦截页面访问本地 HTTP 后端。正式公网试运营时应改为 HTTPS 域名和正式 API base，不应把 `10.0.2.2` 当作真机或服务器配置。

### JDK 21

Android Gradle 构建需要 JDK 21。当前本机可使用：

```powershell
$env:JAVA_HOME='D:\software\jdk-21-temurin'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

先确认设备在线：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -ListDevices
```

安装并启动两个 APK：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1
```

如果使用本机已创建的 `campus_api35` 模拟器：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator
```

脚本会检查并启动：

1. 用户端包名：`com.xiaoyu.campus.user`
2. 兼职端包名：`com.xiaoyu.campus.parttime`

如有多台设备，使用：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -DeviceId <adb-serial>
```

延长首屏等待时间：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -DeviceId emulator-5554 -LaunchWaitSeconds 8
```

如果要验证干净首屏入口，建议清空两个壳的本地 WebView / app data，避免恢复上一次停留路由：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator -ClearData
```

本脚本只负责安装、启动和截图，不自动断言 WebView 内登录是否成功。App 内 `/api` 请求、用户端 / 兼职端登录态隔离、移动网络下后端地址可达性仍需手工或 DevTools smoke 确认。

## 边界

1. Android 壳只负责容器、包名、App 名称和原生工程。
2. 不复制第二套前端源码。
3. 不把 admin 后台打进移动端默认入口。
4. 不因 Android 壳改 bridge、鉴权、token 附着逻辑或后端接口。

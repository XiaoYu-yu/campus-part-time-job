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

本脚本只负责安装、启动和截图，不判断 WebView 内登录是否成功。App 内 `/api` 请求、用户端 / 兼职端登录态隔离、移动网络下后端地址可达性仍需手工 smoke 确认。

## 边界

1. Android 壳只负责容器、包名、App 名称和原生工程。
2. 不复制第二套前端源码。
3. 不把 admin 后台打进移动端默认入口。
4. 不因 Android 壳改 bridge、鉴权、token 附着逻辑或后端接口。

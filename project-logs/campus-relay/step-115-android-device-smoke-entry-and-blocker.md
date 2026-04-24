# Step 115 - Android 真机 / 模拟器 smoke 入口与阻塞确认

## 本轮目标

1. 继续 Step 114 的双 Android APK 构建成果。
2. 优先尝试进入真机或模拟器 smoke。
3. 如果当前环境没有在线设备，不伪造安装与首屏验证结果。
4. 补一个可重复执行的 Android smoke 脚本，便于设备接入后直接验证。
5. 不改前端业务页面、不改 bridge、不改鉴权、不改接口、不改路由。

## 实际环境检查

本机 Android SDK 已存在：

```text
C:\Users\20278\AppData\Local\Android\Sdk
```

已安装 SDK 组件：

1. `platform-tools`
2. `platforms;android-36`
3. `build-tools;35.0.0`
4. `build-tools;36.0.0`
5. `emulator`
6. `system-images;android-35;google_apis;x86_64`

本轮新增创建 AVD：

```text
campus_api35
```

AVD 路径：

```text
C:\Users\20278\.android\avd\campus_api35.avd
```

本轮执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -ListDevices
```

结果：

```text
Using adb: C:\Users\20278\AppData\Local\Android\Sdk\platform-tools\adb.exe
Connected devices:
List of devices attached
```

结论：

1. 当前本机没有在线 Android 真机。
2. 当前本机已经有 `campus_api35` 模拟器配置，但模拟器未能进入 ADB 在线状态。
3. 因此无法真实执行 APK 安装、App 首屏、WebView API 和 storage 隔离验证。

## 模拟器启动阻塞

本轮已安装：

1. Android Emulator
2. Android 35 Google APIs x86_64 system image
3. Android Emulator Hypervisor Driver 安装包

尝试启动：

```powershell
emulator.exe -avd campus_api35 -no-snapshot -gpu swiftshader_indirect -no-boot-anim
```

结果：

1. 5 分钟内 `adb devices` 仍为空。
2. `sys.boot_completed` 无法读取。

检查：

```powershell
emulator.exe -accel-check
```

结果：

```text
Android Emulator hypervisor driver is not installed on this machine
```

已尝试安装 driver：

```powershell
C:\Users\20278\AppData\Local\Android\Sdk\extras\google\Android_Emulator_Hypervisor_Driver\silent_install.bat
```

结果：

1. 安装器请求管理员权限。
2. 当前普通命令环境未完成驱动安装。
3. 再次 `emulator -accel-check` 仍显示 driver 未安装。

已尝试软件加速启动：

```powershell
emulator.exe -avd campus_api35 -no-snapshot -no-window -no-audio -gpu swiftshader_indirect -accel off -no-boot-anim -memory 2048
```

结果：

1. 6 分钟内 `adb devices` 仍为空。
2. 当前 x86_64 模拟器仍无法作为可用 smoke 设备。

下一步必须先用管理员权限安装 hypervisor driver，或通过 Android Studio Device Manager 完成等价模拟器加速配置。

## 新增 Android smoke 脚本

新增：

```text
scripts/trial-operation/android-smoke.ps1
```

脚本能力：

1. 自动定位 `adb.exe`。
2. 支持 `-ListDevices` 列出在线设备。
3. 支持 `-StartEmulator` 启动本机 `campus_api35` AVD 并等待 boot。
4. 支持默认安装两个 Debug APK。
5. 支持启动用户端和兼职端 App。
6. 支持保存两个 App 的启动截图到：
   - `project-logs/campus-relay/runtime/android-smoke/`
7. 支持 `-DeviceId <adb-serial>` 指定设备。
8. 当前无设备时明确失败，不把未执行项写成通过。

默认命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1
```

启动本机已创建的模拟器再执行 smoke：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator
```

当前无设备时输出：

```text
No online Android device or emulator found. Connect a phone with USB debugging enabled or start an emulator, then rerun this script.
```

## APK 与包名边界

脚本使用 Step 114 已产出的 Debug APK：

1. 用户端 APK：
   - `mobile/user-app/android/app/build/outputs/apk/debug/app-debug.apk`
   - 包名：`com.xiaoyu.campus.user`
2. 兼职端 APK：
   - `mobile/parttime-app/android/app/build/outputs/apk/debug/app-debug.apk`
   - 包名：`com.xiaoyu.campus.parttime`

脚本只做安装、启动和截图，不判断业务登录是否成功。

## 仍未真实验证的内容

由于当前没有在线设备 / 可用模拟器，本轮没有完成：

1. 用户端 APK 安装验证。
2. 兼职端 APK 安装验证。
3. 用户端首屏是否进入 `/user/login`。
4. 兼职端首屏是否进入 `/parttime/login`。
5. Capacitor WebView 内 `/api/**` 请求是否能正确访问后端。
6. 是否需要移动端专用 API base URL。
7. 用户端包与兼职端包的 WebView storage / token 隔离。

这些内容必须等真机或模拟器在线后再执行，不允许在日志中提前写通过。

## 文档同步

已同步更新：

1. `scripts/trial-operation/README.md`
2. `mobile/README.md`

新增说明包括：

1. 如何列出设备。
2. 如何执行默认 Android smoke。
3. 如何用 `-StartEmulator` 启动本机 `campus_api35`。
4. 多设备时如何指定 `DeviceId`。
5. 当前脚本的边界：不改业务、不判断登录结果、不替代手工 App 内联调。
6. 模拟器需要 Android Emulator Hypervisor Driver 或等价 Windows hypervisor 配置。

## 本轮明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改 `campus-*` API 文件运行时行为。
4. 没有改 token 附着逻辑。
5. 没有改前端业务页面。
6. 没有改后端接口、鉴权或订单状态机。
7. 没有新增 Android 原生业务页面。
8. 没有补第五个 admin 页。

## 验证结果

已完成：

1. `android-smoke.ps1 -ListDevices` 可正常执行。
2. `android-smoke.ps1` 默认 smoke 在无设备时明确失败。
3. 安装 Android Emulator 与 Android 35 x86_64 system image。
4. 创建 `campus_api35` AVD。
5. 确认当前阻塞是 hypervisor driver 未安装，而不是 APK 缺失或脚本解析错误。

待设备或可用模拟器接入后继续执行：

1. 安装两个 APK。
2. 启动两个 App。
3. 截图两个首屏。
4. 手工验证登录、API base URL 和 token 隔离。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有触发 bridge 恢复推进条件，也没有删除或收紧：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 116 建议进入“Android 模拟器加速修复与真实 smoke”：

1. 用管理员权限运行：

```powershell
C:\Users\20278\AppData\Local\Android\Sdk\extras\google\Android_Emulator_Hypervisor_Driver\silent_install.bat
```

2. 重新启动 `campus_api35` 模拟器。
3. 执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1
```

4. 验证用户端首屏、兼职端首屏。
5. 验证移动端 WebView 内 `/api` 请求。
6. 若 App 内 `/api` 相对路径不可用，再评估移动端 API base URL 配置，不要提前改接口语义。

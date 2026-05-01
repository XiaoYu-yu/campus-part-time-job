# Android 双端 QA APK 交付说明

## 目标

本说明用于把当前 Android 用户端与兼职端整理成可重复生成、可校验、可安装的本地 QA Debug APK。

当前 APK 只用于 owner-controlled 内测 smoke，不等同于应用商店 release 包或外部长期试运营包。

## 双端范围

1. 用户端
   - Capacitor 壳目录：`mobile/user-app`
   - Android 包名：`com.xiaoyu.campus.user`
   - 默认入口：`/user/login`
2. 兼职端
   - Capacitor 壳目录：`mobile/parttime-app`
   - Android 包名：`com.xiaoyu.campus.parttime`
   - 默认入口：`/parttime/login`

## 生成 QA Debug APK

在仓库根目录执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\build-android-qa-apks.ps1 -Mode public
```

脚本会执行：

1. 用户端 public Web 构建与 Capacitor sync。
2. 用户端 `assembleDebug`。
3. 兼职端 public Web 构建与 Capacitor sync。
4. 兼职端 `assembleDebug`。
5. 复制两个 APK 到 git 忽略目录。
6. 写入 `android-qa-apk-manifest.json`，记录 commit、文件大小和 SHA256。

默认输出目录：

```text
project-logs/campus-relay/runtime/android-qa-apks/<timestamp>/
```

该目录已加入 `.gitignore`，不要提交 APK 二进制文件。

## JDK 要求

Android Gradle 构建需要 JDK 21。

脚本会优先查找：

1. 显式传入的 `-Jdk21Home`
2. 当前 `JAVA_HOME`
3. `D:\software\jdk-21-temurin`
4. 常见 `C:\Program Files` JDK 21 目录

如果本机路径不同，执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\build-android-qa-apks.ps1 -Mode public -Jdk21Home "你的 JDK 21 路径"
```

## 安装到模拟器或真机

列出设备：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -ListDevices
```

安装并启动当前构建输出：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -ClearData
```

如果有多个设备在线，指定设备：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -DeviceId <adb-serial> -ClearData
```

## 验证边界

当前已验证：

1. 两端 Web public 构建。
2. 两端 Capacitor sync。
3. 两端 Debug APK 构建。
4. 模拟器 WebView public smoke。

当前仍未宣称：

1. 真机长期稳定。
2. release 签名包稳定。
3. HTTPS / 域名 / 证书完成。
4. Android cleartext 已收口。
5. 外部用户长期试运营可分发。

## 不做事项

1. 不在脚本中写入或提交真实 API host。
2. 不提交 APK 文件。
3. 不提交 keystore、签名密码或任何密钥。
4. 不改 bridge、鉴权、token 附着或路由。
5. 不把 Debug APK 当正式 release 包。

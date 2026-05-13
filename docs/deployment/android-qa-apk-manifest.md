# Android QA APK Manifest

本文件记录当前 owner-controlled 小范围内测使用的 Android 双端 QA Debug 包信息。它只用于本地/内测发包核对，不等于生产 release 包。

## 生成信息

- 生成轮次：Step 166
- 生成时间：2026-05-13 19:55:46 +08:00
- 对应 Git commit：`0e118ce21ef32b097a295ddd7ad07735d7889457`
- 构建模式：`public`
- 构建脚本：`scripts/trial-operation/build-android-qa-apks.ps1 -Mode public -OutputDirectory project-logs\campus-relay\runtime\step-166-android-qa-apks`
- JDK：21
- 产物目录：`project-logs/campus-relay/runtime/step-166-android-qa-apks/`

## APK 清单

| 端 | 文件名 | 包名 | 显示名 | versionCode | versionName | 大小 | SHA256 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 用户端 | `campus-user-public-debug.apk` | `com.xiaoyu.campus.user` | 用户端 | 1 | 1.0 | 5,864,944 bytes | `76398CB22068056DB3845A8AC749D914D4A15381783A643AD992D131CFE855CB` |
| 兼职端 | `campus-parttime-public-debug.apk` | `com.xiaoyu.campus.parttime` | 兼职端 | 1 | 1.0 | 5,574,482 bytes | `197DFD540206EE427D1B5C11AF34E29341BC8C1F985FEC9DED12429B79E8B812` |

## 安装复核

真实设备：

- ADB 设备：`10AE221PGA003Y5`

安装命令：

```powershell
adb install -r "project-logs\campus-relay\runtime\step-166-android-qa-apks\campus-user-public-debug.apk"
adb install -r "project-logs\campus-relay\runtime\step-166-android-qa-apks\campus-parttime-public-debug.apk"
```

结果：

- 用户端安装：`Success`
- 兼职端安装：`Success`
- 用户端 `dumpsys package`：`versionCode=1`，`versionName=1.0`，`lastUpdateTime=2026-05-13 19:56:28`
- 兼职端 `dumpsys package`：`versionCode=1`，`versionName=1.0`，`lastUpdateTime=2026-05-13 19:56:33`
- 用户端 `aapt dump badging`：包名 `com.xiaoyu.campus.user`，显示名 `用户端`
- 兼职端 `aapt dump badging`：包名 `com.xiaoyu.campus.parttime`，显示名 `兼职端`

启动截图：

- 用户端：`project-logs/campus-relay/runtime/step-166-android-qa-apks/user-qa-install-launch.png`
- 兼职端：`project-logs/campus-relay/runtime/step-166-android-qa-apks/parttime-qa-install-launch.png`

## 使用边界

- 这是 Debug QA 包，只适合 owner-controlled 小范围内测。
- 当前仍允许 cleartext，用于内测公网 API 验证；公开公测前应完成 HTTPS / 域名 / 证书和 cleartext 收口。
- 本文件不记录公网地址、服务器密码、GitHub token、腾讯地图 key、`.env` 内容或测试账号密码。
- APK 二进制产物不提交到 Git 仓库，只保留在本地 runtime 目录或由 owner 私下分发。

## 下一步

若要继续扩大测试人群，应先补：

1. release 签名策略。
2. HTTPS / 域名 / 证书。
3. 最小隐私说明与用户协议。
4. 内测反馈入口和问题分级规则。

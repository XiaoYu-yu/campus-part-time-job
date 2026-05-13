# Step 166 - Android QA APK Manifest 与安装复核

## 本轮目标

基于 Step 165 已跑通的 Python Android 双端动作链，补齐一轮 QA APK 分发包 manifest 和真实手机安装复核。

本轮不继续开发功能，不重开 bridge，不改前端样式，不改后端接口，只确认当前双端 APK 是否可构建、可识别、可安装、可启动。

## 生成命令

```powershell
.\scripts\trial-operation\build-android-qa-apks.ps1 -Mode public -OutputDirectory "project-logs\campus-relay\runtime\step-166-android-qa-apks"
```

## 生成结果

产物目录：

- `project-logs/campus-relay/runtime/step-166-android-qa-apks/`

本轮生成：

- `campus-user-public-debug.apk`
- `campus-parttime-public-debug.apk`
- `android-qa-apk-manifest.json`（本地生成文件，不提交）

安全版 manifest：

- `docs/deployment/android-qa-apk-manifest.md`

## APK 信息

### 用户端

- 文件名：`campus-user-public-debug.apk`
- 包名：`com.xiaoyu.campus.user`
- 显示名：`用户端`
- versionCode：`1`
- versionName：`1.0`
- 大小：`5,864,944 bytes`
- SHA256：`76398CB22068056DB3845A8AC749D914D4A15381783A643AD992D131CFE855CB`

### 兼职端

- 文件名：`campus-parttime-public-debug.apk`
- 包名：`com.xiaoyu.campus.parttime`
- 显示名：`兼职端`
- versionCode：`1`
- versionName：`1.0`
- 大小：`5,574,482 bytes`
- SHA256：`197DFD540206EE427D1B5C11AF34E29341BC8C1F985FEC9DED12429B79E8B812`

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

## 包名与显示名核验

使用 `aapt dump badging` 核验：

- 用户端：包名 `com.xiaoyu.campus.user`，显示名 `用户端`
- 兼职端：包名 `com.xiaoyu.campus.parttime`，显示名 `兼职端`

## 启动截图

- 用户端启动截图：`project-logs/campus-relay/runtime/step-166-android-qa-apks/user-qa-install-launch.png`
- 兼职端启动截图：`project-logs/campus-relay/runtime/step-166-android-qa-apks/parttime-qa-install-launch.png`

## 本轮没有做什么

- 没有改业务代码。
- 没有改前端页面。
- 没有改后端接口、数据库、鉴权或路由。
- 没有改 Android 包名或版本号。
- 没有重开 bridge。
- 没有改 `request.js`。
- 没有改 token 附着逻辑。
- 没有删除旧外卖兼容模块。
- 没有提交 APK 二进制产物。
- 没有提交公网地址、服务器密码、GitHub token、腾讯地图 key、测试账号密码或 `.env` 内容。

## 为什么不提交 APK 本体

APK 是二进制分发产物，不适合作为主仓库长期跟踪对象。本轮只提交安全版 manifest、安装复核日志和启动截图；APK 本体保留在本地 runtime 目录，由 owner 私下分发。

## 验证结果

- `build-android-qa-apks.ps1 -Mode public`：通过。
- 用户端 APK 安装：通过。
- 兼职端 APK 安装：通过。
- `aapt dump badging` 包名 / 显示名核验：通过。
- 双端启动截图采集：通过。

## 当前结论

当前已经具备 owner-controlled 小范围内测的 QA APK 分发包清单和安装复核证据。它仍不是公开公测 release 包。

## 当前 bridge 结论

bridge 继续保持 `Phase A no-op` 冻结态。本轮未触发恢复推进条件。

## 下一轮建议

Step 167 建议进入“公开公测前安全与发布缺口收口评估”：

1. release 签名策略。
2. HTTPS / 域名 / 证书。
3. cleartext 收口方案。
4. 隐私说明与用户协议。
5. 内测反馈入口和问题分级规则。

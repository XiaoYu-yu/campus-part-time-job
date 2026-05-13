# Step 161 - Android 双端显示名调整

## 本轮目标

把 Android 双端在手机桌面和系统任务中的显示名称改成更直观的中文端名：

- 用户端：显示为 `用户端`
- 兼职端：显示为 `兼职端`

说明：这里调整的是 Android launcher label / appName，不是 Java/Kotlin 层的真实 `applicationId`。真实包名必须继续保持合法英文包名，否则 Android 安装、升级、调试和发布都会出问题。

## 实际改动

### 用户端

- `mobile/user-app/capacitor.config.json`
  - `appName` 改为 `用户端`
- `mobile/user-app/android/app/src/main/res/values/strings.xml`
  - `app_name` 改为 `用户端`
  - `title_activity_main` 改为 `用户端`
- `mobile/user-app/android/app/src/main/assets/capacitor.config.json`
  - 通过 Capacitor sync 生成并同步 `appName = 用户端`

### 兼职端

- `mobile/parttime-app/capacitor.config.json`
  - `appName` 改为 `兼职端`
- `mobile/parttime-app/android/app/src/main/res/values/strings.xml`
  - `app_name` 改为 `兼职端`
  - `title_activity_main` 改为 `兼职端`
- `mobile/parttime-app/android/app/src/main/assets/capacitor.config.json`
  - 通过 Capacitor sync 生成并同步 `appName = 兼职端`

## 保持不变

- 用户端真实包名仍为 `com.xiaoyu.campus.user`
- 兼职端真实包名仍为 `com.xiaoyu.campus.parttime`
- 未改 `applicationId`
- 未改后端、接口、路由、bridge、`request.js`、token 附着逻辑
- 未改管理后台
- 未改 Android 权限、签名、网络配置

## 验证结果

- `mobile/user-app` 执行 `npm run cap:sync:public` 通过
- `mobile/parttime-app` 执行 `npm run cap:sync:public` 通过
- 用户端 Debug APK 构建通过
- 兼职端 Debug APK 构建通过
- 构建时需使用 Java 21；当前本机可用路径为 `D:\software\jdk-21-temurin`
- `aapt dump badging` 确认：
  - 用户端 package 为 `com.xiaoyu.campus.user`，`application-label` 为 `用户端`
  - 兼职端 package 为 `com.xiaoyu.campus.parttime`，`application-label` 为 `兼职端`
- ADB 设备 `10AE221PGA003Y5` 在线
- 用户端 `adb install -r` 成功
- 兼职端 `adb install -r` 成功
- `adb shell pm list packages` 可见：
  - `package:com.xiaoyu.campus.user`
  - `package:com.xiaoyu.campus.parttime`
- 用户端和兼职端均已通过 `adb shell monkey ... LAUNCHER` 启动验证
- `git diff --check` 通过，仅有既有 CRLF 换行提示

## 当前结论

Android 双端显示名已完成调整并安装到真机。当前手机桌面应显示为 `用户端` 和 `兼职端`，真实包名保持英文合法包名，后续仍可正常覆盖安装和调试。

## 下一轮建议

1. 在手机桌面手动确认图标下方显示名是否符合预期。
2. 若显示名仍被系统桌面缓存，可卸载后重新安装或等待桌面刷新。
3. 下一轮建议继续做 Android 双端真机小回归：登录、发单、查单、兼职端接单、确认取餐、确认送达、用户确认完成。
4. 若准备内测分发，再进入 release 签名包、安装说明和反馈模板整理。

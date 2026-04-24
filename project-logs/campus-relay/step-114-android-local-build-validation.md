# Step 114 - Android 本机构建验证

## 本轮目标

1. 继续 Step 113 的双 Capacitor Android 壳工作。
2. 解决 `assembleDebug` 首轮超时和环境阻塞。
3. 验证用户端和兼职端两个 Android 壳都能真实产出 Debug APK。
4. 不改前端业务页面、不改 bridge、不改鉴权、不改后端接口、不改路由。

## 实际处理

### 1. Gradle 下载阻塞

首轮用户端 `assembleDebug` 失败点不是 Android 工程编译，而是 Gradle wrapper 默认下载：

```text
https://services.gradle.org/distributions/gradle-8.14.3-all.zip
```

默认读取超时为 `10000ms`，在当前网络下不稳定。

本轮修正：

1. 两个壳的 Gradle wrapper 从 `gradle-8.14.3-all.zip` 改为更轻的 `gradle-8.14.3-bin.zip`。
2. 分发地址切到腾讯 Gradle 镜像：
   - `https://mirrors.cloud.tencent.com/gradle/gradle-8.14.3-bin.zip`
3. `networkTimeout` 调整为 `120000`。

### 2. Android Maven 依赖下载

为了减少后续依赖解析的不稳定性，本轮在两个 Android 壳的顶层 `build.gradle` 中加入国内 Maven 镜像优先级：

1. `https://maven.aliyun.com/repository/google`
2. `https://maven.aliyun.com/repository/public`
3. 官方 `google()`
4. 官方 `mavenCentral()`

官方源仍保留为兜底，不改变依赖语义。

### 3. 本机 Android SDK 与 JDK

本机初始缺口：

1. `ANDROID_HOME` / `ANDROID_SDK_ROOT` 未配置。
2. 本机没有 Android SDK。
3. 当前默认 Java 是 JDK 17，但 Capacitor Android 8 的编译链路需要 Java source 21。

本轮本地处理：

1. 使用用户手动下载到 `D:\software\commandlinetools-win-13114758_latest.zip` 的 Android commandline tools。
2. 安装 SDK 到：
   - `C:\Users\20278\AppData\Local\Android\Sdk`
3. 从腾讯 AndroidSDK 镜像手动安装：
   - `platform-tools_r37.0.0-win.zip`
   - `platform-36_r02.zip`
   - `build-tools_r36_windows.zip`
4. 从清华 Adoptium 镜像安装 JDK 21 到：
   - `C:\Users\20278\AppData\Local\Programs\Java\jdk-21.0.10+7`
5. 两个 Android 壳生成本地 `local.properties`，指向本机 SDK。

说明：

1. Android SDK、JDK、`local.properties`、APK 和 Gradle build 目录都没有提交到仓库。
2. `local.properties` 已由 Android scaffold 的 `.gitignore` 忽略。

## APK 构建结果

### 用户端

执行：

```powershell
cd mobile/user-app
npm run cap:sync
cd android
.\gradlew.bat assembleDebug --no-daemon
```

结果：

```text
BUILD SUCCESSFUL
```

APK 输出：

```text
mobile/user-app/android/app/build/outputs/apk/debug/app-debug.apk
```

大小：

```text
4925653 bytes
```

### 兼职端

执行：

```powershell
cd mobile/parttime-app
npm run cap:sync
cd android
.\gradlew.bat assembleDebug --no-daemon
```

结果：

```text
BUILD SUCCESSFUL
```

APK 输出：

```text
mobile/parttime-app/android/app/build/outputs/apk/debug/app-debug.apk
```

大小：

```text
4925665 bytes
```

## 验证结果

已通过：

1. `mobile/user-app`：`npm run cap:sync`
2. `mobile/parttime-app`：`npm run cap:sync`
3. 用户端：`.\gradlew.bat assembleDebug --no-daemon`
4. 兼职端：`.\gradlew.bat assembleDebug --no-daemon`
5. 用户端：`npx cap doctor android`
6. 兼职端：`npx cap doctor android`
7. `frontend`：`npm run build`
8. `backend`：`.\mvnw.cmd -DskipTests compile`
9. `git diff --check`

构建过程观察：

1. Gradle 8.14.3 已从腾讯镜像下载成功。
2. Android SDK 缺失问题已解决。
3. JDK 17 source 21 不兼容问题已通过 JDK 21 解决。
4. Gradle 仍有 `flatDir` 与 Gradle 9 deprecation warning，来源于当前 Capacitor / Android 构建链路，不影响 Debug APK 输出。

## 本轮明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改 `campus-*` API 文件运行时行为。
4. 没有改 token 附着逻辑。
5. 没有改前端业务页面。
6. 没有改后端接口、鉴权或订单状态机。
7. 没有新增 admin Android 壳。
8. 没有接真实支付、真实退款或真实打款。

## 当前风险与后续重点

Step 114 已确认“能构建 APK”，但还没有确认“APK 安装后在 WebView 内能完整访问后端”。

Step 115 需要重点验证：

1. 用户端 APK 安装后是否默认进入 `/user/login`。
2. 兼职端 APK 安装后是否默认进入 `/parttime/login`。
3. Capacitor WebView 内 `/api/**` 请求是否能正确到达当前后端。
4. 如果 `/api` 相对路径在 App 内不可用，需要评估移动端专用 API base URL 配置，但不能直接改后端接口语义。
5. 两个包名下 WebView storage 是否隔离，避免 `customer_token` 和 `courier_token` 混用。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有触发 bridge 恢复推进条件，也没有删除或收紧：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 115 建议进入“Android 真机或模拟器 smoke”：

1. 安装用户端 Debug APK。
2. 安装兼职端 Debug APK。
3. 验证两个 App 的首屏入口。
4. 验证登录、工作台和结果页的最低可用链路。
5. 重点确认移动端 API base URL 是否需要产品化配置。

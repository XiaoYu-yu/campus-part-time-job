# Step 116 - Android 模拟器真实 smoke 与 API base 加固

## 本轮目标

1. 解除 Step 115 留下的 Android Emulator hypervisor 阻塞。
2. 在 `campus_api35` 模拟器上真实安装并启动用户端与兼职端 Debug APK。
3. 验证 Android WebView 内后端 API base、CORS、明文本地 HTTP、token storage 与登录链路。
4. 不改 bridge、鉴权、路由、业务状态机或前端页面语义。

## 已完成项

### 1. 模拟器与构建环境

- 已确认 Android Emulator Hypervisor Driver 可用，`emulator -accel-check` 返回可用状态。
- 已使用 AVD：`campus_api35`。
- 已确认在线设备：`emulator-5554`。
- Android Gradle 构建使用本机 JDK 21：`D:\software\jdk-21-temurin`。
- 用户端与兼职端 Android Debug APK 均完成 `assembleDebug`。

### 2. Android 本地 API base 与 WebView HTTP 策略

本轮新增两个 Android 构建专用 env：

- `frontend/.env.android-user`
- `frontend/.env.android-parttime`

二者均指向模拟器访问宿主机后端的地址：

```properties
VITE_API_BASE_URL=http://10.0.2.2:8080/api
VITE_USE_MOCK=false
```

Capacitor 壳同步增加本地 smoke 所需配置：

- `server.androidScheme = "http"`
- `server.cleartext = true`
- AndroidManifest `usesCleartextTraffic = true`

原因：

1. Android 模拟器访问宿主机不能用 `127.0.0.1`，必须使用 `10.0.2.2`。
2. Capacitor 默认 `https://localhost` 会阻止页面调用 `http://10.0.2.2:8080`，因此本地 smoke 壳切到 `http://localhost`。
3. 该配置只服务本地 / 内测模拟器 smoke；正式服务器或真机公网访问应切到 HTTPS 域名和对应 API base。

后端 dev/test CORS 同步放行：

- `http://localhost`
- `https://localhost`
- `capacitor://localhost`

### 3. smoke 脚本修正

`scripts/trial-operation/android-smoke.ps1` 已修正：

- ADB 参数传递从错误的 splatting 调用修为显式 `-CommandArgs`。
- 新增 `-LaunchWaitSeconds`，默认等待 8 秒再截图，避免首屏尚未渲染完成时过早截图。

### 4. 真实模拟器验证

已在 `emulator-5554` 上完成：

- 用户端包：`com.xiaoyu.campus.user`
- 兼职端包：`com.xiaoyu.campus.parttime`

截图归档：

- `project-logs/campus-relay/runtime/android-smoke/20260425-102758-user-app-launch.png`
- `project-logs/campus-relay/runtime/android-smoke/20260425-102807-parttime-app-launch.png`
- `project-logs/campus-relay/runtime/android-smoke/20260425-1029-parttime-workbench.png`
- `project-logs/campus-relay/runtime/android-smoke/20260425-1031-user-after-login.png`

### 5. WebView API 与登录态验证

兼职端 WebView：

- 首屏进入：`http://localhost/parttime/login`
- `POST /api/campus/courier/auth/token` 返回 200，返回兼职人员 `李四` 的 `courier_token`
- 登录后进入：`http://localhost/parttime/workbench`
- localStorage 内存在 `courier_token`
- `GET /api/campus/courier/profile` 返回 200
- `GET /api/campus/courier/review-status` 返回 200
- `GET /api/campus/courier/orders/available` 返回 200，列表数量为 2

用户端 WebView：

- 首屏进入：`http://localhost/user/login`
- `POST /api/users/login` 返回 200，返回用户 `张三` 的 `token`
- 登录后进入：`http://localhost/user`

## 明确未改

- 未改 bridge。
- 未改 `frontend/src/utils/request.js` 的 token 附着策略。
- 未改 `frontend/src/api/campus-courier.js` 运行时行为。
- 未改后端鉴权规则。
- 未改订单主状态机。
- 未改 settlement、after-sale、exception 后端业务语义。
- 未新增页面。
- 未把 admin 后台打进移动端默认入口。

## 当前剩余问题

1. 用户端 Android 登录后仍进入旧用户首页，页面上还有旧外卖语义；这不是本轮 API / 模拟器阻塞，但会影响“校园兼职项目”移动端产品观感。
2. 当前 Android API base 是模拟器本地 smoke 配置：`10.0.2.2:8080`；真机或服务器内测时需要改为局域网 IP / 公网域名 / HTTPS 地址。
3. `androidScheme=http` 与 `usesCleartextTraffic=true` 只适合本地 / 内测 smoke，正式公网试运营应收敛到 HTTPS。

## 验证结果

- `npm run cap:sync`：用户端通过。
- `npm run cap:sync`：兼职端通过。
- `mobile/user-app/android ./gradlew.bat assembleDebug`：通过。
- `mobile/parttime-app/android ./gradlew.bat assembleDebug`：通过。
- Android smoke 脚本安装、启动、截图：通过。
- WebView 内兼职端 API / token / profile / review-status / available orders：通过。
- WebView 内用户端登录：通过。
- `backend .\mvnw.cmd -DskipTests compile`：通过。
- `frontend npm run build`：通过。
- `frontend npm run build:android:user`：通过。
- `frontend npm run build:android:parttime`：通过。
- `git diff --check`：通过，仅保留 Windows 工作区既有 CRLF 替换提示，无 whitespace error。

## 当前 bridge 结论

bridge 主线仍处于 `Phase A no-op` 冻结态。本轮没有触发恢复推进条件，也没有执行任何 bridge 收口动作。

## 下一步建议

Step 117 建议优先处理“用户端移动首页仍旧外卖语义”的产品问题：

1. 不改 bridge、不改后端接口、不改 Android 壳。
2. 在现有用户端 H5 内，把登录后首页从旧外卖首页收敛为校园兼职 / 校园代送用户端入口。
3. 保留旧外卖模块可运行，但不让 Android 用户端默认展示旧外卖语义。
4. 同步准备真机 / 服务器 API base 策略，区分本地模拟器、局域网真机和公网 HTTPS。

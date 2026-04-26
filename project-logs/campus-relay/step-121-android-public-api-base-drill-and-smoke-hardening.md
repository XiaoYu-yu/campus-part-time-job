# Step 121 - Android public API base 演练与 smoke 加固

## 本轮目标

1. 基于 Step 120 的 Android API base 分层，实际演练 public API base 构建和 Android 壳同步。
2. 确认 public 模式不会被默认 `cap:sync` 覆盖回模拟器 `10.0.2.2`。
3. 加固 Android smoke，让 clean first-entry 验证不会被上一次 WebView 路由缓存误导。
4. 不改 bridge、不改 `request.js`、不改 token 附着逻辑、不改后端接口、不改订单状态机、不改旧外卖模块。

## 实际完成

### 1. 新增 public API base 只读 smoke 脚本

新增 `scripts/trial-operation/android-public-api-smoke.ps1`。

脚本能力：

1. 要求传入 `http(s)://.../api` 形式的 API base。
2. 只检查 Android 用户端代送入口依赖的只读 public API：
   - `GET /api/campus/public/pickup-points`
   - `GET /api/campus/public/delivery-rules`
3. 默认把 API base host 脱敏写入报告。
4. 不创建订单、不写 token、不改 bridge、不写服务器数据。
5. 支持 `-AllowFailures`，用于记录“公网 API 当前不可用但仍需留痕”的 blocked 结果。

本轮运行结果：

1. public API base 检查已执行。
2. 结果为 `passed=0 failed=2`。
3. 两个只读 endpoint 当前均返回 `404`。
4. 结构化报告：
   - `project-logs/campus-relay/runtime/step-121-public-api-base/public-api-smoke.json`

结论：Android public 构建能力已经具备，但当前公网服务没有暴露当前后端 campus public API 路由，完整公网 WebView API smoke 被服务器部署 / 反向代理 / API 容器状态阻塞。

### 2. 修正 Android 壳 public sync 覆盖问题

更新：

1. `mobile/user-app/package.json`
2. `mobile/parttime-app/package.json`

新增命令：

1. `build:web:emulator`
2. `build:web:lan`
3. `build:web:public`
4. `cap:sync:emulator`
5. `cap:sync:lan`
6. `cap:sync:public`

原因：

1. 默认 `cap:sync` 会执行默认 `build:web`。
2. 默认 `build:web` 仍按 Step 120 约定使用模拟器 API base。
3. 如果先生成 public build，再误执行默认 `cap:sync`，Android 壳会被覆盖回 `10.0.2.2`。

本轮改为显式使用：

1. `npm run cap:sync:public`
2. `npm run cap:sync:lan`
3. `npm run cap:sync:emulator`

从命令层降低误操作风险。

### 3. 修正 Android suffixed mode 路由识别

更新 `frontend/src/router/index.js`。

问题：

1. 旧逻辑只精确识别 `android-user` 和 `android-parttime`。
2. `android-user-public`、`android-parttime-public` 会回落到 `admin` shell。
3. clean launch 时两个 Android App 会误进入后台登录页。

修正：

1. `MODE` 以 `android-user` 开头时进入 user shell。
2. `MODE` 以 `android-parttime` 开头时进入 parttime shell。
3. admin web 默认行为不变。

修正后 clean launch 截图：

1. 用户端：
   - `project-logs/campus-relay/runtime/android-smoke/20260426-140630-user-app-launch.png`
2. 兼职端：
   - `project-logs/campus-relay/runtime/android-smoke/20260426-140641-parttime-app-launch.png`

两个截图均确认不再误进 admin 登录页。

### 4. Android smoke 增加清数据能力

更新 `scripts/trial-operation/android-smoke.ps1`。

新增参数：

1. `-ClearData`

行为：

1. 启动用户端前执行 `adb shell pm clear com.xiaoyu.campus.user`。
2. 启动兼职端前执行 `adb shell pm clear com.xiaoyu.campus.parttime`。

原因：

1. Android WebView / App data 会保留上一次路由和 localStorage。
2. 仅重新安装 APK 不一定能验证 clean first-entry。
3. 本轮 smoke 初次截图曾被旧路由缓存误导；加入 `-ClearData` 后可以稳定验证首屏入口。

### 5. 文档同步

更新：

1. `mobile/README.md`
2. `scripts/trial-operation/README.md`

补充内容：

1. public / LAN Android 壳同步必须使用对应 `cap:sync:public` 或 `cap:sync:lan`。
2. public API base smoke 的用法和边界。
3. clean first-entry smoke 建议使用 `android-smoke.ps1 -ClearData`。

## 验证结果

### 构建与静态检查

1. `frontend npm run lint`
   - 通过。
2. `frontend npm run build`
   - 通过。
3. `backend .\mvnw.cmd -DskipTests compile`
   - 通过。
4. `git diff --check`
   - 通过；仅有 Windows CRLF 提示，无空白错误。

### Android 壳同步与构建

1. `mobile/user-app npm run cap:sync:public`
   - 通过。
2. `mobile/parttime-app npm run cap:sync:public`
   - 通过。
3. `mobile/user-app/android .\gradlew.bat assembleDebug --no-daemon`
   - 使用 `JAVA_HOME=D:\software\jdk-21-temurin`。
   - 通过。
4. `mobile/parttime-app/android .\gradlew.bat assembleDebug --no-daemon`
   - 使用 `JAVA_HOME=D:\software\jdk-21-temurin`。
   - 通过。

### public 模式嵌入证据

结构化证据：

1. `project-logs/campus-relay/runtime/step-121-public-api-base/android-public-sync-evidence.json`

结论：

1. 用户端 public 壳中未发现 `10.0.2.2`。
2. 兼职端 public 壳中未发现 `10.0.2.2`。
3. public API base 已嵌入 Android 壳，但报告中仅保留脱敏地址。

### Android clean launch smoke

命令：

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator -ClearData -LaunchWaitSeconds 10`

结果：

1. 用户端 APK 安装成功。
2. 兼职端 APK 安装成功。
3. 用户端清数据后启动到“用户端登录”。
4. 兼职端清数据后启动到“兼职端入口”。
5. 不再误进入 admin 登录页。

### public API smoke

命令：

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-public-api-smoke.ps1 -ApiBase <redacted> -AllowFailures`

结果：

1. `GET /api/campus/public/pickup-points` 返回 `404`。
2. `GET /api/campus/public/delivery-rules` 返回 `404`。
3. 本轮不能声明公网 API WebView smoke 通过。

判断：

1. 这不是 Android 壳构建问题。
2. 当前更可能是公网服务器未部署当前后端、反向代理未指向当前 API、或线上容器仍是旧版本。
3. 下一轮应优先修服务器 API 路由可达性，而不是继续改 Android 前端。

### Android API base 检查

命令：

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-api-base-check.ps1`

结果：

1. 默认模拟器 env：通过。
2. 显式 emulator env：通过。
3. LAN env 缺失：warning。
4. public env 使用 HTTP：warning。
5. hard failure 数为 `0`。

说明：warning 是当前内测环境边界，不代表构建失败；公网正式内测仍建议后续切 HTTPS。

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 token 附着逻辑。
4. 未改后端接口、鉴权或订单状态机。
5. 未改旧外卖模块。
6. 未新增 Android 原生页面。
7. 未接真实支付、真实退款或真实打款。
8. 未提交真实 public API base env 文件。

## 当前结论

1. Android user / parttime public 构建和壳同步链路已加固。
2. public mode 后缀路由识别已修正，两个 App clean launch 已进入正确入口。
3. Android smoke 已支持清数据验证，避免缓存路由误导。
4. 公网 API base 当前仍未达到真实 WebView API smoke 条件，原因是 public campus API 返回 `404`。
5. bridge 继续保持 `Phase A no-op` 冻结态。

## 遗留问题

1. 公网服务器当前没有暴露当前 campus public API。
2. public Android WebView 登录、代送列表、创建订单和 mock-pay 尚未完成公网端到端验证。
3. public API 当前仍使用 HTTP，本地内测可接受；正式对外应切 HTTPS / 域名 / 反向代理。
4. LAN 真机 smoke 尚未执行。

## 下一步建议

1. Step 122 优先修公网服务器 API 可达性：
   - 确认后端容器是否为当前 main。
   - 确认 Nginx / gateway 是否把 `/api/**` 转发到当前 backend。
   - 确认 `/api/campus/public/pickup-points` 与 `/api/campus/public/delivery-rules` 返回 `200`。
2. public API smoke 通过后，再执行 Android WebView 真实接口 smoke：
   - 用户端登录。
   - 进入 `/user/campus/orders`。
   - 读取列表。
   - 创建代送单。
   - mock-pay。
3. 暂不继续扩 Android UI、bridge 或 admin 页面，先解决公网 API 服务端可达性。

# Step 151 - Android 用户端 / 兼职端稳定性复核

## 本轮目标

1. 纠正 Step 150 对“可进入少量内测”的边界表述：后端、管理端和服务器运维链路稳定，不等于 Android 双端已经稳定。
2. 重新验证 Android 用户端与兼职端的 public WebView 构建、同步、Debug APK 和真实 WebView smoke。
3. 如果发现验证脚本本身不稳定，做最小脚本修正，不改业务行为。
4. 不改 bridge、不改鉴权、不改接口、不改路由、不改后端、不新增页面。

## 当前 Android 双端结构

当前仍采用单一 `frontend/` 业务源码 + 两个 Capacitor Android 壳：

1. 用户端：`mobile/user-app`
   - 包名：`com.xiaoyu.campus.user`
   - 默认入口：`/user/login`
2. 兼职端：`mobile/parttime-app`
   - 包名：`com.xiaoyu.campus.parttime`
   - 默认入口：`/parttime/login`

## 本轮验证过程

### 1. Web 构建

已重新执行：

```powershell
npm run build:android:user:public
npm run build:android:parttime:public
```

结果：两端均通过。

### 2. Capacitor 同步

已重新执行：

```powershell
npm run cap:sync:public
```

执行位置：

1. `mobile/user-app`
2. `mobile/parttime-app`

结果：两端均通过，Web assets 已复制到 Android 壳。

### 3. Android Debug APK 构建

首次直接执行 `.\gradlew.bat assembleDebug` 时失败：

```text
错误: 无效的源发行版：21
```

原因：当前 shell 默认 `JAVA_HOME=C:\Program Files\Java\jdk-17`，但 Android Gradle / Capacitor 依赖当前需要 JDK 21。

修正方式：仅在当前命令进程临时设置 JDK 21，不改全局环境。

```powershell
$env:JAVA_HOME='D:\software\jdk-21-temurin'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat assembleDebug
```

结果：两端均通过。

生成 APK：

1. `mobile/user-app/android/app/build/outputs/apk/debug/app-debug.apk`
2. `mobile/parttime-app/android/app/build/outputs/apk/debug/app-debug.apk`

### 4. Android WebView public smoke

首次使用旧默认参数运行聚合 smoke：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\trial-operation\android-webview-public-smoke.ps1 -StartEmulator -ClearData -OutputDirectory "project-logs\campus-relay\runtime\step-151-android-dual-app-stability"
```

结果：

1. 用户端失败：等待 `customer_token` 超时。
2. 兼职端通过：`courierProfileId = 2`，`reviewStatus = APPROVED`，可接单记录数返回成功。
3. 失败报告路径：
   - `project-logs/campus-relay/runtime/step-151-android-dual-app-stability/android-public-webview-readiness-summary.json`

定位结果：

1. 用户端公网登录接口直接调用可返回 token。
2. 用户端单独以更长等待时间复跑通过。
3. 聚合 smoke 以更长等待时间复跑通过。
4. 因此失败原因是冷启动 / WebView 自动化默认等待时间偏紧，不是用户端业务链路或后端接口不可用。

### 5. 脚本最小修正

已把三个 Android WebView smoke 脚本默认等待参数调宽：

1. `scripts/trial-operation/android-webview-public-smoke.ps1`
2. `scripts/trial-operation/android-webview-user-public-smoke.ps1`
3. `scripts/trial-operation/android-webview-parttime-public-smoke.ps1`

调整内容：

```text
LaunchWaitSeconds: 8 -> 12
WaitTimeoutSeconds: 35 -> 90
```

这只影响 smoke 脚本等待策略，不改 App、接口、token、bridge 或业务逻辑。

### 6. 修正后默认参数复跑

执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\trial-operation\android-webview-public-smoke.ps1 -ClearData -OutputDirectory "project-logs\campus-relay\runtime\step-151-android-dual-app-stability\default-after-timeout-hardening"
```

结果：通过。

用户端：

1. 登录成功，WebView localStorage 写入 `customer_token`。
2. `GET /api/campus/public/pickup-points` 通过。
3. `GET /api/campus/public/delivery-rules` 通过。
4. `GET /api/campus/customer/orders` 通过。
5. `POST /api/campus/customer/orders` 通过。
6. `POST /api/campus/customer/orders/{id}/mock-pay` 通过。
7. `GET /api/campus/customer/orders/{id}` 通过。
8. 本轮创建订单：`CR202605010405291760`
9. 最终订单状态：`BUILDING_PRIORITY_PENDING`
10. 最终支付状态：`PAID`

兼职端：

1. 登录成功，WebView localStorage 写入 `courier_token`。
2. `GET /api/campus/courier/profile` 通过。
3. `GET /api/campus/courier/review-status` 通过。
4. `GET /api/campus/courier/orders/available` 通过。
5. `courierProfileId = 2`
6. `reviewStatus = APPROVED`
7. `availableOrders = 5`

证据：

1. `project-logs/campus-relay/runtime/step-151-android-dual-app-stability/default-after-timeout-hardening/android-public-webview-readiness-summary.json`
2. `project-logs/campus-relay/runtime/step-151-android-dual-app-stability/default-after-timeout-hardening/user-public-webview-smoke.json`
3. `project-logs/campus-relay/runtime/step-151-android-dual-app-stability/default-after-timeout-hardening/user-campus-orders-public-webview.png`
4. `project-logs/campus-relay/runtime/step-151-android-dual-app-stability/default-after-timeout-hardening/parttime-public-webview-smoke.json`
5. `project-logs/campus-relay/runtime/step-151-android-dual-app-stability/default-after-timeout-hardening/parttime-workbench-public-webview.png`

## 当前 Android 稳定性判断

当前可以确认：

1. Android 用户端 public Web 构建通过。
2. Android 兼职端 public Web 构建通过。
3. 两端 Capacitor 同步通过。
4. 两端 Debug APK 在 JDK 21 下构建通过。
5. 两端模拟器 WebView public smoke 在默认参数修正后通过。
6. 用户端覆盖登录、读取、创建订单、模拟支付、详情回读。
7. 兼职端覆盖登录、profile、review-status、可接单列表。

当前不能宣称：

1. 真机稳定。
2. release 包稳定。
3. 外部长期 Android 内测稳定。
4. HTTPS / 域名 / 证书完成。
5. Android cleartext 已收口。
6. 兼职端完整动作链路已在 Android WebView 内覆盖到接单、取餐、送达和异常上报。

因此本轮最终结论是：

```text
Android 双端已达到 owner-controlled 模拟器内测 smoke 可用。
仍未达到外部用户真机长期内测 / 正式试运营稳定标准。
```

## 明确没有改动

本轮没有修改：

1. Vue 页面。
2. Java 业务代码。
3. 后端接口。
4. 鉴权。
5. `request.js`。
6. token 附着逻辑。
7. bridge 接口。
8. 路由。
9. 数据库。
10. 旧兼容模块。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

如果继续推进 Android 内测稳定性，建议 Step 152 二选一：

1. **真机 smoke 准备轮**：整理 APK 安装、调试、抓日志和真机 WebView smoke 的步骤，等真机方便时执行。
2. **Android release 包准备轮**：整理签名、版本号、release 构建、安装包命名和分发边界，但仍不接 HTTPS 前不建议给外部用户长期使用。

如果暂不推进 Android，当前可以继续 Web / 管理端 owner-controlled 内测，但文案必须写清 Android 仅为模拟器 smoke 通过。

## Step 152 跟进结果

Step 152 已选择继续 Android 线，并完成 QA Debug APK 准备：

1. 新增统一打包脚本 `scripts/trial-operation/build-android-qa-apks.ps1`。
2. 新增 Android QA APK 交付说明 `docs/mobile/android-qa-apk-handoff.md`。
3. 双端 public Web 构建、Capacitor sync、JDK 21 Debug APK 构建已通过。
4. 已生成用户端 / 兼职端本地 QA APK 和 SHA256 manifest，输出目录为 git 忽略目录。
5. 已在 `campus_api35` 模拟器完成双端安装、清数据、启动和首屏截图 smoke。

Step 151 的“未达到真机长期稳定 / release 包稳定 / HTTPS 收口”判断仍然有效。

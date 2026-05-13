# Step 159 - 公测 P0 收口复核与 Android 安全区修复

## 本轮目标

1. 基于 Step 158 的安卓双端视觉重构结果，继续推进“能否进入小范围公测 / 内测”的 P0 收口复核。
2. 优先处理移动端真机展示中最可能影响可用性的安全区、顶部遮挡、构建 warning 和 APK 可安装性问题。
3. 不改业务语义、不改 API、不改 bridge、不改鉴权、不改 token 附着逻辑、不改路由主链路。

## 本轮实际处理

### 1. Android WebView 安全区修复

- `frontend/index.html`
  - viewport 增加 `viewport-fit=cover`，允许 WebView 正确识别安全区。
- `frontend/src/layout/UserLayout.vue`
  - 用户端移动壳顶部栏增加 `env(safe-area-inset-top)` 支持。
  - 顶部栏最小高度改为 `56px + safe-area`，避免刘海屏 / 状态栏遮挡。
- `frontend/src/layout/ParttimeLayout.vue`
  - 兼职端移动壳顶部栏同步增加安全区支持。
- `frontend/src/views/user/Login.vue`
  - 登录页增加顶部和底部 safe-area padding。
- `frontend/src/views/courier/Login.vue`
  - 兼职端登录页增加顶部和底部 safe-area padding。

### 2. 前端构建 warning 修复

- `frontend/src/views/CampusCourierOpsView.vue`
  - 修复嵌套 `:deep(...)` 导致的 `lightningcss` minify warning。
  - 将表格相关 `:deep` 选择器拉平，保持运行时样式语义不变。

### 3. 用户端确认送达承接修复

- `frontend/src/api/campus-customer.js`
  - 补回 `confirmCampusCustomerOrder(orderId)`，复用既有 `POST /api/campus/customer/orders/{id}/confirm` 后端接口。
- `frontend/src/views/user/CampusOrderResult.vue`
  - `AWAITING_CONFIRMATION` 状态下展示最小确认区块和 `确认已收到` 按钮。
  - 点击确认后调用既有 customer confirm 接口，并自动刷新详情回读最终状态。
  - 结果页文案从纯只读回看修正为“等待确认时可完成送达确认”，避免页面能力和真实链路不一致。
  - 本次只补用户端确认承接，不新增后端接口，不改订单状态机，不改 bridge / auth / token 逻辑。

## 本轮明确没做

- 没有改后端 Java 代码。
- 没有改数据库。
- 没有改 `request.js`。
- 没有改 `api/*` 的接口行为。
- 没有改 router 路由语义。
- 没有改 token 附着逻辑。
- 没有改 bridge。
- 没有删除旧外卖兼容模块。
- 没有提交真实密钥、公网 IP、服务器密码、GitHub token 或腾讯地图 key。
- 没有新增后端接口；用户端确认送达只复用既有 customer confirm 接口。

## 验证结果

### 前端与 Android 构建

- `cd frontend && npm run build`：通过。
- `cd frontend && npm run build:android:user:public`：通过。
- `cd frontend && npm run build:android:parttime:public`：通过。
- `cd mobile/user-app && npm run cap:sync:public`：通过。
- `cd mobile/parttime-app && npm run cap:sync:public`：通过。
- `cd mobile/user-app/android && .\gradlew.bat :app:assembleDebug`：通过。
- `cd mobile/parttime-app/android && .\gradlew.bat :app:assembleDebug`：通过。
- `git diff --check`：通过，仅存在 Git 换行符 CRLF 提示，无空白错误。

### 用户端确认送达修复后的补充构建

- `cd frontend && npm run build`：通过。
- `cd frontend && npm run build:android:user:public`：通过。
- `cd frontend && npm run build:android:parttime:public`：通过。
- `cd mobile/user-app && npm run cap:sync:public`：通过。
- `cd mobile/user-app/android && .\gradlew.bat :app:assembleDebug`：通过。
- `adb install -r mobile/user-app/android/app/build/outputs/apk/debug/app-debug.apk`：命令超时，但 `pm path` 与 `dumpsys package` 确认用户端 APK 已在 2026-05-12 17:32:47 更新安装。
- `git diff --check`：通过，仅 CRLF 换行符提示。

### 真机验证状态

- 2026-05-12 手机重新连接后已完成本轮 APK 真机复测。
- ADB 设备：`10AE221PGA003Y5`
- 设备分辨率：`1260x2800`
- 设备密度：`560`
- `adb install -r` 用户端 APK：成功。
- `adb install -r` 兼职端 APK：成功。
- 手机到内测公网服务器 ping：成功，2/2 received，0% packet loss。真实地址不写入仓库。
- 用户端启动截图：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-step159.png`
- 兼职端启动截图：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-step159.png`
- 兼职端登录表单截图：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-login-form-step159.png`
- 兼职端登录后工作台截图：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-after-login-step159.png`
- 用户端登录后首页截图：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-after-login-step159-2.png`
- 兼职端登录 logcat：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-login-logcat.txt`
- 用户端登录 logcat：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-login-logcat-2.txt`

### 真机完整业务链路补充验证

- 验证设备：`10AE221PGA003Y5`
- 验证订单号：`CR202605010405291760`
- 订单金额：`¥4.00`
- 取餐点：`主大门门卫室西侧临时取餐区`
- 送达信息：`竹园 / 竹园2栋楼下`
- 兼职端已真实执行：
  - 可接任务列表回读订单。
  - 接单成功，订单进入 `ACCEPTED`。
  - 填写取餐凭证 `/api/files/android-smoke-pickup.jpg`。
  - 确认取餐成功，订单进入 `PICKED_UP`。
  - 开始配送成功，订单进入 `DELIVERING`。
  - 确认送达成功，订单进入 `AWAITING_CONFIRMATION`。
- 用户端已真实执行：
  - 结果页查询订单 `CR202605010405291760`。
  - 修复前可回读 `AWAITING_CONFIRMATION`，但页面缺少确认送达按钮。
  - 修复后重新安装用户端 APK，结果页展示 `确认已收到`。
  - 点击 `确认已收到` 后，订单回读为 `已完成 / COMPLETED`。
  - 完成时间回读：`2026-05-12 09:38:42`。
- 关键留痕：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-task-list-step159-2.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-accept-after-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-pickup-after-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-delivering-after-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-parttime-deliver-after-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/window-user-awaiting-with-confirm-ui.xml`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-visible-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/window-user-confirm-visible.xml`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-after-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/window-user-confirm-after.xml`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-logcat.txt`

### 真机验证结论

- 用户端登录成功，进入首页。
- 兼职端登录成功，进入兼职工作台。
- 兼职端工作台显示：
  - `兼职 token 已就绪`
  - `资料状态 APPROVED`
  - `启用状态 可工作`
  - `Token 有效`
  - `可接任务 5`
- Step 158 曾出现的兼职端 `网络连接失败，请检查网络` toast 本轮没有复现。
- 本轮 logcat 中未发现项目 App 自身的 HTTP / cleartext / axios 明确错误；日志中的 Google / Play / AppLinks 连接失败属于设备系统服务访问 Google 域名失败，不归因于本项目接口。
- 安全区修复有效：用户端和兼职端首屏没有被状态栏或底部导航明显遮挡。
- 真机公网主链路已完成闭环：兼职端接单 -> 取餐 -> 配送 -> 送达 -> 用户端确认 -> `COMPLETED` 结果回读。

## 当前公测 P0 判断

当前项目仍不建议直接进入公开公测，但已具备“小范围 owner-controlled 内测”的基础条件：

1. Web 与 Android 双端构建链路已通过。
2. 双端 APK 已重新生成。
3. 移动端安全区和构建 warning 已处理。
4. 用户端和兼职端均已在真机上完成安装、启动和登录 smoke。
5. 兼职端工作台真实回读成功，未复现网络失败 toast。
6. 公网真机主链路已真实跑通到 `COMPLETED` 回读。
7. 当前工作树还有多轮未提交改动与未跟踪文件，需要先整理提交边界。
8. 若要扩大到公开公测，仍需补充：多用户账号矩阵、重复下单 / 异常 / 断网恢复用例、Release 签名包、隐私合规说明、基础监控与回滚策略。

## 下一轮建议

1. 先整理工作树并确认提交边界：
   - `.trae/` 是否保留。
   - 根目录临时截图是否归档或忽略。
   - runtime 截图 / logcat 是否作为验证留痕提交。
2. 进入 owner-controlled 小范围内测前，补一份“内测账号 / APK 安装 / 已知限制 / 反馈模板”。
3. 继续补最小真机回归矩阵：
   - 用户端重复发布订单。
   - 兼职端无任务 / 已接任务 / 已完成任务状态。
   - 用户端异常订单结果回看。
   - Android 后台切回、弱网、重启后 token 恢复。
4. 当前可以进入 owner-controlled 内测继续验证，但不建议直接进入公开公测。

# Step 120 - Android / 内测 API base 分层与用户端代送入口壳级验证

## 本轮目标

1. 固化 Android / 内测 API base 分层，避免本地模拟器、局域网真机、公网服务器混用同一个 `10.0.2.2` 配置。
2. 保留现有 Android 模拟器默认构建行为，不破坏 Step 116 到 Step 119 已验证链路。
3. 对 Step 119 新增的用户端 `/user/campus/orders` 做 Android 壳级 smoke，确认打包进用户端 APK 后可从底部导航进入。
4. 不改 bridge、不改 `request.js`、不改后端接口、鉴权、路由、订单状态机或旧外卖模块。

## 实际完成

### 1. Android API base 构建模式分层

更新 `frontend/package.json`，保留原有默认命令：

1. `npm run build:android:user`
2. `npm run build:android:parttime`

新增显式分层命令：

1. `npm run build:android:user:emulator`
2. `npm run build:android:parttime:emulator`
3. `npm run build:android:user:lan`
4. `npm run build:android:parttime:lan`
5. `npm run build:android:user:public`
6. `npm run build:android:parttime:public`

更新 `frontend/vite.config.js`：

1. `android-user*` mode 统一输出到 `dist-android-user`。
2. `android-parttime*` mode 统一输出到 `dist-android-parttime`。
3. Android 构建 mode 必须提供显式 `VITE_API_BASE_URL`。
4. 如果 LAN/Public 本地 env 未创建，构建会失败，不会静默回退到 `/api`。

### 2. Android env 文件分层

继续保留：

1. `frontend/.env.android-user`
2. `frontend/.env.android-parttime`

新增显式模拟器 env：

1. `frontend/.env.android-user-emulator`
2. `frontend/.env.android-parttime-emulator`

新增局域网真机 example：

1. `frontend/.env.android-user-lan.example`
2. `frontend/.env.android-parttime-lan.example`

新增公网 / 服务器内测 example：

1. `frontend/.env.android-user-public.example`
2. `frontend/.env.android-parttime-public.example`

真实局域网 IP、公网 IP、域名或 HTTPS 地址仍不提交到仓库。实际使用时复制 example 到被 `.gitignore` 忽略的本地 env 文件：

1. `frontend/.env.android-user-lan`
2. `frontend/.env.android-parttime-lan`
3. `frontend/.env.android-user-public`
4. `frontend/.env.android-parttime-public`

### 3. API base 检查脚本

新增 `scripts/trial-operation/android-api-base-check.ps1`。

脚本能力：

1. 校验默认用户端 / 兼职端 Android env 是否仍为模拟器 `10.0.2.2`。
2. 校验显式 emulator env 是否仍为模拟器配置。
3. 对 LAN/Public 本地 env 缺失输出 warning。
4. 使用 `-Strict` 时，把 LAN/Public 本地 env 缺失提升为 hard failure。

更新 `scripts/trial-operation/preflight.ps1`：

1. 新增 `-RunAndroidApiBaseCheck`。
2. 对 API base 检查脚本的 warning exit code `2` 按 warning 处理，不使 preflight 硬失败。

更新 `mobile/README.md` 与 `scripts/trial-operation/README.md`，写清：

1. 模拟器使用 `10.0.2.2`。
2. 局域网真机使用开发机 LAN IP。
3. 公网 / 服务器内测优先使用 HTTPS API base。
4. LAN/Public 本地 env 需要从 example 复制后填写。

## 验证结果

### API base 分层验证

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-api-base-check.ps1`
   - 默认模拟器 env：通过。
   - 显式 emulator env：通过。
   - LAN/Public 本地 env：按预期 warning，原因是当前没有提交真实本地地址。
   - 脚本退出码：`2`，表示无 hard failure、存在 warning。
2. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunAndroidApiBaseCheck`
   - 通过。
   - Android API base warning 被 preflight 记录为 warning，整体退出码 `0`。
3. `npm run build:android:user:emulator`
   - 通过。
4. `npm run build:android:parttime:emulator`
   - 通过。
5. `npm run build:android:user:lan`
   - 在未创建 `frontend/.env.android-user-lan` 时按预期失败。
   - 失败原因是缺少显式 `VITE_API_BASE_URL`，验证没有静默回退到 `/api`。

### Android 壳构建与 smoke

1. `mobile/user-app npm run cap:sync`
   - 通过。
2. `mobile/parttime-app npm run cap:sync`
   - 通过。
3. `mobile/user-app/android .\gradlew.bat assembleDebug --no-daemon`
   - 使用 `JAVA_HOME=D:\software\jdk-21-temurin`。
   - 通过。
4. `mobile/parttime-app/android .\gradlew.bat assembleDebug --no-daemon`
   - 使用 `JAVA_HOME=D:\software\jdk-21-temurin`。
   - 通过。
5. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator`
   - `campus_api35` 已启动为 `emulator-5554`。
   - 用户端 APK 安装成功。
   - 兼职端 APK 安装成功。
   - 两个 APK 均可启动并截图。
6. 用户端 `/user/campus/orders` 壳级 smoke：
   - 在 Android 用户端中从底部导航点击“代送”。
   - 页面成功进入 Step 119 新增的校园代送页。
   - 截图留痕：`project-logs/campus-relay/runtime/android-smoke/20260426-132330-user-campus-orders.png`。

结构化验证记录：

1. `project-logs/campus-relay/runtime/step-120-android-api-base/user-campus-orders-android-smoke.json`

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 token 附着逻辑。
4. 未改后端接口、鉴权或订单状态机。
5. 未改旧外卖模块。
6. 未改 Android 原生壳结构。
7. 未新增原生 Android 页面。
8. 未接真实支付、真实退款或真实打款。

## 当前结论

1. Android API base 已从“只有模拟器默认配置”升级为“模拟器 / 局域网真机 / 公网内测三类构建模式”。
2. 默认 `build:android:user` 与 `build:android:parttime` 仍保持模拟器安全配置，不破坏既有本地 smoke。
3. LAN/Public 构建必须由本地 ignored env 显式提供 API base，否则构建失败，避免错误地使用 `/api` 或 `10.0.2.2`。
4. Step 119 新增的用户端“代送”入口已确认能进入 Android 用户端壳。
5. bridge 继续保持 `Phase A no-op` 冻结态。

## 遗留问题

1. 本轮 Android 壳级 smoke 确认了页面能进入，但未在 Android WebView 内完整执行“创建代送单 -> mock-pay”的表单链路。
2. LAN 真机和公网服务器内测 env 仍需 owner 在真实网络环境中复制 example 后填写。
3. 公网内测仍建议优先走 HTTPS / 反向代理，不建议长期使用 HTTP 公网 IP。

## 下一步建议

1. Step 121 若继续 Android / 内测线，优先做真实 LAN 或公网 API base 的一次配置演练：
   - 复制 `.env.android-user-lan.example` 或 `.env.android-user-public.example`。
   - 填入真实地址。
   - 构建 APK。
   - 真机或模拟器完成用户端登录与 `/user/campus/orders` 真实接口 smoke。
2. 若转回产品功能线，可评估用户端订单详情 / 取消 / confirm 最小入口，但必须继续不改订单状态机。
3. 仍不建议重开 bridge、补第五个 admin 页或接真实支付。

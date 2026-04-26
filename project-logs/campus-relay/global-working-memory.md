# 全局工作记忆

## 用途

这份文件不是完整历史日志，而是给后续继续开发时的“快速恢复上下文入口”。

当上下文压缩、会话切换或阶段跨度过大时，优先先读本文件，再读：

1. `project-logs/campus-relay/pending-items.md`
2. `project-logs/campus-relay/summary.md`
3. 当前最新 step 日志

## 维护规则

1. 只记录当前阶段真正还在生效的状态、冻结线和下一主线。
2. 不重复抄整本 `summary.md`。
3. 不写真实密码、密钥、token、数据库密码或服务器凭据。
4. 当前主线变化、冻结状态变化、部署状态发生变化时，再更新本文件。

## 当前项目状态

当前项目已经达到：

1. 本地可完整演示
2. 本地 / H2 可完整联调
3. GitHub 最小 CI 可运行
4. 单机服务器可启动、可访问、可做内测型试运营 smoke

当前不是最终产品级上线版，而是：

1. 最小闭环
2. 可演示
3. 可讲解
4. 可交接
5. 可做单机服务器内测型试运营

## 当前主链路结论

### 1. bridge 主线

当前状态：

1. `Phase A no-op` 冻结态
2. 完全保留，不删除

保持不动项：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `request.js` 现有 token 附着逻辑
4. `customer/courier-onboarding/*` 现有前置入口
5. `CourierWorkbench.vue` 现有优先 `courier_token` 策略

默认不要做：

1. 不继续找 bridge 收口候选
2. 不改鉴权
3. 不删接口

### 2. 展示 polish 主线

当前状态：

1. 冻结 / 维护态

已经完成第一轮 polish 的关键页：

1. `frontend/src/views/user/CourierOnboarding.vue`
2. `frontend/src/views/courier/CourierWorkbench.vue`
3. `frontend/src/views/user/CampusOrderResult.vue`
4. `frontend/src/views/CampusSettlementOpsView.vue`
5. `frontend/src/views/CampusAfterSaleExecutionList.vue`

默认不要做：

1. 不继续机械 polish 页面
2. 不补第五个 admin 页

### 2.1 admin 文本显示修复

当前状态：

1. Step 101 已在 H2/test SQL 初始化中显式声明 `spring.sql.init.encoding=UTF-8`，从源头修复本地 seed 中文乱码。
2. Step 101 已新增前端文本规范化工具。
3. axios 成功响应和 admin 用户 store 会修复 UTF-8 中文被按 Latin-1 / Windows-1252 误解码后的 mojibake。
4. 该修复不改 bridge、token 附着、接口路径、数据库表结构或业务语义。
5. Step 103 已进一步修复旧 session / localStorage / in-memory 场景：`admin_user_info` 读取时会归一化并写回，`currentUserInfo` getter、MainLayout、Dashboard 和 Employee 高曝光字段都有显示兜底。
6. Step 104 已将 admin 深色玻璃方向回调为浅色校园玻璃方向。
7. Step 104 已修正 Element Plus `light-*` 主题变量映射错误，避免浅色主题下 tag / switch / select 出现反常深色表现。
8. Step 104 已修复 `/campus/courier-ops` 窄屏表格裁切，左侧配送员列表在小窗口下允许横向滚动，不再截断“审核状态”等列。

复核重点：

1. admin 仪表盘欢迎语。
2. 顶部用户姓名。
3. 员工管理页姓名、职位、部门。
4. `/campus/courier-ops` 左侧配送员列表在窄屏下是否完整显示。

### 3. 媒体与交付线

当前状态：

1. 已完成交付整理
2. 已完成演示脚本
3. 已完成截图 / 录屏清单
4. 已完成真实媒体采集
5. 当前媒体线已收住

默认不要做：

1. 不继续机械补截图
2. 不继续机械补录屏

## 当前已稳定的核心能力

### customer / courier / admin 最小闭环

已稳定覆盖：

1. customer onboarding
2. admin 审核
3. customer 申请 courier token
4. courier workbench
5. 接单
6. 取餐
7. deliver
8. 异常上报
9. customer confirm
10. completed 回读
11. customer 结果页
12. admin settlement 只读页
13. admin after-sale 只读页
14. admin courier ops 页
15. admin exception 历史 / resolve 最小闭环

### 非 bridge 后端线

当前已完成最小闭环：

1. 异常历史与最小 resolve
2. 售后执行历史
3. settlement 批次操作审计
4. settlement 对账差异记录

### 试运营与部署线

当前已完成：

1. `.env.example` / 密钥边界
2. 部署前 preflight 文档
3. 部署后 smoke checklist
4. 最小 GitHub Actions CI
5. Compose 单机部署包
6. 单机服务器首轮真实部署与 smoke

## 当前最新部署结论

当前已知稳定结论：

1. 单机服务器 Compose 栈已真实拉起
2. `mysql / backend / frontend` 三容器已验证可运行
3. admin / customer / courier 三类最小 smoke 已通过
4. 单机服务器最小备份入口已真实跑通一轮
5. 服务器已生成 MySQL dump、uploads 归档、`.env` 备份和 manifest
6. 单机服务器最小 restore drill 已真实跑通一轮
7. dump 已恢复到临时 MySQL 容器并验证关键样本订单可回读
8. 单机服务器运维 runbook 已建立，启动、停机、日志、更新、备份、restore drill、smoke 与正式入口 go/no-go 已集中到一处
9. admin 文本乱码已从 H2/test seed UTF-8 初始化根因修复，并在前端保留历史 mojibake 兜底规范化
10. admin 主框架、仪表盘、登录页和运营人员页已完成一轮校园兼职视觉刷新，旧外卖模块仍保留为兼容模块
11. Step 104 已将登录页、admin 外壳、dashboard 和 employee 高曝光区域统一回浅色玻璃风格
12. Step 105 已将 `Employee.vue` 与 `Statistics.vue` 接回 `MainLayout`，修复后台公共导航丢失问题，并把数据看板重整到当前浅色校园后台视觉层级，同时清掉统计页真实 smoke 中暴露的 ECharts 重复初始化 warning
13. Step 108 已新增 `/parttime/login` 作为兼职端独立登录入口，并把 `/courier/workbench` 调整为 `courier_token` 受保护路由；当前产品边界已从“用户端借道兼职工作台”前进到“用户端负责报名，兼职端负责日常登录”
14. Step 109 已新增 `ParttimeLayout`、正式 `/parttime/workbench`、兼容 `/courier/workbench` alias 和 `/parttime/profile`；当前兼职端 H5 最小页面群为“登录 -> 工作台 -> 我的资料”，仍未新增 Android 工程
15. Step 110 已完成双 Android 壳路线评估，当前推荐路线为“单前端源码 + 双 Capacitor Android 壳”；admin 保持 Web-only，旧 `uni-app/` 仅保留为历史占位，不再作为当前实施主线
16. Step 111 已完成双 Capacitor scaffold go / no-go，结论为先补前端 Android 构建目标层，再创建 Capacitor 壳
17. Step 112 已完成前端 Android 构建目标层，`npm run build` 保持 admin Web 默认入口，`build:android:user` 与 `build:android:parttime` 分别输出用户端和兼职端移动构建产物
18. Step 113 已完成双 Capacitor Android 壳 scaffold，`mobile/user-app` 与 `mobile/parttime-app` 均已生成 Android 工程并通过 `cap:sync`
19. Step 114 已完成 Android 本机构建验证：
   - Gradle wrapper 分发包切到腾讯 Gradle 镜像
   - Android Gradle 依赖优先走阿里云 Maven 镜像
   - 本机安装 JDK 21 与 Android SDK 后，用户端和兼职端 Debug APK 均构建成功
   - APK 输出分别在 `mobile/user-app/android/app/build/outputs/apk/debug/app-debug.apk` 与 `mobile/parttime-app/android/app/build/outputs/apk/debug/app-debug.apk`
20. Step 115 已完成 Android smoke 入口与模拟器阻塞确认：
   - 新增 `scripts/trial-operation/android-smoke.ps1`
   - 已安装 Android Emulator 与 `system-images;android-35;google_apis;x86_64`
   - 已创建 AVD：`campus_api35`
   - 当时阻塞是 Android Emulator hypervisor driver 未安装，需要管理员权限安装
21. Step 116 已完成 Android 模拟器真实 smoke 与 API base 加固：
   - Android Emulator Hypervisor Driver 已可用，`campus_api35` 已以 `emulator-5554` 在线
   - 用户端与兼职端 Debug APK 已真实安装、启动和截图
   - Android 构建专用 API base 已固定为模拟器访问宿主机的 `http://10.0.2.2:8080/api`
   - 两个 Capacitor 壳已补齐本地 cleartext HTTP smoke 配置
   - 兼职端 WebView 已真实验证 token 登录、profile、review-status 和 available orders
   - 用户端 WebView 已真实验证用户登录
22. Step 117 已完成用户端移动首页校园兼职化：
   - 用户端登录后默认 `/user` 不再展示旧外卖商品推荐首页
   - `UserLayout.vue` 已切到校园用户端移动 shell 与底部校园入口
   - `Home.vue` 已提供校园结果回看、兼职入驻摘要、token 资格提示和旧外卖兼容入口
   - 旧外卖 `category/cart/orders/profile` 页面仍保留为兼容入口，未删除旧模块
23. Step 118 已完成用户端真实移动视口 smoke：
   - Playwright CLI 已在 390x844 视口真实登录 `13900139000 / 123456`
   - 已采集用户登录页、用户首页、结果回看页和入驻页截图
   - 已修正用户登录页遗留“点餐”文案，统一为校园兼职用户端文案与浅色校园视觉
24. Step 119 已完成用户端校园代送下单 / 我的代送单最小入口：
   - 新增 `/user/campus/orders`，用户端可在移动 shell 内创建校园代送单、查看我的代送单并执行模拟支付
   - 已封装 customer campus 下单、列表、mock-pay 与 public pickup/rules 读取 API
   - 真实 390x844 移动视口 smoke 已创建并模拟支付订单 `CR202604251658356537`
   - 未改 bridge、`request.js`、后端状态机、旧外卖模块或 Android 原生壳结构
25. Step 120 已完成 Android / 内测 API base 分层与用户端代送入口壳级验证：
   - Android 构建已分为 emulator / lan / public 三类 mode，默认 `build:android:user` 与 `build:android:parttime` 仍保留模拟器 `10.0.2.2`
   - 新增 `android-api-base-check.ps1`，可检查默认模拟器配置、LAN 真机本地 env 和公网内测 env
   - LAN / Public 构建必须由本地 ignored env 显式提供 `VITE_API_BASE_URL`，否则构建失败，不会静默回退到 `/api`
   - 已在 `campus_api35` 模拟器中安装启动两个 APK，并确认用户端可进入 `/user/campus/orders`
   - 未改 bridge、`request.js`、token 附着、后端接口、订单状态机、Android 原生壳结构或旧外卖模块
26. Step 123 已完成 Android public WebView 真实接口 smoke：
   - 用户端 public WebView 已真实登录、读取 public 依赖、读取我的代送单、创建校园代送单并 mock-pay
   - 本轮真实创建并支付订单 `CR202604261108119903`，最终回读 `paymentStatus = PAID`
   - 兼职端 public WebView 已真实登录、读取 profile、review-status 和 available orders
   - 证据文件位于 `project-logs/campus-relay/runtime/step-123-android-public-webview/`，真实公网 API base 继续脱敏
27. Step 124 已完成 Android public WebView readiness 复核与试运营入口固化：
   - 新增 `scripts/trial-operation/android-webview-public-smoke.ps1` 聚合入口
   - 已复跑用户端和兼职端 public WebView smoke，聚合结果 `passed = true`
   - 本轮用户端创建并 mock-pay 订单 `CR202604261141588261`
   - 证据文件位于 `project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/`
   - 当前 HTTP public API 仍只适合 owner-controlled smoke；外部用户内测前优先做 HTTPS / 域名 / cleartext 收口

当前已确认的部署层修正：

1. MySQL 8.4 不再使用旧的 `mysql_native_password` 启动参数
2. `backend/db/init.sql` 中 `campus_relay_order` 种子已与当前字段保持一致

注意：

1. 服务器地址、密码、地图 key、JWT secret、数据库密码都不要写进仓库
2. 如需继续部署操作，只在本地终端或服务器环境变量中使用这些敏感信息

## 当前下一主线

默认下一主线：

1. Step 110 已确认 Android 方向优先走双 Capacitor 壳，不再重复做 WebView / 原生 / PWA 总评估
2. Step 112 已确认两个移动端构建产物入口正确：
   - `dist-android-user` 默认进入 `/user/login`
   - `dist-android-parttime` 默认进入 `/parttime/login`
3. Step 113 已新增双 Capacitor Android 壳 scaffold：
   - 用户端壳指向 `frontend/dist-android-user`
   - 兼职端壳指向 `frontend/dist-android-parttime`
4. Step 114 已确认两个 Android 壳可以本机构建 Debug APK
5. Step 115 曾确认本机可准备模拟器基础但缺 hypervisor driver。
6. Step 116 已解除模拟器阻塞并完成真实 Android smoke：
   - App 首屏分别进入 `/user/login` 与 `/parttime/login`
   - Android WebView 内已通过 `http://10.0.2.2:8080/api` 访问本机 backend test profile
   - 用户端和兼职端按不同包名分别维护 storage / token
7. 当前 Android 线仍不要复制第二套前端工程，也不要把 admin 打进移动壳作为默认入口
8. Step 117 已解决用户端 Android 登录后默认首页旧外卖语义问题，`/user` 现在作为校园兼职 / 校园代送用户端首页
9. Step 118 已完成用户端移动视口真实 smoke 并修正登录页旧点餐文案
10. Step 119 已补齐用户端校园代送下单 / 我的代送单最小入口
11. Step 120 已固化 Android / 内测 API base 分层，并完成用户端“代送”入口 Android 壳级 smoke
12. Step 121 已完成 Android public API base 演练与 smoke 加固：
   - 新增 public API base 只读 smoke 脚本，报告只保存脱敏 API base。
   - Android user / parttime 壳新增 `cap:sync:public / lan / emulator`，避免 public build 被默认 `cap:sync` 覆盖回模拟器 `10.0.2.2`。
   - router 已按 `android-user* / android-parttime*` 前缀识别 Android shell，修复 public mode clean launch 误进 admin 的问题。
   - `android-smoke.ps1 -ClearData` 已用于清空 app data 后验证用户端进入用户登录、兼职端进入兼职入口。
13. Step 122 已完成公网 API base 切换与 Android public APK 复核：
   - owner 开机后公网 IP 变化，本地 ignored public env 已切到新 API base。
   - public API base 只读 smoke 已通过，`pickup-points` 与 `delivery-rules` 均返回正常。
   - user / parttime public 壳已重新同步、构建并完成 clean launch smoke。
   - 真实公网 IP 不写入仓库，tracked evidence 只保留脱敏地址。
14. Step 123 已完成 Android public WebView 真实接口 smoke：
   - 用户端登录、代送列表、创建订单和 mock-pay 已通过。
   - 兼职端登录、workbench 依赖读取和可接单列表已通过。
15. Step 124 已完成 Android public WebView readiness 复核：
   - 新增一键聚合 smoke 入口 `android-webview-public-smoke.ps1`。
   - 用户端和兼职端 public WebView smoke 已可重复执行并生成聚合报告。
   - 当前不纳入 CI，因为依赖本机模拟器、APK 与 WebView DevTools 环境。

优先级建议：

1. 如果继续 Android / 内测线，优先做 HTTPS / 域名 / 正式反向代理方案，收口 Android HTTP cleartext 风险。
2. owner-controlled smoke 可继续使用 `android-webview-public-smoke.ps1 -StartEmulator -ClearData`。
3. 如果准备邀请外部用户长期访问，先完成域名、HTTPS 证书、后端 CORS 和 Android cleartext 策略收口。
4. 如果 WebView 内请求失败，优先排查公网 CORS、Android cleartext HTTP、后端 token、反向代理路径，而不是改业务语义。
5. 如果转回用户端产品线，再评估订单详情 / 取消 / confirm 最小入口，仍不要改订单状态机。
6. 如果服务器发生异常，优先按 runbook 做备份、restore drill、smoke 和回滚判断。

当前默认不进入：

1. bridge 收口执行
2. 页面继续 polish
3. 第五个 admin 页
4. 真实支付 / 真实退款 / 真实打款
5. 大范围产品级功能扩张

## 恢复工作时的最小入口

如果后续会话需要快速恢复，建议按这个顺序：

1. 看 `project-logs/campus-relay/global-working-memory.md`
2. 看 `project-logs/campus-relay/pending-items.md`
3. 看最新 step 日志
4. 执行：
   - `git status -sb`
   - `git log --oneline -5`
5. 若继续服务器运维线，再检查：
   - 服务器 compose 状态
   - 最近一次 smoke 是否还有效

## 当前结论

当前项目不是“还在早期拼功能”，而是已经进入：

1. 主链路稳定
2. bridge 冻结
3. 展示线冻结
4. 媒体线收住
5. 试运营部署开始真实落地

因此后续优先级应以：

1. 运维加固
2. 回滚与备份
3. 试运营稳定性

为主，而不是回到“继续补页面”。

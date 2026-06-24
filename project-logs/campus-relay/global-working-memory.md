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

## 2026-05-07 补充：Step 153-157 同步与服务器部署状态

1. Step 153-156 已由其他 AI / 本地协作轮次完成移动端原型视觉和用户端订单主链路对齐：
   - 用户端 / 兼职端移动壳与核心页面已按原型做第一轮和第二轮视觉调整。
   - 用户端订单列表已从下拉筛选改为横滑 tab。
   - 用户端订单结果页已增加状态时间轴和 `AWAITING_CONFIRMATION` 确认收货入口。
   - `campus-customer.js` 已新增 `confirmCampusCustomerOrder`，复用既有 customer confirm 后端接口。
2. Step 157 已完成当前本地工作树服务器部署：
   - 服务器单机内测 compose 中 `mysql / backend / frontend` 均已启动。
   - 公网入口只开放 frontend `80`。
   - backend `8080` 与 MySQL `3306` 继续仅绑定服务器本机。
   - `GET /api/campus/public/health` 返回 `code=200`、`status=UP`。
   - 远端完整 smoke 复跑结果为 25 项通过、0 项失败、0 项跳过。
3. 当前服务器部署使用的是“当前本地工作树”。
   - 若后续查看 GitHub 是否包含同样内容，必须先确认本地改动是否已经提交并推送。
   - 不要把服务器部署成功误解为 GitHub 已同步。
4. 当前敏感信息处理口径：
   - 不在仓库记录服务器密码、GitHub token、腾讯地图 key、JWT secret、数据库密码。
   - 远端 smoke 报告中的 URL 已脱敏。
5. 下一步恢复工作时优先检查：
   - `git status --short`
   - `git log --oneline -5`
   - `project-logs/campus-relay/step-157-current-server-deploy-and-smoke.md`
   - `project-logs/campus-relay/runtime/step-157-server-deploy/remote-smoke-report-rerun.json`

默认下一步建议：

1. 若目标是同步 GitHub：先提交并推送 Step 153-157 及移动端视觉/Android 壳/部署日志相关文件，排除 `target/` 等本地临时产物。
2. 若目标是继续产品化：先做公网入口浏览器人工巡检，再重打 Android 双端 QA APK 并跑公网 API / WebView smoke。
3. 若目标是继续前端：先让 owner 提供新原型或明确页面范围，不再机械 polish。

## 2026-05-07 补充：Step 158 安卓双端前端视觉重构

1. Step 158 已完成安卓双端前端视觉与移动端交互重构：
   - 新增 `frontend/src/styles/mobile-theme.css` 移动端视觉基线，仅在 UserLayout / ParttimeLayout 内生效，不影响后台管理端。
   - UserLayout.vue 和 ParttimeLayout.vue 已重构为移动 App 风格页面壳（sticky 顶部栏 + fixed 底部导航 + 可滚动内容区）。
   - 用户端 7 个页面和兼职端 3 个页面的视觉布局已重构，统一卡片/按钮/状态标签/空态/错误态/loading 态样式。
   - 适配 360px/390px/430px 宽度，添加 overflow-x: hidden。
   - 去掉重 backdrop-filter（blur(18px)），保留轻量 blur(16px) 或纯白背景。
   - 去掉装饰性 ::after 伪元素和复杂 radial-gradient。
   - npm run build / build:android:user / build:android:parttime 均通过。
2. Step 158 未改内容：
   - 未改后台管理端页面、request.js、api/*、router、token、bridge、后端 Java、数据库、Android 原生壳。
3. Step 158 仍需验证：
   - 真机/模拟器 360px/390px/430px 宽度下的实际视觉验证。
   - Drawer 内长内容滚动在真机 WebView 中的表现。
   - 中文显示在真机 WebView 中的表现。

## 2026-05-11 补充：Step 159 公测 P0 收口复核与 Android 安全区修复

1. Step 159 已处理 Android WebView 安全区与构建 warning：
   - `frontend/index.html` viewport 增加 `viewport-fit=cover`。
   - 用户端 / 兼职端移动壳顶部栏增加 `env(safe-area-inset-top)` 支持。
   - 用户端 / 兼职端登录页增加顶部和底部 safe-area padding。
   - `CampusCourierOpsView.vue` 拉平嵌套 `:deep(...)` 表格选择器，消除 `lightningcss` 构建 warning。
2. Step 159 验证结果：
   - `npm run build` 通过。
   - `npm run build:android:user:public` 通过。
   - `npm run build:android:parttime:public` 通过。
   - 用户端 / 兼职端 `cap:sync:public` 通过。
   - 用户端 / 兼职端 Debug APK 构建通过。
   - `git diff --check` 通过，仅 CRLF 提示。
3. 当前真实缺口：
   - 2026-05-12 手机重新连接后，本轮新 APK 已完成双端安装、启动和登录 smoke。
   - 用户端登录成功进入首页。
   - 兼职端登录成功进入工作台。
   - Step 158 真机截图中兼职端曾出现 `网络连接失败，请检查网络` toast，本轮未复现。
   - 当前仍未完成真机完整业务链路：发布订单、模拟支付、接单、取餐、送达、用户确认、结果回读。
   - 工作树仍有多轮未提交改动和未跟踪文件，提交前需要确认哪些属于本次成果、哪些应归档或忽略。
4. 下一步恢复工作建议：
   - 先继续真机完整业务链路验证。
   - 每个关键节点保存截图和必要 logcat。
   - 若完整链路通过，再整理工作树提交边界。
   - 不要在完整真机链路未通过前扩大到公开公测。

## 2026-05-12 补充：Step 159 真机公网主链路已闭环

1. 手机真机和公网服务器验证：
   - ADB 设备：`10AE221PGA003Y5`
   - 公网服务器：已验证可达，真实地址不写入仓库。
   - 手机到公网服务器 ping 成功。
   - 用户端和兼职端 APK 均已安装并可启动。
2. 兼职端真机链路：
   - 订单 `CR202605010405291760` 在兼职端工作台可见。
   - 接单成功，进入 `ACCEPTED`。
   - 取餐凭证 `/api/files/android-smoke-pickup.jpg` 提交成功，进入 `PICKED_UP`。
   - 开始配送成功，进入 `DELIVERING`。
   - 确认送达成功，进入 `AWAITING_CONFIRMATION`。
3. 用户端真机链路：
   - 用户端结果页可查询订单 `CR202605010405291760`。
   - 本轮发现 `AWAITING_CONFIRMATION` 结果页缺少确认按钮。
   - 已补回 `confirmCampusCustomerOrder(orderId)` 和结果页 `确认已收到` 最小承接。
   - 修复后重新构建、同步并安装用户端 APK。
   - 真机点击 `确认已收到` 后，订单回读为 `已完成 / COMPLETED`。
4. 证据位置：
   - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-visible-step159.png`
   - `project-logs/campus-relay/runtime/step-159-android-device/window-user-confirm-visible.xml`
   - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-after-step159.png`
   - `project-logs/campus-relay/runtime/step-159-android-device/window-user-confirm-after.xml`
   - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-logcat.txt`
5. 当前判断：
   - 真机公网主链路已真实闭环，不再是“完整链路未完成”状态。
   - 当前可以进入 owner-controlled 小范围内测准备。
   - 仍不建议公开公测；公开公测前至少补：工作树提交边界、release 签名包、内测说明、反馈模板、弱网 / 后台切回 / 多账号回归。

## 2026-05-12 补充：Step 160 移动端可见文案已自然化

1. 本轮根据用户反馈“界面文字太僵硬”，完成用户端和兼职端核心页面文案优化。
2. 改动范围：
   - 用户端：`Home.vue`、`Login.vue`、`Profile.vue`、`CampusRelayOrders.vue`、`CampusOrderResult.vue`、`CourierOnboarding.vue`、`AfterSaleResult.vue`
   - 兼职端：`Login.vue`、`Profile.vue`、`CourierWorkbench.vue`
3. 可见文案口径：
   - 从 `token / 接口 / 字段 / 回读 / 最小承接` 等工程语言，改为“发单、查进度、报名、接单、确认取餐、确认送达、上报异常”等用户能直接理解的表达。
4. 未改内容：
   - 未改后端、接口、路由、bridge、`request.js`、token 附着逻辑或旧兼容模块。
5. 验证：
   - `npm run build` 通过。
   - `npm run build:android:user:public` 通过。
   - `npm run build:android:parttime:public` 通过。
   - `git diff --check` 通过，仅 CRLF 提示。
6. 下一步建议：
   - 先在真机上快速验收文字是否被截断或挤压。
   - 若准备给他人内测，优先补安装说明、账号说明、反馈模板和已知限制。

## 2026-05-13 补充：Step 161 Android 双端显示名已调整

1. 本轮把 Android 双端桌面显示名改成更清晰的中文端名：
   - 用户端：`用户端`
   - 兼职端：`兼职端`
2. 注意：真实 Android `applicationId` 没有改，也不应改成中文：
   - 用户端：`com.xiaoyu.campus.user`
   - 兼职端：`com.xiaoyu.campus.parttime`
3. 验证：
   - 双端 Capacitor public sync 通过。
   - 双端 Debug APK 构建通过。
   - `aapt dump badging` 确认 package 和 application-label 正确。
   - 手机 ADB 设备 `10AE221PGA003Y5` 在线。
   - 双端 APK 均已 `adb install -r` 成功。
   - 双端均可通过 launcher intent 启动。
4. 未改内容：
   - 未改后端、接口、路由、bridge、`request.js`、token 附着逻辑、管理后台或旧兼容模块。
5. 下一步建议：
   - 手动确认手机桌面图标文字是否刷新。
   - 再做 Android 双端真机小回归。
   - 若准备内测分发，再整理 release 签名包、安装说明和反馈模板。

## 2026-05-13 补充：Step 162 GitHub 与内测服务器已同步

1. 本地最新主线提交已经推送到 `origin/main`：
   - `1d68c534d091c533d82ded13d3b3b924eb00db91`
2. 内测服务器同步方式：
   - 使用本地 HEAD 生成源码归档。
   - 上传到内测服务器后展开到 `/opt/campus-part-time-job`。
   - 服务器原 `.env` 保留，不写入仓库。
   - 旧部署目录保留为上一版目录，便于必要时人工回滚。
3. 部署前备份：
   - 已执行 `deploy/internal-trial/backup-stack.sh`。
   - 已生成 MySQL、uploads 和 `.env` 备份。
4. 远端验证：
   - Docker Compose 已重建并启动 `mysql / backend / frontend`。
   - `scripts/trial-operation/remote-smoke.ps1` 已执行。
   - 远端 smoke 结果：25 PASS / 0 FAIL / 0 SKIP。
   - 报告位置：`project-logs/campus-relay/runtime/step-162-server-sync/remote-smoke-report.json`，URL 已脱敏。
5. 未改内容：
   - 未改业务代码、bridge、`request.js`、token 附着逻辑、接口、路由、鉴权或旧兼容模块。
6. 下一步建议：
   - 先补内测分发材料：APK 安装说明、测试账号、已知限制、反馈模板。
   - 再做 Android 双端真机小回归矩阵。
   - 公开公测前再补 release 签名包、HTTPS / 域名 / 证书和最低限度安全说明。

## 2026-05-13 补充：Step 163 内测分发材料已补齐

1. 新增文档：
   - `docs/deployment/internal-trial-distribution-pack.md`
2. 文档用途：
   - 作为 owner-controlled 小范围内测分发总入口。
   - 覆盖 APK 生成、Android 安装、测试账号发放、用户端 / 兼职端 / 管理后台建议测试链路、已知限制、反馈模板和 owner 发包前检查。
3. 安全边界：
   - 没有把测试账号密码写入公开仓库。
   - 没有写入公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。
4. 未改内容：
   - 未改业务代码、前端页面、后端代码、bridge、`request.js`、token 附着逻辑、接口、路由、鉴权或旧兼容模块。
5. 下一步建议：
   - 做 Android 双端真机小回归矩阵。
   - 若小回归通过，生成一轮 QA APK 分发包并记录 manifest。
   - 若要扩大内测人群，再补 release 签名包和 HTTPS / 域名 / 证书。

## 2026-05-13 补充：Step 164 Android 双端真机小回归矩阵已通过

1. 本轮真实设备：
   - ADB 设备：`10AE221PGA003Y5`
2. 脚本修正：
   - `android-webview-public-smoke.ps1`
   - `android-webview-user-public-smoke.ps1`
   - `android-webview-parttime-public-smoke.ps1`
   - 修正旧 Step 元数据，避免后续 runtime JSON 误标。
3. Android 用户端 WebView smoke：
   - 登录通过。
   - 取餐点读取通过。
   - 配送规则读取通过。
   - 订单列表读取通过。
   - 创建订单通过。
   - 模拟支付通过。
   - 详情回读通过。
   - 创建订单：`CR202605131052401467`
   - 回读状态：`BUILDING_PRIORITY_PENDING / PAID`
4. Android 兼职端 WebView smoke：
   - 登录通过。
   - 资料读取通过。
   - 审核状态读取通过。
   - 可接任务读取通过。
   - `courierProfileId = 2`
   - 审核状态：`APPROVED`
   - 可接任务数：`5`
5. 远端 smoke：
   - 25 PASS / 0 FAIL / 0 SKIP
   - 报告路径：`project-logs/campus-relay/runtime/step-164-android-regression-clear/remote-smoke-report.json`
6. 当前结论：
   - owner-controlled 小范围内测可继续推进。
   - 公开公测仍未就绪。
7. 下一步建议：
   - 补 Android 兼职端接单 / 取餐 / 送达 / 异常上报动作链。
   - 补用户端确认完成和 `COMPLETED` 回读。
   - 通过后再生成 QA APK 分发包 manifest。

## 2026-05-13 补充：Step 165 Python Android 双端动作链矩阵已通过

1. 新增脚本：
   - `scripts/trial-operation/android_action_matrix.py`
2. 脚本用途：
   - 用 Python 标准库和 ADB 跑 Android 双端动作链。
   - ADB 负责真实手机启动与截图。
   - 公开 API 负责驱动订单状态机。
   - JSON 报告脱敏 API host，不保存 token。
3. 本轮真实设备：
   - ADB 设备：`10AE221PGA003Y5`
4. 本轮订单：
   - `CR202605131124021644`
5. 已验证动作链：
   - 用户端登录。
   - 创建订单。
   - 模拟支付。
   - 兼职端登录。
   - 资料与审核状态读取。
   - 可接任务读取。
   - 接单。
   - 确认取餐。
   - 开始配送。
   - 异常上报。
   - 确认送达。
   - 用户确认。
   - 用户端和兼职端 completed 回读。
6. 最终状态：
   - 用户端：`COMPLETED`
   - 兼职端：`COMPLETED`
   - 支付：`PAID`
   - 异常摘要：`联系不上`
7. 证据位置：
   - `project-logs/campus-relay/runtime/step-165-android-action-matrix/android-action-matrix-report.json`
   - `project-logs/campus-relay/runtime/step-165-android-action-matrix/user-launch.png`
   - `project-logs/campus-relay/runtime/step-165-android-action-matrix/parttime-launch.png`
   - `project-logs/campus-relay/runtime/step-165-android-action-matrix/user-after-action-chain.png`
   - `project-logs/campus-relay/runtime/step-165-android-action-matrix/parttime-after-action-chain.png`
8. 未改内容：
   - 未改业务代码、前端页面、后端接口、Android 原生配置、bridge、`request.js`、token 附着逻辑、接口、路由、鉴权或旧兼容模块。
9. 当前结论：
   - owner-controlled 小范围内测可继续推进。
   - 公开公测仍未就绪。
10. 下一步建议：
   - 进入 QA APK 分发包 manifest 与安装复核。
   - 继续避免把公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容写入仓库。

## 2026-05-13 补充：Step 166 Android QA APK Manifest 与安装复核已完成

1. 新增安全版 manifest：
   - `docs/deployment/android-qa-apk-manifest.md`
2. 本轮生成的本地 QA APK：
   - 用户端：`project-logs/campus-relay/runtime/step-166-android-qa-apks/campus-user-public-debug.apk`
   - 兼职端：`project-logs/campus-relay/runtime/step-166-android-qa-apks/campus-parttime-public-debug.apk`
3. APK 基本信息：
   - 用户端包名：`com.xiaoyu.campus.user`
   - 用户端显示名：`用户端`
   - 用户端 version：`1 / 1.0`
   - 用户端 SHA256：`76398CB22068056DB3845A8AC749D914D4A15381783A643AD992D131CFE855CB`
   - 兼职端包名：`com.xiaoyu.campus.parttime`
   - 兼职端显示名：`兼职端`
   - 兼职端 version：`1 / 1.0`
   - 兼职端 SHA256：`197DFD540206EE427D1B5C11AF34E29341BC8C1F985FEC9DED12429B79E8B812`
4. 安装复核：
   - ADB 设备：`10AE221PGA003Y5`
   - 用户端 `adb install -r`：`Success`
   - 兼职端 `adb install -r`：`Success`
   - 用户端 `lastUpdateTime=2026-05-13 19:56:28`
   - 兼职端 `lastUpdateTime=2026-05-13 19:56:33`
5. 包名 / 显示名核验：
   - `aapt dump badging` 确认用户端包名和显示名正确。
   - `aapt dump badging` 确认兼职端包名和显示名正确。
6. 启动截图：
   - `project-logs/campus-relay/runtime/step-166-android-qa-apks/user-qa-install-launch.png`
   - `project-logs/campus-relay/runtime/step-166-android-qa-apks/parttime-qa-install-launch.png`
7. Git 处理：
   - APK 二进制产物不提交。
   - `.gitignore` 已忽略 runtime APK 与本地生成的 `android-qa-apk-manifest.json`。
8. 未改内容：
   - 未改业务代码、前端页面、后端接口、Android 包名/版本号、bridge、`request.js`、token 附着逻辑、接口、路由、鉴权或旧兼容模块。
9. 当前结论：
   - owner-controlled 小范围内测已有可追溯 QA APK manifest 和安装复核。
   - 公开公测仍未就绪。
10. 下一步建议：
   - 评估 release 签名策略。
   - 评估 HTTPS / 域名 / 证书和 cleartext 收口。
   - 补最小隐私说明、用户协议、反馈入口和问题分级规则。

## 2026-05-14 补充：Step 167 公开公测前发布安全基线已推进

1. 本轮根据当前仓库真实文件复核公开公测前缺口：
   - release 签名此前缺入口。
   - Android cleartext 此前一直为 true。
   - HTTPS / 域名 / 证书仍缺。
   - 隐私说明 / 用户协议仍缺。
   - App 内反馈入口仍缺。
2. 已完成 release 签名入口：
   - `mobile/user-app/android/app/build.gradle`
   - `mobile/parttime-app/android/app/build.gradle`
   - 两端均从本地 `android/key.properties` 读取 release 签名配置。
   - 未提供真实 key 时 release 构建仍可执行，但会输出 unsigned release 和 warning。
3. 已新增示例文件：
   - `mobile/user-app/android/key.properties.example`
   - `mobile/parttime-app/android/key.properties.example`
4. 已增加 ignore 保护：
   - `mobile/**/android/key.properties`
   - `mobile/**/android/keystore/`
   - `mobile/**/android/*.jks`
   - `mobile/**/android/*.keystore`
5. 已完成 Android cleartext 策略拆分：
   - Debug：`usesCleartextTraffic=true`
   - Release：`usesCleartextTraffic=false`
   - 两端均新增 debug/release `network_security_config.xml`
   - 两端 Manifest 均使用 placeholder 和 `tools:replace`
6. 验证结果：
   - 用户端 `assembleDebug` 通过。
   - 用户端 `assembleRelease` 通过。
   - 兼职端 `assembleDebug` 通过。
   - 兼职端 `assembleRelease` 通过。
   - 合并 manifest 已确认 debug true / release false。
7. 新增发布缺口清单：
   - `docs/deployment/public-beta-release-gap-closure.md`
8. 未改内容：
   - 未生成或提交真实 keystore。
   - 未提交签名密码。
   - 未配置域名、证书或 HTTPS。
   - 未改后端接口。
   - 未改前端业务页面。
   - 未重开 bridge。
   - 未删除旧兼容模块。
9. 当前结论：
   - 发布安全基线比 Step 166 更接近公开公测。
   - 但公开公测仍未就绪。
10. 下一步建议：
   - 如果冲公开公测，先做域名 / HTTPS / 证书和真实 release 签名。
   - 如果继续内测，先补隐私/用户协议静态页和 App 内反馈入口。

## 2026-05-14 补充：Step 168 xiaoyu.xin HTTPS / Nginx 443 接入准备已完成

1. owner 已确认域名：
   - `xiaoyu.xin`
2. 本地 DNS 解析确认：
   - `xiaoyu.xin` 指向当前服务器。
3. 仓库侧 HTTPS 接入准备已完成：
   - `deploy/internal-trial/docker-compose.yml`
   - `deploy/internal-trial/.env.example`
   - `deploy/internal-trial/nginx-xiaoyu.xin.conf`
   - `docs/deployment/xiaoyu-xin-https-runbook.md`
4. Docker 端口策略：
   - frontend 默认 `127.0.0.1:18080`
   - backend 默认 `127.0.0.1:8080`
   - 公网只应通过宿主机 Nginx 80/443 访问。
5. Android public API base：
   - tracked example：`https://xiaoyu.xin/api`
   - ignored local env 也已同步为 `https://xiaoyu.xin/api`
6. 验证：
   - Web build 通过。
   - Android user public build 通过。
   - Android parttime public build 通过。
   - 后端 compile 通过。
   - public 构建产物命中 `https://xiaoyu.xin/api`，未命中旧 HTTP 公网 IP。
   - `git diff --check` 通过，仅 CRLF 提示。
7. 未完成：
   - 未在服务器实操 Certbot。
   - 未签发真实证书。
   - 未验证 `https://xiaoyu.xin/` 和 `https://xiaoyu.xin/api/`。
   - 未生成真实 release keystore。
   - 未补隐私政策 / 用户协议 / App 内反馈入口。
8. 继续禁止：
   - 不提交真实证书、证书私钥、服务器密码、`.env`、腾讯地图 key、GitHub token 或 release keystore。
   - 不重开 bridge。
   - 不改 `request.js`、token 附着逻辑或旧兼容模块。

## 2026-05-14 补充：Step 169 xiaoyu.xin HTTPS 服务器实操已完成

1. 服务器 HTTPS 已完成：
   - Nginx 已安装并启用。
   - Certbot 与 Nginx 插件已安装。
   - `xiaoyu.xin` 真实证书已签发。
   - HTTP 80 已跳转 HTTPS。
   - `https://xiaoyu.xin/` 已反代 frontend。
   - `https://xiaoyu.xin/api/` 已反代 backend。
   - 证书有效期至 `2026-08-12`。
   - Certbot timer 已存在。
   - `certbot renew --dry-run` 日志确认模拟续期成功。
2. 服务器部署状态：
   - 当前仓库归档已同步到服务器部署目录。
   - Docker Compose 已重建并启动。
   - frontend 仅绑定宿主机本机端口。
   - backend 仍由宿主机 Nginx 通过 `/api/` 反代。
   - 服务器真实 `.env` 未写入仓库。
3. 验证：
   - 远端 smoke：25 PASS / 0 FAIL / 0 SKIP。
   - Android public API smoke：2 PASS / 0 FAIL。
   - Android public Debug QA APK 已重新生成。
   - 真机轻量安装 / 启动 smoke 已通过。
4. 新增脚本：
   - `scripts/trial-operation/android-app-launch-smoke.py`
   - 用途：安装/拉起用户端与兼职端 APK，输出 JSON 报告。
   - 默认不截图。
   - 默认脱敏真实设备 ID。
5. 当前仍未解决：
   - 真实 release keystore。
   - 正式 release 签名包。
   - 隐私政策 / 用户协议入口。
   - App 内反馈入口。
6. 继续禁止：
   - 不提交证书、证书私钥、服务器 `.env`、服务器密码、公网地址、GitHub token、腾讯地图 key、release keystore 或真实设备 ID。
   - 不重开 bridge。
   - 不改 `request.js`、token 附着逻辑或旧兼容模块。

## 2026-05-14 补充：Step 170 隐私协议与反馈入口已完成

1. App 内合规入口：
   - `/legal/privacy`
   - `/legal/terms`
   - 用户端登录页已要求勾选协议。
   - 兼职端登录页已要求勾选协议。
2. App 内反馈入口：
   - `/feedback`
   - 用户端个人中心可进入。
   - 兼职端资料页可进入。
3. 后端最小闭环：
   - 新增 `campus_feedback`。
   - 新增 `POST /api/campus/public/feedback`。
   - 走现有 `/api/campus/public/**` 公共路径，不需要 token。
4. 验证：
   - 后端 compile 通过。
   - Web build 通过。
   - Android user public build 通过。
   - Android parttime public build 通过。
   - H2/test profile 运行态反馈提交通过，返回 `code=200,data=1`。
   - `git diff --check` 通过，仅 CRLF 提示。
5. 当前仍未解决：
   - 真实 release keystore。
   - 正式 release 签名 APK。
   - admin 反馈只读 / 处理入口。
6. 继续禁止：
   - 不提交真实 keystore、`key.properties`、证书私钥、服务器 `.env`、服务器密码、公网地址、GitHub token、腾讯地图 key 或真实设备 ID。
   - 不重开 bridge。
   - 不改 `request.js`、token 附着逻辑、鉴权或旧兼容模块。

## 2026-05-14 补充：Step 171 旧外卖前端可见模块收口已完成

1. 前端旧外卖入口已移除：
   - admin 侧边栏不再显示“旧模块兼容”分组。
   - admin 旧路由已移除：`/category`、`/dish`、`/setmeal`、`/order`、`/shop-status`、`/component-demo`。
   - user 旧路由已移除：`/user/category`、`/user/dish/:id`、`/user/cart`、`/user/checkout`、`/user/orders`。
2. 已删除：
   - 旧前端页面。
   - 旧前端 API wrapper。
   - Vite 模板残留。
   - 未引用 `stores/mock.js`。
3. 已调整：
   - Dashboard 最近订单读取 campus admin 订单列表。
   - 用户端首页不再展示旧模块兼容入口。
   - 用户端个人中心不再展示旧地址、旧订单、旧购物车入口。
4. 未删除：
   - 后端旧外卖 controller / service / mapper / entity。
   - 旧数据库表和 H2 seed。
   - `user`、`employee`、登录、上传、统计等基础能力。
5. 验证：
   - Web build 通过。
   - Android user public build 通过。
   - Android parttime public build 通过。
   - Backend compile 通过。
   - `git diff --check` 通过，仅 CRLF 提示。
6. 当前仍未解决：
   - 旧后端模块删除前依赖审计。
   - 真实 release keystore。
   - 正式 release 签名 APK。
7. 继续禁止：
   - 不重开 bridge。
   - 不改 `request.js`、token 附着逻辑、鉴权或核心状态机。
   - 不删除 `user` / `employee` 等 shared 基础模块。
   - 不提交真实密钥、证书、服务器凭据、release keystore、GitHub token、腾讯地图 key 或 `.env`。

## 2026-05-14 补充：Step 172 前端去旧后本地 smoke 已完成

1. 本地服务：
   - 启动 backend `test` profile。
   - 启动 frontend dev server。
   - smoke 后已关闭本轮启动的服务。
2. API + SPA shell smoke：
   - 报告：`project-logs/campus-relay/runtime/step-172-legacy-frontend-smoke/local-remote-smoke.json`
   - 结果：25 PASS / 0 FAIL / 0 SKIP。
   - 覆盖 admin / customer / parttime 登录和关键接口。
   - 覆盖关键 SPA shell。
3. 浏览器截图 smoke：
   - 报告：`project-logs/campus-relay/runtime/step-172-legacy-frontend-smoke/browser-smoke-report.json`
   - 结果：7 PASS / 0 FAIL。
   - 覆盖 5 个 admin 页面、customer 结果页、parttime workbench。
   - 后续可拆成 admin / user / parttime 三段以降低耗时。
4. 额外修复：
   - `frontend/src/stores/customer.js` 已对 malformed `customer_user_info` 增加安全解析。
   - 坏 localStorage 值会被清理并回退为空用户，不再导致 router 初始化报错。
5. 当前结论：
   - Step 171 前端去旧没有破坏核心 API 和关键 SPA shell。
   - 后端旧模块仍未删除。
6. 当前仍未解决：
   - 旧后端模块删除前依赖审计。
   - 真实 release keystore。
   - 正式 release 签名 APK。
7. 继续禁止：
   - 不重开 bridge。
   - 不改 `request.js`、token 附着逻辑、鉴权或核心状态机。
   - 不删除 shared 基础模块。
   - 不提交真实密钥、证书、服务器凭据、release keystore、GitHub token、腾讯地图 key 或 `.env`。

## 2026-06-23 补充：Step 173 admin 反馈运营闭环与 CI 回归加固

1. `campus_feedback` 已不再是只能入库的孤立能力：
   - admin 可分页筛选、查看详情。
   - admin 可推进到 `IN_PROGRESS / RESOLVED`。
   - 保留处理管理员、处理时间和处理备注。
2. 管理后台新增 `/campus/feedback`。
3. 数据库新增 `V14__campus_feedback_admin_processing.sql`；服务器升级前必须执行该 migration。
4. 回归基线：
   - backend 58 tests / 0 failures。
   - frontend lint 通过。
   - frontend 5 tests 通过。
   - Web、Android user public、Android parttime public 构建通过。
   - sample validation 0 hard failures；3 个既有 optional warnings。
5. CI 已升级：
   - backend 运行 `mvn test`。
   - frontend 运行 lint、Vitest、build。
6. 当前公开公测最后核心阻断项仍是 owner 私有的真实 release keystore 和正式签名 APK。
7. 后续优先级：
   - release 签名包。
   - 部署 V14 与远端 smoke。
   - admin 反馈页 browser smoke。
   - 公共反馈防刷。
   - 旧后端模块逐项依赖审计。

## 2026-06-23 补充：Step 174 反馈防刷与 MuMu 回归已完成

1. 公开反馈：
   - 内容至少 5 个字符。
   - 同一组精确反馈 60 秒内重复提交返回业务码 429。
   - Nginx 对 `/api/campus/public/feedback` 做单独限流。
2. HTTPS / Nginx：
   - TLS 1.2 / 1.3。
   - HSTS、nosniff、SAMEORIGIN、Referrer Policy。
3. 独立部署预案：
   - `deploy/standalone-podman/` 使用 `campus-standalone-*` 命名。
   - backend / MySQL 只绑定回环。
   - 该预案未完成服务器部署。
4. Linux 边界：
   - owner 明确要求停止 Linux 后，不再连接任何服务器。
   - 停止前仅在独立备用机安装 Podman并暂存源码。
   - 未拉镜像、未创建容器/网络/卷、未启动服务、未占用业务端口、未修改集群。
5. MuMu 基线：
   - Android 12。
   - adb 设备：本机回环端口。
   - `10.0.2.2` 可访问 Windows 宿主机。
   - 双端 Debug APK 安装、启动、截图通过。
6. WebView 回归：
   - 用户端登录、创建代送单、模拟支付、详情回读通过。
   - 兼职端登录、profile、review-status、available orders 通过。
   - 协议勾选已纳入自动 smoke。
7. 回归基线：
   - backend 58 tests。
   - frontend lint。
   - frontend 5 tests。
   - frontend build。
   - sample validation 0 hard failures。
8. 当前 P0：
   - owner 私有 release keystore。
   - 正式签名 APK。
   - release 包 HTTPS MuMu / 真机回归。
   - owner 重新授权后的 V14 服务器部署与远端 smoke。

## 2026-06-24 补充：Step 175 Android release candidate 管线已完成

1. Android release 配置：
   - 双端提交态 `capacitor.config.json` 为 `androidScheme=https`、`cleartext=false`。
   - `cap:sync` / `cap:sync:emulator` / `cap:sync:lan` 会切到 HTTP cleartext 调试。
   - `cap:sync:public` 会切回 HTTPS-only。
2. Android release 构建：
   - 双端支持 `CAMPUS_VERSION_CODE`。
   - 双端支持 `CAMPUS_VERSION_NAME`。
   - 双端支持 `CAMPUS_REQUIRE_RELEASE_SIGNING`。
   - `key.properties` 不完整会失败。
   - keystore 文件不存在会失败。
   - release 校验 no-cleartext。
   - release 校验 `allowBackup=false`。
3. 新增脚本：
   - `scripts/trial-operation/build-android-release-apks.ps1`
   - `scripts/trial-operation/android-release-webview-smoke.ps1`
   - `mobile/configure-capacitor.mjs`
4. CI / workflow：
   - `.github/workflows/trial-operation-ci.yml` 新增 unsigned release candidate job。
   - `.github/workflows/android-release.yml` 新增手动 signed release workflow。
   - signed workflow 依赖 owner 配置 GitHub Secrets，构建后清理私有签名文件。
5. 本地验证：
   - unsigned release candidate 构建通过。
   - 临时 QA keystore signed release candidate 构建通过。
   - MuMu Android 12 安装双端 signed release APK 通过。
   - 双端 `versionCode=175`、`versionName=0.9.0-rc.175`。
   - release 启动 smoke 通过：焦点、截图、logcat 均满足要求。
   - backend 58 tests 通过。
   - frontend lint / 5 tests / build 通过。
   - sample validation CI wrapper 通过：0 hard failures，3 个 optional warnings。
   - `git diff --check` 通过，仅 CRLF 提示。
6. 安全边界：
   - 临时 QA keystore 和本地 `key.properties` 已删除。
   - 未连接 Linux。
   - 未部署服务器。
   - 未修改集群环境。
   - 未提交真实 keystore、密码、证书私钥、服务器凭据、GitHub token、腾讯地图 key 或真实设备 ID。
7. 当前 P0：
   - owner 生产 release keystore 或 GitHub signing secrets。
   - 正式 signed APK / AAB。
   - 生产签名包 MuMu / 真机主链路 smoke。
   - owner 重新授权后的服务器部署、V14 migration 和公网 smoke。
8. 当前公网状态：
   - 本机访问 `https://xiaoyu.xin/api/campus/public/health` 超时。
   - 在 owner 重新授权服务器操作前，不默认连接服务器定位。

## 2026-06-24 补充：Step 176 138 standalone smoke 部署已完成

1. owner 已重新授权使用 `192.168.121.138`，但边界是“不动集群环境”。
2. 138 系统盘扩容已完成：
   - `/dev/sda2` 约 39G。
   - root LV / xfs 约 37G。
   - `/` 可用空间约 23G。
   - 分区表备份在 138 的 `/root/sda-partition-before-campus-expand-20260624-124429.sfdisk`。
3. standalone 部署目录：
   - `/opt/campus-part-time-job-standalone`
4. 当前 138 运行的是 H2 smoke-only 栈：
   - frontend：`0.0.0.0:18080`
   - backend：`127.0.0.1:18081`
   - 容器名：`campus-standalone-backend`、`campus-standalone-frontend`
   - 网络名：`campus-standalone-net`
5. 远端 smoke：
   - `project-logs/campus-relay/runtime/step-176-standalone-138-h2-smoke/remote-smoke-report.json`
   - 27 PASS / 0 FAIL / 0 SKIP
6. 持久化 DB 尚未完成：
   - `docker.io` 访问不稳定。
   - MySQL 镜像 mirror 拉取过慢 / 中断。
   - 138 宿主机已有 MySQL root 未授权，未修改、未复用。
7. standalone 脚本状态：
   - `deploy.sh` 支持镜像源覆盖。
   - `deploy.sh` 敏感环境变量通过名称继承，避免明文出现在 `podman run` 命令行。
   - 新增 `deploy-h2-smoke.sh` 作为非持久化 smoke fallback。
8. 后续默认不要把 H2 smoke-only 当正式部署：
   - 若继续上线前发布，优先解决 MySQL 持久化部署。
   - 然后复跑远端 smoke、浏览器 smoke、Android release 公网 smoke。
9. 继续禁止：
   - 不改集群网络、卷、容器或服务。
   - 不提交 `.env`、密码、证书私钥、release keystore、GitHub token、腾讯地图 key 或服务器凭据。

## 2026-06-24 补充：Step 177/178 集群命名与环境整理

1. owner 明确：`192.168.121.138` 是主节点 / 主控入口，日常操作优先在 138 上完成。
2. 三台机器已规范化命名：
   - `192.168.121.138 = master`
   - `192.168.121.139 = worker01`
   - `192.168.121.140 = worker02`
3. `/etc/hosts` 当前策略：
   - 新名字在前。
   - 旧 `hbase01 / hbase02 / hbase03` 保留为兼容别名。
   - 不建议直接删除旧别名，因为 Hive metastore / HDFS 历史路径可能仍有旧 URI。
4. 当前集群主控口径：
   - HDFS NameNode：`master`
   - HDFS SecondaryNameNode：`master`
   - YARN ResourceManager：`master`
   - Hive metastore / HiveServer2：`master`
   - MySQL：`master`
   - workers：`master / worker01 / worker02`
5. 当前配置已更新：
   - Hadoop：`core-site.xml`、`yarn-site.xml`、`hdfs-site.xml`、`workers`
   - ZooKeeper：`zoo.cfg`
   - Hive：`master` 上 `hive-site.xml`
   - HBase：`master` 上 `hbase-site.xml`
   - Chrony：`worker01 / worker02` 注释
6. 当前运行态：
   - 三台 `jps -lm` 只有 `Jps`。
   - 本轮没有启动、停止或重启集群服务。
7. 当前不要改：
   - `/etc/fstab`、GRUB、kernel cmdline 中的 `cs_hbase01`，这是 LVM 名称。
   - `/export/servers/hadoop-3.2.2/tmp/dfs/...` 中的旧名，这是 HDFS 数据块和历史作业内容。
   - `.bak*`、历史备份、日志。
8. 备份位置：
   - Step 177：`/root/cluster-config-backup-20260624-*.tar.gz`
   - Step 178：`/root/cluster-rename-backup-20260624-*.tar.gz`
9. 后续优先：
   - 写 `master` 主控启动 / 停止 runbook。
   - 写只读健康检查脚本。
   - 如需 HBase，先决定单机 HBase 还是三机 HBase；当前只有 `master` 有 HBase 目录，不建议直接启动三机 HBase。

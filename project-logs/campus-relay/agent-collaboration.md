# AI 协作交接文件

## 用途

这份文件是跨 AI 会话 / 跨工具协作时的最小上下文恢复入口。任何新的 AI 实例或协作工具在开始操作之前，必须先读本文件，再读：

1. `project-logs/campus-relay/global-working-memory.md` — 全局状态快照
2. `project-logs/campus-relay/pending-items.md` — 当前待处理事项
3. `project-logs/campus-relay/summary.md` — 完整历史 step 索引
4. `AGENTS.md` — 项目级约束

## 当前项目状态

本项目不是从零开始的新项目，而是在苍穹外卖基础之上改造的校内兼职 / 校园代送平台。

- **当前版本**：`2.0.0-campus-trial`（试运营版）
- **旧外卖模块**：仍作为兼容模块保留，不删除
- **campus 主线**：已是当前主要业务方向，三端闭环完整
- **项目阶段**：主链路稳定，多条线冻结，当前进入运维加固 / 旧模块审计阶段

### 核心能力覆盖

- customer：校园代送下单、模拟支付、onboarding 资料提交、售后结果回看、completed 回读
- courier：workbench、接单、取餐、deliver、异常上报、送达等待确认
- admin：courier 审核、settlement 运营、after-sale 运营、异常处理
- 移动端：双 Capacitor Android 壳（user-app / parttime-app），已验证 public WebView smoke

## 当前冻结线

以下主线当前处于冻结态或维护态，除非本轮任务明确要求，否则**不要重开**：

| 主线 | 状态 | 说明 |
|---|------|------|
| **bridge 主线** | `Phase A no-op` 冻结 | 不删除旧接口、不改鉴权、不找收口候选 |
| **展示 polish 线** | 冻结/维护态 | 不继续机械 polish 页面 |
| **媒体与交付线** | 已收住 | 不继续截图/录屏 |
| **腾讯地图线** | 冻结为单页 admin 只读 | 不扩到第二个页面 |
| **前端打包优化线** | freeze / no-op | 不做 Element Plus 按需拆分 |
| **试运营脚本线** | 维护态 | preflight / validate / commands 已够用 |
| **settlement P3 后端线** | 冻结/维护态 | 异常/售后/settlement 三线已收住 |

## 当前允许推进的主线

- 旧外卖可见痕迹清理（只做文档审计，不删除代码）
- 旧模块删除前依赖审计
- 前端校园兼职风格统一（小范围，不大改）
- 文档和交接材料整理
- 运维加固（备份、恢复、runbook）
- Android / 内测线（HTTPS 收口、域名配置）

## 当前禁止事项

1. **不直接删旧后端模块** — 必须先做依赖审计
2. **不删旧数据库表** — user / employee 仍被 campus 复用
3. **不删旧路由** — 旧路由继续保留为兼容入口
4. **不改鉴权** — JWT 拦截器、token 生成和校验逻辑不动
5. **不改 request.js** — 前端 token 附着和请求拦截逻辑不动
6. **不改 token 逻辑** — customer_token / courier_token / admin_token 策略不动
7. **不提交密钥** — 服务器密码、GitHub token、腾讯地图 key、公网 IP
8. **不碰服务器部署** — 除非任务明确指定
9. **不把审计轮做成删除轮** — 旧模块删除前必须先评估
10. **不新增第五个 admin 页**
11. **不重开 bridge 主线**

### 不可删除的具体项

- `/api/campus/courier/profile`
- `/api/campus/courier/review-status`
- 旧 `orders / cart / address` 语义
- `request.js` 现有 token 附着逻辑
- `CourierWorkbench.vue` 现有优先 `courier_token` 策略

## Claude Code 每轮结束必须在本文件追加

每轮结束时，在本文件末尾追加以下内容：

```markdown
## 轮次记录 YYYY-MM-DD

### 本轮目标
（简述本轮要做什么）

### 实际改动
（列出实际修改的文件和内容摘要）

### 未改动内容
（明确哪些明确不应该动但确实没动）

### 风险
（当前操作可能引入的风险）

### 下一轮建议
（建议下一步做什么）

### 关键确认
- 是否涉及 bridge：是 / 否（默认应为"否"）
- 是否涉及旧外卖删除：是 / 否（默认应为"否，本轮只做评估"）
- 是否改了鉴权：是 / 否（默认应为"否"）
- 是否改了路由：是 / 否（默认应为"否"）
- 是否改了 token 逻辑：是 / 否（默认应为"否"）
- 是否提交了密钥：是 / 否（默认应为"否"）
- backend compile 是否通过：是 / 否
- frontend build 是否通过：是 / 否
```

---

## 轮次记录 2026-04-27

### 本轮目标
旧外卖模块收口评估 + AI 协作交接文件 + 删除前审计计划。仅做文档，不删代码。

### 实际改动
- 新增 `project-logs/campus-relay/agent-collaboration.md`（本文件）
- 新增 `project-logs/campus-relay/legacy-takeaway-removal-readiness.md`
- 更新 `project-logs/campus-relay/summary.md`：追加 Step 125
- 更新 `project-logs/campus-relay/pending-items.md`：追加 Step 126 待处理
- 更新 `project-logs/campus-relay/file-change-list.md`：追加 Step 125 文件清单

### 未改动内容
- 没有改动任何业务代码
- 没有改动任何后端 Java 文件
- 没有改动任何前端 Vue/JS 文件
- 没有改动 bridge
- 没有改动鉴权
- 没有改动路由
- 没有改动数据库表或数据
- 没有改动部署配置
- 没有提交任何密钥

### 风险
- 无。本轮只做文档，不涉及任何运行时风险。

### 下一轮建议
进入 Phase 1：在 MainLayout 中将旧模块菜单项的 "分类兼容 / 商品兼容 / 套餐兼容 / 订单兼容 / 旧店铺状态" 文案进一步收敛为 "兼容模块 - 分类 / 兼容模块 - 商品 / 兼容模块 - 套餐 / 兼容模块 - 订单 / 兼容模块 - 店铺状态"，不改路由、不删页面。

### 关键确认
- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否，本轮只做评估
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- backend compile 是否通过：是
- frontend build 是否通过：是

---

## 轮次记录 2026-04-27 (Step 126)

### 本轮目标
前端视觉优化优先轮：优化后台可见信息架构与文案，让后台看起来像"校内兼职 / 校园代送运营平台"。只做前端展示层和文案层优化，不删旧外卖模块，不改业务逻辑、接口、路由语义、后端代码。

### 实际改动
- 修改 `frontend/src/layout/MainLayout.vue`：菜单"旧店铺状态"→"店铺状态兼容"，breadcrumb "/shop-status"→"店铺状态兼容"
- 修改 `frontend/src/views/Category.vue`：标题→"分类兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/Dish.vue`：标题→"商品兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/Setmeal.vue`：标题→"套餐兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/Order.vue`：标题→"订单兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/ShopStatus.vue`：标题→"店铺状态兼容"，新增兼容提示 banner
- 修改 `frontend/src/stores/mock.js`：mock 数据中"销售额"→"模拟流水"，"总销售额"→"模拟服务流水"，注释"菜品销售分布"→"服务类型分布"
- 新增 `project-logs/campus-relay/step-126-campus-admin-frontend-visual-rebaseline.md`
- 更新 `summary.md`、`pending-items.md`、`file-change-list.md`、本文件

### 未改动内容
- 没有删除任何旧外卖模块
- 没有改后端 Java 代码、数据库、鉴权、路由、request.js、token 逻辑、bridge
- 没有删除 `/api/campus/courier/profile` 和 `/api/campus/courier/review-status`

### 风险
- 极低。所有改动仅限前端文案和展示层。

### 下一轮建议
Phase 2：清理前端残留文件（HelloWorld.vue、vue.svg、vite.svg、ComponentDemo.vue + 对应路由）。

### 关键确认
- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- 是否改了后端 Java 代码：否
- frontend build 是否通过：是（1.06s）
- backend compile 是否通过：本轮未改后端代码，非必须

---

## 轮次记录 2026-04-27 (Step 127)

### 本轮目标
用户端 + 兼职端移动入口视觉统一。统一用户端和兼职端的移动端视觉，让它们看起来像"校内兼职 / 校园代送平台"，而不是旧外卖移动端。只改展示层文案和视觉（卡片玻璃态、campus teal 色系、旧词替换），不改任何业务行为、API、路由语义、鉴权或后端代码。

### 实际改动
- 修改 `frontend/src/views/user/Login.vue`：测试账号提示卡片改为玻璃风格
- 修改 `frontend/src/views/user/CampusRelayOrders.vue`："外卖内容"→"代送内容"，placeholder 去美团化，alert 措辞收敛
- 修改 `frontend/src/views/user/CampusOrderResult.vue`：卡片从 white 升级为玻璃态，campus teal 色系统一
- 修改 `frontend/src/views/user/CourierOnboarding.vue`："兼职配送入驻"→"校园兼职入驻"，卡片玻璃态，campus teal 色系
- 修改 `frontend/src/views/courier/Login.vue`：登录按钮升级为 teal→cyan 渐变
- 修改 `frontend/src/views/courier/CourierWorkbench.vue`：卡片玻璃态，campus teal 色系统一
- 修改 `frontend/src/views/courier/Profile.vue`：summary-item 和 notice-panel 颜色统一为 campus 色系
- 新增 `project-logs/campus-relay/step-127-mobile-user-parttime-visual-unification.md`
- 更新 `summary.md`、`pending-items.md`、`file-change-list.md`、本文件

### 未改动内容
- 没有改后端 Java 代码、SQL、数据库
- 没有改任何 API 文件运行时行为
- 没有改路由语义
- 没有改鉴权
- 没有改 request.js
- 没有改 token 逻辑
- 没有改 bridge
- 没有改 Android 原生壳
- 没有删除旧外卖模块
- 没有清理 HelloWorld.vue / ComponentDemo.vue / vue.svg / vite.svg
- 没有新增第五个 admin 页
- 没有删除 /api/campus/courier/profile 和 /api/campus/courier/review-status

### 风险
- 极低。所有改动仅限前端展示层文案和 CSS 视觉（颜色、背景、圆角、阴影），不改任何业务逻辑、条件判断或 API 调用。

### 下一轮建议
1. 若继续推进视觉统一，可处理旧兼容模块页面的移动端适配。
2. 若转向功能开发，优先评估"用户端自助售后申请"的最小实现。
3. 若保持维护态，当前项目已达到三端视觉统一的稳定状态。

### 关键确认
- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否，本轮只做展示层优化
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- 是否改了后端 Java 代码：否
- frontend build 是否通过：是（1.00s）
- backend compile 是否通过：本轮未改后端代码，非必须

---

## 轮次记录 2026-05-07 (Step 157 / GitHub 同步前上下文维护)

### 本轮目标

整理本地 Step 153-157 以来的移动端视觉、用户端订单主链路、Android 壳、服务器部署与远端 smoke 留痕，并准备同步到 GitHub。

### 实际改动

- 新增 Step 157 服务器部署与远端 smoke 日志。
- 更新 `summary.md`、`pending-items.md`、`file-change-list.md`。
- 更新 `global-working-memory.md`，记录当前服务器部署状态、远端 smoke 结果和恢复工作入口。
- 将根目录临时截图归档到对应 runtime 目录。
- 将根目录 `target/` 加入 `.gitignore`，避免本地临时运行产物进入仓库。

### 未改动内容

- 没有改 bridge。
- 没有改后端鉴权。
- 没有改 `request.js`。
- 没有改 token 附着逻辑。
- 没有删除旧外卖兼容模块。
- 没有提交真实密钥、服务器密码、GitHub token 或腾讯地图 key。

### 风险

- 当前服务器部署来自本地工作树，必须提交并推送后 GitHub 才能体现同一份成果。
- 当前仍是 HTTP 单机内测，不是 HTTPS / 域名 / 正式生产上线。

### 下一轮建议

1. 提交并推送当前本地有效成果。
2. 推送后核对 `origin/main` 与本地 `HEAD` 是否一致。
3. 若继续试运营准备，优先跑浏览器人工巡检和 Android 双端公网 smoke。

---

## 轮次记录 2026-05-07 (Step 158)

### 本轮目标

安卓双端前端视觉与移动端交互重构。统一用户端和兼职端的移动端视觉基线，让两端看起来像两个完整的移动 App。只改前端视觉和移动端交互层，不改后台管理端、API、token、bridge、路由语义。

### 实际改动

- 新增 `frontend/src/styles/mobile-theme.css` 移动端视觉基线
- 重构 `frontend/src/layout/UserLayout.vue` 页面壳
- 重构 `frontend/src/layout/ParttimeLayout.vue` 页面壳
- 重构用户端 7 个页面视觉：Login/Home/CampusRelayOrders/CampusOrderResult/CourierOnboarding/Profile/AfterSaleResult
- 重构兼职端 3 个页面视觉：Login/CourierWorkbench/Profile
- 新增 `project-logs/campus-relay/step-158-android-dual-end-frontend-rebuild.md`
- 更新 `summary.md`、`pending-items.md`、`file-change-list.md`、本文件

### 未改动内容

- 没有改后台管理端页面（MainLayout.vue / Dashboard.vue / Employee.vue 等）
- 没有改 request.js
- 没有改 api/* 的接口行为
- 没有改 router 路由语义
- 没有改 token 附着逻辑
- 没有改 bridge 相关逻辑
- 没有改后端 Java 代码
- 没有改数据库
- 没有改 Android 原生壳层逻辑

### 风险

- 低。所有改动仅限前端 CSS 视觉和纯展示容器/class，不改任何业务逻辑、条件判断或 API 调用。
- 部分 Hero 卡片保留了轻量 backdrop-filter，在极低端 Android 设备上可能有轻微卡顿。

### 下一轮建议

1. 在 Android 模拟器或真机上做 360px/390px/430px 宽度下的实际视觉验证。
2. 如发现具体页面溢出或交互问题，只做 bug 级修复。
3. 可考虑清理 Home.vue 中已隐藏的装饰 HTML 元素。

### 关键确认

- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- 是否改了后端 Java 代码：否
- frontend build 是否通过：是
- build:android:user 是否通过：是
- build:android:parttime 是否通过：是

---

## 轮次记录 2026-05-11 (Step 159)

### 本轮目标

公测 P0 收口复核与 Android 安全区修复。基于 Step 158 的安卓双端视觉重构结果，处理 WebView 安全区、顶部遮挡和构建 warning，并重新生成 public APK。

### 实际改动

- `frontend/index.html` viewport 增加 `viewport-fit=cover`。
- `frontend/src/layout/UserLayout.vue` 顶部栏增加 safe-area top 支持。
- `frontend/src/layout/ParttimeLayout.vue` 顶部栏增加 safe-area top 支持。
- `frontend/src/views/user/Login.vue` 增加 safe-area top / bottom padding。
- `frontend/src/views/courier/Login.vue` 增加 safe-area top / bottom padding。
- `frontend/src/views/CampusCourierOpsView.vue` 拉平嵌套 `:deep(...)` 表格选择器，消除构建 warning。
- 新增 `project-logs/campus-relay/step-159-public-beta-p0-closure-and-android-safe-area-fix.md`。
- 更新 `summary.md`、`pending-items.md`、`file-change-list.md`、本文件和 `global-working-memory.md`。

### 未改动内容

- 没有改后端 Java 代码。
- 没有改数据库。
- 没有改 `request.js`。
- 没有改 `api/*` 运行时行为。
- 没有改 router。
- 没有改 token 附着逻辑。
- 没有改 bridge。
- 没有删除旧外卖兼容模块。
- 没有提交真实密钥、服务器密码、GitHub token 或腾讯地图 key。

### 风险

- ADB 当前无在线设备，本轮重新生成的 APK 尚未完成真机安装和截图复测。
- Step 158 真机截图中兼职端曾出现 `网络连接失败，请检查网络` toast，仍需手机在线后用 logcat 定位。
- 工作树仍有多轮未提交改动和未跟踪文件，提交前需要确认边界。

### 下一轮建议

1. 重新连接手机。
2. 安装本轮新生成的用户端 / 兼职端 Debug APK。
3. 真机复测用户端登录、首页、发布订单、订单列表、订单结果页。
4. 真机复测兼职端登录、工作台、可接任务、详情 drawer。
5. 若兼职端仍网络失败，用 ADB logcat 定位原因。

### 关键确认

- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- 是否改了后端 Java 代码：否
- frontend build 是否通过：是
- build:android:user:public 是否通过：是
- build:android:parttime:public 是否通过：是
- 双端 APK 是否重新构建：是
- 本轮新 APK 是否已真机复测：是，2026-05-12 手机重新连接后已完成双端安装、启动和登录 smoke

### 2026-05-12 补充

- ADB 设备 `10AE221PGA003Y5` 已重新连接。
- 用户端和兼职端 APK 均已 `adb install -r` 成功。
- 手机到内测公网服务器 ping 成功，真实地址不写入仓库。
- 用户端登录成功并进入首页。
- 兼职端登录成功并进入工作台。
- 兼职端工作台显示 token、资料状态、启用状态和可接任务均正常。
- Step 158 曾出现的兼职端网络失败 toast 本轮未复现。
- 兼职端已通过订单 `CR202605010405291760` 完成接单、取餐、配送和送达。
- 用户端结果页发现真实缺口：`AWAITING_CONFIRMATION` 可回读但缺少确认按钮。
- 已在用户端结果页补回 `确认已收到` 最小承接，复用既有 customer confirm 接口，不新增后端接口。
- 修复后重新构建并安装用户端 APK，真机点击 `确认已收到` 成功，订单回读为 `已完成 / COMPLETED`。
- 关键证据：
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-visible-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/window-user-confirm-visible.xml`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-after-step159.png`
  - `project-logs/campus-relay/runtime/step-159-android-device/window-user-confirm-after.xml`
  - `project-logs/campus-relay/runtime/step-159-android-device/campus-user-confirm-logcat.txt`
- 当前判断：真机公网主链路已闭环到 `COMPLETED`，可以进入 owner-controlled 小范围内测准备；公开公测前仍需提交边界整理、内测说明、release 签名包和弱网 / 后台 / 多账号回归。

### 2026-05-12 继续协作注意

- 不要再把 Step 159 写成“完整真机链路未完成”；真实状态已经变为“主链路已闭环，仍缺内测材料与扩展回归”。
- `frontend/src/api/campus-customer.js` 和 `frontend/src/views/user/CampusOrderResult.vue` 是本轮为了修复确认承接缺口新增的代码改动。
- 下一轮优先级不是继续美化页面，而是整理工作树提交边界、内测说明、Android release / 安装分发准备和补充真机回归矩阵。

## Step 160 协作记录 - 移动端界面文案接地气优化

### 本轮目标

用户反馈双端界面文字有些僵硬，本轮只做可见文案自然化，不重开业务开发。

### 实际改动

- 用户端：首页、登录页、个人中心、发单页、结果页、兼职报名页、售后结果页。
- 兼职端：登录页、资料页、工作台。
- 将 `token / 接口 / 字段 / 回读 / 最小承接 / customer / courier` 等工程语境，尽量替换为“登录、接单资格、订单进度、报名、接单、取餐、送达、上报异常”等普通用户口径。
- 创建 Step 160 日志并更新 summary / pending / file-change-list / global-working-memory。

### 未改动内容

- 未改 bridge。
- 未改 `request.js`。
- 未改 token 附着逻辑。
- 未改路由。
- 未改后端 Java、数据库或接口路径。
- 未删除旧外卖兼容模块。
- 未新增页面。

### 风险

- 本轮未做真机逐页截图复核，后续仍需确认文字在 360px / 390px / 430px 宽度下不截断。
- 代码内部变量和 class 仍保留 `token / courier / customer`，这是实现命名，不代表页面可见文案。

### 验证

- `npm run build` 通过。
- `npm run build:android:user:public` 通过。
- `npm run build:android:parttime:public` 通过。
- `git diff --check` 通过，仅 CRLF 提示。

### 下一轮建议

优先做真机页面文字验收和小范围内测材料整理，不建议继续无目标扩文案或样式。

## Step 161 协作记录 - Android 双端显示名调整

### 本轮目标

把 Android 双端手机桌面显示名称改成更直观的端名：用户端 / 兼职端。

### 实际改动

- 用户端 Capacitor `appName` 改为 `用户端`。
- 用户端 Android `app_name` / `title_activity_main` 改为 `用户端`。
- 兼职端 Capacitor `appName` 改为 `兼职端`。
- 兼职端 Android `app_name` / `title_activity_main` 改为 `兼职端`。
- 通过 Capacitor sync 同步生成 Android assets 下的 `capacitor.config.json`。

### 未改动内容

- 未改真实 `applicationId`：`com.xiaoyu.campus.user` / `com.xiaoyu.campus.parttime`。
- 未改 bridge、`request.js`、token 附着逻辑、路由、后端、接口、管理后台或旧兼容模块。

### 风险

- 手机桌面可能缓存旧显示名；如未刷新，可卸载后重装或等待桌面刷新。
- 不能把真实包名改成中文；中文只能用于显示名。

### 验证

- 双端 `npm run cap:sync:public` 通过。
- 双端 Debug APK 构建通过。
- `aapt dump badging` 确认 APK label 分别为 `用户端` / `兼职端`。
- ADB 设备 `10AE221PGA003Y5` 在线，双端 `adb install -r` 成功。
- 双端均已通过 `adb shell monkey ... LAUNCHER` 启动验证。
- `git diff --check` 通过，仅 CRLF 提示。

### 下一轮建议

先手动看手机桌面显示名是否刷新；随后做一轮 Android 双端真机小回归，再整理 release 签名包和内测分发说明。

## Step 162 协作记录 - GitHub 与内测服务器同步确认

### 本轮目标

把本地最新主线同步到 GitHub 和内测服务器，并用远端 smoke 证明服务器可运行。

### 实际改动

- 本地提交已推送到 `origin/main`。
- 服务器部署目录已同步当前主线代码。
- 部署前已执行服务器备份脚本，生成 MySQL / uploads / `.env` 备份。
- 服务器 Docker Compose 已重建并启动 `mysql / backend / frontend`。
- 远端 smoke 已完成，结果为 25 PASS / 0 FAIL / 0 SKIP。
- 新增 Step 162 日志和远端 smoke 报告。

### 未改动内容

- 未改业务代码。
- 未改 bridge。
- 未改 `request.js`。
- 未改 token 附着逻辑。
- 未改接口、路由或鉴权。
- 未删除旧兼容模块。
- 未提交真实密钥、公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。

### 风险

- 当前仍是 owner-controlled 内测环境，不是公开公测环境。
- HTTPS / 域名 / 证书 / 正式监控告警仍未收口。
- Android release 签名包、安装说明、测试账号说明、反馈模板仍需补齐。

### 下一轮建议

优先补内测分发材料和 Android 双端真机小回归矩阵；不要在这些材料未齐之前扩大到公开公测。

## Step 163 协作记录 - 内测分发材料收口

### 本轮目标

在 Step 162 服务器同步与远端 smoke 通过之后，补齐小范围内测分发材料。

### 实际改动

- 新增 `docs/deployment/internal-trial-distribution-pack.md`。
- 新增 Step 163 日志。
- 更新 summary / pending / file-change-list / global-working-memory。
- 分发文档覆盖 APK 生成、Android 安装、账号私下发放、建议测试链路、已知限制、反馈模板和 owner 发包前检查。

### 未改动内容

- 未改业务代码。
- 未改前端页面。
- 未改后端代码。
- 未改 bridge。
- 未改 `request.js`。
- 未改 token 附着逻辑。
- 未改接口、路由或鉴权。
- 未删除旧兼容模块。
- 未提交账号密码、公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。

### 风险

- 当前材料只支撑 owner-controlled 小范围内测，不支撑公开公测。
- APK 仍是 QA / Debug 内测包，release 签名包未固化。
- HTTPS / 域名 / 证书 / 隐私说明仍未完成。

### 下一轮建议

做 Android 双端真机小回归矩阵；如果通过，再生成一轮 QA APK 分发包并记录 manifest。

## Step 164 协作记录 - Android 双端真机小回归矩阵

### 本轮目标

在真实 Android 设备和内测服务器上做一轮小回归，确认用户端、兼职端和远端管理/API 基线可运行。

### 实际改动

- 修正 Android WebView smoke 脚本旧 Step 元数据，避免生成报告误标。
- 使用 `-ClearData` 跑通 Android 双端 WebView smoke。
- 用户端 smoke 通过，自动化创建订单 `CR202605131052401467`，模拟支付后回读为 `BUILDING_PRIORITY_PENDING / PAID`。
- 兼职端 smoke 通过，资料为 `APPROVED`，可接任务数为 `5`。
- 远端 smoke 通过：25 PASS / 0 FAIL / 0 SKIP。
- 新增 Step 164 日志和 runtime 证据。

### 未改动内容

- 未改业务代码。
- 未改后端接口、数据库、鉴权或路由。
- 未改 bridge。
- 未改 `request.js`。
- 未改 token 附着逻辑。
- 未删除旧兼容模块。
- 未提交真实密钥、公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。

### 风险

- 本轮验证的是小回归矩阵，不是完整公开公测验收。
- 兼职端接单、取餐、送达、异常上报和用户端确认完成尚未在 Step 164 自动化中覆盖。
- APK release 签名包、HTTPS / 域名 / 证书和隐私说明仍未收口。

### 下一轮建议

优先补 Android 双端动作链矩阵；如果按钮级自动化成本高，先做手工矩阵和截图留痕，再进入 QA APK 分发包 manifest。

## Step 165 协作记录 - Python Android 双端动作链矩阵

### 本轮目标

把 Step 164 未覆盖的 Android 双端完整动作链做成可重复执行的 Python 验证脚本。

### 实际改动

- 新增 `scripts/trial-operation/android_action_matrix.py`。
- 新增 Step 165 日志。
- 新增 runtime 证据：
  - `project-logs/campus-relay/runtime/step-165-android-action-matrix/android-action-matrix-report.json`
  - `user-launch.png`
  - `parttime-launch.png`
  - `user-after-action-chain.png`
  - `parttime-after-action-chain.png`
- 更新 summary / pending / file-change-list / global-working-memory。
- 真实手机 `10AE221PGA003Y5` 上跑通动作链。
- 本轮订单：`CR202605131124021644`。
- 最终用户端和兼职端均回读 `COMPLETED`。

### 未改动内容

- 未改业务代码。
- 未改前端页面。
- 未改后端接口、数据库、鉴权或路由。
- 未改 Android 原生配置。
- 未改 bridge。
- 未改 `request.js`。
- 未改 token 附着逻辑。
- 未删除旧兼容模块。
- 未提交真实密钥、公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。

### 风险

- 本轮不是坐标级 UI 点击自动化，而是 ADB 启动/截图 + 公开 API 状态机动作链。
- 当前仍是 owner-controlled 小范围内测准备，不是公开公测完成态。
- QA APK manifest、release 签名、HTTPS / 域名 / 证书和隐私说明仍未完成。

### 下一轮建议

进入 QA APK 分发包 manifest 与安装复核：记录双端 APK 文件名、包名、显示名、版本号、构建时间、对应 Git commit 和安装验证结果。

## Step 166 协作记录 - Android QA APK Manifest 与安装复核

### 本轮目标

基于 Step 165 已通过的动作链，确认当前双端 QA APK 是否可构建、可识别、可安装、可启动，并形成安全版 manifest。

### 实际改动

- 新增 `docs/deployment/android-qa-apk-manifest.md`。
- 新增 Step 166 日志。
- 新增双端安装后启动截图：
  - `project-logs/campus-relay/runtime/step-166-android-qa-apks/user-qa-install-launch.png`
  - `project-logs/campus-relay/runtime/step-166-android-qa-apks/parttime-qa-install-launch.png`
- 更新 `.gitignore`，避免误提交 APK 二进制产物和本地生成 manifest。
- 更新 summary / pending / file-change-list / global-working-memory。
- 使用 `build-android-qa-apks.ps1 -Mode public` 生成双端 Debug QA APK。
- 使用 ADB 将双端 APK 覆盖安装到真机 `10AE221PGA003Y5`，结果均为 `Success`。
- 使用 `aapt dump badging` 核验包名和显示名。

### 未改动内容

- 未改业务代码。
- 未改前端页面。
- 未改后端接口、数据库、鉴权或路由。
- 未改 Android 包名、显示名、版本号或原生配置。
- 未改 bridge。
- 未改 `request.js`。
- 未改 token 附着逻辑。
- 未删除旧兼容模块。
- 未提交 APK 二进制产物。
- 未提交真实密钥、公网地址、服务器密码、GitHub token、腾讯地图 key、测试账号密码或 `.env` 内容。

### 风险

- 当前仍是 Debug QA 包，不是生产 release 包。
- 公开公测前仍需处理 release 签名、HTTPS / 域名 / 证书、cleartext 收口和隐私说明。
- APK 本体只在本地 runtime 目录，不在 Git 仓库长期保存。

### 下一轮建议

进入公开公测前安全与发布缺口收口评估，优先评估 release 签名、HTTPS / 域名 / 证书、隐私说明与内测反馈入口。

## Step 167 协作记录 - 公开公测前安全与发布缺口收口

### 本轮目标

复核公开公测前的安全和发布缺口，并优先落地不依赖外部域名、证书或真实密钥的收口项。

### 实际改动

- 双端 Android Gradle 增加 release 签名配置入口。
- 新增双端 `key.properties.example`。
- `.gitignore` 和双端 Android `.gitignore` 已保护 keystore、jks 和 `key.properties`。
- 双端 Android Manifest 使用 `usesCleartextTraffic` placeholder。
- 双端新增 debug / release 分离的 `network_security_config.xml`。
- Debug 保持 cleartext true，继续支持当前 HTTP 内测环境。
- Release 默认 cleartext false。
- 新增 `docs/deployment/public-beta-release-gap-closure.md`。
- 新增 Step 167 日志。
- 更新 summary / pending / file-change-list / global-working-memory。

### 未改动内容

- 未生成真实 release keystore。
- 未提交任何真实签名文件或密码。
- 未配置真实域名。
- 未申请或提交 HTTPS 证书。
- 未改后端接口。
- 未改前端业务页面。
- 未新增反馈接口。
- 未新增隐私协议页面。
- 未重开 bridge。
- 未删除旧兼容模块。
- 未提交公网 IP、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。

### 验证结果

- 用户端 `:app:assembleDebug :app:assembleRelease` 通过。
- 兼职端 `:app:assembleDebug :app:assembleRelease` 通过。
- 用户端 debug 合并 manifest：`usesCleartextTraffic=true`。
- 用户端 release 合并 manifest：`usesCleartextTraffic=false`。
- 兼职端 debug 合并 manifest：`usesCleartextTraffic=true`。
- 兼职端 release 合并 manifest：`usesCleartextTraffic=false`。

### 风险

- 当前 release 包仍是 unsigned，必须等 owner 本地生成真实 keystore 后才可作为正式分发包。
- 当前没有 HTTPS / 域名 / 证书，不能把 cleartext=false 的 release 包发给公开用户。
- 隐私说明、用户协议和 App 内反馈入口仍未落地。

### 下一轮建议

二选一推进：如果准备公开公测，先处理 HTTPS / 域名 / 证书和真实 release 签名；如果继续小范围内测，先补隐私/用户协议静态页和 App 内反馈入口。

## Step 168 协作记录 - xiaoyu.xin HTTPS / Nginx 443 接入准备

### 本轮目标

把 owner 已确认的 `xiaoyu.xin` 域名接入方案固化为仓库模板和执行说明，先完成 HTTPS / Nginx 443 的配置准备，不在本轮提交任何真实证书或服务器密钥。

### 实际改动

- Docker Compose frontend 端口收口为 `127.0.0.1:18080`。
- `deploy/internal-trial/.env.example` 默认 `FRONTEND_PORT=18080`，CORS 示例改为 `https://xiaoyu.xin`。
- 新增 `deploy/internal-trial/nginx-xiaoyu.xin.conf`。
- 新增 `docs/deployment/xiaoyu-xin-https-runbook.md`。
- 更新 `docs/deployment/internal-trial-compose.md`。
- 更新 `docs/deployment/public-beta-release-gap-closure.md`。
- Android public env 示例切换到 `https://xiaoyu.xin/api`。
- 本地 ignored Android public env 也已同步，但不会提交。
- 新增 Step 168 日志。
- 更新 summary / pending / file-change-list / global-working-memory。

### 未改动内容

- 未申请证书。
- 未提交证书、证书私钥、服务器密码或真实 `.env`。
- 未生成真实 Android release keystore。
- 未改后端业务接口。
- 未改前端业务页面。
- 未改 bridge。
- 未改 `request.js`。
- 未改 token 附着逻辑、路由或鉴权。
- 未删除旧兼容模块。

### 验证结果

- DNS 本地解析：`xiaoyu.xin` 指向当前服务器。
- `npm run build` 通过。
- `npm run build:android:user:public` 通过。
- `npm run build:android:parttime:public` 通过。
- Android public 构建产物已包含 `https://xiaoyu.xin/api`，未命中旧 HTTP 公网 IP。
- 后端 `.\mvnw.cmd -DskipTests compile` 通过。
- `git diff --check` 通过，仅 CRLF 提示。
- 本机无 Docker 命令，未能执行 `docker compose config`。

### 风险

- 服务器尚未实际签发证书。
- Nginx 443 尚未在服务器实操验证。
- Android release 真实签名仍未完成。
- 隐私政策、用户协议和 App 内反馈入口仍未完成。

### 下一轮建议

优先在服务器上实操 Nginx / Certbot / 443 并做远端 smoke；如果暂时不碰服务器，则转入隐私政策、用户协议和 App 内反馈入口。

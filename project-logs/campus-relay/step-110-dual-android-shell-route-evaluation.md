# Step 110 - 用户端 / 兼职端双 Android 壳路线评估

## 本轮目标

1. 基于 Step 109 已形成的 H5 基线，评估用户端 / 兼职端双 Android 壳的可行路线。
2. 明确 WebView、Capacitor、原生 Android 和 PWA 的边界，不直接开写 Android 工程。
3. 固化移动端最小试运营范围、登录态隔离方式和 admin Web-only 边界。
4. 保持 bridge、展示 polish、媒体线和现有 H5 业务语义不变。

## 评估输入

1. 当前前端只有一个 `frontend/` Vite + Vue 代码库，没有 Android 工程、没有 Capacitor、没有 Cordova、没有 PWA 配置。
2. 当前 Web 侧已经具备两条清晰入口：
   - 用户端：`/user/**`
   - 兼职端：`/parttime/**`
3. 当前登录态已经做了最小隔离：
   - `customer_token / customer_user_info`
   - `courier_token / courier_profile`
4. 当前 admin 后台仍然是浏览器 Web 管理端，不存在“admin Android 端”需求。
5. 早期文档中提到过未来 `uni-app/` 占位，但那是 Step 01 阶段的远期占位，不应覆盖当前已经成型的单前端代码结构。

## 当前痛点

1. 现在能在浏览器里完整跑用户端和兼职端，但还不能以 Android App 形态安装和分发。
2. 用户端和兼职端虽然已分路由、分 token，但还没有各自独立的 App 容器、包名、图标和启动页。
3. 如果直接做原生 Android，会把当前已经稳定的 H5 页面群重新复制一遍，成本和风险都过高。
4. 如果继续停留在浏览器 H5，试运营阶段不利于 owner 做“用户 App / 兼职 App”双端体验测试。

## 候选方案比较

### 方案 A：纯 WebView 双壳

优点：

1. 初始开发量最小。
2. 可以快速把现有 H5 页面嵌进 Android。

缺点：

1. Android 容器层要自己处理返回键、文件选择、权限、缓存和升级。
2. 后续如果要接设备能力，纯 WebView 维护成本会很快上升。
3. 两个壳虽然能做，但长期会变成手写宿主逻辑的重复劳动。

结论：

1. 可以作为极短期验证手段。
2. 不适合作为当前项目继续往“可内测试运营”推进的正式路线。

### 方案 B：Capacitor 双壳

优点：

1. 最符合当前单一 Vite 前端代码库现状。
2. 可复用现有 H5 页面、路由、状态管理和接口封装，不重写业务 UI。
3. 后续若要接 Android 常用能力，例如文件、相机、推送、定位、状态栏、启动页，Capacitor 比纯 WebView 更稳。
4. 可以做成两个独立 App：
   - 用户端 App
   - 兼职端 App
5. 两个 App 用不同包名时，各自 WebView 存储天然隔离，和当前 `customer_token / courier_token` 双 key 设计相容。

缺点：

1. 仍然需要补 Android 壳工程、图标、包名、启动页、构建脚本和环境配置。
2. 需要明确“同一前端源码，如何产出两个 App 壳”的构建边界。

结论：

1. 这是当前最平衡、最现实的路线。
2. 推荐作为 Step 111 之后的正式主线。

### 方案 C：完整原生 Android 双端

优点：

1. UI 和系统能力可做到最深。
2. 长期产品化弹性最大。

缺点：

1. 需要重写用户端和兼职端页面群。
2. 会复制已有 H5 业务逻辑、接口调用和状态承接。
3. 当前项目还没到值得付出这一层成本的阶段。

结论：

1. 当前阶段明显过重。
2. 不建议作为下一轮主线。

### 方案 D：PWA

优点：

1. 改动最少。
2. 继续沿用 Web 发布流程。

缺点：

1. 不能满足“两个 Android App”这一目标。
2. 安装、权限、升级和系统集成边界都不够清晰。
3. 对试运营阶段的 App 分发和双端身份区分帮助有限。

结论：

1. 只能作为 Web 补充。
2. 不能替代 Android 双壳路线。

## 本轮结论

1. 当前推荐路线为：**单前端源码 + 双 Capacitor Android 壳**。
2. admin 继续保持 Web-only，不进入 Android。
3. 用户端和兼职端继续共用当前 `frontend/` 业务代码，但由两个 Android 壳分别承载：
   - 用户端壳默认进入 `/user/login`
   - 兼职端壳默认进入 `/parttime/login`
4. 旧 `uni-app/` 占位不再作为当前实施主线；如果未来真的要全面原生化，再单独重评估。

## 推荐的最小试运营范围

### 用户端 App

优先承接：

1. `/user/login`
2. `/user`
3. `/user/orders`
4. `/user/profile`
5. `/user/campus/courier-onboarding`
6. `/user/campus/order-result`

暂不扩展：

1. 新支付 SDK
2. 原生地图导航
3. 推送
4. 第三方登录

### 兼职端 App

优先承接：

1. `/parttime/login`
2. `/parttime/workbench`
3. `/parttime/profile`

暂不扩展：

1. 新订单历史页
2. 独立 Android 原生任务流
3. 原生地图 / 导航页

## 登录态与数据隔离策略

1. 代码层继续保留：
   - `customer_token`
   - `courier_token`
2. Android 容器层通过两个独立包名进一步隔离本地 WebView 存储：
   - 用户端 App 不读取兼职端 WebView 存储
   - 兼职端 App 不读取用户端 WebView 存储
3. 因此即使两端复用同一前端源码，也不会把登录态混在一个 App 容器里。
4. admin token 继续只存在 Web 管理端，不进入 Android 壳设计范围。

## 构建边界建议

1. 保持单一 `frontend/` 源码，不复制第二套前端工程。
2. 后续若进入 Step 111，建议新增移动端壳目录，而不是复制业务页面：
   - 例如 `mobile/user-app/`
   - 例如 `mobile/parttime-app/`
3. 两个壳工程只负责：
   - App 名称
   - 包名
   - 图标 / 启动页
   - 默认入口 URL 或默认首页路由
   - Android 容器配置
4. 业务页面、接口调用、状态管理仍由现有 `frontend/` 维护。

## 与现有后端闭环的关系

1. 不新增 mobile-only 后端接口。
2. 用户端继续复用现有 customer 接口。
3. 兼职端继续复用现有 courier 接口。
4. bridge 主线保持 `Phase A no-op` 冻结态，不因为 Android 壳路线而重开。
5. 当前 H5 闭环仍是 Android 壳的业务基线，不需要先改后端协议。

## 明确不做的范围

1. 本轮不新增 Android 工程。
2. 本轮不安装 Capacitor 依赖。
3. 本轮不做原生页面。
4. 本轮不做 uni-app 初始化。
5. 本轮不做 PWA 改造。
6. 本轮不改 bridge。
7. 本轮不改鉴权。
8. 本轮不改接口。
9. 本轮不改路由语义。
10. 本轮不新增第 5 个 admin 页。

## 风险与控制

1. 风险：两个 App 壳如果共用同一线上入口而没有入口限制，用户可能跨端访问非目标页面。
   - 控制：后续壳工程只定义默认入口，不强行屏蔽现有路由；先保证最小试运营可用。
2. 风险：如果后续需要原生定位、文件上传或通知，纯 WebView 会很快触顶。
   - 控制：直接选 Capacitor，而不是纯 WebView。
3. 风险：过早做完整原生重写会吞掉当前试运营节奏。
   - 控制：明确当前不走原生重写。

## 当前结论

1. Step 110 已把 Android 路线从“待讨论”收敛为“推荐双 Capacitor 壳”。
2. 当前项目仍以 H5 为业务真源，Android 壳只是承载层。
3. bridge 继续保持 `Phase A no-op` 冻结态，没有变化。

## 遗留问题

1. owner 仍需要本地复核 `/parttime/login -> /parttime/workbench -> /parttime/profile` 这条 H5 路径。
2. 当前还没有真正的 Android 壳目录、包名、图标和构建脚本。
3. 需要在 Step 111 明确双壳 scaffold 的最小目录方案，避免一上来复制前端工程。

## 下一步建议

1. Step 111 做“双 Capacitor Android 壳最小 scaffold go / no-go”，只评估和收敛目录与构建边界。
2. 如果 Step 111 边界清楚，再进入最小 Android 壳实现轮。
3. 不建议下一轮回头继续抠 admin 样式，也不建议直接开启完整原生重写。

# 用户端 / 兼职端 Android 壳 scaffold 方案

## 当前结论

当前推荐路线是：单一 `frontend/` 业务源码，两个 Capacitor Android 壳。

两个壳分别是：

1. 用户端 App
   - App 名称：校内兼职
   - 默认入口：`/user/login`
   - 建议包名：`com.xiaoyu.campus.user`
2. 兼职端 App
   - App 名称：校内兼职端
   - 默认入口：`/parttime/login`
   - 建议包名：`com.xiaoyu.campus.parttime`

管理后台继续保持 Web-only，不进入 Android。

## 为什么不直接复制前端工程

当前 `frontend/` 已经同时承载：

1. admin 后台
2. 用户端
3. 兼职端

直接复制两份前端工程会带来三个问题：

1. 页面、接口和状态逻辑会重复维护。
2. bug 修复需要同步到多个工程。
3. 用户端和兼职端的 H5 基线会漂移。

因此 Android 壳只做容器，业务仍以现有 `frontend/` 为真源。

## 已解除的前置关键点

Step 112 之前，Vue Router 根路径 `/` 默认跳转到 `/dashboard`。如果直接把同一个 `frontend/dist` 放进两个 Android 壳，两个 App 首屏都会先进入 admin 默认路径，而不是：

1. 用户端 `/user/login`
2. 兼职端 `/parttime/login`

Step 112 已通过构建目标层解除这个问题：

1. `npm run build` 仍输出 admin Web 构建产物。
2. `npm run build:android:user` 输出用户端移动构建产物。
3. `npm run build:android:parttime` 输出兼职端移动构建产物。

## Step 112 推荐先做的最小准备

在 `frontend/` 中增加构建目标配置，但不改变现有 Web 行为。

建议新增三个目标：

1. `web-admin`
   - 默认入口：`/dashboard`
   - 对应当前浏览器后台行为
2. `android-user`
   - 默认入口：`/user/login`
   - 面向用户端 Android 壳
3. `android-parttime`
   - 默认入口：`/parttime/login`
   - 面向兼职端 Android 壳

实现建议：

1. 通过 `VITE_APP_SHELL` 区分运行壳：
   - `admin`
   - `user`
   - `parttime`
2. 路由 `/` 根据 `VITE_APP_SHELL` 做默认跳转。
3. 现有 `npm run build` 保持 admin / web 默认行为。
4. 新增移动端构建脚本：
   - `npm run build:android:user`
   - `npm run build:android:parttime`
5. 分别输出：
   - `frontend/dist-android-user`
   - `frontend/dist-android-parttime`

## Step 113 已完成的 Capacitor scaffold

Step 113 已新增：

1. `mobile/user-app`
2. `mobile/parttime-app`

当前结构：

```text
mobile/
├── README.md
├── user-app/
│   ├── capacitor.config.json
│   └── android/
└── parttime-app/
    ├── capacitor.config.json
    └── android/
```

壳工程职责：

1. App 名称
2. Android 包名
3. 图标和启动页
4. 指向对应的前端构建产物
5. Android 构建配置

壳工程不负责：

1. 重写业务页面
2. 重写接口调用
3. 改后端协议
4. 改 bridge
5. 改 admin 后台

## 登录态隔离

当前前端已经区分：

1. `customer_token`
2. `courier_token`

后续 Android 侧通过不同包名形成不同 WebView 存储空间：

1. 用户端 App 只保留自己的用户登录态。
2. 兼职端 App 只保留自己的兼职登录态。

因此不需要为了 Android 壳新增一套 token 接口。

## 当前明确不做

1. 不做原生 Android 重写。
2. 不做 uni-app 工程。
3. 不做 PWA 替代方案。
4. 不做 admin Android 端。
5. 不接真实支付、真实退款、真实打款。
6. 不因 Android 壳重开 bridge 主线。

## 推荐推进顺序

1. Step 112：实现前端构建目标层。已完成。
2. Step 113：新增双 Capacitor 壳 scaffold。已完成。
3. Step 114：本地 Android 构建验证。下一步。
4. Step 115：真机或模拟器 smoke。

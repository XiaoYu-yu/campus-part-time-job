# Step 111 - 双 Capacitor Android 壳 scaffold go / no-go

## 本轮目标

1. 基于 Step 110 的路线结论，继续收敛双 Capacitor Android 壳的 scaffold 边界。
2. 明确是否可以直接进入 Android scaffold。
3. 明确 `mobile/` 目录、双包名、默认入口和前端构建产物边界。
4. 本轮不新增 Android 工程、不安装 Capacitor、不改业务代码。

## 当前输入

1. 当前只有一个 `frontend/` Vite + Vue 工程。
2. 当前路由入口已经拆分：
   - 用户端：`/user/**`
   - 兼职端：`/parttime/**`
   - admin：`/dashboard` 等后台路由
3. 当前 `frontend/src/router/index.js` 的根路径 `/` 仍默认跳转到 `/dashboard`。
4. 当前没有 `mobile/` 目录，没有 Capacitor 依赖，没有 Android native 工程。

## scaffold go / no-go 结论

结论：**暂不直接创建 Android scaffold，先进入前端构建目标准备轮。**

原因：

1. 如果直接把当前 `frontend/dist` 放入两个 Android 壳，两个壳都会继承 `/ -> /dashboard` 的默认跳转。
2. 用户端 App 的真实默认入口应该是 `/user/login`。
3. 兼职端 App 的真实默认入口应该是 `/parttime/login`。
4. 这个问题不应该在 Android 原生层硬拦截，否则后续调试和回滚都会变复杂。
5. 更稳妥的方式是在前端保留单源码，同时新增构建目标，让不同壳拿到不同默认入口的构建产物。

## 推荐目录边界

后续真正进入 Android scaffold 时，建议新增：

```text
mobile/
├── README.md
├── user-app/
└── parttime-app/
```

两个 App 的建议边界：

1. 用户端 App
   - App 名称：校内兼职
   - 默认入口：`/user/login`
   - 建议包名：`com.xiaoyu.campus.user`
2. 兼职端 App
   - App 名称：校内兼职端
   - 默认入口：`/parttime/login`
   - 建议包名：`com.xiaoyu.campus.parttime`

admin 继续保持 Web-only，不进入 `mobile/`。

## Step 112 推荐优先实现

Step 112 应优先补前端构建目标层，而不是直接创建 Android 工程。

建议范围：

1. 新增 `VITE_APP_SHELL` 构建变量。
2. 保持默认 Web 构建不变：
   - `npm run build`
   - 默认 `admin`
   - `/` 继续进入 `/dashboard`
3. 新增用户端 Android 构建脚本：
   - `npm run build:android:user`
   - 默认入口 `/user/login`
   - 输出 `dist-android-user`
4. 新增兼职端 Android 构建脚本：
   - `npm run build:android:parttime`
   - 默认入口 `/parttime/login`
   - 输出 `dist-android-parttime`
5. 不改用户端、兼职端、admin 既有路由语义。

## 为什么不在本轮安装 Capacitor

1. 当前还没有移动端专用构建产物。
2. 直接安装和初始化 Capacitor 会产生 native scaffold，但它指向的 Web 产物入口仍不正确。
3. 先补构建目标可以把风险留在 Vue / Vite 层，改动更小，验证也更快。

## 与 Step 110 的关系

Step 110 确定“用双 Capacitor 壳”。

Step 111 进一步明确：

1. 不直接进入 Android scaffold。
2. 先补前端构建目标。
3. 构建目标稳定后再进入双壳 scaffold。

## 明确不做

1. 不新增 Android 工程。
2. 不安装 `@capacitor/*`。
3. 不改 bridge。
4. 不改鉴权。
5. 不改后端接口。
6. 不改 token 附着逻辑。
7. 不改订单状态机。
8. 不新增页面。
9. 不做原生 Android 重写。
10. 不重开 admin 样式 polish。

## 验证结果

1. 本轮仅为文档与方案边界更新。
2. `git diff --check`：通过，仅保留 CRLF 提示。

## 当前 bridge 结论

bridge 继续保持 `Phase A no-op` 冻结态。

本轮没有重开 bridge，也没有因为 Android 壳路线调整 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 或 token 附着策略。

## 下一步建议

1. Step 112 进入“前端 Android 构建目标最小实现”。
2. Step 112 只改 `frontend` 构建目标与 `/` 默认跳转逻辑，不创建 Android 工程。
3. Step 112 通过后，再进入 Step 113 双 Capacitor scaffold。

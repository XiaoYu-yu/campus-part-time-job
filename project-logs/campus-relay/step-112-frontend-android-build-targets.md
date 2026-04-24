# Step 112 - 前端 Android 构建目标最小实现

## 本轮目标

1. 在不创建 Android 工程的前提下，先补齐用户端 / 兼职端 Android 壳需要的前端构建目标。
2. 保持现有 `npm run build` 的 Web/admin 行为不变。
3. 新增两个移动端构建产物：
   - `dist-android-user`
   - `dist-android-parttime`
4. 不改 bridge、不改鉴权、不改后端接口、不改业务页面语义。

## 已完成项

1. `frontend/package.json` 新增构建脚本：
   - `npm run build:android:user`
   - `npm run build:android:parttime`
2. `frontend/vite.config.js` 新增基于 Vite `mode` 的 shell 推导：
   - `android-user` -> `user`
   - `android-parttime` -> `parttime`
   - 默认仍为 `admin`
3. `frontend/src/router/index.js` 将根路径 `/` 的默认跳转改为按 shell 决定：
   - admin / Web：`/dashboard`
   - 用户端 Android：`/user/login`
   - 兼职端 Android：`/parttime/login`
4. `.gitignore` 已忽略移动端构建产物目录：
   - `frontend/dist-android-user/`
   - `frontend/dist-android-parttime/`
5. 没有新增 `.env.android-*` 提交文件。原因是项目 `.gitignore` 默认忽略 `.env.*`，最终采用 `--mode` 推导，避免构建目标依赖被忽略的本地文件。

## 当前构建边界

1. `npm run build`
   - 输出：`frontend/dist`
   - 根路径默认进入：`/dashboard`
   - 用途：Web/admin 后台
2. `npm run build:android:user`
   - 输出：`frontend/dist-android-user`
   - 根路径默认进入：`/user/login`
   - 用途：后续用户端 Android 壳
3. `npm run build:android:parttime`
   - 输出：`frontend/dist-android-parttime`
   - 根路径默认进入：`/parttime/login`
   - 用途：后续兼职端 Android 壳

## 明确没做

1. 没有安装 Capacitor。
2. 没有新增 `mobile/user-app` 或 `mobile/parttime-app`。
3. 没有新增 Android 工程。
4. 没有复制第二套前端工程。
5. 没有改 bridge、鉴权、token 附着逻辑、后端接口或订单状态机。
6. 没有改用户端 / 兼职端页面业务动作。

## 验证结果

1. `frontend`：`npm run build` 通过。
2. `frontend`：`npm run build:android:user` 通过。
3. `frontend`：`npm run build:android:parttime` 通过。
4. `frontend`：`npm run lint` 通过。
5. `frontend`：`npm run test -- text.spec.js` 通过。
6. `backend`：`.\\mvnw.cmd -DskipTests compile` 通过。
7. 根目录：`git diff --check` 通过，仅有 Windows CRLF 提示，无空白错误。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。本轮没有触发恢复推进条件，也没有改 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 或 token 附着逻辑。

## 下一步建议

Step 113 可以进入双 Capacitor Android 壳 scaffold：

1. 新增 `mobile/user-app` 和 `mobile/parttime-app` 的最小壳工程。
2. 两个壳分别指向：
   - `frontend/dist-android-user`
   - `frontend/dist-android-parttime`
3. 继续保持 admin Web-only。
4. 不复制第二套业务前端源码。

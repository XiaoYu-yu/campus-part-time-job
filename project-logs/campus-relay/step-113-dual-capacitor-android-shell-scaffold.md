# Step 113 - 双 Capacitor Android 壳 scaffold

## 本轮目标

1. 基于 Step 112 已完成的前端 Android 构建目标，新增用户端 / 兼职端两个 Capacitor Android 壳。
2. 两个壳继续复用同一份 `frontend/` 业务源码，不复制第二套前端工程。
3. admin 后台继续保持 Web-only。
4. 不改 bridge、不改鉴权、不改接口、不改路由结构、不改业务页面语义。

## 已完成项

1. 新增 `mobile/README.md`，说明双 Android 壳边界与常用命令。
2. 新增用户端壳：
   - 目录：`mobile/user-app`
   - App 名称：`校内兼职`
   - 包名：`com.xiaoyu.campus.user`
   - webDir：`../../frontend/dist-android-user`
3. 新增兼职端壳：
   - 目录：`mobile/parttime-app`
   - App 名称：`校内兼职端`
   - 包名：`com.xiaoyu.campus.parttime`
   - webDir：`../../frontend/dist-android-parttime`
4. 两个壳均安装并锁定 Capacitor `8.3.1`：
   - `@capacitor/core`
   - `@capacitor/android`
   - `@capacitor/cli`
5. 两个壳均已生成 Android 原生工程：
   - `mobile/user-app/android`
   - `mobile/parttime-app/android`
6. 两个壳均已执行 `cap:sync`，能将对应移动端前端构建产物复制进 Android 工程。
7. 已修正 Capacitor 模板测试包名：
   - 用户端测试包名改为 `com.xiaoyu.campus.user`
   - 兼职端测试包名改为 `com.xiaoyu.campus.parttime`

## 构建与同步命令

用户端：

```powershell
cd mobile/user-app
npm install
npm run cap:sync
```

兼职端：

```powershell
cd mobile/parttime-app
npm install
npm run cap:sync
```

## 验证结果

1. `frontend`：`npm run build` 通过。
2. `frontend`：`npm run build:android:user` 通过。
3. `frontend`：`npm run build:android:parttime` 通过。
4. `frontend`：`npm run lint` 通过。
5. `frontend`：`npm run test -- text.spec.js` 通过。
6. `backend`：`.\\mvnw.cmd -DskipTests compile` 通过。
7. `mobile/user-app`：`npm install` 通过，0 vulnerabilities。
8. `mobile/parttime-app`：`npm install` 通过，0 vulnerabilities。
9. `mobile/user-app`：`npm run cap:add:android` 通过。
10. `mobile/parttime-app`：`npm run cap:add:android` 通过。
11. `mobile/user-app`：`npm run cap:sync` 通过。
12. `mobile/parttime-app`：`npm run cap:sync` 通过。
13. `mobile/user-app`：`npx cap doctor android` 通过。
14. `mobile/parttime-app`：`npx cap doctor android` 通过。
15. 根目录：`git diff --check` 通过，仅有 Windows CRLF 提示。

## APK 构建验证情况

已尝试在 `mobile/user-app/android` 执行：

```powershell
.\\gradlew.bat assembleDebug --no-daemon
```

结果：首次构建在 5 分钟超时，没有返回明确编译失败信息；已停止残留 Java 进程。当前不把 APK 构建失败写成代码失败，Step 113 的边界是 scaffold + sync。Step 114 建议专门做 Android 本机构建验证，重点排查 Gradle 首次下载、Android SDK、JDK 和网络环境。

## 明确没做

1. 没有把 admin 后台打包成 Android App。
2. 没有复制第二套前端源码。
3. 没有改 `/user/**`、`/parttime/**` 或 `/courier/**` 业务路由语义。
4. 没有改 bridge、鉴权、token 附着逻辑、后端接口或订单状态机。
5. 没有接真实支付、真实退款、真实打款。
6. 没有定制 App 图标、启动页视觉或签名配置。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。本轮没有触发恢复推进条件，也没有改 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 或 token 附着逻辑。

## 下一步建议

Step 114 建议进入 Android 本地构建验证：

1. 复核 Android SDK / JDK / Gradle 环境。
2. 分别执行：
   - `mobile/user-app/android/.\\gradlew.bat assembleDebug --no-daemon`
   - `mobile/parttime-app/android/.\\gradlew.bat assembleDebug --no-daemon`
3. 若 APK 能生成，再进入模拟器或真机 smoke。
4. 若 APK 构建仍超时，优先处理 Gradle 依赖下载、代理和 SDK 环境，不回头改业务代码。

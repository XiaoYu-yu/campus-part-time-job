# Step 118 - 用户端移动首页真实视觉 smoke 与登录文案修正

## 本轮目标

1. 对 Step 117 的用户端新首页做真实移动视口 smoke。
2. 确认 `/user` 登录后首页、底部导航、结果回看快捷入口和兼职入驻摘要在移动宽度下可读。
3. 如果 smoke 发现明显旧外卖语义或视觉问题，做最小修复。
4. 不改 bridge、鉴权、token 附着、API 调用顺序、后端接口、Android 原生工程或旧外卖模块。

## 执行方式

1. 本地启动 backend test profile：
   - `http://127.0.0.1:8080`
2. 本地启动 frontend dev：
   - `http://127.0.0.1:5173`
3. 使用 Playwright CLI + Edge，在 `390x844` 移动视口执行：
   - 打开 `/user/login`
   - 使用 `13900139000 / 123456` 登录
   - 验证跳转到 `/user`
   - 打开 `/user/campus/order-result?orderId=CR202604060001`
   - 打开 `/user/campus/courier-onboarding`
4. 截图归档：
   - `project-logs/campus-relay/runtime/step-118-user-home-smoke/screenshots/user-login-mobile.png`
   - `project-logs/campus-relay/runtime/step-118-user-home-smoke/screenshots/user-home-after-login-mobile.png`
   - `project-logs/campus-relay/runtime/step-118-user-home-smoke/screenshots/user-order-result-mobile.png`
   - `project-logs/campus-relay/runtime/step-118-user-home-smoke/screenshots/user-onboarding-mobile.png`
5. 结构化验证记录：
   - `project-logs/campus-relay/runtime/step-118-user-home-smoke/user-home-smoke-validation.json`

## smoke 发现

1. `/user` 首页已不再展示旧外卖商品推荐语义。
2. 用户首页、底部导航、结果回看快捷入口和兼职入驻摘要在移动视口可读。
3. `/user/campus/order-result?orderId=CR202604060001` 可回读 completed 订单。
4. `/user/campus/courier-onboarding` 可在移动视口正常展示。
5. 发现 `frontend/src/views/user/Login.vue` 仍保留旧文案：
   - `使用测试用户账号进入点餐端`
   - `登录并开始点餐`
   - 登录成功提示 `欢迎回来，开始点餐吧`

## 实际修复

1. `frontend/src/views/user/Login.vue`
   - 文案改为校园兼职用户端登录：
     - `用户端登录`
     - `进入校园代送结果回看、兼职入驻和旧模块兼容入口。`
     - `登录进入校园兼职`
   - 登录成功提示改为：
     - `欢迎回来，进入校园兼职用户端`
   - 视觉从旧橙色外卖登录页改为浅色校园玻璃风格。

## 明确未改

1. 未改 `frontend/src/utils/request.js`。
2. 未改 token 附着逻辑。
3. 未改 `frontend/src/api/campus-courier.js` 或其它 API 文件运行时行为。
4. 未改路由结构。
5. 未改后端接口、鉴权或订单状态机。
6. 未改 Android 原生工程结构。
7. 未删除旧外卖模块。
8. 未重开 bridge 主线。

## 验证结果

1. Playwright 真实页面 smoke：
   - `/user/login` 通过。
   - `/user` 登录后首页通过。
   - `/user/campus/order-result?orderId=CR202604060001` 通过。
   - `/user/campus/courier-onboarding` 通过。
   - browser console：0 error，0 warning。
   - network：
     - `POST /api/users/login => 200`
     - `GET /api/campus/customer/courier-onboarding/review-status => 200`
     - `GET /api/campus/customer/courier-onboarding/token-eligibility => 200`
2. 构建与检查：
   - `frontend npm run lint` 通过。
   - `frontend npm run build` 通过。
   - `frontend npm run build:android:user` 通过。
   - `frontend npm run build:android:parttime` 通过。
   - `mobile/user-app npm run cap:sync` 通过。
   - `mobile/parttime-app npm run cap:sync` 通过。
   - `mobile/user-app/android .\gradlew.bat assembleDebug --no-daemon` 通过。
   - `mobile/parttime-app/android .\gradlew.bat assembleDebug --no-daemon` 通过。
   - `backend .\mvnw.cmd -DskipTests compile` 通过。
   - `git diff --check` 通过；仅保留 Windows 工作区 LF/CRLF 提示。

## 当前结论

1. 用户端移动首页与登录入口已经从旧外卖第一印象收敛到校园兼职 / 校园代送用户端。
2. 本轮只是视觉 smoke 与最小文案修正，没有修改业务语义。
3. 当前 bridge 仍处于 `Phase A no-op` 冻结态，本轮没有触发恢复推进条件。
4. 展示 polish 主线仍保持冻结 / 维护态；本轮修复属于用户端移动入口产品定位问题，不是重新开启无限 polish。

## 遗留问题

1. Android 真机 / 公网服务器 API base 仍需和本地模拟器 `10.0.2.2` 分层。
2. 用户端已有结果回看、入驻、旧模块兼容入口，但还缺“校园代送下单 / 我的代送单”移动端最小主入口。
3. 本轮没有做 Android WebView 内新首页截图，只做了浏览器真实移动视口 smoke。

## 下一步建议

1. Step 119 优先评估并实现用户端“校园代送下单 / 我的代送单”最小入口。
2. 该入口应复用现有 customer campus 接口，不改订单状态机和模拟支付边界。
3. 同步规划 Android API base 分层：本地模拟器、局域网真机、公网 HTTPS。

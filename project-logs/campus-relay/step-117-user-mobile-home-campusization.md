# Step 117 - 用户端移动首页校园兼职化

## 本轮目标

1. 解决用户端 Android 登录后默认首页仍展示旧外卖商品推荐语义的问题。
2. 将 `/user` 收敛为校园兼职 / 校园代送用户端首页。
3. 保持旧外卖模块可运行，只把旧页面作为兼容入口保留。
4. 不改 bridge、鉴权、token 附着、API 调用顺序、路由结构、后端接口或 Android 原生工程。

## 实际完成

1. `frontend/src/layout/UserLayout.vue`
   - 移除用户端全局布局里的旧外卖店铺状态、购物车数量和商品首页导航语义。
   - 新增“校内兼职 / 校园代送 · 兼职报名 · 结果回看”移动端 shell。
   - 底部导航改为：
     - 首页：`/user`
     - 结果：`/user/campus/order-result`
     - 入驻：`/user/campus/courier-onboarding`
     - 我的：`/user/profile`
   - 继续按 `customer_token` 判断是否显示用户端导航；不影响 `/parttime/**` 与 `/courier/**`。

2. `frontend/src/views/user/Home.vue`
   - 将旧商品推荐首页替换为校园用户端首页。
   - 新增结果回看快捷区，默认示例订单为 `CR202604060001`，跳转到现有 `/user/campus/order-result`。
   - 新增兼职入驻摘要，复用现有：
     - `GET /api/campus/customer/courier-onboarding/review-status`
     - `GET /api/campus/customer/courier-onboarding/token-eligibility`
   - 新增旧外卖兼容入口区，保留访问：
     - `/user/category`
     - `/user/cart`
     - `/user/orders`
     - `/user/profile`

## 明确未改

1. 未删除旧外卖模块。
2. 未改旧 `orders/cart/address` 语义。
3. 未改 bridge：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
4. 未改 `frontend/src/utils/request.js`。
5. 未改 token 附着逻辑。
6. 未改 API 调用顺序。
7. 未改路由结构。
8. 未改后端接口。
9. 未改 Android 原生工程结构。

## 验证结果

1. `frontend`：
   - `npm run build` 通过。
   - `npm run lint` 通过。
   - `npm run build:android:user` 通过。
   - `npm run build:android:parttime` 通过。
2. Capacitor：
   - `mobile/user-app` 执行 `npm run cap:sync` 通过。
   - `mobile/parttime-app` 执行 `npm run cap:sync` 通过。
3. Android：
   - `mobile/user-app/android` 执行 `.\gradlew.bat assembleDebug --no-daemon` 通过。
   - `mobile/parttime-app/android` 执行 `.\gradlew.bat assembleDebug --no-daemon` 通过。
4. backend：
   - `.\mvnw.cmd -DskipTests compile` 通过。
5. 代码检查：
   - `git diff --check` 通过；仅保留 Windows 工作区既有 LF/CRLF 提示。

## 当前结论

1. 用户端登录后的默认首页已经从旧外卖语义改为校园兼职 / 校园代送用户端首页。
2. 旧外卖页面仍可从兼容区进入，旧模块未删除。
3. 当前 bridge 仍处于 `Phase A no-op` 冻结态，本轮没有触发恢复推进条件。
4. 当前 Android 线仍保持“双 Capacitor 壳 + 单前端源码”的路线。

## 遗留问题

1. 本轮未做 Android WebView 新首页截图 smoke；建议 Step 118 先做浏览器移动宽度 / Android 用户端视觉 smoke。
2. 用户端目前已有结果回看、兼职入驻和兼容入口，但仍缺更完整的“校园代送下单 / 我的代送单”移动端主入口。
3. Android 构建专用 API base 仍是本地模拟器配置 `http://10.0.2.2:8080/api`，真机 / 服务器内测需要单独配置。

## 下一步建议

1. Step 118 先做用户端 `/user` 新首页的浏览器移动宽度与 Android WebView 视觉 smoke，确认无旧外卖默认语义、无明显溢出和导航异常。
2. smoke 稳定后，再评估是否补用户端“校园代送下单 / 我的代送单”最小入口。
3. 不建议下一轮重开 bridge、继续机械补 admin 页或接真实支付。

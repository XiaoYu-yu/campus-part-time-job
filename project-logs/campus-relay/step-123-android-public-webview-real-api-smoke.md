# Step 123 - Android public WebView 真实接口 smoke

## 本轮目标

1. 在 Android WebView 内真实验证 public API base，而不是只做主机 curl。
2. 用户端覆盖登录、代送依赖读取、我的代送单、创建订单和 mock-pay。
3. 兼职端覆盖登录、profile / review-status 和可接单列表。
4. 继续不提交真实公网 IP、token、服务器凭据或本地 ignored env。
5. 不改 bridge、鉴权、接口语义、路由、订单状态机或旧外卖模块。

## 实际完成

### 1. 新增 WebView 真实接口 smoke 脚本

新增脚本：

1. `scripts/trial-operation/android-webview-user-public-smoke.ps1`
2. `scripts/trial-operation/android-webview-parttime-public-smoke.ps1`

脚本能力：

1. 可按需启动既有 `campus_api35` 模拟器。
2. 可清理 App data 后启动对应 Android 壳。
3. 通过 Android WebView DevTools / CDP 连接真实 WebView。
4. 通过页面输入与按钮点击完成登录。
5. 在同一个 WebView 上下文中调用 public API base。
6. 报告中的 API base 只保留 `http://<redacted>/api`，不写真实公网 IP。

### 2. 用户端 public WebView smoke 已通过

执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\trial-operation\android-webview-user-public-smoke.ps1 -StartEmulator -ClearData
```

结果：

1. Android 用户端包 `com.xiaoyu.campus.user` 正常启动。
2. WebView 首屏为 `http://localhost/user/login`。
3. 页面真实登录成功，`customer_token` 已写入 WebView localStorage。
4. 同一 WebView 上下文中完成：
   - `GET /api/campus/public/pickup-points`
   - `GET /api/campus/public/delivery-rules`
   - `GET /api/campus/customer/orders`
   - `POST /api/campus/customer/orders`
   - `POST /api/campus/customer/orders/{id}/mock-pay`
   - `GET /api/campus/customer/orders/{id}`
5. 本轮真实创建并模拟支付订单：
   - `CR202604261108119903`
6. 回读结果：
   - `status = BUILDING_PRIORITY_PENDING`
   - `paymentStatus = PAID`

证据：

1. `project-logs/campus-relay/runtime/step-123-android-public-webview/user-public-webview-smoke.json`
2. `project-logs/campus-relay/runtime/step-123-android-public-webview/user-campus-orders-public-webview.png`

### 3. 兼职端 public WebView smoke 已通过

执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\trial-operation\android-webview-parttime-public-smoke.ps1 -StartEmulator -ClearData
```

结果：

1. Android 兼职端包 `com.xiaoyu.campus.parttime` 正常启动。
2. WebView 首屏为 `http://localhost/parttime/login`。
3. 页面真实登录成功，`courier_token` 已写入 WebView localStorage。
4. 同一 WebView 上下文中完成：
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
   - `GET /api/campus/courier/orders/available`
5. 回读结果：
   - `courierProfileId = 2`
   - `reviewStatus = APPROVED`
   - `enabled = 1`
   - `availableOrders = 3`

证据：

1. `project-logs/campus-relay/runtime/step-123-android-public-webview/parttime-public-webview-smoke.json`
2. `project-logs/campus-relay/runtime/step-123-android-public-webview/parttime-workbench-public-webview.png`

## 关键结论

1. public API base 不再只停留在只读 curl smoke。
2. 用户端 Android WebView 已真实验证登录、读取、创建订单和 mock-pay。
3. 兼职端 Android WebView 已真实验证登录、profile、review-status 和可接单列表。
4. 两端 public Android 壳在 WebView 内均能通过公网 API base 完成核心最小链路。
5. 当前仍使用 HTTP public API，若要邀请真实内测用户，应优先进入 HTTPS / 域名 / 证书 / Android cleartext 收口。

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 token 附着逻辑。
4. 未改后端接口、鉴权、订单状态机或 settlement。
5. 未改 Vue 页面。
6. 未改 Android 原生壳结构。
7. 未接真实支付、真实退款或真实打款。
8. 未提交真实公网 IP、token、服务器密码或密钥。

## 验证结果

1. `android-webview-user-public-smoke.ps1 -StartEmulator -ClearData`
   - 通过。
2. `android-webview-parttime-public-smoke.ps1 -StartEmulator -ClearData`
   - 通过。
3. 用户端证据 JSON：
   - `passed = true`
   - `createdOrderId = CR202604261108119903`
   - `finalPaymentStatus = PAID`
4. 兼职端证据 JSON：
   - `passed = true`
   - `courierProfileId = 2`
   - `reviewStatus = APPROVED`
   - `availableOrders = 3`

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. `/api/campus/courier/profile` 继续保留。
2. `/api/campus/courier/review-status` 继续保留。
3. `customer_token -> bridge -> courier 前置读取` 继续保留观察态。
4. 本轮没有任何 bridge 删除、鉴权收紧或 token 附着改动。

## 遗留问题

1. public API 仍为 HTTP，不适合正式开放给真实用户长期使用。
2. LAN 真机 smoke 仍未执行；当前证据来自 Android 模拟器 public WebView。
3. Step 123 两个 WebView smoke 目前是手工脚本入口，尚未纳入 preflight 或 CI。

## 下一步建议

1. Step 124 优先进入 Android public WebView 稳定性复核 / 试运营 readiness 判断。
2. 评估是否把两个 WebView smoke 脚本文档化到试运营 preflight。
3. 若准备真实内测，优先做 HTTPS / 域名 / 证书和 Android cleartext 收口方案。
4. 暂不继续扩业务页面、bridge、admin 页面或真实支付能力。

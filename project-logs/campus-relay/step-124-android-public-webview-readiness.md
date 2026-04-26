# Step 124 - Android public WebView readiness 复核与试运营入口固化

## 本轮目标

1. 复用 Step 123 的 user / parttime Android public WebView smoke，验证两条链路可以重复执行。
2. 新增一个聚合入口，避免试运营前分别手工跑两个脚本。
3. 将 Android public WebView smoke 写入试运营 preflight / runbook。
4. 明确当前 HTTP public API 只适合 owner-controlled smoke；若邀请外部内测用户，下一步应优先做 HTTPS / 域名 / 证书 / Android cleartext 收口。
5. 不改 bridge、鉴权、接口语义、路由、订单状态机、Vue 页面或旧外卖模块。

## 实际完成

### 1. 新增聚合 smoke 脚本

新增：

```text
scripts/trial-operation/android-webview-public-smoke.ps1
```

脚本能力：

1. 顺序调用用户端 `android-webview-user-public-smoke.ps1`。
2. 顺序调用兼职端 `android-webview-parttime-public-smoke.ps1`。
3. 支持 `-StartEmulator`、`-ClearData`、`-DeviceId`、`-AdbPath`、端口与等待时间参数。
4. 子报告继续脱敏 public API base。
5. 输出聚合报告到：

```text
project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/android-public-webview-readiness-summary.json
```

### 2. 实际复跑结果

执行：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\trial-operation\android-webview-public-smoke.ps1 -StartEmulator -ClearData
```

最终结果：

1. 聚合结果：`passed = true`
2. 用户端：
   - `passed = true`
   - 本轮创建订单：`CR202604261141588261`
   - 最终订单状态：`BUILDING_PRIORITY_PENDING`
   - 最终支付状态：`PAID`
3. 兼职端：
   - `passed = true`
   - `courierProfileId = 2`
   - `reviewStatus = APPROVED`
   - `availableOrders = 4`

证据：

1. `project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/android-public-webview-readiness-summary.json`
2. `project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/user-public-webview-smoke.json`
3. `project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/user-campus-orders-public-webview.png`
4. `project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/parttime-public-webview-smoke.json`
5. `project-logs/campus-relay/runtime/step-124-android-public-webview-readiness/parttime-workbench-public-webview.png`

### 3. 脚本修正说明

首次聚合运行时，两个子 smoke 实际都通过，但 PowerShell 将子脚本 stdout 混入聚合结果数组，导致 wrapper 误判 `passed = false`。

已修正为：

1. 子进程输出只写到控制台。
2. 聚合 `checks` 只保留结构化结果对象。
3. `adb monkey` 的 stderr 输出不再中断 wrapper。

修正后已重新复跑并通过。

### 4. Runbook / preflight 固化

已更新：

1. `scripts/trial-operation/README.md`
2. `docs/trial-operation-preflight.md`

新增推荐命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-webview-public-smoke.ps1 -StartEmulator -ClearData
```

试运营口径：

1. 该脚本可作为 owner-controlled Android public WebView smoke / handoff 前验收入口。
2. 真实 API base 继续通过 ignored env 文件注入。
3. 报告只提交脱敏 API base。
4. 若准备邀请外部用户安装 APK，不应继续依赖 HTTP cleartext，应先完成域名、HTTPS 证书、反向代理和 Android cleartext 收口。

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 token 附着逻辑。
4. 未改 API 调用顺序。
5. 未改后端接口、鉴权、订单状态机或 settlement。
6. 未改 Vue 页面。
7. 未改 Android 原生壳结构。
8. 未接真实支付、真实退款或真实打款。
9. 未提交真实公网 IP、token、服务器密码或密钥。

## 验证结果

1. `android-webview-public-smoke.ps1 -StartEmulator -ClearData`
   - 通过。
2. 用户端子报告：
   - `passed = true`
   - `createdOrderId = CR202604261141588261`
   - `finalPaymentStatus = PAID`
3. 兼职端子报告：
   - `passed = true`
   - `courierProfileId = 2`
   - `reviewStatus = APPROVED`
   - `availableOrders = 4`

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. `/api/campus/courier/profile` 继续保留。
2. `/api/campus/courier/review-status` 继续保留。
3. `customer_token -> bridge -> courier 前置读取` 继续保留观察态。
4. 本轮没有任何 bridge 删除、鉴权收紧或 token 附着改动。

## 遗留问题

1. 当前 public API 仍是 HTTP，只适合 owner-controlled smoke，不适合外部用户长期内测。
2. LAN 真机 smoke 仍未执行；当前证据来自 Android 模拟器 public WebView。
3. Android public WebView smoke 暂不纳入 CI，因为需要本机模拟器、APK 和 WebView DevTools 环境。

## 下一步建议

1. Step 125 若继续 Android / 内测线，优先做 HTTPS / 域名 / 证书 / Android cleartext 收口方案设计。
2. 若暂不邀请外部用户，当前 Android public WebView readiness 已足够作为本机 / owner-controlled 内测验收入口。
3. 不建议继续扩业务页、第五个 admin 页、bridge 收口或真实支付能力。

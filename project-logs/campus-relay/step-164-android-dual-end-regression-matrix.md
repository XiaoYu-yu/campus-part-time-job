# Step 164 - Android 双端真机小回归矩阵

## 本轮目标

在 Step 163 已补齐内测分发材料后，用真实 Android 设备和内测服务器做一轮可留痕的小回归，确认用户端、兼职端和远端管理/API 基线仍可运行。

本轮重点是验证内测前的真实运行面，不做新功能开发。

## 本轮实际范围

### 已执行

1. 确认 ADB 真机在线。
2. 修正 Android WebView smoke 脚本中的旧 Step 元数据：
   - `android-webview-public-smoke.ps1`
   - `android-webview-user-public-smoke.ps1`
   - `android-webview-parttime-public-smoke.ps1`
3. 使用 `-ClearData` 在真实手机上执行 Android 双端 WebView smoke。
4. 执行内测服务器远端 smoke，覆盖管理端、用户端、兼职端和部分 campus API。
5. 记录真实截图、JSON 报告和本轮结论。

### 未执行

1. 未重新构建 APK。
2. 未改业务代码。
3. 未改后端接口、数据库、鉴权、路由或 token 附着逻辑。
4. 未执行真实第三方支付、退款或打款。
5. 未做公开公测级 HTTPS / 域名 / 证书验证。

## Android 双端 WebView smoke

执行命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-webview-public-smoke.ps1 -DeviceId 10AE221PGA003Y5 -ClearData -OutputDirectory project-logs\campus-relay\runtime\step-164-android-regression-clear -WaitTimeoutSeconds 120 -LaunchWaitSeconds 10
```

结果：通过。

证据文件：

- `project-logs/campus-relay/runtime/step-164-android-regression-clear/android-public-webview-readiness-summary.json`
- `project-logs/campus-relay/runtime/step-164-android-regression-clear/user-public-webview-smoke.json`
- `project-logs/campus-relay/runtime/step-164-android-regression-clear/user-campus-orders-public-webview.png`
- `project-logs/campus-relay/runtime/step-164-android-regression-clear/parttime-public-webview-smoke.json`
- `project-logs/campus-relay/runtime/step-164-android-regression-clear/parttime-workbench-public-webview.png`

### 用户端结果

自动化覆盖：

1. 启动用户端 Android App。
2. 清空 App 数据后进入登录页。
3. 通过可见登录页完成登录。
4. 在 WebView 上下文内读取取餐点。
5. 在 WebView 上下文内读取配送规则。
6. 读取用户订单列表。
7. 创建校园代送订单。
8. 执行模拟支付。
9. 回读订单详情。

关键结果：

- `passed = true`
- 创建订单号：`CR202605131052401467`
- 最终订单状态：`BUILDING_PRIORITY_PENDING`
- 支付状态：`PAID`

### 兼职端结果

自动化覆盖：

1. 启动兼职端 Android App。
2. 清空 App 数据后进入登录页。
3. 通过可见登录页完成登录。
4. 在 WebView 上下文内读取兼职资料。
5. 读取审核状态。
6. 读取可接任务列表。

关键结果：

- `passed = true`
- `courierProfileId = 2`
- 审核状态：`APPROVED`
- 可接任务数：`5`

## 远端 smoke

执行命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase <redacted> -FrontendBase <redacted> -OutputPath project-logs\campus-relay\runtime\step-164-android-regression-clear\remote-smoke-report.json
```

结果：

- PASS：25
- FAIL：0
- SKIP：0

证据文件：

- `project-logs/campus-relay/runtime/step-164-android-regression-clear/remote-smoke-report.json`

覆盖摘要：

1. public health。
2. admin login。
3. customer login。
4. parttime token。
5. admin employee list。
6. admin settlements list / summary。
7. admin after-sale executions。
8. admin couriers list。
9. admin exception history list。
10. customer onboarding / token eligibility。
11. parttime profile / review-status / available orders。
12. 前端 admin / user / parttime shell。

## 首次失败说明

本轮首次执行未加 `-ClearData` 时，用户端脚本停在登录表单查找阶段。复查截图与 XML 后确认：用户端 App 当时已经处于已登录首页状态，脚本期望登录页输入框，因此是自动化前置状态不一致，不是产品链路失败。

该失败产物目录已删除，未作为本轮通过证据提交。后续已使用 `-ClearData` 重新执行并通过。

## 本轮顺手修正的测试脚本问题

Android WebView smoke 系列脚本原本在默认输出目录和 JSON 字段中硬编码旧 Step 编号。为避免后续证据误标，本轮将其改为中性名称：

- 默认 runtime 目录从旧 step 命名改为 `android-public-webview...`
- JSON 字段从 `step = Step 123/124` 改为 `name = Android public WebView ...`
- 手机临时截图文件名从旧 step 命名改为 Android WebView smoke 中性命名

这些改动只影响测试脚本和报告元数据，不影响业务代码。

## 当前仍未完整自动化的事项

1. 兼职端在真机上完成接单、取餐、送达、异常上报的按钮级自动化。
2. 用户端对本轮新建订单执行确认收货并回读 `COMPLETED`。
3. Android 双端在弱网、后台切换、长时间停留后的稳定性。
4. 管理端只做了远端 shell/API smoke，未做浏览器 UI 级完整人工巡检。
5. APK 仍是内测调试链路，公开分发前还需要 release 签名包、HTTPS / 域名 / 证书和最小隐私说明。

## 当前结论

本轮 Android 双端自动化小回归和远端 smoke 均通过，当前已经足够支撑 owner-controlled 小范围内测继续推进。

但这还不是公开公测就绪。公开公测前至少还需要补齐：

1. Android 兼职端动作链自动化或人工矩阵。
2. 用户端确认完成链路的本轮新订单回归。
3. release 签名包和分发记录。
4. HTTPS / 域名 / 证书。
5. 隐私说明、反馈入口和最低限度运维告警。

## 当前 bridge 结论

bridge 仍保持 `Phase A no-op` 冻结态。本轮没有删除 bridge，没有修改 `/api/campus/courier/profile` 或 `/api/campus/courier/review-status`，没有改 `request.js`、token 附着逻辑、鉴权或旧兼容模块。

## 下一轮建议

Step 165 建议进入 Android 双端动作链补测：

1. 以本轮创建订单或新建订单为基础，补兼职端接单、取餐、送达、异常上报的真机验证。
2. 补用户端确认完成和 completed 回读。
3. 若按钮级自动化成本高，先形成手工矩阵和截图证据，再决定是否扩展脚本。
4. 通过后再生成 QA APK 分发包 manifest。

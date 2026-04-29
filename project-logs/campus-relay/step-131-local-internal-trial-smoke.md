# Step 131 - 本地/内测型试运营 smoke 复核

## 本轮目标

在 Step 128-130 完成管理后台视觉与可见问题收口后，本轮不再继续样式优化，转向本地/内测型试运营 smoke：

1. 复核构建、H2 样本和本地运行前置条件。
2. 启动 `test` profile 后端，复核 8080 / 5173 本地入口。
3. 覆盖 admin / customer / parttime 三类账号登录和关键只读接口。
4. 不改 bridge、鉴权、接口、路由、后端业务或 token 附着逻辑。

## 实际执行

### 1. 构建与样本 preflight

执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation -RunBackendCompile -RunFrontendBuild
```

结果：

1. backend compile 通过。
2. frontend build 通过。
3. H2 必需样本通过：
   - admin 账号。
   - customer 账号。
   - courier / parttime 账号。
   - 可接单订单 `CR202604070002`。
   - completed 订单 `CR202604060001`。
   - settlement、location、exception history schema、after-sale history schema、reconcile schema。
4. 仍有 3 个预期 warning：
   - after-sale 固定样本未预置。
   - exception 固定样本未预置。
   - reconcile difference 固定样本未预置。
5. 这些 warning 属于运行期样本缺口，不是构建或启动硬失败。

### 2. 本地运行态

1. frontend dev server：`127.0.0.1:5173` 已可访问。
2. backend test profile：已启动在 `127.0.0.1:8080`。
3. 后端运行日志：
   - `project-logs/campus-relay/runtime/step-131-local-smoke/backend.out.log`
   - `project-logs/campus-relay/runtime/step-131-local-smoke/backend.err.log`

### 3. API smoke

新增并执行：

```powershell
powershell -ExecutionPolicy Bypass -File project-logs\campus-relay\runtime\step-131-local-smoke\api-smoke.ps1
```

报告：

```text
project-logs/campus-relay/runtime/step-131-local-smoke/api-smoke-report.json
```

结果：16 项通过，0 项失败。

覆盖项：

1. admin login。
2. customer login。
3. parttime token。
4. admin employee list。
5. admin settlements list。
6. admin settlement summary。
7. admin after-sale executions。
8. admin couriers list。
9. admin exception history list。
10. customer onboarding profile。
11. customer token eligibility。
12. customer completed order detail。
13. parttime profile。
14. parttime review status。
15. parttime available orders。
16. parttime completed order detail。

### 4. 前端入口 shell smoke

执行本地 SPA shell reachability 检查，以下入口均返回 200 且包含 Vue app root：

1. `/dashboard`
2. `/employee`
3. `/campus/settlements`
4. `/campus/after-sale-executions`
5. `/campus/exceptions`
6. `/user/campus/order-result?orderId=CR202604060001`
7. `/parttime/workbench`

报告：

```text
project-logs/campus-relay/runtime/step-131-local-smoke/frontend-shell-report.json
```

说明：本项只证明 dev server 可返回 SPA shell；认证后的数据可用性以 API smoke 为准。

## 工具限制

尝试使用 Playwright 浏览器脚本做页面截图级 smoke 时，本机 `npx -p playwright` 未能把 `playwright` 包暴露给外部 `.mjs` 脚本，`@playwright/cli` 命令流也存在参数解析不稳定问题。因此本轮不把浏览器自动截图作为通过证据，只记录为工具限制。

## 明确未改

1. 未改 bridge。
2. 未改 `request.js`。
3. 未改 token 附着逻辑。
4. 未改后端接口、数据库、鉴权或路由语义。
5. 未删除旧兼容模块。
6. 未新增页面。
7. 未继续样式重构。

## 当前判断

当前本地/内测型试运营的基础状态是可用的：

1. 构建可通过。
2. H2 必需样本存在。
3. test profile 后端可启动。
4. 前端 dev server 可访问。
5. 三类身份登录和关键接口 smoke 通过。

仍需注意：

1. after-sale / exception / reconcile difference 固定运行期样本不是预置硬样本。
2. 如果要做完整真实浏览器截图级 smoke，需要先修正或替换当前 Playwright CLI 工具链。
3. 如果进入服务器内测，还需要单独做部署环境 smoke，不应拿本地 smoke 直接替代服务器验证。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

## 下一轮建议

Step 132 建议二选一：

1. 若继续本地内测：补一个可重复的浏览器 smoke 工具链，避免每次靠人工点页面。
2. 若准备服务器内测：先整理部署配置与服务器 smoke 清单，再做远端环境验证。

## Step 132 跟进结果

已选择方向 1，并补齐可重复执行的本地浏览器 smoke 工具链：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\browser-smoke.ps1
```

该脚本通过真实登录接口获取 token，使用浏览器 CLI 写入 localStorage，覆盖 7 个关键页面并保存截图报告。Step 131 中记录的 Playwright CLI 参数问题已确认是调用方式问题，后续统一使用 `npx --yes --package @playwright/cli -- playwright-cli ...`。

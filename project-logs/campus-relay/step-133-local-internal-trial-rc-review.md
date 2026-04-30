# Step 133 - 本地内测 RC 状态复盘

## 本轮目标

基于 Step 131 API smoke 与 Step 132 浏览器 smoke，本轮只做本地/内测型试运营 RC 状态复盘：

1. 汇总当前可重复验证的本地基线。
2. 判断本地/内测型试运营是否已经具备可反复验证能力。
3. 明确当前仍缺什么、哪些不影响本地 RC、哪些进入下一轮。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 当前可重复验证基线

### 1. 构建与样本基线

Step 131 已执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation -RunBackendCompile -RunFrontendBuild
```

结果：

1. backend compile 通过。
2. frontend build 通过。
3. H2 必需样本存在：
   - admin 账号。
   - customer 账号。
   - parttime / courier 账号。
   - 可接单订单 `CR202604070002`。
   - completed 订单 `CR202604060001`。
   - settlement、location、exception history、after-sale history、reconcile schema。
4. 仍有 3 个预期 warning：
   - after-sale 固定样本未预置。
   - exception 固定样本未预置。
   - reconcile difference 固定样本未预置。
5. 这些 warning 不影响当前本地 RC 的主链路验证。

### 2. API smoke 基线

报告：

```text
project-logs/campus-relay/runtime/step-131-local-smoke/api-smoke-report.json
```

结果：

1. 16 项通过。
2. 0 项失败。
3. 覆盖 admin / customer / parttime 三类身份。

覆盖范围：

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

### 3. 浏览器 smoke 基线

脚本：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\browser-smoke.ps1
```

报告：

```text
project-logs/campus-relay/runtime/step-132-browser-smoke/browser-smoke-report.json
```

结果：

1. 7 项通过。
2. 0 项失败。
3. 每项均保存 full-page 截图。
4. token 已脱敏，不写入报告。

覆盖页面：

1. `/dashboard`
2. `/employee`
3. `/campus/settlements`
4. `/campus/after-sale-executions`
5. `/campus/exceptions`
6. `/user/campus/order-result?orderId=CR202604060001`
7. `/parttime/workbench`

截图目录：

```text
project-logs/campus-relay/runtime/step-132-browser-smoke/
```

## 本地内测 RC 判断

当前本地/内测型试运营已经具备“可反复验证”的 RC 基线：

1. 有构建/编译/样本 preflight。
2. 有 API 级 smoke。
3. 有浏览器页面级 smoke。
4. 有截图证据。
5. 有固定本地命令入口。
6. 有明确的 bridge 冻结边界。
7. 有明确的旧兼容模块保留边界。

这意味着后续如果改前端样式、修 bug 或补小功能，可以先跑：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation -RunBackendCompile -RunFrontendBuild
powershell -ExecutionPolicy Bypass -File project-logs\campus-relay\runtime\step-131-local-smoke\api-smoke.ps1
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\browser-smoke.ps1
git diff --check
```

若上述通过，即可认为本地核心链路没有被明显破坏。

## 当前仍存在的非阻塞缺口

1. after-sale 固定样本仍不是 H2 预置强样本。
2. exception 固定样本和 reconcile difference 固定样本仍主要依赖运行期生成或单独验证。
3. 浏览器 smoke 目前覆盖关键入口渲染与截图，不覆盖页面内每个按钮动作。
4. Android 真机不方便时，Android WebView smoke 仍依赖模拟器和已配置的 Android 环境。
5. 服务器内测 smoke 需要单独复核，不应直接用本地 smoke 替代远端验证。

这些缺口不阻塞当前本地 RC，但会影响更正式的服务器内测或外部用户试用。

## 下一步判断

当前不建议继续凭感觉扩大样式重构，也不建议马上删除旧兼容模块。

更合适的下一步是二选一：

1. 若继续本地完善：只修用户实际指出的前端 bug，并每次跑 Step 131/132 smoke。
2. 若转服务器内测：先确认服务器部署状态，再按本地 RC 基线设计远端 smoke，不要直接复用本地结论。

## 明确未改

1. 未改 bridge。
2. 未改 `request.js`。
3. 未改 token 附着逻辑。
4. 未改 `campus-*` API 文件运行时行为。
5. 未改路由。
6. 未改后端接口、数据库、状态机或鉴权。
7. 未新增页面。
8. 未删除旧兼容模块。
9. 未提交真实密钥、公网 IP、服务器密码、GitHub token 或腾讯地图 key。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

## Step 134 建议

建议进入“远端/服务器内测 smoke 准备”或“真实前端 bug 修补”二选一：

1. 如果 owner 要开始服务器内测，Step 134 优先整理远端部署 smoke 命令、环境变量和端口检查，不直接改业务代码。
2. 如果 owner 继续本地体验，Step 134 只修真实截图或手动体验中发现的页面问题，不再大范围重做视觉体系。

## Step 134 跟进结果

Step 134 已选择服务器内测 smoke 准备方向，并完成：

1. 新增 `scripts/trial-operation/remote-smoke.ps1`，支持远端 API / SPA shell smoke。
2. 新增 `docs/deployment/remote-internal-trial-smoke.md`。
3. 同步 `scripts/trial-operation/README.md`、`commands.ps1`、`post-deploy-smoke-checklist.md` 和 `internal-trial-ops-runbook.md`。
4. 报告默认脱敏 host，不输出 token，不提交真实公网 IP、服务器密码或地图 key。
5. 本轮仍没有改 bridge、`request.js`、API 运行时行为、路由、后端业务或旧兼容模块。

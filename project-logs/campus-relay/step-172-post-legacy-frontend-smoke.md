# Step 172 - 旧前端入口移除后的本地 smoke 复核

## 本轮目标

Step 171 已经移除旧外卖前端可见入口和旧前端页面。本轮不继续删除后端旧模块，先做本地 smoke，确认 admin / 用户端 / 兼职端主链路没有被前端去旧误伤。

## 执行范围

1. 启动本地 backend `test` profile。
2. 启动本地 frontend dev server。
3. 检查 8080 / 5173 端口可达。
4. 执行 API + SPA shell smoke。
5. 执行浏览器截图 smoke。
6. 处理 smoke 暴露的最小前端稳健性问题。
7. smoke 后关闭本轮启动的本地服务。

## 本地服务状态

1. 启动前：
   - `127.0.0.1:8080` 不可达。
   - `127.0.0.1:5173` 不可达。
2. 启动后：
   - `127.0.0.1:8080` 可达。
   - `127.0.0.1:5173` 可达。
3. smoke 后：
   - 已关闭本轮启动的 backend / frontend 进程。
   - 端口恢复不可达，符合预期。

## API + SPA shell smoke 结果

报告文件：

- `project-logs/campus-relay/runtime/step-172-legacy-frontend-smoke/local-remote-smoke.json`

结果：

- PASS：25
- FAIL：0
- SKIP：0

覆盖项：

1. public health。
2. admin 登录。
3. customer 登录。
4. parttime token。
5. admin employee list。
6. admin settlements list。
7. admin settlement summary。
8. admin after-sale executions。
9. admin couriers list。
10. admin exception history list。
11. customer onboarding profile。
12. customer token eligibility。
13. customer completed order detail。
14. parttime profile。
15. parttime review status。
16. parttime available orders。
17. parttime completed order detail。
18. frontend shell home。
19. frontend shell admin login。
20. frontend shell admin dashboard。
21. frontend shell admin settlements。
22. frontend shell admin after-sale。
23. frontend shell admin exceptions。
24. frontend shell customer result。
25. frontend shell parttime workbench。

说明：

1. 报告中的 URL 已脱敏。
2. token 未写入报告。
3. 该 smoke 证明前端 shell 与核心 API 在旧前端入口移除后仍可访问。

## 浏览器截图 smoke 结果

脚本：

- `scripts/trial-operation/browser-smoke.ps1`

结果：

1. 报告文件：
   - `project-logs/campus-relay/runtime/step-172-legacy-frontend-smoke/browser-smoke-report.json`
2. 结果：
   - PASS：7
   - FAIL：0
3. 已生成 7 个页面截图：
   - `admin-dashboard.png`
   - `admin-employee.png`
   - `admin-settlements.png`
   - `admin-after-sale-executions.png`
   - `admin-exceptions.png`
   - `customer-order-result.png`
   - `parttime-workbench.png`
4. 首次命令等待时出现外层超时，但后台脚本最终生成完整 report，以上 7 项均为 PASS。
5. 完整通过依据：
   - API + SPA shell smoke：25 PASS / 0 FAIL。
   - 浏览器截图 smoke：7 PASS / 0 FAIL。

## 额外修复

浏览器 smoke 期间 Vite 日志暴露 `customer_user_info` malformed JSON 时会导致 customer store 初始化报错。本轮做了一个最小稳健性修复：

1. `frontend/src/stores/customer.js` 新增安全解析函数。
2. 如果 `customer_user_info` 不是合法 JSON，自动移除该 localStorage 项并回退到空用户对象。
3. 不改变登录、登出、token、接口或路由语义。

## 旧入口移除后的结论

1. admin 关键 API 和 SPA shell 正常。
2. 用户端关键 API 和 SPA shell 正常。
3. 兼职端关键 API 和 SPA shell 正常。
4. 浏览器截图 smoke 覆盖 5 个 admin 页面、customer 结果页和 parttime workbench。
5. Step 171 删除的旧前端页面 / 旧 API wrapper 未在 `frontend/src` 中发现残留引用。
6. 后端旧模块仍未删除，后续如继续去旧，必须进入后端依赖审计。

## 明确未做

1. 未继续删除后端旧模块。
2. 未删除旧数据库表。
3. 未改 bridge。
4. 未改 `request.js`。
5. 未改 token 附着逻辑。
6. 未改鉴权。
7. 未生成 release keystore。
8. 未构建正式 release 签名包。
9. 未提交真实密钥、证书、服务器凭据、GitHub token、腾讯地图 key 或 `.env`。

## 当前风险

1. 浏览器截图 smoke 脚本耗时偏长，后续可考虑拆分 admin / user / parttime 三段执行。
2. 后端旧模块仍存在，前端去旧不等于后端已可删除。
3. 真实 release 签名 APK 仍未完成，公开公测前仍是阻断项。

## 下一轮建议

1. 如果继续去旧：进入旧后端模块删除前依赖审计，按 `setmeal / dish / category / shop / order` 单模块推进。
2. 如果转向公测准备：优先生成真实 release keystore，并构建用户端 / 兼职端 release APK 做真机 smoke。
3. 浏览器截图 smoke 可优化为分段脚本，避免一次跑完导致超时。

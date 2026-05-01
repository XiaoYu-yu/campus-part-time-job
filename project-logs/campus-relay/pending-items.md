# 校园代送待处理事项

## 下一步待处理 / 建议

1. Step 150 已完成内测试运行状态总收口：
   - Step 142 到 Step 149 的运维、内测说明和反馈规则已覆盖。
   - 当前已具备 owner-controlled 内测条件。
   - 当前不建议继续无反馈地新增文档、脚本或功能。
2. 下一步建议：
   - 先发放少量测试账号。
   - 按 `docs/deployment/internal-trial-user-test-guide.md` 控制测试范围。
   - 按 `docs/deployment/internal-trial-feedback-triage.md` 记录真实反馈。
   - 只修阻塞 / 主要问题。
3. 如果必须继续推进工程事项，优先级为：
   - owner 在云控制台限制 SSH `22` 来源 IP。
   - 服务器目录 fast-forward 到最新文档 / 脚本提交，但不重建容器。
   - HTTPS / 域名 / 证书专项。
4. 当前仍未处理但不阻塞 owner-controlled 内测：
   - 当前没有 HTTPS、域名、证书、正式监控告警。
   - SSH `22` 当前可达，长期内测建议在云安全组限制来源 IP。
   - password login 仍保留，关闭前必须确认 key 登录和安全组回滚路径。
5. 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不删除旧兼容模块。
   - 不提交真实密钥、公网 IP、服务器密码、GitHub token 或腾讯地图 key。

## Step 141 历史待处理记录

1. Step 140 已完成 backend health endpoint 最小实现：
   - 新增 `GET /api/campus/public/health`。
   - 接口只返回 `status/service/checkedAt`。
   - 不读取用户、订单、资金、地图或数据库数据。
   - 不改 `JwtInterceptor`，复用既有 `/api/campus/public/**` 公开前缀。
   - remote smoke 已新增 `public health` 检查。
2. Step 140 已完成本地与服务器验证：
   - `.\mvnw.cmd -DskipTests compile` 通过。
   - `npm run build` 通过。
   - `commands.ps1` 可执行。
   - 服务器已备份、拉取最新代码并重建 compose。
   - 服务器 health 返回 `code=200`、`status=UP`。
   - 新版远端 smoke：25 项通过、0 项失败、0 项跳过。
3. Step 141 建议二选一：
   - A. 固化服务器最小日志留存 / 轮转策略说明，避免后续内测日志无限增长或定位困难。
   - B. 固化 SSH `22` 安全组来源限制操作清单，由 owner 在云控制台手动执行。
4. Step 141 已选择 A 并完成：
   - Compose 中 `mysql / backend / frontend` 统一启用 Docker `json-file` 日志轮转。
   - 新增日志留存文档和部署后检查入口。

## Step 140 历史待处理记录

1. Step 139 已完成单机内测安全边界固化：
   - 新增 `docs/deployment/internal-trial-security-boundary.md`。
   - 明确业务公网入口只走 frontend `80`。
   - backend `8080` 和 MySQL `3306` 仅绑定 `127.0.0.1`。
   - SSH `22` 仅作为运维入口，建议在云安全组限制来源 IP。
   - 已同步 runbook、部署后 smoke checklist、远端 smoke 文档和命令索引。
2. Step 139 已完成真实边界复核：
   - 服务器监听显示 `22 / 80` 为公网监听。
   - 服务器监听显示 `8080 / 3306` 仅为 `127.0.0.1`。
   - 本地公网探测显示 `22 / 80` 可达，`8080 / 3306` 不可达。
3. Step 140 建议：
   - 先评估是否新增一个无业务副作用的最小健康检查接口。
   - 如果新增，应只表达应用存活，不读取用户、订单、资金或地图数据。
   - 如果不新增，应继续明确 remote smoke 是部署后健康证据。
4. Step 140 已完成本地最小实现：
   - 新增 `GET /api/campus/public/health`。
   - 接口只返回 `status/service/checkedAt`。
   - 不读取用户、订单、资金、地图或数据库数据。
   - 不改 `JwtInterceptor`，复用既有 `/api/campus/public/**` 公开前缀。
   - remote smoke 已新增 `public health` 检查。
5. Step 140 本地验证已完成：
   - `.\mvnw.cmd -DskipTests compile` 通过。
   - `npm run build` 通过。
   - `commands.ps1` 可执行。

## Step 139 历史待处理记录

1. Step 138 已完成内测服务器端口边界与备份告警加固：
   - `mysql` 端口改为 `127.0.0.1:${MYSQL_PORT:-3306}:3306`。
   - `backend` 端口改为 `127.0.0.1:${BACKEND_PORT:-8080}:8080`。
   - frontend 继续对外开放 80，并通过 nginx 代理 `/api`。
   - `backup-stack.sh` 增加 `--no-tablespaces`。
   - 远端 smoke 文档推荐入口切换为 `http://your-host/api`。
2. Step 138 已完成真实服务器验证：
   - 服务器已拉取最新代码并重建 compose。
   - 公网 `80` 可访问。
   - 公网 `8080` 不可访问。
   - 公网 `3306` 不可访问。
   - 通过 nginx `/api` 入口远端 smoke：24 项通过、0 项失败、0 项跳过。
   - 最新备份已通过非破坏性 restore drill，恢复出 7 笔订单，关键订单 `CR202604070002` 和 `CR202604060001` 均存在。
3. Step 139 建议二选一：
   - A. 固化单机内测安全组 / 防火墙说明，明确服务器侧只开放 80，其余端口仅本机或 SSH tunnel 诊断。
   - B. 评估是否补一个最小 backend health endpoint，用于替代对业务登录接口的存活探测。

## Step 138 历史待处理记录

1. Step 137 已完成 GitHub / 服务器同步与远端 smoke 复核：
   - 本地 `main` 已推送到 GitHub。
   - 服务器已从 `1a2329e` 更新到 `3bf59cb`。
   - 更新前已执行 `backup-stack.sh`。
   - 更新后已重建 compose，backend / frontend / mysql 均为 running。
   - 已配置本机专用 SSH key 免密登录，私钥未进入仓库。
   - 远端 smoke 24 项通过、0 项失败、0 项跳过。
   - 最新备份已通过非破坏性 restore drill。
2. Step 138 建议：
   - 收紧公网端口暴露。
   - 保留 frontend 80 作为默认公网入口。
   - backend 8080 与 MySQL 3306 仅绑定服务器本机。
   - 远端 smoke 改为通过 nginx `/api` 访问。
   - 修正 MySQL 8 `mysqldump` tablespace 权限 warning。

## Step 137 历史待处理记录

1. Step 136 已完成服务器内测运维检查与恢复演练：
   - 服务器部署目录存在，工作区干净。
   - compose 中 backend / frontend / mysql 均为 Up。
   - 服务器本机前端和后端基础访问可用。
   - `backup-stack.sh` 已成功生成 MySQL dump、uploads 归档、env 备份和 manifest。
   - `restore-drill.sh` 已成功完成非破坏性恢复演练，关键订单 `CR202604070002` 和 `CR202604060001` 均存在。
   - 日志查看流程可用。
2. Step 137 建议：
   - 先把本地领先提交推送到 GitHub。
   - 服务器先备份，再 `git pull --ff-only origin main`，再重建 compose。
   - 重建后重新执行 `remote-smoke.ps1`。
   - 评估是否先收紧公网端口，至少不要长期公网暴露 mysql 3306。

## Step 136 历史待处理记录

1. Step 135 已完成远端内测 smoke 真实验证：
   - 使用 `scripts/trial-operation/remote-smoke.ps1` 对 owner 提供的内测服务器完成 API + frontend shell smoke。
   - 24 项通过，0 项失败，0 项跳过。
   - 报告路径：`project-logs/campus-relay/runtime/step-135-remote-smoke/remote-smoke-report.json`。
   - 报告默认脱敏 host 和 endpoint，未提交真实公网 IP、服务器密码、token 或腾讯地图 key。
2. Step 136 建议二选一：
   - A. 若继续验证服务器真实体验：执行远端浏览器截图 smoke，证据保存在未跟踪目录或确保脱敏后再归档。
   - B. 若进入内测运维加固：检查服务器 compose 状态、备份脚本、restore drill 和日志查看流程是否可执行。
3. Step 136 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧兼容模块。
   - 不提交真实密钥、公网 IP、服务器密码、GitHub token 或腾讯地图 key。

## Step 135 历史待处理记录

1. Step 134 已完成远端/服务器内测 smoke 准备：
   - 新增 `scripts/trial-operation/remote-smoke.ps1`。
   - 新增 `docs/deployment/remote-internal-trial-smoke.md`。
   - `scripts/trial-operation/README.md` 与 `commands.ps1` 已补远端 smoke 命令。
   - `post-deploy-smoke-checklist.md` 与 `internal-trial-ops-runbook.md` 已补远端 smoke 入口。
   - 报告默认脱敏 host，不写真实公网 IP、服务器密码、token 或腾讯地图 key。
2. Step 135 建议：
   - 如果服务器已部署并启动，使用真实服务器地址手工执行 `remote-smoke.ps1`，但不要提交未脱敏报告。
   - 如果服务器 smoke 失败，优先修部署、端口、profile、CORS、seed 或静态资源 fallback。
   - 如果服务器尚未稳定部署，先按 `docs/deployment/internal-trial-ops-runbook.md` 完成启动、备份和状态检查。
3. Step 135 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧兼容模块。
   - 不提交真实密钥、公网 IP、服务器密码、GitHub token 或腾讯地图 key。

## Step 134 历史待处理记录

1. Step 133 已完成本地内测 RC 状态复盘：
   - Step 131 API smoke：16 项通过，0 项失败。
   - Step 132 浏览器 smoke：7 项通过，0 项失败，并保存 7 张关键页面截图。
   - 当前本地/内测型试运营已具备构建、API、页面截图三层可重复验证基线。
2. Step 134 建议二选一：
   - A. 如果准备服务器内测：整理远端部署 smoke 命令、环境变量、端口检查和远端页面/API 验证清单。
   - B. 如果继续本地体验：只修真实体验中发现的前端 bug，不再凭感觉继续大范围改样式。
3. Step 134 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧兼容模块。

## Step 133 历史待处理记录

1. Step 132 已完成稳定浏览器 smoke 工具链：
   - 新增 `scripts/trial-operation/browser-smoke.ps1`。
   - 已通过真实登录接口获取 admin / customer / parttime token，报告中不输出 token。
   - 已覆盖 `/dashboard`、`/employee`、`/campus/settlements`、`/campus/after-sale-executions`、`/campus/exceptions`、`/user/campus/order-result?orderId=CR202604060001`、`/parttime/workbench` 7 个关键入口。
   - 浏览器 smoke 7 项通过，0 项失败。
   - 截图与报告已写入 `project-logs/campus-relay/runtime/step-132-browser-smoke/`。
2. Step 133 建议先做本地内测 RC 状态复盘：
   - 汇总 Step 131 API smoke 与 Step 132 浏览器 smoke 覆盖范围。
   - 判断当前本地/内测型试运营是否已具备可反复验证基线。
   - 如果继续开发，优先只处理真实用户指出的前端 bug 或试运营阻塞，不再凭感觉继续大改样式。
3. Step 133 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧兼容模块。

## Step 132 历史待处理记录

1. Step 131 已完成本地/内测型试运营 smoke：
   - `preflight.ps1 -RunSampleValidation -RunBackendCompile -RunFrontendBuild` 通过，无硬失败。
   - H2 必需样本存在；after-sale / exception / reconcile difference 固定运行期样本仍为 warning。
   - backend test profile 已在 `127.0.0.1:8080` 启动。
   - frontend dev server 已在 `127.0.0.1:5173` 可访问。
   - API smoke 16 项通过，0 项失败，覆盖 admin / customer / parttime 三类身份和关键接口。
   - SPA shell reachability 7 项通过。
2. Step 132 建议二选一：
   - A. 若继续本地内测：补一个可重复的浏览器 smoke 工具链，避免每次靠人工点页面。
   - B. 若准备服务器内测：先整理部署配置与服务器 smoke 清单，再做远端环境验证。
3. Step 132 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧兼容模块。

## Step 131 历史待处理记录

1. Step 130 已完成管理后台最终可见问题巡检与低风险修补：
   - 已清理 `frontend/src` 范围内剩余可见“外卖”等旧词残留。
   - 已移除 `BaseTable.vue` 的操作列 `fixed="right"`，避免后续复用时重新出现右侧固定列遮挡。
   - 已确认 `frontend/src` 范围内不再命中 `苍穹 / 外卖 / 旧店铺 / 骑手 / 销售额 / 总销售额 / fixed="right"`。
   - `npm run build`、`.\mvnw.cmd -DskipTests compile`、`git diff --check` 均通过。
2. Step 131 不再默认继续样式优化：
   - 如果用户指出具体页面 bug，则只做 bug 级修补。
   - 如果进入试运营准备，优先复核本地运行、账号、样本数据和关键 smoke。
   - 如果继续功能开发，优先评估 admin 异常历史/处理前端承接是否值得做。
3. Step 131 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧兼容模块。

## Step 130 历史待处理记录

1. Step 129 已完成管理后台可用性巡检与明显样式问题修复：
   - breadcrumb 首页入口已补充“运营总览”文字，后台子页面返回主页更明确。
   - 已取消后台所有 `fixed="right"` 操作列，避免固定列阴影/遮罩造成按钮覆盖或右侧视觉异常。
   - 已强化 Element Plus 表格 resize 句柄隐藏和表格按钮间距。
   - 已强化 `.campus-admin-page` 的表格、筛选区和分页区横向约束。
   - `npm run build` 通过，`git diff --check` 通过。
2. Step 130 不建议继续凭感觉大改后台样式：
   - 如果用户提供完整后台原型图，优先选择 `Dashboard.vue` 或 `Employee.vue` 做第一张样板页。
   - 如果没有新原型图，只做 bug 级前端修复，不继续扩大视觉重构范围。
3. Step 130 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧外卖兼容模块。

## Step 129 历史待处理记录

1. Step 128 已完成管理后台视觉基线修正：
   - 修复 `MainLayout.vue` 侧边栏折叠外层 class 未同步问题。
   - Element Plus 全局切换中文 locale，分页不再显示英文文案。
   - 表格列拖拽视觉条、固定列背景和 danger plain 按钮状态已做全局修正。
   - `.campus-admin-page` 已补统一浅色蓝白玻璃态页面基线。
   - `MainLayout.vue` 已按原型图方向调整为蓝白后台壳子：白底侧栏、蓝色 active、顶部搜索占位和轻量用户区。
   - `npm run build` 通过，`git diff --check` 通过。
2. Step 129 建议继续只围绕管理后台做单页精修，不铺开重做：
   - A. `Dashboard.vue`：后台首屏，继续对齐原型图的卡片密度和数据层级。
   - B. `Employee.vue`：基础表格和操作按钮细节，继续按原型图校准筛选区与表格区。
   - C. `CampusExceptionOpsView.vue`：新异常闭环能力页，适合后续做单页原型化。
3. Step 129 继续禁止：
   - 不改 bridge。
   - 不改 `request.js`。
   - 不改 token 附着逻辑。
   - 不改后端接口和路由。
   - 不删除旧外卖兼容模块。

## Step 128 历史待处理记录

1. Step 127 已完成用户端 + 兼职端移动入口视觉统一：
   - 用户端 5 个页面：Login/Home/CampusRelayOrders/CampusOrderResult/CourierOnboarding
   - 兼职端 3 个页面：Login/CourierWorkbench/Profile
   - 全部统一为浅色玻璃态 + campus teal 色系
   - 关键旧词替换：”外卖内容”→”代送内容”、”兼职配送入驻”→”校园兼职入驻”
   - 新增 `step-127-mobile-user-parttime-visual-unification.md`
   - 更新 `summary.md`、`pending-items.md`、`file-change-list.md`、`agent-collaboration.md`
   - **本轮未改任何业务行为、API、路由语义、鉴权或后端代码**
2. Step 128 候选方向：
   - A. 继续视觉统一：处理旧兼容模块页面（Category/Dish/Setmeal/Order/ShopStatus）的移动端适配
   - B. 功能开发：评估”用户端自助售后申请”的最小实现
   - C. 维护态：当前项目已达到三端视觉统一的稳定状态，可暂停推进
   - D. 清理残留文件：HelloWorld.vue / ComponentDemo.vue / vue.svg / vite.svg（低优先级）
3. Step 128 不进入 Phase 3（后端旧模块删除）。
4. bridge 主线继续保持 `Phase A no-op` 冻结态。
8. Step 77 已完成“试运营交付文档一致性复核 / 最小 preflight 验证”：
   - 已复核 `README.md`、`docs/delivery-guide.md`、`docs/trial-operation-preflight.md`、`docs/simulated-funds-boundary.md` 与 `summary.md` 的交付口径，docs 范围内未发现“地图 SDK 未接入 / 真实支付已接入 / 真实退款已接入 / 真实打款已接入 / bridge 已可删除”等过期表述。
   - 已通过 backend `.\mvnw.cmd -DskipTests compile`。
   - 已通过 frontend `npm run build`，仅保留既有 Sass `@import` 弃用告警和 Vite chunk size 告警。
   - 已在本地完成最小 preflight：customer 登录后可访问 `/user/campus/courier-onboarding` 与 `/user/campus/order-result?orderId=CR202604060001`；`/courier/workbench` 能稳定展示无 `courier_token` 提示；admin `/campus/settlements`、`/campus/after-sale-executions`、`/campus/courier-ops`、`/campus/exceptions` 页面可正常打开。
   - 本轮没有改业务代码、后端接口、路由、鉴权、bridge 或前端页面。
9. Step 78 已完成“试运营交付包 RC 收口复盘 / 最小 smoke 复核”：
   - 已基于 Step 40 到 Step 42 的交付文档、样本索引、截图、录屏和 Step 77 的 preflight 结果完成 RC 复盘。
   - 已确认 `project-logs/campus-relay/runtime/step-42-media/screenshots`、`videos`、`logs` 目录存在，截图 15 张、录屏 5 段，可直接用于答辩与交接。
   - 已确认 frontend `5173` 与 backend `8080` 本地端口在 smoke 复核时可访问。
   - 当前项目已经达到“可演示、可移交、可答辩、可复盘”的试运营 RC 状态。
   - 当前仍保留的非阻塞缺口只有：
     - `application-test.properties` 默认 `server.port=0`
     - frontend build 仍有 Sass `@import` 与 Vite chunk size 告警
     - after-sale 固定真实样本仍不是交付必需项
10. Step 79 已完成“试运营 RC 运行配置与构建告警减噪”：
   - `backend/src/main/resources/application-test.properties` 默认端口已改为 `SERVER_PORT:8080`，`test profile + H2` 现在可以直接用于浏览器联调，同时保留环境变量覆盖能力。
   - `frontend/src/styles/global.scss` 与 `frontend/src/styles/element-plus.scss` 已从 Sass `@import` 迁移到 `@use`。
   - `docs/trial-operation-preflight.md` 已同步更新 test profile 默认端口与 `SERVER_PORT` 覆盖说明。
   - backend compile、frontend build、H2/test 启动和最小运行验证已完成；frontend build 不再出现 Sass `@import` 弃用告警，仅保留 Vite chunk size 告警。
   - 本轮没有改 bridge、接口、路由、鉴权、token 附着、地图代码或业务页面语义。
11. Step 80 已完成“前端打包告警与分包 go / no-go 评估”：
   - 已确认 chunk 告警的主要来源不是路由未懒加载，而是 `Dashboard.vue` 与 `Statistics.vue` 两处整包引入 `echarts`，以及当前全局 `ElementPlus` 运行时 vendor 包体。
   - 已新增 `frontend/src/utils/echarts.js`，将图表依赖收口为按需注册的 `Line / Bar / Pie + Tooltip / Legend / Grid + CanvasRenderer` 最小集合。
   - `frontend/src/views/Dashboard.vue` 与 `frontend/src/views/Statistics.vue` 已切到共享按需 `echarts` 入口，图表共享 chunk 从约 `1.11 MB` 收敛到约 `545 KB`。
   - 已在 `frontend/vite.config.js` 中将 `build.chunkSizeWarningLimit` 调整到 `1100`，与当前试运营 RC 接受的 `ElementPlus` 全局 vendor 基线对齐；`npm run build` 不再输出 Vite chunk size 告警。
   - 本轮没有改 bridge、接口、路由、鉴权、token 附着或业务语义。
12. Step 81 已完成“前端打包线 freeze / no-op 复盘”：
   - 已确认 Step 80 后当前显性工程噪音已收敛：Sass `@import` 告警已清除，Vite chunk warning 已清除。
   - 已确认剩余大包主要来自当前全局 `ElementPlus` vendor 基线，而不是路由未懒加载或图表整包误用。
   - 已判断当前不值得继续做高风险全局 `ElementPlus` 按需拆分；前端打包优化线正式进入冻结 / no-op。
   - 仅在出现真实性能信号、交付压力或全局组件装配重构时，才重新打开该主线。
13. Step 82 已完成“试运营 RC 下一阶段主线重排 / go-no-go 评估”：
   - 已对 3 条候选方向完成对比：
     - A. 试运营运维化最小能力
     - B. 非 bridge 后端继续深化
     - C. 产品级上线前方案设计
   - 最终选择 A 作为下一条真实主线。
   - 选择原因：当前 bridge、展示 polish、媒体、地图、前端打包五条线都已冻结或收住，最有价值的增量不再是继续补功能，而是把“本地试运营如何稳定启动、重置、预检、复跑”从纯文档操作收敛为更可执行的最小运维化能力。
   - 方向 B 当前收益低于 A，因为异常 / 售后 / settlement 三条后端线已经达到可演示、可交接、可验证状态，再继续深化会提前进入复杂化。
   - 方向 C 当前过早，因为当前项目没有真实部署面，也不准备马上做真实支付、真实打款、真实监控和生产级安全加固。
14. Step 83 已完成“试运营运维化最小能力边界收敛 / preflight 脚本入口”：
   - 新增 `scripts/trial-operation/preflight.ps1` 和 `scripts/trial-operation/README.md`。
   - `preflight.ps1` 已收敛关键文件检查、本地地图 key 变量检查、`.env.local` git 跟踪检查、可选端口检查、可选 backend compile 和 frontend build。
   - 脚本不自动重置 H2、不启动长驻进程、不打印真实地图 key、不修改 bridge、接口、路由、鉴权或业务页面。
15. Step 84 已完成“试运营样本状态校验脚本”：
   - 新增 `scripts/trial-operation/validate-samples.ps1`。
   - `validate-samples.ps1` 只读校验 `schema-h2.sql` 与 `data-h2.sql` 中的关键演示锚点，不连接数据库、不写入样本、不重置 H2。
   - `preflight.ps1` 新增 `-RunSampleValidation` 参数，并把样本校验的 warning 识别为 warning 而非硬失败。
   - 当前必需样本锚点通过；after-sale / exception / reconcile difference 固定样本未预置，按运行态样本 warning 如实保留。
16. Step 85 已完成“试运营命令索引与手动 H2 reset 指南”：
   - 新增 `scripts/trial-operation/commands.ps1`。
   - 该脚本只输出常用命令和页面入口，不启动长驻进程、不 kill 进程、不自动 reset H2。
   - H2 reset 口径固定为停止 backend test-profile 进程后重新启动，由 `schema-h2.sql` 与 `data-h2.sql` 重新加载。
17. Step 86 已完成“试运营脚本线收口 / no-op 冻结判断”：
   - `preflight.ps1`、`validate-samples.ps1` 和 `commands.ps1` 已覆盖当前本地试运营最小运维化入口。
   - 当前不继续补 runtime smoke、自动 H2 reset 或长驻进程管理。
   - 试运营脚本线进入 no-op 冻结 / 维护态，只有出现 CI 化、部署 smoke、多人试运营或真实重复手工失误时才重开。
18. Step 87 建议转入“产品级试运营前剩余差距清单 / go-no-go 评估”：
   - 不再默认继续补脚本、页面 polish、地图、媒体、前端打包或 bridge。
   - 从产品级试运营角度梳理仍缺的安全、配置、部署、监控、备份、模拟资金边界和真实公司能力替代项。
   - 若目标仍是本地答辩交付，可保持当前 RC + 脚本线 no-op 状态，不进入重开发。
19. Step 87 已完成“产品级试运营前剩余差距清单 / go-no-go 评估”：
   - 当前本地答辩 / 交付 RC 已足够。
   - 产品级试运营仍缺环境变量样例、密钥注入边界、部署前 preflight、CI、监控、备份和外部服务降级策略。
   - 下一主线建议优先进入试运营环境与密钥配置硬化，不再默认继续扩业务功能或脚本。
20. Step 88 建议进入“试运营环境与密钥配置硬化 / deployment preflight 准备”：
   - 优先补 `.env.example` 或等价配置说明。
   - 明确 backend / frontend 试运营环境变量清单。
   - 明确腾讯地图 key 的本地与部署注入边界，继续不提交真实 key。
   - 补部署前配置核对清单，不做真实部署、不改业务代码。
21. Step 88 已完成“试运营环境与密钥配置硬化 / deployment preflight 准备”：
   - 新增 `.env.example`、`backend/.env.example`、`frontend/.env.example`。
   - 新增 `docs/deployment/env-and-secret-checklist.md`。
   - `.gitignore` 已继续忽略真实 `.env` / `.env.*`，但允许提交 example 文件。
   - 本轮没有提交真实腾讯地图 key、数据库密码或 JWT secret。
22. Step 89 建议继续沿产品级试运营准备线推进：
   - 若偏部署准备，补一份部署后 smoke checklist 或 deployment preflight 文档。
   - 若偏 CI 准备，设计最小 CI 检查边界：backend compile、frontend build、sample validation。
   - 不重开 bridge、页面 polish、地图扩展或真实支付接入。
23. Step 89 已完成“部署后 smoke checklist / deployment preflight 文档”：
   - 新增 `docs/deployment/post-deploy-smoke-checklist.md`。
   - 覆盖部署产物、基础访问、customer / courier / admin smoke、模拟资金口径、bridge 冻结口径和回滚触发。
   - 本轮没有新增脚本、没有改业务代码、bridge、接口、路由、鉴权或前端页面。
24. Step 90 建议进入“最小 CI 检查边界设计 / go-no-go”：
   - 只评估 CI 是否值得做。
   - 最小 CI 候选只覆盖 backend compile、frontend build、sample validation。
   - 不接真实部署流水线，不重开 bridge、页面 polish、地图扩展或真实支付接入。
25. Step 90 已完成“最小 CI 检查边界设计 / go-no-go”：
   - 已确认当前仓库有 GitHub issue/PR 模板，但尚无 `.github/workflows`。
   - 已新增 `docs/deployment/ci-check-boundary.md`。
   - 结论是建议 Step 91 进入最小 GitHub Actions CI 实现。
26. Step 91 建议进入“最小 GitHub Actions CI 实现”：
   - 新增 `.github/workflows/trial-operation-ci.yml`。
   - 只覆盖 backend compile、frontend build、sample validation。
   - 不做部署、不跑浏览器 E2E、不注入真实密钥。
   - bridge、展示 polish、地图、媒体、前端打包和真实支付线继续冻结 / 收住。
27. Step 91 已完成“最小 GitHub Actions CI 实现”：
   - 新增 `.github/workflows/trial-operation-ci.yml`。
   - 包含 backend compile、frontend build、trial sample validation 三个 job。
   - sample validation 的 warning exit code `2` 按 warning 处理，不使 CI 失败。
   - 本轮没有做部署、没有注入真实密钥、没有跑 E2E。
28. Step 92 建议做“CI 首轮运行结果跟踪 / 本地与远端一致性复核”：
   - 若 push 成功，观察 GitHub Actions 首次运行。
   - 若 CI 失败，只修 workflow 或环境差异，不扩业务功能。
   - 若 push 仍失败，先解决 GitHub 443 网络或远端同步问题，不继续堆积 CI 相关提交。
29. Step 92 已完成“CI 首轮运行结果跟踪 / 本地与远端一致性复核”：
   - `Trial Operation CI #1` 已由 `450e823ec35f22dba9463c71e1b85b854b1aa6e5` 的 push 触发。
   - `Backend compile`、`Frontend build`、`Trial sample validation` 三个 job 均成功。
   - 本地等价验证与远端 CI 结果一致。
   - 当前仅观察到 GitHub Actions Node.js runtime deprecation warning，不影响 CI 绿色结果。
30. Step 93 建议做“GitHub Actions runtime warning 处理 go / no-go”：
   - 先核查 `actions/checkout`、`actions/setup-java`、`actions/setup-node` 当前官方推荐版本。
   - 如果存在明确的新 runtime 兼容版本，再做只改 action 版本的最小变更。
   - 如果 warning 短期不阻塞试运营，可记录为非阻塞观察项，不扩业务功能。
   - bridge、展示 polish、地图、媒体、前端打包、真实支付和部署自动化线继续冻结 / 收住。
31. Step 93 已完成“GitHub Actions runtime warning 处理 go / no-go”：
   - 已核查官方 action README。
   - 已将 `actions/checkout@v4` 升级到 `actions/checkout@v6`。
   - 已将 `actions/setup-java@v4` 升级到 `actions/setup-java@v5`。
   - 已将 `actions/setup-node@v4` 升级到 `actions/setup-node@v6`。
   - CI job、触发条件、命令、Java 17 / Node 20 构建版本、sample validation warning-only 策略均保持不变。
32. Step 94 已完成“CI action 版本升级后远端运行结果跟踪”：
   - `2406c1b2586996a1e0fdea1946394022894b3b0e` 对应的 `Trial Operation CI #2` 已远端成功。
   - `Backend compile`、`Frontend build`、`Trial sample validation` 三个 job 均继续通过。
   - 最新 run 摘要页未再观察到显性 runtime deprecation warning 文本。
33. Step 95 已完成“内测型试运营 Compose 部署包最小实现”：
   - 已新增 `deploy/internal-trial/docker-compose.yml`。
   - 已新增前后端 Dockerfile、Nginx 代理配置、compose `.env.example` 和部署说明。
   - 已通过 backend package、frontend build、sample validation 和 `git diff --check`。
   - 当前机器未安装 Docker，因此容器级运行验证后置到服务器首轮部署。
34. Step 96 已完成“单机服务器首轮内测部署与 smoke 验证”：
   - 已在服务器安装 Docker / Docker Compose。
   - 已按 `deploy/internal-trial/.env.example` 生成真实 `.env`。
   - 已完成 compose 启动并修正 MySQL 8.4 参数兼容问题与 `backend/db/init.sql` 种子漂移问题。
   - 已通过公网验证 admin / customer / courier 三类最小 smoke。
   - 下一轮不再重复首轮部署，转入最小运维加固与回滚 / 备份准备。
35. Step 97 已完成“全局工作记忆日志建立”：
   - 已新增 `project-logs/campus-relay/global-working-memory.md`。
   - 已把当前仍生效的冻结主线、部署状态、下一主线和恢复工作入口从 `summary.md` 中抽离。
   - 后续如遇上下文压缩，优先先读 global working memory，再读 pending 与最新 step 日志。
12. Step 40 已完成交付整理与演示脚本固化：
   - 当前交付边界。
   - 主演示脚本。
   - 演示账号与样本数据索引。
   - 页面展示清单。
   - 风险与答辩口径。
13. Step 41 已完成交付材料补完：
   - 截图清单。
   - 录屏顺序。
   - 演示前检查 checklist。
14. Step 42 已完成真实媒体采集与归档：
   - 已采集 15 张真实截图，其中 13 张主交付截图、2 张异常后 confirm 失败留痕。
   - 已采集 5 段真实录屏，其中 4 段主交付录屏、1 段异常后 confirm 失败留痕。
   - 媒体目录：`project-logs/campus-relay/runtime/step-42-media/`。
15. Step 43 已选择路径 B：
   - 不补固定 after-sale 样本。
   - 不继续补媒体。
   - 正式收住媒体线。
   - 完成 3 个非 bridge 后端方向评估。
16. Step 44 已完成异常历史与处理闭环最小方案设计：
   - 建议新增 `campus_exception_record`。
   - 继续保留 `campus_relay_order.exception_type / exception_remark / exception_reported_at` 作为 latest exception 兼容摘要。
   - 复用现有 courier `exception-report` 入口。
   - 最小状态集建议为 `REPORTED / RESOLVED`。
   - admin 侧先做历史分页和详情只读，再评估最小 resolve。
17. Step 45A 已完成异常历史最小实现：
   - 新增 `campus_exception_record`。
   - 现有 courier `POST /api/campus/courier/orders/{id}/exception-report` 同事务写入历史表并继续更新订单 latest exception 摘要。
   - 新增 admin 只读接口 `GET /api/campus/admin/exceptions` 与 `GET /api/campus/admin/exceptions/{id}`。
   - MySQL init、V9 migration、H2 schema 已同步；H2 未预置复杂样本，运行态通过真实异常上报生成历史。
18. Step 45B 已完成 admin 异常最小处理动作设计：
   - 继续坚持最小状态集 `REPORTED / RESOLVED`。
   - 不引入 `ACKNOWLEDGED`。
   - 不把 `REJECTED` 做成主状态，后续如需标记无效可使用 `processResult = MARKED_INVALID`。
   - resolve 只更新 `campus_exception_record` 处理字段，不改订单主状态、不改 settlement、不清空 latest exception 摘要。
19. Step 46 已完成异常 resolve 最小实现：
   - 新增 `POST /api/campus/admin/exceptions/{id}/resolve`。
   - 请求体最小字段为 `processResult` 与 `adminNote`。
   - 最小 `processResult` 固定为 `HANDLED`、`MARKED_INVALID`、`FOLLOWED_UP`。
   - 只允许 `REPORTED -> RESOLVED`。
   - 重复处理返回“异常记录已处理，不能重复处理”。
   - 不改订单主状态、不改 settlement、不清空 latest exception 摘要。
   - 没有补 admin 前端页面。
12. Step 47 已完成 admin 异常前端承接 go / no-go：
   - 方向 A：admin 异常历史 / resolve 最小前端承接，收益明确、风险可控、现有接口已足够。
   - 方向 B：P2 售后执行历史表，长期审计价值高，但需要新表、迁移和独立兼容策略设计，复杂度更高。
   - 最终选择方向 A。
   - 本轮没有写前端页面、没有新增后端接口、没有改 bridge。
13. Step 48 已完成 admin 异常前端最小承接：
   - 新增 `/campus/exceptions`。
   - 新增 `CampusExceptionOpsView.vue`。
   - 列表接入 `GET /api/campus/admin/exceptions`。
   - 详情 drawer 接入 `GET /api/campus/admin/exceptions/{id}`。
   - 最小 resolve 区接入 `POST /api/campus/admin/exceptions/{id}/resolve`。
   - resolve 区只在 `processStatus = REPORTED` 时展示。
   - 没有新增后端接口，没有改订单主状态、settlement、latest exception 摘要或 bridge。
14. Step 49 已完成 admin 异常页运行态验证：
   - H2/test 下使用 `CR202604070002` 真实完成接单、两次异常上报、异常历史列表、详情、resolve、重复 resolve 失败。
   - `campus_exception_record` 对该订单产生 2 条历史，1 条 `REPORTED`、1 条 `RESOLVED`。
   - latest exception 摘要仍保留最后一次上报内容，`/courier/workbench`、customer 订单详情和 admin 最近异常摘要语义未被破坏。
   - `/campus/exceptions` 已通过 Playwright 验证页面加载、列表展示、详情 drawer 和 resolve 区展示。
   - 证据文件位于 `project-logs/campus-relay/runtime/step-49/`。
15. Step 50 已完成 P2 售后执行历史表最小方案设计：
   - 建议新增 `campus_after_sale_execution_record` 作为售后执行审计主表。
   - 每次 admin 成功记录售后执行结果时新增一条历史。
   - 同事务内继续更新 `campus_relay_order` 上的当前售后执行摘要。
   - 订单表现有 `after_sale_execution_*` 字段继续作为兼容摘要保留。
   - Step 51A 才允许评估进入最小后端实现。
16. Step 51A 已完成售后执行历史最小实现：
   - 新增 `campus_after_sale_execution_record`，并同步 MySQL init、V10 migration、H2 schema。
   - 复用现有 `POST /api/campus/admin/orders/{id}/after-sale-execution`，在同事务内追加历史写入。
   - 新增 admin 只读分页接口 `GET /api/campus/admin/after-sale-execution-records`。
   - H2/test 下已验证 `PENDING -> FAILED -> SUCCESS` 产生 2 条历史，且当前售后执行摘要保持兼容。
   - 证据文件：`project-logs/campus-relay/runtime/step-51a/after-sale-execution-history-validation.json`。
17. Step 51B 已完成售后执行历史前端承接 go / no-go：
   - 方向 A：在现有 `CampusAfterSaleExecutionList.vue` 详情 drawer 内增加售后执行历史只读区。
   - 方向 B：切到 P3 settlement 批次复核、撤回和对账方案设计。
   - 最终选择方向 A。
   - 本轮没有写前端页面、没有新增后端接口、没有改 bridge。
18. Step 52 已完成售后执行历史前端最小承接：
   - `frontend/src/api/campus-admin.js` 新增 `getCampusAfterSaleExecutionRecords`。
   - `frontend/src/views/CampusAfterSaleExecutionList.vue` 详情 drawer 新增“执行历史”只读区。
   - 打开详情时继续读取原 after-sale-result 当前摘要，同时按 `relayOrderId` 读取历史记录。
   - 未新增页面、未新增路由、未新增写接口。
19. Step 53 已完成售后执行历史前端运行态验证：
   - H2/test 下重新生成 `CR202604060001` 的 `PENDING -> FAILED -> SUCCESS` 售后执行历史样本。
   - `GET /api/campus/admin/after-sale-execution-records` 返回 2 条历史。
   - `/campus/after-sale-executions` 详情 drawer 可展示当前摘要和执行历史区。
   - 证据文件位于 `project-logs/campus-relay/runtime/step-53/`。
20. Step 54 已完成 P3 settlement 批次复核、撤回和对账最小方案设计：
   - 当前 settlement 批次仍由 `campus_settlement_record.payout_batch_no` 聚合产生，不是独立批次主表。
   - 建议后续新增 `campus_settlement_batch_operation_record`，先承接批次复核/撤回操作审计。
   - 建议后续新增 `campus_settlement_reconcile_difference_record`，承接对账差异和处理状态。
   - 现有 `campus_settlement_record` payout 摘要字段继续作为单笔、批次和 reconcile-summary 的兼容读取基础。
   - 本轮只写方案，没有写 SQL、Java、Vue、接口或页面。
21. Step 55 已完成旧外卖文档清理与目录归档：
   - 旧外卖 API/DB 设计归档到 `docs/legacy-takeaway/`。
   - 根 `project-logs/` 下旧外卖修复日志归档到 `project-logs/legacy-takeaway/`。
   - 当前 README、docs index、frontend README、交付说明、检查说明、部署说明等已改为校园代送试运营口径。
   - 新增 `docs/api-overview.md` 与 `docs/db-overview.md` 作为当前主线入口。
   - 本轮没有改 Java、SQL、Vue 业务代码、bridge、接口、鉴权或路由。
22. Step 56 已完成 settlement 批次操作审计 go / no-go：
   - 确认下一步进入 `campus_settlement_batch_operation_record` 最小实现。
   - 确认批次操作审计只承接 `REVIEW / WITHDRAW` 操作留痕。
   - 确认 `operation_result` 最小使用 `PASSED / REJECTED / REQUESTED / RECORDED`。
   - 明确 review / withdraw 只写批次操作审计，不改 `payout_status`，不清空 `payout_batch_no`，不做真实财务撤回。
   - 对账差异记录继续后置，不和批次操作审计并发实现。
   - 本轮没有写 SQL、Java、Vue、接口实现或页面。
23. Step 57 已完成 settlement 批次操作审计最小实现：
   - 新增 `campus_settlement_batch_operation_record`。
   - 同步 MySQL init、V11 migration、H2 schema。
   - 新增 admin 接口：
     - `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
     - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
     - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`
   - 写入前复用当前 settlement 批次聚合查询校验 `batchNo` 真实存在。
   - review / withdraw 只写操作审计，不改 `payout_status`，不清空 `payout_batch_no`。
   - 本轮没有新增前端页面，没有实现对账差异记录。
   - `.\mvnw.cmd -DskipTests compile` 和 `npm run build` 已通过。
23. Step 58 已完成 settlement 批次操作审计 H2/test 运行态验证：
   - 通过 API 生成固定批次 `PBSTEP58VALIDATION`。
   - review 写入 `REVIEW / PASSED`。
   - withdraw 写入 `WITHDRAW / REQUESTED`。
   - `/operations` 返回 2 条操作历史。
   - 单笔结算 `payoutStatus / payoutBatchNo / payoutVerified` 在 review / withdraw 后保持不变。
   - 批次详情 `totalCount / paidCount / payoutStatus` 在 review / withdraw 后保持不变。
   - 证据文件：`project-logs/campus-relay/runtime/step-58/settlement-batch-operation-api-validation.json`。
24. Step 59 已完成 settlement 批次操作审计前端承接 go / no-go：
   - 方向 A：在现有 `CampusSettlementBatchDetail.vue` 中增加 operations 只读承接。
   - 方向 B：进入 settlement 对账差异记录最小方案设计。
   - 最终选择方向 A。
   - 选择原因：后端 operations 已通过 Step 58 运行态验证，现有批次详情页天然具备 `batchNo` 上下文，且只读承接能补齐批次操作审计展示闭环。
   - 本轮没有写业务代码，没有新增页面，没有改 bridge。
25. Step 60 已完成 settlement 批次操作审计前端最小只读承接：
   - `frontend/src/api/campus-admin.js` 新增 `getCampusSettlementBatchOperations(batchNo, params)`。
   - `frontend/src/views/CampusSettlementBatchDetail.vue` 新增“批次操作历史”只读区。
   - 只调用 `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`。
   - 展示 `operationType / operationResult / operationRemark / operatedByEmployeeId / operatedAt`。
   - 没有调用 review / withdraw 写接口。
   - 没有新增页面、路由、后端接口或 bridge 改动。
   - `npm run build` 已通过。
26. Step 61 已完成 settlement 批次操作审计前端运行态验证：
   - H2/test 下准备固定批次 `PBSTEP61UI`。
   - 成功写入 `REVIEW / PASSED` 和 `WITHDRAW / REQUESTED`。
   - `/campus/settlement-batches/PBSTEP61UI` 能展示批次汇总、settlement 明细和 2 条操作审计记录。
   - 浏览器上下文中 `/operations` 返回 `code = 200`、`total = 2`。
   - 证据文件：
     - `project-logs/campus-relay/runtime/step-61/settlement-batch-operation-ui-api-prep.json`
     - `project-logs/campus-relay/runtime/step-61/settlement-batch-operation-page-validation.json`
27. Step 62 已完成 settlement 对账差异记录最小方案设计：
   - 建议后续新增独立 `campus_settlement_reconcile_difference_record` 作为审计主数据。
   - 差异来源第一版只建议 `MANUAL_ADMIN / SIMULATED_RECONCILE`，不接真实银行流水或支付网关。
   - 最小差异类型建议 `AMOUNT_MISMATCH / STATUS_MISMATCH / UNVERIFIED_PAID / FAILED_NEEDS_RETRY / MANUAL_NOTE`。
   - 最小处理状态建议 `OPEN / RESOLVED`。
   - 差异 resolve 只写差异记录处理字段，不改 `payout_status`、不清空 `payout_batch_no`、不改 settlement 金额、不触发真实财务动作。
   - 现有 `campus_settlement_record` payout 摘要继续服务 settlement 单笔页、批次页和 `reconcile-summary` 兼容读取。
   - 本轮没有写 SQL、Java、Vue、接口实现或页面。
28. Step 63 已完成 settlement 对账差异记录实现 go / no-go：
   - 方向 A：进入 `campus_settlement_reconcile_difference_record` 最小后端实现。
   - 方向 B：继续停留在方案设计，补充字段校验细节。
   - 最终选择方向 A。
   - 选择原因：Step 62 已把数据模型、状态、接口和兼容策略收敛到足够小，且实现可独立于现有 settlement payout 摘要。
   - 本轮没有写 SQL、Java、Vue、接口实现或页面。
29. Step 64 已完成 settlement 对账差异记录最小后端实现：
   - 新增 `campus_settlement_reconcile_difference_record`。
   - 同步 MySQL init、V12 migration、H2 schema。
   - 新增 admin 差异列表、详情、创建、resolve 四个最小接口。
   - resolve 只允许 `OPEN -> RESOLVED`，重复 resolve 返回明确业务错误。
   - H2/test 下使用固定批次 `PBSTEP64RECON` 验证创建、查询、resolve、重复 resolve 失败和 settlement 摘要不变。
   - 证据文件：`project-logs/campus-relay/runtime/step-64/settlement-reconcile-difference-validation.json`。
   - 本轮没有新增前端页面，没有新增第五个 admin 页，没有接真实财务，没有改 payout 摘要、订单主状态或 bridge。
30. Step 65 已完成 settlement 对账差异前端承接 go / no-go：
   - 方向 A：在现有 `CampusSettlementOpsView.vue` 详情 drawer 内增加“对账差异记录”只读区。
   - 方向 B：暂不做前端承接，收住 settlement 对账差异线。
   - 方向 C：放到批次详情页。
   - 最终选择方向 A。
   - 选择原因：单笔 settlement 详情 drawer 最适合解释差异记录与 payout 摘要兼容关系，且不需要新增页面、路由或写操作。
   - 本轮没有写前端页面、没有新增 API 封装、没有改 bridge。
31. Step 66 已完成 settlement 对账差异前端最小只读承接：
   - `frontend/src/api/campus-admin.js` 新增 `getCampusSettlementReconcileDifferences(params)`。
   - `frontend/src/views/CampusSettlementOpsView.vue` 详情 drawer 新增“对账差异记录”只读区。
   - 打开 settlement 详情时按 `settlementRecordId` 加载差异记录。
   - 只读展示 `OPEN / RESOLVED`、差异类型、金额、来源、处理结果和时间字段。
   - 空态提示“暂无对账差异记录，当前 settlement 仍按原 payout 摘要展示”。
   - 没有接入 create / resolve 写操作，没有新增页面或路由。
32. Step 67 已完成 settlement 对账差异前端运行态验证：
   - H2/test 下准备固定批次 `PBSTEP67UI`、settlement `id=1` 和 `AMOUNT_MISMATCH` 差异记录。
   - `/campus/settlements` 列表可显示 `CR202604060001`，详情 drawer 可展示“对账差异记录”只读区。
   - 浏览器中验证 `GET /api/campus/admin/settlements/reconcile-differences?settlementRecordId=1&page=1&pageSize=10` 返回 `total = 1`。
   - 对账差异只读展示不改变 settlement payout 摘要，当前摘要仍为 `FAILED / PBSTEP67UI`。
   - 证据文件：
     - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-ui-api-prep.json`
     - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-page-validation.json`
     - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-drawer.png`
33. Step 68 已完成 settlement 对账差异前端线收口评估：
   - 最终结论为 no-go。
   - 当前不继续为 `CampusSettlementOpsView.vue` 增加 resolve 写操作。
   - settlement 对账差异前端线在“只读展示 + 运行态验证”后正式收住。
   - `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve` 后端接口继续保留，但当前不在只读运营页暴露。
34. Step 69 已完成 settlement P3 主线阶段复盘：
   - 批次操作审计线已在“后端实现 + 前端只读承接 + 运行态验证”后收住。
   - 对账差异线已在“后端实现 + 前端只读承接 + 运行态验证 + 前端线收口评估”后收住。
   - settlement P3 主线整体进入冻结/维护态。
   - 当前不继续为现有 settlement 只读页补 review / withdraw / resolve 前端写动作。
35. Step 70 已完成非 bridge 后端三线整体复盘：
   - 异常历史与最小 resolve 线已达到“后端闭环 + admin 前端承接 + 运行态验证”。
   - 售后执行历史线已达到“后端闭环 + admin 前端承接 + 运行态验证”。
   - settlement P3 线已在 Step 69 确认进入冻结/维护态。
   - 当前不再默认继续扩异常、售后或 settlement 的单点能力。
36. Step 71 已完成整体维护 / 交付口径复盘：
   - 当前最小闭环已达到“可试运营、可演示、可交接”的稳定状态。
   - bridge 冻结态、展示 polish 冻结态、媒体线收住、非 bridge 后端三线收住四条主线当前都没有必须继续补的高优先级缺口。
   - 当前不再默认继续扩异常、售后、settlement 或页面 polish 的单点能力。
37. Step 72 最高优先级建议：
   - 如果继续推进，先进入真正产品化方向规划，统一判断：
     - 试运营之后最值得打开的下一阶段目标是什么
     - 哪些属于真实产品化能力，哪些仍应继续冻结
     - 下一阶段是否优先地图/调度、支付模拟深化、更多运营处理能力，还是整体交付打包
   - 若暂不进入产品化方向规划，则当前项目可保持维护态，不再机械追加新轮次。
38. Step 72 明确禁止：
   - 不改 bridge
   - 不改 `request.js`
   - 不改 `campus-courier.js` bridge 行为
   - 不改 token 附着逻辑
   - 不改路由
   - 不接真实打款
   - 不补完整财务系统
   - 不改订单主状态机
   - 不为补页数机械新增 admin 页面
   - 不接入 create / resolve 写操作
   - 不改后端接口
39. admin 剩余只读运营页和 Profile 页仍属于后续展示级优化候选，但默认不再继续机械 polish。
40. 第五个 admin 页继续后置，不再以“补页数”为目标。

## 已完成但仍需继续扩展的部分

- `campus_*` 核心表、实体、DTO、VO、Mapper、Service、Controller 已齐全
- customer 已打通：创建单、模拟支付、列表、详情、确认送达、取消、售后
- customer 已打通 onboarding 新入口：资料提交、资料读取、审核状态、token 资格判断
- customer 已新增 `/user/campus/order-result`，可最小回看 `AWAITING_CONFIRMATION / COMPLETED` 下的代送结果
- `frontend/src/views/user/CampusOrderResult.vue` 已补清晰的初始提示、错误态和 completed 结果摘要，适合真实演示与回读
- `frontend/src/views/user/CampusOrderResult.vue` 已在 Step 35 追加只读回看标记、三段式引导、状态卡片和错误态说明 polish
- frontend 已新增 `/courier/workbench`，作为 token 获取后的最小 courier 前台承接页
- courier workbench 已补最小接单动作，token 获取后不再停在纯只读承接页
- courier workbench 已补订单详情 drawer，接单后可直接查看当前订单详情
- courier workbench 已补最小取餐承接，可在详情 drawer 中直接调用现有 pickup 接口
- courier workbench 已补最小 deliver 承接，可在详情 drawer 中按真实状态机继续推进配送流程
- courier workbench 已补最小异常上报承接，可在详情 drawer 中按真实后端 DTO 上报最新一次异常
- courier workbench 已补最小 confirm 前可视化与 completed 后只读承接，可在 `AWAITING_CONFIRMATION / COMPLETED` 状态下只读展示送达后状态
- courier workbench 已补按订单号查看详情的最小入口，可在可接单列表为空时回读已完成订单结果
- courier workbench 已在 Step 35 追加最小承接标记、四段式演示链路、可接单区提示和详情 drawer 状态摘要 polish
- courier 已打通：资料提交、资料详情、审核状态、token 发行、可接单列表、接单、取餐、配送推进、异常上报、位置上报
- admin 已打通：
  - 订单分页、详情、时间线
  - 售后处理、售后决策、售后执行结果记录
  - 售后结果分页、售后结果汇总
  - 售后执行分页、人工纠正审计
  - 配送员分页、审核、最近异常、低频位置记录
  - 异常历史分页、详情、最小 resolve 处理
  - 结算分页、详情、确认结算、单笔打款记录、批量打款记录、对账摘要、批次列表、批次详情、二次核对
  - 按订单查看位置记录、按订单查看异常摘要
- customer 已打通售后结果回执查询与 courier onboarding 前台入口
- customer onboarding 页面已补齐 courier token 申请动作衔接
- customer onboarding 页面已在 Step 36 追加 customer 前置入口标记、三段式流程、审核资格概览、token 申请说明和表单分组 polish
- frontend 已打通 admin settlement 批次列表页、批次详情页、售后执行分页页、courier 异常/位置联动页和 settlement 只读运营页最小演示入口
- `frontend/src/views/CampusSettlementOpsView.vue` 已在 Step 37 追加只读运营标记、三段式运营引导、筛选区说明、只读提示、中文状态 tag 和详情 drawer 摘要卡片 polish
- `frontend/src/views/CampusAfterSaleExecutionList.vue` 已在 Step 38 追加只读执行标记、三段式页面导览、筛选区说明、只读提示、中文状态 tag 和售后结果 drawer 摘要卡片 polish
- Step 39 已复盘 `CourierOnboarding.vue`、`CourierWorkbench.vue`、`CampusOrderResult.vue`、`CampusSettlementOpsView.vue`、`CampusAfterSaleExecutionList.vue` 五个关键页面，并将展示 polish 线收成冻结/维护态
- Step 40 已固化交付边界、主演示脚本、演示账号与样本数据索引、页面展示清单和答辩口径
- settlement 已在订单 `COMPLETED` 时自动生成或更新

## 已锁定的默认处理策略

### 1. 账号与鉴权

- 普通用户继续复用 `user`
- 管理员继续复用 `employee`
- 配送员继续通过 `campus_courier_profile.user_id` 关联 `user`
- `/api/campus/admin/**` 继续只允许 `employee`
- `/api/campus/customer/**` 继续只允许 `customer`
- `/api/campus/courier/orders/**` 继续只允许 `courier`
- `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留 `customer/courier` 双 token bridge
- `/api/campus/customer/courier-onboarding/**` 作为新的 onboarding 稳定入口，只允许 `customer`

### 2. 支付、退款与打款

- 第一版支付继续只支持模拟支付
- 售后决策、售后执行结果、结算打款结果都只是后台最小记录
- 不接第三方支付，不做真实退款，不做真实打款
- 金额继续统一为 `BigDecimal`、两位小数、`HALF_UP`

### 3. 状态与运营字段

- 不新增新的订单主状态
- `after_sale_execution_status` 独立表达售后执行结果
- `payout_status` 独立表达打款结果
- `decisionType = NONE` 的订单自动初始化为 `after_sale_execution_status = NOT_REQUIRED`
- `REFUND / COMPENSATE` 的订单自动初始化为 `after_sale_execution_status = PENDING`
- `FAILED -> SUCCESS` 的售后执行人工纠正会写入 `after_sale_execution_corrected*` 审计字段
- settlement confirm 后自动初始化 `payout_status = UNPAID`
- batch payout 未传 `batchNo` 时会在 service 层自动生成统一批次号，只写入成功处理的记录
- `payout_verified*` 字段独立表达二次核对结果，不复用 `settlement_status`

### 4. 异常与位置

- 异常历史已落地到 `campus_exception_record`
- 订单上的 latest exception 字段继续作为兼容摘要保留
- admin resolve 仅支持 `REPORTED -> RESOLVED`，不改订单主状态和 settlement
- 位置上报继续只写 `campus_location_report`
- 不做实时轨迹、地图页、频控策略

### 5. SQL 与持久化

- 继续维护：
  - `backend/db/init.sql`
  - `backend/db/migrations`
  - `backend/src/main/resources/db/schema-h2.sql`
  - `backend/src/main/resources/db/data-h2.sql`
- 继续使用注解式 MyBatis
- 不引入新迁移工具

## 当前主要阻塞点

### 1. bridge 仍是过渡态，但已进入 `Phase A no-op` 冻结态

- 影响：`courier/profile` 与 `courier/review-status` 继续依赖双 token 兼容
- 当前证据：
  - `customer/courier-onboarding/*` 已覆盖资料提交、资料读取、审核状态、资格判断、token 申请
  - `/courier/workbench` 已覆盖 token 获取后的最小前台承接页
  - `/courier/workbench` 已具备接单、详情承接、最小取餐承接、最小 deliver 承接和最小异常上报承接
  - 当前仓库内前端对旧 bridge 的直接调用已盘点到只剩 `CourierWorkbench.vue`
  - onboarding 新入口已能承接未拿 courier token 前的主要前端场景
- Step 29 owner 明确确认：
  - 当前项目从未部署、从未发布、没有历史发布包
  - 当前项目唯一维护人就是 owner 本人，且没有团队
  - 不存在团队共享 `Postman / Apifox / 联调脚本` 资产
  - 不存在仓库外旧页面副本、历史前端包或临时脚本继续依赖旧 bridge
- 当前判断：
  - repo 外阻塞项已关闭
  - Step 33 已正式进入 `Phase A no-op` 冻结态
  - Step 30 已明确 `Phase A` 的边界、bridge 保留范围、回滚策略和最小回归清单
  - Step 34 已转向不触 bridge 的展示级优化候选评估和演示资料整理
- 默认处理：继续保留旧 bridge，不做删除动作；后续不再默认寻找 bridge 候选，只有触发条件出现时才重开 bridge 主线
- repo 内已新增一轮真实局部留痕：
  - 真实 token 申请成功
  - 真实 workbench 加载成功
  - 真实确认 `profile / review-status` 优先走 `courier_token`
  - 真实确认无 `courier_token` 时不调用 courier 业务接口
  - 真实通过订单号 `CR202604060001` 回读 completed 订单详情
- 真实跑通 `CR202604070002` 的本地完整链路：onboarding -> 审核 -> token -> 接单 -> 取餐 -> deliver -> 异常上报 -> customer confirm -> completed 回读
- Step 23 已把该链路整理为可共享回归留痕：
  - `project-logs/campus-relay/step-23-shared-regression-evidence.md`
- 仍缺：
  - 若执行失败时的实际回滚操作演练

### 2. 前端 admin 侧已完成四个只读运营演示页和一个异常处理承接页

- 影响：admin 现在已有 settlement 批次演示页、售后执行演示页、courier 异常/位置联动演示页、settlement 只读运营页，以及异常历史 / resolve 最小承接页
- 默认处理：继续保持现有 admin 演示页和异常处理页稳定，不为补页数机械新增第五页

### 3. 售后执行历史后端和最小前端承接已落地，运行态验证已通过

- 影响：售后执行现在已有历史表、同事务写入、admin 只读分页接口和 drawer 内只读历史区
- 当前进展：Step 51A 已完成 `campus_after_sale_execution_record`、现有执行接口追加历史写入和 `GET /api/campus/admin/after-sale-execution-records`
- 当前进展：Step 51B 已选择方向 A，售后执行历史值得做最小前端承接
- 当前进展：Step 52 已在现有 `CampusAfterSaleExecutionList.vue` 详情 drawer 内增加只读历史区
- 当前进展：Step 53 已通过 H2/test API 和 Playwright 页面验证
- 默认处理：售后执行历史主线已在后端、前端承接和运行态验证后收住，不再默认继续扩展。

### 4. settlement 仍没有真实财务执行能力

- 影响：目前只有后台记录、批次和核对，尚无真实打款、撤回、对账对接
- 默认处理：继续保持最小运营记录，等业务边界稳定后再决定是否引入更深财务能力

## 当前明确没做的事情

- 没有重写 `frontend/` 主链路，只新增了最小 customer 页面、admin settlement 演示页与轻量脚本
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有接真实支付、真实退款、真实打款
- 没有做完整售后工单系统
- 没有做完整异常工单系统；异常历史表和最小 resolve 已完成，但没有做 ACKNOWLEDGED、reopen、delete、通知或多角色审批
- 没有做轨迹回放、路线规划、实时调度或地图大屏；当前只保留 Step 72 的单页腾讯地图点位预览试点
- 没有新增第二套返回体

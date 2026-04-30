# 校园代送改造总览

## 当前轮次

- 当前已完成：`Step 01 - 领域模型重构规划`
- 当前已完成：`Step 02A - 数据库与后端骨架`
- 当前已完成：`Step 02B - 只读接口完善`
- 当前已完成：`Step 03A - customer 下单与模拟支付`
- 当前已完成：`Step 03B - courier 资料链路与 admin 最小审核/详情`
- 当前已完成：`Step 03C - courier token 与最小接单链路`
- 当前已完成：`Step 03D - courier 取餐 / 配送推进 / customer 确认送达`
- 当前已完成：`Step 03E - customer 取消 / 售后 / admin 时间线 / 结算联动`
- 当前已完成：`Step 03F - admin 售后处理 / courier 异常上报 / courier 位置上报 / admin 结算分页`
- 当前已完成：`Step 04 - 售后决策 / 结算确认 / admin 运营只读接口`
- 当前已完成：`Step 05 - 售后执行记录 / 结算打款运营 / 订单级运营查询`
- 当前已完成：`Step 06 - customer 售后结果回执 / admin 执行纠正审计 / settlement 批次核对`
- 当前已完成：`Step 07 - customer onboarding 替代链路 / frontend 最小接入`
- 当前已完成：`Step 08 - admin settlement 批次演示页 / frontend 最小只读运营接入`
- 当前已完成：`Step 09 - admin 售后执行演示页 / frontend 第二个只读运营入口`
- 当前已完成：`Step 10 - admin courier 异常/位置联动演示页 / frontend 第三个只读运营入口`
- 当前已完成：`Step 11 - admin settlement 只读运营页 / frontend 第四个只读运营入口`
- 当前已完成：`Step 12 - onboarding token 申请衔接 / bridge 收口条件细化 / admin 演示页小修`
- 当前已完成：`Step 13 - courier workbench 最小承接页 / bridge 收口证据链细化`
- 当前已完成：`Step 14 - bridge 真实调用盘点 / courier workbench 最小接单承接`
- 当前已完成：`Step 15 - bridge 依赖评估细化 / courier workbench 订单详情承接`
- 当前已完成：`Step 16 - bridge 收口计划评估 / courier workbench 最小取餐承接`
- 当前已完成：`Step 17 - bridge 执行前评估 / courier workbench 最小 deliver 承接`
- 当前已完成：`Step 18 - bridge 执行准备清单 / courier workbench 最小异常上报承接`
- 当前已完成：`Step 19 - bridge 执行准备模板 / courier workbench confirm 前可视化`
- 当前已完成：`Step 20 - bridge 模板可执行化 / courier workbench completed 后最小承接`
- 当前已完成：`Step 21 - bridge 局部真实验证 / courier workbench completed 结果回读`
- 当前已完成：`Step 22 - H2 可接单数据补齐 / 本地完整链路真实联调`
- 当前已完成：`Step 23 - 共享回归留痕整理 / customer completed 结果回看`
- 当前已完成：`Step 24 - repo 外核实准备增强 / customer 结果页体验增强 / onboarding 请求体类型核查`
- 当前已完成：`Step 25 - repo 外 bridge 依赖核实与证据回填`
- 当前已完成：`Step 26 - repo 外真实资产追补与阻塞项重新评估`
- 当前已完成：`Step 27 - 关键外部资产追补与阻塞项继续关闭`
- 当前已完成：`Step 28 - 关键业务资产追补与阻塞项继续关闭`
- 当前已完成：`Step 29 - owner 确认回填 / bridge repo 外阻塞关闭与阶段重评估`
- 当前已完成：`Step 30 - Phase A 执行准备重新评估`
- 当前已完成：`Step 31 - 最小 Phase A 动作候选评估 / 执行前最小回归复核`
- 当前已完成：`Step 32 - Phase A 候选池扩展与 go / no-go 决策`
- 当前已完成：`Step 33 - Phase A no-op 冻结态 / 恢复推进条件`
- 当前已完成：`Step 34 - 非 bridge 方向收束 / 展示级优化候选评估`
- 当前已完成：`Step 35 - 展示级优化执行轮 1`
- 当前已完成：`Step 36 - 展示级优化执行轮 2`
- 当前已完成：`Step 37 - 展示级优化执行轮 3`
- 当前已完成：`Step 38 - 展示级优化执行轮 4`
- 当前已完成：`Step 39 - 展示 polish 复盘与冻结判断`
- 当前已完成：`Step 40 - 交付整理与演示脚本固化`
- 当前已完成：`Step 41 - 交付材料补完 / 截图清单 / 录屏顺序 / 演示前检查`
- 当前已完成：`Step 42 - 真实媒体采集与归档`
- 当前已完成：`Step 43 - 媒体缺口分叉判断 / 非 bridge 后端评估入口`
- 当前已完成：`Step 44 - 异常历史与处理闭环最小方案设计`
- 当前已完成：`Step 45A - 异常历史最小实现`
- 当前已完成：`Step 45B - 异常最小处理动作设计`
- 当前已完成：`Step 46 - 异常 resolve 最小实现`
- 当前已完成：`Step 47 - admin 异常前端承接 go / no-go`
- 当前已完成：`Step 48 - admin 异常历史 / resolve 最小前端承接`
- 当前已完成：`Step 49 - admin 异常处理页运行态验证`
- 当前已完成：`Step 50 - 售后执行历史表最小方案设计`
- 当前已完成：`Step 51A - 售后执行历史最小实现`
- 当前已完成：`Step 51B - 售后执行历史前端承接 go / no-go`
- 当前已完成：`Step 52 - 售后执行历史前端最小承接`
- 当前已完成：`Step 53 - 售后执行历史前端运行态验证`
- 当前已完成：`Step 54 - settlement 批次复核、撤回和对账最小方案设计`
- 当前已完成：`Step 55 - 旧外卖文档清理与目录归档`
- 当前已完成：`Step 56 - settlement 批次操作审计 go / no-go`
- 当前已完成：`Step 57 - settlement 批次操作审计最小实现`
- 当前已完成：`Step 58 - settlement 批次操作审计运行态验证`
- 当前已完成：`Step 59 - settlement 批次操作审计前端承接 go / no-go`
- 当前已完成：`Step 60 - settlement 批次操作审计前端最小只读承接`
- 当前已完成：`Step 61 - settlement 批次操作审计前端运行态验证`
- 当前已完成：`Step 62 - settlement 对账差异记录最小方案设计`
- 当前已完成：`Step 63 - settlement 对账差异记录实现 go / no-go`
- 当前已完成：`Step 64 - settlement 对账差异记录最小后端实现`
- 当前已完成：`Step 65 - settlement 对账差异前端承接 go / no-go`
- 当前已完成：`Step 66 - settlement 对账差异前端最小只读承接`
- 当前已完成：`Step 67 - settlement 对账差异前端运行态验证`
- 当前已完成：`Step 68 - settlement 对账差异前端线收口评估`
- 当前已完成：`Step 69 - settlement P3 主线阶段复盘`
- 当前已完成：`Step 70 - 非 bridge 后端三线整体复盘`
- 当前已完成：`Step 71 - 整体维护 / 交付口径复盘`
- 当前已完成：`Step 72 - 腾讯地图最小产品化试点 / 园区要素运营地图预览`
- 当前已完成：`Step 73 - 腾讯地图产品化线 go / no-go 与试点收口`
- 当前已完成：`Step 74 - 试运营版产品化下一阶段规划`
- 当前已完成：`Step 75 - 试运营运行配置与 Preflight 手册`
- 当前已完成：`Step 76 - 模拟资金链路产品化边界说明`
- 当前已完成：`Step 77 - 试运营交付文档一致性复核 / 最小 preflight 验证`
- 当前已完成：`Step 78 - 试运营交付包 RC 收口复盘 / 最小 smoke 复核`
- 当前已完成：`Step 79 - 试运营 RC 运行配置与构建告警减噪`
- 当前已完成：`Step 80 - 前端打包告警与分包 go / no-go 评估`
- 当前已完成：`Step 81 - 前端打包线 freeze / no-op 复盘`
- 当前已完成：`Step 82 - 试运营 RC 下一阶段主线重排 / go-no-go 评估`
- 当前已完成：`Step 83 - 试运营运维化最小能力边界收敛 / preflight 脚本入口`
- 当前已完成：`Step 84 - 试运营样本状态校验脚本`
- 当前已完成：`Step 85 - 试运营命令索引与手动 H2 reset 指南`
- 当前已完成：`Step 86 - 试运营脚本线收口 / no-op 冻结判断`
- 当前已完成：`Step 87 - 产品级试运营前剩余差距清单 / go-no-go 评估`
- 当前已完成：`Step 88 - 试运营环境与密钥配置硬化 / deployment preflight 准备`
- 当前已完成：`Step 89 - 部署后 smoke checklist / deployment preflight 文档`
- 当前已完成：`Step 90 - 最小 CI 检查边界设计 / go-no-go`
- 当前已完成：`Step 91 - 最小 GitHub Actions CI 实现`
- 当前已完成：`Step 92 - CI 首轮运行结果跟踪 / 本地与远端一致性复核`
- 当前已完成：`Step 93 - GitHub Actions runtime warning 处理 go / no-go`
- 当前已完成：`Step 94 - CI action 版本升级后远端运行结果跟踪`
- 当前已完成：`Step 95 - 内测型试运营 Compose 部署包最小实现`
- 当前已完成：`Step 96 - 单机服务器首轮内测部署与 smoke 验证`
- 当前已完成：`Step 97 - 全局工作记忆日志建立`
- 当前已完成：`Step 98 - 单机服务器内测运维加固 / 最小回滚与备份准备`
- 当前已完成：`Step 99 - 单机服务器最小恢复演练 / 运维交付加固`
- 当前已完成：`Step 100 - 单机服务器运维交接与正式入口 go/no-go`
- 当前已完成：`Step 101 - admin 文本乱码修复与前端可读性加固`
- 当前已完成：`Step 102 - 校园兼职视觉体系与 admin 外壳刷新`
- 当前已完成：`Step 103 - admin 玻璃拟态视觉重基线与残留乱码兜底`
- 当前已完成：`Step 104 - 浅色校园视觉回调与 courier ops 窄屏裁切修复`
- 当前已完成：`Step 105 - admin 公共壳层一致性修复与数据看板展示重整`
- 当前已完成：`Step 106 - 售后执行页表格列拖拽关闭修复`
- 当前已完成：`Step 107 - 全局按钮 plain 语义修正与员工页操作列样式清理`
- 当前已完成：`Step 108 - 兼职端独立登录与前台入口拆分`
- 当前已完成：`Step 109 - 兼职端壳层与资料页最小补齐`
- 当前已完成：`Step 110 - 用户端 / 兼职端双 Android 壳路线评估`
- 当前已完成：`Step 111 - 双 Capacitor Android 壳 scaffold go / no-go`
- 当前已完成：`Step 112 - 前端 Android 构建目标最小实现`
- 当前已完成：`Step 113 - 双 Capacitor Android 壳 scaffold`
- 当前已完成：`Step 114 - Android 本机构建验证`
- 当前已完成：`Step 115 - Android 真机 / 模拟器 smoke 入口与模拟器阻塞确认`
- 当前已完成：`Step 116 - Android 模拟器真实 smoke 与 API base 加固`
- 当前已完成：`Step 117 - 用户端移动首页校园兼职化`
- 当前已完成：`Step 118 - 用户端移动首页真实视觉 smoke 与登录文案修正`
- 当前已完成：`Step 119 - 用户端校园代送下单 / 我的代送单最小入口`
- 当前已完成：`Step 120 - Android / 内测 API base 分层与用户端代送入口壳级验证`
- 当前已完成：`Step 121 - Android public API base 演练与 smoke 加固`
- 当前已完成：`Step 122 - 公网 API base 切换与 Android public APK 复核`
- 当前已完成：`Step 123 - Android public WebView 真实接口 smoke`
- 当前已完成：`Step 124 - Android public WebView readiness 复核与试运营入口固化`
- 当前已完成：`Step 125 - 旧外卖模块删除前审计与 AI 协作交接`
- 当前已完成：`Step 126 - 校园运营后台前端视觉基线统一`
- 当前已完成：`Step 127 - 用户端 + 兼职端移动入口视觉统一`
- 当前已完成：`Step 128 - 管理后台视觉基线修正`
- 当前已完成：`Step 129 - 管理后台可用性巡检与明显样式问题修复`
- 当前已完成：`Step 130 - 管理后台最终可见问题巡检与低风险修补`
- 当前已完成：`Step 131 - 本地/内测型试运营 smoke 复核`
- 当前已完成：`Step 132 - 稳定浏览器 smoke 工具链`
- 当前已完成：`Step 133 - 本地内测 RC 状态复盘`
- 当前已完成：`Step 134 - 远端内测 smoke 准备`
- 当前已完成：`Step 135 - 远端内测 smoke 真实验证`
- 当前已完成：`Step 136 - 服务器内测运维检查与恢复演练`
- 当前已完成：`Step 137 - GitHub / 服务器同步与远端 smoke 复核`
- 当前已完成：`Step 138 - 内测服务器端口边界与备份告警加固`
- 当前日期：`2026-04-30`
- Step 125 补充：已完成旧外卖模块删除前审计与 AI 协作交接文件建立。新增 `agent-collaboration.md`、`legacy-takeaway-removal-readiness.md`，审计覆盖 10 个旧前端页面、13 个旧 API 文件、14 个旧 Controller 等，明确标记每个模块的 campus 依赖关系。本轮仅做文档，未改任何业务代码。
- Step 126 补充：已完成前端视觉优化优先轮：MainLayout 菜单"旧店铺状态"→"店铺状态兼容"，5 个旧兼容页面标题更新为"X 兼容管理"并新增兼容提示 banner，mock.js 旧词替换。本轮仅改前端可见文案，未删除任何旧模块代码。
- Step 127 补充：已完成用户端 + 兼职端移动入口视觉统一。用户端 5 个页面（Login/Home/CampusRelayOrders/CampusOrderResult/CourierOnboarding）和兼职端 3 个页面（Login/CourierWorkbench/Profile）全部统一为浅色玻璃态 + campus teal 色系。关键旧词替换："外卖内容"→"代送内容"、"兼职配送入驻"→"校园兼职入驻"。本轮只改展示层（CSS 颜色/背景/圆角/阴影 + 页面文案），未改任何业务行为、API、路由语义、鉴权或后端代码。已通过 `npm run build`（1.00s）。
- Step 128 补充：已完成管理后台视觉基线修正，统一后台浅色蓝白外壳、Element Plus 中文 locale 和表格/按钮基础可读性；未改 bridge、接口、鉴权、路由或后端。
- Step 129 补充：已完成后台可用性巡检和明显样式问题修复，取消后台固定操作列、修正 breadcrumb 返回主页可见性并收紧表格布局；未改 bridge、接口、路由或后端。
- Step 130 补充：已完成管理后台最终可见问题巡检，移除通用 `BaseTable.vue` 操作列 `fixed="right"`，清理前端可见旧词残留；backend compile、frontend build 和 `git diff --check` 均通过。
- Step 131 补充：已完成本地/内测型试运营 smoke 复核，API smoke 16 项通过，SPA shell 7 项可访问；本轮没有改业务代码、bridge、接口、鉴权或路由。
- Step 132 补充：已新增稳定浏览器 smoke 工具链 `scripts/trial-operation/browser-smoke.ps1`，覆盖 admin / customer / parttime 7 个关键页面并保存截图报告，浏览器 smoke 7 项通过、0 项失败；本轮没有改业务代码、bridge、接口、鉴权或路由。
- Step 133 补充：已完成本地内测 RC 状态复盘，确认 Step 131 API smoke 与 Step 132 浏览器 smoke 已形成可重复验证基线；当前本地/内测型试运营具备构建、API、页面截图三层验证证据，服务器内测仍需单独做远端 smoke。本轮只改日志文档，未改业务代码、bridge、接口、鉴权或路由。
- Step 134 补充：已新增 `scripts/trial-operation/remote-smoke.ps1` 与 `docs/deployment/remote-internal-trial-smoke.md`，把服务器内测 API / SPA shell smoke 做成可参数化、默认脱敏的执行入口；同步更新部署后 smoke checklist、内测运维 runbook 和试运营命令索引。本轮没有改业务代码、bridge、接口、鉴权、路由或旧兼容模块。
- Step 135 补充：已使用 Step 134 的 `remote-smoke.ps1` 对 owner 提供的内测服务器完成真实远端 smoke，API + frontend shell 共 24 项通过、0 项失败；报告默认脱敏 host 与 endpoint，未提交真实公网 IP、服务器密码、token 或地图 key。本轮没有改业务代码、bridge、接口、鉴权、路由或旧兼容模块。
- Step 136 补充：已完成服务器内测运维检查与恢复演练。compose 中 backend / frontend / mysql 均为 Up；备份脚本成功生成 MySQL dump、uploads、env backup 和 manifest；非破坏性 restore drill 成功恢复最近备份并校验关键订单。当前主要风险是服务器部署 hash 仍停在 `1a2329e`，不是本地最新提交，且公网仍暴露 backend 8080 与 mysql 3306。本轮没有改业务代码、bridge、接口、鉴权、路由或旧兼容模块。
- Step 137 补充：已将本地 `main` 推送到 GitHub，并把服务器从 `1a2329e` fast-forward 到 `3bf59cb`；服务器更新前完成备份，更新后完成 compose 重建，backend / frontend / mysql 均为 running。已配置本机专用 SSH key 免密登录，私钥未进入仓库。远端 smoke 24 项通过、0 项失败、0 项跳过，报告路径为 `project-logs/campus-relay/runtime/step-137-remote-smoke/remote-smoke-report.json`。最新备份已通过非破坏性 restore drill；当前剩余风险是公网仍暴露 backend 8080 与 MySQL 3306，且 backup 脚本仍有 MySQL 8 tablespace 权限 warning。
- Step 138 补充：已完成内测服务器端口边界与备份告警加固。服务器已拉取最新提交并重建 compose，公网 `80` 可访问，公网 `8080 / 3306` 不可访问；远端 smoke 已通过 nginx `/api` 入口复跑，24 项通过、0 项失败、0 项跳过，报告路径为 `project-logs/campus-relay/runtime/step-138-remote-smoke/remote-smoke-report.json`。`backup-stack.sh` 增加 `--no-tablespaces` 后备份成功，最新备份已通过非破坏性 restore drill，恢复出 7 笔订单，关键订单 `CR202604070002` 和 `CR202604060001` 均存在。本轮未改业务代码、bridge、接口、鉴权、路由或旧兼容模块。
- Step 102 补充：已把 admin 主框架、仪表盘和运营人员页从旧外卖后台视觉收敛到校园兼职运营风格；本轮只改展示层和全局主题变量，未改 bridge、接口、鉴权、路由、API 调用顺序或后端业务。已通过 `npm run build`、`npm run test -- text.spec.js` 和 `git diff --check`；本地 admin seed 登录与员工列表复核返回 `管理员 / 技术部`。
- Step 103 补充：已把登录页改为 `校内兼职运营台`，并将 admin 外壳 / dashboard 进一步按深色玻璃拟态方向重基线；同时补齐旧 session / localStorage / in-memory 场景的 admin 文本归一化兜底，覆盖顶部用户名、dashboard 欢迎语和 Employee 页姓名 / 职位 / 部门显示。本轮未改 bridge、接口、鉴权、路由、API 调用顺序、后端业务或数据库。
- Step 104 补充：根据 owner 反馈，已把 Step 103 的深色玻璃方向回调为浅色校园兼职运营风格；登录页、admin 主框架、dashboard 和 Employee 高曝光区域均切回浅色玻璃，同时修正 Element Plus `light-*` 主题变量映射，并修复 `/campus/courier-ops` 窄屏下配送员列表表格裁切导致“审核状态”列显示一半的问题。本轮未改 bridge、接口、鉴权、路由、API 调用顺序、后端业务或数据库。
- Step 105 补充：已将 `Employee.vue` 与 `Statistics.vue` 重新接回 `MainLayout`，恢复统一侧边栏、breadcrumb 和首页返回入口；同时重整 `Statistics.vue` 展示层级，并补上 ECharts 页面卸载清理、实例复用和 `LegacyGridContainLabel` 注册，消除统计页真实 smoke 中的控制台 warning。本轮未改 bridge、接口、鉴权、路由、token 附着、后端业务或新增页面。
- Step 106 补充：已在 `frontend/src/views/CampusAfterSaleExecutionList.vue` 的主列表和详情 drawer 执行历史表中显式关闭列宽拖拽，为所有 `el-table-column` 增加 `:resizable="false"`；本轮只消除 Element Plus 默认列拖拽交互噪音，未改接口、分页、筛选语义、drawer 行为、bridge、鉴权、路由或后端业务。
- Step 107 补充：已修正 `frontend/src/styles/element-plus.scss` 中对 `el-button` 的全局主题覆盖，避免把 `is-plain` / `is-link` / `is-text` 误刷成实心渐变按钮；同时在 `frontend/src/views/Employee.vue` 中收紧操作列按钮间距，移除 `gap` 与 Element Plus 默认 `margin-left` 叠加导致的布局噪音。当前 `type=\"danger\" plain` 的删除按钮已恢复浅底红框语义，且不再出现视觉覆盖问题。本轮未改 bridge、接口、鉴权、路由、后端业务或页面语义。
- Step 108 补充：已新增 `/parttime/login` 作为兼职端独立登录入口，兼容 `/courier/login`；`/courier/workbench` 已变为需要 `courier_token` 的受保护路由，token 失效时统一回到兼职端登录页。当前边界是“用户端负责报名 / 入驻资料，兼职端负责日常登录”。本轮未改后端 token 接口、bridge、鉴权或订单状态机。
- Step 109 补充：已新增 `ParttimeLayout` 与 `/parttime/profile`，并把正式工作台入口调整为 `/parttime/workbench`，旧 `/courier/workbench` 继续作为兼容 alias。`CourierWorkbench.vue` 已切换到兼职端壳层，不再借用户端 Layout；用户端 onboarding 和个人中心的工作台跳转同步指向正式兼职端入口。已通过 frontend build/lint/test、backend compile 和 `git diff --check`。本轮未改 bridge、鉴权、后端接口、工作台动作语义或订单状态机。
- Step 110 补充：已完成用户端 / 兼职端双 Android 壳路线评估，结论为“单前端源码 + 双 Capacitor Android 壳”最符合当前项目结构；admin 继续保持 Web-only，旧 `uni-app/` 仅保留为历史占位，不再作为当前实施主线。本轮只做路线设计，不新增 Android 工程、不改 bridge、鉴权、接口、路由或前端页面语义。
- Step 111 补充：已完成双 Capacitor Android 壳 scaffold go / no-go，结论为暂不直接创建 Android scaffold，先补前端 Android 构建目标层；原因是当前 `/` 仍默认进入 `/dashboard`，若直接打包同一个 `frontend/dist`，用户端和兼职端壳都会继承后台入口。已新增 `docs/mobile/android-shell-scaffold-plan.md`，明确 Step 112 优先实现 `VITE_APP_SHELL` 与 `build:android:user / build:android:parttime` 等前端构建边界。本轮未安装 Capacitor、未新增 Android 工程、未改 bridge、鉴权、接口或业务页面语义。
- Step 112 补充：已新增前端 Android 构建目标层，`npm run build` 继续输出 Web/admin 的 `dist` 且 `/` 默认进 `/dashboard`；`npm run build:android:user` 输出 `dist-android-user` 且 `/` 默认进 `/user/login`；`npm run build:android:parttime` 输出 `dist-android-parttime` 且 `/` 默认进 `/parttime/login`。构建目标通过 Vite `mode` 推导，不依赖被 `.gitignore` 忽略的 `.env.*` 文件。本轮未安装 Capacitor、未新增 Android 工程、未改 bridge、鉴权、接口、路由结构或业务页面语义。
- Step 113 补充：已新增 `mobile/user-app` 与 `mobile/parttime-app` 两个 Capacitor Android 壳，分别使用包名 `com.xiaoyu.campus.user` 与 `com.xiaoyu.campus.parttime`，并分别指向 `dist-android-user` 与 `dist-android-parttime`。两个壳均已完成 `npm install`、`cap:add:android`、`cap:sync` 和 `npx cap doctor android`；`assembleDebug` 首次构建在 5 分钟超时，已留给 Step 114 做 Android 本机构建验证。本轮未改 bridge、鉴权、接口、路由结构或业务页面语义。
- Step 114 补充：已完成 Android 本机构建验证；Gradle wrapper 分发包切到腾讯 Gradle 镜像，Android Gradle 依赖优先走阿里云 Maven 镜像；本机 JDK 21 与 Android SDK 就绪后，用户端和兼职端均通过 `cap:sync` 与 `assembleDebug`，Debug APK 分别输出到两个壳的 `android/app/build/outputs/apk/debug/app-debug.apk`。本轮未改 bridge、鉴权、接口、路由结构、前端业务页面或后端业务语义。
- Step 115 补充：已新增 Android smoke 脚本 `scripts/trial-operation/android-smoke.ps1`，可在设备在线后安装、启动用户端和兼职端 Debug APK 并保存启动截图；同时已安装 Android Emulator、Android 35 Google APIs x86_64 system image 并创建 `campus_api35` AVD。当前真实阻塞是 Android Emulator hypervisor driver 未安装，普通命令环境无法完成管理员驱动安装，`-accel off` 软件启动也未能让 AVD 进入 `adb devices` 在线状态；因此本轮未伪造 APK 安装、首屏和 WebView API 验证结果。
- Step 116 补充：Android Emulator Hypervisor Driver 已可用，`campus_api35` 已以 `emulator-5554` 在线；用户端和兼职端 Debug APK 已真实安装、启动和截图。新增 Android 构建专用 API base：`frontend/.env.android-user` 与 `frontend/.env.android-parttime` 指向 `http://10.0.2.2:8080/api`；两个 Capacitor 壳补齐本地 cleartext smoke 配置，backend dev/test CORS 放行 WebView 本地 origin。真实 WebView 验证已通过兼职端 token 登录、profile、review-status、available orders，以及用户端登录。当前剩余产品问题是用户端登录后首页仍带旧外卖语义，建议 Step 117 优先校园兼职化用户端移动首页。本轮未改 bridge、鉴权、接口语义、路由、订单状态机或页面业务行为。
- Step 117 补充：已将用户端登录后的默认首页从旧外卖商品推荐语义收敛为“校园兼职 / 校园代送用户端首页”。`UserLayout.vue` 改为用户端移动 shell 与底部校园入口，`Home.vue` 改为校园结果回看、兼职入驻状态、token 资格提示和旧外卖兼容入口；旧 `category/cart/orders/profile` 页面仍保留为兼容入口。本轮没有改 bridge、`request.js`、token 附着、API 调用顺序、路由结构、后端接口、Android 原生工程或旧外卖模块。
- Step 118 补充：已启动本地 backend(test) 与 frontend dev，使用 Playwright CLI 在 390x844 移动视口真实登录用户端并采集 `/user/login`、`/user`、`/user/campus/order-result?orderId=CR202604060001`、`/user/campus/courier-onboarding` 截图。smoke 发现用户登录页仍有“点餐”文案，已修正为校园兼职用户端登录文案和浅色校园风格；用户首页、底部导航、结果回看快捷入口和入驻摘要可读。本轮没有改 bridge、鉴权、token 附着、API 调用顺序、后端接口、Android 原生工程或旧外卖模块。
- Step 119 补充：已新增用户端 `/user/campus/orders` 校园代送下单 / 我的代送单最小入口，复用现有 customer campus 接口读取取餐点、配送规则、创建校园代送单、分页读取我的代送单并手动触发 mock-pay。真实移动视口 smoke 已创建并模拟支付订单 `CR202604251658356537`，接口均返回 200，browser console 无 error / warning；用户端底部导航新增“代送”，首页主按钮切到发布代送单。本轮未改 bridge、`request.js`、token 附着、后端接口、订单状态机、Android 原生工程或旧外卖模块。
- Step 120 补充：已将 Android API base 从单一模拟器配置固化为模拟器、局域网真机、公网内测三类构建模式；新增 `android-api-base-check.ps1` 与 preflight 集成，LAN/Public 未配置时只给 warning，但对应构建会因缺少显式 `VITE_API_BASE_URL` 失败，不再静默回退。已在 `campus_api35` 模拟器中完成用户端和兼职端 APK 安装启动，并确认用户端可从底部导航进入 `/user/campus/orders`。本轮未改 bridge、`request.js`、token 附着、后端接口、订单状态机、旧外卖模块或 Android 原生壳结构。
- Step 46 补充：已新增 admin 异常 resolve 后端接口 `POST /api/campus/admin/exceptions/{id}/resolve`，只允许 `REPORTED -> RESOLVED`，重复处理返回明确业务错误；本轮未改订单主状态、settlement、latest exception 摘要、bridge、前端页面或路由。
- Step 47 补充：本轮只做 admin 异常前端承接 go / no-go 评估，不写业务代码、不补页面；最终选择方向 A，建议 Step 48 进入 admin 异常历史 / resolve 最小前端承接方案与实现准备，P2 售后执行历史表继续后置。
- Step 48 补充：已新增 `/campus/exceptions` admin 异常处理页，接入异常历史列表、详情 drawer 和 `REPORTED -> RESOLVED` 最小 resolve 动作；本轮未改后端接口、bridge、鉴权、订单主状态、settlement 或 latest exception 摘要。
- Step 49 补充：已在 H2/test 下对 `/campus/exceptions` 完成运行态验证，覆盖异常历史列表、详情 drawer、resolve 成功、重复 resolve 失败、latest exception 摘要兼容和 customer 订单详情兼容；本轮未修改业务代码、bridge、接口、鉴权、路由或订单主状态。
- Step 62 补充：已完成 settlement 对账差异记录最小方案设计，建议后续新增独立 `campus_settlement_reconcile_difference_record` 作为审计主数据；差异处理只记录 `OPEN / RESOLVED` 和处理结果，不改 settlement payout 摘要、不接真实财务、不新增前端页面。
- Step 63 补充：已完成 settlement 对账差异记录实现 go / no-go，最终选择进入最小后端实现；Step 64 只允许落表、MySQL/H2 schema、admin 列表/详情/创建/resolve 最小接口，不新增前端页面、不改 payout 摘要、不接真实财务。
- Step 64 补充：已新增 `campus_settlement_reconcile_difference_record`、V12 migration、H2 schema 和 admin 对账差异列表/详情/创建/resolve 四个最小接口；H2/test 下使用 `PBSTEP64RECON` 验证创建、查询、resolve、重复 resolve 失败和 settlement payout 摘要 / `reconcile-summary` 不变。
- Step 65 补充：已完成 settlement 对账差异前端承接 go / no-go，最终选择方向 A，建议 Step 66 在现有 `CampusSettlementOpsView.vue` 详情 drawer 内增加“对账差异记录”只读区，不新增页面、不接入 create/resolve 写操作。
- Step 66 补充：已在 `CampusSettlementOpsView.vue` 详情 drawer 内新增“对账差异记录”只读区，并在 `campus-admin.js` 新增 `getCampusSettlementReconcileDifferences`；打开 settlement 详情时按 `settlementRecordId` 读取差异记录，只展示只读列表、空态和错误态，不接入 create / resolve 写操作。
- Step 67 补充：已在 H2/test + Vite 下准备 `PBSTEP67UI` / settlement `id=1` 的对账差异样本，并通过浏览器验证 `/campus/settlements` 详情 drawer 能展示“对账差异记录”只读区；页面中可见 `AMOUNT_MISMATCH`、`¥6.00 / ¥4.50`、`OPEN / 待处理`、`SIMULATED_RECONCILE` 和 `Step67 UI drawer amount mismatch validation`，且 settlement payout 摘要仍为 `FAILED / PBSTEP67UI`。
- Step 68 补充：已完成 settlement 对账差异前端线收口评估，结论为 no-go；当前不继续为 `CampusSettlementOpsView.vue` 增加 resolve 写操作，settlement 对账差异前端线在“只读展示 + 运行态验证”后正式收住，后端 resolve 接口继续保留但不在当前只读运营页暴露。
- Step 69 补充：已完成 settlement P3 主线阶段复盘，确认“批次操作审计线”和“对账差异线”都已在最小实现、前端只读承接和运行态验证后收住；当前 settlement P3 主线整体进入冻结/维护态，不继续为现有只读页补 review / withdraw / resolve 前端写动作。
- Step 70 补充：已完成异常历史线、售后执行历史线和 settlement P3 线的整体复盘，确认三条非 bridge 后端线都已达到“最小闭环 + 前端承接/验证”状态；当前不再默认继续扩单点后端能力，下一阶段优先转入整体维护/交付口径复盘。
- Step 71 补充：已完成整体维护 / 交付口径复盘，确认当前最小闭环已经达到“可试运营、可演示、可交接”的稳定状态；bridge 冻结态、展示 polish 冻结态、媒体线收住、非 bridge 后端三线收住四条主线当前都没有必须继续补的高优先级缺口，后续如继续推进应进入真正产品化方向规划。
- Step 72 补充：已在 `CampusCourierOpsView.vue` 接入腾讯地图 JS SDK 最小预览能力，使用现有配送员位置记录在 admin 只读运营页中展示真实地图点位；当前不改后端接口、不改 bridge、不改鉴权和路由。运行态验证确认 `CR202604060001` / 配送员 `id=2` 可在 `/campus/courier-ops` 看到腾讯地图比例尺与版权信息；同时确认当前 key 未开启 WebService API，因此本轮放弃静态图方案，改走 JS SDK 方案。
- Step 73 补充：已完成腾讯地图产品化线 go / no-go 评估，结论是不扩到第二个既有页面；当前地图能力正式收住为 `CampusCourierOpsView.vue` 的单页 admin 只读点位预览，继续保留 JS SDK 试点成果，但不做轨迹、路线、调度、导航或地图写操作。下一轮建议转入整体产品化下一阶段规划，而不是继续扩地图页面。
- Step 74 补充：已完成试运营版产品化下一阶段规划，评估地图扩展、模拟资金链路、试运营运行配置、admin 运营处理动作和真实部署等候选方向；最终结论是下一阶段优先做“试运营运行配置与 preflight 手册”，先固化 profile、启动方式、腾讯地图 key 本地配置、样本数据、模拟资金口径和关键页面 / 接口检查，不进入真实部署、不扩地图第二页、不新增页面。
- Step 75 补充：已新增 `docs/trial-operation-preflight.md`，明确 backend / frontend 启动方式、H2/test 与 MySQL/dev 边界、腾讯地图 key 本地配置、演示账号、样本订单、模拟资金链路口径、关键页面和关键接口 preflight；同时更新 `docs/delivery-guide.md`、`docs/README.md` 与根 `README.md` 的交付入口，修正“地图 SDK 未接入”的过期表述为“腾讯地图已在 admin courier ops 单页完成 JS SDK 只读点位预览，轨迹/调度仍未接入”。
- Step 76 补充：已新增 `docs/simulated-funds-boundary.md`，明确 mock-pay、售后模拟退款 / 执行、settlement 模拟打款、批次操作审计和对账差异都只属于试运营状态推进与运营审计，不代表真实支付、真实退款、真实打款、银行流水或第三方清结算；同步更新 `README.md`、`docs/README.md`、`docs/delivery-guide.md` 与 `docs/trial-operation-preflight.md` 的交付入口和资金口径，本轮没有修改业务代码、接口、路由、鉴权、bridge、地图代码或前端页面。
- Step 77 补充：已完成 `README.md`、`docs/delivery-guide.md`、`docs/trial-operation-preflight.md`、`docs/simulated-funds-boundary.md` 与 `project-logs/campus-relay/summary.md` 的口径复核，docs 范围内未发现“地图 SDK 未接入 / 真实支付已接入 / 真实退款已接入 / 真实打款已接入 / bridge 已可删除”等过期表述；同时完成 backend `.\mvnw.cmd -DskipTests compile`、frontend `npm run build` 和最小 preflight 运行态验证，确认 customer onboarding、customer completed 结果回看、courier workbench 无 token 提示页，以及 admin `/campus/settlements`、`/campus/after-sale-executions`、`/campus/courier-ops`、`/campus/exceptions` 均可正常打开。本轮未修改业务代码、接口、路由、鉴权、bridge、地图代码或前端页面。
- Step 78 补充：已基于 Step 40 到 Step 42 的交付文档、样本索引、截图、录屏和 Step 77 的 preflight 结果完成试运营交付包 RC 收口复盘；本轮额外确认 `project-logs/campus-relay/runtime/step-42-media/screenshots`、`videos`、`logs` 目录均存在，截图 15 张、录屏 5 段、frontend `5173` 与 backend `8080` 端口在本地 smoke 复核时可访问。结论是当前项目已经达到“可演示、可移交、可答辩、可复盘”的试运营 RC 状态，但仍保留非阻塞遗留项：`application-test.properties` 默认 `server.port=0`、frontend build 仍有 Sass `@import` 和 chunk size 告警、after-sale 固定真实样本仍非必须项。本轮未修改业务代码、接口、路由、鉴权、bridge、地图代码或前端页面。
- Step 79 补充：已将 `application-test.properties` 默认端口从随机端口收口为 `SERVER_PORT:8080`，使 `test profile + H2` 可直接用于浏览器联调，同时保留环境变量覆盖能力；前端 `global.scss` 与 `element-plus.scss` 已从 Sass `@import` 迁移到 `@use`，`npm run build` 不再出现 Sass `@import` 弃用告警。本轮未修改 bridge、接口、路由、鉴权、token 附着、地图代码或业务页面语义。
- Step 80 补充：已将 `Dashboard.vue` 与 `Statistics.vue` 从整包 `echarts` 引用切到共享按需注册入口 `frontend/src/utils/echarts.js`，图表共享 chunk 从约 `1.11 MB` 收敛到约 `545 KB`；同时在 `frontend/vite.config.js` 中把 `build.chunkSizeWarningLimit` 调整为 `1100`，与当前全局 `ElementPlus` vendor 基线对齐，`npm run build` 不再输出 Vite chunk size 告警。本轮未修改 bridge、接口、路由、鉴权、token 附着或业务页面语义。
- Step 81 补充：已完成前端打包线 freeze / no-op 复盘，确认 Step 80 后剩余大包主要来自当前全局 `ElementPlus` vendor 基线，而不是新的误打包问题；在没有真实性能压力或全局组件装配重构前，不再继续推进高风险按需拆分，前端打包优化线正式进入冻结 / no-op。本轮未修改业务代码、bridge、接口、路由、鉴权或页面行为。
- Step 82 补充：已完成试运营 RC 下一阶段主线重排 / go-no-go 评估。当前 bridge、展示 polish、媒体、地图和前端打包优化五条主线都已冻结或收住，因此下一阶段不再优先继续做局部功能或局部 polish；最终选择“试运营运维化最小能力”作为下一条真实主线，优先把启动、preflight、H2/reset、样本校验等日常试运营动作从文档步骤收敛成更可执行的本地操作层。非 bridge 后端继续深化与产品级上线前方案设计当前都不作为下一条主线。本轮未修改业务代码、bridge、接口、路由、鉴权或页面行为。
- Step 83 补充：已完成试运营运维化最小能力边界收敛并新增 `scripts/trial-operation/preflight.ps1` 与 `scripts/trial-operation/README.md`；该脚本检查关键文件、地图本地 key 变量、`.env.local` git 跟踪状态，可选检查 8080 / 5173 端口并可选执行 backend compile / frontend build。本轮未自动重置 H2、未启动长驻进程、未打印真实地图 key，且未修改 bridge、接口、路由、鉴权、前端页面或后端业务语义。
- Step 84 补充：已新增 `scripts/trial-operation/validate-samples.ps1`，用于只读校验 H2 seed / schema 中的试运营样本锚点；`preflight.ps1` 已新增 `-RunSampleValidation` 参数并能把样本校验 warning 识别为 warning 而非硬失败。本轮未连接数据库、未重置 H2、未写入样本、未改 bridge、接口、路由、鉴权、前端页面或后端业务语义。
- Step 85 补充：已新增 `scripts/trial-operation/commands.ps1`，用于输出本地试运营命令索引、浏览器入口和手动 H2 reset 说明；该脚本只打印命令，不启动长驻进程、不 kill 进程、不自动 reset H2。本轮未改 bridge、接口、路由、鉴权、前端页面或后端业务语义。
- Step 86 补充：已完成试运营脚本线收口 / no-op 冻结判断。当前 `preflight.ps1`、`validate-samples.ps1` 和 `commands.ps1` 已足够覆盖本地试运营的文件检查、样本锚点校验、常用命令索引和手动 H2 reset 口径；继续扩到 runtime smoke、自动 reset 或长驻进程管理的收益低于风险，因此脚本线进入维护态。本轮未改业务代码、bridge、接口、路由、鉴权、前端页面或后端业务语义。
- Step 87 补充：已完成产品级试运营前剩余差距清单 / go-no-go 评估。当前本地答辩 / 交付 RC 已足够，但产品级试运营仍缺环境变量样例、密钥注入边界、部署前 preflight、CI、监控、备份和外部服务降级策略；下一主线建议优先进入试运营环境与密钥配置硬化，而不是继续扩业务功能或脚本。
- Step 88 补充：已新增 `.env.example`、`backend/.env.example`、`frontend/.env.example` 和 `docs/deployment/env-and-secret-checklist.md`，明确环境变量、密钥、腾讯地图 key、DB、JWT、CORS 与上传目录的配置边界；本轮没有提交真实 key、没有改运行时行为、没有改业务代码、bridge、接口、路由或鉴权。
- Step 89 补充：已新增 `docs/deployment/post-deploy-smoke-checklist.md`，覆盖部署产物、基础访问、customer / courier / admin smoke、模拟资金口径、bridge 冻结口径和回滚触发；本轮没有新增脚本、没有改业务代码、bridge、接口、路由、鉴权或前端页面。
- Step 90 补充：已完成最小 CI 检查边界设计 / go-no-go，确认当前仓库尚无 `.github/workflows`；建议 Step 91 新增最小 GitHub Actions workflow，仅覆盖 backend compile、frontend build 和 sample validation，不做部署、不注入真实密钥、不跑 E2E、不改变 bridge。
- Step 91 补充：已新增 `.github/workflows/trial-operation-ci.yml`，包含 backend compile、frontend build 和 sample validation 三个 job；sample validation 的 warning exit code `2` 在 CI 中按 warning 处理，不使 CI 失败。本轮没有做部署、没有注入真实密钥、没有跑 E2E、没有改业务代码、bridge、接口、路由或鉴权。
- Step 92 补充：已确认 GitHub Actions `Trial Operation CI #1` 由 `450e823ec35f22dba9463c71e1b85b854b1aa6e5` 的 push 触发，`Backend compile`、`Frontend build`、`Trial sample validation` 三个 job 均成功；本地等价验证与远端 CI 结果一致。当前仅观察到 GitHub Actions Node.js runtime deprecation warning，建议下一轮单独评估 action 版本升级，不在 Step 92 混入 workflow 变更。
- Step 93 补充：已基于官方 action README 对 GitHub Actions runtime warning 做 go / no-go 评估，并执行最小 workflow 版本升级：`actions/checkout@v6`、`actions/setup-java@v5`、`actions/setup-node@v6`；CI job、触发条件、命令、Java 17 / Node 20 构建版本、sample validation warning-only 策略均保持不变。本轮没有改业务代码、bridge、接口、路由、鉴权或前端页面。
- Step 94 补充：已确认 `2406c1b2586996a1e0fdea1946394022894b3b0e` 对应的 GitHub Actions `Trial Operation CI #2` 远端运行成功，`Backend compile`、`Frontend build`、`Trial sample validation` 三个 job 均继续通过，最新 run 摘要页中未再观察到显性 runtime deprecation warning 文本。
- Step 95 补充：已新增内测型试运营 Compose 部署包，包括 `deploy/internal-trial/docker-compose.yml`、前后端 Dockerfile、Nginx 代理配置、compose `.env.example` 和部署说明文档；本轮没有改业务代码、bridge、鉴权、接口、路由或前端页面。
- Step 96 补充：已在单机公网服务器上真实拉起内测型试运营 Compose 栈，并修正两个部署层阻塞：一是移除 `deploy/internal-trial/docker-compose.yml` 中与 MySQL 8.4 不兼容的 `mysql_native_password` 启动参数，二是修正 `backend/db/init.sql` 中 `campus_relay_order` 种子列清单与样本数据漂移。当前远端 `mysql / backend / frontend` 三个容器均正常运行，`/` 返回 `HTTP 200`，且 admin/customer/courier 三类最小 smoke 接口均已通过公网验证，本项目现已达到“单机服务器可启动、可访问、可进行内测型试运营 smoke”的状态。
- Step 97 补充：已新增 `project-logs/campus-relay/global-working-memory.md`，把 bridge 冻结态、展示 polish 冻结态、媒体线收住、试运营部署现状和下一主线从 `summary.md` 的全量历史里单独抽离出来，作为后续上下文压缩后的快速恢复入口；本轮没有改业务代码、接口、部署逻辑、bridge、鉴权或页面。
- Step 98 补充：已新增 `deploy/internal-trial/backup-stack.sh`，并同步更新 `docs/deployment/backup-and-rollback.md`、`docs/deployment/internal-trial-compose.md`、`.gitignore` 与 `.dockerignore`，把单机服务器内测部署从“能起服务”推进到“有最小备份 / 回滚入口”。本轮已在真实服务器执行一轮备份，成功产出 MySQL dump、uploads 归档、`.env` 备份和 manifest，当前项目已达到“单机服务器可启动、可 smoke、可最小备份”的内测型试运营状态；本轮没有改业务代码、bridge、接口、路由、鉴权或前端页面。
- Step 99 补充：已新增 `deploy/internal-trial/restore-drill.sh`，并同步更新 `docs/deployment/backup-and-rollback.md` 与 `docs/deployment/internal-trial-compose.md`，把单机服务器内测部署从“可备份”推进到“可做非破坏性恢复演练”。本轮已在真实服务器成功执行一轮 restore drill：dump 已恢复到临时 MySQL 容器，关键样本订单 `CR202604070002` / `CR202604060001` 可回读，uploads 归档可解压，且现有 compose 栈保持运行不受影响；本轮没有改业务代码、bridge、接口、路由、鉴权或前端页面。
- Step 100 补充：已新增 `docs/deployment/internal-trial-ops-runbook.md`，并更新 `README.md`、`docs/README.md`、`docs/deployment/internal-trial-compose.md` 与 `docs/deployment/backup-and-rollback.md` 的入口，把单机服务器内测部署的启动、停止、日志、更新、备份、restore drill、smoke、回滚触发和 HTTPS / 域名 go-no-go 统一收敛成一份可交接 runbook。本轮复核服务器 compose 仍为 `Up`、本机 HTTP 返回 `200`、最新 backup manifest 存在；结论是当前内测阶段暂不强制进入 HTTPS / 域名 / 正式反向代理准备。
- 当前范围：后端最小闭环已扩展到 customer onboarding 替代链路、customer 侧 courier token 申请衔接、customer completed 结果回看页、用户端校园代送下单 / 我的代送单 / 模拟支付入口、courier workbench 最小承接页、最小接单动作、订单详情承接、最小取餐承接、最小 deliver 承接、最小异常上报承接、confirm 前可视化、completed 后最小只读承接与按订单号结果回读，并已在本地 `test profile + H2 + frontend vite` 下真实跑通 `onboarding -> 审核 -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报 -> customer confirm -> completed 回读` 一轮链路，且已整理成可共享回归留痕；Step 29 基于项目 owner 的明确确认关闭了 repo 外阻塞项，Step 30 则已把 `Phase A` 的执行边界、bridge 保留范围、回滚策略和最小回归清单正式固化，Step 31 已真实复核了一轮最小回归清单并评估最小候选动作，Step 32 在此基础上进一步扩大候选池并完成 go / no-go 决策，Step 33 则正式将 bridge 主线收成 `Phase A no-op` 冻结态；Step 34 到 Step 39 已完成展示 polish 候选评估、5 个关键页面小范围 polish 和展示 polish 冻结判断；Step 40 到 Step 43 已完成交付整理、截图/录屏计划、真实媒体采集和非 bridge 后端方向评估；Step 44 到 Step 49 已完成异常历史表、异常上报写历史、admin 异常只读/处理查询、最小 resolve 后端闭环、admin 异常前端承接和运行态验证；Step 50 到 Step 53 已完成售后执行历史表后端/前端承接与运行态验证；Step 54 到 Step 97 已完成 settlement 批次操作审计后端、前端、运行态验证、对账差异后端实现、对账差异前端最小只读承接、运行态验证、前端线收口评估、settlement P3 主线阶段复盘、非 bridge 后端三线整体复盘、整体维护 / 交付口径复盘、腾讯地图单页产品化试点、地图线收口判断、试运营版产品化下一阶段规划、试运营运行配置与 preflight 手册、模拟资金链路产品化边界说明、试运营交付文档一致性复核与最小 preflight 验证、试运营交付包 RC 收口复盘 / 最小 smoke 复核、试运营 RC 运行配置与构建告警减噪、前端打包告警与分包 go / no-go 评估、前端打包线 freeze / no-op 复盘、试运营 RC 下一阶段主线重排 / go-no-go 评估、试运营本地 preflight 脚本入口、试运营样本状态校验脚本、试运营命令索引、试运营脚本线 no-op 冻结判断、产品级试运营前剩余差距评估、环境变量与密钥配置硬化、部署后 smoke checklist、最小 CI 检查边界设计、最小 GitHub Actions CI 实现、CI 首轮运行结果跟踪、GitHub Actions runtime warning 最小处理、CI runtime 升级后远端验证、内测型试运营 Compose 部署包最小实现、单机服务器首轮内测部署 / smoke 验证和全局工作记忆日志建立；当前 bridge 完全保留、展示 polish 主线默认冻结、媒体线已收住、地图线收住为单页 admin 只读点位预览、前端打包优化线已冻结为 no-op，模拟资金链路仍不接真实支付 / 退款 / 打款，旧外卖模块仍保留可运行、旧前端主链路未被替换

## 当前状态

### 数据与领域

- 已并行新增表：
  - `campus_customer_profile`
  - `campus_courier_profile`
  - `campus_pickup_point`
  - `campus_relay_order`
  - `campus_exception_record`
  - `campus_location_report`
  - `campus_settlement_record`
  - `campus_settlement_reconcile_difference_record`
- 普通用户继续复用 `user`
- 管理员继续复用 `employee`
- 配送员继续通过 `campus_courier_profile.user_id` 关联 `user`
- 旧外卖表与旧控制器未删除，旧 `orders/cart/address` 语义未改

### 当前已开放的 campus 接口

- public：
  - `/api/campus/public/pickup-points`
  - `/api/campus/public/delivery-rules`
- customer：
  - `/api/campus/customer/orders`
  - `/api/campus/customer/orders/{id}`
  - `/api/campus/customer/orders/{id}/mock-pay`
  - `/api/campus/customer/orders/{id}/confirm`
  - `/api/campus/customer/orders/{id}/cancel`
  - `/api/campus/customer/orders/{id}/after-sale`
  - `/api/campus/customer/orders/{id}/after-sale-result`
  - `/api/campus/customer/courier-onboarding/profile`
  - `/api/campus/customer/courier-onboarding/review-status`
  - `/api/campus/customer/courier-onboarding/token-eligibility`
- courier：
  - `/api/campus/courier/auth/token`
  - `/api/campus/courier/profile`
  - `/api/campus/courier/review-status`
  - `/api/campus/courier/orders/available`
  - `/api/campus/courier/orders/{id}`
  - `/api/campus/courier/orders/{id}/accept`
  - `/api/campus/courier/orders/{id}/pickup`
  - `/api/campus/courier/orders/{id}/deliver`
  - `/api/campus/courier/orders/{id}/exception-report`
  - `/api/campus/courier/location-reports`
- admin：
  - `/api/campus/admin/orders`
  - `/api/campus/admin/orders/{id}`
  - `/api/campus/admin/orders/{id}/timeline`
  - `/api/campus/admin/orders/after-sale`
  - `/api/campus/admin/orders/{id}/after-sale-handle`
  - `/api/campus/admin/orders/{id}/after-sale-decision`
  - `/api/campus/admin/orders/{id}/after-sale-result`
  - `/api/campus/admin/orders/{id}/after-sale-execution`
  - `/api/campus/admin/orders/after-sale-executions`
  - `/api/campus/admin/orders/{id}/location-reports`
  - `/api/campus/admin/orders/{id}/exception-summary`
  - `/api/campus/admin/exceptions`
  - `/api/campus/admin/exceptions/{id}`
  - `/api/campus/admin/exceptions/{id}/resolve`
  - `/api/campus/admin/couriers`
  - `/api/campus/admin/couriers/{id}/review`
  - `/api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
  - `/api/campus/admin/couriers/{courierProfileId}/location-reports`
  - `/api/campus/admin/settlements`
  - `/api/campus/admin/settlements/{id}`
  - `/api/campus/admin/settlements/{id}/confirm`
  - `/api/campus/admin/settlements/{id}/payout-result`
  - `/api/campus/admin/settlements/batch-payout-result`
  - `/api/campus/admin/settlements/reconcile-summary`
  - `/api/campus/admin/settlements/payout-batches`
  - `/api/campus/admin/settlements/payout-batches/{batchNo}`
  - `/api/campus/admin/settlements/{id}/verify-payout`

### 状态与运营能力

- 订单主状态已覆盖：
  - `PENDING_PAYMENT`
  - `BUILDING_PRIORITY_PENDING`
  - `WAITING_ACCEPT`
  - `ACCEPTED`
  - `PICKED_UP`
  - `DELIVERING`
  - `AWAITING_CONFIRMATION`
  - `COMPLETED`
  - `CANCELLED`
  - `AFTER_SALE_OPEN`
  - `AFTER_SALE_RESOLVED`
  - `AFTER_SALE_REJECTED`
- `after_sale_execution_status` 独立于 `order_status`
  - `PENDING`
  - `SUCCESS`
  - `FAILED`
  - `NOT_REQUIRED`
- `payout_status` 独立于 `settlement_status`
  - `UNPAID`
  - `PAID`
  - `FAILED`
- `customerReceiptStatus` 继续只作为 customer 结果 VO 计算字段，不落数据库主状态
- `decisionType = REFUND / COMPENSATE` 记录后会自动初始化 `after_sale_execution_status = PENDING`
- `decisionType = NONE` 记录后会自动初始化 `after_sale_execution_status = NOT_REQUIRED`
- settlement confirm 后会自动初始化 `payout_status = UNPAID`
- `FAILED -> SUCCESS` 的售后执行人工纠正会写入 `after_sale_execution_corrected*` 审计字段，初次 `SUCCESS` 不算纠正
- batch payout 未传 `batchNo` 时会在 service 层自动生成统一批次号，只写入本次成功处理的 settlement 记录
- `payout_verified*` 字段独立表达二次核对结果，不复用 `settlement_status`
- courier 异常上报会新增 `campus_exception_record` 历史，并继续同步订单 latest exception 摘要；admin resolve 只更新异常历史处理字段，不改 `order_status`
- courier 位置上报仍只写 `campus_location_report`，不进入 timeline

### frontend 最小接入

- 已新增 customer 侧最小演示入口：
  - `/user/campus/after-sale-result`
  - `/user/campus/order-result`
  - `/user/campus/courier-onboarding`
- 已新增 courier 侧最小承接入口：
  - `/courier/workbench`
- 已新增 admin 侧最小演示入口：
  - `/campus/settlement-batches`
  - `/campus/settlement-batches/:batchNo`
  - `/campus/after-sale-executions`
  - `/campus/courier-ops`
  - `/campus/settlements`
- 已新增 customer 侧 API 封装：
  - `frontend/src/api/campus-customer.js`
- 已新增 courier 侧 API 封装：
  - `frontend/src/api/campus-courier.js`
- 已新增 admin 侧 API 封装：
  - `frontend/src/api/campus-admin.js`
- 已在 `frontend/src/utils/request.js` 放通 `/campus/customer/**` 的 customer token 附着
- 已在 admin 现有平铺路由体系中新增“校园结算批次”只读入口，没有另起第二套路由体系
- 已在 `frontend/src/views/user/Profile.vue` 追加 customer 轻量入口，不替换旧页面、不改旧登录主入口
- 已在 `frontend/src/views/CampusCourierOpsView.vue` 接入腾讯地图 JS SDK 最小地图预览，使用现有位置上报数据做只读点位展示
- 当前前端仍然只是最小联调与演示接入，不是完整校园代送前台，也不是完整校园代送后台

## Step 07 实际完成事项

1. 新增 customer onboarding 替代入口：
   - `POST /api/campus/customer/courier-onboarding/profile`
   - `GET /api/campus/customer/courier-onboarding/profile`
   - `GET /api/campus/customer/courier-onboarding/review-status`
   - `GET /api/campus/customer/courier-onboarding/token-eligibility`
2. onboarding 新入口只允许 `customer` token，不替代现有 `/api/campus/courier/auth/token`
3. onboarding 资料提交与旧 bridge 复用同一套 service 提交流程，没有复制两套资料提交流程
4. onboarding 首次提交或重提后，仍统一走现有资料状态机：
   - `reviewStatus = PENDING`
   - `enabled = 0`
   - 清空审核人和审核时间
5. customer onboarding 资料读取支持“未提交资料”默认态，便于前端页面首屏回填与联调
6. `canApplyCourierToken` 继续只作为 VO 计算字段：
   - `APPROVED` 且 `enabled = 1` 时为 `true`
   - 其他情况为 `false`
7. `token-eligibility.message` 继续只作为 VO 计算字段，不新增数据库字段
8. 为 `campus_courier_profile` 最小补齐 onboarding 所需字段：
   - `gender`
   - `campus_zone`
   - `enabled_work_in_own_building`
   - `applicant_remark`
9. 同步放宽旧桥接链路依赖的部分资料字段必填约束，保证 onboarding 新入口可在不引入新表的前提下稳定落到 `campus_courier_profile`
10. 新增 `CampusCourierOnboardingIntegrationTest`
11. 旧 bridge 兼容性测试继续保留并通过，确保 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 未被破坏
12. frontend 最小接入已落地：
   - 新增 customer 售后结果页 `frontend/src/views/user/AfterSaleResult.vue`
   - 新增 courier onboarding 页 `frontend/src/views/user/CourierOnboarding.vue`
   - 新增 customer campus API 封装 `frontend/src/api/campus-customer.js`
   - 新增用户侧路由 `/user/campus/after-sale-result` 与 `/user/campus/courier-onboarding`
   - 在 `frontend/src/views/user/Profile.vue` 追加“校园代送”入口区块
13. 前端接入保持“新增页面 + 新增轻量脚本”方式，没有重写旧页面，没有切旧主链路
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `.\mvnw.cmd "-Dtest=CampusCourierOnboardingIntegrationTest,CampusCourierProfileIntegrationTest" test`
   - `.\mvnw.cmd test`
   - `npm run build`
15. 当前累计 `57` 个后端测试通过，前端生产构建通过

## Step 08 实际完成事项

1. 本轮优先选择了 admin settlement 批次列表页与批次详情页，而不是继续扩 customer 页面
2. 选择 settlement 批次页的原因：
   - 批次字段更聚合，演示信号更强
   - 与 Step 06 的批次审计能力直接衔接
   - 风险低于继续扩 customer 页面或直接上售后执行分页页
   - 更适合作为首个 admin 只读运营演示页
3. 因现有 admin 页面采用顶层平铺视图与顶层路由风格，本轮继续沿用该风格：
   - `frontend/src/views/CampusSettlementBatchList.vue`
   - `frontend/src/views/CampusSettlementBatchDetail.vue`
   - 路由使用 `/campus/settlement-batches` 与 `/campus/settlement-batches/:batchNo`
4. 新增 admin 侧 API 封装：
   - `getCampusSettlementPayoutBatches`
   - `getCampusSettlementPayoutBatchDetail`
5. settlement 批次列表页能力：
   - 调用 `GET /api/campus/admin/settlements/payout-batches`
   - 支持 `payoutStatus` 与 `payoutVerified` 两个最小筛选
   - 展示 `payoutBatchNo`、`totalCount`、`paidCount`、`failedCount`、`verifiedCount`、`unverifiedCount`、`totalPendingAmount`、`firstRecordedAt`、`lastRecordedAt`
   - 支持点击批次进入详情页
6. settlement 批次详情页能力：
   - 调用 `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
   - 展示批次汇总卡片
   - 展示 records 明细表
   - 明细默认只读，不新增写操作
7. 为 admin 现有侧边栏新增“校园结算批次”入口，并补齐 breadcrumb 与高亮逻辑
8. 本轮没有改任何 campus 后端接口，也没有新增写接口，页面直接消费现有后端返回
9. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 现在承担未拿到 courier token 前的稳定前台 onboarding 入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容与旧桥接入口
10. Step 08 的 bridge 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
11. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
12. 本轮不扩 customer 业务页面，不扩 admin 写操作，不动旧前端主链路

## Step 09 实际完成事项

1. 本轮优先选择了 admin 售后执行分页演示页，而不是继续扩 customer 页面
2. 选择售后执行分页页的原因：
   - Step 08 已经有 settlement 批次页，继续补第二个 admin 只读运营页，能更快形成演示闭环
   - `GET /api/campus/admin/orders/after-sale-executions` 与 `GET /api/campus/admin/orders/{id}/after-sale-result` 已存在，前端可直接复用，不需要新增写接口或改状态机
   - 当前重点是 admin 运营只读可视化，不是继续扩 customer 页面
3. 延续现有 admin 顶层平铺视图与顶层路由风格，没有另起第二套路由体系：
   - `frontend/src/views/CampusAfterSaleExecutionList.vue`
   - 路由 `/campus/after-sale-executions`
4. 新增 admin 侧 API 封装：
   - `getCampusAfterSaleExecutions`
   - `getCampusAdminAfterSaleResult`
5. 售后执行页能力：
   - 调用 `GET /api/campus/admin/orders/after-sale-executions`
   - 支持 `afterSaleExecutionStatus`、`decisionType`、`correctedOnly` 三个最小筛选
   - 列表展示订单号、订单状态、用户 ID、配送员 ID、决策类型、决策金额、执行状态、执行备注、人工纠正标记、执行时间、纠正时间
   - 通过 drawer 复用 `GET /api/campus/admin/orders/{id}/after-sale-result` 查看单笔售后结果汇总
6. 为 admin 现有侧边栏新增“校园售后执行”入口，并补齐 breadcrumb 与菜单高亮逻辑
7. 本轮没有改任何 campus 后端接口，没有新增写接口，没有改数据库
8. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 现在承担未拿到 courier token 前的稳定 customer onboarding 入口与前端首选入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容与 bridge 入口
9. Step 09 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
10. 达到以下条件后，才进入“逐步收口 bridge”评估：
   - onboarding 新入口完成稳定联调与演示
   - 历史调用方完成迁移盘点
   - 未拿 courier token 前的主要资料提交与状态查询场景已被 customer onboarding 覆盖
11. 执行：
   - `npm run build`
12. 本轮继续只做只读 admin 演示页，不扩 customer 主链路，不切旧前端主入口

## Step 10 实际完成事项

1. 本轮优先选择了 admin courier 异常/位置联动视图，而不是单独做 after-sale result 汇总页
2. 选择 courier 异常/位置联动视图的原因：
   - Step 09 已经在售后执行页通过 drawer 复用了 `after-sale-result` 接口，再单独做汇总页收益较低
   - 当前更需要补齐第三个 admin 只读运营页，扩展示范面，而不是继续堆叠售后视图
   - `/api/campus/admin/couriers`、`/api/campus/admin/couriers/{courierProfileId}/exceptions/recent`、`/api/campus/admin/couriers/{courierProfileId}/location-reports` 已存在，足够支撑前端最小联动页
3. 延续现有 admin 顶层平铺视图与顶层路由风格，没有另起第二套路由体系：
   - `frontend/src/views/CampusCourierOpsView.vue`
   - 路由 `/campus/courier-ops`
4. 新增 admin API 封装：
   - `getCampusCouriers`
   - `getCampusCourierRecentExceptions`
   - `getCampusCourierLocationReports`
5. 页面布局采用左侧 courier 列表、右侧异常列表与位置记录两块联动区：
   - 左侧 courier 列表调用 `GET /api/campus/admin/couriers`
   - 右上异常区调用 `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
   - 右下位置区调用 `GET /api/campus/admin/couriers/{courierProfileId}/location-reports`
6. 页面行为：
   - 默认自动选中当前 courier 列表页第一条记录
   - courier 列表为空时，右侧统一展示空态
   - 不接地图 SDK，不画轨迹，不做实时刷新
   - 位置记录按后端现有字段只读展示，不在前端重建定位语义
7. courier 列表页最小筛选使用后端现有真实参数：
   - `realName`
   - `reviewStatus`
   - `enabled`
8. 位置记录列表分页继续沿用后端现有 `page/pageSize` 风格
9. 为 admin 现有侧边栏新增“校园配送运营”入口，并补齐 breadcrumb 与菜单高亮
10. 本轮没有改任何后端接口、VO、数据库和状态机
11. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 继续承担未拿 courier token 前的稳定 onboarding 新入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容入口
12. Step 10 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
13. 达到以下条件后，才进入“逐步收口 bridge”评估：
   - onboarding 新入口完成稳定联调与演示
   - 历史调用方完成迁移盘点
   - customer onboarding 页面已覆盖未拿 courier token 前的主要资料提交和状态查询场景
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
15. 本轮继续只做只读 admin 演示页，不扩 customer 主链路，不切旧前端主入口

## Step 11 实际完成事项

1. 本轮优先选择了 admin settlement 只读运营页，而不是单独做 after-sale result 汇总页
2. 选择 settlement 只读运营页的原因：
   - Step 09 已经在售后执行页通过 drawer 复用了 `GET /api/campus/admin/orders/{id}/after-sale-result`
   - 单独再做 after-sale result 汇总页会和 Step 09 的能力高度重叠
   - 当前更适合补齐第四个 admin 只读运营页，形成更完整的 settlement 运营演示面
   - `GET /api/campus/admin/settlements`、`GET /api/campus/admin/settlements/reconcile-summary`、`GET /api/campus/admin/settlements/{id}` 已存在，足够支撑页面，不需要扩后端
3. 延续现有 admin 顶层平铺视图与顶层路由风格，没有另起第二套路由体系：
   - `frontend/src/views/CampusSettlementOpsView.vue`
   - 路由 `/campus/settlements`
4. 新增 admin 侧 API 封装：
   - `getCampusSettlements`
   - `getCampusSettlementReconcileSummary`
   - `getCampusSettlementDetail`
5. 页面布局采用“顶部摘要卡片 + 下方结算表格 + 详情 drawer”：
   - 摘要区调用 `GET /api/campus/admin/settlements/reconcile-summary`
   - 列表区调用 `GET /api/campus/admin/settlements`
   - 详情 drawer 调用 `GET /api/campus/admin/settlements/{id}`
6. 列表页最小真实筛选使用后端现有参数：
   - `settlementStatus`
   - `payoutStatus`
   - `courierProfileId`
   - `relayOrderId`
7. 页面行为：
   - 默认加载摘要和列表
   - 点击单条记录后打开详情 drawer
   - 列表为空时摘要区仍正常显示，表格区展示空态
   - 全部保持只读，不新增写操作按钮
8. 为 admin 现有侧边栏新增“校园结算运营”入口，并补齐 breadcrumb 与菜单高亮
9. 本轮没有改任何后端接口、VO、数据库和状态机
10. bridge 并行策略继续固化：
   - `customer/courier-onboarding/*` 继续承担未拿 courier token 前的稳定 onboarding 新入口
   - 旧 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续承担历史兼容入口
11. Step 11 观察指标明确为：
   - onboarding 页面是否稳定联调
   - 是否仍有历史调用依赖旧 bridge
   - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
12. 达到以下条件后，才进入“逐步收口 bridge”评估：
   - onboarding 新入口完成稳定联调与演示
   - 历史调用方完成迁移盘点
   - customer onboarding 页面已覆盖未拿 courier token 前的主要资料提交和状态查询场景
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
14. 本轮继续只做只读 admin 演示页，不扩 customer 主链路，不切旧前端主入口

## Step 12 实际完成事项

1. 本轮优先做“收口型增强”，没有机械新增第五个 admin 页。
2. customer onboarding 页面已从“只能看审核状态与 token 资格”升级为“可直接申请 courier token”：
   - 继续复用 `GET /api/campus/customer/courier-onboarding/token-eligibility`
   - 直接接入现有 `POST /api/campus/courier/auth/token`
3. onboarding 页面行为已补齐：
   - `eligible = true` 时展示“申请 courier token”按钮
   - 用户输入当前登录账号密码后即可申请
   - 成功后显示申请成功提示、返回 token、返回的 `courierProfile` 摘要
   - 失败时继续直接展示后端返回错误信息
4. 本轮没有新建后端认证接口，也没有改状态机；前端沿用现有 `phone + password` 调用方式。
5. 为避免 token 只展示不生效，本轮最小补齐了 courier token 的前端会话能力：
   - 申请成功后写入 `localStorage.courier_token`
   - 同时写入 `localStorage.courier_profile`
   - `request.js` 已开始为 `/api/campus/courier/orders/**` 与 `/api/campus/courier/location-reports` 附加 `courier_token`
   - customer logout 或 courier 相关 401 时会清理本地 courier token
6. 本轮没有改 `/api/campus/courier/profile`、`/api/campus/courier/review-status`、`/api/campus/courier/auth/token`，bridge 仍完整保留。
7. 本轮对现有 admin 演示页做了最小细化，而不是新加第五页：
   - `CampusSettlementOpsView.vue`
   - `CampusAfterSaleExecutionList.vue`
   - `CampusCourierOpsView.vue`
8. 细化点固定为：
   - 顶部说明 alert 更明确
   - 空态文案更清晰
   - “暂无”字段展示统一
   - 非金额型处理显示为“无金额型处理”
9. 本轮不补第五个 admin 页的原因：
   - 当前更高优先级是把 onboarding 新入口真正闭环到 token 申请
   - 现有四个 admin 演示页已经足够支撑本阶段演示
   - 继续机械加页的收益低于先把 onboarding 与 bridge 观察项做实
10. Step 12 后 bridge 观察项比之前更具体：
   - 新 onboarding 页面已覆盖资料提交、资料读取、审核状态查询、资格判断、token 申请五个前置场景
   - 旧 bridge 继续覆盖历史调用方和双 token 兼容读取
   - 进入“逐步收口 bridge”前，仍需补足历史调用盘点与一轮稳定联调证据
11. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
12. 本轮没有改 backend 接口、数据库和测试集，只做前端接入与演示细化。

## Step 13 实际完成事项

1. 本轮优先补的是 courier token 申请成功后的最小前台承接页，而不是继续机械新增第五个 admin 页。
2. 新增 courier workbench 页面：
   - `frontend/src/views/courier/CourierWorkbench.vue`
   - 路由 `/courier/workbench`
3. courier workbench 的定位固定为“token 获取后的最小承接页”，不扩成完整 courier 端，不补地图、不补大屏、不补完整接单页群。
4. 页面结构保持最小三段：
   - 身份状态卡：展示 `courierProfileId`、`realName`、`reviewStatus`、`enabled`、token 状态
   - 可接单预览：调用 `GET /api/campus/courier/orders/available`，只展示前 5 条
   - 快捷入口区：刷新工作台、返回 onboarding 页面、返回个人中心
5. 页面行为固定为：
   - 本地存在 `courier_token` 时，自动读取 `GET /api/campus/courier/profile`、`GET /api/campus/courier/review-status`、`GET /api/campus/courier/orders/available`
   - 本地不存在 `courier_token` 时，不调用 courier 业务接口，只展示明确空态和回退入口
   - available orders 为空时，只读展示空态，不做异常处理分叉
6. 为支撑 workbench，本轮新增 courier 前端 API 封装：
   - `getCourierProfile`
   - `getCourierReviewStatus`
   - `getCourierAvailableOrders`
7. `request.js` 已最小修正 courier token 附着范围：
   - `/api/campus/courier/orders/**`
   - `/api/campus/courier/location-reports`
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
8. 对 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留 bridge 兼容策略：
   - 优先使用 `courier_token`
   - 若没有 `courier_token`，回退使用 `customer_token`
9. onboarding 页面已继续打通到 workbench：
   - 申请 courier token 成功后，页面展示“前往 courier 工作台”按钮
10. customer 个人中心已新增“配送员工作台”轻量入口，但没有改旧主链路，没有替换旧页面。
11. Step 13 后 bridge 收口证据链更具体了：
   - 新入口已覆盖资料提交、资料读取、审核状态、资格判断、token 申请、token 后最小承接六个前端场景
   - 旧 bridge 仍承担历史兼容读取与双 token 过渡场景
   - 仍缺历史调用盘点与一轮稳定联调回归证据，因此本轮结论仍是“继续保留，但已接近具备收口评估条件”
12. 本轮没有补第五个 admin 页。
13. 不补第五页的原因：
   - 当前更高优先级是让 onboarding 页面不再停在“拿到 token 即结束”
   - workbench 比新增展示页更直接决定 onboarding 替代链路是否成立
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
15. 本轮没有改 backend 接口、数据库和状态机，只做前端最小承接与 bridge 证据链细化。

## Step 14 实际完成事项

1. 本轮优先做的是两件事：
   - 盘点旧 bridge 的真实调用方
   - 给 courier workbench 补一个最小可接单动作
2. 当前仓库内对旧 bridge 的前端直接调用已经盘点完成：
   - `frontend/src/api/campus-courier.js`
     - `GET /api/campus/courier/profile`
     - `GET /api/campus/courier/review-status`
   - 这两个 API 在当前 frontend 代码里只被 `frontend/src/views/courier/CourierWorkbench.vue` 调用
3. 当前仓库内没有发现其他前端页面继续直接调用上述两个旧 bridge 接口。
4. `request.js` 中这两类请求的 token 附着策略已再次确认：
   - 优先 `courier_token`
   - 若不存在 `courier_token`，才回退到 `customer_token`
5. 因此，本轮已能明确区分：
   - 当前仍在仓库内依赖旧 bridge endpoint 的调用：`CourierWorkbench.vue`
   - 但该调用运行时应走 `courier_token`
   - customer 侧 onboarding 前置场景已由 `customer/courier-onboarding/*` 覆盖，不再依赖旧 bridge 做 customer 读取
6. bridge 证据链本轮已更具体：
   - 当前仓库内前端没有发现“仍必须通过 customer_token 调用 `/api/campus/courier/profile` 或 `/api/campus/courier/review-status` 才能完成”的页面场景
   - 但是否存在仓库外历史调用、手工调用或其他客户端依赖，当前仍未完成盘点
7. courier workbench 已从“只读承接页”升级为“最小接单承接页”：
   - 新增 `acceptCourierOrder`
   - workbench 可直接调用 `POST /api/campus/courier/orders/{id}/accept`
   - 接单成功后立即刷新可接单预览列表
   - 接单失败时继续展示后端原错误信息
   - 无 `courier_token` 时不调用 courier 业务接口
8. 本轮没有改 backend 接口、数据库和状态机。
9. 本轮没有补第五个 admin 页。
10. 不补第五页的原因：
   - 当前更高优先级是把 courier onboarding 替代链路的“token 获取后承接动作”补成最小闭环
   - 继续机械补展示页的收益低于把 workbench 做到“看得到也能接第一单”
11. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 15 实际完成事项

1. 本轮继续优先做两件事：
   - 把 bridge 收口证据链从“repo 内已盘点”推进到“repo 外依赖边界已说明”
   - 给 courier workbench 补一个接单后的最小详情承接点
2. 旧 bridge 依赖评估进一步具体化：
   - repo 内已确认调用方仍只有 `frontend/src/views/courier/CourierWorkbench.vue`
   - 这两个旧 bridge endpoint 在 repo 内只通过 `frontend/src/api/campus-courier.js` 暴露
   - 当前 repo 内已经没有发现 customer 页面继续依赖 `customer_token` 调用 `/api/campus/courier/profile` 或 `/api/campus/courier/review-status`
3. 对 repo 外依赖的结论本轮明确为：
   - 由于当前仓库内没有其他调用方，repo 外依赖无法从代码中继续穷举
   - 现阶段只能将 repo 外依赖范围定义为：历史客户端、手工联调脚本、未纳入本仓库的旧页面或外部调用方
   - 这部分不能编造，只能作为待核实边界保留
4. 因此，本轮 bridge 结论更明确：
   - 已经具备进入“逐步收口评估”的基础
   - 真正收口前还差两类证据：
     - repo 外依赖确认
     - 一轮稳定联调与回归记录
5. courier workbench 已补“订单详情承接点”：
   - 采用 drawer 方案，没有新增独立页面
   - 在可接单预览表格中增加“详情”按钮
   - 调用 `GET /api/campus/courier/orders/{id}`
6. 详情 drawer 最小展示字段：
   - 订单号
   - 订单状态
   - 取餐点
   - 配送楼栋
   - 配送详情
   - 订单金额
   - 取餐码
   - customer 备注
   - 接单时间
   - 创建时间
   - 更新时间
   - 当前 courierProfileId
7. 接单成功后，workbench 现在会：
   - 先调用 `POST /api/campus/courier/orders/{id}/accept`
   - 刷新可接单列表
   - 自动打开该订单详情 drawer
8. 无 `courier_token` 时：
   - 页面不调用详情接口
   - 继续只展示空态和回退入口
9. 本轮没有改 backend 接口、数据库和状态机
10. 本轮没有补第五个 admin 页
11. 不补第五页的原因：
   - 当前更高优先级仍是把 onboarding 替代链路做成“token 前 + token 后”连续闭环
   - workbench 详情承接比新增一个展示页更直接支撑 bridge 收口评估
12. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 16 实际完成事项

1. 本轮新增 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 文档中已经明确拆分：
   - repo 内已确认调用
   - repo 内可明确排除的依赖
   - repo 外暂未确认依赖范围
   - 进入逐步收口计划前仍缺的证据
   - Phase A / B / C 分阶段建议
   - 回滚与兼容保留策略
3. 本轮 bridge 结论已推进为：
   - 已可以进入“逐步收口计划设计阶段”
   - 但仍不能进入实际删除阶段
4. courier workbench 已补最小取餐承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入 `POST /api/campus/courier/orders/{id}/pickup`
5. workbench 没有新建页面，直接在订单详情 drawer 中新增最小取餐区：
   - `pickupProofImageUrl`
   - `courierRemark`
   - “确认取餐”
6. 本轮按后端真实 DTO 接入，没有臆造取餐码或新接口。
7. 接单成功后：
   - 仍会刷新可接单列表
   - 自动打开订单详情 drawer
   - 用户可以继续在 drawer 中尝试取餐
8. 取餐成功后：
   - 页面显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
9. 取餐失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页
12. 不补第五页的原因：
   - 当前更高优先级是把 bridge 收口评估补成“可进入计划设计阶段”
   - workbench 的最小取餐承接比继续补展示页更直接支撑主链路闭环
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 17 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮把 bridge 评估重点从“能否做计划设计”推进为“能否进入 Phase A 执行准备”。
3. 当前文档结论已明确：
   - repo 内证据已经稳定
   - repo 外依赖仍只能列为待确认边界
   - 当前还不具备进入 Phase A 执行准备的完整条件
4. courier workbench 已补最小 deliver 承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入 `POST /api/campus/courier/orders/{id}/deliver`
5. deliver 没有新建页面，直接放在订单详情 drawer 中。
6. 本轮按后端真实 DTO 接入，没有臆造 deliveredAt、proofImageUrl 等字段：
   - `courierRemark`
7. deliver 区按真实状态机变化显示动作：
   - `PICKED_UP -> 开始配送`
   - `DELIVERING -> 确认送达`
8. 取餐成功后，当前详情刷新为 `PICKED_UP` 时，用户可继续在同一 drawer 中操作 deliver。
9. deliver 成功后：
   - 显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
10. deliver 失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
11. 本轮没有改 backend 接口、数据库和状态机
12. 本轮没有补第五个 admin 页
13. 不补第五页的原因：
   - 当前更高优先级是把 bridge 评估推进到“执行前评估”层
   - workbench 的最小 deliver 承接比继续补展示页更直接支撑主链路闭环
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 18 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮把 bridge 评估重点从“能否进入执行前评估”推进为“能否进入执行准备”。
3. 文档新增了 `Step 18 执行准备清单`，把以下事项明确列为“待人工核实项”：
   - 是否还有仓库外旧页面直接调用 `/api/campus/courier/profile`
   - 是否还有仓库外旧页面直接调用 `/api/campus/courier/review-status`
   - 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge
   - 是否已有一轮 `customer onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的稳定联调记录
   - workbench 在纯 `courier_token` 路径下是否稳定
4. 本轮 bridge 结论已进一步具体化：
   - repo 内证据已经稳定
   - repo 外依赖仍只能列为待人工核实边界
   - 当前还不具备进入 `Phase A` 执行准备的完整条件
   - 缺口已经收敛到执行准备清单，而不是泛化观察
5. courier workbench 已补最小异常上报承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入 `POST /api/campus/courier/orders/{id}/exception-report`
6. exception-report 没有新建页面，直接放在订单详情 drawer 中。
7. 本轮按后端真实 DTO 接入，没有臆造 proofImageUrl 等字段：
   - `exceptionType`
   - `exceptionRemark`
8. 异常上报区只在后端真实允许的状态下可用：
   - `ACCEPTED`
   - `PICKED_UP`
   - `DELIVERING`
   - `AWAITING_CONFIRMATION`
9. 异常上报成功后：
   - 显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
10. 异常上报失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
11. 本轮没有改 backend 接口、数据库和状态机
12. 本轮没有补第五个 admin 页
13. 不补第五页的原因：
   - 当前更高优先级是把 bridge 执行准备证据写实
   - workbench 的最小异常上报承接比继续补展示页更直接支撑 courier 主链路
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 19 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮没有伪造 repo 外依赖结果，而是把执行准备缺口继续改造成可人工关闭的模板文档。
3. 新增 bridge 人工核实 checklist：
   - `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
4. checklist 中为每个待人工核实项提供了统一结构：
   - 核实方法
   - 记录位置
   - 核实结果
   - 负责人
   - 日期
   - 是否通过
5. 新增 bridge 联调/回归模板：
   - `project-logs/campus-relay/bridge-regression-template.md`
6. 回归模板已按完整链路拆分：
   - customer onboarding 提交资料
   - customer 查看审核状态
   - customer 申请 courier token
   - courier workbench 加载 profile/review-status
   - courier 接单
   - courier 取餐
   - courier deliver
   - courier 异常上报
   - customer 侧确认前状态可视化或结果查看
7. courier workbench 本轮没有新增页面，继续复用订单详情 drawer。
8. 本轮新增了“送达后 / confirm 前”状态区：
   - 当订单状态为 `AWAITING_CONFIRMATION` 时，显示“已送达，等待用户确认”
   - 当订单状态为 `COMPLETED` 时，显示“订单已完成”的最小只读状态
9. confirm 前状态区继续只读复用现有详情字段：
   - `status`
   - `deliveredAt`
   - `exceptionType`
   - `exceptionRemark`
   - `acceptedAt`
   - `pickedUpAt`
   - `deliveredAt`
   - `autoCompleteAt`
   - `updatedAt`
10. deliver 成功后刷新详情时，如果订单进入 `AWAITING_CONFIRMATION`，可直接在同一 drawer 中看到等待确认区，不需要新页面。
11. 本轮再次确认 `request.js` 的 courier token 附着范围已足够覆盖：
   - `/api/campus/courier/orders/**`
   - `/api/campus/courier/location-reports`
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
12. 本轮没有补第五个 admin 页。
13. 不补第五页的原因：
   - 当前更高优先级是把 bridge 执行准备缺口变成可落地模板
   - confirm 前可视化比继续补一个展示页更直接支撑 courier 主链路闭环
14. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 20 实际完成事项

1. 本轮继续更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮没有伪造任何 repo 外依赖结果，也没有代填任何人工核实结论。
3. 本轮把 bridge 模板继续补成“可真正执行和可真正填写”的状态：
   - `bridge-execution-readiness-checklist.md`
   - `bridge-regression-template.md`
4. checklist 本轮新增了每个核实项的执行辅助字段：
   - 推荐执行入口
   - 推荐证据类型
   - 失败时如何记录
   - 是否影响进入 `Phase A`
5. regression template 本轮新增了每一步的联调辅助字段：
   - 接口观察点
   - 关键状态字段
   - 失败记录建议
   - 是否为阻塞项
6. 本轮结论继续保持真实边界：
   - 模板已经可执行
   - 但真实人工核实结果仍待补齐
   - 当前仍不具备进入 `Phase A` 执行准备的完整条件
7. courier workbench 本轮没有新增页面，继续复用 `CourierWorkbench.vue` 的订单详情 drawer。
8. 本轮新增了 completed 后最小只读承接：
   - `AWAITING_CONFIRMATION` 时继续展示等待用户确认态
   - `COMPLETED` 时展示明确的已完成摘要态
9. completed / confirm 后状态区继续只读复用现有详情字段：
   - `status`
   - `pickupPointName`
   - `deliveryBuilding`
   - `deliveryDetail`
   - `totalAmount`
   - `customerRemark`
   - `deliveredAt`
   - `autoCompleteAt`
   - `exceptionType`
   - `exceptionRemark`
   - `updatedAt`
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页
12. 不补第五页的原因：
   - 当前更高优先级是把 bridge 模板从“存在”推进到“可真正执行”
   - courier workbench 的 completed 后最小承接比继续机械补展示页更直接支撑主链路闭环
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 21 实际完成事项

1. 本轮继续更新 bridge 评估与执行准备文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
   - `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
   - `project-logs/campus-relay/bridge-regression-template.md`
2. 本轮没有伪造 repo 外依赖结果，也没有把待执行项写成“已通过”。
3. 本轮补齐了 repo 内真实可执行验证：
   - 真实调用 `POST /api/campus/courier/auth/token` 成功取得 `courier_token`
   - 真实打开 `/courier/workbench`
   - 真实抓取 `/api/campus/courier/profile`、`/api/campus/courier/review-status`、`/api/campus/courier/orders/available` 请求
   - 真实确认 `profile` / `review-status` 优先走 `courier_token`
   - 真实确认无 `courier_token` 时页面停在空态，且不再请求 courier 业务接口
4. checklist 本轮已填写一项真实通过项：
   - workbench 的 `profile / review-status` 请求是否全程只走 `courier_token`
5. checklist 本轮也明确留下了一项真实但未完成的结果：
   - 当前 H2 种子下 `available orders = 0`
   - 因此还没有形成完整的 `接单 -> 取餐 -> deliver -> 异常上报` 真实联调记录
6. regression template 本轮已补入真实执行结果：
   - token 申请接口层验证
   - workbench 加载验证
   - completed 结果回读验证
   - 未执行步骤保持为未执行，没有代填成功
7. courier workbench 本轮继续复用 `CourierWorkbench.vue`，没有新建页面群。
8. 本轮新增了最小“按订单号查看详情”入口：
   - 用于在当前可接单列表为空时，仍可读取已完成订单详情
   - 直接复用 `GET /api/campus/courier/orders/{id}`
9. 已真实验证 completed 结果回读：
   - 通过订单号 `CR202604060001`
   - drawer 成功展示 `COMPLETED` 状态、`deliveredAt`、`autoCompleteAt` 和完成后最小只读承接区
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页
12. 不补第五页的原因：
   - 当前更高优先级是把 bridge 评估推进到“有真实局部留痕”
   - completed 结果回读比继续机械补展示页更直接支撑 courier 主链路与收口评估
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 22 实际完成事项

1. 本轮先解决了本地 `available orders = 0` 的真实联调阻塞，而不是继续堆前端展示页。
2. 在 `backend/src/main/resources/db/data-h2.sql` 新增了一笔 H2 联调样本：
   - 订单号：`CR202604070002`
   - customer：`13900139001`
   - pickup point：`2 / NORTH_GATE_LOCKER`
   - delivery building：`梅园`
   - 状态：`WAITING_ACCEPT`
   - paymentStatus：`PAID`
3. 这笔数据使 `GET /api/campus/courier/orders/available?page=1&pageSize=10` 在本地 `test profile + H2` 下真实返回至少一条可接单记录。
4. 本轮同时修正了一个 repo 内真实阻塞：
   - `/courier/workbench` 在只有 `courier_token` 时会被 `UserLayout` 的 customer 购物车请求拉回 `/user/login`
   - 现在 `frontend/src/layout/UserLayout.vue` 在无 `customer_token` 时不再请求 customer 购物车接口，且 `/courier/**` 不再渲染 customer 导航
5. 本轮真实跑通了一轮完整本地链路：
   - customer onboarding 提交资料
   - admin 审核通过
   - customer 查看 token eligibility
   - customer 申请 courier token
   - workbench 加载 profile / review-status / available orders
   - courier 接单
   - courier 取餐
   - courier deliver 到 `DELIVERING`
   - courier deliver 到 `AWAITING_CONFIRMATION`
   - courier 异常上报
   - customer 确认送达
   - courier completed 结果回读
6. 本轮真实联调样本账号：
   - courier candidate / onboarding：`13900139000 / 123456`
   - customer 订单所属人：`13900139001 / 123456`
   - admin：`13800138000 / 123456`
7. 本轮真实样本订单：
   - `CR202604070002`
   - 状态推进：`WAITING_ACCEPT -> ACCEPTED -> PICKED_UP -> DELIVERING -> AWAITING_CONFIRMATION -> COMPLETED`
8. bridge checklist 已补入真实结果：
   - workbench 的 `profile / review-status` 已在纯 `courier_token` 路径下真实验证通过
   - `onboarding -> token -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报 -> customer confirm -> completed 回读` 已在本地真实跑通
9. bridge regression template 已不再只是模板空位，已经补入第一轮真实留痕。
10. 当前 bridge 仍不删除，原因已收敛到 repo 外依赖人工核实，而不是 repo 内页面或 H2 数据阻塞。
11. 本轮没有补 customer confirm 结果回看新页面，也没有补第五个 admin 页。
12. 不补这些页面的原因：
   - 当前最高优先级是先把本地完整链路真实跑通，并把 bridge 评估从“可执行模板”推进到“有真实本地留痕”
13. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 23 实际完成事项

1. 本轮没有伪造新的联调结果，而是把 Step 22 已真实跑通的本地链路整理成可共享、可复用、可答辩说明的证据文档：
   - `project-logs/campus-relay/step-23-shared-regression-evidence.md`
2. 共享回归证据文档已真实整理以下信息：
   - customer 账号 `13900139001 / 123456`
   - courier onboarding / token 申请账号 `13900139000 / 123456`
   - admin 账号 `13800138000 / 123456`
   - 样本订单 `CR202604070002`
   - 状态流转 `WAITING_ACCEPT -> ACCEPTED -> PICKED_UP -> DELIVERING -> AWAITING_CONFIRMATION -> COMPLETED`
   - 对应接口、页面入口、日志位置、现有证据和待补截图项
3. 本轮新增 customer 侧最小 completed 结果回看页：
   - 路由 `/user/campus/order-result`
   - 页面文件 `frontend/src/views/user/CampusOrderResult.vue`
4. completed 结果回看页复用现有读取接口：
   - `GET /api/campus/customer/orders/{id}`
   - 没有新增后端写接口，没有改状态机
5. 页面按真实字段展示：
   - `status`
   - `deliveredAt`
   - `autoCompleteAt`
   - `pickupPointName`
   - `deliveryBuilding`
   - `deliveryDetail`
   - `totalAmount`
   - `remark`
   - `exceptionType`
   - `exceptionRemark`
   - `updatedAt`
6. 页面按真实状态做最小只读提示：
   - `AWAITING_CONFIRMATION` 时显示等待确认文案
   - `COMPLETED` 时显示已完成结果回看文案
7. 在 `frontend/src/views/user/Profile.vue` 新增“代送结果回看”入口，不改旧 `orders/cart/address` 主链路。
8. 在 `frontend/src/api/campus-customer.js` 补充：
   - `getCampusCustomerOrderDetail`
9. 本轮继续更新 bridge 相关文档，但没有把 repo 外人工核实项写成已完成：
   - `bridge-phaseout-evaluation.md`
   - `bridge-execution-readiness-checklist.md`
   - `bridge-regression-template.md`
10. `bridge-regression-template.md` 本轮明确补入：
    - Step 22 已跑通完整链路的共享证据引用
    - Step 23 customer completed 结果回看页的真实验证结果
11. 本轮没有补第五个 admin 页。
12. 不补第五页的原因：
    - 当前更高优先级是把 Step 22 的真实链路沉淀成可共享留痕
    - 以及补 customer confirm 后的最小结果回看
13. 执行：
    - `.\mvnw.cmd -DskipTests compile`
    - `npm run build`

## Step 24 实际完成事项

1. 本轮没有新增 repo 外人工核实结果，也没有把任何待人工核实项改写成“已通过”。
2. 继续增强了 repo 外人工核实准备：
   - `bridge-execution-readiness-checklist.md` 对未关闭项补齐了：
     - 去哪里核
     - 看什么证据
     - 成功时如何留痕
     - 失败时如何留痕
     - 是否阻塞 `Phase A`
3. 继续更新了：
   - `bridge-phaseout-evaluation.md`
   - `bridge-regression-template.md`
   其中明确写清了：repo 内证据稳定，但 repo 外依赖仍待人工核实。
4. customer 结果回看页 `frontend/src/views/user/CampusOrderResult.vue` 做了最小体验增强：
   - 查询前显示“等待输入订单号”初始提示
   - 查询时清空旧结果，避免上一笔结果残留
   - 无订单号时清空 URL 中旧 `orderId`
   - 查询不存在订单时显示明确错误态
   - `AWAITING_CONFIRMATION / COMPLETED` 状态文案与摘要分组更清楚
5. 本轮对 customer 结果页做了真实页面级验证：
   - 无 `orderId` 时能看到初始提示
   - 查询 `CR404` 时显示“订单不存在”
   - 查询 `CR202604070002` 时显示 completed 结果摘要
6. 本轮真实核查了 `enabledWorkInOwnBuilding` 提交类型：
   - 检查文件：
     - `frontend/src/views/user/CourierOnboarding.vue`
     - `frontend/src/api/campus-customer.js`
   - 通过 Playwright 抓取 `POST /api/campus/customer/courier-onboarding/profile` 的真实请求体，确认 `enabledWorkInOwnBuilding` 当前发送的是整数 `1/0`，不会再发送 boolean。
7. 本轮没有补第五个 admin 页。
8. 不补第五页的原因：
   - 当前更高优先级是继续推进 bridge 收口评估和 customer 结果页真实使用体验
   - 继续补 admin 页会稀释 repo 外人工核实准备的优先级
9. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 25 实际完成事项

1. 本轮没有改业务代码，没有扩新页面，没有新增接口，主目标是 repo 外 bridge 依赖核实与证据回填。
2. 已按 Step 25 的核查顺序处理三类 repo 外阻塞项：
   - 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`
   - 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`
   - 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge
3. 本轮已真实核查的入口包括：
   - `frontend/dist/assets`
   - `docs/deployment`
   - `C:\Users\20278\Documents`
   - `C:\Users\20278\Desktop`
   - `C:\Users\20278\Downloads`
   - `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
   - `D:\20278\code`
   - 常见 `Postman / Apifox` 目录
   - 常见 `Nginx / gateway` 日志目录
4. 本轮真实新增的 repo 外阴性证据：
   - 在当前可访问范围内未发现 repo 外静态资源或旧页面直接命中 `/api/campus/courier/profile`
   - 在当前可访问范围内未发现 repo 外静态资源或旧页面直接命中 `/api/campus/courier/review-status`
   - 在当前可访问范围内未发现 repo 外脚本或集合依赖 `customer_token` 调旧 bridge
5. 本轮真实新增的边界说明：
   - `docs/deployment` 只给出通用部署方式，没有实际已部署静态资源路径
   - 当前环境未发现可用 `Postman / Apifox` 资产目录
   - 当前环境未发现可归因的 `Nginx / gateway` 访问日志
   - `HBuilder X` 本地项目目录只看到空的 `setting.json`，不能证明存在仓库外旧页面项目
6. 因为“当前核查范围内未发现命中”不等于“确认没有 repo 外依赖”，所以本轮没有把 checklist 第 1~3 项写成通过，三项都继续保留为“待人工核实”。
7. 本轮同步更新了：
   - `bridge-execution-readiness-checklist.md`
   - `bridge-phaseout-evaluation.md`
   - `bridge-regression-template.md`
   并新增：
   - `step-25-repo-external-bridge-verification.md`
8. 本轮没有补第五个 admin 页。
9. 不补第五页的原因：
   - Step 25 的主线是 repo 外 bridge 依赖核实
   - 继续补 admin 页只会稀释收口评估主线
10. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## Step 26 实际完成事项

1. 本轮没有改业务代码，没有扩页面，没有新增接口，主目标是继续拿 Step 25 缺失的 repo 外真实资产。
2. 本轮新增拿到的 repo 外真实资产：
   - `C:\Users\20278\.ssh\config`
   - 可登录公网服务器 `xiaoyu_TenXun_Ubuntu (106.54.211.68)` 与 `xiaoyu_root_ALi_Ubuntu (47.243.129.186)`
   - repo 外共享文档 `D:\software\校园代送项目_完整交接总结_Step24.md`
   - repo 外共享文档 `D:\software\step-25-execution-prompt.md`
3. 本轮在两台公网服务器上实际核查了：
   - `/opt`
   - `/data`
   - `/root`
   - `/home`
   - `/usr/local/bin`
   - `/var/log`
   - 运行进程
   - docker 容器
   - shell 历史
4. 本轮在两台公网服务器上未发现：
   - campus / takeaway / delivery 相关部署目录
   - `nginx / openresty / caddy` 业务进程
   - `access.log` 或 gateway 日志中的旧 bridge endpoint 命中
   - `customer_token` 调旧 bridge 的脚本痕迹
5. 本轮在 repo 外共享文档里只发现 bridge 策略说明和执行提示，没有发现旧页面产物路径、发布包位置或 `customer_token` 调旧 bridge 的运行时证据。
6. 因为这轮虽然拿到了新的 repo 外真实资产，但仍然缺：
   - 实际业务部署的静态资源目录或历史发布包
   - 可归因的 Nginx / gateway 访问日志
   - 团队共享 Postman / Apifox / 联调脚本资产
   所以本轮没有把 checklist 第 1~3 项改成通过。
7. 本轮没有补第五个 admin 页。
8. 不补第五页的原因：
   - Step 26 的主线是 repo 外真实资产追补与阻塞项重新评估
   - 继续补 repo 内页面只会稀释 bridge 收口评估主线
9. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`

## 当前锁定的技术事实

1. 继续使用注解式 MyBatis，不改 XML
2. 继续沿用 `init.sql + migrations + schema-h2.sql + data-h2.sql`
3. 第一版支付仍只允许模拟支付，不接第三方支付
4. 退款/补偿/打款都仍然只是后台运营记录，不是真实支付执行
5. 所有金额处理继续统一为 `BigDecimal`、两位小数、`HALF_UP`
6. `courier/profile` 与 `courier/review-status` 的 bridge 继续保留
7. `customer/courier-onboarding/*` 只是 onboarding 新入口，不替代 `/api/campus/courier/auth/token`
8. 保留 bridge 的原因：
   - 未审核通过用户拿不到 `courier` token`
   - 但资料提交与审核状态查询必须发生在拿 token 之前
9. 收口 bridge 的前提：
   - 先有稳定的 onboarding 替代链路
   - 新入口完成一轮实际前端联调与演示验证
   - 或把资料提交与审核查询统一改为不依赖 `courier` token 的入口
10. 当前 bridge 收口阶段结论：
   - repo 内证据已足够支持进入“逐步收口计划设计阶段”
   - Step 22 已在本地 `test profile + H2` 下真实跑通一轮完整链路，repo 内阻塞已基本清掉
   - Step 23 已把这轮真实链路整理成共享回归留痕，并补了 customer completed 结果回看页
   - Step 25 到 Step 28 把 repo 外阻塞压缩到了“真实部署物 / 访问日志 / 团队共享资产”这三类假设前提
   - Step 29 owner 已明确确认：项目从未部署、从未发布、没有历史发布包、没有团队、没有团队共享调试资产、也没有仓库外旧页面副本或临时脚本
   - 因此这些 repo 外阻塞项已经按 owner 明确确认关闭
   - 当前可以进入 `Phase A` 执行准备重新评估
   - Step 30 已正式固化 `Phase A` 的执行边界、bridge 保留范围、回滚策略和最小回归清单
   - 但当前仍不能直接删除旧 bridge，也不能跳过 `Phase A` 方案设计与回滚评估

## Step 27 实际完成事项

1. 本轮没有扩 repo 内功能，也没有补第五个 admin 页。
2. 本轮继续沿 Step 26 主线，只追三类更接近真实业务运行面的 repo 外关键资产：
   - repo 外前端源码 / 历史项目目录：`D:\software\GOT\html`
   - Windows Recent 指向的 repo 外文档入口
   - 两台公网服务器上的真实部署候选路径、nginx/openresty 配置与日志位置
3. 本轮在 `D:\software\GOT\html` 排除 `node_modules/dist/build/.venv` 后，对以下关键字做了定点搜索：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
   - `getCourierProfile`
   - `getCourierReviewStatus`
   - `customer_token`
   结果未发现 repo 外代码或脚本命中。
4. 本轮从 `C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent` 解析到新的 repo 外文档入口：
   - `C:\Users\20278\Desktop\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md`
   - 当前目标文件已缺失，因此只能作为“存在过额外文档入口”的证据，不能作为运行时依赖证明。
5. 本轮在两台公网服务器上进一步核查了更贴近业务部署面的路径：
   - `/www`
   - `/www/wwwroot`
   - `/www/server/nginx/logs`
   - `/var/log/nginx`
   - `/var/log/openresty`
   - `/usr/share/nginx/html`
   - `/var/www`
   - `/srv`
   - `/etc/nginx`
   - `/etc/openresty`
   结果仍未发现业务部署物、`nginx.conf`、`access.log` 或 bridge endpoint 命中。
6. 本轮还真实确认当前机器不存在常见 `Postman / Apifox` 资产目录：
   - `C:\Users\20278\AppData\Roaming\Postman`
   - `C:\Users\20278\AppData\Roaming\Apifox`
   - `C:\Users\20278\AppData\Local\Apifox`
   - `C:\Users\20278\Documents\Postman`
   - `C:\Users\20278\Documents\Apifox`
7. 因为这轮虽然拿到了更多 repo 外关键资产，但仍没有拿到：
   - 实际业务静态资源目录或历史发布包
   - 可归因的 Nginx / gateway 访问日志
   - 团队共享 Postman / Apifox / 联调脚本资产
   所以 bridge 结论没有变化，仍不能进入 `Phase A` 执行准备。

## Step 28 实际完成事项

1. 本轮没有扩 repo 内功能，也没有补第五个 admin 页。
2. 本轮继续沿 Step 27 主线，只追三类真正可能关项的关键业务资产：
   - 云盘侧真实下载根路径与下载日志
   - repo 外真实压缩包 / 构建产物
   - 更接近部署持有面的服务器 shell 历史
3. 本轮新增拿到的关键业务资产：
   - `C:\Users\20278\AppData\Roaming\aDrive\preference.json`
   - `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log`
   - `D:\software\GOT\html\project.zip`
   - `xiaoyu_TenXun_Ubuntu:/root/.bash_history`
   - `xiaoyu_TenXun_Ubuntu:/home/ubuntu/.bash_history`
   - `xiaoyu_root_ALi_Ubuntu:/root/.bash_history`
4. 本轮真实确认：
   - `D:\software` 是当前机器阿里云盘客户端下载根路径，而不是随机本机目录
   - `D:\software\GOT\html\project.zip` 是真实 repo 外压缩包，但内部项目为 `healthy-management`，不是校园代送
   - 两台公网服务器 shell 历史中没有 `nginx/openresty/wwwroot/deploy/campus/delivery/takeaway` 等业务部署痕迹
5. 本轮对以下关键字做了真实搜索：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
   - `getCourierProfile`
   - `getCourierReviewStatus`
   - `customer_token`
6. 本轮结论没有变化：
   - 当前新增资产范围内仍未发现旧 bridge 命中
   - 但这仍不等于“确认无依赖”
   - 因为还没有拿到校园代送真实业务静态资源目录、历史发布包、可归因访问日志和团队共享调试资产
   - 所以 bridge 仍不能进入 `Phase A` 执行准备
7. 本轮没有补第五个 admin 页。
8. 不补第五页的原因：
   - 当前更高优先级仍然是 bridge repo 外关键业务资产追补
   - 继续补 admin 页会稀释收口评估主线

## Step 29 实际完成事项

1. 本轮没有扩 repo 内功能，没有补新页面，没有新增接口。
2. 本轮直接使用项目 owner 在当前轮次的明确确认，重评估 repo 外阻塞项：
   - 当前项目唯一维护人就是 owner 本人
   - 当前项目从未部署、从未发布
   - 不存在历史发布包
   - 当前没有团队
   - 不存在团队共享 `Postman / Apifox / 联调脚本` 资产
   - 不存在仓库外旧页面副本、历史前端包或非 repo 管理的联调副本
3. 本轮因此关闭了三个 repo 外阻塞项：
   - 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`
   - 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`
   - 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge
4. 本轮同步更新了：
   - `bridge-execution-readiness-checklist.md`
   - `bridge-phaseout-evaluation.md`
   - `summary.md`
   - `pending-items.md`
   - `file-change-list.md`
5. 本轮新增日志：
   - `step-29-owner-confirmation-and-bridge-reassessment.md`
6. 本轮结论不是“立即删 bridge”，而是：
   - repo 外阻塞项已关闭
   - 可以进入 `Phase A` 执行准备重新评估
   - 下一步应设计 bridge 保留范围、回滚策略和最小回归清单
7. 本轮没有补第五个 admin 页。
8. 不补第五页的原因：
   - 当前主线已经从“追 repo 外证据”切到“Phase A 重新评估”
   - 继续补 admin 页会偏离 bridge 收口主线

## Step 30 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口，只做 `Phase A` 执行准备重新评估。
2. 本轮正式把阶段结论从“repo 外阻塞已关闭”推进到“可以进入 `Phase A` 执行准备重新评估”。
3. 本轮明确了 `Phase A` 的执行边界：
   - 只做执行准备
   - 不做真正收口动作
   - 不删接口
   - 不删 bridge
   - 不改鉴权
   - 不改 repo 内业务代码
4. 本轮明确了 bridge 在 `Phase A` 期间的保留范围：
   - `/api/campus/courier/profile` 继续保留
   - `/api/campus/courier/review-status` 继续保留
   - `customer_token -> bridge -> courier 前置读取` 继续允许，但只观察，不改行为
   - `/courier/workbench` 继续维持优先 `courier_token` 的策略
5. 本轮明确了回滚策略：
   - 任何未来收口动作若导致 workbench、onboarding、token 或 completed 回读链路回归失败，第一回滚点就是恢复到 Step 29 结束时的 bridge 保守状态
   - `request.js` 现有 token 附着逻辑、`CourierWorkbench.vue` 现有读取行为和 `customer/courier-onboarding/*` 前置链路都属于回滚关键点
6. 本轮明确了最小回归清单，至少覆盖：
   - customer onboarding 提交资料
   - customer 查看审核状态
   - customer 申请 courier token
   - `/courier/workbench` 加载 profile / review-status
   - pure `courier_token` 路径稳定
   - 接单 / 取餐 / deliver / 异常上报
   - customer confirm
   - completed 回读
   - customer 结果回看页
7. 本轮同步统一了 bridge 文档口径：
   - `bridge-execution-readiness-checklist.md`
   - `bridge-phaseout-evaluation.md`
   - `step-29-owner-confirmation-and-bridge-reassessment.md`
8. 本轮没有补第五个 admin 页。
9. 不补第五页的原因：
   - Step 30 的唯一主线是 `Phase A` 执行准备重新评估
   - 继续补展示页会稀释 bridge 收口评估主线

## Step 31 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口。
2. 本轮先按 Step 30 固化的最小回归清单做了一轮真实执行前复核。
3. 本轮真实复核通过的链路包括：
   - customer onboarding 提交资料
   - customer 查看审核状态
   - admin 审核通过
   - customer 申请 courier token
   - `/courier/workbench` 加载 `profile / review-status / available orders`
   - pure `courier_token` 路径稳定
   - 接单 / 取餐 / deliver / 异常上报
   - customer confirm
   - completed 回读
   - customer 结果回看页
4. 本轮继续使用真实样本订单 `CR202604070002` 与既有测试账号完成预检。
5. 本轮新增的浏览器级确认：
   - `/courier/workbench` 在仅保留 `courier_token` 的情况下可稳定加载
   - `profile / review-status / available orders` 请求均附着 `courier_token`
   - `/user/campus/order-result?orderId=CR202604070002` 可正常显示 `COMPLETED` 结果摘要
6. 本轮评估了两个最小 `Phase A` 候选动作：
   - 进一步收紧 `CourierWorkbench.vue` 对 bridge 的运行时使用边界
   - 仅在 `campus-courier.js / request.js` 做注释级边界显式化
7. 本轮最终结论是：`暂不执行任何收口动作`
8. 不执行的原因：
   - 候选 1 会开始触碰稳定链路语义，不够保守
   - 候选 2 虽然安全，但实际推进收益过低，不值得作为一次正式 `Phase A` 动作
9. 因此 Step 31 后的准确状态是：
   - `Phase A` 执行准备仍有效
   - bridge 仍完全保留
   - 真正的收口动作还没有开始

## Step 32 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口。
2. 本轮在 Step 31 已完成真实执行前复核的前提下，只继续扩大候选池，并做 go / no-go 决策。
3. 本轮重新评估的候选只有两类：
   - repo 内 bridge 使用边界的行为不变型显式隔离
   - 把 Step 30 / Step 31 的执行边界转成代码旁证级约束
4. 本轮两个候选都没有执行：
   - 候选 A 不执行，因为收益仍不足以覆盖其对稳定链路结构的潜在影响
   - 候选 B 不执行，因为收益仍偏低，属于“文档已清楚但代码动作本身没有推进边界”
5. 本轮最终结论是继续 `no-op`。
6. 继续 `no-op` 的原因不是链路不稳，而是当前仍没有一个同时满足“有明确收益 + 风险足够小 + 单提交可回滚”的最小候选动作。
7. bridge 当前仍完全保留。
8. 本轮没有补第五个 admin 页。
9. 不补第五页的原因：
   - 当前主线仍是 `Phase A` 的最小动作评估
   - 继续补展示页会稀释当前 bridge 收口主线

## Step 33 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口。
2. 本轮没有继续机械扩 `Phase A` 候选池。
3. 本轮基于 Step 31 / Step 32 连续两轮 `no-go / no-op`，正式将 bridge 主线收成 `Phase A no-op` 冻结态。
4. 本轮明确了冻结态保持不动项：
   - `/api/campus/courier/profile` 继续保留
   - `/api/campus/courier/review-status` 继续保留
   - `/courier/workbench` 继续维持优先 `courier_token`
   - `customer_token -> bridge -> courier 前置读取` 继续保留观察态
   - `customer/courier-onboarding/*` 继续作为前置入口
   - 不改 `request.js`
   - 不改后端鉴权
5. 本轮明确了恢复推进触发条件：
   - 出现更高收益且更低风险的新候选
   - repo 内出现新的 bridge 使用点或边界变化
   - 业务要求必须进一步收紧 bridge
   - 出现新的真实回归信号，证明当前保留策略开始有成本
   - 后续前端结构改造、入口整理或交付目标需要更清晰的桥接边界
6. 本轮明确了冻结态下允许并行推进的非 bridge 工作：
   - 文档整理和演示资料整理
   - 现有页面纯展示级优化候选评估
   - 与 bridge 无关的后端能力梳理
   - 售后执行、异常上报、settlement 等非 bridge 主线需求评估
7. 当前最终 bridge 结论：
   - 正式进入 `Phase A no-op` 冻结态
   - bridge 完全保留
   - 不默认继续寻找 bridge 收口候选
   - 只有触发条件出现时才重新打开 bridge 主线

## Step 34 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口。
2. 本轮没有重新打开 bridge 主线，也没有更新 bridge 收口评估文档。
3. 本轮基于 Step 33 的 `Phase A no-op` 冻结态，正式转向不触 bridge 的并行工作。
4. 本轮收束出的非 bridge 优先方向：
   - 现有页面纯展示级优化候选评估
   - 演示资料整理
   - 与 bridge 无关的后端能力梳理
5. 本轮完成了现有页面展示级优化候选池评估：
   - P0：`frontend/src/views/courier/CourierWorkbench.vue`
   - P0：`frontend/src/views/user/CampusOrderResult.vue`
   - P1：`frontend/src/views/user/CourierOnboarding.vue`
   - P1：admin 四个只读运营页
   - P2：`frontend/src/views/user/Profile.vue`
6. 本轮建议 Step 35 进入一个小范围展示级优化执行轮，优先只处理：
   - `/courier/workbench`
   - `/user/campus/order-result`
7. Step 35 若执行，应限制为展示 polish：
   - 状态提示
   - 空态 / loading / 错误态文案
   - drawer 分区层级
   - 表格、卡片、tag 一致性
8. 本轮整理了当前最适合演示的真实链路：
   - onboarding
   - admin 审核
   - courier token 申请
   - workbench
   - 接单 / 取餐 / deliver / 异常上报
   - customer confirm
   - completed 回读
   - customer 结果回看页
9. 本轮继续确认第五个 admin 页后置。
10. 当前 bridge 结论保持不变：
   - `Phase A no-op` 冻结态
   - bridge 完全保留
   - 不删接口、不改鉴权、不改 token 附着逻辑

## Step 35 实际完成事项

1. 本轮执行了 Step 34 建议的小范围展示级优化执行轮。
2. 本轮只修改两个 P0 页面：
   - `frontend/src/views/user/CampusOrderResult.vue`
   - `frontend/src/views/courier/CourierWorkbench.vue`
3. `CampusOrderResult.vue` 展示级优化：
   - 顶部新增“只读回看”标记
   - 增加三段式查询/状态/字段核对说明
   - 优化初始态、loading 态、错误态文案和布局
   - 优化 `AWAITING_CONFIRMATION / COMPLETED` 结果状态提示层级
4. `CourierWorkbench.vue` 展示级优化：
   - 顶部新增“最小承接”标记
   - 增加四段式 workbench 演示链路说明
   - 可接单区增加空态场景说明
   - 订单详情 drawer 增加当前订单状态摘要卡片
   - 订单详情 drawer 增加基本信息分组标题和只读字段来源说明
   - 订单详情空态文案更明确
5. 本轮明确未做：
   - 未改 bridge
   - 未改 `request.js`
   - 未改 `campus-courier.js`
   - 未改 token 附着逻辑
   - 未改 API 调用顺序
   - 未改按钮动作语义
   - 未改按钮可用条件
   - 未改路由
   - 未新增页面
   - 未改后端代码
6. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
7. 当前 bridge 结论保持不变：
   - `Phase A no-op` 冻结态
   - bridge 完全保留
   - 不删接口、不改鉴权、不改 token 附着逻辑

## Step 36 实际完成事项

1. 本轮在 Step 35 后进入第二轮展示级优化决策，没有铺开第二批所有页面。
2. 本轮二选一后选择方案 A：只处理 `frontend/src/views/user/CourierOnboarding.vue`。
3. 选择方案 A 的原因：
   - onboarding 是 customer 到 courier token 的前置入口
   - Step 35 已 polish workbench 和 customer result，本轮补 onboarding 能把主演示链路前半段补齐
   - onboarding 的资料提交、审核状态、token 资格和 token 申请解释成本高于单个 admin 只读页
   - 改动可以限制在模板和样式层，不需要改提交逻辑
4. `CourierOnboarding.vue` 展示级优化：
   - 顶部新增“customer 前置入口”标记
   - 新增三段式流程说明：提交资料、等待审核、申请 token
   - 审核与资格概览区新增标题和说明
   - token 申请区新增开放条件、申请凭证和成功后承接说明
   - token 申请成功结果区增加结果面板样式
   - 资料表单区新增说明标题
   - 表单内部新增“基础与校区信息”和“接单偏好与备注”分组说明
5. 本轮明确未做：
   - 未改 bridge
   - 未改 `request.js`
   - 未改 `campus-courier.js`
   - 未改 `campus-customer.js`
   - 未改 token 附着逻辑
   - 未改 API 调用顺序
   - 未改 `enabledWorkInOwnBuilding` 的整数提交逻辑
   - 未改 token 申请逻辑
   - 未改按钮行为
   - 未改路由
   - 未改后端代码
   - 未处理任何 admin 只读运营页
   - 未新增第五个 admin 页
6. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
7. 当前 bridge 结论保持不变：
   - `Phase A no-op` 冻结态
   - bridge 完全保留
   - 不删接口、不改鉴权、不改 token 附着逻辑

## Step 37 实际完成事项

1. 本轮在 Step 35 / Step 36 已完成 customer / courier 三个主演示页面 polish 后，进入单页 admin 只读运营页展示级优化执行轮。
2. 本轮二选一后选择 settlement，只处理：
   - `frontend/src/views/CampusSettlementOpsView.vue`
3. 选择 settlement 的原因：
   - `CampusSettlementOpsView.vue` 同时包含摘要、筛选、列表和详情 drawer，更适合作为完整后台只读运营演示页。
   - settlement 能承接 completed 订单链路后的结算回看，和 Step 35 / Step 36 的主演示链路互补性更强。
   - after-sale 执行页包含执行状态和纠正信息，解释成本更高，适合后续单独处理。
4. `CampusSettlementOpsView.vue` 展示级优化：
   - 顶部新增“只读运营”标记。
   - 新增三段式运营引导：摘要、筛选、详情。
   - 筛选区新增标题和说明，明确筛选只影响读取。
   - 表格区新增只读提示，明确不提供确认结算、打款、撤回或核对写操作。
   - 表格空态文案更明确。
   - 结算状态和打款状态 tag 改为中文展示文案。
   - 详情 drawer 新增当前结算记录摘要卡片和单笔详情说明。
5. 本轮明确未做：
   - 未改 bridge。
   - 未改 `request.js`。
   - 未改任何 `campus-*` API 文件运行时行为。
   - 未改 token 附着逻辑。
   - 未改 API 调用顺序。
   - 未改分页逻辑。
   - 未改筛选参数语义。
   - 未改 settlement 业务逻辑。
   - 未改路由。
   - 未改后端代码。
   - 未处理 `CampusAfterSaleExecutionList.vue`。
   - 未新增第五个 admin 页。
6. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
7. 当前 bridge 结论保持不变：
   - `Phase A no-op` 冻结态
   - bridge 完全保留
   - 不删接口、不改鉴权、不改 token 附着逻辑

## Step 38 实际完成事项

1. 本轮基于 Step 37 已完成 settlement 单页 polish，继续进入单页 admin 只读运营页展示级优化执行轮。
2. 本轮只处理：
   - `frontend/src/views/CampusAfterSaleExecutionList.vue`
3. 选择 after-sale 的原因：
   - Step 37 已完成 settlement 只读运营页，Step 38 处理 after-sale 能补齐当前两个最主要 admin 只读运营演示面。
   - after-sale 页承载执行状态、决策类型、人工纠正和售后结果 drawer，解释成本高，适合单独 polish。
   - 本页是只读页，改动可限制在模板、状态文案和样式层，风险低。
4. `CampusAfterSaleExecutionList.vue` 展示级优化：
   - 顶部新增“只读执行”标记。
   - 新增三段式页面导览：列表、状态、详情。
   - 筛选区新增标题和说明，明确筛选只影响读取。
   - 表格区新增标题说明和只读提示。
   - 表格空态文案更明确。
   - 决策类型、执行状态和人工纠正 tag 改为中文展示文案。
   - 详情 drawer 新增当前售后订单摘要卡片。
   - 详情 drawer 增加字段来源说明和三个只读分区说明。
   - 详情 drawer 无数据时补充明确空态。
5. 本轮明确未做：
   - 未改 bridge。
   - 未改 `request.js`。
   - 未改任何 `campus-*` API 文件运行时行为。
   - 未改 token 附着逻辑。
   - 未改 API 调用顺序。
   - 未改售后执行逻辑。
   - 未改任何状态推进。
   - 未改筛选参数语义。
   - 未改分页逻辑。
   - 未改路由。
   - 未改后端代码。
   - 未回改 `CampusSettlementOpsView.vue`。
   - 未新增第五个 admin 页。
6. 执行：
   - `.\mvnw.cmd -DskipTests compile`
   - `npm run build`
   - `git diff --check`
7. 当前 bridge 结论保持不变：
   - `Phase A no-op` 冻结态
   - bridge 完全保留
   - 不删接口、不改鉴权、不改 token 附着逻辑

## Step 39 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口。
2. 本轮完成 Step 35 到 Step 38 展示 polish 线复盘，覆盖 5 个关键页面：
   - `frontend/src/views/user/CourierOnboarding.vue`
   - `frontend/src/views/courier/CourierWorkbench.vue`
   - `frontend/src/views/user/CampusOrderResult.vue`
   - `frontend/src/views/CampusSettlementOpsView.vue`
   - `frontend/src/views/CampusAfterSaleExecutionList.vue`
3. 本轮复盘结论：
   - customer / courier 主演示链路三页已覆盖 onboarding、token、workbench、completed 回读。
   - admin 两个核心只读运营页已覆盖 settlement 和 after-sale。
   - 当前没有发现会明显影响演示或答辩的展示短板。
   - 继续 polish 的收益已经明显下降，容易变成机械美化。
4. 本轮正式将展示 polish 线收成“冻结/维护态”：
   - 不再默认为“还能美化一点”继续开页面 polish 轮次。
   - 只有出现真实演示反馈、明显展示缺陷或交付材料要求时，才重新打开展示 polish。
5. 当前形成双冻结状态：
   - bridge 主线继续保持 `Phase A no-op` 冻结态。
   - 展示 polish 线进入冻结/维护态。
6. 本轮明确未做：
   - 未改任何 Vue 页面。
   - 未改 bridge。
   - 未改 `request.js`。
   - 未改任何 `campus-*` API 文件运行时行为。
   - 未改 token 附着逻辑。
   - 未改 API 调用顺序。
   - 未改路由。
   - 未改后端代码。
   - 未新增页面。

## Step 40 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口。
2. 本轮进入稳定交付整理阶段，只做交付整理与演示脚本固化。
3. 本轮新增 `step-40-delivery-packaging-and-demo-script.md`，集中整理：
   - 当前交付边界。
   - 当前明确未交付能力。
   - bridge 冻结原因。
   - 展示 polish 冻结原因。
   - 13 步主演示脚本。
   - 演示账号与样本数据索引。
   - 演示页面清单。
   - 风险与答辩口径。
4. 当前交付边界已明确为：
   - 不是最终产品版。
   - 是“最小闭环 + 可演示 + 可讲解 + 可交接”的稳定版。
   - 不以继续补页数、继续无限 polish 或继续扩能力为默认目标。
5. 主演示脚本已覆盖：
   - customer onboarding 提交资料。
   - admin 审核。
   - customer 申请 courier token。
   - courier workbench 加载。
   - 接单、取餐、deliver、异常上报。
   - customer confirm。
   - completed 回读。
   - customer 结果页。
   - admin settlement 只读页。
   - admin after-sale 只读页。
6. 演示账号与样本数据已统一索引：
   - `13900139000 / 123456`
   - `13900139001 / 123456`
   - `13800138000 / 123456`
   - `CR202604070002`
   - `CR202604060001`
7. 本轮明确 after-sale H2 固定样本仍不足，不脑补数据。
8. 当前形成稳定交付口径：
   - bridge 主线继续 `Phase A no-op` 冻结。
   - 展示 polish 线继续冻结/维护。
   - 第五个 admin 页继续后置。

## Step 41 实际完成事项

1. 本轮没有改业务代码、没有补新页面、没有新增接口。
2. 本轮继续停留在交付材料补完阶段，只整理截图清单、录屏顺序和演示前检查 checklist。
3. 本轮新增 `step-41-delivery-assets-checklist-and-recording-plan.md`，集中整理：
   - 14 项截图清单。
   - 4 段式录屏顺序。
   - 环境、账号、数据、页面和失败处理检查表。
4. 截图清单已覆盖：
   - customer onboarding。
   - 审核状态与 token 资格。
   - courier token 申请成功。
   - courier workbench 首页。
   - 可接单列表。
   - 订单详情 drawer。
   - 取餐、deliver、异常上报关键状态。
   - customer completed 结果页。
   - settlement 只读运营页。
   - after-sale 只读运营页。
5. 录屏顺序已拆成：
   - customer onboarding 与 token 申请。
   - courier workbench 动作链路。
   - customer confirm 与 completed 结果回读。
   - admin 只读运营页。
6. 演示前检查 checklist 已覆盖：
   - 后端、前端、profile、H2 初始状态。
   - customer / courier / admin 三类账号和 token。
   - `CR202604070002`、`CR202604060001` 与 settlement / after-sale 样本。
   - 关键页面打开检查。
   - 演示失败时哪些必须停止、哪些可以跳过。
7. 当前交付材料已具备“可截图、可录屏、可检查”的执行基础。
8. 当前仍未产出真实截图文件和录屏文件；如需答辩归档，下一轮可按清单采集并记录文件位置。
9. 本轮继续确认：
   - bridge 主线保持 `Phase A no-op` 冻结态。
   - 展示 polish 线保持冻结/维护态。
   - 第五个 admin 页继续后置。

## 当前未解决的问题

- customer 仍没有自助退款申请和结果确认交互，只能查看售后结果回执
- admin 仍没有独立售后执行历史表，只保留订单上的当前执行结果和一次纠正审计
- settlement 仍没有真实打款、撤回打款和复杂对账
- 异常历史表、admin resolve 最小处理动作和 admin 异常前端承接已落地并通过 H2/test 运行态验证，但仍未做完整异常工单系统
- 位置记录仍是低频明细，没有地图、轨迹聚合和频控
- frontend 目前已接入 customer 三个演示页、courier workbench、admin 四个只读运营演示页和 admin 异常处理承接页，但仍不是完整校园代送后台
- `CampusRuleCatalog` 仍是代码常量

## Step 46 实际完成事项

1. 本轮只做后端最小 resolve 实现，没有补前端页面、没有新增路由、没有改 bridge。
2. 新增 `CampusAdminExceptionResolveDTO`，请求体保持最小：
   - `processResult`
   - `adminNote`
3. 新增 admin resolve 接口：
   - `POST /api/campus/admin/exceptions/{id}/resolve`
4. service / mapper 层已落地状态保护：
   - 只允许 `REPORTED -> RESOLVED`
   - SQL 更新条件限制 `process_status = 'REPORTED'`
   - 已 `RESOLVED` 的记录重复处理会返回“异常记录已处理，不能重复处理”
5. 最小 `processResult` 固定为：
   - `HANDLED`
   - `MARKED_INVALID`
   - `FOLLOWED_UP`
6. resolve 只更新 `campus_exception_record` 的处理字段：
   - `process_status`
   - `process_result`
   - `admin_note`
   - `processed_by_employee_id`
   - `processed_at`
   - `updated_at`
7. 本轮兼容策略保持不变：
   - 不改订单主状态
   - 不改 settlement
   - 不清空 `campus_relay_order.exception_type / exception_remark / exception_reported_at`
   - 旧 latest exception 摘要继续作为兼容摘要
8. H2/test profile 运行态已验证：
   - 异常上报生成 `REPORTED`
   - resolve 后详情和列表可回读 `RESOLVED`
   - 重复 resolve 返回业务错误
   - courier order detail、customer order detail、admin recent exceptions 仍可正常读取 latest summary
9. 本轮继续确认：
   - bridge 主线保持 `Phase A no-op` 冻结态
   - 展示 polish 线保持冻结/维护态
   - 媒体线继续收住
   - 第五个 admin 页继续后置

## Step 47 实际完成事项

1. 本轮只做 admin 异常历史 / resolve 前端承接 go / no-go 评估，没有新增页面、没有写业务代码、没有改后端接口、没有改 bridge。
2. 方向 A 评估结论：
   - Step 45A / Step 46 的异常历史与 resolve 后端闭环已经稳定。
   - 没有前端承接时，admin 异常历史查询和 resolve 仍停留在 curl / 手工接口层，演示、联调和交接成本偏高。
   - 最小承接可以限制为一个 admin 页面、列表、详情 drawer 和一个 `processStatus = REPORTED` 下的 resolve 区。
   - 当前已有 `GET /api/campus/admin/exceptions`、`GET /api/campus/admin/exceptions/{id}`、`POST /api/campus/admin/exceptions/{id}/resolve`，不需要新增后端接口。
3. 方向 B 评估结论：
   - 售后执行历史表长期审计价值高，但需要新增表、迁移、兼容策略和独立方案设计。
   - 与 admin 异常前端最小承接相比，P2 的复杂度和风险更高，当前不作为 Step 48 第一优先级。
4. 最终选择方向 A：
   - Step 48 建议进入 admin 异常前端最小承接方案 / 实现准备。
   - 该页面的目的不是补第五个 admin 页，而是承接 Step 45A / Step 46 已完成的异常历史处理闭环。
5. 本轮明确未做：
   - 未新增前端页面。
   - 未新增后端接口。
   - 未改订单主状态、settlement、latest exception 摘要。
   - 未改 bridge、鉴权、token 附着逻辑或路由。

## Step 48 实际完成事项

1. 新增 admin 异常处理页：
   - `/campus/exceptions`
   - `frontend/src/views/CampusExceptionOpsView.vue`
2. 新增 admin 侧 API 封装：
   - `getCampusExceptionRecords`
   - `getCampusExceptionDetail`
   - `resolveCampusException`
3. MainLayout 已新增“校园异常处理”入口，并补齐菜单高亮和 breadcrumb。
4. 页面只接入 Step 45A / Step 46 已有接口：
   - `GET /api/campus/admin/exceptions`
   - `GET /api/campus/admin/exceptions/{id}`
   - `POST /api/campus/admin/exceptions/{id}/resolve`
5. 页面能力保持最小：
   - 列表支持订单号、配送员 ID、处理状态筛选。
   - 详情 drawer 展示异常历史、订单关联、配送员关联、latest exception 兼容摘要和处理字段。
   - resolve 区只在 `processStatus = REPORTED` 时展示。
   - resolve 成功后刷新详情和列表。
6. 本轮明确未做：
   - 未新增后端接口。
   - 未做完整异常工单系统。
   - 未做 reopen、delete、ACKNOWLEDGED、消息通知或多角色审批。
   - 未改订单主状态、settlement、latest exception 摘要。
   - 未改 bridge、鉴权、token 附着逻辑或旧前端主链路。
7. 验证结果：
   - `.\mvnw.cmd -DskipTests compile` 通过。
   - `npm run build` 通过，仅保留既有 Sass `@import` deprecation 与 chunk size warning。
   - `git diff --check` 通过，仅有 Windows 工作区 LF/CRLF 提示。

## Step 49 实际完成事项

1. 对 `/campus/exceptions` 完成 H2/test 运行态验证。
2. 使用订单 `CR202604070002` 真实完成：
   - courier 接单。
   - 连续两次异常上报。
   - admin 异常历史列表查询。
   - admin 异常详情查询。
   - admin 最小 resolve。
   - 重复 resolve 失败。
   - latest exception 摘要兼容回读。
   - customer 订单详情兼容回读。
3. API 验证结果：
   - 异常历史共 2 条。
   - `REPORTED` 1 条。
   - `RESOLVED` 1 条。
   - resolve 记录 ID 为 `2`。
   - 重复 resolve 返回“异常记录已处理，不能重复处理”。
4. 前端页面验证结果：
   - `/campus/exceptions` 可正常加载。
   - `CR202604070002` 异常历史列表可见。
   - 详情 drawer 可打开。
   - `REPORTED` 记录的 resolve 区可见。
   - latest exception 兼容摘要可见。
5. 证据文件：
   - `project-logs/campus-relay/runtime/step-49/admin-exception-api-validation.json`
   - `project-logs/campus-relay/runtime/step-49/admin-exception-page-validation.json`
6. 验证结果：
   - `.\mvnw.cmd -DskipTests compile` 通过。
   - `npm run build` 通过，仅保留既有 Sass `@import` deprecation 与 chunk size warning。
   - `git diff --check` 通过。
7. 本轮明确未做：
   - 未修改业务代码。
   - 未新增后端接口。
   - 未新增前端页面。
   - 未改订单主状态、settlement、latest exception 摘要。
   - 未改 bridge、鉴权、token 附着逻辑或旧前端主链路。

## Step 50 实际完成事项

1. 完成 P2 售后执行历史表最小方案设计。
2. 基于真实代码确认当前售后执行现状：
   - 写接口为 `POST /api/campus/admin/orders/{id}/after-sale-execution`。
   - 查询接口为 `GET /api/campus/admin/orders/after-sale-executions`。
   - 当前订单表只保存 latest/current 售后执行摘要与 `FAILED -> SUCCESS` 的 corrected 字段。
   - 当前执行状态只允许 `SUCCESS / FAILED` 写入，`FAILED -> SUCCESS` 作为人工纠正。
3. 设计建议：
   - 后续新增 `campus_after_sale_execution_record` 作为售后执行审计主表。
   - 每次 admin 成功记录售后执行结果时新增一条历史。
   - 同事务内继续更新 `campus_relay_order` 上的当前售后执行摘要。
   - 订单表现有 `after_sale_execution_*` 字段继续作为兼容摘要保留。
4. 明确 Step 51A 推荐实现边界：
   - 只做后端最小实现：表、同事务写入、admin 只读分页查询。
   - 不做前端页面。
   - 不做修改、删除、回滚、真实退款、settlement 联动或完整售后工单系统。
5. 本轮没有修改业务代码、SQL、前端页面、bridge、接口实现、鉴权或路由。

## Step 51A 实际完成事项

1. 完成售后执行历史最小后端实现。
2. 新增 `campus_after_sale_execution_record`：
   - MySQL init 已同步。
   - 新增 `V10__campus_after_sale_execution_record.sql`。
   - H2 schema 已同步。
   - H2 data 未预置复杂样本，运行态通过真实售后执行链路生成历史。
3. 复用现有 `POST /api/campus/admin/orders/{id}/after-sale-execution`：
   - 请求体、路径和前端调用方式不变。
   - 现有状态校验不变。
   - 成功写入售后执行结果后，同事务追加一条历史记录。
   - `FAILED -> SUCCESS` 会生成 `corrected = 1` 的历史记录。
4. 新增 admin 只读分页接口：
   - `GET /api/campus/admin/after-sale-execution-records`
   - 支持 `relayOrderId`、`executionStatus`、`corrected`、`page`、`pageSize / size`。
   - 默认按 `executed_at desc, id desc` 返回。
5. 兼容策略：
   - 订单表 `after_sale_execution_*` 继续作为当前摘要字段。
   - 现有售后执行分页、admin 售后结果、customer 售后结果继续读当前摘要。
   - 新接口只承担历史审计读取。
6. H2/test 运行态验证：
   - 对 `CR202604060001` 真实执行 `PENDING -> FAILED -> SUCCESS`。
   - 历史表得到 2 条记录。
   - 最新 `SUCCESS` 记录 `previousExecutionStatus = FAILED` 且 `corrected = 1`。
   - 旧售后执行分页仍显示当前摘要 `SUCCESS / corrected = 1`。
   - customer 售后结果仍可回读当前执行状态。
   - 证据文件：`project-logs/campus-relay/runtime/step-51a/after-sale-execution-history-validation.json`。
7. 验证结果：
   - `.\mvnw.cmd -DskipTests compile` 通过。
   - `.\mvnw.cmd -DskipTests package` 通过。
   - `npm run build` 通过，仅保留既有 Sass `@import` deprecation 与 chunk size warning。
   - `git diff --check` 通过，仅有 Windows 工作区 LF/CRLF 提示。
8. 本轮明确未做：
   - 未新增前端页面。
   - 未新增售后执行历史详情页。
   - 未新增修改、删除、回滚接口。
   - 未做真实退款、settlement 联动或完整售后工单系统。
   - 未改 bridge、鉴权、token 附着、路由或旧外卖模块。

## Step 51B 实际完成事项

1. 完成售后执行历史前端承接 go / no-go 评估。
2. 对比方向 A 与方向 B：
   - 方向 A：在现有 `CampusAfterSaleExecutionList.vue` 详情 drawer 内增加售后执行历史只读区。
   - 方向 B：切到 P3 settlement 批次复核、撤回和对账方案设计。
3. 最终选择方向 A：
   - Step 51A 后端历史表和分页接口已稳定，缺少前端承接会让审计能力停留在 API 层。
   - drawer 内只读历史区可以解释“当前摘要”和“历史审计”的兼容关系。
   - 相比 P3 settlement，方向 A 不需要改库、不需要后端实现、不新增路由，风险更低。
   - 该方向不是补第五个 admin 页，而是承接已完成的售后执行历史后端闭环。
4. 固定 Step 52 推荐实现边界：
   - 只复用现有 `CampusAfterSaleExecutionList.vue`。
   - 只在详情 drawer 中增加售后执行历史只读区。
   - 只接 `GET /api/campus/admin/after-sale-execution-records`。
   - 不新增页面、不新增路由、不新增写接口。
   - 不改 bridge、鉴权、token 附着、订单主状态或 settlement。
5. 本轮没有修改业务代码、前端页面、后端接口、SQL、bridge、鉴权或路由。

## Step 52 实际完成事项

1. 完成售后执行历史前端最小承接。
2. 新增 API 封装：
   - `getCampusAfterSaleExecutionRecords`
   - 请求 `GET /api/campus/admin/after-sale-execution-records`
   - 继续复用现有 `request` 和 `normalizePageParams`
3. 复用现有 `CampusAfterSaleExecutionList.vue`：
   - 不新增页面。
   - 不新增路由。
   - 不改 admin 主框架。
4. 在售后结果详情 drawer 中新增“执行历史”只读区：
   - 打开详情时继续读取原 after-sale-result 当前摘要。
   - 同时按 `relayOrderId` 读取售后执行历史。
   - 展示上次状态、本次状态、纠正标记、执行备注、参考号、执行人和执行时间。
   - 增加历史区 loading、空态和失败提示。
5. 兼容策略保持不变：
   - 当前摘要仍读订单表 `after_sale_execution_*`。
   - 历史区读 `campus_after_sale_execution_record`。
   - customer 售后结果、admin 售后执行分页和 after-sale-result drawer 语义不变。
6. 本轮明确未做：
   - 未新增独立售后执行历史页。
   - 未新增写接口。
   - 未做修改、删除、回滚、真实退款或 settlement 联动。
   - 未改 bridge、鉴权、token 附着、路由或旧外卖模块。

## Step 53 实际完成事项

1. 完成售后执行历史前端运行态验证。
2. H2/test 下重新生成 `CR202604060001` 售后执行历史样本：
   - customer 发起售后。
   - admin 处理售后为 `RESOLVED`。
   - admin 记录 `REFUND` 决策。
   - 第一次执行 `FAILED`。
   - 第二次执行 `SUCCESS`，参考号 `STEP53-REFUND-001`。
3. API 验证结果：
   - `GET /api/campus/admin/after-sale-execution-records` 返回 2 条历史。
   - 最新记录为 `FAILED -> SUCCESS`，`corrected = 1`。
   - 旧记录为 `PENDING -> FAILED`，`corrected = 0`。
   - 旧售后执行分页仍返回当前摘要 `SUCCESS / corrected = 1`。
   - customer 售后结果仍可回读当前执行状态。
4. 前端验证结果：
   - `/campus/after-sale-executions` 可正常打开。
   - `CR202604060001` 详情 drawer 可正常打开。
   - 当前售后执行摘要正常展示。
   - “执行历史”只读区正常展示 2 条历史。
5. 证据文件：
   - `project-logs/campus-relay/runtime/step-53/after-sale-execution-history-api-validation.json`
   - `project-logs/campus-relay/runtime/step-53/after-sale-execution-history-page-validation.json`
6. 本轮没有修改业务代码、前端页面、后端接口、SQL、bridge、鉴权或路由。

## Step 54 实际完成事项

1. 完成 P3 settlement 批次复核、撤回和对账最小方案设计。
2. 基于真实代码核查确认当前 settlement 能力：
   - `campus_settlement_record` 仍是单笔结算与打款摘要主表。
   - `payout_batch_no` 仍是当前批次聚合依据。
   - 已有批次列表、批次详情、对账摘要、单笔核对和批量打款结果记录。
3. 明确后续最小模型建议：
   - 先新增批次操作审计表，承接批次复核和撤回记录。
   - 再新增对账差异记录表，承接差异项和处理状态。
4. 明确兼容策略：
   - 现有 `campus_settlement_record` payout 摘要字段继续保留。
   - 现有 settlement 单笔页、批次页和 reconcile-summary 继续按当前字段读取。
   - 后续新增模型只作为批次操作审计和差异审计主数据。
5. 本轮明确未做：
   - 未写 SQL。
   - 未写 Java 代码。
   - 未改前端页面。
   - 未新增接口。
   - 未做真实打款、真实撤回或完整财务后台。
   - 未改 bridge、鉴权、token 附着、路由或旧外卖模块。

## Step 55 实际完成事项

1. 完成旧外卖文档清理与目录归档。
2. 将旧外卖阶段 API/DB 设计归档到 `docs/legacy-takeaway/`。
3. 将根 `project-logs/` 下旧外卖修复日志归档到 `project-logs/legacy-takeaway/`。
4. 将当前入口文档改为校园代送试运营口径：
   - 根 `README.md`
   - `docs/README.md`
   - `docs/delivery-guide.md`
   - `docs/project-status-review.md`
   - `frontend/README.md`
   - 部署、回滚、贡献、安全和变更文档
5. 新增当前主线总览文档：
   - `docs/api-overview.md`
   - `docs/db-overview.md`
   - `docs/legacy-takeaway/README.md`
   - `project-logs/README.md`
6. 本轮没有修改 Java、SQL、Vue 业务代码、接口、路由、bridge、鉴权或 token 附着逻辑。

## Step 56 实际完成事项

1. 完成 settlement 批次操作审计 go / no-go 评估。
2. 确认当前 settlement 批次仍由 `campus_settlement_record.payout_batch_no` 聚合产生，不引入批次主表迁移。
3. 选择下一步进入 `campus_settlement_batch_operation_record` 最小实现。
4. 将 Step 54 的操作枚举草案收敛为两段式字段：
   - `operation_type`: `REVIEW` / `WITHDRAW`
   - `operation_result`: `PASSED` / `REJECTED` / `REQUESTED` / `RECORDED`
5. 明确 Step 57 只允许实现：
   - 批次操作审计表。
   - 批次操作历史列表接口。
   - review 审计写入接口。
   - withdraw 审计写入接口。
6. 明确 review / withdraw 只写操作审计：
   - 不改 `payout_status`。
   - 不改 `payout_verified*`。
   - 不清空 `payout_batch_no`。
   - 不做真实财务撤回。
7. 明确对账差异记录继续后置，不和批次操作审计并发实现。
8. 本轮没有修改 Java、SQL、Vue 业务代码、接口实现、路由、bridge、鉴权或 token 附着逻辑。

## Step 57 实际完成事项

1. 完成 settlement 批次操作审计最小后端实现。
2. 新增 `campus_settlement_batch_operation_record`，用于记录批次级 `REVIEW / WITHDRAW` 操作审计。
3. 同步维护：
   - `backend/db/init.sql`
   - `backend/db/migrations/V11__campus_settlement_batch_operation_record.sql`
   - `backend/src/main/resources/db/schema-h2.sql`
   - `backend/db/migrations/README.md`
4. 新增后端模型与接口层：
   - entity / DTO / query / VO
   - mapper
   - service / service impl
   - controller
5. 新增 admin 最小接口：
   - `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
   - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
   - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`
6. 写入前会复用现有批次聚合查询校验 `batchNo` 真实存在。
7. review / withdraw 只写 `campus_settlement_batch_operation_record`：
   - 不改 `payout_status`。
   - 不改 `payout_verified*`。
   - 不清空 `payout_batch_no`。
   - 不做真实财务撤回。
8. 本轮同步更新当前 API/DB 总览文档。
9. 本轮没有新增前端页面，没有改 bridge、鉴权、token 附着、路由、订单主状态或旧外卖模块。
10. 验证结果：
   - `.\mvnw.cmd -DskipTests compile` 通过。
   - `npm run build` 通过。
   - H2/test 接口运行态验证尚未执行，后置到 Step 58。

## Step 58 实际完成事项

1. 完成 settlement 批次操作审计 H2/test 运行态验证。
2. 使用 `campus_settlement_record.id = 1`、订单 `CR202604060001` 作为 H2 验证基线。
3. 通过 admin API 真实完成：
   - `POST /api/campus/admin/settlements/1/confirm`
   - `POST /api/campus/admin/settlements/batch-payout-result`
4. 生成固定验证批次号：`PBSTEP58VALIDATION`。
5. 调用 review 写入 `REVIEW / PASSED` 操作审计。
6. 调用 withdraw 写入 `WITHDRAW / REQUESTED` 操作审计。
7. 调用 `/operations` 查询到 2 条操作历史。
8. 验证 review / withdraw 后：
   - 单笔结算 `payoutStatus` 保持 `PAID`。
   - 单笔结算 `payoutBatchNo` 保持 `PBSTEP58VALIDATION`。
   - 单笔结算 `payoutVerified` 保持不变。
   - 批次详情 `totalCount / paidCount / payoutStatus` 保持不变。
9. 新增运行态证据文件：
   - `project-logs/campus-relay/runtime/step-58/settlement-batch-operation-api-validation.json`
10. 本轮没有修改 Java、SQL、Vue 业务代码，没有新增前端页面，没有改 bridge、鉴权、token 附着、路由或旧外卖模块。

## 下一轮建议

- 进入 `Step 62`
- 推荐顺序：
  1. 进入 settlement 对账差异记录最小方案设计。
  2. 先判断是否需要独立差异记录表。
  3. 明确差异来源、处理状态、接口边界和与 `campus_settlement_record` payout 摘要的兼容策略。
  4. 不默认写 SQL、Java、Vue 或页面。
  5. 不新增第五个 admin 页，不接真实打款，不改 bridge、不改鉴权、不改 token 附着。

## Step 59 实际完成事项

1. 完成 settlement 批次操作审计前端承接 go / no-go。
2. 评估方向 A：在现有 `CampusSettlementBatchDetail.vue` 中增加 operations 只读承接。
3. 评估方向 B：进入 settlement 对账差异记录最小方案设计。
4. 最终选择方向 A。
5. 选择原因：
   - Step 58 已证明后端 operations 可真实查询审计历史。
   - 现有批次详情页天然具备 `batchNo` 上下文。
   - 只读承接能补齐“批次汇总 -> 明细 -> 操作历史”的最小展示闭环。
   - 改动范围可限制在一个 API 封装和一个现有页面。
   - 不新增第五个 admin 页，不改后端、不改状态、不改 bridge。
6. 本轮没有修改 Java、SQL、Vue 业务代码，没有新增前端页面，没有改 bridge、鉴权、token 附着、路由或旧外卖模块。

## Step 60 实际完成事项

1. 完成 settlement 批次操作审计前端最小只读承接。
2. `frontend/src/api/campus-admin.js` 新增：
   - `getCampusSettlementBatchOperations(batchNo, params)`
3. `frontend/src/views/CampusSettlementBatchDetail.vue` 新增“批次操作历史”只读区。
4. 操作历史区只调用：
   - `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
5. 展示字段：
   - `id`
   - `operationType`
   - `operationResult`
   - `operationRemark`
   - `operatedByEmployeeId`
   - `operatedAt`
6. 增加中文 tag、空态、错误态和刷新操作历史按钮。
7. 本轮没有新增页面、路由、写操作按钮或后端接口。
8. 本轮没有调用 review / withdraw 写接口，没有改 bridge、鉴权、token 附着、订单主状态或真实财务动作。
9. 验证结果：
   - `npm run build` 通过。

## Step 61 实际完成事项

1. 完成 settlement 批次操作审计前端运行态验证。
2. 使用 H2/test 后端和 Vite 前端准备固定批次 `PBSTEP61UI`。
3. API 准备成功写入：
   - `REVIEW / PASSED`
   - `WITHDRAW / REQUESTED`
4. 打开页面：
   - `/campus/settlement-batches/PBSTEP61UI`
5. 页面验证通过：
   - 能展示批次号 `PBSTEP61UI`。
   - 能展示 settlement 明细订单 `CR202604060001`。
   - 能展示“批次操作历史”只读区。
   - 能展示 2 条操作审计记录。
   - 未出现操作历史加载失败。
6. 浏览器上下文中 `/operations` 返回 `code = 200`、`total = 2`。
7. 新增运行态证据文件：
   - `project-logs/campus-relay/runtime/step-61/settlement-batch-operation-ui-api-prep.json`
   - `project-logs/campus-relay/runtime/step-61/settlement-batch-operation-page-validation.json`
8. 本轮没有修改 Java、SQL、Vue 业务代码，没有新增页面，没有改 bridge、鉴权、token 附着、路由或旧外卖模块。

## Step 62 实际完成事项

1. 完成 settlement 对账差异记录最小方案设计。
2. 明确建议后续新增独立表 `campus_settlement_reconcile_difference_record`。
3. 明确差异记录只作为 admin 运营审计主数据，不直接改 settlement 金额、打款状态、批次号或订单主状态。
4. 明确最小差异类型建议：
   - `AMOUNT_MISMATCH`
   - `STATUS_MISMATCH`
   - `UNVERIFIED_PAID`
   - `FAILED_NEEDS_RETRY`
   - `MANUAL_NOTE`
5. 明确最小处理状态建议：
   - `OPEN`
   - `RESOLVED`
6. 明确现有 `campus_settlement_record` payout 摘要字段继续保留，并继续服务单笔页、批次页和 `reconcile-summary` 兼容读取。
7. 明确后续最小接口建议：
   - `GET /api/campus/admin/settlements/reconcile-differences`
   - `GET /api/campus/admin/settlements/reconcile-differences/{id}`
   - `POST /api/campus/admin/settlements/reconcile-differences`
   - `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`
8. 本轮没有写 SQL、Java、Vue、接口实现或页面，没有改 bridge、鉴权、token 附着、路由或旧外卖模块。

## Step 63 实际完成事项

1. 完成 settlement 对账差异记录实现 go / no-go。
2. 评估方向 A：进入 `campus_settlement_reconcile_difference_record` 最小后端实现。
3. 评估方向 B：继续停留在方案设计，补充字段校验细节。
4. 最终选择方向 A。
5. 选择原因：
   - Step 62 已把数据模型、状态、接口和兼容策略收敛到足够小。
   - 实现可独立于现有 settlement payout 摘要，不必修改已验证链路。
   - Step 64 可限定为后端最小闭环，不新增页面，不接真实财务。
   - 风险点已有明确控制方式：校验 settlement 存在、校验 batchNo、只更新差异记录自身字段。
6. Step 64 建议只实现表、MySQL/H2 schema、admin 列表/详情/创建/resolve 最小接口和 `OPEN -> RESOLVED` 状态保护。
7. 本轮没有写 SQL、Java、Vue、接口实现或页面，没有改 bridge、鉴权、token 附着、路由或旧外卖模块。

## 日志索引

- [领域模型重构规划](../../docs/campus-relay/domain-refactor-plan.md)
- [旧模块到校园代送模块映射](../../docs/campus-relay/legacy-to-campus-mapping.md)
- [Step 01 日志](step-01-domain-model-planning.md)
- [Step 02A 日志](step-02a-db-and-backend-skeleton.md)
- [Step 02B 日志](step-02b-readonly-api.md)
- [Step 03A 日志](step-03a-customer-order-create-and-pay.md)
- [Step 03B 日志](step-03b-courier-profile-and-admin-review.md)
- [Step 03C 日志](step-03c-courier-token-and-accept.md)
- [Step 03D 日志](step-03d-pickup-deliver-confirm.md)
- [Step 03E 日志](step-03e-cancel-after-sale-timeline-settlement.md)
- [Step 03F 日志](step-03f-admin-after-sale-exception-location-settlement.md)
- [Step 04 日志](step-04-after-sale-decision-settlement-admin-ops.md)
- [Step 05 日志](step-05-after-sale-execution-payout-and-order-ops.md)
- [Step 06 日志](step-06-customer-receipt-execution-audit-and-payout-verify.md)
- [Step 07 日志](step-07-customer-onboarding-and-frontend-entry.md)
- [Step 08 日志](step-08-admin-settlement-batch-demo-page.md)
- [Step 09 日志](step-09-admin-after-sale-execution-demo-page.md)
- [Step 10 日志](step-10-admin-courier-ops-demo-page.md)
- [Step 11 日志](step-11-admin-settlement-ops-demo-page.md)
- [Step 12 日志](step-12-onboarding-token-bridge-and-demo-polish.md)
- [Step 13 日志](step-13-courier-workbench-and-bridge-evidence.md)
- [Step 14 日志](step-14-bridge-audit-and-workbench-accept.md)
- [Step 15 日志](step-15-bridge-evidence-and-workbench-detail.md)
- [Step 16 日志](step-16-bridge-plan-and-workbench-pickup.md)
- [Step 17 日志](step-17-bridge-readiness-and-workbench-deliver.md)
- [Step 18 日志](step-18-bridge-readiness-checklist-and-workbench-exception.md)
- [Step 19 日志](step-19-bridge-templates-and-workbench-confirm-visual.md)
- [Step 20 日志](step-20-bridge-template-hardening-and-workbench-completed-visual.md)
- [Step 21 日志](step-21-real-verification-and-workbench-completed-readback.md)
- [Step 22 日志](step-22-real-local-chain-and-h2-seed.md)
- [Step 23 日志](step-23-shared-regression-and-customer-result-readback.md)
- [Step 23 共享回归留痕](step-23-shared-regression-evidence.md)
- [Step 24 日志](step-24-bridge-readiness-and-customer-result-polish.md)
- [Step 25 日志](step-25-repo-external-bridge-verification.md)
- [Step 26 日志](step-26-repo-external-evidence-closure.md)
- [Step 27 日志](step-27-key-external-asset-verification.md)
- [Step 28 日志](step-28-critical-business-asset-followup.md)
- [Step 29 日志](step-29-owner-confirmation-and-bridge-reassessment.md)
- [Step 30 日志](step-30-phase-a-readiness-reassessment.md)
- [Step 31 日志](step-31-minimal-phase-a-candidate-and-preflight.md)
- [Step 32 日志](step-32-phase-a-candidate-expansion-and-go-no-go.md)
- [Step 33 日志](step-33-phase-a-no-op-freeze-and-resume-criteria.md)
- [Step 34 日志](step-34-non-bridge-direction-and-display-polish-assessment.md)
- [Step 35 日志](step-35-display-polish-execution-round-1.md)
- [Step 36 日志](step-36-display-polish-execution-round-2.md)
- [Step 37 日志](step-37-display-polish-execution-round-3.md)
- [Step 38 日志](step-38-display-polish-execution-round-4.md)
- [Step 39 日志](step-39-display-polish-review-and-freeze-decision.md)
- [Step 40 日志](step-40-delivery-packaging-and-demo-script.md)
- [Step 41 日志](step-41-delivery-assets-checklist-and-recording-plan.md)
- [Step 42 日志](step-42-real-media-capture-and-archive.md)
- [Step 43 日志](step-43-media-gap-gate-or-non-bridge-backend-assessment.md)
- [Step 44 日志](step-44-exception-history-minimal-solution-design.md)
- [Step 45A 日志](step-45a-exception-history-minimal-implementation.md)
- [Step 45B 日志](step-45b-exception-resolve-minimal-action-design.md)
- [Step 46 日志](step-46-exception-resolve-minimal-implementation.md)
- [Step 47 日志](step-47-admin-exception-frontend-go-no-go.md)
- [Step 48 日志](step-48-admin-exception-frontend-minimal-handoff.md)
- [Step 49 日志](step-49-admin-exception-runtime-validation.md)
- [Step 50 日志](step-50-after-sale-execution-history-design.md)
- [Step 51A 日志](step-51a-after-sale-execution-history-minimal-implementation.md)
- [Step 51B 日志](step-51b-after-sale-execution-history-frontend-go-no-go.md)
- [Step 52 日志](step-52-after-sale-execution-history-frontend-minimal-handoff.md)
- [Step 53 日志](step-53-after-sale-execution-history-frontend-runtime-validation.md)
- [Step 54 日志](step-54-settlement-batch-review-withdraw-reconcile-design.md)
- [Step 55 日志](step-55-docs-cleanup-and-legacy-archive.md)
- [Step 56 日志](step-56-settlement-batch-operation-audit-go-no-go.md)
- [Step 57 日志](step-57-settlement-batch-operation-audit-minimal-implementation.md)
- [Step 58 日志](step-58-settlement-batch-operation-runtime-validation.md)
- [Step 59 日志](step-59-settlement-batch-operation-frontend-go-no-go.md)
- [Step 60 日志](step-60-settlement-batch-operation-frontend-minimal-handoff.md)
- [Step 61 日志](step-61-settlement-batch-operation-frontend-runtime-validation.md)
- [Step 62 日志](step-62-settlement-reconcile-difference-minimal-solution-design.md)
- [Step 63 日志](step-63-settlement-reconcile-difference-implementation-go-no-go.md)
- [Step 64 日志](step-64-settlement-reconcile-difference-minimal-implementation.md)
- [Step 65 日志](step-65-settlement-reconcile-difference-frontend-go-no-go.md)
- [Step 66 日志](step-66-settlement-reconcile-difference-frontend-minimal-handoff.md)
- [Step 67 日志](step-67-settlement-reconcile-difference-frontend-runtime-validation.md)
- [Step 68 日志](step-68-settlement-reconcile-difference-frontend-freeze-decision.md)
- [Step 69 日志](step-69-settlement-p3-freeze-review-and-maintenance-decision.md)
- [Step 70 日志](step-70-non-bridge-backend-lines-overall-review.md)
- [Step 71 日志](step-71-overall-maintenance-and-delivery-readiness-review.md)
- [Step 72 日志](step-72-tencent-map-admin-ops-minimal-pilot.md)
- [Step 73 日志](step-73-tencent-map-productization-go-no-go.md)
- [Step 74 日志](step-74-trial-operation-productization-roadmap.md)
- [Step 75 日志](step-75-trial-operation-preflight-handbook.md)
- [试运营运行配置与 Preflight 手册](../../docs/trial-operation-preflight.md)
- [Step 76 日志](step-76-simulated-funds-productization-boundary.md)
- [模拟资金链路产品化边界](../../docs/simulated-funds-boundary.md)
- [Step 77 日志](step-77-delivery-doc-consistency-and-minimal-preflight.md)
- [Step 78 日志](step-78-trial-operation-rc-review-and-minimal-smoke.md)
- [Step 79 日志](step-79-rc-runtime-config-and-build-warning-hardening.md)
- [Step 80 日志](step-80-frontend-bundle-warning-go-no-go.md)
- [Step 81 日志](step-81-frontend-bundle-freeze-and-no-op-review.md)
- [Step 82 日志](step-82-trial-operation-next-mainline-go-no-go.md)
- [Step 83 日志](step-83-trial-operation-ops-entrypoint-boundary-and-preflight-script.md)
- [Step 84 日志](step-84-trial-operation-sample-validation-script.md)
- [Step 85 日志](step-85-trial-operation-command-index-and-manual-h2-reset-guide.md)
- [Step 86 日志](step-86-trial-operation-ops-line-freeze-and-next-mainline.md)
- [Step 87 日志](step-87-product-trial-readiness-gap-and-next-mainline.md)
- [Step 88 日志](step-88-env-secret-hardening-and-deployment-preflight.md)
- [环境变量与密钥配置清单](../../docs/deployment/env-and-secret-checklist.md)
- [Step 89 日志](step-89-post-deploy-smoke-checklist.md)
- [部署后 Smoke Checklist](../../docs/deployment/post-deploy-smoke-checklist.md)
- [Step 90 日志](step-90-minimal-ci-check-boundary-go-no-go.md)
- [最小 CI 检查边界](../../docs/deployment/ci-check-boundary.md)
- [Step 91 日志](step-91-minimal-github-actions-ci.md)
- [Step 92 日志](step-92-ci-first-run-followup.md)
- [Step 93 日志](step-93-github-actions-runtime-warning-go-no-go.md)
- [Step 94 日志](step-94-ci-runtime-upgrade-followup.md)
- [Step 95 日志](step-95-internal-trial-compose-package.md)
- [Step 96 日志](step-96-single-server-internal-trial-deploy-and-smoke.md)
- [Step 97 日志](step-97-global-working-memory-bootstrap.md)
- [Step 98 日志](step-98-internal-trial-ops-hardening-and-backup-prep.md)
- [Step 99 日志](step-99-minimal-restore-drill-and-ops-handover.md)
- [Step 100 日志](step-100-internal-trial-ops-runbook-and-entry-go-no-go.md)
- [Step 101 日志](step-101-admin-text-mojibake-fix-and-frontend-readability.md)
- [Step 102 日志](step-102-campus-visual-system-admin-shell-refresh.md)
- [Step 103 日志](step-103-admin-glass-visual-rebaseline-and-text-hardening.md)
- [Step 104 日志](step-104-light-admin-visual-and-courier-ops-mobile-fix.md)
- [Step 105 日志](step-105-admin-shell-consistency-and-statistics-rebaseline.md)
- [Step 106 日志](step-106-after-sale-table-resize-handle-fix.md)
- [Step 107 日志](step-107-global-button-plain-fix-and-employee-action-cell-cleanup.md)
- [Step 108 日志](step-108-parttime-login-and-independent-entry.md)
- [Step 109 日志](step-109-parttime-shell-and-profile-page.md)
- [Step 110 日志](step-110-dual-android-shell-route-evaluation.md)
- [Step 111 日志](step-111-dual-capacitor-scaffold-go-no-go.md)
- [Step 112 日志](step-112-frontend-android-build-targets.md)
- [Step 113 日志](step-113-dual-capacitor-android-shell-scaffold.md)
- [Step 114 日志](step-114-android-local-build-validation.md)
- [Step 115 日志](step-115-android-device-smoke-entry-and-blocker.md)
- [Step 116 日志](step-116-android-emulator-smoke-and-api-base.md)
- [Step 117 日志](step-117-user-mobile-home-campusization.md)
- [Step 118 日志](step-118-user-home-visual-smoke-and-login-copy.md)
- [Step 119 日志](step-119-user-campus-order-entry.md)
- [Step 120 日志](step-120-android-api-base-layering-and-user-entry-smoke.md)
- [Step 121 日志](step-121-android-public-api-base-drill-and-smoke-hardening.md)
- [Step 122 日志](step-122-public-api-base-refresh-and-android-public-build.md)
- [Step 123 日志](step-123-android-public-webview-real-api-smoke.md)
- [Step 124 日志](step-124-android-public-webview-readiness.md)
- [Step 125 日志](step-125-legacy-takeaway-audit-and-handoff.md)
- [Step 126 日志](step-126-campus-admin-frontend-visual-rebaseline.md)
- [Step 127 日志](step-127-mobile-user-parttime-visual-unification.md)
- [Step 128 日志](step-128-admin-shell-visual-hardening.md)
- [Step 129 日志](step-129-admin-usability-sweep-and-visible-ui-fixes.md)
- [Step 130 日志](step-130-admin-final-visible-ui-sweep.md)
- [Step 131 日志](step-131-local-internal-trial-smoke.md)
- [Step 132 日志](step-132-stable-browser-smoke-toolchain.md)
- [Step 133 日志](step-133-local-internal-trial-rc-review.md)
- [Step 134 日志](step-134-remote-internal-trial-smoke-prep.md)
- [Step 135 日志](step-135-remote-internal-trial-smoke-validation.md)
- [Step 136 日志](step-136-server-ops-check-and-restore-drill.md)
- [Step 137 日志](step-137-github-server-sync-and-remote-smoke.md)
- [Step 138 日志](step-138-internal-trial-port-hardening.md)
- [远端内测 Smoke 执行说明](../../docs/deployment/remote-internal-trial-smoke.md)
- [旧外卖模块删除前审计与分阶段收口计划](legacy-takeaway-removal-readiness.md)
- [AI 协作交接文件](agent-collaboration.md)
- [全局工作记忆](global-working-memory.md)
- [试运营脚本入口说明](../../scripts/trial-operation/README.md)
- [bridge 收口评估](bridge-phaseout-evaluation.md)
- [bridge 执行准备 checklist](bridge-execution-readiness-checklist.md)
- [bridge 联调/回归模板](bridge-regression-template.md)
- [待处理事项](pending-items.md)
- [文件改动清单](file-change-list.md)

# Step 129 - 管理后台可用性巡检与明显样式问题修复

## 本轮目标

在 Step 128 管理后台视觉基线修正之后，继续处理当前后台最容易影响本地查看和演示的明显问题：

1. 页面间返回运营总览入口不够明确。
2. Element Plus 表格右侧 fixed 操作列容易出现阴影、遮罩或按钮覆盖观感。
3. 表头列 resize 视觉条仍可能被误认为可拖拽界面。
4. 管理后台表格、筛选区和分页区需要继续限制横向溢出。

本轮只做管理后台前端可用性修补，不改业务逻辑。

## 已完成项

1. `MainLayout.vue` breadcrumb 首页入口由单图标补充为“运营总览”文字入口，进入 `运营人员`、`数据看板` 等页面后能更明确回到后台主页。
2. 取消所有后台 Vue 页面中的 `fixed="right"` 操作列，避免 Element Plus fixed column 阴影/遮罩导致操作按钮被覆盖或右侧出现异常层。
3. 强化 Element Plus 表格全局修正：
   - 扩展隐藏 `.el-table__column-resize`、`.el-table__resize-proxy` 等 resize 句柄。
   - 禁止表头显示 resize cursor。
   - 固定列相关 class 继续强制白底、无阴影，避免遗留 fixed 列样式污染。
   - 调整表格内按钮间距，降低操作区拥挤感。
4. 强化 `.campus-admin-page` 通用布局：
   - 卡片和表格容器加 `min-width: 0`。
   - 表格 wrapper 加最大宽度约束。
   - inline form 自动换行，避免筛选区在窄屏把页面撑开。
   - 分页区保留受控横向滚动，不影响主页面。

## 修改文件

- `frontend/src/layout/MainLayout.vue`
- `frontend/src/styles/element-plus.scss`
- `frontend/src/styles/global.scss`
- `frontend/src/views/Employee.vue`
- `frontend/src/views/CampusAfterSaleExecutionList.vue`
- `frontend/src/views/CampusSettlementOpsView.vue`
- `frontend/src/views/CampusExceptionOpsView.vue`
- `frontend/src/views/CampusSettlementBatchList.vue`
- `frontend/src/views/Category.vue`
- `frontend/src/views/Dish.vue`
- `frontend/src/views/Order.vue`
- `frontend/src/views/Setmeal.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 未改动内容

- 未改 bridge。
- 未改 `request.js`。
- 未改任何 `campus-*` API 文件运行时行为。
- 未改 token 附着逻辑。
- 未改路由语义。
- 未改后端。
- 未新增页面。
- 未删除旧外卖兼容模块。

## 验证结果

- `npm run build` 通过。
- `git diff --check` 通过。
- 已确认源码范围内不再存在 `fixed="right"` 操作列。
- 已启动本地 frontend dev server 并打开 `/employee` 做过一次视觉抽查；后续 Playwright MCP transport 中断，未继续批量截图，因此本轮以源码巡检、构建和 diff 校验为准。

## 遗留问题

1. 当前修的是明显可用性问题，不是按新原型 1:1 重做后台。
2. 若后续用户提供完整后台原型图，应按页面分批迁移，不要一次性重构全部后台。
3. 如果真实数据量导致某些长表仍需横向滚动，优先保留表格自身滚动，不恢复 fixed 操作列。

## 下一步建议

Step 130 建议先暂停继续“凭感觉”改后台样式，等待用户提供更明确原型或选择一个具体后台页作为样式样板：

1. 若用户提供原型图：优先以 `Dashboard.vue` 或 `Employee.vue` 作为第一张样板页。
2. 若不提供原型图：只做 bug 级修复，不继续大改视觉。
3. 继续保持 bridge 冻结、旧模块兼容和不改业务语义。

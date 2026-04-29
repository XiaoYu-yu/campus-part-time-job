# Step 128 - 管理后台视觉基线修正

## 本轮目标

只处理管理后台展示层的明显问题，暂不处理用户端、兼职端和服务器部署。

## 已完成项

1. 修复 `MainLayout.vue` 侧边栏折叠状态未同步到外层 aside 的问题。
2. 将 Element Plus 全局语言切换为中文，避免分页组件继续显示 `Total / Go to / page` 等英文文案。
3. 修正 Element Plus 表格视觉噪声：
   - 隐藏表头列拖拽视觉条。
   - 固定列背景回到白色，降低右侧操作列阴影/遮挡感。
   - 禁止表头显示拖拽 cursor。
4. 修正 danger plain 按钮视觉：
   - 默认保持白底红边。
   - disabled 状态不再显示成一整块高饱和红色。
5. 为 `.campus-admin-page` 统一补一层后台页面视觉基线：
   - 页面头图区改为贴近原型图的浅色蓝白玻璃态。
   - 卡片、筛选区、表格区、说明块、只读 badge 统一边框、圆角和阴影。
   - 不改变页面业务结构、接口调用、路由和状态机。
6. 将 `MainLayout.vue` 从早期青绿色后台壳子调整为原型图方向的蓝白后台壳子：
   - 左侧菜单采用白底、蓝色 active、轻分组。
   - 顶部栏增加搜索占位区域，保留通知和用户入口。
   - logo、侧栏、用户卡片、breadcrumb hover 统一为蓝色视觉 token。

## 修改文件

- `frontend/src/layout/MainLayout.vue`
- `frontend/src/main.js`
- `frontend/src/styles/element-plus.scss`
- `frontend/src/styles/global.scss`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 未改动内容

- 未改 bridge。
- 未改 `request.js`。
- 未改任何 `campus-*` API 文件运行时行为。
- 未改路由。
- 未改后端。
- 未新增页面。
- 未删除旧外卖兼容模块。

## 验证结果

- `npm run build` 通过。
- `git diff --check` 通过。
- 已用本地前端 dev server 和 Playwright MCP 进行后台页面视觉抽查：`/dashboard`、`/employee`、`/campus/after-sale-executions` 可打开；因后端未运行，页面顶部出现网络错误提示属于预期。
- `/campus/after-sale-executions` 抽查时未发现页面级横向溢出，表格列拖拽视觉条已隐藏。

## 遗留问题

1. 当前只做管理后台全局视觉修正，未逐页精修全部 admin 页面。
2. 若后续要进一步贴近原型图，建议按单页推进，不要一次性重做整个后台。
3. 旧兼容模块仍保留，只做兼容文案和展示隔离，不删除后端旧模块。

## 下一步建议

Step 129 建议只选一个管理后台页面继续精修，优先级建议：

1. `Dashboard.vue`：作为后台入口页，最影响第一印象。
2. `Employee.vue`：作为基础运营人员管理页，最容易暴露表格和按钮细节。
3. `CampusExceptionOpsView.vue`：作为新异常处理闭环页，最能体现项目新能力。

继续保持 bridge 冻结、旧模块兼容和不改业务语义。

# Step 105 - admin 公共壳层一致性修复与数据看板展示重整

## 本轮目标

1. 修复 `运营人员` 与 `数据看板` 两个页面进入后丢失后台公共导航、无法像其他后台页一样回到首页的问题。
2. 将 `Statistics.vue` 拉回当前浅色校园后台视觉基线，避免仍保留旧外卖风格孤岛页面。
3. 在不改接口、鉴权、路由、bridge 和业务语义的前提下，多跑一轮前端与后端回归检查。

## 根因判断

1. `frontend/src/views/Employee.vue` 没有接入 `MainLayout`，因此进入页面后丢失左侧菜单、顶部 breadcrumb 和首页返回入口。
2. `frontend/src/views/Statistics.vue` 同样没有接入 `MainLayout`，并且仍保留旧式独立看板样式，导致和现有 admin 页面不一致。
3. 当前仓库内使用 `MainLayout` 的后台高曝光页面已经基本统一，缺口集中在这两个旧页面，不是路由或鉴权层故障。

## 已完成项

1. `Employee.vue` 已接回 `MainLayout`，进入页面后恢复：
   - 左侧后台菜单
   - 顶部 breadcrumb
   - 首页返回入口
2. `Statistics.vue` 已接回 `MainLayout`，进入页面后恢复统一后台导航结构。
3. `Statistics.vue` 已按当前浅色校园后台风格重整展示层：
   - 新增顶部 hero 区
   - 新增指标区和图表区说明层级
   - 统一卡片、标题、tag 风格
   - 保留原有图表与数据加载逻辑
4. `Statistics.vue` 已补 `onBeforeUnmount` 清理：
   - 移除 `resize` 事件监听
   - 释放 ECharts 实例
5. `frontend/src/utils/echarts.js` 已注册 `LegacyGridContainLabel`，消除 ECharts 6 在当前 `containLabel` 配置下的告警。
6. `Statistics.vue` 已改为复用已有图表实例，不再重复 `echarts.init()`，真实页面 smoke 下控制台 warning 已清零。
7. 已确认当前仓库内缺失 `MainLayout` 的后台高曝光页就是 `Employee.vue` 和 `Statistics.vue`，本轮已补齐。

## 明确没动的范围

1. 没改 `frontend/src/utils/request.js`
2. 没改任何 `campus-*` API 文件运行时行为
3. 没改 token 附着逻辑
4. 没改接口路径、接口语义和调用顺序
5. 没改 bridge、鉴权、路由
6. 没改后端业务和数据库
7. 没新增页面

## 实际修改文件

1. `frontend/src/views/Employee.vue`
2. `frontend/src/views/Statistics.vue`
3. `frontend/src/utils/echarts.js`
4. `project-logs/campus-relay/summary.md`
5. `project-logs/campus-relay/pending-items.md`
6. `project-logs/campus-relay/file-change-list.md`
7. `project-logs/campus-relay/global-working-memory.md`
8. `project-logs/campus-relay/step-105-admin-shell-consistency-and-statistics-rebaseline.md`

## 验证结果

1. `git diff --check` 通过
2. `frontend` `npm run build` 通过
3. `frontend` `npm run test -- text.spec.js` 通过
4. `frontend` `npm run lint` 通过
5. `backend` `.\mvnw.cmd -DskipTests compile` 通过
6. 复用本地已运行的 `127.0.0.1:5173 + 127.0.0.1:8080` 完成 Playwright smoke：
   - `/login` 可用 admin 账号登录
   - `/employee` 已可见统一 sidebar + breadcrumb + 首页入口
   - `/statistics` 已可见统一 sidebar + breadcrumb + 首页入口
   - `/statistics` 控制台 warning 已降为 0

## 当前结论

1. `运营人员` 和 `数据看板` 页面已恢复和其他后台页一致的公共壳层行为，不再是独立孤岛页。
2. 这轮修复的是 admin 壳层一致性和统计页展示层，不涉及 bridge 主线。
3. bridge 继续保持 `Phase A no-op` 冻结态，没有变化。

## 遗留问题

1. 这轮还没有在本地浏览器里做一轮人工点击验收，仍建议 owner 重点复核：
   - `/employee`
   - `/statistics`
   - breadcrumb 返回首页
   - 左侧菜单高亮
2. 统计页虽然已回到统一视觉层级，但图表内容本身仍是旧统计域，只做了展示重整，没有重做数据语义。

## 下一步建议

1. 先由 owner 本地点开 `/employee` 和 `/statistics` 确认导航返回和视觉层级是否已回正。
2. 如果仍有高曝光前端小问题，继续走单页、小范围、只改展示层的修复方式。
3. 在没有新的真实试运营阻塞前，不重开 bridge，不扩第五个 admin 页。

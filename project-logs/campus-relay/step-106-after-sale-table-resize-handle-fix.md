# Step 106 - 售后执行页表格列拖拽关闭修复

## 本轮目标

1. 修复 `CampusAfterSaleExecutionList.vue` 表格列之间出现可鼠标拖拽 resize 条的问题。
2. 保持页面读取逻辑、筛选逻辑、详情 drawer 和只读语义不变。

## 根因判断

1. `Element Plus` 的 `el-table-column` 在当前 `border` 表格下默认允许列宽拖拽。
2. `frontend/src/views/CampusAfterSaleExecutionList.vue` 主列表和执行历史表都没有显式关闭 `resizable`，因此列分隔线会表现为可拖拽条。

## 已完成项

1. 为售后执行主表所有列显式追加 `:resizable="false"`。
2. 为详情 drawer 内的执行历史表所有列显式追加 `:resizable="false"`。
3. 保留原有：
   - 接口调用
   - 分页逻辑
   - 筛选参数语义
   - 详情 drawer 行为
   - 只读运营定位

## 明确没动的范围

1. 没改 bridge
2. 没改 `request.js`
3. 没改任何 `campus-*` API 文件运行时行为
4. 没改接口语义
5. 没改路由
6. 没改后端
7. 没新增页面

## 实际修改文件

1. `frontend/src/views/CampusAfterSaleExecutionList.vue`
2. `project-logs/campus-relay/summary.md`
3. `project-logs/campus-relay/pending-items.md`
4. `project-logs/campus-relay/file-change-list.md`
5. `project-logs/campus-relay/step-106-after-sale-table-resize-handle-fix.md`

## 验证结果

1. `frontend` `npm run build` 通过
2. `frontend` `npm run lint` 通过
3. `git diff --check` 通过

## 当前结论

1. 售后执行页和其历史表不再出现列宽可拖拽条。
2. 这轮只修了组件默认交互噪音，不涉及业务行为变更。
3. bridge 继续保持 `Phase A no-op` 冻结态，没有变化。

## 下一步建议

1. owner 本地再看一眼 `CampusAfterSaleExecutionList.vue`，确认列分隔线已不再可拖拽。
2. 若其它高曝光表格也有同类拖拽条，再按单页、小范围方式逐个关闭，不做全站机械改。

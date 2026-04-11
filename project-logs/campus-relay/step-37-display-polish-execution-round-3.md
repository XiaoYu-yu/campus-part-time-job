# Step 37 - 展示级优化执行轮 3

## 本轮目标

1. 基于 Step 35 / Step 36 已完成 customer / courier 三个主演示页面 polish，进入单页 admin 只读运营页 polish。
2. 本轮只允许从两个候选中选择一个：
   - `frontend/src/views/CampusSettlementOpsView.vue`
   - `frontend/src/views/CampusAfterSaleExecutionList.vue`
3. 不铺开 admin 四页，不新增页面，不改 bridge，不改接口，不改路由，不改后端。

## 本轮选择

结论：选择 `CampusSettlementOpsView.vue`。

## 为什么选 settlement 不选 after-sale

### 演示收益

1. `CampusSettlementOpsView.vue` 同时包含摘要、筛选、列表和详情 drawer 四段结构，是当前 admin 只读运营页里最完整的后台演示形态。
2. settlement 运营页更适合承接 Step 35 / Step 36 的主演示闭环：订单完成后可展示结算摘要、单笔记录和详情核对。
3. after-sale 执行页更偏售后决策和纠正审计，解释成本高，适合下一轮单独处理。

### 风险

1. settlement 页当前是纯只读页面，本轮 polish 可以限制在展示层。
2. 本轮没有改查询参数、分页逻辑、接口调用或 settlement 业务逻辑。
3. after-sale 页虽然也是只读，但包含执行状态、纠正信息和售后结果 drawer，状态语义更复杂。

### 改动范围

1. 只改一个 Vue 页面。
2. 不改 API 封装。
3. 不改 router。
4. 不改后端。

### 与当前主演示链路的互补性

1. Step 35 / Step 36 已覆盖 customer / courier 前台链路。
2. settlement 单页 polish 能补上 admin 运营视角中的“完成后结算回看”。
3. 这比继续处理 after-sale 更贴近当前已跑通的 completed 链路收尾。

## `CampusSettlementOpsView.vue` 展示级优化

本轮只做展示层调整：

1. 顶部新增“只读运营”标记。
2. 新增三段式运营引导：
   - 摘要：对账聚合
   - 筛选：状态定位
   - 详情：单笔核对
3. 筛选区新增标题和说明，明确筛选只影响读取，不触发写操作。
4. 表格区新增只读提示，明确不提供确认结算、打款、撤回或核对写操作。
5. 表格空态文案更明确，提示可调整状态或订单号后重新查询。
6. 结算状态和打款状态 tag 从原始枚举展示调整为中文展示：
   - `PENDING` -> `待结算`
   - `SETTLED` -> `已结算`
   - `UNPAID` -> `未打款`
   - `PAID` -> `已打款`
   - `FAILED` -> `打款失败`
7. 详情 drawer 顶部新增当前结算记录摘要卡片。
8. 详情 drawer 新增“单笔结算详情”分组说明，明确字段来自现有详情读取接口。
9. 增加对应 CSS，用于统一只读标记、运营引导、表格提示和详情摘要层级。

## 明确没动什么

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改任何 `campus-*` API 文件运行时行为。
4. 未改 token 附着逻辑。
5. 未改 API 调用顺序。
6. 未改 `getCampusSettlementReconcileSummary`。
7. 未改 `getCampusSettlements`。
8. 未改 `getCampusSettlementDetail`。
9. 未改分页逻辑。
10. 未改筛选参数语义。
11. 未改 settlement 业务逻辑。
12. 未改路由。
13. 未改后端代码。
14. 未处理 `CampusAfterSaleExecutionList.vue`。
15. 未新增第五个 admin 页。

## 验证结果

1. `.\mvnw.cmd -DskipTests compile`：通过。
2. `npm run build`：通过。
3. 静态范围确认：
   - 本轮没有修改 `request.js`
   - 本轮没有修改 `campus-admin.js`
   - 本轮没有修改 router
   - 本轮没有修改后端代码
4. 页面行为层面：
   - 摘要仍通过现有 `GET /api/campus/admin/settlements/reconcile-summary`
   - 列表仍通过现有 `GET /api/campus/admin/settlements`
   - 详情 drawer 仍通过现有 `GET /api/campus/admin/settlements/{id}`
   - 筛选参数和分页参数未改变

## 是否影响 bridge

不影响。

本轮没有出现 Step 33 定义的恢复推进触发条件，bridge 继续保持 `Phase A no-op` 冻结态。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. bridge 完全保留。
3. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
4. 本轮最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `frontend/src/views/CampusSettlementOpsView.vue`
2. `project-logs/campus-relay/summary.md`
3. `project-logs/campus-relay/pending-items.md`
4. `project-logs/campus-relay/file-change-list.md`
5. `project-logs/campus-relay/step-36-display-polish-execution-round-2.md`
6. `project-logs/campus-relay/step-37-display-polish-execution-round-3.md`

## 下一轮建议

1. Step 38 先回看 Step 37 settlement 单页 polish 效果。
2. 若继续 admin 展示级优化，建议只处理 `CampusAfterSaleExecutionList.vue`，不要铺开其余 admin 页。
3. 第五个 admin 页继续后置。
4. bridge 主线继续冻结，除非出现 Step 33 定义的恢复推进触发条件。

## Step 38 执行回填

1. Step 38 已按本建议进入 after-sale 单页 admin 只读运营页 polish。
2. 本轮只处理 `frontend/src/views/CampusAfterSaleExecutionList.vue`，没有继续回改 `CampusSettlementOpsView.vue`，也没有处理其余 admin 页。
3. 选择原因：
   - Step 37 已完成 settlement 只读运营页，Step 38 处理 after-sale 能补齐当前两个最主要 admin 只读运营演示面。
   - after-sale 页承载执行状态、决策类型、人工纠正和售后结果 drawer，解释成本高，适合单独 polish。
   - 本轮改动可限制在模板、状态文案和样式层，不需要改售后执行逻辑。
4. 展示级优化摘要：
   - 增加“只读执行”标记。
   - 增加三段式页面导览。
   - 增加筛选区说明和表格只读提示。
   - 优化空态文案。
   - 将决策类型、执行状态和人工纠正 tag 改为中文展示文案。
   - 优化售后结果 drawer 顶部摘要、字段来源说明和只读分区说明。
5. Step 38 明确未改：
   - bridge
   - `request.js`
   - `campus-*` API 文件运行时行为
   - token 附着逻辑
   - API 调用顺序
   - 售后执行逻辑
   - 筛选参数语义
   - 分页逻辑
   - 路由
   - 后端代码
   - 新页面
6. Step 39 建议：
   - 先回看 Step 37 / Step 38 两个 admin 单页 polish 效果。
   - 若没有明显展示短板，不建议继续机械寻找下一页 polish。
   - 第五个 admin 页继续后置。
   - bridge 继续保持 `Phase A no-op` 冻结态。

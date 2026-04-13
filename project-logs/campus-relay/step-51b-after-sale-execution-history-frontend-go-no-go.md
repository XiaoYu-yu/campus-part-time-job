# Step 51B - 售后执行历史前端承接 go / no-go

## 本轮目标

1. 基于 Step 51A 已落地的售后执行历史后端能力，判断是否值得做 admin 最小前端承接。
2. 只做 go / no-go 评估，不直接新增页面、不改接口、不改 bridge。
3. 在方向 A“售后执行历史最小前端承接”和方向 B“P3 settlement 批次复核、撤回和对账方案设计”之间二选一。

## 为什么 Step 51B 先做 go / no-go

Step 51A 已让售后执行历史具备后端审计能力：

1. `campus_after_sale_execution_record` 已落地。
2. 现有 `POST /api/campus/admin/orders/{id}/after-sale-execution` 已同事务追加历史写入。
3. `GET /api/campus/admin/after-sale-execution-records` 已可分页查询历史。
4. 订单表 `after_sale_execution_*` 当前摘要继续兼容旧读取路径。

当前缺口是：这套历史审计能力仍只能通过接口或运行态验证 JSON 查看，admin 售后执行页只展示当前摘要和 after-sale-result drawer。若不做前端承接，运营演示时无法直接解释 `PENDING -> FAILED -> SUCCESS` 多次执行留痕。

## 方向 A：售后执行历史最小前端承接

### 最小承接形态

如果后续执行，建议只做最小承接，不新增复杂后台系统：

1. 优先复用现有 `CampusAfterSaleExecutionList.vue`，在详情 drawer 中增加“执行历史”只读区。
2. 接入 `GET /api/campus/admin/after-sale-execution-records`。
3. 以当前 `relayOrderId` 拉取该订单历史。
4. 展示历史列表字段：
   - `id`
   - `relayOrderId`
   - `previousExecutionStatus`
   - `executionStatus`
   - `executionRemark`
   - `executionReferenceNo`
   - `executedByEmployeeId`
   - `executedAt`
   - `corrected`
5. 只读展示，不新增执行、回滚、删除、修改按钮。
6. 不改变现有售后结果 drawer 的 after-sale-result 读取语义。

### 是否需要独立新页面

当前不建议独立新增售后执行历史页。

理由：

1. 现有 `CampusAfterSaleExecutionList.vue` 已经是 admin 售后执行演示页。
2. 历史记录天然跟某个订单的详情 drawer 关联。
3. 独立新页容易被误解为“补第五个 admin 页”，而当前目标不是补页数。
4. drawer 内只读承接改动更小，行为边界更清楚。

### 演示收益

收益较高：

1. 能直接展示 Step 51A 的后端成果，而不是停留在 API 层。
2. 能讲清“当前摘要”和“历史审计”的关系。
3. 能展示 `FAILED -> SUCCESS` 人工纠正时历史表保留两条记录，而当前摘要只显示最新结果。
4. 能补齐售后执行页目前只看当前结果、看不到历史尝试的问题。

### 真实使用收益

收益明确：

1. admin 可在同一订单详情内查看每次执行尝试。
2. 执行失败原因、执行参考号和执行人可以按时间回看。
3. 不需要依赖数据库或 curl 才能解释纠正审计。
4. 仍保持售后执行历史为只读审计能力，不引入真实退款或回滚操作。

### 改动范围

如果 Step 52 执行方向 A，应限制在最小前端范围：

1. `frontend/src/api/campus-admin.js` 增加：
   - `getCampusAfterSaleExecutionRecords`
2. `frontend/src/views/CampusAfterSaleExecutionList.vue`：
   - 详情 drawer 内增加“执行历史”只读区。
   - 打开 drawer 时按 `relayOrderId` 加载历史。
   - 增加历史 loading / empty / error 提示。
3. 日志同步更新。

不应新增后端接口、不应改路由、不应改 admin 主框架、不应改 bridge。

### 风险

主要风险：

1. 容易从只读历史承接扩成执行回滚、删除、重新执行等写操作。
2. 容易与现有售后执行分页的“当前摘要”概念混淆。
3. 若做独立新页，会偏离“不补第五个 admin 页”的约束。
4. 若一次性做筛选页、详情页和写操作，范围会超过当前最小承接目标。

### 风险控制

Step 52 若进入实现，必须固定以下限制：

1. 只做 drawer 内只读历史区。
2. 只接 `GET /api/campus/admin/after-sale-execution-records`。
3. 不新增路由。
4. 不新增写接口。
5. 不做修改、删除、回滚、真实退款、settlement 联动。
6. 不改当前售后执行摘要接口。
7. 不改 bridge、鉴权、token 附着逻辑或旧外卖模块。

## 方向 B：P3 settlement 批次复核、撤回和对账方案设计

### 当前价值

P3 settlement 批次复核、撤回和对账具备长期运营价值：

1. 能补齐结算批次二次复核后的撤回和重算边界。
2. 能让 settlement 从只读演示向更真实运营靠近。
3. 能为后续模拟财务闭环提供更清晰审计边界。

### 当前风险

复杂度明显高于方向 A：

1. 需要重新设计批次状态、撤回条件和对账差异处理。
2. 容易牵涉真实财务语义。
3. 可能需要新增表、接口和前端承接。
4. 需要更谨慎地区分“模拟打款记录”和“真实资金状态”。

### 是否现在切到 P3

当前不建议 Step 52 直接切到 P3。

理由：

1. Step 51A 后端成果刚落地，缺一个低风险前端只读承接即可闭环。
2. 方向 A 不需要改库、不需要后端实现、不改路由，收益/风险比更好。
3. P3 更适合在售后执行历史前端承接完成或明确 no-go 后，作为下一条方案设计主线。

## A/B 对比结论

本轮选择方向 A。

理由：

1. 后端接口已稳定，H2/test 运行态已验证 `PENDING -> FAILED -> SUCCESS` 两条历史。
2. 没有前端承接会让售后执行历史审计停在接口层，形成明显演示和运营短板。
3. 最小承接可以限制在现有售后执行页 drawer 内，不需要新增路由和新页面。
4. 只读历史区能清楚解释“当前摘要”和“历史审计”的兼容策略。
5. 相比 P3 settlement，方向 A 不需要新增后端能力，风险更低。
6. 方向 A 不是补第五个 admin 页，而是承接 Step 51A 已完成的售后执行历史后端闭环。

## 当前最终结论

结论 A：售后执行历史值得做最小 admin 前端承接。

Step 52 可以进入售后执行历史前端最小承接实现轮，但必须保持：

1. 只复用现有 `CampusAfterSaleExecutionList.vue`。
2. 只在详情 drawer 增加只读执行历史区。
3. 只接现有 `GET /api/campus/admin/after-sale-execution-records`。
4. 不新增页面、不新增路由、不新增写接口。
5. 不改 bridge、鉴权、token 附着、订单主状态或 settlement。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 52 进入售后执行历史前端最小承接实现轮。
2. 只改 `frontend/src/api/campus-admin.js` 和 `frontend/src/views/CampusAfterSaleExecutionList.vue`。
3. 不新增路由，不新增页面，不补第五个 admin 页。
4. 验证重点：
   - 详情 drawer 正常打开。
   - 当前售后结果仍正常展示。
   - 执行历史按订单号加载。
   - 空态 / loading / error 提示清楚。
   - `npm run build` 通过。
5. P3 settlement 批次复核、撤回和对账继续后置。
6. bridge 主线继续冻结。
7. 展示 polish 线继续冻结。
8. 媒体线继续收住。

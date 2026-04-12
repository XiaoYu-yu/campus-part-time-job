# Step 47 - admin 异常前端承接 go / no-go

## 本轮目标

1. 基于 Step 46 已落地的异常 resolve 最小后端实现，判断 admin 异常历史 / resolve 是否值得做最小前端承接。
2. 只做 go / no-go 评估，不写前端页面、不写后端新能力、不补第五个 admin 页。
3. 在方向 A“admin 异常历史 / resolve 最小前端承接”和方向 B“P2 售后执行历史表重新升优先级”之间二选一。

## 为什么 Step 47 先做 admin 前端承接 go / no-go

Step 45A 和 Step 46 已经让异常历史闭环具备后端能力：

1. courier 上报异常会写入 `campus_exception_record`。
2. admin 可通过只读接口查询列表和详情。
3. admin 可通过 resolve 接口完成 `REPORTED -> RESOLVED`。
4. latest exception 摘要继续兼容旧页面。

当前缺口不是后端闭环，而是这套能力没有 admin 前端承接。没有前端承接时，异常历史和 resolve 只能依赖 curl / 手工接口调用，演示、联调和交接成本都偏高。因此本轮先判断是否值得把后端最小闭环接到一个最小 admin 只读/处理页面，而不是直接切到新的后端表设计。

## 方向 A：admin 异常历史 / resolve 最小前端承接

### 最小页面形态

如果后续执行，建议只做一个 admin 异常历史页：

1. 延续现有 admin 顶层平铺页风格。
2. 新增一个轻量入口和路由，但不重构 admin 主框架。
3. 顶部最小筛选：
   - `relayOrderId`
   - `processStatus`
   - `courierProfileId`
4. 中部列表：
   - 异常记录 ID
   - 订单号
   - 配送员 profile ID
   - 异常类型
   - 异常说明
   - 上报时间
   - 处理状态
   - 处理结果
5. 详情 drawer：
   - 调用 `GET /api/campus/admin/exceptions/{id}`
   - 展示异常详情、订单关联、配送员关联、latest summary 兼容摘要和处理字段。
6. resolve 动作：
   - 只在 `processStatus = REPORTED` 时展示一个最小 resolve 区。
   - 调用 `POST /api/campus/admin/exceptions/{id}/resolve`。
   - 请求体只包含 `processResult` 和 `adminNote`。
   - 成功后刷新详情和列表。

### 已有接口支撑

当前接口已足够支撑最小前端承接：

1. `GET /api/campus/admin/exceptions`
2. `GET /api/campus/admin/exceptions/{id}`
3. `POST /api/campus/admin/exceptions/{id}/resolve`

不需要新增后端写接口，不需要改订单主状态，不需要改 settlement。

### 演示收益

收益较高：

1. Step 45A / Step 46 的后端成果可以被 admin 页面直接展示，而不是停在接口层。
2. 可以让“异常上报 -> 历史留痕 -> admin 查看 -> admin resolve -> 回读处理状态”形成可讲解闭环。
3. 能补齐 Step 42 媒体里异常后 customer confirm 被拒绝留痕之后的 admin 运营承接说明。
4. 适合答辩说明“不是完整工单系统，但已经有最小处理闭环”。

### 真实使用收益

收益明确：

1. admin 不需要依赖 curl 才能查看异常历史。
2. admin 可以在 UI 中区分 `REPORTED / RESOLVED`。
3. admin 可以最小记录处理结果和备注。
4. 不再只有 courier 维度最近异常摘要，能看到历史列表。

### 改动范围

如果进入实现，改动应限制在前端最小范围：

1. `frontend/src/api/campus-admin.js` 增加异常历史/resolve API 封装。
2. 新增一个 admin 顶层 Vue 页。
3. router / MainLayout 增加一个入口。
4. 日志同步更新。

不应改后端、不应改 bridge、不应改订单状态机。

### 风险

主要风险：

1. 容易被扩成完整异常工单系统。
2. 容易顺手加入 `ACKNOWLEDGED / REOPEN / DELETE / 通知` 等非本阶段能力。
3. 新增 admin 页可能被误解为“为了补第五个 admin 页”，需要在日志中明确它是 Step 45A/46 后端闭环的最小承接，不是补页数。
4. resolve 按钮的可用条件必须只跟 `processStatus = REPORTED` 绑定，不能改后端状态语义。

### 风险控制

如果 Step 48 进入方向 A，应固定以下边界：

1. 只做一个页面。
2. 只接三个既有接口。
3. drawer 内只放一个 resolve 区。
4. 不做 reopen、delete、acknowledge、reject 主状态。
5. 不做 customer / courier 异常处理反馈页。
6. 不改 bridge、request.js、路由主框架或后端接口。

## 方向 B：P2 售后执行历史表重新升优先级

### 当前售后执行历史缺口

当前售后执行仍主要记录在订单当前字段上：

1. 当前执行状态。
2. 当前执行备注。
3. 当前执行参考号。
4. 一次失败到成功的纠正审计字段。

它仍没有完整执行历史表，无法表达多次执行尝试、每次执行人、每次失败原因、补偿/退款后续链路等更细审计。

### 业务收益

售后执行历史的长期收益高：

1. 更贴近财务/售后审计。
2. 可解释“退款/补偿执行失败后如何留痕”。
3. 比异常处理更接近真实经营审计。

### 风险与复杂度

风险和复杂度也更高：

1. 需要新表设计和迁移。
2. 需要重新设计订单当前执行字段与历史表的兼容关系。
3. 可能影响 Step 09 的售后执行前端 drawer。
4. 容易牵出真实退款、财务执行、撤回和多次纠正。
5. 若直接实现，风险明显高于 admin 异常前端承接。

### 是否应作为下一主线

当前不建议 Step 48 立即切到 P2。

原因：

1. 异常历史/resolve 后端刚落地，缺一个低风险前端承接即可形成可演示闭环。
2. 售后执行历史表需要先做方案设计轮，不能直接实现。
3. 当前 admin 异常前端承接不需要改库、不需要改后端，收益/风险比更好。

## A/B 对比结论

本轮选择方向 A。

理由：

1. 后端闭环已经稳定，Step 46 H2 运行态验证通过。
2. 没有前端承接会让 admin 异常历史和 resolve 停在接口层，形成明显演示和使用短板。
3. 最小承接可以限定为“列表 + 详情 drawer + 一个 resolve 动作”。
4. 当前接口已足够，不需要新增后端接口或改状态机。
5. 相比售后执行历史表，方向 A 不需要改库、不需要迁移、不牵涉财务/退款语义，风险更低。
6. 方向 A 的目的不是补第五个 admin 页，而是承接 Step 45A / Step 46 已完成的异常历史处理闭环。

## 当前最终结论

结论 A：admin 异常历史 / resolve 值得做最小前端承接。

Step 48 可以进入 admin 异常前端最小承接方案 / 实现准备轮。

但 Step 48 必须保持以下限制：

1. 只做最小 admin 页面。
2. 只接既有异常历史列表、详情和 resolve 接口。
3. 不改后端接口。
4. 不改 bridge。
5. 不扩完整异常工单系统。
6. 不补 customer / courier 异常处理页面。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 48 进入 admin 异常前端最小承接方案 / 实现准备。
2. 优先固定页面范围、路由、API 封装和 resolve drawer 行为。
3. 如果 Step 48 决定实现，也必须只做一个页面，不补完整工单系统。
4. P2 售后执行历史表继续后置，待异常前端承接完成或明确 no-go 后再重新评估。
5. bridge 主线继续冻结。
6. 展示 polish 线继续冻结。
7. 媒体线继续收住。
8. 第五个 admin 页仍不以“补页数”为目标。

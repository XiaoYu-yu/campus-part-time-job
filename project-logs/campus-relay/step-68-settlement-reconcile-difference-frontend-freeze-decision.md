# Step 68 - settlement 对账差异前端线收口评估

## 本轮目标

1. 基于 Step 64 到 Step 67 已完成的后端实现、前端只读承接和运行态验证，判断 settlement 对账差异前端线是否应该继续扩前端动作。
2. 只做一个 go / no-go 结论：
   - 继续评估前端 resolve 承接
   - 或正式收住当前前端线
3. 不写业务代码，不新增页面，不改变 settlement 页的只读语义。

## 当前起点

Step 64 到 Step 67 已经完成：

1. `campus_settlement_reconcile_difference_record` 已落地。
2. admin 已具备：
   - `GET /api/campus/admin/settlements/reconcile-differences`
   - `GET /api/campus/admin/settlements/reconcile-differences/{id}`
   - `POST /api/campus/admin/settlements/reconcile-differences`
   - `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`
3. `CampusSettlementOpsView.vue` 已在 settlement 详情 drawer 内展示“对账差异记录”只读区。
4. Step 67 已在 H2/test + Vite 下真实验证：
   - `/campus/settlements` 可显示 `CR202604060001`
   - 详情 drawer 可展示 `PBSTEP67UI`
   - 差异区可显示 `AMOUNT_MISMATCH`、`¥6.00 / ¥4.50`、`OPEN / 待处理`
   - settlement payout 摘要仍保持 `FAILED / PBSTEP67UI`

## 方向 A：继续补前端 resolve 承接

### 最小形态

若继续做，理论上的最小承接只能是：

1. 继续复用 `CampusSettlementOpsView.vue`
2. 在现有详情 drawer 的“对账差异记录”区增加一个最小 resolve 入口
3. 通过 drawer 内最小表单提交：
   - `processResult`
   - `adminNote`
4. 成功后刷新差异列表

### 收益

1. admin 可以直接在页面里把 `OPEN -> RESOLVED` 走完。
2. settlement 对账差异后端闭环可以在 UI 层形成写操作闭环。

### 风险

1. 当前 `CampusSettlementOpsView.vue` 从 Step 11 到 Step 67 一直被定义为“只读运营页”，页面顶部也明确写了“不做任何写操作”。
2. resolve 属于处理动作，不再是只读展示，会直接打破当前 settlement 运营页的只读定位。
3. resolve 需要最小表单、成功/失败提示、处理状态刷新、重复处理错误反馈，这会把当前 drawer 从演示型只读区推向处理台。
4. 对账差异 resolve 虽不改 payout 摘要，但它已经属于财务核对处理动作；在“模拟财务、保守试运营”口径下，前端补这个动作的演示收益并不高。
5. 当前后端 resolve 接口已经存在，可通过 API 或后续独立处理页承接；现在把动作塞进只读 settlement 页，会让页面职责变混。

### 结论

方向 A 当前收益不足以覆盖风险，不适合作为 Step 68 的继续推进方向。

## 方向 B：正式收住 settlement 对账差异前端线

### 收益

1. 当前前端已经完成：
   - 差异记录只读展示
   - 空态
   - 错误态
   - 运行态验证
2. 当前最小试运营和答辩演示并不依赖 UI 上直接执行 resolve；只读展示已经足够解释“差异记录不改 payout 摘要”的兼容策略。
3. 继续保持 settlement 页“只读运营”定位，可以避免把本项目重新拉回“财务处理台”扩张。
4. 后端 resolve 能力已经具备，后续若确有运营侧处理需求，可单独评估更合适的承接位置，而不是挤进当前只读页。

### 代价

1. 当前 admin 不能在现有 settlement 页直接点按钮处理差异。
2. 若后续真实试运营要由运营人员频繁处理差异，仍需要单独再评估 UI 承接。

### 结论

方向 B 更符合当前项目边界，是本轮推荐结论。

## 最终结论

Step 68 结论为 no-go：当前不继续为 settlement 对账差异补前端 resolve 承接，正式收住 settlement 对账差异前端线。

收住后的定义：

1. `CampusSettlementOpsView.vue` 保持只读运营页定位不变。
2. 现有“对账差异记录”区继续只展示：
   - 差异类型
   - 金额差
   - 差异说明
   - 处理状态
   - 处理结果
   - 来源与时间字段
3. `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve` 后端接口继续保留，但当前不做前端入口。
4. settlement payout 摘要、`reconcile-summary`、批次操作审计和旧页面兼容语义不变。

## 明确没有做

1. 没有新增 resolve 按钮。
2. 没有新增表单输入区。
3. 没有新增页面或路由。
4. 没有修改 `CampusSettlementOpsView.vue` 行为。
5. 没有修改后端接口、鉴权、bridge、`request.js`、token 附着或 settlement 业务语义。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、`request.js`、`campus-courier.js`、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 69 先做 settlement P3 主线阶段复盘，判断：
   - 批次操作审计线是否收住
   - 对账差异线是否收住
   - settlement 线是否整体进入冻结/维护态
2. 若 Step 69 结论为 settlement P3 已够用，再评估下一条非 bridge 后端方向，而不是继续在 settlement 页上机械加动作。

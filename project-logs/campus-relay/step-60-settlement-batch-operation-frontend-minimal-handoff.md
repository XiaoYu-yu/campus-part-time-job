# Step 60 - settlement 批次操作审计前端最小只读承接

## 本轮目标

1. 基于 Step 59 的方向 A 结论，在现有 settlement 批次详情页承接批次操作审计只读展示。
2. 只改一个 API 封装和一个现有页面。
3. 不新增独立 admin 页面，不新增路由，不调用 review / withdraw 写接口。
4. 不实现对账差异记录，不接真实打款，不触 bridge。

## 实际完成项

### API 封装

在 `frontend/src/api/campus-admin.js` 新增：

1. `getCampusSettlementBatchOperations(batchNo, params)`
2. 请求路径：`GET /campus/admin/settlements/payout-batches/{batchNo}/operations`
3. 继续复用现有 `request` 基础逻辑和 `normalizePageParams`。
4. 未复制 request 基础逻辑，未改 token 附着策略。

### 页面承接

在 `frontend/src/views/CampusSettlementBatchDetail.vue` 增加“批次操作历史”只读区：

1. 页面加载时并行读取批次详情和操作历史。
2. 操作历史默认读取第一页：`page=1&pageSize=10`。
3. 展示字段：
   - `id`
   - `operationType`
   - `operationResult`
   - `operationRemark`
   - `operatedByEmployeeId`
   - `operatedAt`
4. 增加中文 tag 映射：
   - `REVIEW -> 批次复核`
   - `WITHDRAW -> 撤回留痕`
   - `PASSED -> 通过`
   - `REJECTED -> 驳回`
   - `REQUESTED -> 已发起`
   - `RECORDED -> 已记录`
5. 增加空态：`暂无批次操作记录`。
6. 增加错误态提示和“刷新操作历史”按钮。
7. 在原 settlement 明细表上方补“批次结算明细”说明，区分 settlement 明细和操作审计。

## 明确没动

1. 没有新增页面。
2. 没有新增路由。
3. 没有调用 review / withdraw 写接口。
4. 没有新增任何写操作按钮。
5. 没有修改 `payout_status`。
6. 没有清空或替换 `payout_batch_no`。
7. 没有实现对账差异记录。
8. 没有接真实打款。
9. 没有修改后端 Java / SQL。
10. 没有修改 bridge、`request.js`、token 附着、鉴权或旧外卖模块。

## 验证结果

已完成：

1. `npm run build` 通过。

待下一轮运行态验证：

1. H2/test 下重新准备或复用真实 `payout_batch_no`。
2. 写入 review / withdraw 操作审计。
3. 打开 `/campus/settlement-batches/{batchNo}`。
4. 验证批次详情页能展示操作历史。
5. 验证无操作历史时展示空态。
6. 验证操作历史区不影响原 settlement 明细展示。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 61 优先做 settlement 批次操作审计前端运行态验证。
2. 验证顺序建议：
   - 启动 H2/test 后端。
   - 准备固定 `payout_batch_no`。
   - 写入 review / withdraw 操作审计。
   - 用浏览器或 Playwright 打开 `/campus/settlement-batches/{batchNo}`。
   - 验证“批次操作历史”区展示 2 条审计记录。
3. 运行态验证通过后，再评估是否进入 settlement 对账差异记录方案设计。
4. 不默认新增第五个 admin 页，不默认继续做页面 polish。

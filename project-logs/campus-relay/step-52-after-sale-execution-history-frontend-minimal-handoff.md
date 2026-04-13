# Step 52 - 售后执行历史前端最小承接

## 本轮目标

1. 基于 Step 51B 的 go / no-go 结论，完成售后执行历史的最小前端承接。
2. 只复用现有 `CampusAfterSaleExecutionList.vue`，不新增页面、不新增路由。
3. 只读接入 `GET /api/campus/admin/after-sale-execution-records`。
4. 不新增写接口，不做修改、删除、回滚、真实退款或 settlement 联动。

## 为什么只改现有售后执行页

售后执行历史天然依附单个售后订单：

1. 现有 `CampusAfterSaleExecutionList.vue` 已承担 admin 售后执行演示入口。
2. 现有详情 drawer 已展示 after-sale-result 当前摘要。
3. 在同一个 drawer 内补“执行历史”只读区，可以直接解释“当前摘要”和“历史审计”的关系。
4. 独立新页会扩大范围，并容易被误解为补第五个 admin 页。

因此本轮选择在现有 drawer 内承接，不新增页面、不新增路由。

## 实际完成项

### API 封装

在 `frontend/src/api/campus-admin.js` 新增：

1. `getCampusAfterSaleExecutionRecords(params)`
2. 请求路径：`GET /api/campus/admin/after-sale-execution-records`
3. 继续复用现有 `request` 和 `normalizePageParams`
4. 没有复制 request 基础逻辑

### 页面承接

在 `frontend/src/views/CampusAfterSaleExecutionList.vue` 中：

1. 详情 drawer 保持原有 `GET /api/campus/admin/orders/{id}/after-sale-result` 读取不变。
2. 打开详情后按 `relayOrderId` 调用 `getCampusAfterSaleExecutionRecords`。
3. 新增“执行历史”只读区。
4. 展示字段：
   - `id`
   - `previousExecutionStatus`
   - `executionStatus`
   - `corrected`
   - `executionRemark`
   - `executionReferenceNo`
   - `executedByEmployeeId`
   - `executedAt`
5. 增加历史区 loading、空态和失败提示。
6. 历史加载失败只影响历史区提示，不新增任何写操作。

## 兼容策略

当前摘要和历史审计继续分工：

1. 当前摘要仍来自 `GET /api/campus/admin/orders/{id}/after-sale-result`。
2. 售后执行历史来自 `GET /api/campus/admin/after-sale-execution-records`。
3. 订单表 `after_sale_execution_*` 字段继续作为当前兼容摘要。
4. 历史表继续作为审计主数据。
5. 现有售后执行分页、customer 售后结果和 admin after-sale-result 语义不变。

## 明确没做

1. 没有新增独立售后执行历史页面。
2. 没有新增路由。
3. 没有新增后端接口。
4. 没有新增修改、删除、回滚或重新执行按钮。
5. 没有做真实退款、settlement 联动或完整售后工单系统。
6. 没有改 bridge。
7. 没有改 `request.js`。
8. 没有改 token 附着逻辑。
9. 没有改订单主状态机。
10. 没有改旧外卖模块。

## 验证结果

1. `npm run build`
   - 通过。
   - 仍保留既有 Sass `@import` deprecation 与 chunk size warning。
2. `git diff --check`
   - 通过，仅有 Windows 工作区 LF/CRLF 提示。
3. 本轮未改后端代码，未运行后端编译。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 53 进入售后执行历史前端运行态验证轮。
2. 推荐使用 H2/test 的 `CR202604060001` 售后执行历史样本：
   - 打开 `/campus/after-sale-executions`。
   - 查看详情 drawer。
   - 验证当前摘要仍正常展示。
   - 验证执行历史区能展示 `FAILED` 与 `SUCCESS` 两条历史。
   - 验证空态和加载态不影响现有详情。
3. 验证通过后，再评估是否进入 P3 settlement 批次复核、撤回和对账方案设计。
4. 不补第五个 admin 页。
5. bridge 主线继续冻结。
6. 展示 polish 线继续冻结。
7. 媒体线继续收住。

## Step 53 回填

Step 53 已完成售后执行历史前端运行态验证：

1. H2/test 下重新生成 `CR202604060001` 的售后执行历史样本。
2. API 验证确认：
   - `GET /api/campus/admin/after-sale-execution-records` 返回 2 条历史。
   - 最新记录为 `FAILED -> SUCCESS`，`corrected = 1`。
   - 旧记录为 `PENDING -> FAILED`，`corrected = 0`。
   - 当前售后执行摘要仍为 `SUCCESS / corrected = 1`。
3. Playwright 页面验证确认：
   - `/campus/after-sale-executions` 可正常打开。
   - 订单详情 drawer 可正常打开。
   - “执行历史”区可展示 2 条记录。
   - `STEP53-REFUND-001`、失败备注和成功备注均可见。
4. 证据文件：
   - `project-logs/campus-relay/runtime/step-53/after-sale-execution-history-api-validation.json`
   - `project-logs/campus-relay/runtime/step-53/after-sale-execution-history-page-validation.json`
5. 本轮未修改业务代码、前端页面、后端接口、SQL、bridge、鉴权或路由。

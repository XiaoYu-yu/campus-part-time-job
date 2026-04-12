# Step 45B - 异常最小处理动作设计

## 本轮目标

1. 基于 Step 45A 已落地的异常历史表和 admin 只读查询能力，评估是否进入最小 resolve 实现。
2. 只围绕一个动作设计：`REPORTED -> RESOLVED`。
3. 不写代码、不补前端页面、不改 bridge、不扩完整工单系统。

## 为什么 Step 45B 先做 resolve 动作边界评估

Step 45A 已经解决“异常是否有历史留痕”和“admin 是否能只读查询历史”的问题，但还没有处理闭环。当前最小缺口不是新页面，而是 admin 能否把一条已上报异常标记为已处理。

如果直接实现完整工单系统，会带出分派、通知、驳回、重开、客户反馈和订单状态联动，明显超出当前阶段。因此 Step 45B 只评估一个足够小的动作：把异常记录从 `REPORTED` 标记为 `RESOLVED`。

## 状态边界收敛

当前继续坚持最小状态集：

1. `REPORTED`
   - courier 已上报。
   - admin 尚未关闭处理。
2. `RESOLVED`
   - admin 已完成最小处理记录。
   - 不代表订单主状态被推进，也不代表售后已处理。

当前不引入 `ACKNOWLEDGED`，原因：

1. `ACKNOWLEDGED` 只能表达“已看到”，不能形成处理闭环。
2. 它会让最小闭环变成三段状态，增加 UI 和接口复杂度。
3. 当前没有消息通知、派单或 SLA 机制，单独引入 `ACKNOWLEDGED` 收益不足。

当前不把 `REJECTED` 作为独立主状态，原因：

1. `REJECTED` 容易被误解为订单异常不存在或订单状态回退。
2. 当前最小处理更适合用 `processResult = MARKED_INVALID` 表达“记录已处理，但判断为无效/误报”。
3. 这样主状态仍只有 `REPORTED / RESOLVED`，复杂度最低。

重复执行策略：

1. `resolve` 只允许从 `REPORTED` 执行到 `RESOLVED`。
2. 已经 `RESOLVED` 的记录不允许重复 resolve。
3. 不允许从 `RESOLVED` 改回 `REPORTED`。
4. 若状态不满足，后端应返回明确业务错误，例如“异常记录已处理，不能重复处理”。

## resolve 接口最小设计

建议新增：

`POST /api/campus/admin/exceptions/{id}/resolve`

请求体最小字段：

| 字段 | 必需 | 说明 |
| --- | --- | --- |
| `processResult` | 是 | 处理结果枚举 |
| `adminNote` | 是 | 管理员处理备注 |

最小 `processResult` 候选：

1. `HANDLED`
   - 已完成人工处理或线下跟进。
2. `MARKED_INVALID`
   - 判断为误报或无需处理。
3. `FOLLOWED_UP`
   - 已联系相关人员或转入线下跟进。

resolve 写入字段：

1. `process_status = RESOLVED`
2. `process_result = 请求体 processResult`
3. `admin_note = 请求体 adminNote`
4. `processed_by_employee_id = BaseContext 当前 employeeId`
5. `processed_at = now`
6. `updated_at = now`

最小返回：

1. 可以返回 `void`，由前端或调用方重新 GET 详情。
2. 也可以返回更新后的详情 VO。
3. 当前更推荐返回 `void`，保持写接口最小；验证时再通过 `GET /api/campus/admin/exceptions/{id}` 回读。

## 是否影响订单主状态、settlement、latest summary

resolve 不影响订单主状态。

原因：

1. 异常处理状态是运营处理状态，不等于配送状态。
2. 订单可能仍处于 `AWAITING_CONFIRMATION`、`COMPLETED` 或其他状态，异常关闭不应自动推进订单。
3. 若未来需要影响订单状态，应单独设计售后或异常处置规则，不放入最小 resolve。

resolve 不影响 settlement。

原因：

1. settlement 当前依赖订单完成和结算流程。
2. 异常处理只是运营留痕，不能自动改变打款或结算结果。
3. 若未来异常影响结算，应进入 settlement 深化主线单独设计。

resolve 不清空、不覆盖 latest exception 摘要。

原因：

1. `campus_relay_order.exception_type / exception_remark / exception_reported_at` 表达“最后一次上报了什么”。
2. 它不是处理状态字段。
3. 清空会破坏 workbench、customer result、admin recent exceptions 和 exception-summary 的兼容语义。

## 兼容策略

resolve 完成后，旧页面继续看到最后一次上报的 latest exception 摘要：

1. `CourierWorkbench.vue` 仍显示最后一次异常类型、说明和时间。
2. `CampusOrderResult.vue` 仍按订单摘要只读展示。
3. `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent` 仍保持最近异常摘要语义。
4. `GET /api/campus/admin/orders/{id}/exception-summary` 仍保持 latest summary 语义。

admin 如需判断是否已处理，应读取：

1. `GET /api/campus/admin/exceptions`
2. `GET /api/campus/admin/exceptions/{id}`

这种方案风险最低，因为它不改变冻结页面和已归档媒体依赖的旧摘要语义，只在新历史表中补处理闭环。

## 接口边界

1. 只允许 admin 调用。
   - 路径位于 `/api/campus/admin/**`，沿用现有 employee 鉴权。
2. 需要幂等保护。
   - 不是“重复成功”的幂等，而是“状态不满足即拒绝”的保护。
   - SQL 更新条件应限制 `process_status = 'REPORTED'`。
3. 需要审计字段。
   - `processed_by_employee_id`
   - `processed_at`
   - `admin_note`
4. 列表接口现有 `processStatus` 筛选已足够支持 `REPORTED / RESOLVED`。
5. 详情接口现有返回字段已包含处理信息，只需 resolve 后可回读。

## 明确不做的范围

1. 不做异常前端处理页。
2. 不做 customer 异常查询页。
3. 不做 courier 异常处理反馈页。
4. 不做 `ACKNOWLEDGED`。
5. 不做 `REJECTED` 主状态。
6. 不做 reopen。
7. 不做异常删除。
8. 不做消息通知。
9. 不做多角色审批。
10. 不做工单分派。
11. 不改订单主状态。
12. 不改 settlement。
13. 不改 bridge。

## 当前结论

结论 A：边界已经清楚，Step 46 可以进入 resolve 最小实现轮。

执行条件：

1. 只新增 `POST /api/campus/admin/exceptions/{id}/resolve`。
2. 只更新 `campus_exception_record` 的处理字段。
3. SQL 更新必须限制当前状态为 `REPORTED`。
4. 不改订单主状态、不改 settlement、不清空 latest exception 摘要。
5. 不补前端页面。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 46 进入 resolve 最小实现轮。
2. 只新增 admin resolve 后端接口和必要 DTO / service / mapper 更新。
3. 验证：`REPORTED` 可处理为 `RESOLVED`，重复处理失败，详情和列表可回读 `RESOLVED`。
4. 继续不补前端页面，除非 Step 46 后明确只读/处理承接成为最高优先级。

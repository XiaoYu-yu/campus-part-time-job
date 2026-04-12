# Step 46 - 异常 resolve 最小实现

## 本轮目标

1. 基于 Step 45B 已明确的 `REPORTED -> RESOLVED` 最小处理边界，真实落地 admin 异常 resolve 后端接口。
2. 只新增一个动作：`POST /api/campus/admin/exceptions/{id}/resolve`。
3. 不补前端页面、不扩完整工单系统、不改 bridge、不改订单主状态、不改 settlement。

## 实际新增与改动

### DTO

新增 `CampusAdminExceptionResolveDTO`：

1. `processResult`
2. `adminNote`

两个字段均为必填，保持请求体最小。

### Controller

在 `CampusAdminExceptionController` 新增：

`POST /api/campus/admin/exceptions/{id}/resolve`

实现要点：

1. 路径仍位于 `/api/campus/admin/**`，继续沿用 employee 鉴权。
2. 从 `BaseContext` 获取当前 `employeeId`。
3. 调用 `CampusExceptionRecordService.resolveByAdmin`。
4. 返回 `Result<Void>`，处理结果通过详情接口回读。

### Service

在 `CampusExceptionRecordService` 新增：

`void resolveByAdmin(Long id, CampusAdminExceptionResolveDTO dto, Long employeeId)`

实现要点：

1. 校验 `employeeId`、异常记录 ID、请求体、处理结果和处理备注。
2. 只允许最小 `processResult`：
   - `HANDLED`
   - `MARKED_INVALID`
   - `FOLLOWED_UP`
3. 将处理结果统一转为大写存储。
4. 使用事务包裹 resolve 更新。

### Mapper

在 `CampusExceptionRecordMapper` 新增状态受限更新：

1. 只更新 `campus_exception_record`。
2. `WHERE id = #{id} AND process_status = 'REPORTED'`。
3. 写入：
   - `process_status = 'RESOLVED'`
   - `process_result`
   - `processed_by_employee_id`
   - `processed_at`
   - `admin_note`
   - `updated_at`

## 状态限制如何落地

当前只允许：

`REPORTED -> RESOLVED`

落地方式：

1. SQL 更新条件限制 `process_status = 'REPORTED'`。
2. 更新成功即完成处理。
3. 更新行数为 0 时回读记录：
   - 记录不存在：返回“异常记录不存在”。
   - 已是 `RESOLVED`：返回“异常记录已处理，不能重复处理”。
   - 其他状态：返回“当前异常记录状态不可处理”。
4. 不支持 reopen，不支持从 `RESOLVED` 回退到 `REPORTED`。

## processResult 最终落地值

本轮只接受三种最小处理结果：

1. `HANDLED`
   - 已处理。
2. `MARKED_INVALID`
   - 标记为误报或无效。
3. `FOLLOWED_UP`
   - 已线下跟进。

未引入 `ACKNOWLEDGED`，也未把 `REJECTED` 做成主状态。

## latest exception 兼容策略

本轮没有修改订单 latest exception 摘要语义。

继续保留并继续读取：

1. `campus_relay_order.exception_type`
2. `campus_relay_order.exception_remark`
3. `campus_relay_order.exception_reported_at`

resolve 后：

1. 不清空订单 latest exception 摘要。
2. 不覆盖订单 latest exception 摘要。
3. 不修改订单主状态。
4. 不修改 settlement。
5. `CourierWorkbench.vue`、`CampusOrderResult.vue`、admin 最近异常摘要和订单异常摘要接口仍可继续按旧摘要语义读取。

处理状态只通过新异常历史接口读取：

1. `GET /api/campus/admin/exceptions`
2. `GET /api/campus/admin/exceptions/{id}`

这样可以让新历史表成为审计主数据，同时避免破坏已有前端和已归档演示媒体依赖的 latest summary 行为。

## 明确没做

1. 没有做前端 admin 异常处理页。
2. 没有新增异常前端列表页。
3. 没有新增 customer 异常入口。
4. 没有新增 courier 异常反馈入口。
5. 没有做 `ACKNOWLEDGED`。
6. 没有做 `REJECTED` 主状态。
7. 没有做 reopen。
8. 没有做 delete。
9. 没有做消息通知。
10. 没有改订单主状态机。
11. 没有改 settlement。
12. 没有改 bridge。
13. 没有改 `request.js`。
14. 没有改路由。
15. 没有补第五个 admin 页。

## 验证结果

### 构建验证

1. `.\mvnw.cmd -DskipTests compile`
   - 结果：通过。
2. `npm run build`
   - 结果：通过。
   - 说明：仅保留既有 Sass `@import` deprecation 与 chunk size warning。

### H2/test profile 运行态验证

本轮启动 test profile + H2，并使用真实接口完成以下验证：

1. admin 登录成功。
2. courier profile 1 审核为 `APPROVED`。
3. courier token 申请成功。
4. `GET /api/campus/courier/orders/available` 返回可接单订单 `CR202604070002`。
5. courier 接单 `CR202604070002` 成功。
6. courier 调用现有 `POST /api/campus/courier/orders/CR202604070002/exception-report` 成功，生成 `REPORTED` 历史记录。
7. `GET /api/campus/admin/exceptions?relayOrderId=CR202604070002&processStatus=REPORTED&page=1&pageSize=10` 可查到记录。
8. `POST /api/campus/admin/exceptions/{id}/resolve` 成功。
9. `GET /api/campus/admin/exceptions/{id}` 回读结果：
   - `processStatus = RESOLVED`
   - `processResult = HANDLED`
   - `adminNote = Step46 resolve validation`
   - `processedByEmployeeId = 1`
   - `processedAt` 非空
10. `GET /api/campus/admin/exceptions?relayOrderId=CR202604070002&processStatus=RESOLVED&page=1&pageSize=10` 可查到已处理记录。
11. 重复调用同一条 resolve 返回业务错误：
   - `code = 500`
   - `msg = 异常记录已处理，不能重复处理`
12. `GET /api/campus/courier/orders/CR202604070002` 仍可看到 latest exception 摘要：
   - `exceptionType = DELIVERY_BLOCKED`
   - `exceptionRemark = Step46 validation exception before resolve`
13. `GET /api/campus/customer/orders/CR202604070002` 不报错。
14. `GET /api/campus/admin/couriers/1/exceptions/recent?limit=5` 仍可返回最近异常摘要。

### 空白检查

1. `git diff --check`
   - 结果：通过。
   - 说明：仅有 Windows 工作区 LF/CRLF 提示，无空白错误。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 47 才允许评估是否进入 admin 异常历史/处理前端承接。
2. 如果前端承接收益不足，可重新比较 P2 售后执行历史表是否更值得作为下一条非 bridge 后端主线。
3. 不要在 Step 47 直接扩成完整异常工单系统。
4. bridge 主线继续冻结。
5. 展示 polish 线继续冻结。
6. 媒体线继续收住。
7. 第五个 admin 页继续后置。

## Step 47 回填

Step 47 已完成 admin 异常前端承接 go / no-go 评估，最终选择方向 A：

1. admin 异常历史 / resolve 后端闭环已经稳定，缺少前端承接会让查询和处理停留在 curl / 手工接口层。
2. 最小前端承接可限制为一个 admin 页面、列表、详情 drawer 和一个 `processStatus = REPORTED` 下的 resolve 区。
3. 现有接口已经足够支撑承接：
   - `GET /api/campus/admin/exceptions`
   - `GET /api/campus/admin/exceptions/{id}`
   - `POST /api/campus/admin/exceptions/{id}/resolve`
4. P2 售后执行历史表继续后置，因为它需要新增表、迁移和兼容策略设计，复杂度高于异常前端最小承接。
5. Step 48 建议进入 admin 异常前端最小承接方案 / 实现准备。
6. bridge 仍处于 `Phase A no-op` 冻结态，本轮没有新增页面、没有改后端接口、没有改 bridge。

# Step 45A - 异常历史最小实现

## 本轮目标

1. 基于 Step 44 的最小方案，真实落地异常历史表。
2. 复用现有 courier `exception-report` 入口，在同一事务中写入异常历史并继续更新订单 latest exception 摘要。
3. 新增 admin 异常历史只读列表和详情接口。
4. 不做 resolve 处理接口、不做前端页面、不触 bridge。

## 实际落地的表与字段

新增表：`campus_exception_record`

字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 异常历史记录主键 |
| `relay_order_id` | 对应 `campus_relay_order.id` |
| `courier_profile_id` | 上报异常的配送员 profile |
| `exception_type` | 异常类型，复用现有 DTO 语义 |
| `exception_remark` | 异常说明，复用现有 DTO 语义 |
| `reported_at` | courier 上报时间 |
| `process_status` | 处理状态，当前默认 `REPORTED`，最小状态集保留 `REPORTED / RESOLVED` |
| `process_result` | 处理结果，本轮只读预留，默认空 |
| `processed_by_employee_id` | 处理管理员，本轮只读预留，默认空 |
| `processed_at` | 处理时间，本轮只读预留，默认空 |
| `admin_note` | 管理员备注，本轮只读预留，默认空 |
| `source` | 来源，当前写入 `COURIER` |
| `created_at` | 创建时间 |
| `updated_at` | 更新时间 |

索引：

1. `relay_order_id`
2. `courier_profile_id`
3. `process_status`
4. `reported_at`

## 现有异常上报接口接入方式

复用现有接口：

`POST /api/campus/courier/orders/{id}/exception-report`

实现语义：

1. 保持原接口路径、请求体和前端调用方式不变。
2. 继续校验 courier profile、订单归属和订单状态。
3. 同一事务内先更新 `campus_relay_order` latest exception 摘要字段。
4. 更新成功后插入一条 `campus_exception_record`。
5. 若任一步失败，事务回滚，避免订单摘要和历史表只写一半。
6. 每次上报都新增一条历史，不覆盖旧历史。

## 新增 admin 只读查询接口

1. `GET /api/campus/admin/exceptions`
   - 支持分页。
   - 支持按 `relayOrderId` 筛选。
   - 支持按 `courierProfileId` 筛选。
   - 支持按 `processStatus` 筛选。
   - 默认按 `reported_at desc, id desc`。
2. `GET /api/campus/admin/exceptions/{id}`
   - 返回单条异常历史。
   - 包含订单状态、customer user、courier profile、courier name。
   - 包含当前订单 latest exception 摘要字段，用于说明历史记录与兼容摘要的映射关系。

## 兼容策略落地

继续保留并继续写入订单摘要字段：

1. `campus_relay_order.exception_type`
2. `campus_relay_order.exception_remark`
3. `campus_relay_order.exception_reported_at`

旧读取路径继续读订单摘要：

1. `CourierWorkbench.vue` 订单详情。
2. `CampusOrderResult.vue` customer 结果页。
3. `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`。
4. `GET /api/campus/admin/orders/{id}/exception-summary`。

新查询路径开始读历史表：

1. `GET /api/campus/admin/exceptions`
2. `GET /api/campus/admin/exceptions/{id}`

这样做的风险最低：旧页面、旧接口和已归档媒体仍保持 latest summary 语义；新表只承接审计历史，不强行替换现有展示链路。

## 数据库路径改动

1. `backend/db/init.sql`
   - 新增 `campus_exception_record` 建表语句和索引。
2. `backend/db/migrations/V9__campus_exception_record.sql`
   - 新增 MySQL migration。
3. `backend/src/main/resources/db/schema-h2.sql`
   - 新增 H2 表和索引。
4. `backend/src/main/resources/db/data-h2.sql`
   - 本轮未修改。
   - 原因：H2 下通过真实 `exception-report` 调用生成历史记录即可验证“每次新增，不覆盖旧记录”；不预置复杂异常历史样本可以保持种子数据简单。

## 明确没做

1. 未新增 `POST /api/campus/admin/exceptions/{id}/resolve`。
2. 未实现 `ACKNOWLEDGED / REJECTED` 等复杂状态。
3. 未新增 admin 异常历史前端页面。
4. 未新增 customer 异常页面。
5. 未改 bridge。
6. 未改 `request.js`。
7. 未改路由。
8. 未改后端鉴权。
9. 未补第五个 admin 页。

## 验证结果

1. 后端构建：`.\mvnw.cmd -DskipTests compile` 通过。
2. 前端构建：`npm run build` 通过。
3. H2/test profile 启动成功，`campus_exception_record` 初始化通过。
4. 运行态验证：
   - admin 登录 `13800138000 / 123456`。
   - admin 审核 courier profile `1` 为 `APPROVED`。
   - courier `13900139000 / 123456` 申请 courier token 成功。
   - `GET /api/campus/courier/orders/available?page=1&pageSize=10` 可返回可接单记录。
   - `POST /api/campus/courier/orders/CR202604070002/accept` 成功。
   - 连续两次调用 `POST /api/campus/courier/orders/CR202604070002/exception-report` 成功。
   - `GET /api/campus/courier/orders/CR202604070002` 显示 latest exception 为第二次异常。
   - `GET /api/campus/admin/exceptions?relayOrderId=CR202604070002&page=1&pageSize=10` 返回 `total = 2`。
   - `GET /api/campus/admin/exceptions/{id}` 可回读最新单条历史，`processStatus = REPORTED`。
   - `GET /api/campus/admin/couriers/1/exceptions/recent?limit=5` 仍按订单摘要返回最近异常。
   - `GET /api/campus/customer/orders/CR202604070002` 不报错。
5. `git diff --check` 通过，仅有 CRLF 转换提示，无空白错误。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 45B 先评估是否进入 admin 异常最小处理动作，即 `REPORTED -> RESOLVED`。
2. 如果进入实现，优先只做后端最小处理接口，不默认补前端页面。
3. 如果处理状态边界仍需收敛，Step 45B 先补方案，不写代码。
4. bridge、展示 polish、媒体线继续冻结。

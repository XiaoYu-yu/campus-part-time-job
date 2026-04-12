# Step 44 - 异常历史与处理闭环最小方案设计

## 本轮目标

1. 基于 Step 43 的 P1 方向，设计“异常历史与处理闭环”的最小方案。
2. 只做方案设计，不写 Java 代码、不写 SQL migration、不改前端页面、不补新接口实现。
3. 明确数据模型、状态边界、接口边界和与现有 latest exception 字段的兼容策略。

## 为什么 Step 44 选择异常历史与处理闭环

当前 courier workbench 已经支持 `POST /api/campus/courier/orders/{id}/exception-report`，但后端真实实现仍只更新 `campus_relay_order` 上的最新异常摘要字段：

1. `exception_type`
2. `exception_remark`
3. `exception_reported_at`

admin 侧当前能力也主要围绕“最近异常”：

1. `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
2. `GET /api/campus/admin/orders/{id}/exception-summary`

这已经能支撑演示，但无法回答“同一订单是否多次异常、异常是否处理、谁处理、处理结果是什么”。因此 Step 44 进入异常历史与处理闭环设计，而不是继续补媒体、polish 页面或重开 bridge。

## 当前痛点

1. courier 可以多次上报异常，但当前只保留最新一次摘要。
2. admin 只能看最近异常摘要，不能看历史记录。
3. 当前没有异常处理状态，无法区分“已上报但未处理”和“已处理”。
4. 当前没有处理人、处理时间、处理结果和 admin 备注。
5. Step 42 媒体采集中已经验证：异常上报会真实影响后续 confirm 行为，因此异常需要有更清晰的运营留痕。

## 最小目标

最小闭环只覆盖：

1. courier 上报异常时新增一条历史记录。
2. 同步更新订单上的 latest exception 摘要字段。
3. admin 可以分页查看异常历史。
4. admin 可以查看单条异常详情。
5. admin 可以做一次最小处理闭环，把异常从待处理关闭到已处理。
6. 现有 workbench、customer result、admin 最近异常接口继续可用。

## 明确不覆盖

本方案不做：

1. 完整异常工单系统。
2. 地图调度和轨迹回放。
3. 消息通知。
4. customer 异常自助处理页面或接口。
5. 多角色审批流。
6. 异常自动派单。
7. 自动重试或赔付规则。
8. 新前端页面。
9. bridge 行为变更。
10. settlement 或售后执行历史表。

## 数据模型草案

建议新增表：`campus_exception_record`

命名理由：

1. 继续使用 `campus_` 前缀。
2. 记录对象是校园代送异常，不直接替代 `campus_relay_order`。
3. 与现有 `campus_location_report`、`campus_settlement_record` 的命名风格一致。

### 必需字段

| 字段 | 建议类型 | 必需性 | 说明 |
| --- | --- | --- | --- |
| `id` | BIGINT | 必需 | 主键 |
| `relay_order_id` | VARCHAR | 必需 | 对应 `campus_relay_order.id` |
| `courier_profile_id` | BIGINT | 必需 | 上报异常的配送员 profile |
| `exception_type` | VARCHAR(50) | 必需 | 复用现有 DTO 字段语义 |
| `exception_remark` | VARCHAR(255/500) | 必需 | 复用现有 DTO 字段语义，长度可比订单摘要略宽 |
| `reported_at` | DATETIME/TIMESTAMP | 必需 | courier 上报时间 |
| `process_status` | VARCHAR(30) | 必需 | 最小处理状态，默认 `REPORTED` |
| `source` | VARCHAR(30) | 必需 | 默认 `COURIER`，为未来 admin/system 来源预留 |
| `created_at` | DATETIME/TIMESTAMP | 必需 | 创建时间 |
| `updated_at` | DATETIME/TIMESTAMP | 必需 | 更新时间 |

### 最小处理字段

| 字段 | 建议类型 | 必需性 | 说明 |
| --- | --- | --- | --- |
| `process_result` | VARCHAR(50) | 可空 | admin 处理结果，例如 `CONTACTED_CUSTOMER`、`NO_ACTION_REQUIRED`、`MARKED_INVALID` |
| `processed_by_employee_id` | BIGINT | 可空 | 处理管理员 |
| `processed_at` | DATETIME/TIMESTAMP | 可空 | 处理时间 |
| `admin_note` | VARCHAR(255/500) | 可空 | 处理备注 |

### 后续可扩展字段

| 字段 | 暂不放入最小方案的原因 |
| --- | --- |
| `proof_image_url` | 当前 courier exception-report DTO 没有该字段，加入会改前端表单和 DTO |
| `location_report_id` | 位置与异常联动可后续做，不影响当前异常历史闭环 |
| `severity` | 当前没有异常分级规则 |
| `assigned_employee_id` | 会把异常扩成工单分派系统 |
| `resolved_action` | 可先用 `process_result` 承接 |
| `before_order_status` / `after_order_status` | 当前异常处理不改变订单主状态，暂不需要 |

### 写入策略

1. 每次 courier 调用 `POST /api/campus/courier/orders/{id}/exception-report`，新增一条 `campus_exception_record`。
2. 同一个事务内继续更新 `campus_relay_order.exception_type / exception_remark / exception_reported_at`。
3. 历史表是审计主数据。
4. 订单 latest exception 字段是兼容摘要和快速展示字段。

## 状态流草案

### 最小状态集

推荐最小状态只保留：

1. `REPORTED`
2. `RESOLVED`

理由：

1. 当前目标是最小处理闭环，不是完整工单系统。
2. `ACKNOWLEDGED` 会引入“已读但未处理”的中间状态，当前收益不高。
3. `REJECTED` 更适合作为 `process_result = MARKED_INVALID`，不必拆成独立主状态。

### 状态含义

| 状态 | 含义 | 触发方 |
| --- | --- | --- |
| `REPORTED` | courier 已上报，admin 未处理 | courier exception-report |
| `RESOLVED` | admin 已完成最小处理并关闭该异常记录 | admin process |

### process_result 建议值

`process_result` 不是主状态，只表达处理结果：

1. `CONTACTED_CUSTOMER`
2. `CONTACTED_COURIER`
3. `NO_ACTION_REQUIRED`
4. `MARKED_INVALID`
5. `OTHER`

### 与订单主状态的关系

1. 异常处理状态与订单主状态解耦。
2. 新增异常历史不会改变 `WAITING_ACCEPT / ACCEPTED / PICKED_UP / DELIVERING / AWAITING_CONFIRMATION / COMPLETED` 主状态定义。
3. courier 上报异常的允许状态继续沿用现有实现：
   - `ACCEPTED`
   - `PICKED_UP`
   - `DELIVERING`
   - `AWAITING_CONFIRMATION`
4. admin 处理异常默认不推进订单主状态。
5. 如果未来要让异常处理影响订单主状态，必须单独进入状态机设计轮，不在最小方案内处理。

## 接口边界草案

### courier 侧

继续复用现有接口：

`POST /api/campus/courier/orders/{id}/exception-report`

最小变化仅在后端持久化策略：

1. 仍校验 courier token。
2. 仍校验订单属于当前 courier。
3. 仍校验订单状态允许上报异常。
4. 仍使用当前 DTO：
   - `exceptionType`
   - `exceptionRemark`
5. 新增落历史表。
6. 继续同步更新订单 latest exception 摘要字段。
7. 响应体不变。

### admin 侧只读接口

建议新增：

1. `GET /api/campus/admin/exceptions`
   - 分页查询异常历史。
   - 最小筛选：`processStatus`、`courierProfileId`、`relayOrderId`、`page`、`pageSize`。
2. `GET /api/campus/admin/exceptions/{id}`
   - 查询单条异常详情。

### admin 侧最小处理接口

建议新增：

`POST /api/campus/admin/exceptions/{id}/resolve`

请求体最小字段：

1. `processResult`
2. `adminNote`

行为边界：

1. 只允许 `REPORTED -> RESOLVED`。
2. 不修改订单主状态。
3. 不修改 settlement。
4. 不触发通知。
5. 不删除异常记录。

### 保留现有接口

以下接口继续保留：

1. `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
2. `GET /api/campus/admin/orders/{id}/exception-summary`
3. `GET /api/campus/customer/orders/{id}`
4. `GET /api/campus/courier/orders/{id}`

### 明确不新增的接口

1. 不新增 customer 异常处理接口。
2. 不新增异常删除接口。
3. 不新增异常重新打开接口。
4. 不新增异常分派接口。
5. 不新增异常地图/位置联动接口。

## 兼容策略

### latest exception 字段继续保留

继续保留 `campus_relay_order` 上的：

1. `exception_type`
2. `exception_remark`
3. `exception_reported_at`

保留原因：

1. `CourierWorkbench.vue` 当前订单详情直接展示这些字段。
2. `CampusOrderResult.vue` customer 结果页直接展示这些字段。
3. `CampusCourierOpsView.vue` 当前最近异常区依赖 admin recent exceptions 接口。
4. 现有 admin `exception-summary` 是 latest summary 语义。
5. 直接删除或改语义会破坏已冻结的展示 polish 线和媒体交付线。

### 新旧字段关系

1. `campus_exception_record` 是审计主数据。
2. `campus_relay_order.exception_*` 是兼容摘要字段。
3. courier 每次上报异常时：
   - 插入一条历史记录。
   - 同步更新订单 latest exception 摘要。
4. 老页面继续读订单 latest summary。
5. 新 admin 异常历史列表优先读 `campus_exception_record`。

### 历史数据处理

最小实现不强制做一次性 backfill。

原因：

1. 当前项目没有生产发布数据。
2. test/H2 可通过新链路生成异常历史样本。
3. 强制 backfill 会增加 migration 复杂度。

如果后续发现 H2 演示需要从已有 latest exception 生成历史记录，可单独设计一次性兼容迁移，不放入最小实现默认范围。

## 风险点

1. 如果把异常历史做成工单系统，会快速扩大到分派、通知、审批、地图调度，超出当前主线。
2. 如果让 admin 处理异常直接改订单主状态，会引入状态机风险。
3. 如果删除 latest exception 字段，会破坏当前已冻结的前端展示和媒体成果。
4. 如果新增 customer 异常处理入口，会重新打开 customer 页面主链路，不符合当前阶段收口策略。
5. 如果 Step 45 直接实现而不先核对 SQL / H2 / 测试范围，容易漏维护 `init.sql`、migration、H2 schema/data。

## Step 45 推荐进入方式

如果进入实现轮，建议仍拆成最小后端优先：

1. 新增 `campus_exception_record` 表设计与 H2/MySQL 初始化同步。
2. 新增 entity / VO / Mapper。
3. 修改现有 courier exception-report service：新增历史记录 + 保持 latest summary 同步。
4. 新增 admin 异常历史分页和详情只读接口。
5. 最后再评估是否加入 `resolve` 最小处理接口。

实现顺序建议：

1. Step 45A：只做历史表 + 写入 + 只读查询。
2. Step 45B：再做 admin resolve 最小处理接口。

这样可以避免一次性把历史、处理、前端页面全部打开。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本方案不触 bridge、不改鉴权、不改 token 附着逻辑。
4. 当前最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-43-media-gap-gate-or-non-bridge-backend-assessment.md`
5. `project-logs/campus-relay/step-44-exception-history-minimal-solution-design.md`

## 下一轮建议

1. Step 45A 已按本方案落地历史表 + 写入 + admin 只读查询最小实现。
2. Step 45A 已同步维护：
   - `backend/db/init.sql`
   - `backend/db/migrations`
   - `backend/src/main/resources/db/schema-h2.sql`
3. Step 45A 未修改 `backend/src/main/resources/db/data-h2.sql`，原因是 H2 演示样本可以通过现有 courier 异常上报接口真实生成，预置历史样本反而会弱化“新增历史不覆盖”的验证。
4. Step 45B 如继续推进，应先评估 `REPORTED -> RESOLVED` 的最小 admin 处理动作接口。
5. Step 45B 不要默认补前端页面。
6. Step 45B 不要默认补完整处理工单。
7. bridge、展示 polish、媒体线继续冻结。

## Step 45A 落地回填

Step 45A 已真实实现本方案中的最小后端部分：

1. 新增 `campus_exception_record`，作为异常审计主数据。
2. 复用 `POST /api/campus/courier/orders/{id}/exception-report`，每次上报新增历史记录。
3. 同事务继续更新 `campus_relay_order.exception_type / exception_remark / exception_reported_at` 兼容摘要。
4. 新增 `GET /api/campus/admin/exceptions` 与 `GET /api/campus/admin/exceptions/{id}`，用于 admin 只读查看历史和详情。
5. 本轮没有实现 `resolve`、没有前端页面、没有 bridge 变更。

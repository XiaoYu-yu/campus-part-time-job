# Step 48 - admin 异常历史 / resolve 最小前端承接

## 本轮目标

1. 基于 Step 47 选择的方向 A，补一个 admin 异常历史 / resolve 最小前端承接页。
2. 只接入 Step 45A / Step 46 已有的异常历史列表、详情和 resolve 接口。
3. 不新增后端接口，不改 bridge，不改鉴权，不改订单主状态，不改 settlement，不改 latest exception 摘要。

## 为什么本轮进入前端承接

Step 45A / Step 46 已经让异常历史后端闭环具备：

1. courier 异常上报写入 `campus_exception_record`。
2. admin 可查询异常历史列表和详情。
3. admin 可将 `REPORTED` 记录 resolve 为 `RESOLVED`。

如果没有前端承接，这套能力仍停留在 curl / 手工接口层，不利于试运营、演示和交接。因此本轮只补一个最小 admin 页面，让异常上报、历史留痕、详情回读和最小处理动作形成可见闭环。

## 实际新增页面

1. 页面文件：`frontend/src/views/CampusExceptionOpsView.vue`
2. 访问路径：`/campus/exceptions`
3. MainLayout 入口：`校园异常处理`
4. breadcrumb：`校园异常处理`

## 页面能力

### 列表区

调用：

1. `GET /api/campus/admin/exceptions`

支持最小筛选：

1. `relayOrderId`
2. `courierProfileId`
3. `processStatus`
4. `page`
5. `pageSize`

展示字段：

1. 异常 ID
2. 订单号
3. 订单状态
4. 配送员 ID
5. 配送员姓名
6. 异常类型
7. 异常说明
8. 上报时间
9. 处理状态
10. 处理结果
11. 处理时间

### 详情 drawer

调用：

1. `GET /api/campus/admin/exceptions/{id}`

展示内容：

1. 异常历史基本信息。
2. 订单关联信息。
3. 配送员关联信息。
4. latest exception 兼容摘要。
5. 当前处理状态、处理结果、处理人和处理备注。

### 最小 resolve 区

调用：

1. `POST /api/campus/admin/exceptions/{id}/resolve`

展示条件：

1. 仅当 `processStatus = REPORTED` 时展示。

请求体：

1. `processResult`
2. `adminNote`

可选 `processResult`：

1. `HANDLED`
2. `MARKED_INVALID`
3. `FOLLOWED_UP`

成功后行为：

1. 展示成功提示。
2. 刷新当前详情。
3. 刷新列表。
4. 清空 resolve 表单。

已 `RESOLVED` 的记录：

1. 不展示 resolve 表单。
2. 展示只读提示，明确不支持重复处理、reopen 或 delete。

## API 封装

`frontend/src/api/campus-admin.js` 新增：

1. `getCampusExceptionRecords(params)`
2. `getCampusExceptionDetail(id)`
3. `resolveCampusException(id, data)`

未复制 request 基础逻辑，继续沿用现有 admin token 附着与响应拦截器。

## 兼容策略

本轮只做前端承接，不改变 Step 46 的后端兼容策略：

1. 订单表上的 `exception_type / exception_remark / exception_reported_at` 继续作为 latest exception 摘要。
2. `CourierWorkbench.vue` 仍按原接口读取订单详情和 latest 摘要。
3. `CampusOrderResult.vue` 不受影响。
4. `/campus/courier-ops` 里的最近异常摘要语义不变。
5. admin 想看处理状态时，使用新的异常历史列表和详情接口。

## 明确没做

1. 没有新增后端接口。
2. 没有改 `POST /api/campus/admin/exceptions/{id}/resolve` 语义。
3. 没有做完整异常工单系统。
4. 没有做 reopen。
5. 没有做 delete。
6. 没有做 `ACKNOWLEDGED`。
7. 没有做消息通知。
8. 没有做 customer 异常页面。
9. 没有做 courier 异常反馈页面。
10. 没有改订单主状态。
11. 没有改 settlement。
12. 没有改 bridge。
13. 没有改 `request.js`。
14. 没有改 token 附着逻辑。
15. 没有补第五个 admin 页作为页数目标。

## 验证结果

1. `.\mvnw.cmd -DskipTests compile`
   - 结果：通过。
   - 说明：本轮未改后端，验证后端编译未受影响。
2. `npm run build`
   - 结果：通过。
   - 说明：新增 `CampusExceptionOpsView` 已进入构建产物；仍保留既有 Sass `@import` deprecation 与 chunk size warning。
3. `git diff --check`
   - 结果：通过。
   - 说明：仅有 Windows 工作区 LF/CRLF 提示。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未修改 bridge、鉴权、token 附着逻辑或前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 49 优先对 `/campus/exceptions` 做 H2/test 运行态联调。
2. 联调至少覆盖：
   - 列表查询。
   - 详情 drawer。
   - `REPORTED -> RESOLVED` 成功。
   - 重复 resolve 失败。
   - latest exception 摘要仍不被清空。
3. 如果 Step 49 联调通过，再评估是否进入 P2 售后执行历史表方案设计。
4. 不要在 Step 49 扩完整异常工单系统。
5. bridge 主线继续冻结。
6. 展示 polish 线继续冻结。
7. 媒体线继续收住。
8. 第五个 admin 页继续后置。

## Step 49 回填

1. 已完成 `/campus/exceptions` H2/test 运行态验证。
2. 使用订单 `CR202604070002` 真实完成：
   - 接单。
   - 连续两次 courier 异常上报。
   - admin 异常历史列表查询。
   - admin 异常详情查询。
   - `REPORTED -> RESOLVED`。
   - 重复 resolve 失败。
   - latest exception 摘要兼容回读。
   - customer 订单详情兼容回读。
3. Playwright 已验证 admin 异常处理页可加载、列表可见、详情 drawer 可打开、REPORTED 记录可展示 resolve 区。
4. 证据文件：
   - `project-logs/campus-relay/runtime/step-49/admin-exception-api-validation.json`
   - `project-logs/campus-relay/runtime/step-49/admin-exception-page-validation.json`
5. Step 49 验证通过后，下一主线可转入 P2 售后执行历史表方案设计；异常前端承接当前先保持稳定，不继续扩完整异常工单系统。

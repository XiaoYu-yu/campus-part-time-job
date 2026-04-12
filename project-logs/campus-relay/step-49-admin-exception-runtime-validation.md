# Step 49 - admin 异常处理页运行态验证

## 本轮目标

1. 对 Step 48 新增的 `/campus/exceptions` 做 H2/test 运行态联调。
2. 验证异常历史列表、详情 drawer、`REPORTED -> RESOLVED`、重复 resolve 失败和 latest exception 兼容摘要。
3. 不新增业务代码，不改 bridge，不改鉴权，不改接口，不改路由，不新增页面。

## 运行环境

1. 后端：`java -jar backend/target/takeaway-0.0.1-SNAPSHOT.jar --spring.profiles.active=test --server.port=8080`
2. 前端：`npm run dev -- --host 127.0.0.1 --port 5173`
3. admin 账号：`13800138000 / 123456`
4. courier 账号：`13900139000 / 123456`
5. customer 账号：`13900139001 / 123456`
6. 验证订单：`CR202604070002`

## 实际验证链路

1. admin 登录并将 courier profile `1` 审核为 `APPROVED`。
2. courier 申请 token。
3. courier 查询可接单列表，H2/test 返回 `CR202604070001` 与 `CR202604070002`。
4. 选择 `CR202604070002` 执行接单。
5. courier 对 `CR202604070002` 连续上报两次异常：
   - `DELIVERY_DELAY`
   - `CONTACT_FAILED`
6. admin 调用异常历史列表：
   - `GET /api/campus/admin/exceptions?relayOrderId=CR202604070002&page=1&pageSize=10`
   - 结果：返回 2 条异常历史。
7. admin 调用异常历史详情：
   - `GET /api/campus/admin/exceptions/{id}`
8. admin 对异常记录 `2` 执行 resolve：
   - `POST /api/campus/admin/exceptions/2/resolve`
   - 请求体：`processResult = HANDLED`，`adminNote = Step49 runtime validation resolved`
9. admin 回读详情：
   - `processStatus = RESOLVED`
   - `processResult = HANDLED`
   - `processedByEmployeeId = 1`
   - `processedAt` 非空
10. 对同一记录再次 resolve：
   - 结果：失败，返回“异常记录已处理，不能重复处理”。
11. courier 回读订单详情：
   - latest exception 摘要仍为最后一次上报的 `CONTACT_FAILED`。
12. admin 回读最近异常和订单异常摘要：
   - 最近异常摘要仍保持 latest summary 语义。
13. customer 回读订单详情：
   - customer 详情接口不报错，仍可看到 latest exception 摘要。

## 前端页面验证

访问路径：

1. `/campus/exceptions`

Playwright 验证结果：

1. 页面正常加载。
2. 菜单与标题“校园异常处理”可见。
3. 异常历史列表可见。
4. `CR202604070002` 的 2 条异常历史可见。
5. `RESOLVED` 记录可见。
6. `REPORTED` 记录可见。
7. 点击 `REPORTED` 记录可打开详情 drawer。
8. drawer 内 latest exception 兼容摘要可见。
9. drawer 内最小 resolve 区可见。

前端页面未执行 resolve 写操作；resolve 成功和重复失败通过 API 验证完成。

## 证据文件

1. API 验证证据：`project-logs/campus-relay/runtime/step-49/admin-exception-api-validation.json`
2. 页面验证证据：`project-logs/campus-relay/runtime/step-49/admin-exception-page-validation.json`

证据文件不包含 token。

## 验证结果

1. `.\mvnw.cmd -DskipTests compile`
   - 结果：通过。
2. `npm run build`
   - 结果：通过。
   - 说明：仍保留既有 Sass `@import` deprecation 与 chunk size warning。
3. `git diff --check`
   - 结果：通过。
4. H2/test 运行态验证：
   - 异常历史写入：通过。
   - 列表查询：通过。
   - 详情查询：通过。
   - resolve 成功：通过。
   - 重复 resolve 失败：通过。
   - latest exception 摘要兼容：通过。
   - customer 详情兼容：通过。
   - 前端 `/campus/exceptions` 页面加载与详情 drawer：通过。

## 明确没做

1. 没有修改业务代码。
2. 没有新增后端接口。
3. 没有修改 `POST /api/campus/admin/exceptions/{id}/resolve` 语义。
4. 没有新增前端页面。
5. 没有修改 bridge。
6. 没有修改 `request.js`。
7. 没有修改 token 附着逻辑。
8. 没有修改订单主状态。
9. 没有修改 settlement。
10. 没有扩完整异常工单系统。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮未触碰 bridge、鉴权、token 附着逻辑或旧前端主链路。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 50 可进入 P2 售后执行历史表方案设计轮。
2. 先设计售后执行历史表边界、写入时机、兼容策略和最小只读/处理接口，不要直接写代码。
3. 异常历史 / resolve / admin 异常页面当前先保持稳定，不继续扩完整异常工单系统。
4. bridge 主线继续冻结。
5. 展示 polish 线继续冻结。
6. 媒体线继续收住。
7. 第五个 admin 页继续后置。

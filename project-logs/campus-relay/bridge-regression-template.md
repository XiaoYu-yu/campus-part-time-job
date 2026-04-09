# bridge 联调 / 回归记录模板

## 使用说明

1. 本文档是待填写模板，不代表当前已经完成联调。
2. 每一步都应留下实际结果、截图路径或日志位置。
3. 若某一步失败，应保留失败现象和回放信息，不要覆盖成成功。
4. 本模板的目标是让执行人知道每一步要看什么接口、看哪些状态字段、失败时怎么留痕。
5. 被标记为“阻塞项”的步骤未通过时，默认不能判断 bridge 已具备 `Phase A` 执行准备。

## 基本信息

- 执行人：
  - Codex
- 执行日期：
  - `2026-04-09`
- 环境：
  - `backend test profile + H2 + port 8080`，`frontend vite dev + port 5173`
- 浏览器 / 设备：
  - Playwright / Chromium 146 / Windows
- 关联分支 / commit：
  - `main @ Step22/Step23 local verification`

## 链路步骤

### 1. customer onboarding 提交资料

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `POST /api/campus/customer/courier-onboarding/profile`
- 关键状态字段：
  - `reviewStatus`
  - `enabled`
- 预期结果：
  - 资料提交成功，资料状态重置为 `PENDING`
- 实际结果：
  - `2026-04-09` 已真实执行。使用用户 `13900139000 / 123456` 调用 `POST /api/campus/customer/courier-onboarding/profile`，资料提交成功，返回 `profileId = 1`，并将资料状态重置为 `PENDING`、`enabled = 0`。
- 失败记录建议：
  - 记录请求体、响应码、返回 message 和页面提示
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 是
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 2. customer 查看审核状态

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `GET /api/campus/customer/courier-onboarding/review-status`
- 关键状态字段：
  - `reviewStatus`
  - `enabled`
  - `canApplyCourierToken`
  - `message`
- 预期结果：
  - 可读到当前 `reviewStatus`、`enabled`、`message`
- 实际结果：
  - `2026-04-09` 已真实执行。提交资料后立即调用 `GET /api/campus/customer/courier-onboarding/review-status`，返回 `reviewStatus = PENDING`、`enabled = 0`、`canApplyCourierToken = false`。admin 审核通过后再次调用，返回 `reviewStatus = APPROVED`、`enabled = 1`、`canApplyCourierToken = true`。
- 失败记录建议：
  - 记录返回体、页面展示和 network 截图
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 是
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 3. customer 申请 courier token

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `GET /api/campus/customer/courier-onboarding/token-eligibility`
  - `POST /api/campus/courier/auth/token`
- 关键状态字段：
  - `eligible`
  - `reviewStatus`
  - `enabled`
  - `token`
- 预期结果：
  - token 申请成功并按现有前端机制存储
- 实际结果：
  - `2026-04-09` 已真实执行。先通过 `GET /api/campus/customer/courier-onboarding/token-eligibility` 确认 `eligible = true`，再使用用户 `13900139000 / 123456` 调用 `POST /api/campus/courier/auth/token` 成功返回 `courier_token` 与 `courierProfile.id = 1`。
- 失败记录建议：
  - 记录 eligibility 返回值、token 申请响应、浏览器本地存储结果
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 是
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 4. courier workbench 加载 profile / review-status

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `GET /api/campus/courier/profile`
  - `GET /api/campus/courier/review-status`
  - `GET /api/campus/courier/orders/available`
- 关键状态字段：
  - `Authorization`
  - `id`
  - `realName`
  - `reviewStatus`
  - `enabled`
- 预期结果：
  - `/courier/workbench` 正常加载资料与审核状态
  - `profile` / `review-status` 请求优先使用 `courier_token`
- 实际结果：
  - `2026-04-09` 已真实执行并通过。先用 API 确认 `GET /api/campus/courier/orders/available?page=1&pageSize=10` 返回 `CR202604070002`。随后在 Playwright 中只注入 `courier_token` 打开 `/courier/workbench`，页面成功停留在 workbench，且能加载 `profile / review-status / available orders`。这轮同时修正了 `UserLayout` 在无 `customer_token` 场景下误请求 customer 购物车接口的问题。
- 失败记录建议：
  - 记录 request headers、响应 message 和页面空态是否符合预期
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 是
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 5. courier 接单

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `GET /api/campus/courier/orders/available`
  - `POST /api/campus/courier/orders/{id}/accept`
  - `GET /api/campus/courier/orders/{id}`
- 关键状态字段：
  - `status`
  - `courierProfileId`
  - `acceptedAt`
- 预期结果：
  - 可接单列表中成功接单
  - 列表刷新，详情 drawer 打开
- 实际结果：
  - `2026-04-09` 已真实执行并通过。使用 user1 的 `courier_token` 查询可接单列表，真实返回 `CR202604070002`。随后调用 `POST /api/campus/courier/orders/CR202604070002/accept` 成功，详情返回 `status = ACCEPTED`、`courierProfileId = 1`、`acceptedAt` 已写入。
- 失败记录建议：
  - 记录订单 id、响应 message、接单前后列表差异
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 是
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 6. courier 取餐

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `POST /api/campus/courier/orders/{id}/pickup`
  - `GET /api/campus/courier/orders/{id}`
- 关键状态字段：
  - `status`
  - `pickedUpAt`
  - `updatedAt`
- 预期结果：
  - 订单状态推进到 `PICKED_UP`
  - 详情 drawer 刷新
- 实际结果：
  - `2026-04-09` 已真实执行并通过。调用 `POST /api/campus/courier/orders/CR202604070002/pickup`，请求体使用真实字段 `pickupProofImageUrl='/api/files/step22-pickup-proof.jpg'`、`courierRemark='Step22 真实联调取餐'`。详情回读显示 `status = PICKED_UP`、`pickedUpAt` 已写入。
- 失败记录建议：
  - 记录请求体、响应 message、详情刷新后的状态字段
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 是
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 7. courier deliver

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `POST /api/campus/courier/orders/{id}/deliver`
  - `GET /api/campus/courier/orders/{id}`
- 关键状态字段：
  - `status`
  - `deliveredAt`
  - `updatedAt`
  - `autoCompleteAt`
- 预期结果：
  - 订单状态从 `PICKED_UP -> DELIVERING` 或 `DELIVERING -> AWAITING_CONFIRMATION`
  - 详情 drawer 刷新
- 实际结果：
  - `2026-04-09` 已真实执行并通过。第一轮 `POST /api/campus/courier/orders/CR202604070002/deliver` 使用 `courierRemark='Step22 开始配送'`，状态推进到 `DELIVERING`；第二轮使用 `courierRemark='Step22 已送达'`，状态推进到 `AWAITING_CONFIRMATION`，并写入 `deliveredAt`。
- 失败记录建议：
  - 记录配送 remark、响应 message 和状态推进结果
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 是
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 8. courier 异常上报

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `POST /api/campus/courier/orders/{id}/exception-report`
  - `GET /api/campus/courier/orders/{id}`
- 关键状态字段：
  - `exceptionType`
  - `exceptionRemark`
  - `exceptionReportedAt`
- 预期结果：
  - 可在允许状态下提交 `exceptionType`、`exceptionRemark`
  - 详情 drawer 刷新并显示最新异常信息
- 实际结果：
  - `2026-04-09` 已真实执行并通过。在 `AWAITING_CONFIRMATION` 状态下调用 `POST /api/campus/courier/orders/CR202604070002/exception-report`，请求体使用真实字段 `exceptionType='DELIVERY_DELAY'`、`exceptionRemark='Step22 真实联调异常上报'`。详情回读显示 `exceptionType`、`exceptionRemark`、`exceptionReportedAt` 已写入。
- 失败记录建议：
  - 记录异常类型、异常说明、响应 message 和详情刷新后的异常字段
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 是否为阻塞项：
  - 否
- 是否通过：
  - [x] 通过
  - [ ] 不通过

### 9. customer 侧确认前状态可视化或结果查看

- 前置条件：
  - 
- 操作步骤：
  - 
- 接口观察点：
  - `GET /api/campus/courier/orders/{id}`
  - 如需要 customer 对照，可观察 `GET /api/campus/customer/orders/{id}`
- 关键状态字段：
  - `status`
  - `deliveredAt`
  - `autoCompleteAt`
  - `exceptionType`
  - `exceptionRemark`
- 预期结果：
  - courier 详情 drawer 在 `AWAITING_CONFIRMATION` 时展示等待用户确认区
  - 若订单已完成，可看到最小完成态
- 实际结果：
  - `2026-04-09` 已真实执行并通过。`CR202604070002` 在 deliver 后详情真实进入 `AWAITING_CONFIRMATION`，随后 customer2 调用 `POST /api/campus/customer/orders/CR202604070002/confirm` 成功。customer 详情与 courier 详情回读均显示 `status = COMPLETED`、`autoCompleteAt` 已写入。Playwright 再次通过 workbench 的“按订单号查看详情”入口回读 `CR202604070002`，drawer 成功展示 completed 最终摘要态。
  - `2026-04-09` Step 23 继续通过 customer 页面 `/user/campus/order-result?orderId=CR202604070002` 回读同一订单，页面成功展示 `COMPLETED` 状态、`deliveredAt`、`autoCompleteAt` 与完成后最小结果回看文案。
- 失败记录建议：
  - 记录状态区未展示或字段缺失的位置，并附带详情接口返回体截图
- 截图 / 日志占位：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
  - `project-logs/campus-relay/step-23-shared-regression-evidence.md`
- 是否为阻塞项：
  - 否
- 是否通过：
  - [x] 通过
  - [ ] 不通过

## 总结

- 本轮联调总体结论：
  - 已在本地 `backend test profile + H2 + frontend vite` 环境下真实跑通一轮完整链路，包含 onboarding、审核、token 申请、workbench 加载、接单、取餐、deliver、异常上报、customer 确认送达和 completed 结果回读。
- 阻塞问题：
  - repo 外依赖与仓库外历史调用仍未完成人工核实，因此 bridge 仍不能进入 `Phase A` 执行准备。
- 是否支持进入 `Phase A` 执行准备：
  - [ ] 支持
  - [x] 暂不支持

# bridge 联调 / 回归记录模板

## 使用说明

1. 本文档是待填写模板，不代表当前已经完成联调。
2. 每一步都应留下实际结果、截图路径或日志位置。
3. 若某一步失败，应保留失败现象和回放信息，不要覆盖成成功。
4. 本模板的目标是让执行人知道每一步要看什么接口、看哪些状态字段、失败时怎么留痕。
5. 被标记为“阻塞项”的步骤未通过时，默认不能判断 bridge 已具备 `Phase A` 执行准备。

## 基本信息

- 执行人：
  - 
- 执行日期：
  - 
- 环境：
  - 
- 浏览器 / 设备：
  - 
- 关联分支 / commit：
  - 

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
  - 
- 失败记录建议：
  - 记录请求体、响应码、返回 message 和页面提示
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 是
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录返回体、页面展示和 network 截图
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 是
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录 eligibility 返回值、token 申请响应、浏览器本地存储结果
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 是
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录 request headers、响应 message 和页面空态是否符合预期
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 是
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录订单 id、响应 message、接单前后列表差异
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 是
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录请求体、响应 message、详情刷新后的状态字段
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 是
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录配送 remark、响应 message 和状态推进结果
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 是
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录异常类型、异常说明、响应 message 和详情刷新后的异常字段
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 否
- 是否通过：
  - [ ] 通过
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
  - 
- 失败记录建议：
  - 记录状态区未展示或字段缺失的位置，并附带详情接口返回体截图
- 截图 / 日志占位：
  - 
- 是否为阻塞项：
  - 否
- 是否通过：
  - [ ] 通过
  - [ ] 不通过

## 总结

- 本轮联调总体结论：
  - 
- 阻塞问题：
  - 
- 是否支持进入 `Phase A` 执行准备：
  - [ ] 支持
  - [ ] 暂不支持

# API 总览

本文只记录当前主线需要理解的 API 分组，不替代源码。真实接口以 controller 代码为准。

## public

- `GET /api/public/shop/status`
- `GET /api/campus/public/health`
- `GET /api/campus/public/pickup-points`
- `GET /api/campus/public/delivery-rules`
- `POST /api/campus/public/feedback`

public 接口提供健康检查、规则读取、App 内反馈提交，以及基础运行检查能力。

## admin

### 基础管理能力

- employee
- category
- dish
- setmeal
- order
- statistics
- shop status

这些模块作为基础管理与运行检查能力保留；当前主演示链路聚焦 campus 订单、兼职、售后、反馈和结算。

### campus 订单与运营

- `GET /api/campus/admin/orders`
- `GET /api/campus/admin/orders/{id}`
- `GET /api/campus/admin/orders/{id}/timeline`
- `GET /api/campus/admin/orders/{id}/exception-summary`
- `GET /api/campus/admin/orders/{id}/location-reports`

### campus courier

- `GET /api/campus/admin/couriers`
- courier 审核接口
- `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
- `GET /api/campus/admin/couriers/{courierProfileId}/location-reports`

### campus exception

- `GET /api/campus/admin/exceptions`
- `GET /api/campus/admin/exceptions/{id}`
- `POST /api/campus/admin/exceptions/{id}/resolve`

### campus feedback

- `GET /api/campus/admin/feedback`
- `GET /api/campus/admin/feedback/{id}`
- `POST /api/campus/admin/feedback/{id}/process`

反馈状态固定为 `PENDING / IN_PROGRESS / RESOLVED`。管理员处理接口只允许推进到 `IN_PROGRESS` 或 `RESOLVED`，已完成反馈不可重复覆盖。

### campus after-sale

- 售后处理与决策接口
- `GET /api/campus/admin/orders/after-sale-executions`
- `GET /api/campus/admin/after-sale-execution-records`

### campus settlement

- `GET /api/campus/admin/settlements`
- `GET /api/campus/admin/settlements/{id}`
- `POST /api/campus/admin/settlements/{id}/confirm`
- `POST /api/campus/admin/settlements/{id}/payout-result`
- `POST /api/campus/admin/settlements/batch-payout-result`
- `GET /api/campus/admin/settlements/reconcile-summary`
- `POST /api/campus/admin/settlements/{id}/verify-payout`
- `GET /api/campus/admin/settlements/payout-batches`
- `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
- `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
- `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
- `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`
- `GET /api/campus/admin/settlements/reconcile-differences`
- `GET /api/campus/admin/settlements/reconcile-differences/{id}`
- `POST /api/campus/admin/settlements/reconcile-differences`
- `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve`

## customer

- customer 登录和用户资料
- 校园代送订单创建、模拟支付、列表、详情
- customer confirm
- cancel / after-sale
- `GET /api/campus/customer/orders/{id}`
- `GET /api/campus/customer/orders/{id}/after-sale-result`
- onboarding:
  - `POST /api/campus/customer/courier-onboarding/profile`
  - `GET /api/campus/customer/courier-onboarding/profile`
  - `GET /api/campus/customer/courier-onboarding/review-status`
  - `GET /api/campus/customer/courier-onboarding/token-eligibility`

## courier

- `POST /api/campus/courier/auth/token`
- `GET /api/campus/courier/profile`
- `GET /api/campus/courier/review-status`
- `GET /api/campus/courier/orders/available`
- `GET /api/campus/courier/orders/{id}`
- `POST /api/campus/courier/orders/{id}/accept`
- `POST /api/campus/courier/orders/{id}/pickup`
- `POST /api/campus/courier/orders/{id}/deliver`
- `POST /api/campus/courier/orders/{id}/exception-report`
- courier location report

## 兼容接口说明

`/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 保留 customer/courier 双 token 兼容读取。当前兼容接口处于冻结态，上线前不应临时删除或收紧。

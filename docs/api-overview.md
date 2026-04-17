# API 总览

本文只记录当前主线需要理解的 API 分组，不替代源码。真实接口以 controller 代码为准。

## public

- `GET /api/public/shop/status`

保留旧外卖公开能力，用于基础运行检查。

## admin

### 旧外卖后台

- employee
- category
- dish
- setmeal
- order
- statistics
- shop status

这些模块保留可运行，但不是当前 campus 主演示链路。

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

## bridge 说明

`/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 仍保留 customer/courier 双 token 兼容读取。当前 bridge 处于 `Phase A no-op` 冻结态，不能直接删除。

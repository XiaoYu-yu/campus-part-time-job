# Frontend

前端基于 Vue 3 + Vite，保留旧外卖管理端和用户端，同时新增校园代送 customer / courier / admin 演示链路。

整项目入口请先看：

- [根 README](../README.md)
- [交付与启动说明](../docs/delivery-guide.md)
- [校园代送总览](../project-logs/campus-relay/summary.md)

## 本地启动

```powershell
cd D:\20278\code\Campus part-time job\frontend
npm install
npm run dev
```

前端默认代理 `/api` 到 `http://localhost:8080`。

## 验证命令

```powershell
npm run lint
npm run test
npm run build
```

## 主要路由

### customer

- `/user/login`
- `/user`
- `/user/orders`
- `/user/profile`
- `/user/campus/courier-onboarding`
- `/user/campus/order-result`
- `/user/campus/after-sale-result`

### courier

- `/courier/workbench`

### admin

- `/login`
- `/dashboard`
- `/campus/settlement-batches`
- `/campus/settlement-batches/:batchNo`
- `/campus/settlements`
- `/campus/after-sale-executions`
- `/campus/courier-ops`
- `/campus/exceptions`

旧外卖后台路由仍保留，包括员工、分类、菜品、套餐、订单、统计和店铺状态。

## API 封装

### campus

- `src/api/campus-customer.js`
- `src/api/campus-courier.js`
- `src/api/campus-admin.js`

### 旧外卖与通用模块

- `src/api/customer.js`
- `src/api/customer-order.js`
- `src/api/cart.js`
- `src/api/address.js`
- `src/api/order.js`
- `src/api/statistics.js`
- `src/api/shop.js`

## token 约束

1. `admin_token` 用于 `/api/campus/admin/**` 和旧管理端接口。
2. `customer_token` 用于 `/api/campus/customer/**`。
3. `courier_token` 用于 `/api/campus/courier/orders/**`、courier workbench 等 courier 主链路。
4. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 仍保留 bridge 兼容读取，不要随意删除或收紧。

## 当前冻结线

1. bridge 主线处于 `Phase A no-op` 冻结态。
2. 展示 polish 线处于冻结/维护态。
3. 不要为了展示继续机械新增页面或改 token 附着逻辑。

## 已知非阻塞问题

1. 构建可能出现 Sass `@import` 弃用告警。
2. ECharts / 统计相关 chunk 体积可能偏大。
3. 这些告警不阻塞本地演示和试运营闭环。

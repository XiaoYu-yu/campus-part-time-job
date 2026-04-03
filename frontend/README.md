# Frontend

前端基于 Vue 3 + Vite，包含管理后台与用户端两套路由。这个 README 只负责前端本地开发说明，整项目交付请先看根目录 [README.md](D:/20278/code/show_shop1/README.md) 和 [项目交付说明.md](D:/20278/code/show_shop1/项目交付说明.md)。

## 本地联调前提

- 前端默认代理 `/api` 到 `http://localhost:8080`
- 后端开发环境默认连接本机 MariaDB
- 后端必须显式以 `dev` profile 启动，例如 `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- 启动顺序建议先后端，再前端

## 在 HBuilder X 中使用

- HBuilder X 可以直接打开 `frontend` 目录进行编辑
- 这个项目是标准 Vite 项目，不使用 HBuilder X 专属运行器
- 前端启动仍然通过终端执行：

```bash
npm install
npm run dev
```

## 运行命令

```bash
npm install
npm run dev
npm run lint
npm run test
npm run build
```

## 路由说明

### 管理端

- 登录页：`/login`
- 主布局：`/`
- 受保护页面：仪表盘、员工、分类、菜品、套餐、订单、统计、店铺营业状态
- 未登录访问管理端受保护路由时，会跳转到 `/login`

### 用户端

- 登录页：`/user/login`
- 主要页面：`/user/home`、`/user/category/:id`、`/user/dish/:id`、`/user/cart`、`/user/checkout`、`/user/orders`、`/user/profile`
- 用户端采用全量登录守卫，未登录访问 `/user/**` 业务页会跳转到 `/user/login`

## API 模块

- [src/api/order.js](D:/20278/code/show_shop1/frontend/src/api/order.js)：管理端订单接口
- [src/api/statistics.js](D:/20278/code/show_shop1/frontend/src/api/statistics.js)：统计接口
- [src/api/public.js](D:/20278/code/show_shop1/frontend/src/api/public.js)：用户端公开浏览接口
- [src/api/customer.js](D:/20278/code/show_shop1/frontend/src/api/customer.js)：用户登录和用户信息
- [src/api/cart.js](D:/20278/code/show_shop1/frontend/src/api/cart.js)：购物车接口
- [src/api/address.js](D:/20278/code/show_shop1/frontend/src/api/address.js)：地址接口
- [src/api/customer-order.js](D:/20278/code/show_shop1/frontend/src/api/customer-order.js)：用户订单接口
- [src/api/shop.js](D:/20278/code/show_shop1/frontend/src/api/shop.js)：店铺营业状态接口

## 状态管理

- [src/stores/user.js](D:/20278/code/show_shop1/frontend/src/stores/user.js)：管理端身份与资料
- [src/stores/customer.js](D:/20278/code/show_shop1/frontend/src/stores/customer.js)：用户端身份与资料
- [src/stores/mock.js](D:/20278/code/show_shop1/frontend/src/stores/mock.js)：保留为旧辅助文件，核心用户页已不再依赖

## 关键实现约束

- 前端订单页统一传 `orderNo/customerName/status/beginTime/endTime/page/pageSize`
- 状态展示由前端完成中文映射，后端只处理整数状态码
- 请求拦截器会按 URL 前缀自动选择 `admin_token` 或 `customer_token`
- `BaseDialogForm.vue` 已改为操作内部副本，避免直接修改 props

## 当前已知非阻塞问题

- 构建时仍存在 Sass `@import` 弃用告警
- 统计与图表相关打包 chunk 体积偏大，`npm run build` 会给出告警

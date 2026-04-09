# Step 23 - 共享回归留痕

## 目标

1. 将 Step 22 已真实跑通的本地完整链路整理成可共享、可复用、可答辩说明的证据摘要。
2. 明确哪些证据已经真实存在，哪些内容仍需后续补截图或人工核实。
3. 不伪造 repo 外依赖核实结果，不把待人工项写成已关闭。

## 已真实跑通的链路范围

本轮整理的是 Step 22 已真实执行的本地链路，不新增伪造结果：

1. customer onboarding 提交资料
2. admin 审核通过 courier profile
3. customer 申请 courier token
4. courier workbench 加载 profile / review-status / available orders
5. courier 接单
6. courier 取餐
7. courier deliver 到 `DELIVERING`
8. courier deliver 到 `AWAITING_CONFIRMATION`
9. courier 异常上报
10. customer 确认送达
11. courier completed 结果回读
12. Step 23 customer 侧 completed 结果回看页回读

## 使用的测试账号

### customer onboarding / courier token 申请账号

1. 手机号：`13900139000`
2. 密码：`123456`
3. 用途：
   - onboarding 提交资料
   - token eligibility
   - courier token 申请

### customer 订单所属账号

1. 手机号：`13900139001`
2. 密码：`123456`
3. 用途：
   - 订单 `CR202604070002` 所属 customer
   - customer confirm
   - customer completed 结果回看

### admin 账号

1. 手机号：`13800138000`
2. 密码：`123456`
3. 用途：
   - admin 审核 courier profile

## 使用的样本订单

### workbench 可接单样本

1. 订单号：`CR202604070002`
2. 来源：
   - `backend/src/main/resources/db/data-h2.sql`
3. 关键初始条件：
   - `payment_status = PAID`
   - `status = WAITING_ACCEPT`
   - `pickup_point_id = 2`
   - `delivery_building = 梅园`

### completed 回读样本

1. 订单号：`CR202604070002`
2. Step 22 完整链路跑通后，该订单已进入 `COMPLETED`
3. Step 23 继续用同一订单完成 customer 页面结果回看

## 关键状态流转

真实已跑通的状态流转如下：

1. `WAITING_ACCEPT`
2. `ACCEPTED`
3. `PICKED_UP`
4. `DELIVERING`
5. `AWAITING_CONFIRMATION`
6. `COMPLETED`

## 对应接口

### customer 侧

1. `POST /api/campus/customer/courier-onboarding/profile`
2. `GET /api/campus/customer/courier-onboarding/review-status`
3. `GET /api/campus/customer/courier-onboarding/token-eligibility`
4. `POST /api/campus/courier/auth/token`
5. `POST /api/campus/customer/orders/CR202604070002/confirm`
6. `GET /api/campus/customer/orders/CR202604070002`

### admin 侧

1. `POST /api/campus/admin/couriers/1/review`

### courier 侧

1. `GET /api/campus/courier/profile`
2. `GET /api/campus/courier/review-status`
3. `GET /api/campus/courier/orders/available`
4. `POST /api/campus/courier/orders/CR202604070002/accept`
5. `POST /api/campus/courier/orders/CR202604070002/pickup`
6. `POST /api/campus/courier/orders/CR202604070002/deliver`
7. `POST /api/campus/courier/orders/CR202604070002/exception-report`
8. `GET /api/campus/courier/orders/CR202604070002`

## 对应页面入口

1. customer onboarding：
   - `/user/campus/courier-onboarding`
2. courier workbench：
   - `/courier/workbench`
3. customer completed 结果回看：
   - `/user/campus/order-result?orderId=CR202604070002`

## 相关运行日志位置

### 已存在的本地服务日志

1. `project-logs/runtime/step21-backend.out.log`
2. `project-logs/runtime/step21-backend.err.log`
3. `project-logs/runtime/step21-frontend.out.log`
4. `project-logs/runtime/step21-frontend.err.log`

### 已存在的联调过程文档

1. `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
2. `project-logs/campus-relay/bridge-regression-template.md`

## 已真实存在的证据

1. H2/test 环境中的可接单样本订单 `CR202604070002`
2. Step 22 真实链路说明文档
3. bridge regression template 中的真实回填结果
4. Step 23 customer completed 结果回看页面：
   - 路由已接入
   - 页面已能回读 `CR202604070002`
   - 已验证显示 `COMPLETED` 状态与结果回看文案

## 仍缺的补充证据

1. 统一截图归档：
   - onboarding 提交页
   - workbench 接单 / 取餐 / deliver / completed 状态截图
   - customer 结果回看页截图
2. repo 外依赖人工核实结果
3. 共享位置可访问的完整 network 导出或操作录像

## repo 外依赖边界

以下内容仍无法从当前仓库直接证明：

1. 仓库外旧页面是否仍直接调用 `/api/campus/courier/profile`
2. 仓库外旧页面是否仍直接调用 `/api/campus/courier/review-status`
3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

这部分仍必须按 `bridge-execution-readiness-checklist.md` 做人工核实，不能在本文件中伪造关闭。

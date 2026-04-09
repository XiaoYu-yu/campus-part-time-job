# Step 22 - H2 可接单数据补齐 / 本地完整链路真实联调

## 本次目标

1. 解决 `test profile + H2` 下 `GET /api/campus/courier/orders/available` 返回空列表的真实阻塞。
2. 在不伪造结果的前提下，尽量跑通一轮完整本地链路：
   - onboarding
   - 审核
   - token 申请
   - workbench
   - 接单
   - 取餐
   - deliver
   - 异常上报
   - customer confirm
   - completed 回读
3. 继续推进 bridge 收口评估，但不删除旧 bridge。

## 已完成项

1. 在 `backend/src/main/resources/db/data-h2.sql` 新增 H2 联调样本订单：
   - `CR202604070002`
   - `customer_user_id = 2`
   - `pickup_point_id = 2`
   - `payment_status = PAID`
   - `status = WAITING_ACCEPT`
   - `delivery_building = 梅园`
2. 该数据使 `GET /api/campus/courier/orders/available?page=1&pageSize=10` 在本地真实返回至少一条可接单记录。
3. 修正 `/courier/workbench` 的 repo 内阻塞：
   - `frontend/src/layout/UserLayout.vue` 在无 `customer_token` 时不再请求 customer 购物车接口
   - `/courier/**` 不再强依赖 customer 导航渲染
4. 本地真实执行并通过以下步骤：
   - customer onboarding 提交资料
   - customer 查看审核状态
   - admin 审核通过
   - customer 查看 token eligibility
   - customer 申请 courier token
   - courier workbench 加载 profile / review-status / available orders
   - courier 接单
   - courier 取餐
   - courier deliver 到 `DELIVERING`
   - courier deliver 到 `AWAITING_CONFIRMATION`
   - courier 异常上报
   - customer 确认送达
   - courier completed 结果回读
5. Playwright 已再次验证：
   - 纯 `courier_token` 路径可稳定停留在 `/courier/workbench`
   - completed 订单 `CR202604070002` 可通过“按订单号查看详情”入口回读

## 本地联调样本

### 账号

1. courier candidate / customer onboarding：
   - `13900139000 / 123456`
2. customer order owner：
   - `13900139001 / 123456`
3. admin：
   - `13800138000 / 123456`

### 样本订单

1. `CR202604070002`
2. 本轮真实状态推进：
   - `WAITING_ACCEPT`
   - `ACCEPTED`
   - `PICKED_UP`
   - `DELIVERING`
   - `AWAITING_CONFIRMATION`
   - `COMPLETED`

## 如何复现

1. 在 `backend/` 下使用 `test` profile 启动服务：
   - `.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.arguments=--server.port=8080`
2. 在 `frontend/` 下启动 Vite：
   - `npm run dev -- --host 127.0.0.1 --port 5173`
3. H2 为内存库，重启 backend 后会重新执行 `backend/src/main/resources/db/data-h2.sql`，样本订单 `CR202604070002` 会重新回到 `WAITING_ACCEPT`
4. 使用本文记录的三个账号按顺序执行 onboarding / 审核 / token / workbench 联调
5. 若已经消费过 `CR202604070002`，直接重启 backend test profile 即可重置样本状态

## 真实执行记录

1. onboarding 提交资料：
   - `POST /api/campus/customer/courier-onboarding/profile`
   - user：`13900139000`
2. admin 审核：
   - `POST /api/campus/admin/couriers/1/review`
   - action：通过
3. token 申请：
   - `POST /api/campus/courier/auth/token`
   - user：`13900139000`
4. available order：
   - `GET /api/campus/courier/orders/available?page=1&pageSize=10`
   - 真实返回：`CR202604070002`
5. 接单：
   - `POST /api/campus/courier/orders/CR202604070002/accept`
6. 取餐：
   - `POST /api/campus/courier/orders/CR202604070002/pickup`
   - 请求体：
     - `pickupProofImageUrl=/api/files/step22-pickup-proof.jpg`
     - `courierRemark=Step22 真实联调取餐`
7. deliver：
   - 第一次：`courierRemark=Step22 开始配送`
   - 第二次：`courierRemark=Step22 已送达`
8. 异常上报：
   - `POST /api/campus/courier/orders/CR202604070002/exception-report`
   - 请求体：
     - `exceptionType=DELIVERY_DELAY`
     - `exceptionRemark=Step22 真实联调异常上报`
9. customer confirm：
   - `POST /api/campus/customer/orders/CR202604070002/confirm`
   - user：`13900139001`
10. completed 回读：
   - `GET /api/campus/courier/orders/CR202604070002`
   - Playwright 在 `/courier/workbench` 中通过订单号入口回读

## 修改文件

1. `backend/src/main/resources/db/data-h2.sql`
2. `frontend/src/layout/UserLayout.vue`
3. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
4. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
5. `project-logs/campus-relay/bridge-regression-template.md`
6. `project-logs/campus-relay/summary.md`
7. `project-logs/campus-relay/pending-items.md`
8. `project-logs/campus-relay/file-change-list.md`
9. `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`

## 遗留问题

1. repo 外旧页面、历史客户端和手工脚本依赖仍待人工核实。
2. 当前 bridge 仍不能进入 `Phase A` 执行准备。
3. completed 后只做到最小只读承接，没有继续扩更完整 courier 端。
4. 本轮没有补第五个 admin 页。

## 下一步建议

1. 先按 checklist 关闭 repo 外依赖人工核实项。
2. 再基于本轮真实样本补一轮可共享的联调记录归档。
3. 若继续扩前端，优先评估 customer confirm 结果回看或 completed 后更明确结果承接。

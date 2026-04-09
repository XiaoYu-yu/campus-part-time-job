# Step 18 - bridge 执行准备清单 / courier workbench 最小异常上报承接

## 本次目标

1. 继续把 bridge 收口评估从“执行前评估”推进到“执行准备清单”层。
2. 为 courier workbench 补最小异常上报承接。
3. 保持旧系统可运行，不改后端状态机，不删除旧 bridge。

## 已完成项

1. 更新了 [bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)，新增 `Step 18 执行准备清单`。
2. 文档中把以下事项明确标成“待人工核实”：
   - 是否还有仓库外旧页面直接调用 `/api/campus/courier/profile`
   - 是否还有仓库外旧页面直接调用 `/api/campus/courier/review-status`
   - 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge
   - 是否已有一轮 `customer onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的稳定联调记录
   - workbench 在纯 `courier_token` 路径下是否稳定
3. bridge 结论本轮已进一步收敛：
   - repo 内证据稳定
   - repo 外依赖仍只能列为待人工核实边界
   - 当前还不具备进入 `Phase A` 执行准备的完整条件
4. courier workbench 在订单详情 drawer 中新增最小异常上报区。
5. 前端严格复用现有异常上报接口：
   - `POST /api/campus/courier/orders/{id}/exception-report`
6. 前端严格复用现有 DTO 字段：
   - `exceptionType`
   - `exceptionRemark`
7. 异常上报区只在后端真实允许的状态下启用：
   - `ACCEPTED`
   - `PICKED_UP`
   - `DELIVERING`
   - `AWAITING_CONFIRMATION`
8. 异常上报成功后会：
   - 弹成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
9. 异常上报失败时继续展示后端原错误信息。
10. 本轮没有新增后端接口、没有改数据库、没有补第五个 admin 页。

## 修改文件

1. `frontend/src/api/campus-courier.js`
2. `frontend/src/views/courier/CourierWorkbench.vue`
3. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
4. `project-logs/campus-relay/summary.md`
5. `project-logs/campus-relay/pending-items.md`
6. `project-logs/campus-relay/file-change-list.md`
7. `project-logs/campus-relay/step-18-bridge-readiness-checklist-and-workbench-exception.md`

## 手工联调说明

1. 页面路径：`/courier/workbench`
2. 页面调用接口：
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
   - `GET /api/campus/courier/orders/available`
   - `GET /api/campus/courier/orders/{id}`
   - `POST /api/campus/courier/orders/{id}/accept`
   - `POST /api/campus/courier/orders/{id}/pickup`
   - `POST /api/campus/courier/orders/{id}/deliver`
   - `POST /api/campus/courier/orders/{id}/exception-report`
3. 无 `courier_token` 时，workbench 不调用 courier 业务接口，只显示空态和回退入口。
4. 有 `courier_token` 时，可在详情 drawer 中完成：
   - 接单后的详情查看
   - 取餐
   - deliver
   - 异常上报

## 遗留问题

1. repo 外或历史调用对旧 bridge 的依赖仍未核实完成。
2. 还缺一轮稳定联调与回归记录，暂不具备进入 `Phase A` 执行准备的完整条件。
3. workbench 目前只做到最小异常上报承接，还没有更完整的后续动作页群。
4. 第五个 admin 只读页本轮未做。

## 下一步建议

1. 优先关闭 bridge 执行准备清单中的人工核实项。
2. 若继续扩 courier 前端，优先补异常上报后的后续承接或 customer 确认前可视化。
3. 在人工核实项和一轮稳定联调完成后，再判断是否进入 `Phase A` 执行准备。

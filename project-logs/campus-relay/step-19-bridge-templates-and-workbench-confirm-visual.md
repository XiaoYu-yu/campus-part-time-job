# Step 19 - bridge 模板文档 / courier workbench confirm 前可视化

## 本次目标

1. 把 bridge 执行准备缺口从“清单”推进到“可填写的人工核实模板和联调模板”。
2. 为 courier workbench 补送达后 / confirm 前的最小可视化承接。
3. 继续保持旧系统可运行，不删除旧 bridge，不改后端状态机。

## 已完成项

1. 更新了 [bridge-phaseout-evaluation.md](bridge-phaseout-evaluation.md)。
2. 结论仍然明确为：
   - repo 内证据稳定
   - repo 外依赖仍是待人工核实边界
   - 当前还不具备进入 `Phase A` 执行准备的完整条件
3. 新增了 bridge 人工核实 checklist：
   - [bridge-execution-readiness-checklist.md](bridge-execution-readiness-checklist.md)
4. checklist 已把待人工核实项改写成可执行模板，每项都包含：
   - 核实方法
   - 记录位置
   - 核实结果
   - 负责人
   - 日期
   - 是否通过
5. 新增了 bridge 联调 / 回归记录模板：
   - [bridge-regression-template.md](bridge-regression-template.md)
6. 回归模板已覆盖完整链路：
   - customer onboarding 提交资料
   - customer 查看审核状态
   - customer 申请 courier token
   - courier workbench 加载 profile/review-status
   - courier 接单
   - courier 取餐
   - courier deliver
   - courier 异常上报
   - customer 侧确认前状态可视化或结果查看
7. courier workbench 本轮继续复用现有详情 drawer，没有新增页面。
8. 在 drawer 中新增“送达后状态可视化”区：
   - `AWAITING_CONFIRMATION` 时提示“已送达，等待用户确认”
   - `COMPLETED` 时提示“订单已完成”
9. 该区域继续只读复用现有详情返回字段：
   - `status`
   - `deliveredAt`
   - `acceptedAt`
   - `pickedUpAt`
   - `autoCompleteAt`
   - `updatedAt`
   - `exceptionType`
   - `exceptionRemark`
10. deliver 成功后刷新详情时，如订单进入 `AWAITING_CONFIRMATION`，用户可直接在同一 drawer 中看到等待确认区。
11. 无 `courier_token` 时，workbench 仍不调用 courier 业务接口，只展示空态和回退入口。
12. 本轮没有补第五个 admin 页。

## 修改文件

1. `frontend/src/views/courier/CourierWorkbench.vue`
2. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
3. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
4. `project-logs/campus-relay/bridge-regression-template.md`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`
8. `project-logs/campus-relay/step-19-bridge-templates-and-workbench-confirm-visual.md`

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
3. deliver 成功后：
   - 刷新当前订单详情
   - 若状态进入 `AWAITING_CONFIRMATION`，drawer 中出现等待用户确认区
4. 若订单已 `COMPLETED`：
   - drawer 中展示已完成态
5. 无 `courier_token` 时：
   - 页面不调用 courier 业务接口
   - 继续显示空态和回退入口

## 遗留问题

1. repo 外或历史调用对旧 bridge 的依赖仍未核实完成。
2. checklist 和回归模板已经具备，但还没有真实填写结果。
3. 当前仍不具备进入 `Phase A` 执行准备的完整条件。
4. 第五个 admin 只读页本轮未做。

## 下一步建议

1. 先按 checklist 和联调模板补齐人工核实结果。
2. 若继续扩 courier 前端，优先补 confirm 后或 completed 后的最小承接点。
3. 在人工核实结果和一轮稳定联调完成后，再判断是否进入 `Phase A` 执行准备。

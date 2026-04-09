# Step 17 - bridge 执行前评估 / courier workbench 最小 deliver 承接

## 本次目标

1. 让 bridge 收口评估从“已可进入计划设计阶段”推进到“是否具备进入计划执行准备阶段的条件”
2. 让 courier workbench 从“接单 + 详情 + 取餐”推进到“至少完成最小 deliver 承接”
3. 保持旧系统可运行，不切旧前端主链路

## 实际完成项

1. 更新 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 本轮把 bridge 评估重点从“能否做计划设计”推进为“能否进入 Phase A 执行准备”。
3. 当前文档结论已明确：
   - repo 内证据已经稳定
   - repo 外依赖仍只能列为待确认边界
   - 当前还不具备进入 Phase A 执行准备的完整条件
4. courier workbench 已补最小 deliver 承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入现有 `POST /api/campus/courier/orders/{id}/deliver`
5. deliver 依然没有新建页面，直接放在订单详情 drawer 中。
6. 本轮按后端真实 DTO 接入，没有臆造 proofImageUrl 或 deliveredAt：
   - `courierRemark`
7. deliver 区按真实 service 语义显示动作：
   - `PICKED_UP -> 开始配送`
   - `DELIVERING -> 确认送达`
8. 取餐成功后，当前订单详情会刷新；当状态变为 `PICKED_UP` 时，用户可继续在同一 drawer 中点击“开始配送”。
9. deliver 成功后：
   - 显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 状态
10. deliver 失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
11. 本轮没有改 backend 接口、数据库和状态机
12. 本轮没有补第五个 admin 页

## 实际修改文件

- `frontend/src/api/campus-courier.js`
- `frontend/src/views/courier/CourierWorkbench.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- `project-logs/campus-relay/step-17-bridge-readiness-and-workbench-deliver.md`

## bridge 执行前评估结论

1. repo 内已确认调用方仍收敛到：
   - `frontend/src/views/courier/CourierWorkbench.vue`
2. repo 内 customer onboarding 前置场景已不再依赖旧 bridge
3. repo 外依赖仍无法仅凭当前仓库代码确认
4. 当前最准确结论：
   - 已具备继续做收口计划设计的稳定基础
   - 但还不具备进入 Phase A 执行准备的完整条件
5. 真正开始收口前仍缺：
   - repo 外依赖确认
   - 一轮稳定联调与回归记录

## courier workbench 最小 deliver 承接说明

1. 页面仍是：
   - `/courier/workbench`
2. 接单后：
   - 可刷新列表
   - 自动打开详情 drawer
3. 详情 drawer 里现在有两个后续动作区：
   - 最小取餐承接
   - 最小送达承接
4. deliver 动作调用：
   - `POST /api/campus/courier/orders/{id}/deliver`
5. 动作文案按真实状态机变化：
   - `PICKED_UP` 时显示“开始配送”
   - `DELIVERING` 时显示“确认送达”
6. 有 `courier_token` 时：
   - 可查看详情
   - 可接单
   - 接单后可继续取餐
   - 取餐后可继续在同一 drawer 中推进 deliver
7. 无 `courier_token` 时：
   - 页面不调用 courier 业务接口
   - 只展示空态和回退入口

## 为什么这轮不补第五个 admin 页

1. 当前更高优先级是把 bridge 评估推进到“执行前评估”层
2. workbench 的最小 deliver 承接比继续补展示页更直接支撑主链路闭环
3. 第五个 admin 页仍不是当前主阻塞点

## 手工联调说明

1. 访问 `/courier/workbench`
2. 有 `courier_token` 时：
   - 页面会加载 profile、review-status、available orders
   - 点击“接单”后会刷新列表并自动打开详情 drawer
   - 在详情 drawer 输入取餐凭证路径后可点击“确认取餐”
   - 状态刷新到 `PICKED_UP` 后，可继续点击“开始配送”
   - 状态刷新到 `DELIVERING` 后，可继续点击“确认送达”
3. 无 `courier_token` 时：
   - 页面不调用 courier 接口
   - 只展示空态和回退入口
4. deliver 成功时：
   - 显示成功提示
   - 刷新订单详情与 workbench
5. deliver 失败时：
   - 显示后端原错误信息

## 遗留问题

1. repo 外依赖仍未确认
2. workbench 目前只补到 deliver 承接，还没有 exception-report 的前端动作
3. 真实支付、真实退款、真实打款仍未接入
4. 第五个 admin 页本轮未做

## 下一步建议

1. 进入 Step 18
2. 先补 repo 外依赖确认或联调回归证据
3. 若继续扩 courier 前端，优先补异常上报承接，而不是继续机械补展示页

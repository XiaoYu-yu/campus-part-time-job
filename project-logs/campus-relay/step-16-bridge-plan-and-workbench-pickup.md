# Step 16 - bridge 收口计划评估 / courier workbench 最小取餐承接

## 本次目标

1. 让 bridge 收口评估从“已具备基础”推进到“是否可以进入实际逐步收口计划”的结论层
2. 让 courier workbench 从“接单 + 看详情”推进到“至少还能继续做下一步”
3. 保持旧系统可运行，不切旧前端主链路

## 实际完成项

1. 新增 bridge 收口评估文档：
   - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
2. 文档中明确了：
   - repo 内已确认调用
   - repo 内可明确排除的依赖
   - repo 外暂未确认依赖范围
   - 进入逐步收口计划前仍缺的证据
   - 建议的 Phase A / B / C 分阶段动作
   - 回滚与兼容保留策略
3. 本轮 bridge 结论已从“原则上接近”推进为：
   - 已可以进入“逐步收口计划设计阶段”
   - 但仍不等于实际删除旧 bridge
4. courier workbench 已补最小取餐承接：
   - 继续复用 `GET /api/campus/courier/orders/{id}`
   - 直接接入现有 `POST /api/campus/courier/orders/{id}/pickup`
5. 取餐动作没有新建页面，直接放在订单详情 drawer 中：
   - 新增 `pickupProofImageUrl` 输入框
   - 新增 `courierRemark` 输入框
   - 新增“确认取餐”按钮
6. 本轮按后端真实 DTO 接入，没有臆造取餐码入参：
   - `pickupProofImageUrl`
   - `courierRemark`
7. 接单成功后，用户现在可以：
   - 刷新可接单列表
   - 自动打开详情 drawer
   - 继续在 drawer 中尝试取餐
8. 取餐成功后，页面会：
   - 显示成功提示
   - 刷新当前订单详情
   - 刷新 workbench 数据
9. 取餐失败时：
   - 继续展示后端原错误信息
   - 不新增前端自定义状态机
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页

## 实际修改文件

- `frontend/src/api/campus-courier.js`
- `frontend/src/views/courier/CourierWorkbench.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-16-bridge-plan-and-workbench-pickup.md`
- `project-logs/campus-relay/bridge-phaseout-evaluation.md`

## bridge 收口评估结论

1. repo 内已确认调用方仍收敛到：
   - `frontend/src/views/courier/CourierWorkbench.vue`
2. repo 内 customer onboarding 前置场景已不再依赖旧 bridge
3. repo 外依赖仍无法仅凭当前仓库代码确认
4. 因此当前最准确结论是：
   - 已可以进入“逐步收口计划设计阶段”
   - 但还不能进入实际删除阶段

## courier workbench 最小取餐承接说明

1. 页面仍是：
   - `/courier/workbench`
2. 接单后：
   - 成功提示
   - 刷新可接单列表
   - 自动打开订单详情 drawer
3. 详情 drawer 里新增最小取餐区：
   - `pickupProofImageUrl`
   - `courierRemark`
   - “确认取餐”
4. 取餐动作调用：
   - `POST /api/campus/courier/orders/{id}/pickup`
5. 有 `courier_token` 时：
   - 可查看详情
   - 可尝试接单
   - 订单状态为 `ACCEPTED` 时可尝试取餐
6. 无 `courier_token` 时：
   - 页面不调用 courier 业务接口
   - 只展示空态和回退入口

## 为什么这轮不补第五个 admin 页

1. 当前更高优先级是把 bridge 收口评估补成“可进入计划设计阶段”
2. workbench 的最小取餐承接比继续补展示页更直接支撑主链路闭环
3. 第五个 admin 页不是当前主阻塞点

## 手工联调说明

1. 访问 `/courier/workbench`
2. 有 `courier_token` 时：
   - 页面会加载 profile、review-status、available orders
   - 点击“接单”后会刷新列表并自动打开详情 drawer
   - 详情 drawer 中可继续输入取餐凭证路径并尝试取餐
3. 无 `courier_token` 时：
   - 页面不调用 courier 接口
   - 只展示空态和回退入口
4. 取餐成功时：
   - 显示成功提示
   - 刷新订单详情与 workbench
5. 取餐失败时：
   - 显示后端原错误信息

## 遗留问题

1. repo 外依赖仍未确认
2. workbench 目前只补到最小取餐承接，还没有 deliver / exception-report 的前端动作
3. 真实支付、真实退款、真实打款仍未接入
4. 第五个 admin 页本轮未做

## 下一步建议

1. 进入 Step 17
2. 先补 repo 外依赖确认或联调回归证据
3. 若继续扩 courier 前端，优先补 deliver 或异常上报承接，而不是继续机械补展示页

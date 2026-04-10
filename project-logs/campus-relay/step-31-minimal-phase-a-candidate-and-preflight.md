# Step 31 - 最小 `Phase A` 动作候选评估 / 执行前最小回归复核

## 本轮目标

1. 先按 Step 30 的最小回归清单做一轮真实执行前复核。
2. 再评估一个最小、可回滚的 `Phase A` 动作候选。
3. 如果候选不够安全或收益不足，就明确记录“暂不执行”，而不是为了推进阶段硬改代码。

## 执行前最小回归复核结果

### 运行环境

1. backend：
   - `test profile`
   - `http://127.0.0.1:8080`
2. frontend：
   - Vite dev server
   - `http://127.0.0.1:5173`

### 样本账号与订单

1. courier candidate / onboarding：
   - `13900139000 / 123456`
2. customer order owner：
   - `13900139001 / 123456`
3. admin：
   - `13800138000 / 123456`
4. 样本订单：
   - `CR202604070002`

### 本轮真实复核步骤

1. `customer onboarding` 提交资料：通过
2. customer 查看审核状态（审核前 `PENDING`）：通过
3. admin 审核通过：通过
4. customer 再查审核状态（审核后 `APPROVED`）：通过
5. customer 申请 `courier token`：通过
6. `/courier/workbench` 加载 `profile / review-status / available orders`：通过
7. pure `courier_token` 路径稳定：通过
8. 接单：通过
9. 取餐：通过
10. `deliver -> AWAITING_CONFIRMATION`：通过
11. 异常上报：通过
12. customer confirm：通过
13. `completed` 回读：通过
14. customer 结果回看页：通过

### 关键状态流转

1. `WAITING_ACCEPT`
2. `ACCEPTED`
3. `PICKED_UP`
4. `DELIVERING`
5. `AWAITING_CONFIRMATION`
6. `COMPLETED`

### 浏览器级确认

1. Playwright 已在仅保留 `courier_token`、移除 `customer_token` 的情况下打开 `/courier/workbench`
2. Playwright network 已确认：
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
   - `GET /api/campus/courier/orders/available`
   都附着 `courier_token`
3. Playwright 已确认：
   - `/user/campus/order-result?orderId=CR202604070002`
   - 页面可正常显示 `COMPLETED` 结果摘要

## 本轮评估过的最小动作候选

### 候选 A：继续收紧 `CourierWorkbench.vue` 对 bridge 的运行时使用边界

1. 评估范围：
   - `frontend/src/views/courier/CourierWorkbench.vue`
2. 想解决的问题：
   - 进一步缩小 repo 内对 `/api/campus/courier/profile` 和 `/api/campus/courier/review-status` 的桥接使用面
3. 为什么最终不执行：
   - 当前 workbench 已是 repo 内最小、最清晰的 bridge 观察面
   - 任何进一步收紧运行时行为，都会开始改变稳定链路的语义
   - Step 31 的正确目标是“确认是否值得动”，而不是“为了有动作就动”

### 候选 B：在 `campus-courier.js / request.js` 中做 bridge 边界的注释级显式化

1. 评估范围：
   - `frontend/src/api/campus-courier.js`
   - `frontend/src/utils/request.js`
2. 想解决的问题：
   - 让 bridge 使用边界在代码层更显式
3. 为什么最终不执行：
   - 这类改动不改变行为，回滚确实简单
   - 但对 `Phase A` 的实际推进收益过低
   - 当前文档里已经把边界、回滚和回归写清楚，再补一次注释只会形成“改了代码但没推进边界”的假动作

## 本轮最终结论

1. 本轮已完成真实执行前最小回归复核。
2. 本轮已评估最小 `Phase A` 动作候选。
3. 本轮最终结论：`暂不执行任何收口动作`。
4. 不执行的原因：
   - 当前不存在一个同时满足“有实际收益 + 风险足够小 + 单提交可回滚”的最小候选动作
5. 当前 bridge 结论保持不变：
   - 可以继续停留在 `Phase A` 执行准备阶段
   - bridge 仍完全保留
   - 尚未开始真正的收口动作

## 修改文件

1. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
2. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
3. `project-logs/campus-relay/summary.md`
4. `project-logs/campus-relay/pending-items.md`
5. `project-logs/campus-relay/file-change-list.md`
6. `project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md`
7. `project-logs/campus-relay/step-31-minimal-phase-a-candidate-and-preflight.md`

## 下一轮建议

1. 继续在 Step 30 的边界内寻找真正值得执行的最小 `Phase A` 候选动作。
2. 若仍找不到足够安全且有明确收益的动作，就继续保持 bridge 完全保留。
3. 不要为了推进阶段而制造低收益的注释型或无实质收益的代码改动。

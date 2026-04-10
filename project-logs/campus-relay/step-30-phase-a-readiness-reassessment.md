# Step 30 - Phase A 执行准备重新评估

## 本轮目标

1. 基于 Step 29 已关闭的 repo 外阻塞项，正式进入 `Phase A` 执行准备重新评估。
2. 明确 `Phase A` 的执行边界、bridge 保留范围、回滚策略和最小回归清单。
3. 不做业务代码改动，不删 bridge，不改鉴权，不补新页面。

## Step 29 带来的状态变化

1. 项目 owner 已明确确认：
   - 项目从未部署、从未发布
   - 不存在历史发布包
   - 当前项目唯一维护人就是 owner 本人
   - 当前没有团队
   - 不存在团队共享 `Postman / Apifox / 联调脚本`
   - 不存在仓库外旧页面副本、历史前端包或非 repo 管理的联调副本
2. 因此此前保守保留的 repo 外阻塞项已关闭。
3. 当前阶段判断从“不能进入 `Phase A` 执行准备”推进为“可以进入 `Phase A` 执行准备重新评估”。

## 为什么 repo 外阻塞现在可以视为关闭

1. Step 25 到 Step 28 追的 repo 外证据，本质是在验证是否仍存在：
   - 已部署旧页面
   - 历史发布包
   - 团队共享调试资产
2. Step 29 的 owner 明确确认表明这些前提本身不成立。
3. 因此 repo 外阻塞的结论已足够支撑进入 `Phase A` 执行准备重新评估。

## 为什么这仍不等于立即删 bridge

1. repo 外阻塞关闭，只说明可以开始设计执行动作，不说明可以直接实施收口。
2. repo 内当前仍有明确 bridge 保留场景：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
   - `/courier/workbench` 对这两个接口的读取
3. 当前主链路已经稳定，不能跳过边界、回滚和回归设计。

## Phase A 的执行边界

### Phase A 做什么

1. 固化 `Phase A` 的执行边界。
2. 固化 bridge 在 `Phase A` 期间的保留范围。
3. 固化回滚策略与触发条件。
4. 固化最小回归清单。
5. 统一 bridge 相关文档口径，为 Step 31 以后可能的最小执行动作做准备。

### Phase A 不做什么

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不做大规模鉴权收紧。
4. 不改 `frontend/src/utils/request.js` 的现有 token 附着逻辑。
5. 不改 repo 内业务代码。
6. 不补新页面、不补新接口。

## bridge 保留范围

1. `Phase A` 期间继续保留以下 bridge 接口：
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
2. `customer_token -> bridge -> courier 前置读取` 继续允许存在，但仅观察，不改变行为。
3. `/courier/workbench` 继续维持现有优先 `courier_token` 的策略。
4. `customer/courier-onboarding/*` 继续作为新的前置入口，不回退到旧入口。
5. `Phase A` 期间不因为“进入执行准备”而修改当前演示行为。

## 回滚策略

### 第一回滚点

1. 恢复到 Step 29 结束时的 bridge 保守状态。

### 回滚关键点

1. bridge 接口继续保留。
2. `frontend/src/utils/request.js` 现有 token 附着逻辑保留。
3. `frontend/src/views/courier/CourierWorkbench.vue` 现有读取行为保留。
4. `customer/courier-onboarding/*` 继续作为 customer 前置链路。

### 触发回滚的现象

1. `/courier/workbench` 无法稳定读取 `profile / review-status`。
2. pure `courier_token` 路径失稳。
3. `onboarding -> token -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报 -> customer confirm -> completed 回读` 任一关键链路回归失败。
4. customer 结果回看页的 `AWAITING_CONFIRMATION / COMPLETED` 结果异常。

### 回滚后的目标状态

1. repo 内现有链路恢复到 Step 29 结束时的工作方式。
2. bridge 继续保留。
3. 不引入新的行为变化。

## 最小回归清单

1. customer onboarding 提交资料
2. customer 查看审核状态
3. customer 申请 courier token
4. `/courier/workbench` 加载 `profile / review-status`
5. pure `courier_token` 路径稳定
6. 接单
7. 取餐
8. deliver
9. 异常上报
10. customer confirm
11. completed 回读
12. customer 结果回看页
13. 任何 bridge 收紧候选动作都不能破坏上述链路

## 执行前触发条件

只有同时满足以下条件，才允许从“执行准备重新评估”进入真正的执行动作：

1. `Phase A` 范围已明确。
2. bridge 保留范围已明确。
3. 回滚策略已明确。
4. 最小回归清单已明确。
5. 执行动作和非执行动作边界已明确。
6. bridge 相关文档口径已统一。

## 当前结论

1. 当前已经可以进入 `Phase A` 执行准备重新评估。
2. repo 外阻塞已关闭。
3. bridge 仍保留，且没有执行删除动作。
4. 当前真正缺的不是 repo 外证据，而是是否执行最小、可回滚的下一步动作。

## 修改文件

1. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
2. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
3. `project-logs/campus-relay/summary.md`
4. `project-logs/campus-relay/pending-items.md`
5. `project-logs/campus-relay/file-change-list.md`
6. `project-logs/campus-relay/step-29-owner-confirmation-and-bridge-reassessment.md`
7. `project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md`

## 下一轮建议

1. 进入 Step 31。
2. 在 Step 30 已定义好的边界内，评估是否执行最小、可回滚的 `Phase A` 动作。
3. 若要执行，优先从 repo 内调用边界最清晰、影响面最小的场景开始。
4. 第五个 admin 页继续后置。

## Step 31 衔接说明

1. `2026-04-10` 已按本轮固化的最小回归清单完成一轮真实执行前复核。
2. 复核结果显示 repo 内关键链路稳定，可继续停留在 `Phase A` 执行准备阶段。
3. Step 31 同时评估了最小动作候选，但最终结论是：
   - 本轮暂不执行任何收口动作
4. 暂不执行的原因不是链路失败，而是当前没有一个同时满足“有实际收益 + 风险足够小 + 单提交可回滚”的最小候选动作。

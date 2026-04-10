# Step 32 - `Phase A` 候选池扩展与 go / no-go 决策

## 本轮目标

1. 在 Step 31 已完成真实执行前复核的前提下，继续扩大一点点候选池。
2. 只允许在“最小、明确、可回滚”的范围内做 go / no-go 决策。
3. 如果找不到真正值得执行的候选，就明确记录继续 `no-op` 的理由，而不是为了推进阶段硬改代码。

## Step 31 留下的真实起点

1. Step 31 已真实复核本地完整链路，样本订单仍为 `CR202604070002`。
2. bridge 当前仍完全保留。
3. Step 31 已排除两类低质量推进方式：
   - 直接收紧 `CourierWorkbench.vue` 的 bridge 运行时边界，风险偏高
   - 只做注释级边界显式化，收益偏低
4. 因此 Step 32 不再重复“能不能先动 workbench 运行时行为”这类已经给出否定判断的问题，而是只扩大一点点候选池。

## 本轮重新评估的候选池

### 候选 A：repo 内 bridge 使用边界的行为不变型显式隔离

1. 评估范围：
   - `frontend/src/views/courier/CourierWorkbench.vue`
   - `frontend/src/api/campus-courier.js`
2. 目标：
   - 把 bridge 使用点的责任归属写得更清楚
   - 但不改变接口调用顺序、不改变 token 附着策略、不改变现有运行时行为
3. 预期收益：
   - 降低后续误把 bridge 读取当成“随手可删”的风险
   - 为真正的收口动作提供更明确的 repo 内边界
4. 风险：
   - 一旦隔离方式落到真实调用结构调整，就会开始触碰稳定链路语义
   - 当前 workbench 已经是 repo 内最小 bridge 使用面，进一步隔离的收益不高
5. 影响面：
   - 主要集中在 courier 前台读取链路
6. 回滚方式：
   - 单提交回滚
7. 结论：
   - 本轮不执行
   - 原因：仍然没有形成足够高收益的行为不变候选，且任何再往前走一步的隔离都容易开始动到运行时结构

### 候选 B：把 Step 30 / Step 31 的执行边界转成代码旁证级约束

1. 评估范围：
   - `frontend/src/api/campus-courier.js`
   - `frontend/src/utils/request.js`
3. 目标：
   - 在 repo 内用最小的边界说明，降低后续误改风险
4. 预期收益：
   - 帮助下一轮执行者理解“哪些点不能随便收紧”
5. 风险：
   - 运行时风险几乎没有
6. 影响面：
   - 只在注释、说明或最小边界常量层
7. 回滚方式：
   - 单提交回滚
8. 结论：
   - 本轮不执行
   - 原因：收益仍低于其维护成本，本质上仍属于“文档已足够清晰，但代码里多一层提示并没有明显推进 Phase A”

### 候选 C：继续保持 `no-op`

1. 适用条件：
   - 候选 A 风险仍偏高
   - 候选 B 收益仍偏低
   - 当前没有第三种更安全、又更有收益的候选
2. 收益：
   - 保持当前稳定链路
   - 避免为了阶段推进制造低价值代码噪音
   - 保留 Step 30 / Step 31 已经明确的边界和回滚点
3. 风险：
   - `Phase A` 继续停留在准备阶段，推进速度慢
4. 回滚方式：
   - 无需回滚，因为本轮不执行代码动作
5. 结论：
   - 本轮采用候选 C

## go / no-go 决策

1. 本轮最终结论：`no-go`
2. 本轮不执行任何 `Phase A` 代码动作。
3. 原因：
   - 当前没有一个同时满足“有明确收益 + 风险足够小 + 单提交可回滚”的最小候选
   - 继续保持 bridge 完全保留，是当前最优保守策略

## 为什么继续 `no-op` 更合理

1. 收益：
   - 保持 Step 31 已验证稳定的链路不受影响
   - 避免把 `Phase A` 变成“为了推进阶段而制造的低收益代码动作”
   - 保持回滚点简单清楚
2. 代价：
   - bridge 仍完全保留
   - repo 内候选动作尚未进入真实执行阶段
3. 但在当前阶段，这个代价低于“为做而做”引入的误伤风险。

## 当前 bridge 结论

1. bridge 仍完全保留。
2. 当前仍处于 `Phase A` 执行准备阶段。
3. Step 32 的结论不是失败，而是：
   - 候选池已扩展并重新评估
   - 当前继续 `no-op` 是更合理的保守决策
   - bridge 仍不能写成“已可删除”

## 修改文件

1. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
2. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
3. `project-logs/campus-relay/summary.md`
4. `project-logs/campus-relay/pending-items.md`
5. `project-logs/campus-relay/file-change-list.md`
6. `project-logs/campus-relay/step-31-minimal-phase-a-candidate-and-preflight.md`
7. `project-logs/campus-relay/step-32-phase-a-candidate-expansion-and-go-no-go.md`

## 下一轮建议

1. Step 33 如果继续推进，应优先寻找“比候选 B 更有实际收益、但比候选 A 更安全”的中间型候选。
2. 若仍找不到，就继续保持 `Phase A no-op`，不要为了推进阶段而强行动代码。

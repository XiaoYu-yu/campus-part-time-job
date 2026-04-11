# Step 43 - 媒体缺口分叉判断 / 非 bridge 后端评估入口

## 本轮目标

1. 基于 Step 42 的真实媒体采集结果，判断是否还必须补 after-sale 固定真实样本媒体。
2. 如果 after-sale 固定样本不是答辩或交接必须项，则正式收住媒体线。
3. 在不写代码、不改接口、不补页面的前提下，评估下一条非 bridge 后端主线。

## 为什么 Step 43 先做分叉判断

Step 42 已经完成 15 张真实截图与 5 段真实录屏，覆盖 customer/courier 主链路、customer completed 回读、admin settlement 只读运营页和 admin after-sale 只读运营页。当前只剩两个媒体层面的注意点：

1. after-sale 固定真实样本不是稳定样本。
2. customer confirm 没有单独 UI 按钮截图，但已有真实接口录屏留痕。

如果继续补媒体，必须证明 after-sale 固定样本是答辩或交接的硬要求。否则继续停留在媒体线会拖慢下一条非 bridge 后端能力梳理。

## 分叉判断

结论：本轮选择路径 B，不补固定 after-sale 样本，不补新媒体。

判断依据：

| 判断项 | 结论 |
| --- | --- |
| 现有 5 个关键页面和 5 段媒体是否覆盖主链路 | 已覆盖 onboarding、token、workbench、接单、取餐、deliver、异常上报、customer confirm、completed 回读、settlement 和 after-sale 只读页 |
| after-sale 在当前演示中的权重 | after-sale 是 admin 只读运营补充线，不是 customer/courier 主闭环的必经步骤 |
| 无固定 after-sale 样本时是否足够展示 | 当前 after-sale 页面截图与录屏可展示页面结构、筛选区、只读说明和运营入口；固定数据不是当前答辩硬阻塞 |
| 补 after-sale 样本成本 | 需要额外准备售后申请、admin 决策、执行记录和回读状态，容易把 Step 43 拖回媒体循环 |
| 补 after-sale 样本风险 | 会引入新的可变 H2 运行态和单独采集流程；收益低于进入后端能力评估 |
| 是否值得继续停留在交付媒体线 | 不值得；媒体材料已经基本足够 |

因此：Step 43 正式收住媒体线，转入非 bridge 后端能力梳理评估。

## 路径 B：非 bridge 后端能力梳理评估

本轮只评估，不写代码、不补表、不补接口、不补页面。

### 方向一：售后执行历史表

| 维度 | 评估 |
| --- | --- |
| 当前痛点 | 目前售后执行结果主要落在订单当前字段与一次纠正审计字段上，缺少多次执行、失败重试、人工纠正的完整时间序列 |
| 业务收益 | 可以提升售后可追责能力，支持 admin 展示执行记录、纠正原因、执行人和时间线 |
| 与当前最小闭环关系 | 当前 after-sale 执行分页页已经能读当前结果；历史表属于增强审计，不是主链路必需 |
| 风险 | 需要新增表、Mapper、Service、只读查询接口和测试；如果顺手补写操作，容易扩大范围 |
| 开发复杂度 | 中等偏高 |
| 是否适合作为下一主线 | 可作为候选，但不是最高优先级 |
| 优先级 | P2 |

判断：售后执行历史表有审计价值，但当前 after-sale 页面在演示中权重低于异常上报闭环；建议后置到异常历史之后。

### 方向二：异常历史与处理闭环

| 维度 | 评估 |
| --- | --- |
| 当前痛点 | 当前异常只保留订单上的最新一次异常，courier workbench 已能上报异常，但 admin 侧只能看到最近异常，无法查看历史、处理状态和处理结果 |
| 业务收益 | 能把 courier 异常上报从“最新一次字段”升级为真正可追踪的运营闭环，支持异常列表、异常详情、处理记录和责任追踪 |
| 与当前最小闭环关系 | 当前主链路已经包含异常上报；补历史与处理闭环能直接增强已演示链路，而不触 bridge |
| 风险 | 需要设计异常历史表、最小处理状态、admin 只读/处理接口和测试；必须避免过度扩成工单系统 |
| 开发复杂度 | 中等 |
| 是否适合作为下一主线 | 适合；收益明确、边界比 settlement 财务能力更可控 |
| 优先级 | P1 |

判断：异常历史与处理闭环最适合作为 Step 44 的非 bridge 后端主线入口。它直接承接 courier workbench 的异常上报动作，也能增强 admin courier 运营页的真实性。

### 方向三：settlement 批次复核、撤回和对账

| 维度 | 评估 |
| --- | --- |
| 当前痛点 | settlement 当前已有记录、批次、打款标记和二次核对，但没有撤回、复核流转、复杂对账差异和真实财务执行 |
| 业务收益 | 能提升财务后台完整性，支持更真实的结算运营与对账流程 |
| 与当前最小闭环关系 | 当前 completed 后 settlement 已能自动生成并在 admin 页只读展示；更深财务能力不是主链路阻塞 |
| 风险 | 财务语义重，容易牵涉真实打款、撤回、对账差异表和复杂状态机 |
| 开发复杂度 | 高 |
| 是否适合作为下一主线 | 暂不适合作为下一主线，除非交付目标明确转向财务运营 |
| 优先级 | P3 |

判断：settlement 深化价值高，但风险和复杂度最高；当前不应在媒体线刚收住后立即进入财务扩展。

## 优先级排序

1. P1：异常历史与处理闭环。
2. P2：售后执行历史表。
3. P3：settlement 批次复核、撤回和对账。

推荐 Step 44 先进入“异常历史与处理闭环”的最小方案设计轮，不直接写代码。

## 当前媒体材料是否已足够

结论：已足够用于当前答辩和交接。

理由：

1. 主链路媒体已经覆盖从 onboarding 到 completed 回读的完整闭环。
2. admin settlement 与 after-sale 两个只读运营页已都有真实截图和录屏。
3. after-sale 固定样本不是当前主链路硬阻塞。
4. 异常后 customer confirm 被拒绝的失败留痕已保留，证明媒体采集没有伪造成通过。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮没有改 bridge、没有改鉴权、没有改 token 附着逻辑。
4. 当前最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `project-logs/campus-relay/summary.md`
2. `project-logs/campus-relay/pending-items.md`
3. `project-logs/campus-relay/file-change-list.md`
4. `project-logs/campus-relay/step-42-real-media-capture-and-archive.md`
5. `project-logs/campus-relay/step-43-media-gap-gate-or-non-bridge-backend-assessment.md`

## 下一轮建议

1. Step 44 推荐进入“异常历史与处理闭环”的最小方案设计轮。
2. Step 44 仍不应直接写代码，先明确表边界、状态边界、接口边界、与现有最新异常字段的兼容策略。
3. bridge 主线继续冻结。
4. 展示 polish 线继续冻结。
5. 第五个 admin 页继续后置，不以补页数为目标。

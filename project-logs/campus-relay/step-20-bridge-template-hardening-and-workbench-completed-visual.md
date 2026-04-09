# Step 20 - bridge 模板可执行化 / courier workbench completed 后最小承接

## 本次目标

1. 继续推进 bridge 执行准备，但不伪造任何 repo 外依赖结果或人工核实结果。
2. 把 `bridge-execution-readiness-checklist.md` 与 `bridge-regression-template.md` 补成可真正执行、可真正填写的模板。
3. 为 courier workbench 在 confirm 前之后继续补一个 completed 后的最小只读承接点。
4. 保持旧系统可运行，不切旧前端主链路，不新增 backend 写接口。

## 已完成项

1. 更新 `bridge-phaseout-evaluation.md`，把 Step 20 结论写实为：
   - 模板已经可执行
   - 但真实人工核实结果仍待补齐
   - 当前仍不具备进入 `Phase A` 执行准备的完整条件
2. 重写 `bridge-execution-readiness-checklist.md`，为每个待核实项新增：
   - 推荐执行入口
   - 推荐证据类型
   - 失败时如何记录
   - 是否影响进入 `Phase A`
3. 重写 `bridge-regression-template.md`，为每个联调步骤新增：
   - 接口观察点
   - 关键状态字段
   - 失败记录建议
   - 是否为阻塞项
4. 保持所有 checklist 与模板为“待填写”状态，没有代填任何人工核实结论或联调结果。
5. 在 `CourierWorkbench.vue` 的订单详情 drawer 中补齐送达后最小只读承接：
   - `AWAITING_CONFIRMATION` 时展示等待用户确认态
   - `COMPLETED` 时展示明确的已完成摘要态
   - 继续只读展示真实后端已有字段，不臆造 customer confirm 时间等字段
6. 更新总览日志、待处理事项和文件改动清单。

## 修改文件

1. `frontend/src/views/courier/CourierWorkbench.vue`
2. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
3. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
4. `project-logs/campus-relay/bridge-regression-template.md`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`
8. `project-logs/campus-relay/step-20-bridge-template-hardening-and-workbench-completed-visual.md`

## 当前遗留问题

1. repo 外或历史调用对旧 bridge 的依赖仍未核实完成。
2. checklist 和 regression template 现在可执行，但还没有真实填写结果。
3. current repo 仍不具备进入 `Phase A` 执行准备的完整条件。
4. courier workbench 目前只补到 completed 后最小只读承接，还没有更完整的 confirm 后回读承接。
5. 本轮没有补第五个 admin 页。

## 下一步建议

1. 按 checklist 和 regression template 跑第一轮真实人工核实与联调留痕。
2. 若继续扩 courier 前端，优先补 completed 后或 customer confirm 结果回读承接。
3. 在 repo 外依赖确认和回归记录补齐前，继续保留旧 bridge，不做删除动作。

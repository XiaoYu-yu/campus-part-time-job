# Step 86 - 试运营脚本线收口 / no-op 冻结判断

## 本轮目标

1. 基于 Step 83 到 Step 85 已完成的 preflight、样本校验和命令索引，判断试运营脚本线是否继续扩展。
2. 明确是否需要继续补只读 runtime smoke、自动 H2 reset、长驻进程管理或更多脚本入口。
3. 不改业务代码、不改 bridge、不改接口、不改路由、不改鉴权、不新增页面。

## 当前起点

Step 83 到 Step 85 已经形成三类本地试运营入口：

1. `scripts/trial-operation/preflight.ps1`
   - 检查关键文件、本地地图 key 变量、`.env.local` git 跟踪状态。
   - 可选检查端口、backend compile、frontend build。
2. `scripts/trial-operation/validate-samples.ps1`
   - 只读校验 H2 seed / schema 的关键样本和表结构锚点。
   - 对可选运行态样本保留 warning，不把非必需运行态样本写成硬失败。
3. `scripts/trial-operation/commands.ps1`
   - 输出本地试运营常用命令、浏览器入口和手动 H2 reset 说明。
   - 只打印命令，不启动长驻进程、不 kill 进程、不自动 reset H2。

## 本轮评估过的继续增强方向

### 方向 A：继续补只读 runtime smoke

收益：

1. 可以进一步把浏览器页面可访问性收敛成脚本化检查。
2. 能减少人工打开页面的成本。

风险：

1. 需要依赖 backend / frontend 已经运行，脚本容易和本地运行状态耦合。
2. 如果加入 token 登录、页面状态、样本订单等真实链路检查，很容易从“运维化入口”扩大成端到端自动化测试。
3. 当前已有 Step 77、Step 78、Step 79 和 Step 84 的验证留痕，继续补 runtime smoke 的边际收益不高。

结论：

当前不进入实现。只在后续出现真实重复手工检查成本、交付要求或 CI 化需求时再重新评估。

### 方向 B：自动 H2 reset

收益：

1. 可以降低重置 test profile 样本状态的人工步骤。

风险：

1. 可能误杀正在演示的 backend 进程。
2. 可能误清正在验证的运行态样本。
3. 当前 `test profile + in-memory H2` 已经可以通过停止 backend 再启动完成 reset，手动路径足够可控。

结论：

继续 no-op。保留手动 H2 reset 指南，不写自动 reset 脚本。

### 方向 C：长驻进程管理

收益：

1. 可以把 backend / frontend 启停进一步脚本化。

风险：

1. 会把当前脚本线从“检查和索引”扩大成进程编排。
2. Windows 本地开发环境差异较大，容易引入误杀、端口占用和后台进程残留问题。
3. 这不是当前试运营 RC 的必要能力。

结论：

继续 no-op。当前脚本只提供命令索引，不接管进程生命周期。

## 最终判断

试运营脚本线进入 no-op 冻结 / 维护态。

理由：

1. 当前脚本能力已经覆盖试运营前最关键的文件检查、地图 key 本地变量检查、样本锚点校验、构建命令入口和手动 H2 reset 口径。
2. 继续扩到 runtime smoke、自动 reset 或进程管理，会明显增加环境耦合和误操作风险。
3. 当前项目仍处于本地试运营和答辩交付口径，不需要提前做生产级运维编排。
4. 更高价值的下一步不再是继续补脚本，而是从项目整体角度评估产品级试运营前的剩余差距。

## 保持不动项

1. 不改 bridge。
2. 不改 `/api/campus/courier/profile`。
3. 不改 `/api/campus/courier/review-status`。
4. 不改 `request.js`。
5. 不改 token 附着逻辑。
6. 不改后端接口、路由或鉴权。
7. 不新增页面。
8. 不自动 reset H2。
9. 不启动或停止长驻进程。
10. 不打印真实腾讯地图 key。

## 重新打开脚本线的触发条件

只有出现以下真实信号时，才建议重新打开试运营脚本线：

1. 手工启动和检查步骤在演示或交接中反复出错。
2. 需要接入 CI 或固定的本地一键验证流程。
3. 试运营环境从单人本地演示升级为多人共同使用。
4. 后续引入部署环境，需要明确的 smoke check。
5. 样本数据形态变化，导致现有 `validate-samples.ps1` 无法覆盖关键锚点。

## 验证结果

本轮执行：

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1 -Full`
   - 结果：通过。
   - 说明：命令索引可正常输出。
2. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation`
   - 结果：通过。
   - 说明：必需项通过；可选运行态样本保留 warning。
3. `git diff --check`
   - 结果：通过。
   - 说明：仅保留 Windows 工作区 LF -> CRLF 提示，无空白错误。

本轮未执行 backend compile 或 frontend build，因为未改 Java、Vue、构建配置或依赖；Step 84 已验证完整 preflight 可串联 backend compile 与 frontend build。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 本轮只是试运营脚本线收口判断，不触碰 bridge 行为。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 87 建议转入“产品级试运营前剩余差距清单 / go-no-go 评估”：

1. 不再默认继续补脚本。
2. 不再默认继续 polish 页面。
3. 不再默认继续扩地图、媒体、前端打包或 bridge。
4. 从产品级试运营角度梳理仍缺的安全、配置、部署、监控、数据备份、模拟资金边界和真实公司能力替代项。
5. 若只做本地答辩交付，则可保持当前 RC + 脚本线 no-op 状态。

## Step 87 回填

Step 87 已完成该建议：

1. 已确认当前本地答辩 / 交付 RC 足够。
2. 已梳理产品级试运营前仍缺的环境、密钥、部署、CI、监控、备份和外部服务降级能力。
3. 下一主线建议转入试运营环境与密钥配置硬化 / deployment preflight 准备。

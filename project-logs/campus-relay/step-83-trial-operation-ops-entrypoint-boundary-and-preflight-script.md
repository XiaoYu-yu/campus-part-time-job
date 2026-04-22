# Step 83 - 试运营运维化最小能力边界收敛 / preflight 脚本入口

## 本轮目标

1. 基于 Step 82 选择的“试运营运维化最小能力”主线，先落一个低风险、可复用、可交接的本地操作入口。
2. 把现有 trial-operation 文档中的部分检查收敛为可执行脚本。
3. 不重开 bridge，不继续页面 polish，不扩业务功能，不做 H2 自动重置或长驻进程启动。

## 为什么本轮先做 preflight 脚本

当前项目已经进入试运营 RC 后的维护阶段，主要问题不再是“缺少一个页面或接口”，而是每次演示 / 试运营前仍需要手工按文档确认：

1. backend / frontend 基础文件是否还在。
2. test profile + H2 关键配置是否可用。
3. 本地地图 key 是否存在但不被提交。
4. 是否需要执行 backend compile / frontend build。
5. 本地 8080 / 5173 端口是否已经启动。

因此本轮选择先做 `preflight.ps1`，它比继续写文档更可执行，又比自动启动 / 重置数据更安全。

## 实际新增内容

### `scripts/trial-operation/preflight.ps1`

能力：

1. 检查关键项目文件存在性。
2. 检查 `frontend/.env.local` 是否存在 `VITE_TENCENT_MAP_KEY`，但不打印真实 key。
3. 检查 `frontend/.env.local` 未被 git 跟踪。
4. 可选检查 `127.0.0.1:8080` 和 `127.0.0.1:5173` 端口。
5. 可选执行 backend `.\mvnw.cmd -DskipTests compile`。
6. 可选执行 frontend `npm run build`。

边界：

1. 不自动重置 H2。
2. 不启动 backend / frontend 长驻进程。
3. 不修改业务数据。
4. 不修改 bridge、鉴权、接口、路由、页面行为。
5. 端口检查默认是 warning；只有显式加 `-StrictPorts` 才作为硬失败。

### `scripts/trial-operation/README.md`

记录脚本用途、边界和常用命令。

### `docs/trial-operation-preflight.md`

新增“本地 preflight 脚本入口”说明，保留原有手工 runbook。

## 本轮没有做什么

1. 没有新增 H2 reset 脚本：当前 `test profile` 使用内存 H2，进程重启即回到 seed 数据；自动 reset 容易误伤运行中的演示链路。
2. 没有新增启动脚本：backend / frontend 启动仍建议由开发者明确打开终端运行，避免脚本隐藏长驻进程和端口占用。
3. 没有新增样本写入脚本：当前 H2 seed 与运行态验证样本已经足够支撑 RC 演示；继续写自动造数会扩大数据语义风险。
4. 没有改 bridge、接口、路由、鉴权、前端页面或后端业务代码。

## 验证结果

已执行：

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1`
   - 结果：通过。
   - 覆盖：关键文件存在性、`frontend/.env.local` key 变量存在性、`.env.local` 未被 git 跟踪。
   - 说明：脚本确认本地腾讯地图 key 变量存在，但未打印真实 key。
2. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunBackendCompile -RunFrontendBuild`
   - 结果：通过。
   - backend `.\mvnw.cmd -DskipTests compile`：通过。
   - frontend `npm run build`：通过。
   - preflight 总结：`0 hard failure(s)`，`0 warning(s)`。
3. `git diff --check`
   - 结果：通过。
   - 说明：仅出现既有 Windows 工作区 LF -> CRLF 提示，无空白错误。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 本轮只是试运营运维入口收敛，不触碰 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 或 token 附着逻辑。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 84 已按建议继续沿着“试运营运维化最小能力”推进，并落地为只读样本状态校验脚本：

1. 新增 `scripts/trial-operation/validate-samples.ps1`。
2. `preflight.ps1` 新增 `-RunSampleValidation` 参数。
3. 样本校验只读取 H2 schema / seed，不连接数据库、不写入数据、不重置 H2。

Step 85 建议继续保持克制：

1. 先评估是否需要一个显式的“启动命令索引”或“手动 H2 reset 说明”，而不是直接自动化重置数据库。
2. 若继续脚本化，优先做只读 runtime smoke 或命令聚合。
3. 仍不重开 bridge、页面 polish、地图、媒体或复杂业务功能线。

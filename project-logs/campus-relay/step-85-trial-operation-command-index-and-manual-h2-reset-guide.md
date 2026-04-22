# Step 85 - 试运营命令索引与手动 H2 reset 指南

## 本轮目标

1. 继续推进“试运营运维化最小能力”主线。
2. 新增一个无副作用的命令索引入口，帮助快速查看本地试运营常用命令。
3. 固化手动 H2 reset 口径，但不提供自动 reset 脚本。
4. 不重开 bridge、页面 polish、地图、媒体、前端打包或复杂业务功能线。

## 为什么本轮不做自动 reset 脚本

当前 `test profile` 使用内存 H2，backend 进程重启即可重新加载：

1. `backend/src/main/resources/db/schema-h2.sql`
2. `backend/src/main/resources/db/data-h2.sql`

如果做自动 reset，很容易引入两个问题：

1. 误杀正在演示的 backend 进程。
2. 误清正在验证中的运行态样本。

因此本轮只固化“手动 reset 指南”，不做自动 kill / reset。

## 实际新增内容

### `scripts/trial-operation/commands.ps1`

能力：

1. 输出轻量 preflight 命令。
2. 输出带样本校验的 preflight 命令。
3. 输出完整 backend compile + frontend build + sample validation 命令。
4. 输出 backend test profile 启动命令。
5. 输出 frontend dev server 启动命令。
6. 输出手动 H2 reset 说明。
7. 加 `-Full` 时输出浏览器入口清单。

边界：

1. 不启动 backend。
2. 不启动 frontend。
3. 不 kill 进程。
4. 不 reset H2。
5. 不写入数据。
6. 不改 bridge、鉴权、接口、路由或页面行为。

### 文档同步

1. `scripts/trial-operation/README.md` 增加 command index 用法。
2. `docs/trial-operation-preflight.md` 增加命令索引和手动 H2 reset 说明。

## 验证结果

已执行：

1. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1 -Full`
   - 结果：通过。
   - 已输出 preflight、backend test profile、frontend dev server、手动 H2 reset 和浏览器入口清单。
2. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation`
   - 结果：通过。
   - 必需项：通过。
   - 可选运行态样本：保留 warning。
   - preflight 总结：`0 hard failure(s)`、`1 warning(s)`。
3. `git diff --check`
   - 结果：通过。
   - 说明：仅出现 Windows 工作区 LF -> CRLF 提示，无空白错误。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 本轮只是本地试运营命令入口整理，不触碰 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 或 token 附着逻辑。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 86 建议继续沿着试运营运维化方向做 go / no-go：

1. 若命令索引与样本校验已足够，评估是否冻结试运营脚本线。
2. 若仍需增强，只考虑只读 runtime smoke，不做自动造数、自动 reset 或长驻进程管理。
3. 继续保持 bridge、展示 polish、地图、媒体、前端打包和非 bridge 业务功能线冻结 / 收住。

## Step 86 回填

Step 86 已完成该建议：

1. 试运营脚本线已判断进入 no-op 冻结 / 维护态。
2. 当前不继续追加 runtime smoke、自动 H2 reset 或长驻进程管理。
3. 下一轮建议转入产品级试运营前剩余差距清单 / go-no-go 评估，而不是继续补脚本。

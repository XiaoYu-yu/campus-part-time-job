# Step 84 - 试运营样本状态校验脚本

## 本轮目标

1. 沿着 Step 83 的“试运营运维化最小能力”继续推进。
2. 新增一个只读样本状态校验入口，用于确认 H2 seed / schema 中的关键演示锚点仍存在。
3. 不自动重置 H2，不连接数据库，不写入数据，不启动长驻进程。
4. 不重开 bridge、展示 polish、媒体、地图或复杂业务功能线。

## 为什么本轮做样本校验脚本

Step 83 已经把本地 preflight 做成可执行入口，但它主要检查文件、key、端口和构建。试运营演示还依赖一组固定样本：

1. admin / customer / courier 账号。
2. 可接单订单 `CR202604070002`。
3. completed 回读订单 `CR202604060001`。
4. settlement 与 location report 样本。
5. 异常、售后执行、对账差异这些后续能力对应的 schema。

这些样本以前只能人工翻 `data-h2.sql` 和 `schema-h2.sql` 确认。本轮将它们收敛成只读脚本检查。

## 实际新增内容

### `scripts/trial-operation/validate-samples.ps1`

能力：

1. 读取 `backend/src/main/resources/db/schema-h2.sql`。
2. 读取 `backend/src/main/resources/db/data-h2.sql`。
3. 校验 admin / customer / courier seed 账号。
4. 校验 `CR202604070002` 是可接单样本锚点。
5. 校验 `CR202604060001` 是 completed 样本锚点。
6. 校验位置上报和 settlement 样本锚点。
7. 校验异常历史、售后执行历史、settlement 对账差异的 H2 schema 存在。
8. 对非预置的运行态样本给 warning，而不是伪装成已预置。

退出码：

1. `0`：必需项和可选项都通过。
2. `1`：存在硬失败。
3. `2`：必需项通过，但存在可选样本 warning。

### `scripts/trial-operation/preflight.ps1`

新增 `-RunSampleValidation` 参数：

1. 调用 `validate-samples.ps1`。
2. 将 `validate-samples.ps1` 的退出码 `2` 识别为 warning，而不是硬失败。
3. 保持 preflight 本身退出码为 `0`，前提是没有硬失败。

### `scripts/trial-operation/README.md`

补充样本校验脚本用法、校验范围和退出码说明。

### `docs/trial-operation-preflight.md`

补充：

1. `-RunSampleValidation` 参数说明。
2. `validate-samples.ps1` 独立运行示例。
3. 样本校验只读边界。
4. 演示前检查清单中的样本锚点校验入口。

## 本轮验证结果

### 独立样本校验

命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1
```

结果：

1. 必需锚点通过：
   - admin seed account
   - customer seed account
   - courier seed account
   - approved courier profile seed
   - available order `CR202604070002`
   - completed order `CR202604060001`
   - location report seed
   - settlement seed
   - exception history schema
   - after-sale execution history schema
   - settlement reconcile difference schema
2. 可选运行态样本 warning：
   - after-sale fixed sample 未预置。
   - exception fixed sample 未预置。
   - reconcile difference fixed sample 未预置。
3. 退出码为 `2`，表示必需项通过但存在 warning。

### preflight 集成校验

命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation
```

结果：

1. preflight 必需项通过。
2. `validate-samples.ps1` 被正确调用。
3. sample validation warning 被 preflight 识别为 warning。
4. preflight 总结为 `0 hard failure(s)`、`1 warning(s)`。
5. preflight 退出码为 `0`。

### preflight + 构建 + 样本校验

命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation -RunBackendCompile -RunFrontendBuild
```

结果：

1. backend `.\mvnw.cmd -DskipTests compile`：通过。
2. frontend `npm run build`：通过。
3. 样本校验必需项：通过。
4. 样本校验可选运行态样本：保留 3 个 warning。
5. preflight 总结为 `0 hard failure(s)`、`1 warning(s)`。

## 本轮没有做什么

1. 没有自动重置 H2。
2. 没有连接 H2 / MySQL 数据库。
3. 没有写入样本数据。
4. 没有新增后端接口。
5. 没有修改前端页面、路由、鉴权、bridge、token 附着或业务语义。
6. 没有把 optional runtime sample warning 伪装成通过。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 本轮只做本地试运营样本校验脚本，不触碰 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 或 token 附着逻辑。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 85 建议继续保持“试运营运维化最小能力”方向，但仍不做隐式副作用脚本：

1. 评估是否需要“启动命令索引”或“手动 H2 reset 指南”。
2. 若要继续脚本化，优先做只读 runtime smoke 或命令聚合，不直接写自动造数 / 自动重置。
3. bridge、展示 polish、地图、媒体、前端打包和非 bridge 业务扩展线继续保持冻结 / 收住口径。

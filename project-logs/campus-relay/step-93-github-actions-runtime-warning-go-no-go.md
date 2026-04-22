# Step 93 - GitHub Actions runtime warning 处理 go / no-go

## 本轮目标

1. 基于 Step 92 记录到的 GitHub Actions Node.js runtime deprecation warning，评估是否需要处理。
2. 只允许处理 GitHub 官方 action 版本，不改 CI 检查边界。
3. 不改 Java / Node 项目版本、不改业务代码、不改部署流程、不改 bridge。

## 当前起点

Step 92 已确认：

1. `Trial Operation CI #1` 由 `450e823ec35f22dba9463c71e1b85b854b1aa6e5` 的 push 触发。
2. `Backend compile`、`Frontend build`、`Trial sample validation` 三个 job 均成功。
3. 远端 CI 与本地等价验证一致。
4. 唯一观察项是 GitHub Actions 页面提示 Node.js runtime deprecation warning。

## 官方版本核查

核查官方 action README 后确认：

1. `actions/checkout`
   - 当前官方 README 已示例 `actions/checkout@v6`。
   - README 说明新版更新到 `node24` runtime。
2. `actions/setup-node`
   - 当前官方 README 已示例 `actions/setup-node@v6`。
   - README 示例仍支持显式设置 `node-version`。
3. `actions/setup-java`
   - 当前官方 README 已示例 `actions/setup-java@v5`。
   - README 示例包含 Maven cache 场景。

## go / no-go 判断

结论：go。

原因：

1. 这是典型 CI 维护型变更，收益明确：消除或降低当前 GitHub Actions runtime deprecation warning。
2. 改动边界足够小，只涉及 `.github/workflows/trial-operation-ci.yml` 中官方 action 版本。
3. 不改变 CI job 数量、触发条件、命令、环境变量或 sample validation warning 策略。
4. 不改变项目运行时 Java / Node 版本：
   - backend 仍使用 Java 17。
   - frontend 仍使用 Node 20 构建。
5. 回滚简单，单提交 revert 即可恢复到 Step 91 的 workflow 版本。

## 实际变更

只更新官方 action 版本：

1. `actions/checkout@v4` -> `actions/checkout@v6`
2. `actions/setup-java@v4` -> `actions/setup-java@v5`
3. `actions/setup-node@v4` -> `actions/setup-node@v6`

保持不变：

1. workflow 名称不变。
2. push / pull_request 触发条件不变。
3. `permissions: contents: read` 不变。
4. backend compile 命令不变。
5. frontend `node-version: "20"` 不变。
6. `VITE_TENCENT_MAP_KEY=ci-placeholder-key` 不变。
7. sample validation 的 exit code `2` warning-only 策略不变。

## 明确没有做

1. 没有新增 CI job。
2. 没有新增部署。
3. 没有新增 E2E。
4. 没有注入真实密钥。
5. 没有改业务代码、后端接口、前端页面、路由、鉴权或 token 附着。
6. 没有改 bridge。
7. 没有接真实支付、真实退款、真实打款。

## 本地验证

本轮本地验证：

1. `git diff --check`
   - 结果：通过。
2. `.\mvnw.cmd -DskipTests compile`
   - 结果：通过。
3. `npm run build`
   - 结果：通过。
4. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1`
   - 结果：exit code `2`，仅 warning，无 hard failure。

说明：

1. 本地验证无法直接执行 GitHub-hosted runner 的 action runtime。
2. 真正的 warning 消除效果需要等待本次提交 push 后的 GitHub Actions 运行结果。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 本轮 CI action 版本升级不改变 bridge 保留策略。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 94 建议做“CI action 版本升级后远端运行结果跟踪”：

1. 如果本次提交成功 push，观察 `Trial Operation CI` 新一轮运行。
2. 确认三个 job 是否仍全部通过。
3. 确认 Node.js runtime deprecation warning 是否消除或减少。
4. 若 CI 失败，只回滚或修正 action 版本，不扩业务功能。

## Step 94 回填

Step 94 已完成该跟踪：

1. `Trial Operation CI #2` 已由 `2406c1b2586996a1e0fdea1946394022894b3b0e` 的 push 触发。
2. `Backend compile`、`Frontend build`、`Trial sample validation` 三个 job 均成功。
3. 最新 run 摘要页中未再观察到显性 runtime deprecation warning 文本。
4. 因此 CI runtime warning 处理线可收住，不再构成当前试运营阻塞。

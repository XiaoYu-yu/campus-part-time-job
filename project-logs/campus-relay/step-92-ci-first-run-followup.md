# Step 92 - CI 首轮运行结果跟踪 / 本地与远端一致性复核

## 本轮目标

1. 基于 Step 91 已推送的 `.github/workflows/trial-operation-ci.yml`，核查 GitHub Actions 首次运行结果。
2. 对比本地等价验证与远端 CI 结果是否一致。
3. 如果 CI 失败，只记录 workflow / 环境差异修复入口，不扩业务功能。
4. 不改 bridge、不改鉴权、不改接口、不改路由、不改业务代码。

## 核查对象

- commit：`450e823ec35f22dba9463c71e1b85b854b1aa6e5`
- commit message：`ci: add trial operation checks`
- workflow：`Trial Operation CI`
- 运行链接：`https://github.com/XiaoYu-yu/campus-part-time-job/actions/runs/24759086379`

## 核查方式

1. 本地确认：
   - `git status -sb`
   - `git rev-parse HEAD`
2. GitHub 连接器确认：
   - `fetch_commit` 确认远端已有 `450e823ec35f22dba9463c71e1b85b854b1aa6e5`
   - `get_commit_combined_status` 返回空 status，说明没有传统 commit status 上报
   - `fetch_commit_workflow_runs` 未返回 push run，因为该连接器接口当前只覆盖 PR 触发场景
3. GitHub Actions 页面确认：
   - `Trial Operation CI #1` 由 `450e823` 的 push 触发
   - 3 个 job 均完成成功

## 远端 CI 结果

GitHub Actions 首次运行结果：

1. `Backend compile`
   - 状态：成功
   - 对应 Step 91 本地验证：`.\mvnw.cmd -DskipTests compile` 通过
2. `Frontend build`
   - 状态：成功
   - 对应 Step 91 本地验证：`npm ci && npm run build` 通过
3. `Trial sample validation`
   - 状态：成功
   - 对应 Step 91 本地验证：`validate-samples.ps1` 返回 exit code `2`，仅 warning，无 hard failure
   - 远端 workflow 已按设计把 warning exit code `2` 转为 CI 绿色

## 本地与远端一致性

结论：一致。

1. 本地 backend compile 通过，远端 backend compile 通过。
2. 本地 frontend build 通过，远端 frontend build 通过。
3. 本地 sample validation 为 warning-only，远端 sample validation 按 warning-only 保持通过。
4. 当前最小 CI 边界符合 Step 90 / Step 91 的设计：只验证编译、构建和样本锚点，不部署、不跑 E2E、不注入真实密钥。

## 观察到的非阻塞 warning

GitHub Actions 页面显示 Node.js runtime deprecation warning：

1. warning 指向 `actions/checkout@v4`、`actions/setup-java@v4`、`actions/setup-node@v4` 等 action 当前运行时基线。
2. 该 warning 不影响本次 CI 结果，3 个 job 均成功。
3. 当前不在 Step 92 直接升级 action 版本，避免在“首轮运行结果跟踪”中混入新变更。
4. 下一轮可单独评估是否按 GitHub 官方当前版本把 action 升级到支持新 runtime 的版本。

## 明确没有做

1. 没有修改 workflow。
2. 没有修改业务代码。
3. 没有新增部署流程。
4. 没有注入真实腾讯地图 key、数据库密码、JWT secret 或任何生产密钥。
5. 没有运行浏览器 E2E。
6. 没有修改 bridge、鉴权、接口、路由、token 附着或前端页面。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. CI 首轮通过不改变 bridge 保留策略。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 93 建议进入“GitHub Actions runtime warning 处理 go / no-go”：

1. 先核查 `actions/checkout`、`actions/setup-java`、`actions/setup-node` 当前官方推荐版本。
2. 如果存在明确的 Node.js 24 runtime 兼容版本，则做一个只改 action 版本的最小 PR / commit。
3. 如果当前仓库继续保持绿色且 warning 不阻塞短期试运营，也可以记录为非阻塞观察项，不扩业务功能。
4. 不重开 bridge、展示 polish、地图扩展、真实支付或部署自动化。

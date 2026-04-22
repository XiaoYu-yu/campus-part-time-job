# Step 90 - 最小 CI 检查边界设计 / go-no-go

## 本轮目标

1. 基于 Step 89 已完成部署后 smoke checklist，评估是否值得进入最小 CI。
2. 明确 CI 的最小检查范围、触发条件、密钥策略和不做范围。
3. 本轮只做设计和 go / no-go，不直接新增 workflow。
4. 不改业务代码、不改 bridge、不改接口、不改路由、不改鉴权。

## 当前仓库扫描结果

已有：

1. `.github/pull_request_template.md`
2. `.github/ISSUE_TEMPLATE/bug_report.yml`
3. `.github/ISSUE_TEMPLATE/feature_request.yml`

未发现：

1. `.github/workflows/*`
2. 已存在 GitHub Actions CI。

## 评估方向

### 方向 A：暂不做 CI

收益：

1. 不增加 GitHub Actions 维护成本。
2. 不受 CI 环境差异影响。

问题：

1. main 上仍可能出现后端无法编译、前端无法构建或 H2 样本锚点被误删的问题。
2. 当前项目已经进入产品级试运营准备线，仅靠人工本地验证已经不够稳。
3. Step 83 到 Step 89 已经把本地 preflight、样本校验、环境、密钥、部署后 smoke 都文档化，再缺 CI 会成为明显工程短板。

结论：

不建议继续 no-op。

### 方向 B：新增最小 CI

收益：

1. 自动防止 main 进入无法编译或无法构建状态。
2. 自动检查试运营 H2 样本锚点。
3. 不需要真实腾讯地图 key、真实数据库密码或生产 JWT secret。
4. 不触业务代码，不触 bridge，不触部署。

风险：

1. CI 首次运行可能暴露平台差异。
2. 前端 `npm ci` 依赖 lockfile 和 Node 版本。
3. Windows / Linux shell 差异需要在 workflow 中明确。

控制方式：

1. 只跑 backend compile、frontend build 和 sample validation。
2. 不启动长驻进程。
3. 不跑浏览器 E2E。
4. 不接真实部署。
5. sample validation 的 warning 不作为硬失败。

结论：

建议进入最小 CI 实现，但放到 Step 91。

## 最小 CI 边界

建议 Step 91 只新增一个 workflow：

- `.github/workflows/trial-operation-ci.yml`

最小 job：

1. backend compile
   - `cd backend`
   - `.\mvnw.cmd -DskipTests compile`
2. frontend build
   - `cd frontend`
   - `npm ci`
   - `npm run build`
3. sample validation
   - `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1`
   - exit code `1` 失败。
   - exit code `2` 仅 warning，不失败。

触发条件：

1. push 到 `main`。
2. pull request 指向 `main`。

## 明确不做

1. 不做自动部署。
2. 不做真实数据库连接。
3. 不注入真实腾讯地图 key。
4. 不注入生产 JWT secret。
5. 不启动 backend / frontend 长驻进程。
6. 不跑浏览器 E2E。
7. 不做 H2 自动 reset。
8. 不删除或收紧 bridge。
9. 不接真实支付、真实退款或真实打款。

## 新增文档

新增：

- `docs/deployment/ci-check-boundary.md`

用于记录最小 CI 边界、推荐触发条件、密钥策略、go / no-go 结论和验收口径。

## 验证结果

已执行：

1. `git diff --check`
   - 结果：通过。
   - 说明：仅保留 Windows 工作区 LF -> CRLF 提示，无空白错误。

本轮未执行 backend compile 或 frontend build，因为未改运行时代码、依赖、构建配置或接口；本轮只是 CI 方案设计。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 最小 CI 不会删除、收紧或改变 bridge。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 91 建议进入“最小 GitHub Actions CI 实现”：

1. 新增 `.github/workflows/trial-operation-ci.yml`。
2. 覆盖 backend compile、frontend build、sample validation。
3. 不做部署、不跑 E2E、不接真实密钥。
4. 验证 workflow YAML 语法和本地等价命令。

## Step 91 回填

Step 91 已完成该建议：

1. 已新增 `.github/workflows/trial-operation-ci.yml`。
2. workflow 覆盖 backend compile、frontend build、sample validation。
3. sample validation warning exit code `2` 已按 warning 处理，不使 CI 失败。
4. 下一轮建议观察 GitHub Actions 首次运行结果。

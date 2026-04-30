# Step 147 - 内测入口状态复盘

## 本轮目标

1. 复盘当前本地、GitHub、服务器运行态和运维脚本状态。
2. 判断当前是否已经具备少量真实用户本地/内测型试用条件。
3. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 本地仓库状态

当前本地：

```text
branch: main
latest commit: f06d837 docs: define internal trial cleanup retention policy
status: clean before Step 147 logging
```

本地已包含：

1. 日志轮转策略。
2. SSH 硬化清单。
3. 部署后验证脚本。
4. 服务器只读运维健康脚本。
5. 清理与留存策略。

## GitHub 状态

`origin/main` 当前指向：

```text
f06d837f89b39914cf694ac00bc2c9deb90badcc
```

结论：

1. 本地 `main` 与 GitHub `origin/main` 一致。
2. 当前公开仓库已包含最新文档与运维脚本。

## 服务器运行态状态

本轮使用 `server-post-deploy-check.ps1` 对服务器复核：

报告路径：

1. `project-logs/campus-relay/runtime/step-147-internal-trial-status-review/remote-smoke-report.json`
2. `project-logs/campus-relay/runtime/step-147-internal-trial-status-review/server-post-deploy-check-report.json`

结果：

```text
server-post-deploy-check: PASS=2 FAIL=0 SKIP=0
remote smoke: PASS=25 FAIL=0 SKIP=0
```

覆盖范围：

1. public health。
2. admin / customer / parttime 登录。
3. admin 核心只读运营接口。
4. customer onboarding / completed order detail。
5. parttime profile / review-status / available orders / completed order detail。
6. frontend shell 关键入口。

## 服务器部署提交说明

服务器部署目录当前读取到：

```text
deploy_commit=1f343ce
```

说明：

1. 该提交已包含当前服务器运行所需的应用代码与 compose 日志轮转配置。
2. Step 142 之后的提交主要是本地文档、日志、运维脚本和运行报告。
3. 这些后续提交不影响当前服务器容器运行态。
4. 如果后续需要让服务器目录也包含最新运维脚本，可单独执行一次 `git pull --ff-only origin main`，但不需要因此重建业务容器。

## 运维脚本状态

当前可用脚本：

1. `remote-smoke.ps1`
2. `server-post-deploy-check.ps1`
3. `server-ops-health.ps1`
4. `commands.ps1`
5. `preflight.ps1`
6. `validate-samples.ps1`
7. `browser-smoke.ps1`

已验证：

1. `server-post-deploy-check.ps1` 可真实跑通。
2. `server-ops-health.ps1` 可真实跑通。
3. `commands.ps1` 可执行。

## 当前能支持的试用范围

当前已经可以支持少量 owner-controlled 内测：

1. Web 管理后台访问。
2. customer 用户端 Web / Android WebView 入口。
3. parttime 兼职端 Web / Android WebView 入口。
4. 样本账号下的完整主要链路 smoke。
5. 服务器端口边界：公网业务入口走 `80`，backend / MySQL 仅本机绑定。
6. 备份、restore drill、日志轮转、health、remote smoke、运维健康检查。

## 仍不适合直接对外开放的原因

当前仍不建议称为正式上线：

1. 没有 HTTPS / 域名 / 证书。
2. SSH `22` 仍需 owner 在云安全组限制来源 IP。
3. password login 仍保留，关闭前必须确认 key 登录和回滚路径。
4. 没有正式监控告警。
5. 真实支付、真实退款、真实打款仍为模拟链路。
6. 当前样本数据仍偏演示和内测，不是正式生产数据治理。

## 当前结论

当前状态可定义为：

```text
本地 / 内测型试运营可用
但不是完整产品级正式上线
```

适合：

1. 自己本地测试。
2. 小范围演示。
3. owner 控制的内测访问。
4. 继续围绕真实试用反馈修 bug。

不适合：

1. 对公网用户开放注册和长期使用。
2. 接真实资金。
3. 宣称生产可用。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 148 建议二选一：

1. 若继续面向内测：补“内测用户试用说明与账号发放边界”，明确谁能测、怎么登录、不要测什么、反馈怎么记录。
2. 若继续运维：把服务器部署目录也 fast-forward 到最新文档 / 脚本提交，但不重建容器，随后用 `server-post-deploy-check.ps1` 复核。

默认建议选择 1，因为当前真正缺的是内测使用边界，而不是继续堆运维脚本。

Step 148 已完成补充：

1. 新增 `docs/deployment/internal-trial-user-test-guide.md`。
2. 已明确内测参与范围、账号发放边界、可测范围、不要测范围和反馈记录格式。
3. 未提交真实账号密码或任何密钥。

# Step 144 - 服务器部署后验证清单脚本化

## 本轮目标

1. 把服务器部署后验证流程从手工命令收敛成可重复执行脚本。
2. 在一个只读入口中串起 remote smoke 和可选 SSH 部署检查。
3. 保持 bridge、鉴权、接口、路由和业务代码不变。

## 为什么做这个

Step 142 已证明日志轮转配置在服务器容器中生效。

Step 143 已证明显式 identity key 登录可用，但不建议直接关闭 password login。

因此 Step 144 不继续改服务器配置，而是先把部署后验证动作固化为脚本，减少后续每次部署后的人工遗漏。

## 实际完成项

### 1. 新增部署后验证脚本

新增：

- `scripts/trial-operation/server-post-deploy-check.ps1`

脚本能力：

1. 调用 `remote-smoke.ps1` 执行远端 API + SPA shell smoke。
2. 支持通过 `-SshHost / -SshIdentity` 做可选只读 SSH 检查。
3. SSH 检查读取：
   - 服务器当前部署提交。
   - `campus-trial-backend-1` LogConfig。
   - `campus-trial-frontend-1` LogConfig。
   - `campus-trial-mysql-1` LogConfig。
4. 输出脱敏 JSON 报告。
5. 不修改服务器状态。

### 2. 同步命令索引

已更新：

1. `scripts/trial-operation/README.md`
2. `scripts/trial-operation/commands.ps1`

新增 `server-post-deploy-check.ps1` 的示例入口，并继续要求真实 host 只在执行时传入，不写入仓库文档。

## 真实运行结果

本轮已用真实服务器执行一次：

```text
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\server-post-deploy-check.ps1 ...
```

报告路径：

1. `project-logs/campus-relay/runtime/step-144-server-post-deploy-check/remote-smoke-report.json`
2. `project-logs/campus-relay/runtime/step-144-server-post-deploy-check/server-post-deploy-check-report.json`

结果：

```text
remote smoke: PASS
ssh deployment checks: PASS
server-post-deploy-check summary: PASS=2 FAIL=0 SKIP=0
remote smoke detail: PASS=25 FAIL=0 SKIP=0
```

SSH 只读检查确认：

1. key-based SSH 可用。
2. 可读取服务器部署提交。
3. 三个核心容器 LogConfig 均包含 `json-file / max-size / max-file`。

说明：

1. 当前服务器部署提交仍停在运行态所需的应用提交。
2. Step 142 之后的日志文档类提交不需要立即部署到服务器。
3. 后续真正部署新业务代码时，再使用本脚本做部署后复核。

## 脱敏与安全

报告已脱敏：

1. `apiBase` 使用 `<redacted>`。
2. `frontendBase` 使用 `<redacted>`。
3. `sshHost` 使用 `<redacted-host>`。
4. remote smoke endpoint 中 host 使用 `<redacted>`。

本轮没有提交：

1. 真实公网 IP。
2. 服务器密码。
3. 私钥内容。
4. GitHub token。
5. 腾讯地图 key。

## 明确没有改动

本轮没有修改：

1. Java 业务代码。
2. Vue 页面。
3. `request.js`。
4. token 附着逻辑。
5. bridge 相关接口。
6. 后端鉴权。
7. 路由。
8. 数据库表结构。
9. 旧兼容模块。
10. 服务器 `sshd_config`。

本轮没有关闭 password login。

## 验证结果

1. `server-post-deploy-check.ps1` 真实运行通过。
2. remote smoke：25 项通过、0 项失败、0 项跳过。
3. SSH 部署检查：通过。
4. `scripts/trial-operation/commands.ps1`：通过。
5. `git diff --check`：通过。
6. 敏感信息扫描：无真实 secret / host 命中。

本轮只新增脚本和文档，不改业务代码，因此没有重新执行 backend compile 或 frontend build。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 145 建议进入“内测运维最小监控 / 磁盘空间检查”：

1. 固化服务器磁盘空间检查命令。
2. 固化 Docker 日志占用查看命令。
3. 固化备份目录占用查看命令。
4. 不引入完整监控系统，不接告警平台。

如果不继续运维线，也可以转入 HTTPS / 域名 / 证书方案设计，但当前用户已明确先不做 HTTPS，因此建议继续做轻量运维检查。

Step 145 已完成补充：

1. 新增 `scripts/trial-operation/server-ops-health.ps1`。
2. 已真实读取服务器磁盘、Docker 占用、容器日志大小和备份目录占用。
3. 当前磁盘和日志压力低，本轮未执行任何清理动作。

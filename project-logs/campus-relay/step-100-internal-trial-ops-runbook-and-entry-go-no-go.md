# Step 100 - 单机服务器运维交接与正式入口 go/no-go

## 本轮目标

1. 在 Step 98 / Step 99 已经完成备份与 restore drill 的前提下，补齐单机服务器内测运维交接入口。
2. 把启动、停止、日志、更新、备份、恢复演练、smoke、回滚触发和正式入口判断集中到一份 runbook。
3. 判断当前是否需要进入 HTTPS / 域名 / 正式反向代理准备。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由、不新增页面。

## 为什么进入这一轮

当前已经具备：

1. 单机服务器 Compose 栈真实运行。
2. 真实公网 smoke 已通过。
3. 真实备份已跑通。
4. 非破坏性 restore drill 已跑通。

因此本轮不再继续证明“能不能备份 / 能不能恢复”，而是把日常怎么运维、什么情况下该回滚、什么时候才需要正式入口写清楚。

## 实际完成项

### 1. 新增单机内测试运营运维 Runbook

已新增：

- `docs/deployment/internal-trial-ops-runbook.md`

覆盖内容：

1. 当前服务器部署约定。
2. 敏感信息不入仓库的规则。
3. 日常状态检查。
4. 查看日志。
5. 启动 / 重建。
6. 停止服务。
7. 重置环境。
8. 备份。
9. 非破坏性 restore drill。
10. 最小 smoke。
11. 更新部署。
12. 回滚优先级。
13. 回滚触发条件。
14. HTTPS / 域名 / 反向代理 go-no-go。
15. 当前冻结主线。

### 2. 更新文档入口

已更新：

1. `README.md`
2. `docs/README.md`
3. `docs/deployment/internal-trial-compose.md`
4. `docs/deployment/backup-and-rollback.md`

现在文档入口已经能从项目根 README 直接跳到：

1. Compose 部署说明。
2. 环境变量与密钥清单。
3. 部署后 smoke checklist。
4. 单机内测试运营运维 runbook。

### 3. 正式入口 go/no-go 判断

本轮结论：当前内测阶段暂不强制进入 HTTPS / 域名 / 正式反向代理准备。

理由：

1. 当前目标仍是 owner 自测 / 小范围内测型试运营。
2. 当前单机服务器 IP 访问已经可用。
3. 当前更关键的风险已经通过备份、restore drill、smoke 和回滚说明收敛。
4. 过早接入正式域名 / HTTPS / 反向代理会引入证书、CORS、端口收口、续期和配置变更风险。

进入正式入口准备的触发条件已写入 runbook：

1. 开始邀请非开发者长期访问。
2. 需要微信 / 移动端真实环境稳定访问。
3. 浏览器安全策略开始影响地图、登录或 token 行为。
4. 需要把 backend / mysql 从公网端口收回到内网。
5. 需要更正式的访问日志、证书续期和反向代理规则。

### 4. 服务器现状复核

本轮复核真实服务器：

1. 当前 compose 栈仍为 `Up`。
2. `frontend / backend / mysql` 均仍在运行。
3. `curl http://127.0.0.1/` 返回 `200`。
4. 最新 backup manifest 仍存在：
   - `deploy/internal-trial/backups/meta/campus-trial-backup-20260422-195207.txt`

同时确认：

1. 服务器 Git 工作区曾经经历过部署热修和脚本上传。
2. GitHub `main` 仍是源码真相来源。
3. 服务器目录只作为部署目录，不作为开发源。
4. 后续更新部署时必须先看 `git status --short`，不要直接覆盖无法确认来源的 dirty 文件。

## 本轮明确没做

1. 没有接 HTTPS。
2. 没有配置域名。
3. 没有改反向代理正式入口。
4. 没有改 compose 对外端口。
5. 没有改业务代码。
6. 没有改 bridge。
7. 没有改接口、路由、鉴权或页面。

## 修改文件

1. `README.md`
2. `docs/README.md`
3. `docs/deployment/internal-trial-ops-runbook.md`
4. `docs/deployment/internal-trial-compose.md`
5. `docs/deployment/backup-and-rollback.md`
6. `project-logs/campus-relay/global-working-memory.md`
7. `project-logs/campus-relay/summary.md`
8. `project-logs/campus-relay/pending-items.md`
9. `project-logs/campus-relay/file-change-list.md`
10. `project-logs/campus-relay/step-100-internal-trial-ops-runbook-and-entry-go-no-go.md`

## 验证结果

1. 服务器 compose 状态复核通过。
2. 服务器本机 HTTP `200` 复核通过。
3. 最新备份 manifest 存在。
4. `git diff --check` 通过。

## 当前 bridge 结论

1. bridge 继续保持 `Phase A no-op` 冻结态。
2. 本轮运维交接没有修改 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status`。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 101 建议进入“内测运行观察与 HTTPS / 域名触发条件跟踪”：

1. 如果近期仍是 owner 自测，先按 runbook 做日常内测，不继续扩功能。
2. 如果准备邀请外部用户长期访问，再进入 HTTPS / 域名 / 正式反向代理准备。
3. 如果服务器发生异常，优先按 runbook 做备份、restore drill、smoke 和回滚判断。
4. 继续不重开 bridge，不继续页面 polish，不补第五个 admin 页。

# Step 142 - 服务器日志轮转部署与远端验证

## 本轮目标

1. 将 Step 141 的 Docker `json-file` 日志轮转配置部署到单机内测服务器。
2. 重建 compose 后验证 `mysql / backend / frontend` 三个容器的 `LogConfig` 生效。
3. 通过 public health endpoint 和远端 smoke 确认服务器内测链路仍可用。
4. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 部署前置

1. 服务器部署目录工作区干净。
2. 服务器部署更新前已执行 `backup-stack.sh`。
3. 服务器部署从旧提交 `9cc8d13` fast-forward 到 `1f343ce`。
4. 本轮记录中不写真实公网 IP、服务器密码、token、地图 key 或 GitHub token。

## 实际完成项

### 1. 服务器拉取并重建 compose

在服务器部署目录执行：

1. `git pull --ff-only origin main`
2. `docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml up -d --build`

重建后 `mysql / backend / frontend` 均处于 running 状态：

1. `backend` 继续绑定服务器本机 `127.0.0.1:8080`。
2. `mysql` 继续绑定服务器本机 `127.0.0.1:3306`。
3. `frontend` 继续对外提供 `80` 并代理 `/api`。

### 2. Docker 日志轮转配置生效

通过 `docker inspect` 验证三个核心容器：

```text
backend:  json-file max-size=20m max-file=5
frontend: json-file max-size=20m max-file=5
mysql:    json-file max-size=20m max-file=5
```

结论：

1. Step 141 的 compose logging 配置已在服务器运行容器中生效。
2. 当前默认每个容器最多保留约 `100m` 日志。
3. 三个核心容器理论日志上限约 `300m`。

### 3. health endpoint 验证

compose 重建后立即访问 health 曾出现一次短暂 `502`，原因是 backend 刚重建后仍在 warm-up。

随后重新检查：

```text
GET /api/campus/public/health -> code=200, status=UP
```

结论：

1. `502` 属于重建后的短暂 warm-up 现象。
2. backend 启动完成后 health endpoint 正常。

### 4. 远端 smoke 复核

使用远端 smoke 脚本通过 nginx `/api` 入口复核。

报告路径：

- `project-logs/campus-relay/runtime/step-142-remote-smoke/remote-smoke-report.json`

结果：

```text
PASS: 25
FAIL: 0
SKIP: 0
```

覆盖范围包括：

1. public health。
2. admin / customer / parttime 登录。
3. admin settlements / after-sale / courier / exception 查询。
4. customer onboarding / token eligibility / completed order detail。
5. parttime profile / review-status / available orders / completed order detail。
6. frontend shell 关键入口。

报告已脱敏：

1. `apiBase` 与 `frontendBase` 使用 `<redacted>`。
2. endpoint 中 host 使用 `<redacted>`。
3. token 不写入报告。

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

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 验证结果

1. 服务器更新前备份：通过。
2. 服务器 `git pull --ff-only origin main`：通过。
3. 服务器 compose rebuild：通过。
4. `mysql / backend / frontend` LogConfig：均为 `json-file max-size=20m max-file=5`。
5. `GET /api/campus/public/health`：warm-up 后通过。
6. 远端 smoke：25 项通过、0 项失败、0 项跳过。
7. 远端 smoke 报告脱敏检查：通过。

本轮没有业务代码变更，因此没有重新执行 backend compile 或 frontend build；Step 141 已在本地完成 compile/build，本轮重点是服务器运行态验证。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

当前结论仍然是：

1. bridge 保留。
2. 不删除旧 bridge 接口。
3. 不改 token 附着逻辑。
4. 不改鉴权策略。
5. 不因为服务器部署验证而重开 bridge 收口主线。

## 下一轮建议

Step 143 建议优先处理 SSH 运维入口硬化，但仍先做清单和安全边界，不直接破坏当前可登录状态：

1. 重新确认 key-based SSH 是否可用。
2. 在 key 登录确认可用前，不关闭 password login。
3. 由 owner 在云控制台限制 SSH `22` 来源 IP。
4. 记录回滚方式，避免把服务器锁死。

如果暂不处理 SSH，则建议补一个“服务器部署后验证清单”小文档，把 backup、pull、compose rebuild、health、remote smoke、LogConfig 检查顺序固定下来。

Step 143 已完成补充：

1. 显式 identity key 登录已验证可用。
2. 新增 `docs/deployment/internal-trial-ssh-hardening.md`。
3. password login 未关闭，服务器 `sshd_config` 未修改。
4. 后续如需继续硬化，应由 owner 先在云控制台限制 SSH `22` 来源 IP，再决定是否关闭 password login。

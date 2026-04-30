# 单机内测安全边界说明

## 目标

这份文档固化当前单机内测服务器的最小安全边界，避免后续部署时重新暴露 backend 或 MySQL。

它不是完整生产安全方案，也不替代 HTTPS、域名、WAF、正式监控、堡垒机或企业级审计。

## 当前边界结论

当前单机内测环境采用以下边界：

1. 业务公网入口只使用 frontend `80`。
2. API 公网访问统一走 frontend nginx `/api` 反向代理。
3. backend `8080` 只绑定服务器本机 `127.0.0.1`。
4. MySQL `3306` 只绑定服务器本机 `127.0.0.1`。
5. SSH `22` 仅作为运维入口，建议在云厂商安全组中限制来源 IP。

## 允许暴露的端口

| 端口 | 用途 | 公网策略 | 说明 |
| --- | --- | --- | --- |
| `80` | frontend / nginx / `/api` 反向代理 | 允许 | 当前唯一业务公网入口 |
| `22` | SSH 运维 | 仅限 owner 或可信来源 IP | 不属于业务入口，建议收窄安全组来源 |

## 不允许公网暴露的端口

| 端口 | 用途 | 策略 | 替代访问方式 |
| --- | --- | --- | --- |
| `8080` | backend 容器端口 | 不允许公网访问 | 服务器本机 `127.0.0.1:8080` 或 SSH tunnel |
| `3306` | MySQL 容器端口 | 不允许公网访问 | 服务器本机 `127.0.0.1:3306` 或 SSH tunnel |

## Compose 绑定要求

`deploy/internal-trial/docker-compose.yml` 中必须保持：

```yaml
mysql:
  ports:
    - "127.0.0.1:${MYSQL_PORT:-3306}:3306"

backend:
  ports:
    - "127.0.0.1:${BACKEND_PORT:-8080}:8080"
```

frontend 继续作为业务公网入口：

```yaml
frontend:
  ports:
    - "${FRONTEND_PORT:-80}:80"
```

## 云安全组建议

云安全组建议只放行：

1. `80/tcp`：面向内测用户访问前端和 `/api`。
2. `22/tcp`：面向 owner 运维，建议限制为固定来源 IP。

云安全组不应放行：

1. `8080/tcp`。
2. `3306/tcp`。
3. 其它未明确需要的业务端口。

如果后续启用 HTTPS，再新增 `443/tcp`，但本阶段不做 HTTPS / 域名 / 证书。

## 验证命令

### 服务器本机监听检查

在服务器执行：

```bash
ss -tlnp | grep -E ':(22|80|8080|3306)\b' || true
```

期望：

1. `0.0.0.0:80` 或 `[::]:80` 可见。
2. `0.0.0.0:22` 或 `[::]:22` 可见。
3. `127.0.0.1:8080` 可见。
4. `127.0.0.1:3306` 可见。
5. 不应出现 `0.0.0.0:8080`。
6. 不应出现 `0.0.0.0:3306`。

### 本地公网端口探测

在本地 PowerShell 执行，注意不要把真实 host 写入仓库：

```powershell
$hostName = "your-host"
foreach ($port in @(22, 80, 8080, 3306)) {
  $result = Test-NetConnection -ComputerName $hostName -Port $port -InformationLevel Quiet -WarningAction SilentlyContinue
  [pscustomobject]@{ Port = $port; TcpReachable = $result }
}
```

期望：

1. `80` 可访问。
2. `22` 可访问或按安全组策略限制为可信来源可访问。
3. `8080` 不可访问。
4. `3306` 不可访问。

### 远端 smoke

端口收敛后，远端 smoke 必须走 `/api`：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host/api -FrontendBase http://your-host/
```

不要再默认使用：

```powershell
http://your-host:8080/api
```

除非你明确临时开放了 8080 做诊断，并且诊断后立即关闭。

## 当前实测摘要

Step 139 已基于当前内测服务器做脱敏验证：

1. 服务器监听：
   - SSH `22` 监听公网。
   - frontend `80` 监听公网。
   - backend `8080` 监听 `127.0.0.1`。
   - MySQL `3306` 监听 `127.0.0.1`。
2. 本地公网端口探测：
   - `22` 可达。
   - `80` 可达。
   - `8080` 不可达。
   - `3306` 不可达。
3. 服务器 OS 防火墙状态：
   - `ufw` 当前 inactive。
   - `firewalld` 当前不可用。

因此当前主要边界由 Docker 端口绑定和云安全组共同承担。后续如果进入更长期内测，建议优先在云安全组层面限制 SSH 来源 IP。

## 回滚与例外

如果需要临时诊断 backend：

1. 优先在服务器本机访问 `http://127.0.0.1:8080`。
2. 或使用 SSH tunnel。
3. 不建议直接把 `8080` 改回公网绑定。

如果误把 `8080 / 3306` 开到公网：

1. 立即恢复 compose 的 `127.0.0.1` 绑定。
2. 重建 compose。
3. 重新执行公网端口探测。
4. 重新执行远端 smoke。

## 明确不做

1. 不配置 HTTPS、域名或证书。
2. 不新增后端 health endpoint。
3. 不改 bridge。
4. 不改 `request.js`。
5. 不改 token 附着逻辑。
6. 不改业务接口、鉴权、路由或数据库。
7. 不提交真实公网 IP、服务器密码、SSH 私钥、GitHub token、数据库密码或腾讯地图 key。

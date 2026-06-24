# Step 182 - 138 standalone 稳定性检查与上线差距评估

## 本次目标

owner 询问“稳不稳”和“距离上线还差什么、该如何弄”。本轮目标是在不触碰集群环境的前提下，对 `192.168.121.138 / master` 上的 isolated standalone 栈做稳定性验证，并形成上线前差距清单。

范围限定：

- 只测试 `campus-standalone-*` 项目容器。
- 不复用或修改宿主机 Hive metastore MySQL。
- 不启动、停止或重启 Hadoop / Hive / HBase / ZooKeeper / 宿主机 MySQL。

## 已完成项

### 1. 容器运行态检查

当前 standalone 容器均运行：

```text
campus-standalone-mysql
campus-standalone-backend
campus-standalone-frontend
```

检查结果：

- restart count：0
- OOMKilled：false
- 后端 / 前端 / MySQL 均可响应
- `flyway_schema_history`：14 行，14 行成功，最新 V14

### 2. 真实业务闭环稳定性测试

执行 5 轮完整写入链路：

```text
用户登录 -> 创建代送单 -> 模拟支付 -> 兼职端接单 -> 取餐 -> 配送中 -> 已送达 -> 用户确认
```

结果：

```text
5 PASS / 0 FAIL
```

生成并完成的订单：

- `CR202606241142417810`
- `CR202606241142416164`
- `CR202606241142424858`
- `CR202606241142423471`
- `CR202606241142425530`

每条完整链路总耗时约 0.15s - 0.22s，最终状态均为 `COMPLETED`，并生成对应结算记录。

### 3. 连续远程 smoke

执行 3 轮远程 smoke：

```text
run 1: 27 PASS / 0 FAIL / 0 SKIP
run 2: 27 PASS / 0 FAIL / 0 SKIP
run 3: 27 PASS / 0 FAIL / 0 SKIP
```

报告文件：

- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-run-1.json`
- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-run-2.json`
- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-run-3.json`

### 4. 高频健康探测

对以下地址合计执行 60 次探测：

- `http://192.168.121.138:18080/api/campus/public/health`
- `http://192.168.121.138:18080/`
- `http://192.168.121.138:18080/login`
- `http://192.168.121.138:18080/parttime/workbench`

结果：

```text
60 PASS / 0 FAIL
```

观测：

- 前端 shell 多数请求 2ms - 6ms。
- health 平均约 22ms，最大一次约 212ms。

### 5. 日志与资源检查

最近错误日志扫描：

- 后端：未发现 `ERROR` / OOM / MySQL 连接失败。
- MySQL：未发现 crash / corrupt / fatal。
- Nginx：未发现 error / crit / emerg / alert。

当前主机资源：

```text
/ 可用约 17G，使用率约 55%
内存总量约 3.5Gi，可用约 531Mi
Swap 2.0Gi，已使用约 306Mi
Podman images 约 6.5GB，其中可回收约 5.6GB
```

结论：磁盘当前够用；内存偏紧，若同时运行 Hadoop / Hive / 上课任务与项目容器，存在余量不足风险。

### 6. 重启恢复检查

重启：

- `campus-standalone-backend`
- `campus-standalone-frontend`

结果：

- 后端约 8 秒完成启动。
- Flyway 在重启后执行 validate，确认 schema 当前版本为 14，并提示无需迁移。
- 重启后 health 恢复。
- 重启后 remote smoke 再次通过：27 PASS / 0 FAIL / 0 SKIP。

报告文件：

- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-after-restart.json`

说明：第一次固定等待 8 秒后立即 curl 命中后端尚未完全 ready，属于探测太早；随后健康检查和 smoke 均通过。

## 当前稳定性结论

当前状态可以认为：

- 局域网内测：可用。
- 答辩 / 演示：可用。
- 小流量试运行：基本可用，但建议先补备份和监控。
- 正式公网长期运行：还不建议直接宣布完成。

主要原因不是核心业务链路不通，而是生产运维与发布材料还没完全闭环。

## 距离上线还差什么

### P0 - 必须先完成

1. 生产 release 签名材料
   - 准备用户端、兼职端正式 release keystore。
   - 配好本地 `android/key.properties` 或 GitHub Actions signing secrets。
   - 构建正式 signed APK / AAB。
   - 用 MuMu 或真机安装正式签名包验证。

2. HTTPS / 域名 / 证书
   - 当前 138 只是 `http://192.168.121.138:18080/`。
   - 公网或真实用户使用前，需要域名、HTTPS、证书和 Nginx 入口。
   - Android release WebView 最终也应验证 HTTPS 地址。

3. 生产 secrets 管理
   - 当前 `.env` 只能留在服务器。
   - 不能提交数据库密码、JWT secret、证书私钥、keystore、腾讯地图 key。
   - 上线前应确认生产 JWT secret、DB 密码、CORS 域名。

4. MySQL 备份 / 恢复
   - 当前 MySQL 数据卷已持久化，但还缺正式备份脚本和恢复演练。
   - 至少需要每日 dump、保留周期、恢复命令和一次实际恢复演练。

5. 内存余量
   - 138 当前只有约 3.5Gi 内存，项目容器运行后可用内存约 531Mi，并已使用少量 swap。
   - 如果上课同时跑 Hive / Hadoop，再加项目容器，余量偏紧。
   - 建议生产环境至少单独给项目 4Gi+ 内存；更稳妥是 8Gi 或把项目与课堂集群拆开。

### P1 - 上线前强烈建议

1. 监控与日志
   - 增加容器健康检查脚本。
   - 增加磁盘、内存、容器重启、MySQL 备份结果检查。
   - 确认日志轮转，避免日志长期撑爆磁盘。

2. 回滚与重启 runbook
   - 写清楚如何重启 `campus-standalone-*`。
   - 写清楚如何回滚到上一个镜像 / 上一次 Git commit。
   - 写清楚如何只重启项目而不影响 Hive / Hadoop。

3. 更长时间 soak test
   - 当前验证是短时稳定性。
   - 建议再跑 2 小时 / 8 小时级别的健康探测和 smoke 定时检查。

4. 轻量并发压测
   - 当前已验证连续和少量写链路。
   - 上线前建议做 20 - 50 并发级别的登录、查询、下单、接单混合测试。

5. 数据清理策略
   - 内测订单会留在 MySQL。
   - 如果要转正式环境，应明确是保留内测数据、清空重建，还是迁移到新库。

### P2 - 后续优化

1. `backend/db/init.sql` 标注为历史参考，避免误用。
2. 完善管理端可视化 smoke。
3. 增加更多边界测试：重复接单、并发抢单、取消/售后/异常分支。
4. 继续评估旧后端模块去留，但不能破坏 `user` / `employee` 等 campus 复用基础能力。

## 修改文件

- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-run-1.json`
- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-run-2.json`
- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-run-3.json`
- `project-logs/campus-relay/runtime/step-182-stability-check/remote-smoke-after-restart.json`
- `project-logs/campus-relay/step-182-stability-check-and-launch-gap.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 下一步建议

1. 先补 standalone MySQL 备份 / 恢复脚本和 runbook。
2. 再决定上线形态：
   - 只做局域网演示：当前已经够用。
   - 做公网内测：先补 HTTPS / 域名 / secrets / 备份。
   - 做 Android 正式分发：先准备生产 keystore 并构建 signed release。
3. 若继续放在 138，建议避免上课重负载时同时压项目；更稳妥是给项目单独机器或扩内存。

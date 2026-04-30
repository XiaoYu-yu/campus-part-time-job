# 部署后 Smoke Checklist

## 目标

这份清单用于 prod-like 试运营部署后的最小 smoke 检查。

它不是完整自动化测试，也不代表生产上线验收完成。它只用于确认：

1. 部署包可启动。
2. 前端静态资源可访问。
3. customer / courier / admin 主链路入口没有明显断裂。
4. 模拟资金、地图和 bridge 的试运营边界没有被误改。
5. 出现关键失败时知道是否需要回滚。

## 执行前提

- [ ] 已阅读 [环境变量与密钥配置清单](env-and-secret-checklist.md)。
- [ ] 已阅读 [生产部署说明](production-deploy.md)。
- [ ] 已阅读 [备份与回滚说明](backup-and-rollback.md)。
- [ ] 若是单机内测服务器，已阅读 [远端内测 Smoke 执行说明](remote-internal-trial-smoke.md)。
- [ ] 已确认真实腾讯地图 key、数据库密码、JWT secret 没有提交到仓库。
- [ ] 已确认当前仍不接真实支付、真实退款、真实打款。
- [ ] 已确认 bridge 仍处于 `Phase A no-op` 冻结态。

## 一、部署产物检查

### 后端

- [ ] 后端 jar 来自当前计划部署的 Git commit。
- [ ] `SPRING_PROFILES_ACTIVE=prod` 或目标 profile 已明确。
- [ ] `JWT_SECRET` 已注入，且不是文档占位值。
- [ ] `DB_PASSWORD` 已注入，且不是文档占位值。
- [ ] `APP_CORS_ALLOWED_ORIGINS` 只包含目标前端域名。
- [ ] 上传目录 `APP_UPLOAD_STORAGE_PATH` 可写。
- [ ] 数据库 migration 已在目标数据库执行。

### 前端

- [ ] `frontend/dist/` 来自当前计划部署的 Git commit。
- [ ] `VITE_API_BASE_URL` 与反向代理路径一致。
- [ ] `VITE_USE_MOCK=false`。
- [ ] 如需地图，`VITE_TENCENT_MAP_KEY` 已在构建环境注入。
- [ ] 如不展示地图，也已准备好说明地图区域可降级，不影响主链路。

## 二、基础访问检查

### 静态资源

- [ ] 打开前端首页，页面能加载。
- [ ] 浏览器控制台没有阻断页面加载的 JS 错误。
- [ ] 主要静态资源没有 404。

### 单机内测端口边界

单机内测服务器应先阅读 [单机内测安全边界说明](internal-trial-security-boundary.md)。

- [ ] 业务公网入口只开放 frontend `80`。
- [ ] SSH `22` 只作为运维入口，建议限制来源 IP。
- [ ] backend `8080` 不可公网访问。
- [ ] MySQL `3306` 不可公网访问。
- [ ] 远端 smoke 使用 `http://your-host/api`，不再默认使用 `http://your-host:8080/api`。

### 单机内测日志轮转

单机内测服务器应先阅读 [单机内测日志留存与轮转策略](internal-trial-log-retention.md)。

- [ ] `backend` 容器启用 Docker `json-file` 日志轮转。
- [ ] `frontend` 容器启用 Docker `json-file` 日志轮转。
- [ ] `mysql` 容器启用 Docker `json-file` 日志轮转。
- [ ] 默认或当前 `.env` 生效的 `max-size / max-file` 已记录。

推荐检查命令：

```bash
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-backend-1
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-frontend-1
docker inspect --format='{{json .HostConfig.LogConfig}}' campus-trial-mysql-1
```

### 后端访问

部署后 smoke 可以先用最小 health 接口确认应用存活，再用真实业务入口确认鉴权和主链路：

- [ ] backend health 接口可用：`GET /api/campus/public/health`。
- [ ] customer 登录接口可用。
- [ ] admin 登录接口可用。
- [ ] 受保护接口未带 token 时不会误放行。
- [ ] 带正确 token 时关键接口可访问。

单机内测服务器可以先执行参数化脚本：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host:8080/api -FrontendBase http://your-host/
```

该脚本默认脱敏报告中的 host；不要把真实公网 IP、域名或 token 写入仓库。

单机内测服务器默认应只公网开放 frontend 80，backend 8080 和 MySQL 3306 仅绑定服务器本机 `127.0.0.1`。因此远端 smoke 推荐通过 nginx 反向代理访问：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\remote-smoke.ps1 -ApiBase http://your-host/api -FrontendBase http://your-host/
```

## 三、customer smoke

- [ ] 使用 customer 账号登录成功。
- [ ] 打开 `/user/campus/courier-onboarding`。
- [ ] 页面能展示 onboarding 资料 / 状态区。
- [ ] 打开 `/user/campus/order-result`。
- [ ] 输入或携带样本订单号后可以回读订单结果。
- [ ] 若订单处于 `AWAITING_CONFIRMATION`，页面能显示等待确认态。
- [ ] 若订单处于 `COMPLETED`，页面能显示完成结果回读。

## 四、courier smoke

- [ ] 使用 courier token 路径进入 `/courier/workbench`。
- [ ] workbench 能加载 profile。
- [ ] workbench 能加载 review-status。
- [ ] 可接单列表能正常展示空态或列表态。
- [ ] 订单详情 drawer 能打开。
- [ ] 若样本状态允许，接单 / 取餐 / deliver / 异常上报动作可按演示链路完成。
- [ ] 若本轮不做动作，只读状态也必须能稳定显示。

## 五、admin smoke

- [ ] admin 登录成功。
- [ ] `/campus/settlements` 可打开。
- [ ] settlement 列表和详情 drawer 可读。
- [ ] `/campus/after-sale-executions` 可打开。
- [ ] 售后执行列表和详情 drawer 可读。
- [ ] `/campus/courier-ops` 可打开。
- [ ] courier 列表、最近异常、位置记录可读。
- [ ] 如果地图 key 有效，地图点位预览能加载。
- [ ] 如果地图 key 无效，页面应保持其它只读信息可用。
- [ ] `/campus/exceptions` 可打开。
- [ ] 异常历史列表、详情 drawer 和 resolve 状态展示可读。

## 六、模拟资金链路口径检查

- [ ] mock-pay 仍只表示模拟支付。
- [ ] 售后执行仍只表示模拟退款 / 补偿执行审计。
- [ ] settlement payout 仍只表示模拟打款 / 运营记录。
- [ ] 页面和交付说明没有暗示真实支付、真实退款或真实打款已经接入。

## 七、bridge 冻结口径检查

- [ ] `/api/campus/courier/profile` 仍存在。
- [ ] `/api/campus/courier/review-status` 仍存在。
- [ ] 未在部署前临时删除 bridge。
- [ ] 未在部署前临时收紧 bridge 鉴权。
- [ ] customer onboarding 新入口仍可用。
- [ ] courier workbench 仍优先使用 courier token 路径。

## 八、失败处理与回滚触发

以下情况建议停止继续 smoke，并按 [备份与回滚说明](backup-and-rollback.md) 处理：

- [ ] 后端无法启动或无法连接数据库。
- [ ] 前端静态资源大面积 404。
- [ ] customer / courier / admin 任一角色完全无法登录。
- [ ] customer onboarding 或 courier workbench 主入口不可用。
- [ ] admin 运营只读页大面积报错。
- [ ] 数据库 migration 后出现不可恢复的字段缺失或类型错误。
- [ ] bridge 接口被误删或误收紧。
- [ ] 页面出现真实支付 / 真实退款 / 真实打款误导性文案。

## 九、记录模板

| 项目 | 结果 | 证据位置 | 负责人 | 时间 | 备注 |
| --- | --- | --- | --- | --- | --- |
| 后端启动 | 待填写 |  |  |  |  |
| 前端静态资源 | 待填写 |  |  |  |  |
| customer smoke | 待填写 |  |  |  |  |
| courier smoke | 待填写 |  |  |  |  |
| admin smoke | 待填写 |  |  |  |  |
| 地图预览 / 降级 | 待填写 |  |  |  |  |
| 模拟资金口径 | 待填写 |  |  |  |  |
| bridge 冻结口径 | 待填写 |  |  |  |  |
| 回滚是否触发 | 待填写 |  |  |  |  |

## 明确不做

1. 不把这份 smoke checklist 当成完整生产验收。
2. 不因为 smoke 通过就宣称真实支付、真实退款或真实打款已接入。
3. 不因为 smoke 通过就删除 bridge。
4. 不在 smoke 中修改生产数据结构。
5. 不在 smoke 中新增页面或接口。

## 下一步

如果需要把人工检查前移到代码合并阶段，先阅读：

- [最小 CI 检查边界](ci-check-boundary.md)

如果需要在单机内测服务器上做远端 smoke，先阅读：

- [远端内测 Smoke 执行说明](remote-internal-trial-smoke.md)

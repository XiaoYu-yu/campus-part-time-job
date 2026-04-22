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

### 后端访问

当前项目没有单独新增生产健康检查接口。部署后 smoke 可以用真实业务入口确认：

- [ ] customer 登录接口可用。
- [ ] admin 登录接口可用。
- [ ] 受保护接口未带 token 时不会误放行。
- [ ] 带正确 token 时关键接口可访问。

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

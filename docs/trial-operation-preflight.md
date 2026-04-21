# 试运营运行配置与 Preflight 手册

## 适用范围

本文用于本地试运营、答辩演示和交接复现。当前项目不是完整生产上线版：

1. 支付、退款、打款均为模拟或运营记录，不接真实第三方支付。
2. 腾讯地图已在 admin `courier-ops` 单页完成 JS SDK 只读点位预览，不做轨迹、导航、路线规划或实时调度。
3. bridge 仍处于 `Phase A no-op` 冻结态，不删除兼容接口。
4. 本文只说明运行配置和检查清单，不引导修改业务代码。

## 推荐运行方式

### 后端：test profile + H2

推荐用于演示和回归。

```powershell
cd D:\20278\code\Campus part-time job\backend
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

说明：

1. `application-test.properties` 使用内存 H2：`jdbc:h2:mem:takeaway_test`。
2. H2 启动时会加载：
   - `backend/src/main/resources/db/schema-h2.sql`
   - `backend/src/main/resources/db/data-h2.sql`
3. 适合演示固定账号、固定样本订单和本地完整链路。
4. 进程重启后 H2 内存库会重置到 seed 数据。

### 后端：dev profile + MySQL / MariaDB

用于长期开发调试，不作为当前演示首选。

```powershell
cd D:\20278\code\Campus part-time job\backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

默认连接参数来自 `application-dev.properties`：

1. `DB_HOST` 默认 `127.0.0.1`
2. `DB_PORT` 默认 `3306`
3. `DB_NAME` 默认 `cangqiong_takeaway`
4. `DB_USERNAME` 默认 `root`
5. `DB_PASSWORD` 默认 `xyz12345`

dev profile 默认不自动执行 SQL 初始化。使用前需要确认数据库已按 `backend/db/init.sql` 和 migrations 准备。

### 前端：Vite dev server

```powershell
cd D:\20278\code\Campus part-time job\frontend
npm install
npm run dev
```

默认地址：

1. `http://localhost:5173`
2. 后端默认访问 `http://localhost:8080`

## 本地密钥配置

### 腾讯地图 key

腾讯地图 key 只允许放在本地未跟踪环境文件中。

推荐文件：

```text
frontend/.env.local
```

推荐变量名：

```text
VITE_TENCENT_MAP_KEY=your-local-key
```

注意：

1. 不要把真实 key 写入 `README.md`、日志或源码。
2. `frontend/.env.local` 已被 `*.local` 忽略。
3. 根 `.gitignore` 也忽略 `.env` 与 `.env.*`。
4. 当前腾讯地图 WebService API 未开启，静态图不可用。
5. 当前只使用腾讯地图 JS SDK，在 `/campus/courier-ops` 做只读点位预览。

## 演示账号

| 角色 | 手机号 | 密码 | 用途 |
| --- | --- | --- | --- |
| admin | `13800138000` | `123456` | 管理端、审核、运营页、异常处理 |
| customer | `13900139000` | `123456` | 用户端、onboarding、结果回看 |
| courier candidate | `13900139000` | `123456` | 通过 onboarding 申请 courier token |
| existing courier | `13900139001` | `123456` | H2 seed 中已有 courier 资料与位置样本 |

说明：

1. courier token 通过 customer onboarding 申请获得。
2. 演示时如需要严格走主链路，建议从 customer onboarding 重新申请 token。
3. 如仅演示 admin courier ops 地图点位，可使用 H2 seed 中已有配送员 `id=2` 的位置记录。

## 样本订单

| 订单号 | 推荐用途 | 备注 |
| --- | --- | --- |
| `CR202604070002` | 可接单、接单后动作链路 | H2 seed 中 `WAITING_ACCEPT` 示例 |
| `CR202604060001` | completed 回读、settlement、地图位置点位 | H2 seed 中已有 completed 与位置上报样本 |
| `CR202604070001` | 待 building priority 示例 | 可用于基础列表观察 |

## 模拟资金链路口径

当前试运营版不接真实支付、真实退款、真实打款。

### 模拟支付

1. customer 下单后的支付状态由后端模拟推进。
2. 不调用第三方支付 SDK。
3. 不产生真实支付流水。

### 模拟退款 / 售后执行

1. 售后决策和售后执行用于展示运营处理闭环。
2. `after-sale execution` 记录是运营审计和模拟执行结果。
3. 不接真实退款通道。

### 模拟打款 / settlement

1. settlement 单笔、批次、批次操作审计、对账差异均为运营记录。
2. 不接真实打款通道。
3. 不做真实财务撤回。
4. payout batch、reconcile difference 只用于演示后台运营审计。

## 关键页面 Preflight

演示前逐项打开：

| 页面 | 路径 | 检查点 |
| --- | --- | --- |
| customer onboarding | `/user/campus/courier-onboarding` | 可查看资料、审核状态、token 资格和 token 申请区 |
| customer 结果页 | `/user/campus/order-result` | 可按订单号回读 `AWAITING_CONFIRMATION / COMPLETED` |
| courier workbench | `/courier/workbench` | 有 courier token 时能加载 profile / review-status / available orders |
| admin settlement 批次页 | `/campus/settlement-batches` | 批次列表与详情可打开 |
| admin settlement 单笔页 | `/campus/settlements` | 单笔列表、详情 drawer、对账差异只读区可打开 |
| admin after-sale 执行页 | `/campus/after-sale-executions` | 售后执行列表、详情 drawer、执行历史可打开 |
| admin courier ops | `/campus/courier-ops` | courier 列表、最近异常、位置记录、腾讯地图预览可打开 |
| admin exception ops | `/campus/exceptions` | 异常历史列表、详情 drawer、resolve 区可打开 |

## 关键接口 Preflight

建议演示前至少抽查：

1. `GET /api/campus/customer/courier-onboarding/token-eligibility`
2. `POST /api/campus/courier/auth/token`
3. `GET /api/campus/courier/profile`
4. `GET /api/campus/courier/review-status`
5. `GET /api/campus/courier/orders/available`
6. `GET /api/campus/courier/orders/{id}`
7. `POST /api/campus/courier/orders/{id}/accept`
8. `POST /api/campus/courier/orders/{id}/pickup`
9. `POST /api/campus/courier/orders/{id}/deliver`
10. `POST /api/campus/courier/orders/{id}/exception-report`
11. `GET /api/campus/customer/orders/{id}`
12. `GET /api/campus/admin/exceptions`
13. `GET /api/campus/admin/settlements/reconcile-summary`

## 演示前检查清单

### 环境

- [ ] 后端 test profile 已启动。
- [ ] 前端 Vite dev server 已启动。
- [ ] 浏览器打开 `http://localhost:5173`。
- [ ] 后端端口可访问。
- [ ] H2 seed 数据已重置到预期状态。

### 密钥

- [ ] 如需地图，`frontend/.env.local` 中存在 `VITE_TENCENT_MAP_KEY`。
- [ ] 确认未把真实 key 写入源码或文档。
- [ ] `/campus/courier-ops` 地图区不显示 key 缺失提示。

### 账号与 token

- [ ] admin 可登录。
- [ ] customer 可登录。
- [ ] courier token 可申请或已有可用 token。
- [ ] 无 courier token 时 `/courier/workbench` 不调用 courier 业务接口。
- [ ] 有 courier token 时 `/courier/workbench` 可加载 profile / review-status。

### 样本数据

- [ ] `CR202604070002` 可用于可接单链路。
- [ ] `CR202604060001` 可用于 completed 回读、settlement 和地图点位演示。
- [ ] 如需售后执行历史，确认已准备对应售后样本。
- [ ] 如需对账差异，确认已准备或使用已有运行态验证样本。

### 页面

- [ ] onboarding 页面可打开。
- [ ] workbench 页面可打开。
- [ ] customer 结果页可打开。
- [ ] admin settlement 页面可打开。
- [ ] admin after-sale 页面可打开。
- [ ] admin courier ops 页面可打开，地图可渲染。
- [ ] admin exception 页面可打开。

### 失败处理

- [ ] 地图失败时可说明：当前地图只是单页产品化试点，主业务链路不依赖地图。
- [ ] after-sale 样本不足时可展示页面结构和历史能力说明。
- [ ] GitHub push 失败不影响本地演示，但需要记录本地 commit id。
- [ ] H2 数据被操作污染时，重启 backend test profile 以恢复 seed 状态。

## 不建议在演示前临时做的事

1. 不临时改 bridge。
2. 不临时接真实支付。
3. 不临时接真实退款或真实打款。
4. 不临时扩地图第二页。
5. 不临时新增 admin 页面。
6. 不临时修改 H2 seed 之外的数据结构。
7. 不把真实腾讯地图 key 发到仓库。

## 后续产品化候选

1. 模拟资金链路产品化说明继续增强。
2. dev / MySQL 运行手册细化。
3. 未来真实部署前再补 Nginx、域名、HTTPS、日志、备份和监控。
4. 只有试运营明确要求时，才重新打开地图产品线或 bridge 收口主线。

# Step 75 - 试运营运行配置与 Preflight 手册

## 本轮目标

1. 基于 Step 74 的产品化规划，补一份可执行的试运营运行配置与 preflight 手册。
2. 明确 backend / frontend 启动方式、H2/test 与 MySQL/dev 边界、腾讯地图 key 本地配置、模拟资金链路口径和关键检查项。
3. 保持 bridge、展示 polish、媒体线、地图线和非 bridge 后端三线的既有冻结 / 收住口径。
4. 本轮不改业务代码、不新增页面、不改接口、不改路由、不改鉴权。

## 实际完成内容

### 新增试运营运行手册

新增：

`docs/trial-operation-preflight.md`

该文档覆盖：

1. 后端 `test profile + H2` 推荐启动方式。
2. 后端 `dev profile + MySQL / MariaDB` 使用边界。
3. 前端 Vite dev server 启动方式。
4. 腾讯地图 key 的本地配置方式。
5. 演示账号与样本订单索引。
6. 模拟支付、模拟退款、模拟打款和 settlement 口径。
7. customer / courier / admin 关键页面 preflight。
8. 关键接口 preflight。
9. 演示前环境、密钥、账号、样本数据、页面检查清单。
10. 失败处理与不建议临时做的事项。

### 更新交付说明

更新：

`docs/delivery-guide.md`

调整点：

1. 将“地图 SDK 未接入”的过期表述更新为：
   - 腾讯地图已在 admin `courier-ops` 单页完成 JS SDK 只读点位预览。
   - 轨迹、导航、路线规划和实时调度仍未接入。
2. 在主演示链路参考中增加 `trial-operation-preflight.md`。

### 更新文档入口

更新：

1. `README.md`
2. `docs/README.md`

新增试运营 preflight 手册入口，避免后续演示前仍需要翻 Step 日志。

## 本地密钥口径

1. 腾讯地图 key 只允许放在 `frontend/.env.local`。
2. 推荐变量名为 `VITE_TENCENT_MAP_KEY`。
3. `frontend/.env.local` 已被 `frontend/.gitignore` 的 `*.local` 忽略。
4. 根 `.gitignore` 也忽略 `.env` 与 `.env.*`。
5. 本轮没有把真实地图 key 写入仓库、日志或文档。

## 模拟资金链路口径

1. 模拟支付只用于本地试运营链路推进，不调用第三方支付。
2. 售后执行记录用于模拟退款 / 执行审计，不接真实退款通道。
3. settlement 单笔、批次、操作审计、对账差异用于模拟打款和运营审计，不接真实打款。
4. 当前不做完整财务后台、不做真实对账、不做真实撤回。

## 关键 preflight 摘要

### 环境

1. 后端 test profile 启动。
2. 前端 Vite dev server 启动。
3. H2 seed 数据处于预期状态。
4. 如需地图，`frontend/.env.local` 配置 `VITE_TENCENT_MAP_KEY`。

### 账号

1. admin：`13800138000 / 123456`
2. customer：`13900139000 / 123456`
3. courier candidate：通过 customer onboarding 申请 token
4. existing courier：`13900139001 / 123456`

### 样本订单

1. `CR202604070002`：可接单链路。
2. `CR202604060001`：completed 回读、settlement、地图位置点位。
3. `CR202604070001`：基础列表观察。

### 关键页面

1. `/user/campus/courier-onboarding`
2. `/user/campus/order-result`
3. `/courier/workbench`
4. `/campus/settlement-batches`
5. `/campus/settlements`
6. `/campus/after-sale-executions`
7. `/campus/courier-ops`
8. `/campus/exceptions`

## 明确没做

1. 没有修改 Vue 页面。
2. 没有修改 Java / SQL 业务代码。
3. 没有新增后端接口。
4. 没有新增路由。
5. 没有改 bridge、鉴权、token 附着、`request.js` 或 `campus-*` API 运行时行为。
6. 没有提交真实腾讯地图 key。
7. 没有做真实支付、真实退款、真实打款。
8. 没有进入真实部署上线。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮运行手册不涉及 bridge 行为变化，也不构成 bridge 恢复推进触发条件。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 76 建议评估“模拟资金链路产品化边界说明”是否需要独立成文。
2. 如果继续推进，仍优先只做文档 / 口径整理：
   - 模拟支付
   - 售后模拟退款 / 执行
   - settlement 模拟打款
   - 对账差异和批次操作审计
3. 不建议直接写真实支付、真实退款、真实打款代码。

## Step 76 回填

Step 76 已将模拟资金链路独立成文：

`docs/simulated-funds-boundary.md`

回填结论：

1. mock-pay 只推进订单业务状态，不代表真实支付。
2. 售后执行只记录模拟退款 / 执行审计，不接真实退款通道。
3. settlement payout、批次操作审计和对账差异只表达模拟打款与运营审计，不代表真实财务执行。
4. 当前不接真实支付、真实退款、真实打款、银行、支付网关或第三方清结算。
5. 本次回填没有修改业务代码、接口、路由、鉴权、bridge 或前端页面。

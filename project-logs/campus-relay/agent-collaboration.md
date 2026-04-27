# AI 协作交接文件

## 用途

这份文件是跨 AI 会话 / 跨工具协作时的最小上下文恢复入口。任何新的 AI 实例或协作工具在开始操作之前，必须先读本文件，再读：

1. `project-logs/campus-relay/global-working-memory.md` — 全局状态快照
2. `project-logs/campus-relay/pending-items.md` — 当前待处理事项
3. `project-logs/campus-relay/summary.md` — 完整历史 step 索引
4. `AGENTS.md` — 项目级约束

## 当前项目状态

本项目不是从零开始的新项目，而是在苍穹外卖基础之上改造的校内兼职 / 校园代送平台。

- **当前版本**：`2.0.0-campus-trial`（试运营版）
- **旧外卖模块**：仍作为兼容模块保留，不删除
- **campus 主线**：已是当前主要业务方向，三端闭环完整
- **项目阶段**：主链路稳定，多条线冻结，当前进入运维加固 / 旧模块审计阶段

### 核心能力覆盖

- customer：校园代送下单、模拟支付、onboarding 资料提交、售后结果回看、completed 回读
- courier：workbench、接单、取餐、deliver、异常上报、送达等待确认
- admin：courier 审核、settlement 运营、after-sale 运营、异常处理
- 移动端：双 Capacitor Android 壳（user-app / parttime-app），已验证 public WebView smoke

## 当前冻结线

以下主线当前处于冻结态或维护态，除非本轮任务明确要求，否则**不要重开**：

| 主线 | 状态 | 说明 |
|---|------|------|
| **bridge 主线** | `Phase A no-op` 冻结 | 不删除旧接口、不改鉴权、不找收口候选 |
| **展示 polish 线** | 冻结/维护态 | 不继续机械 polish 页面 |
| **媒体与交付线** | 已收住 | 不继续截图/录屏 |
| **腾讯地图线** | 冻结为单页 admin 只读 | 不扩到第二个页面 |
| **前端打包优化线** | freeze / no-op | 不做 Element Plus 按需拆分 |
| **试运营脚本线** | 维护态 | preflight / validate / commands 已够用 |
| **settlement P3 后端线** | 冻结/维护态 | 异常/售后/settlement 三线已收住 |

## 当前允许推进的主线

- 旧外卖可见痕迹清理（只做文档审计，不删除代码）
- 旧模块删除前依赖审计
- 前端校园兼职风格统一（小范围，不大改）
- 文档和交接材料整理
- 运维加固（备份、恢复、runbook）
- Android / 内测线（HTTPS 收口、域名配置）

## 当前禁止事项

1. **不直接删旧后端模块** — 必须先做依赖审计
2. **不删旧数据库表** — user / employee 仍被 campus 复用
3. **不删旧路由** — 旧路由继续保留为兼容入口
4. **不改鉴权** — JWT 拦截器、token 生成和校验逻辑不动
5. **不改 request.js** — 前端 token 附着和请求拦截逻辑不动
6. **不改 token 逻辑** — customer_token / courier_token / admin_token 策略不动
7. **不提交密钥** — 服务器密码、GitHub token、腾讯地图 key、公网 IP
8. **不碰服务器部署** — 除非任务明确指定
9. **不把审计轮做成删除轮** — 旧模块删除前必须先评估
10. **不新增第五个 admin 页**
11. **不重开 bridge 主线**

### 不可删除的具体项

- `/api/campus/courier/profile`
- `/api/campus/courier/review-status`
- 旧 `orders / cart / address` 语义
- `request.js` 现有 token 附着逻辑
- `CourierWorkbench.vue` 现有优先 `courier_token` 策略

## Claude Code 每轮结束必须在本文件追加

每轮结束时，在本文件末尾追加以下内容：

```markdown
## 轮次记录 YYYY-MM-DD

### 本轮目标
（简述本轮要做什么）

### 实际改动
（列出实际修改的文件和内容摘要）

### 未改动内容
（明确哪些明确不应该动但确实没动）

### 风险
（当前操作可能引入的风险）

### 下一轮建议
（建议下一步做什么）

### 关键确认
- 是否涉及 bridge：是 / 否（默认应为"否"）
- 是否涉及旧外卖删除：是 / 否（默认应为"否，本轮只做评估"）
- 是否改了鉴权：是 / 否（默认应为"否"）
- 是否改了路由：是 / 否（默认应为"否"）
- 是否改了 token 逻辑：是 / 否（默认应为"否"）
- 是否提交了密钥：是 / 否（默认应为"否"）
- backend compile 是否通过：是 / 否
- frontend build 是否通过：是 / 否
```

---

## 轮次记录 2026-04-27

### 本轮目标
旧外卖模块收口评估 + AI 协作交接文件 + 删除前审计计划。仅做文档，不删代码。

### 实际改动
- 新增 `project-logs/campus-relay/agent-collaboration.md`（本文件）
- 新增 `project-logs/campus-relay/legacy-takeaway-removal-readiness.md`
- 更新 `project-logs/campus-relay/summary.md`：追加 Step 125
- 更新 `project-logs/campus-relay/pending-items.md`：追加 Step 126 待处理
- 更新 `project-logs/campus-relay/file-change-list.md`：追加 Step 125 文件清单

### 未改动内容
- 没有改动任何业务代码
- 没有改动任何后端 Java 文件
- 没有改动任何前端 Vue/JS 文件
- 没有改动 bridge
- 没有改动鉴权
- 没有改动路由
- 没有改动数据库表或数据
- 没有改动部署配置
- 没有提交任何密钥

### 风险
- 无。本轮只做文档，不涉及任何运行时风险。

### 下一轮建议
进入 Phase 1：在 MainLayout 中将旧模块菜单项的 "分类兼容 / 商品兼容 / 套餐兼容 / 订单兼容 / 旧店铺状态" 文案进一步收敛为 "兼容模块 - 分类 / 兼容模块 - 商品 / 兼容模块 - 套餐 / 兼容模块 - 订单 / 兼容模块 - 店铺状态"，不改路由、不删页面。

### 关键确认
- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否，本轮只做评估
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- backend compile 是否通过：是
- frontend build 是否通过：是

---

## 轮次记录 2026-04-27 (Step 126)

### 本轮目标
前端视觉优化优先轮：优化后台可见信息架构与文案，让后台看起来像"校内兼职 / 校园代送运营平台"。只做前端展示层和文案层优化，不删旧外卖模块，不改业务逻辑、接口、路由语义、后端代码。

### 实际改动
- 修改 `frontend/src/layout/MainLayout.vue`：菜单"旧店铺状态"→"店铺状态兼容"，breadcrumb "/shop-status"→"店铺状态兼容"
- 修改 `frontend/src/views/Category.vue`：标题→"分类兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/Dish.vue`：标题→"商品兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/Setmeal.vue`：标题→"套餐兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/Order.vue`：标题→"订单兼容管理"，新增兼容提示 banner
- 修改 `frontend/src/views/ShopStatus.vue`：标题→"店铺状态兼容"，新增兼容提示 banner
- 修改 `frontend/src/stores/mock.js`：mock 数据中"销售额"→"模拟流水"，"总销售额"→"模拟服务流水"，注释"菜品销售分布"→"服务类型分布"
- 新增 `project-logs/campus-relay/step-126-campus-admin-frontend-visual-rebaseline.md`
- 更新 `summary.md`、`pending-items.md`、`file-change-list.md`、本文件

### 未改动内容
- 没有删除任何旧外卖模块
- 没有改后端 Java 代码、数据库、鉴权、路由、request.js、token 逻辑、bridge
- 没有删除 `/api/campus/courier/profile` 和 `/api/campus/courier/review-status`

### 风险
- 极低。所有改动仅限前端文案和展示层。

### 下一轮建议
Phase 2：清理前端残留文件（HelloWorld.vue、vue.svg、vite.svg、ComponentDemo.vue + 对应路由）。

### 关键确认
- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- 是否改了后端 Java 代码：否
- frontend build 是否通过：是（1.06s）
- backend compile 是否通过：本轮未改后端代码，非必须

---

## 轮次记录 2026-04-27 (Step 127)

### 本轮目标
用户端 + 兼职端移动入口视觉统一。统一用户端和兼职端的移动端视觉，让它们看起来像"校内兼职 / 校园代送平台"，而不是旧外卖移动端。只改展示层文案和视觉（卡片玻璃态、campus teal 色系、旧词替换），不改任何业务行为、API、路由语义、鉴权或后端代码。

### 实际改动
- 修改 `frontend/src/views/user/Login.vue`：测试账号提示卡片改为玻璃风格
- 修改 `frontend/src/views/user/CampusRelayOrders.vue`："外卖内容"→"代送内容"，placeholder 去美团化，alert 措辞收敛
- 修改 `frontend/src/views/user/CampusOrderResult.vue`：卡片从 white 升级为玻璃态，campus teal 色系统一
- 修改 `frontend/src/views/user/CourierOnboarding.vue`："兼职配送入驻"→"校园兼职入驻"，卡片玻璃态，campus teal 色系
- 修改 `frontend/src/views/courier/Login.vue`：登录按钮升级为 teal→cyan 渐变
- 修改 `frontend/src/views/courier/CourierWorkbench.vue`：卡片玻璃态，campus teal 色系统一
- 修改 `frontend/src/views/courier/Profile.vue`：summary-item 和 notice-panel 颜色统一为 campus 色系
- 新增 `project-logs/campus-relay/step-127-mobile-user-parttime-visual-unification.md`
- 更新 `summary.md`、`pending-items.md`、`file-change-list.md`、本文件

### 未改动内容
- 没有改后端 Java 代码、SQL、数据库
- 没有改任何 API 文件运行时行为
- 没有改路由语义
- 没有改鉴权
- 没有改 request.js
- 没有改 token 逻辑
- 没有改 bridge
- 没有改 Android 原生壳
- 没有删除旧外卖模块
- 没有清理 HelloWorld.vue / ComponentDemo.vue / vue.svg / vite.svg
- 没有新增第五个 admin 页
- 没有删除 /api/campus/courier/profile 和 /api/campus/courier/review-status

### 风险
- 极低。所有改动仅限前端展示层文案和 CSS 视觉（颜色、背景、圆角、阴影），不改任何业务逻辑、条件判断或 API 调用。

### 下一轮建议
1. 若继续推进视觉统一，可处理旧兼容模块页面的移动端适配。
2. 若转向功能开发，优先评估"用户端自助售后申请"的最小实现。
3. 若保持维护态，当前项目已达到三端视觉统一的稳定状态。

### 关键确认
- 是否涉及 bridge：否
- 是否涉及旧外卖删除：否，本轮只做展示层优化
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- 是否改了后端 Java 代码：否
- frontend build 是否通过：是（1.00s）
- backend compile 是否通过：本轮未改后端代码，非必须

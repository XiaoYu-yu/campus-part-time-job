# Step 11 - admin settlement 只读运营页 / frontend 第四个只读运营入口

## 本次目标

1. 为 `frontend/` 增加第四个 admin 最小只读运营页
2. 该页聚焦结算对账摘要、单笔结算记录列表和详情查看
3. 继续观察 `customer/courier-onboarding/*` 与旧 bridge 的并行策略
4. 不改后端状态机，不新增写接口，不破坏旧系统

## 为什么这轮优先 settlement 只读运营页，而不是单独做 after-sale result 汇总页

1. Step 09 已经在售后执行页通过 drawer 复用了 `GET /api/campus/admin/orders/{id}/after-sale-result`
2. 如果再单独做 after-sale result 汇总页，演示面会与 Step 09 的能力明显重叠
3. 当前更适合补齐 settlement 运营视角，形成第四个 admin 只读演示页
4. `GET /api/campus/admin/settlements`、`GET /api/campus/admin/settlements/reconcile-summary`、`GET /api/campus/admin/settlements/{id}` 已存在，足够支撑前端最小只读页

## 实际完成项

1. 新增 admin settlement 只读运营页：
   - `frontend/src/views/CampusSettlementOpsView.vue`
2. 新增 admin 路由：
   - `/campus/settlements`
3. 新增 admin API 封装：
   - `getCampusSettlements`
   - `getCampusSettlementReconcileSummary`
   - `getCampusSettlementDetail`
4. 页面布局采用“顶部摘要卡片 + 下方结算表格 + 详情 drawer”
5. 摘要区调用：
   - `GET /api/campus/admin/settlements/reconcile-summary`
6. 表格区调用：
   - `GET /api/campus/admin/settlements`
7. 详情 drawer 调用：
   - `GET /api/campus/admin/settlements/{id}`
8. 列表区支持最小真实筛选：
   - `settlementStatus`
   - `payoutStatus`
   - `courierProfileId`
   - `relayOrderId`
9. 页面行为：
   - 默认加载摘要和列表
   - 点击单条记录后打开详情 drawer
   - 列表为空时摘要区正常显示，表格区展示空态
   - 全部保持只读，不新增写操作按钮
10. 为 admin 现有侧边栏新增“校园结算运营”入口，并补齐 breadcrumb 与菜单高亮
11. 本轮没有改任何后端接口、VO、数据库和状态机

## 实际修改文件

1. `frontend/src/api/campus-admin.js`
2. `frontend/src/router/index.js`
3. `frontend/src/layout/MainLayout.vue`
4. `frontend/src/views/CampusSettlementOpsView.vue`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`

## 页面访问路径与接口

### 访问路径

1. settlement 只读运营页：`/campus/settlements`

### 调用接口

1. 摘要区：
   - `GET /api/campus/admin/settlements/reconcile-summary`
2. 表格区：
   - `GET /api/campus/admin/settlements`
3. 详情 drawer：
   - `GET /api/campus/admin/settlements/{id}`

### 参数使用方式

1. 摘要区与表格区共用筛选：
   - `settlementStatus`
   - `payoutStatus`
   - `courierProfileId`
   - `relayOrderId`
2. 表格区分页：
   - `page`
   - `pageSize`

## onboarding 新入口与旧 bridge 的并行策略

### 新入口现在承担什么

1. `customer/courier-onboarding/*` 继续承担未拿 courier token 前的稳定前台 onboarding 入口
2. 新 customer 页面已经直接接入这组接口，适合作为未来主 onboarding 前端入口

### 旧 bridge 现在承担什么

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`

这两条继续承担历史兼容和过渡 bridge 入口，避免影响已有调用方和现有测试链路。

### Step 11 观察指标

1. onboarding 页面是否稳定联调
2. 是否仍有历史调用依赖旧 bridge
3. customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景

### 何时可以进入逐步收口 bridge

1. onboarding 新入口完成稳定联调和演示
2. 历史调用方完成迁移盘点
3. customer onboarding 页面已覆盖未拿 courier token 前的主要资料提交和状态查询场景

本轮结论：继续保留 bridge，不做删除动作。

## 本轮没有做的事

1. 没有改后端接口和状态机
2. 没有新增 admin 写操作
3. 没有单独做 after-sale result 汇总页
4. 没有收口旧 bridge
5. 没有新建 settlement 撤回/复核复杂表

## 验证结果

1. `.\mvnw.cmd -DskipTests compile` 通过
2. `npm run build` 通过
3. 页面访问路径：
   - `/campus/settlements`
4. 手工联调方式：
   - 使用 admin 账号登录后台
   - 进入“校园结算运营”
   - 查看顶部摘要卡片是否加载
   - 使用结算状态、打款状态、配送员ID、订单号进行筛选
   - 点击“查看详情”验证 drawer 是否能读取单笔结算记录详情
   - 若列表为空，摘要区继续正常显示，表格区展示空态

## 遗留问题

1. admin 侧仍没有第五个运营演示页
2. customer onboarding 页面还没有直接衔接“申请 courier token”动作
3. 旧 bridge 仍是过渡态，需要继续观察收口条件

## 下一步建议

1. 进入 Step 12
2. 评估第五个 admin 最小只读运营页，优先 after-sale result 汇总页或更细的 batch / payout 只读页
3. 继续观察 onboarding 新入口与旧 bridge 的并行表现，再决定是否逐步收口

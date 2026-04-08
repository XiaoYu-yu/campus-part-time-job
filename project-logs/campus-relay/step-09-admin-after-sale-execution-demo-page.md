# Step 09 - admin 售后执行演示页 / frontend 第二个只读运营入口

## 本次目标

1. 为 `frontend/` 增加第二个 admin 最小只读运营页，优先售后执行分页页
2. 延续现有 admin 平铺路由和菜单风格，不新建第二套路由体系
3. 继续观察 `customer/courier-onboarding/*` 与旧 bridge 的并行策略
4. 不改后端状态机，不新增写接口，不破坏旧系统

## 为什么这轮先做 admin 演示页而不是继续扩 customer 页面

1. Step 08 已经有 settlement 批次演示页，继续补第二个 admin 只读运营页，可以更快形成“可演示”的后台入口组
2. `GET /api/campus/admin/orders/after-sale-executions` 和 `GET /api/campus/admin/orders/{id}/after-sale-result` 已经存在，前端可以直接消费，不需要再扩后端状态机
3. 当前 customer 页面已经有 onboarding 与售后结果页，继续扩 customer 页面并不能明显提升演示价值
4. 这轮优先补 admin 演示页，更符合 `pending-items.md` 的当前优先级

## 实际完成项

1. 新增 admin 售后执行分页页：
   - `frontend/src/views/CampusAfterSaleExecutionList.vue`
2. 新增 admin 路由：
   - `/campus/after-sale-executions`
3. 新增 admin API 封装：
   - `getCampusAfterSaleExecutions`
   - `getCampusAdminAfterSaleResult`
4. 页面支持最小筛选：
   - `afterSaleExecutionStatus`
   - `decisionType`
   - `correctedOnly`
5. 页面展示最小字段：
   - `relayOrderId`
   - `orderStatus`
   - `customerUserId`
   - `courierProfileId`
   - `decisionType`
   - `decisionAmount`
   - `afterSaleExecutionStatus`
   - `afterSaleExecutionRemark`
   - `afterSaleExecutionCorrected`
   - `afterSaleExecutedAt`
   - `afterSaleExecutionCorrectedAt`
6. 新增“查看详情” drawer，复用 `GET /api/campus/admin/orders/{id}/after-sale-result` 查看单笔售后结果汇总
7. 在 `frontend/src/layout/MainLayout.vue` 中新增“校园售后执行”菜单入口
8. 补齐菜单高亮与 breadcrumb

## 实际修改文件

1. `frontend/src/api/campus-admin.js`
2. `frontend/src/router/index.js`
3. `frontend/src/layout/MainLayout.vue`
4. `frontend/src/views/CampusAfterSaleExecutionList.vue`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`

## 页面访问路径与接口

### 访问路径

1. 售后执行分页页：`/campus/after-sale-executions`

### 调用接口

1. `GET /api/campus/admin/orders/after-sale-executions`
2. `GET /api/campus/admin/orders/{id}/after-sale-result`

### 筛选参数使用方式

1. `afterSaleExecutionStatus`
   - 可选值：`PENDING`、`SUCCESS`、`FAILED`、`NOT_REQUIRED`
2. `decisionType`
   - 可选值：`REFUND`、`COMPENSATE`、`NONE`
3. `correctedOnly`
   - 页面使用布尔开关
   - 开启时传 `true`
   - 关闭时不传值，保持后端默认查询

## onboarding 新入口与旧 bridge 的并行策略

### 新入口现在承担什么

1. `customer/courier-onboarding/*` 现在承担未拿到 courier token 前的稳定前台 onboarding 入口
2. 新 customer 页面已经直接接入这组接口，适合作为未来主 onboarding 前端入口

### 旧 bridge 现在承担什么

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`

这两条仍承担历史兼容和过渡 bridge 入口，避免影响已有调用方和测试链路。

### Step 09 观察指标

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
3. 没有继续扩 customer 页面
4. 没有收口旧 bridge
5. 没有新建历史表

## 验证结果

1. `npm run build` 通过
2. 页面访问路径：
   - `/campus/after-sale-executions`
3. 手工联调方式：
   - 使用 admin 账号登录后台
   - 进入“校园售后执行”
   - 使用执行状态、决策类型、人工纠正开关做筛选
   - 点击“查看详情”验证 drawer 是否能读取单笔售后结果汇总

## 遗留问题

1. admin 侧仍没有第三个运营演示页
2. customer onboarding 页面还没有直接衔接“申请 courier token”动作
3. 旧 bridge 仍是过渡态，需要继续观察收口条件

## 下一步建议

1. 进入 Step 10
2. 评估第三个 admin 最小只读运营页，优先 after-sale result 或 courier 异常/位置联动视图
3. 继续观察 onboarding 新入口与旧 bridge 的并行表现，再决定是否逐步收口

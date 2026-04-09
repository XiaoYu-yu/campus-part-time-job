# Step 10 - admin courier 异常/位置联动演示页 / frontend 第三个只读运营入口

## 本次目标

1. 为 `frontend/` 增加第三个 admin 最小只读运营页
2. 该页聚焦配送员最近异常与低频位置记录联动查看
3. 继续观察 `customer/courier-onboarding/*` 与旧 bridge 的并行策略
4. 不改后端状态机，不新增写接口，不破坏旧系统

## 为什么这轮优先 courier 异常/位置联动视图，而不是单独做 after-sale result 汇总页

1. Step 09 已经在售后执行页通过 drawer 复用了 `GET /api/campus/admin/orders/{id}/after-sale-result`
2. 如果继续单独做 after-sale result 汇总页，演示面会与 Step 09 高度重叠，新增价值有限
3. 当前更需要扩展 admin 演示面的广度，补齐配送运营视角，而不是继续堆叠售后视图
4. 现有 courier 相关只读接口已经足够支撑联动页，风险更低、落地更快

## 实际完成项

1. 新增 admin courier 异常/位置联动页：
   - `frontend/src/views/CampusCourierOpsView.vue`
2. 新增 admin 路由：
   - `/campus/courier-ops`
3. 新增 admin API 封装：
   - `getCampusCouriers`
   - `getCampusCourierRecentExceptions`
   - `getCampusCourierLocationReports`
4. 页面布局采用“两栏联动”：
   - 左侧 courier 列表区
   - 右上最近异常区
   - 右下低频位置记录区
5. 左侧 courier 列表调用：
   - `GET /api/campus/admin/couriers`
6. 右上异常区调用：
   - `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
7. 右下位置区调用：
   - `GET /api/campus/admin/couriers/{courierProfileId}/location-reports`
8. 左侧列表支持最小真实筛选：
   - `realName`
   - `reviewStatus`
   - `enabled`
9. 页面行为：
   - 默认自动选中当前 courier 列表页第一条记录
   - courier 列表为空时，右侧统一展示空态
   - 不接地图 SDK
   - 不画轨迹
   - 不做实时刷新
   - 位置记录按后端现有字段只读展示，不在前端重建定位语义
10. 为 admin 现有侧边栏新增“校园配送运营”入口，并补齐 breadcrumb 与菜单高亮
11. 本轮没有改任何后端接口、VO、数据库和状态机

## 实际修改文件

1. `frontend/src/api/campus-admin.js`
2. `frontend/src/router/index.js`
3. `frontend/src/layout/MainLayout.vue`
4. `frontend/src/views/CampusCourierOpsView.vue`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`

## 页面访问路径与接口

### 访问路径

1. courier 异常/位置联动页：`/campus/courier-ops`

### 调用接口

1. 左侧 courier 列表：
   - `GET /api/campus/admin/couriers`
2. 右上最近异常：
   - `GET /api/campus/admin/couriers/{courierProfileId}/exceptions/recent`
3. 右下位置记录：
   - `GET /api/campus/admin/couriers/{courierProfileId}/location-reports`

### 参数使用方式

1. courier 列表最小筛选：
   - `realName`
   - `reviewStatus`
   - `enabled`
2. 最近异常区：
   - 固定读取最近 `10` 条
3. 位置记录区：
   - 使用 `page`
   - 使用 `pageSize`
   - 默认按后端现有 `reportedAt DESC` 只读展示

## onboarding 新入口与旧 bridge 的并行策略

### 新入口现在承担什么

1. `customer/courier-onboarding/*` 继续承担未拿 courier token 前的稳定前台 onboarding 入口
2. 新 customer 页面已经直接接入这组接口，适合作为未来主 onboarding 前端入口

### 旧 bridge 现在承担什么

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`

这两条继续承担历史兼容和过渡 bridge 入口，避免影响已有调用方和现有测试链路。

### Step 10 观察指标

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
5. 没有新建异常历史表或位置轨迹聚合表

## 验证结果

1. `.\mvnw.cmd -DskipTests compile` 通过
2. `npm run build` 通过
3. 页面访问路径：
   - `/campus/courier-ops`
4. 手工联调方式：
   - 使用 admin 账号登录后台
   - 进入“校园配送运营”
   - 左侧选择 courier
   - 观察右上异常区与右下位置记录区是否联动刷新
   - 若 courier 列表为空，右侧展示空态

## 遗留问题

1. admin 侧仍没有第四个运营演示页
2. customer onboarding 页面还没有直接衔接“申请 courier token”动作
3. 旧 bridge 仍是过渡态，需要继续观察收口条件

## 下一步建议

1. 进入 Step 11
2. 评估第四个 admin 最小只读运营页，优先 after-sale result 汇总页或更细的 settlement 只读页
3. 继续观察 onboarding 新入口与旧 bridge 的并行表现，再决定是否逐步收口

# Step 08 - admin settlement 批次演示页 / frontend 最小只读运营接入

## 本次目标

1. 为 frontend 增加一个真正可演示的 admin 最小只读运营页
2. 继续固化 onboarding 新入口与旧 bridge 的并行策略
3. 保持旧系统可运行，不切旧前端主链路，不补新的后端写接口

## 为什么本轮先做 admin 演示页，而不是继续扩 customer 页面

1. Step 07 已经为 customer 侧补了售后结果页和 onboarding 页，customer 演示面已经具备最小联调能力
2. 当前更缺的是 admin 侧真正可见的运营入口，而不是继续堆 customer 页面
3. settlement 批次列表页字段更聚合，演示效果比售后执行分页页更直接
4. settlement 批次页与 Step 06 的批次审计能力衔接最强，复用现有后端接口即可落地
5. 风险更低：
   - 不需要新增写接口
   - 不需要补新的复杂 VO
   - 不需要改变现有状态机

## 已完成项

1. 本轮选择实现 `settlement 批次列表页 + 批次详情页`，没有继续扩 customer 业务页面
2. 新增 admin 侧 API 封装：
   - `getCampusSettlementPayoutBatches`
   - `getCampusSettlementPayoutBatchDetail`
3. 新增 admin 只读演示页：
   - `frontend/src/views/CampusSettlementBatchList.vue`
   - `frontend/src/views/CampusSettlementBatchDetail.vue`
4. 因现有 admin 页面采用平铺视图与平铺路由风格，本轮继续沿用该结构，没有新建 `views/admin/` 第二套目录体系
5. 新增 admin 路由：
   - `/campus/settlement-batches`
   - `/campus/settlement-batches/:batchNo`
6. 新增 admin 侧边栏入口“校园结算批次”
7. 批次列表页能力：
   - 调用 `GET /api/campus/admin/settlements/payout-batches`
   - 支持 `payoutStatus`、`payoutVerified` 两个最小筛选
   - 展示：
     - `payoutBatchNo`
     - `totalCount`
     - `paidCount`
     - `failedCount`
     - `verifiedCount`
     - `unverifiedCount`
     - `totalPendingAmount`
     - `firstRecordedAt`
     - `lastRecordedAt`
   - 支持点击进入详情页
8. 批次详情页能力：
   - 调用 `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
   - 顶部展示批次汇总
   - 下方展示 `records` 明细表
   - 页面只读，不做写操作
9. 本轮没有改动 campus 后端接口，也没有新增任何写接口

## 主要修改文件

- `frontend/src/api/campus-admin.js`
- `frontend/src/router/index.js`
- `frontend/src/layout/MainLayout.vue`
- `frontend/src/views/CampusSettlementBatchList.vue`
- `frontend/src/views/CampusSettlementBatchDetail.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`

## 手工联调说明

1. admin 列表页访问路径：
   - `/campus/settlement-batches`
2. admin 详情页访问路径：
   - `/campus/settlement-batches/:batchNo`
3. 列表页调用接口：
   - `GET /api/campus/admin/settlements/payout-batches`
4. 详情页调用接口：
   - `GET /api/campus/admin/settlements/payout-batches/{batchNo}`
5. 列表筛选使用方式：
   - `payoutStatus`：`UNPAID / PAID / FAILED`
   - `payoutVerified`：`0 / 1`
6. 详情页是纯只读页，不发写请求

## 测试与验证

1. 本轮后端未改接口，继续复用 Step 06 已通过的 backend 能力
2. 执行 `.\mvnw.cmd -DskipTests compile`，通过
3. 执行 `npm run build`，通过
4. 手工验证说明：
   - 页面路由已挂进现有 admin 路由体系
   - `MainLayout` 侧边栏已增加入口，可直接演示访问
   - 新页面直接消费现有后端返回字段，没有在前端重建业务状态机

## Bridge 并行策略评估结论

- 本轮继续保留旧 bridge：
  - `/api/campus/courier/profile`
  - `/api/campus/courier/review-status`
- `customer/courier-onboarding/*` 现在承担：
  - 未拿 courier token 前的稳定 onboarding 前台入口
  - 未来前端 onboarding 页面默认调用链
- 旧 bridge 现在承担：
  - 历史兼容入口
  - 旧桥接链路与现有调用方兼容保障
- Step 08 观察指标：
  - onboarding 页面是否稳定联调
  - 是否仍有历史调用依赖旧 bridge
  - customer onboarding 页面是否已覆盖未拿 courier token 前的主要场景
- 达到以下条件后，才进入逐步收口 bridge 的讨论：
  - onboarding 新入口稳定运行
  - 前端 onboarding 页面完成一轮实际演示验证
  - 历史调用方已明确或迁移完毕

## 遗留问题

1. admin 目前只有 settlement 批次演示页，售后执行分页页仍未接入 frontend
2. customer onboarding 页面还没有直接打通“申请 courier token”按钮流转
3. 旧 bridge 仍保留，需要后续基于运行情况再评估收口
4. `CampusRuleCatalog` 仍是代码常量

## 下一步建议

1. 进入 Step 09，优先做 admin 售后执行分页演示页
2. 继续观察 onboarding 新入口与旧 bridge 的并行表现，再决定是否逐步收口 bridge
3. 视业务需要补更细粒度的售后执行、异常和结算运营审计

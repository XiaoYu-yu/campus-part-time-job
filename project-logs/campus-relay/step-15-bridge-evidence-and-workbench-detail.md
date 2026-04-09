# Step 15 - bridge 依赖评估细化 / courier workbench 订单详情承接

## 本次目标

1. 让 bridge 收口评估从“仓库内证据接近够了”推进到“只差最后一层证据”
2. 让 courier workbench 从“可接单”推进到“接单后能看到一个最小后续承接点”
3. 保持旧系统可运行，不切旧前端主链路

## 实际完成项

1. 继续细化旧 bridge 依赖评估：
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
2. repo 内已确认调用方：
   - `frontend/src/api/campus-courier.js`
   - `frontend/src/views/courier/CourierWorkbench.vue`
3. 当前 repo 内可以明确排除的依赖：
   - 没有发现 customer 页面仍必须通过 `customer_token` 调用旧 bridge 才能完成前置 onboarding 场景
   - onboarding 前置场景已经由 `customer/courier-onboarding/*` 覆盖
4. repo 外暂未确认的依赖范围：
   - 仓库外历史客户端
   - 手工联调脚本
   - 未纳入当前 repo 的旧页面或外部调用方
5. 当前阶段结论：
   - 已具备进入“逐步收口评估”的基础
   - 但还不具备直接删除旧 bridge 的条件
   - 真正收口前还差两类证据：
     - repo 外依赖确认
     - 一轮稳定联调与回归记录
6. courier workbench 已补订单详情承接点：
   - 采用 drawer 方案
   - 没有新增独立页面
7. workbench 表格新增“详情”按钮：
   - 调用 `GET /api/campus/courier/orders/{id}`
   - 展示最小详情字段
8. 接单成功后：
   - 调用 `POST /api/campus/courier/orders/{id}/accept`
   - 刷新可接单预览
   - 自动打开该订单详情 drawer
9. 无 `courier_token` 时：
   - workbench 继续不调用 courier 业务接口
   - 只展示空态与回退入口
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页

## 实际修改文件

- `frontend/src/api/campus-courier.js`
- `frontend/src/views/courier/CourierWorkbench.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-15-bridge-evidence-and-workbench-detail.md`

## 旧 bridge 依赖评估结果

### repo 内已确认调用

1. `frontend/src/views/courier/CourierWorkbench.vue`
   - 通过 `frontend/src/api/campus-courier.js`
   - 调用：
     - `GET /api/campus/courier/profile`
     - `GET /api/campus/courier/review-status`
   - 运行时应优先使用 `courier_token`

### repo 外暂未确认依赖

1. 仓库外历史客户端
2. 手工调用脚本或临时联调代码
3. 未纳入当前 repo 的旧页面或外部调用方

### 当前可明确排除的依赖

1. 当前 repo 内没有发现 customer onboarding 前置场景仍必须依赖旧 bridge
2. 当前 repo 内没有发现除 workbench 之外的其他页面继续直接调用旧 bridge

### 进入逐步收口评估前仍缺的证据

1. repo 外依赖确认结果
2. 一轮稳定联调与回归记录

### 当前阶段结论

- 已接近并且已经具备进入“逐步收口评估”的基础
- 但还不能直接删除旧 bridge

## courier workbench 详情承接说明

1. workbench 页面仍是：
   - `/courier/workbench`
2. 详情查看方式：
   - 在可接单预览表格点击“详情”
   - 打开 drawer
   - 调用 `GET /api/campus/courier/orders/{id}`
3. 接单成功后的最小后续动作：
   - 成功提示
   - 刷新可接单列表
   - 自动打开详情 drawer
4. 详情加载失败时：
   - 继续展示后端原错误信息
   - drawer 不保留空壳状态
5. 无 `courier_token` 时：
   - 页面不调用详情接口
   - 只展示空态和回退入口

## 为什么这轮不补第五个 admin 页

1. 当前更高优先级是把 courier onboarding 替代链路继续做实
2. workbench 从“接单”推进到“接单后能看详情”更直接支撑收口评估
3. 继续机械新增第五个 admin 页的收益低于补足这条主链路

## 手工联调说明

1. 访问 `/courier/workbench`
2. 有 `courier_token` 时：
   - 页面会加载 profile、review-status、available orders
   - 点击“详情”可查看订单详情 drawer
   - 点击“接单”后，成功会刷新列表并自动打开详情 drawer
3. 无 `courier_token` 时：
   - 页面不调用 courier 接口
   - 只展示空态和回退入口
4. 详情加载失败时：
   - 直接展示后端返回错误信息
   - 页面不进入假成功状态

## 遗留问题

1. 旧 bridge 仍未完成 repo 外依赖确认
2. workbench 目前只补到最小详情承接，还没有 pickup / deliver 等后续动作
3. 真实支付、真实退款、真实打款仍未接入
4. 第五个 admin 页本轮未做

## 下一步建议

1. 进入 Step 16
2. 优先补 repo 外或历史调用的依赖确认结论
3. 若继续扩前端，优先补 workbench 的后续动作承接，而不是继续机械补页

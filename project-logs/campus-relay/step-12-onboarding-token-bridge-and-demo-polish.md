# Step 12 - onboarding token 申请衔接 / bridge 收口条件细化 / admin 演示页小修

## 本次目标

1. 让 customer onboarding 页面从“能看状态”升级到“能衔接申请 courier token”
2. 把 bridge 的收口条件从原则描述推进到更具体的前端/调用方观察项
3. 对现有 admin 演示页做最小细化，提升演示完成度
4. 保持旧系统可运行，不切旧前端主链路

## 已完成项

1. 在 `frontend/src/views/user/CourierOnboarding.vue` 中接入现有 `POST /api/campus/courier/auth/token`
2. 新增 `applyCourierToken` 前端 API 封装，复用现有后端 token 申请入口，没有新增后端认证接口
3. onboarding 页面仅在 `token-eligibility.eligible = true` 时展示“申请 courier token”按钮
4. token 申请成功后，页面会展示成功提示、返回 token 和 `courierProfile` 摘要
5. token 申请成功后写入：
   - `localStorage.courier_token`
   - `localStorage.courier_profile`
6. `frontend/src/utils/request.js` 已最小补齐 courier token 附着能力：
   - `/api/campus/courier/orders/**`
   - `/api/campus/courier/location-reports`
7. customer logout 与 courier 请求 401 时，会清理本地 courier token，避免残留脏会话
8. 对以下 admin 演示页做了最小只读细化：
   - `CampusSettlementOpsView.vue`
   - `CampusAfterSaleExecutionList.vue`
   - `CampusCourierOpsView.vue`
9. 细化内容只包括：
   - 更明确的说明 alert
   - 空态文案统一
   - “暂无”字段展示统一
   - 非金额型处理显示为“无金额型处理”
10. 本轮没有新增第五个 admin 页

## 修改文件

- `frontend/src/api/campus-customer.js`
- `frontend/src/utils/request.js`
- `frontend/src/stores/customer.js`
- `frontend/src/views/user/CourierOnboarding.vue`
- `frontend/src/views/CampusSettlementOpsView.vue`
- `frontend/src/views/CampusAfterSaleExecutionList.vue`
- `frontend/src/views/CampusCourierOpsView.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-12-onboarding-token-bridge-and-demo-polish.md`

## 为什么这轮不补第五个 admin 页

1. Step 12 的主目标是把 onboarding 新入口真正收口到 courier token 申请动作
2. 现有四个 admin 演示页已经足以支撑当前阶段演示，不缺页面数量，缺的是 onboarding 链路完整度
3. 在 bridge 仍保留的阶段，优先补“替代链路是否跑通”的证据，比继续机械加一个只读页更有价值

## bridge 并行策略结论

### 新入口现在承担什么

- `customer/courier-onboarding/profile`：customer 侧资料提交/重提
- `customer/courier-onboarding/profile`：customer 侧资料读取
- `customer/courier-onboarding/review-status`：customer 侧审核状态读取
- `customer/courier-onboarding/token-eligibility`：customer 侧资格判断
- customer onboarding 页面：前端 token 申请入口

### 旧 bridge 现在承担什么

- `/api/campus/courier/profile`
- `/api/campus/courier/review-status`

旧 bridge 仍承担历史兼容与双 token 过渡读取能力。

### 本轮观察指标

1. onboarding 页面是否能稳定完成：
   - 资料提交
   - 审核状态读取
   - token 资格判断
   - token 申请
2. 是否仍有历史前端或联调脚本依赖旧 bridge
3. customer onboarding 页面是否已足以覆盖未拿 courier token 前的主要场景

### 是否已具备收口条件

- 结论：**暂未具备**

原因：

1. 新入口已经覆盖核心前置场景，但还缺少对历史调用方的完整盘点
2. 目前仍没有一轮足够明确的“旧 bridge 实际已无人依赖”的证据
3. 因此本轮只推进到“更接近可评估状态”，不做收口动作

## 遗留问题

1. 旧 bridge 仍是过渡态，未进入逐步删除阶段
2. customer onboarding 页面还没有衔接更完整的 courier 前台后续流，只完成 token 申请
3. admin 仍没有第五个只读演示页，但当前不是最高优先级
4. 真实支付、真实退款、真实打款仍然不做

## 下一步建议

1. 继续观察 onboarding 新入口 + token 申请动作是否稳定
2. 盘点是否仍有历史调用依赖旧 bridge
3. 如果 bridge 收口证据不足，下一轮仍优先补“承接 token 后的最小 courier 前台动作”而不是继续堆展示页

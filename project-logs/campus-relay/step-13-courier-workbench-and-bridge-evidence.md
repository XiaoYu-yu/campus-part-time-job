# Step 13 - courier workbench 最小承接页 / bridge 收口证据链细化

## 本次目标

1. 让 customer onboarding 页面在申请到 courier token 后，不停在“拿到 token 即结束”
2. 为 courier 侧补一个最小前台承接页或入口
3. 继续观察并细化 bridge 的收口证据链
4. 保持旧系统可运行，不切旧前端主链路

## 实际完成项

1. 新增 courier workbench 页面：
   - `frontend/src/views/courier/CourierWorkbench.vue`
   - 路由：`/courier/workbench`
2. workbench 页面已接入：
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
   - `GET /api/campus/courier/orders/available`
3. workbench 页面已具备最小三段能力：
   - 身份状态卡
   - 可接单预览
   - 快捷入口区
4. 当本地无 `courier_token` 时：
   - 页面不调用 courier 业务接口
   - 直接展示空态
   - 引导返回 onboarding 页面或个人中心
5. 当本地有 `courier_token` 时：
   - 页面自动刷新 courier profile
   - 自动刷新 review-status
   - 自动刷新前 5 条可接单预览
6. 新增 courier 侧前端 API 封装：
   - `getCourierProfile`
   - `getCourierReviewStatus`
   - `getCourierAvailableOrders`
7. onboarding 页面已补“前往 courier 工作台”按钮，token 申请成功后可直接继续下一步
8. 用户个人中心已补轻量入口：
   - “配送员工作台”
9. `frontend/src/utils/request.js` 已补 courier bridge token 附着逻辑：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
10. 本轮没有改 backend 接口、数据库和状态机
11. 本轮没有补第五个 admin 页

## 实际修改文件

- `frontend/src/api/campus-courier.js`
- `frontend/src/router/index.js`
- `frontend/src/utils/request.js`
- `frontend/src/views/user/Profile.vue`
- `frontend/src/views/user/CourierOnboarding.vue`
- `frontend/src/views/courier/CourierWorkbench.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-13-courier-workbench-and-bridge-evidence.md`

## bridge 并行策略结论

### 新入口现在承担什么

- `customer/courier-onboarding/profile`：资料提交与重提
- `customer/courier-onboarding/profile`：资料读取
- `customer/courier-onboarding/review-status`：审核状态读取
- `customer/courier-onboarding/token-eligibility`：资格判断
- `/api/campus/courier/auth/token`：token 申请
- `/courier/workbench`：token 获取后的最小前台承接页

### 旧 bridge 现在承担什么

- `/api/campus/courier/profile`：历史兼容读取
- `/api/campus/courier/review-status`：历史兼容读取
- 双 token 过渡期兼容能力

### Step 13 观察指标

1. onboarding 页面到 token 申请动作是否稳定联调
2. workbench 页面是否能稳定承接 token 获取后的最小 courier 场景
3. 是否仍存在历史调用直接依赖旧 bridge
4. 是否仍存在必须通过旧 bridge 才能完成的前端场景

### 当前结论

- 本轮仍然保留旧 bridge
- 但已经更接近具备“逐步收口评估条件”
- 还缺两类证据：
  - 历史调用依赖清单
  - 一轮稳定联调与回归记录

## 为什么这轮不补第五个 admin 页

1. 当前更高优先级是让 onboarding 页面不再停在“拿到 token 即结束”
2. courier workbench 直接决定 onboarding 替代链路是否成立
3. 继续机械新增展示页，会稀释本轮收口型增强的重点

## 手工联调说明

1. 访问 `/user/campus/courier-onboarding`
2. 当 `eligible = false` 时，只显示当前 message，不显示申请按钮
3. 当 `eligible = true` 时，输入当前登录账号密码，点击“申请 courier token”
4. 成功后页面展示：
   - 成功提示
   - 返回 token
   - “前往 courier 工作台”按钮
5. 进入 `/courier/workbench` 后：
   - 无 `courier_token` 时显示空态和回退入口
   - 有 `courier_token` 时显示资料卡、审核状态和可接单预览
   - 可接单列表为空时展示空态，不报错

## 遗留问题

1. 旧 bridge 仍是过渡态，还不能删
2. workbench 目前只做到最小承接，没有继续补接单动作
3. 历史调用依赖还没有完成盘点
4. 真实支付、真实退款、真实打款仍未接入

## 下一步建议

1. 进入 Step 14
2. 优先盘点旧 bridge 的真实调用方
3. 若继续扩前端，优先补 courier workbench 之后的最小可接单承接动作

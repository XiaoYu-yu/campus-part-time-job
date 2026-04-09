# Step 14 - bridge 真实调用盘点 / courier workbench 最小接单承接

## 本次目标

1. 让 bridge 收口评估从“原则上接近”变成“有证据支撑的接近”
2. 让 courier workbench 从“只读承接页”升级为“带一个最小可接单动作的承接页”
3. 保持旧系统可运行，不切旧前端主链路

## 实际完成项

1. 完成旧 bridge 的当前仓库内真实调用盘点：
   - `GET /api/campus/courier/profile`
   - `GET /api/campus/courier/review-status`
2. 盘点结果：
   - 这两个接口的直接前端 API 定义位于 `frontend/src/api/campus-courier.js`
   - 当前 frontend 代码内唯一实际调用页面是 `frontend/src/views/courier/CourierWorkbench.vue`
   - 当前没有发现其他 customer 页面、admin 页面继续直接调用这两个接口
3. 当前请求附着策略确认：
   - `request.js` 中，这两个接口被归为 `COURIER_BRIDGE_PREFIXES`
   - 运行时优先附着 `courier_token`
   - 若没有 `courier_token`，才回退到 `customer_token`
4. 基于当前仓库代码可以明确下结论：
   - 旧 bridge endpoint 还在被 workbench 使用
   - 但当前 repo 前端已没有“必须用 customer_token 访问这两个接口才能完成前端主场景”的页面
5. 新 onboarding 链路当前已覆盖：
   - 资料提交
   - 资料读取
   - 审核状态
   - token 资格判断
   - token 申请
   - token 获取后的最小承接页
6. courier workbench 已补最小接单动作：
   - 新增 `acceptCourierOrder`
   - 可接单表格增加“接单”按钮
   - 直接调用 `POST /api/campus/courier/orders/{id}/accept`
   - 成功后弹提示并刷新可接单列表
   - 失败时继续展示后端原错误信息
7. 无 `courier_token` 时：
   - workbench 不调用 courier 业务接口
   - 只展示空态和回退入口
8. 本轮没有改 backend 接口、数据库和状态机
9. 本轮没有补第五个 admin 页

## 实际修改文件

- `frontend/src/api/campus-courier.js`
- `frontend/src/views/courier/CourierWorkbench.vue`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-14-bridge-audit-and-workbench-accept.md`

## 旧 bridge 真实调用方盘点结果

### 当前仍依赖旧 bridge endpoint 的调用

1. `frontend/src/views/courier/CourierWorkbench.vue`
   - 通过 `frontend/src/api/campus-courier.js` 调用：
     - `GET /api/campus/courier/profile`
     - `GET /api/campus/courier/review-status`
   - 该页面定位是 token 获取后的 courier 最小承接页
   - 该页面运行时应优先使用 `courier_token`

### 已可被新链路覆盖的调用

1. customer 侧资料提交与重提
   - 已由 `/api/campus/customer/courier-onboarding/profile` 覆盖
2. customer 侧资料读取
   - 已由 `/api/campus/customer/courier-onboarding/profile` 覆盖
3. customer 侧审核状态查看
   - 已由 `/api/campus/customer/courier-onboarding/review-status` 覆盖
4. customer 侧 token 资格判断
   - 已由 `/api/campus/customer/courier-onboarding/token-eligibility` 覆盖
5. customer 侧 token 申请
   - 已由 `/api/campus/courier/auth/token` 的页面接入覆盖

### 暂未能下结论的调用

1. 仓库外历史前端、脚本、手工调用是否仍依赖：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
2. 是否存在未纳入当前 repo 的旧客户端仍通过 `customer_token` 调用这两个接口

## bridge 并行策略结论

- 本轮结论不是“已经具备收口条件”
- 更准确的结论是：
  - 已经接近具备进入逐步收口评估的证据基础
  - 下一轮主要缺的是仓库外或历史调用依赖盘点，以及一轮联调回归证据

## 为什么这轮不补第五个 admin 页

1. 当前更高优先级是把 onboarding 替代链路补成“拿 token 后可继续做事”
2. workbench 最小接单动作比继续加展示页更能验证替代链路是否成立
3. 继续机械补页会稀释 bridge 收口评估的重点

## 手工联调说明

1. 访问 `/courier/workbench`
2. 无 `courier_token` 时：
   - 页面只展示空态
   - 不调用 courier 业务接口
3. 有 `courier_token` 时：
   - 自动加载 courier 资料
   - 自动加载审核状态
   - 自动加载可接单预览
4. 点击“接单”按钮后：
   - 成功：提示“接单成功，已刷新当前可接单列表”，并刷新预览表
   - 失败：直接展示后端返回的错误信息
5. 如果接单后当前列表为空：
   - 页面展示表格空态，不报错

## 遗留问题

1. 旧 bridge 仍未完成仓库外依赖盘点
2. workbench 目前只补到了最小接单动作，还没有订单详情或后续动作承接
3. 真实支付、真实退款、真实打款仍未接入
4. 第五个 admin 页本轮未做

## 下一步建议

1. 进入 Step 15
2. 优先继续盘点仓库外或历史调用对旧 bridge 的依赖
3. 若继续扩前端，优先补 workbench 的最小订单详情或后续动作承接

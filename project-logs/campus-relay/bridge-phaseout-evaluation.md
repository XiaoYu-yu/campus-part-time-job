# bridge 收口评估

## 背景

当前仍保留以下兼容 bridge 接口：

1. `GET /api/campus/courier/profile`
2. `GET /api/campus/courier/review-status`

这两个接口没有在 Step 18 做删除或鉴权收紧。本次评估目标是判断：是否已经可以从“逐步收口计划设计阶段”推进到“Phase A 执行准备阶段”。

## repo 内已确认调用

### 直接调用来源

1. `frontend/src/api/campus-courier.js`
   - `getCourierProfile`
   - `getCourierReviewStatus`
2. `frontend/src/views/courier/CourierWorkbench.vue`
   - workbench 页面会在本地存在 `courier_token` 时调用上述两个 API

### token 使用方式

1. `frontend/src/utils/request.js`
   - `/campus/courier/profile`
   - `/campus/courier/review-status`
2. 当前策略：
   - 优先附着 `courier_token`
   - 如果本地不存在 `courier_token`，才回退使用 `customer_token`

## repo 内可明确排除的依赖

1. 当前 repo 内没有发现 customer onboarding 前置场景继续依赖旧 bridge。
2. 以下前置场景已经由 `customer/courier-onboarding/*` 覆盖：
   - 资料提交
   - 资料读取
   - 审核状态查询
   - token 资格判断
   - token 申请
3. token 申请成功后的最小前台承接，已经由 `/courier/workbench` 覆盖。
4. 因此，当前 repo 内已没有“必须靠 customer_token 调旧 bridge 才能完成”的前端主场景。

## repo 外暂未确认依赖范围

以下依赖范围无法仅从当前仓库代码直接证明是否存在：

1. 仓库外历史客户端
2. 手工联调脚本
3. 未纳入本仓库的旧页面或外部调用方
4. 运维或测试过程中的临时调用习惯

本次评估不编造 repo 外系统，只保留这部分为“待核实边界”。

## 进入逐步收口计划前仍缺的证据

1. repo 外依赖确认结果
   - 至少确认是否还有历史调用必须依赖双 token bridge
2. 一轮稳定联调与回归记录
   - customer onboarding 新入口稳定
   - token 申请稳定
   - courier workbench 稳定
   - workbench 上 profile / review-status 的 courier_token 路径稳定

## Step 18 执行准备清单

以下清单在当前仓库内无法全部自动证明，必须作为“待人工核实项”保留：

1. 是否还有仓库外旧页面直接调用 `GET /api/campus/courier/profile`
2. 是否还有仓库外旧页面直接调用 `GET /api/campus/courier/review-status`
3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge
4. 是否已有一轮 `customer onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的稳定联调记录
5. workbench 在纯 `courier_token` 路径下是否稳定

当前仓库内能明确给出的状态只有：

- repo 内直接调用方已收敛到 `frontend/src/views/courier/CourierWorkbench.vue`
- customer 前置 onboarding 场景已不再依赖旧 bridge
- 其余条目都不能仅凭 repo 代码证明为“已完成”，只能标注为“待人工核实”

## Step 18 执行准备评估

### repo 内证据是否已经稳定

1. 是。
2. 当前 repo 内已确认调用方仍只有：
   - `frontend/src/views/courier/CourierWorkbench.vue`
3. customer onboarding 前置场景已经不再依赖旧 bridge。
4. token 获取后的最小前台承接也已落到 workbench。

### repo 外依赖是否仍只能列为待确认边界

1. 是。
2. 当前无法仅凭仓库代码证明：
   - 是否仍有仓库外历史客户端直接调用旧 bridge
   - 是否仍有手工联调脚本或临时调用习惯依赖双 token 兼容
3. 因此 repo 外依赖目前仍只能列为“待确认边界”，不能被当作已排除。

### 是否已经具备进入 Phase A 执行准备的条件

1. 还不具备完整条件。
2. 当前不是 repo 内证据不足，而是执行准备清单中的人工核实项还没有被关闭。
3. 更准确的判断是：
   - 已具备 Phase A 的 repo 内准备基础
   - 已把缺口收敛到“repo 外依赖确认 + 一轮稳定联调记录”
   - 但尚不具备发起 Phase A 执行准备的全量证据

### 真正开始删除前还差什么

1. repo 外依赖确认结论
2. 一轮稳定联调与回归记录
3. 证明 workbench 在纯 `courier_token` 路径下运行稳定
4. 把 Step 18 执行准备清单中的人工核实项逐项关闭

## 下一步人工核实建议

1. 逐一确认是否还有仓库外旧页面直接调用旧 bridge 接口
2. 逐一确认是否还有手工联调脚本使用 `customer_token` 调旧 bridge
3. 留存一轮 `onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的联调记录
4. 在联调记录中单独确认 workbench 的 profile / review-status 请求是否全程只走 `courier_token`

## 建议的分阶段动作

### Phase A

1. 保留 bridge
2. 新前端全部走：
   - `customer/courier-onboarding/*`
   - `/api/campus/courier/auth/token`
   - `/courier/workbench`
3. 继续观察 workbench 是否还需要旧 bridge 的 customer_token 回退

### Phase B

1. 保留 bridge，但明确标注为兼容入口
2. 将 repo 内调用控制在极少量遗留读取场景
3. 优先推动 workbench 只走 `courier_token`

### Phase C

1. 在 repo 外依赖确认完成
2. 一轮稳定回归通过
3. 再评估是否真正删除旧 bridge 或收紧为仅 courier_token

## 回滚与兼容保留策略

1. 在 Phase A 和 Phase B 期间，保留：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
2. 在任何回归发现历史调用受影响时，可继续依赖当前 bridge 策略回退。
3. 删除动作必须晚于：
   - repo 外依赖确认
   - 一轮稳定联调
   - 一轮稳定回归

## 当前阶段结论

1. 当前已经可以继续留在“逐步收口计划设计阶段”。
2. 当前还不具备进入 `Phase A` 执行准备的完整条件。
3. 当前更准确的判断是：
   - repo 内证据已经稳定
   - repo 外依赖确认仍然只能列为待人工核实边界
   - 缺口已经收敛到执行准备清单，而不是继续停留在泛化观察阶段
   - 因此下一步应先关闭人工核实项，再决定是否进入执行准备

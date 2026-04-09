# bridge 收口评估

## 背景

当前仍保留以下兼容 bridge 接口：

1. `GET /api/campus/courier/profile`
2. `GET /api/campus/courier/review-status`

这两个接口没有在 Step 16 做删除或鉴权收紧。本次评估目标是判断：是否已经可以进入“逐步收口计划设计阶段”。

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

1. 当前已经可以进入“逐步收口计划设计阶段”。
2. 当前还不具备“实际删除旧 bridge”的条件。
3. 更准确的判断是：
   - repo 内证据已经足够
   - repo 外依赖确认和回归证据仍然缺失
   - 因此下一步应该先做收口计划设计，而不是直接删 bridge

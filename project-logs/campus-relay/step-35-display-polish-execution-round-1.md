# Step 35 - 展示级优化执行轮 1

## 本轮目标

1. 基于 Step 34 的展示级优化候选评估，执行一个小范围 polish 轮次。
2. 本轮只处理两个 P0 页面：
   - `frontend/src/views/user/CampusOrderResult.vue`
   - `frontend/src/views/courier/CourierWorkbench.vue`
3. 只做展示层改动，不改业务语义、不改接口、不改路由、不改 bridge。
4. 保持 bridge `Phase A no-op` 冻结态不变。

## 为什么只选这两个页面

1. `CampusOrderResult.vue` 是 customer 侧 completed / awaiting confirmation 结果回看的收尾页，且是只读页面，展示 polish 风险最低。
2. `CourierWorkbench.vue` 是 courier token 获取后的核心承接页，覆盖 profile / review-status、可接单预览、订单详情、接单、取餐、deliver、异常上报和 completed 回读，是演示价值最高的页面。
3. Step 35 不铺开 onboarding、Profile 和 admin 四个只读页，避免把展示 polish 变成大范围 UI 重做。

## `CampusOrderResult.vue` 展示级优化

本轮只做展示层调整：

1. 顶部标题区新增“只读回看”标记，明确该页不包含写操作。
2. 查询区下方新增三段式说明：
   - 输入订单号
   - 查看确认状态
   - 核对结果字段
3. 初始态增加更明确的状态图标与示例说明。
4. loading 态增加更明确的只读读取提示。
5. 错误态增加权限与订单号检查提示。
6. 结果状态提示块从单行文案调整为“状态标题 + 说明文案”的层级。
7. 增加对应 CSS，用于统一只读 badge、状态图标、引导条和状态卡片层级。

明确未做：

1. 未新增动作按钮。
2. 未新增写接口。
3. 未改订单查询逻辑。
4. 未改 URL query 处理逻辑。
5. 未改 customer 主链路。

## `CourierWorkbench.vue` 展示级优化

本轮只做展示层调整：

1. 顶部标题区新增“最小承接”标记，说明该页仍不是完整 courier 端。
2. courier token 存在时新增四段式演示链路说明：
   - 身份：profile / review-status
   - 订单：可接单预览 / 详情
   - 动作：接单 / 取餐 / 送达
   - 结果：异常 / 确认 / 完成
3. 可接单区新增演示提示，说明列表为空时可通过订单号回读入口验证 completed 态。
4. 订单详情 drawer 顶部新增当前订单状态摘要卡片。
5. 订单详情 drawer 的基本信息区新增分组标题和说明，明确字段来源仍是 courier 订单详情读取接口。
6. 订单详情空态文案更明确，提示可从可接单列表或订单号回读进入详情。
7. 增加对应 CSS，用于统一 flow strip、table note、drawer 状态卡片和分区标题。

明确未做：

1. 未改任何接口调用。
2. 未改任何 token 判断。
3. 未改接单、取餐、deliver、异常上报按钮可用条件。
4. 未改状态机判断。
5. 未改 workbench 与 onboarding / profile 的跳转逻辑。
6. 未改 `request.js` 与 `campus-courier.js`。

## 验证结果

1. `.\mvnw.cmd -DskipTests compile`：通过。
2. `npm run build`：通过。
3. 前端构建仍有既有 Sass `@import` deprecation warning 与 chunk size warning，不是本轮新增问题。
4. 静态检查确认：
   - 本轮没有修改 `frontend/src/utils/request.js`
   - 本轮没有修改 `frontend/src/api/campus-courier.js`
   - 本轮没有修改路由
   - 本轮没有修改后端代码

## 是否影响 bridge

不影响。

本轮没有修改：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `frontend/src/utils/request.js`
4. `frontend/src/api/campus-courier.js`
5. 后端鉴权逻辑
6. token 附着逻辑

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. bridge 完全保留。
3. 本轮未出现恢复推进触发条件。
4. 当前最终结论仍不是“bridge 已可删除”。

## 修改文件

1. `frontend/src/views/user/CampusOrderResult.vue`
2. `frontend/src/views/courier/CourierWorkbench.vue`
3. `project-logs/campus-relay/summary.md`
4. `project-logs/campus-relay/pending-items.md`
5. `project-logs/campus-relay/file-change-list.md`
6. `project-logs/campus-relay/step-34-non-bridge-direction-and-display-polish-assessment.md`
7. `project-logs/campus-relay/step-35-display-polish-execution-round-1.md`

## 下一轮建议

1. Step 36 不要自动扩大到 onboarding + admin 四页全改。
2. 先回看 Step 35 两个 P0 页面 polish 的效果。
3. 若继续展示级优化，建议二选一：
   - 只处理 `CourierOnboarding.vue` 的说明层级与表单展示一致性
   - 或只处理一个 admin 只读运营页的空态 / loading / tag 一致性
4. 第五个 admin 页继续后置。
5. bridge 主线继续冻结，除非出现 Step 33 定义的恢复推进触发条件。

## Step 36 执行回填

1. Step 36 已按本轮建议进行二选一，最终选择方案 A，只处理 `frontend/src/views/user/CourierOnboarding.vue`。
2. 选择 A 的原因：
   - onboarding 是 customer 到 courier token 的前置入口
   - Step 35 已 polish workbench 和 customer result，本轮补 onboarding 能补齐主演示链路前半段
   - onboarding 的资料提交、审核状态、token 资格和 token 申请解释成本高于单个 admin 只读页
3. Step 36 实际改动保持在展示层：
   - 顶部新增 customer 前置入口标记
   - 新增提交资料 / 等待审核 / 申请 token 三段式流程说明
   - 审核与资格概览区新增标题说明
   - token 申请区新增开放条件、申请凭证和成功后承接说明
   - 资料表单区新增说明标题与字段分组说明
4. Step 36 未改：
   - bridge
   - `request.js`
   - `campus-courier.js`
   - `campus-customer.js`
   - token 附着逻辑
   - API 调用顺序
   - `enabledWorkInOwnBuilding` 的整数提交逻辑
   - token 申请逻辑
   - 按钮行为
   - 路由
   - 后端代码
5. Step 36 验证：
   - `.\mvnw.cmd -DskipTests compile` 通过
   - `npm run build` 通过

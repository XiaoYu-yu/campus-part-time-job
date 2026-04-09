# Step 24 - bridge 核实准备增强 / customer 结果页体验增强

## 本次目标

1. 继续推进 repo 外人工核实准备，但不编造 repo 外结果
2. 增强 customer completed 结果回看页的真实使用体验
3. 真实核查 `enabledWorkInOwnBuilding` 提交类型是否仍可能发 boolean
4. 不补第五个 admin 页，不改后端状态机，不切旧前端主链路

## 已完成项

### 1. bridge repo 外人工核实准备继续增强

1. 更新了 `bridge-execution-readiness-checklist.md`
2. 对 repo 外仍未关闭的核实项补齐了：
   - 去哪里核
   - 看什么证据
   - 成功时如何留痕
   - 失败时如何留痕
   - 是否阻塞 `Phase A`
3. 明确写清：
   - 本轮未新增 repo 外人工核实结果
   - 当前文档仍然只是可执行 checklist，不是“已关闭 repo 外依赖”的证明

### 2. customer 结果回看页做了最小体验增强

修改页面：`frontend/src/views/user/CampusOrderResult.vue`

增强内容：

1. 增加无订单号初始提示
2. 增加结果读取中的 loading 态
3. 增加查无订单 / 接口失败的明确错误态
4. 增加结果摘要分组：
   - 配送结果摘要
   - 异常信息摘要
   - 时间信息
5. 区分 `AWAITING_CONFIRMATION` 与 `COMPLETED` 的状态说明文案
6. 查询新订单时先清空旧结果，避免旧结果残留
7. 用户把订单号清空后，清掉 URL 中旧的 `orderId` 参数

### 3. 对 customer 结果回看页做了真实页面验证

使用本地运行环境：

1. backend：`test profile + H2 + 8080`
2. frontend：`vite dev + 5173`
3. 浏览器：Playwright / Chromium

本轮真实验证结果：

1. `/user/campus/order-result` 无 `orderId` 时，页面显示“等待输入订单号”初始提示
2. 查询 `CR404` 时，页面显示“订单不存在”错误态
3. 查询 `CR202604070002` 时，页面显示 completed 结果摘要

### 4. 真实核查了 `enabledWorkInOwnBuilding` 类型

核查文件：

1. `frontend/src/views/user/CourierOnboarding.vue`
2. `frontend/src/api/campus-customer.js`

核查方式：

1. 代码检查：确认 submit payload 使用 `form.enabledWorkInOwnBuildingSwitch ? 1 : 0`
2. Playwright 抓取真实网络请求：
   - `POST /api/campus/customer/courier-onboarding/profile`
   - 请求体中 `enabledWorkInOwnBuilding` 实际值为整数 `1`

结论：

1. 当前不会再发送 boolean `true/false`
2. 前端请求体已与后端 DTO 期望的 `Integer` 保持一致

## 本轮真实新增核实结果

1. customer 结果回看页三种真实使用反馈：
   - 无订单号
   - 查无订单
   - 已完成订单回看
2. onboarding 提交体中 `enabledWorkInOwnBuilding` 的真实请求体类型

## 本轮未新增的核实结果

1. 未新增 repo 外旧页面依赖核实结果
2. 未新增 repo 外历史客户端依赖核实结果
3. 未新增手工脚本依赖核实结果

原因：

1. 当前仓库与当前运行环境无法直接证明 repo 外依赖是否已清空
2. 因此只能继续保留为待人工核实

## 修改文件

1. `frontend/src/views/user/CampusOrderResult.vue`
2. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
3. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
4. `project-logs/campus-relay/bridge-regression-template.md`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`
8. `project-logs/campus-relay/step-24-bridge-readiness-and-customer-result-polish.md`

## 当前遗留问题

1. repo 外依赖仍待人工核实，bridge 仍不能进入 `Phase A` 执行准备
2. customer completed 结果回看页仍只是最小只读承接，没有扩完整 customer 主链路
3. 当前没有新增第五个 admin 页

## 下一步建议

1. 按 checklist 继续补 repo 外人工核实结果
2. 若继续扩 customer 前端，优先评估 completed 结果页是否还需更明确摘要
3. 第五个 admin 页继续后置，不抢 bridge 收口评估优先级

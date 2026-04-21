# 校园代送待处理事项

## Step 72 最高优先级

1. bridge 主线继续保持 `Phase A no-op` 冻结态，下一轮仍不默认寻找 bridge 收口候选。
2. 展示 polish 线继续保持冻结/维护态，下一轮仍不默认继续 polish 页面。
3. Step 40 已完成交付整理与演示脚本固化：
   - 当前交付边界。
   - 主演示脚本。
   - 演示账号与样本数据索引。
   - 页面展示清单。
   - 风险与答辩口径。
4. Step 41 已完成交付材料补完：
   - 截图清单。
   - 录屏顺序。
   - 演示前检查 checklist。
5. Step 42 已完成真实媒体采集与归档：
   - 已采集 15 张真实截图，其中 13 张主交付截图、2 张异常后 confirm 失败留痕。
   - 已采集 5 段真实录屏，其中 4 段主交付录屏、1 段异常后 confirm 失败留痕。
   - 媒体目录：`project-logs/campus-relay/runtime/step-42-media/`。
6. Step 43 已选择路径 B：
   - 不补固定 after-sale 样本。
   - 不继续补媒体。
   - 正式收住媒体线。
   - 完成 3 个非 bridge 后端方向评估。
7. Step 44 已完成异常历史与处理闭环最小方案设计：
   - 建议新增 `campus_exception_record`。
   - 继续保留 `campus_relay_order.exception_type / exception_remark / exception_reported_at` 作为 latest exception 兼容摘要。
   - 复用现有 courier `exception-report` 入口。
   - 最小状态集建议为 `REPORTED / RESOLVED`。
   - admin 侧先做历史分页和详情只读，再评估最小 resolve。
8. Step 45A 已完成异常历史最小实现：
   - 新增 `campus_exception_record`。
   - 现有 courier `POST /api/campus/courier/orders/{id}/exception-report` 同事务写入历史表并继续更新订单 latest exception 摘要。
   - 新增 admin 只读接口 `GET /api/campus/admin/exceptions` 与 `GET /api/campus/admin/exceptions/{id}`。
   - MySQL init、V9 migration、H2 schema 已同步；H2 未预置复杂样本，运行态通过真实异常上报生成历史。
9. Step 45B 已完成 admin 异常最小处理动作设计：
   - 继续坚持最小状态集 `REPORTED / RESOLVED`。
   - 不引入 `ACKNOWLEDGED`。
   - 不把 `REJECTED` 做成主状态，后续如需标记无效可使用 `processResult = MARKED_INVALID`。
   - resolve 只更新 `campus_exception_record` 处理字段，不改订单主状态、不改 settlement、不清空 latest exception 摘要。
10. Step 46 已完成异常 resolve 最小实现：
   - 新增 `POST /api/campus/admin/exceptions/{id}/resolve`。
   - 请求体最小字段为 `processResult` 与 `adminNote`。
   - 最小 `processResult` 固定为 `HANDLED`、`MARKED_INVALID`、`FOLLOWED_UP`。
   - 只允许 `REPORTED -> RESOLVED`。
   - 重复处理返回“异常记录已处理，不能重复处理”。
   - 不改订单主状态、不改 settlement、不清空 latest exception 摘要。
   - 没有补 admin 前端页面。
11. Step 47 已完成 admin 异常前端承接 go / no-go：
   - 方向 A：admin 异常历史 / resolve 最小前端承接，收益明确、风险可控、现有接口已足够。
   - 方向 B：P2 售后执行历史表，长期审计价值高，但需要新表、迁移和独立兼容策略设计，复杂度更高。
   - 最终选择方向 A。
   - 本轮没有写前端页面、没有新增后端接口、没有改 bridge。
12. Step 48 已完成 admin 异常前端最小承接：
   - 新增 `/campus/exceptions`。
   - 新增 `CampusExceptionOpsView.vue`。
   - 列表接入 `GET /api/campus/admin/exceptions`。
   - 详情 drawer 接入 `GET /api/campus/admin/exceptions/{id}`。
   - 最小 resolve 区接入 `POST /api/campus/admin/exceptions/{id}/resolve`。
   - resolve 区只在 `processStatus = REPORTED` 时展示。
   - 没有新增后端接口，没有改订单主状态、settlement、latest exception 摘要或 bridge。
13. Step 49 已完成 admin 异常页运行态验证：
   - H2/test 下使用 `CR202604070002` 真实完成接单、两次异常上报、异常历史列表、详情、resolve、重复 resolve 失败。
   - `campus_exception_record` 对该订单产生 2 条历史，1 条 `REPORTED`、1 条 `RESOLVED`。
   - latest exception 摘要仍保留最后一次上报内容，`/courier/workbench`、customer 订单详情和 admin 最近异常摘要语义未被破坏。
   - `/campus/exceptions` 已通过 Playwright 验证页面加载、列表展示、详情 drawer 和 resolve 区展示。
   - 证据文件位于 `project-logs/campus-relay/runtime/step-49/`。
14. Step 50 已完成 P2 售后执行历史表最小方案设计：
   - 建议新增 `campus_after_sale_execution_record` 作为售后执行审计主表。
   - 每次 admin 成功记录售后执行结果时新增一条历史。
   - 同事务内继续更新 `campus_relay_order` 上的当前售后执行摘要。
   - 订单表现有 `after_sale_execution_*` 字段继续作为兼容摘要保留。
   - Step 51A 才允许评估进入最小后端实现。
15. Step 51A 已完成售后执行历史最小实现：
   - 新增 `campus_after_sale_execution_record`，并同步 MySQL init、V10 migration、H2 schema。
   - 复用现有 `POST /api/campus/admin/orders/{id}/after-sale-execution`，在同事务内追加历史写入。
   - 新增 admin 只读分页接口 `GET /api/campus/admin/after-sale-execution-records`。
   - H2/test 下已验证 `PENDING -> FAILED -> SUCCESS` 产生 2 条历史，且当前售后执行摘要保持兼容。
   - 证据文件：`project-logs/campus-relay/runtime/step-51a/after-sale-execution-history-validation.json`。
16. Step 51B 已完成售后执行历史前端承接 go / no-go：
   - 方向 A：在现有 `CampusAfterSaleExecutionList.vue` 详情 drawer 内增加售后执行历史只读区。
   - 方向 B：切到 P3 settlement 批次复核、撤回和对账方案设计。
   - 最终选择方向 A。
   - 本轮没有写前端页面、没有新增后端接口、没有改 bridge。
17. Step 52 已完成售后执行历史前端最小承接：
   - `frontend/src/api/campus-admin.js` 新增 `getCampusAfterSaleExecutionRecords`。
   - `frontend/src/views/CampusAfterSaleExecutionList.vue` 详情 drawer 新增“执行历史”只读区。
   - 打开详情时继续读取原 after-sale-result 当前摘要，同时按 `relayOrderId` 读取历史记录。
   - 未新增页面、未新增路由、未新增写接口。
18. Step 53 已完成售后执行历史前端运行态验证：
   - H2/test 下重新生成 `CR202604060001` 的 `PENDING -> FAILED -> SUCCESS` 售后执行历史样本。
   - `GET /api/campus/admin/after-sale-execution-records` 返回 2 条历史。
   - `/campus/after-sale-executions` 详情 drawer 可展示当前摘要和执行历史区。
   - 证据文件位于 `project-logs/campus-relay/runtime/step-53/`。
19. Step 54 已完成 P3 settlement 批次复核、撤回和对账最小方案设计：
   - 当前 settlement 批次仍由 `campus_settlement_record.payout_batch_no` 聚合产生，不是独立批次主表。
   - 建议后续新增 `campus_settlement_batch_operation_record`，先承接批次复核/撤回操作审计。
   - 建议后续新增 `campus_settlement_reconcile_difference_record`，承接对账差异和处理状态。
   - 现有 `campus_settlement_record` payout 摘要字段继续作为单笔、批次和 reconcile-summary 的兼容读取基础。
   - 本轮只写方案，没有写 SQL、Java、Vue、接口或页面。
20. Step 55 已完成旧外卖文档清理与目录归档：
   - 旧外卖 API/DB 设计归档到 `docs/legacy-takeaway/`。
   - 根 `project-logs/` 下旧外卖修复日志归档到 `project-logs/legacy-takeaway/`。
   - 当前 README、docs index、frontend README、交付说明、检查说明、部署说明等已改为校园代送试运营口径。
   - 新增 `docs/api-overview.md` 与 `docs/db-overview.md` 作为当前主线入口。
   - 本轮没有改 Java、SQL、Vue 业务代码、bridge、接口、鉴权或路由。
21. Step 56 已完成 settlement 批次操作审计 go / no-go：
   - 确认下一步进入 `campus_settlement_batch_operation_record` 最小实现。
   - 确认批次操作审计只承接 `REVIEW / WITHDRAW` 操作留痕。
   - 确认 `operation_result` 最小使用 `PASSED / REJECTED / REQUESTED / RECORDED`。
   - 明确 review / withdraw 只写批次操作审计，不改 `payout_status`，不清空 `payout_batch_no`，不做真实财务撤回。
   - 对账差异记录继续后置，不和批次操作审计并发实现。
   - 本轮没有写 SQL、Java、Vue、接口实现或页面。
22. Step 57 已完成 settlement 批次操作审计最小实现：
   - 新增 `campus_settlement_batch_operation_record`。
   - 同步 MySQL init、V11 migration、H2 schema。
   - 新增 admin 接口：
     - `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`
     - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/review`
     - `POST /api/campus/admin/settlements/payout-batches/{batchNo}/withdraw`
   - 写入前复用当前 settlement 批次聚合查询校验 `batchNo` 真实存在。
   - review / withdraw 只写操作审计，不改 `payout_status`，不清空 `payout_batch_no`。
   - 本轮没有新增前端页面，没有实现对账差异记录。
   - `.\mvnw.cmd -DskipTests compile` 和 `npm run build` 已通过。
23. Step 58 已完成 settlement 批次操作审计 H2/test 运行态验证：
   - 通过 API 生成固定批次 `PBSTEP58VALIDATION`。
   - review 写入 `REVIEW / PASSED`。
   - withdraw 写入 `WITHDRAW / REQUESTED`。
   - `/operations` 返回 2 条操作历史。
   - 单笔结算 `payoutStatus / payoutBatchNo / payoutVerified` 在 review / withdraw 后保持不变。
   - 批次详情 `totalCount / paidCount / payoutStatus` 在 review / withdraw 后保持不变。
   - 证据文件：`project-logs/campus-relay/runtime/step-58/settlement-batch-operation-api-validation.json`。
24. Step 59 已完成 settlement 批次操作审计前端承接 go / no-go：
   - 方向 A：在现有 `CampusSettlementBatchDetail.vue` 中增加 operations 只读承接。
   - 方向 B：进入 settlement 对账差异记录最小方案设计。
   - 最终选择方向 A。
   - 选择原因：后端 operations 已通过 Step 58 运行态验证，现有批次详情页天然具备 `batchNo` 上下文，且只读承接能补齐批次操作审计展示闭环。
   - 本轮没有写业务代码，没有新增页面，没有改 bridge。
25. Step 60 已完成 settlement 批次操作审计前端最小只读承接：
   - `frontend/src/api/campus-admin.js` 新增 `getCampusSettlementBatchOperations(batchNo, params)`。
   - `frontend/src/views/CampusSettlementBatchDetail.vue` 新增“批次操作历史”只读区。
   - 只调用 `GET /api/campus/admin/settlements/payout-batches/{batchNo}/operations`。
   - 展示 `operationType / operationResult / operationRemark / operatedByEmployeeId / operatedAt`。
   - 没有调用 review / withdraw 写接口。
   - 没有新增页面、路由、后端接口或 bridge 改动。
   - `npm run build` 已通过。
26. Step 61 已完成 settlement 批次操作审计前端运行态验证：
   - H2/test 下准备固定批次 `PBSTEP61UI`。
   - 成功写入 `REVIEW / PASSED` 和 `WITHDRAW / REQUESTED`。
   - `/campus/settlement-batches/PBSTEP61UI` 能展示批次汇总、settlement 明细和 2 条操作审计记录。
   - 浏览器上下文中 `/operations` 返回 `code = 200`、`total = 2`。
   - 证据文件：
     - `project-logs/campus-relay/runtime/step-61/settlement-batch-operation-ui-api-prep.json`
     - `project-logs/campus-relay/runtime/step-61/settlement-batch-operation-page-validation.json`
27. Step 62 已完成 settlement 对账差异记录最小方案设计：
   - 建议后续新增独立 `campus_settlement_reconcile_difference_record` 作为审计主数据。
   - 差异来源第一版只建议 `MANUAL_ADMIN / SIMULATED_RECONCILE`，不接真实银行流水或支付网关。
   - 最小差异类型建议 `AMOUNT_MISMATCH / STATUS_MISMATCH / UNVERIFIED_PAID / FAILED_NEEDS_RETRY / MANUAL_NOTE`。
   - 最小处理状态建议 `OPEN / RESOLVED`。
   - 差异 resolve 只写差异记录处理字段，不改 `payout_status`、不清空 `payout_batch_no`、不改 settlement 金额、不触发真实财务动作。
   - 现有 `campus_settlement_record` payout 摘要继续服务 settlement 单笔页、批次页和 `reconcile-summary` 兼容读取。
   - 本轮没有写 SQL、Java、Vue、接口实现或页面。
28. Step 63 已完成 settlement 对账差异记录实现 go / no-go：
   - 方向 A：进入 `campus_settlement_reconcile_difference_record` 最小后端实现。
   - 方向 B：继续停留在方案设计，补充字段校验细节。
   - 最终选择方向 A。
   - 选择原因：Step 62 已把数据模型、状态、接口和兼容策略收敛到足够小，且实现可独立于现有 settlement payout 摘要。
   - 本轮没有写 SQL、Java、Vue、接口实现或页面。
29. Step 64 已完成 settlement 对账差异记录最小后端实现：
   - 新增 `campus_settlement_reconcile_difference_record`。
   - 同步 MySQL init、V12 migration、H2 schema。
   - 新增 admin 差异列表、详情、创建、resolve 四个最小接口。
   - resolve 只允许 `OPEN -> RESOLVED`，重复 resolve 返回明确业务错误。
   - H2/test 下使用固定批次 `PBSTEP64RECON` 验证创建、查询、resolve、重复 resolve 失败和 settlement 摘要不变。
   - 证据文件：`project-logs/campus-relay/runtime/step-64/settlement-reconcile-difference-validation.json`。
   - 本轮没有新增前端页面，没有新增第五个 admin 页，没有接真实财务，没有改 payout 摘要、订单主状态或 bridge。
30. Step 65 已完成 settlement 对账差异前端承接 go / no-go：
   - 方向 A：在现有 `CampusSettlementOpsView.vue` 详情 drawer 内增加“对账差异记录”只读区。
   - 方向 B：暂不做前端承接，收住 settlement 对账差异线。
   - 方向 C：放到批次详情页。
   - 最终选择方向 A。
   - 选择原因：单笔 settlement 详情 drawer 最适合解释差异记录与 payout 摘要兼容关系，且不需要新增页面、路由或写操作。
   - 本轮没有写前端页面、没有新增 API 封装、没有改 bridge。
31. Step 66 已完成 settlement 对账差异前端最小只读承接：
   - `frontend/src/api/campus-admin.js` 新增 `getCampusSettlementReconcileDifferences(params)`。
   - `frontend/src/views/CampusSettlementOpsView.vue` 详情 drawer 新增“对账差异记录”只读区。
   - 打开 settlement 详情时按 `settlementRecordId` 加载差异记录。
   - 只读展示 `OPEN / RESOLVED`、差异类型、金额、来源、处理结果和时间字段。
   - 空态提示“暂无对账差异记录，当前 settlement 仍按原 payout 摘要展示”。
   - 没有接入 create / resolve 写操作，没有新增页面或路由。
32. Step 67 已完成 settlement 对账差异前端运行态验证：
   - H2/test 下准备固定批次 `PBSTEP67UI`、settlement `id=1` 和 `AMOUNT_MISMATCH` 差异记录。
   - `/campus/settlements` 列表可显示 `CR202604060001`，详情 drawer 可展示“对账差异记录”只读区。
   - 浏览器中验证 `GET /api/campus/admin/settlements/reconcile-differences?settlementRecordId=1&page=1&pageSize=10` 返回 `total = 1`。
   - 对账差异只读展示不改变 settlement payout 摘要，当前摘要仍为 `FAILED / PBSTEP67UI`。
   - 证据文件：
     - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-ui-api-prep.json`
     - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-page-validation.json`
     - `project-logs/campus-relay/runtime/step-67/settlement-reconcile-difference-drawer.png`
33. Step 68 已完成 settlement 对账差异前端线收口评估：
   - 最终结论为 no-go。
   - 当前不继续为 `CampusSettlementOpsView.vue` 增加 resolve 写操作。
   - settlement 对账差异前端线在“只读展示 + 运行态验证”后正式收住。
   - `POST /api/campus/admin/settlements/reconcile-differences/{id}/resolve` 后端接口继续保留，但当前不在只读运营页暴露。
34. Step 69 已完成 settlement P3 主线阶段复盘：
   - 批次操作审计线已在“后端实现 + 前端只读承接 + 运行态验证”后收住。
   - 对账差异线已在“后端实现 + 前端只读承接 + 运行态验证 + 前端线收口评估”后收住。
   - settlement P3 主线整体进入冻结/维护态。
   - 当前不继续为现有 settlement 只读页补 review / withdraw / resolve 前端写动作。
35. Step 70 已完成非 bridge 后端三线整体复盘：
   - 异常历史与最小 resolve 线已达到“后端闭环 + admin 前端承接 + 运行态验证”。
   - 售后执行历史线已达到“后端闭环 + admin 前端承接 + 运行态验证”。
   - settlement P3 线已在 Step 69 确认进入冻结/维护态。
   - 当前不再默认继续扩异常、售后或 settlement 的单点能力。
36. Step 71 已完成整体维护 / 交付口径复盘：
   - 当前最小闭环已达到“可试运营、可演示、可交接”的稳定状态。
   - bridge 冻结态、展示 polish 冻结态、媒体线收住、非 bridge 后端三线收住四条主线当前都没有必须继续补的高优先级缺口。
   - 当前不再默认继续扩异常、售后、settlement 或页面 polish 的单点能力。
37. Step 72 最高优先级建议：
   - 如果继续推进，先进入真正产品化方向规划，统一判断：
     - 试运营之后最值得打开的下一阶段目标是什么
     - 哪些属于真实产品化能力，哪些仍应继续冻结
     - 下一阶段是否优先地图/调度、支付模拟深化、更多运营处理能力，还是整体交付打包
   - 若暂不进入产品化方向规划，则当前项目可保持维护态，不再机械追加新轮次。
38. Step 72 明确禁止：
   - 不改 bridge
   - 不改 `request.js`
   - 不改 `campus-courier.js` bridge 行为
   - 不改 token 附着逻辑
   - 不改路由
   - 不接真实打款
   - 不补完整财务系统
   - 不改订单主状态机
   - 不为补页数机械新增 admin 页面
   - 不接入 create / resolve 写操作
   - 不改后端接口
39. admin 剩余只读运营页和 Profile 页仍属于后续展示级优化候选，但默认不再继续机械 polish。
40. 第五个 admin 页继续后置，不再以“补页数”为目标。

## 已完成但仍需继续扩展的部分

- `campus_*` 核心表、实体、DTO、VO、Mapper、Service、Controller 已齐全
- customer 已打通：创建单、模拟支付、列表、详情、确认送达、取消、售后
- customer 已打通 onboarding 新入口：资料提交、资料读取、审核状态、token 资格判断
- customer 已新增 `/user/campus/order-result`，可最小回看 `AWAITING_CONFIRMATION / COMPLETED` 下的代送结果
- `frontend/src/views/user/CampusOrderResult.vue` 已补清晰的初始提示、错误态和 completed 结果摘要，适合真实演示与回读
- `frontend/src/views/user/CampusOrderResult.vue` 已在 Step 35 追加只读回看标记、三段式引导、状态卡片和错误态说明 polish
- frontend 已新增 `/courier/workbench`，作为 token 获取后的最小 courier 前台承接页
- courier workbench 已补最小接单动作，token 获取后不再停在纯只读承接页
- courier workbench 已补订单详情 drawer，接单后可直接查看当前订单详情
- courier workbench 已补最小取餐承接，可在详情 drawer 中直接调用现有 pickup 接口
- courier workbench 已补最小 deliver 承接，可在详情 drawer 中按真实状态机继续推进配送流程
- courier workbench 已补最小异常上报承接，可在详情 drawer 中按真实后端 DTO 上报最新一次异常
- courier workbench 已补最小 confirm 前可视化与 completed 后只读承接，可在 `AWAITING_CONFIRMATION / COMPLETED` 状态下只读展示送达后状态
- courier workbench 已补按订单号查看详情的最小入口，可在可接单列表为空时回读已完成订单结果
- courier workbench 已在 Step 35 追加最小承接标记、四段式演示链路、可接单区提示和详情 drawer 状态摘要 polish
- courier 已打通：资料提交、资料详情、审核状态、token 发行、可接单列表、接单、取餐、配送推进、异常上报、位置上报
- admin 已打通：
  - 订单分页、详情、时间线
  - 售后处理、售后决策、售后执行结果记录
  - 售后结果分页、售后结果汇总
  - 售后执行分页、人工纠正审计
  - 配送员分页、审核、最近异常、低频位置记录
  - 异常历史分页、详情、最小 resolve 处理
  - 结算分页、详情、确认结算、单笔打款记录、批量打款记录、对账摘要、批次列表、批次详情、二次核对
  - 按订单查看位置记录、按订单查看异常摘要
- customer 已打通售后结果回执查询与 courier onboarding 前台入口
- customer onboarding 页面已补齐 courier token 申请动作衔接
- customer onboarding 页面已在 Step 36 追加 customer 前置入口标记、三段式流程、审核资格概览、token 申请说明和表单分组 polish
- frontend 已打通 admin settlement 批次列表页、批次详情页、售后执行分页页、courier 异常/位置联动页和 settlement 只读运营页最小演示入口
- `frontend/src/views/CampusSettlementOpsView.vue` 已在 Step 37 追加只读运营标记、三段式运营引导、筛选区说明、只读提示、中文状态 tag 和详情 drawer 摘要卡片 polish
- `frontend/src/views/CampusAfterSaleExecutionList.vue` 已在 Step 38 追加只读执行标记、三段式页面导览、筛选区说明、只读提示、中文状态 tag 和售后结果 drawer 摘要卡片 polish
- Step 39 已复盘 `CourierOnboarding.vue`、`CourierWorkbench.vue`、`CampusOrderResult.vue`、`CampusSettlementOpsView.vue`、`CampusAfterSaleExecutionList.vue` 五个关键页面，并将展示 polish 线收成冻结/维护态
- Step 40 已固化交付边界、主演示脚本、演示账号与样本数据索引、页面展示清单和答辩口径
- settlement 已在订单 `COMPLETED` 时自动生成或更新

## 已锁定的默认处理策略

### 1. 账号与鉴权

- 普通用户继续复用 `user`
- 管理员继续复用 `employee`
- 配送员继续通过 `campus_courier_profile.user_id` 关联 `user`
- `/api/campus/admin/**` 继续只允许 `employee`
- `/api/campus/customer/**` 继续只允许 `customer`
- `/api/campus/courier/orders/**` 继续只允许 `courier`
- `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留 `customer/courier` 双 token bridge
- `/api/campus/customer/courier-onboarding/**` 作为新的 onboarding 稳定入口，只允许 `customer`

### 2. 支付、退款与打款

- 第一版支付继续只支持模拟支付
- 售后决策、售后执行结果、结算打款结果都只是后台最小记录
- 不接第三方支付，不做真实退款，不做真实打款
- 金额继续统一为 `BigDecimal`、两位小数、`HALF_UP`

### 3. 状态与运营字段

- 不新增新的订单主状态
- `after_sale_execution_status` 独立表达售后执行结果
- `payout_status` 独立表达打款结果
- `decisionType = NONE` 的订单自动初始化为 `after_sale_execution_status = NOT_REQUIRED`
- `REFUND / COMPENSATE` 的订单自动初始化为 `after_sale_execution_status = PENDING`
- `FAILED -> SUCCESS` 的售后执行人工纠正会写入 `after_sale_execution_corrected*` 审计字段
- settlement confirm 后自动初始化 `payout_status = UNPAID`
- batch payout 未传 `batchNo` 时会在 service 层自动生成统一批次号，只写入成功处理的记录
- `payout_verified*` 字段独立表达二次核对结果，不复用 `settlement_status`

### 4. 异常与位置

- 异常历史已落地到 `campus_exception_record`
- 订单上的 latest exception 字段继续作为兼容摘要保留
- admin resolve 仅支持 `REPORTED -> RESOLVED`，不改订单主状态和 settlement
- 位置上报继续只写 `campus_location_report`
- 不做实时轨迹、地图页、频控策略

### 5. SQL 与持久化

- 继续维护：
  - `backend/db/init.sql`
  - `backend/db/migrations`
  - `backend/src/main/resources/db/schema-h2.sql`
  - `backend/src/main/resources/db/data-h2.sql`
- 继续使用注解式 MyBatis
- 不引入新迁移工具

## 当前主要阻塞点

### 1. bridge 仍是过渡态，但已进入 `Phase A no-op` 冻结态

- 影响：`courier/profile` 与 `courier/review-status` 继续依赖双 token 兼容
- 当前证据：
  - `customer/courier-onboarding/*` 已覆盖资料提交、资料读取、审核状态、资格判断、token 申请
  - `/courier/workbench` 已覆盖 token 获取后的最小前台承接页
  - `/courier/workbench` 已具备接单、详情承接、最小取餐承接、最小 deliver 承接和最小异常上报承接
  - 当前仓库内前端对旧 bridge 的直接调用已盘点到只剩 `CourierWorkbench.vue`
  - onboarding 新入口已能承接未拿 courier token 前的主要前端场景
- Step 29 owner 明确确认：
  - 当前项目从未部署、从未发布、没有历史发布包
  - 当前项目唯一维护人就是 owner 本人，且没有团队
  - 不存在团队共享 `Postman / Apifox / 联调脚本` 资产
  - 不存在仓库外旧页面副本、历史前端包或临时脚本继续依赖旧 bridge
- 当前判断：
  - repo 外阻塞项已关闭
  - Step 33 已正式进入 `Phase A no-op` 冻结态
  - Step 30 已明确 `Phase A` 的边界、bridge 保留范围、回滚策略和最小回归清单
  - Step 34 已转向不触 bridge 的展示级优化候选评估和演示资料整理
- 默认处理：继续保留旧 bridge，不做删除动作；后续不再默认寻找 bridge 候选，只有触发条件出现时才重开 bridge 主线
- repo 内已新增一轮真实局部留痕：
  - 真实 token 申请成功
  - 真实 workbench 加载成功
  - 真实确认 `profile / review-status` 优先走 `courier_token`
  - 真实确认无 `courier_token` 时不调用 courier 业务接口
  - 真实通过订单号 `CR202604060001` 回读 completed 订单详情
- 真实跑通 `CR202604070002` 的本地完整链路：onboarding -> 审核 -> token -> 接单 -> 取餐 -> deliver -> 异常上报 -> customer confirm -> completed 回读
- Step 23 已把该链路整理为可共享回归留痕：
  - `project-logs/campus-relay/step-23-shared-regression-evidence.md`
- 仍缺：
  - 若执行失败时的实际回滚操作演练

### 2. 前端 admin 侧已完成四个只读运营演示页和一个异常处理承接页

- 影响：admin 现在已有 settlement 批次演示页、售后执行演示页、courier 异常/位置联动演示页、settlement 只读运营页，以及异常历史 / resolve 最小承接页
- 默认处理：继续保持现有 admin 演示页和异常处理页稳定，不为补页数机械新增第五页

### 3. 售后执行历史后端和最小前端承接已落地，运行态验证已通过

- 影响：售后执行现在已有历史表、同事务写入、admin 只读分页接口和 drawer 内只读历史区
- 当前进展：Step 51A 已完成 `campus_after_sale_execution_record`、现有执行接口追加历史写入和 `GET /api/campus/admin/after-sale-execution-records`
- 当前进展：Step 51B 已选择方向 A，售后执行历史值得做最小前端承接
- 当前进展：Step 52 已在现有 `CampusAfterSaleExecutionList.vue` 详情 drawer 内增加只读历史区
- 当前进展：Step 53 已通过 H2/test API 和 Playwright 页面验证
- 默认处理：售后执行历史主线暂可收住，下一轮转向 P3 settlement 批次复核、撤回和对账方案设计

### 4. settlement 仍没有真实财务执行能力

- 影响：目前只有后台记录、批次和核对，尚无真实打款、撤回、对账对接
- 默认处理：继续保持最小运营记录，等业务边界稳定后再决定是否引入更深财务能力

## 当前明确没做的事情

- 没有重写 `frontend/` 主链路，只新增了最小 customer 页面、admin settlement 演示页与轻量脚本
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有接真实支付、真实退款、真实打款
- 没有做完整售后工单系统
- 没有做完整异常工单系统；异常历史表和最小 resolve 已完成，但没有做 ACKNOWLEDGED、reopen、delete、通知或多角色审批
- 没有做地图和轨迹大屏
- 没有新增第二套返回体

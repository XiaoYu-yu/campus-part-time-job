# 校园代送待处理事项

## Step 36 最高优先级

1. bridge 主线继续保持 `Phase A no-op` 冻结态，下一轮仍不默认寻找 bridge 收口候选。
2. 只有出现以下恢复推进触发条件时，才重新打开 bridge 主线：
   - 出现比 Step 31 / Step 32 更高收益且更低风险的新候选
   - repo 内出现新的 bridge 使用点或边界变化
   - 业务要求必须进一步收紧 bridge
   - 出现新的真实回归信号，证明当前保留策略开始有成本
   - 后续前端结构改造、入口整理或交付目标需要更清晰的桥接边界
3. Step 35 已完成第一轮小范围展示级优化执行，优先回看两个 P0 页面效果：
   - `frontend/src/views/courier/CourierWorkbench.vue`
   - `frontend/src/views/user/CampusOrderResult.vue`
4. 若没有触发条件，Step 36 可继续评估第二批展示级优化，但不要自动铺开所有页面。
5. Step 36 推荐二选一：
   - 只处理 `frontend/src/views/user/CourierOnboarding.vue` 的说明层级与表单展示一致性
   - 或只处理一个 admin 只读运营页的空态 / loading / tag 一致性
6. Step 36 明确禁止：
   - 不改 bridge
   - 不改 `request.js`
   - 不改 `campus-courier.js` bridge 行为
   - 不改 token 附着逻辑
   - 不改 API 调用顺序
   - 不改路由
   - 不新增页面
   - 不改后端状态机
7. onboarding 页、admin 四个只读运营页和 Profile 页仍属于第二批展示级优化候选，不建议下一轮一次性铺开。
8. 第五个 admin 页继续后置，不再以“补页数”为目标。
9. 视业务需要为售后执行、异常上报补更细粒度的历史审计能力。
10. 视业务需要为 settlement 补更完整的批次复核、撤回和对账能力。

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
  - 结算分页、详情、确认结算、单笔打款记录、批量打款记录、对账摘要、批次列表、批次详情、二次核对
  - 按订单查看位置记录、按订单查看异常摘要
- customer 已打通售后结果回执查询与 courier onboarding 前台入口
- customer onboarding 页面已补齐 courier token 申请动作衔接
- frontend 已打通 admin settlement 批次列表页、批次详情页、售后执行分页页、courier 异常/位置联动页和 settlement 只读运营页最小演示入口
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

- 异常仍只保留订单上的最新一次异常信息
- 不补异常历史表
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

### 2. 前端 admin 侧已完成四个最小演示页

- 影响：admin 现在已有 settlement 批次演示页、售后执行演示页、courier 异常/位置联动演示页和 settlement 只读运营页，但更多运营视图仍未接入
- 默认处理：继续保持现有四页稳定，不为补页数机械新增第五页

### 3. 售后与异常仍是最小审计模型

- 影响：售后执行和异常上报目前只保留当前结果与一次纠正信息，缺少完整历史
- 默认处理：在 Step 07 之后再评估是否需要单独历史表，当前先避免过度设计

### 4. settlement 仍没有真实财务执行能力

- 影响：目前只有后台记录、批次和核对，尚无真实打款、撤回、对账对接
- 默认处理：继续保持最小运营记录，等业务边界稳定后再决定是否引入更深财务能力

## 当前明确没做的事情

- 没有重写 `frontend/` 主链路，只新增了最小 customer 页面、admin settlement 演示页与轻量脚本
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有接真实支付、真实退款、真实打款
- 没有做完整售后工单系统
- 没有做异常历史表
- 没有做地图和轨迹大屏
- 没有新增第二套返回体

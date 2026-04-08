# 校园代送待处理事项

## Step 06 最高优先级

1. 为 customer 增加售后结果查询与最小退款/补偿处理回执
2. 为 admin 增加售后执行结果列表、结果修正查询和按执行状态的运营筛选
3. 为 settlement 增加批次审计明细、打款结果二次核对和更清晰的运营查询
4. 继续评估 `courier/profile` 与 `courier/review-status` 的 bridge 收口条件
5. 在不动旧系统的前提下，规划 `frontend/` 接入 campus 最小后台页面的路径

## 已完成但仍需继续扩展的部分

- `campus_*` 核心表、实体、DTO、VO、Mapper、Service、Controller 已齐全
- customer 已打通：创建单、模拟支付、列表、详情、确认送达、取消、售后
- courier 已打通：资料提交、资料详情、审核状态、token 发行、可接单列表、接单、取餐、配送推进、异常上报、位置上报
- admin 已打通：
  - 订单分页、详情、时间线
  - 售后处理、售后决策、售后执行结果记录
  - 售后结果分页、售后结果汇总
  - 配送员分页、审核、最近异常、低频位置记录
  - 结算分页、详情、确认结算、单笔打款记录、批量打款记录、对账摘要
  - 按订单查看位置记录、按订单查看异常摘要
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
- settlement confirm 后自动初始化 `payout_status = UNPAID`

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

### 1. customer 仍然看不到售后最终结果

- 影响：用户发起售后后只能依赖后台处理，缺少面向用户的结果回执
- 默认处理：Step 06 优先补 customer 侧售后结果只读接口

### 2. admin 运营结果仍缺少更强的审计查询

- 影响：已有售后执行记录和打款记录，但还缺批次、执行状态和修正视角的只读查询
- 默认处理：Step 06 补运营查询，不急于做复杂页面

### 3. bridge 仍是过渡态

- 影响：`courier/profile` 与 `courier/review-status` 继续依赖双 token 兼容
- 默认处理：继续保留，等 onboarding 替代链路稳定后再收口

### 4. 前端尚未接 campus 新接口

- 影响：现阶段主要靠测试和后端联调演示
- 默认处理：继续保持 `frontend/` 不动，避免范围膨胀

## 当前明确没做的事情

- 没有改 `frontend/`
- 没有删除旧外卖模块
- 没有改旧 `orders/cart/address` 语义
- 没有接真实支付、真实退款、真实打款
- 没有做完整售后工单系统
- 没有做异常历史表
- 没有做地图和轨迹大屏
- 没有新增第二套返回体

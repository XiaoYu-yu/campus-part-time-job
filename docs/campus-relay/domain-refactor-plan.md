# 校园代送领域模型重构规划

> 状态：规划中，未实现
>
> 本文用于锁定“重庆工信职业学院渝中校区校园代送平台”第一轮重构方案。本轮只落规划文档，不改运行时代码、不改数据库结构、不改变现有接口行为。

## 1. 本轮目标与约束

- 目标不是立刻把现有外卖代码硬改成代送系统，而是先把改造边界、阶段顺序和核心模型写清楚。
- 现有 `backend/` 继续保留，不新建替代后端。
- 现有 `frontend/` 继续承担管理端，同时保留当前用户 Web 页面用于接口联调和阶段性演示。
- 真正的移动端放到后续 uni-app 轮次，目录默认规划为 `uni-app/`，本轮不创建。
- 第一版支付采用平台内模拟支付，只做支付状态流转，不接第三方收款。
- 第一版不做自动提现、自动分账、实时轨迹动画、复杂教室课表联动。

## 2. 新业务边界

### 2.1 平台负责什么

- 用户已在校外平台完成点餐与餐费支付。
- 外卖已送到学校北门取餐点。
- 用户在本平台发布校内代送单。
- 兼职配送员接单、取餐、送达。
- 平台只处理校内最后一段代送服务、模拟支付状态、配送过程、订单确认、售后投诉和待结算记录。

### 2.2 平台不负责什么

- 不管理商家、菜品、套餐。
- 不参与餐费支付，不对接商家结算。
- 不做外部骑手调度。
- 不做高频实时定位轨迹动画。
- 不做第一版真实支付和真实提现。

## 3. 角色与权限规划

### 3.1 Public

- 游客可查看平台公告、取餐点信息、配送规则、楼栋覆盖范围、兼职申请说明。

### 3.2 Customer

- 注册字段以手机号为主账号，补充学号或工号。
- 可创建代送单、查看订单、模拟支付代送费、确认收餐、发起售后。
- Customer 账户沿用现有 `user` 作为基础登录身份，下一轮在该身份体系上扩展校园字段，不新建第二套普通用户登录表。

### 3.3 Courier

- Courier 不是 `employee` 的别名，也不是后台账号。
- Courier 通过独立的 `CourierProfile` 承载实名认证、宿舍楼栋、课程表、人工审核状态等信息。
- Courier 使用普通用户基础身份登录，再通过独立配送员资料表和审核状态控制接单资格。

### 3.4 Admin

- 现有 `employee` 继续承担后台管理员、审核人员、运营人员身份。
- 管理员负责审核兼职申请、查看代送单、处理投诉、查看待结算记录和配置校园规则。
- 第一版不新增独立后台鉴权体系，继续复用现有管理端 JWT 和管理员登录链路。

## 4. 校园规则落模方式

### 4.1 固定取餐点

第一版固定两个取餐点，作为 `PickupPoint` 初始化数据：

1. 主大门门卫室西侧临时取餐区
2. 主大门外卖柜旁固定取餐区

每个取餐点都需要保留：

- 名称
- 所属区域
- 描述
- 是否启用
- 排序

### 4.2 配送目标类型

`DeliveryTarget` 固定拆成四类：

1. `DORMITORY`
2. `TEACHING_BUILDING`
3. `OFFICE`
4. `LIBRARY`

每一单都保存目标类型快照、楼栋名称、用户填写的补充描述和系统生成的规则说明，避免后续校园规则变化影响历史订单展示。

### 4.3 宿舍楼规则

宿舍楼固定枚举：

- 竹园
- 杏园
- 李园
- 桃园
- 梅园
- 馨园

系统规则固定如下：

- 宿舍订单创建并完成支付后，若目标为宿舍楼，进入 5 分钟本楼栋优先窗口。
- 订单上保留 `priorityDormitoryBuilding` 和 `priorityWindowDeadline` 字段。
- 只有 `CourierProfile.dormitoryBuilding` 与订单目标楼栋一致的配送员，才能在优先窗口内看到该订单。
- 优先窗口结束后，订单才向全校已审核通过的配送员开放。
- 非本楼栋配送员可接单，但系统文案必须提示“仅可送至宿舍楼下或门厅，不可进入宿舍楼层”。
- 本楼栋配送员允许送至宿舍门口，该能力由接单时的楼栋匹配结果决定，而不是由用户自由绕过规则。

### 4.4 教学楼、办公区、图书馆规则

- 教学楼默认只支持“楼下”或“门厅”交付，不提供“教室门口”选项。
- 第一版不做上课时段识别，直接通过 UI 只暴露允许的交付点来规避违规配送。
- 教师办公室订单允许送到办公室门口，但规则文案必须明确为“指定办公室门口”，不是任意办公区域通行。
- 图书馆只允许一楼门口和二楼门口两个目标点。
- 后续如果要细化建筑内路径或课表限制，作为第二阶段增强，不进入第一版。

## 5. 核心领域对象

### 5.1 RelayOrder

用途：承载校园代送单主记录，替代现有外卖订单语义。

第一版必须预留的关键字段：

- `id` / `orderNo`：继续采用字符串订单号，便于复用现有分页与检索风格。
- `customerUserId`
- `courierProfileId`
- `pickupPointId`
- `deliveryTargetType`
- `deliveryBuilding`
- `deliveryDetail`
- `deliveryContactName`
- `deliveryContactPhone`
- `foodDescription`
- `externalPlatformName`
- `externalOrderRef`
- `pickupCode`
- `baseFee`
- `priorityFee`
- `tipFee`
- `totalAmount`
- `paymentStatus`
- `orderStatus`
- `priorityDormitoryBuilding`
- `priorityWindowDeadline`
- `acceptedAt`
- `cancelLockedUntil`
- `pickedUpAt`
- `deliveredAt`
- `autoCompleteAt`
- `pickupProofImageUrl`
- `customerRemark`
- `courierRemark`
- `afterSaleReason`
- `createdAt`
- `updatedAt`

### 5.2 CourierProfile

用途：承载兼职配送员资料和审核状态，不复用 `employee`。

第一版必须预留的关键字段：

- `id`
- `userId`
- `realName`
- `studentNo`
- `college`
- `major`
- `className`
- `dormitoryBuilding`
- `dormitoryRoom`
- `idCardLast4`
- `emergencyContactName`
- `emergencyContactPhone`
- `verificationPhotoUrl`
- `scheduleAttachmentUrl`
- `reviewStatus`
- `reviewComment`
- `reviewedByEmployeeId`
- `reviewedAt`
- `enabled`
- `createdAt`
- `updatedAt`

### 5.3 PickupPoint

用途：存放固定取餐点字典，由后台维护启停状态和排序。

关键字段：

- `id`
- `code`
- `name`
- `gateArea`
- `description`
- `enabled`
- `sort`

### 5.4 DeliveryTarget

用途：统一表达配送目标规则和订单快照，不再使用旧“收货地址”语义。

关键字段：

- `targetType`
- `buildingName`
- `specificPoint`
- `roomOrOfficeNo`
- `handoffMode`
- `accessRuleText`
- `visibilityRuleText`

说明：

- `DeliveryTarget` 是新领域对象，不等价于现有 `Address`。
- 订单表保存快照，后台可维护一份目标点字典或规则模板，但第一版以订单快照可落地为先。

### 5.5 CourierLocationReport

用途：存放配送中的低频位置上报记录。

关键字段：

- `id`
- `relayOrderId`
- `courierProfileId`
- `latitude`
- `longitude`
- `reportedAt`
- `source`
- `note`

说明：

- 第一版只要求记录最新点位和有限历史记录，不做轨迹动画。
- 位置能力和地图展示能力分离；即使地图暂未接入，也可先展示“最近一次上报时间 + 经纬度/文字位置”。

### 5.6 SettlementRecord

用途：记录兼职配送员待结算收入。

关键字段：

- `id`
- `relayOrderId`
- `courierProfileId`
- `grossAmount`
- `platformCommission`
- `pendingAmount`
- `settlementStatus`
- `settledAt`
- `remark`

说明：

- 第一版 `platformCommission` 固定为 `0`。
- 第一版只显示待结算，不做自动提现和自动分账。

## 6. 状态枚举与状态机

### 6.1 订单状态 `RelayOrderStatus`

第一版固定以下状态，后续接口和数据库按此命名收敛：

1. `PENDING_PAYMENT`：待支付
2. `BUILDING_PRIORITY_PENDING`：宿舍楼本楼栋优先窗口中
3. `WAITING_ACCEPT`：开放接单中
4. `ACCEPTED`：已接单，进入 5 分钟取消锁定计时
5. `PICKED_UP`：已取餐，已上传取餐凭证
6. `DELIVERING`：配送中，允许位置上报
7. `AWAITING_CONFIRMATION`：已送达待用户确认
8. `COMPLETED`：已完成
9. `AFTER_SALE_OPEN`：售后中
10. `CANCELLED`：已取消

状态流转固定如下：

- 创建订单后进入 `PENDING_PAYMENT`
- 模拟支付成功后：
  - 宿舍单进入 `BUILDING_PRIORITY_PENDING`
  - 非宿舍单直接进入 `WAITING_ACCEPT`
- 宿舍优先窗口超时后，从 `BUILDING_PRIORITY_PENDING` 进入 `WAITING_ACCEPT`
- 配送员接单后进入 `ACCEPTED`
- 取餐拍照并提交后进入 `PICKED_UP`
- 配送员开始配送或首次位置上报后进入 `DELIVERING`
- 配送员点击已送达后进入 `AWAITING_CONFIRMATION`
- 用户确认收餐或超时 10 分钟自动完成后进入 `COMPLETED`
- 已取餐后若产生投诉或异常，进入 `AFTER_SALE_OPEN`
- 在允许取消的前置状态下可进入 `CANCELLED`

取消规则固定如下：

- 未支付可直接取消。
- 待接单阶段可取消。
- 接单后 5 分钟内不可取消，系统通过 `cancelLockedUntil` 控制。
- 已取餐后不能直接取消，只能转售后。

### 6.2 支付状态 `RelayPaymentStatus`

第一版固定最小状态集：

1. `UNPAID`：未支付
2. `PAID`：已支付
3. `REFUND_PENDING`：退款处理中
4. `REFUNDED`：已退款
5. `CLOSED`：已关闭

说明：

- 第一版“支付成功”由平台内模拟动作触发。
- 不接第三方支付网关，但状态字段和日志要保留后续接入空间。

### 6.3 兼职审核状态 `CourierReviewStatus`

第一版固定以下状态：

1. `PENDING`
2. `APPROVED`
3. `REJECTED`
4. `DISABLED`

说明：

- 只有 `APPROVED` 且 `enabled=true` 的 `CourierProfile` 才允许进入可接单列表。
- `REJECTED` 需要保留驳回原因。
- `DISABLED` 用于后续违规停用，不和审核驳回混用。

## 7. 支付、接单、结算策略

### 7.1 费用

- 基础代送费固定 `3 元`
- 加急加价固定允许区间 `3 - 5 元`
- 打赏固定允许区间 `1 - 10 元`
- 第一版平台抽成固定为 `0`

总金额计算固定为：

`baseFee + priorityFee + tipFee`

### 7.2 模拟支付

- Customer 在订单详情或提交页点击支付后，系统直接写入 `PAID`。
- 不生成第三方支付单号。
- 如需退款，只在后台或售后流程中修改为 `REFUND_PENDING / REFUNDED`。

### 7.3 接单资格

- Courier 必须审核通过。
- Courier 必须处于启用状态。
- 宿舍优先窗口内，只有同楼栋配送员可见。
- 宿舍优先窗口后，全校配送员可见，但非本楼栋不能进入宿舍楼层。

### 7.4 待结算

- 订单 `COMPLETED` 后生成或更新 `SettlementRecord`。
- 待结算金额默认为订单总代送费，不扣平台抽成。
- 第一版后台只查看待结算列表，不触发打款。

## 8. API 命名空间与未来接口边界

> 以下接口均为规划中，未实现。

### 8.1 `/api/campus/public/*`

- `GET /api/campus/public/config`
- `GET /api/campus/public/pickup-points`
- `GET /api/campus/public/delivery-rules`
- `GET /api/campus/public/buildings`

### 8.2 `/api/campus/customer/*`

- `POST /api/campus/customer/auth/login`
- `POST /api/campus/customer/orders`
- `GET /api/campus/customer/orders`
- `GET /api/campus/customer/orders/{id}`
- `POST /api/campus/customer/orders/{id}/pay`
- `POST /api/campus/customer/orders/{id}/confirm`
- `POST /api/campus/customer/orders/{id}/after-sale`

### 8.3 `/api/campus/courier/*`

- `POST /api/campus/courier/auth/login`
- `POST /api/campus/courier/profile`
- `GET /api/campus/courier/profile`
- `GET /api/campus/courier/orders/available`
- `POST /api/campus/courier/orders/{id}/accept`
- `POST /api/campus/courier/orders/{id}/pickup`
- `POST /api/campus/courier/orders/{id}/location-reports`
- `POST /api/campus/courier/orders/{id}/arrive`

### 8.4 `/api/campus/admin/*`

- `GET /api/campus/admin/orders`
- `GET /api/campus/admin/orders/{id}`
- `GET /api/campus/admin/couriers`
- `POST /api/campus/admin/couriers/{id}/approve`
- `POST /api/campus/admin/couriers/{id}/reject`
- `POST /api/campus/admin/couriers/{id}/disable`
- `GET /api/campus/admin/settlements`
- `GET /api/campus/admin/rules`
- `PUT /api/campus/admin/rules`

## 9. 技术并行改造策略

### 9.1 后端包结构

下一轮开始新增校园代送模块时，Java 包默认放在：

`com.cangqiong.takeaway.campus`

建议细分为：

- `controller.campus`
- `service.campus`
- `mapper.campus`
- `entity.campus`
- `dto.campus`
- `vo.campus`

### 9.2 数据库命名

下一轮新增校园代送表时，默认统一使用 `campus_` 前缀，避免和旧外卖表语义冲突。

示例：

- `campus_relay_order`
- `campus_courier_profile`
- `campus_pickup_point`
- `campus_location_report`
- `campus_settlement_record`

### 9.3 旧外卖模型的处理策略

- 不把 `dish/setmeal/cart/address` 原地改名成校园语义。
- 新旧模型并行一段时间，先让校园代送能力可开发、可测试、可演示。
- 旧用户点餐链路和旧表在第 09 轮统一收口，而不是在第 02 轮一次性删除。

### 9.4 前端并行策略

- `frontend/` 继续保留管理端。
- 现有用户 Web 页面先保留，用于代送 API 联调时的过渡页或演示页。
- 后续新增校园代送前端代码时，默认落在：
  - `frontend/src/api/campus/`
  - `frontend/src/views/campus/`
- 真正的移动端工程后续单独建立 `uni-app/`。

## 10. 位置上报与刷新策略

- 位置能力默认只在订单进入 `DELIVERING` 后启用。
- 配送员端默认每 60 秒允许一次自动上报，状态切换节点允许立即补报一次。
- 用户端和后台默认每 60 秒轮询最新位置，不使用 WebSocket。
- 第一版地图功能不是阻塞项；如果暂未接入地图 SDK，至少展示最后一次上报时间和位置文本。
- 第一版不做轨迹回放、不做平滑动画、不做高频点位刷新。

## 11. 分阶段路线

1. 第 01 轮：领域模型重构规划
2. 第 02 轮：后端领域与数据库改造
3. 第 03 轮：认证与角色改造
4. 第 04 轮：代送订单闭环 API
5. 第 05 轮：uni-app 移动端工程初始化
6. 第 06 轮：用户端 MVP
7. 第 07 轮：兼职端 MVP
8. 第 08 轮：后台管理端二阶段规划与替换
9. 第 09 轮：技术债清理、旧模块隐藏或删除、文档收口

## 12. 本轮结论

- 高层方案已经锁定为“并行新增校园代送域”，不是“原地硬改所有外卖表”。
- 管理员继续复用 `employee`；配送员单独建模为 `CourierProfile`。
- 旧外卖页面和旧表暂时保留，避免第 02 轮直接爆炸式重构。
- 下一轮直接进入数据库与后端领域落地，不再回到“要不要并行建模”“要不要真实支付”“要不要先删旧模块”这些问题上反复讨论。

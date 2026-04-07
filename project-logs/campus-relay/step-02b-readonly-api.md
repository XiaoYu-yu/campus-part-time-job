# Step 02B - 只读接口完善

## 本次目标

在已经完成的 campus 数据结构和后端骨架基础上，只完善以下 4 个只读接口及其查询层，不进入写接口与前端改造：

1. `/api/campus/public/pickup-points`
2. `/api/campus/public/delivery-rules`
3. `/api/campus/admin/orders`
4. `/api/campus/admin/couriers`

## 本次实际扫描的文件

- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusPublicController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminRelayOrderController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusAdminCourierController.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusRelayOrderQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/query/CampusCourierQuery.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusDeliveryRuleVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusRelayOrderVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/vo/CampusCourierProfileVO.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/support/CampusRuleCatalog.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusRelayOrderMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/mapper/CampusCourierProfileMapper.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusRelayOrderServiceImpl.java`
- `backend/src/main/java/com/cangqiong/takeaway/campus/service/impl/CampusCourierProfileServiceImpl.java`
- `backend/src/test/java/com/cangqiong/takeaway/CampusSkeletonIntegrationTest.java`
- `docs/campus-relay/domain-refactor-plan.md`
- `project-logs/campus-relay/step-02a-db-and-backend-skeleton.md`

## 本次实际改动

### public 接口

- `pickup-points` 继续从 `campus_pickup_point` 读取启用点位，按 `sort` 升序返回
- `delivery-rules` 改为结构化返回：
  - 宿舍楼栋列表
  - 教学楼列表
  - 图书馆支持点位
  - 宿舍优先窗口分钟数
  - 基础费、加急区间、打赏区间
  - 位置上报/刷新频率
  - 规则文案仍集中在 `CampusRuleCatalog`

### admin 订单查询

- `CampusRelayOrderQuery` 补充：
  - `customerName`
  - `courierName`
  - `status`
  - `deliveryBuilding`
  - `pickupPointId`
  - `beginTime`
  - `endTime`
  - `size`
- `CampusRelayOrderMapper` 改为显式字段查询并关联：
  - `campus_pickup_point`
  - `campus_customer_profile`
  - `campus_courier_profile`
- `CampusRelayOrderVO` 增补：
  - `customerName`
  - `customerPhone`
  - `courierName`
  - `pickupPointCode`
  - `pickupPointName`
  - `foodDescription`
  - `externalPlatformName`
  - `externalOrderRef`
  - `pickupCode`
  - `baseFee`
  - `priorityFee`
  - `tipFee`
  - `status`
  - `acceptedAt`
  - `pickedUpAt`
  - `deliveredAt`
- `CampusRelayOrderServiceImpl` 兼容 `pageSize` 和 `size`，并统一空白参数处理

### admin 配送员查询

- `CampusCourierQuery` 补充：
  - `phone`
  - `studentNo`
  - `college`
  - `size`
- `CampusCourierProfileMapper` 改为显式字段查询并关联：
  - `user`
  - `employee`
- `CampusCourierProfileVO` 增补：
  - `phone`
  - `reviewedByName`
- `CampusCourierProfileServiceImpl` 兼容 `pageSize` 和 `size`，并统一空白参数处理

### controller 层

- 3 个 controller 均补充明确日志输出
- 路由不变，仍只保留只读 GET 接口
- `Result` 和 `PageResult` 继续复用，没有新增第二套返回体

## 本次实际开放的接口

- `GET /api/campus/public/pickup-points`
- `GET /api/campus/public/delivery-rules`
- `GET /api/campus/admin/orders`
- `GET /api/campus/admin/couriers`

## 本次没有做的事情

- 没有做 customer 写接口
- 没有做 courier 写接口
- 没有做创建代送单、模拟支付、接单、取餐、完成闭环
- 没有改 `frontend/`
- 没有删除旧外卖模块

## 本次测试与结果

### 编译

- 在 `backend/` 执行：`.\mvnw.cmd -DskipTests compile`
- 结果：通过

### 测试

- 在 `backend/` 执行：`.\mvnw.cmd test`
- 结果：通过
- 汇总：`14` 个测试全部通过，`0` 失败，`0` 错误，`0` 跳过

### 本次新增覆盖点

1. `pickup-points` 返回启用点位和排序结果
2. `delivery-rules` 返回结构化规则字段
3. `admin/orders` 未登录 `401`、customer token `403`、employee token `200`
4. `admin/couriers` 未登录 `401`、customer token `403`、employee token `200`
5. `admin/orders` 支持筛选与分页元数据返回
6. `admin/couriers` 支持筛选与分页元数据返回

## 当前仍未解决的风险

1. `/api/campus/courier/**` 仍无真实登录与 token 发行链路
2. customer profile 仍未进入资料补录与注册完善流程
3. admin 只读列表已完善，但详情接口、审核接口、状态流转接口仍未实现
4. 规则仍是代码常量，尚未后台配置化
5. 数据库种子中的中文字段在测试链路里不适合作为强断言主字段，后续回归测试需继续以编码、状态、手机号、订单号为主

## Step 03 建议

1. 先补 `/api/campus/customer/orders` 的创建与模拟支付
2. 再补 `/api/campus/courier/profile` 的资料提交与状态查询
3. 再补 `/api/campus/admin/orders/{id}` 和 courier 审核接口
4. 保持 `frontend/` 继续不动，先把后端写接口和详情接口收口

# Step 119 - 用户端校园代送下单 / 我的代送单最小入口

## 本轮目标

1. 在用户端移动 H5 中补一个“校园代送下单 / 我的代送单”最小主入口。
2. 复用现有 customer campus 后端接口，不新增后端写接口。
3. 只接入创建代送单、我的代送单列表和模拟支付，不改订单状态机、不接真实支付。
4. 保持 bridge、鉴权、`request.js`、旧外卖模块和 Android 原生壳结构不变。

## 为什么本轮做用户端代送入口

Step 117 / Step 118 已经把用户端移动首页从旧外卖首页收敛到校园兼职 / 校园代送语义，并完成了登录、首页、结果回看和入驻页真实移动视口 smoke。

当前用户端仍缺“从用户端发起一笔校园代送单”的主入口。该缺口会导致试运营自测只能依赖已有样本订单，而不能由用户端自己创建新订单。因此本轮优先补“发布代送 + 我的代送单 + 模拟支付”的最小闭环。

## 实际完成

### 1. API 封装

更新 `frontend/src/api/campus-customer.js`，新增最小 customer campus API：

1. `getCampusPickupPoints`
   - 调用 `GET /api/campus/public/pickup-points`
2. `getCampusDeliveryRules`
   - 调用 `GET /api/campus/public/delivery-rules`
3. `createCampusCustomerOrder`
   - 调用 `POST /api/campus/customer/orders`
4. `getCampusCustomerOrders`
   - 调用 `GET /api/campus/customer/orders`
5. `mockPayCampusCustomerOrder`
   - 调用 `POST /api/campus/customer/orders/{id}/mock-pay`

没有复制 request 基础逻辑，没有新增第二套 token 管理。

### 2. 新增用户端页面

新增 `frontend/src/views/user/CampusRelayOrders.vue`。

页面能力：

1. 顶部说明当前只做 campus 代送单创建和模拟支付。
2. 从 public 接口读取取餐点和配送规则。
3. 提供最小创建表单：
   - 取餐点
   - 送达类型
   - 送达楼栋
   - 送达详情
   - 联系人
   - 手机号
   - 外卖内容
   - 外部平台
   - 外部订单号
   - 取餐码
   - 打赏金额
   - 是否加急
   - 备注
4. 创建成功后展示新订单号。
5. 新订单可直接点击“模拟支付”进入待接单链路。
6. 支持“结果回看”跳转到现有 `/user/campus/order-result?orderId=...`。
7. 下方展示“我的代送单”分页列表。
8. 列表支持最小筛选：
   - `orderNo`
   - `status`
9. 只允许 `PENDING_PAYMENT + UNPAID` 的订单展示模拟支付按钮。

### 3. 用户端入口

更新 `frontend/src/router/index.js`：

1. 新增路由：
   - `/user/campus/orders`
   - name: `UserCampusRelayOrders`
   - 需要 customer auth

更新 `frontend/src/layout/UserLayout.vue`：

1. 用户端底部导航从 4 项调整为 5 项。
2. 新增“代送”入口：
   - `/user/campus/orders`
3. 保留：
   - 首页
   - 结果
   - 入驻
   - 我的

更新 `frontend/src/views/user/Home.vue`：

1. hero 主按钮改为“发布代送单”，跳转 `/user/campus/orders`。
2. 保留结果回看、入驻状态和旧模块兼容入口。
3. 增加“发布代送”快捷卡片。

## 兼容策略

1. 仍然不接真实支付，只调用 mock-pay。
2. 不改变旧 `orders/cart/address` 语义。
3. 不删除旧外卖模块。
4. 不改订单主状态机。
5. 不改 bridge：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
6. 不改 `request.js` 或 token 附着逻辑。
7. 不改后端接口和 Android 原生壳结构。

## 真实 smoke 留痕

本轮本地启动 backend(test profile) 与 frontend dev，使用 390x844 移动视口完成真实用户端 smoke。

验证账号：

1. customer：`13900139000 / 123456`

实际流程：

1. 打开 `/user/login`。
2. 登录用户端。
3. 打开 `/user/campus/orders`。
4. 验证取餐点、配送规则、我的代送单分页接口均返回 200。
5. 点击“创建代送单”。
6. 成功创建订单：
   - `CR202604251658356537`
7. 点击新订单卡片中的“模拟支付”。
8. 确认模拟支付。
9. mock-pay 返回 200，并刷新我的代送单列表。

接口留痕：

1. `POST /api/users/login => 200`
2. `GET /api/campus/public/delivery-rules => 200`
3. `GET /api/campus/public/pickup-points => 200`
4. `GET /api/campus/customer/orders?page=1&pageSize=5 => 200`
5. `POST /api/campus/customer/orders => 200`
6. `POST /api/campus/customer/orders/CR202604251658356537/mock-pay => 200`

截图：

1. `project-logs/campus-relay/runtime/step-119-user-campus-orders-smoke/screenshots/user-campus-orders-mobile.png`
2. `project-logs/campus-relay/runtime/step-119-user-campus-orders-smoke/screenshots/user-campus-orders-created-mobile.png`
3. `project-logs/campus-relay/runtime/step-119-user-campus-orders-smoke/screenshots/user-campus-orders-paid-mobile.png`

结构化验证记录：

1. `project-logs/campus-relay/runtime/step-119-user-campus-orders-smoke/user-campus-orders-validation.json`

## 验证结果

1. `frontend npm run lint` 通过。
2. `frontend npm run build` 通过。
3. `backend .\mvnw.cmd -DskipTests compile` 通过。
4. `frontend npm run build:android:user` 通过。
5. `frontend npm run build:android:parttime` 通过。
6. `mobile/user-app npm run cap:sync` 通过。
7. `mobile/parttime-app npm run cap:sync` 通过。
8. `mobile/user-app/android .\gradlew.bat assembleDebug --no-daemon` 通过。
   - 首次使用默认 Java 17 失败，原因是 Android 构建要求 source 21。
   - 临时切换 `JAVA_HOME=D:\software\jdk-21-temurin` 后通过。
9. `mobile/parttime-app/android .\gradlew.bat assembleDebug --no-daemon` 通过。
   - 同样使用 `JAVA_HOME=D:\software\jdk-21-temurin`。
10. `git diff --check` 通过。
    - 仅有 Windows 工作区 LF/CRLF 提示。

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 token 附着逻辑。
4. 未改 API 调用顺序。
5. 未改后端接口、鉴权或订单状态机。
6. 未改 Android 原生工程结构。
7. 未删除旧外卖模块。
8. 未新增第五个 admin 页。
9. 未接真实支付、真实退款或真实打款。

## 当前结论

1. 用户端现在具备“登录 -> 首页 -> 发布校园代送单 -> 模拟支付 -> 我的代送单 -> 结果回看”的最小移动端闭环。
2. 本轮只补用户端移动 H5 的校园代送主入口，没有修改后端状态机或旧系统语义。
3. bridge 继续保持 `Phase A no-op` 冻结态。
4. 展示 polish 主线继续保持冻结/维护态；本轮是产品主入口补齐，不是重新开启无限 polish。

## 遗留问题

1. Android 真机 / 公网服务器 API base 仍需和本地模拟器 `10.0.2.2` 分层。
2. 本轮完成的是浏览器移动视口真实 smoke，没有再安装 APK 到模拟器里跑新页面。
3. 用户端当前仍是 H5 / WebView 共用页面，尚未做原生 Android 页面。

## 下一步建议

1. Step 120 优先做 Android / 内测 API base 分层评估：
   - 本地模拟器继续使用 `10.0.2.2`
   - 局域网真机应使用宿主机局域网 IP
   - 服务器内测应使用公网域名或公网 IP + 反向代理 / HTTPS 规划
2. 若继续产品主链路，可做用户端订单详情 / 取消 / confirm 的最小入口评估，但必须避免改订单状态机。
3. 仍不建议重开 bridge、补第五个 admin 页或接真实支付。

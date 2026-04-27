# 旧外卖模块删除前审计与分阶段收口计划

## 用途

本文件是旧外卖（苍穹外卖）模块能否删除的完整审计入口。当前结论是：**不能直接删除，只能分阶段收口**。

本文件会持续更新，每次旧模块状态变化时同步记录。

---

## 一、旧外卖模块清单

### 1.1 前端旧页面（admin 端，非 campus / 非 user / 非 courier）

| 文件 | 页面说明 | 路由 |
|---|---|---|
| `Login.vue` | 管理员登录页 | `/login` |
| `Dashboard.vue` | 运营总览仪表盘 | `/dashboard` |
| `Employee.vue` | 运营人员管理 | `/employee` |
| `Statistics.vue` | 数据看板 | `/statistics` |
| `Category.vue` | 分类管理（旧外卖） | `/category` |
| `Dish.vue` | 菜品管理（旧外卖） | `/dish` |
| `Setmeal.vue` | 套餐管理（旧外卖） | `/setmeal` |
| `Order.vue` | 订单管理（旧外卖） | `/order` |
| `ShopStatus.vue` | 店铺营业状态（旧外卖） | `/shop-status` |
| `ComponentDemo.vue` | 组件演示页 | `/component-demo` |

**共 10 个旧 admin 页面**。其中 `Login.vue`、`Dashboard.vue`、`Employee.vue`、`Statistics.vue` 已被当前 campus 运营台复用（视觉已刷新），**不可删除**。

### 1.2 前端旧 API 文件

| 文件 | 用途 | 是否仍被使用 |
|---|---|---|
| `address.js` | 用户地址 API | 用户端 checkout 复用 |
| `cart.js` | 购物车 API | 用户端 cart 复用 |
| `category.js` | 分类 API | admin Category.vue 使用 |
| `customer-order.js` | 用户订单 API | 用户端 Orders.vue 复用 |
| `customer.js` | 用户登录/个人信息 API | 用户端 Login/Profile 复用 |
| `dish.js` | 菜品 API | admin Dish.vue 使用 |
| `employee.js` | 员工登录 API | admin 登录使用 |
| `order.js` | 管理端订单 API | admin Order.vue 使用 |
| `public.js` | 公开接口（分类/菜品/店铺状态） | 用户端 Home 复用 |
| `setmeal.js` | 套餐 API | admin Setmeal.vue 使用 |
| `shop.js` | 店铺状态 API | admin ShopStatus.vue 使用 |
| `statistics.js` | 统计 API | admin Statistics.vue 使用 |
| `upload.js` | 文件上传 API | 全局复用（包括 campus onboarding） |

**共 13 个旧 API 文件**。其中 `address.js`、`cart.js`、`customer-order.js`、`customer.js`、`public.js`、`upload.js` 被用户端或 campus onboarding 链使用，**不可删除**。其余 7 个仅被旧 admin 页面引用。

### 1.3 后端旧 Controller（旧外卖模块）

| Controller | 路由前缀 | 是否仍被 campus 使用 |
|---|---|---|
| `AddressController.java` | `/api/address` | 用户端复用 |
| `CartController.java` | `/api/cart` | 用户端复用 |
| `CategoryController.java` | `/api/admin/category` | 仅旧页面 |
| `DishController.java` | `/api/admin/dish` | 仅旧页面 |
| `EmployeeController.java` | `/api/employees` | **admin 登录核心**，不可删 |
| `FileController.java` | `/api/files` | 全局复用（含 campus 凭证上传） |
| `OrderController.java` | `/api/admin/order` | 仅旧页面 |
| `PublicController.java` | `/api/public` | 用户端复用 |
| `SetmealController.java` | `/api/admin/setmeal` | 仅旧页面 |
| `ShopController.java` | `/api/admin/shop` | 仅旧页面 |
| `StatisticsController.java` | `/api/admin/statistics` | admin 数据看板使用 |
| `UploadController.java` | `/api/upload` | 全局复用 |
| `UserController.java` | `/api/users` | **用户端登录核心**，不可删 |
| `UserOrderController.java` | `/api/user/order` | 用户端订单复用 |

**共 14 个旧 Controller**。其中 EmployeeController、UserController、FileController、UploadController、PublicController 被 campus 当前逻辑直接或间接依赖，**不可删除**。AddressController、CartController、UserOrderController 被用户端复用，**不可删除**。

### 1.4 后端旧 Service / ServiceImpl

| Service | 是否仍被 campus 使用 |
|---|---|
| `CategoryService` / `CategoryServiceImpl` | 仅旧页面 |
| `DishService` / `DishServiceImpl` | 仅旧页面 |
| `EmployeeService` / `EmployeeServiceImpl` | **admin 登录核心** |
| `LocalFileStorageService` / `LocalFileStorageServiceImpl` | 全局复用 |
| `OrderService` / `OrderServiceImpl` | 仅旧页面 |
| `PasswordService` / `PasswordServiceImpl` | campus 模块使用（BCrypt） |
| `SetmealService` / `SetmealServiceImpl` | 仅旧页面 |
| `StatisticsService` / `StatisticsServiceImpl` | admin 数据看板依赖 |
| `UserService` / `UserServiceImpl` | **campus 直接依赖**（`CampusCourierAuthController`） |

**共 9 个旧 Service + 9 个 Impl**。其中 EmployeeService、UserService、PasswordService、LocalFileStorageService、StatisticsService 当前被 campus 或 admin 依赖，**不可删除**。

### 1.5 后端旧 Mapper

| Mapper | 是否仍被 campus 使用 |
|---|---|
| `AddressMapper.java` | 用户端复用 |
| `CartMapper.java` | 用户端复用 |
| `CategoryMapper.java` | 仅旧页面 |
| `DishMapper.java` | 仅旧页面 |
| `EmployeeMapper.java` | **admin 登录核心** |
| `OrderItemMapper.java` | 仅旧页面 |
| `OrderMapper.java` | 仅旧页面 |
| `SetmealDishMapper.java` | 仅旧页面 |
| `SetmealMapper.java` | 仅旧页面 |
| `ShopConfigMapper.java` | 仅旧页面 |
| `StatisticsMapper.java` | admin 数据看板依赖 |
| `UserMapper.java` | **campus 直接依赖**（`CampusCourierProfileServiceImpl`） |

**共 12 个旧 Mapper**。其中 EmployeeMapper、UserMapper 被 campus 直接依赖，AddressMapper、CartMapper 被用户端复用，StatisticsMapper 被 admin 数据看板依赖，**不可删除**。

### 1.6 后端旧 Entity / DTO / VO

| 类型 | 数量 | 说明 |
|---|---|---|
| Entity | 12 | User、Employee、Order、OrderItem、Cart、Address、Category、Dish、Setmeal、SetmealDish、ShopConfig、ShopBusinessHour |
| DTO | 12 | AddressDTO、BusinessHourDTO、CartItemDTO 等 |
| VO | 16 | OrderVO、DishVO、SetmealVO、StatisticsVO 等 |
| Query | 4 | DishQuery、EmployeeQuery、OrderQuery、SetmealQuery |

### 1.7 数据库旧表

| 表名 | 是否仍被 campus 使用 | 删除可行性 |
|---|---|---|
| `employee` | **是** — campus admin 登录、审核、结算等外键引用 | **不可删除** |
| `user` | **是** — campus 下单、onboarding、courier 关联 | **不可删除** |
| `orders` | 否 — campus 使用 campus_relay_order | Phase 4 评估 |
| `order_item` | 否 — 关联旧 orders | Phase 4 评估 |
| `cart` | 否 — 但用户端仍可访问旧购物车 | Phase 4 评估 |
| `address` | 否 — 但用户端仍可访问旧地址 | Phase 4 评估 |
| `category` | 否 — 但旧页面和 H2 种子依赖 | Phase 4 评估 |
| `dish` | 否 — 但旧页面和 H2 种子依赖 | Phase 4 评估 |
| `setmeal` | 否 — 但旧页面和 H2 种子依赖 | Phase 4 评估 |
| `setmeal_dish` | 否 — 关联旧 setmeal | Phase 4 评估 |
| `shop_config` | 否 — 但旧页面和 H2 种子依赖 | Phase 4 评估 |
| `shop_business_hour` | 否 — 关联旧 shop_config | Phase 4 评估 |

**关键判断**：`user` 和 `employee` 表绝对不可删除，因为 campus_* 表有外键引用它们，且 campus 业务逻辑通过 UserMapper / EmployeeMapper 访问。

---

## 二、前端旧外卖可见入口清单

### 2.1 当前 admin 侧边栏中的旧模块菜单项

位于 `frontend/src/layout/MainLayout.vue` 第 67-87 行，已归属在 **"旧模块兼容"** 分区下：

| 当前文案 | 路由 | 页面 | 可见性建议 |
|---|---|---|---|
| 分类兼容 | `/category` | Category.vue | 可保留，文案已足够清楚 |
| 商品兼容 | `/dish` | Dish.vue | 可保留，文案已足够清楚 |
| 套餐兼容 | `/setmeal` | Setmeal.vue | 可保留，文案已足够清楚 |
| 订单兼容 | `/order` | Order.vue | 可保留，文案已足够清楚 |
| 旧店铺状态 | `/shop-status` | ShopStatus.vue | 可改名 "店铺状态兼容" |

**当前状态**：这些菜单项已经在 Step 102-105 阶段被明确标注为 "旧模块兼容"，与校园运营区隔清晰。当前的文案和分组已经是合理的最小兼容状态。

### 2.2 用户端旧外卖可见入口

位于 `frontend/src/views/user/Home.vue` 的底部导航和页面内容中：

- "点餐" 相关的旧入口（已被 Step 117-118 校园化）
- 旧外卖 `category/cart/orders/profile` 页面仍为兼容入口保留

### 2.3 需要进一步审计的入口

- `frontend/src/views/ComponentDemo.vue`：Vite 模板残留组件演示页，无业务价值，可优先删除
- `frontend/src/components/HelloWorld.vue`：Vite 模板残留，可优先删除

---

## 三、当前 campus 新模块依赖旧模块的地方

### 3.1 后端直接依赖

| campus 文件 | 依赖的旧模块 | 依赖原因 |
|---|---|---|
| `CampusCourierAuthController.java` | `UserService` | 用户登录校验后发放 courier token |
| `CampusCourierProfileServiceImpl.java` | `UserMapper` | 通过 userId 关联查询用户基础信息 |
| `CampusRelayOrderServiceImpl.java` | `CampusCustomerProfileService` → `UserMapper` | 下单前校验用户资料存在 |

### 3.2 数据库外键依赖

所有 campus_* 表通过外键引用 `user(id)` 和 `employee(id)`：

- `campus_customer_profile.user_id` → `user.id`
- `campus_courier_profile.user_id` → `user.id`
- `campus_courier_profile.reviewed_by_employee_id` → `employee.id`
- `campus_relay_order.customer_user_id` → `user.id`
- `campus_relay_order.courier_profile_id` → `campus_courier_profile.id`
- `campus_relay_order` 多处 `after_sale_*_by_employee_id` → `employee.id`

### 3.3 认证体系依赖

- JwtInterceptor 同时处理 employee（admin）/ customer / courier 三种 userType
- admin 登录通过 EmployeeController + EmployeeService
- customer 登录通过 UserController + UserService
- 密码加密通过 PasswordService（BCrypt）

### 3.4 前端间接依赖

- `request.js` 中 `PUBLIC_PREFIXES` 包含 `/employees/login`、`/users/login`
- `request.js` 中 `CUSTOMER_PREFIXES` 包含 `/users/`、`/user/`
- admin store `user.js` 通过 `/api/employees/login` 登录
- customer store `customer.js` 通过 `/api/users/login` 登录
- Dashboard.vue 和 Statistics.vue 通过 statistics API 获取数据

---

## 四、当前仍不可删除的模块

### 绝对不可删除（campus 运行时依赖）

| 模块 | 原因 |
|---|---|
| `user` 表 + `User.java` Entity + `UserMapper` + `UserService` | campus 下单、onboarding、courier 关联 |
| `employee` 表 + `Employee.java` Entity + `EmployeeMapper` + `EmployeeService` | admin 登录、审核、结算、售后执行 |
| `PasswordService` | campus 和旧模块共用 BCrypt |
| `JwtInterceptor` + `JwtUtil` | 三端认证核心 |
| `FileController` / `UploadController` + `LocalFileStorageService` | campus 凭证上传 |
| `Result.java` + `PageResult.java` | campus 控制器统一返回 |
| `BusinessException.java` + `GlobalExceptionHandler.java` | campus 全局异常处理 |
| `BaseContext.java` | campus 控制器获取当前用户 |

### 当前不应删除（用户端复用或 admin 运营依赖）

| 模块 | 原因 |
|---|---|
| `AddressController` / `CartController` / `UserOrderController` | 用户端 H5 兼容入口 |
| `PublicController` | 用户端公开数据（分类/菜品/店铺状态） |
| `StatisticsController` / `StatisticsService` / `StatisticsMapper` | admin 数据看板 |
| `Dashboard.vue` / `Employee.vue` / `Statistics.vue` | admin 运营核心页面 |
| `Login.vue` | admin 登录页（已改为校园视觉但仍是旧路由） |
| `category` / `dish` / `setmeal` / `shop_*` 表 | H2 种子和旧页面依赖 |

---

## 五、当前可以后续优先处理的内容

### 5.1 前端可直接清理的残留文件（Phase 2）

| 文件 | 原因 |
|---|---|
| `frontend/src/components/HelloWorld.vue` | Vite 模板残留，经搜索无任何引用 |
| `frontend/src/assets/vue.svg` | Vite 模板残留 |
| `frontend/src/assets/vite.svg` | Vite 模板残留 |
| `frontend/src/views/ComponentDemo.vue` | 组件演示页，无业务价值 |

### 5.2 前端文案可进一步统一的（Phase 1）

| 当前 | 建议 |
|---|---|
| MainLayout 旧模块菜单项 "旧店铺状态" | 可改为 "店铺状态兼容" 以统一风格 |
| 旧 admin 页面内部的占位文案 | 可在不改变路由的情况下微调 |

### 5.3 文档中可清理的过期描述

当前 `README.md`、`docs/README.md` 等已在前几轮做过一致性复核（Step 77），过期表述已清理。后续维护时注意保持。

---

## 六、分阶段删除计划

### Phase 0：只做审计，不删代码（当前轮次 ✓ 已完成）

- [x] 输出旧模块清单（前端页面、API 文件、后端 controller/service/mapper/entity）
- [x] 输出依赖清单（campus → 旧模块）
- [x] 输出可见入口清单（admin 侧边栏、用户端）
- [x] 输出风险判断
- [x] **不删除任何代码**

### Phase 1：隐藏或改名前端旧外卖可见入口（下一轮建议）

- [ ] 统一 MainLayout 旧模块菜单文案（"旧店铺状态" → "店铺状态兼容"）
- [ ] 评估是否将 `/shop-status` 从旧模块分区移除或合并
- [ ] 保持所有路由和页面不变
- [ ] 不删接口、不删页面、不删路由

### Phase 2：删除确定无依赖的未使用前端文件和页面

- [ ] 删除 `HelloWorld.vue`（先 grep 确认无引用）
- [ ] 删除 `vue.svg` / `vite.svg`（先 grep 确认无引用）
- [ ] 删除 `ComponentDemo.vue` + 对应路由（先 grep 确认无引用）
- [ ] 删除后 `npm run build` 必须通过
- [ ] 删除后 `npm run lint` 必须通过
- [ ] **不删除任何业务页面和路由**

### Phase 3：确认后端旧模块无依赖后，逐模块删除

每个旧模块删除前必须独立验证：
- [ ] 不被任何 campus controller / service / mapper import
- [ ] 不被 JwtInterceptor 路由白名单依赖
- [ ] 不被 admin 登录或 customer 登录依赖
- [ ] 不被 H2 数据库初始化依赖
- [ ] 不被集成测试依赖
- [ ] 不被前端 API 文件引用
- [ ] backend compile 必须通过
- [ ] 相关集成测试必须通过

当前判断 **暂时没有任何旧后端模块满足全部条件**。

建议 Phase 3 的删除顺序（每个模块独立提交）：
1. `SetmealController` + `SetmealService` + `SetmealMapper` + `SetmealDishMapper`
2. `DishController` + `DishService` + `DishMapper`
3. `CategoryController` + `CategoryService` + `CategoryMapper`
4. `ShopController` + `ShopConfigMapper`
5. `OrderController` + `OrderService` + `OrderMapper` + `OrderItemMapper`

每个模块删除后必须：
- `mvn compile` 通过
- `npm run build` 通过
- 旧 admin 页面可能需要同步删除或改为空态

### Phase 4：最后才评估旧表和旧初始化数据清理

前置条件：
- 所有引用该表的旧后端模块已删除
- 所有引用该表的前端页面已删除或改为空态
- H2 schema/data 已移除对应表定义和数据
- MySQL init.sql 已移除对应表定义和数据
- campus 表的外键引用已确认不依赖该表
- 集成测试不再需要该表的数据

当前判断 **Phase 4 之前必须先完成 Phase 3**。

---

## 七、当前结论

1. **旧外卖模块现在不能直接删除。**
2. 当前 14 个旧 Controller 中，**至少 8 个**仍被 campus 或用户端直接/间接依赖。
3. 当前 12 个旧 Mapper 中，**至少 6 个**仍被 campus 或用户端依赖。
4. 当前 12 个旧数据库表中，**user 和 employee 绝对不可删除**，其余 10 张表需要 Phase 4 才能评估。
5. 前端 10 个旧 admin 页面中，**Login/Dashboard/Employee/Statistics 已被 campus 运营台复用**。
6. 前端 13 个旧 API 文件中，**至少 6 个**仍被 campus 或用户端使用。
7. **当前最安全的行动**是 Phase 1：微调 MainLayout 旧模块菜单文案，不改代码。
8. **任何旧模块删除前，必须通过完整的依赖审计和不低于一轮的编译/构建验证。**

---

## 八、风险提示

- 删除 `category/dish/setmeal/shop_*` 表前必须确认 H2 种子不依赖这些表。当前 `data-h2.sql` 中有旧外卖分类/菜品/套餐的 INSERT 语句，删除表会导致 test profile 启动失败。
- 删除旧 Controller 前必须确认前端 API 文件不再调用对应路由。当前 `category.js`、`dish.js`、`setmeal.js`、`order.js`、`shop.js` 仍被旧 admin 页面引用。
- 删除旧 Entity 前必须确认 campus 代码未通过 import 间接引用。虽然当前 audit 显示 campus 不 import 旧 Entity（User/Employee 除外），但 MyBatis XML/注解可能通过 resultType 间接引用。
- `Orders` → `Order` 的命名不一致：旧表叫 `orders`（复数），Entity 叫 `Order`（单数）。如果将来删除旧订单模块，要小心不要误删 campus 依赖的 `OrderMapper` 或 `OrderService`。

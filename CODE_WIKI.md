# 校内兼职平台 — Code Wiki

> 项目根目录：`D:\20278\code\Campus part-time job`
> 生成日期：2026-05-06
> 基于苍穹外卖（Cangqiong Takeaway）增量改造，旧外卖模块保留可运行，校园代送功能以并行 campus 领域扩展。

---

## 目录

1. [项目概述](#1-项目概述)
2. [整体架构](#2-整体架构)
3. [后端架构](#3-后端架构)
   - 3.1 [技术栈与依赖](#31-技术栈与依赖)
   - 3.2 [包结构](#32-包结构)
   - 3.3 [配置体系](#33-配置体系)
   - 3.4 [鉴权与拦截器](#34-鉴权与拦截器)
   - 3.5 [统一响应与异常处理](#35-统一响应与异常处理)
   - 3.6 [旧外卖模块](#36-旧外卖模块)
   - 3.7 [Campus 校园代送模块](#37-campus-校园代送模块)
4. [前端架构](#4-前端架构)
   - 4.1 [技术栈与依赖](#41-技术栈与依赖)
   - 4.2 [目录结构](#42-目录结构)
   - 4.3 [路由体系](#43-路由体系)
   - 4.4 [状态管理](#44-状态管理)
   - 4.5 [API 层](#45-api-层)
   - 4.6 [请求封装与 Token 策略](#46-请求封装与-token-策略)
5. [移动端架构](#5-移动端架构)
6. [数据库设计](#6-数据库设计)
   - 6.1 [旧外卖表](#61-旧外卖表)
   - 6.2 [Campus 表](#62-campus-表)
   - 6.3 [ER 关系概览](#63-er-关系概览)
7. [部署与运行](#7-部署与运行)
   - 7.1 [本地开发](#71-本地开发)
   - 7.2 [Docker 部署](#72-docker-部署)
   - 7.3 [Android 构建](#73-android-构建)
8. [测试](#8-测试)
9. [关键业务流程](#9-关键业务流程)
10. [项目约束与约定](#10-项目约束与约定)

---

## 1. 项目概述

本项目是一个**校内兼职 / 校园代送平台**，在原有"苍穹外卖"项目基础上增量改造而来。核心业务是让学生用户可以发布代送需求（从校门取餐点代送到宿舍/图书馆），兼职配送员接单配送。

**双模块并行**：
- **旧外卖模块**：完整的餐饮点餐系统（菜品管理、购物车、下单、支付），保留可运行
- **Campus 校园代送模块**：新增的代送业务闭环，独立包 `com.cangqiong.takeaway.campus`、独立表前缀 `campus_`

**三类角色**：
| 角色 | 说明 | Token 存储 |
|------|------|-----------|
| Admin（管理员） | 复用 employee 表，管理后台 | `admin_token` |
| Customer（用户） | 复用 user 表，下单代送 | `customer_token` |
| Courier（配送员） | 独立建模 CampusCourierProfile，通过 userId 关联 user | `courier_token` |

---

## 2. 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                      客户端层                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │ Admin Web    │  │ Customer H5  │  │ Courier Parttime │  │
│  │ (Vue3 SPA)  │  │ (Vue3 SPA)   │  │ (Vue3 SPA)       │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘  │
│         │                 │                    │            │
│  ┌──────┴─────────────────┴────────────────────┴──────┐    │
│  │         Android Capacitor 壳 (可选)                │    │
│  │   user-app / parttime-app                         │    │
│  └──────────────────────┬────────────────────────────┘    │
└─────────────────────────┼──────────────────────────────────┘
                          │ HTTP (JWT Bearer)
┌─────────────────────────┼──────────────────────────────────┐
│                    后端层 (Spring Boot)                      │
│  ┌──────────────────────┴──────────────────────────────┐   │
│  │              WebMvcConfig + JwtInterceptor           │   │
│  │   /api/campus/admin/**  → employee                  │   │
│  │   /api/campus/customer/** → customer                │   │
│  │   /api/campus/courier/**  → courier                 │   │
│  │   /api/campus/public/**   → 公开                    │   │
│  └──────────────────────┬──────────────────────────────┘   │
│                         │                                   │
│  ┌──────────────────────┴──────────────────────────────┐   │
│  │              Controller → Service → Mapper           │   │
│  │  ┌─────────────────┐  ┌──────────────────────────┐  │   │
│  │  │ 旧外卖模块       │  │ Campus 校园代送模块       │  │   │
│  │  │ .takeaway.*     │  │ .takeaway.campus.*       │  │   │
│  │  └─────────────────┘  └──────────────────────────┘  │   │
│  └──────────────────────┬──────────────────────────────┘   │
│                         │ MyBatis                           │
│  ┌──────────────────────┴──────────────────────────────┐   │
│  │              MySQL / H2 (test)                       │   │
│  │  旧表: employee, user, category, dish, orders...    │   │
│  │  Campus表: campus_relay_order, campus_courier_...   │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. 后端架构

### 3.1 技术栈与依赖

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.4 | 主框架 |
| JDK | 17 | Java 版本 |
| MyBatis Spring Boot | 3.0.3 | ORM 框架 |
| MySQL Connector | runtime | 生产数据库驱动 |
| H2 | runtime | 测试内存数据库 |
| JJWT | 0.12.3 | JWT 令牌生成/验证 |
| Lombok | 1.18.44 | 代码简化 |
| Spring Security Crypto | — | 密码加密（BCrypt） |
| Spring Boot Validation | — | 参数校验 |

### 3.2 包结构

```
com.cangqiong.takeaway
├── TakeawayApplication.java          # Spring Boot 启动类
├── config/
│   ├── WebMvcConfig.java             # MVC 配置（CORS、拦截器注册）
│   ├── ProductionReadinessValidator.java
│   └── properties/
│       ├── CorsProperties.java       # CORS 配置属性
│       ├── JwtProperties.java        # JWT 配置属性
│       └── UploadProperties.java     # 文件上传配置属性
├── interceptor/
│   ├── JwtInterceptor.java           # JWT 鉴权拦截器
│   └── BaseContext.java              # 线程级用户上下文
├── exception/
│   ├── BusinessException.java        # 业务异常
│   └── GlobalExceptionHandler.java   # 全局异常处理
├── utils/
│   ├── Result.java                   # 统一响应封装
│   ├── JwtUtil.java                  # JWT 工具类
│   └── MD5Util.java                  # MD5 工具类
│
├── [旧外卖模块]
│   ├── controller/                   # 14 个 Controller
│   ├── dto/                          # 请求 DTO
│   ├── entity/                       # 12 个实体
│   ├── mapper/                       # 12 个 Mapper
│   ├── query/                        # 查询参数
│   ├── service/                      # 9 个 Service 接口 + impl
│   └── vo/                           # 响应 VO
│
└── campus/                           # ★ 校园代送新增模块
    ├── controller/                   # 14 个 Controller
    ├── dto/                          # 请求 DTO
    ├── entity/                       # 9 个实体
    ├── enums/                        # 9 个枚举
    ├── mapper/                       # 10 个 Mapper
    ├── query/                        # 15 个查询参数
    ├── service/                      # 10 个 Service 接口 + impl
    ├── support/                      # 规则目录
    └── vo/                           # 28 个响应 VO
```

### 3.3 配置体系

| Profile | 数据库 | 用途 |
|---------|--------|------|
| `dev` | MySQL (`cangqiong_takeaway`) | 本地开发 |
| `test` | H2 内存库 | 自动初始化 schema/data，集成测试 |
| `prod` | MySQL | 生产部署 |

**核心配置项**（`application-dev.properties`）：

| 配置 | 值 | 说明 |
|------|----|------|
| `server.port` | 8080 | 服务端口 |
| `spring.datasource.url` | `jdbc:mysql://127.0.0.1:3306/cangqiong_takeaway` | 数据库连接 |
| `mybatis.mapper-locations` | `classpath:mappers/*.xml` | Mapper XML 位置 |
| `mybatis.type-aliases-package` | `...entity,...campus.entity` | 类型别名包 |
| `mybatis.configuration.map-underscore-to-camel-case` | true | 驼峰映射 |
| `jwt.secret` | 环境变量 / 默认值 | JWT 密钥 |
| `jwt.expiration` | 86400000 (24h) | Token 过期时间 |
| `app.security.cors.allowed-origins` | `localhost:5173` 等 | CORS 白名单 |
| `upload.storage-path` | `uploads/private/images/` | 文件存储路径 |

### 3.4 鉴权与拦截器

**JwtInterceptor** 是全局鉴权拦截器，对所有路径生效（排除 `/error`、`/h2-console/**`）。

**公开路径**（无需 Token）：
- `OPTIONS` 请求
- `/api/campus/public/**`
- `/api/campus/courier/auth/token`
- `/api/employees/login`、`/api/users/login`
- `GET /api/public/categories`、`/api/public/dishes`、`/api/public/shop/status`
- `GET /api/public/dishes/**`、`/api/files/**`

**角色路由规则**：

| URI 前缀 | 所需 userType | 说明 |
|----------|--------------|------|
| `/api/campus/admin/**` | `employee` | 管理员接口 |
| `/api/campus/customer/**` | `customer` | 用户接口 |
| `/api/campus/courier/**` | `courier` | 配送员接口 |
| `/api/users/**`、`/api/user/**` | `customer` | 旧用户接口 |
| `/api/public/**` | 无 | 公开接口 |
| 其他 | `employee` | 默认管理员 |

**Bridge 路径**（兼容 onboarding）：
- `/api/campus/courier/profile` 和 `/api/campus/courier/review-status` 同时接受 `customer` 和 `courier` token，因为审核中的用户尚未获得 courier token。

**BaseContext**：使用 `ThreadLocal` 存储当前用户 ID 和类型，请求结束后清理。

### 3.5 统一响应与异常处理

**Result\<T\>** 统一响应结构：

```java
{
    "code": 200,       // 200=成功, 401=未授权, 403=无权限, 404=不存在, 500=错误
    "msg": "success",
    "data": T          // 泛型数据
}
```

**GlobalExceptionHandler** 处理四类异常：

| 异常类型 | 处理方式 |
|----------|----------|
| `BusinessException` | 返回 `Result.error(code, msg)` |
| `MethodArgumentNotValidException` | 返回 `Result.error(校验消息)` |
| `BindException` | 返回 `Result.error(绑定消息)` |
| `Exception` | 记录错误日志，返回 `Result.error("系统繁忙")` |

### 3.6 旧外卖模块

#### 3.6.1 实体类

| 实体 | 表名 | 关键字段 |
|------|------|----------|
| `Employee` | employee | id, name, phone, password, position, department, entryDate, status |
| `User` | user | id, name, phone, password, avatar, status |
| `Category` | category | id, name, sort, status |
| `Dish` | dish | id, name, categoryId, price, description, image, status |
| `Setmeal` | setmeal | id, name, categoryId, price, description, image, status |
| `SetmealDish` | setmeal_dish | id, setmealId, dishId, quantity |
| `Cart` | cart | id, userId, dishId, setmealId, quantity, checked |
| `Address` | address | id, userId, consignee, phone, province, city, district, detail, isDefault |
| `Order` | orders | id(VARCHAR 32), userId, customerName, customerPhone, customerAddress, totalAmount, status, paymentTime, deliveryTime |
| `OrderItem` | order_item | id, orderId, dishId, setmealId, name, price, quantity, total |
| `ShopConfig` | shop_config | id, isOpen, restNotice |
| `ShopBusinessHour` | shop_business_hour | id, dayKey, dayName, isOpen, openTime, closeTime, sort |

#### 3.6.2 Service 接口

| Service | 核心方法 |
|---------|----------|
| `EmployeeService` | login, create, update, getById, pageQuery, updateStatus |
| `UserService` | login, getById |
| `CategoryService` | create, update, delete, getById, list, updateStatus |
| `DishService` | create, update, delete, getById, pageQuery, updateStatus, listByCategoryId |
| `SetmealService` | create, update, delete, getById, pageQuery, updateStatus, listByCategoryId |
| `OrderService` | create, getById, pageQuery, updateStatus, pay, cancel |
| `CategoryServiceImpl` | 分类增删改查 + 状态管理 |
| `StatisticsService` | turnoverStatistics, orderStatistics, dishRanking, dashboard, userStatistics |

#### 3.6.3 Controller API 端点

| Controller | 路径前缀 | 核心端点 |
|------------|----------|----------|
| `EmployeeController` | `/api/employees` | POST /login, POST /, PUT /, GET /page, GET /{id}, PUT /status |
| `UserController` | `/api/users` | POST /login, GET / |
| `CategoryController` | `/api/categories` | GET /, POST /, PUT /, DELETE /{id}, PUT /status |
| `DishController` | `/api/dishes` | GET /page, GET /{id}, POST /, PUT /, DELETE /{id}, PUT /status |
| `SetmealController` | `/api/setmeals` | GET /page, GET /{id}, POST /, PUT /, DELETE /{id}, PUT /status |
| `OrderController` | `/api/orders` | GET /page, GET /{id}, PUT /status, PUT /pay, PUT /cancel |
| `UserOrderController` | `/api/user/orders` | GET /, GET /{id}, POST / |
| `CartController` | `/api/carts` | GET /, POST /, PUT /, DELETE / |
| `AddressController` | `/api/addresses` | GET /, POST /, PUT /, DELETE /{id} |
| `PublicController` | `/api/public` | GET /categories, GET /dishes, GET /shop/status |
| `ShopController` | `/api/shop` | GET /config, PUT /status |
| `StatisticsController` | `/api/statistics` | GET /turnover, GET /orders, GET /dishes, GET /dashboard, GET /users |
| `FileController` | `/api/files` | GET /** |
| `UploadController` | `/api/upload` | POST / |

### 3.7 Campus 校园代送模块

#### 3.7.1 枚举定义

| 枚举 | 值 | 说明 |
|------|----|------|
| `CampusRelayOrderStatus` | `PENDING_PAYMENT` → `BUILDING_PRIORITY_PENDING` → `WAITING_ACCEPT` → `ACCEPTED` → `PICKED_UP` → `DELIVERING` → `AWAITING_CONFIRMATION` → `COMPLETED` / `CANCELLED` / `AFTER_SALE_*` | 代送单状态机 |
| `CampusPaymentStatus` | `UNPAID`, `PAID` | 支付状态 |
| `CampusPayoutStatus` | 打款状态 | 配送员打款状态 |
| `CampusCourierReviewStatus` | `PENDING`, `APPROVED`, `REJECTED` | 配送员审核状态 |
| `CampusDeliveryTargetType` | `DORMITORY`, `LIBRARY`, etc. | 配送目标类型 |
| `CampusAfterSaleDecisionType` | 售后决定类型 | 退款/部分退款/拒绝 |
| `CampusAfterSaleExecutionStatus` | 售后执行状态 | 执行中/已完成/已纠正 |
| `CampusAfterSaleHandleAction` | 售后处理动作 | 处理方式 |
| `CampusSettlementStatus` | 结算状态 | 待结算/已结算 |

#### 3.7.2 实体类

| 实体 | 表名 | 说明 |
|------|------|------|
| `CampusRelayOrder` | campus_relay_order | 代送订单（核心实体），含完整生命周期字段 |
| `CampusCourierProfile` | campus_courier_profile | 配送员资料，通过 userId 关联 user |
| `CampusCustomerProfile` | campus_customer_profile | 用户校园资料（实名、身份类型） |
| `CampusPickupPoint` | campus_pickup_point | 取餐点 |
| `CampusLocationReport` | campus_location_report | 配送员位置上报 |
| `CampusExceptionRecord` | campus_exception_record | 异常记录 |
| `CampusSettlementRecord` | campus_settlement_record | 结算记录 |
| `CampusAfterSaleExecutionRecord` | campus_after_sale_execution_record | 售后执行记录 |
| `CampusSettlementBatchOperationRecord` | campus_settlement_batch_operation_record | 结算批次操作记录 |
| `CampusSettlementReconcileDifferenceRecord` | campus_settlement_reconcile_difference_record | 结算对账差异记录 |

#### 3.7.3 Controller API 端点

**公开接口** — `CampusPublicController` (`/api/campus/public`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/pickup-points` | 获取取餐点列表 |
| GET | `/delivery-rules` | 获取配送规则 |
| GET | `/health` | 健康检查 |

**用户接口** — `CampusCustomerOrderController` (`/api/campus/customer/orders`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/` | 创建代送单 |
| POST | `/{id}/mock-pay` | 模拟支付 |
| POST | `/{id}/confirm` | 确认收货 |
| POST | `/{id}/cancel` | 取消订单 |
| POST | `/{id}/after-sale` | 申请售后 |
| GET | `/{id}/after-sale-result` | 获取售后结果 |
| GET | `/{id}` | 获取订单详情 |
| GET | `/` | 分页查询我的订单 |

**用户端 Onboarding** — `CampusCustomerCourierOnboardingController` (`/api/campus/customer/courier-onboarding`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/profile` | 提交配送员申请资料 |
| GET | `/profile` | 获取已提交的申请资料 |
| GET | `/review-status` | 获取审核状态 |
| GET | `/token-eligibility` | 检查是否有资格获取 courier token |

**配送员认证** — `CampusCourierAuthController` (`/api/campus/courier/auth`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/token` | 配送员登录获取 token（公开路径） |

**配送员接口** — `CampusCourierOrderController` (`/api/campus/courier/orders`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/available` | 查询可接订单 |
| GET | `/{id}` | 获取订单详情 |
| POST | `/{id}/accept` | 接单 |
| POST | `/{id}/pickup` | 取餐 |
| POST | `/{id}/deliver` | 配送 |
| POST | `/{id}/exception-report` | 上报异常 |

**配送员资料** — `CampusCourierProfileController` (`/api/campus/courier`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/profile` | 提交/更新资料（bridge 路径） |
| GET | `/profile` | 获取当前资料（bridge 路径） |
| GET | `/review-status` | 获取审核状态（bridge 路径） |

**配送员位置** — `CampusCourierLocationController` (`/api/campus/courier/location-reports`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/` | 上报位置 |

**管理员 — 订单管理** — `CampusAdminRelayOrderController` (`/api/campus/admin/orders`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 分页查询订单 |
| GET | `/after-sale` | 分页查询售后订单 |
| GET | `/after-sale-executions` | 查询售后执行列表 |
| GET | `/{id}/after-sale-result` | 获取售后结果 |
| GET | `/{id}` | 获取订单详情 |
| GET | `/{id}/timeline` | 获取订单时间线 |
| GET | `/{id}/location-reports` | 获取位置上报记录 |
| GET | `/{id}/exception-summary` | 获取异常摘要 |
| POST | `/{id}/after-sale-handle` | 处理售后 |
| POST | `/{id}/after-sale-decision` | 记录售后决定 |
| POST | `/{id}/after-sale-execution` | 记录售后执行 |

**管理员 — 配送员管理** — `CampusAdminCourierController` (`/api/campus/admin/couriers`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 分页查询配送员 |
| GET | `/{id}/exceptions/recent` | 查询近期异常 |
| GET | `/{id}/location-reports` | 查询位置记录 |
| POST | `/{id}/review` | 审核配送员 |

**管理员 — 结算管理** — `CampusAdminSettlementController` (`/api/campus/admin/settlements`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 分页查询结算记录 |
| GET | `/payout-batches` | 查询打款批次 |
| GET | `/payout-batches/{batchNo}` | 批次详情 |
| GET | `/{id}` | 结算详情 |
| POST | `/{id}/confirm` | 确认结算 |
| POST | `/{id}/payout-result` | 记录打款结果 |
| POST | `/batch-payout-result` | 批量记录打款结果 |
| GET | `/reconcile-summary` | 对账汇总 |
| POST | `/{id}/verify-payout` | 验证打款 |

**管理员 — 异常管理** — `CampusAdminExceptionController` (`/api/campus/admin/exceptions`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 分页查询异常 |
| GET | `/{id}` | 异常详情 |
| POST | `/{id}/resolve` | 处理异常 |

**管理员 — 售后执行记录** — `CampusAdminAfterSaleExecutionRecordController` (`/api/campus/admin/after-sale-execution-records`)

**管理员 — 结算批次操作** — `CampusAdminSettlementBatchOperationController` (`/api/campus/admin/settlements/payout-batches/{batchNo}/operations`)

**管理员 — 对账差异** — `CampusAdminSettlementReconcileDifferenceController`

#### 3.7.4 Service 接口

| Service | 核心方法 |
|---------|----------|
| `CampusRelayOrderService` | create, mockPay, cancel, openAfterSale, confirm, getById, pageQuery, pageAfterSale, getAfterSaleResult, handleAfterSale, recordAfterSaleDecision, recordAfterSaleExecution, accept, pickup, deliver, reportException, getTimeline, getExceptionSummary |
| `CampusCourierProfileService` | submitProfile, getByUserId, getReviewStatus, pageQuery, review, getByProfileId |
| `CampusCustomerProfileService` | getByUserId, create |
| `CampusPickupPointService` | listAll |
| `CampusLocationReportService` | report, getByOrderId, getByCourierProfileId |
| `CampusExceptionRecordService` | report, pageQuery, getById, resolve, listRecentByCourierProfileId |
| `CampusSettlementRecordService` | getByRelayOrderId, pageQuery, getById, confirm, recordPayoutResult, batchRecordPayoutResult, pagePayoutBatches, getPayoutBatchDetail, getReconcileSummary, verifyPayout |
| `CampusAfterSaleExecutionRecordService` | record, pageQuery |
| `CampusSettlementBatchOperationRecordService` | record, pageQuery |
| `CampusSettlementReconcileDifferenceRecordService` | create, pageQuery, resolve |

#### 3.7.5 规则目录

`CampusRuleCatalog` 提供平台规则常量和 `buildDeliveryRuleVO()` 方法，返回配送规则信息（基础费用、优先楼栋费用、小费等）。

---

## 4. 前端架构

### 4.1 技术栈与依赖

| 组件 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.30 | UI 框架 |
| Vite | 8.0.1 | 构建工具 |
| Vue Router | 4.6.4 | 路由 |
| Pinia | 3.0.4 | 状态管理 |
| Element Plus | 2.13.6 | UI 组件库 |
| Axios | 1.14.0 | HTTP 客户端 |
| ECharts | 6.0.0 | 图表 |
| Sass | 1.98.0 | CSS 预处理 |
| Vitest | 4.0.3 | 单元测试 |
| ESLint | 9.36.0 | 代码检查 |

### 4.2 目录结构

```
frontend/src/
├── main.js                    # 入口文件
├── App.vue                    # 根组件
├── style.css                  # 全局样式
├── api/                       # API 封装层
│   ├── campus-admin.js        # Campus 管理员 API
│   ├── campus-courier.js      # Campus 配送员 API
│   ├── campus-customer.js     # Campus 用户 API
│   ├── address.js             # 地址 API
│   ├── cart.js                # 购物车 API
│   ├── category.js            # 分类 API
│   ├── customer.js            # 用户 API
│   ├── customer-order.js      # 用户订单 API
│   ├── dish.js                # 菜品 API
│   ├── employee.js            # 员工 API
│   ├── order.js               # 订单 API
│   ├── public.js              # 公开 API
│   ├── setmeal.js             # 套餐 API
│   ├── shop.js                # 店铺 API
│   ├── statistics.js          # 统计 API
│   └── upload.js              # 上传 API
├── components/                # 通用组件
│   ├── BaseDialogForm.vue     # 弹窗表单
│   ├── BaseSearch.vue         # 搜索组件
│   ├── BaseTable.vue          # 表格组件
│   └── HelloWorld.vue         # 示例组件
├── config/
│   └── index.js               # 全局配置
├── layout/                    # 布局组件
│   ├── MainLayout.vue         # Admin 布局
│   ├── ParttimeLayout.vue     # 配送员布局
│   └── UserLayout.vue         # 用户布局
├── router/
│   └── index.js               # 路由定义 + 导航守卫
├── stores/                    # Pinia 状态管理
│   ├── user.js                # Admin Store
│   ├── customer.js            # Customer Store
│   ├── courier.js             # Courier Store
│   ├── mock.js                # Mock 数据
│   └── index.js               # Store 入口
├── styles/                    # 样式
│   ├── element-plus.scss      # Element Plus 主题覆盖
│   ├── global.scss            # 全局样式
│   ├── mobile-app.scss        # 移动端样式
│   └── variables.scss         # SCSS 变量
├── utils/
│   ├── request.js             # Axios 封装 + Token 策略
│   ├── echarts.js             # ECharts 初始化
│   ├── tencentMap.js          # 腾讯地图
│   └── text.js                # 文本工具
└── views/                     # 页面视图
    ├── Login.vue              # Admin 登录
    ├── Dashboard.vue          # Admin 仪表盘
    ├── Employee.vue           # 员工管理
    ├── Category.vue           # 分类管理
    ├── Dish.vue               # 菜品管理
    ├── Setmeal.vue            # 套餐管理
    ├── Order.vue              # 订单管理
    ├── Statistics.vue         # 统计分析
    ├── ShopStatus.vue         # 店铺状态
    ├── CampusSettlementBatchList.vue    # 结算批次列表
    ├── CampusSettlementBatchDetail.vue  # 结算批次详情
    ├── CampusAfterSaleExecutionList.vue # 售后执行列表
    ├── CampusCourierOpsView.vue         # 配送员运营
    ├── CampusSettlementOpsView.vue      # 结算运营
    ├── CampusExceptionOpsView.vue       # 异常处理
    ├── user/                  # 用户端页面
    │   ├── Login.vue          # 用户登录
    │   ├── Home.vue           # 首页
    │   ├── Category.vue       # 分类
    │   ├── DishDetail.vue     # 菜品详情
    │   ├── Cart.vue           # 购物车
    │   ├── Checkout.vue       # 结算
    │   ├── Orders.vue         # 订单列表
    │   ├── Profile.vue        # 个人中心
    │   ├── CampusRelayOrders.vue       # 代送订单
    │   ├── CampusOrderResult.vue       # 代送下单结果
    │   ├── AfterSaleResult.vue         # 售后结果
    │   └── CourierOnboarding.vue       # 配送员申请
    └── courier/               # 配送员页面
        ├── Login.vue          # 配送员登录
        ├── CourierWorkbench.vue  # 工作台
        └── Profile.vue       # 个人资料
```

### 4.3 路由体系

路由通过 `meta` 字段区分三种鉴权模式：

| meta 字段 | 说明 | 未登录跳转 |
|-----------|------|-----------|
| `public: true` | 公开页面 | — |
| `requiresAdminAuth: true` | 需要管理员登录 | `/login` |
| `requiresCustomerAuth: true` | 需要用户登录 | `/user/login` |
| `requiresCourierAuth: true` | 需要配送员登录 | `/parttime/login` |

**`VITE_APP_SHELL`** 环境变量控制根路径重定向：
- `admin`（默认）→ `/dashboard`
- `user` → `/user/login`
- `parttime` → `/parttime/login`

**完整路由表**：

| 路径 | 组件 | 鉴权 |
|------|------|------|
| `/login` | Login.vue | public |
| `/dashboard` | Dashboard.vue | admin |
| `/employee` | Employee.vue | admin |
| `/category` | Category.vue | admin |
| `/dish` | Dish.vue | admin |
| `/setmeal` | Setmeal.vue | admin |
| `/order` | Order.vue | admin |
| `/statistics` | Statistics.vue | admin |
| `/shop-status` | ShopStatus.vue | admin |
| `/campus/settlement-batches` | CampusSettlementBatchList.vue | admin |
| `/campus/settlement-batches/:batchNo` | CampusSettlementBatchDetail.vue | admin |
| `/campus/after-sale-executions` | CampusAfterSaleExecutionList.vue | admin |
| `/campus/courier-ops` | CampusCourierOpsView.vue | admin |
| `/campus/settlements` | CampusSettlementOpsView.vue | admin |
| `/campus/exceptions` | CampusExceptionOpsView.vue | admin |
| `/user/login` | user/Login.vue | public |
| `/user` | user/Home.vue | customer |
| `/user/category` | user/Category.vue | customer |
| `/user/dish/:id` | user/DishDetail.vue | customer |
| `/user/cart` | user/Cart.vue | customer |
| `/user/checkout` | user/Checkout.vue | customer |
| `/user/orders` | user/Orders.vue | customer |
| `/user/profile` | user/Profile.vue | customer |
| `/user/campus/orders` | user/CampusRelayOrders.vue | customer |
| `/user/campus/order-result` | user/CampusOrderResult.vue | customer |
| `/user/campus/after-sale-result` | user/AfterSaleResult.vue | customer |
| `/user/campus/courier-onboarding` | user/CourierOnboarding.vue | customer |
| `/parttime/login` (alias `/courier/login`) | courier/Login.vue | public |
| `/parttime/workbench` (alias `/courier/workbench`) | courier/CourierWorkbench.vue | courier |
| `/parttime/profile` | courier/Profile.vue | courier |

### 4.4 状态管理

三个 Pinia Store 对应三种角色：

**UserStore（Admin）**
- State: `token`, `userInfo(name, avatar, role)`, `isLoggedIn`
- Actions: `login(userInfo, token)`, `setUserInfo()`, `setToken()`, `logout()`
- 持久化: `admin_token`, `admin_user_info`

**CustomerStore**
- State: `token`, `userInfo(id, name, phone, avatar)`
- Getters: `isLoggedIn`
- Actions: `login(userInfo, token)`, `setUserInfo()`, `logout()`
- 持久化: `customer_token`, `customer_user_info`

**CourierStore**
- State: `token`, `profile(id, realName, reviewStatus, enabled)`
- Getters: `isLoggedIn`
- Actions: `login(token, profile)`, `setProfile()`, `logout()`
- 持久化: `courier_token`, `courier_profile`

### 4.5 API 层

**Campus API**（前端 → 后端映射）：

| 前端模块 | 后端路径前缀 | 说明 |
|----------|-------------|------|
| `campus-customer.js` | `/campus/customer/`, `/campus/public/`, `/campus/courier/auth/` | 用户端 + 公开 + 配送员认证 |
| `campus-courier.js` | `/campus/courier/` | 配送员端 |
| `campus-admin.js` | `/campus/admin/` | 管理员端 |

**campus-customer.js 接口**：

| 方法 | HTTP | URL |
|------|------|-----|
| `getCampusPickupPoints` | GET | `/campus/public/pickup-points` |
| `getCampusDeliveryRules` | GET | `/campus/public/delivery-rules` |
| `createCampusCustomerOrder` | POST | `/campus/customer/orders` |
| `getCampusCustomerOrders` | GET | `/campus/customer/orders` |
| `mockPayCampusCustomerOrder` | POST | `/campus/customer/orders/{id}/mock-pay` |
| `getCampusCustomerAfterSaleResult` | GET | `/campus/customer/orders/{id}/after-sale-result` |
| `getCampusCustomerOrderDetail` | GET | `/campus/customer/orders/{id}` |
| `submitCourierOnboardingProfile` | POST | `/campus/customer/courier-onboarding/profile` |
| `getCourierOnboardingProfile` | GET | `/campus/customer/courier-onboarding/profile` |
| `getCourierOnboardingReviewStatus` | GET | `/campus/customer/courier-onboarding/review-status` |
| `getCourierTokenEligibility` | GET | `/campus/customer/courier-onboarding/token-eligibility` |
| `applyCourierToken` | POST | `/campus/courier/auth/token` |

**campus-courier.js 接口**：

| 方法 | HTTP | URL |
|------|------|-----|
| `getCourierProfile` | GET | `/campus/courier/profile` |
| `getCourierReviewStatus` | GET | `/campus/courier/review-status` |
| `getCourierAvailableOrders` | GET | `/campus/courier/orders/available` |
| `acceptCourierOrder` | POST | `/campus/courier/orders/{id}/accept` |
| `getCourierOrderDetail` | GET | `/campus/courier/orders/{id}` |
| `pickupCourierOrder` | POST | `/campus/courier/orders/{id}/pickup` |
| `deliverCourierOrder` | POST | `/campus/courier/orders/{id}/deliver` |
| `reportCourierOrderException` | POST | `/campus/courier/orders/{id}/exception-report` |

**campus-admin.js 接口**：

| 方法 | HTTP | URL |
|------|------|-----|
| `getCampusSettlementPayoutBatches` | GET | `/campus/admin/settlements/payout-batches` |
| `getCampusSettlementPayoutBatchDetail` | GET | `/campus/admin/settlements/payout-batches/{batchNo}` |
| `getCampusSettlementBatchOperations` | GET | `/campus/admin/settlements/payout-batches/{batchNo}/operations` |
| `getCampusAfterSaleExecutions` | GET | `/campus/admin/orders/after-sale-executions` |
| `getCampusAfterSaleExecutionRecords` | GET | `/campus/admin/after-sale-execution-records` |
| `getCampusAdminAfterSaleResult` | GET | `/campus/admin/orders/{id}/after-sale-result` |
| `getCampusCouriers` | GET | `/campus/admin/couriers` |
| `getCampusCourierRecentExceptions` | GET | `/campus/admin/couriers/{id}/exceptions/recent` |
| `getCampusCourierLocationReports` | GET | `/campus/admin/couriers/{id}/location-reports` |
| `getCampusSettlements` | GET | `/campus/admin/settlements` |
| `getCampusSettlementReconcileSummary` | GET | `/campus/admin/settlements/reconcile-summary` |
| `getCampusSettlementDetail` | GET | `/campus/admin/settlements/{id}` |
| `getCampusSettlementReconcileDifferences` | GET | `/campus/admin/settlements/reconcile-differences` |
| `getCampusExceptionRecords` | GET | `/campus/admin/exceptions` |
| `getCampusExceptionDetail` | GET | `/campus/admin/exceptions/{id}` |
| `resolveCampusException` | POST | `/campus/admin/exceptions/{id}/resolve` |

### 4.6 请求封装与 Token 策略

`request.js` 基于 axios 封装，核心逻辑：

**请求拦截器 — Token 选择策略**：

| URL 前缀 | Token 来源 |
|----------|-----------|
| `/public/`, `/campus/public/`, `/campus/courier/auth/token`, `/employees/login`, `/users/login` | 不附加 Token |
| `/users/`, `/user/`, `/campus/customer/` | `customer_token` |
| `/campus/courier/profile`, `/campus/courier/review-status` | `courier_token` || `customer_token` |
| `/campus/courier/orders/`, `/campus/courier/location-reports` | `courier_token` |
| 其他 | `admin_token` |

**响应拦截器**：
- `code === 200` → 返回 `data` 字段（经过 `normalizeTextFields` 处理）
- 其他 code → `ElMessage.error` 提示
- HTTP 401 → 按角色清除对应 token 并跳转登录页
- HTTP 403/404/500 → 对应错误提示

---

## 5. 移动端架构

两个 Capacitor Android 壳工程，共用 `frontend/` 业务源码：

| 壳工程 | 包名 | 默认入口 | 构建产物 |
|--------|------|----------|----------|
| `mobile/user-app` | `com.xiaoyu.campus.user` | `/user/login` | `frontend/dist-android-user` |
| `mobile/parttime-app` | `com.xiaoyu.campus.parttime` | `/parttime/login` | `frontend/dist-android-parttime` |

**构建流程**：
1. `npm run build:android:user` 或 `build:android:parttime` → 输出到 `dist-android-*`
2. `cd mobile/*/ && npm run cap:sync` → 同步到 Android 工程
3. Android Studio 构建 APK

**API Base URL 策略**：
- 模拟器：`10.0.2.2`（Android 模拟器映射宿主机）
- 局域网真机：`.env.android-*-lan`
- 公网内测：`.env.android-*-public`

---

## 6. 数据库设计

### 6.1 旧外卖表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `employee` | 员工/管理员 | id, name, phone, password, position, department, status |
| `user` | 用户 | id, name, phone, password, avatar, status |
| `category` | 分类 | id, name, sort, status |
| `dish` | 菜品 | id, name, category_id, price, description, image, status |
| `setmeal` | 套餐 | id, name, category_id, price, description, image, status |
| `setmeal_dish` | 套餐菜品关联 | id, setmeal_id, dish_id, quantity |
| `cart` | 购物车 | id, user_id, dish_id, setmeal_id, quantity, checked |
| `address` | 地址 | id, user_id, consignee, phone, province, city, district, detail, is_default |
| `orders` | 订单 | id(VARCHAR 32), user_id, customer_name, customer_phone, customer_address, total_amount, status |
| `order_item` | 订单明细 | id, order_id, dish_id, setmeal_id, name, price, quantity, total |
| `shop_config` | 店铺配置 | id, is_open, rest_notice |
| `shop_business_hour` | 营业时间 | id, day_key, day_name, is_open, open_time, close_time |

### 6.2 Campus 表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `campus_customer_profile` | 用户校园资料 | id, user_id(UNIQUE), real_name, identity_type, identity_no |
| `campus_courier_profile` | 配送员资料 | id, user_id(UNIQUE), real_name, gender, campus_zone, student_no, college, major, dormitory_building, review_status, enabled |
| `campus_pickup_point` | 取餐点 | id, code(UNIQUE), name, gate_area, description, enabled, sort |
| `campus_relay_order` | 代送订单 | id(VARCHAR 32), customer_user_id, courier_profile_id, pickup_point_id, delivery_target_type, delivery_building, food_description, base_fee, priority_fee, tip_fee, total_amount, payment_status, order_status, + 大量生命周期时间戳和售后字段 |
| `campus_location_report` | 位置上报 | id, relay_order_id, courier_profile_id, latitude, longitude, source, reported_at |
| `campus_exception_record` | 异常记录 | id, relay_order_id, courier_profile_id, exception_type, process_status, processed_by_employee_id |
| `campus_settlement_record` | 结算记录 | id, relay_order_id(UNIQUE), courier_profile_id, gross_amount, platform_commission, pending_amount, settlement_status, payout_status, payout_batch_no |
| `campus_after_sale_execution_record` | 售后执行记录 | id, relay_order_id, decision_type, execution_status, executed_by_employee_id |
| `campus_settlement_batch_operation_record` | 结算批次操作 | id, payout_batch_no, operation_type, operation_result, operated_by_employee_id |
| `campus_settlement_reconcile_difference_record` | 对账差异 | id, payout_batch_no, settlement_record_id, difference_type, process_status |

### 6.3 ER 关系概览

```
user ──1:1── campus_customer_profile
  │
  └──1:1── campus_courier_profile ──1:N── campus_relay_order (as courier)
  │                                         │
  └──1:N── campus_relay_order (as customer) │
                │                           │
                ├── N:1 ── campus_pickup_point
                │
                ├── 1:N ── campus_location_report
                ├── 1:N ── campus_exception_record
                ├── 1:1 ── campus_settlement_record
                └── 1:N ── campus_after_sale_execution_record

campus_settlement_record ──1:N── campus_settlement_batch_operation_record (via payout_batch_no)
                          └──1:N── campus_settlement_reconcile_difference_record (via payout_batch_no)

employee ──1:N── campus_courier_profile (reviewed_by)
         ──1:N── campus_exception_record (processed_by)
         ──1:N── campus_after_sale_execution_record (executed_by)
         ──1:N── campus_settlement_record (payout_recorded_by, payout_verified_by)
```

---

## 7. 部署与运行

### 7.1 本地开发

**后端**（需要 JDK 17 + MySQL）：

```powershell
cd backend
.\mvnw.cmd -DskipTests compile          # 编译
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev    # 启动（dev profile，连接 MySQL）
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test   # 启动（test profile，H2 内存库）
```

**前端**（需要 Node.js）：

```powershell
cd frontend
npm install
npm run dev          # 启动开发服务器 http://localhost:5173，/api 代理到 :8080
npm run lint         # 代码检查
npm run test         # 运行测试
npm run build        # 生产构建
```

**Demo 账号**（test profile seed data）：

| 角色 | 手机号 | 密码 |
|------|--------|------|
| admin | 13800138000 | 123456 |
| customer | 13900139000 | 123456 |

### 7.2 Docker 部署

`deploy/internal-trial/docker-compose.yml` 定义三个服务：

| 服务 | 镜像 | 端口 |
|------|------|------|
| mysql | mysql:8 | 3306 |
| backend | 自建 (backend.Dockerfile) | 8080 |
| frontend | 自建 (frontend.Dockerfile + nginx.conf) | 80 |

### 7.3 Android 构建

```powershell
cd frontend

# 用户端
npm run build:android:user:emulator    # 模拟器
npm run build:android:user:lan         # 局域网真机
npm run build:android:user:public      # 公网内测

# 兼职端
npm run build:android:parttime:emulator
npm run build:android:parttime:lan
npm run build:android:parttime:public

# 同步到 Capacitor
cd mobile/user-app && npm run cap:sync
cd mobile/parttime-app && npm run cap:sync
```

---

## 8. 测试

### 后端测试

测试框架：Spring Boot Test + H2 内存库

| 测试类 | 说明 |
|--------|------|
| `CampusCustomerOrderIntegrationTest` | 用户下单全流程 |
| `CampusCourierAcceptIntegrationTest` | 配送员接单 |
| `CampusCourierDeliveryIntegrationTest` | 配送员配送 |
| `CampusCourierOnboardingIntegrationTest` | 配送员入驻 |
| `CampusCourierProfileIntegrationTest` | 配送员资料 |
| `CampusOrderExceptionIntegrationTest` | 订单异常 |
| `CampusSkeletonIntegrationTest` | Campus 骨架测试 |
| `CampusStep03FIntegrationTest` | Step 03F 阶段测试 |
| `CampusStep04IntegrationTest` | Step 04 阶段测试 |
| `CampusStep05IntegrationTest` | Step 05 阶段测试 |
| `CampusStep06IntegrationTest` | Step 06 阶段测试 |
| `AuthenticationMigrationIntegrationTest` | 鉴权迁移测试 |
| `CriticalApiIntegrationTest` | 关键 API 测试 |
| `OrderControllerTest` | 旧订单 Controller 测试 |
| `StatisticsControllerTest` | 统计 Controller 测试 |

运行命令：
```powershell
cd backend
.\mvnw.cmd test                                              # 全部测试
.\mvnw.cmd -Dtest=CampusCustomerOrderIntegrationTest test    # 单个测试类
```

### 前端测试

测试框架：Vitest + jsdom

| 测试文件 | 说明 |
|----------|------|
| `src/stores/customer.spec.js` | Customer Store 测试 |
| `src/utils/text.spec.js` | 文本工具测试 |

运行命令：
```powershell
cd frontend
npm run test
npm run test -- src/utils/text.spec.js    # 单个测试
```

---

## 9. 关键业务流程

### 9.1 代送单全生命周期

```
用户创建代送单 → 模拟支付 → [楼栋优先窗口] → 等待接单 → 配送员接单
→ 取餐 → 配送中 → 等待确认 → 用户确认 → 完成 → 结算
```

状态机（`CampusRelayOrderStatus`）：

```
PENDING_PAYMENT
    │ (支付)
    ▼
BUILDING_PRIORITY_PENDING ──(优先窗口过期)──▶ WAITING_ACCEPT
    │                                           │
    │                                    (配送员接单)
    ▼                                           ▼
                                          ACCEPTED
                                               │ (取餐)
                                               ▼
                                          PICKED_UP
                                               │ (开始配送)
                                               ▼
                                          DELIVERING
                                               │ (到达)
                                               ▼
                                     AWAITING_CONFIRMATION
                                          │          │
                                     (用户确认)   (自动完成)
                                          ▼          ▼
                                       COMPLETED ◄──┘

任意未取餐状态 ──(取消)──▶ CANCELLED
已取餐后 ──(售后)──▶ AFTER_SALE_OPEN ──▶ AFTER_SALE_RESOLVED / AFTER_SALE_REJECTED
```

### 9.2 配送员入驻流程

```
用户登录 → 提交配送员申请资料 (CampusCustomerCourierOnboardingController)
→ 等待审核 (PENDING) → 管理员审核 (APPROVED/REJECTED)
→ 审核通过后获取 courier token (CampusCourierAuthController.createToken)
→ 以配送员身份工作
```

### 9.3 结算流程

```
订单完成 → 自动生成结算记录 (campus_settlement_record)
→ 管理员确认结算 → 记录打款结果 → 验证打款
→ 批量打款管理 (payout_batch_no)
→ 对账差异处理
```

### 9.4 售后流程

```
用户申请售后 → 管理员处理售后 (handleAfterSale)
→ 管理员记录售后决定 (recordAfterSaleDecision)
→ 管理员记录售后执行 (recordAfterSaleExecution)
→ 执行可纠正 (corrected)
```

---

## 10. 项目约束与约定

### 命名约定

| 维度 | 约定 |
|------|------|
| 新表前缀 | `campus_` |
| 新包根 | `com.cangqiong.takeaway.campus` |
| 新 Controller 路径 | `/api/campus/{role}/...` |
| 实体类 | `Campus*` 前缀 |
| DTO | `Campus*DTO` 后缀 |
| VO | `Campus*VO` 后缀 |
| Query | `Campus*Query` 后缀 |
| Mapper | `Campus*Mapper` 后缀 |
| Service | `Campus*Service` 接口 + `Campus*ServiceImpl` 实现 |

### 硬约束

1. **不删除旧外卖模块**，旧表、旧包、旧页面保留可运行
2. **支付/退款/打款仅模拟**，不接真实第三方
3. **取餐后不可直接取消**，只能走售后或异常流程
4. **修改前先扫描真实代码结构**，禁止假设不存在的目录和文件
5. **每次变更更新日志** `project-logs/campus-relay/`
6. **优先保证项目可运行**，其次再考虑重构和美化

### 数据库迁移

迁移脚本位于 `backend/db/migrations/`，按版本号递增：

| 版本 | 说明 |
|------|------|
| V1 | 基线 schema（旧外卖表） |
| V2 | campus_relay_schema |
| V3 | campus_relay_order_timeline_columns |
| V4 | campus_relay_ops_and_settlement |
| V5 | campus_after_sale_decision_and_settlement_ops |
| V6 | campus_after_sale_execution_and_settlement_payout |
| V7 | campus_customer_receipt_and_settlement_audit |
| V8 | campus_courier_onboarding_bridge_replacement |
| V9 | campus_exception_record |
| V10 | campus_after_sale_execution_record |
| V11 | campus_settlement_batch_operation_record |
| V12 | campus_settlement_reconcile_difference_record |

新增 DB 结构时需同步维护：
- `backend/db/init.sql`
- `backend/db/migrations/`
- `backend/src/main/resources/db/schema-h2.sql`
- `backend/src/main/resources/db/data-h2.sql`
- `docs/db-overview.md`

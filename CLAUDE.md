# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 常用命令

后端（Spring Boot 3.2.4 / JDK 17 / Maven Wrapper）：
```powershell
cd backend
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd test
.\mvnw.cmd -Dtest=CampusCustomerOrderIntegrationTest test
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```
- `test` profile 使用 H2 内存库，会自动加载 `src/main/resources/db/schema-h2.sql` 与 `data-h2.sql`。
- `dev` profile 使用本机 MySQL/MariaDB（库名 `cangqiong_takeaway`），不会自动初始化 schema。

前端（Vue 3 / Vite / Element Plus）：
```powershell
cd frontend
npm install
npm run dev
npm run lint
npm run test
npm run test -- src/utils/text.spec.js
npm run build
```
- dev server 默认在 `http://localhost:5173`，并把 `/api` 代理到 `http://localhost:8080`。
- 现有 Vitest 用例包括 `src/stores/customer.spec.js` 与 `src/utils/text.spec.js`。

Android WebView 构建（单份前端源码，两个 Capacitor 壳）：
```powershell
cd frontend
npm run build:android:user
npm run build:android:parttime
npm run build:android:user:emulator
npm run build:android:parttime:emulator
npm run build:android:user:lan
npm run build:android:parttime:lan
npm run build:android:user:public
npm run build:android:parttime:public
```
```powershell
cd mobile/user-app
npm install
npm run cap:sync
npm run cap:sync:lan
npm run cap:sync:public
```
```powershell
cd mobile/parttime-app
npm install
npm run cap:sync
npm run cap:sync:lan
npm run cap:sync:public
```
- Android 构建模式必须配置显式 `VITE_API_BASE_URL`，不能使用默认 `/api` 代理。
- 默认模拟器 API base 使用 `10.0.2.2`；局域网真机和公网内测分别使用 ignored 的 `.env.android-*-lan` / `.env.android-*-public`。
- Android Gradle 本地构建需要 JDK 21；后端普通开发仍按 JDK 17。

试运营脚本（维护模式，不随意新增）：
```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunBackendCompile -RunFrontendBuild
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-api-base-check.ps1
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -ListDevices
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\android-smoke.ps1 -StartEmulator -ClearData
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1 -Full
```

## 项目定位

这是一个在旧“苍穹外卖”项目上增量改造出的校内兼职 / 校园代送试运营版。旧外卖模块仍是可运行资产，不能删除；校园代送功能以并行 campus 领域逐步扩展。

当前交付边界是本地可运行、可演示、可交接：支付、退款、打款均为模拟或只读记录；不接入真实支付网关、退款通道、打款通道、消息推送、实时调度、轨迹回放、路线规划或导航。

## 后端架构

后端位于 `backend/`，主包为 `com.cangqiong.takeaway`，校园代送新增代码位于 `com.cangqiong.takeaway.campus`。

| 维度 | 旧外卖模块 | 校园代送模块 |
| --- | --- | --- |
| Java 包 | `com.cangqiong.takeaway` | `com.cangqiong.takeaway.campus` |
| 主要表 | `employee`、`user`、`category`、`dish`、`cart`、`orders` 等 | `campus_*` 表，例如 relay order、courier profile、settlement、exception、after-sale、location、pickup point |
| 账号模型 | employee / user JWT | employee / customer / courier JWT，按 URI 前缀区分 |

关键约定：
- 控制器统一返回 `Result<T>`，成功码为 `200`，字段为 `code`、`msg`、`data`。
- JWT 拦截器按 URI 前缀判断角色：`/api/campus/admin/**` 需要 employee，`/api/campus/customer/**` 需要 customer，`/api/campus/courier/**` 需要 courier。
- 公开接口绕过鉴权：`/api/campus/public/**`、`/api/campus/courier/auth/token`、登录接口、`GET /api/public/**`。
- `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 是 onboarding 兼容桥路径，可接受 customer / courier token，不要随意收紧。
- 测试 profile 的 H2 schema 覆盖旧表和 campus 表；新增 DB 结构时同步维护 `backend/db/init.sql`、`backend/db/migrations/`、H2 schema/data 以及 `docs/db-overview.md`。

## 前端架构

前端位于 `frontend/`，使用 Vue 3、Vite、Pinia、Vue Router、Element Plus。一个 SPA 同时保留旧外卖管理端 / 用户端，并新增 campus customer、courier/parttime、admin 演示链路。

三类入口由 router guard 和 localStorage token 区分：
1. admin：`/login`、`/dashboard`、`/campus/*`，使用 `admin_token`。
2. customer：`/user/*`，使用 `customer_token`。
3. courier/parttime：`/parttime/*`、`/courier/*`，使用 `courier_token`。

API 封装位于 `src/api/`：
- campus：`campus-customer.js`、`campus-courier.js`、`campus-admin.js`。
- 旧外卖与通用：`customer.js`、`customer-order.js`、`cart.js`、`address.js`、`order.js`、`statistics.js`、`shop.js` 等。
- 共享 axios 实例在 `src/utils/request.js`，负责按 URL 前缀附加对应 token，并在 401 时清理对应 token 后跳转到角色登录页。

`VITE_APP_SHELL` 控制构建壳：标准 dev 下三种角色都可访问；Android user / parttime 模式分别输出到 `dist-android-user`、`dist-android-parttime`。

## 移动端架构

`mobile/` 下有两个 Capacitor Android 壳，共用 `frontend/` 业务源码：
- `mobile/user-app`：用户端，包名 `com.xiaoyu.campus.user`，默认入口 `/user/login`，构建产物来自 `frontend/dist-android-user`。
- `mobile/parttime-app`：兼职端，包名 `com.xiaoyu.campus.parttime`，默认入口 `/parttime/login`，构建产物来自 `frontend/dist-android-parttime`。

移动端只负责容器、包名、App 名称和原生工程；不要复制第二套前端源码，不要把 admin 后台打进移动端默认入口，也不要因 Android 壳改 bridge、鉴权、token 附着逻辑或后端接口。

## 当前硬约束

1. 不删除、不重构旧外卖模块；旧表、旧包、旧页面要继续可运行。
2. 新 campus 表使用 `campus_` 前缀；新 campus Java 代码放在 `com.cangqiong.takeaway.campus`。
3. bridge 主线冻结在 Phase A no-op，不继续扩展。
4. 展示 polish、媒体线、bridge 线已冻结；当前主线是 ops hardening 与 Android internal testing。
5. 支付、退款、打款只能是 mock / simulated，不接真实第三方能力。
6. 取餐后禁止直接取消订单，只能走售后或异常流程。
7. 前端构建优化（Sass 迁移、chunk splitting、Element Plus tree-shaking）冻结，除非有真实性能信号。
8. `scripts/trial-operation/` 为冻结/维护模式；没有重复人工错误要消除时，不新增脚本。
9. 修改前必须扫描真实代码结构；路径、包名、类名、表名必须来自当前仓库，不能凭空编造。
10. 非平凡 campus 变更后，更新 `project-logs/campus-relay/`：目标、完成项、修改文件、遗留问题、下一步。

## Demo 账号（test profile seed data）

| 角色 | 手机号 | 密码 |
| --- | --- | --- |
| admin | `13800138000` | `123456` |
| customer | `13900139000` | `123456` |
| courier | customer onboarding 后申请 token | `123456` |

## 重要文档入口

- `docs/README.md`：当前项目文档索引。
- `docs/delivery-guide.md`：本地启动、交付定位、主演示链路。
- `docs/api-overview.md`：API 总览。
- `docs/db-overview.md`：数据库总览。
- `docs/simulated-funds-boundary.md`：模拟资金链路边界。
- `docs/trial-operation-preflight.md`：试运营 preflight 手册。
- `docs/deployment/internal-trial-compose.md`：内测型试运营 Compose 部署。
- `mobile/README.md`：Android 壳工程说明。
- `scripts/trial-operation/README.md`：试运营脚本说明。
- `project-logs/README.md`：项目日志索引。
- `project-logs/campus-relay/summary.md`：当前 campus 主线总览。
- `project-logs/campus-relay/pending-items.md`：待处理事项。
- `project-logs/campus-relay/global-working-memory.md`：快速恢复上下文。
- `docs/legacy-takeaway/` 与 `project-logs/legacy-takeaway/`：旧外卖阶段归档，仅作历史参考。

## 本地工具

- `.mcp.json` 配置了 Playwright，可用于 UI smoke。
- 前端 UI 改动完成后，应启动 dev server 并用浏览器验证主路径和边界状态。
- Android smoke 脚本只安装、启动、截图；不会断言 WebView 内业务登录是否成功，业务接口可达性仍需手工或 DevTools smoke 验证。

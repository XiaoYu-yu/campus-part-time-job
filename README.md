# show_shop1

`show_shop1` 是一个前后端分离的外卖系统项目，包含管理后台和用户端两条业务链路。当前交付版本已经完成核心修复、生产准备基础加固，以及本地开发数据库切换到 MariaDB，适合你用 IDEA + HBuilder X 在 Windows 上直接打开和运行。

## 项目状态

- 状态：可公开交付的训练项目
- 仓库地址：[XiaoYu-yu/show_shop1](https://github.com/XiaoYu-yu/show_shop1)
- 开源许可证：[`MIT`](D:/20278/code/show_shop1/LICENSE)
- 后端构建方式：`Maven Wrapper`

## 技术栈

- 前端：Vue 3、Vite、Vue Router、Pinia、Element Plus、ECharts、Axios、Vitest、ESLint
- 后端：Spring Boot 3.2.4、MyBatis、MariaDB（本地开发）、MySQL/MariaDB（生产）、JWT、Maven、JUnit 5
- 运行环境：Node.js 18+、JDK 17

## 目录结构

```text
show_shop1/
├── backend/                后端服务
├── frontend/               前端应用
├── docs/                   补充文档索引
├── project-logs/           本轮修复日志
├── 项目检查说明.md         项目审计说明
└── README.md               当前总览
```

## 当前模块状态

### 管理后台

- 员工、分类、菜品、套餐、订单、统计、店铺营业状态管理可走真实接口
- 订单查询参数已统一为 `orderNo/customerName/status/beginTime/endTime/page/pageSize`
- 仪表盘和统计页使用真实聚合接口

### 用户端

- 用户登录、用户信息、购物车、地址管理、下单、订单查询、再来一单、店铺营业状态已接入真实接口
- 用户端采用独立 token，与管理端 token 分离
- 用户页面不再依赖 `frontend/src/stores/mock.js`

## 启动顺序

1. 先确认 MariaDB 服务已启动
2. 再启动后端
3. 最后启动前端

## 快速开始

### 1. 开发环境数据库

- 开发环境默认使用本机 `MariaDB`
- 当前默认连接参数：
  - 主机：`127.0.0.1`
  - 端口：`3306`
  - 数据库：`cangqiong_takeaway`
  - 用户：`root`
- 初始化脚本使用 [backend/db/init.sql](D:/20278/code/show_shop1/backend/db/init.sql)
- 建议使用 `HeidiSQL` 或 `DBeaver` 查看数据库

### 2. 生产环境数据库

- 生产环境仍按 MySQL 配置设计
- 迁移脚本目录在 [backend/db/migrations](D:/20278/code/show_shop1/backend/db/migrations)
- 原始初始化脚本保留在 [backend/db/init.sql](D:/20278/code/show_shop1/backend/db/init.sql)

### 3. 启动后端

```bash
cd backend
.\mvnw.cmd test
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

默认地址：`http://localhost:8080`

注意：

- 项目不再在 `application.properties` 中写死 `spring.profiles.active`
- 开发环境请显式使用 `dev`
- 生产环境必须显式使用 `prod`
- 本地开发默认连接 MariaDB，而不是 H2

### 4. 启动前端

```bash
cd frontend
npm install
npm run lint
npm run test
npm run dev
```

默认地址：`http://localhost:5173`

## 生产环境约束

- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET` 必须显式配置，且长度至少 32
- `APP_CORS_ALLOWED_ORIGINS` 必须显式配置为正式前端域名
- `prod` 环境禁用 H2、禁用 H2 Console、禁用自动初始化脚本
- 上传文件仅允许白名单图片类型，统一使用 UUID 文件名，并通过受控接口访问

## 已验证可访问地址

- 前端首页：[http://localhost:5173](http://localhost:5173)
- 后端服务：[http://localhost:8080](http://localhost:8080)
- 店铺状态接口：[http://localhost:8080/api/public/shop/status](http://localhost:8080/api/public/shop/status)

## 默认测试账号

### 管理端

- 手机号：`13800138000`
- 密码：`123456`

### 用户端

- 手机号：`13900139000`
- 密码：`123456`

## 本轮修复重点

- 修复后端 Lombok 依赖，恢复 `mvn test` 和 `mvn -DskipTests compile`
- 统一订单主表为 `orders`
- 统一订单查询契约和状态码
- 补齐 `/api/statistics/dashboard`、`/sales`、`/users`、`/popular-dishes`
- 拆分管理端与用户端 JWT 鉴权
- 用户端去 mock 化并接入真实接口
- 密码存储从无盐 MD5 升级为 BCrypt，并支持老密码首次登录自动迁移
- 生产环境 JWT 密钥改为强制环境变量校验
- 上传接口增加扩展名、MIME、大小和图片内容校验，并移除裸露静态目录
- CORS 改为环境化配置
- 新增数据库迁移目录、生产部署说明、备份与回滚说明、关键接口回归测试
- 本地开发默认数据库从 H2 切换为 MariaDB，便于使用中文图形工具管理
- 增加 `.gitignore`、前端 lint/test、后端最小回归测试
- 公开仓库改用 `Maven Wrapper`，不再跟踪 Maven 发行包目录

## 验证结果

已验证以下命令可执行：

- 后端：`mvn test`
- 后端：`mvn -DskipTests compile`
- 前端：`npm run lint`
- 前端：`npm run test`
- 前端：`npm run build`

前端构建目前仍有两类非阻塞告警：

- Sass `@import` 弃用告警
- 大体积 chunk 告警

## 文档入口

- 交付说明：[项目交付说明.md](D:/20278/code/show_shop1/项目交付说明.md)
- 变更记录：[CHANGELOG.md](D:/20278/code/show_shop1/CHANGELOG.md)
- 贡献说明：[CONTRIBUTING.md](D:/20278/code/show_shop1/CONTRIBUTING.md)
- 安全说明：[SECURITY.md](D:/20278/code/show_shop1/SECURITY.md)
- 总体检查说明：[项目检查说明.md](D:/20278/code/show_shop1/项目检查说明.md)
- 前端说明：[frontend/README.md](D:/20278/code/show_shop1/frontend/README.md)
- 文档索引：[docs/README.md](D:/20278/code/show_shop1/docs/README.md)
- 修复日志：[project-logs/summary.md](D:/20278/code/show_shop1/project-logs/summary.md)
- 生产准备日志：[project-logs/production-readiness.md](D:/20278/code/show_shop1/project-logs/production-readiness.md)
- 环境搭建日志：[project-logs/runtime-setup.md](D:/20278/code/show_shop1/project-logs/runtime-setup.md)

## 仓库维护约定

- 公开仓库默认以 `README + LICENSE + CHANGELOG + CONTRIBUTING + SECURITY` 作为基础交付材料
- Issue 和 PR 模板已放入 [`.github/`](D:/20278/code/show_shop1/.github)

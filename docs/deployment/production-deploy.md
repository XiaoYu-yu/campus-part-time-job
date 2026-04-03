# 生产部署说明

## 部署目标

将 `show_shop1` 以“前端静态资源 + Spring Boot 后端 + MySQL 数据库”的方式部署到生产环境。

## 前置条件

- JDK 17 或 21 LTS
- Node.js 18+
- Maven 3.9+
- MySQL 8+
- 已准备正式域名
- 已准备环境变量：
  - `SPRING_PROFILES_ACTIVE=prod`
  - `JWT_SECRET`
  - `APP_CORS_ALLOWED_ORIGINS`
  - `DB_HOST`
  - `DB_PORT`
  - `DB_NAME`
  - `DB_USERNAME`
  - `DB_PASSWORD`
  - 可选：`APP_UPLOAD_STORAGE_PATH`

## 部署步骤

### 1. 数据库准备

1. 创建生产数据库
2. 执行 [V1__baseline_schema.sql](D:/20278/code/show_shop1/backend/db/migrations/V1__baseline_schema.sql)
3. 按正式业务要求导入初始员工、分类、菜品、套餐等基础数据

### 2. 构建前端

```bash
cd frontend
npm install
npm run lint
npm run test
npm run build
```

将 `frontend/dist/` 部署到 Nginx、对象存储静态站点或 CDN。

### 3. 构建后端

```bash
cd backend
..\apache-maven-3.9.14\bin\mvn.cmd test
..\apache-maven-3.9.14\bin\mvn.cmd -DskipTests package
```

### 4. 启动后端

```bash
java -jar target/takeaway-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

也可以通过环境变量设置：

```bash
set SPRING_PROFILES_ACTIVE=prod
java -jar target/takeaway-0.0.1-SNAPSHOT.jar
```

## 上线前检查清单

- `JWT_SECRET` 已配置且长度至少 32
- `APP_CORS_ALLOWED_ORIGINS` 已指向正式前端域名
- 生产环境未启用 H2
- 生产环境未启用自动初始化脚本
- 上传目录位于受控存储路径
- 关键接口回归测试已通过
- 生产 JVM 使用 LTS 版本，避免在高版本 JDK 下放大三方依赖兼容性告警

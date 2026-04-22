# 部署说明

## 当前定位

当前项目可用于本地演示、试运营验证和交接答辩。它不是完整生产版，真实支付、真实退款、真实打款、地图 SDK、消息推送、生产监控与限流尚未接入。

如需正式上线，应先补齐这些外部能力和生产安全能力，再按正式发布流程部署。

## 前置条件

- JDK 17 或 21 LTS
- Node.js 18+
- MySQL 8+ 或 MariaDB
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

环境变量样例与密钥边界见：

- [环境变量与密钥配置清单](env-and-secret-checklist.md)

仓库已提供以下占位样例，复制后在本地或部署环境中填真实值，不要把真实值提交到仓库：

- 根目录 `.env.example`
- `backend/.env.example`
- `frontend/.env.example`

## 数据库准备

1. 创建生产数据库。
2. 按顺序执行 `backend/db/migrations/` 中的版本脚本。
3. 不建议生产环境直接依赖 H2 或自动初始化脚本。
4. 初始化 admin、customer、演示样本数据前，应先确认是否为演示环境。

## 前端构建

```powershell
cd D:\20278\code\Campus part-time job\frontend
npm install
npm run build
```

将 `frontend/dist/` 部署到 Nginx、静态资源服务器或对象存储静态站点。

如果需要腾讯地图预览，构建前必须通过环境变量注入：

```powershell
set VITE_TENCENT_MAP_KEY=replace-with-real-key
npm run build
```

不要把真实腾讯地图 key 写入 `frontend/.env.example`、README、日志或源码。

## 后端构建

```powershell
cd D:\20278\code\Campus part-time job\backend
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd -DskipTests package
```

## 后端启动

```powershell
java -jar target/takeaway-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

或通过环境变量指定：

```powershell
set SPRING_PROFILES_ACTIVE=prod
java -jar target/takeaway-0.0.1-SNAPSHOT.jar
```

## 上线前检查

1. `JWT_SECRET` 已配置且长度足够。
2. `APP_CORS_ALLOWED_ORIGINS` 只包含正式前端域名。
3. 生产环境未启用 H2 Console。
4. 数据库迁移脚本已在预发环境验证。
5. 上传目录位于受控存储路径。
6. customer / courier / admin 关键链路已完成回归。
7. bridge 当前仍保留，不应在上线前临时删除。
8. 真实支付、地图、消息、打款若未接入，应在页面和交付说明中明确为模拟或试运营能力。
9. `frontend/.env.local`、真实数据库密码和真实地图 key 没有被提交到仓库。
10. `.env.example` 文件只包含占位值。

部署完成后继续执行：

- [部署后 Smoke Checklist](post-deploy-smoke-checklist.md)

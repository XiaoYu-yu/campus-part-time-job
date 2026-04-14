# 可生产部署准备日志

## 目标

将 `show_shop1` 从“可本地联调”继续推进到“可生产部署准备”阶段，优先修复安全和环境配置问题，同时保持 `dev` 环境仍可直接运行。

## 本轮完成事项

### 1. 密码存储升级

- 移除“无盐 MD5 作为生产存储方案”的做法，新增 BCrypt 密码服务
- 兼容老账号迁移：
  - 登录时先识别是否为旧 MD5 散列
  - 若旧密码校验成功，则自动升级为 BCrypt 并写回数据库
- 新建员工账号默认密码改为 BCrypt 存储
- 用户登录链路也支持老口令首次登录自动迁移

### 2. 强制生产 profile

- 删除 `application.properties` 中写死 profile 的方式
- 改为通过启动参数或环境变量显式指定 `dev` / `prod`
- `dev` 保留 H2 和 SQL 初始化，仅用于本地开发
- `prod` 禁用 H2、禁用 H2 Console、禁用自动初始化脚本

### 3. JWT 密钥强校验

- 生产环境 JWT 密钥仅从 `JWT_SECRET` 环境变量读取
- 不再保留默认回退值
- 新增生产启动校验器：
  - 未配置 `JWT_SECRET` 则启动失败
  - 密钥长度少于 32 则启动失败
  - 未配置生产 CORS 域名则启动失败

### 4. 上传安全加固

- 上传接口增加扩展名白名单校验
- 上传接口增加 MIME 白名单校验
- 上传接口增加大小限制校验
- 上传接口增加图片内容有效性校验
- 文件名改为 UUID，不再使用原始文件名
- 取消上传目录的静态资源裸暴露，改为通过 `/api/files/{name}` 受控读取

### 5. CORS 环境化

- 将 CORS 配置从代码硬编码改为配置项
- `dev` 允许本地前端地址
- `prod` 要求显式配置正式前端域名

### 6. 发布保障

- 新增数据库迁移目录 `backend/db/migrations`
- 新增生产部署说明和备份回滚说明
- 新增关键接口集成测试：
  - 登录与老密码迁移
  - 用户下单
  - 用户订单查询
  - 统计接口
  - 上传接口
  - 受控文件访问

## 关键修改文件

- [backend/pom.xml](D:/20278/code/show_shop1/backend/pom.xml)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/PasswordServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/PasswordServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/EmployeeServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/EmployeeServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/UserServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/UserServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/config/ProductionReadinessValidator.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/config/ProductionReadinessValidator.java)
- [backend/src/main/java/com/cangqiong/takeaway/config/WebMvcConfig.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/config/WebMvcConfig.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/LocalFileStorageServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/LocalFileStorageServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/FileController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/FileController.java)
- [backend/src/main/resources/application.properties](D:/20278/code/show_shop1/backend/src/main/resources/application.properties)
- [backend/src/main/resources/application-dev.properties](D:/20278/code/show_shop1/backend/src/main/resources/application-dev.properties)
- [backend/src/main/resources/application-prod.properties](D:/20278/code/show_shop1/backend/src/main/resources/application-prod.properties)
- [backend/src/test/java/com/cangqiong/takeaway/AuthenticationMigrationIntegrationTest.java](D:/20278/code/show_shop1/backend/src/test/java/com/cangqiong/takeaway/AuthenticationMigrationIntegrationTest.java)
- [backend/src/test/java/com/cangqiong/takeaway/CriticalApiIntegrationTest.java](D:/20278/code/show_shop1/backend/src/test/java/com/cangqiong/takeaway/CriticalApiIntegrationTest.java)
- [docs/deployment/production-deploy.md](D:/20278/code/show_shop1/docs/deployment/production-deploy.md)
- [docs/deployment/backup-and-rollback.md](D:/20278/code/show_shop1/docs/deployment/backup-and-rollback.md)

## 验证结果

- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd test` 通过
- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd -DskipTests compile` 通过
- `backend`: 使用 `prod` profile 启动且未配置 `JWT_SECRET` 时，应用按预期失败并输出明确错误信息
- `frontend`: `npm run lint` 通过
- `frontend`: `npm run test` 通过
- `frontend`: `npm run build` 通过
- 关键回归测试覆盖：
  - 管理员登录
  - 用户登录
  - 老密码升级为 BCrypt
  - 用户下单与订单查询
  - 仪表盘统计接口
  - 上传非法文件拦截
  - 合法图片上传与受控读取

## 当前结论

项目现在已经从“能跑”推进到了“可生产部署准备”阶段：

- `dev` 环境仍可本地快速联调
- `prod` 环境不再允许危险默认配置直接启动
- 密码、JWT、上传、CORS 和部署说明都完成了第一轮硬化
- 生产部署建议使用 JDK 17 或 21 LTS

仍然没有完成的部分见 [pending-items.md](D:/20278/code/show_shop1/project-logs/pending-items.md)，主要集中在进一步的生产级安全能力和观测能力，而不是当前这一轮的基础部署准备。

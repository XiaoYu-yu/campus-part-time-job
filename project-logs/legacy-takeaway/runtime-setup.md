# 运行环境搭建日志

## 目标

把项目从“代码修好了但本机缺 MySQL 导致接口不能真正返回数据”的状态，推进到“前后端本地直接启动即可联调”的完整状态。

## 做了什么

1. 检查本机数据库环境，确认没有可直接复用的 MySQL/MariaDB 进程或安装目录
2. 将开发环境数据源从本地 MySQL 切换为 H2 文件数据库
3. 新增 H2 开发初始化脚本：
   - [schema-h2.sql](D:/20278/code/show_shop1/backend/src/main/resources/db/schema-h2.sql)
   - [data-h2.sql](D:/20278/code/show_shop1/backend/src/main/resources/db/data-h2.sql)
4. 为 H2 控制台开启访问路径 `/h2-console`
5. 修复若干开发库兼容问题：
   - 购物车查询移除 MySQL 专用空安全比较符 `<=>`
   - 多处 `NOW()` 改为 `CURRENT_TIMESTAMP`
   - 统计 SQL 改为 `CAST(... AS DATE)` / `EXTRACT(HOUR ...)` 的跨库写法
   - 统计分布查询用显式结果映射避开 H2 对 `value` 标识符的解析问题
6. 保留生产环境 MySQL 配置不变

## 结果

- 后端现在可以在无 MySQL 环境下启动
- 启动时自动创建并填充开发数据
- 管理端和用户端都能使用默认账号登录
- 管理端统计接口、用户端订单接口、公开菜品接口都已验证可返回真实数据

## 本次涉及文件

- [backend/pom.xml](D:/20278/code/show_shop1/backend/pom.xml)
- [backend/src/main/resources/application-dev.properties](D:/20278/code/show_shop1/backend/src/main/resources/application-dev.properties)
- [backend/src/main/resources/db/schema-h2.sql](D:/20278/code/show_shop1/backend/src/main/resources/db/schema-h2.sql)
- [backend/src/main/resources/db/data-h2.sql](D:/20278/code/show_shop1/backend/src/main/resources/db/data-h2.sql)
- [backend/src/main/java/com/cangqiong/takeaway/db/H2Functions.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/db/H2Functions.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/AddressMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/AddressMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/CartMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/CartMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/CategoryMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/CategoryMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/DishMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/DishMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/EmployeeMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/EmployeeMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/OrderMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/OrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/SetmealMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/SetmealMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/ShopConfigMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/ShopConfigMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/StatisticsMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/StatisticsMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/StatisticsVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/StatisticsVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/StatisticsServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/StatisticsServiceImpl.java)

## 运行记录

- 前端运行日志：
  - [frontend.out.log](D:/20278/code/show_shop1/project-logs/runtime/frontend.out.log)
  - [frontend.err.log](D:/20278/code/show_shop1/project-logs/runtime/frontend.err.log)
- 后端运行日志：
  - [backend.out.log](D:/20278/code/show_shop1/project-logs/runtime/backend.out.log)
  - [backend.err.log](D:/20278/code/show_shop1/project-logs/runtime/backend.err.log)

## 验证结果

- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd test` 通过
- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd -DskipTests compile` 通过
- `frontend`: `npm run lint` 通过
- `frontend`: `npm run test` 通过
- `frontend`: `npm run build` 通过
- `GET /api/public/shop/status` 返回 200
- `POST /api/employees/login` 返回 200
- `GET /api/statistics/dashboard` 返回 200
- `POST /api/users/login` 返回 200
- `GET /api/user/orders` 返回 200

# 交付整理日志

## 目标

将项目整理为适合当前这台 Windows 电脑直接交付和直接启动的训练项目包，不再继续追加功能，只做目录清理、数据库落地和启动文档定稿。

## 本轮做了什么

### 1. 本地开发数据库切换到 MariaDB

- 确认本机 `MariaDB` 服务已安装并运行
- 使用 `root / xyz12345` 连通数据库
- 执行 [backend/db/init.sql](D:/20278/code/show_shop1/backend/db/init.sql) 初始化 `cangqiong_takeaway`
- 将开发环境配置改为默认连接本机 `MariaDB`
- 保留 H2 仅用于自动化测试，不再作为平时开发默认数据库

### 2. 交付目录清理

- 删除前端依赖目录、构建产物和后端编译产物
- 删除私有编辑器配置和 AI 规范目录
- 删除临时运行日志
- 删除历史噪音文档
- 删除旧的 H2 本地数据目录，避免与新的 MariaDB 开发方式混淆

### 3. 启动文档定稿

- 重写根 README，明确当前项目以 MariaDB 为本地开发数据库
- 新增 [项目交付说明.md](D:/20278/code/show_shop1/项目交付说明.md)
- 明确说明：
  - 如何用 IDEA 启动后端
  - 如何用 HBuilder X 打开前端并通过终端启动
  - 如何理解和使用 MariaDB、HeidiSQL、DBeaver

## 本轮修改文件

- [backend/src/main/resources/application-dev.properties](D:/20278/code/show_shop1/backend/src/main/resources/application-dev.properties)
- [README.md](D:/20278/code/show_shop1/README.md)
- [frontend/README.md](D:/20278/code/show_shop1/frontend/README.md)
- [docs/README.md](D:/20278/code/show_shop1/docs/README.md)
- [项目交付说明.md](D:/20278/code/show_shop1/项目交付说明.md)

## 本轮删除内容

- `frontend/dist`
- `frontend/node_modules`
- `backend/target`
- `backend/data`
- `project-logs/runtime`
- `.vscode`
- `frontend/.vscode`
- `.trae`
- `frontend/美化升级修改说明.md`

## 验证结果

- MariaDB 连接成功，版本：`12.2.2-MariaDB`
- `cangqiong_takeaway` 已初始化完成
- 默认管理账号和用户账号已导入
- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd test` 通过
- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd -DskipTests compile` 通过
- `frontend`: `npm install` 通过
- `frontend`: `npm run lint` 通过
- `frontend`: `npm run test` 通过
- `frontend`: `npm run build` 通过
- 运行态验证：
  - `GET /api/public/shop/status` 返回 200
  - `POST /api/employees/login` 返回 200

## 当前交付结论

项目现在已经整理为适合你当前环境的交付包：

- 本地数据库使用 MariaDB
- 后端适合用 IDEA 启动
- 前端适合用 HBuilder X 打开并通过终端运行
- 文档、日志、源码和内置 Maven 都保留
- 临时产物和私有噪音文件已清理

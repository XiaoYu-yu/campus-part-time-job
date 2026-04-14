# 修复总览

本轮对 `show_shop1` 按 7 个阶段推进修复，随后又补了一轮“可生产部署准备”加固，目标是把项目从“可展示但契约不一致、用户端依赖 mock、后端构建有问题”的状态，修复到“前后端主要链路可联调、基础命令可执行、文档与代码一致，并具备生产部署前置约束”的状态。

## 已完成事项

1. 修复后端构建，恢复 Maven 正常测试与编译
2. 统一订单主表为 `orders`
3. 统一订单接口契约与状态码语义
4. 补齐统计聚合接口，打通仪表盘与统计页
5. 拆分管理端和用户端鉴权策略
6. 用户端改用真实接口，核心页面去 mock 化
7. 补齐 `.gitignore`、README、lint/test、修复日志
8. 开发环境切换为内置 H2 文件数据库，项目可在无 MySQL 条件下启动联调
9. 密码体系升级为 BCrypt，兼容老 MD5 口令首次登录自动迁移
10. 取消默认激活 profile，改为启动参数或环境变量显式指定
11. 为 `prod` 增加 JWT 密钥和 CORS 配置强校验
12. 上传链路增加扩展名、MIME、大小和图片内容校验，并改为受控读取
13. 新增数据库迁移目录、生产部署文档、备份回滚文档和关键接口集成测试
14. 本地开发数据库切换到 MariaDB，并补齐最终交付说明
15. 清理构建产物、私有配置、临时日志，整理为可直接交付的训练项目包
16. 补齐公开仓库标准材料，包括许可证、贡献说明、变更记录、安全说明和 GitHub 模板

## 已验证命令

- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd test`
- `backend`: `..\apache-maven-3.9.14\bin\mvn.cmd -DskipTests compile`
- `backend`: `prod` profile 未配置 `JWT_SECRET` 时启动失败并输出明确错误
- `frontend`: `npm run lint`
- `frontend`: `npm run test`
- `frontend`: `npm run build`
- 运行验证：
  - 后端 `http://localhost:8080/api/public/shop/status` 返回 200
  - 管理端 `/api/employees/login` 返回 200
  - 管理端 `/api/statistics/dashboard` 登录后返回 200
  - 用户端 `/api/user/orders` 登录后返回 200
  - 上传接口 `/api/upload/image` 与受控文件读取 `/api/files/{name}` 回归测试通过
  - 本地 MariaDB `cangqiong_takeaway` 已完成初始化导入

## 当前结论

- 管理端与用户端的基础业务链路已能围绕真实接口运行
- 统计页和仪表盘不再依赖缺失接口
- 认证链路已经按端区分 token
- 生产环境不再允许使用默认 JWT 密钥回退
- 生产环境配置必须显式声明 profile 与允许域名
- 本地开发默认数据库已经切换为 MariaDB，可直接配合 HeidiSQL / DBeaver 使用
- 公开仓库基础材料已经补齐，并已成功推送到 GitHub
- 前端构建仍有 Sass 与 chunk 体积告警，但不阻塞运行
- 生产运行时建议使用 JDK 17 或 21 LTS

## 日志索引

- [step-01.md](D:/20278/code/show_shop1/project-logs/step-01.md)
- [step-02.md](D:/20278/code/show_shop1/project-logs/step-02.md)
- [step-03.md](D:/20278/code/show_shop1/project-logs/step-03.md)
- [step-04.md](D:/20278/code/show_shop1/project-logs/step-04.md)
- [step-05.md](D:/20278/code/show_shop1/project-logs/step-05.md)
- [step-06.md](D:/20278/code/show_shop1/project-logs/step-06.md)
- [step-07.md](D:/20278/code/show_shop1/project-logs/step-07.md)
- [production-readiness.md](D:/20278/code/show_shop1/project-logs/production-readiness.md)
- [delivery-handover.md](D:/20278/code/show_shop1/project-logs/delivery-handover.md)
- [github-release.md](D:/20278/code/show_shop1/project-logs/github-release.md)
- [runtime-setup.md](D:/20278/code/show_shop1/project-logs/runtime-setup.md)

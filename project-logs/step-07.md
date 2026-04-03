# Step 07 工程化收尾

## 做了什么

- 修复生产环境数据库时区配置
- 新增根目录 `.gitignore`
- 重写根 README、前端 README、docs README
- 增加前端 `lint` 和 `test` 脚本及配置
- 增加前端最小 store 测试
- 增加后端订单与统计控制器最小回归测试
- 修复 `BaseDialogForm.vue` 对 props 的直接写入问题
- 生成本轮修复日志目录
- 补充开发环境内置 H2 数据库，保证本地无需额外安装 MySQL 也能联调

## 为什么这么改

- 没有工程化收尾，项目即使能跑也难以长期维护，文档还会继续和真实状态脱节

## 验证结果

- `backend`: `mvn test` 通过
- `backend`: `mvn -DskipTests compile` 通过
- `frontend`: `npm run lint` 通过
- `frontend`: `npm run test` 通过
- `frontend`: `npm run build` 通过
- 运行接口 smoke test 通过

## 修改文件

- [backend/src/main/resources/application-prod.properties](D:/20278/code/show_shop1/backend/src/main/resources/application-prod.properties)
- [backend/src/test/java/com/cangqiong/takeaway/OrderControllerTest.java](D:/20278/code/show_shop1/backend/src/test/java/com/cangqiong/takeaway/OrderControllerTest.java)
- [backend/src/test/java/com/cangqiong/takeaway/StatisticsControllerTest.java](D:/20278/code/show_shop1/backend/src/test/java/com/cangqiong/takeaway/StatisticsControllerTest.java)
- [.gitignore](D:/20278/code/show_shop1/.gitignore)
- [README.md](D:/20278/code/show_shop1/README.md)
- [frontend/README.md](D:/20278/code/show_shop1/frontend/README.md)
- [docs/README.md](D:/20278/code/show_shop1/docs/README.md)
- [frontend/package.json](D:/20278/code/show_shop1/frontend/package.json)
- [frontend/eslint.config.js](D:/20278/code/show_shop1/frontend/eslint.config.js)
- [frontend/vitest.config.js](D:/20278/code/show_shop1/frontend/vitest.config.js)
- [frontend/src/stores/customer.spec.js](D:/20278/code/show_shop1/frontend/src/stores/customer.spec.js)
- [frontend/src/components/BaseDialogForm.vue](D:/20278/code/show_shop1/frontend/src/components/BaseDialogForm.vue)
- [project-logs/summary.md](D:/20278/code/show_shop1/project-logs/summary.md)
- [project-logs/step-01.md](D:/20278/code/show_shop1/project-logs/step-01.md)
- [project-logs/step-02.md](D:/20278/code/show_shop1/project-logs/step-02.md)
- [project-logs/step-03.md](D:/20278/code/show_shop1/project-logs/step-03.md)
- [project-logs/step-04.md](D:/20278/code/show_shop1/project-logs/step-04.md)
- [project-logs/step-05.md](D:/20278/code/show_shop1/project-logs/step-05.md)
- [project-logs/step-06.md](D:/20278/code/show_shop1/project-logs/step-06.md)
- [project-logs/step-07.md](D:/20278/code/show_shop1/project-logs/step-07.md)
- [project-logs/file-change-list.md](D:/20278/code/show_shop1/project-logs/file-change-list.md)
- [project-logs/pending-items.md](D:/20278/code/show_shop1/project-logs/pending-items.md)
- [project-logs/runtime-setup.md](D:/20278/code/show_shop1/project-logs/runtime-setup.md)

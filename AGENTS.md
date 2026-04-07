\# AGENTS.md



项目名称：校内兼职平台 / 校园代送改造

项目根目录：D:\\20278\\code\\Campus part-time job

当前阶段：Step 02 - 后端领域与数据库改造

基础项目：由苍穹外卖风格项目改造，不重建后端，优先并行新增 campus 领域



全局约束：

1\. 不删除旧外卖模块，默认并行新增 campus 领域

2\. 新表统一使用 campus\_ 前缀

3\. 新包根统一使用 com.cangqiong.takeaway.campus

4\. 普通用户复用 user

5\. 管理员复用 employee

6\. 兼职配送员独立建模为 CourierProfile，通过 userId 关联

7\. 第一版支付只允许模拟支付，不接第三方支付

8\. 已取餐后不可直接取消，只能进入售后或异常流程

9\. 本阶段优先后端领域、数据库、接口闭环，不优先改前端页面

10\. 不破坏当前旧链路，旧外卖模块保留可运行

11\. 每次任务结束必须更新 project-logs/campus-relay/ 日志

12\. 日志必须写清楚：本次目标、已完成项、修改文件、遗留问题、下一步建议

13\. 修改前先扫描真实代码结构，禁止假设不存在的目录和文件

14\. 优先保证项目可运行，其次再考虑重构和美化

15\. 所有路径、包名、表名、类名必须基于仓库真实结构输出，不允许编造



当前已知文档：

1\. docs/README.md

2\. docs/campus-relay/domain-refactor-plan.md

3\. docs/campus-relay/legacy-to-campus-mapping.md

4\. project-logs/campus-relay/summary.md

5\. project-logs/campus-relay/pending-items.md

6\. project-logs/campus-relay/file-change-list.md

7\. project-logs/campus-relay/step-01-domain-model-planning.md



本阶段目标：

1\. 新增 campus\_\* 核心表

2\. 新增 campus 领域实体、DTO、VO、Mapper、Service、Controller

3\. 逐步打通 创建代送单 -> 模拟支付 -> 接单 -> 取餐 -> 配送 -> 完成 的最小闭环

4\. 不在本阶段大规模替换前端页面


# Tasks

- [ ] Task 1: 创建移动端视觉基线样式文件
  - [ ] SubTask 1.1: 新建 `frontend/src/styles/mobile-theme.css`，定义移动端专用 CSS 变量和组件基类
  - [ ] SubTask 1.2: 在 UserLayout.vue 和 ParttimeLayout.vue 中引入 mobile-theme.css
  - [ ] SubTask 1.3: 定义统一卡片、按钮、状态标签、空态、错误态、loading 态的 CSS 类

- [ ] Task 2: 重构 UserLayout.vue 页面壳
  - [ ] SubTask 2.1: 简化顶部栏样式，去掉渐变装饰，改为白底 + 轻边框
  - [ ] SubTask 2.2: 优化底部导航样式，增加 safe-area 适配
  - [ ] SubTask 2.3: 确保主内容区 padding 和底部留白正确
  - [ ] SubTask 2.4: 添加 overflow-x: hidden 防止横向溢出

- [ ] Task 3: 重构 ParttimeLayout.vue 页面壳
  - [ ] SubTask 3.1: 简化顶部栏样式，与用户端视觉统一但保持品牌区分
  - [ ] SubTask 3.2: 优化底部导航样式，增加 safe-area 适配
  - [ ] SubTask 3.3: 确保主内容区宽度限制和底部留白正确
  - [ ] SubTask 3.4: 添加 overflow-x: hidden 防止横向溢出

- [ ] Task 4: 重构用户端 Login.vue 视觉
  - [ ] SubTask 4.1: 简化登录卡片样式，去掉装饰元素
  - [ ] SubTask 4.2: 优化输入框和按钮的移动端触控体验
  - [ ] SubTask 4.3: 适配 360px 宽度

- [ ] Task 5: 重构用户端 Home.vue 视觉
  - [ ] SubTask 5.1: 简化 Hero 卡片，去掉毛玻璃和渐变装饰
  - [ ] SubTask 5.2: 统一快捷入口卡片样式
  - [ ] SubTask 5.3: 优化入驻状态三态（loading/error/正常）展示
  - [ ] SubTask 5.4: 适配 360px 宽度，确保无横向溢出

- [ ] Task 6: 重构用户端 CampusRelayOrders.vue 视觉
  - [ ] SubTask 6.1: 简化 Hero 卡片和引导卡片样式
  - [ ] SubTask 6.2: 统一表单区域样式，两列在窄屏转单列
  - [ ] SubTask 6.3: 优化费用预览卡片
  - [ ] SubTask 6.4: 优化创建成功提示卡片
  - [ ] SubTask 6.5: 统一订单列表卡片样式（路线、金额、操作按钮层级）
  - [ ] SubTask 6.6: 优化状态筛选标签横向滚动
  - [ ] SubTask 6.7: 适配 360px 宽度

- [ ] Task 7: 重构用户端 CampusOrderResult.vue 视觉
  - [ ] SubTask 7.1: 简化搜索入口和引导卡片
  - [ ] SubTask 7.2: 统一初始提示态、Loading 态、错误态样式
  - [ ] SubTask 7.3: 优化结果卡片（状态 pill、摘要网格、分组详情）
  - [ ] SubTask 7.4: 适配 360px 宽度

- [ ] Task 8: 重构用户端 CourierOnboarding.vue 视觉
  - [ ] SubTask 8.1: 简化状态卡片和流程引导
  - [ ] SubTask 8.2: 统一审核概览和 Token 申请区样式
  - [ ] SubTask 8.3: 优化资料表单分组展示
  - [ ] SubTask 8.4: 适配 360px 宽度

- [ ] Task 9: 重构用户端 Profile.vue 视觉
  - [ ] SubTask 9.1: 优化头像卡片和统计数据卡片
  - [ ] SubTask 9.2: 统一菜单入口列表样式
  - [ ] SubTask 9.3: 优化退出登录按钮
  - [ ] SubTask 9.4: 适配 360px 宽度

- [ ] Task 10: 重构用户端 AfterSaleResult.vue 视觉
  - [ ] SubTask 10.1: 统一搜索入口和结果卡片样式
  - [ ] SubTask 10.2: 优化回执状态 pill 和详情网格
  - [ ] SubTask 10.3: 适配 360px 宽度

- [ ] Task 11: 重构兼职端 Login.vue 视觉
  - [ ] SubTask 11.1: 简化品牌面板和登录表单样式
  - [ ] SubTask 11.2: 优化辅助操作按钮布局
  - [ ] SubTask 11.3: 适配 360px 宽度

- [ ] Task 12: 重构兼职端 CourierWorkbench.vue 视觉
  - [ ] SubTask 12.1: 简化 Hero 卡片和 Token 状态展示
  - [ ] SubTask 12.2: 统一状态指标网格样式
  - [ ] SubTask 12.3: 优化可接单列表卡片样式
  - [ ] SubTask 12.4: 优化无 Token 空态展示
  - [ ] SubTask 12.5: 优化订单详情 Drawer 内各区域样式
  - [ ] SubTask 12.6: 确保 Drawer 内容可正常滚动
  - [ ] SubTask 12.7: 适配 360px 宽度

- [ ] Task 13: 重构兼职端 Profile.vue 视觉
  - [ ] SubTask 13.1: 简化身份 Hero 卡片
  - [ ] SubTask 13.2: 统一状态详情网格和审核说明面板
  - [ ] SubTask 13.3: 适配 360px 宽度

- [ ] Task 14: 全局溢出检查和构建验证
  - [ ] SubTask 14.1: 在 360px / 390px / 430px 宽度下检查所有页面无横向溢出
  - [ ] SubTask 14.2: 运行 `cd frontend && npm run build` 确认构建通过
  - [ ] SubTask 14.3: 运行 `npm run build:android:user` 确认用户端构建通过
  - [ ] SubTask 14.4: 运行 `npm run build:android:parttime` 确认兼职端构建通过
  - [ ] SubTask 14.5: 运行 `git diff --check` 确认无空白错误
  - [ ] SubTask 14.6: 确认后台管理端页面未被误改

- [ ] Task 15: 更新项目日志
  - [ ] SubTask 15.1: 新增 `project-logs/campus-relay/step-158-android-dual-end-frontend-rebuild.md`
  - [ ] SubTask 15.2: 更新 `project-logs/campus-relay/summary.md`
  - [ ] SubTask 15.3: 更新 `project-logs/campus-relay/pending-items.md`
  - [ ] SubTask 15.4: 更新 `project-logs/campus-relay/file-change-list.md`
  - [ ] SubTask 15.5: 更新 `project-logs/campus-relay/agent-collaboration.md`
  - [ ] SubTask 15.6: 更新 `project-logs/campus-relay/global-working-memory.md`

# Task Dependencies
- [Task 2] depends on [Task 1]
- [Task 3] depends on [Task 1]
- [Task 4] ~ [Task 13] depend on [Task 1, Task 2, Task 3]
- [Task 14] depends on [Task 4] ~ [Task 13]
- [Task 15] depends on [Task 14]
- [Task 4] ~ [Task 10] (用户端) 可并行
- [Task 11] ~ [Task 13] (兼职端) 可并行

# Step 158: 安卓双端前端视觉与移动端交互重构

## 本轮目标

重构用户端和兼职端 Android / H5 前端视觉与移动端交互体验，让两端看起来像两个完整的移动 App，而不是网页拼凑。

重点：
1. 统一移动端视觉基线（卡片、按钮、状态标签、空态、错误态、loading 态）
2. 重构 UserLayout 和 ParttimeLayout 页面壳（顶部栏 + 底部导航 + 主内容区）
3. 重构用户端 7 个核心页面的视觉布局
4. 重构兼职端 3 个核心页面的视觉布局
5. 适配 360px / 390px / 430px 手机宽度
6. 不改后台管理端、不改 API、不改 token、不改 bridge、不改路由语义

## 实际修改文件

### 新增文件
- `frontend/src/styles/mobile-theme.css` — 移动端视觉基线样式文件

### 修改文件（用户端）
1. `frontend/src/layout/UserLayout.vue` — 页面壳重构
2. `frontend/src/views/user/Login.vue` — 登录页视觉重构
3. `frontend/src/views/user/Home.vue` — 首页视觉重构
4. `frontend/src/views/user/CampusRelayOrders.vue` — 代送订单页视觉重构
5. `frontend/src/views/user/CampusOrderResult.vue` — 订单结果页视觉重构
6. `frontend/src/views/user/CourierOnboarding.vue` — 兼职入驻页视觉重构
7. `frontend/src/views/user/Profile.vue` — 个人页视觉重构
8. `frontend/src/views/user/AfterSaleResult.vue` — 售后结果页视觉重构

### 修改文件（兼职端）
9. `frontend/src/layout/ParttimeLayout.vue` — 页面壳重构
10. `frontend/src/views/courier/Login.vue` — 登录页视觉重构
11. `frontend/src/views/courier/CourierWorkbench.vue` — 工作台视觉重构
12. `frontend/src/views/courier/Profile.vue` — 个人页视觉重构

## 明确没有改的内容

- 没有改后台管理端页面（MainLayout.vue / Dashboard.vue / Employee.vue 等）
- 没有改 request.js
- 没有改 api/* 的接口行为
- 没有改 router 路由语义
- 没有改 token 附着逻辑
- 没有改 bridge 相关逻辑
- 没有改后端 Java 代码
- 没有改数据库
- 没有改 Android 原生壳层逻辑

## 用户端每个页面改了什么

### UserLayout.vue
- 顶部栏：白底 + 轻边框 + 轻阴影，品牌标识方块 + 标题 + "我的"按钮
- 底部导航：5 项（首页/发布/结果/入驻/我的），当前项高亮主色
- 主内容区：padding-bottom 72px 给底部导航留空间
- 768px 以上最大宽度 520px 居中
- 引入 mobile-theme.css

### Login.vue
- 登录卡片：轻玻璃效果（backdrop-filter: blur(16px)），柔和双层阴影
- 输入框：min-height 44px，border-radius 10px
- 登录按钮：全宽、hover/active 渐变反馈
- 360px 适配：padding 缩小

### Home.vue
- Hero 卡片：保留浅渐变背景，装饰性 campus-building/route-dot CSS 隐藏（HTML 保留）
- 主操作按钮：全宽、min-height 52px、触控反馈
- 快捷入口：2x2 网格，active 缩放反馈
- 入驻状态三态：loading spinner、error warning、正常 success
- 480px 断点：grid 转单列
- 360px 断点：hero padding 缩小、h1 24px

### CampusRelayOrders.vue
- Hero 卡片：浅渐变背景，去掉 ::after 装饰，border-radius 16px
- 引导卡片：简化背景，border-radius 12px
- 表单卡片：去掉 backdrop-filter，纯白背景，border-radius 14px
- 费用卡片：简化背景，金额 24px
- 订单列表：卡片式，border-radius 14px
- 状态筛选：pill 形态，横向可滚动
- 两列布局：480px 以下转单列

### CampusOrderResult.vue
- 卡片统一：白底 + 轻边框 + 轻阴影，border-radius 14px
- Hero 卡片：浅渐变，去掉 ::after 装饰
- 状态卡片：简化背景
- 结果卡片：状态 pill + 摘要网格 + 详情网格
- 搜索行：480px 以下转单列
- 360px 断点：所有 grid 转单列

### CourierOnboarding.vue
- 卡片统一：白底 + 轻边框 + 轻阴影，border-radius 14px
- 状态卡片：浅渐变，去掉 ::after 装饰
- 流程引导：480px 以下转单列
- Token 申请区：简化背景
- 资料表单：480px 以下转单列
- 输入框：min-height 44px

### Profile.vue
- 头像卡片：72px 圆形 + 边框
- 统计卡片：2 列 grid，数字 22px 主色
- 菜单入口：右箭头指示器 + active 反馈
- 退出按钮：全宽红色，min-height 48px
- 360px 适配：padding 缩小

### AfterSaleResult.vue
- 卡片统一：白底 + 轻边框 + 双层轻阴影
- 搜索区：输入框 min-height 44px
- 结果卡片：receipt-pill + 详情 grid
- 480px 断点：grid 转单列
- 360px 断点：padding 缩小

## 兼职端每个页面改了什么

### ParttimeLayout.vue
- 顶部栏：白底 + 轻边框 + 轻阴影，品牌标识方块 + 标题 + 退出按钮
- 底部导航：3 项（工作台/我的/入驻），当前项高亮主色
- 主内容区：padding-bottom 80px
- 768px 以上最大宽度 520px 居中
- 引入 mobile-theme.css

### Login.vue
- 品牌面板：border-radius 14px
- 表单面板：border-radius 14px
- 登录按钮：全宽、min-height 48px
- 360px 适配：padding 缩小

### CourierWorkbench.vue
- Hero 卡片：浅渐变，去掉 ::after 装饰，h2 22px
- 状态指标：4 列转 2 列（640px/380px），简化渐变为纯色
- 卡片统一：白底 + 轻边框 + 轻阴影，border-radius 14px，去掉 backdrop-filter
- 任务卡片：border-radius 14px，价格 18px
- 详情 Drawer：输入框 min-height 44px
- 380px 断点：status-tiles 2 列

### Profile.vue
- 身份 Hero：浅渐变，border-radius 14px
- 卡片统一：白底 + 轻边框 + 轻阴影，border-radius 14px
- 详情网格：minmax(150px, 1fr)
- 审核说明：border-radius 12px
- 380px 断点：grid 转单列

## 构建验证结果

| 验证项 | 结果 |
|--------|------|
| `cd frontend && npm run build` | ✅ 通过 (1.41s) |
| `npm run build:android:user` | ✅ 通过 (1.70s) |
| `npm run build:android:parttime` | ✅ 通过 (1.49s) |
| `git diff --check` | ✅ 通过（仅 CRLF 换行符警告，非错误） |

## 仍然存在的问题

1. 横向溢出需要真机/模拟器在 360px/390px/430px 宽度下实际验证，CSS 层面已添加 overflow-x: hidden 和响应式断点
2. 部分 Hero 卡片保留了轻量 backdrop-filter（blur(16px)），在极低端 Android 设备上可能有轻微卡顿，但主流设备应无问题
3. Home.vue 的 campus-building 装饰 HTML 保留但 CSS 隐藏，后续可考虑从模板中移除
4. 中文显示需在真机 WebView 中验证，CSS 层面已确保 font-family 和编码正确
5. Drawer 内长内容滚动需在真机 WebView 中验证

## 下一轮建议

1. 在 Android 模拟器或真机上做 360px/390px/430px 宽度下的实际视觉验证
2. 如发现具体页面溢出或交互问题，只做 bug 级修复
3. 可考虑清理 Home.vue 中已隐藏的装饰 HTML 元素
4. 如需进一步优化，可评估是否将 Element Plus 组件在移动端替换为更轻量的实现

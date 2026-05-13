# 安卓双端前端视觉与移动端交互重构 Spec

## Why
用户端和兼职端在 Android WebView 中运行时，视觉上仍像网页拼凑而非原生 App，存在横向溢出、状态层级不清、卡片/按钮/空态/loading/错误态不统一、360-430px 宽度适配不足等问题。需要统一移动端视觉基线，让两端看起来像两个完整的移动 App。

## What Changes
- 重构 UserLayout.vue 和 ParttimeLayout.vue 的页面壳（顶部栏、底部导航、主内容区）
- 重构用户端 6 个核心页面的视觉布局与移动端交互（Login、Home、CampusRelayOrders、CampusOrderResult、CourierOnboarding、Profile、AfterSaleResult）
- 重构兼职端 3 个核心页面的视觉布局与移动端交互（Login、CourierWorkbench、Profile）
- 新增移动端专用样式文件 `frontend/src/styles/mobile-theme.css`，隔离在 UserLayout / ParttimeLayout 作用域内
- 统一移动端卡片、按钮、状态标签、空态、错误态、loading 态的视觉规范
- 适配 360px / 390px / 430px 手机宽度，禁止横向溢出
- 表格类内容在移动端转成卡片展示
- Drawer / 弹窗 / 长内容正常滚动

## Impact
- Affected specs: 用户端前端视觉、兼职端前端视觉
- Affected code:
  - `frontend/src/layout/UserLayout.vue`
  - `frontend/src/layout/ParttimeLayout.vue`
  - `frontend/src/views/user/Login.vue`
  - `frontend/src/views/user/Home.vue`
  - `frontend/src/views/user/CampusRelayOrders.vue`
  - `frontend/src/views/user/CampusOrderResult.vue`
  - `frontend/src/views/user/CourierOnboarding.vue`
  - `frontend/src/views/user/Profile.vue`
  - `frontend/src/views/user/AfterSaleResult.vue`
  - `frontend/src/views/courier/Login.vue`
  - `frontend/src/views/courier/CourierWorkbench.vue`
  - `frontend/src/views/courier/Profile.vue`
  - `frontend/src/styles/mobile-theme.css`（新增）
  - `frontend/src/style.css`（仅限移动端安全区补充）

## ADDED Requirements

### Requirement: 移动端视觉基线
系统 SHALL 提供统一的移动端视觉基线，包括：
- 页面壳：sticky 顶部栏 + 可滚动主内容区 + fixed 底部导航
- 顶部栏：品牌标识 + 页面标题 + 操作按钮，高度 56px
- 底部导航：用户端 5 项（首页/发布/结果/入驻/我的），兼职端 3 项（工作台/我的/入驻），高度 56px + safe-area
- 主内容区：padding 16px，底部留出 72px 给导航
- 最大宽度 520px 居中（768px 以上）

#### Scenario: 用户端页面壳渲染
- **WHEN** 用户在手机浏览器或 Android WebView 中访问用户端页面
- **THEN** 页面显示 sticky 顶部栏、可滚动内容区、fixed 底部导航
- **AND** 底部导航 5 个 tab 可点击跳转
- **AND** 当前 tab 高亮显示

#### Scenario: 兼职端页面壳渲染
- **WHEN** 用户在手机浏览器或 Android WebView 中访问兼职端页面
- **THEN** 页面显示 sticky 顶部栏、可滚动内容区、fixed 底部导航
- **AND** 底部导航 3 个 tab 可点击跳转

### Requirement: 移动端组件视觉统一
系统 SHALL 在移动端统一以下组件的视觉风格：
- 卡片：白色背景、12px 圆角、1px #e4e4e7 边框、轻阴影，无毛玻璃/渐变装饰
- 按钮：主色 #0f9f8f、44px 最小触控高度、8px 圆角
- 状态标签：pill 形态、对应语义色、12px 字号
- 空态：居中图标 + 描述文字 + 可选操作按钮
- 错误态：红色边框卡片 + 错误图标 + 错误信息 + 重试按钮
- Loading 态：居中 spinner + 描述文字
- 金额：右对齐、加粗、主色
- 订单号：等宽/加粗、可复制
- 时间：灰色辅助文字、12px

#### Scenario: 卡片在移动端渲染
- **WHEN** 页面包含卡片组件
- **THEN** 卡片为白色背景、12px 圆角、轻边框和阴影
- **AND** 无毛玻璃效果、无渐变装饰背景

#### Scenario: 状态标签在移动端渲染
- **WHEN** 页面包含订单状态标签
- **THEN** 标签为 pill 形态（圆角 999px）
- **AND** 待支付为 warning 色、待接单为 info 色、配送中为 primary 色、已完成为 success 色

### Requirement: 移动端宽度适配
系统 SHALL 适配 360px / 390px / 430px 手机宽度：
- 页面不横向溢出（overflow-x: hidden）
- 两列布局在 640px 以下转单列
- 筛选标签横向可滚动但不溢出
- 表格内容转卡片展示
- 输入框和按钮宽度 100%

#### Scenario: 360px 宽度设备访问
- **WHEN** 用户在 360px 宽度设备上访问任何用户端或兼职端页面
- **THEN** 页面无横向滚动条
- **AND** 所有卡片和按钮不溢出屏幕
- **AND** 两列表单自动转单列

### Requirement: 用户端登录页视觉重构
系统 SHALL 重构用户端登录页视觉：
- 全屏居中登录卡片
- 品牌标识 + 标题 + 副标题
- 手机号/密码输入框 + 登录按钮
- 测试账号提示区
- 无装饰性场景元素（campus-scene 隐藏）

#### Scenario: 用户端登录页渲染
- **WHEN** 用户访问 /user/login
- **THEN** 页面显示居中登录卡片，品牌标识清晰
- **AND** 输入框和按钮适配移动端触控

### Requirement: 用户端首页视觉重构
系统 SHALL 重构用户端首页视觉：
- Hero 区域：品牌标题 + 欢迎语 + 快捷指标
- 主操作按钮：发布代送需求（全宽、主色）
- 快捷入口网格：2x2 卡片（发布/结果/入驻/兼职端）
- 校园代送主入口卡片
- 订单搜索卡片
- 入驻状态卡片（loading/error/正常三态）
- 旧模块兼容入口（降级展示）

#### Scenario: 首页各状态渲染
- **WHEN** 用户访问 /user
- **THEN** 页面显示 Hero + 主操作 + 快捷入口 + 功能卡片
- **AND** 入驻状态在 loading 时显示 spinner，error 时显示警告

### Requirement: 用户端代送订单页视觉重构
系统 SHALL 重构 CampusRelayOrders 页面视觉：
- Hero 卡片：标题 + 费用规则
- 流程引导卡片：3 步骤
- 创建表单：分组展示、两列转单列
- 费用预览卡片
- 创建成功提示卡片
- 订单列表：卡片式展示（非表格）
- 状态筛选标签：横向可滚动
- 分页组件

#### Scenario: 创建订单流程
- **WHEN** 用户填写代送信息并提交
- **THEN** 表单校验失败时显示行内错误
- **AND** 提交中按钮显示 loading
- **AND** 成功后显示创建成功卡片

#### Scenario: 订单列表渲染
- **WHEN** 用户查看我的代送单
- **THEN** 订单以卡片形式展示，包含订单号、状态标签、取送路线、金额、操作按钮
- **AND** 状态筛选标签可横向滚动

### Requirement: 用户端订单结果页视觉重构
系统 SHALL 重构 CampusOrderResult 页面视觉：
- 搜索入口卡片
- 初始提示态
- Loading 态
- 错误态
- 结果卡片：订单号 + 状态 pill + 摘要网格 + 分组详情
- 异常提示区

#### Scenario: AWAITING_CONFIRMATION 状态
- **WHEN** 用户查看等待确认的订单
- **THEN** 状态 pill 显示"等待确认"，蓝色系
- **AND** 消息框提示等待确认

#### Scenario: COMPLETED 状态
- **WHEN** 用户查看已完成的订单
- **THEN** 状态 pill 显示"已完成"，绿色系

### Requirement: 用户端兼职入驻页视觉重构
系统 SHALL 重构 CourierOnboarding 页面视觉：
- 状态卡片：审核状态 pill
- 流程引导：3 步骤
- 审核与资格概览
- Token 申请区（可申请/不可申请两种状态）
- 资料表单（分组展示）

#### Scenario: Token 可申请状态
- **WHEN** 用户审核通过且启用
- **THEN** Token 申请区显示密码输入框和申请按钮
- **AND** 申请成功后显示结果和跳转按钮

### Requirement: 用户端个人页视觉重构
系统 SHALL 重构 Profile 页面视觉：
- 头像 + 姓名 + 手机号卡片
- 统计数据卡片
- 地址管理卡片
- 菜单入口列表
- 校园代送快捷入口
- 退出登录按钮

### Requirement: 用户端售后结果页视觉重构
系统 SHALL 重构 AfterSaleResult 页面视觉：
- 搜索入口
- 结果卡片：订单号 + 回执状态 + 详情网格
- 处理说明区

### Requirement: 兼职端登录页视觉重构
系统 SHALL 重构兼职端登录页视觉：
- 品牌面板：标题 + 引导说明
- 登录表单：手机号/密码 + 登录按钮
- 测试账号提示
- 辅助操作：首次报名 / 返回用户端

#### Scenario: 兼职端登录
- **WHEN** 用户访问 /parttime/login
- **THEN** 页面显示品牌面板 + 登录表单
- **AND** 登录成功后跳转工作台

### Requirement: 兼职端工作台视觉重构
系统 SHALL 重构 CourierWorkbench 页面视觉：
- Hero 卡片 + Token 状态 pill
- Token 检测提示
- 状态指标网格（4 格）
- 身份状态卡
- 可接单列表（卡片式）
- 空任务状态
- 快捷入口区
- 订单详情 Drawer（取餐/送达/异常上报/送达后状态）

#### Scenario: 无 Token 状态
- **WHEN** 用户没有兼职 token
- **THEN** 工作台显示空态提示 + 登录/入驻入口

#### Scenario: 可接任务列表
- **WHEN** 有可接任务时
- **THEN** 任务以卡片展示，包含路线、金额、接单按钮

#### Scenario: 订单详情 Drawer
- **WHEN** 配送员查看订单详情
- **THEN** Drawer 内展示订单信息 + 取餐表单 + 送达表单 + 异常上报表单
- **AND** Drawer 内容可正常滚动

### Requirement: 兼职端个人页视觉重构
系统 SHALL 重构兼职端 Profile 页面视觉：
- 身份 Hero 卡片 + 审核状态标签
- 身份状态详情网格
- 审核说明面板
- 快捷操作按钮组

## MODIFIED Requirements

### Requirement: 移动端全局样式
原 `mobile-app.scss` 的 Element Plus 覆写保留，但新增 `mobile-theme.css` 用于用户端/兼职端专属样式，通过 UserLayout / ParttimeLayout 的 class 前缀隔离，不影响后台管理端。

### Requirement: 页面壳布局
UserLayout 和 ParttimeLayout 的顶部栏和底部导航样式统一为移动 App 风格，去掉毛玻璃和渐变装饰，改为简洁白底 + 轻边框。

## REMOVED Requirements

### Requirement: 页面内毛玻璃/渐变装饰效果
**Reason**: 移动端 WebView 性能有限，毛玻璃效果（backdrop-filter: blur）和复杂渐变装饰在低端 Android 设备上导致渲染卡顿和文字模糊。统一改为简洁白底 + 轻边框 + 轻阴影。
**Migration**: 所有 `.card`、`.hero-card`、`.status-card` 等组件的 `backdrop-filter` 和装饰性 `::before`/`::after` 渐变移除，改为纯色背景。

### Requirement: 页面内装饰性伪元素
**Reason**: 装饰性圆形、渐变斑块在移动端小屏幕上占用渲染资源且视觉价值低。
**Migration**: 移除 hero-card、status-card 等组件的 `::after` 装饰性圆形元素。

# 安卓双端前端视觉重构检查清单

## 视觉基线
- [ ] mobile-theme.css 已创建并包含统一卡片、按钮、状态标签、空态、错误态、loading 态样式
- [ ] UserLayout.vue 和 ParttimeLayout.vue 已引入 mobile-theme.css
- [ ] 移动端样式不影响后台管理端（MainLayout.vue 及 admin 页面）

## 页面壳
- [ ] UserLayout.vue 顶部栏为白底 + 轻边框，无毛玻璃/渐变装饰
- [ ] UserLayout.vue 底部导航 5 项可用，当前项高亮
- [ ] ParttimeLayout.vue 顶部栏为白底 + 轻边框，无毛玻璃/渐变装饰
- [ ] ParttimeLayout.vue 底部导航 3 项可用，当前项高亮
- [ ] 两端页面壳在 360px 宽度下无横向溢出

## 用户端页面
- [ ] /user/login 登录页视觉简洁，输入框和按钮适配移动端
- [ ] /user 首页 Hero 区域清晰，快捷入口可用，入驻状态三态可读
- [ ] /user/campus/orders 代送订单页表单可填写，订单列表为卡片式，状态筛选可横向滚动
- [ ] /user/campus/order-result 结果页搜索可用，初始/Loading/错误/结果四态可读
- [ ] /user/campus/courier-onboarding 入驻页审核状态清晰，Token 申请区可用
- [ ] /user/profile 个人页头像/统计/地址/菜单可用
- [ ] /user/campus/after-sale-result 售后结果页搜索和结果展示可读

## 兼职端页面
- [ ] /parttime/login 登录页品牌面板和表单清晰
- [ ] /parttime/workbench 工作台状态指标、可接单列表、空态展示正确
- [ ] /parttime/workbench 订单详情 Drawer 可正常滚动
- [ ] /parttime/workbench 取餐/送达/异常上报表单可用
- [ ] /parttime/profile 个人页身份状态和审核说明可读

## 宽度适配
- [ ] 360px 宽度下所有页面无横向滚动条
- [ ] 390px 宽度下所有页面无横向滚动条
- [ ] 430px 宽度下所有页面无横向滚动条
- [ ] 两列布局在 640px 以下转单列

## 状态层级
- [ ] 订单号加粗可辨识
- [ ] 金额右对齐加粗主色
- [ ] 时间灰色辅助文字
- [ ] 状态标签颜色语义正确（待支付 warning、待接单 info、配送中 primary、已完成 success）
- [ ] 操作按钮层级清晰（主操作 > 次操作）

## 中文文案
- [ ] 所有页面中文无乱码
- [ ] 无英文残留（除技术术语如 token、ID）

## 构建验证
- [ ] `cd frontend && npm run build` 构建通过
- [ ] `npm run build:android:user` 构建通过
- [ ] `npm run build:android:parttime` 构建通过
- [ ] `git diff --check` 无空白错误

## 未误改确认
- [ ] MainLayout.vue 未被修改
- [ ] Dashboard.vue 未被修改
- [ ] Employee.vue 未被修改
- [ ] CampusSettlementOpsView.vue 未被修改
- [ ] CampusAfterSaleExecutionList.vue 未被修改
- [ ] CampusCourierOpsView.vue 未被修改
- [ ] CampusExceptionOpsView.vue 未被修改
- [ ] request.js 未被修改
- [ ] api/* 接口行为未被修改
- [ ] router 路由语义未被修改
- [ ] token 附着逻辑未被修改
- [ ] bridge 相关逻辑未被修改

## App 感
- [ ] 用户端看起来像一个完整的移动 App
- [ ] 兼职端看起来像一个完整的移动 App
- [ ] 两端不像网页拼凑

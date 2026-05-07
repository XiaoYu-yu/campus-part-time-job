# Step 156 — 用户端订单主链路原型对齐第一轮

## 本轮目标

执行 Step 155 推荐方案 A：用户端订单发布+模拟支付+订单结果三页联动视觉对齐。聚焦 P0 全部 3 项（订单列表 tab 筛选、订单状态时间轴、确认收货功能），同时强化订单卡片设计。

## 完成项

### 1. 订单列表 tab 状态筛选

- `CampusRelayOrders.vue`：将 el-select 下拉筛选替换为横滑 tab 栏（全部/待支付/待接单/配送中/已完成）
- tab 使用 campus teal 渐变高亮激活态，支持移动端横滑溢出
- 点击 tab 自动重置分页并重新加载

### 2. 订单卡片设计强化

- `CampusRelayOrders.vue`：订单卡片重构为路线展示风格
- 顶部：订单号 + 状态标签
- 中部：取餐点 → 送达地路线展示（圆点 + 连接线 + 标签）
- 底部：取餐码标签 + 金额醒目展示 + 操作按钮
- 移动端底部区域纵向排列

### 3. 订单状态时间轴

- `CampusOrderResult.vue`：在 message-box 后新增时间轴组件
- 展示 5 个节点：已接单 → 已取餐 → 配送中 → 已送达 → 已完成
- 已完成节点显示绿色勾选圆，当前节点显示蓝色脉冲圆，未完成显示灰色空心
- 节点间有渐变连接线，已完成段为 teal 渐变
- 时间字段来自 `acceptedAt`、`pickedUpAt`、`deliveringAt`、`deliveredAt`、`autoCompleteAt/completedAt`

### 4. 确认收货功能

- `CampusOrderResult.vue`：AWAITING_CONFIRMATION 状态时显示确认收货区块
- 确认收货卡片包含蓝色脉冲时钟图标、说明文字和确认按钮
- 点击弹出 ElMessageBox 二次确认
- 调用 `POST /api/campus/customer/orders/{id}/confirm`
- 成功后自动刷新详情，页面进入 COMPLETED 回读态
- `campus-customer.js`：新增 `confirmCampusCustomerOrder` API 封装

## 修改文件

- `frontend/src/api/campus-customer.js` — 新增 `confirmCampusCustomerOrder`
- `frontend/src/views/user/CampusRelayOrders.vue` — tab 筛选 + 卡片强化
- `frontend/src/views/user/CampusOrderResult.vue` — 时间轴 + 确认收货

## 未改动项

- 未新增后端接口
- 未改状态机
- 未改 bridge
- 未改路由
- 未改 `request.js` 或 token 附着逻辑
- 未删除旧兼容模块

## 验证

- `npm run build` 通过
- `npm run build:android:user` 通过
- `npm run build:android:parttime` 通过

## bridge 结论

没有改 bridge。bridge 主线仍处于 Phase A no-op 冻结态。

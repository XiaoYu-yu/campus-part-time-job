# Step 171 - 旧外卖前端可见模块收口

## 本轮目标

按 owner 最新指令先去掉旧外卖模块，但保持安全边界：本轮只删除已经确认不再需要的前端可见入口、路由、页面和旧 API wrapper，不删除后端旧模块、不删旧表、不改鉴权、不动 bridge。

## 实际完成

1. 管理后台侧边栏移除“旧模块兼容”分组：
   - 分类兼容
   - 商品兼容
   - 套餐兼容
   - 订单兼容
   - 店铺状态兼容
2. 管理后台路由移除旧外卖页面：
   - `/category`
   - `/dish`
   - `/setmeal`
   - `/order`
   - `/shop-status`
   - `/component-demo`
3. 用户端路由移除旧外卖 H5 页面：
   - `/user/category`
   - `/user/dish/:id`
   - `/user/cart`
   - `/user/checkout`
   - `/user/orders`
4. 删除旧前端页面和 Vite 模板残留：
   - admin 旧外卖页面
   - user 旧外卖页面
   - `HelloWorld.vue`
   - `vue.svg`
   - `vite.svg`
   - `ComponentDemo.vue`
5. 删除前端旧外卖 API wrapper：
   - `address.js`
   - `cart.js`
   - `category.js`
   - `customer-order.js`
   - `dish.js`
   - `order.js`
   - `public.js`
   - `setmeal.js`
   - `shop.js`
6. 删除未引用的旧 mock store：
   - `frontend/src/stores/mock.js`
7. `Dashboard.vue` 最近订单改为读取 campus admin 订单列表：
   - 新增 `getCampusAdminOrders`
   - 复用 `GET /api/campus/admin/orders`
   - 状态标签改为 campus 订单状态语义
8. 用户端 `Profile.vue` 移除旧地址 / 购物车 / 旧订单入口：
   - 保留用户资料展示和退出登录
   - 改为展示校园代送单数量
   - 常用入口改为 campus 代送、结果、售后、兼职报名、反馈和隐私政策
9. 用户端首页移除旧模块兼容卡片：
   - 不再展示分类、购物车、旧订单、地址入口

## 明确未做

1. 未删除后端旧外卖 controller / service / mapper / entity。
2. 未删除旧数据库表或初始化数据。
3. 未删除 `user`、`employee`、登录、上传、统计等仍被 campus 复用的基础能力。
4. 未改 bridge：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
5. 未改 `request.js`。
6. 未改 token 附着逻辑。
7. 未改后端鉴权。
8. 未改核心状态机。
9. 未提交真实密钥、服务器凭据、证书、release keystore、GitHub token、腾讯地图 key 或 `.env`。

## 删除前核查

已做前端源码引用扫描：

1. 被删除的旧页面、旧路由和旧 API wrapper 在 `frontend/src` 中无剩余导入引用。
2. `stores/mock.js` 无导入方。
3. 残留命中主要来自 `project-logs` 历史记录，不属于运行时代码。

## 验证结果

1. `npm run build`：通过。
2. `npm run build:android:user:public`：通过。
3. `npm run build:android:parttime:public`：通过。
4. `.\mvnw.cmd -DskipTests compile`：通过。
5. `git diff --check`：通过，仅 CRLF 换行符提示。

## 当前风险

1. 后端旧模块仍在仓库中，不能直接删除，因为 user / employee / auth / upload / statistics 等基础模块仍被 campus 复用。
2. 旧数据库表和 H2 seed 仍存在，删除前必须按模块做后端依赖审计。
3. `Statistics.vue` 和 `statistics.js` 仍保留，作为当前 admin 数据看板的一部分；是否继续重构为 campus 专属统计需要单独评估。
4. 本轮只完成前端运行入口收口，不等于旧后端模块已可删除。

## 当前结论

旧外卖模块已从前端可见主链路移除：管理后台不再显示旧兼容分组，用户端不再显示旧分类 / 购物车 / 旧订单 / 地址入口，相关旧前端页面和 API wrapper 已删除。后端旧模块仍保留，等待下一轮按模块审计。

## 下一轮建议

1. 先做一次本地/服务器 smoke，确认 admin、用户端、兼职端关键入口都正常。
2. 如果要继续去旧，下一轮进入“旧后端模块删除前依赖审计”，按 `category / dish / setmeal / shop / order` 分模块逐个判断。
3. 真实 release 签名包仍是公开公测前阻断项，旧模块审计后需要回到 release keystore / release APK 回归。

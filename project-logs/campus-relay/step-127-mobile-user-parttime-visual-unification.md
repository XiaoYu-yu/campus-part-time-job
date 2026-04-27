# Step 127 - 用户端 + 兼职端移动入口视觉统一

## 本轮目标

统一用户端和兼职端的移动端视觉，让它们看起来像"校内兼职 / 校园代送平台"，而不是旧外卖移动端。本轮是展示层优化，不改任何业务行为、API、路由语义、鉴权或后端代码。

## 为什么本轮优先用户端 + 兼职端，而不是清理残留文件

1. 残留文件（HelloWorld.vue / ComponentDemo.vue / vue.svg / vite.svg）属于"代码卫生"问题，不是当前最影响用户观感的问题。
2. 清理这些文件不会明显改善项目打开后的产品感。
3. 当前后台已经开始像"校园兼职运营台"，但用户端和兼职端仍需进一步统一成同一个产品体系。
4. 如果只优化后台，不优化用户端和兼职端，项目仍会显得割裂。
5. 因此本轮最高优先级从"清理残留文件"调整为"用户端 + 兼职端移动入口视觉统一"。

## 设计方向

1. 年轻、简洁、浅色。
2. 不花哨，有轻玻璃感。
3. 类似 Chrome 那种干净、克制、清楚。
4. 不要暗黑重风格、不要大屏风、不要炫技动效。
5. 重点是可读、顺手、统一。

## 用户端改了哪些页面

### 1. `frontend/src/views/user/Login.vue`
- 测试账号提示卡片改为玻璃风格标签式展示（label + value 分行）
- 视觉风格保持浅色校园玻璃感

### 2. `frontend/src/views/user/Home.vue`
- 已在 Step 117-118 完成校园兼职化，本轮仅做视觉一致性校验
- 确认无旧外卖词、无骑手端表述

### 3. `frontend/src/views/user/CampusRelayOrders.vue`
- **"外卖内容" → "代送内容"**：表单标签从旧外卖词替换为校园代送词
- placeholder "美团订单：汉堡套餐 + 奶茶" → "请描述需要代送的物品，例如：汉堡套餐 + 奶茶"
- 表单校验提示"请填写外卖内容" → "请填写代送内容"
- 页面 alert 措辞收敛："旧 orders/cart/address 语义" → "旧模块语义"
- **未动**：`foodDescription` 变量名（后端 DTO 字段）、API 调用、路由、鉴权

### 4. `frontend/src/views/user/CampusOrderResult.vue`
- 卡片背景从 plain white 升级为玻璃态（rgba + backdrop-filter + box-shadow）
- 状态图标、引导卡片、结果摘要均切换为校园 teal 色系
- 状态 pill / message box 颜色从 Element Plus 默认色切为 teal/emerald 柔和色调
- 详情项（detail-item）背景和圆角统一为 campus 风格
- **未动**：查询逻辑、API 调用、路由、字段读取

### 5. `frontend/src/views/user/CourierOnboarding.vue`
- **"兼职配送入驻" → "校园兼职入驻"**：页面主标题更新
- **"customer 前置入口" → "用户端前置入口"**：badge 文案更易懂
- 描述文案从技术导向改为用户导向
- 卡片背景从 plain white 升级为玻璃态
- 引导步骤编号升级为 teal 渐变圆形
- 状态 pill、summary-item、guide-item 颜色统一为 campus teal 系
- 表单分区标题增加浅色 teal 背景块
- **未动**：提交逻辑、表单字段名、enabledWorkInOwnBuilding 整数提交、token 申请逻辑、API 调用

## 兼职端改了哪些页面

### 6. `frontend/src/views/courier/Login.vue`
- 登录按钮从 Element Plus 默认样式升级为 teal→cyan 渐变 + 圆角 + 阴影
- 与用户端登录页按钮保持视觉统一
- **未动**：token 申请逻辑、API 调用、路由、鉴权

### 7. `frontend/src/views/courier/CourierWorkbench.vue`
- 卡片背景从 plain white 升级为玻璃态
- 流程引导步骤编号升级为 teal 渐变圆形
- 状态 pill / badge 颜色统一为 campus teal 系
- section-header、summary-item、table-note 颜色统一
- drawer 内分区标题、详情卡片、分割线均切换为 campus 色系
- **未动**：接单/取餐/deliver/异常上报/详情回读逻辑、API 调用、token 判断条件、路由

### 8. `frontend/src/views/courier/Profile.vue`
- summary-item 背景从纯色升级为 campus 玻璃风格
- notice-panel 颜色从 Element Plus 默认色切为 teal/amber 柔和色调
- **未动**：资料读取逻辑、API 调用、路由、退出逻辑

## 哪些旧外卖文案被替换

| 页面 | 旧文案 | 新文案 |
|------|--------|--------|
| CampusRelayOrders.vue | 外卖内容 | 代送内容 |
| CampusRelayOrders.vue | 请填写外卖内容 | 请填写代送内容 |
| CampusRelayOrders.vue | 美团订单：汉堡套餐 + 奶茶 | 请描述需要代送的物品，例如：汉堡套餐 + 奶茶 |
| CampusRelayOrders.vue | 不改旧 orders/cart/address 语义 | 不改旧模块语义 |
| CourierOnboarding.vue | 兼职配送入驻 | 校园兼职入驻 |
| CourierOnboarding.vue | customer 前置入口 | 用户端前置入口 |

## 哪些明确没动

1. **后端**：未改任何 Java 代码、SQL、数据库表。
2. **API**：未改任何 API 文件运行时行为、接口签名、返回体。
3. **路由语义**：未改任何路由 path、name、meta。
4. **鉴权**：未改 JWT 拦截器、token 生成和校验逻辑。
5. **request.js**：未改 token 附着逻辑。
6. **token 逻辑**：未改 customer_token / courier_token / admin_token 策略。
7. **bridge**：继续 `Phase A no-op` 冻结态，未重开。
8. **Android 原生壳**：未改 Capacitor 配置、AndroidManifest、Gradle。
9. **旧外卖模块**：未删除任何旧页面、旧 Controller、旧 Mapper、旧表。
10. **HelloWorld.vue / ComponentDemo.vue / vue.svg / vite.svg**：本轮不清理。
11. **未新增第五个 admin 页**。
12. **未接真实支付**。
13. **未删除** `/api/campus/courier/profile` 和 `/api/campus/courier/review-status`。
14. **未改** 旧 `orders / cart / address` 语义。

## 是否删除旧外卖模块

**没有。** 旧外卖模块完全保留，本轮只优化展示层文案和视觉。

## 是否改后端 / API / 鉴权 / bridge

**没有。** 本轮未改任何后端代码、API 文件、鉴权逻辑或 bridge。

## 验证结果

1. `npm run build`：**通过**（1.00s），无新增 warning 或 error。
2. `git diff --check`：**通过**，仅有 Windows 工作区 LF/CRLF 提示（非错误）。
3. `backend compile`：本轮未改后端代码，**非必须**。
4. 浏览器 smoke 建议检查页面：
   - `/user/login` — 用户端登录
   - `/user` — 用户端首页
   - `/user/campus/orders` — 校园代送下单/列表
   - `/user/campus/order-result` — 结果回看
   - `/user/campus/courier-onboarding` — 校园兼职入驻
   - `/parttime/login` — 兼职端登录
   - `/parttime/workbench` — 兼职工作台
   - `/parttime/profile` — 兼职资料

## 下一轮建议

1. 若继续推进视觉统一，可处理旧兼容模块页面（Category/Dish/Setmeal/Order/ShopStatus）的移动端适配。
2. 若转向功能开发，优先评估"用户端自助售后申请"的最小实现。
3. 若保持维护态，当前项目已达到"三端视觉统一、可演示、可交接"的稳定状态。

## 关键确认

- 是否涉及 bridge：**否**
- 是否涉及旧外卖删除：**否，本轮只做展示层优化**
- 是否改了鉴权：**否**
- 是否改了路由：**否**
- 是否改了 token 逻辑：**否**
- 是否提交了密钥：**否**
- 是否改了后端 Java 代码：**否**
- frontend build 是否通过：**是**（1.00s）
- backend compile 是否通过：**本轮未改后端代码，非必须**

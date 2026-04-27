# Step 126 — 校园运营后台前端视觉基线统一

## 日期

2026-04-27

## 本轮目标

在前端展示层和文案层优化，让后台看起来像"校内兼职 / 校园代送运营平台"，而不是苍穹外卖后台。本轮只做前端可见层的视觉和文案调整，不删除旧外卖模块，不改业务逻辑、接口、路由语义、后端代码。

## 为什么当前优先做前端视觉，而不是删旧外卖模块

1. 旧外卖模块当前仍被 campus 主业务多处依赖（user/employee 表、8 个 Controller、6 个 Mapper），不能直接删除。
2. 当前最大问题不是"代码里有旧模块"，而是用户打开后台后仍能看到明显旧外卖痕迹（标题、菜单、文案等）。
3. 前端视觉优化是最低风险、最高感知收益的操作——改的是展示文案和样式，不动任何运行时逻辑。
4. 本轮把可见入口和页面气质统一到"校园兼职运营台"，同时保持旧模块作为降级兼容入口完整可用。

## 实际修改文件列表

### 修改的文件

1. `frontend/src/layout/MainLayout.vue`
   - 菜单项："旧店铺状态" → "店铺状态兼容"（第86行）
   - breadcrumb 映射：`/shop-status`："店铺营业状态" → "店铺状态兼容"（第216行）
   - 菜单结构保持不变：校园运营 8 项 + 旧模块兼容 5 项

2. `frontend/src/views/Category.vue`
   - 页面标题："分类管理" → "分类兼容管理"
   - 新增 `el-alert` 兼容提示："该页面保留旧模块兼容能力，不作为当前校园兼职主业务入口。"
   - 注释描述更新为兼容语义

3. `frontend/src/views/Dish.vue`
   - 页面标题："菜品管理" → "商品兼容管理"
   - 新增 `el-alert` 兼容提示
   - 注释描述更新为兼容语义

4. `frontend/src/views/Setmeal.vue`
   - 页面标题："套餐管理" → "套餐兼容管理"
   - 新增 `el-alert` 兼容提示
   - 注释描述更新为兼容语义

5. `frontend/src/views/Order.vue`
   - 页面标题："订单管理" → "订单兼容管理"
   - 新增 `el-alert` 兼容提示
   - 注释描述更新为兼容语义

6. `frontend/src/views/ShopStatus.vue`
   - 页面标题："店铺营业状态管理" → "店铺状态兼容"
   - 新增 `el-alert` 兼容提示

7. `frontend/src/stores/mock.js`
   - "销售额" → "模拟流水"（dashboardData.stats）
   - "总销售额" → "模拟服务流水"（statisticsData.metrics）
   - 注释"菜品销售分布" → "服务类型分布"

### 未修改的文件

- `frontend/src/styles/variables.scss` — 已在前序轮次完成校园视觉基线，本轮无需调整
- `frontend/src/styles/global.scss` — 已在前序轮次完成，无需调整
- `frontend/src/styles/element-plus.scss` — 表格列缩放代理已隐藏、固定列阴影已移除、空态文案换行已处理，无需调整
- `frontend/src/views/Dashboard.vue` — 已在前序轮次完成 campus 语义转换
- `frontend/src/views/Statistics.vue` — 已在前序轮次完成 campus 语义转换
- `frontend/src/router/index.js` — 路由不变
- `frontend/src/api/*` — API 调用不变
- All backend Java files — 未改动
- All database files — 未改动

## 旧入口降级为兼容入口

菜单已明确分为两组：
- **校园运营**（主业务入口，8 项）：运营总览、运营人员、数据看板、校园结算批次、校园售后执行、校园配送运营、校园结算运营、校园异常处理
- **旧模块兼容**（降级兼容入口，5 项）：分类兼容、商品兼容、套餐兼容、订单兼容、店铺状态兼容

每个旧兼容页面打开后，顶部有蓝色信息提示条，明确标注"该页面保留旧模块兼容能力，不作为当前校园兼职主业务入口。"

## 旧词替换清单

| 原词 | 替换为 | 位置 |
|------|--------|------|
| 旧店铺状态 | 店铺状态兼容 | MainLayout 菜单 |
| 店铺营业状态 | 店铺状态兼容 | MainLayout breadcrumb |
| 店铺营业状态管理 | 店铺状态兼容 | ShopStatus 页面标题 |
| 分类管理 | 分类兼容管理 | Category 页面标题 |
| 菜品管理 | 商品兼容管理 | Dish 页面标题 |
| 套餐管理 | 套餐兼容管理 | Setmeal 页面标题 |
| 订单管理 | 订单兼容管理 | Order 页面标题 |
| 销售额 | 模拟流水 | mock.js dashboardData |
| 总销售额 | 模拟服务流水 | mock.js statisticsData |
| 菜品销售分布 | 服务类型分布 | mock.js 注释 |

## 明确没有改

- 后端代码：未改
- 数据库 schema / migration / H2 数据：未改
- 路由语义：未改
- 鉴权（JWT 拦截器 / token 生成 / 校验）：未改
- request.js（token 附着逻辑）：未改
- token 策略（customer_token / courier_token / admin_token）：未改
- bridge 主线：继续保持 `Phase A no-op` 冻结态
- 旧外卖模块删除：没有删除任何后端/前端/数据库代码
- 旧 `orders / cart / address` 语义：未改
- `/api/campus/courier/profile` 和 `/api/campus/courier/review-status`：未删除
- 服务器部署：未动
- 密钥：未提交

## 验证结果

### frontend build

```
npm run build → ✓ built in 1.06s
```
构建通过，无错误。

### git diff --check

无空白错误，仅有 LF/CRLF 行尾告警（Windows 环境正常现象）。

### Dashboard / Statistics 当前状态

- Dashboard：已在前序轮次完成 campus 语义转换。统计数据标签为"模拟流水 / 订单快照 / 用户规模 / 平均单值"。图表 legend 为"模拟流水 / 订单数 / 任务量"。
- Statistics：已在前序轮次完成。指标为"模拟服务流水 / 校园订单数 / 新用户数 / 客单价 / 转化率 / 复访率"。图表标题为"模拟服务趋势 / 服务类型分布 / 订单状态分布 / 用户地域分布 / 任务时段分布"。

### 本轮未改后端代码

backend compile 非必须。

## 下一轮建议

1. Phase 2：清理前端残留文件（HelloWorld.vue、vue.svg、vite.svg、ComponentDemo.vue + 对应路由），每项删除前 grep 确认无引用，删除后 build 和 lint 通过。
2. 不建议现在进入 Phase 3（后端旧模块删除），因为旧 Controller/Service/Mapper 仍被 campus 多处依赖。

## 关键确认

- 是否涉及 bridge：否，bridge 保持 Phase A no-op 冻结
- 是否涉及旧外卖删除：否，本轮只做前端视觉层优化
- 是否改了鉴权：否
- 是否改了路由：否
- 是否改了 token 逻辑：否
- 是否提交了密钥：否
- 是否改了后端 Java 代码：否
- 是否改了数据库：否
- 是否删除了任何旧模块：否
- frontend build 是否通过：是（1.06s）

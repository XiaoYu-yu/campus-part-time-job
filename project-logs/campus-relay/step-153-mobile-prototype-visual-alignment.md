# Step 153 - 用户端 / 兼职端移动原型视觉对齐第一轮

## 本轮目标

基于 `frontend/prototype/customer-h5-prototype/` 与 `frontend/prototype/parttime-app-prototype/` 中的用户端、兼职端原型图，先做一轮移动端视觉对齐：

1. 解决浏览器自动翻译导致的中文乱码 / 错译问题。
2. 将用户端首页、发布代送、结果回看、入驻页调整为更接近移动端原型的校园兼职风格。
3. 将兼职端登录页和工作台调整为更接近“兼职接单端”的移动卡片风格。
4. 保持行为、接口、路由、bridge、鉴权和 token 附着逻辑不变。

## 实际完成

### 根级中文显示修复

- `frontend/index.html`
  - `<html>` 改为 `lang="zh-CN"`。
  - 增加 `translate="no"` 与 `google notranslate`，避免浏览器翻译插件把“兼职 / 代送 / 校园”等词误翻译成错误文本。
  - 页面标题改为“校内兼职平台”。

### 全局视觉基线

- `frontend/src/style.css`
  - 新增校园移动端全局变量、字体栈、背景色、Element Plus 输入框 / 按钮基础视觉。
- `frontend/src/main.js`
  - 引入 `style.css`。
- `frontend/src/App.vue`
  - 调整全局字体与背景，避免仍带旧模板感。

### 用户端页面

- `frontend/src/layout/UserLayout.vue`
  - 调整用户端移动壳：顶部文案、底部导航、浅色玻璃背景。
- `frontend/src/views/user/Home.vue`
  - 按原型重构为移动首页：校园代送 hero、主操作按钮、快捷入口卡片、订单回看、入驻状态。
  - 修复 390px 手机视口下标题被拆成单字的问题。
- `frontend/src/views/user/Login.vue`
  - 调整为校园兼职登录入口风格。
- `frontend/src/views/user/CampusRelayOrders.vue`
  - 调整发布代送页说明区、步骤区、表单区视觉。
- `frontend/src/views/user/CampusOrderResult.vue`
  - 调整结果回看页 hero、查询区和状态展示基线。
- `frontend/src/views/user/CourierOnboarding.vue`
  - 调整入驻页说明、状态卡、表单层级和 token 申请区域。

### 兼职端页面

- `frontend/src/layout/ParttimeLayout.vue`
  - 调整兼职端移动壳：顶部描述、底部导航和移动端宽度约束。
- `frontend/src/views/courier/Login.vue`
  - 调整为兼职端独立登录入口风格。
- `frontend/src/views/courier/CourierWorkbench.vue`
  - 可接任务由表格改为更适合手机端的任务卡片。
  - 增加状态 tiles、任务卡、详情 drawer 分区样式。
  - 修复接口不可用时启用状态不应错误显示“待启用”的本地 profile 兜底。
  - drawer 在手机视口下改为 100% 宽度，避免小屏裁切。

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js`。
3. 未改 `frontend/src/api/campus-courier.js` 或其它 API 文件运行时行为。
4. 未改 token 附着逻辑。
5. 未改接口调用顺序。
6. 未改后端代码、数据库、鉴权、路由或订单状态机。
7. 未新增页面。
8. 未删除旧兼容模块。
9. 未提交 prototype 原图目录；原图仍作为本地参考资产保留。

## 视觉检查

使用 Playwright 在 `390x844` 手机视口检查：

1. `/user`
   - 中文不再被浏览器翻译插件误改。
   - 首页 hero、主 CTA、快捷入口卡片正常展示。
2. `/user/campus/orders`
   - 发布代送页顶部说明、步骤卡、表单入口正常展示。
3. `/parttime/login`
   - 兼职端登录入口正常展示。
4. `/parttime/workbench`
   - 使用本地 visual smoke token 检查工作台空数据状态和本地 profile 兜底。
   - 因当前后端未运行，接口请求出现 `502`，但页面展示兜底正常；这不是本轮前端样式问题。

## 验证结果

1. `npm run build`：通过。
2. `git diff --check`：通过，无空白错误。

## 当前 bridge 结论

bridge 仍处于 `Phase A no-op` 冻结态。本轮没有触碰 bridge、鉴权、token 附着、旧兼容接口或后端语义，不能也不应写成“bridge 已可删除”。

## 下一轮建议

1. 继续做第二轮移动端原型视觉对齐，但只处理当前已经发现的真实视觉问题，不机械大改。
2. 优先检查：
   - 用户端订单发布表单在长表单滚动下的按钮固定与错误态。
   - 用户端结果回看页在 `COMPLETED / AWAITING_CONFIRMATION / 查无订单` 三种状态下的样式。
   - 兼职工作台在真实后端数据下的任务卡、详情 drawer、取餐 / 送达 / 异常上报区。
3. 在视觉稳定后，再重新构建 Android 双端 Web 资源并跑一次模拟器启动 smoke。

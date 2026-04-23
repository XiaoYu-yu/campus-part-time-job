# Step 103 - admin 玻璃拟态视觉重基线与残留乱码兜底

## 本轮目标

用户基于本地截图反馈：后台仍有旧苍穹外卖观感，并且 dashboard / employee 高曝光区域仍能看到 `ç®¡ç...` 这类乱码。本轮目标是先处理真实影响第一印象的入口：

1. 登录页从旧餐饮后台改成校内兼职运营台。
2. admin 主框架和 dashboard 继续向用户选定的深色玻璃拟态参考靠拢。
3. 对残留的 admin 用户名、角色、员工姓名、职位、部门做展示层兜底归一化。
4. 只改展示与文本显示，不改业务语义。

## 根因判断

本轮直接调用后端接口复核：

1. `POST /api/employees/login` 原始响应中 `name / position / department` 已是正常中文。
2. `GET /api/employees?page=1&size=5` 原始响应中 `管理员 / 技术部` 已是正常中文。
3. 因此当前截图中的乱码不是数据库或后端接口源数据问题，而是前端运行态可能仍存在旧 session / localStorage / in-memory 数据没有被重新归一化的问题。

## 已完成项

### 1. 登录页视觉重做

文件：`frontend/src/views/Login.vue`

完成：

1. 品牌从 `智慧餐饮管理系统` 改为 `校内兼职运营台 / Campus Part-time Console`。
2. 登录按钮从旧 `登 录` 改为 `进入运营台`。
3. 演示账号区域改为 `本地演示账号`。
4. 右侧功能说明改为校园代送、异常、售后、结算和兼职身份承接口径。
5. 背景改为深色玻璃拟态、网格、渐变光晕和半透明面板，贴近用户选择的 Figma 参考方向。

### 2. admin 外壳与 dashboard 继续重基线

文件：

1. `frontend/src/layout/MainLayout.vue`
2. `frontend/src/views/Dashboard.vue`
3. `frontend/src/styles/variables.scss`

完成：

1. admin 外壳从浅色旧后台进一步改为深色玻璃拟态侧边栏和顶部栏。
2. 主色继续保持校园 teal / deep-blue / lime，不再使用旧外卖橙红主色。
3. dashboard 卡片、图表、表格统一到深色玻璃视觉层。
4. 清理 dashboard 注释中的旧 `热门菜品` 表述，改为 `服务热度`。

### 3. 残留乱码显示兜底

文件：

1. `frontend/src/stores/user.js`
2. `frontend/src/layout/MainLayout.vue`
3. `frontend/src/views/Dashboard.vue`
4. `frontend/src/views/Employee.vue`

完成：

1. `admin_user_info` 从 localStorage 读取时会归一化并写回，避免旧缓存继续显示 mojibake。
2. `currentUserInfo` getter 每次返回前都会重新归一化，避免旧内存态用户名继续显示乱码。
3. MainLayout 顶部用户名 / 角色显示增加显示级兜底。
4. Dashboard 欢迎语增加显示级兜底。
5. Employee 列表和编辑弹窗在赋值前再次归一化，兜底姓名、职位、部门等高曝光字段。

## 明确未改

1. 未改 bridge。
2. 未改 `frontend/src/utils/request.js` 的 token 附着逻辑。
3. 未改任何 `campus-*` API 文件运行时行为。
4. 未改接口路径、接口语义或 API 调用顺序。
5. 未改路由。
6. 未改后端代码、数据库或状态机。
7. 未新增页面。
8. 未删除旧外卖模块；旧模块仍以兼容模块方式保留。

## 验证结果

1. `POST /api/employees/login` 原始后端响应为正常中文：`管理员 / 技术部`。
2. `GET /api/employees?page=1&size=5` 原始后端响应为正常中文：`管理员 / 技术部`。
3. `npm run test -- text.spec.js` 通过。
4. `npm run build` 通过。
5. `git diff --check` 通过，仅有 Git 的 CRLF 工作区提示。

说明：本轮 Playwright MCP 浏览器上下文在调用时返回 `Target page, context or browser has been closed`，未用其截图；本轮使用接口复核、构建和文本单测完成验证。

## 本地复核建议

1. 打开 `http://127.0.0.1:5173/login`。
2. 用 admin seed 登录：
   - 手机号：`13800138000`
   - 密码：`123456`
3. 优先复核：
   - 登录页是否已经不再出现 `智慧餐饮管理系统`。
   - `/dashboard` 顶部欢迎语是否正常中文。
   - 顶部用户信息是否显示 `管理员`，不再显示 mojibake。
   - `/employee` 表格中的姓名、职位、部门是否正常中文。
4. 如果浏览器仍显示旧样式，先强制刷新或清理该站点 localStorage；本轮代码会在读取旧 `admin_user_info` 时自动写回归一化结果。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。本轮没有触发 bridge 恢复推进条件，也没有改变 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 或 token 附着策略。

## 下一步建议

1. 先由 owner 在本地复核登录页、dashboard、employee 三个高曝光入口。
2. 如果这三个入口确认方向正确，再继续小步统一其它高曝光页面，不建议一次性全站重写。
3. 如果仍觉得“不像校园兼职项目”，下一轮应该先确定视觉方向和页面信息架构，而不是继续零散改组件。

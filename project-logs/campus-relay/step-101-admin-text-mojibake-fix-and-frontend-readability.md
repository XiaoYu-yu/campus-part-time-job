# Step 101 - admin 文本乱码修复与前端可读性加固

## 本轮目标

修复 admin 页面中接口数据或本地缓存数据出现的中文 mojibake 乱码问题，优先保证截图中出现的顶部用户姓名、仪表盘欢迎语、员工列表姓名 / 职位 / 部门等文字能正常显示。

## 问题定位

- `Dashboard.vue`、`Employee.vue`、`MainLayout.vue` 中的静态中文文案本身正常。
- 截图中的异常字符集中出现在接口返回或 `localStorage` 缓存字段，例如 `ç®¡ç†å‘˜`、`æŠ€æœ¯éƒ¨`。
- 本地 `test profile + H2` 复核时，登录接口和员工列表接口曾返回 `绠＄悊鍛� / 鎶�鏈儴`，说明根因不只是 CSS 或前端展示，而是 H2 seed SQL 初始化阶段未显式声明 UTF-8 编码。
- 因此本轮采用“根因修复 + 前端兼容兜底”：
  - 根因修复：H2/test SQL 初始化显式使用 UTF-8。
  - 兼容兜底：前端对历史缓存或历史错误响应中的 mojibake 做安全规范化。

## 已完成项

1. `backend/src/main/resources/application-test.properties` 新增 `spring.sql.init.encoding=UTF-8`：
   - 保证 H2 seed 数据按 UTF-8 加载。
   - 重启 backend 后，`/api/employees/login` 与 `/api/employees` 已真实返回 `管理员 / 技术部`。
2. 新增 `frontend/src/utils/text.js`：
   - 提供 `normalizeDisplayText`，只在解码后明显更像中文且 mojibake 评分下降时替换文本。
   - 同时兼容 Latin-1 与 Windows-1252 形态的乱码字符。
   - 提供 `normalizeTextFields`，递归规范化接口响应对象 / 数组中的字符串字段。
3. 在 `frontend/src/utils/request.js` 响应拦截器中接入 `normalizeTextFields`：
   - 仅处理成功响应的 `res.data`。
   - 不改接口路径、token 附着、错误处理和业务动作。
4. 在 `frontend/src/stores/user.js` 中接入文本规范化：
   - 读取旧 `admin_user_info` 缓存时会修复已存在的乱码。
   - 登录或刷新用户信息时写回规范化后的用户信息。
5. 新增 `frontend/src/utils/text.spec.js`：
   - 覆盖 `ç®¡ç†å‘˜ -> 管理员`。
   - 覆盖 `æŠ€æœ¯éƒ¨ -> 技术部`。
   - 覆盖已正常中文与英文不被误改。

## 明确未改

- 未改 bridge。
- 未改 `request.js` 的 token 附着逻辑。
- 未改任何接口路径或 API 调用顺序。
- 未改后端、数据库、路由、订单 / 结算 / 售后 / 异常业务语义。
- 除 H2/test SQL 初始化编码配置外，未改后端业务代码、数据库表结构、路由、订单 / 结算 / 售后 / 异常业务语义。
- 未新增页面。

## 验证结果

- `npm run test -- text.spec.js` 通过。
- `npm run build` 通过。
- `.\mvnw.cmd -DskipTests compile` 通过。
- 本地 backend `test profile + H2` 重启后，接口真实返回：
  - `EmployeeName = 管理员`
  - `FirstEmployeeName = 管理员`
  - `FirstEmployeePosition = 管理员`
  - `FirstEmployeeDepartment = 技术部`

## 使用说明

- 如果页面仍显示旧乱码，刷新浏览器页面即可触发新的响应和 store 初始化逻辑。
- 如果浏览器仍保留极旧的构建缓存，可清缓存后刷新。

## 遗留问题

- H2/test 新启动已从源头修复 seed 数据编码。
- 如果 MySQL/dev 或服务器已有历史脏数据，需要单独做数据库级清洗评估；本轮不直接改已有生产/内测库数据。

## 下一步建议

1. 重新构建并部署前端后，在 admin 仪表盘和员工管理页复核文字显示。
2. 若只是在内测服务器上看到该问题，可按现有 runbook 更新服务器前端镜像。
3. 若后续发现更多历史脏数据，再单独做数据库级清洗评估，不要和本轮前端显示修复混在一起。

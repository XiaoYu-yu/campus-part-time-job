# bridge 执行准备人工核实 checklist

## 使用说明

1. 本文档是待填写 checklist，不代表当前已经完成这些核实项。
2. 本文档的目标是让执行人拿着就能做人工核实，而不是继续停留在概念描述。
3. 若某项无法从当前仓库代码直接证明，应明确填写“待人工核实”，不要主观补结论。
4. 核实失败时不要覆盖成成功，要保留失败现象、失败证据和下一步处理建议。
5. 进入 `Phase A` 执行准备前，至少需要把所有“阻塞项”明确填完。

## 核实项

### 1. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`

- 推荐执行入口：
  - 线上旧页面访问清单
  - 已部署静态资源目录
  - 网关 / Nginx 访问日志
- 核实方法：
  - 盘点仍在线的旧页面、外部前端包、部署脚本或静态资源
  - 检查访问日志、浏览器 network 或人工联调记录
- 推荐证据类型：
  - 截图
  - 访问日志片段
  - 静态资源搜索结果
- 失败时如何记录：
  - 写明仍在调用的页面路径、调用时间、证据链接和影响范围
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 2. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`

- 推荐执行入口：
  - 线上旧页面访问清单
  - 已部署静态资源目录
  - 网关 / Nginx 访问日志
- 核实方法：
  - 盘点仍在线的旧页面、外部前端包、部署脚本或静态资源
  - 检查访问日志、浏览器 network 或人工联调记录
- 推荐证据类型：
  - 截图
  - 访问日志片段
  - 静态资源搜索结果
- 失败时如何记录：
  - 写明仍在调用的页面路径、调用时间、证据链接和影响范围
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

- 推荐执行入口：
  - Postman / Apifox 集合
  - 项目 Wiki 或联调说明
  - 团队常用脚本目录
- 核实方法：
  - 盘点本地脚本、测试说明、团队常用调试集合
  - 人工确认是否仍通过 `customer_token` 调用旧 bridge 读取资料或审核状态
- 推荐证据类型：
  - 集合截图
  - 调试脚本片段
  - 调用日志或抓包记录
- 失败时如何记录：
  - 写明脚本名称、调用命令、token 类型、影响场景和替代计划
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 4. workbench 的 profile / review-status 请求是否全程只走 `courier_token`

- 推荐执行入口：
  - 浏览器开发者工具 Network
  - 前端本地联调环境
- 核实方法：
  - 打开浏览器开发者工具 Network
  - 访问 `/courier/workbench`
  - 观察 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 的 `Authorization` 头来源
- 推荐证据类型：
  - Network 截图
  - 请求头导出
  - 本地联调日志
- 失败时如何记录：
  - 写明哪个请求仍回退使用 `customer_token`，以及触发条件
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 核实结果：
  - `2026-04-09` 已在本地启动 `backend(test profile, 8080)` 与 `frontend(vite, 5173)`，并完成两轮真实验证。第一轮暴露出 `/courier/workbench` 仍会被 `UserLayout` 的 customer 购物车请求拉回 `/user/login`；第二轮已在修正 `frontend/src/layout/UserLayout.vue` 后重新用 Playwright 验证，纯 `courier_token` 路径可稳定停留在 `/courier/workbench`，且 `GET /api/campus/courier/profile` 与 `GET /api/campus/courier/review-status` 均优先使用 `courier_token`。
- 负责人：
  - Codex
- 日期：
  - `2026-04-09`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 5. 是否已有一轮 `customer onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的稳定联调记录

- 推荐执行入口：
  - [bridge-regression-template.md](bridge-regression-template.md)
  - 测试环境前端页面
  - 浏览器 Network / Console
- 核实方法：
  - 按联调模板执行完整链路
  - 确认每一步都有实际结果、截图或日志占位
- 推荐证据类型：
  - 联调模板填写结果
  - 页面截图
  - Network / Console 记录
- 失败时如何记录：
  - 写明具体失败步骤、阻塞接口、错误码、重现步骤和是否阻塞 Phase A
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
- 核实结果：
  - `2026-04-09` 已在本地 `backend test profile + H2 + 8080` 与 `frontend vite + 5173` 下形成一轮真实完整链路：`customer onboarding 提交资料 -> admin 审核通过 -> customer 申请 courier token -> /courier/workbench 加载 profile/review-status/available orders -> courier 接单 -> 取餐 -> deliver(配送中) -> deliver(已送达) -> 异常上报 -> customer 确认送达 -> courier completed 结果回读`。联调样本订单为 `CR202604070002`，customer 为 `13900139001`，courier 为 `13900139000`。
- 负责人：
  - Codex
- 日期：
  - `2026-04-09`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

## 收口前判断

- 是否可以进入 `Phase A` 执行准备：
  - [ ] 可以
  - [x] 还不可以
- 若还不可以，阻塞项：
  - repo 外旧页面、历史客户端和手工联调脚本对旧 bridge 的依赖仍待人工核实
- 下一步建议：
  - 按 checklist 关闭 repo 外依赖核实项，并基于 `bridge-regression-template.md` 补齐可共享的回归留痕

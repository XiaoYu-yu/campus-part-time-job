# Step 21 - bridge 局部真实验证 / courier workbench completed 结果回读

## 本次目标

1. 不伪造 repo 外依赖结果的前提下，先落一轮 repo 内可真实执行的 bridge 验证
2. 把 `bridge-execution-readiness-checklist.md` 与 `bridge-regression-template.md` 从“可执行模板”推进到“已有局部真实留痕”
3. 在不新增后端接口、不新增页面群的前提下，为 courier workbench 补一个 completed 订单最小结果回读入口

## 实际执行与结果

### 1. 真实启动环境

- 已真实启动 backend：
  - `.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test -Dspring-boot.run.arguments=--server.port=8080`
- 已真实启动 frontend：
  - `npm run dev -- --host 127.0.0.1`
- 运行日志位置：
  - `project-logs/runtime/step21-backend.out.log`
  - `project-logs/runtime/step21-backend.err.log`
  - `project-logs/runtime/step21-frontend.out.log`
  - `project-logs/runtime/step21-frontend.err.log`

### 2. 真实执行的接口级验证

- 已真实调用 `POST /api/users/login`
  - 用户：`13900139001`
  - 结果：成功获取 `customer_token`
- 已真实调用 `POST /api/campus/courier/auth/token`
  - 用户：`13900139001`
  - 结果：成功获取 `courier_token`
- 已真实调用 `GET /api/campus/courier/orders/available?page=1&pageSize=10`
  - 结果：成功，但当前 H2 种子返回空列表
- 已真实调用 `GET /api/campus/courier/orders/CR202604060001`
  - 结果：成功读取一笔 `COMPLETED` 订单

### 3. 真实执行的前端验证

- 已真实打开 `/courier/workbench`
- 已通过 Playwright 抓取并确认：
  - `GET /api/campus/courier/profile`
  - `GET /api/campus/courier/review-status`
  - `GET /api/campus/courier/orders/available`
  都优先携带 `courier_token`
- 已真实验证无 `courier_token` 时：
  - `/courier/workbench` 只展示空态
  - 不再触发 courier 业务接口请求
- 已在 workbench 中补充“按订单号查看详情”入口
- 已真实通过订单号 `CR202604060001` 打开详情 drawer，并看到：
  - `COMPLETED` 状态
  - `deliveredAt`
  - `autoCompleteAt`
  - completed 后最小只读承接区

### 4. 本轮没有伪造的内容

- 没有把 repo 外依赖写成“已清空”
- 没有把完整链路写成“已联调通过”
- 没有把可接单为空的环境强行写成“已完成接单 -> 取餐 -> deliver -> 异常上报”

## 已完成项

1. `bridge-execution-readiness-checklist.md` 新增真实通过项：
   - workbench 的 `profile / review-status` 请求优先走 `courier_token`
2. `bridge-regression-template.md` 补入真实执行结果：
   - token 申请接口层验证
   - workbench 加载验证
   - completed 订单结果回读验证
3. `bridge-phaseout-evaluation.md` 补入 Step 21 repo 内真实验证进展
4. `CourierWorkbench.vue` 新增按订单号查看详情入口，解决当前种子数据下“无可接单记录时无法回读 completed 订单”的问题

## 修改文件

- `frontend/src/views/courier/CourierWorkbench.vue`
- `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
- `project-logs/campus-relay/bridge-regression-template.md`
- `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-21-real-verification-and-workbench-completed-readback.md`

## 遗留问题

1. 当前 H2 种子没有可直接用于 workbench 的 available order，本轮无法在真实前端里继续完成接单 -> 取餐 -> deliver -> 异常上报完整链路
2. repo 外或历史调用对旧 bridge 的依赖仍未核实完成
3. 还没有一轮完整的人工联调回归记录

## 下一步建议

1. 继续按 checklist 做 repo 外依赖人工核实
2. 构造或准备一笔可用于 workbench 的真实 available order，再跑完整链路回归
3. 在 completed 回读基础上，再评估 customer confirm 结果回看是否值得继续补

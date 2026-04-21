# Step 79 - 试运营 RC 运行配置与构建告警减噪

## 本轮目标

1. 收掉试运营 RC 阶段两个最明显的工程摩擦点：
   - `test profile + H2` 默认随机端口
   - frontend build 中的 Sass `@import` 弃用告警
2. 保持业务语义、bridge、接口、路由、鉴权和地图能力不变。
3. 只做低风险运行配置与构建入口收敛。

## 为什么选这条主线

当前项目已经达到试运营 RC 状态，但还有两个非常具体的工程噪音：

1. `application-test.properties` 默认 `server.port=0`，每次浏览器联调都要临时覆盖端口。
2. frontend build 每次都会输出 Sass `@import` 弃用告警，噪音高但收益低，适合在 RC 阶段统一收掉。

这两项都不涉及业务语义变化，适合作为低风险继续开发项。

## 实际改动

### 1. test profile 默认端口

文件：

- `backend/src/main/resources/application-test.properties`

变更：

1. 将 `server.port=0` 调整为 `server.port=${SERVER_PORT:8080}`。
2. 默认直接监听 `8080`，便于 frontend / 浏览器联调。
3. 保留 `SERVER_PORT` 环境变量覆盖能力，避免完全写死。

效果：

1. `test profile + H2` 与现有试运营文档口径一致。
2. 不再需要每次通过临时环境变量把随机端口改到 `8080`。

### 2. Sass 样式入口

文件：

- `frontend/src/styles/global.scss`
- `frontend/src/styles/element-plus.scss`

变更：

1. 将 `@import './variables.scss';` 迁移为 `@use './variables.scss' as *;`。
2. 不改变量名、不改样式语义，只更新 Sass 入口写法。

效果：

1. `npm run build` 不再出现 Sass `@import` 弃用告警。
2. 当前视觉效果和运行时行为保持不变。

### 3. preflight 手册

文件：

- `docs/trial-operation-preflight.md`

变更：

1. 补充 test profile 默认监听 `http://localhost:8080`。
2. 补充可通过 `SERVER_PORT` 环境变量临时覆盖端口。

效果：

1. 文档与真实配置一致。
2. 后续演示和交接不需要再依赖隐式端口知识。

## 明确没做

1. 没有修改 Java、SQL、Vue 业务逻辑。
2. 没有新增页面、路由或后端接口。
3. 没有修改 bridge、鉴权、token 附着、`request.js` 或地图代码。
4. 没有处理 Vite chunk size 告警。
5. 没有接真实支付、真实退款、真实打款。

## 验证结果

1. backend：`.\mvnw.cmd -DskipTests compile` 通过。
2. frontend：`npm run build` 通过。
3. frontend build 已不再出现 Sass `@import` 弃用告警。
4. H2/test profile 启动成功，默认端口可直接监听 `8080`。
5. `git diff --check` 通过。

## 修改文件

1. `backend/src/main/resources/application-test.properties`
2. `frontend/src/styles/global.scss`
3. `frontend/src/styles/element-plus.scss`
4. `docs/trial-operation-preflight.md`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`
8. `project-logs/campus-relay/step-78-trial-operation-rc-review-and-minimal-smoke.md`
9. `project-logs/campus-relay/step-79-rc-runtime-config-and-build-warning-hardening.md`

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. 本轮只处理运行配置和构建入口，不构成 bridge 恢复推进触发条件。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 80 建议进入“前端打包告警与分包 go / no-go 评估”：

1. 只评估当前 Vite chunk size 告警是否值得处理。
2. 若收益明确，可选择最小分包或 lazy load 收敛方案。
3. 若收益不足，也允许正式记为 no-op，继续保持试运营 RC 状态。

# Step 80 - 前端打包告警与分包 go / no-go 评估

## 本轮目标

1. 评估试运营 RC 当前剩余的 Vite chunk size 告警是否值得处理。
2. 在不触碰 bridge、接口、路由和业务语义的前提下，只做最小、可回滚的前端打包噪音收敛。
3. 若需要动作，只允许落在前端依赖装配层，不进入页面职责重构或全局 UI 重构。

## 为什么选这条线

1. Step 79 已消除 Sass `@import` 弃用告警，当前剩余的显性工程噪音只剩 Vite chunk size warning。
2. 这类 warning 会持续污染 RC 构建输出，但并不必然代表当前必须做大范围前端重构。
3. 当前更合适的做法是先确认 warning 的真实来源，再决定是：
   - 做一个低风险、行为不变的最小优化；
   - 还是正式记为 no-op，不再继续追。

## 实际评估结果

### 1. 真实来源

本轮先核查了 `frontend` 构建产物和依赖分布，结论如下：

1. 路由本身已经是懒加载，不是“所有页面都打到首页包里”的问题。
2. 两个图表页：
   - `frontend/src/views/Dashboard.vue`
   - `frontend/src/views/Statistics.vue`
   都在使用整包 `import * as echarts from 'echarts'`。
3. Step 79 结束时最大的 warning chunk 为：
   - `statistics-*.js` 约 `1.11 MB`
   - `es-*.js` 约 `954 KB`
4. 进一步检查后确认：
   - `statistics-*.js` 主要承载图表依赖。
   - `es-*.js` 主要承载当前全局 `ElementPlus` vendor 基线。

### 2. go / no-go 判断

本轮最终判断为：

1. 对 `echarts` 做最小按需注册优化：`go`
   - 范围足够小
   - 行为可保持不变
   - 回滚只需单提交
2. 对全局 `ElementPlus` 做按需改造：`no-go`
   - 需要波及全局组件装配和大量页面依赖
   - 风险明显高于当前试运营 RC 收益
   - 容易把本轮拖成大范围前端结构改造
3. 对 warning 阈值做基线校准：`go`
   - 仅作用于构建提示层
   - 不改变运行时行为
   - 可明确记录为当前 RC 接受的工程基线

## 实际改动

### 1. `echarts` 最小按需注册

新增文件：

- `frontend/src/utils/echarts.js`

内容：

1. 仅注册当前 `Dashboard.vue` 与 `Statistics.vue` 真实使用的最小集合：
   - `LineChart`
   - `BarChart`
   - `PieChart`
   - `TooltipComponent`
   - `LegendComponent`
   - `GridComponent`
   - `CanvasRenderer`
2. 继续暴露 `init` 与 `graphic`，保持调用点语义稳定。

页面改动：

- `frontend/src/views/Dashboard.vue`
- `frontend/src/views/Statistics.vue`

调整方式：

1. 从整包 `echarts` 改为导入共享按需注册入口。
2. 现有 `echarts.init(...)` 与 `new echarts.graphic.LinearGradient(...)` 写法保持不变。
3. 不改图表配置，不改接口，不改页面行为。

效果：

1. 图表共享 chunk 从约 `1.11 MB` 收敛到约 `545 KB`。
2. 图表页行为保持不变。

### 2. Vite warning 阈值基线校准

修改文件：

- `frontend/vite.config.js`

改动：

1. 新增 `build.chunkSizeWarningLimit = 1100`。

原因：

1. 当前剩余大包主要来自全局 `ElementPlus` vendor 基线。
2. 试运营 RC 阶段不值得为了清 warning 去做高风险全局按需改造。
3. `1100` 既能覆盖当前接受的 `954 KB` vendor 基线，也仍能在更大回归出现时继续报警。

## 明确没做

1. 没有改 bridge。
2. 没有改 `frontend/src/utils/request.js`。
3. 没有改任何 `campus-*` API 文件运行时行为。
4. 没有改路由。
5. 没有改后端代码。
6. 没有做全局 `ElementPlus` 按需引入改造。
7. 没有做页面职责重构或 UI 重做。

## 验证结果

1. backend：`.\mvnw.cmd -DskipTests compile` 通过。
2. frontend：`npm run build` 通过。
3. Step 79 末的 `statistics` 大包约 `1.11 MB`，本轮收敛到约 `545 KB`。
4. `frontend-build.log` 中已不再出现 Vite chunk size warning。
5. 本轮未引入新的 Sass 告警或 build 错误。

运行态证据目录：

- `project-logs/campus-relay/runtime/step-80/backend-compile.log`
- `project-logs/campus-relay/runtime/step-80/frontend-build.log`

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. 本轮只处理前端依赖装配和构建告警基线，不构成 bridge 恢复推进触发条件。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 81 建议进入“前端打包线 freeze / no-op 复盘”：

1. 评估是否还有必要继续处理当前全局 `ElementPlus` vendor 基线。
2. 若没有明确性能或交付压力，则正式将前端打包优化线收成冻结 / no-op。
3. 不再默认继续做前端分包改造。

## Step 81 回填

1. 已完成前端打包线 freeze / no-op 复盘。
2. 已确认 Step 80 后的剩余大包主要来自当前全局 `ElementPlus` vendor 基线，而不是路由懒加载缺失或 `echarts` 误打包。
3. 当前 `npm run build` 已无 Sass `@import` 告警，也无 Vite chunk size warning。
4. 在没有真实性能压力、移动端体验问题、交付阻塞或全局组件装配重构的前提下，不再继续推进高风险全局按需拆分。
5. 前端打包优化线正式进入冻结 / no-op，仅在触发条件出现时重开。

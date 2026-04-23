# Step 107 - 全局按钮 plain 语义修正与员工页操作列样式清理

## 本轮目标

1. 修复 `/employee` 页面中 `删除` 按钮被一整块红色背景覆盖的问题。
2. 不只修单页，而是回到全局 `Element Plus` 按钮主题层，排查 `plain / link / text` 语义是否被误伤。
3. 保持 bridge、接口、鉴权、路由、后端业务和页面行为不变。

## 根因判断

1. `frontend/src/styles/element-plus.scss` 里对 `.el-button--danger`、`.el-button--primary` 等类型按钮的渐变背景覆盖过宽。
2. 这些规则没有排除 `is-plain`、`is-link`、`is-text` 语义，导致：
   - `type="danger" plain` 仍被刷成实心红色渐变按钮；
   - 文本色却保留 plain 语义，出现低对比度和“被红色背景盖住”的视觉错乱；
   - 后续其它页面若使用 plain/link 按钮，也存在同类风险。
3. `frontend/src/views/Employee.vue` 的操作列同时使用了 flex `gap` 和 Element Plus 默认按钮 `margin-left`，进一步放大了操作列的拥挤感。

## 已完成项

1. 收紧全局实心渐变按钮规则，只让以下场景继续使用实心样式：
   - `:not(.is-plain):not(.is-link):not(.is-text)` 的 `primary / success / warning / danger`
2. 为以下语义补齐显式视觉规则：
   - `primary.is-plain`
   - `success.is-plain`
   - `warning.is-plain`
   - `danger.is-plain`
   - `info.is-plain`
3. 为 `is-link` / `is-text` 按钮显式恢复透明背景、透明边框和无位移 hover。
4. 在 `Employee.vue` 的 `.action-cell` 中：
   - 增加 `justify-content: flex-end`
   - 增加 `flex-wrap: nowrap`
   - 清掉按钮之间默认 `margin-left`
   - 保留 `gap` 作为唯一间距来源

## 实际修改文件

1. `frontend/src/styles/element-plus.scss`
2. `frontend/src/views/Employee.vue`
3. `project-logs/campus-relay/summary.md`
4. `project-logs/campus-relay/pending-items.md`
5. `project-logs/campus-relay/file-change-list.md`
6. `project-logs/campus-relay/step-107-global-button-plain-fix-and-employee-action-cell-cleanup.md`

## 样式排查结论

1. 代码层检索发现当前高曝光页面中，明确使用 `type="danger" plain"` 的主要就是 `Employee.vue`。
2. admin 运营页中大量“查看详情”按钮是 `type="primary" link`，因此本轮同步把 `link / text` 语义从全局实心按钮规则里剥离，避免后续出现同类视觉污染。
3. 当前没有证据表明 bridge、接口返回、路由切换或后端状态机与这次问题有关，属于纯展示层主题覆盖问题。

## 明确没动的范围

1. 没改 bridge
2. 没改 `request.js`
3. 没改任何 `campus-*` API 文件运行时行为
4. 没改 token 附着逻辑
5. 没改接口语义
6. 没改路由
7. 没改后端
8. 没新增页面

## 验证结果

1. `frontend` `npm run build` 通过
2. `frontend` `npm run lint` 通过
3. `frontend` `npm run test -- text.spec.js` 通过
4. `backend` `.\\mvnw.cmd -DskipTests compile` 通过
5. `git diff --check` 通过

## 当前结论

1. `/employee` 中 `删除` 按钮的异常红色覆盖问题属于全局按钮主题误伤 `plain` 语义，现已从根修复。
2. 这轮不是业务修复，而是前端主题语义修复；后续其它页面的 `plain / link / text` 按钮也会一起受益。
3. bridge 继续保持 `Phase A no-op` 冻结态，没有变化。

## 下一步建议

1. owner 本地优先复核：
   - `/employee`
   - `/campus/after-sale-executions`
   - `/campus/exceptions`
   - `/campus/settlements`
2. 若还有高曝光样式穿帮，继续按“单页、小范围、只改展示层”处理，不重开全站改版。

# Step 104 - 浅色校园视觉回调与 courier ops 窄屏裁切修复

## 本轮目标

1. 按 owner 本地反馈，将 Step 103 的深色玻璃拟态方向回调为浅色、简洁、年轻的校园兼职运营风格。
2. 修复 `/campus/courier-ops` 在窄屏下左侧配送员表格被裁切，导致“审核状态”等列只露出一半的问题。
3. 继续保持仅展示层改动，不修改 bridge、接口、鉴权、路由、token 附着或后端业务。

## 已完成项

1. `MainLayout.vue` 已从深色玻璃外壳切回浅色玻璃外壳：
   - 背景改为浅色 teal / blue 渐变。
   - 侧边栏、顶部栏改为半透明白底。
   - 菜单文字、面包屑、用户信息改为深色文字，避免浅字在浅背景或深背景中混乱。
2. `Login.vue` 已从深色登录页切回浅色登录页：
   - 保留 `校内兼职运营台` 口径。
   - 右侧展示区改为浅色玻璃卡片，不再使用深色大背景。
3. `Dashboard.vue` 已切回浅色运营总览：
   - hero、统计卡片、图表卡片和最近订单表格改为浅色卡片。
   - ECharts 坐标轴、图例文字同步改为浅色主题下可读的深色文字。
4. `Employee.vue` 高曝光 hero 区已改为浅色玻璃卡片，避免后台局部仍保留深色主视觉。
5. `element-plus.scss` 修正 Element Plus 颜色变量：
   - `--el-color-*-light-5/7/9` 从错误的深色档改为真正浅色档。
   - 该修复用于从根上避免 tag、switch、select、hover 背景在浅色主题下出现反常深色。
6. `CampusCourierOpsView.vue` 修复窄屏表格裁切：
   - 配送员列表表格外层增加横向滚动容器。
   - 表格设置最小宽度，审核状态和启用列不再被卡片裁掉。
   - 移动端卡片 padding 和分页横向溢出处理同步收敛。

## 明确未改

1. 未改 bridge：
   - `/api/campus/courier/profile`
   - `/api/campus/courier/review-status`
2. 未改 `request.js`。
3. 未改任何 `campus-*` API 文件运行时行为。
4. 未改 token 附着逻辑。
5. 未改 API 调用顺序。
6. 未改路由。
7. 未改后端业务、数据库、鉴权或接口。
8. 未新增页面。

## 验证结果

1. `npm run build` 通过。
2. `npm run test -- text.spec.js` 通过，3 个文本乱码归一化用例全部通过。
3. `.\mvnw.cmd -DskipTests compile` 通过。
4. `git diff --check` 待提交前执行。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。本轮只做浅色视觉回调和窄屏展示修复，不代表 bridge 可删除，也不改变任何 bridge 行为。

## 下一步建议

1. owner 本地刷新 `/login`、`/dashboard`、`/employee`、`/campus/courier-ops` 复核浅色方向。
2. 若仍发现具体页面的文字错位或裁切，只按单页修复，不再重开全站大改。
3. 若视觉基线确认，再回到本地 / 内测型试运营稳定性复核。

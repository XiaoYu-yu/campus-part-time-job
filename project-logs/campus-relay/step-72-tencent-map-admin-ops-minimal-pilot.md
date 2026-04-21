# Step 72 - 腾讯地图最小产品化试点 / 园区要素运营地图预览

## 本轮目标

1. 在不改后端接口、不改 bridge、不改鉴权和路由的前提下，为现有 admin 只读运营页补一个真实地图承接点。
2. 只选择一个现有页面做最小产品化试点，避免重新铺开整条地图 / 调度线。
3. 通过真实运行态验证确认地图能力可用，再决定后续是否继续扩这一条产品化线。

## 为什么选择 CampusCourierOpsView

1. 当前 `CampusCourierOpsView.vue` 已经聚合了 courier 列表、最近异常和低频位置记录，是最适合承接地图点位预览的现有页面。
2. 该页本身就是 admin 只读运营页，补地图预览不会改变 customer / courier 主链路行为。
3. 现有后端已经能提供经纬度字段，不需要新增接口或改后端语义。
4. 如果本轮试点失败，回滚范围只在一个现有 Vue 页面和一个前端工具文件，风险最小。

## 实际实现内容

1. 新增 `frontend/src/utils/tencentMap.js`
   - 统一封装腾讯地图 JS SDK 动态加载。
   - 通过 `VITE_TENCENT_MAP_KEY` 读取本地地图 key。
   - 未把真实 key 写入仓库。
2. 修改 `frontend/src/views/CampusCourierOpsView.vue`
   - 在右侧新增“位置地图预览”卡片。
   - 复用现有位置记录列表，点击记录即可同步切换地图中心点。
   - 地图区保留坐标摘要、位置来源、订单号和上报时间。
   - 缺 key、经纬度无效或 SDK 加载失败时都有明确只读提示。
3. 没有修改：
   - 后端 controller / service / mapper。
   - `request.js`。
   - `campus-admin.js` 现有运行时行为。
   - 路由。
   - bridge。

## 为什么放弃静态图，改走 JS SDK

本轮实际探测过两种腾讯地图接入路径：

1. WebService 静态图
   - 请求 `https://apis.map.qq.com/ws/staticmap/v2/...`
   - 返回 `status=199`
   - 明确提示：当前 key 未开启 WebService API 功能
2. JS SDK
   - 请求 `https://map.qq.com/api/gljs?v=1.exp&key=...`
   - 可正常返回脚本
   - 页面可真实渲染地图比例尺和版权信息

因此，本轮正式放弃静态图方案，改用 JS SDK 方案。这是当前 key 配置下风险最低、真实可跑通的地图承接方式。

## 运行态验证

### 本地运行条件

1. backend：`test profile + H2`
2. frontend：Vite dev server
3. 地图 key：仅放在本地未跟踪环境文件中，仓库未提交真实 key

### 实际验证结果

1. 页面入口：`/campus/courier-ops`
2. 选中配送员：`id=2`
3. 选中位置记录订单：`CR202604060001`
4. 页面内已能看到：
   - 腾讯地图比例尺
   - 腾讯地图版权信息
   - 对应位置记录的订单号、上报时间、来源和坐标摘要
5. 没有影响现有：
   - courier 列表读取
   - 最近异常读取
   - 低频位置记录列表读取
   - 现有只读运营页行为

### 证据文件

1. 运行日志：
   - `project-logs/campus-relay/runtime/step-72/backend.log`
   - `project-logs/campus-relay/runtime/step-72/frontend.log`
2. 接口 / SDK 探测：
   - `project-logs/campus-relay/runtime/step-72/tencent-map-sdk-probe.json`
3. 页面验证：
   - `project-logs/campus-relay/runtime/step-72/campus-courier-ops-map-validation.json`
4. 截图：
   - `project-logs/campus-relay/runtime/step-72/campus-courier-ops-tencent-map.png`
   - `project-logs/campus-relay/runtime/step-72/campus-courier-ops-map-panel.png`

## 修改文件

1. `frontend/src/views/CampusCourierOpsView.vue`
2. `frontend/src/utils/tencentMap.js`
3. `project-logs/campus-relay/summary.md`
4. `project-logs/campus-relay/pending-items.md`
5. `project-logs/campus-relay/file-change-list.md`
6. `project-logs/campus-relay/step-71-overall-maintenance-and-delivery-readiness-review.md`
7. `project-logs/campus-relay/step-72-tencent-map-admin-ops-minimal-pilot.md`

## 明确没做

1. 没有新增页面。
2. 没有新增路由。
3. 没有新增后端接口。
4. 没有改 bridge。
5. 没有改鉴权。
6. 没有改 `request.js`。
7. 没有改 token 附着逻辑。
8. 没有做轨迹回放、实时调度、路线规划或地图写操作。
9. 没有把真实地图 key 写入仓库。

## 当前 bridge 结论

1. bridge 仍处于 `Phase A no-op` 冻结态。
2. `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 继续保留。
3. 本轮地图试点不涉及 bridge 行为变化，也不构成 bridge 恢复推进触发条件。
4. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

1. Step 73 先做地图产品化线 go / no-go：
   - 评估是否继续把腾讯地图承接扩到第二个既有页面。
   - 或确认当前只在 admin 园区要素运营页保留地图试点并收住该线。
2. 如果继续推进，优先仍保持：
   - 单页面
   - 单能力
   - 不改 bridge
   - 不改后端接口
   - 不扩成轨迹 / 调度系统。

## Step 73 回填

1. Step 73 已完成地图产品化线 go / no-go。
2. 最终结论是不扩到第二个既有页面，当前地图能力正式收住为 `CampusCourierOpsView.vue` 单页 admin 只读点位预览。
3. `frontend/src/utils/tencentMap.js` 和现有地图预览继续保留，作为当前产品化试点成果。
4. 后续只有出现真实试运营触发条件时，才重新评估地图线；当前不做轨迹、路线、调度、导航、地图写操作或第二个地图页面。

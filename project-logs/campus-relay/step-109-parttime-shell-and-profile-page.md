# Step 109 - 兼职端壳层与资料页最小补齐

## 本轮目标

1. 在 Step 108 已新增 `/parttime/login` 的基础上，继续补齐兼职端最小页面群。
2. 把日常工作入口从旧 `/courier/workbench` 逐步迁到正式 `/parttime/workbench`。
3. 新增兼职端独立壳层和资料页，让“登录 -> 工作台 -> 我的资料”形成最小闭环。
4. 保持旧 `/courier/workbench` 兼容入口，不删除旧 bridge、不改鉴权、不改后端接口。

## 已完成项

1. 新增 `frontend/src/layout/ParttimeLayout.vue`
   - 提供兼职端独立 header、底部导航和退出登录。
   - 底部导航包含：
     - `/parttime/workbench`：任务 / 工作台
     - `/parttime/profile`：我的 / 兼职资料
     - `/user/campus/courier-onboarding`：入驻资料入口
   - 退出登录只清理 `courier_token / courier_profile`，不影响用户端 customer 登录态。
2. 新增 `frontend/src/views/courier/Profile.vue`
   - 读取现有 `GET /api/campus/courier/profile`。
   - 读取现有 `GET /api/campus/courier/review-status`。
   - 展示兼职资料 ID、真实姓名、手机号、学号、校区、宿舍楼栋、启用状态和审核说明。
   - 提供进入工作台、更新入驻资料、退出兼职端三个最小入口。
3. 更新 `frontend/src/router/index.js`
   - 正式工作台路由改为 `/parttime/workbench`。
   - 兼容旧入口 `/courier/workbench` 作为 alias。
   - 新增 `/parttime/profile`，同样要求 `courier_token`。
   - 已登录访问 `/parttime/login` 时默认进入 `/parttime/workbench`。
4. 更新 `frontend/src/views/courier/CourierWorkbench.vue`
   - 从 `UserLayout` 切换为 `ParttimeLayout`。
   - 工作台中的个人资料入口改为 `/parttime/profile`。
   - 不改接单、取餐、送达、异常上报、详情 drawer 的业务条件和接口调用。
5. 更新兼职端相关跳转：
   - `frontend/src/views/courier/Login.vue` 登录成功默认进入 `/parttime/workbench`。
   - `frontend/src/utils/request.js` 中 courier 401 回退目标改为 `/parttime/login?redirect=/parttime/workbench`。
   - `frontend/src/views/user/CourierOnboarding.vue` token 申请成功后的工作台入口改为 `/parttime/workbench`。
   - `frontend/src/views/user/Profile.vue` 中“兼职工作台”入口改为 `/parttime/workbench`。

## 实际修改文件

1. `frontend/src/layout/ParttimeLayout.vue`
2. `frontend/src/views/courier/Profile.vue`
3. `frontend/src/router/index.js`
4. `frontend/src/utils/request.js`
5. `frontend/src/views/courier/Login.vue`
6. `frontend/src/views/courier/CourierWorkbench.vue`
7. `frontend/src/views/user/CourierOnboarding.vue`
8. `frontend/src/views/user/Profile.vue`
9. `project-logs/campus-relay/summary.md`
10. `project-logs/campus-relay/pending-items.md`
11. `project-logs/campus-relay/file-change-list.md`
12. `project-logs/campus-relay/global-working-memory.md`
13. `project-logs/campus-relay/step-108-parttime-login-and-independent-entry.md`
14. `project-logs/campus-relay/step-109-parttime-shell-and-profile-page.md`

## 新增前端入口

1. `/parttime/workbench`
   - 兼职端正式工作台入口。
   - 继续兼容旧 `/courier/workbench`。
2. `/parttime/profile`
   - 兼职端资料 / 审核状态页。
   - 需要 `courier_token`。

## 兼容边界

1. 不删除 `/courier/workbench`，仅将其降为兼容 alias。
2. 不删除 `/courier/login`，继续作为 `/parttime/login` 的兼容 alias。
3. 不改 `POST /api/campus/courier/auth/token`。
4. 不改 `GET /api/campus/courier/profile`。
5. 不改 `GET /api/campus/courier/review-status`。
6. 不改 bridge 鉴权、不改 token 附着策略，只调整兼职端前端默认跳转目标。
7. 不改接单、取餐、送达、异常上报、订单详情等工作台动作语义。

## 验证结果

1. `frontend` `npm run build`：通过
2. `frontend` `npm run lint`：通过
3. `frontend` `npm run test -- text.spec.js`：通过
4. `backend` `.\\mvnw.cmd -DskipTests compile`：通过
5. `git diff --check`：通过（仅保留 LF/CRLF 提示，无空白错误）

## 当前结论

1. 兼职端已经从“独立登录 + 单个工作台页面”推进到“独立登录 + 独立壳层 + 工作台 + 我的资料”的最小页面群。
2. 用户端仍负责报名 / 入驻资料，兼职端负责日常登录、工作台和资料状态查看。
3. 当前仍不是 Android 原生端；它是后续用户端 Android 壳、兼职端 Android 壳的 Web/H5 业务基线。
4. bridge 继续保持 `Phase A no-op` 冻结态，没有变化。

## 遗留问题

1. 兼职端还没有独立 Android 工程或 Capacitor / WebView 壳。
2. 兼职端还没有独立订单历史页；当前只通过工作台回读详情。
3. 用户端与兼职端的移动端导航仍是 H5 形态，后续若要做 Android，需要先定技术路线。

## 下一步建议

1. owner 本地验证：
   - `/parttime/login`
   - `/parttime/workbench`
   - `/parttime/profile`
   - `/courier/workbench` 兼容 alias
2. 若兼职端 H5 最小页面群稳定，下一轮建议做“用户端 / 兼职端 Android 壳路线评估”，先比较原生、WebView、Capacitor 和 PWA，不直接开写 Android。
3. 不建议下一轮继续扩后台样式或补第五个 admin 页。

# Step 108 - 兼职端独立登录与前台入口拆分

## 本轮目标

1. 不再让兼职端日常使用必须先经过用户端页面。
2. 在不改后端鉴权和 token 接口的前提下，补一个独立的兼职端登录入口。
3. 将“首次入驻”和“日常工作”拆成两条清晰路径：
   - 首次入驻：用户端 onboarding
   - 日常工作：兼职端登录 -> 工作台

## 方案判断

1. 现有后端已具备 `POST /api/campus/courier/auth/token`，本质上就是兼职端可复用的换 token 能力。
2. 当前短板不在后端，而在前端入口设计：
   - 用户端登录后去 onboarding 申请 token 没问题；
   - 兼职人员日常工作不应每次都从用户端绕行。
3. 因此本轮选择纯前端最小改造：
   - 新增独立兼职端登录页；
   - 新增 `courier_token` 前端 store；
   - 将 `/courier/workbench` 改为需要兼职 token 的受保护路由；
   - token 失效时统一回到兼职端登录页，而不是回 onboarding。

## 已完成项

1. 新增 `frontend/src/stores/courier.js`
   - 继续复用现有 `courier_token / courier_profile`
   - 不引入第二套 token 体系
2. 新增 `frontend/src/views/courier/Login.vue`
   - 新路由入口：`/parttime/login`
   - 兼容别名：`/courier/login`
   - 登录成功后默认进入 `/courier/workbench`
3. 更新 `frontend/src/router/index.js`
   - 新增 `ParttimeLogin`
   - 为 `/courier/workbench` 增加 `requiresCourierAuth`
   - 无兼职 token 时自动跳转 `/parttime/login`
4. 更新 `frontend/src/utils/request.js`
   - courier 业务接口或 bridge 读取接口返回 `401` 时，清理本地 `courier_token / courier_profile`
   - 统一跳回 `/parttime/login?redirect=/courier/workbench`
5. 更新 `frontend/src/views/user/CourierOnboarding.vue`
   - token 申请成功后通过 `courier store` 写入本地状态
   - 文案收敛为“兼职配送入驻 / 兼职端 token / 兼职工作台”
6. 更新 `frontend/src/views/user/Profile.vue`
   - 用户端个人中心新增“兼职端登录”入口
   - 原“配送员入驻 / 配送员工作台”文案改为“兼职配送入驻 / 兼职工作台”
7. 更新 `frontend/src/views/courier/CourierWorkbench.vue`
   - 高曝光文案改成“兼职工作台 / 兼职 token”
   - 无 token 空态增加“前往兼职端登录”按钮

## 实际修改文件

1. `frontend/src/stores/courier.js`
2. `frontend/src/views/courier/Login.vue`
3. `frontend/src/router/index.js`
4. `frontend/src/utils/request.js`
5. `frontend/src/views/courier/CourierWorkbench.vue`
6. `frontend/src/views/user/CourierOnboarding.vue`
7. `frontend/src/views/user/Profile.vue`
8. `project-logs/campus-relay/global-working-memory.md`
9. `project-logs/campus-relay/summary.md`
10. `project-logs/campus-relay/pending-items.md`
11. `project-logs/campus-relay/file-change-list.md`
12. `project-logs/campus-relay/step-108-parttime-login-and-independent-entry.md`

## 兼容与边界

1. 没有改后端 `POST /api/campus/courier/auth/token`
2. 没有改 bridge 鉴权逻辑，只调整了前端 `401` 回退目标
3. 没有改 `courier_token` 的存储键名
4. 没有改 onboarding 的接口语义，首次入驻链路仍保留在用户端
5. 没有新增 Android 工程，也没有新增兼职端第二个页面群

## 登录与使用路径

### 用户端

1. 入口：`/user/login`
2. 用户登录后仍可进入：
   - `/user/campus/courier-onboarding`
   - 提交兼职配送资料
   - 申请兼职端 token

### 兼职端

1. 入口：`/parttime/login`
2. 兼容入口：`/courier/login`
3. 登录成功后进入：`/courier/workbench`
4. 推荐 H2 测试账号：`13900139001 / 123456`

## 验证结果

1. `frontend` `npm run build` 通过
2. `frontend` `npm run lint` 通过
3. `frontend` `npm run test -- text.spec.js` 通过
4. `backend` `.\\mvnw.cmd -DskipTests compile` 通过
5. `git diff --check` 通过

## 当前结论

1. 当前项目已经从“用户端借道兼职工作台”前进到“用户端负责报名，兼职端负责日常登录”的更合理结构。
2. 这轮没有补完整兼职端页面群，但已经把后续双 Android 端所需的登录入口边界拆出来了。
3. bridge 继续保持 `Phase A no-op` 冻结态，没有变化。

## 下一步建议

1. owner 本地优先验证：
   - `/parttime/login`
   - `/user/campus/courier-onboarding`
   - `/courier/workbench`
2. 若验证通过，下一轮不要回去继续抠 admin 样式，应该转到：
   - 兼职端最小页面群补齐
   - 用户端 / 兼职端双 Android 壳路线评估
3. 如果只准备短期本地演示，这轮已经足够解决“怎么登录兼职端”的核心缺口。

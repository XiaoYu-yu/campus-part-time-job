# Step 05 补齐权限链路

## 做了什么

- 前端增加管理端与用户端分离的登录守卫
- 新增用户端身份 store
- 请求拦截器按 URL 自动选择管理端或用户端 token
- 后端 JWT 增加 `userType`，并按接口前缀区分员工、用户、公开接口

## 为什么这么改

- 原先系统基本只有员工登录链路，用户端页面虽然存在，但鉴权策略不完整，无法形成真实业务闭环

## 验证结果

- 管理端未登录访问受保护页会跳转 `/login`
- 用户端未登录访问业务页会跳转 `/user/login`
- 公开浏览接口无需 token

## 修改文件

- [frontend/src/router/index.js](D:/20278/code/show_shop1/frontend/src/router/index.js)
- [frontend/src/stores/user.js](D:/20278/code/show_shop1/frontend/src/stores/user.js)
- [frontend/src/stores/customer.js](D:/20278/code/show_shop1/frontend/src/stores/customer.js)
- [frontend/src/utils/request.js](D:/20278/code/show_shop1/frontend/src/utils/request.js)
- [frontend/src/views/user/Login.vue](D:/20278/code/show_shop1/frontend/src/views/user/Login.vue)
- [backend/src/main/java/com/cangqiong/takeaway/config/WebMvcConfig.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/config/WebMvcConfig.java)
- [backend/src/main/java/com/cangqiong/takeaway/interceptor/BaseContext.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/interceptor/BaseContext.java)
- [backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/interceptor/JwtInterceptor.java)
- [backend/src/main/java/com/cangqiong/takeaway/utils/JwtUtil.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/utils/JwtUtil.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/EmployeeController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/EmployeeController.java)

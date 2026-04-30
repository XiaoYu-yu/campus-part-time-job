# Step 140 - backend health endpoint 最小实现

## 本轮目标

基于 Step 139 已固化单机内测安全边界，本轮评估并实现最小 backend health endpoint：

1. 替代使用业务登录接口做基础存活探测。
2. 保持无业务副作用。
3. 不读取用户、订单、资金、地图或数据库数据。
4. 不改 bridge、不改 `request.js`、不改 token 附着逻辑。
5. 不改鉴权主链路、不新增前端页面。

## Go / No-go 结论

结论：执行最小实现。

原因：

1. Step 138 / Step 139 后，公网 API 已统一走 nginx `/api`，需要一个低成本存活探测点。
2. 继续用登录接口做存活探测会制造业务日志噪音，也可能误触发参数校验或鉴权路径。
3. 将 health 放在现有公开前缀 `/api/campus/public/**` 下，可以复用已有 public 放行规则，不需要修改 `JwtInterceptor`。
4. health 只返回应用存活，不读数据库，不改变任何业务状态。

## 实际实现

新增接口：

```http
GET /api/campus/public/health
```

实现位置：

```text
backend/src/main/java/com/cangqiong/takeaway/campus/controller/CampusPublicController.java
```

返回结构：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "UP",
    "service": "campus-part-time-job",
    "checkedAt": "<server-time>"
  }
}
```

边界：

1. 不读取数据库。
2. 不读取用户、订单、资金、地图数据。
3. 不校验 token。
4. 不改任何订单、结算、售后、异常状态。
5. 不代表完整业务健康，只代表应用与 HTTP 入口可用。

## remote smoke 更新

已更新：

```text
scripts/trial-operation/remote-smoke.ps1
```

新增第一项：

1. `public health`

这样远端 smoke 会先验证 `/api/campus/public/health`，再进入 admin / customer / parttime 登录与业务接口检查。

## 文档更新

已同步：

1. `docs/deployment/internal-trial-ops-runbook.md`
2. `docs/deployment/internal-trial-security-boundary.md`
3. `docs/deployment/post-deploy-smoke-checklist.md`
4. `docs/deployment/remote-internal-trial-smoke.md`
5. `scripts/trial-operation/commands.ps1`

## 明确没做

1. 没有改 bridge。
2. 没有改 `request.js`。
3. 没有改 token 附着逻辑。
4. 没有改 `JwtInterceptor`。
5. 没有改后端业务接口、数据库 schema、状态机或鉴权。
6. 没有改任何 Vue 页面。
7. 没有新增页面或路由。
8. 没有接真实支付、真实退款或真实打款。
9. 没有提交真实公网 IP、服务器密码、GitHub token、SSH 私钥或腾讯地图 key。

## 本地验证

已完成：

1. `.\mvnw.cmd -DskipTests compile` 通过。
2. `npm run build` 通过。
3. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\commands.ps1` 通过。

待服务器部署后完成：

1. 服务器拉取最新提交并重建 compose。
2. 通过 nginx `/api/campus/public/health` 验证 health。
3. 用新版 `remote-smoke.ps1` 复跑远端 smoke。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态：

1. 不删除 `/api/campus/courier/profile`。
2. 不删除 `/api/campus/courier/review-status`。
3. 不替换 `/api/campus/courier/auth/token`。
4. 不改 `request.js` 和 token 附着逻辑。

## Step 141 建议

待 Step 140 服务器验证完成后再确定。

如果 health 与新版远端 smoke 通过，Step 141 建议进入：

1. 服务器最小日志留存 / 轮转策略说明。
2. 或 SSH `22` 安全组来源限制操作清单。
3. 或内测部署回滚入口再验证。

如果 health 或远端 smoke 失败，Step 141 必须先修部署入口，不进入新功能开发。

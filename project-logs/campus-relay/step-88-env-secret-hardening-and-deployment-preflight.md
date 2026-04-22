# Step 88 - 试运营环境与密钥配置硬化 / deployment preflight 准备

## 本轮目标

1. 基于 Step 87 的产品级试运营差距清单，先补环境变量样例和密钥边界。
2. 明确腾讯地图 key、数据库密码、JWT secret 等敏感配置如何本地 / 部署注入。
3. 不提交真实 key，不改业务代码，不改 bridge，不改接口，不改路由，不改鉴权。

## 实际完成

### 1. 新增安全占位 env 样例

新增：

1. 根目录 `.env.example`
   - 作为入口提示。
   - 指向 backend / frontend 的具体样例。
2. `backend/.env.example`
   - 覆盖 `SPRING_PROFILES_ACTIVE`、`SERVER_PORT`、DB、JWT、CORS 和上传目录。
   - 所有敏感项均为占位值。
3. `frontend/.env.example`
   - 覆盖 `VITE_API_BASE_URL`、`VITE_USE_MOCK` 和 `VITE_TENCENT_MAP_KEY`。
   - 地图 key 使用占位值，不包含真实 key。

### 2. 更新 gitignore 规则

`.gitignore` 继续忽略真实 env：

1. `.env`
2. `.env.*`

同时显式允许提交：

1. `.env.example`
2. `backend/.env.example`
3. `frontend/.env.example`

这样可以保留样例文件，同时避免误提交 `frontend/.env.local`、真实数据库密码或真实腾讯地图 key。

### 3. 新增环境变量与密钥配置清单

新增：

- `docs/deployment/env-and-secret-checklist.md`

内容覆盖：

1. 后端必填配置。
2. 前端必填配置。
3. 腾讯地图 key 处理规则。
4. 部署前配置检查 checklist。
5. 明确不做真实支付、真实退款、真实打款、短信、消息推送或第三方风控。

### 4. 更新部署和 preflight 文档

更新：

1. `docs/deployment/production-deploy.md`
   - 增加 env example 入口。
   - 增加腾讯地图 key 构建注入说明。
   - 增加真实 env 不得提交的上线前检查项。
2. `docs/trial-operation-preflight.md`
   - 增加 `frontend/.env.example` 说明。
   - 增加环境变量与密钥配置清单链接。
3. `docs/README.md`
   - 增加部署与运维文档索引入口。

## 当前没有做的事情

1. 没有提交真实腾讯地图 key。
2. 没有提交真实数据库密码。
3. 没有提交生产 JWT secret。
4. 没有改运行时配置默认值。
5. 没有改 Java / Vue 业务代码。
6. 没有改 bridge、鉴权、接口、路由或 token 附着逻辑。
7. 没有做真实部署。
8. 没有接真实支付、退款或打款。

## 验证结果

已执行：

1. `git ls-files -- frontend/.env.local`
   - 结果：无输出。
   - 说明：真实本地地图 key 文件未被 git 跟踪。
2. `git diff --check`
   - 结果：通过。
   - 说明：仅保留 Windows 工作区 LF -> CRLF 提示，无空白错误。

本轮未执行 backend compile 或 frontend build，因为未改运行时代码、依赖、构建配置或接口。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 本轮只做环境与密钥配置硬化，不触碰 bridge 行为。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 89 建议继续沿产品级试运营准备线推进：

1. 若继续偏部署准备，补一份部署后 smoke checklist 或 `deployment-preflight` 文档。
2. 若转向 CI 准备，设计最小 CI 检查边界：backend compile、frontend build、sample validation。
3. 不要重开 bridge、页面 polish、地图扩展或真实支付接入。

## Step 89 回填

Step 89 已完成该建议中的部署准备方向：

1. 已新增 `docs/deployment/post-deploy-smoke-checklist.md`。
2. 已覆盖部署产物、基础访问、customer / courier / admin smoke、模拟资金口径、bridge 冻结口径和回滚触发。
3. 下一轮建议评估最小 CI 检查边界，而不是继续补部署文档。

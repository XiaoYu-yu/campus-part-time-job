# Step 89 - 部署后 smoke checklist / deployment preflight 文档

## 本轮目标

1. 基于 Step 88 已完成的环境变量与密钥配置硬化，补一份部署后 smoke checklist。
2. 明确 prod-like 试运营部署后应该检查什么、失败时如何判断是否回滚。
3. 不改业务代码、不新增脚本、不改 bridge、不改接口、不改路由、不改鉴权。

## 为什么本轮做部署后 smoke checklist

Step 87 已确认当前本地答辩 / 交付 RC 足够，但产品级试运营仍缺部署运行面的检查口径。

Step 88 已补齐：

1. `.env.example`
2. `backend/.env.example`
3. `frontend/.env.example`
4. `docs/deployment/env-and-secret-checklist.md`

因此 Step 89 最合理的下一步是把“配置准备好以后，部署后怎么确认没坏”写成可执行 checklist，而不是继续改业务功能。

## 实际完成

新增：

- `docs/deployment/post-deploy-smoke-checklist.md`

内容覆盖：

1. 执行前提。
2. 后端 / 前端部署产物检查。
3. 基础访问检查。
4. customer smoke。
5. courier smoke。
6. admin smoke。
7. 模拟资金链路口径检查。
8. bridge 冻结口径检查。
9. 失败处理与回滚触发。
10. smoke 记录模板。

同步更新：

1. `docs/README.md`
   - 增加部署后 smoke checklist 索引。
2. `docs/deployment/production-deploy.md`
   - 增加部署后 smoke checklist 链接。
3. `docs/deployment/env-and-secret-checklist.md`
   - 增加下一步 smoke 入口。
4. `project-logs/campus-relay/summary.md`
5. `project-logs/campus-relay/pending-items.md`
6. `project-logs/campus-relay/file-change-list.md`
7. `project-logs/campus-relay/step-88-env-secret-hardening-and-deployment-preflight.md`

## 本轮没有做

1. 没有新增脚本。
2. 没有改 Java / Vue 业务代码。
3. 没有改 bridge。
4. 没有改接口、路由、鉴权或 token 附着逻辑。
5. 没有接真实支付、真实退款或真实打款。
6. 没有做真实部署。
7. 没有新增页面。

## 验证结果

已执行：

1. `git diff --check`
   - 结果：通过。
   - 说明：仅保留 Windows 工作区 LF -> CRLF 提示，无空白错误。

本轮未执行 backend compile 或 frontend build，因为只新增和更新文档，没有改运行时代码、依赖、构建配置或接口签名。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. 部署后 smoke checklist 明确要求不得在部署前临时删除或收紧 bridge。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 90 建议进入“最小 CI 检查边界设计 / go-no-go”：

1. 只评估 CI 是否值得做。
2. 最小 CI 候选只覆盖 backend compile、frontend build、sample validation。
3. 不接真实部署流水线。
4. 不改业务代码。
5. 不重开 bridge、页面 polish、地图扩展或真实支付接入。

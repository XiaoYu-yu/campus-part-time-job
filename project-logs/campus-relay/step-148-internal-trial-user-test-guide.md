# Step 148 - 内测用户试用说明与账号发放边界

## 本轮目标

1. 基于 Step 147 的“本地 / 内测型试运营可用”结论，补齐真实内测前的用户说明。
2. 明确谁能测、怎么发账号、能测什么、不能测什么。
3. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 为什么做这个

Step 147 已确认：

1. 本地 `main` 与 GitHub `origin/main` 一致。
2. 服务器运行态 remote smoke 25 项通过。
3. 服务器 key SSH 与 LogConfig 只读检查通过。
4. 当前状态适合 owner-controlled 内测，但不是完整产品级正式上线。

下一步如果不给测试者明确边界，就容易出现两类问题：

1. 测试者去测真实支付、真实退款、真实打款等当前明确未交付能力。
2. 账号、密码、截图、反馈散落，后续无法定位问题。

因此本轮先补内测用户说明，而不是继续扩业务功能。

## 实际完成项

新增：

- `docs/deployment/internal-trial-user-test-guide.md`

文档覆盖：

1. 参与范围。
2. admin / customer / parttime 三类账号发放边界。
3. customer 用户端可测 / 不要测范围。
4. parttime 兼职端可测 / 不要测范围。
5. admin 管理端可测 / 不要测范围。
6. 反馈记录格式。
7. 测试前检查。
8. 出问题时处理顺序。
9. 当前不是正式上线的声明。

## 账号与密钥处理

本轮没有在文档中写入：

1. 真实账号密码。
2. 服务器 IP。
3. 服务器密码。
4. 私钥内容。
5. GitHub token。
6. 腾讯地图 key。

文档只规定：

1. 账号由 owner 私下发放。
2. 每个测试者只拿自己需要的角色账号。
3. 账号密码不进入公开仓库。

## 明确没有改动

本轮没有修改：

1. Java 业务代码。
2. Vue 页面。
3. `request.js`。
4. token 附着逻辑。
5. bridge 相关接口。
6. 后端鉴权。
7. 路由。
8. 数据库表结构。
9. 旧兼容模块。
10. 服务器配置。

## 验证结果

1. `git diff --check` 通过。
2. 敏感信息扫描无真实 secret / host 命中。

本轮只新增内测说明文档，不改业务代码，因此没有重新执行 backend compile 或 frontend build。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 149 建议进入“内测反馈记录模板 / issue triage 规则”：

1. 给用户反馈建立最小模板。
2. 明确阻塞 / 主要 / 次要 / 建议四类优先级。
3. 明确哪些反馈进入修复，哪些进入后续规划。
4. 不默认改业务代码，先把反馈入口和分级规则固定下来。

Step 149 已完成补充：

1. 新增 `docs/deployment/internal-trial-feedback-triage.md`。
2. 已固化反馈记录模板、优先级定义、进入修复条件和敏感信息禁止项。

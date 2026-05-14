# Step 170 - App 内隐私协议与反馈入口收口

## 本轮目标

基于 Step 169 已完成 `xiaoyu.xin` HTTPS / Nginx / Certbot 实操，本轮补齐公开公测前仍缺的 App 内合规与反馈最小闭环：

1. 用户端与兼职端登录前必须能看到并同意用户协议 / 隐私政策。
2. App 内提供真实可提交的问题反馈入口。
3. 反馈能写入后端数据库，形成可追踪记录。
4. 不改 bridge、鉴权、token 附着逻辑、旧外卖兼容模块或核心业务状态机。

## 已完成项

### 1. 隐私政策 / 用户协议入口

- 新增公共路由：
  - `/legal/privacy`
  - `/legal/terms`
- 新增静态协议页：
  - `frontend/src/views/common/LegalDocument.vue`
- 用户端登录页和兼职端登录页均新增协议确认勾选：
  - 未勾选时不允许登录。
  - 登录请求、token 保存和跳转逻辑未改。

### 2. App 内反馈入口

- 新增公共反馈页：
  - `/feedback`
  - `frontend/src/views/common/Feedback.vue`
- 用户端个人中心新增“问题反馈”和“隐私政策”入口。
- 兼职端资料页新增“问题反馈”入口。
- 反馈页支持：
  - 反馈角色：`USER` / `PARTTIME`
  - 问题类型：订单问题、账号/登录、功能异常、改进建议、其他
  - 反馈内容
  - 联系方式
  - 关联订单号
  - 来源页面

### 3. 后端反馈提交闭环

- 新增表：`campus_feedback`
- 新增 DTO / Entity / Mapper / Service / Controller。
- 新增接口：
  - `POST /api/campus/public/feedback`
- 该接口走 `/api/campus/public/**` 公共前缀，复用现有 `JwtInterceptor` 公共路径放行能力，不需要登录 token。
- 入库字段包括：
  - `submitter_role`
  - `category`
  - `content`
  - `contact`
  - `page_path`
  - `order_id`
  - `status`
  - `created_at`
  - `updated_at`

## 数据库路径

- MySQL init 已追加 `campus_feedback`。
- migration 已新增 `V13__campus_feedback.sql`。
- H2 schema 已追加 `campus_feedback`。
- 本轮未新增 H2 seed，原因是反馈记录属于用户运行时提交数据，固定样本价值不高。

## 明确未改动

- 未改 bridge。
- 未改 `/api/campus/courier/profile`。
- 未改 `/api/campus/courier/review-status`。
- 未改 `request.js` token 附着逻辑。
- 未改后端鉴权策略。
- 未改路由主链路。
- 未删除旧外卖兼容模块。
- 未提交任何真实密钥、服务器凭据、证书私钥、release keystore、GitHub token、腾讯地图 key 或 `.env` 内容。

## 验证结果

- `.\mvnw.cmd -DskipTests compile`：通过。
- `npm run build`：通过。
- `npm run build:android:user:public`：通过。
- `npm run build:android:parttime:public`：通过。
- `git diff --check`：通过，仅 CRLF 提示。
- H2/test profile 运行态验证：
  - 启动后端到本地测试端口。
  - 调用 `POST /api/campus/public/feedback`。
  - 返回：`{"code":200,"msg":"success","data":1}`。

## 当前结论

隐私政策 / 用户协议入口和 App 内反馈提交入口已经完成最小可用闭环。当前项目距离公开公测又少两个阻断项，但仍不能把 Debug QA 包当正式公测包。

## 当前仍未解决

1. 真实 release keystore 仍未由 owner 生成。
2. 正式 release 签名 APK 仍未生成和回归。
3. 反馈目前可提交入库，但还没有 admin 反馈处理 / 只读列表页面。
4. 隐私政策 / 用户协议为最小静态版本，正式公开前仍建议 owner 根据实际运营主体补齐联系方式和删除/注销流程。

## 下一轮建议

Step 171 推荐优先做真实 release 签名包准备：

1. owner 本地生成双端 release keystore。
2. 填写本地 `key.properties`，确认不进入 Git。
3. 构建用户端 / 兼职端 release APK。
4. 用 `https://xiaoyu.xin/api` 做 release 包安装和主链路 smoke。
5. 如还有余力，再补 admin 反馈只读列表，方便公测问题回收。

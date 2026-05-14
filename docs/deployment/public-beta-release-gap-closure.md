# 公开公测前发布缺口收口清单

本清单用于判断当前项目从 owner-controlled 小范围内测进入公开公测前，还需要关闭哪些发布、安全和合规缺口。

当前结论：项目已经具备小范围内测基础，但仍不建议直接公开公测。

## 当前已收口

### 1. Android release 签名配置入口

已完成：

- 用户端和兼职端 Gradle 均支持从本地 `android/key.properties` 读取 release 签名配置。
- 已提供示例文件：
  - `mobile/user-app/android/key.properties.example`
  - `mobile/parttime-app/android/key.properties.example`
- `.gitignore` 已忽略：
  - `mobile/**/android/key.properties`
  - `mobile/**/android/keystore/`
  - `mobile/**/android/*.jks`
  - `mobile/**/android/*.keystore`
- 没有提交真实 keystore、密码或别名。

仍需 owner 本地完成：

```powershell
keytool -genkeypair -v -keystore mobile\user-app\android\keystore\campus-user-release.jks -alias campus-user -keyalg RSA -keysize 2048 -validity 10000
keytool -genkeypair -v -keystore mobile\parttime-app\android\keystore\campus-parttime-release.jks -alias campus-parttime -keyalg RSA -keysize 2048 -validity 10000
```

然后分别复制 `key.properties.example` 为 `key.properties`，填入真实密码。真实文件必须保留在仓库外或本地私有目录，不允许提交。

### 2. Android cleartext release 收口

已完成：

- 用户端和兼职端 Manifest 已改为使用 `usesCleartextTraffic` placeholder。
- Debug 构建保留 `usesCleartextTraffic=true`，继续支持当前 HTTP 内测环境。
- Release 构建设置 `usesCleartextTraffic=false`。
- 已添加 debug/release 分离的 `network_security_config.xml`。
- 已通过 Gradle 验证：
  - 用户端 debug / release 构建通过。
  - 兼职端 debug / release 构建通过。
  - 合并后的 debug manifest 为 `usesCleartextTraffic=true`。
  - 合并后的 release manifest 为 `usesCleartextTraffic=false`。

注意：

- 当前 HTTPS 入口已就绪，但 release 包仍需要真实 keystore 签名后才能给公开用户。
- 当前 public Debug QA 包仍是内测包，不是正式 release 包。

### 3. HTTPS / 域名 / 证书

已完成：

- 域名 `xiaoyu.xin` 已确认解析到当前服务器。
- Docker 前端端口已收口为宿主机本机端口：
  - `127.0.0.1:18080`
- 宿主机 Nginx 已接入：
  - `/` 反代 frontend。
  - `/api/` 反代 backend。
- 已通过 Certbot 为 `xiaoyu.xin` 签发真实证书。
- 证书只保存在服务器宿主机 Nginx 层，未进入 Docker 容器，未提交到 Git。
- HTTP 80 已跳转 HTTPS。
- 已提供仓库侧 Nginx 443 模板：
  - `deploy/internal-trial/nginx-xiaoyu.xin.conf`
- 已提供执行与回滚说明：
  - `docs/deployment/xiaoyu-xin-https-runbook.md`
- Android public env 示例已切换为 `https://xiaoyu.xin/api`。
- 已完成远端 smoke：
  - 25 PASS / 0 FAIL / 0 SKIP。
- 已重新生成 Android public QA 包，并完成真机轻量安装 / 启动复核。

注意：

- 不要提交证书、证书私钥、服务器 `.env` 或服务器登录凭据。
- 后续正式 release 包仍需真实 keystore 签名。

### 4. 隐私说明与用户协议入口

已完成：

- 新增公共页面：
  - `/legal/privacy`
  - `/legal/terms`
- 用户端登录页和兼职端登录页已增加协议勾选。
- 未勾选时不允许继续登录。
- 当前静态文本已覆盖：收集信息、使用目的、保存期限、用户权利和反馈渠道。

仍需 owner 补充：

- 正式公开前建议补齐真实运营主体、联系邮箱或其他正式联系方式。
- 如果后续上架应用商店，仍需按应用商店要求补隐私政策公开 URL 和首次启动弹窗。

### 5. App 内反馈入口

已完成：

- 新增公共反馈页：`/feedback`。
- 用户端个人中心新增“问题反馈”入口。
- 兼职端资料页新增“问题反馈”入口。
- 后端新增 `campus_feedback` 表。
- 新增提交接口：`POST /api/campus/public/feedback`。
- H2/test profile 已验证反馈提交返回 `code=200` 并生成记录 ID。

仍需后续优化：

- 目前反馈可提交入库，但尚无 admin 反馈列表 / 处理页面。
- 公测早期可先通过数据库查询处理；若测试人数扩大，建议补一个 admin 只读反馈列表。

## 当前仍未收口

### 1. 真实 release keystore 与正式签名 APK

当前状态：

- 仓库已有 release 签名配置入口和 `key.properties.example`。
- 真实 keystore、真实 `key.properties` 和正式 release APK 仍未由 owner 本地生成。

这是进入公开公测前最后的核心阻断项。Debug QA 包仍只适合 owner-controlled 内测，不应作为公开公测包分发。

## 当前禁止项

- 不提交真实 keystore。
- 不提交 `key.properties`。
- 不提交公网 IP、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。
- 不把 Debug QA 包当正式 release 包发给公开用户。
- 不重开 bridge 主线。
- 不删除旧外卖兼容模块。

## 推荐下一步

Step 171 建议：

1. 生成用户端 / 兼职端真实 release keystore。
2. 填写本地 `key.properties`，确认不提交。
3. 构建双端 release APK。
4. 基于 `https://xiaoyu.xin/api` 做 release 包真机安装和主链路 smoke。
5. 如反馈量开始增加，再补 admin 反馈只读列表。

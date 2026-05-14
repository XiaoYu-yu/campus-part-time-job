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

- 当前没有真实域名和 HTTPS 证书时，不应把 release 包发给公开用户。
- 当前 public Debug QA 包仍是内测包，不是正式 release 包。

## 当前仍未收口

### 1. HTTPS / 域名 / 证书

当前状态：

- 域名 `xiaoyu.xin` 已确认解析到当前服务器。
- Docker 前端端口已收口为宿主机本地 `127.0.0.1:18080`。
- 已提供宿主机 Nginx 443 模板：
  - `deploy/internal-trial/nginx-xiaoyu.xin.conf`
- 已提供执行说明：
  - `docs/deployment/xiaoyu-xin-https-runbook.md`
- Android public env 示例已切换为 `https://xiaoyu.xin/api`。

公开公测前必须完成：

1. 服务器安全组放行 `80/tcp` 和 `443/tcp`。
2. 在服务器安装 Nginx、Certbot 和 `python3-certbot-nginx`。
3. 用 Certbot 为 `xiaoyu.xin` 签发证书。
4. 套用 `nginx-xiaoyu.xin.conf`，确认 HTTP 80 统一跳转 HTTPS。
5. 服务器真实 `.env` 设置 `APP_CORS_ALLOWED_ORIGINS=https://xiaoyu.xin`。
6. 重新生成 Android public 包并确认 cleartext false 下可正常访问。

### 2. 隐私说明与用户协议

当前状态：

- 项目还没有面向用户展示的隐私政策和用户协议入口。
- 移动端登录页还没有协议勾选或首次启动说明。

公开公测前至少需要覆盖：

1. 收集哪些信息：手机号、姓名/昵称、收货/送达信息、订单信息、兼职资料、异常备注/图片、日志。
2. 为什么收集：账号登录、订单履约、异常处理、售后和安全审计。
3. 保存多久：内测/运营期间的保存策略。
4. 用户权利：查询、更正、删除、注销或联系处理。
5. 联系方式：反馈邮箱、表单或站内反馈入口。

### 3. App 内反馈入口

当前状态：

- 已有文档级反馈分级规则。
- App 内还没有真实反馈入口。
- 后端还没有反馈提交接口或反馈表。

可选收口路径：

- 最小过渡方案：个人中心增加“问题反馈”，跳转到外部表单/问卷链接。
- 正式方案：新增 `campus_feedback` 表、提交接口和 admin 只读列表。

公开公测前至少应先有一种可用反馈入口，否则测试用户遇到问题只能靠私聊，问题不可追踪。

## 当前禁止项

- 不提交真实 keystore。
- 不提交 `key.properties`。
- 不提交公网 IP、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。
- 不把 Debug QA 包当正式 release 包发给公开用户。
- 不在没有 HTTPS 的情况下分发 release cleartext=false 包。
- 不重开 bridge 主线。
- 不删除旧外卖兼容模块。

## 推荐下一步

Step 168 建议二选一：

1. 如果要继续推进公开公测：先做 HTTPS / 域名 / 证书和 release 签名实操。
2. 如果仍只做小范围内测：先补 App 内反馈入口和隐私/用户协议静态页。

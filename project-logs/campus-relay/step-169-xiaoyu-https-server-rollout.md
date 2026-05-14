# Step 169 - xiaoyu.xin HTTPS 服务器实操与 Android 公网包复核

## 本轮目标

基于 Step 168 的仓库侧准备，在真实服务器完成 `xiaoyu.xin` HTTPS / Nginx 443 接入，并重新生成 Android 公网 QA 包，确认双端可以指向 `https://xiaoyu.xin/api` 继续运行。

## 执行边界

- 只做服务器 HTTPS 接入、远端 smoke、Android 公网包复核和交付日志。
- 不改业务代码。
- 不改 bridge、`request.js`、token 附着逻辑、鉴权、路由或旧兼容模块。
- 不提交服务器真实 `.env`、服务器密码、证书私钥、release keystore、GitHub token、腾讯地图 key。

## 服务器 HTTPS 实操结果

1. 已将当前仓库归档同步到服务器部署目录。
2. 服务器 Docker Compose 已重建并启动。
3. 服务器真实 `.env` 已设置为：
   - frontend 仅绑定宿主机本机端口。
   - CORS origin 使用 `https://xiaoyu.xin`。
4. 已安装并启用 Nginx。
5. 已安装 Certbot 与 Nginx 插件。
6. 已通过 Certbot 为 `xiaoyu.xin` 签发真实证书。
7. 证书位置在服务器：
   - `/etc/letsencrypt/live/xiaoyu.xin/fullchain.pem`
   - `/etc/letsencrypt/live/xiaoyu.xin/privkey.pem`
8. 证书有效期至 `2026-08-12`。
9. 已开启 HTTP -> HTTPS 跳转。
10. Docker 容器不保存证书，证书只留在宿主机 Nginx 层。

## 访问验证

已验证：

- `http://xiaoyu.xin/` 会跳转到 HTTPS。
- `https://xiaoyu.xin/` 返回前端页面。
- `https://xiaoyu.xin/api/` 能进入后端代理，返回认证拦截结果属于预期。
- 服务器本机可访问 frontend 本机端口。
- 服务器本机可访问 backend 本机端口。
- `nginx -t` 通过。
- Certbot timer 已存在。
- `certbot renew --dry-run` 日志确认模拟续期成功，没有续期失败项。

## 远端 smoke 结果

运行：

```powershell
.\scripts\trial-operation\remote-smoke.ps1 -ApiBase "https://xiaoyu.xin/api" -FrontendBase "https://xiaoyu.xin" -OutputPath "project-logs\campus-relay\runtime\step-169-xiaoyu-https\remote-smoke-report.json"
```

结果：

- `PASS = 25`
- `FAIL = 0`
- `SKIP = 0`

覆盖：

- public health
- admin / customer / parttime 登录
- admin employee / settlement / after-sale / courier / exception 历史
- customer onboarding / token eligibility / completed detail
- parttime profile / review / available / completed
- frontend shell / admin / campus pages / customer result / parttime workbench

报告中的 URL 与 token 已脱敏。

## Android 公网 API smoke

运行：

```powershell
.\scripts\trial-operation\android-public-api-smoke.ps1 -ApiBase "https://xiaoyu.xin/api" -OutputPath "project-logs\campus-relay\runtime\step-169-xiaoyu-https\android-public-api-smoke.json"
```

结果：

- `passed = 2`
- `failed = 0`

覆盖：

- 取餐点公共读取
- 配送规则公共读取

## Android 公网 QA 包

运行：

```powershell
.\scripts\trial-operation\build-android-qa-apks.ps1 -Mode public -OutputDirectory "project-logs\campus-relay\runtime\step-169-xiaoyu-https\android-qa-apks"
```

结果：

- 用户端 public Debug APK 构建完成。
- 兼职端 public Debug APK 构建完成。
- Manifest 记录在本地 runtime 目录。
- APK 二进制不提交到 Git。

本轮 APK SHA256：

- 用户端：`A2997B715CA683599AE5E19707791EB8A15604F84CC69C0EF4FF9DA20F4C8D62`
- 兼职端：`EE8BC87E80DCE0D0F3899A7760489695B086633665A9D6C0A64784170C3FEAE1`

## Android 真机轻量启动复核

新增脚本：

```text
scripts/trial-operation/android-app-launch-smoke.py
```

用途：

- 安装用户端 APK。
- 安装兼职端 APK。
- 拉起两个 Android 包。
- 输出 JSON 报告。
- 不抓截图。
- 默认脱敏真实设备 ID。

本轮运行结果：

- 用户端 `com.xiaoyu.campus.user` 安装成功。
- 用户端 launcher 启动成功。
- 兼职端 `com.xiaoyu.campus.parttime` 安装成功。
- 兼职端 launcher 启动成功。
- 报告：`project-logs/campus-relay/runtime/step-169-xiaoyu-https/android-app-launch-smoke.json`。

## 本轮明确未做

- 未生成正式 release keystore。
- 未提交真实 `key.properties`。
- 未提交证书、证书私钥、服务器 `.env`、服务器密码、公网地址、GitHub token 或腾讯地图 key。
- 未新增隐私政策 / 用户协议入口。
- 未新增 App 内反馈入口。
- 未改业务接口。
- 未改前端业务页面。
- 未改 bridge。
- 未删除旧兼容模块。

## 当前结论

HTTPS / 域名 / Nginx 443 这一公开公测阻断项已经实际收口。项目现在比 Step 168 明显更接近公开公测，但仍不是完整公开公测状态。

仍阻塞公开公测的事项：

1. 真实 Android release keystore 与签名包。
2. 隐私政策 / 用户协议入口。
3. App 内反馈入口。
4. 正式 release 包的端到端安装与 HTTPS API 回归。

## 下一轮建议

Step 170 优先做 App 内合规与反馈入口：

1. 用户端和兼职端登录页增加隐私政策 / 用户协议入口。
2. 增加最小 App 内反馈入口，优先先做外部表单跳转或轻量后端 `campus_feedback`。
3. 继续不提交真实密钥和服务器敏感信息。
4. 继续保持 bridge 冻结、展示 polish 维护态和旧兼容模块保留策略。

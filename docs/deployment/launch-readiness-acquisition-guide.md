# 上线差距与材料获取指南

## 当前结论

截至 Step 182，项目已经具备：

- 后端 Spring Boot + MySQL + Flyway 可运行。
- 138 standalone MySQL 栈可用于局域网内测和答辩演示。
- customer / courier / admin 三端主链路可 smoke。
- 创建代送单 -> 模拟支付 -> 接单 -> 取餐 -> 配送 -> 确认 的最小闭环已验证。

当前还不建议直接作为正式公网长期生产环境。原因不是业务链路不通，而是正式上线需要的外部材料、运维能力和安全边界还没完全补齐。

## 当前适合什么

| 场景 | 当前是否适合 | 说明 |
| --- | --- | --- |
| 本地开发 | 适合 | H2 test profile / MySQL standalone 都可用 |
| 局域网内测 | 适合 | 当前 138 `http://192.168.121.138:18080/` 已验证 |
| 答辩 / 演示 | 适合 | smoke 与真实闭环均通过 |
| 小范围人工试用 | 基本适合 | 建议先补备份脚本 |
| 公网正式上线 | 暂不建议 | 还缺 HTTPS、域名/备案、正式签名包、备份恢复、监控告警等 |

## P0：正式上线前必须补齐

### 1. 生产域名

用途：

- 给 Web 前端和 API 一个稳定访问地址。
- Android release WebView 最终也应访问 HTTPS 域名，而不是局域网 IP。

你需要获得：

- 一个域名，例如 `example.com`。
- DNS 控制权，可添加 A / CNAME 记录。

怎么获得：

1. 在阿里云、腾讯云、Cloudflare、Namesilo 等域名服务商购买域名。
2. 完成域名实名信息。
3. 将域名解析到正式服务器公网 IP。

如果服务器在中国大陆：

- 网站 / App 对公网提供服务通常需要 ICP 备案。
- 备案入口一般走云厂商代备案系统，例如阿里云或腾讯云。
- 备案通过后，按要求在网站底部展示备案号。

如果只做局域网演示：

- 暂时不需要域名。
- 继续使用 `http://192.168.121.138:18080/` 即可。

参考入口：

- 工信部 ICP 备案系统：https://beian.miit.gov.cn/
- 阿里云 ICP 备案：https://beian.aliyun.com/
- 腾讯云 ICP 备案：https://cloud.tencent.com/product/ba
- Cloudflare 域名接入文档：https://developers.cloudflare.com/fundamentals/manage-domains/add-site/

### 2. HTTPS 证书

用途：

- Web 正式访问必须走 HTTPS。
- Android release 环境不建议使用 HTTP。
- 登录 token、账号密码和管理端操作都不应在明文 HTTP 上传输。

你需要获得：

- 域名对应的 TLS/SSL 证书。
- Nginx 可读取的证书文件和私钥文件。

怎么获得：

1. 已有域名后，优先使用 Let’s Encrypt 免费证书。
2. 在服务器上使用 Certbot 或其它 ACME client 签发证书。
3. 配置 Nginx：
   - 80 端口跳转 443。
   - 443 代理到 frontend / backend。
4. 开启自动续期。

参考入口：

- Let’s Encrypt Getting Started：https://letsencrypt.org/getting-started/
- Let’s Encrypt 文档：https://letsencrypt.org/docs/

### 3. Android 生产 release 签名

用途：

- 生成正式 signed APK / AAB。
- 以后升级 App 时必须继续使用同一套签名材料，否则用户无法覆盖安装升级。

你需要获得 / 生成：

- 用户端 release keystore。
- 兼职端 release keystore。
- keystore 密码、key alias、key password。
- `android/key.properties` 或 GitHub Actions signing secrets。

怎么获得：

1. 在 Android Studio 使用 Generate Signed Bundle / APK 创建 release key。
2. 或用 `keytool` 生成 keystore。
3. 把 keystore 放到本机私有目录，不要提交 Git。
4. 在项目中只配置本地 `key.properties`，并确认 `.gitignore` 已忽略。
5. 运行当前仓库已有 release 构建脚本，产出 signed APK / AAB。
6. 用 MuMu 或真机安装正式签名包并跑 smoke。

注意：

- 临时 QA keystore 不能当生产 keystore。
- keystore 丢了会影响后续升级，必须备份到安全位置。
- 如果发布到 Google Play，需要按 Play App Signing 流程处理 app signing key / upload key。

参考入口：

- Android App Signing：https://developer.android.com/studio/publish/app-signing
- Android Build for Release：https://developer.android.com/build/build-for-release

### 4. 生产 secrets

用途：

- 防止数据库、JWT、地图 key、证书私钥等敏感信息泄漏。

你需要准备：

- 生产 `JWT_SECRET`。
- 生产 MySQL 用户和密码。
- 生产 `.env`。
- 腾讯地图 key。
- HTTPS 证书私钥。
- Android keystore 和密码。

怎么获得 / 生成：

1. `JWT_SECRET`：用随机生成器生成 32 字节以上随机字符串。
2. MySQL 密码：单独生成强密码，不复用内测密码。
3. `.env`：只保存在服务器，不提交仓库。
4. 如果之前在聊天、截图或文档里出现过内测密码，正式上线前要换掉。

推荐存放：

```text
服务器：/opt/campus-part-time-job-standalone/deploy/standalone-podman/.env
本机：只保留私有备份，不进 Git
GitHub：只放 GitHub Secrets，不放明文文件
```

不要提交：

- `.env`
- `key.properties`
- `*.jks`
- `*.keystore`
- 证书私钥
- 真实腾讯地图 key
- 数据库密码

### 5. MySQL 备份与恢复

用途：

- 防止误删数据、服务器损坏、迁移失败后无法恢复。

你需要获得：

- 每日自动备份脚本。
- 备份保存目录。
- 备份保留策略。
- 至少一次恢复演练记录。

怎么做：

1. 使用 `mysqldump` 导出 `campus_standalone`。
2. 同时备份上传文件 volume。
3. 备份文件命名带日期。
4. 至少保留 7 - 14 天。
5. 用临时 MySQL 容器做一次恢复演练。

当前项目建议下一步优先补：

```text
scripts / deploy:
- campus-backup
- campus-restore-drill
- campus-status
```

### 6. 服务器资源

当前 138 状态：

- 磁盘 `/` 可用约 17G，当前够用。
- 内存总量约 3.5Gi，可用约 531Mi，已使用少量 swap。

判断：

- 项目单独跑：够用。
- 同时跑 Hadoop / Hive 课堂任务：偏紧。
- 正式长期运行：建议至少 4Gi 内存，最好 8Gi 或独立机器。

怎么获得：

1. 如果继续用 VMware：给 138 虚拟机加内存。
2. 如果公网内测：买一台云服务器，至少 2C4G；更稳妥 2C8G。
3. 如果还要保留课程集群，项目服务尽量不要和 Hive 高负载同时跑。

## P1：强烈建议上线前补齐

### 1. 监控与健康检查

需要：

- 容器是否运行。
- 前端是否可访问。
- 后端 health 是否 `UP`。
- MySQL 是否可连接。
- 磁盘空间是否不足。
- 内存是否接近耗尽。
- 最近日志是否出现 `ERROR`。

建议实现：

```bash
campus-status
campus-smoke
campus-logs
```

### 2. 日志轮转

需要：

- 限制容器日志大小。
- 定期清理旧日志。
- 避免磁盘被日志撑爆。

建议：

- Podman / systemd 层配置日志保留。
- Nginx access/error log 做 rotate。
- 应用日志不要长期无限增长。

### 3. 回滚 runbook

需要写清楚：

1. 当前稳定 commit 是哪个。
2. 如何回到上一个 commit。
3. 如何重建 backend/frontend 镜像。
4. 如何不删除 MySQL volume。
5. 如何验证回滚后 smoke 通过。

### 4. 更长时间稳定性测试

当前已完成短时验证：

- 5 轮真实业务闭环。
- 3 轮远程 smoke。
- 60 次 health 探测。
- 重启恢复测试。

上线前建议再补：

- 2 小时 soak test。
- 8 小时 soak test。
- 20 - 50 并发轻量压测。

### 5. 腾讯地图 key

用途：

- 如果前端展示腾讯地图、位置预览或地点能力，需要真实 key。

怎么获得：

1. 登录腾讯位置服务。
2. 创建应用。
3. 申请 JavaScript API GL 或 WebService API key。
4. 配置域名白名单。
5. 构建前注入 `VITE_TENCENT_MAP_KEY`。

参考入口：

- 腾讯位置服务：https://lbs.qq.com/
- 腾讯地图 JavaScript API GL：https://lbs.qq.com/javascript_gl/index.html
- 腾讯地图 WebService API：https://lbs.qq.com/webservice_v1/index.html

### 6. 生产 CORS 与访问边界

当前内测：

```text
APP_CORS_ALLOWED_ORIGINS=http://192.168.121.138:18080
```

正式上线后应改为：

```text
APP_CORS_ALLOWED_ORIGINS=https://你的正式域名
```

不要使用：

```text
*
```

## P2：后续优化

### 1. 真实支付 / 退款 / 打款

当前第一版明确只允许模拟支付，不接第三方支付。

如果后续要真实资金流，需要单独立项：

- 支付资质。
- 商户号。
- 回调验签。
- 退款流程。
- 打款或提现流程。
- 财务对账。
- 风控和审计。

不建议在当前版本临时接入。

### 2. 消息通知

可选能力：

- 订单被接单通知。
- 取餐通知。
- 送达通知。
- 售后处理通知。

获取方式取决于渠道：

- App 推送：厂商推送 / Firebase Cloud Messaging / 国内推送服务。
- 短信：阿里云短信、腾讯云短信等，需要签名和模板审核。

### 3. 旧模块清理

当前 AGENTS 约束要求：

- 不删除旧外卖模块。
- 普通用户复用 `user`。
- 管理员复用 `employee`。

因此短期不建议为“看起来干净”直接删旧后端模块。后续若要清理，必须按 controller -> service -> mapper -> table 逐项审计。

## 项目文件整理口径

当前仓库主线建议按以下方式理解：

```text
README.md                         项目总入口
docs/README.md                    文档索引
docs/deployment/                  部署、上线、运维、备份、密钥文档
docs/campus-relay/                校园代送领域规划
backend/                          Spring Boot 后端
frontend/                         Vue 前端
mobile/                           Android 壳工程
deploy/standalone-podman/         138 当前 standalone 部署脚本
project-logs/campus-relay/        每轮工作日志与证据
```

不建议现在做的整理：

- 不移动 `backend/`、`frontend/`、`mobile/`。
- 不删除 `legacy-takeaway/`。
- 不删除旧后端外卖模块。
- 不把 `project-logs/` 改名或拆散。
- 不把真实 `.env` 放进仓库。

建议现在做的整理：

1. 所有“上线还差什么”看本文档。
2. 所有部署动作看 `docs/deployment/`。
3. 所有阶段证据看 `project-logs/campus-relay/summary.md`。
4. `main` 分支保持干净、可运行、可推送。
5. 以后每完成一次上线补强，都更新对应 Step 日志。

## main 分支状态口径

当前 `main` 应保持：

- 可构建。
- 可 smoke。
- 不包含真实 secrets。
- 包含最新文档和部署脚本。
- 包含 Flyway 迁移和 MySQL standalone 验证记录。

每次提交前至少执行：

```powershell
git status --branch --short
git diff --check
```

有代码改动时再执行：

```powershell
cd backend
.\mvnw.cmd test
```

```powershell
cd frontend
npm run build
```

只有文档变更时，至少执行：

```powershell
git diff --check
```

## 推荐下一步顺序

### 如果目标是“先稳住 138 内测”

1. 做 MySQL 备份脚本。
2. 做恢复演练脚本。
3. 做 `campus-status` / `campus-smoke`。
4. 做日志轮转。

### 如果目标是“给别人公网访问”

1. 买域名。
2. 按服务器所在地区判断是否需要 ICP 备案。
3. 配 DNS。
4. 配 HTTPS。
5. 改生产 CORS。
6. 跑公网 smoke。

### 如果目标是“发 Android 包”

1. 准备生产 keystore。
2. 构建 signed APK / AAB。
3. 配 HTTPS API 地址。
4. MuMu / 真机安装验证。
5. 决定分发方式：直接 APK、应用商店、Google Play 或校内分发。

## 本文档引用的官方入口

- Android App Signing：https://developer.android.com/studio/publish/app-signing
- Android Build for Release：https://developer.android.com/build/build-for-release
- Let’s Encrypt Getting Started：https://letsencrypt.org/getting-started/
- Cloudflare 添加域名：https://developers.cloudflare.com/fundamentals/manage-domains/add-site/
- 工信部 ICP 备案系统：https://beian.miit.gov.cn/
- 阿里云 ICP 备案：https://beian.aliyun.com/
- 腾讯云 ICP 备案：https://cloud.tencent.com/product/ba
- 腾讯位置服务：https://lbs.qq.com/

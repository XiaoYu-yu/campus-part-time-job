# Step 168 - xiaoyu.xin HTTPS / Nginx 443 接入准备

## 本轮目标

基于 owner 已确认域名 `xiaoyu.xin` 并已解析到服务器，本轮把 HTTPS / Nginx 443 接入准备沉淀到仓库配置中，避免只停留在口头命令。

本轮不直接操作服务器证书，不提交真实证书、服务器密码、`.env`、腾讯地图 key 或任何真实密钥。

## 实际完成

1. Docker Compose 前端公网暴露收口：
   - `deploy/internal-trial/docker-compose.yml`
   - frontend 从 `${FRONTEND_PORT:-80}:80` 改为 `127.0.0.1:${FRONTEND_PORT:-18080}:80`
   - 后续公网访问统一经宿主机 Nginx 443 入口，不再让 frontend 容器直接占用公网 80。

2. 部署环境样例更新：
   - `deploy/internal-trial/.env.example`
   - `FRONTEND_PORT=18080`
   - `APP_CORS_ALLOWED_ORIGINS=https://xiaoyu.xin`

3. 新增宿主机 Nginx 模板：
   - `deploy/internal-trial/nginx-xiaoyu.xin.conf`
   - `80` 统一跳转 `443`
   - `/` 反代 `127.0.0.1:18080`
   - `/api/` 反代 `127.0.0.1:8080`
   - 证书路径使用 Let's Encrypt 默认路径：
     - `/etc/letsencrypt/live/xiaoyu.xin/fullchain.pem`
     - `/etc/letsencrypt/live/xiaoyu.xin/privkey.pem`

4. 新增 HTTPS 执行说明：
   - `docs/deployment/xiaoyu-xin-https-runbook.md`
   - 覆盖安全组、Nginx / Certbot 安装、证书签发、模板套用、HTTPS 验证、续期验证和回滚。

5. Android public API base 示例切换到 HTTPS：
   - `frontend/.env.android-user-public.example`
   - `frontend/.env.android-parttime-public.example`
   - 均改为 `https://xiaoyu.xin/api`

6. 本地 ignored public env 已同步：
   - `frontend/.env.android-user-public`
   - `frontend/.env.android-parttime-public`
   - 均改为 `https://xiaoyu.xin/api`
   - 这两个文件不提交，只用于本机后续 public 包构建。

7. 部署文档同步：
   - `docs/deployment/internal-trial-compose.md`
   - `docs/deployment/public-beta-release-gap-closure.md`

## 验证结果

1. DNS 本地解析：
   - `xiaoyu.xin` 已指向当前试运营服务器。

2. 前端构建：
   - `npm run build`：通过
   - `npm run build:android:user:public`：通过
   - `npm run build:android:parttime:public`：通过

3. Android public 构建产物 API base 检查：
   - `dist-android-user` 中已包含 `https://xiaoyu.xin/api`
   - `dist-android-parttime` 中已包含 `https://xiaoyu.xin/api`
   - 未再命中旧 HTTP 公网 IP。

4. 后端构建：
   - `.\mvnw.cmd -DskipTests compile`：通过

5. 空白检查：
   - `git diff --check`：通过，仅 CRLF 提示。

6. Docker Compose 配置校验：
   - 本机没有 `docker` 命令，无法执行 `docker compose config`。
   - 本轮只完成文本级配置审查，服务器实操时仍需在服务器上执行 `docker compose config` 或直接 `up -d --build` 前检查。

## 本轮没有做

- 没有申请真实证书。
- 没有登录服务器执行 Nginx / Certbot。
- 没有提交 `/etc/letsencrypt` 下的任何证书文件。
- 没有提交真实 `.env`。
- 没有生成真实 Android release keystore。
- 没有改后端业务接口。
- 没有改前端业务页面。
- 没有改 bridge。
- 没有改 `request.js`、token 附着逻辑、路由或鉴权。
- 没有删除旧兼容模块。

## 当前结论

`xiaoyu.xin` HTTPS 接入已经具备仓库侧配置模板和执行说明，但公开公测仍未完全就绪。

下一步必须在服务器上完成：

1. 安全组确认开放 `80` / `443`。
2. 安装宿主机 Nginx / Certbot。
3. 签发 `xiaoyu.xin` 证书。
4. 套用 `nginx-xiaoyu.xin.conf`。
5. 验证 `https://xiaoyu.xin/` 和 `https://xiaoyu.xin/api/`。
6. 重新打 Android public 包并真机验证 HTTPS。

## 下一轮建议

优先级 A：服务器实操 HTTPS / Nginx 443，并做远端 smoke。

优先级 B：如果暂时不动服务器，则补隐私政策 / 用户协议 / App 内反馈入口。

bridge 主线继续保持 `Phase A no-op` 冻结态。

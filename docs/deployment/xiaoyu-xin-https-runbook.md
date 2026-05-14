# xiaoyu.xin HTTPS / Nginx 443 执行说明

本文记录当前校园兼职平台内测服务器的 HTTPS 接入方案。

## 当前执行状态

截至 Step 169，`xiaoyu.xin` HTTPS 已在服务器完成实操：

- 宿主机 Nginx 已安装并启用。
- Certbot 与 Nginx 插件已安装。
- `xiaoyu.xin` 证书已签发。
- HTTP 80 已跳转 HTTPS。
- `https://xiaoyu.xin/` 已反代到 frontend。
- `https://xiaoyu.xin/api/` 已反代到 backend。
- 证书有效期至 `2026-08-12`。
- Certbot timer 已存在。
- `certbot renew --dry-run` 日志确认模拟续期成功。
- 远端 smoke 结果：25 PASS / 0 FAIL / 0 SKIP。

本文后续内容保留为复现、排查和回滚说明。

## 当前目标

- 域名：`xiaoyu.xin`
- DNS：`xiaoyu.xin` 已解析到当前试运营服务器。
- 公网入口：
  - `https://xiaoyu.xin/` -> 宿主机 Nginx -> frontend 容器
  - `https://xiaoyu.xin/api/` -> 宿主机 Nginx -> backend 容器
- Docker 本机端口：
  - frontend 容器绑定到 `127.0.0.1:18080`
  - backend 容器绑定到 `127.0.0.1:8080`

证书只放在宿主机 Nginx 层，Docker 容器不保存证书文件。

## 服务器前置条件

1. 云服务器安全组已放行 `80/tcp` 和 `443/tcp`。
2. `xiaoyu.xin` 已解析到服务器。
3. Docker Compose 栈使用 `FRONTEND_PORT=18080` 部署。
4. 在服务器上能访问 backend：

```bash
curl -I http://127.0.0.1:8080/api/
```

5. 在服务器上能访问 frontend：

```bash
curl -I http://127.0.0.1:18080/
```

## 安装 Nginx 与 Certbot

```bash
apt update
apt install -y nginx certbot python3-certbot-nginx
systemctl enable --now nginx
nginx -t
```

## 首次申请证书

使用 Certbot 的 Nginx 插件为域名签发证书：

```bash
certbot --nginx -d xiaoyu.xin
```

Certbot 询问是否启用 HTTP 到 HTTPS 跳转时，选择启用跳转。

证书签发完成后，最终 Nginx 配置应与以下模板保持一致：

```text
deploy/internal-trial/nginx-xiaoyu.xin.conf
```

## 手动套用 Nginx 配置

如果 Certbot 没有正确改写 Nginx 配置，可以手动套用模板：

```bash
cp deploy/internal-trial/nginx-xiaoyu.xin.conf /etc/nginx/conf.d/xiaoyu.xin.conf
nginx -t
systemctl reload nginx
```

模板默认读取证书：

```text
/etc/letsencrypt/live/xiaoyu.xin/fullchain.pem
/etc/letsencrypt/live/xiaoyu.xin/privkey.pem
```

## HTTPS 验证

```bash
curl -I http://xiaoyu.xin
curl -I https://xiaoyu.xin
curl -I https://xiaoyu.xin/api/
```

预期结果：

- `http://xiaoyu.xin` 返回 `301` 并跳转 HTTPS。
- `https://xiaoyu.xin` 返回前端页面。
- `https://xiaoyu.xin/api/` 能进入后端代理；如果 `/api/` 本身没有接口，返回后端 404 也可以接受，重点是代理路径正确。

## 自动续期验证

```bash
systemctl list-timers | grep certbot
certbot renew --dry-run
```

## Android Public 构建对齐

公开 Android 构建示例已指向：

```text
https://xiaoyu.xin/api
```

打公开包前确认这两个本地 ignored env 文件一致：

```text
frontend/.env.android-user-public
frontend/.env.android-parttime-public
```

前端 public 构建：

```bash
cd frontend
npm run build:android:user:public
npm run build:android:parttime:public
```

然后按项目现有脚本同步 Capacitor Android 工程并重新构建 APK。

## 回滚

如果 HTTPS 配置期间出现问题：

1. 只禁用宿主机 Nginx 域名配置：

```bash
mv /etc/nginx/conf.d/xiaoyu.xin.conf /etc/nginx/conf.d/xiaoyu.xin.conf.disabled
nginx -t
systemctl reload nginx
```

2. 保持 Docker 容器继续运行在本机端口。
3. 在修好证书配置前，只允许 owner-controlled 测试通过 SSH tunnel 或服务器本机诊断访问。

不要为了公开包回退 Android release 的明文 HTTP 限制。

## 本说明未解决的事项

- Android 真实 release keystore 仍需要本地生成并私有保存。
- 公开公测前仍需要隐私政策和用户协议。
- 公开公测前仍需要 App 内反馈入口。

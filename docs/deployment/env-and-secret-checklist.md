# 环境变量与密钥配置清单

## 目标

这份清单用于把当前本地试运营 RC 推进到 prod-like 试运营前的配置检查状态。

它不接真实支付、真实退款、真实打款，也不要求现在完成真实部署。它只回答：

1. 哪些配置必须准备。
2. 哪些密钥不能提交到仓库。
3. 本地演示和部署注入分别怎么处理。
4. 部署前应该逐项检查什么。

## 已新增的样例文件

1. 根目录 `.env.example`
   - 只作为入口提示。
   - 指向 backend / frontend 的具体样例文件。
2. `backend/.env.example`
   - 覆盖 `SPRING_PROFILES_ACTIVE`、`SERVER_PORT`、DB、JWT、CORS 和上传目录。
3. `frontend/.env.example`
   - 覆盖 `VITE_API_BASE_URL`、`VITE_USE_MOCK` 和 `VITE_TENCENT_MAP_KEY`。

这些文件只允许包含占位值，不允许填入真实腾讯地图 key、数据库密码或生产 JWT secret。

## 后端必填配置

| 变量 | 用途 | 本地 H2/test | prod-like 试运营 |
| --- | --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `test` | `prod` |
| `SERVER_PORT` | 后端端口 | 默认 `8080` | 按部署端口设置 |
| `DB_HOST` | MySQL / MariaDB 地址 | 不需要 | 必填 |
| `DB_PORT` | 数据库端口 | 不需要 | 必填 |
| `DB_NAME` | 数据库名 | 不需要 | 必填 |
| `DB_USERNAME` | 数据库账号 | 不需要 | 必填 |
| `DB_PASSWORD` | 数据库密码 | 不需要 | 必填 |
| `JWT_SECRET` | JWT 签名密钥 | test profile 内置测试值 | 必填，必须是长随机值 |
| `JWT_EXPIRATION` | JWT 有效期 | 默认 `86400000` | 按安全要求设置 |
| `APP_CORS_ALLOWED_ORIGINS` | 前端允许来源 | `localhost:5173` | 必填，只允许正式前端域名 |
| `APP_UPLOAD_STORAGE_PATH` | 上传文件目录 | test profile 指向 `target/test-uploads/` | 建议显式配置 |

## 前端必填配置

| 变量 | 用途 | 本地演示 | prod-like 试运营 |
| --- | --- | --- | --- |
| `VITE_API_BASE_URL` | 后端 API 根路径 | 默认 `/api` | 由反向代理决定，通常仍为 `/api` |
| `VITE_USE_MOCK` | mock 开关 | `false` | `false` |
| `VITE_TENCENT_MAP_KEY` | 腾讯地图 JS SDK key | 放在 `frontend/.env.local` | 通过构建环境或密钥系统注入 |

## 腾讯地图 key 处理规则

1. 不把真实 key 写入 `README.md`、日志、Vue 源码、`.env.example` 或提交记录。
2. 本地演示只写入未跟踪文件 `frontend/.env.local`。
3. 部署构建时通过环境变量注入 `VITE_TENCENT_MAP_KEY`。
4. 如果配置了域名白名单，必须确保前端正式域名已加入腾讯地图控制台。
5. 如果 key 缺失，`/campus/courier-ops` 地图区域应只作为降级展示，不影响其它 admin 只读运营页。

## 部署前配置检查

- [ ] `backend/.env.example` 已按目标环境转换为真实环境变量，但真实值没有提交仓库。
- [ ] `frontend/.env.example` 已按目标环境转换为构建环境变量，但真实 key 没有提交仓库。
- [ ] `JWT_SECRET` 为长随机值，不使用默认占位值。
- [ ] `DB_PASSWORD` 不为空，不使用文档示例值。
- [ ] `APP_CORS_ALLOWED_ORIGINS` 只包含正式前端域名。
- [ ] 生产或试运营环境未启用 H2。
- [ ] 数据库迁移已在预发或本地 MySQL/MariaDB 验证。
- [ ] 腾讯地图 key 已确认额度、域名白名单和调用产品范围。
- [ ] 模拟支付、模拟退款、模拟打款口径已在交付说明中明确。
- [ ] bridge 仍处于 `Phase A no-op` 冻结态，不在部署前临时删除或收紧。

## 明确不做

1. 不接真实支付。
2. 不接真实退款。
3. 不接真实打款。
4. 不启用真实短信、消息推送或第三方风控。
5. 不因为新增样例文件而改变任何运行时默认行为。
6. 不把本地 `.env.local` 纳入 git 管理。

## 下一步

配置检查完成后，部署完成的最小验证入口是：

- [部署后 Smoke Checklist](post-deploy-smoke-checklist.md)

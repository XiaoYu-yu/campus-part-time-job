# Step 183 - 上线材料获取指南与 main 主线整理

## 本次目标

owner 要求：

1. 暂停继续部署和服务器操作。
2. 写一份项目距离上线还差什么、这些东西怎么做、怎么申请、怎么获得的文档。
3. 顺手整理项目文件入口和 `main` 主线口径。

本轮只做文档与仓库主线整理，不改服务器、不改数据库、不改业务代码。

## 已完成项

### 1. 新增上线差距与材料获取指南

新增：

- `docs/deployment/launch-readiness-acquisition-guide.md`

文档覆盖：

- 当前状态判断：局域网内测 / 答辩演示可用，公网正式长期运行仍需补强。
- P0 必须补齐：
  - 生产域名
  - HTTPS 证书
  - Android 生产 release 签名
  - 生产 secrets
  - MySQL 备份与恢复
  - 服务器资源
- P1 强烈建议：
  - 监控与健康检查
  - 日志轮转
  - 回滚 runbook
  - 更长时间稳定性测试
  - 腾讯地图 key
  - 生产 CORS 与访问边界
- P2 后续优化：
  - 真实支付 / 退款 / 打款
  - 消息通知
  - 旧模块清理
- 项目文件整理口径。
- `main` 分支状态口径。
- 后续推荐路线：
  - 先稳住 138 内测
  - 给别人公网访问
  - 发 Android 包

### 2. 增加官方入口参考

文档中加入外部材料申请 / 获取入口：

- Android App Signing
- Android Build for Release
- Let’s Encrypt
- Cloudflare 添加域名
- 工信部 ICP 备案系统
- 阿里云 ICP 备案
- 腾讯云 ICP 备案
- 腾讯位置服务

### 3. 整理 README 与 docs 索引入口

更新：

- `README.md`
- `docs/README.md`

调整内容：

- 根 README 当前交付定位增加 138 standalone MySQL + Flyway 栈可用于局域网内测 / 答辩演示。
- 内测型试运营部署入口增加 standalone Podman 部署文档。
- 文档入口增加“上线差距与材料获取指南”。
- `docs/README.md` 当前交付文档和部署与运维部分均增加新文档入口。

## 修改文件

- `README.md`
- `docs/README.md`
- `docs/deployment/launch-readiness-acquisition-guide.md`
- `project-logs/campus-relay/step-183-launch-material-guide-and-mainline-organization.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 验证结果

- 本轮仅文档与索引变更，无业务代码变更。
- 未连接 138。
- 未启动、停止或重启任何服务器服务。
- 未修改数据库。
- 未提交任何真实 `.env`、密码、证书私钥、release keystore、GitHub token 或腾讯地图 key。

待提交前执行：

```powershell
git diff --check
```

## 遗留问题

1. 仍需真正实现 MySQL 备份 / 恢复脚本。
2. 仍需 owner 准备生产 release keystore。
3. 仍需 owner 决定公网域名、备案和 HTTPS 路线。
4. 仍需根据最终上线方式决定是否继续放在 138，或迁移到独立云服务器。

## 下一步建议

优先做 138 standalone 运维三件套：

1. `campus-backup`
2. `campus-restore-drill`
3. `campus-status`

然后再根据 owner 的目标选择：

- 网页公网内测：域名 / 备案 / HTTPS。
- Android 分发：生产 keystore / signed APK / signed AAB。

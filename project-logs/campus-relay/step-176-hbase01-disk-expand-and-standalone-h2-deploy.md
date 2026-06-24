# Step 176 - 138 磁盘扩容与 standalone H2 smoke 部署

日期：2026-06-24

## 本次目标

1. 在 owner 指定可用的 `192.168.121.138` 上完成扩容后的系统盘分区 / LVM / 文件系统扩展。
2. 继续遵守“不动集群环境”的边界，用独立目录和独立命名部署当前项目。
3. 在 138 上先拿到一个可访问、可 smoke 的版本，支撑后续上线前验证。
4. 同步补齐 standalone 部署脚本在当前网络环境下的镜像源、Maven / npm mirror 与 H2 smoke fallback。

## 已完成项

1. 138 磁盘扩展已完成：
   - 扩展前：`/dev/sda` 已是 40G，但 `/dev/sda2` / root LV 仍约 19G / 17G。
   - 已备份分区表到：`/root/sda-partition-before-campus-expand-20260624-124429.sfdisk`。
   - 已执行分区扩展、`pvresize`、`lvextend -r`。
   - 扩展后：`/dev/sda2` 约 39G，root LV / xfs 约 37G，`/` 可用空间约 23G。
2. standalone 源码已放到：
   - `/opt/campus-part-time-job-standalone`
3. 已生成私有 `.env`：
   - `/opt/campus-part-time-job-standalone/deploy/standalone-podman/.env`
   - 权限为 `0600`
   - 未写入仓库，日志不记录密钥内容。
4. 由于 138 当前拉取 MySQL 镜像不稳定，已先启动 H2 smoke-only 栈：
   - `campus-standalone-backend`
   - `campus-standalone-frontend`
   - `campus-standalone-net`
5. 当前访问入口：
   - 前端 / API 代理：`http://192.168.121.138:18080/`
   - 后端宿主机绑定：`127.0.0.1:18081`
   - MySQL 未启动 standalone 容器。
6. 远端 smoke 已通过：
   - `27 PASS / 0 FAIL / 0 SKIP`
   - 报告：`project-logs/campus-relay/runtime/step-176-standalone-138-h2-smoke/remote-smoke-report.json`
7. 138 上 `firewalld` 当前为 inactive；`18080` 对局域网可访问，`18081` 只监听本机回环。
8. standalone 脚本已同步到 138 并通过 `bash -n` 语法检查。

## 修改文件

- `deploy/internal-trial/backend.Dockerfile`
  - 增加 `MAVEN_IMAGE` / `JRE_IMAGE` build arg。
  - 增加 Maven mirror settings 复制。
- `deploy/internal-trial/frontend.Dockerfile`
  - 增加 `NODE_IMAGE` / `NGINX_IMAGE` / `NPM_REGISTRY` build arg。
  - 构建阶段写入 npm registry。
- `deploy/internal-trial/maven-settings.xml`（新增）
  - 使用 Aliyun Maven public mirror，降低 138 构建时 Maven 中央仓库访问失败概率。
- `deploy/standalone-podman/deploy.sh`
  - 默认镜像改为可覆盖的镜像源参数。
  - MySQL 镜像改为 `MYSQL_IMAGE` 可覆盖。
  - 容器敏感环境变量改为 `-e NAME` 继承，避免密码明文直接出现在 `podman run` 命令行。
- `deploy/standalone-podman/deploy-h2-smoke.sh`（新增）
  - 新增非持久化 H2 smoke fallback，用于 MySQL 镜像暂时不可拉取时的局域网验证。
- `deploy/standalone-podman/README.md`
  - 补充镜像源覆盖说明。
  - 补充 H2 smoke fallback 边界说明。
- `project-logs/campus-relay/runtime/step-176-standalone-138-h2-smoke/remote-smoke-report.json`（新增）
  - 138 H2 smoke 报告，URL 已脱敏。
- `project-logs/campus-relay/step-176-hbase01-disk-expand-and-standalone-h2-deploy.md`（新增）
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 验证结果

1. 138 磁盘：
   - root 文件系统已扩到 37G 左右。
   - `/` 使用率从扩容前约 83% 降到约 39%。
2. 138 standalone 容器：
   - backend：`127.0.0.1:18081->8080`
   - frontend：`0.0.0.0:18080->80`
3. 健康检查：
   - `http://127.0.0.1:18081/api/campus/public/health` 返回 `code=200`
   - `http://127.0.0.1:18080/api/campus/public/health` 返回 `code=200`
4. 本机访问 138：
   - `remote-smoke.ps1` 通过 `http://192.168.121.138:18080/api` 跑通 API 与 SPA shell。
5. shell 脚本：
   - `deploy.sh`
   - `deploy-h2-smoke.sh`
   - `init-env.sh`
   - `status.sh`
   - `stop.sh`
   - 均已在 138 上通过 `bash -n`。

## 遗留问题

1. 当前 138 部署是 H2 smoke-only，不是持久化生产部署；容器重建后数据会回到 H2 seed。
2. MySQL standalone 容器尚未完成：
   - `docker.io` 访问不稳定。
   - mirror 拉取 MySQL 镜像过慢 / 中断。
   - 宿主机已有 MySQL 未获授权凭据，未修改、未复用。
3. owner 私有生产 release keystore / GitHub signing secrets 仍未配置。
4. 正式 signed APK / AAB 仍待 owner 私有签名材料到位后生成。
5. 如果要作为公开入口，还需要重新确认 HTTPS / 域名 / 证书 / 访问边界。

## 下一步建议

1. 先选择 138 的持久化数据库路线：
   - 修复镜像源后继续 `deploy.sh` 的 Podman MySQL；
   - 或由 owner 提供 138 上专用 MySQL 库 / 用户，脚本改为外部 DB 模式。
2. 持久化 DB 到位后，重新跑：
   - `remote-smoke.ps1`
   - admin 反馈页浏览器 smoke
   - Android release 包公网 API smoke
3. owner 配置生产 release signing 后，用 `.github/workflows/android-release.yml` 或本地脚本生成正式 signed APK / AAB。
4. 如果 138 要扩大试用范围，建议限制 `18080` 的来源网段，或再接一层正式 Nginx / HTTPS 入口。

## 边界确认

- 未修改 138 上已有集群服务、集群网络、集群卷或集群配置。
- 未停止宿主机已有 MySQL。
- 未复用宿主机 MySQL 凭据。
- 未暴露 backend 18081 到非本机地址。
- 未提交 `.env`、密码、证书私钥、release keystore、GitHub token、腾讯地图 key 或服务器凭据。

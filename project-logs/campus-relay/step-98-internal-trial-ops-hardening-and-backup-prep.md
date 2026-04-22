# Step 98 - 单机服务器内测运维加固 / 最小回滚与备份准备

## 本轮目标

1. 把 Step 96 已成功启动的单机服务器内测环境，继续收敛成“可备份、可回滚、可重复执行”的最小运维入口。
2. 为当前 Compose 部署补一份可实际执行的备份脚本，而不只是停留在文档说明。
3. 在真实服务器上跑通一轮备份，确认 SQL dump、uploads 归档、`.env` 备份和 manifest 都能真实落盘。
4. 不改业务代码、不改 bridge、不改接口、不改鉴权、不改页面。

## 为什么进入这一轮

Step 96 已经证明当前项目：

1. 可以在公网单机服务器上真实拉起。
2. admin / customer / courier 三类 smoke 可通过公网验证。

但当时还缺：

1. 最小备份入口。
2. 最小回滚说明。
3. 一次真实服务器备份验证。

因此 Step 98 的价值不是继续扩业务，而是把“能起服务”推进到“出了问题也知道怎么保底”。

## 实际完成项

### 1. 新增单机内测备份脚本

已新增：

- `deploy/internal-trial/backup-stack.sh`

脚本职责保持最小且明确：

1. 读取 `deploy/internal-trial/.env`
2. 对 compose 内 `mysql` 服务执行 `mysqldump`
3. 打包 uploads volume
4. 备份当前 `.env`
5. 生成一份 manifest，记录时间戳、文件路径和当前 Git commit

默认输出目录：

- `deploy/internal-trial/backups/mysql`
- `deploy/internal-trial/backups/uploads`
- `deploy/internal-trial/backups/meta`

### 2. 补齐备份与回滚文档

已更新：

1. `docs/deployment/backup-and-rollback.md`
2. `docs/deployment/internal-trial-compose.md`

当前文档口径已经收敛为：

1. 这是单机服务器 / 内测型试运营的最小备份与回滚方案。
2. 优先支持“代码或容器更新失败但数据不回滚”的保守回滚。
3. 数据恢复只提供保守流程说明，不提供一键 destructive restore 脚本。
4. 不因为补回滚文档就去改 bridge 或业务主链路。

### 3. `.gitignore` / `.dockerignore` 已忽略备份产物

已更新：

1. `.gitignore`
2. `.dockerignore`

统一忽略：

- `deploy/internal-trial/backups/`

避免服务器真实备份产物被误提交进仓库。

### 4. 真实服务器已跑通一轮备份

本轮在真实服务器上完成：

1. 将 `backup-stack.sh` 上传到 `/opt/campus-part-time-job/deploy/internal-trial/`
2. 修正行尾并赋执行权限
3. 执行：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/backup-stack.sh
```

实际产物已落盘：

1. MySQL dump
   - `/opt/campus-part-time-job/deploy/internal-trial/backups/mysql/campus-trial-cangqiong_takeaway-20260422-195207.sql.gz`
2. uploads 归档
   - `/opt/campus-part-time-job/deploy/internal-trial/backups/uploads/campus-trial-uploads-20260422-195207.tar.gz`
3. `.env` 备份
   - `/opt/campus-part-time-job/deploy/internal-trial/backups/meta/campus-trial-env-20260422-195207.bak`
4. manifest
   - `/opt/campus-part-time-job/deploy/internal-trial/backups/meta/campus-trial-backup-20260422-195207.txt`

manifest 已真实记录：

1. 时间戳
2. compose project name
3. `.env` 路径
4. SQL dump 路径
5. uploads 归档路径
6. `.env` 备份路径
7. 当前 Git commit：`84db483bff4c5cb4e81b13aa4e15a872361218c3`

## 本轮明确没做

1. 没有做自动化 restore 脚本。
2. 没有做 destructive 数据恢复演练。
3. 没有接 HTTPS / 域名。
4. 没有补监控、告警或进程守护。
5. 没有改 bridge。
6. 没有改任何页面、接口、路由、鉴权或业务状态机。

## 修改文件

1. `.gitignore`
2. `.dockerignore`
3. `deploy/internal-trial/backup-stack.sh`
4. `docs/deployment/backup-and-rollback.md`
5. `docs/deployment/internal-trial-compose.md`
6. `project-logs/campus-relay/global-working-memory.md`
7. `project-logs/campus-relay/summary.md`
8. `project-logs/campus-relay/pending-items.md`
9. `project-logs/campus-relay/file-change-list.md`
10. `project-logs/campus-relay/step-98-internal-trial-ops-hardening-and-backup-prep.md`

## 验证结果

1. 真实服务器 compose 栈仍保持运行：
   - `mysql`
   - `backend`
   - `frontend`
2. `backup-stack.sh` 已在服务器上真实执行成功。
3. SQL dump、uploads 归档、`.env` 备份、manifest 四类产物均已落盘。
4. manifest 已记录当前 Git commit。
5. `git diff --check` 通过。

## 当前 bridge 结论

1. bridge 继续保持 `Phase A no-op` 冻结态。
2. 本轮运维加固没有修改 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 的保留策略。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 99 建议进入“单机服务器最小恢复演练 / 运维交付加固 go-no-go”：

1. 评估是否需要做一次保守 restore drill。
2. 梳理 `.env`、MySQL dump、uploads 归档三类恢复入口的优先级。
3. 若当前不适合做真实恢复演练，则至少把运维交接和回滚触发条件写得更明确。
4. 继续不扩业务功能，不重开 bridge，不继续补页面。

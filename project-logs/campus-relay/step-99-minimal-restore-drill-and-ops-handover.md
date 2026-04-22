# Step 99 - 单机服务器最小恢复演练 / 运维交付加固

## 本轮目标

1. 在不影响现有单机服务器内测环境的前提下，验证 Step 98 生成的备份是否至少可恢复到临时环境。
2. 为当前 Compose 内测部署补一个非破坏性的 restore drill 入口，而不是停留在“只有备份脚本，没有恢复验证”。
3. 继续不改业务代码、不改 bridge、不改接口、不改鉴权、不改页面。

## 为什么进入这一轮

Step 98 已经证明：

1. 当前项目在真实服务器上可以完成一轮备份。
2. 服务器上已经有 SQL dump、uploads 归档、`.env` 备份和 manifest。

但那还不能证明：

1. 这些备份文件真的可恢复。
2. 当前 restore 路径不会因为细节错误而失效。

因此 Step 99 的价值是补一轮“保守 restore drill”，确认备份不是摆设。

## 实际完成项

### 1. 新增非破坏性恢复演练脚本

已新增：

- `deploy/internal-trial/restore-drill.sh`

脚本边界保持最小：

1. 读取最新或指定的 backup manifest。
2. 自动定位 SQL dump、uploads 归档和 `.env` 备份。
3. 启一个临时 MySQL 8.4 容器。
4. 把 dump 恢复到临时数据库 `restore_drill`。
5. 校验关键样本订单是否存在：
   - `CR202604070002`
   - `CR202604060001`
6. 解压 uploads 归档并统计恢复出的文件数量。
7. 结束后自动清理临时容器与临时目录。

### 2. 文档已补 restore drill 入口

已更新：

1. `docs/deployment/backup-and-rollback.md`
2. `docs/deployment/internal-trial-compose.md`

文档已明确：

1. `backup-stack.sh` 负责生成备份。
2. `restore-drill.sh` 负责非破坏性恢复演练。
3. restore drill 不会动当前运行中的 compose 栈。

### 3. 真实服务器已跑通一轮 restore drill

本轮在真实服务器上执行：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/restore-drill.sh
```

实际结果：

1. 成功读取 Step 98 生成的 manifest：
   - `/opt/campus-part-time-job/deploy/internal-trial/backups/meta/campus-trial-backup-20260422-195207.txt`
2. 成功恢复 dump：
   - `/opt/campus-part-time-job/deploy/internal-trial/backups/mysql/campus-trial-cangqiong_takeaway-20260422-195207.sql.gz`
3. 成功验证 `campus_relay_order` 恢复后共有 `3` 条记录。
4. 成功验证：
   - `CR202604070002` 存在
   - `CR202604060001` 存在
5. 成功解压并验证 uploads 归档：
   - `/opt/campus-part-time-job/deploy/internal-trial/backups/uploads/campus-trial-uploads-20260422-195207.tar.gz`
6. 当前 uploads 归档恢复出的文件数为 `0`，与当前内测环境“尚无真实上传文件”一致，不构成失败。
7. restore drill 执行结束后，临时 MySQL 容器已自动清理。
8. 现有 compose 栈 `mysql / backend / frontend` 仍保持 `Up`。

## 本轮明确没做

1. 没有做 destructive 数据恢复。
2. 没有覆盖当前 MySQL 数据卷。
3. 没有恢复线上 `.env`。
4. 没有做一键 restore 脚本。
5. 没有接 HTTPS / 域名。
6. 没有改 bridge、业务代码、接口、路由、鉴权或页面。

## 修改文件

1. `deploy/internal-trial/restore-drill.sh`
2. `docs/deployment/backup-and-rollback.md`
3. `docs/deployment/internal-trial-compose.md`
4. `project-logs/campus-relay/global-working-memory.md`
5. `project-logs/campus-relay/summary.md`
6. `project-logs/campus-relay/pending-items.md`
7. `project-logs/campus-relay/file-change-list.md`
8. `project-logs/campus-relay/step-99-minimal-restore-drill-and-ops-handover.md`

## 验证结果

1. 真实服务器 restore drill 已成功完成。
2. dump 可恢复到临时数据库。
3. 关键样本订单可回读。
4. uploads 归档可解压。
5. 临时容器已自动清理。
6. 现有 compose 栈仍保持运行。
7. 当前机器无本地 `bash`，因此脚本语法校验主要通过真实服务器执行闭环完成。
8. `git diff --check` 通过。

## 当前 bridge 结论

1. bridge 继续保持 `Phase A no-op` 冻结态。
2. 本轮恢复演练没有修改 `/api/campus/courier/profile`、`/api/campus/courier/review-status` 的保留策略。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 100 建议进入“单机服务器运维交接与正式入口 go-no-go”：

1. 固化日常运维入口：
   - 启停
   - 备份
   - restore drill
   - smoke
2. 梳理是否需要进入 HTTPS / 域名 / 反向代理正式入口准备。
3. 若当前只做内测，不急于上正式域名，则先把运维交接边界写清楚再收口。

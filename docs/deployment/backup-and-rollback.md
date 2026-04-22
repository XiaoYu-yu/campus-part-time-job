# 备份与回滚说明

## 当前定位

这份文档只面向当前项目的：

1. 单机服务器
2. 内测型试运营
3. Compose 部署

它不是正式生产级灾备方案，但足够覆盖当前最小运维入口：

1. `.env` 备份
2. MySQL 数据备份
3. 上传目录备份
4. 基于 Git commit 的最小回滚

## 当前备份入口

优先使用：

- [backup-stack.sh](../../deploy/internal-trial/backup-stack.sh)

该脚本会一次性完成：

1. MySQL `mysqldump`
2. 上传目录 volume 打包
3. 当前 `.env` 备份
4. 生成一份 manifest，记录时间戳、备份文件路径和当前 Git commit

## 备份前提

执行前至少确认：

1. 已在服务器上部署当前仓库代码
2. `deploy/internal-trial/.env` 已存在
3. `docker compose ps` 中 `mysql` 与 `backend` 至少已正常运行
4. 当前服务器磁盘空间足够存放 SQL dump 和上传目录压缩包

## 备份命令

在服务器仓库目录执行：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/backup-stack.sh
```

如需指定其它 `.env` 路径：

```bash
bash deploy/internal-trial/backup-stack.sh /path/to/.env
```

默认输出目录：

```text
deploy/internal-trial/backups/
├── mysql/
├── uploads/
└── meta/
```

其中：

1. `mysql/` 保存 `.sql.gz`
2. `uploads/` 保存上传目录 `.tar.gz`
3. `meta/` 保存 `.env` 备份和 manifest

## 非破坏性恢复演练入口

当前推荐使用：

- [restore-drill.sh](../../deploy/internal-trial/restore-drill.sh)

该脚本不会动正在运行的 compose 栈，而是：

1. 读取最新或指定的 backup manifest
2. 启一个临时 MySQL 8.4 容器
3. 将 `.sql.gz` 恢复到临时数据库
4. 校验关键样本订单是否存在
5. 解压 uploads 归档并统计恢复出的文件数量
6. 演练结束后自动清理临时容器和临时目录

推荐命令：

```bash
cd /opt/campus-part-time-job
bash deploy/internal-trial/restore-drill.sh
```

如需指定 `.env` 与 manifest：

```bash
bash deploy/internal-trial/restore-drill.sh \
  deploy/internal-trial/.env \
  deploy/internal-trial/backups/meta/campus-trial-backup-YYYYMMDD-HHMMSS.txt
```

它的意义是：

1. 证明当前备份不是“看起来有文件”
2. 而是“至少能恢复到一个临时验证环境”
3. 同时不影响正在运行的内测试运营 compose 栈

## 备份后需要确认

至少确认：

1. SQL dump 文件已生成且大小不是 0
2. uploads 压缩包已生成且大小不是 0
3. `.env` 备份文件已生成
4. manifest 中记录了当前 Git commit

建议额外记录：

1. 备份时间
2. 当前 smoke 结果
3. 是否包含最新演示样本

## 最小回滚策略

### 场景 A：代码或容器更新后应用异常，但数据不需要回滚

这是当前最优先使用的最小回滚策略。

步骤：

1. 找到上一个稳定 Git commit
2. 回到该 commit
3. 用原 `.env` 重新构建并拉起 compose

推荐步骤：

```bash
cd /opt/campus-part-time-job
git log --oneline -5
git checkout <stable-commit>
docker compose --env-file deploy/internal-trial/.env -f deploy/internal-trial/docker-compose.yml up -d --build
```

适用场景：

1. 前端静态资源异常
2. backend 新版本启动异常
3. 配置外的容器行为异常

不适用场景：

1. 数据已被错误写坏
2. schema 已发生不可兼容变化且需要恢复旧数据

### 场景 B：需要连同数据一起回滚

当前不提供“一键数据库恢复脚本”，避免误操作。

如果必须恢复数据，使用：

1. 备份时生成的 SQL dump
2. 备份时生成的 uploads 压缩包
3. 备份时保留的 `.env`

推荐保守流程：

1. 停止当前 compose
2. 保留现场目录另存问题证据
3. 按需要清理 MySQL 数据卷或新建恢复环境
4. 先恢复 SQL dump，再恢复 uploads
5. 最后再拉起应用并跑 smoke

### 场景 C：只恢复配置

如果问题只来自错误 `.env`：

1. 用 `deploy/internal-trial/backups/meta/` 下的 `.env` 备份覆盖当前 `.env`
2. 重新执行 compose 重建

## 哪些现象出现时建议立即回滚

1. `backend` 容器无法稳定启动
2. `frontend` 首页无法访问
3. admin / customer / courier 任一登录入口不可用
4. `customer onboarding` 或 `/courier/workbench` 主入口失效
5. bridge 接口被误删或误收紧
6. 运行态出现明显错误 schema / seed 漂移
7. 页面出现真实支付 / 真实退款 / 真实打款误导性行为

## 当前明确不做

1. 不提供自动化“一键数据恢复”脚本
2. 不做多节点备份编排
3. 不做异地容灾
4. 不做正式生产级监控恢复
5. 不因为补回滚文档就改 bridge

## 与当前冻结主线的关系

当前必须继续保持：

1. bridge 主线 `Phase A no-op` 冻结态
2. 展示 polish 线冻结
3. 媒体线收住

因此回滚策略也必须遵守：

1. 不删除 `/api/campus/courier/profile`
2. 不删除 `/api/campus/courier/review-status`
3. 不改 `request.js` 现有 token 附着逻辑
4. 不让回滚动作把 customer onboarding 入口回退到旧链路

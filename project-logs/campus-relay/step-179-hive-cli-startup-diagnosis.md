# Step 179 - 138/master Hive CLI 启动诊断

## 本次目标

owner 反馈“我就是直接在 138 里面执行的 hive”，需要重新按 `hive` CLI 口径排查，而不是只按 Beeline / HiveServer2 口径判断。

本轮目标：

1. 在 `192.168.121.138 / master` 上复现直接执行 `hive` 的结果。
2. 区分 `hive` CLI 可用性与 HiveServer2 `10000` 端口未启动的问题。
3. 不修改集群配置，不启动/停止 Hadoop、Hive、HBase、ZooKeeper 或 MySQL，避免影响 owner 上课环境。

## 已完成项

### 1. 138/master 上复现 Hive CLI

执行入口确认：

```text
hostname = master
HIVE_HOME = /export/servers/apache-hive-3.1.3-bin
HADOOP_HOME = /export/servers/hadoop-3.2.2
hive = /export/servers/apache-hive-3.1.3-bin/bin/hive
```

复现命令：

```bash
hive -S -e "show databases;"
```

结果：命令执行成功，返回数据库列表：

```text
default
hw_bucket_20260519
hw_import_20260519
hw_import_ky_20260519
hw_partition_20260508
ky
test
xyz
xyz2025010302138
zjsm
```

结论：`138/master` 上直接执行 `hive` CLI 当前是可用的。

### 2. 已区分 CLI 与 HiveServer2

同一轮排查中曾检查到：

- Hive metastore schema 信息可读，`schematool -dbType mysql -info` 通过。
- HDFS/YARN 已由 owner 启动，三台 DataNode / NodeManager 在线。
- `jdbc:hive2://master:10000` 连接失败的原因是 HiveServer2 未运行或 `10000` 未监听。

因此：

- 直接执行 `hive`：可用。
- 使用 `beeline -u jdbc:hive2://master:10000`：需要先启动 HiveServer2。

### 3. 启动 warning 记录

执行 `hive` 时仍出现以下 warning：

- Hadoop shell 中 `HADOOP_ORG.APACHE.HADOOP.HBASE.UTIL.GETJAVAPROPERTY_USER / OPTS` 变量名 warning。
- SLF4J multiple bindings warning。
- DataNucleus metadata warning。

这些 warning 在本轮复现中没有阻止 `hive -S -e "show databases;"` 成功执行，暂不作为 Hive CLI 阻断问题处理。

## 修改文件

本轮仅更新仓库日志：

- `project-logs/campus-relay/step-179-hive-cli-startup-diagnosis.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/agent-collaboration.md`
- `project-logs/campus-relay/global-working-memory.md`

## 服务器侧动作

- 连接 `192.168.121.138` 执行只读诊断命令。
- 执行 `hive -S -e "show databases;"` 进行 CLI 可用性验证。

本轮未做：

- 未修改服务器配置文件。
- 未启动、停止或重启 Hadoop / Hive / HBase / ZooKeeper / MySQL。
- 未修改 Hive metastore 数据。
- 未修改 HDFS 数据目录。
- 未删除旧主机名兼容别名。

## 遗留问题

1. HiveServer2 当前未确认运行；如果课程或工具使用 Beeline / JDBC，需要单独启动并验证 `master:10000`。
2. HBase 相关 Hadoop shell warning 暂未清理；它当前不是 Hive CLI 阻断项，但后续可在非上课窗口整理环境变量。
3. 仍缺少统一的 `master` 主控集群健康检查脚本和启动/停止 runbook。

## 下一步建议

1. 上课前如果只用 `hive` CLI，当前可继续使用，不建议临时大改环境。
2. 如果需要 Beeline / JDBC：

   ```bash
   cd /export/servers/apache-hive-3.1.3-bin
   nohup bin/hiveserver2 > logs/hiveserver2.out 2>&1 &
   ss -lntp | grep 10000
   beeline -u jdbc:hive2://master:10000 -e "show databases;"
   ```

3. 后续单独补一份 `master` 主控健康检查脚本，统一检查 HDFS、YARN、MySQL、Hive CLI、HiveServer2 和关键端口。

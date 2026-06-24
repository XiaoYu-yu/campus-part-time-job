# Step 177 - 三机集群环境巡检与低风险整理

日期：2026-06-24

## 本次目标

owner 暂停应用部署，要求先整理 `192.168.121.138 / 139 / 140` 三台机器的集群与环境，避免环境继续混乱，同时不能破坏 Hive 上课环境。

本轮采用“先只读巡检，再做低风险一致性修正”的方式：

1. 不重装系统。
2. 不停止、重启 Hadoop / HBase / Hive / MySQL。
3. 不删除数据目录。
4. 不重置 MySQL root。
5. 不迁移 HDFS 数据目录。
6. 只修正已明确发现且当前未运行服务不受影响的配置不一致项。

## Owner 决策

owner 明确要求：主节点和主要操作入口使用 `192.168.121.138 / hbase01`，大部分组件和日常操作都在 138 上完成。

因此本轮后续整理按以下口径收敛：

1. `hbase01` 是主控节点。
2. `hbase02 / hbase03` 主要作为 worker / 辅助节点。
3. Hive metastore、HiveServer2、MySQL、NameNode、ResourceManager 等主控能力优先放在 `hbase01`。
4. 为了简单、可记、可恢复，SecondaryNameNode 也统一回 `hbase01:50090`。

## 已完成项

### 1. 三台基础状态

- `hbase01`：`192.168.121.138`
  - 内存约 3.5GiB。
  - `/` 已扩展为约 37G，可用约 21G。
  - MySQL `8.0.45` 正在运行，监听 `3306 / 33060`。
  - 当前还有 `campus-standalone-backend / frontend` smoke 容器。
- `hbase02`：`192.168.121.139`
  - 内存约 3.5GiB。
  - `/` 约 17G，可用约 13G。
- `hbase03`：`192.168.121.140`
  - 内存约 3.5GiB。
  - `/` 约 17G，可用约 13G。

三台均：

- `firewalld` inactive。
- `chronyd` active。
- `NetworkManager` active。
- 时间同步状态正常。
- `/etc/hosts` 均包含：
  - `192.168.121.138 hbase01`
  - `192.168.121.139 hbase02`
  - `192.168.121.140 hbase03`

### 2. 当前组件分布

- 三台均有：
  - JDK `1.8.0_333`
  - Hadoop `3.2.2`
  - ZooKeeper `3.7.0`
  - Hive `3.1.3`
- 仅 `hbase01` 发现：
  - HBase `2.4.18`
  - MySQL `8.0.45`
- `hbase02 / hbase03` 未发现 HBase 安装目录。

### 3. 当前运行态

本轮巡检时三台 Java 进程均只有 `Jps`，说明 Hadoop / YARN / ZooKeeper / HBase / HiveServer2 / Metastore 均未运行。

本轮没有启动或停止任何集群组件。

### 4. 已发现的混乱点

1. HDFS 数据目录落在安装目录内：
   - `hadoop.tmp.dir=/export/servers/hadoop-3.2.2/tmp`
   - `dfs.namenode.name.dir=file:///export/servers/hadoop-3.2.2/tmp/dfs/name`
   - `dfs.datanode.data.dir=file:///export/servers/hadoop-3.2.2/tmp/dfs/data`
   - 风险：后续误清理 Hadoop 安装目录时可能误删 HDFS 数据。
2. `hbase01` 的 ZooKeeper 配置缺少三机 `server.1/2/3` 列表，而 `hbase02 / hbase03` 已有。
3. `dfs.namenode.secondary.http-address` 原本不一致：`hbase01` 指向 `hbase01:50090`，而 `hbase02 / hbase03` 指向 `hbase02:50090`。owner 后续明确 138 作为主控节点，因此最终统一为 `hbase01:50090`。
4. HBase 配置仍像单机/半成品状态：
   - `hbase.rootdir=hdfs://hbase01:9000/hbase`
   - `hbase.cluster.distributed=true`
   - `hbase.zookeeper.quorum=hbase01`
   - `regionservers=localhost`
   - 但 `hbase02 / hbase03` 没有 HBase 安装目录。
5. SSH 免密不完全对称：
   - 从 `hbase01` 到 `hbase01 / hbase02 / hbase03` 可免密。
   - 从 `hbase02 / hbase03` 到 `hbase01` 免密失败。
   - 当前如果统一从 `hbase01` 启动 Hadoop，影响较小；但运维体验不一致。

## 已执行的低风险整理

### 1. 三台配置备份

已在三台机器分别生成配置备份：

- `hbase01`：`/root/cluster-config-backup-20260624-182153.tar.gz`
- `hbase02`：`/root/cluster-config-backup-20260624-182154.tar.gz`
- `hbase03`：`/root/cluster-config-backup-20260624-182154.tar.gz`

备份覆盖：

- `/etc/hosts`
- `/etc/profile`
- `/etc/profile.d`
- Hadoop 配置目录
- ZooKeeper 配置目录
- Hive 配置目录
- `hbase01` 上的 HBase 配置目录

### 2. 修正 hbase01 ZooKeeper 配置

文件：

- `/export/servers/apache-zookeeper-3.7.0-bin/conf/zoo.cfg`

备份：

- `/export/servers/apache-zookeeper-3.7.0-bin/conf/zoo.cfg.bak_step177`

修正后关键值：

```properties
dataDir=/export/data/zookeeper/zkdata
server.1=hbase01:2888:3888
server.2=hbase02:2888:3888
server.3=hbase03:2888:3888
```

### 3. 按 138 主控口径统一 SecondaryNameNode 指向

文件：

- `hbase01 / hbase02 / hbase03` 的 `/export/servers/hadoop-3.2.2/etc/hadoop/hdfs-site.xml`

备份：

- `/export/servers/hadoop-3.2.2/etc/hadoop/hdfs-site.xml.bak_step177`
- `/export/servers/hadoop-3.2.2/etc/hadoop/hdfs-site.xml.bak_step177_primary138`

修正后：

```xml
<name>dfs.namenode.secondary.http-address</name>
<value>hbase01:50090</value>
```

### 4. 修正后复核

三台均确认：

- ZooKeeper `server.1/2/3` 一致。
- `dfs.namenode.secondary.http-address` 一致为 `hbase01:50090`。
- Java 进程仍只有 `Jps`，没有因本轮操作启动或停止集群服务。

## 当前建议启动顺序

如果只是为了 Hive 上课使用，建议后续只启动必要链路：

1. 在三台启动 ZooKeeper。
2. 在 `hbase01` 启动 HDFS：
   - NameNode：`hbase01`
   - DataNode：`hbase01 / hbase02 / hbase03`
   - SecondaryNameNode：`hbase01`
3. 必要时启动 YARN：
   - ResourceManager：`hbase01`
   - NodeManager：三台
4. 在 `hbase01` 启动 Hive Metastore / HiveServer2。
5. 暂不启动 HBase，除非先完成 HBase 三机安装/配置整理。

## 遗留问题

### P0 - 启动前需要注意

1. HDFS 数据目录仍在 Hadoop 安装目录下，本轮未迁移。
2. 当前没有确认 HDFS / Hive 数据是否完整可用，因为本轮没有启动集群。
3. Hive metastore 依赖 `hbase01:3306/metastore`，但本轮没有读取或修改 MySQL 凭据。

### P1 - 建议下一轮整理

1. 建立只读健康检查脚本，统一输出：
   - 三台 JPS。
   - 关键端口。
   - HDFS safemode。
   - HDFS report。
   - Hive beeline 连接测试。
2. 修正 SSH 免密不对称问题。
3. 给 Hadoop / ZooKeeper / Hive 写清楚启动和停止 runbook。
4. 将 HDFS 数据目录从 `/export/servers/hadoop-3.2.2/tmp` 迁移到 `/export/data/hadoop`，但必须安排停机窗口并保留完整备份。

### P2 - HBase 后续方案

当前 HBase 不建议直接启动成三机集群，因为：

1. 只有 `hbase01` 有 HBase 安装目录。
2. `regionservers` 仍为 `localhost`。
3. `hbase.zookeeper.quorum` 仍为 `hbase01`，不是三机 ensemble。

如果确实需要 HBase 三机：

1. 先将 HBase 安装和配置同步到 `hbase02 / hbase03`。
2. 修正 `hbase.zookeeper.quorum=hbase01,hbase02,hbase03`。
3. 修正 `regionservers`。
4. 在 HDFS / ZooKeeper 正常后再启动 HBase。

## 本轮未做

- 未重装任何机器。
- 未停止、启动或重启 Hadoop / Hive / HBase / ZooKeeper / MySQL。
- 未删除任何数据目录。
- 未修改 MySQL root 或 Hive metastore 数据库。
- 未迁移 HDFS 数据。
- 未启动 HBase。
- 未修改项目应用容器。

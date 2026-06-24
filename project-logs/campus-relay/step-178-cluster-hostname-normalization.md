# Step 178 - 集群主机名规范化

日期：2026-06-24

## 本次目标

owner 要求把三台机器中所有“hbase01 / hbase02 / hbase03”这类不合适的主机名改成正常命名，并同步修改主机名变更后的关联配置，例如 `/etc/hosts`、Hadoop、ZooKeeper、Hive、HBase 配置。

本轮继续遵守安全边界：

1. 不启动、停止或重启 Hadoop / Hive / HBase / ZooKeeper / MySQL。
2. 不删除数据目录。
3. 不改 LVM / GRUB / fstab 这类启动关键项。
4. 不改 HDFS 数据块、Hive 历史作业文件、旧备份文件中的历史字符串。

## 命名结果

| IP | 旧名 | 新名 | 角色口径 |
| --- | --- | --- | --- |
| `192.168.121.138` | `hbase01` | `master` | 主控节点，日常操作入口 |
| `192.168.121.139` | `hbase02` | `worker01` | worker / 辅助节点 |
| `192.168.121.140` | `hbase03` | `worker02` | worker / 辅助节点 |

## 已完成项

### 1. 系统 hostname

已修改三台 `/etc/hostname` 与运行态 hostname：

- `192.168.121.138`：`master`
- `192.168.121.139`：`worker01`
- `192.168.121.140`：`worker02`

### 2. `/etc/hosts`

三台 `/etc/hosts` 已统一为新名优先、旧名兼容别名：

```text
127.0.0.1 localhost localhost.localdomain
::1 localhost localhost.localdomain
192.168.121.138 master hbase01
192.168.121.139 worker01 hbase02
192.168.121.140 worker02 hbase03
```

`master` 上额外保留原有：

```text
103.198.200.200 repo.huaweicloud.com
```

保留旧别名的原因：

1. HDFS 数据块和 Hive 历史作业中已存在 `hdfs://hbase01:9000/...` 等历史 URI。
2. Hive metastore 里可能也保存了旧 URI。
3. 直接删除旧别名可能导致历史 Hive 表或历史作业路径解析失败。

因此当前策略是：新配置一律使用 `master / worker01 / worker02`，旧名只作为兼容 DNS 别名兜底。

### 3. Hadoop 配置

三台已同步修改：

- `core-site.xml`
  - `fs.defaultFS=hdfs://master:9000`
- `yarn-site.xml`
  - `yarn.resourcemanager.hostname=master`
- `hdfs-site.xml`
  - `dfs.namenode.secondary.http-address=master:50090`
- `workers`
  - `master`
  - `worker01`
  - `worker02`

### 4. ZooKeeper 配置

三台已同步修改 `zoo.cfg`：

```properties
server.1=master:2888:3888
server.2=worker01:2888:3888
server.3=worker02:2888:3888
```

### 5. Hive 配置

`master` 上已修改 Hive metastore 连接地址：

```text
jdbc:mysql://master:3306/metastore?...
```

本轮没有读取或修改 MySQL 密码，也没有修改 Hive metastore 数据库内容。

### 6. HBase 配置

`master` 上已修改当前 HBase 配置：

- `hbase.rootdir=hdfs://master:9000/hbase`
- `hbase.zookeeper.quorum=master`

注意：当前仍不建议直接启动 HBase 三机，因为 `worker01 / worker02` 尚未发现 HBase 安装目录，且 `regionservers` 仍需单独整理。

### 7. Chrony 注释

`worker01 / worker02` 的 `/etc/chrony.conf` 注释已从 `hbase01` 改为 `master`。

## 备份位置

本轮改名前已在三台生成备份：

- `master`：`/root/cluster-rename-backup-20260624-182620.tar.gz`
- `worker01`：`/root/cluster-rename-backup-20260624-182642.tar.gz`
- `worker02`：`/root/cluster-rename-backup-20260624-182642.tar.gz`

另外每个被修改的配置文件均带有 `bak_step178_*` 或 `bak_step178` 备份。

## 复核结果

### 1. 当前生效配置旧名残留

已扫描以下当前生效配置：

- `/etc/hostname`
- `/etc/hosts`
- `/etc/chrony.conf`
- `/etc/profile`
- `/etc/profile.d`
- Hadoop 配置目录
- ZooKeeper 配置目录
- Hive 配置目录
- `master` 上的 HBase 配置目录

结果：

- 除 `/etc/hosts` 中保留的旧名兼容别名外，没有 `hbase01 / hbase02 / hbase03` 残留。

### 2. 关键配置复核

- `fs.defaultFS=hdfs://master:9000`
- `yarn.resourcemanager.hostname=master`
- `dfs.namenode.secondary.http-address=master:50090`
- `workers=master,worker01,worker02`
- `zoo.cfg=master,worker01,worker02`
- Hive JDBC：`jdbc:mysql://master:3306/metastore`

### 3. 运行态复核

三台 `jps -lm` 仍只有 `Jps`，说明本轮没有误启动、停止或重启任何 Java 集群服务。

## 明确不改的旧名

以下位置出现 `hbase01` 等旧字符串，但本轮明确不改：

1. `/etc/fstab`、`/etc/default/grub`、`/etc/kernel/cmdline` 中的 `cs_hbase01`：
   - 这是 LVM volume group 名称，不是普通主机名。
   - 改错会导致系统无法启动。
2. `/etc/lvm/archive/*`：
   - LVM 历史归档，不能当普通文本批量改。
3. `/export/servers/hadoop-3.2.2/tmp/dfs/...`：
   - HDFS 数据块、元数据、历史 job 配置。
   - 直接文本替换可能破坏 HDFS 数据。
4. `.bak*`、历史备份、日志文件：
   - 保留原样用于回滚和审计。

## 后续建议

1. 下一轮写一个 `master` 主控的集群健康检查脚本。
2. 再写一份按新命名的启动/停止 runbook。
3. 如果要彻底删除 `/etc/hosts` 里的旧别名，需要先确认 Hive metastore 表路径和 HDFS 历史路径已迁移，不建议现在删除。
4. HBase 若要正常化，需要先决定：
   - 单机 HBase on `master`；
   - 或完整三机 HBase，并把 HBase 安装同步到 `worker01 / worker02`。

## 本轮未做

- 未启动、停止或重启 Hadoop / Hive / HBase / ZooKeeper / MySQL。
- 未删除任何数据。
- 未修改 MySQL root、Hive metastore 数据库或 HDFS 数据块。
- 未修改 LVM / GRUB / fstab。
- 未删除旧主机名兼容别名。

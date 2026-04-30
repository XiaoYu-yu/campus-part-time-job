# 单机内测 SSH 运维入口硬化说明

## 目标

单机内测阶段需要保留 SSH 作为运维入口，但不应长期依赖任意来源的密码登录。

本说明用于固定当前可执行的 SSH 硬化顺序，避免直接关闭密码登录导致服务器锁死。

## 当前结论

1. 使用显式 identity 文件可完成 key-based SSH 登录。
2. 默认 `ssh root@host` 未必会自动选择项目专用 key，因此可能失败。
3. password login 当前仍保留，作为 key 登录确认后的备用回滚入口。
4. 本轮没有修改服务器 `sshd_config`。
5. 本轮没有关闭 password login。

## 推荐登录方式

使用项目专用 key 显式登录：

```bash
ssh -i ~/.ssh/campus_trial_ed25519 -o IdentitiesOnly=yes root@<internal-trial-host>
```

说明：

1. `<internal-trial-host>` 应替换为真实服务器地址。
2. 不要把真实服务器 IP、密码或私钥内容写入仓库。
3. 如果希望简化命令，可在本机 `~/.ssh/config` 增加本地 alias，但该配置不进入仓库。

## 安全组建议

长期内测建议在云控制台限制 SSH `22` 来源 IP：

1. 仅允许 owner 当前可信公网出口访问 `22`。
2. 保留业务入口 `80` 对外。
3. backend `8080` 和 MySQL `3306` 继续只允许服务器本机访问。
4. 修改安全组前先确认当前公网出口 IP，避免误封自己。

## 禁止直接执行的动作

在未完成 key 登录复核前，不要执行：

```bash
PasswordAuthentication no
systemctl reload sshd
```

原因：

1. 如果 key 登录命令或安全组配置有误，可能直接失去服务器运维入口。
2. 当前内测阶段更重要的是保持可恢复性，而不是追求一次性安全收口。

## 建议执行顺序

1. 使用显式 identity 验证 key 登录。
2. 新开一个终端保持当前 SSH 会话不关闭。
3. 在云控制台限制 SSH `22` 来源 IP。
4. 从允许来源重新验证 key 登录。
5. 再评估是否关闭 password login。
6. 如果任一步失败，回滚安全组来源限制，保持 password login。

## 回滚策略

如果限制 SSH 来源后无法登录：

1. 立即在云控制台恢复 `22` 来源到临时开放或原配置。
2. 不修改服务器 `sshd_config`。
3. 重新验证显式 key 登录命令。
4. 记录失败原因后再重试安全组收紧。

## 当前不做

1. 不关闭 password login。
2. 不修改 `sshd_config`。
3. 不安装额外 SSH 管理工具。
4. 不把私钥、公网 IP、服务器密码写入仓库。
5. 不把 SSH 硬化和业务部署变更混在同一动作里。

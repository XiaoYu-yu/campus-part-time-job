# Step 143 - SSH 运维入口硬化清单

## 本轮目标

1. 在不破坏当前服务器可登录状态的前提下，确认 key-based SSH 运维入口。
2. 固化 SSH `22` 安全组收紧建议和回滚边界。
3. 不改业务代码、不改 bridge、不改鉴权、不改接口、不改路由。

## 为什么先做 SSH 运维入口硬化

Step 142 已完成服务器日志轮转部署与远端 smoke 验证。

当前服务器内测链路已经具备：

1. frontend `80` 作为公网业务入口。
2. backend `8080` 与 MySQL `3306` 仅服务器本机绑定。
3. health endpoint。
4. remote smoke。
5. backup 与 restore drill。
6. Docker 日志轮转。

剩余较直接的运维风险是 SSH `22` 仍作为公网运维入口存在。该风险不应该通过直接关闭密码登录来解决，而应先确认 key 登录，再由 owner 在云安全组限制来源 IP。

## 实际验证结果

### 1. 默认 key 登录复核

执行默认 key 登录时失败：

```text
ssh -o BatchMode=yes root@<internal-trial-host>
Permission denied (publickey,password).
```

结论：

1. 默认 SSH 客户端没有自动选择项目专用 key。
2. 不能因为服务器已有公钥就直接关闭 password login。

### 2. 服务器 authorized_keys 复核

通过 password login 进入服务器后检查项目专用公钥：

```text
authorized_key_status=already_present
authorized_keys_lines=1
```

结论：

1. 项目专用公钥已经存在于服务器 `authorized_keys`。
2. 本轮没有新增重复公钥。
3. 本轮没有修改 `sshd_config`。

### 3. 显式 identity key 登录复核

使用显式 identity 登录成功：

```text
ssh -i ~/.ssh/campus_trial_ed25519 -o IdentitiesOnly=yes root@<internal-trial-host>
key-auth-ok
```

结论：

1. key-based SSH 实际可用。
2. 后续应使用显式 identity 或本机 SSH alias 登录。
3. 关闭 password login 前必须再次用显式 identity 验证。

## 新增文档

新增：

- `docs/deployment/internal-trial-ssh-hardening.md`

文档明确：

1. 推荐 key 登录命令。
2. 云安全组限制 SSH 来源 IP 的建议。
3. 不直接关闭 password login 的原因。
4. 禁止直接执行的危险动作。
5. 回滚策略。

## 明确没有改动

本轮没有修改：

1. Java 业务代码。
2. Vue 页面。
3. `request.js`。
4. token 附着逻辑。
5. bridge 相关接口。
6. 后端鉴权。
7. 路由。
8. 数据库表结构。
9. 旧兼容模块。
10. 服务器 `sshd_config`。

本轮没有关闭 password login。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。

本轮没有删除、收紧或替换：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `/api/campus/courier/auth/token`

## 下一轮建议

Step 144 建议二选一：

1. 由 owner 在云控制台按 `docs/deployment/internal-trial-ssh-hardening.md` 限制 SSH `22` 来源 IP，然后复核显式 key 登录。
2. 如果暂不动云安全组，转入“服务器部署后验证清单脚本化”，把 backup、pull、compose rebuild、health、remote smoke、LogConfig 检查固定成可重复执行步骤。

Step 144 已完成补充：

1. 新增 `scripts/trial-operation/server-post-deploy-check.ps1`。
2. 该脚本已真实串起 remote smoke 与 key-based SSH 只读检查。
3. 当前不修改云安全组、不关闭 password login。

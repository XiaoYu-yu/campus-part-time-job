# Step 97 - 全局工作记忆日志建立

## 本轮目标

1. 为后续长周期开发增加一份“上下文压缩后也能快速恢复”的全局工作记忆。
2. 把 `summary.md` 的全量历史和“当前还在生效的状态”拆开。
3. 不改业务代码，不改部署逻辑，不改 bridge，不改页面。

## 为什么要做这件事

当前项目已经推进到：

1. bridge 冻结态
2. 展示 polish 冻结态
3. 媒体线收住
4. 单机服务器首轮内测部署已跑通

这意味着后续会话的上下文成本主要来自“历史太长”，不是“功能还不够”。

`summary.md` 适合作为完整总史书，但不适合作为每次恢复工作的第一入口。因此本轮补一份更短的全局工作记忆是合理的。

## 实际完成项

### 1. 新增全局工作记忆

新增：

- `project-logs/campus-relay/global-working-memory.md`

该文件只保留仍然生效的核心状态：

1. bridge 当前为什么冻结
2. 展示 polish 当前为什么冻结
3. 媒体与交付线当前为什么收住
4. 当前已经稳定的主链路和非 bridge 后端线
5. 当前部署与试运营状态
6. 当前下一主线
7. 后续恢复工作时应先看什么、先跑什么

### 2. 与现有日志分工明确

本轮明确：

1. `summary.md` 继续记录完整阶段历史
2. `pending-items.md` 继续记录当前下一优先级
3. `global-working-memory.md` 只记录“现在仍然有效的状态和恢复入口”

## 本轮明确没做

1. 没有改业务代码
2. 没有改部署脚本
3. 没有改 bridge
4. 没有改鉴权
5. 没有改页面
6. 没有改接口

## 修改文件

1. `project-logs/campus-relay/global-working-memory.md`
2. `project-logs/campus-relay/summary.md`
3. `project-logs/campus-relay/pending-items.md`
4. `project-logs/campus-relay/file-change-list.md`
5. `project-logs/campus-relay/step-97-global-working-memory-bootstrap.md`

## 当前 bridge 结论

1. bridge 继续保持 `Phase A no-op` 冻结态
2. 当前最终结论仍不是“bridge 已可删除”

## 下一轮建议

1. 继续按 Step 97 之后的主线推进单机服务器内测运维加固
2. 优先补最小回滚、备份和环境收敛说明
3. 后续如果上下文压缩，先读：
   - `global-working-memory.md`
   - `pending-items.md`
   - 最新 step 日志

# 全局工作记忆

## 用途

这份文件不是完整历史日志，而是给后续继续开发时的“快速恢复上下文入口”。

当上下文压缩、会话切换或阶段跨度过大时，优先先读本文件，再读：

1. `project-logs/campus-relay/pending-items.md`
2. `project-logs/campus-relay/summary.md`
3. 当前最新 step 日志

## 维护规则

1. 只记录当前阶段真正还在生效的状态、冻结线和下一主线。
2. 不重复抄整本 `summary.md`。
3. 不写真实密码、密钥、token、数据库密码或服务器凭据。
4. 当前主线变化、冻结状态变化、部署状态发生变化时，再更新本文件。

## 当前项目状态

当前项目已经达到：

1. 本地可完整演示
2. 本地 / H2 可完整联调
3. GitHub 最小 CI 可运行
4. 单机服务器可启动、可访问、可做内测型试运营 smoke

当前不是最终产品级上线版，而是：

1. 最小闭环
2. 可演示
3. 可讲解
4. 可交接
5. 可做单机服务器内测型试运营

## 当前主链路结论

### 1. bridge 主线

当前状态：

1. `Phase A no-op` 冻结态
2. 完全保留，不删除

保持不动项：

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `request.js` 现有 token 附着逻辑
4. `customer/courier-onboarding/*` 现有前置入口
5. `CourierWorkbench.vue` 现有优先 `courier_token` 策略

默认不要做：

1. 不继续找 bridge 收口候选
2. 不改鉴权
3. 不删接口

### 2. 展示 polish 主线

当前状态：

1. 冻结 / 维护态

已经完成第一轮 polish 的关键页：

1. `frontend/src/views/user/CourierOnboarding.vue`
2. `frontend/src/views/courier/CourierWorkbench.vue`
3. `frontend/src/views/user/CampusOrderResult.vue`
4. `frontend/src/views/CampusSettlementOpsView.vue`
5. `frontend/src/views/CampusAfterSaleExecutionList.vue`

默认不要做：

1. 不继续机械 polish 页面
2. 不补第五个 admin 页

### 3. 媒体与交付线

当前状态：

1. 已完成交付整理
2. 已完成演示脚本
3. 已完成截图 / 录屏清单
4. 已完成真实媒体采集
5. 当前媒体线已收住

默认不要做：

1. 不继续机械补截图
2. 不继续机械补录屏

## 当前已稳定的核心能力

### customer / courier / admin 最小闭环

已稳定覆盖：

1. customer onboarding
2. admin 审核
3. customer 申请 courier token
4. courier workbench
5. 接单
6. 取餐
7. deliver
8. 异常上报
9. customer confirm
10. completed 回读
11. customer 结果页
12. admin settlement 只读页
13. admin after-sale 只读页
14. admin courier ops 页
15. admin exception 历史 / resolve 最小闭环

### 非 bridge 后端线

当前已完成最小闭环：

1. 异常历史与最小 resolve
2. 售后执行历史
3. settlement 批次操作审计
4. settlement 对账差异记录

### 试运营与部署线

当前已完成：

1. `.env.example` / 密钥边界
2. 部署前 preflight 文档
3. 部署后 smoke checklist
4. 最小 GitHub Actions CI
5. Compose 单机部署包
6. 单机服务器首轮真实部署与 smoke

## 当前最新部署结论

当前已知稳定结论：

1. 单机服务器 Compose 栈已真实拉起
2. `mysql / backend / frontend` 三容器已验证可运行
3. admin / customer / courier 三类最小 smoke 已通过
4. 单机服务器最小备份入口已真实跑通一轮
5. 服务器已生成 MySQL dump、uploads 归档、`.env` 备份和 manifest
6. 单机服务器最小 restore drill 已真实跑通一轮
7. dump 已恢复到临时 MySQL 容器并验证关键样本订单可回读
8. 单机服务器运维 runbook 已建立，启动、停机、日志、更新、备份、restore drill、smoke 与正式入口 go/no-go 已集中到一处

当前已确认的部署层修正：

1. MySQL 8.4 不再使用旧的 `mysql_native_password` 启动参数
2. `backend/db/init.sql` 中 `campus_relay_order` 种子已与当前字段保持一致

注意：

1. 服务器地址、密码、地图 key、JWT secret、数据库密码都不要写进仓库
2. 如需继续部署操作，只在本地终端或服务器环境变量中使用这些敏感信息

## 当前下一主线

默认下一主线：

1. 内测运行观察与 HTTPS / 域名触发条件跟踪

优先级建议：

1. 如果近期仍是 owner 自测，先按 runbook 做日常内测，不继续扩功能
2. 如果准备邀请外部用户长期访问，再进入 HTTPS / 域名 / 正式反向代理准备
3. 如果服务器发生异常，优先按 runbook 做备份、restore drill、smoke 和回滚判断
4. 若无新触发条件，不重开 bridge、页面 polish 或第五个 admin 页

当前默认不进入：

1. bridge 收口执行
2. 页面继续 polish
3. 第五个 admin 页
4. 真实支付 / 真实退款 / 真实打款
5. 大范围产品级功能扩张

## 恢复工作时的最小入口

如果后续会话需要快速恢复，建议按这个顺序：

1. 看 `project-logs/campus-relay/global-working-memory.md`
2. 看 `project-logs/campus-relay/pending-items.md`
3. 看最新 step 日志
4. 执行：
   - `git status -sb`
   - `git log --oneline -5`
5. 若继续服务器运维线，再检查：
   - 服务器 compose 状态
   - 最近一次 smoke 是否还有效

## 当前结论

当前项目不是“还在早期拼功能”，而是已经进入：

1. 主链路稳定
2. bridge 冻结
3. 展示线冻结
4. 媒体线收住
5. 试运营部署开始真实落地

因此后续优先级应以：

1. 运维加固
2. 回滚与备份
3. 试运营稳定性

为主，而不是回到“继续补页面”。

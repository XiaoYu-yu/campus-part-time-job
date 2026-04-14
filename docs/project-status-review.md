# 项目状态检查

检查时间：2026-04-14

## 当前结论

项目已经从原苍穹外卖训练项目，推进为以校园代送为主线的试运营版。旧外卖模块仍保留可运行，但当前交付和演示主线是 campus 领域。

当前状态可以概括为：

1. customer / courier / admin 三端最小闭环已具备。
2. 本地 H2/test 环境可用于演示和回归。
3. admin 只读运营页已覆盖 settlement、after-sale、courier ops、exception 等核心观察面。
4. 异常历史和售后执行历史已具备最小审计能力。
5. bridge 主线和展示 polish 主线均已冻结。
6. 当前仍不是完整商业生产版，真实支付、真实地图、真实打款等外部能力未接入。

## 已完成主能力

### customer

- 登录
- 校园代送下单与模拟支付
- onboarding 资料提交
- 审核状态和 token 资格查看
- courier token 申请
- 售后结果回看
- completed 结果回看

### courier

- courier token workbench
- 可接单列表
- 接单
- 取餐
- deliver
- 异常上报
- completed 后只读回读

### admin

- courier 审核与运营查看
- settlement 单笔、批次、对账摘要查看
- settlement 打款结果记录与二次核对
- after-sale 执行分页
- after-sale 执行历史只读 drawer
- exception 历史列表、详情、resolve

## 文档状态

当前文档入口已经调整为：

- 根 [README](../README.md)
- [docs/README.md](README.md)
- [project-logs/campus-relay/summary.md](../project-logs/campus-relay/summary.md)

旧外卖阶段文档已归档到：

- [docs/legacy-takeaway/](legacy-takeaway/README.md)
- [project-logs/legacy-takeaway/](../project-logs/legacy-takeaway/summary.md)

## 主要风险

1. 外部公司能力未接入：支付、退款、打款、地图、消息。
2. settlement 仍停留在模拟打款和运营记录层面，不是真实财务系统。
3. 异常处理已具备最小 resolve，但未扩展为完整工单平台。
4. 旧外卖模块仍保留，后续如果要产品化，需要明确是否继续维护。
5. 生产级限流、监控、告警、审计和安全加固仍未作为正式上线能力完成。

## 建议下一阶段

1. 若目标是试运营演示，优先继续完善 settlement 批次操作审计。
2. 若目标是产品级上线，先补生产级安全、部署、监控和真实外部能力接入方案。
3. 不建议继续机械新增页面或无限 polish。

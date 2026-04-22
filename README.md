# 校内兼职平台 / 校园代送试运营版

![Vue](https://img.shields.io/badge/Vue-3-42b883?logo=vue.js&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-6DB33F?logo=springboot&logoColor=white)
![JDK](https://img.shields.io/badge/JDK-17-007396?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL%20%2F%20MariaDB-supported-003545?logo=mysql&logoColor=white)
![H2](https://img.shields.io/badge/H2-test%20profile-blue)

本项目由苍穹外卖风格项目增量改造而来。当前主线是“校园代送 / 校内兼职配送”试运营闭环，旧外卖模块仍保留可运行，不作为当前演示主线。

当前交付定位：

- 可本地启动
- 可完整演示 customer / courier / admin 三端闭环
- 可使用 H2/test 数据完成主链路联调
- 支付、地图、真实打款等外部公司能力暂用模拟数据或只读记录代替
- bridge 主线处于 `Phase A no-op` 冻结态，不删除旧兼容接口
- 模拟支付、模拟退款、模拟打款和 settlement 只作为试运营状态与审计记录，不代表真实资金流

## 当前核心能力

### customer

- 校园代送下单与模拟支付
- courier onboarding 资料提交、审核状态查看、token 资格判断与 token 申请
- 售后结果回看
- completed 结果回看页

### courier

- courier workbench
- 可接单列表
- 接单
- 取餐
- deliver
- 异常上报
- 送达后等待确认与 completed 只读回读

### admin

- courier 审核与运营查看
- settlement 单笔、批次、对账摘要只读运营页
- after-sale 执行分页与执行历史 drawer
- 异常历史列表、详情与最小 resolve 处理
- 旧外卖后台基础页面仍保留

## 明确边界

当前版本不是完整商业生产版。以下能力仍不接真实外部服务：

- 真实支付
- 真实退款
- 真实打款
- 轨迹回放、路线规划、实时调度和导航
- 消息推送
- 生产级风控、限流、审计平台

这些能力如果需要上线，应作为独立阶段接入，不应混入当前试运营闭环。

模拟资金链路的详细边界见：

- [模拟资金链路产品化边界](docs/simulated-funds-boundary.md)

## 技术栈

- Frontend: `Vue 3`、`Vite`、`Vue Router`、`Pinia`、`Element Plus`、`Axios`
- Backend: `Spring Boot 3.2.4`、`MyBatis`、`JWT`、`H2`、`MySQL/MariaDB`
- Runtime: `JDK 17`、`Node.js 18+`

## 目录结构

```text
Campus part-time job/
├── backend/                         Spring Boot 后端
├── frontend/                        Vue 3 前端
├── docs/                            当前交付、API、数据库、部署文档
│   ├── campus-relay/                校园代送规划与映射
│   ├── deployment/                  部署、备份和回滚说明
│   └── legacy-takeaway/             旧外卖阶段参考文档归档
├── project-logs/
│   ├── campus-relay/                当前校园代送主线日志
│   └── legacy-takeaway/             旧外卖修复日志归档
├── AGENTS.md                        协作约束
└── README.md                        当前项目入口
```

## 快速启动

### 后端

```powershell
cd backend
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

后端默认地址：

- `http://localhost:8080`

### 前端

```powershell
cd frontend
npm install
npm run dev
```

前端默认地址：

- `http://localhost:5173`

## 内测型试运营部署

如果你准备把当前试运营版部署到单机服务器做内测，而不是正式生产上线，优先看：

- [内测型试运营 Compose 部署说明](docs/deployment/internal-trial-compose.md)
- [环境变量与密钥配置清单](docs/deployment/env-and-secret-checklist.md)
- [部署后 Smoke Checklist](docs/deployment/post-deploy-smoke-checklist.md)
- [单机内测试运营运维 Runbook](docs/deployment/internal-trial-ops-runbook.md)

## 常用演示账号

| 角色 | 手机号 | 密码 | 说明 |
| --- | --- | --- | --- |
| admin | `13800138000` | `123456` | 管理后台 |
| customer | `13900139000` | `123456` | 用户端与 onboarding |
| courier | 通过 customer onboarding 申请 | `123456` | 使用 courier token 进入 workbench |

典型样本订单与媒体证据见：

- [Step 40 交付整理](project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md)
- [Step 42 媒体归档](project-logs/campus-relay/step-42-real-media-capture-and-archive.md)
- [试运营运行配置与 Preflight 手册](docs/trial-operation-preflight.md)

## 关键页面

### customer

- `/user/campus/courier-onboarding`
- `/user/campus/order-result`
- `/user/campus/after-sale-result`

### courier

- `/courier/workbench`

### admin

- `/campus/settlement-batches`
- `/campus/settlements`
- `/campus/after-sale-executions`
- `/campus/courier-ops`
- `/campus/exceptions`

## 验证命令

```powershell
cd backend
.\mvnw.cmd -DskipTests compile
```

```powershell
cd frontend
npm run build
```

更完整的历史运行态验证见：

- [校园代送总览](project-logs/campus-relay/summary.md)
- [待处理事项](project-logs/campus-relay/pending-items.md)
- [文件改动清单](project-logs/campus-relay/file-change-list.md)

## 文档入口

- [文档索引](docs/README.md)
- [交付与启动说明](docs/delivery-guide.md)
- [试运营运行配置与 Preflight 手册](docs/trial-operation-preflight.md)
- [模拟资金链路产品化边界](docs/simulated-funds-boundary.md)
- [项目状态检查](docs/project-status-review.md)
- [API 总览](docs/api-overview.md)
- [数据库总览](docs/db-overview.md)
- [旧外卖文档归档](docs/legacy-takeaway/README.md)

## License

This project is licensed under the [MIT License](LICENSE).

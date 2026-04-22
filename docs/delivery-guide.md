# 交付与启动说明

## 交付定位

当前版本是“校园代送试运营版”，目标是本地可运行、可演示、可交接。真实支付、真实退款、真实打款、消息推送等外部公司能力暂不接入，均以模拟数据、只读记录或后续候选方式保留。腾讯地图已在 admin courier ops 单页完成 JS SDK 只读点位预览，不做轨迹、导航、路线规划或实时调度。

模拟支付、模拟退款、模拟打款、settlement 批次操作和对账差异的详细产品化边界见：

- [模拟资金链路产品化边界](simulated-funds-boundary.md)

## 本地启动方式

### 后端 H2/test

```powershell
cd D:\20278\code\Campus part-time job\backend
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

适用场景：

- 本地演示
- H2 样本数据验证
- 不依赖本机 MySQL/MariaDB

### 后端 dev

```powershell
cd D:\20278\code\Campus part-time job\backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

适用场景：

- 使用本机 MySQL/MariaDB 数据库
- 长期开发调试

### 前端

```powershell
cd D:\20278\code\Campus part-time job\frontend
npm install
npm run dev
```

前端默认地址：

- `http://localhost:5173`

## 默认账号

| 角色 | 手机号 | 密码 | 说明 |
| --- | --- | --- | --- |
| admin | `13800138000` | `123456` | 管理后台 |
| customer | `13900139000` | `123456` | 用户端、onboarding |
| courier | 通过 onboarding 申请 token | `123456` | courier workbench |

## 主演示链路

1. customer 打开 `/user/campus/courier-onboarding`。
2. customer 提交 courier 资料并查看审核状态。
3. admin 审核 courier。
4. customer 申请 courier token。
5. courier 打开 `/courier/workbench`。
6. courier 接单、取餐、deliver、异常上报。
7. customer confirm 后通过 `/user/campus/order-result` 回看 completed 结果。
8. admin 打开 settlement、after-sale、exception 等只读运营页。

详细脚本见：

- [Step 40 交付整理与演示脚本](../project-logs/campus-relay/step-40-delivery-packaging-and-demo-script.md)
- [Step 41 截图和录屏计划](../project-logs/campus-relay/step-41-delivery-assets-checklist-and-recording-plan.md)
- [Step 42 媒体归档](../project-logs/campus-relay/step-42-real-media-capture-and-archive.md)
- [内测型试运营 Compose 部署说明](deployment/internal-trial-compose.md)
- [试运营运行配置与 Preflight 手册](trial-operation-preflight.md)
- [模拟资金链路产品化边界](simulated-funds-boundary.md)

## 验证命令

```powershell
cd D:\20278\code\Campus part-time job\backend
.\mvnw.cmd -DskipTests compile
```

```powershell
cd D:\20278\code\Campus part-time job\frontend
npm run build
```

如涉及前端 lint/test，再额外执行：

```powershell
npm run lint
npm run test
```

## 当前冻结线

1. bridge 主线：`Phase A no-op` 冻结态。
2. 展示 polish 线：冻结/维护态。
3. 媒体线：已收住。

冻结不代表删除或废弃，只表示当前稳定闭环下不再默认继续改动。

## 当前不交付内容

1. 真实支付。
2. 真实退款。
3. 真实打款。
4. 轨迹回放、路线规划、实时调度和导航。
5. 完整财务后台。
6. 完整异常工单系统。
7. 完整生产监控、告警、限流和安全风控。

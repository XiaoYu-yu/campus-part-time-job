# Step 165 - Python Android 双端动作链矩阵

## 本轮目标

把 Step 164 仍未自动化覆盖的双端动作链，沉淀成可重复执行的 Python 脚本和真实手机留痕。

本轮重点不是继续开发功能，而是验证当前用户端、兼职端和后端公开 API 在真实 Android 设备 + 内测服务器环境下能否串成完整订单动作链：

1. 用户端登录。
2. 创建校园代送订单。
3. 模拟支付。
4. 兼职端登录。
5. 兼职端读取资料、审核状态和可接任务。
6. 接单。
7. 确认取餐。
8. 开始配送。
9. 上报异常。
10. 确认送达。
11. 用户端确认收货。
12. 用户端和兼职端同时回读 `COMPLETED`。

## 为什么用 Python

PowerShell smoke 脚本已经能覆盖基础接口和截图，但继续扩动作链会让脚本可读性变差。Python 更适合承载完整矩阵：

- 用标准库 `urllib` 调接口，不新增依赖。
- 用 `subprocess` 调 ADB，保留真实设备启动和截图证据。
- 统一输出 JSON 报告，方便下一轮继续复跑或比对。
- 报告默认脱敏 API host，不写 token。

## 新增脚本

- `scripts/trial-operation/android_action_matrix.py`

脚本能力：

- 自动读取 `frontend/.env.android-user-public` 中的 `VITE_API_BASE_URL`，也支持 `--api-base` 显式传入。
- 自动定位 `adb.exe`，也支持 `--adb-path` 显式传入。
- 支持 `--device-id` 指定真机。
- 支持 `--output-dir` 指定 runtime 证据目录。
- 报告中只记录脱敏 API base，不保存 customer / courier token。

## 实际执行命令

```powershell
py scripts\trial-operation\android_action_matrix.py --device-id 10AE221PGA003Y5 --output-dir project-logs\campus-relay\runtime\step-165-android-action-matrix
```

## 真实执行结果

- 执行结果：通过。
- ADB 真机：`10AE221PGA003Y5` 在线。
- 本轮创建订单：`CR202605131124021644`。
- 支付结果：`PAID`。
- 兼职资料：`profileId = 2`，审核状态 `APPROVED`。
- 可接任务读取：通过，本轮创建订单出现在首屏可接单列表。
- 接单后状态：`ACCEPTED`。
- 取餐后状态：`PICKED_UP`。
- 第一次送达接口后状态：`DELIVERING`。
- 异常上报：通过，`exceptionType = 联系不上`。
- 第二次送达接口后状态：`AWAITING_CONFIRMATION`。
- 用户端确认后状态：`COMPLETED`。
- 兼职端最终回读状态：`COMPLETED`。

## 证据文件

- JSON 报告：
  - `project-logs/campus-relay/runtime/step-165-android-action-matrix/android-action-matrix-report.json`
- 用户端启动截图：
  - `project-logs/campus-relay/runtime/step-165-android-action-matrix/user-launch.png`
- 兼职端启动截图：
  - `project-logs/campus-relay/runtime/step-165-android-action-matrix/parttime-launch.png`
- 动作链完成后用户端截图：
  - `project-logs/campus-relay/runtime/step-165-android-action-matrix/user-after-action-chain.png`
- 动作链完成后兼职端截图：
  - `project-logs/campus-relay/runtime/step-165-android-action-matrix/parttime-after-action-chain.png`

## 本轮没有做什么

- 没有改后端业务代码。
- 没有改前端页面。
- 没有改 Android 原生配置。
- 没有改 bridge。
- 没有改 `request.js`。
- 没有改 token 附着逻辑。
- 没有改接口、路由或鉴权。
- 没有删除旧外卖兼容模块。
- 没有提交真实公网地址、服务器密码、GitHub token、腾讯地图 key 或 `.env` 内容。
- 没有做坐标级 UI 点击自动化，本轮是 ADB 启动/截图 + 公开 API 状态机动作链。

## 验证结果

- `py -m py_compile scripts\trial-operation\android_action_matrix.py`：通过。
- Python Android action matrix：通过。
- `git diff --check`：通过，仅 CRLF 换行符提示。
- 敏感信息扫描：通过，未在本轮新增文件中发现公网 IP、服务器密码、GitHub token、腾讯地图 key 或 OpenAI key。

## 当前结论

当前双端不再只是“能启动 / 能登录 / 能读基础接口”，已经有一条可重复执行的 Python 动作链能把用户端和兼职端串到 `COMPLETED`。

这足够支撑 owner-controlled 小范围内测继续推进，但仍不是公开公测验收完成态。公开公测前仍建议补：

1. QA APK 分发包 manifest。
2. release 签名包策略。
3. HTTPS / 域名 / 证书。
4. 隐私说明与最小用户协议。
5. 弱网、后台切换、多账号并发和失败重试验证。

## 当前 bridge 结论

bridge 仍保持 `Phase A no-op` 冻结态。本轮没有触发任何 bridge 恢复推进条件，也没有删除 `/api/campus/courier/profile` 或 `/api/campus/courier/review-status`。

## 下一轮建议

Step 166 建议进入 QA APK 分发包 manifest 与安装复核：

1. 基于当前通过的双端动作链，重新生成或确认一轮 Android 用户端 / 兼职端 QA APK。
2. 记录 APK 文件名、包名、显示名、版本号、构建时间、对应 Git commit。
3. 记录安装方式、测试账号私下发放方式和内测反馈入口。
4. 不要在 manifest 中写入真实服务器密码、`.env`、token 或地图 key。

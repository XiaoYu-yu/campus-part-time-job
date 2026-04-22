# Step 91 - 最小 GitHub Actions CI 实现

## 本轮目标

1. 基于 Step 90 已完成的最小 CI 边界设计，新增一个 GitHub Actions workflow。
2. CI 只覆盖 backend compile、frontend build 和 sample validation。
3. 不做部署、不跑 E2E、不注入真实密钥、不改业务代码、不改 bridge。

## 实际新增

新增：

- `.github/workflows/trial-operation-ci.yml`

## workflow 边界

### 1. Backend compile

运行环境：

- `windows-latest`
- JDK 17 Temurin
- Maven cache

命令：

```powershell
cd backend
.\mvnw.cmd -DskipTests compile
```

边界：

1. 只编译。
2. 不连接真实数据库。
3. 不启动长驻进程。
4. 不跑完整集成测试。

### 2. Frontend build

运行环境：

- `windows-latest`
- Node.js 20
- npm cache

命令：

```powershell
cd frontend
npm ci
npm run build
```

环境：

- `VITE_TENCENT_MAP_KEY=ci-placeholder-key`

边界：

1. 只做构建。
2. 不使用真实腾讯地图 key。
3. 不启动 dev server。
4. 不跑浏览器 E2E。

### 3. Trial sample validation

运行环境：

- `windows-latest`

命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1
```

退出码策略：

1. `0`：通过。
2. `2`：仅 warning，CI 仍保持绿色。
3. 其它：失败。

这样保持与当前脚本语义一致：固定运行态样本缺失只是 warning，H2 seed / schema 的关键锚点缺失才应阻断。

## 触发条件

1. push 到 `main`。
2. pull request 指向 `main`。

## 明确没有做

1. 没有自动部署。
2. 没有真实支付、真实退款、真实打款。
3. 没有真实地图 key。
4. 没有 production secret。
5. 没有 E2E。
6. 没有 H2 自动 reset。
7. 没有 bridge 改动。
8. 没有接口、鉴权、路由或业务代码改动。

## 验证结果

本地等价验证已执行：

1. `.\mvnw.cmd -DskipTests compile`
   - 结果：通过。
2. `npm ci`
   - 结果：通过。
3. `npm run build`
   - 结果：通过。
4. `powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1`
   - 结果：退出码 `2`。
   - 说明：仅存在预期 warning，无 hard failure。
5. `git diff --check`
   - 结果：通过。
   - 说明：仅保留 Windows 工作区 LF -> CRLF 提示，无空白错误。

## 当前 bridge 结论

1. bridge 继续处于 `Phase A no-op` 冻结态。
2. CI 不会删除、收紧或改变 bridge。
3. 当前最终结论仍不是“bridge 已可删除”。

## 下一轮建议

Step 92 建议先做“CI 首轮运行结果跟踪 / 本地与远端一致性复核”：

1. 如果 GitHub push 成功，观察 Actions 首次运行结果。
2. 如果 CI 失败，只修 workflow 或环境差异，不扩业务功能。
3. 不重开 bridge、页面 polish、地图扩展或真实支付接入。

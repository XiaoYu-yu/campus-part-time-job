# 最小 CI 检查边界

## 目标

这份文档定义当前项目是否值得引入最小 CI，以及 CI 只应该检查什么。

当前目标不是生产级发布流水线，也不是自动部署。当前最小 CI 只用于：

1. 避免 main 上出现无法编译的后端代码。
2. 避免 main 上出现无法构建的前端代码。
3. 避免试运营 H2 样本锚点被误删。
4. 保持当前 bridge、模拟资金、地图和试运营边界不被 CI 误解为生产上线能力。

## 当前仓库状态

已有：

1. `.github/pull_request_template.md`
2. `.github/ISSUE_TEMPLATE/*`

尚无：

1. `.github/workflows/*`
2. 自动化 GitHub Actions 检查。
3. 自动部署流水线。

## 推荐最小 CI 范围

### 1. backend compile

命令：

```powershell
cd backend
.\mvnw.cmd -DskipTests compile
```

目标：

1. 校验 Java / Spring / MyBatis 编译通过。
2. 不运行完整集成测试，避免 CI 首轮耗时和环境复杂度过高。
3. 不连接真实数据库。

### 2. frontend build

命令：

```powershell
cd frontend
npm ci
npm run build
```

目标：

1. 校验 Vue / Vite 构建通过。
2. 校验前端配置、路由和组件编译无错误。
3. 不要求真实腾讯地图 key；构建阶段可使用占位 key 或不注入 key。

### 3. sample validation

命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1
```

目标：

1. 校验 `schema-h2.sql` 与 `data-h2.sql` 的关键试运营锚点仍存在。
2. 保留当前 warning 语义：运行态可选样本缺失不应导致硬失败。
3. CI 中应只把 hard failure 视为失败，warning 作为提示。

## 暂不纳入 CI 的内容

1. 不自动启动 backend 长驻进程。
2. 不自动启动 frontend dev server。
3. 不做浏览器端到端测试。
4. 不做 H2 自动 reset。
5. 不跑真实腾讯地图 SDK。
6. 不接真实支付、退款、打款。
7. 不做部署。
8. 不做数据库备份或恢复。
9. 不删除或收紧 bridge。

## 推荐触发条件

最小 CI 建议先绑定：

1. `push` 到 `main`
2. `pull_request` 指向 `main`

后续如果发现耗时过长，可拆成：

1. 后端 job
2. 前端 job
3. 试运营样本校验 job

当前不建议加入定时任务。

## 密钥策略

最小 CI 不需要真实密钥：

1. 不需要真实数据库密码。
2. 不需要生产 JWT secret。
3. 不需要真实腾讯地图 key。
4. 不需要 GitHub repository secrets。

如后续加入浏览器地图 smoke，再单独评估是否需要 CI secret。

## go / no-go 结论

当前建议进入最小 CI 实现：

1. 收益明确：防止 main 上出现编译 / 构建 / H2 样本锚点破坏。
2. 风险较低：只跑 compile、build、只读 sample validation。
3. 不触业务逻辑，不触 bridge，不触部署。
4. 可单文件回滚。

建议下一轮新增一个最小 GitHub Actions workflow，名称可为：

- `.github/workflows/trial-operation-ci.yml`

## Step 91 落地结果

已新增：

- `.github/workflows/trial-operation-ci.yml`

该 workflow 按本文件边界实现：

1. backend compile。
2. frontend build。
3. sample validation。
4. sample validation 的 warning exit code `2` 不使 CI 失败。
5. 不部署、不注入真实密钥、不运行 E2E。

## 验收口径

最小 CI 首轮只要求：

1. workflow 文件存在。
2. backend compile job 通过。
3. frontend build job 通过。
4. sample validation hard failure 能使 CI 失败。
5. sample validation warning 不导致 CI 失败。
6. workflow 不打印任何真实 key。
7. workflow 不做部署。

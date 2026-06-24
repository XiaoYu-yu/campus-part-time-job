# 最小 CI 检查边界

## 目标

这份文档定义当前项目是否值得引入最小 CI，以及 CI 只应该检查什么。

当前目标不是生产级发布流水线，也不是自动部署。当前最小 CI 只用于：

1. 避免 main 上出现后端集成测试失败。
2. 避免 main 上出现前端 lint、单测或构建失败。
3. 避免试运营 H2 样本锚点被误删。
4. 避免 Android release candidate 的 HTTPS / no-cleartext / no-backup 边界被误改。
5. 保持当前 bridge、模拟资金、地图和试运营边界不被 CI 误解为生产上线能力。

## 当前仓库状态

已有：

1. `.github/pull_request_template.md`
2. `.github/ISSUE_TEMPLATE/*`
3. `.github/workflows/trial-operation-ci.yml`
4. main push / pull request 自动检查。
5. Android unsigned release candidate 构建 job。

尚无自动部署流水线。正式 signed release 包只允许通过手动 workflow 或 owner 本地私有签名材料构建。

## 推荐最小 CI 范围

### 1. backend tests

命令：

```powershell
cd backend
.\mvnw.cmd test
```

目标：

1. 校验 Java / Spring / MyBatis 编译通过。
2. 使用 H2/test profile 执行当前后端单元与集成测试。
3. 不连接真实数据库。

### 2. frontend build

命令：

```powershell
cd frontend
npm ci
npm run lint
npm test
npm run build
```

目标：

1. 校验 Vue / Vite 源码通过 ESLint。
2. 执行 Vitest 单测。
3. 校验前端配置、路由和组件构建无错误。
4. 不要求真实腾讯地图 key；构建阶段使用占位 key。

### 3. sample validation

命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\validate-samples.ps1
```

目标：

1. 校验 `schema-h2.sql` 与 `data-h2.sql` 的关键试运营锚点仍存在。
2. 保留当前 warning 语义：运行态可选样本缺失不应导致硬失败。
3. CI 中应只把 hard failure 视为失败，warning 作为提示。

### 4. Android unsigned release candidate

命令：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\trial-operation\build-android-release-apks.ps1 `
  -VersionCode 1 `
  -VersionName "0.0.0-ci.<run_number>" `
  -ApiBase "https://xiaoyu.xin/api" `
  -OutputDirectory target/android-release-candidate
```

目标：

1. 校验用户端和兼职端 release APK / AAB 均可构建。
2. 校验 release API base 必须是 HTTPS 且以 `/api` 结尾。
3. 校验 release manifest 不允许 cleartext。
4. 校验 release manifest 禁止 Android backup。
5. 校验生成后的 Capacitor 配置为 `androidScheme=https`、`cleartext=false`。
6. 校验包名、显示名和版本号未漂移。
7. 只上传 `android-release-manifest.json`，不上传 unsigned APK / AAB。

注意：

- 该 job 不注入真实 keystore，不生成可公开分发的正式安装包。
- 正式签名包由 `.github/workflows/android-release.yml` 手动触发，并依赖 owner 配置的 GitHub Secrets。

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
10. 不把 unsigned release candidate 当正式 release 分发。

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
5. Android unsigned release candidate job 不需要 release keystore。

如后续加入浏览器地图 smoke，再单独评估是否需要 CI secret。

正式 Android signed release workflow 需要单独配置以下 GitHub Secrets，且只应由 owner 管理：

1. `ANDROID_USER_KEYSTORE_BASE64`
2. `ANDROID_USER_STORE_PASSWORD`
3. `ANDROID_USER_KEY_ALIAS`
4. `ANDROID_USER_KEY_PASSWORD`
5. `ANDROID_PARTTIME_KEYSTORE_BASE64`
6. `ANDROID_PARTTIME_STORE_PASSWORD`
7. `ANDROID_PARTTIME_KEY_ALIAS`
8. `ANDROID_PARTTIME_KEY_PASSWORD`

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

1. backend tests。
2. frontend lint / tests / build。
3. sample validation。
4. sample validation 的 warning exit code `2` 不使 CI 失败。
5. 不部署、不注入真实密钥、不运行 E2E。

## 验收口径

最小 CI 首轮只要求：

1. workflow 文件存在。
2. backend tests job 通过。
3. frontend lint、tests、build 通过。
4. sample validation hard failure 能使 CI 失败。
5. sample validation warning 不导致 CI 失败。
6. workflow 不打印任何真实 key。
7. workflow 不做部署。

## Step 175 落地结果

已补充：

- `.github/workflows/trial-operation-ci.yml`
  - 新增 `android-release-candidate` job。
  - 使用 Node 20、JDK 21、Android SDK 36。
  - 构建双端 unsigned release candidate。
  - 只上传 manifest，不上传 APK / AAB。
- `.github/workflows/android-release.yml`
  - 新增手动 signed release workflow。
  - 从 GitHub Secrets 物化临时 keystore 和 `key.properties`。
  - 构建完成后无论成功失败都会清理私有签名文件。

该变更仍不代表自动部署或公开发布能力；它只是把 release 候选构建边界纳入可重复检查。

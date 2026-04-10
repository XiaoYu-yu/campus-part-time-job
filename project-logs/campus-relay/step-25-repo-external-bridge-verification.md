# Step 25 - repo 外 bridge 依赖核实与证据回填

## 本轮目标

1. 处理 Step 25 的三个 repo 外阻塞项，而不是继续扩 repo 内功能。
2. 用真实核查范围和真实证据回填 bridge 文档，不伪造 repo 外结果。
3. 明确哪些项在当前环境下能核，哪些项仍然只能保留为待人工核实。

## 本轮核查范围

### 1. 静态资源 / 页面产物 / 部署提示

- `D:\20278\code\Campus part-time job\frontend\dist\assets`
- `D:\20278\code\Campus part-time job\docs\deployment\production-deploy.md`
- `D:\20278\code\Campus part-time job\docs\deployment\backup-and-rollback.md`
- `C:\Users\20278\Documents`
- `C:\Users\20278\Desktop`
- `C:\Users\20278\Downloads`
- `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
- `D:\20278\code`

### 2. 访问日志 / 网关日志候选目录

- `C:\nginx\logs`
- `D:\nginx\logs`
- `C:\Program Files\nginx\logs`
- `D:\20278\deploy`
- `D:\deploy`
- `D:\logs`

### 3. 调试资产 / 集合 / 脚本候选目录

- `C:\Users\20278\AppData\Roaming\Apifox`
- `C:\Users\20278\AppData\Local\Apifox`
- `C:\Users\20278\AppData\Roaming\Postman`
- `C:\Users\20278\AppData\Local\Postman`
- `C:\Users\20278\Documents\Postman`
- `C:\Users\20278\Documents\Postman Files`
- `C:\Users\20278\Documents`
- `C:\Users\20278\Desktop`
- `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
- `D:\20278\code`

## 实际使用了哪些入口做核查

1. 静态资源目录：
   - 搜索 `/api/campus/courier/profile`
   - 搜索 `/api/campus/courier/review-status`
   - 搜索 `getCourierProfile`
   - 搜索 `getCourierReviewStatus`
2. 部署文档：
   - 读取 `docs/deployment/production-deploy.md`
   - 读取 `docs/deployment/backup-and-rollback.md`
3. 本机本地项目目录：
   - 检查 `HBuilder X` 项目列表
   - 读取 `C:\Users\20278\AppData\Roaming\HBuilder X\projects\89af163a3cc20c2544b1549ff099321a\setting.json`
4. 调试资产与脚本目录：
   - 搜索 `customer_token`
   - 搜索旧 bridge endpoint
   - 搜索辅助函数名
5. 日志目录：
   - 检查候选目录是否存在
   - 若存在则查 `access.log / error.log / nginx.conf / gateway*.log`

## 三个 repo 外阻塞项实际核查结果

### 1. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`

- 核查结果：
  - 在当前环境可访问范围内，未发现 repo 外静态资源、旧页面包或本地项目命中 `GET /api/campus/courier/profile`。
  - `frontend/dist/assets` 搜索无命中。
  - `docs/deployment` 只给出通用部署说明，没有配置具体已部署静态资源路径。
  - `C:\Users\20278\AppData\Roaming\HBuilder X\projects\89af163a3cc20c2544b1549ff099321a\setting.json` 内容为空对象 `{}`，不能证明存在可继续追查的旧页面项目。
  - `D:\20278\code` 全范围搜索该 endpoint 时，命中仅来自当前 repo 源码、测试和日志文档，没有 repo 外命中。
- 证据位置：
  - `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
  - `D:\20278\code\Campus part-time job\docs\deployment\production-deploy.md`
  - `D:\20278\code\Campus part-time job\frontend\dist\assets`
  - `C:\Users\20278\AppData\Roaming\HBuilder X\projects\89af163a3cc20c2544b1549ff099321a\setting.json`
- 结论：
  - 当前核查范围内未发现命中。
  - 但没有已部署静态资源目录和访问日志，不能写“确认不存在依赖”。
- 状态：
  - 待人工核实

### 2. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`

- 核查结果：
  - 在当前环境可访问范围内，未发现 repo 外静态资源、旧页面包或本地项目命中 `GET /api/campus/courier/review-status`。
  - `frontend/dist/assets` 搜索无命中。
  - `docs/deployment` 未提供已部署静态资源真实路径。
  - `D:\20278\code` 全范围搜索该 endpoint 时，命中仅来自当前 repo 源码、测试和日志文档，没有 repo 外命中。
- 证据位置：
  - `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
  - `D:\20278\code\Campus part-time job\docs\deployment\production-deploy.md`
  - `D:\20278\code\Campus part-time job\frontend\dist\assets`
- 结论：
  - 当前核查范围内未发现命中。
  - 但没有已部署静态资源目录和访问日志，不能写“确认不存在依赖”。
- 状态：
  - 待人工核实

### 3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

- 核查结果：
  - 在以下范围内搜索 `customer_token`、`/api/campus/courier/profile`、`/api/campus/courier/review-status`：
    - `C:\Users\20278\Documents`
    - `C:\Users\20278\Desktop`
    - `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
    - `D:\20278\code`
  - 未发现 repo 外脚本、集合或说明文档依赖 `customer_token` 访问旧 bridge。
  - 当前环境未发现常见 `Postman / Apifox` 目录，因此无法对团队共享集合做进一步核查。
- 证据位置：
  - `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
  - `C:\Users\20278\Documents`
  - `C:\Users\20278\Desktop`
  - `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
  - `D:\20278\code`
- 结论：
  - 当前核查范围内未发现命中。
  - 但由于没有团队共享 Postman / Apifox 集合、仓库外脚本目录或其他机器上的联调资产，因此不能写“确认不存在依赖”。
- 状态：
  - 待人工核实

## 访问日志核查结果

1. 当前环境未发现可用的 Nginx / 网关日志目录：
   - `C:\nginx\logs`
   - `D:\nginx\logs`
   - `C:\Program Files\nginx\logs`
   - `D:\20278\deploy`
   - `D:\deploy`
   - `D:\logs`
2. 在 `D:\` 全盘候选日志名搜索中，只发现与 `Trae` 插件构建相关的错误日志，不是业务访问日志，无法用于 bridge 来源归因。
3. 本轮没有拿到任何可归因的 `access.log`、`gateway.log`、`Referer` 或 `UA` 证据。

## 通过 / 不通过 / 待人工核实汇总

- 通过：
  - 无
- 不通过：
  - 无直接证据证明 repo 外依赖仍存在，因此本轮没有“明确不通过”的闭环项
- 待人工核实：
  - 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`
  - 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`
  - 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

## 当前卡点

1. 没有可访问的已部署静态资源目录。
2. 没有可归因的 Nginx / 网关访问日志。
3. 没有可访问的团队共享 Postman / Apifox 集合。
4. 不能从当前仓库和当前本机环境直接证明 repo 外依赖已经清空。

## 本轮结束后的 bridge 结论

1. repo 内证据继续稳定。
2. repo 外依赖在当前核查范围内未发现命中。
3. 但由于缺少已部署静态资源、访问日志和团队共享联调资产，本轮仍不能把 repo 外依赖写成“已关闭”。
4. 因此 bridge 仍不能进入 `Phase A` 执行准备。

## 下一轮建议

1. 继续按 checklist 去真实拿 repo 外资产：
   - 已部署静态资源目录
   - Nginx / 网关访问日志
   - 团队共享 Postman / Apifox 集合
   - 仓库外联调脚本目录
2. 每拿到一种资产，就按本日志结构继续补证据位置和结论。
3. 在 repo 外阻塞项关闭前，不要把 Step 26 改成新页面或新接口开发轮。

## Step 26 补充证据

1. 本轮新增拿到的 repo 外真实资产：
   - `C:\Users\20278\.ssh\config`
   - `xiaoyu_TenXun_Ubuntu (106.54.211.68)`
   - `xiaoyu_root_ALi_Ubuntu (47.243.129.186)`
   - `D:\software\校园代送项目_完整交接总结_Step24.md`
   - `D:\software\step-25-execution-prompt.md`
2. 本轮新增真实核查结果：
   - 两台公网服务器均未发现项目部署目录、nginx 进程、docker 容器、bridge endpoint 命中或 `customer_token` 调试脚本痕迹
   - repo 外共享文档只出现 bridge 阶段说明和执行提示，没有出现运行时调用证据
3. 这轮新增证据比 Step 25 更强，但仍不足以关闭第 1~3 项阻塞，因为：
   - 不能证明这两台公网服务器覆盖了全部历史部署环境
   - 没有真实已部署静态资源目录或历史发布包
   - 没有可归因的 Nginx / gateway 访问日志
   - 没有团队共享 Postman / Apifox / 联调脚本资产
4. 因此 Step 25 的最终阶段结论不变：
   - bridge 仍不能进入 `Phase A` 执行准备

## Step 27 补充证据

1. 本轮没有重复 Step 25 的泛目录搜索，而是继续追三类更接近真实运行面的 repo 外资产：
   - repo 外前端源码 / 历史项目目录：`D:\software\GOT\html`
   - Windows Recent 外部文档入口：`C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent`
   - 两台公网服务器上的常见业务部署和日志路径：`/www`、`/www/wwwroot`、`/var/log/nginx`、`/etc/nginx` 等
2. 本轮新增真实核查结果：
   - 在 `D:\software\GOT\html` 排除 `node_modules/dist/build/.venv` 后，对旧 bridge 关键字无命中
   - Windows Recent 解析到额外的 repo 外文档入口 `C:\Users\20278\Desktop\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md`，但目标文件当前已缺失
   - 当前机器不存在常见 `Postman / Apifox` 资产目录
   - 两台公网服务器在更贴近业务部署面的路径下仍未发现业务部署物、`nginx.conf` 或 `access.log`
3. 这轮新增证据比 Step 26 更接近真实业务运行面，但仍不足以关闭第 1~3 项阻塞，因为：
   - 还没有拿到实际业务静态资源目录或历史发布包
   - 还没有拿到可归因的 Nginx / gateway 访问日志
   - 还没有拿到团队共享 Postman / Apifox / 联调脚本资产
4. 因此 Step 25 的最终阶段结论继续保持：
   - bridge 仍不能进入 `Phase A` 执行准备

## Step 28 补充证据

1. 本轮没有再重复 Step 25 到 Step 27 的泛目录搜索，而是继续追关键业务资产：
   - `C:\Users\20278\AppData\Roaming\aDrive\preference.json`
   - `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log`
   - `D:\software\GOT\html\project.zip`
   - 两台公网服务器的 shell 历史
2. 本轮新增真实核查结果：
   - `aDrive` 配置和日志证明 `D:\software` 是当前机器阿里云盘客户端下载根路径
   - `D:\software\GOT\html\project.zip` 是真实外部压缩包，但内部项目为 `healthy-management`，不是校园代送项目
   - 两台公网服务器 shell 历史中未发现业务部署痕迹
3. 这批证据比 Step 27 更接近“资产持有人和发布包入口”，但仍不足以关闭三项阻塞，因为：
   - 没有校园代送项目自己的真实静态资源目录或历史发布包
   - 没有可归因访问日志
   - 没有团队共享 Postman / Apifox / 联调脚本资产
4. 因此 Step 25 的最终阶段结论继续保持：
   - bridge 仍不能进入 `Phase A` 执行准备

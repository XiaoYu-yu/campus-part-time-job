# bridge 执行准备人工核实 checklist

## 使用说明

1. 本文档是待填写 checklist，不代表当前已经完成这些核实项。
2. 本文档的目标是让执行人拿着就能做人工核实，而不是继续停留在概念描述。
3. 若某项无法从当前仓库代码直接证明，应明确填写“待人工核实”，不要主观补结论。
4. 核实失败时不要覆盖成成功，要保留失败现象、失败证据和下一步处理建议。
5. 进入 `Phase A` 执行准备前，至少需要把所有“阻塞项”明确填完。

## Step 28 说明

1. Step 28 不再重复 Step 25 到 Step 27 已做过的泛目录搜索。
2. 本轮只追三类更接近业务持有人或真实运行面的关键资产：
   - 云盘侧真实下载根路径与下载日志
   - repo 外历史压缩包 / 构建产物
   - 服务器 shell 历史里可能残留的部署线索
3. 本轮新增拿到的关键资产与证据入口：
   - `C:\Users\20278\AppData\Roaming\aDrive\preference.json`
   - `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log`
   - `D:\software\GOT\html\project.zip`
   - `ssh xiaoyu_TenXun_Ubuntu` 下 `/root/.bash_history`、`/home/ubuntu/.bash_history`
   - `ssh xiaoyu_root_ALi_Ubuntu` 下 `/root/.bash_history`
4. 本轮新增结论不是“通过”，而是：
   - 已能证明 `D:\software` 是当前机器阿里云盘客户端下载根路径
   - 已能证明 `D:\software\GOT\html\project.zip` 是 repo 外真实压缩包，但内容是 `healthy-management`，不是当前校园代送项目
   - 已能证明当前已知两台公网服务器 shell 历史中没有业务部署痕迹
   - 但仍拿不到真正承载校园代送的业务静态资源目录、历史发布包、可归因访问日志和团队共享调试资产

## 核实项

### 1. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`

- 推荐执行入口：
  - 线上旧页面访问清单
  - 已部署静态资源目录
  - 网关 / Nginx 访问日志
- 核实方法：
  - 盘点仍在线的旧页面、外部前端包、部署脚本或静态资源
  - 检查访问日志、浏览器 network 或人工联调记录
- 推荐证据类型：
  - 截图
  - 访问日志片段
  - 静态资源搜索结果
- 成功时如何记录：
  - 记录“未发现调用”的核查范围、核查入口、执行时间和证据链接
  - 至少附一份静态资源搜索结果或访问日志片段
- 失败时如何记录：
  - 写明仍在调用的页面路径、调用时间、证据链接和影响范围
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - `project-logs/campus-relay/step-25-repo-external-bridge-verification.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已在当前环境核查以下可访问范围：
    - `D:\20278\code\Campus part-time job\frontend\dist\assets`
    - `D:\20278\code\Campus part-time job\docs\deployment`
    - `C:\Users\20278\Documents`
    - `C:\Users\20278\Desktop`
    - `C:\Users\20278\Downloads`
    - `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
    - `D:\20278\code`（搜索结果只命中当前 repo 内文件）
  - 在上述范围内未发现 repo 外静态资源、旧页面包或本地项目直接调用 `GET /api/campus/courier/profile` 的命中。
  - 但当前环境没有提供已部署静态资源目录，也没有提供可归因的 Nginx / 网关访问日志，因此不能据此判定 repo 外依赖已关闭。
  - `2026-04-10` Step 26 继续补查 repo 外真实资产：
    - 本机 SSH 配置：`C:\Users\20278\.ssh\config`
    - 公网服务器：`xiaoyu_TenXun_Ubuntu (106.54.211.68)`、`xiaoyu_root_ALi_Ubuntu (47.243.129.186)`
    - 外部文档：`D:\software\校园代送项目_完整交接总结_Step24.md`、`D:\software\step-25-execution-prompt.md`
  - 通过 SSH 实际检查两台公网服务器的 `/opt`、`/data`、`/root`、`/home`、`/usr/local/bin`、`/var/log`、运行进程、docker 容器与 shell 历史：
    - 未发现 campus / takeaway / delivery 相关部署目录
    - 未发现 `nginx` / `openresty` / `caddy` 业务进程
    - 未发现 `access.log` 或 endpoint 命中
  - 外部文档里只出现 bridge 策略说明，没有出现旧页面产物路径或运行时调用证据。
  - `2026-04-10` Step 27 继续补拿更接近真实运行面的外部资产：
    - repo 外前端源码树：`D:\software\GOT\html`
    - Windows Recent 外部文档入口：`C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md.lnk`
    - 两台公网服务器的常见部署 / nginx 路径：`/www`、`/www/wwwroot`、`/var/log/nginx`、`/etc/nginx` 等
  - Step 27 结果：
    - `D:\software\GOT\html` 排除 `node_modules/dist/build/.venv` 后，对旧 bridge 关键字无命中
    - Windows Recent 只证明当前机器近期打开过额外的 repo 外工作流文档，但目标文件当前缺失，无法继续读取
    - 两台公网服务器在常见部署 / 日志路径下仍未发现业务部署物、`nginx.conf` 或 `access.log`
  - 因为仍未拿到实际业务静态资源目录或历史发布包，所以第 1 项继续保持待人工核实。
  - `2026-04-10` Step 28 继续追关键业务资产：
    - `C:\Users\20278\AppData\Roaming\aDrive\preference.json` 明确记录：
      - `downloadPath = D:\software`
      - 当前机器阿里云盘下载根路径就是 `D:\software`
      - 当前用户侧偏好项键为 `e9b4317bda8649b7af3c521cd1a42293`
    - `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log` 明确出现真实下载落地路径：
      - `D:\software\VMware-workstation-full-17.5.0-22583795 (1).exe.part`
      - 这证明 `D:\software` 不是随机本机目录，而是云盘客户端下载根路径
    - `D:\software\GOT\html\project.zip` 是 repo 外真实压缩包；解包清单与 `package.json` 显示其主体为：
      - `project/healthy-management`
      - `description: 健康颐养平台-管理端`
      - `repository: http://gitlab.neumooc.com/yiyanghealthy/healthy-management.git`
    - 对该压缩包内部搜索：
      - `/api/campus/courier/profile`
      - `/api/campus/courier/review-status`
      - `getCourierProfile`
      - `getCourierReviewStatus`
      - `customer_token`
      均无命中
    - 因此 Step 28 拿到的是真实 repo 外发布包级资产，但它能证明的只是：
      - 当前云盘根目录存在其他项目压缩包
      - 尚未拿到校园代送项目自己的真实发布包或静态资源目录
  - `2026-04-10` Step 28 服务器 shell 历史补证：
    - `xiaoyu_TenXun_Ubuntu:/root/.bash_history` 与 `/home/ubuntu/.bash_history`
    - `xiaoyu_root_ALi_Ubuntu:/root/.bash_history`
    - 对 `nginx|openresty|wwwroot|/www|scp|rsync|zip|tar|deploy|campus|delivery|takeaway|git clone|git checkout` 检索后，未发现业务部署命令痕迹
    - 这进一步说明当前已知两台公网服务器不像校园代送的实际静态资源承载机
  - 但第 1 项仍不能关闭，因为还缺：
    - 校园代送项目自己的真实业务静态资源目录或历史发布包
    - 可归因访问日志
  - `2026-04-10` Step 29 owner 明确确认：
    - 当前项目唯一维护人就是 owner 本人
    - 当前项目从未部署、从未发布
    - 不存在历史发布包
    - 不存在仓库外旧页面副本、历史前端包或非 repo 管理的联调副本
  - 因此第 1 项不再需要通过“真实业务静态资源目录 / 历史发布包 / Nginx 日志”去关闭；基于 owner 明确确认，可以关闭“仓库外旧页面仍直接调用旧 bridge”这一阻塞项。
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 2. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`

- 推荐执行入口：
  - 线上旧页面访问清单
  - 已部署静态资源目录
  - 网关 / Nginx 访问日志
- 核实方法：
  - 盘点仍在线的旧页面、外部前端包、部署脚本或静态资源
  - 检查访问日志、浏览器 network 或人工联调记录
- 推荐证据类型：
  - 截图
  - 访问日志片段
  - 静态资源搜索结果
- 成功时如何记录：
  - 记录“未发现调用”的核查范围、核查入口、执行时间和证据链接
  - 至少附一份静态资源搜索结果或访问日志片段
- 失败时如何记录：
  - 写明仍在调用的页面路径、调用时间、证据链接和影响范围
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - `project-logs/campus-relay/step-25-repo-external-bridge-verification.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已在当前环境核查以下可访问范围：
    - `D:\20278\code\Campus part-time job\frontend\dist\assets`
    - `D:\20278\code\Campus part-time job\docs\deployment`
    - `C:\Users\20278\Documents`
    - `C:\Users\20278\Desktop`
    - `C:\Users\20278\Downloads`
    - `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
    - `D:\20278\code`（搜索结果只命中当前 repo 内文件）
  - 在上述范围内未发现 repo 外静态资源、旧页面包或本地项目直接调用 `GET /api/campus/courier/review-status` 的命中。
  - 但当前环境没有提供已部署静态资源目录，也没有提供可归因的 Nginx / 网关访问日志，因此不能据此判定 repo 外依赖已关闭。
  - `2026-04-10` Step 26 继续补查 repo 外真实资产：
    - 本机 SSH 配置：`C:\Users\20278\.ssh\config`
    - 公网服务器：`xiaoyu_TenXun_Ubuntu (106.54.211.68)`、`xiaoyu_root_ALi_Ubuntu (47.243.129.186)`
    - 外部文档：`D:\software\校园代送项目_完整交接总结_Step24.md`、`D:\software\step-25-execution-prompt.md`
  - 通过 SSH 实际检查两台公网服务器的 `/opt`、`/data`、`/root`、`/home`、`/usr/local/bin`、`/var/log`、运行进程、docker 容器与 shell 历史：
    - 未发现 campus / takeaway / delivery 相关部署目录
    - 未发现 `nginx` / `openresty` / `caddy` 业务进程
    - 未发现 `access.log` 或 endpoint 命中
  - 外部文档里只出现 bridge 策略说明，没有出现旧页面产物路径或运行时调用证据。
  - `2026-04-10` Step 27 继续补拿更接近真实运行面的外部资产：
    - repo 外前端源码树：`D:\software\GOT\html`
    - Windows Recent 外部文档入口：`C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md.lnk`
    - 两台公网服务器的常见部署 / nginx 路径：`/www`、`/www/wwwroot`、`/var/log/nginx`、`/etc/nginx` 等
  - Step 27 结果：
    - `D:\software\GOT\html` 排除 `node_modules/dist/build/.venv` 后，对旧 bridge 关键字无命中
    - Windows Recent 只证明当前机器近期打开过额外的 repo 外工作流文档，但目标文件当前缺失，无法继续读取
    - 两台公网服务器在常见部署 / 日志路径下仍未发现业务部署物、`nginx.conf` 或 `access.log`
  - 因为仍未拿到实际业务静态资源目录或历史发布包，所以第 2 项继续保持待人工核实。
  - `2026-04-10` Step 28 继续追关键业务资产：
    - `C:\Users\20278\AppData\Roaming\aDrive\preference.json` 与 `aDrive\logs\main.log` 证明 `D:\software` 是当前机器的云盘下载根路径
    - `D:\software\GOT\html\project.zip` 是 repo 外真实压缩包，但内部项目为 `healthy-management`，对旧 bridge 关键字无命中
    - `xiaoyu_TenXun_Ubuntu` 与 `xiaoyu_root_ALi_Ubuntu` 的 shell 历史对部署关键字无命中
  - 因此 Step 28 拿到的新资产更接近“发布包/部署持有人”，但仍没有拿到校园代送项目自己的静态资源包或运行中页面产物。
  - 第 2 项继续保持待人工核实，原因是：
    - 仍缺校园代送真实发布包或静态目录
    - 仍缺可归因访问日志
  - `2026-04-10` Step 29 owner 明确确认：
    - 当前项目唯一维护人就是 owner 本人
    - 当前项目从未部署、从未发布
    - 不存在历史发布包
    - 不存在仓库外旧页面副本、历史前端包或非 repo 管理的联调副本
  - 因此第 2 项不再需要通过“真实业务静态资源目录 / 历史发布包 / Nginx 日志”去关闭；基于 owner 明确确认，可以关闭“仓库外旧页面仍直接调用旧 bridge”这一阻塞项。
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

- 推荐执行入口：
  - Postman / Apifox 集合
  - 项目 Wiki 或联调说明
  - 团队常用脚本目录
- 核实方法：
  - 盘点本地脚本、测试说明、团队常用调试集合
  - 人工确认是否仍通过 `customer_token` 调用旧 bridge 读取资料或审核状态
- 推荐证据类型：
  - 集合截图
  - 调试脚本片段
  - 调用日志或抓包记录
- 成功时如何记录：
  - 记录“未发现依赖 customer_token 调旧 bridge”的核查范围、集合名称或脚本目录、执行时间和证据链接
  - 至少附一份脚本搜索结果、集合截图或抓包记录
- 失败时如何记录：
  - 写明脚本名称、调用命令、token 类型、影响场景和替代计划
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - `project-logs/campus-relay/step-25-repo-external-bridge-verification.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已在当前环境检查以下脚本和联调资产入口：
    - `C:\Users\20278\Documents`
    - `C:\Users\20278\Desktop`
    - `C:\Users\20278\AppData\Roaming\HBuilder X\projects`
    - `D:\20278\code`
  - 在上述范围内对 `.json/.md/.txt/.bat/.sh/.ps1/.py` 等文本资产搜索 `customer_token`、`/api/campus/courier/profile`、`/api/campus/courier/review-status`，未发现 repo 外脚本或集合依赖 `customer_token` 调旧 bridge 的命中。
  - 当前环境未发现常见 API 调试资产目录：
    - `C:\Users\20278\AppData\Roaming\Apifox`
    - `C:\Users\20278\AppData\Local\Apifox`
    - `C:\Users\20278\AppData\Roaming\Postman`
    - `C:\Users\20278\AppData\Local\Postman`
    - `C:\Users\20278\Documents\Postman`
    - `C:\Users\20278\Documents\Postman Files`
  - 由于无法证明团队共享集合、仓库外脚本目录或其他机器上的联调资产不存在，当前仍只能标记为待人工核实。
  - `2026-04-10` Step 26 继续补查 repo 外真实资产：
    - 外部文档：`D:\software\校园代送项目_完整交接总结_Step24.md`、`D:\software\step-25-execution-prompt.md`
    - 公网服务器：`xiaoyu_TenXun_Ubuntu (106.54.211.68)`、`xiaoyu_root_ALi_Ubuntu (47.243.129.186)`
  - 在上述公网服务器上对 `/root`、`/home`、`/usr/local/bin`、`/root/.bash_history` 和 `/var/log` 继续检索：
    - `customer_token`
    - `/api/campus/courier/profile`
    - `/api/campus/courier/review-status`
    - `getCourierProfile`
    - `getCourierReviewStatus`
  - 结果：未发现脚本、shell 历史或服务器日志依赖 `customer_token` 调旧 bridge 的命中。
  - 外部文档里只出现旧 bridge 的阶段性说明和执行提示，没有发现可执行脚本或集合导出。
  - `2026-04-10` Step 27 继续补拿更接近团队共享资产的入口：
    - 当前机器标准 `Postman / Apifox` 目录：
      - `C:\Users\20278\AppData\Roaming\Postman`
      - `C:\Users\20278\AppData\Roaming\Apifox`
      - `C:\Users\20278\AppData\Local\Apifox`
      - `C:\Users\20278\Documents\Postman`
      - `C:\Users\20278\Documents\Apifox`
    - Windows Recent 外部文档入口：`C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md.lnk`
    - `D:\software\GOT\html` 与 `Downloads / Desktop / Documents / D:\software` 的文本 / 脚本类资产
  - Step 27 结果：
    - 当前机器不存在常见 `Postman / Apifox` 资产目录
    - 在上述文本和脚本类资产中，未发现 `customer_token` 调旧 bridge 的真实脚本命中
    - Windows Recent 只证明存在过额外工作流文档入口，但目标文件当前缺失，无法继续提取集合或脚本线索
  - 因为仍没有团队共享 Postman / Apifox / 联调脚本资产，所以第 3 项继续保持待人工核实。
  - `2026-04-10` Step 28 继续补追团队共享调试资产线索：
    - `C:\Users\20278\AppData\Roaming\aDrive\preference.json` 证明当前机器确实通过阿里云盘把文件下载到 `D:\software`
    - 但在 `D:\software` 当前已知的校园代送相关 repo 外文件仍只有：
      - `D:\software\重庆工信职业学院渝中校区_校园代送平台_信息采集表.md`
      - `D:\software\校园代送项目_完整交接总结_Step24.md`
    - 未拿到：
      - 团队共享 `Postman collection / environment`
      - `Apifox` 项目导出
      - 联调脚本包
    - `C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent\GOT.zip.lnk` 解析到目标 `D:\software\GOT.zip`，但目标文件已不存在，无法继续读取是否包含调试资产
  - 因此第 3 项在 Step 28 后依然不能关闭，仍缺团队共享调试资产本体，不是只缺搜索动作。
  - `2026-04-10` Step 29 owner 明确确认：
    - 当前项目唯一维护人就是 owner 本人
    - 当前没有团队
    - 不存在团队共享 `Postman / Apifox / 联调脚本` 资产
    - 不存在仓库外临时脚本仍以 `customer_token` 调旧 bridge
  - 因此第 3 项不再需要继续追“团队共享调试资产”作为关闭条件；基于 owner 明确确认，可以关闭“手工联调脚本仍依赖 customer_token 调旧 bridge”这一阻塞项。
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 4. workbench 的 profile / review-status 请求是否全程只走 `courier_token`

- 推荐执行入口：
  - 浏览器开发者工具 Network
  - 前端本地联调环境
- 核实方法：
  - 打开浏览器开发者工具 Network
  - 访问 `/courier/workbench`
  - 观察 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 的 `Authorization` 头来源
- 推荐证据类型：
  - Network 截图
  - 请求头导出
  - 本地联调日志
- 失败时如何记录：
  - 写明哪个请求仍回退使用 `customer_token`，以及触发条件
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
  - `project-logs/campus-relay/step-23-shared-regression-evidence.md`
- 核实结果：
  - `2026-04-09` 已在本地启动 `backend(test profile, 8080)` 与 `frontend(vite, 5173)`，并完成两轮真实验证。第一轮暴露出 `/courier/workbench` 仍会被 `UserLayout` 的 customer 购物车请求拉回 `/user/login`；第二轮已在修正 `frontend/src/layout/UserLayout.vue` 后重新用 Playwright 验证，纯 `courier_token` 路径可稳定停留在 `/courier/workbench`，且 `GET /api/campus/courier/profile` 与 `GET /api/campus/courier/review-status` 均优先使用 `courier_token`。
- 负责人：
  - Codex
- 日期：
  - `2026-04-09`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 5. 是否已有一轮 `customer onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的稳定联调记录

- 推荐执行入口：
  - [bridge-regression-template.md](bridge-regression-template.md)
  - 测试环境前端页面
  - 浏览器 Network / Console
- 核实方法：
  - 按联调模板执行完整链路
  - 确认每一步都有实际结果、截图或日志占位
- 推荐证据类型：
  - 联调模板填写结果
  - 页面截图
  - Network / Console 记录
- 失败时如何记录：
  - 写明具体失败步骤、阻塞接口、错误码、重现步骤和是否阻塞 Phase A
- 是否影响进入 `Phase A`：
  - 是，阻塞项
- 记录位置：
  - `project-logs/campus-relay/step-22-real-local-chain-and-h2-seed.md`
  - `project-logs/campus-relay/step-23-shared-regression-evidence.md`
- 核实结果：
  - `2026-04-09` 已在本地 `backend test profile + H2 + 8080` 与 `frontend vite + 5173` 下形成一轮真实完整链路：`customer onboarding 提交资料 -> admin 审核通过 -> customer 申请 courier token -> /courier/workbench 加载 profile/review-status/available orders -> courier 接单 -> 取餐 -> deliver(配送中) -> deliver(已送达) -> 异常上报 -> customer 确认送达 -> courier completed 结果回读`。联调样本订单为 `CR202604070002`，customer 为 `13900139001`，courier 为 `13900139000`。
- 负责人：
  - Codex
- 日期：
  - `2026-04-09`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

## 收口前判断

- 是否可以进入 `Phase A` 执行准备：
  - [x] 可以
  - [ ] 还不可以
- 当前判断：
  - repo 外阻塞项已由项目 owner 明确确认关闭
  - 当前可以进入 `Phase A` 执行准备重新评估
  - 这里的“可以”不等于立即删除 bridge，也不等于直接改鉴权策略
- 下一步建议：
  - 进入 `Phase A` 范围评估，明确 bridge 保留范围、回滚策略和最小回归清单

## Step 24 说明

- 本轮未新增 repo 外人工核实结果。
- 本轮新增的是 repo 外待关闭项的执行提示：
  - 去哪里核
  - 看什么证据
  - 成功时如何留痕
  - 失败时如何留痕
- 因此本文档当前仍是“可执行 checklist”，不是“repo 外依赖已关闭”的证明。

## Step 25 说明

- 本轮没有把 repo 外待核实项写成“已通过”。
- 本轮新增的是三类真实阴性证据和边界说明：
  - 当前环境可访问的静态资源 / 本地项目目录里未发现旧 bridge 命中
  - 当前环境未发现常见 Postman / Apifox 资产目录
  - 当前环境未发现可归因的 Nginx / 网关访问日志目录
- 因为“未在当前核查范围命中”不等于“确认无 repo 外依赖”，所以第 1~3 项仍保留为“待人工核实”。

## Step 26 说明

- 本轮新增了真正的 repo 外资产入口：
  - `C:\Users\20278\.ssh\config`
  - `xiaoyu_TenXun_Ubuntu (106.54.211.68)`
  - `xiaoyu_root_ALi_Ubuntu (47.243.129.186)`
  - `D:\software\校园代送项目_完整交接总结_Step24.md`
  - `D:\software\step-25-execution-prompt.md`
- 本轮已在上述公网服务器和外部文档范围内完成真实核查，但结果仍不足以关闭第 1~3 项。
- 更准确的状态是：
  - 已知公网服务器未发现项目部署目录、业务访问日志和调试脚本命中
  - 外部文档只给出 bridge 说明，不构成运行时依赖证明
  - 因此三项阻塞仍然只能保留为“待人工核实”

## Step 27 说明

- 本轮没有把“当前范围内未命中”改写成“确认不存在依赖”。
- 本轮新增的是更接近真实运行面的 repo 外资产与入口：
  - repo 外前端源码树：`D:\software\GOT\html`
  - Windows Recent 快捷方式：`C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent`
  - 具体缺失的外部文档目标：`C:\Users\20278\Desktop\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md`
  - 两台公网服务器上的真实部署候选路径：
    - `/www`
    - `/www/wwwroot`
    - `/www/server/nginx/logs`
    - `/var/log/nginx`
    - `/var/log/openresty`
    - `/usr/share/nginx/html`
    - `/var/www`
    - `/srv`
    - `/etc/nginx`
    - `/etc/openresty`
- 本轮实际补充的关键结论：
  - 在 `D:\software\GOT\html` 排除 `node_modules/dist/build/.venv` 后，对旧 bridge 关键字无命中
  - Windows Recent 证明当前机器最近打开过额外的 repo 外工作流文档，但该目标文件已缺失，无法进一步读取其内容
  - 当前机器不存在常见 `Postman / Apifox` 资产目录：
    - `C:\Users\20278\AppData\Roaming\Postman`
    - `C:\Users\20278\AppData\Roaming\Apifox`
    - `C:\Users\20278\AppData\Local\Apifox`
    - `C:\Users\20278\Documents\Postman`
    - `C:\Users\20278\Documents\Apifox`
  - 两台公网服务器在更贴近业务部署面的路径上仍未发现：
    - 真实静态资源目录
    - `nginx.conf`
    - `access.log`
    - `openresty` 配置
- 这轮让三项阻塞的缺口更具体了：
  - 第 1、2 项仍缺“实际业务静态资源目录或历史发布包”，理论上应在当前部署维护人或发布机备份目录手里
  - 第 3 项仍缺“团队共享 Postman / Apifox / 联调脚本资产”，理论上应在接口联调维护人或共享资产目录里
  - 当前环境无法证明具体持有人，只能明确下一步要向“当前部署维护人 / 联调资产维护人”索取

## Step 29 说明

- 本轮没有继续追部署目录、历史发布包和访问日志。
- 新增事实来自项目 owner 在当前轮次的明确确认：
  - 当前项目唯一维护人就是 owner 本人
  - 当前项目从未部署、从未发布
  - 不存在历史发布包
  - 当前没有团队
  - 不存在团队共享 `Postman / Apifox / 联调脚本` 资产
  - 不存在仓库外旧页面副本、历史前端包或非 repo 管理的联调副本
- 因此第 1~3 项的关闭依据已经从“继续追 repo 外部署物和团队资产”收敛为“owner 明确确认当前不存在这类依赖”。
- Step 29 的通过含义仅限于：
  - repo 外阻塞项已关闭
  - 可以进入 `Phase A` 执行准备重新评估
  - 仍不等于立即删除 bridge 或直接修改鉴权策略

## Step 30 说明

- 本轮不再继续追 repo 外证据。
- 本轮只做一件事：把“可以进入 `Phase A` 执行准备重新评估”落成可执行的正式方案。
- 这里的“执行准备”仍不等于实际收口动作：
  - 不删 `/api/campus/courier/profile`
  - 不删 `/api/campus/courier/review-status`
  - 不改 `request.js` 的现有 token 附着逻辑
  - 不改后端鉴权规则

## Step 30 - 进入 Phase A 前的执行准备项

### 6. `Phase A` 的执行边界是否已明确

- 记录位置：
  - `project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已明确 `Phase A` 只做执行准备，不做真正收口动作。
  - `Phase A` 明确做的事：
    - 固化 repo 内调用边界
    - 固化 bridge 保留范围
    - 固化最小回归清单
    - 固化回滚策略
  - `Phase A` 明确不做的事：
    - 不删接口
    - 不删 bridge
    - 不改鉴权
    - 不改 repo 内业务代码
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 7. bridge 保留范围是否已明确

- 记录位置：
  - `project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已明确：
    - `/api/campus/courier/profile` 继续保留
    - `/api/campus/courier/review-status` 继续保留
    - `customer_token -> bridge -> courier 前置读取` 在 `Phase A` 期间继续允许观察，不做行为变更
    - `/courier/workbench` 继续维持优先 `courier_token` 的现有策略
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 8. 回滚策略是否已明确

- 记录位置：
  - `project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已明确回滚关键点：
    - bridge 接口继续保留
    - `frontend/src/utils/request.js` 的现有 token 附着逻辑继续保留
    - `frontend/src/views/courier/CourierWorkbench.vue` 的现有行为不提前变更
    - `customer/courier-onboarding/*` 仍作为新的前置入口，不回退到旧入口
  - 已明确回滚触发条件：
    - workbench 无法稳定读取 `profile / review-status`
    - 纯 `courier_token` 路径失稳
    - onboarding -> token -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报 -> confirm / completed 回读任一关键链路回归失败
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 9. 最小回归清单是否已明确

- 记录位置：
  - `project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已明确最小回归清单至少覆盖：
    - customer onboarding 提交资料
    - customer 查看审核状态
    - customer 申请 courier token
    - `/courier/workbench` 加载 `profile / review-status`
    - pure `courier_token` 路径稳定
    - 接单
    - 取餐
    - deliver
    - 异常上报
    - customer confirm
    - completed 回读
    - customer 结果回看页
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 10. 执行动作与非执行动作边界是否已明确

- 记录位置：
  - `project-logs/campus-relay/step-30-phase-a-readiness-reassessment.md`
  - `project-logs/campus-relay/bridge-phaseout-evaluation.md`
- 核实结果：
  - `2026-04-10` 已明确：
    - Step 30 只做 `Phase A` 执行准备重新评估
    - 真正的收口动作应放到 Step 31 以后，在回滚和回归都准备好的前提下再做
    - 当前阶段结论是“可以进入 `Phase A` 执行准备重新评估”，不是“现在就执行收口”
- 负责人：
  - Codex
- 日期：
  - `2026-04-10`
- 是否通过：
  - [x] 通过
  - [ ] 不通过
  - [ ] 待人工核实

# Step 26 - repo 外真实资产追补与阻塞项重新评估

## 本轮目标

1. 继续关闭 bridge 的 3 个 repo 外阻塞项。
2. 优先拿 Step 25 缺失的真实外部资产，而不是继续停留在“当前范围未发现命中”。
3. 基于新拿到的 repo 外资产，重新评估三项阻塞，并同步回填 checklist 与评估文档。

## 本轮新增拿到的 repo 外真实资产

### 1. 本机 SSH 配置

- `C:\Users\20278\.ssh\config`
- 关键入口：
  - `xiaoyu_TenXun_Ubuntu`
  - `xiaoyu_root_ALi_Ubuntu`

### 2. 可登录的公网服务器

- `xiaoyu_TenXun_Ubuntu (106.54.211.68)`
- `xiaoyu_root_ALi_Ubuntu (47.243.129.186)`

### 3. repo 外共享文档资产

- `D:\software\校园代送项目_完整交接总结_Step24.md`
- `D:\software\step-25-execution-prompt.md`

## 本轮实际核查入口

1. 先通过 `C:\Users\20278\.ssh\config` 确认可用公网服务器入口。
2. 再 SSH 登录两台公网服务器，检查：
   - `/opt`
   - `/data`
   - `/root`
   - `/home`
   - `/usr/local/bin`
   - `/var/log`
   - 运行进程
   - docker 容器
   - shell 历史
3. 再对 repo 外共享文档做关键字搜索和正文读取。

## 本轮搜索关键字

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `getCourierProfile`
4. `getCourierReviewStatus`
5. `customer_token`
6. `campus`
7. `takeaway`
8. `delivery`

## 三个阻塞项重新评估结果

### 1. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`

- 真实核查结果：
  - 在两台公网服务器上未发现项目部署目录、静态资源目录、nginx 进程或 `access.log`。
  - 在可访问的 repo 外共享文档中，只发现 bridge 阶段说明，没有发现旧页面产物路径或运行时调用证据。
- 证据位置：
  - `C:\Users\20278\.ssh\config`
  - `ssh xiaoyu_TenXun_Ubuntu`
  - `ssh xiaoyu_root_ALi_Ubuntu`
  - `D:\software\校园代送项目_完整交接总结_Step24.md`
  - `D:\software\step-25-execution-prompt.md`
- 阶段判断：
  - 当前已知公网服务器和外部文档范围内未发现命中。
  - 但因为还没有实际业务部署的静态资源目录或历史发布包，所以不能写通过。
- 当前状态：
  - 待人工核实

### 2. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`

- 真实核查结果：
  - 在两台公网服务器上未发现项目部署目录、静态资源目录、nginx 进程或 `access.log`。
  - 在可访问的 repo 外共享文档中，只发现 bridge 阶段说明，没有发现旧页面产物路径或运行时调用证据。
- 证据位置：
  - `C:\Users\20278\.ssh\config`
  - `ssh xiaoyu_TenXun_Ubuntu`
  - `ssh xiaoyu_root_ALi_Ubuntu`
  - `D:\software\校园代送项目_完整交接总结_Step24.md`
  - `D:\software\step-25-execution-prompt.md`
- 阶段判断：
  - 当前已知公网服务器和外部文档范围内未发现命中。
  - 但因为还没有实际业务部署的静态资源目录或历史发布包，所以不能写通过。
- 当前状态：
  - 待人工核实

### 3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

- 真实核查结果：
  - 在两台公网服务器的 `/root`、`/home`、`/usr/local/bin`、`/root/.bash_history` 与 `/var/log` 中，未发现 `customer_token` 和旧 bridge endpoint 命中。
  - 在 repo 外共享文档中，只发现 bridge 执行提示与交接说明，没有发现 `customer_token` 调旧 bridge 的脚本或集合导出。
- 证据位置：
  - `C:\Users\20278\.ssh\config`
  - `ssh xiaoyu_TenXun_Ubuntu`
  - `ssh xiaoyu_root_ALi_Ubuntu`
  - `D:\software\校园代送项目_完整交接总结_Step24.md`
  - `D:\software\step-25-execution-prompt.md`
- 阶段判断：
  - 当前新增外部资产内未发现命中。
  - 但由于仍没有团队共享 Postman / Apifox / 联调脚本资产，所以不能写通过。
- 当前状态：
  - 待人工核实

## 哪些项通过 / 不通过 / 待人工核实

- 通过：
  - 无
- 不通过：
  - 无直接命中证据证明旧 bridge 仍被 repo 外调用，因此本轮没有新增“不通过”闭环项
- 待人工核实：
  - 阻塞项 1
  - 阻塞项 2
  - 阻塞项 3

## 本轮结束后的 bridge 结论

1. 本轮确实拿到了新的 repo 外真实资产。
2. 这些资产把 repo 外边界进一步收窄到：
   - 当前已知公网服务器未承载可见部署物
   - 当前拿到的 repo 外共享文档未暴露旧 bridge 的运行时调用证据
3. 但本轮仍未拿到：
   - 实际业务部署的静态资源目录或历史发布包
   - 可归因的 Nginx / gateway 访问日志
   - 团队共享 Postman / Apifox / 联调脚本资产
4. 因此 bridge 仍不能进入 `Phase A` 执行准备。

## 下一轮建议

1. 继续追真实业务部署物，而不是只看公网服务器是否可达：
   - 当前正式或测试环境静态资源目录
   - 历史发布包、备份包、zip/tar 包
2. 继续追真实访问日志：
   - Nginx access log
   - gateway access log
   - 对应时间窗口的 Referer / UA / 来源 IP
3. 继续追真实共享调试资产：
   - Postman collection / environment
   - Apifox 项目导出
   - 团队共享脚本目录
4. 在 3 个阻塞项都真正关闭前，不要提前宣布 bridge 可进入 `Phase A`，也不要转去补第五个 admin 页。

## Step 27 补充证据

1. 本轮拿到的新增关键外部资产：
   - repo 外前端源码树：`D:\software\GOT\html`
   - Windows Recent 外部文档入口：`C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent`
   - 两台公网服务器上更贴近业务部署面的目录与日志路径核查结果
2. 本轮新增真实核查结果：
   - `D:\software\GOT\html` 中未发现旧 bridge endpoint、辅助函数名或 `customer_token` 命中
   - Windows Recent 证明当前机器近期打开过额外的校园代送工作流文档，但目标文件已缺失
   - `xiaoyu_TenXun_Ubuntu` 与 `xiaoyu_root_ALi_Ubuntu` 的 `/www`、`/var/log/nginx`、`/etc/nginx` 等路径仍未发现业务部署物或访问日志
   - 当前机器不存在常见 `Postman / Apifox` 资产目录
3. 这些证据让 repo 外边界继续收窄，但依然不足以关闭 3 个阻塞项。
4. Step 26 的最终结论保持不变：
   - bridge 仍不能进入 `Phase A` 执行准备

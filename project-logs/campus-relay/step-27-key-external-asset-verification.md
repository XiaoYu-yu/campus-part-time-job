# Step 27 - 关键外部资产追补与阻塞项继续关闭

## 本轮目标

1. 继续关闭 bridge 的 3 个 repo 外阻塞项。
2. 只追真正可能关项的关键外部资产，不重复 Step 26 的泛搜索。
3. 用新拿到的资产重新评估三个阻塞项，并把“还缺什么、理论上在哪里、下一步去哪拿”写实。

## 本轮新增拿到的关键外部资产

### 1. repo 外前端源码 / 历史项目目录

- `D:\software\GOT\html`
- 性质：
  - 当前机器上的 repo 外前端源码树
  - 比 Step 26 的共享文档更接近旧页面代码层

### 2. Windows Recent 外部文档入口

- `C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md.lnk`
- 解析目标：
  - `C:\Users\20278\Desktop\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md`
- 当前状态：
  - 目标文件已不存在
- 含义：
  - 当前机器近期确实打开过额外的 repo 外校园代送工作流文档
  - 但这份外部文档当前不可读取，无法继续提取部署或脚本证据

### 3. 更贴近业务部署面的服务器路径核查结果

- 机器：
  - `xiaoyu_TenXun_Ubuntu (106.54.211.68)`
  - `xiaoyu_root_ALi_Ubuntu (47.243.129.186)`
- 实际核查路径：
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

### 4. 团队共享调试资产入口现状

- 当前机器实际不存在以下常见目录：
  - `C:\Users\20278\AppData\Roaming\Postman`
  - `C:\Users\20278\AppData\Roaming\Apifox`
  - `C:\Users\20278\AppData\Local\Apifox`
  - `C:\Users\20278\Documents\Postman`
  - `C:\Users\20278\Documents\Apifox`
- 这不等于团队没有共享资产，只能说明当前机器上拿不到这些入口。

## 本轮实际核查入口

1. `D:\software\GOT\html`
2. `C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent`
3. `C:\Users\20278\Desktop`
4. `xiaoyu_TenXun_Ubuntu`
5. `xiaoyu_root_ALi_Ubuntu`
6. 本机标准 `Postman / Apifox` 资产目录

## 本轮搜索关键字

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `getCourierProfile`
4. `getCourierReviewStatus`
5. `customer_token`

## 三个阻塞项分别核到了什么

### 1. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`

- 本轮新增核查结果：
  - 在 `D:\software\GOT\html` 排除 `node_modules/dist/build/.venv` 后，对旧 bridge 关键字无命中
  - 两台公网服务器的常见业务部署路径和 nginx/openresty 配置、日志路径无命中
  - Windows Recent 只证明存在额外外部工作流文档入口，不构成旧页面运行时调用证据
- 当前判断：
  - 本轮新增资产范围内未发现命中
  - 但仍没有真实业务静态资源目录或历史发布包
- 是否通过：
  - 否，继续待人工核实

### 2. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`

- 本轮新增核查结果：
  - 在 `D:\software\GOT\html` 排除 `node_modules/dist/build/.venv` 后，对旧 bridge 关键字无命中
  - 两台公网服务器的常见业务部署路径和 nginx/openresty 配置、日志路径无命中
  - Windows Recent 没有提供可继续读取的外部旧页面产物，只指向缺失文档
- 当前判断：
  - 本轮新增资产范围内未发现命中
  - 但仍没有真实业务静态资源目录或历史发布包
- 是否通过：
  - 否，继续待人工核实

### 3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

- 本轮新增核查结果：
  - 当前机器不存在标准 `Postman / Apifox` 资产目录
  - 在 `Downloads / Desktop / Documents / D:\software` 的文本和脚本类资产中，只命中已有外部交接文档，没有命中真实联调脚本或集合
  - 两台公网服务器的 shell 历史和常见脚本目录仍无 `customer_token` 调旧 bridge 命中
- 当前判断：
  - 本轮新增资产范围内未发现命中
  - 但仍没有团队共享 Postman / Apifox / 联调脚本资产
- 是否通过：
  - 否，继续待人工核实

## 哪些项通过 / 不通过 / 待人工核实

- 通过：
  - 无
- 不通过：
  - 无新增直接命中证据，因此本轮没有形成新的“不通过”闭环项
- 待人工核实：
  - 阻塞项 1
  - 阻塞项 2
  - 阻塞项 3

## 为什么还没关掉

### 仍缺的资产

1. 实际业务静态资源目录或历史发布包
2. 可归因的 Nginx / gateway 访问日志
3. 团队共享 Postman / Apifox / 联调脚本资产

### 这些资产理论上应该在哪里

1. 静态资源 / 发布包
   - 当前部署维护人的业务服务器
   - 发布机备份目录
   - 历史发布包或压缩包目录
   - 共享网盘或交接包目录
2. 访问日志
   - 实际业务服务器的 `nginx access.log`
   - gateway / 反向代理日志目录
   - 运维导出的日志片段
3. 调试资产
   - 团队共享 Postman collection / environment
   - Apifox 项目导出
   - 联调脚本目录
   - Wiki / 联调说明 / 测试说明附带脚本

### 当前为什么拿不到

1. 当前已知两台公网服务器不承载可见部署物，也没有 nginx/openresty 业务痕迹
2. 当前机器只有 repo 外交接文档和 Recent 入口，没有真实发布包
3. 当前机器没有标准 `Postman / Apifox` 资产目录，也没有团队共享集合导出
4. 当前环境无法证明具体持有人，只能按资产类型判断需要外部配合

### 下一步应该找谁、去哪里拿

1. 向当前部署维护人索取：
   - 实际业务静态资源目录
   - 历史发布包
   - 备份包
2. 向当前运维或网关维护人索取：
   - 真实时间窗口的 `access.log`
   - gateway / reverse proxy 日志
3. 向当前接口联调维护人索取：
   - 团队共享 Postman / Apifox 集合
   - 联调脚本目录或导出包

## 每项证据位置

1. `D:\software\GOT\html`
2. `C:\Users\20278\AppData\Roaming\Microsoft\Windows\Recent\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md.lnk`
3. `C:\Users\20278\Desktop\工信渝中校园代送平台_Codex提示词补充版_IDEA与HBuilderX工作流.md`（当前缺失）
4. `C:\Users\20278\.ssh\config`
5. `ssh xiaoyu_TenXun_Ubuntu`
6. `ssh xiaoyu_root_ALi_Ubuntu`
7. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
8. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
9. `project-logs/campus-relay/step-25-repo-external-bridge-verification.md`
10. `project-logs/campus-relay/step-26-repo-external-evidence-closure.md`

## 本轮结束后的 bridge 结论

1. 本轮确实拿到了比 Step 26 更接近真实业务运行面的关键外部资产。
2. 这些资产继续证明：
   - 当前新增资产范围内未发现旧 bridge 命中
   - 但“未发现命中”仍不等于“确认无依赖”
3. 由于三类关键业务资产仍缺失：
   - 实际业务静态资源目录或历史发布包
   - 可归因的 Nginx / gateway 访问日志
   - 团队共享 Postman / Apifox / 联调脚本资产
4. 因此 bridge 仍不能进入 `Phase A` 执行准备。

## 下一轮建议

1. 不要再重复同一批服务器的泛搜索。
2. 直接按本日志“仍缺的资产”逐项向外部持有人索取。
3. 只有当 3 个阻塞项都拿到真实关闭证据后，才重新评估是否进入 `Phase A` 执行准备。

## Step 28 补充证据

1. 本轮没有重复 Step 27 已经做过的外部源码树和服务器路径搜索，而是继续追：
   - 云盘下载根路径与下载日志
   - 真实外部压缩包
   - 服务器 shell 历史
2. 本轮新增真实核查结果：
   - `aDrive` 配置与日志证明 `D:\software` 是当前机器真实云盘下载根路径
   - `D:\software\GOT\html\project.zip` 是真实外部压缩包，但内部项目不是校园代送
   - 两台公网服务器 shell 历史中未发现业务部署痕迹
3. 这些证据进一步说明：
   - 当前已知服务器不像校园代送部署持有机
   - 当前已知云盘根路径里还没有拿到校园代送项目自己的真实发布包
4. 因此 Step 27 的最终结论继续保持：
   - bridge 仍不能进入 `Phase A` 执行准备

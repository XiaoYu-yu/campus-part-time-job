# Step 28 - 关键业务资产追补与阻塞项继续关闭

## 本轮目标

1. 继续关闭 bridge 的 3 个 repo 外阻塞项。
2. 不再重复 Step 25 到 Step 27 已做过的泛目录搜索。
3. 只追真正可能关项的关键业务资产，并把“资产持有人和获取路径”写实。

## 本轮拿到的新的关键业务资产

### 1. 云盘下载根路径与下载日志

- `C:\Users\20278\AppData\Roaming\aDrive\preference.json`
- `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log`
- 来源：
  - 当前机器阿里云盘客户端配置与运行日志
- 资产含义：
  - 证明 `D:\software` 是真实云盘下载根路径
  - 证明 `D:\software` 属于 repo 外资产持有入口

### 2. repo 外真实压缩包

- `D:\software\GOT\html\project.zip`
- 来源：
  - 当前机器云盘下载根路径下的真实外部压缩包
- 资产含义：
  - 证明当前已经拿到一份真实外部构建产物级资产
  - 但其项目身份仍需核查

### 3. 更接近部署持有面的服务器 shell 历史

- `xiaoyu_TenXun_Ubuntu:/root/.bash_history`
- `xiaoyu_TenXun_Ubuntu:/home/ubuntu/.bash_history`
- `xiaoyu_root_ALi_Ubuntu:/root/.bash_history`
- 来源：
  - 两台已知公网服务器的真实 shell 历史
- 资产含义：
  - 用于判断这些机器是否像历史发布机或静态资源承载机

## 每类资产的真实路径、来源、机器或持有人

1. `C:\Users\20278\AppData\Roaming\aDrive\preference.json`
   - 机器：当前 Windows 工作机
   - 来源：阿里云盘客户端配置
   - 持有人类型：当前机器云盘资产下载持有人
2. `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log`
   - 机器：当前 Windows 工作机
   - 来源：阿里云盘客户端运行日志
   - 持有人类型：当前机器云盘资产下载持有人
3. `D:\software\GOT\html\project.zip`
   - 机器：当前 Windows 工作机
   - 来源：云盘下载根路径下的真实压缩包
   - 持有人类型：当前机器云盘下载资产持有人
4. `xiaoyu_TenXun_Ubuntu`
   - 机器：`106.54.211.68`
   - 来源：`C:\Users\20278\.ssh\config`
   - 持有人类型：当前已知公网服务器维护入口
5. `xiaoyu_root_ALi_Ubuntu`
   - 机器：`47.243.129.186`
   - 来源：`C:\Users\20278\.ssh\config`
   - 持有人类型：当前已知公网服务器维护入口

## 本轮实际使用的核查入口

1. `C:\Users\20278\AppData\Roaming\aDrive\preference.json`
2. `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log`
3. `D:\software\GOT\html\project.zip`
4. `ssh xiaoyu_TenXun_Ubuntu`
5. `ssh xiaoyu_root_ALi_Ubuntu`

## 本轮搜索关键字

1. `/api/campus/courier/profile`
2. `/api/campus/courier/review-status`
3. `getCourierProfile`
4. `getCourierReviewStatus`
5. `customer_token`
6. `nginx`
7. `openresty`
8. `deploy`
9. `campus`
10. `delivery`
11. `takeaway`

## 三个阻塞项分别核到了什么

### 1. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`

- 本轮核查结果：
  - `aDrive` 配置和日志证明 `D:\software` 是真实云盘资产根路径
  - `D:\software\GOT\html\project.zip` 是真实外部压缩包，但内部项目为 `healthy-management`
  - 在该压缩包内未发现旧 bridge endpoint 或辅助函数名命中
  - 两台公网服务器 shell 历史中未发现业务部署痕迹
- 当前结论：
  - 本轮新增资产范围内未发现命中
  - 但仍缺校园代送项目自己的真实业务静态资源目录或历史发布包
  - 所以本项不能写通过

### 2. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`

- 本轮核查结果：
  - 与第 1 项同一批新增资产
  - `project.zip` 内对旧 bridge 关键字无命中
  - 两台公网服务器 shell 历史中未发现部署痕迹
- 当前结论：
  - 本轮新增资产范围内未发现命中
  - 但仍缺校园代送项目自己的真实业务静态资源目录或历史发布包，以及可归因访问日志
  - 所以本项不能写通过

### 3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

- 本轮核查结果：
  - `project.zip` 内对 `customer_token` 无命中
  - 当前云盘根路径下没有拿到团队共享 Postman / Apifox / 联调脚本资产
  - 两台公网服务器 shell 历史也未发现 `customer_token` 调旧 bridge 的命令痕迹
- 当前结论：
  - 本轮新增资产范围内未发现命中
  - 但仍缺团队共享 Postman / Apifox / 联调脚本资产
  - 所以本项不能写通过

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

### 仍缺的关键资产

1. 校园代送项目自己的真实业务静态资源目录或历史发布包
2. 可归因的 Nginx / gateway / reverse proxy 访问日志
3. 团队共享 Postman / Apifox / 联调脚本资产

### 这些资产理论上应该在哪里

1. 静态资源 / 发布包
   - 当前部署维护人的业务服务器
   - 发布机备份目录
   - 历史发布包或压缩包目录
   - 共享网盘或交接包目录
2. 访问日志
   - 实际业务服务器的 `access.log`
   - gateway / reverse proxy 日志目录
   - 运维导出的时间窗口日志片段
3. 调试资产
   - 团队共享 Postman collection / environment
   - 团队共享 Apifox 项目或导出文件
   - 联调脚本目录或共享脚本包

### 当前为什么拿不到

1. 当前已知两台公网服务器不像实际部署或发布持有机
2. 当前云盘根路径虽已确认，但还没有拿到校园代送项目自己的真实发布包
3. 当前没有可归因访问日志入口
4. 当前没有团队共享 Postman / Apifox / 脚本导出入口

### 下一步应该找谁、去哪里拿

1. 向当前部署维护人或发布机持有人索取：
   - 校园代送真实静态资源目录
   - 历史发布包 / 备份包 / 压缩包
2. 向运维或网关维护人索取：
   - 指定时间窗口的 `access.log`
   - gateway / reverse proxy 日志片段
3. 向接口联调维护人、测试负责人或共享资产维护人索取：
   - Postman collection / environment
   - Apifox 项目导出
   - 联调脚本目录或导出包

## 每项证据位置

1. `C:\Users\20278\AppData\Roaming\aDrive\preference.json`
2. `C:\Users\20278\AppData\Roaming\aDrive\logs\main.log`
3. `D:\software\GOT\html\project.zip`
4. `C:\Users\20278\.ssh\config`
5. `ssh xiaoyu_TenXun_Ubuntu`
6. `ssh xiaoyu_root_ALi_Ubuntu`
7. `project-logs/campus-relay/bridge-execution-readiness-checklist.md`
8. `project-logs/campus-relay/bridge-phaseout-evaluation.md`
9. `project-logs/campus-relay/step-25-repo-external-bridge-verification.md`
10. `project-logs/campus-relay/step-26-repo-external-evidence-closure.md`
11. `project-logs/campus-relay/step-27-key-external-asset-verification.md`

## 本轮结束后的 bridge 结论

1. Step 28 确实拿到了比 Step 27 更接近真实业务持有面的资产。
2. 这些资产继续证明：
   - 当前新增资产范围内未发现旧 bridge 命中
   - 但“未发现命中”仍不等于“确认无依赖”
3. 因为三类真正能关项的关键资产仍未拿到，bridge 仍不能进入 `Phase A` 执行准备。

## 下一轮建议

1. 不要再回到泛目录搜索。
2. 直接按本日志列出的“持有人类型”和“理论位置”去追三类关键资产。
3. 只有当三个阻塞项都拿到真实关闭证据后，才重新评估是否进入 `Phase A` 执行准备。

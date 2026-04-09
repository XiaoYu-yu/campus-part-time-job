# bridge 执行准备人工核实 checklist

## 使用说明

1. 本文档只用于人工核实与留痕，不代表当前已完成这些核实项。
2. 每项都应由明确负责人填写结果、日期和记录位置。
3. 若某项无法从当前仓库代码直接证明，应明确填写“待人工核实”，不要主观补结论。

## 核实项

### 1. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/profile`

- 核实方法：
  - 盘点仍在线的旧页面、外部前端包、部署脚本或静态资源
  - 检查网关/访问日志或人工联调记录
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 2. 仓库外旧页面是否仍直接调用 `GET /api/campus/courier/review-status`

- 核实方法：
  - 盘点仍在线的旧页面、外部前端包、部署脚本或静态资源
  - 检查网关/访问日志或人工联调记录
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 3. 是否仍有手工联调脚本依赖 `customer_token` 访问旧 bridge

- 核实方法：
  - 盘点本地脚本、测试说明、团队常用 Postman/Apifox 集合
  - 人工确认是否仍通过 `customer_token` 调用旧 bridge 读取资料或审核状态
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 4. workbench 的 profile / review-status 请求是否全程只走 `courier_token`

- 核实方法：
  - 打开浏览器开发者工具 Network
  - 访问 `/courier/workbench`
  - 观察 `/api/campus/courier/profile` 与 `/api/campus/courier/review-status` 的 `Authorization` 头来源
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

### 5. 是否已有一轮 `customer onboarding -> token 申请 -> workbench -> 接单 -> 取餐 -> deliver -> 异常上报` 的稳定联调记录

- 核实方法：
  - 按 [bridge-regression-template.md](bridge-regression-template.md) 执行完整链路
  - 确认每一步都有实际结果、截图或日志占位
- 记录位置：
  - 
- 核实结果：
  - 
- 负责人：
  - 
- 日期：
  - 
- 是否通过：
  - [ ] 通过
  - [ ] 不通过
  - [ ] 待人工核实

## 收口前判断

- 是否可以进入 `Phase A` 执行准备：
  - [ ] 可以
  - [ ] 还不可以
- 若还不可以，阻塞项：
  - 

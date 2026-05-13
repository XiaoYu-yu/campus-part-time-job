# Step 160 - 移动端界面文案接地气优化

## 本轮目标

把用户端和兼职端里偏工程、偏文档口吻的界面文字改成更像真实校园兼职 App 的中文表达，减少 `token`、接口、字段、回读、承接等开发语境在普通用户界面中的出现。

## 实际修改范围

本轮只做前端文案和少量模板缩进整理，没有改业务逻辑、接口调用、路由、鉴权、bridge、token 附着逻辑或后端代码。

修改页面：

1. `frontend/src/views/user/Home.vue`
2. `frontend/src/views/user/Login.vue`
3. `frontend/src/views/user/Profile.vue`
4. `frontend/src/views/user/CampusRelayOrders.vue`
5. `frontend/src/views/user/CampusOrderResult.vue`
6. `frontend/src/views/user/CourierOnboarding.vue`
7. `frontend/src/views/user/AfterSaleResult.vue`
8. `frontend/src/views/courier/Login.vue`
9. `frontend/src/views/courier/Profile.vue`
10. `frontend/src/views/courier/CourierWorkbench.vue`

## 主要文案调整

### 用户端

1. 首页从“校园代送、订单回看、兼职入驻申请入口”等偏功能说明，改为“发代送单、查订单、报名做兼职”等更直白表达。
2. 发布代送页保留现有字段与表单逻辑，但把“外部订单号”“执行模拟支付”等表述改成“平台订单号”“使用模拟支付，不会真实扣费”。
3. 订单结果页把“结果回看”“送达结果确认”等表达改成“查看进度”“确认已经收到 / 已确认收货”。
4. 兼职报名页把“courier token / onboarding / DTO”等语境隐藏为“开通兼职端登录”“接单资格”“报名资料”。
5. 售后结果页把“回执、决策类型、决策金额”调整为“售后处理结果、处理方式、涉及金额”等更适合用户理解的表述。

### 兼职端

1. 登录页从“换取 courier token”改成普通“手机号 + 密码登录”。
2. 兼职资料页从“profile / review-status 读取”改为“查看资料和审核状态”。
3. 工作台把“最小承接、只读回读、接口字段、token 状态”等表述改成“接单中、可接任务、订单详情、登录状态”。
4. 取餐区域把“取餐凭证路径 / 受控文件路径”改为“取餐凭证图片地址”，并给出测试可填地址。
5. 异常上报区域从“最小异常上报承接”改成“上报异常”，按钮改为“提交异常”。

## 明确未改动

1. 未改后端 Java、数据库、接口路径或状态机。
2. 未改 `frontend/src/utils/request.js`。
3. 未改任何 token 附着、存储或鉴权策略。
4. 未改 bridge 主线。
5. 未改路由主链路。
6. 未删除旧外卖兼容模块。
7. 未新增页面。

## 验证结果

1. `npm run build`：通过。
2. `npm run build:android:user:public`：通过。
3. `npm run build:android:parttime:public`：通过。
4. `git diff --check`：无空白错误，仅 CRLF 换行提示。

## 当前仍需关注

1. 本轮是文案优化，不等同于完成真机视觉复测。
2. 仍建议后续在真实手机上快速点一遍用户端首页、发单页、结果页、报名页、兼职端登录页、工作台，确认文字没有被截断。
3. 部分技术类变量名和 CSS class 中仍包含 `token / courier / customer`，这是代码内部命名，不是页面可见文案，本轮不改。

## 当前 bridge 结论

bridge 主线继续保持 `Phase A no-op` 冻结态。本轮没有触发任何恢复推进条件。

## 下一轮建议

1. 若继续做 UI，优先做真机截图级验收，而不是继续盲改文案。
2. 若准备给他人试用，优先补 APK 安装说明、测试账号说明、反馈模板和已知限制清单。
3. 若要继续提升产品完整度，建议先做 Android release 签名包和小范围内测回归矩阵。

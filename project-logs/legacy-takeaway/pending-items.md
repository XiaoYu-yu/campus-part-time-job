# 待处理事项

## 已知未阻塞项

- 前端构建仍有 Sass `@import` 弃用告警，涉及：
  - [frontend/src/styles/global.scss](D:/20278/code/show_shop1/frontend/src/styles/global.scss)
  - [frontend/src/styles/element-plus.scss](D:/20278/code/show_shop1/frontend/src/styles/element-plus.scss)
- 前端构建仍有大体积 chunk 告警，主要集中在统计和图表相关资源

## 后续建议

1. 将 Sass `@import` 迁移为 `@use` / `@forward`
2. 对统计页和图表模块做按路由或按组件拆包
3. 为用户端订单、购物车、地址链路补更完整的集成测试
4. 为生产环境补充验证码、限流、登录失败锁定、日志审计和异常告警
5. 如需多环境发布，继续补齐 Flyway 或 Liquibase 自动迁移接入
6. 为上传链路接入对象存储和病毒扫描能力
7. 生产运行时优先使用 JDK 17 或 21 LTS，避免高版本 JDK 下的三方依赖兼容性告警

## 本轮未做的事情

- 没有新增视觉美化型页面
- 没有重构 UI 风格
- 没有引入支付、短信验证码、消息推送等扩展能力
- 没有接入生产级对象存储、WAF、集中日志、监控告警平台

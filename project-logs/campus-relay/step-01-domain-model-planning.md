# Step 01 - 领域模型重构规划

## 本轮目标

把现有 `show_shop1` 从“普通外卖平台”迁移到“重庆工信职业学院渝中校区校园代送平台”的第一轮方案锁定为可执行文档，明确：

- 哪些模块复用
- 哪些模块替换
- 新领域对象是什么
- 状态机怎么定义
- 下一轮从哪里下手

本轮只做规划落档，不做代码和数据库改造。

## 改前是什么

- 后端实体和数据库仍围绕菜品、套餐、购物车、收货地址、外卖订单展开
- 前端同时包含管理端和用户点餐端，用户链路是典型点餐结算流程
- 日志目录中还没有独立的校园代送改造记录
- `docs/README.md` 中也没有校园代送专用文档入口

## 本轮改后准备成什么

### 1. 业务模型

- 校园代送将采用并行新增领域模型，而不是在旧外卖表上硬改字段
- 普通用户继续复用 `user` 基础身份
- 管理员继续复用 `employee`
- 配送员独立建模为 `CourierProfile`

### 2. 核心对象

本轮已经锁定未来核心对象：

- `RelayOrder`
- `CourierProfile`
- `PickupPoint`
- `DeliveryTarget`
- `CourierLocationReport`
- `SettlementRecord`

### 3. 核心状态机

本轮已经锁定：

- 订单状态
- 模拟支付状态
- 配送员审核状态
- 宿舍楼 5 分钟优先接单窗口
- 已取餐后不可直接取消，只能售后

### 4. 技术路径

- 后端继续使用现有 Spring Boot 工程
- 校园代送新包根固定为 `com.cangqiong.takeaway.campus`
- 新表默认使用 `campus_` 前缀
- 前端管理端继续留在 `frontend/`
- 正式移动端推迟到后续 `uni-app/`

## 为什么本轮不直接改代码

1. 当前旧外卖语义和校园代送语义差距太大，若不先定规则就直接写数据库和接口，后续高概率返工
2. 宿舍优先接单、办公室门口、图书馆限制、模拟支付、待结算等规则需要先有统一口径，不能让后端、前端和日志各写一套
3. 旧模块较多，必须先写清楚哪些复用、哪些隐藏、哪些延后删除，否则下一轮容易误删现有可运行链路

## 本轮新增文件

- `docs/campus-relay/domain-refactor-plan.md`
- `docs/campus-relay/legacy-to-campus-mapping.md`
- `project-logs/campus-relay/summary.md`
- `project-logs/campus-relay/pending-items.md`
- `project-logs/campus-relay/file-change-list.md`
- `project-logs/campus-relay/step-01-domain-model-planning.md`

## 本轮修改文件

- `docs/README.md`

## 验证方式

本轮没有运行后端或前端测试，原因是：

- 本轮不涉及运行时代码变化
- 本轮验证重点是文档完整性和索引可达性

本轮应验证：

1. `docs/README.md` 已出现校园代送规划入口
2. `docs/campus-relay/` 下两份文档已创建
3. `project-logs/campus-relay/` 下四份日志文件已创建
4. 规划文档已明确标注“规划中，未实现”

## 当前仍未解决的问题

1. 数据库表还没建
2. Java 实体、Mapper、Service、Controller 还没写
3. 管理端菜单还没替换
4. 用户 Web 仍然是点餐链路
5. 移动端工程还不存在

## 下一轮建议

下一轮直接进入“后端领域与数据库改造”，优先顺序如下：

1. 新增 `campus_` 表和初始化脚本
2. 新增校园代送实体、DTO、VO、Mapper
3. 把普通用户、配送员、管理员三类接口命名空间搭起来
4. 先实现代送主订单闭环，再逐步替换前端页面

## 本轮结论

本轮已经把“从苍穹外卖风格项目改造成校园代送平台”的高层设计和旧模块去留方案写成独立文档，下一轮可以直接开始落库和落后端，不需要再回头讨论是否并行建模、是否真实支付、是否复用 `employee` 等基础问题。

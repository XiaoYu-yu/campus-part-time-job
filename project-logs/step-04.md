# Step 04 统一统计接口

## 做了什么

- 后端新增并补齐 `/api/statistics/dashboard`、`/sales`、`/users`、`/popular-dishes`
- 增加统计所需聚合 SQL 与 VO
- 调整仪表盘状态展示以兼容整数状态码

## 为什么这么改

- 前端仪表盘和统计页原先依赖的接口并不完整，页面只能停留在“页面已写好但真实接口不齐”的状态

## 验证结果

- Dashboard 与 Statistics 页面可直接请求真实统计接口
- 新接口返回结构已按前端现有消费方式组织

## 修改文件

- [backend/src/main/java/com/cangqiong/takeaway/controller/StatisticsController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/StatisticsController.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/StatisticsService.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/StatisticsService.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/StatisticsServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/StatisticsServiceImpl.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/StatisticsMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/StatisticsMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/LabelValueVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/LabelValueVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/PopularDishVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/PopularDishVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/SalesTrendVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/SalesTrendVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/UserStatisticsDetailVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/UserStatisticsDetailVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/DashboardStatisticsVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/DashboardStatisticsVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/ChartSeriesVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/ChartSeriesVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/TimeSeriesVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/TimeSeriesVO.java)
- [frontend/src/views/Dashboard.vue](D:/20278/code/show_shop1/frontend/src/views/Dashboard.vue)

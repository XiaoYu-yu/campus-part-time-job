# Step 03 统一订单接口契约

## 做了什么

- 统一前后端订单查询参数为 `orderNo/customerName/status/beginTime/endTime/page/pageSize`
- 后端状态字段统一为整数
- 前端负责状态中文映射
- 调整订单页请求、分页和状态更新逻辑

## 为什么这么改

- 旧实现里前后端参数命名、状态类型、时间字段都不一致，导致页面筛选和状态修改容易失效

## 验证结果

- 管理端订单页按统一参数发起请求
- 后端可按统一查询对象处理分页查询
- 状态更新使用整数值，不再传中文状态

## 修改文件

- [frontend/src/utils/request.js](D:/20278/code/show_shop1/frontend/src/utils/request.js)
- [frontend/src/api/order.js](D:/20278/code/show_shop1/frontend/src/api/order.js)
- [frontend/src/views/Order.vue](D:/20278/code/show_shop1/frontend/src/views/Order.vue)
- [backend/src/main/java/com/cangqiong/takeaway/query/OrderQuery.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/query/OrderQuery.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/OrderController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/OrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/OrderMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/OrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/OrderServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/OrderServiceImpl.java)

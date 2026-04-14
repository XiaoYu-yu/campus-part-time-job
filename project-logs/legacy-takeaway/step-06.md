# Step 06 用户端去 mock 化

## 做了什么

- 新增用户登录、用户信息、购物车、地址、用户订单、店铺营业状态相关后端接口
- 扩展数据库脚本，新增地址和店铺配置表
- 用户端页面改为真实请求与真实状态
- `Checkout.vue` 和 `Profile.vue` 使用内嵌地址管理，不额外扩展独立地址页面

## 为什么这么改

- 这一阶段的目标不是做新页面，而是把已有用户端页面真正跑在真实数据上

## 验证结果

- 用户登录、购物车、下单、订单、个人信息、店铺营业状态链路已接入真实接口
- 用户端核心页面不再依赖 `frontend/src/stores/mock.js`

## 修改文件

- [backend/db/init.sql](D:/20278/code/show_shop1/backend/db/init.sql)
- [backend/src/main/java/com/cangqiong/takeaway/controller/PublicController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/PublicController.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/UserController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/UserController.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/CartController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/CartController.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/AddressController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/AddressController.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/ShopController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/ShopController.java)
- [backend/src/main/java/com/cangqiong/takeaway/controller/UserOrderController.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/controller/UserOrderController.java)
- [backend/src/main/java/com/cangqiong/takeaway/entity/User.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/entity/User.java)
- [backend/src/main/java/com/cangqiong/takeaway/entity/Cart.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/entity/Cart.java)
- [backend/src/main/java/com/cangqiong/takeaway/entity/Address.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/entity/Address.java)
- [backend/src/main/java/com/cangqiong/takeaway/entity/ShopConfig.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/entity/ShopConfig.java)
- [backend/src/main/java/com/cangqiong/takeaway/entity/ShopBusinessHour.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/entity/ShopBusinessHour.java)
- [backend/src/main/java/com/cangqiong/takeaway/dto/UserLoginDTO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/dto/UserLoginDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/dto/CartItemDTO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/dto/CartItemDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/dto/CartItemUpdateDTO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/dto/CartItemUpdateDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/dto/AddressDTO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/dto/AddressDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/dto/BusinessHourDTO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/dto/BusinessHourDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/dto/ShopStatusDTO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/dto/ShopStatusDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/dto/UserSubmitOrderDTO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/dto/UserSubmitOrderDTO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/UserVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/UserVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/CartItemVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/CartItemVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/ShopBusinessHourVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/ShopBusinessHourVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/vo/ShopStatusVO.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/vo/ShopStatusVO.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/CategoryMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/CategoryMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/DishMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/DishMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/OrderMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/OrderMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/UserMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/UserMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/CartMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/CartMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/AddressMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/AddressMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/mapper/ShopConfigMapper.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/mapper/ShopConfigMapper.java)
- [backend/src/main/java/com/cangqiong/takeaway/service/impl/OrderServiceImpl.java](D:/20278/code/show_shop1/backend/src/main/java/com/cangqiong/takeaway/service/impl/OrderServiceImpl.java)
- [frontend/src/api/public.js](D:/20278/code/show_shop1/frontend/src/api/public.js)
- [frontend/src/api/customer.js](D:/20278/code/show_shop1/frontend/src/api/customer.js)
- [frontend/src/api/cart.js](D:/20278/code/show_shop1/frontend/src/api/cart.js)
- [frontend/src/api/address.js](D:/20278/code/show_shop1/frontend/src/api/address.js)
- [frontend/src/api/customer-order.js](D:/20278/code/show_shop1/frontend/src/api/customer-order.js)
- [frontend/src/api/shop.js](D:/20278/code/show_shop1/frontend/src/api/shop.js)
- [frontend/src/layout/MainLayout.vue](D:/20278/code/show_shop1/frontend/src/layout/MainLayout.vue)
- [frontend/src/layout/UserLayout.vue](D:/20278/code/show_shop1/frontend/src/layout/UserLayout.vue)
- [frontend/src/views/user/Home.vue](D:/20278/code/show_shop1/frontend/src/views/user/Home.vue)
- [frontend/src/views/user/Category.vue](D:/20278/code/show_shop1/frontend/src/views/user/Category.vue)
- [frontend/src/views/user/DishDetail.vue](D:/20278/code/show_shop1/frontend/src/views/user/DishDetail.vue)
- [frontend/src/views/user/Cart.vue](D:/20278/code/show_shop1/frontend/src/views/user/Cart.vue)
- [frontend/src/views/user/Checkout.vue](D:/20278/code/show_shop1/frontend/src/views/user/Checkout.vue)
- [frontend/src/views/user/Orders.vue](D:/20278/code/show_shop1/frontend/src/views/user/Orders.vue)
- [frontend/src/views/user/Profile.vue](D:/20278/code/show_shop1/frontend/src/views/user/Profile.vue)
- [frontend/src/views/ShopStatus.vue](D:/20278/code/show_shop1/frontend/src/views/ShopStatus.vue)

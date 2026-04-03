# API设计文档

## 1. 接口设计规范

### 1.1 统一接口命名风格
- **RESTful风格**：使用HTTP方法表示操作类型
- **URL格式**：`/api/{模块}/{资源}/{操作}`
- **示例**：`GET /api/employees` 获取员工列表

### 1.2 统一请求参数格式
- **查询参数**：用于过滤、排序、分页
- **路径参数**：用于标识资源ID
- **请求体**：用于创建或更新资源，使用JSON格式

### 1.3 统一响应结构
```json
{
  "code": 200,
  "message": "成功",
  "data": {}
}
```
- **code**：状态码，200表示成功，其他表示错误
- **message**：响应消息
- **data**：响应数据

### 1.4 统一分页格式
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```
- **records**：数据列表
- **total**：总记录数
- **size**：每页大小
- **current**：当前页码
- **pages**：总页数

### 1.5 统一状态字段和值
- **启用/禁用**：1-启用，0-禁用
- **订单状态**：待支付、处理中、已完成、已取消

### 1.6 统一登录鉴权规则
- **认证方式**：JWT令牌
- **令牌传递**：在请求头中添加 `Authorization: Bearer {token}`
- **过期时间**：默认24小时

### 1.7 图片上传和静态资源
- **上传接口**：`POST /api/upload/image`
- **返回格式**：返回图片URL
- **存储路径**：`/uploads/images/`

## 2. 接口清单

### 2.1 认证模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/auth/login` | `POST` | 用户登录 | `{"phone": "13800138001", "password": "123456"}` | `{"token": "...", "user": {...}}` |
| `/api/auth/logout` | `POST` | 用户登出 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/auth/refresh` | `POST` | 刷新令牌 | N/A | `{"token": "..."}` |

### 2.2 员工管理模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/employees` | `GET` | 获取员工列表 | `page`, `size`, `name`, `phone` | 分页数据 |
| `/api/employees/{id}` | `GET` | 获取员工详情 | N/A | 员工信息 |
| `/api/employees` | `POST` | 新增员工 | `{"name": "张三", "phone": "13800138001", "position": "经理", "department": "管理部", "entryDate": "2023-01-01"}` | 员工信息 |
| `/api/employees/{id}` | `PUT` | 更新员工 | `{"name": "张三", "phone": "13800138001", "position": "经理", "department": "管理部", "entryDate": "2023-01-01"}` | 员工信息 |
| `/api/employees/{id}` | `DELETE` | 删除员工 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/employees/{id}/status` | `PUT` | 更新员工状态 | `{"status": 1}` | `{"code": 200, "message": "成功"}` |

### 2.3 分类管理模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/categories` | `GET` | 获取分类列表 | N/A | 分类列表 |
| `/api/categories/{id}` | `GET` | 获取分类详情 | N/A | 分类信息 |
| `/api/categories` | `POST` | 新增分类 | `{"name": "热菜", "sort": 1}` | 分类信息 |
| `/api/categories/{id}` | `PUT` | 更新分类 | `{"name": "热菜", "sort": 1}` | 分类信息 |
| `/api/categories/{id}` | `DELETE` | 删除分类 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/categories/{id}/status` | `PUT` | 更新分类状态 | `{"status": 1}` | `{"code": 200, "message": "成功"}` |
| `/api/categories/{id}/sort` | `PUT` | 更新分类排序 | `{"sort": 1}` | `{"code": 200, "message": "成功"}` |

### 2.4 菜品管理模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/dishes` | `GET` | 获取菜品列表 | `page`, `size`, `name`, `categoryId` | 分页数据 |
| `/api/dishes/{id}` | `GET` | 获取菜品详情 | N/A | 菜品信息 |
| `/api/dishes` | `POST` | 新增菜品 | `{"name": "红烧肉", "categoryId": 1, "price": 88.00, "description": "传统红烧肉", "image": "url"}` | 菜品信息 |
| `/api/dishes/{id}` | `PUT` | 更新菜品 | `{"name": "红烧肉", "categoryId": 1, "price": 88.00, "description": "传统红烧肉", "image": "url"}` | 菜品信息 |
| `/api/dishes/{id}` | `DELETE` | 删除菜品 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/dishes/{id}/status` | `PUT` | 更新菜品状态 | `{"status": 1}` | `{"code": 200, "message": "成功"}` |
| `/api/dishes/status/batch` | `PUT` | 批量更新菜品状态 | `{"ids": [1, 2], "status": 1}` | `{"code": 200, "message": "成功"}` |

### 2.5 套餐管理模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/setmeals` | `GET` | 获取套餐列表 | `page`, `size`, `name`, `categoryId` | 分页数据 |
| `/api/setmeals/{id}` | `GET` | 获取套餐详情 | N/A | 套餐信息（包含菜品） |
| `/api/setmeals` | `POST` | 新增套餐 | `{"name": "双人套餐", "categoryId": 1, "price": 168.00, "description": "双人份套餐", "image": "url", "dishIds": [1, 2]}` | 套餐信息 |
| `/api/setmeals/{id}` | `PUT` | 更新套餐 | `{"name": "双人套餐", "categoryId": 1, "price": 168.00, "description": "双人份套餐", "image": "url", "dishIds": [1, 2]}` | 套餐信息 |
| `/api/setmeals/{id}` | `DELETE` | 删除套餐 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/setmeals/{id}/status` | `PUT` | 更新套餐状态 | `{"status": 1}` | `{"code": 200, "message": "成功"}` |
| `/api/setmeals/status/batch` | `PUT` | 批量更新套餐状态 | `{"ids": [1, 2], "status": 1}` | `{"code": 200, "message": "成功"}` |

### 2.6 购物车管理模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/cart` | `GET` | 获取购物车列表 | N/A | 购物车列表 |
| `/api/cart` | `POST` | 添加购物车商品 | `{"dishId": 1, "quantity": 1}` 或 `{"setmealId": 1, "quantity": 1}` | 购物车商品 |
| `/api/cart/{id}` | `PUT` | 更新购物车商品 | `{"quantity": 2, "checked": 1}` | 购物车商品 |
| `/api/cart/{id}` | `DELETE` | 删除购物车商品 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/cart/clear` | `DELETE` | 清空购物车 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/cart/check` | `PUT` | 全选/取消全选 | `{"checked": 1}` | `{"code": 200, "message": "成功"}` |

### 2.7 订单管理模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/orders` | `GET` | 获取订单列表 | `page`, `size`, `status`, `startDate`, `endDate` | 分页数据 |
| `/api/orders/{id}` | `GET` | 获取订单详情 | N/A | 订单信息（包含明细） |
| `/api/orders` | `POST` | 创建订单 | `{"customerName": "张三", "customerPhone": "13800138001", "customerAddress": "北京市朝阳区", "items": [{"dishId": 1, "quantity": 1}, {"setmealId": 1, "quantity": 1}]}` | 订单信息 |
| `/api/orders/{id}/status` | `PUT` | 更新订单状态 | `{"status": "处理中"}` | `{"code": 200, "message": "成功"}` |
| `/api/orders/{id}/pay` | `PUT` | 支付订单 | N/A | `{"code": 200, "message": "成功"}` |
| `/api/orders/{id}/cancel` | `PUT` | 取消订单 | N/A | `{"code": 200, "message": "成功"}` |

### 2.8 用户管理模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/users/me` | `GET` | 获取当前用户信息 | N/A | 用户信息 |
| `/api/users/me` | `PUT` | 更新用户信息 | `{"name": "张三", "avatar": "url"}` | 用户信息 |
| `/api/users/password` | `PUT` | 修改密码 | `{"oldPassword": "123456", "newPassword": "654321"}` | `{"code": 200, "message": "成功"}` |
| `/api/users` | `POST` | 注册新用户 | `{"name": "张三", "phone": "13800138001", "password": "123456"}` | 用户信息 |

### 2.9 上传模块
| 接口路径 | 方法 | 功能描述 | 请求参数 | 响应数据 |
| :--- | :--- | :--- | :--- | :--- |
| `/api/upload/image` | `POST` | 上传图片 | `multipart/form-data` 形式的文件 | `{"url": "uploads/images/20231201123456.jpg"}` |

## 3. 字段说明

### 3.1 通用字段
- **id**：资源唯一标识
- **created_at**：创建时间
- **updated_at**：更新时间
- **status**：状态（1-启用，0-禁用）

### 3.2 员工相关字段
- **name**：员工姓名
- **phone**：手机号
- **position**：职位
- **department**：部门
- **entry_date**：入职日期

### 3.3 分类相关字段
- **name**：分类名称
- **sort**：排序

### 3.4 菜品相关字段
- **name**：菜品名称
- **category_id**：分类ID
- **price**：价格
- **description**：描述
- **image**：图片URL

### 3.5 套餐相关字段
- **name**：套餐名称
- **category_id**：分类ID
- **price**：价格
- **description**：描述
- **image**：图片URL
- **dish_ids**：包含的菜品ID列表

### 3.6 购物车相关字段
- **user_id**：用户ID
- **dish_id**：菜品ID（二选一）
- **setmeal_id**：套餐ID（二选一）
- **quantity**：数量
- **checked**：是否选中

### 3.7 订单相关字段
- **id**：订单号
- **user_id**：用户ID
- **customer_name**：客户姓名
- **customer_phone**：联系电话
- **customer_address**：配送地址
- **total_amount**：总金额
- **status**：订单状态
- **payment_time**：支付时间
- **delivery_time**：送达时间

### 3.8 订单明细相关字段
- **order_id**：订单号
- **dish_id**：菜品ID（二选一）
- **setmeal_id**：套餐ID（二选一）
- **name**：商品名称
- **price**：单价
- **quantity**：数量
- **total**：小计

### 3.9 用户相关字段
- **name**：用户名
- **phone**：手机号
- **password**：密码（加密存储）
- **avatar**：头像URL

## 4. 响应示例

### 4.1 成功响应
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "13800138001",
    "position": "经理",
    "department": "管理部",
    "entryDate": "2023-01-01",
    "status": 1,
    "createdAt": "2023-01-01T00:00:00",
    "updatedAt": "2023-01-01T00:00:00"
  }
}
```

### 4.2 分页响应
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "张三",
        "phone": "13800138001",
        "position": "经理",
        "department": "管理部",
        "entryDate": "2023-01-01",
        "status": 1
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 4.3 错误响应
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null
}
```

## 5. 注意事项

1. **认证**：所有需要登录的接口都需要在请求头中携带JWT令牌
2. **权限**：不同角色的用户拥有不同的操作权限
3. **数据验证**：所有接口都需要进行参数验证
4. **错误处理**：统一错误处理，返回标准错误格式
5. **性能**：对于查询接口，需要考虑分页和缓存
6. **安全**：密码需要加密存储，防止SQL注入和XSS攻击
7. **日志**：关键操作需要记录日志
8. **版本控制**：API版本控制，建议在URL中添加版本号（如 `/api/v1/employees`）
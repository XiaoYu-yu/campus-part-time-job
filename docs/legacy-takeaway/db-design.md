# 数据库设计文档

## 1. 业务模块

### 1.1 员工管理
- 员工信息管理
- 员工状态管理

### 1.2 分类管理
- 菜品分类管理
- 分类状态管理

### 1.3 菜品管理
- 菜品信息管理
- 菜品状态管理
- 菜品图片管理

### 1.4 套餐管理
- 套餐信息管理
- 套餐包含菜品管理
- 套餐状态管理

### 1.5 订单管理
- 订单信息管理
- 订单明细管理
- 订单状态管理

### 1.6 购物车管理
- 购物车商品管理
- 购物车数量管理

### 1.7 用户管理
- 用户信息管理
- 用户认证管理

## 2. 表结构设计

### 2.1 员工表（employee）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 员工ID |
| `name` | `VARCHAR(50)` | `NOT NULL` | 员工姓名 |
| `phone` | `VARCHAR(20)` | `UNIQUE NOT NULL` | 手机号 |
| `position` | `VARCHAR(50)` | `NOT NULL` | 职位 |
| `department` | `VARCHAR(50)` | `NOT NULL` | 部门 |
| `entry_date` | `DATE` | `NOT NULL` | 入职日期 |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:启用, 0:禁用） |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 2.2 分类表（category）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 分类ID |
| `name` | `VARCHAR(50)` | `UNIQUE NOT NULL` | 分类名称 |
| `sort` | `INT` | `DEFAULT 0` | 排序 |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:启用, 0:禁用） |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 2.3 菜品表（dish）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 菜品ID |
| `name` | `VARCHAR(100)` | `UNIQUE NOT NULL` | 菜品名称 |
| `category_id` | `BIGINT` | `NOT NULL REFERENCES category(id)` | 分类ID |
| `price` | `DECIMAL(10,2)` | `NOT NULL` | 价格 |
| `description` | `VARCHAR(255)` | | 描述 |
| `image` | `VARCHAR(255)` | | 图片URL |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:启用, 0:禁用） |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 2.4 套餐表（setmeal）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 套餐ID |
| `name` | `VARCHAR(100)` | `UNIQUE NOT NULL` | 套餐名称 |
| `category_id` | `BIGINT` | `NOT NULL REFERENCES category(id)` | 分类ID |
| `price` | `DECIMAL(10,2)` | `NOT NULL` | 价格 |
| `description` | `VARCHAR(255)` | | 描述 |
| `image` | `VARCHAR(255)` | | 图片URL |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:启用, 0:禁用） |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 2.5 套餐菜品关联表（setmeal_dish）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 关联ID |
| `setmeal_id` | `BIGINT` | `NOT NULL REFERENCES setmeal(id)` | 套餐ID |
| `dish_id` | `BIGINT` | `NOT NULL REFERENCES dish(id)` | 菜品ID |
| `quantity` | `INT` | `DEFAULT 1` | 菜品数量 |

### 2.6 用户表（user）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 用户ID |
| `name` | `VARCHAR(50)` | `NOT NULL` | 用户名 |
| `phone` | `VARCHAR(20)` | `UNIQUE NOT NULL` | 手机号 |
| `password` | `VARCHAR(100)` | `NOT NULL` | 密码（加密存储） |
| `avatar` | `VARCHAR(255)` | | 头像URL |
| `status` | `TINYINT` | `DEFAULT 1` | 状态（1:启用, 0:禁用） |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 2.7 购物车表（cart）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 购物车ID |
| `user_id` | `BIGINT` | `NOT NULL REFERENCES user(id)` | 用户ID |
| `dish_id` | `BIGINT` | `REFERENCES dish(id)` | 菜品ID |
| `setmeal_id` | `BIGINT` | `REFERENCES setmeal(id)` | 套餐ID |
| `quantity` | `INT` | `NOT NULL DEFAULT 1` | 数量 |
| `checked` | `TINYINT` | `DEFAULT 1` | 是否选中（1:是, 0:否） |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 2.8 订单表（order）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `VARCHAR(32)` | `PRIMARY KEY` | 订单号 |
| `user_id` | `BIGINT` | `NOT NULL REFERENCES user(id)` | 用户ID |
| `customer_name` | `VARCHAR(50)` | `NOT NULL` | 客户姓名 |
| `customer_phone` | `VARCHAR(20)` | `NOT NULL` | 联系电话 |
| `customer_address` | `VARCHAR(255)` | `NOT NULL` | 配送地址 |
| `total_amount` | `DECIMAL(10,2)` | `NOT NULL` | 总金额 |
| `status` | `VARCHAR(20)` | `DEFAULT '待支付'` | 订单状态（待支付、处理中、已完成、已取消） |
| `payment_time` | `DATETIME` | | 支付时间 |
| `delivery_time` | `DATETIME` | | 送达时间 |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间 |

### 2.9 订单明细表（order_item）
| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | 明细ID |
| `order_id` | `VARCHAR(32)` | `NOT NULL REFERENCES order(id)` | 订单号 |
| `dish_id` | `BIGINT` | `REFERENCES dish(id)` | 菜品ID |
| `setmeal_id` | `BIGINT` | `REFERENCES setmeal(id)` | 套餐ID |
| `name` | `VARCHAR(100)` | `NOT NULL` | 商品名称 |
| `price` | `DECIMAL(10,2)` | `NOT NULL` | 单价 |
| `quantity` | `INT` | `NOT NULL` | 数量 |
| `total` | `DECIMAL(10,2)` | `NOT NULL` | 小计 |

## 3. 主要表关系说明

1. **分类与菜品/套餐关系**：
   - 一个分类可以包含多个菜品（一对多）
   - 一个分类可以包含多个套餐（一对多）

2. **套餐与菜品关系**：
   - 一个套餐可以包含多个菜品（多对多），通过setmeal_dish表关联

3. **用户与购物车关系**：
   - 一个用户可以有多个购物车商品（一对多）

4. **用户与订单关系**：
   - 一个用户可以有多个订单（一对多）

5. **订单与订单明细关系**：
   - 一个订单可以包含多个订单明细（一对多）

6. **购物车与菜品/套餐关系**：
   - 一个购物车商品可以是菜品或套餐（多对一）

7. **订单明细与菜品/套餐关系**：
   - 一个订单明细可以是菜品或套餐（多对一）

## 4. 字段说明

### 4.1 状态字段
- **员工状态**：1-启用，0-禁用
- **分类状态**：1-启用，0-禁用
- **菜品状态**：1-启用，0-禁用
- **套餐状态**：1-启用，0-禁用
- **用户状态**：1-启用，0-禁用
- **购物车选中状态**：1-选中，0-未选中
- **订单状态**：待支付、处理中、已完成、已取消

### 4.2 图片上传和静态资源字段设计
- **图片URL格式**：使用相对路径或完整URL
- **存储方式**：建议使用对象存储服务（如OSS）存储图片，数据库中只存储URL
- **图片字段**：employee.avatar、dish.image、setmeal.image、user.avatar

### 4.3 订单号生成规则
- **格式**：年月日时分秒 + 6位随机数（例如：20231201123456123456）
- **长度**：32位字符
- **唯一性**：确保全局唯一

### 4.4 价格字段
- **数据类型**：DECIMAL(10,2)
- **精度**：保留两位小数
- **范围**：0.01-9999999.99

### 4.5 时间字段
- **创建时间**：记录数据创建时间
- **更新时间**：记录数据最后更新时间
- **支付时间**：记录订单支付时间
- **送达时间**：记录订单送达时间
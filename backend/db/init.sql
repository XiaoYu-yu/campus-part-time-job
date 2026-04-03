-- 数据库初始化脚本
-- 数据库: cangqiong_takeaway

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS cangqiong_takeaway
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE cangqiong_takeaway;

-- 员工表
CREATE TABLE IF NOT EXISTS employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '员工ID',
    name VARCHAR(50) NOT NULL COMMENT '员工姓名',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
    password VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    position VARCHAR(50) NOT NULL COMMENT '职位',
    department VARCHAR(50) NOT NULL COMMENT '部门',
    entry_date DATE NOT NULL COMMENT '入职日期',
    status TINYINT DEFAULT 1 COMMENT '状态（1:启用, 0:禁用）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='员工表';

-- 插入测试数据（密码: 123456，MD5: e10adc3949ba59abbe56e057f20f883e）
INSERT INTO employee (name, phone, password, position, department, entry_date, status) VALUES
('管理员', '13800138000', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '技术部', '2024-01-01', 1);

-- 分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) UNIQUE NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（1:启用, 0:禁用）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    name VARCHAR(100) UNIQUE NOT NULL COMMENT '菜品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    description VARCHAR(255) COMMENT '描述',
    image VARCHAR(255) COMMENT '图片URL',
    status TINYINT DEFAULT 1 COMMENT '状态（1:启用, 0:禁用）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES category(id)
) COMMENT='菜品表';

-- 套餐表
CREATE TABLE IF NOT EXISTS setmeal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '套餐ID',
    name VARCHAR(100) UNIQUE NOT NULL COMMENT '套餐名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    description VARCHAR(255) COMMENT '描述',
    image VARCHAR(255) COMMENT '图片URL',
    status TINYINT DEFAULT 1 COMMENT '状态（1:启用, 0:禁用）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES category(id)
) COMMENT='套餐表';

-- 套餐菜品关联表
CREATE TABLE IF NOT EXISTS setmeal_dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    setmeal_id BIGINT NOT NULL COMMENT '套餐ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    quantity INT DEFAULT 1 COMMENT '菜品数量',
    FOREIGN KEY (setmeal_id) REFERENCES setmeal(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id)
) COMMENT='套餐菜品关联表';

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '用户名',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号',
    password VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态（1:启用, 0:禁用）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='用户表';

-- 购物车表
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    checked TINYINT DEFAULT 1 COMMENT '是否选中（1:是, 0:否）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id),
    FOREIGN KEY (setmeal_id) REFERENCES setmeal(id)
) COMMENT='购物车表';

-- 用户地址表
CREATE TABLE IF NOT EXISTS address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    consignee VARCHAR(50) NOT NULL COMMENT '收货人',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    province VARCHAR(50) NOT NULL COMMENT '省',
    city VARCHAR(50) NOT NULL COMMENT '市',
    district VARCHAR(50) NOT NULL COMMENT '区',
    detail VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址（1:是, 0:否）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT='用户地址表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(32) PRIMARY KEY COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    customer_name VARCHAR(50) NOT NULL COMMENT '客户姓名',
    customer_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    customer_address VARCHAR(255) NOT NULL COMMENT '配送地址',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '订单状态（0:待支付, 1:处理中, 2:已配送, 3:已完成, 4:已取消）',
    payment_time DATETIME COMMENT '支付时间',
    delivery_time DATETIME COMMENT '送达时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id VARCHAR(32) NOT NULL COMMENT '订单号',
    dish_id BIGINT COMMENT '菜品ID',
    setmeal_id BIGINT COMMENT '套餐ID',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    total DECIMAL(10,2) NOT NULL COMMENT '小计',
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (dish_id) REFERENCES dish(id),
    FOREIGN KEY (setmeal_id) REFERENCES setmeal(id)
) COMMENT='订单明细表';

-- 店铺配置表
CREATE TABLE IF NOT EXISTS shop_config (
    id BIGINT PRIMARY KEY COMMENT '配置ID',
    is_open TINYINT DEFAULT 1 COMMENT '是否营业（1:营业, 0:休息）',
    rest_notice VARCHAR(255) DEFAULT '' COMMENT '休息公告',
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT='店铺配置表';

-- 店铺营业时间表
CREATE TABLE IF NOT EXISTS shop_business_hour (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '营业时间ID',
    day_key VARCHAR(20) NOT NULL UNIQUE COMMENT '星期标识',
    day_name VARCHAR(20) NOT NULL COMMENT '星期名称',
    is_open TINYINT DEFAULT 1 COMMENT '当天是否营业（1:营业, 0:休息）',
    open_time VARCHAR(5) NOT NULL COMMENT '开始营业时间',
    close_time VARCHAR(5) NOT NULL COMMENT '结束营业时间',
    sort INT DEFAULT 0 COMMENT '排序'
) COMMENT='店铺营业时间表';

-- ============================================
-- 测试数据
-- ============================================

-- 分类测试数据
INSERT INTO category (name, sort, status) VALUES
('热销菜品', 1, 1),
('主食', 2, 1),
('饮品', 3, 1);

-- 菜品测试数据
INSERT INTO dish (name, category_id, price, description, image, status) VALUES
('宫保鸡丁', 1, 38.00, '经典川菜，鸡肉嫩滑，花生酥脆', '/images/dish/gongbao-jiding.jpg', 1),
('鱼香肉丝', 1, 32.00, '酸甜可口，下饭神器', '/images/dish/yuxiang-rousi.jpg', 1),
('麻婆豆腐', 1, 22.00, '麻辣鲜香，入口即化', '/images/dish/mapo-doufu.jpg', 1),
('扬州炒饭', 2, 28.00, '粒粒分明，配料丰富', '/images/dish/yangzhou-chaofan.jpg', 1),
('可乐', 3, 8.00, '冰爽可乐', '/images/dish/kele.jpg', 1);

-- 套餐测试数据
INSERT INTO setmeal (name, category_id, price, description, image, status) VALUES
('超值双人套餐', 1, 88.00, '包含宫保鸡丁、鱼香肉丝、扬州炒饭、可乐2份', '/images/setmeal/shuangren-taocan.jpg', 1),
('经典单人套餐', 1, 45.00, '包含麻婆豆腐、扬州炒饭、可乐', '/images/setmeal/danren-taocan.jpg', 1);

-- 套餐菜品关联测试数据
INSERT INTO setmeal_dish (setmeal_id, dish_id, quantity) VALUES
(1, 1, 1),  -- 超值双人套餐 - 宫保鸡丁
(1, 2, 1),  -- 超值双人套餐 - 鱼香肉丝
(1, 4, 1),  -- 超值双人套餐 - 扬州炒饭
(1, 5, 2),  -- 超值双人套餐 - 可乐2份
(2, 3, 1),  -- 经典单人套餐 - 麻婆豆腐
(2, 4, 1),  -- 经典单人套餐 - 扬州炒饭
(2, 5, 1);  -- 经典单人套餐 - 可乐

-- 用户测试数据（密码: 123456，MD5: e10adc3949ba59abbe56e057f20f883e）
INSERT INTO user (name, phone, password, avatar, status) VALUES
('张三', '13900139000', 'e10adc3949ba59abbe56e057f20f883e', '/images/avatar/user1.jpg', 1),
('李四', '13900139001', 'e10adc3949ba59abbe56e057f20f883e', '/images/avatar/user2.jpg', 1);

-- 地址测试数据
INSERT INTO address (user_id, consignee, phone, province, city, district, detail, is_default) VALUES
(1, '张三', '13900139000', '北京市', '北京市', '朝阳区', '建国路88号', 1),
(2, '李四', '13900139001', '上海市', '上海市', '浦东新区', '张江高科技园区', 1);

-- 店铺配置测试数据
INSERT INTO shop_config (id, is_open, rest_notice, last_updated) VALUES
(1, 1, '如遇节假日调整营业时间，将提前发布公告。', NOW());

-- 店铺营业时间测试数据
INSERT INTO shop_business_hour (day_key, day_name, is_open, open_time, close_time, sort) VALUES
('monday', '周一', 1, '09:00', '22:00', 1),
('tuesday', '周二', 1, '09:00', '22:00', 2),
('wednesday', '周三', 1, '09:00', '22:00', 3),
('thursday', '周四', 1, '09:00', '22:00', 4),
('friday', '周五', 1, '09:00', '23:00', 5),
('saturday', '周六', 1, '10:00', '23:00', 6),
('sunday', '周日', 1, '10:00', '22:00', 7);

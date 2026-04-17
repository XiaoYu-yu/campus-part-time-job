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

-- ============================================
-- Campus relay tables
-- ============================================

CREATE TABLE IF NOT EXISTS campus_customer_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '校园用户资料ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联用户ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    identity_type VARCHAR(20) NOT NULL COMMENT '身份类型(STUDENT/STAFF)',
    identity_no VARCHAR(50) NOT NULL COMMENT '学号或工号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT='校园普通用户资料表';

CREATE TABLE IF NOT EXISTS campus_courier_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '兼职配送员资料ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联用户ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    gender VARCHAR(20) COMMENT '性别',
    campus_zone VARCHAR(50) COMMENT '校区',
    student_no VARCHAR(50) NOT NULL COMMENT '学号',
    college VARCHAR(100) COMMENT '学院',
    major VARCHAR(100) COMMENT '专业',
    class_name VARCHAR(100) COMMENT '班级',
    dormitory_building VARCHAR(50) NOT NULL COMMENT '宿舍楼栋',
    dormitory_room VARCHAR(50) COMMENT '宿舍号',
    enabled_work_in_own_building TINYINT DEFAULT 1 COMMENT '是否愿意优先接本楼栋订单',
    applicant_remark VARCHAR(255) COMMENT '申请人备注',
    id_card_last4 VARCHAR(4) COMMENT '身份证后四位',
    emergency_contact_name VARCHAR(50) NOT NULL COMMENT '紧急联系人',
    emergency_contact_phone VARCHAR(20) NOT NULL COMMENT '紧急联系人电话',
    verification_photo_url VARCHAR(255) COMMENT '学信网认证照片',
    schedule_attachment_url VARCHAR(255) COMMENT '课程表附件',
    review_status VARCHAR(20) NOT NULL COMMENT '审核状态',
    review_comment VARCHAR(255) COMMENT '审核备注',
    reviewed_by_employee_id BIGINT COMMENT '审核管理员ID',
    reviewed_at DATETIME COMMENT '审核时间',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (reviewed_by_employee_id) REFERENCES employee(id)
) COMMENT='兼职配送员资料表';

CREATE TABLE IF NOT EXISTS campus_pickup_point (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '取餐点ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '取餐点编码',
    name VARCHAR(100) NOT NULL COMMENT '取餐点名称',
    gate_area VARCHAR(100) NOT NULL COMMENT '所属区域',
    description VARCHAR(255) NOT NULL COMMENT '说明',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    sort INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='校园取餐点表';

CREATE TABLE IF NOT EXISTS campus_relay_order (
    id VARCHAR(32) PRIMARY KEY COMMENT '代送订单号',
    customer_user_id BIGINT NOT NULL COMMENT '下单用户ID',
    courier_profile_id BIGINT COMMENT '接单配送员资料ID',
    pickup_point_id BIGINT NOT NULL COMMENT '取餐点ID',
    delivery_target_type VARCHAR(30) NOT NULL COMMENT '配送目标类型',
    delivery_building VARCHAR(100) NOT NULL COMMENT '配送楼栋',
    delivery_detail VARCHAR(255) NOT NULL COMMENT '配送详细说明',
    delivery_contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    delivery_contact_phone VARCHAR(20) NOT NULL COMMENT '联系人电话',
    food_description VARCHAR(255) NOT NULL COMMENT '外卖描述',
    external_platform_name VARCHAR(50) COMMENT '外部平台名称',
    external_order_ref VARCHAR(100) COMMENT '外部订单号',
    pickup_code VARCHAR(50) COMMENT '取餐码',
    base_fee DECIMAL(10,2) NOT NULL COMMENT '基础代送费',
    priority_fee DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '加急费',
    tip_fee DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '打赏费',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    payment_status VARCHAR(20) NOT NULL COMMENT '支付状态',
    order_status VARCHAR(30) NOT NULL COMMENT '订单状态',
    priority_dormitory_building VARCHAR(50) COMMENT '优先楼栋',
    paid_at DATETIME COMMENT '支付时间',
    priority_window_deadline DATETIME COMMENT '优先窗口截止时间',
    accepted_at DATETIME COMMENT '接单时间',
    cancel_locked_until DATETIME COMMENT '取消锁定截止时间',
    picked_up_at DATETIME COMMENT '取餐时间',
    delivered_at DATETIME COMMENT '送达时间',
    auto_complete_at DATETIME COMMENT '自动完成时间',
    cancelled_at DATETIME COMMENT '取消时间',
    after_sale_applied_at DATETIME COMMENT '发起售后时间',
    after_sale_handled_at DATETIME COMMENT '售后处理时间',
    pickup_proof_image_url VARCHAR(255) COMMENT '取餐凭证',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    customer_remark VARCHAR(255) COMMENT '用户备注',
    courier_remark VARCHAR(255) COMMENT '配送员备注',
    after_sale_reason VARCHAR(255) COMMENT '售后原因',
    after_sale_handle_action VARCHAR(20) COMMENT '售后处理动作',
    after_sale_handle_remark VARCHAR(255) COMMENT '售后处理备注',
    after_sale_handled_by_employee_id BIGINT COMMENT '售后处理管理员ID',
    after_sale_decision_type VARCHAR(20) COMMENT '售后决策类型',
    after_sale_decision_amount DECIMAL(10,2) COMMENT '售后决策金额',
    after_sale_decision_remark VARCHAR(255) COMMENT '售后决策备注',
    after_sale_decided_by_employee_id BIGINT COMMENT '售后决策管理员ID',
    after_sale_decided_at DATETIME COMMENT '售后决策时间',
    after_sale_execution_status VARCHAR(20) COMMENT '售后执行结果状态',
    after_sale_execution_remark VARCHAR(255) COMMENT '售后执行结果备注',
    after_sale_execution_reference_no VARCHAR(100) COMMENT '售后执行参考号',
    after_sale_executed_by_employee_id BIGINT COMMENT '售后执行管理员ID',
    after_sale_executed_at DATETIME COMMENT '售后执行时间',
    after_sale_execution_corrected TINYINT NOT NULL DEFAULT 0 COMMENT '售后执行是否人工纠正',
    after_sale_execution_corrected_by_employee_id BIGINT COMMENT '售后执行纠正管理员ID',
    after_sale_execution_corrected_at DATETIME COMMENT '售后执行纠正时间',
    exception_type VARCHAR(50) COMMENT '最新异常类型',
    exception_remark VARCHAR(255) COMMENT '最新异常说明',
    exception_reported_at DATETIME COMMENT '最新异常上报时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_user_id) REFERENCES user(id),
    FOREIGN KEY (courier_profile_id) REFERENCES campus_courier_profile(id),
    FOREIGN KEY (pickup_point_id) REFERENCES campus_pickup_point(id),
    FOREIGN KEY (after_sale_handled_by_employee_id) REFERENCES employee(id),
    FOREIGN KEY (after_sale_decided_by_employee_id) REFERENCES employee(id),
    FOREIGN KEY (after_sale_executed_by_employee_id) REFERENCES employee(id),
    FOREIGN KEY (after_sale_execution_corrected_by_employee_id) REFERENCES employee(id)
) COMMENT='校园代送订单表';

CREATE TABLE IF NOT EXISTS campus_exception_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '异常历史记录ID',
    relay_order_id VARCHAR(32) NOT NULL COMMENT '代送订单号',
    courier_profile_id BIGINT NOT NULL COMMENT '配送员资料ID',
    exception_type VARCHAR(50) NOT NULL COMMENT '异常类型',
    exception_remark VARCHAR(255) NOT NULL COMMENT '异常说明',
    reported_at DATETIME NOT NULL COMMENT '异常上报时间',
    process_status VARCHAR(20) NOT NULL DEFAULT 'REPORTED' COMMENT '处理状态(REPORTED/RESOLVED)',
    process_result VARCHAR(50) COMMENT '处理结果',
    processed_by_employee_id BIGINT COMMENT '处理管理员ID',
    processed_at DATETIME COMMENT '处理时间',
    admin_note VARCHAR(255) COMMENT '管理员处理备注',
    source VARCHAR(30) NOT NULL DEFAULT 'COURIER' COMMENT '来源',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_campus_exception_record_order_id (relay_order_id),
    INDEX idx_campus_exception_record_courier_profile_id (courier_profile_id),
    INDEX idx_campus_exception_record_process_status (process_status),
    INDEX idx_campus_exception_record_reported_at (reported_at),
    FOREIGN KEY (relay_order_id) REFERENCES campus_relay_order(id),
    FOREIGN KEY (courier_profile_id) REFERENCES campus_courier_profile(id),
    FOREIGN KEY (processed_by_employee_id) REFERENCES employee(id)
) COMMENT='校园代送异常历史记录表';

CREATE TABLE IF NOT EXISTS campus_after_sale_execution_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '售后执行历史记录ID',
    relay_order_id VARCHAR(32) NOT NULL COMMENT '代送订单号',
    decision_type VARCHAR(20) NOT NULL COMMENT '售后决策类型快照',
    decision_amount DECIMAL(10,2) COMMENT '售后决策金额快照',
    previous_execution_status VARCHAR(20) NOT NULL COMMENT '写入前执行状态',
    execution_status VARCHAR(20) NOT NULL COMMENT '本次执行结果(SUCCESS/FAILED)',
    execution_remark VARCHAR(255) NOT NULL COMMENT '本次执行备注',
    execution_reference_no VARCHAR(100) COMMENT '本次执行参考号',
    executed_by_employee_id BIGINT NOT NULL COMMENT '执行管理员ID',
    executed_at DATETIME NOT NULL COMMENT '执行时间',
    corrected TINYINT NOT NULL DEFAULT 0 COMMENT '是否为FAILED到SUCCESS的人工纠正',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_campus_after_sale_execution_record_order_id (relay_order_id),
    INDEX idx_campus_after_sale_execution_record_status (execution_status),
    INDEX idx_campus_after_sale_execution_record_executed_at (executed_at),
    INDEX idx_campus_after_sale_execution_record_corrected (corrected),
    FOREIGN KEY (relay_order_id) REFERENCES campus_relay_order(id),
    FOREIGN KEY (executed_by_employee_id) REFERENCES employee(id)
) COMMENT='校园代送售后执行历史记录表';

CREATE TABLE IF NOT EXISTS campus_location_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '位置上报ID',
    relay_order_id VARCHAR(32) NOT NULL COMMENT '代送订单号',
    courier_profile_id BIGINT NOT NULL COMMENT '配送员资料ID',
    latitude DECIMAL(10,7) NOT NULL COMMENT '纬度',
    longitude DECIMAL(10,7) NOT NULL COMMENT '经度',
    source VARCHAR(30) NOT NULL COMMENT '来源',
    note VARCHAR(255) COMMENT '备注',
    reported_at DATETIME NOT NULL COMMENT '上报时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (relay_order_id) REFERENCES campus_relay_order(id),
    FOREIGN KEY (courier_profile_id) REFERENCES campus_courier_profile(id)
) COMMENT='配送位置上报表';

CREATE TABLE IF NOT EXISTS campus_settlement_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结算记录ID',
    relay_order_id VARCHAR(32) NOT NULL UNIQUE COMMENT '代送订单号',
    courier_profile_id BIGINT NOT NULL COMMENT '配送员资料ID',
    gross_amount DECIMAL(10,2) NOT NULL COMMENT '应结金额',
    platform_commission DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '平台抽成',
    pending_amount DECIMAL(10,2) NOT NULL COMMENT '待结算金额',
    settlement_status VARCHAR(20) NOT NULL COMMENT '结算状态',
    payout_status VARCHAR(20) COMMENT '打款结果状态',
    payout_batch_no VARCHAR(100) COMMENT '打款批次号',
    payout_remark VARCHAR(255) COMMENT '打款备注',
    payout_reference_no VARCHAR(100) COMMENT '打款参考号',
    payout_recorded_by_employee_id BIGINT COMMENT '打款记录管理员ID',
    payout_recorded_at DATETIME COMMENT '打款记录时间',
    payout_verified TINYINT NOT NULL DEFAULT 0 COMMENT '打款是否已二次核对',
    payout_verified_by_employee_id BIGINT COMMENT '打款核对管理员ID',
    payout_verified_at DATETIME COMMENT '打款核对时间',
    payout_verify_remark VARCHAR(255) COMMENT '打款核对备注',
    settled_at DATETIME COMMENT '结算时间',
    remark VARCHAR(255) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (relay_order_id) REFERENCES campus_relay_order(id),
    FOREIGN KEY (courier_profile_id) REFERENCES campus_courier_profile(id),
    FOREIGN KEY (payout_recorded_by_employee_id) REFERENCES employee(id),
    FOREIGN KEY (payout_verified_by_employee_id) REFERENCES employee(id)
) COMMENT='待结算记录表';

CREATE TABLE IF NOT EXISTS campus_settlement_batch_operation_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结算批次操作审计ID',
    payout_batch_no VARCHAR(100) NOT NULL COMMENT '打款批次号',
    operation_type VARCHAR(30) NOT NULL COMMENT '操作类型(REVIEW/WITHDRAW)',
    operation_result VARCHAR(30) NOT NULL COMMENT '操作结果(PASSED/REJECTED/REQUESTED/RECORDED)',
    operation_remark VARCHAR(255) NOT NULL COMMENT '操作备注',
    operated_by_employee_id BIGINT NOT NULL COMMENT '操作管理员ID',
    operated_at DATETIME NOT NULL COMMENT '操作时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_campus_settlement_batch_operation_batch_no (payout_batch_no),
    INDEX idx_campus_settlement_batch_operation_type (operation_type),
    INDEX idx_campus_settlement_batch_operation_result (operation_result),
    INDEX idx_campus_settlement_batch_operation_operated_at (operated_at),
    FOREIGN KEY (operated_by_employee_id) REFERENCES employee(id)
) COMMENT='校园代送结算批次操作审计表';

INSERT INTO campus_customer_profile (user_id, real_name, identity_type, identity_no) VALUES
(1, '张三', 'STUDENT', '2023123401'),
(2, '李四', 'STAFF', 'T20260001');

INSERT INTO campus_courier_profile (
    user_id, real_name, gender, campus_zone, student_no, college, major, class_name, dormitory_building, dormitory_room,
    enabled_work_in_own_building, applicant_remark, id_card_last4, emergency_contact_name, emergency_contact_phone, verification_photo_url,
    schedule_attachment_url, review_status, review_comment, reviewed_by_employee_id, reviewed_at, enabled
) VALUES
(1, '张三', 'MALE', '渝中校区', '2023123401', '信息工程学院', '软件技术', '软工2301', '竹园', '2-403', 1, '希望优先接本楼栋订单', '1234', '张父', '13900000001', '/api/files/courier-zhangsan-verify.jpg', '/api/files/courier-zhangsan-schedule.jpg', 'PENDING', '待人工审核', NULL, NULL, 0),
(2, '李四', 'MALE', '渝中校区', '2023123402', '信息工程学院', '计算机网络技术', '网工2302', '杏园', '1-206', 1, '可参与图书馆与教学楼订单', '5678', '李母', '13900000002', '/api/files/courier-lisi-verify.jpg', '/api/files/courier-lisi-schedule.jpg', 'APPROVED', '首批示例账号', 1, NOW(), 1);

INSERT INTO campus_pickup_point (code, name, gate_area, description, enabled, sort) VALUES
('NORTH_GATE_TEMP', '主大门门卫室西侧临时取餐区', '北门', '适用于临时堆放和高峰期取餐', 1, 1),
('NORTH_GATE_LOCKER', '主大门外卖柜旁固定取餐区', '北门', '适用于固定外卖柜和外卖架旁取餐', 1, 2);

INSERT INTO campus_relay_order (
    id, customer_user_id, courier_profile_id, pickup_point_id, delivery_target_type, delivery_building,
    delivery_detail, delivery_contact_name, delivery_contact_phone, food_description, external_platform_name,
    external_order_ref, pickup_code, base_fee, priority_fee, tip_fee, total_amount, payment_status,
    order_status, priority_dormitory_building, priority_window_deadline, accepted_at, cancel_locked_until,
    picked_up_at, delivered_at, auto_complete_at, after_sale_handled_at, pickup_proof_image_url, customer_remark, courier_remark,
    after_sale_reason, after_sale_handle_action, after_sale_handle_remark, after_sale_handled_by_employee_id,
    after_sale_decision_type, after_sale_decision_amount, after_sale_decision_remark, after_sale_decided_by_employee_id, after_sale_decided_at,
    after_sale_execution_status, after_sale_execution_remark, after_sale_execution_reference_no, after_sale_executed_by_employee_id, after_sale_executed_at,
    exception_type, exception_remark, exception_reported_at, created_at, updated_at
) VALUES
('CR202604070001', 1, NULL, 1, 'DORMITORY', '竹园', '竹园2栋门口', '张三', '13900139000', '美团订单：汉堡套餐 + 奶茶', '美团', 'MT-20260407-001', 'A18', 3.00, 3.00, 2.00, 8.00, 'PAID', 'BUILDING_PRIORITY_PENDING', '竹园', DATE_ADD(NOW(), INTERVAL 5 MINUTE), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '放在门厅即可', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
('CR202604060001', 1, 2, 2, 'LIBRARY', '图书馆', '二楼门口', '张三', '13900139000', '饿了么订单：咖啡 + 面包', '饿了么', 'ELE-20260406-008', 'B09', 3.00, 0.00, 3.00, 6.00, 'PAID', 'COMPLETED', NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 115 MINUTE), DATE_SUB(NOW(), INTERVAL 100 MINUTE), DATE_SUB(NOW(), INTERVAL 70 MINUTE), DATE_SUB(NOW(), INTERVAL 60 MINUTE), NULL, NULL, NULL, '/api/files/campus-pickup-proof-001.jpg', NULL, '送到图书馆二楼门口', '已按要求送达', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 60 MINUTE));

INSERT INTO campus_location_report (
    relay_order_id, courier_profile_id, latitude, longitude, source, note, reported_at
) VALUES
('CR202604060001', 2, 29.5630100, 106.5515500, 'MANUAL', '已到图书馆二楼门口', DATE_SUB(NOW(), INTERVAL 65 MINUTE));

INSERT INTO campus_settlement_record (
    relay_order_id, courier_profile_id, gross_amount, platform_commission, pending_amount, settlement_status,
    payout_status, payout_remark, payout_reference_no, payout_recorded_by_employee_id, payout_recorded_at,
    settled_at, remark
) VALUES
('CR202604060001', 2, 6.00, 0.00, 6.00, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, '第一版待结算示例');

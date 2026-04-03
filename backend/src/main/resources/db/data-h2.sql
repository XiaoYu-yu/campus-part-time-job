MERGE INTO employee (id, name, phone, password, position, department, entry_date, status, created_at, updated_at) KEY (id) VALUES
(1, '管理员', '13800138000', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '技术部', DATE '2024-01-01', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO category (id, name, sort, status, created_at, updated_at) KEY (id) VALUES
(1, '热销菜品', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '主食', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '饮品', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO dish (id, name, category_id, price, description, image, status, created_at, updated_at) KEY (id) VALUES
(1, '宫保鸡丁', 1, 38.00, '经典川菜，鸡肉嫩滑，花生酥脆', '/images/dish/gongbao-jiding.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '鱼香肉丝', 1, 32.00, '酸甜可口，下饭神器', '/images/dish/yuxiang-rousi.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '麻婆豆腐', 1, 22.00, '麻辣鲜香，入口即化', '/images/dish/mapo-doufu.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '扬州炒饭', 2, 28.00, '粒粒分明，配料丰富', '/images/dish/yangzhou-chaofan.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '可乐', 3, 8.00, '冰爽可乐', '/images/dish/kele.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO setmeal (id, name, category_id, price, description, image, status, created_at, updated_at) KEY (id) VALUES
(1, '超值双人套餐', 1, 88.00, '包含宫保鸡丁、鱼香肉丝、扬州炒饭、可乐2份', '/images/setmeal/shuangren-taocan.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '经典单人套餐', 1, 45.00, '包含麻婆豆腐、扬州炒饭、可乐', '/images/setmeal/danren-taocan.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO setmeal_dish (id, setmeal_id, dish_id, quantity) KEY (id) VALUES
(1, 1, 1, 1),
(2, 1, 2, 1),
(3, 1, 4, 1),
(4, 1, 5, 2),
(5, 2, 3, 1),
(6, 2, 4, 1),
(7, 2, 5, 1);

MERGE INTO user (id, name, phone, password, avatar, status, created_at, updated_at) KEY (id) VALUES
(1, '张三', '13900139000', 'e10adc3949ba59abbe56e057f20f883e', '/images/avatar/user1.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '李四', '13900139001', 'e10adc3949ba59abbe56e057f20f883e', '/images/avatar/user2.jpg', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO address (id, user_id, consignee, phone, province, city, district, detail, is_default, created_at, updated_at) KEY (id) VALUES
(1, 1, '张三', '13900139000', '北京市', '北京市', '朝阳区', '建国路88号', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '李四', '13900139001', '上海市', '上海市', '浦东新区', '张江高科技园区', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO shop_config (id, is_open, rest_notice, last_updated) KEY (id) VALUES
(1, 1, '如遇节假日调整营业时间，将提前发布公告。', CURRENT_TIMESTAMP);

MERGE INTO shop_business_hour (id, day_key, day_name, is_open, open_time, close_time, sort) KEY (id) VALUES
(1, 'monday', '周一', 1, '09:00', '22:00', 1),
(2, 'tuesday', '周二', 1, '09:00', '22:00', 2),
(3, 'wednesday', '周三', 1, '09:00', '22:00', 3),
(4, 'thursday', '周四', 1, '09:00', '22:00', 4),
(5, 'friday', '周五', 1, '09:00', '23:00', 5),
(6, 'saturday', '周六', 1, '10:00', '23:00', 6),
(7, 'sunday', '周日', 1, '10:00', '22:00', 7);

MERGE INTO cart (id, user_id, dish_id, setmeal_id, quantity, checked, created_at, updated_at) KEY (id) VALUES
(1, 1, 1, NULL, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, NULL, 2, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO orders (id, user_id, customer_name, customer_phone, customer_address, total_amount, status, payment_time, delivery_time, created_at, updated_at) KEY (id) VALUES
('ORD2026001', 1, '张三', '13900139000', '北京市朝阳区建国路88号', 74.00, 3, DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
('ORD2026002', 2, '李四', '13900139001', '上海市浦东新区张江高科技园区', 88.00, 2, DATEADD('DAY', -1, CURRENT_TIMESTAMP), NULL, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
('ORD2026003', 1, '张三', '13900139000', '北京市朝阳区建国路88号', 45.00, 0, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO order_item (id, order_id, dish_id, setmeal_id, name, price, quantity, total) KEY (id) VALUES
(1, 'ORD2026001', 1, NULL, '宫保鸡丁', 38.00, 1, 38.00),
(2, 'ORD2026001', 4, NULL, '扬州炒饭', 28.00, 1, 28.00),
(3, 'ORD2026001', 5, NULL, '可乐', 8.00, 1, 8.00),
(4, 'ORD2026002', NULL, 1, '超值双人套餐', 88.00, 1, 88.00),
(5, 'ORD2026003', NULL, 2, '经典单人套餐', 45.00, 1, 45.00);

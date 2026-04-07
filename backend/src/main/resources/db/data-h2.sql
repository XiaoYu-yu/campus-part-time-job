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

MERGE INTO campus_customer_profile (id, user_id, real_name, identity_type, identity_no, created_at, updated_at) KEY (id) VALUES
(1, 1, '张三', 'STUDENT', '2023123401', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '李四', 'STAFF', 'T20260001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO campus_courier_profile (
    id, user_id, real_name, student_no, college, major, class_name, dormitory_building, dormitory_room,
    id_card_last4, emergency_contact_name, emergency_contact_phone, verification_photo_url,
    schedule_attachment_url, review_status, review_comment, reviewed_by_employee_id, reviewed_at,
    enabled, created_at, updated_at
) KEY (id) VALUES
(1, 1, '张三', '2023123401', '信息工程学院', '软件技术', '软工2301', '竹园', '2-403', '1234', '张父', '13900000001', '/api/files/courier-zhangsan-verify.jpg', '/api/files/courier-zhangsan-schedule.jpg', 'PENDING', '待人工审核', NULL, NULL, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '李四', '2023123402', '信息工程学院', '计算机网络技术', '网工2302', '杏园', '1-206', '5678', '李母', '13900000002', '/api/files/courier-lisi-verify.jpg', '/api/files/courier-lisi-schedule.jpg', 'APPROVED', '首批示例账号', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO campus_pickup_point (id, code, name, gate_area, description, enabled, sort, created_at, updated_at) KEY (id) VALUES
(1, 'NORTH_GATE_TEMP', '主大门门卫室西侧临时取餐区', '北门', '适用于临时堆放和高峰期取餐', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'NORTH_GATE_LOCKER', '主大门外卖柜旁固定取餐区', '北门', '适用于固定外卖柜和外卖架旁取餐', 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO campus_relay_order (
    id, customer_user_id, courier_profile_id, pickup_point_id, delivery_target_type, delivery_building,
    delivery_detail, delivery_contact_name, delivery_contact_phone, food_description, external_platform_name,
    external_order_ref, pickup_code, base_fee, priority_fee, tip_fee, total_amount, payment_status,
    order_status, priority_dormitory_building, paid_at, priority_window_deadline, accepted_at, cancel_locked_until,
    picked_up_at, delivered_at, auto_complete_at, cancelled_at, after_sale_applied_at, pickup_proof_image_url,
    cancel_reason, customer_remark, courier_remark, after_sale_reason, created_at, updated_at
) KEY (id) VALUES
('CR202604070001', 1, NULL, 1, 'DORMITORY', '竹园', '竹园2栋门口', '张三', '13900139000', '美团订单：汉堡套餐 + 奶茶', '美团', 'MT-20260407-001', 'A18', 3.00, 3.00, 2.00, 8.00, 'PAID', 'BUILDING_PRIORITY_PENDING', '竹园', CURRENT_TIMESTAMP, DATEADD('MINUTE', 5, CURRENT_TIMESTAMP), NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '放在门厅即可', NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CR202604060001', 1, 2, 2, 'LIBRARY', '图书馆', '二楼门口', '张三', '13900139000', '饿了么订单：咖啡 + 面包', '饿了么', 'ELE-20260406-008', 'B09', 3.00, 0.00, 3.00, 6.00, 'PAID', 'COMPLETED', NULL, DATEADD('MINUTE', -170, CURRENT_TIMESTAMP), NULL, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), DATEADD('MINUTE', -115, CURRENT_TIMESTAMP), DATEADD('MINUTE', -100, CURRENT_TIMESTAMP), DATEADD('MINUTE', -70, CURRENT_TIMESTAMP), DATEADD('MINUTE', -60, CURRENT_TIMESTAMP), NULL, NULL, '/api/files/campus-pickup-proof-001.jpg', NULL, '送到图书馆二楼门口', '已按要求送达', NULL, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), DATEADD('MINUTE', -60, CURRENT_TIMESTAMP));

MERGE INTO campus_location_report (
    id, relay_order_id, courier_profile_id, latitude, longitude, source, note, reported_at, created_at
) KEY (id) VALUES
(1, 'CR202604060001', 2, 29.5630100, 106.5515500, 'MANUAL', '已到图书馆二楼门口', DATEADD('MINUTE', -65, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

MERGE INTO campus_settlement_record (
    id, relay_order_id, courier_profile_id, gross_amount, platform_commission, pending_amount,
    settlement_status, settled_at, remark, created_at, updated_at
) KEY (id) VALUES
(1, 'CR202604060001', 2, 6.00, 0.00, 6.00, 'PENDING', NULL, '第一版待结算示例', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

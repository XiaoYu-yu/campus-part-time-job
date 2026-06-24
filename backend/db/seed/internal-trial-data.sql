-- Optional internal-trial seed data for MySQL deployments.
-- Apply only after Flyway migrations have completed.

SET NAMES utf8mb4;
SET time_zone = '+08:00';

INSERT INTO employee (id, name, phone, password, position, department, entry_date, status, created_at, updated_at) VALUES
(1, '管理员', '13800138000', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '技术部', '2024-01-01', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status), updated_at = NOW();

INSERT INTO category (id, name, sort, status, created_at, updated_at) VALUES
(1, '热销菜品', 1, 1, NOW(), NOW()),
(2, '主食', 2, 1, NOW(), NOW()),
(3, '饮品', 3, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE sort = VALUES(sort), status = VALUES(status), updated_at = NOW();

INSERT INTO dish (id, name, category_id, price, description, image, status, created_at, updated_at) VALUES
(1, '宫保鸡丁', 1, 38.00, '经典川菜，鸡肉嫩滑，花生酥脆', '/images/dish/gongbao-jiding.jpg', 1, NOW(), NOW()),
(2, '鱼香肉丝', 1, 32.00, '酸甜可口，下饭神器', '/images/dish/yuxiang-rousi.jpg', 1, NOW(), NOW()),
(3, '麻婆豆腐', 1, 22.00, '麻辣鲜香，入口即化', '/images/dish/mapo-doufu.jpg', 1, NOW(), NOW()),
(4, '扬州炒饭', 2, 28.00, '粒粒分明，配料丰富', '/images/dish/yangzhou-chaofan.jpg', 1, NOW(), NOW()),
(5, '可乐', 3, 8.00, '冰爽可乐', '/images/dish/kele.jpg', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE price = VALUES(price), status = VALUES(status), updated_at = NOW();

INSERT INTO setmeal (id, name, category_id, price, description, image, status, created_at, updated_at) VALUES
(1, '超值双人套餐', 1, 88.00, '包含宫保鸡丁、鱼香肉丝、扬州炒饭、可乐2份', '/images/setmeal/shuangren-taocan.jpg', 1, NOW(), NOW()),
(2, '经典单人套餐', 1, 45.00, '包含麻婆豆腐、扬州炒饭、可乐', '/images/setmeal/danren-taocan.jpg', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE price = VALUES(price), status = VALUES(status), updated_at = NOW();

INSERT INTO setmeal_dish (id, setmeal_id, dish_id, quantity) VALUES
(1, 1, 1, 1),
(2, 1, 2, 1),
(3, 1, 4, 1),
(4, 1, 5, 2),
(5, 2, 3, 1),
(6, 2, 4, 1),
(7, 2, 5, 1)
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity);

INSERT INTO user (id, name, phone, password, avatar, status, created_at, updated_at) VALUES
(1, '张三', '13900139000', 'e10adc3949ba59abbe56e057f20f883e', '/images/avatar/user1.jpg', 1, NOW(), NOW()),
(2, '李四', '13900139001', 'e10adc3949ba59abbe56e057f20f883e', '/images/avatar/user2.jpg', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status), updated_at = NOW();

INSERT INTO address (id, user_id, consignee, phone, province, city, district, detail, is_default, created_at, updated_at) VALUES
(1, 1, '张三', '13900139000', '北京市', '北京市', '朝阳区', '建国路88号', 1, NOW(), NOW()),
(2, 2, '李四', '13900139001', '上海市', '上海市', '浦东新区', '张江高科技园区', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE detail = VALUES(detail), is_default = VALUES(is_default), updated_at = NOW();

INSERT INTO shop_config (id, is_open, rest_notice, last_updated) VALUES
(1, 1, '如遇节假日调整营业时间，将提前发布公告。', NOW())
ON DUPLICATE KEY UPDATE is_open = VALUES(is_open), rest_notice = VALUES(rest_notice), last_updated = NOW();

INSERT INTO shop_business_hour (id, day_key, day_name, is_open, open_time, close_time, sort) VALUES
(1, 'monday', '周一', 1, '09:00', '22:00', 1),
(2, 'tuesday', '周二', 1, '09:00', '22:00', 2),
(3, 'wednesday', '周三', 1, '09:00', '22:00', 3),
(4, 'thursday', '周四', 1, '09:00', '22:00', 4),
(5, 'friday', '周五', 1, '09:00', '23:00', 5),
(6, 'saturday', '周六', 1, '10:00', '23:00', 6),
(7, 'sunday', '周日', 1, '10:00', '22:00', 7)
ON DUPLICATE KEY UPDATE is_open = VALUES(is_open), open_time = VALUES(open_time), close_time = VALUES(close_time), sort = VALUES(sort);

INSERT INTO cart (id, user_id, dish_id, setmeal_id, quantity, checked, created_at, updated_at) VALUES
(1, 1, 1, NULL, 2, 1, NOW(), NOW()),
(2, 1, NULL, 2, 1, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), checked = VALUES(checked), updated_at = NOW();

INSERT INTO orders (id, user_id, customer_name, customer_phone, customer_address, total_amount, status, payment_time, delivery_time, created_at, updated_at) VALUES
('ORD2026001', 1, '张三', '13900139000', '北京市朝阳区建国路88号', 74.00, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD2026002', 2, '李四', '13900139001', '上海市浦东新区张江高科技园区', 88.00, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD2026003', 1, '张三', '13900139000', '北京市朝阳区建国路88号', 45.00, 0, NULL, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE status = VALUES(status), updated_at = NOW();

INSERT INTO order_item (id, order_id, dish_id, setmeal_id, name, price, quantity, total) VALUES
(1, 'ORD2026001', 1, NULL, '宫保鸡丁', 38.00, 1, 38.00),
(2, 'ORD2026001', 4, NULL, '扬州炒饭', 28.00, 1, 28.00),
(3, 'ORD2026001', 5, NULL, '可乐', 8.00, 1, 8.00),
(4, 'ORD2026002', NULL, 1, '超值双人套餐', 88.00, 1, 88.00),
(5, 'ORD2026003', NULL, 2, '经典单人套餐', 45.00, 1, 45.00)
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), total = VALUES(total);

INSERT INTO campus_customer_profile (id, user_id, real_name, identity_type, identity_no, created_at, updated_at) VALUES
(1, 1, '张三', 'STUDENT', '2023123401', NOW(), NOW()),
(2, 2, '李四', 'STAFF', 'T20260001', NOW(), NOW())
ON DUPLICATE KEY UPDATE real_name = VALUES(real_name), identity_type = VALUES(identity_type), updated_at = NOW();

INSERT INTO campus_courier_profile (
    id, user_id, real_name, gender, campus_zone, student_no, college, major, class_name, dormitory_building, dormitory_room,
    enabled_work_in_own_building, applicant_remark, id_card_last4, emergency_contact_name, emergency_contact_phone, verification_photo_url,
    schedule_attachment_url, review_status, review_comment, reviewed_by_employee_id, reviewed_at,
    enabled, created_at, updated_at
) VALUES
(1, 1, '张三', 'MALE', '渝中校区', '2023123401', '信息工程学院', '软件技术', '软工2301', '竹园', '2-403', 1, '希望优先接本楼栋订单', '1234', '张父', '13900000001', '/api/files/courier-zhangsan-verify.jpg', '/api/files/courier-zhangsan-schedule.jpg', 'PENDING', '待人工审核', NULL, NULL, 0, NOW(), NOW()),
(2, 2, '李四', 'MALE', '渝中校区', '2023123402', '信息工程学院', '计算机网络技术', '网工2302', '杏园', '1-206', 1, '可参与图书馆与教学楼订单', '5678', '李母', '13900000002', '/api/files/courier-lisi-verify.jpg', '/api/files/courier-lisi-schedule.jpg', 'APPROVED', '首批示例账号', 1, NOW(), 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE review_status = VALUES(review_status), enabled = VALUES(enabled), updated_at = NOW();

INSERT INTO campus_pickup_point (id, code, name, gate_area, description, enabled, sort, created_at, updated_at) VALUES
(1, 'NORTH_GATE_TEMP', '主大门门卫室西侧临时取餐区', '北门', '适用于临时堆放和高峰期取餐', 1, 1, NOW(), NOW()),
(2, 'NORTH_GATE_LOCKER', '主大门外卖柜旁固定取餐区', '北门', '适用于固定外卖柜和外卖架旁取餐', 1, 2, NOW(), NOW())
ON DUPLICATE KEY UPDATE enabled = VALUES(enabled), sort = VALUES(sort), updated_at = NOW();

INSERT INTO campus_relay_order (
    id, customer_user_id, courier_profile_id, pickup_point_id, delivery_target_type, delivery_building,
    delivery_detail, delivery_contact_name, delivery_contact_phone, food_description, external_platform_name,
    external_order_ref, pickup_code, base_fee, priority_fee, tip_fee, total_amount, payment_status,
    order_status, priority_dormitory_building, paid_at, priority_window_deadline, accepted_at, cancel_locked_until,
    picked_up_at, delivered_at, auto_complete_at, pickup_proof_image_url, customer_remark, courier_remark,
    created_at, updated_at
) VALUES
('CR202604070001', 1, NULL, 1, 'DORMITORY', '竹园', '竹园2栋门口', '张三', '13900139000', '美团订单：汉堡套餐 + 奶茶', '美团', 'MT-20260407-001', 'A18', 3.00, 3.00, 2.00, 8.00, 'PAID', 'BUILDING_PRIORITY_PENDING', '竹园', NOW(), DATE_ADD(NOW(), INTERVAL 5 MINUTE), NULL, NULL, NULL, NULL, NULL, NULL, '放在门厅即可', NULL, NOW(), NOW()),
('CR202604070002', 2, NULL, 2, 'DORMITORY', '梅园', '梅园1栋楼下', '李四', '13900139001', '饿了么订单：轻食沙拉 + 冰美式', '饿了么', 'ELE-20260407-009', 'C12', 3.00, 0.00, 1.00, 4.00, 'PAID', 'WAITING_ACCEPT', NULL, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, NULL, '送到梅园1栋楼下即可', NULL, NOW(), NOW()),
('CR202604060001', 1, 2, 2, 'LIBRARY', '图书馆', '二楼门口', '张三', '13900139000', '饿了么订单：咖啡 + 面包', '饿了么', 'ELE-20260406-008', 'B09', 3.00, 0.00, 3.00, 6.00, 'PAID', 'COMPLETED', NULL, DATE_SUB(NOW(), INTERVAL 170 MINUTE), NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 115 MINUTE), DATE_SUB(NOW(), INTERVAL 100 MINUTE), DATE_SUB(NOW(), INTERVAL 70 MINUTE), DATE_SUB(NOW(), INTERVAL 60 MINUTE), '/api/files/campus-pickup-proof-001.jpg', '送到图书馆二楼门口', '已按要求送达', DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 60 MINUTE))
ON DUPLICATE KEY UPDATE order_status = VALUES(order_status), updated_at = NOW();

INSERT INTO campus_location_report (
    id, relay_order_id, courier_profile_id, latitude, longitude, source, note, reported_at, created_at
) VALUES
(1, 'CR202604060001', 2, 29.5630100, 106.5515500, 'MANUAL', '已到图书馆二楼门口', DATE_SUB(NOW(), INTERVAL 65 MINUTE), NOW())
ON DUPLICATE KEY UPDATE note = VALUES(note), reported_at = VALUES(reported_at);

INSERT INTO campus_settlement_record (
    id, relay_order_id, courier_profile_id, gross_amount, platform_commission, pending_amount,
    settlement_status, payout_status, payout_remark, payout_reference_no, payout_recorded_by_employee_id, payout_recorded_at,
    settled_at, remark, created_at, updated_at
) VALUES
(1, 'CR202604060001', 2, 6.00, 0.00, 6.00, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, '第一版待结算示例', NOW(), NOW())
ON DUPLICATE KEY UPDATE settlement_status = VALUES(settlement_status), updated_at = NOW();

INSERT INTO campus_feedback (
    id, submitter_role, category, content, contact, page_path, order_id, status,
    processed_by_employee_id, processed_at, admin_note, created_at, updated_at
) VALUES
(1, 'USER', 'ORDER', '订单送达后状态刷新较慢，希望能更清楚地提示。', '13900139000', '/user/campus/order-result', 'CR202604060001', 'PENDING', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(2, 'PARTTIME', 'SUGGESTION', '建议在工作台增加取餐点快速筛选。', '13900139001', '/parttime/workbench', NULL, 'RESOLVED', 1, DATE_SUB(NOW(), INTERVAL 10 MINUTE), '已记录为后续体验优化项。', DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 10 MINUTE))
ON DUPLICATE KEY UPDATE status = VALUES(status), admin_note = VALUES(admin_note), updated_at = NOW();

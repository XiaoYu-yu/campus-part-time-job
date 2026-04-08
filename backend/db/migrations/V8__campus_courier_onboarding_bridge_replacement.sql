ALTER TABLE campus_courier_profile
    MODIFY COLUMN college VARCHAR(100) NULL COMMENT '学院',
    MODIFY COLUMN major VARCHAR(100) NULL COMMENT '专业',
    MODIFY COLUMN class_name VARCHAR(100) NULL COMMENT '班级',
    MODIFY COLUMN dormitory_room VARCHAR(50) NULL COMMENT '宿舍号',
    MODIFY COLUMN id_card_last4 VARCHAR(4) NULL COMMENT '身份证后四位',
    ADD COLUMN gender VARCHAR(20) NULL COMMENT '性别',
    ADD COLUMN campus_zone VARCHAR(50) NULL COMMENT '校区',
    ADD COLUMN enabled_work_in_own_building TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否愿意优先接本楼栋订单',
    ADD COLUMN applicant_remark VARCHAR(255) NULL COMMENT '申请人备注';

UPDATE campus_courier_profile
SET campus_zone = '渝中校区'
WHERE campus_zone IS NULL;

UPDATE campus_courier_profile
SET enabled_work_in_own_building = 1
WHERE enabled_work_in_own_building IS NULL;

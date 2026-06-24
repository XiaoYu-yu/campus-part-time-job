ALTER TABLE campus_feedback
    ADD COLUMN processed_by_employee_id BIGINT NULL COMMENT '最近处理管理员ID' AFTER status,
    ADD COLUMN processed_at DATETIME NULL COMMENT '最近处理时间' AFTER processed_by_employee_id,
    ADD COLUMN admin_note VARCHAR(500) NULL COMMENT '管理员处理备注' AFTER processed_at;

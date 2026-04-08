ALTER TABLE campus_relay_order
    ADD COLUMN after_sale_decision_type VARCHAR(20) NULL COMMENT '售后决策类型',
    ADD COLUMN after_sale_decision_amount DECIMAL(10, 2) NULL COMMENT '售后决策金额',
    ADD COLUMN after_sale_decision_remark VARCHAR(255) NULL COMMENT '售后决策备注',
    ADD COLUMN after_sale_decided_by_employee_id BIGINT NULL COMMENT '售后决策管理员ID',
    ADD COLUMN after_sale_decided_at DATETIME NULL COMMENT '售后决策时间';

ALTER TABLE campus_relay_order
    ADD CONSTRAINT fk_campus_relay_order_after_sale_decision_employee
        FOREIGN KEY (after_sale_decided_by_employee_id) REFERENCES employee(id);

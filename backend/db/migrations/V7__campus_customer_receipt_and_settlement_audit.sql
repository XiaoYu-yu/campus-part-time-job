ALTER TABLE campus_relay_order
    ADD COLUMN after_sale_execution_corrected TINYINT(1) NOT NULL DEFAULT 0 COMMENT '售后执行是否人工纠正',
    ADD COLUMN after_sale_execution_corrected_by_employee_id BIGINT NULL COMMENT '售后执行纠正管理员ID',
    ADD COLUMN after_sale_execution_corrected_at DATETIME NULL COMMENT '售后执行纠正时间';

ALTER TABLE campus_relay_order
    ADD CONSTRAINT fk_campus_relay_order_after_sale_execution_corrected_employee
        FOREIGN KEY (after_sale_execution_corrected_by_employee_id) REFERENCES employee(id);

UPDATE campus_relay_order
SET after_sale_execution_corrected = 0
WHERE after_sale_execution_corrected IS NULL;

ALTER TABLE campus_settlement_record
    ADD COLUMN payout_batch_no VARCHAR(100) NULL COMMENT '打款批次号',
    ADD COLUMN payout_verified TINYINT(1) NOT NULL DEFAULT 0 COMMENT '打款是否已二次核对',
    ADD COLUMN payout_verified_by_employee_id BIGINT NULL COMMENT '打款核对管理员ID',
    ADD COLUMN payout_verified_at DATETIME NULL COMMENT '打款核对时间',
    ADD COLUMN payout_verify_remark VARCHAR(255) NULL COMMENT '打款核对备注';

ALTER TABLE campus_settlement_record
    ADD CONSTRAINT fk_campus_settlement_record_payout_verify_employee
        FOREIGN KEY (payout_verified_by_employee_id) REFERENCES employee(id);

UPDATE campus_settlement_record
SET payout_verified = 0
WHERE payout_verified IS NULL;

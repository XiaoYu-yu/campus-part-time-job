ALTER TABLE campus_relay_order
    ADD COLUMN after_sale_execution_status VARCHAR(20) NULL COMMENT '售后执行结果状态',
    ADD COLUMN after_sale_execution_remark VARCHAR(255) NULL COMMENT '售后执行结果备注',
    ADD COLUMN after_sale_execution_reference_no VARCHAR(100) NULL COMMENT '售后执行参考号',
    ADD COLUMN after_sale_executed_by_employee_id BIGINT NULL COMMENT '售后执行管理员ID',
    ADD COLUMN after_sale_executed_at DATETIME NULL COMMENT '售后执行时间';

ALTER TABLE campus_relay_order
    ADD CONSTRAINT fk_campus_relay_order_after_sale_execution_employee
        FOREIGN KEY (after_sale_executed_by_employee_id) REFERENCES employee(id);

UPDATE campus_relay_order
SET after_sale_execution_status = CASE
    WHEN after_sale_decision_type = 'NONE' THEN 'NOT_REQUIRED'
    WHEN after_sale_decision_type IN ('REFUND', 'COMPENSATE') THEN 'PENDING'
    ELSE after_sale_execution_status
END
WHERE after_sale_decision_type IS NOT NULL
  AND after_sale_execution_status IS NULL;

ALTER TABLE campus_settlement_record
    ADD COLUMN payout_status VARCHAR(20) NULL COMMENT '打款结果状态',
    ADD COLUMN payout_remark VARCHAR(255) NULL COMMENT '打款备注',
    ADD COLUMN payout_reference_no VARCHAR(100) NULL COMMENT '打款参考号',
    ADD COLUMN payout_recorded_by_employee_id BIGINT NULL COMMENT '打款记录管理员ID',
    ADD COLUMN payout_recorded_at DATETIME NULL COMMENT '打款记录时间';

ALTER TABLE campus_settlement_record
    ADD CONSTRAINT fk_campus_settlement_record_payout_employee
        FOREIGN KEY (payout_recorded_by_employee_id) REFERENCES employee(id);

UPDATE campus_settlement_record
SET payout_status = 'UNPAID'
WHERE settlement_status = 'SETTLED'
  AND payout_status IS NULL;

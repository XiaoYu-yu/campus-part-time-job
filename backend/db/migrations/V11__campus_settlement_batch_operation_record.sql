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
    CONSTRAINT fk_campus_settlement_batch_operation_employee FOREIGN KEY (operated_by_employee_id) REFERENCES employee(id)
) COMMENT='校园代送结算批次操作审计表';

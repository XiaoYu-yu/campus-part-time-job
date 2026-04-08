ALTER TABLE campus_relay_order
    ADD COLUMN after_sale_handled_at DATETIME NULL,
    ADD COLUMN after_sale_handle_action VARCHAR(20) NULL,
    ADD COLUMN after_sale_handle_remark VARCHAR(255) NULL,
    ADD COLUMN after_sale_handled_by_employee_id BIGINT NULL,
    ADD COLUMN exception_type VARCHAR(50) NULL,
    ADD COLUMN exception_remark VARCHAR(255) NULL,
    ADD COLUMN exception_reported_at DATETIME NULL;

ALTER TABLE campus_relay_order
    ADD CONSTRAINT fk_campus_relay_order_after_sale_employee
        FOREIGN KEY (after_sale_handled_by_employee_id) REFERENCES employee(id);

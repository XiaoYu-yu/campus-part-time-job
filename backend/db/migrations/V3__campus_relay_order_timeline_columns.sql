ALTER TABLE campus_relay_order
    ADD COLUMN paid_at DATETIME NULL,
    ADD COLUMN cancelled_at DATETIME NULL,
    ADD COLUMN after_sale_applied_at DATETIME NULL,
    ADD COLUMN cancel_reason VARCHAR(255) NULL;

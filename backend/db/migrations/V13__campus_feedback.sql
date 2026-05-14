CREATE TABLE IF NOT EXISTS campus_feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID',
    submitter_role VARCHAR(20) NOT NULL COMMENT '反馈角色(USER/PARTTIME)',
    category VARCHAR(30) NOT NULL DEFAULT 'OTHER' COMMENT '反馈类型',
    content VARCHAR(1000) NOT NULL COMMENT '反馈内容',
    contact VARCHAR(100) COMMENT '联系方式',
    page_path VARCHAR(120) COMMENT '来源页面',
    order_id VARCHAR(32) COMMENT '关联订单号',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_campus_feedback_role (submitter_role),
    INDEX idx_campus_feedback_category (category),
    INDEX idx_campus_feedback_status (status),
    INDEX idx_campus_feedback_created_at (created_at),
    INDEX idx_campus_feedback_order_id (order_id)
) COMMENT='校园平台用户反馈表';

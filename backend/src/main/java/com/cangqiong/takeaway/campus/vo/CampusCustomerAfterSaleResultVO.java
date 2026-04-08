package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusCustomerAfterSaleResultVO {

    private String relayOrderId;
    private String orderStatus;
    private String afterSaleReason;
    private String afterSaleHandleAction;
    private String afterSaleHandleRemark;
    private String decisionType;
    private BigDecimal decisionAmount;
    private String afterSaleExecutionStatus;
    private String customerReceiptStatus;
    private String customerReceiptTitle;
    private String customerReceiptMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleAppliedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;
}

package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusAdminAfterSaleOrderVO {

    private String relayOrderId;
    private String orderStatus;
    private Long customerUserId;
    private Long courierProfileId;
    private BigDecimal totalAmount;
    private String afterSaleReason;
    private String afterSaleHandleAction;
    private String afterSaleHandleRemark;
    private Long afterSaleHandledByEmployeeId;
    private String afterSaleDecisionType;
    private BigDecimal afterSaleDecisionAmount;
    private String afterSaleDecisionRemark;
    private Long afterSaleDecidedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleAppliedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleHandledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleDecidedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

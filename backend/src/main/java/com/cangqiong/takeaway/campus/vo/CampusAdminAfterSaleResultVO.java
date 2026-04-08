package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusAdminAfterSaleResultVO {

    private String relayOrderId;
    private String orderStatus;
    private String paymentStatus;
    private Long customerUserId;
    private Long courierProfileId;
    private Long pickupPointId;
    private String pickupPointCode;
    private String pickupPointName;
    private String deliveryTargetType;
    private String deliveryBuilding;
    private String deliveryDetail;
    private String customerName;
    private String customerPhone;
    private String courierName;
    private BigDecimal totalAmount;
    private String afterSaleReason;
    private String afterSaleHandleAction;
    private String afterSaleHandleRemark;
    private Long afterSaleHandledByEmployeeId;
    private String afterSaleDecisionType;
    private BigDecimal afterSaleDecisionAmount;
    private String afterSaleDecisionRemark;
    private Long afterSaleDecidedByEmployeeId;
    private String afterSaleExecutionStatus;
    private String afterSaleExecutionRemark;
    private String afterSaleExecutionReferenceNo;
    private Long afterSaleExecutedByEmployeeId;
    private Integer afterSaleExecutionCorrected;
    private Long afterSaleExecutionCorrectedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleAppliedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleHandledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleDecidedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleExecutedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleExecutionCorrectedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

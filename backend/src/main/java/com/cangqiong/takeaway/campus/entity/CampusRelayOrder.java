package com.cangqiong.takeaway.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusRelayOrder {

    private String id;
    private Long customerUserId;
    private Long courierProfileId;
    private Long pickupPointId;
    private String deliveryTargetType;
    private String deliveryBuilding;
    private String deliveryDetail;
    private String deliveryContactName;
    private String deliveryContactPhone;
    private String foodDescription;
    private String externalPlatformName;
    private String externalOrderRef;
    private String pickupCode;
    private BigDecimal baseFee;
    private BigDecimal priorityFee;
    private BigDecimal tipFee;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String orderStatus;
    private String priorityDormitoryBuilding;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime priorityWindowDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelLockedUntil;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pickedUpAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveredAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime autoCompleteAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleAppliedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleHandledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime exceptionReportedAt;

    private String pickupProofImageUrl;
    private String cancelReason;
    private String customerRemark;
    private String courierRemark;
    private String afterSaleReason;
    private String afterSaleHandleAction;
    private String afterSaleHandleRemark;
    private Long afterSaleHandledByEmployeeId;
    private String exceptionType;
    private String exceptionRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

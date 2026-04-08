package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusCourierOrderVO {

    private String id;
    private Long pickupPointId;
    private String pickupPointCode;
    private String pickupPointName;
    private String deliveryTargetType;
    private String deliveryBuilding;
    private String deliveryDetail;
    private String contactName;
    private String contactPhone;
    private String foodDescription;
    private String externalPlatformName;
    private String externalOrderRef;
    private String pickupCode;
    private BigDecimal baseFee;
    private BigDecimal priorityFee;
    private BigDecimal tipFee;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String status;
    private Long courierProfileId;
    private String priorityDormitoryBuilding;
    private String pickupProofImageUrl;
    private String cancelReason;
    private String customerRemark;
    private String courierRemark;
    private String afterSaleReason;
    private String afterSaleHandleAction;
    private String afterSaleHandleRemark;
    private String exceptionType;
    private String exceptionRemark;
    private Boolean priorityWindowActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime priorityWindowDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptedAt;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

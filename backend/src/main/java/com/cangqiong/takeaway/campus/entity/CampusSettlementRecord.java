package com.cangqiong.takeaway.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusSettlementRecord {

    private Long id;
    private String relayOrderId;
    private Long courierProfileId;
    private BigDecimal grossAmount;
    private BigDecimal platformCommission;
    private BigDecimal pendingAmount;
    private String settlementStatus;
    private String payoutStatus;
    private String payoutBatchNo;
    private String payoutRemark;
    private String payoutReferenceNo;
    private Long payoutRecordedByEmployeeId;
    private Integer payoutVerified;
    private Long payoutVerifiedByEmployeeId;
    private String payoutVerifyRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settledAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payoutRecordedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payoutVerifiedAt;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusSettlementReconcileDifferenceRecordVO {

    private Long id;
    private String payoutBatchNo;
    private Long settlementRecordId;
    private String relayOrderId;
    private Long courierProfileId;
    private String differenceType;
    private BigDecimal expectedAmount;
    private BigDecimal actualAmount;
    private String differenceRemark;
    private String processStatus;
    private String processResult;
    private String processRemark;
    private Long reportedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedAt;

    private Long processedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;

    private String source;
    private String settlementStatus;
    private String payoutStatus;
    private String currentPayoutBatchNo;
    private String payoutRemark;
    private String payoutReferenceNo;
    private Integer payoutVerified;
    private String payoutVerifyRemark;
    private BigDecimal pendingAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payoutRecordedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payoutVerifiedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

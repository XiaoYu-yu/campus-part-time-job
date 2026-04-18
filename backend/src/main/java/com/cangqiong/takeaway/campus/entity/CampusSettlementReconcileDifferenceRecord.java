package com.cangqiong.takeaway.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusSettlementReconcileDifferenceRecord {

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

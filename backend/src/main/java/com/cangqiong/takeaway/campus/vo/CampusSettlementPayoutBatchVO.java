package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusSettlementPayoutBatchVO {

    private String payoutBatchNo;
    private Long totalCount;
    private Long paidCount;
    private Long failedCount;
    private Long verifiedCount;
    private Long unverifiedCount;
    private BigDecimal totalPendingAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstRecordedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRecordedAt;
}

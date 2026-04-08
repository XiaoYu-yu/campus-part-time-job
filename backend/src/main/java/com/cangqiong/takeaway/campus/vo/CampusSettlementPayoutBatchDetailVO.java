package com.cangqiong.takeaway.campus.vo;

import lombok.Data;

import java.util.List;

@Data
public class CampusSettlementPayoutBatchDetailVO {

    private String payoutBatchNo;
    private Long totalCount;
    private Long paidCount;
    private Long failedCount;
    private Long verifiedCount;
    private Long unverifiedCount;
    private java.math.BigDecimal totalPendingAmount;
    private java.time.LocalDateTime firstRecordedAt;
    private java.time.LocalDateTime lastRecordedAt;
    private List<CampusSettlementRecordVO> records;
}

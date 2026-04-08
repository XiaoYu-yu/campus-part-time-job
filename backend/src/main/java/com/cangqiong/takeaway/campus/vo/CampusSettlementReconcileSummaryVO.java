package com.cangqiong.takeaway.campus.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampusSettlementReconcileSummaryVO {

    private Long totalCount;
    private Long pendingPayoutCount;
    private Long paidCount;
    private Long failedPayoutCount;
    private BigDecimal totalPendingAmount;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalFailedAmount;
}

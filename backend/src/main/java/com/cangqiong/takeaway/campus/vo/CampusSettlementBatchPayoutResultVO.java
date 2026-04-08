package com.cangqiong.takeaway.campus.vo;

import lombok.Data;

import java.util.List;

@Data
public class CampusSettlementBatchPayoutResultVO {

    private Integer totalRequested;
    private Integer successCount;
    private Integer skippedCount;
    private Integer failedCount;
    private List<Long> skippedIds;
    private List<Long> failedIds;
}

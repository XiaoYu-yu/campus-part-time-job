package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusSettlementReconcileDifferenceQuery {

    private Integer page = 1;
    private Integer pageSize;
    private Integer size;
    private String payoutBatchNo;
    private Long settlementRecordId;
    private String relayOrderId;
    private String differenceType;
    private String processStatus;
}

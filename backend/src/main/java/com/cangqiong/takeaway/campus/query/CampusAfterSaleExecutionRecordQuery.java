package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusAfterSaleExecutionRecordQuery {

    private Integer page = 1;
    private Integer pageSize;
    private Integer size;
    private String relayOrderId;
    private String executionStatus;
    private Boolean corrected;
}

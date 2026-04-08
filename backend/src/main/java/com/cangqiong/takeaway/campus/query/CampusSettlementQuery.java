package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusSettlementQuery {

    private Integer page = 1;
    private Integer pageSize;
    private Integer size;
    private String settlementStatus;
    private Long courierProfileId;
    private String relayOrderId;
}

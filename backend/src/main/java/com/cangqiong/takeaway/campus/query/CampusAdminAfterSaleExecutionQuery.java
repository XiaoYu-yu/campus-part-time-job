package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusAdminAfterSaleExecutionQuery {

    private Integer page = 1;
    private Integer pageSize;
    private Integer size;
    private String afterSaleExecutionStatus;
    private String decisionType;
    private Boolean correctedOnly;
    private Long customerUserId;
    private Long courierProfileId;
    private String relayOrderId;
}

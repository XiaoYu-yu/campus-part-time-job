package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusExceptionRecordQuery {

    private Integer page = 1;
    private Integer pageSize;
    private Integer size;
    private String relayOrderId;
    private Long courierProfileId;
    private String processStatus;
}

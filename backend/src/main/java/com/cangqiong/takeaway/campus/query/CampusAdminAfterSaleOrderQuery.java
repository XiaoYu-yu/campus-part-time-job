package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusAdminAfterSaleOrderQuery {

    private Integer page = 1;
    private Integer pageSize;
    private Integer size;
    private String orderStatus;
    private String afterSaleHandleAction;
    private Long courierProfileId;
    private Long customerUserId;
    private String relayOrderId;
}

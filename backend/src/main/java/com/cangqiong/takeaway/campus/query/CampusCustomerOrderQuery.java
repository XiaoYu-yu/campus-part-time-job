package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusCustomerOrderQuery {

    private String orderNo;
    private String status;
    private String paymentStatus;
    private String deliveryTargetType;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer size;
}

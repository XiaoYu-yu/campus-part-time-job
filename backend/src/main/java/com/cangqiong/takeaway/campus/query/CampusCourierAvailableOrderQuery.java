package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusCourierAvailableOrderQuery {

    private String orderNo;
    private String deliveryTargetType;
    private String deliveryBuilding;
    private Long pickupPointId;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer size;
}

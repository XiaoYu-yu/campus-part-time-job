package com.cangqiong.takeaway.campus.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampusCustomerOrderCreateDTO {

    private Long pickupPointId;
    private String targetType;
    private String deliveryBuilding;
    private String deliveryDetail;
    private String contactName;
    private String contactPhone;
    private String foodDescription;
    private String externalPlatformName;
    private String externalOrderRef;
    private String pickupCode;
    private String remark;
    private BigDecimal tipFee;
    private Integer urgentFlag;
}

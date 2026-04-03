package com.cangqiong.takeaway.dto;

import lombok.Data;

@Data
public class UserSubmitOrderDTO {

    private Long addressId;
    private String deliveryType;
    private String deliveryTime;
    private String remark;
}

package com.cangqiong.takeaway.dto;

import lombok.Data;

@Data
public class AddressDTO {

    private String consignee;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
}

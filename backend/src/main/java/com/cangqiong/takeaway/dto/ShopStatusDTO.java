package com.cangqiong.takeaway.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShopStatusDTO {

    private Boolean isOpen;
    private String restNotice;
    private List<BusinessHourDTO> businessHours;
}

package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.util.List;

@Data
public class ShopStatusVO {

    private Boolean isOpen;
    private String restNotice;
    private String lastUpdated;
    private List<ShopBusinessHourVO> businessHours;
}

package com.cangqiong.takeaway.entity;

import lombok.Data;

@Data
public class ShopBusinessHour {

    private Long id;
    private String dayKey;
    private String dayName;
    private Integer isOpen;
    private String openTime;
    private String closeTime;
    private Integer sort;
}

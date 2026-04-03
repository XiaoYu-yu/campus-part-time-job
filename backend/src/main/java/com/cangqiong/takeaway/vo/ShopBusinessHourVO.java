package com.cangqiong.takeaway.vo;

import lombok.Data;

@Data
public class ShopBusinessHourVO {

    private String day;
    private String dayName;
    private Boolean isOpen;
    private String openTime;
    private String closeTime;
}

package com.cangqiong.takeaway.dto;

import lombok.Data;

@Data
public class BusinessHourDTO {

    private String day;
    private String dayName;
    private Boolean isOpen;
    private String openTime;
    private String closeTime;
}

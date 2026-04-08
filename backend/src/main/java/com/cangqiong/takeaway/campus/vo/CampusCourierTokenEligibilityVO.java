package com.cangqiong.takeaway.campus.vo;

import lombok.Data;

@Data
public class CampusCourierTokenEligibilityVO {

    private Boolean eligible;
    private String reviewStatus;
    private Integer enabled;
    private String message;
}

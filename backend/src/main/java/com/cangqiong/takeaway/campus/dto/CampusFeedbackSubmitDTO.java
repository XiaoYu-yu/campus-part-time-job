package com.cangqiong.takeaway.campus.dto;

import lombok.Data;

@Data
public class CampusFeedbackSubmitDTO {

    private String submitterRole;
    private String category;
    private String content;
    private String contact;
    private String page;
    private String orderId;
}

package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusFeedbackQuery {

    private Integer page = 1;
    private Integer pageSize;
    private Integer size;
    private String submitterRole;
    private String category;
    private String status;
    private String orderId;
}

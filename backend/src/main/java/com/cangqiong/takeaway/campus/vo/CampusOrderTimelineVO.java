package com.cangqiong.takeaway.campus.vo;

import lombok.Data;

import java.util.List;

@Data
public class CampusOrderTimelineVO {

    private String orderId;
    private String orderStatus;
    private String paymentStatus;
    private String settlementStatus;
    private List<CampusOrderTimelineItemVO> items;
}

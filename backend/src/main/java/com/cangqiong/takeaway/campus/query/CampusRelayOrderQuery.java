package com.cangqiong.takeaway.campus.query;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class CampusRelayOrderQuery {

    private String orderNo;
    private String customerName;
    private String courierName;
    private String status;
    private String orderStatus;
    private String paymentStatus;
    private String deliveryTargetType;
    private String deliveryBuilding;
    private Long pickupPointId;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer size;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}

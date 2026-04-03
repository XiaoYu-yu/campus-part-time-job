package com.cangqiong.takeaway.dto;

import lombok.Data;

@Data
public class OrderItemDTO {

    private Long dishId;
    private Long setmealId;
    private Integer quantity;
}

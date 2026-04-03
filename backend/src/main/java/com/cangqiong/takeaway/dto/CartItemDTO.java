package com.cangqiong.takeaway.dto;

import lombok.Data;

@Data
public class CartItemDTO {

    private Long dishId;
    private Long setmealId;
    private Integer quantity;
}

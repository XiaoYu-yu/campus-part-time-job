package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private Long id;
    private Long dishId;
    private Long setmealId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
}

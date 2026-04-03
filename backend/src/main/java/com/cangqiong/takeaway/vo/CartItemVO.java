package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {

    private Long id;
    private Long dishId;
    private Long setmealId;
    private String name;
    private String description;
    private String image;
    private String type;
    private BigDecimal price;
    private Integer quantity;
    private Integer checked;
}

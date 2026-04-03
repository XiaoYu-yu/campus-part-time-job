package com.cangqiong.takeaway.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishDTO {

    private String name;
    private Long categoryId;
    private BigDecimal price;
    private String description;
    private String image;
}

package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishStatisticsVO {

    private Long dishId;
    private String name;
    private Integer saleCount;
    private BigDecimal saleAmount;
}

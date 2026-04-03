package com.cangqiong.takeaway.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SetmealDTO {

    private String name;
    private Long categoryId;
    private BigDecimal price;
    private String description;
    private String image;
    private List<Long> dishIds;
}

package com.cangqiong.takeaway.query;

import lombok.Data;

@Data
public class SetmealQuery {

    private Integer page = 1;
    private Integer size = 10;
    private String name;
    private Long categoryId;
}

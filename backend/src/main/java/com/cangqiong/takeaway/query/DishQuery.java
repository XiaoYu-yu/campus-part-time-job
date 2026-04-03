package com.cangqiong.takeaway.query;

import lombok.Data;

@Data
public class DishQuery {

    private Integer page = 1;
    private Integer size = 10;
    private String name;
    private Long categoryId;
}

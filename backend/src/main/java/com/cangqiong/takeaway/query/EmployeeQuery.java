package com.cangqiong.takeaway.query;

import lombok.Data;

@Data
public class EmployeeQuery {

    private Integer page = 1;
    private Integer size = 10;
    private String name;
    private String phone;
}

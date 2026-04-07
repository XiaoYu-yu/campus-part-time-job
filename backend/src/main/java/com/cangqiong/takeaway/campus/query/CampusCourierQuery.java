package com.cangqiong.takeaway.campus.query;

import lombok.Data;

@Data
public class CampusCourierQuery {

    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer size;
    private String realName;
    private String phone;
    private String studentNo;
    private String college;
    private String reviewStatus;
    private String dormitoryBuilding;
    private Integer enabled;
}

package com.cangqiong.takeaway.campus.vo;

import lombok.Data;

@Data
public class CampusPickupPointVO {

    private Long id;
    private String code;
    private String name;
    private String gateArea;
    private String description;
    private Integer enabled;
    private Integer sort;
}

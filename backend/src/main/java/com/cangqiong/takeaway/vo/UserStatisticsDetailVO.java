package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserStatisticsDetailVO {

    private Long totalUsers;
    private Long newUsers;
    private Long activeUsers;
    private String conversionRate;
    private String repurchaseRate;
    private List<String> regions;
    private List<Long> regionValues;
}

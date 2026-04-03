package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardStatisticsVO {

    private String totalSales;
    private Long totalOrders;
    private Long totalUsers;
    private Long newUsers;
    private String avgOrderValue;
    private String salesChange;
    private String salesChangeType;
    private String ordersChange;
    private String ordersChangeType;
    private String usersChange;
    private String usersChangeType;
    private String avgChange;
    private String avgChangeType;
    private String conversionRate;
    private String conversionChange;
    private String conversionChangeType;
    private String repurchaseRate;
    private String repurchaseChange;
    private String repurchaseChangeType;
    private SalesTrendVO orderTrend;
    private List<PopularDishVO> popularDishes;
    private ChartSeriesVO orderStatus;
    private ChartSeriesVO userRegion;
    private TimeSeriesVO salesTime;
}

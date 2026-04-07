package com.cangqiong.takeaway.campus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CampusDeliveryRuleVO {

    private String platformName;
    private List<String> dormitoryBuildings;
    private List<String> teachingBuildings;
    private List<String> libraryDeliveryPoints;
    private List<String> pickupPointRules;
    private List<String> dormitoryRules;
    private List<String> teachingBuildingRules;
    private List<String> libraryRules;
    private List<String> feeRules;
    private Integer priorityWindowMinutes;
    private BigDecimal baseFee;
    private BigDecimal priorityFeeMin;
    private BigDecimal priorityFeeMax;
    private BigDecimal tipFeeMin;
    private BigDecimal tipFeeMax;
    private Integer locationReportIntervalSeconds;
    private Integer locationRefreshIntervalSeconds;
    private String locationRefreshStrategy;
}

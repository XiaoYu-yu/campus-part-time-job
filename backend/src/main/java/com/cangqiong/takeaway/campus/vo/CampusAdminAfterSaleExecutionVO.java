package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusAdminAfterSaleExecutionVO {

    private String relayOrderId;
    private String orderStatus;
    private Long customerUserId;
    private Long courierProfileId;
    private String decisionType;
    private BigDecimal decisionAmount;
    private String afterSaleExecutionStatus;
    private String afterSaleExecutionRemark;
    private Integer afterSaleExecutionCorrected;
    private Long afterSaleExecutionCorrectedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleExecutedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleExecutionCorrectedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime afterSaleAppliedAt;
}

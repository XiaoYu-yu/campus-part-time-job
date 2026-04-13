package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusAfterSaleExecutionRecordVO {

    private Long id;
    private String relayOrderId;
    private String orderStatus;
    private Long customerUserId;
    private Long courierProfileId;
    private String decisionType;
    private BigDecimal decisionAmount;
    private String previousExecutionStatus;
    private String executionStatus;
    private String executionRemark;
    private String executionReferenceNo;
    private Long executedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedAt;

    private Integer corrected;
    private String currentExecutionStatus;
    private String currentExecutionRemark;
    private String currentExecutionReferenceNo;
    private Integer currentExecutionCorrected;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime currentExecutionCorrectedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

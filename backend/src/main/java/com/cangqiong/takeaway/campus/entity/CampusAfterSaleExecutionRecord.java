package com.cangqiong.takeaway.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusAfterSaleExecutionRecord {

    private Long id;
    private String relayOrderId;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

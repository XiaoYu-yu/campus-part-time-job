package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusOrderExceptionSummaryVO {

    private String relayOrderId;
    private String orderStatus;
    private Long courierProfileId;
    private String latestExceptionType;
    private String latestExceptionRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestExceptionReportedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestLocationReportedAt;

    private Long locationReportCount;
}

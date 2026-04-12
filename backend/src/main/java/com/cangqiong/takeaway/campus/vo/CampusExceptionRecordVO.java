package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusExceptionRecordVO {

    private Long id;
    private String relayOrderId;
    private String orderStatus;
    private Long customerUserId;
    private Long courierProfileId;
    private String courierName;
    private String exceptionType;
    private String exceptionRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedAt;

    private String processStatus;
    private String processResult;
    private Long processedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;

    private String adminNote;
    private String source;
    private String latestExceptionType;
    private String latestExceptionRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime latestExceptionReportedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

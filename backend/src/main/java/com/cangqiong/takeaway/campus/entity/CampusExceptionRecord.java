package com.cangqiong.takeaway.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusExceptionRecord {

    private Long id;
    private String relayOrderId;
    private Long courierProfileId;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusCourierRecentExceptionVO {

    private String relayOrderId;
    private String orderStatus;
    private Long customerUserId;
    private String exceptionType;
    private String exceptionRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime exceptionReportedAt;
}

package com.cangqiong.takeaway.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CampusLocationReport {

    private Long id;
    private String relayOrderId;
    private Long courierProfileId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String source;
    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

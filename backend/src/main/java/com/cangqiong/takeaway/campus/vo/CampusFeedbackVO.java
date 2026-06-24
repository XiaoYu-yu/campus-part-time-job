package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusFeedbackVO {

    private Long id;
    private String submitterRole;
    private String category;
    private String content;
    private String contact;
    private String page;
    private String orderId;
    private String status;
    private Long processedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;

    private String adminNote;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

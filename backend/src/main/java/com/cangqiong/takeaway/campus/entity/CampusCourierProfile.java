package com.cangqiong.takeaway.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusCourierProfile {

    private Long id;
    private Long userId;
    private String realName;
    private String studentNo;
    private String college;
    private String major;
    private String className;
    private String dormitoryBuilding;
    private String dormitoryRoom;
    private String idCardLast4;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String verificationPhotoUrl;
    private String scheduleAttachmentUrl;
    private String reviewStatus;
    private String reviewComment;
    private Long reviewedByEmployeeId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;

    private Integer enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusCourierProfileVO {

    private Long id;
    private Long userId;
    private String realName;
    private String phone;
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
    private Integer enabled;
    private Long reviewedByEmployeeId;
    private String reviewedByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

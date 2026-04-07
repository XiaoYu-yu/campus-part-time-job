package com.cangqiong.takeaway.campus.dto;

import lombok.Data;

@Data
public class CampusCourierProfileSubmitDTO {

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
}

package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusCustomerCourierOnboardingProfileVO {

    private Long profileId;
    private Long userId;
    private String realName;
    private String phone;
    private String gender;
    private String studentNo;
    private String campusZone;
    private String dormBuilding;
    private Integer enabledWorkInOwnBuilding;
    private String remark;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String reviewStatus;
    private String reviewRemark;
    private Integer enabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

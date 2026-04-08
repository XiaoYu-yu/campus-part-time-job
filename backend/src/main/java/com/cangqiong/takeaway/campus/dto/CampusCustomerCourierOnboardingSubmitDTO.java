package com.cangqiong.takeaway.campus.dto;

import lombok.Data;

@Data
public class CampusCustomerCourierOnboardingSubmitDTO {

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
}

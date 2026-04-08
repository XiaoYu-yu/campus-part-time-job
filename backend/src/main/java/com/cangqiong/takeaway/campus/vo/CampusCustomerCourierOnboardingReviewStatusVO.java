package com.cangqiong.takeaway.campus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampusCustomerCourierOnboardingReviewStatusVO {

    private String reviewStatus;
    private Integer enabled;
    private String reviewRemark;
    private Boolean canApplyCourierToken;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewedAt;
}

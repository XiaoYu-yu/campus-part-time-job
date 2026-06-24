package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminFeedbackProcessDTO {

    @NotBlank(message = "处理状态不能为空")
    private String status;

    @NotBlank(message = "处理备注不能为空")
    private String adminNote;
}

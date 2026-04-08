package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminAfterSaleHandleDTO {

    @NotBlank(message = "处理动作不能为空")
    private String action;

    @NotBlank(message = "处理备注不能为空")
    private String handleRemark;
}

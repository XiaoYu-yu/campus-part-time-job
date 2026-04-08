package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminAfterSaleExecutionDTO {

    @NotBlank(message = "售后执行结果不能为空")
    private String executionStatus;

    @NotBlank(message = "售后执行备注不能为空")
    private String executionRemark;

    private String executionReferenceNo;
}

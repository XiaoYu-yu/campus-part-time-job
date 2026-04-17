package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminSettlementBatchOperationDTO {

    @NotBlank(message = "操作结果不能为空")
    private String operationResult;

    @NotBlank(message = "操作备注不能为空")
    private String operationRemark;
}

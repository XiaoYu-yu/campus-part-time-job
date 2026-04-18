package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminSettlementReconcileDifferenceResolveDTO {

    @NotBlank(message = "处理结果不能为空")
    private String processResult;

    @NotBlank(message = "处理备注不能为空")
    private String processRemark;
}

package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminSettlementConfirmDTO {

    @NotBlank(message = "结算备注不能为空")
    private String settleRemark;
}

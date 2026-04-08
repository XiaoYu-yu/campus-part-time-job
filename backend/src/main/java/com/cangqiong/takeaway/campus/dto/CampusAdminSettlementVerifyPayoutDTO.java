package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminSettlementVerifyPayoutDTO {

    @NotBlank(message = "打款核对备注不能为空")
    private String verifyRemark;
}

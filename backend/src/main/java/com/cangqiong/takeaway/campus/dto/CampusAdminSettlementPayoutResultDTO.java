package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusAdminSettlementPayoutResultDTO {

    @NotBlank(message = "打款结果不能为空")
    private String payoutStatus;

    @NotBlank(message = "打款备注不能为空")
    private String payoutRemark;

    private String payoutReferenceNo;
}

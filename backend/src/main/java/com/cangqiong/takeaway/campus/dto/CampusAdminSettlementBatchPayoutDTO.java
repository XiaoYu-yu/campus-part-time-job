package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CampusAdminSettlementBatchPayoutDTO {

    @NotEmpty(message = "结算记录ID列表不能为空")
    private List<Long> settlementIds;

    @NotBlank(message = "批量打款结果不能为空")
    private String payoutStatus;

    @NotBlank(message = "批量打款备注不能为空")
    private String payoutRemark;

    private String batchNo;
}

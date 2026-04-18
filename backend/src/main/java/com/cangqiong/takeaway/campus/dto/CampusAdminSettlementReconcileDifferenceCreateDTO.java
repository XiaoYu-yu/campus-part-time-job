package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampusAdminSettlementReconcileDifferenceCreateDTO {

    @NotBlank(message = "打款批次号不能为空")
    private String payoutBatchNo;

    @NotNull(message = "结算记录ID不能为空")
    private Long settlementRecordId;

    @NotBlank(message = "差异类型不能为空")
    private String differenceType;

    private BigDecimal expectedAmount;
    private BigDecimal actualAmount;

    @NotBlank(message = "差异备注不能为空")
    private String differenceRemark;

    private String source;
}

package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampusAdminAfterSaleDecisionDTO {

    @NotBlank(message = "售后决策类型不能为空")
    private String decisionType;

    private BigDecimal decisionAmount;

    @NotBlank(message = "售后决策备注不能为空")
    private String decisionRemark;
}

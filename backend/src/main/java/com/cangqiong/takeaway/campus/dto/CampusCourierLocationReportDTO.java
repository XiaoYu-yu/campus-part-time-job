package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CampusCourierLocationReportDTO {

    @NotBlank(message = "订单号不能为空")
    private String relayOrderId;

    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @NotBlank(message = "位置来源不能为空")
    private String source;

    private String note;
}

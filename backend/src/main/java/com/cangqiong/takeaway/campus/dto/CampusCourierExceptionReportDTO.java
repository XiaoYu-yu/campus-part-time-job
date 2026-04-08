package com.cangqiong.takeaway.campus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampusCourierExceptionReportDTO {

    @NotBlank(message = "异常类型不能为空")
    private String exceptionType;

    @NotBlank(message = "异常说明不能为空")
    private String exceptionRemark;
}

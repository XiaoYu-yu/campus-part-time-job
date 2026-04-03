package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TimeSeriesVO {

    private List<String> hours;
    private List<BigDecimal> values;
}

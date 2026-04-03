package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChartSeriesVO {

    private List<String> names;
    private List<Long> values;
}

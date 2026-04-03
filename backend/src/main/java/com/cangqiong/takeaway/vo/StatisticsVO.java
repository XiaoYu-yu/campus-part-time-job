package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StatisticsVO {

    private LocalDate date;
    private BigDecimal amount;
    private Integer orderCount;
}

package com.cangqiong.takeaway.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SalesTrendVO {

    private List<String> dates;
    private List<BigDecimal> sales;
    private List<Long> orders;
}

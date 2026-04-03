package com.cangqiong.takeaway.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private List<OrderItemDTO> items;
}

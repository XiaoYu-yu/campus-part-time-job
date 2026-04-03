package com.cangqiong.takeaway.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单项实体类
 * 对应数据库表 order_item
 */
@Data
public class OrderItem {

    /** 订单项 ID */
    private Long id;

    /** 订单 ID */
    private String orderId;

    /** 菜品 ID */
    private Long dishId;

    /** 套餐 ID */
    private Long setmealId;

    /** 名称（菜品或套餐名称） */
    private String name;

    /** 单价 */
    private BigDecimal price;

    /** 数量 */
    private Integer quantity;

    /** 小计金额 */
    private BigDecimal total;
}

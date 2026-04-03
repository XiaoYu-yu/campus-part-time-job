package com.cangqiong.takeaway.entity;

import lombok.Data;

/**
 * 套餐菜品关联实体类
 * 对应数据库表 setmeal_dish
 */
@Data
public class SetmealDish {

    /** 关联 ID */
    private Long id;

    /** 套餐 ID */
    private Long setmealId;

    /** 菜品 ID */
    private Long dishId;

    /** 菜品数量 */
    private Integer quantity;
}

package com.cangqiong.takeaway.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 * 对应数据库表 dish
 */
@Data
public class Dish {

    /** 菜品 ID */
    private Long id;

    /** 菜品名称 */
    private String name;

    /** 分类 ID */
    private Long categoryId;

    /** 价格 */
    private BigDecimal price;

    /** 描述 */
    private String description;

    /** 图片 URL */
    private String image;

    /** 状态（1-启用，0-禁用） */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

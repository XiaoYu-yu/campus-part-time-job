package com.cangqiong.takeaway.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体类
 * 对应数据库表 category
 */
@Data
public class Category {

    /** 分类 ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 排序值 */
    private Integer sort;

    /** 状态（1-启用，0-禁用） */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

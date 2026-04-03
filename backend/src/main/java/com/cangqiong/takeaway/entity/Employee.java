package com.cangqiong.takeaway.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 员工实体类
 * 对应数据库表 employee
 */
@Data
public class Employee {

    /** 员工 ID */
    private Long id;

    /** 员工姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 密码 */
    private String password;

    /** 职位 */
    private String position;

    /** 部门 */
    private String department;

    /** 入职日期 */
    private LocalDate entryDate;

    /** 状态（1-启用，0-禁用） */
    private Integer status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

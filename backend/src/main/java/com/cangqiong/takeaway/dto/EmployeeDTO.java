package com.cangqiong.takeaway.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeDTO {

    private String name;
    private String phone;
    private String position;
    private String department;
    private LocalDate entryDate;
}

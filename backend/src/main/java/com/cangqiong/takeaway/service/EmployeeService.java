package com.cangqiong.takeaway.service;

import com.cangqiong.takeaway.dto.EmployeeDTO;
import com.cangqiong.takeaway.entity.Employee;
import com.cangqiong.takeaway.query.EmployeeQuery;
import com.cangqiong.takeaway.vo.PageResult;

/**
 * 员工服务接口
 * 定义员工相关的业务逻辑操作
 */
public interface EmployeeService {

    /**
     * 员工登录
     * @param phone 手机号
     * @param password 密码
     * @return 登录成功的员工信息
     */
    Employee login(String phone, String password);

    /**
     * 根据 ID 获取员工信息
     * @param id 员工 ID
     * @return 员工信息
     */
    Employee getById(Long id);

    /**
     * 分页查询员工列表
     * @param query 查询参数
     * @return 员工分页结果
     */
    PageResult<Employee> pageQuery(EmployeeQuery query);

    /**
     * 新增员工
     * @param dto 员工信息
     */
    void add(EmployeeDTO dto);

    /**
     * 更新员工信息
     * @param id 员工 ID
     * @param dto 员工信息
     */
    void update(Long id, EmployeeDTO dto);

    /**
     * 删除员工
     * @param id 员工 ID
     */
    void delete(Long id);

    /**
     * 修改员工状态
     * @param id 员工 ID
     * @param status 状态（1-启用，0-禁用）
     */
    void updateStatus(Long id, Integer status);
}

package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.dto.EmployeeDTO;
import com.cangqiong.takeaway.entity.Employee;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.mapper.EmployeeMapper;
import com.cangqiong.takeaway.query.EmployeeQuery;
import com.cangqiong.takeaway.service.EmployeeService;
import com.cangqiong.takeaway.service.PasswordService;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private PasswordService passwordService;

    @Override
    public Employee login(String phone, String password) {
        Employee employee = employeeMapper.selectByPhone(phone);

        if (employee == null) {
            throw new BusinessException("账号不存在");
        }

        if (!passwordService.matches(password, employee.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (employee.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }

        upgradePasswordIfNeeded(employee.getId(), password, employee.getPassword());
        return employeeMapper.selectById(employee.getId());
    }

    @Override
    public Employee getById(Long id) {
        return employeeMapper.selectById(id);
    }

    @Override
    public PageResult<Employee> pageQuery(EmployeeQuery query) {
        log.info("员工分页查询，参数：{}", query);

        int offset = (query.getPage() - 1) * query.getSize();
        List<Employee> records = employeeMapper.selectByCondition(query.getName(), query.getPhone(), offset, query.getSize());
        Long total = employeeMapper.countByCondition(query.getName(), query.getPhone());

        PageResult<Employee> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize(Long.valueOf(query.getSize()));
        pageResult.setCurrent(Long.valueOf(query.getPage()));
        pageResult.setPages((total + query.getSize() - 1) / query.getSize());

        return pageResult;
    }

    @Override
    @Transactional
    public void add(EmployeeDTO dto) {
        log.info("新增员工：{}", dto);

        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);

        employee.setPassword(passwordService.encode("123456"));
        employee.setStatus(1);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());

        employeeMapper.insert(employee);
        log.info("员工新增成功，ID：{}", employee.getId());
    }

    @Override
    @Transactional
    public void update(Long id, EmployeeDTO dto) {
        log.info("更新员工，ID：{}，参数：{}", id, dto);

        Employee existingEmployee = employeeMapper.selectById(id);
        if (existingEmployee == null) {
            throw new BusinessException("员工不存在");
        }

        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);
        employee.setId(id);
        employee.setStatus(existingEmployee.getStatus());
        employee.setPassword(existingEmployee.getPassword());
        employee.setCreatedAt(existingEmployee.getCreatedAt());
        employee.setUpdatedAt(LocalDateTime.now());

        employeeMapper.update(employee);
        log.info("员工更新成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除员工，ID：{}", id);

        Employee existingEmployee = employeeMapper.selectById(id);
        if (existingEmployee == null) {
            throw new BusinessException("员工不存在");
        }

        employeeMapper.deleteById(id);
        log.info("员工删除成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        log.info("修改员工状态，ID：{}，状态：{}", id, status);

        Employee existingEmployee = employeeMapper.selectById(id);
        if (existingEmployee == null) {
            throw new BusinessException("员工不存在");
        }

        employeeMapper.updateStatus(id, status);
        log.info("员工状态修改成功，ID：{}，状态：{}", id, status);
    }

    private void upgradePasswordIfNeeded(Long employeeId, String rawPassword, String storedPassword) {
        if (passwordService.shouldUpgrade(storedPassword)) {
            employeeMapper.updatePassword(employeeId, passwordService.encode(rawPassword));
        }
    }
}

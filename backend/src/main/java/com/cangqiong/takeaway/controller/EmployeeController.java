package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.EmployeeDTO;
import com.cangqiong.takeaway.dto.EmployeeLoginDTO;
import com.cangqiong.takeaway.entity.Employee;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.query.EmployeeQuery;
import com.cangqiong.takeaway.service.EmployeeService;
import com.cangqiong.takeaway.utils.JwtUtil;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.EmployeeVO;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 员工管理控制器
 * 处理员工相关的 HTTP 请求，包括登录、查询、增删改查等操作
 */
@Slf4j
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 员工登录
     * @param loginDTO 登录参数（手机号、密码）
     * @return 登录成功返回员工信息和 token
     */
    @PostMapping("/api/employees/login")
    public Result<Map<String, Object>> login(@RequestBody EmployeeLoginDTO loginDTO) {
        log.info("员工登录: {}", loginDTO.getPhone());

        Employee employee = employeeService.login(loginDTO.getPhone(), loginDTO.getPassword());

        String token = jwtUtil.generateToken(employee.getId(), employee.getName(), "employee");

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employee, employeeVO);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("employee", employeeVO);

        return Result.success(result);
    }

    /**
     * 获取当前登录员工信息
     * @return 当前员工信息
     */
    @GetMapping("/api/employees/info")
    public Result<EmployeeVO> info() {
        Long userId = BaseContext.getCurrentUserId();
        log.info("获取当前员工信息: {}", userId);

        Employee employee = employeeService.getById(userId);

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employee, employeeVO);

        return Result.success(employeeVO);
    }

    /**
     * 员工登出
     * @return 登出结果
     */
    @PostMapping("/api/employees/logout")
    public Result<Void> logout() {
        log.info("员工登出");
        return Result.success();
    }

    /**
     * 分页查询员工列表
     * @param query 查询参数（页码、页大小、姓名、手机号等）
     * @return 员工分页列表
     */
    @GetMapping("/api/employees")
    public Result<PageResult<EmployeeVO>> page(EmployeeQuery query) {
        log.info("分页查询员工列表: page={}, size={}, name={}, phone={}", 
                query.getPage(), query.getSize(), query.getName(), query.getPhone());

        PageResult<Employee> pageResult = employeeService.pageQuery(query);

        List<EmployeeVO> voList = pageResult.getRecords().stream().map(employee -> {
            EmployeeVO vo = new EmployeeVO();
            BeanUtils.copyProperties(employee, vo);
            return vo;
        }).collect(Collectors.toList());

        PageResult<EmployeeVO> result = new PageResult<>();
        BeanUtils.copyProperties(pageResult, result);
        result.setRecords(voList);

        return Result.success(result);
    }

    /**
     * 根据 ID 获取员工详情
     * @param id 员工 ID
     * @return 员工详情
     */
    @GetMapping("/api/employees/{id}")
    public Result<EmployeeVO> getById(@PathVariable Long id) {
        log.info("获取员工详情: {}", id);

        Employee employee = employeeService.getById(id);

        EmployeeVO employeeVO = new EmployeeVO();
        BeanUtils.copyProperties(employee, employeeVO);

        return Result.success(employeeVO);
    }

    /**
     * 新增员工
     * @param dto 员工信息
     * @return 操作结果
     */
    @PostMapping("/api/employees")
    public Result<Void> add(@RequestBody EmployeeDTO dto) {
        log.info("新增员工: {}", dto.getName());

        employeeService.add(dto);

        return Result.success();
    }

    /**
     * 更新员工信息
     * @param id 员工 ID
     * @param dto 员工信息
     * @return 操作结果
     */
    @PutMapping("/api/employees/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody EmployeeDTO dto) {
        log.info("更新员工: {}", id);

        employeeService.update(id, dto);

        return Result.success();
    }

    /**
     * 删除员工
     * @param id 员工 ID
     * @return 操作结果
     */
    @DeleteMapping("/api/employees/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除员工: {}", id);

        employeeService.delete(id);

        return Result.success();
    }

    /**
     * 修改员工状态
     * @param id 员工 ID
     * @param params 状态参数
     * @return 操作结果
     */
    @PutMapping("/api/employees/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        log.info("修改员工状态: id={}, status={}", id, status);

        employeeService.updateStatus(id, status);

        return Result.success();
    }
}

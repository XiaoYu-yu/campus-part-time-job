package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.DishDTO;
import com.cangqiong.takeaway.query.DishQuery;
import com.cangqiong.takeaway.service.DishService;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.DishVO;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜品管理控制器
 * 处理菜品相关的 HTTP 请求，包括分页查询、增删改查、状态修改等操作
 */
@Slf4j
@RestController
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 菜品分页查询
     * @param query 查询参数（页码、页大小、分类 ID、名称等）
     * @return 菜品分页列表
     */
    @GetMapping("/api/dishes")
    public Result<PageResult<DishVO>> pageQuery(DishQuery query) {
        log.info("菜品分页查询，参数：{}", query);

        PageResult<DishVO> pageResult = dishService.pageQuery(query);

        return Result.success(pageResult);
    }

    /**
     * 根据 ID 获取菜品详情
     * @param id 菜品 ID
     * @return 菜品详情
     */
    @GetMapping("/api/dishes/{id}")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("获取菜品详情: {}", id);

        DishVO dishVO = dishService.getById(id);

        return Result.success(dishVO);
    }

    /**
     * 新增菜品
     * @param dto 菜品信息
     * @return 操作结果
     */
    @PostMapping("/api/dishes")
    public Result<Void> add(@RequestBody DishDTO dto) {
        log.info("新增菜品: {}", dto.getName());

        dishService.add(dto);

        return Result.success();
    }

    /**
     * 更新菜品信息
     * @param id 菜品 ID
     * @param dto 菜品信息
     * @return 操作结果
     */
    @PutMapping("/api/dishes/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DishDTO dto) {
        log.info("更新菜品: {}", id);

        dishService.update(id, dto);

        return Result.success();
    }

    /**
     * 删除菜品
     * @param id 菜品 ID
     * @return 操作结果
     */
    @DeleteMapping("/api/dishes/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除菜品: {}", id);

        dishService.delete(id);

        return Result.success();
    }

    /**
     * 修改菜品状态
     * @param id 菜品 ID
     * @param params 状态参数
     * @return 操作结果
     */
    @PutMapping("/api/dishes/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        log.info("修改菜品状态: id={}, status={}", id, status);

        dishService.updateStatus(id, status);

        return Result.success();
    }

    /**
     * 批量修改菜品状态
     * @param params 包含菜品 ID 列表和状态参数
     * @return 操作结果
     */
    @PutMapping("/api/dishes/status/batch")
    public Result<Void> batchUpdateStatus(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        Integer status = (Integer) params.get("status");
        log.info("批量修改菜品状态: ids={}, status={}", ids, status);

        dishService.batchUpdateStatus(ids, status);

        return Result.success();
    }
}

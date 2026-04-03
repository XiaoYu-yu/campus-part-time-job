package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.CategoryDTO;
import com.cangqiong.takeaway.entity.Category;
import com.cangqiong.takeaway.service.CategoryService;
import com.cangqiong.takeaway.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分类管理控制器
 * 处理菜品分类相关的 HTTP 请求，包括查询、增删改查等操作
 */
@Slf4j
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表
     * @return 所有分类列表
     */
    @GetMapping("/api/categories")
    public Result<List<Category>> list() {
        log.info("获取分类列表");

        List<Category> categories = categoryService.list();

        return Result.success(categories);
    }

    /**
     * 根据 ID 获取分类详情
     * @param id 分类 ID
     * @return 分类详情
     */
    @GetMapping("/api/categories/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        log.info("获取分类详情: {}", id);

        Category category = categoryService.getById(id);

        return Result.success(category);
    }

    /**
     * 新增分类
     * @param dto 分类信息
     * @return 操作结果
     */
    @PostMapping("/api/categories")
    public Result<Void> add(@RequestBody CategoryDTO dto) {
        log.info("新增分类: {}", dto.getName());

        categoryService.add(dto);

        return Result.success();
    }

    /**
     * 更新分类信息
     * @param id 分类 ID
     * @param dto 分类信息
     * @return 操作结果
     */
    @PutMapping("/api/categories/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        log.info("更新分类: {}", id);

        categoryService.update(id, dto);

        return Result.success();
    }

    /**
     * 删除分类
     * @param id 分类 ID
     * @return 操作结果
     */
    @DeleteMapping("/api/categories/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除分类: {}", id);

        categoryService.delete(id);

        return Result.success();
    }

    /**
     * 修改分类状态
     * @param id 分类 ID
     * @param params 状态参数
     * @return 操作结果
     */
    @PutMapping("/api/categories/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        log.info("修改分类状态: id={}, status={}", id, status);

        categoryService.updateStatus(id, status);

        return Result.success();
    }

    /**
     * 修改分类排序
     * @param id 分类 ID
     * @param params 排序参数
     * @return 操作结果
     */
    @PutMapping("/api/categories/{id}/sort")
    public Result<Void> updateSort(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer sort = params.get("sort");
        log.info("修改分类排序: id={}, sort={}", id, sort);

        categoryService.updateSort(id, sort);

        return Result.success();
    }
}

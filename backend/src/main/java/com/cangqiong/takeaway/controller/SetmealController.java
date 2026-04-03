package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.SetmealDTO;
import com.cangqiong.takeaway.query.SetmealQuery;
import com.cangqiong.takeaway.service.SetmealService;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.PageResult;
import com.cangqiong.takeaway.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 套餐管理控制器
 * 处理套餐相关的 HTTP 请求，包括分页查询、增删改查、状态修改等操作
 */
@Slf4j
@RestController
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 套餐分页查询
     * @param query 查询参数（页码、页大小、分类 ID、名称等）
     * @return 套餐分页列表
     */
    @GetMapping("/api/setmeals")
    public Result<PageResult<SetmealVO>> pageQuery(SetmealQuery query) {
        log.info("套餐分页查询，参数：{}", query);

        PageResult<SetmealVO> pageResult = setmealService.pageQuery(query);

        return Result.success(pageResult);
    }

    /**
     * 根据 ID 获取套餐详情
     * @param id 套餐 ID
     * @return 套餐详情
     */
    @GetMapping("/api/setmeals/{id}")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("获取套餐详情: {}", id);

        SetmealVO setmealVO = setmealService.getById(id);

        return Result.success(setmealVO);
    }

    /**
     * 新增套餐
     * @param dto 套餐信息
     * @return 操作结果
     */
    @PostMapping("/api/setmeals")
    public Result<Void> add(@RequestBody SetmealDTO dto) {
        log.info("新增套餐: {}", dto.getName());

        setmealService.add(dto);

        return Result.success();
    }

    /**
     * 更新套餐信息
     * @param id 套餐 ID
     * @param dto 套餐信息
     * @return 操作结果
     */
    @PutMapping("/api/setmeals/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SetmealDTO dto) {
        log.info("更新套餐: {}", id);

        setmealService.update(id, dto);

        return Result.success();
    }

    /**
     * 删除套餐
     * @param id 套餐 ID
     * @return 操作结果
     */
    @DeleteMapping("/api/setmeals/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除套餐: {}", id);

        setmealService.delete(id);

        return Result.success();
    }

    /**
     * 修改套餐状态
     * @param id 套餐 ID
     * @param params 状态参数
     * @return 操作结果
     */
    @PutMapping("/api/setmeals/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        log.info("修改套餐状态: id={}, status={}", id, status);

        setmealService.updateStatus(id, status);

        return Result.success();
    }

    /**
     * 批量修改套餐状态
     * @param params 包含套餐 ID 列表和状态参数
     * @return 操作结果
     */
    @PutMapping("/api/setmeals/status/batch")
    public Result<Void> batchUpdateStatus(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        Integer status = (Integer) params.get("status");
        log.info("批量修改套餐状态: ids={}, status={}", ids, status);

        setmealService.batchUpdateStatus(ids, status);

        return Result.success();
    }
}

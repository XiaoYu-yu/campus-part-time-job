package com.cangqiong.takeaway.service;

import com.cangqiong.takeaway.dto.SetmealDTO;
import com.cangqiong.takeaway.query.SetmealQuery;
import com.cangqiong.takeaway.vo.PageResult;
import com.cangqiong.takeaway.vo.SetmealVO;

import java.util.List;

/**
 * 套餐服务接口
 * 定义套餐相关的业务逻辑操作
 */
public interface SetmealService {

    /**
     * 分页查询套餐列表
     * @param query 查询参数
     * @return 套餐分页结果
     */
    PageResult<SetmealVO> pageQuery(SetmealQuery query);

    /**
     * 根据 ID 获取套餐详情
     * @param id 套餐 ID
     * @return 套餐详情
     */
    SetmealVO getById(Long id);

    /**
     * 新增套餐
     * @param dto 套餐信息
     */
    void add(SetmealDTO dto);

    /**
     * 更新套餐信息
     * @param id 套餐 ID
     * @param dto 套餐信息
     */
    void update(Long id, SetmealDTO dto);

    /**
     * 删除套餐
     * @param id 套餐 ID
     */
    void delete(Long id);

    /**
     * 修改套餐状态
     * @param id 套餐 ID
     * @param status 状态（1-启用，0-禁用）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 批量修改套餐状态
     * @param ids 套餐 ID 列表
     * @param status 状态（1-启用，0-禁用）
     */
    void batchUpdateStatus(List<Long> ids, Integer status);
}

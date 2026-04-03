package com.cangqiong.takeaway.service;

import com.cangqiong.takeaway.dto.DishDTO;
import com.cangqiong.takeaway.query.DishQuery;
import com.cangqiong.takeaway.vo.DishVO;
import com.cangqiong.takeaway.vo.PageResult;

import java.util.List;

/**
 * 菜品服务接口
 * 定义菜品相关的业务逻辑操作
 */
public interface DishService {

    /**
     * 分页查询菜品列表
     * @param query 查询参数
     * @return 菜品分页结果
     */
    PageResult<DishVO> pageQuery(DishQuery query);

    /**
     * 根据 ID 获取菜品详情
     * @param id 菜品 ID
     * @return 菜品详情
     */
    DishVO getById(Long id);

    /**
     * 新增菜品
     * @param dto 菜品信息
     */
    void add(DishDTO dto);

    /**
     * 更新菜品信息
     * @param id 菜品 ID
     * @param dto 菜品信息
     */
    void update(Long id, DishDTO dto);

    /**
     * 删除菜品
     * @param id 菜品 ID
     */
    void delete(Long id);

    /**
     * 修改菜品状态
     * @param id 菜品 ID
     * @param status 状态（1-启用，0-禁用）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 批量修改菜品状态
     * @param ids 菜品 ID 列表
     * @param status 状态（1-启用，0-禁用）
     */
    void batchUpdateStatus(List<Long> ids, Integer status);
}

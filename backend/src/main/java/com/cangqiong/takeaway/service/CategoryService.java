package com.cangqiong.takeaway.service;

import com.cangqiong.takeaway.dto.CategoryDTO;
import com.cangqiong.takeaway.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * 定义菜品分类相关的业务逻辑操作
 */
public interface CategoryService {

    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    List<Category> list();

    /**
     * 根据 ID 获取分类信息
     * @param id 分类 ID
     * @return 分类信息
     */
    Category getById(Long id);

    /**
     * 新增分类
     * @param dto 分类信息
     */
    void add(CategoryDTO dto);

    /**
     * 更新分类信息
     * @param id 分类 ID
     * @param dto 分类信息
     */
    void update(Long id, CategoryDTO dto);

    /**
     * 删除分类
     * @param id 分类 ID
     */
    void delete(Long id);

    /**
     * 修改分类状态
     * @param id 分类 ID
     * @param status 状态（1-启用，0-禁用）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 修改分类排序
     * @param id 分类 ID
     * @param sort 排序值
     */
    void updateSort(Long id, Integer sort);
}

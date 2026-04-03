package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.dto.CategoryDTO;
import com.cangqiong.takeaway.entity.Category;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.mapper.CategoryMapper;
import com.cangqiong.takeaway.mapper.DishMapper;
import com.cangqiong.takeaway.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public List<Category> list() {
        return categoryMapper.selectAll();
    }

    @Override
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(CategoryDTO dto) {
        log.info("新增分类：{}", dto);

        Long count = categoryMapper.countByName(dto.getName());
        if (count != null && count > 0) {
            throw new BusinessException("分类名称已存在");
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category);

        category.setStatus(1);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        categoryMapper.insert(category);
        log.info("分类新增成功，ID：{}", category.getId());
    }

    @Override
    @Transactional
    public void update(Long id, CategoryDTO dto) {
        log.info("更新分类，ID：{}，参数：{}", id, dto);

        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        if (!existingCategory.getName().equals(dto.getName())) {
            Long count = categoryMapper.countByName(dto.getName());
            if (count != null && count > 0) {
                throw new BusinessException("分类名称已存在");
            }
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        category.setId(id);
        category.setStatus(existingCategory.getStatus());
        category.setCreatedAt(existingCategory.getCreatedAt());
        category.setUpdatedAt(LocalDateTime.now());

        categoryMapper.update(category);
        log.info("分类更新成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除分类，ID：{}", id);

        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        Long dishCount = dishMapper.countByCategoryId(id);
        if (dishCount != null && dishCount > 0) {
            throw new BusinessException("该分类下存在菜品，无法删除");
        }

        categoryMapper.deleteById(id);
        log.info("分类删除成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        log.info("修改分类状态，ID：{}，状态：{}", id, status);

        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        categoryMapper.updateStatus(id, status);
        log.info("分类状态修改成功，ID：{}，状态：{}", id, status);
    }

    @Override
    @Transactional
    public void updateSort(Long id, Integer sort) {
        log.info("修改分类排序，ID：{}，排序：{}", id, sort);

        Category existingCategory = categoryMapper.selectById(id);
        if (existingCategory == null) {
            throw new BusinessException("分类不存在");
        }

        categoryMapper.updateSort(id, sort);
        log.info("分类排序修改成功，ID：{}，排序：{}", id, sort);
    }
}

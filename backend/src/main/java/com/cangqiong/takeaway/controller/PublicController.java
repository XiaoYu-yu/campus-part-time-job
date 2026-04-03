package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.entity.Category;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.mapper.CategoryMapper;
import com.cangqiong.takeaway.mapper.DishMapper;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.DishVO;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PublicController {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @GetMapping("/api/public/categories")
    public Result<List<Category>> listCategories() {
        return Result.success(categoryMapper.selectEnabled());
    }

    @GetMapping("/api/public/dishes")
    public Result<PageResult<DishVO>> listDishes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safePageSize = pageSize == null || pageSize < 1 ? 20 : pageSize;
        int offset = (safePage - 1) * safePageSize;

        List<DishVO> records = dishMapper.selectEnabledByCondition(name, categoryId, offset, safePageSize);
        Long total = dishMapper.countEnabledByCondition(name, categoryId);

        PageResult<DishVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setCurrent((long) safePage);
        pageResult.setSize((long) safePageSize);
        pageResult.setPages((total + safePageSize - 1) / safePageSize);

        return Result.success(pageResult);
    }

    @GetMapping("/api/public/dishes/{id}")
    public Result<DishVO> getDish(@PathVariable Long id) {
        DishVO dishVO = dishMapper.selectEnabledById(id);
        if (dishVO == null) {
            throw new BusinessException("菜品不存在或已下架");
        }
        return Result.success(dishVO);
    }
}

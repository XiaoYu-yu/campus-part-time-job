package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.dto.DishDTO;
import com.cangqiong.takeaway.entity.Dish;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.mapper.DishMapper;
import com.cangqiong.takeaway.query.DishQuery;
import com.cangqiong.takeaway.service.DishService;
import com.cangqiong.takeaway.vo.DishVO;
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
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult<DishVO> pageQuery(DishQuery query) {
        log.info("菜品分页查询，参数：{}", query);

        int offset = (query.getPage() - 1) * query.getSize();
        List<DishVO> records = dishMapper.selectByCondition(query.getName(), query.getCategoryId(), offset, query.getSize());
        Long total = dishMapper.countByCondition(query.getName(), query.getCategoryId());

        PageResult<DishVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize(Long.valueOf(query.getSize()));
        pageResult.setCurrent(Long.valueOf(query.getPage()));
        pageResult.setPages((total + query.getSize() - 1) / query.getSize());

        return pageResult;
    }

    @Override
    public DishVO getById(Long id) {
        log.info("获取菜品详情，ID：{}", id);

        DishVO dishVO = dishMapper.selectById(id);
        if (dishVO == null) {
            throw new BusinessException("菜品不存在");
        }

        return dishVO;
    }

    @Override
    @Transactional
    public void add(DishDTO dto) {
        log.info("新增菜品：{}", dto.getName());

        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);

        dish.setStatus(1);
        dish.setCreatedAt(LocalDateTime.now());
        dish.setUpdatedAt(LocalDateTime.now());

        dishMapper.insert(dish);
        log.info("菜品新增成功，ID：{}", dish.getId());
    }

    @Override
    @Transactional
    public void update(Long id, DishDTO dto) {
        log.info("更新菜品，ID：{}，参数：{}", id, dto);

        DishVO existingDish = dishMapper.selectById(id);
        if (existingDish == null) {
            throw new BusinessException("菜品不存在");
        }

        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        dish.setId(id);
        dish.setStatus(existingDish.getStatus());
        dish.setCreatedAt(existingDish.getCreatedAt());
        dish.setUpdatedAt(LocalDateTime.now());

        dishMapper.update(dish);
        log.info("菜品更新成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除菜品，ID：{}", id);

        DishVO existingDish = dishMapper.selectById(id);
        if (existingDish == null) {
            throw new BusinessException("菜品不存在");
        }

        dishMapper.deleteById(id);
        log.info("菜品删除成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        log.info("修改菜品状态，ID：{}，状态：{}", id, status);

        DishVO existingDish = dishMapper.selectById(id);
        if (existingDish == null) {
            throw new BusinessException("菜品不存在");
        }

        dishMapper.updateStatus(id, status);
        log.info("菜品状态修改成功，ID：{}，状态：{}", id, status);
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        log.info("批量修改菜品状态，IDs：{}，状态：{}", ids, status);

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要操作的菜品");
        }

        dishMapper.batchUpdateStatus(ids, status);
        log.info("菜品状态批量修改成功，IDs：{}，状态：{}", ids, status);
    }
}

package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.dto.SetmealDTO;
import com.cangqiong.takeaway.entity.Setmeal;
import com.cangqiong.takeaway.entity.SetmealDish;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.mapper.DishMapper;
import com.cangqiong.takeaway.mapper.SetmealDishMapper;
import com.cangqiong.takeaway.mapper.SetmealMapper;
import com.cangqiong.takeaway.query.SetmealQuery;
import com.cangqiong.takeaway.service.SetmealService;
import com.cangqiong.takeaway.vo.DishVO;
import com.cangqiong.takeaway.vo.PageResult;
import com.cangqiong.takeaway.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult<SetmealVO> pageQuery(SetmealQuery query) {
        log.info("套餐分页查询，参数：{}", query);

        int offset = (query.getPage() - 1) * query.getSize();
        List<SetmealVO> records = setmealMapper.selectByCondition(query.getName(), query.getCategoryId(), offset, query.getSize());
        Long total = setmealMapper.countByCondition(query.getName(), query.getCategoryId());

        PageResult<SetmealVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize(Long.valueOf(query.getSize()));
        pageResult.setCurrent(Long.valueOf(query.getPage()));
        pageResult.setPages((total + query.getSize() - 1) / query.getSize());

        return pageResult;
    }

    @Override
    public SetmealVO getById(Long id) {
        log.info("获取套餐详情，ID：{}", id);

        SetmealVO setmealVO = setmealMapper.selectById(id);
        if (setmealVO == null) {
            throw new BusinessException("套餐不存在");
        }

        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        List<DishVO> dishes = new ArrayList<>();
        for (SetmealDish setmealDish : setmealDishes) {
            DishVO dishVO = dishMapper.selectById(setmealDish.getDishId());
            if (dishVO != null) {
                dishes.add(dishVO);
            }
        }
        setmealVO.setDishes(dishes);

        return setmealVO;
    }

    @Override
    @Transactional
    public void add(SetmealDTO dto) {
        log.info("新增套餐：{}", dto.getName());

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto, setmeal);

        setmeal.setStatus(1);
        setmeal.setCreatedAt(LocalDateTime.now());
        setmeal.setUpdatedAt(LocalDateTime.now());

        setmealMapper.insert(setmeal);

        if (dto.getDishIds() != null && !dto.getDishIds().isEmpty()) {
            List<SetmealDish> setmealDishes = new ArrayList<>();
            for (Long dishId : dto.getDishIds()) {
                SetmealDish setmealDish = new SetmealDish();
                setmealDish.setSetmealId(setmeal.getId());
                setmealDish.setDishId(dishId);
                setmealDish.setQuantity(1);
                setmealDishes.add(setmealDish);
            }
            setmealDishMapper.batchInsert(setmealDishes);
        }

        log.info("套餐新增成功，ID：{}", setmeal.getId());
    }

    @Override
    @Transactional
    public void update(Long id, SetmealDTO dto) {
        log.info("更新套餐，ID：{}，参数：{}", id, dto);

        SetmealVO existingSetmeal = setmealMapper.selectById(id);
        if (existingSetmeal == null) {
            throw new BusinessException("套餐不存在");
        }

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto, setmeal);
        setmeal.setId(id);
        setmeal.setStatus(existingSetmeal.getStatus());
        setmeal.setCreatedAt(existingSetmeal.getCreatedAt());
        setmeal.setUpdatedAt(LocalDateTime.now());

        setmealMapper.update(setmeal);

        setmealDishMapper.deleteBySetmealId(id);

        if (dto.getDishIds() != null && !dto.getDishIds().isEmpty()) {
            List<SetmealDish> setmealDishes = new ArrayList<>();
            for (Long dishId : dto.getDishIds()) {
                SetmealDish setmealDish = new SetmealDish();
                setmealDish.setSetmealId(id);
                setmealDish.setDishId(dishId);
                setmealDish.setQuantity(1);
                setmealDishes.add(setmealDish);
            }
            setmealDishMapper.batchInsert(setmealDishes);
        }

        log.info("套餐更新成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("删除套餐，ID：{}", id);

        SetmealVO existingSetmeal = setmealMapper.selectById(id);
        if (existingSetmeal == null) {
            throw new BusinessException("套餐不存在");
        }

        setmealDishMapper.deleteBySetmealId(id);
        setmealMapper.deleteById(id);

        log.info("套餐删除成功，ID：{}", id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        log.info("修改套餐状态，ID：{}，状态：{}", id, status);

        SetmealVO existingSetmeal = setmealMapper.selectById(id);
        if (existingSetmeal == null) {
            throw new BusinessException("套餐不存在");
        }

        setmealMapper.updateStatus(id, status);
        log.info("套餐状态修改成功，ID：{}，状态：{}", id, status);
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        log.info("批量修改套餐状态，IDs：{}，状态：{}", ids, status);

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要操作的套餐");
        }

        setmealMapper.batchUpdateStatus(ids, status);
        log.info("套餐状态批量修改成功，IDs：{}，状态：{}", ids, status);
    }
}

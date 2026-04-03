package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.SetmealDish;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    @Insert("INSERT INTO setmeal_dish (setmeal_id, dish_id, quantity) " +
            "VALUES (#{setmealId}, #{dishId}, #{quantity})")
    void insert(SetmealDish setmealDish);

    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);

    @Insert("<script>" +
            "INSERT INTO setmeal_dish (setmeal_id, dish_id, quantity) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.setmealId}, #{item.dishId}, #{item.quantity})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("list") List<SetmealDish> list);
}

package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.OrderItem;
import com.cangqiong.takeaway.vo.OrderItemVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Insert("<script>" +
            "INSERT INTO order_item (order_id, dish_id, setmeal_id, name, price, quantity, total) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.orderId}, #{item.dishId}, #{item.setmealId}, #{item.name}, #{item.price}, #{item.quantity}, #{item.total})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("items") List<OrderItem> items);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItemVO> selectByOrderId(String orderId);
}

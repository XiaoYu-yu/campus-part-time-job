package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.Order;
import com.cangqiong.takeaway.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("<script>" +
            "SELECT * FROM orders " +
            "<where>" +
            "<if test='orderNo != null and orderNo != \"\"'>AND id LIKE CONCAT('%', #{orderNo}, '%')</if>" +
            "<if test='customerName != null and customerName != \"\"'>AND customer_name LIKE CONCAT('%', #{customerName}, '%')</if>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "<if test='beginTime != null'>AND created_at &gt;= #{beginTime}</if>" +
            "<if test='endTime != null'>AND created_at &lt;= #{endTime}</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<OrderVO> selectByCondition(
            @Param("orderNo") String orderNo,
            @Param("customerName") String customerName,
            @Param("status") Integer status,
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select("<script>" +
            "SELECT COUNT(*) FROM orders " +
            "<where>" +
            "<if test='orderNo != null and orderNo != \"\"'>AND id LIKE CONCAT('%', #{orderNo}, '%')</if>" +
            "<if test='customerName != null and customerName != \"\"'>AND customer_name LIKE CONCAT('%', #{customerName}, '%')</if>" +
            "<if test='status != null'>AND status = #{status}</if>" +
            "<if test='beginTime != null'>AND created_at &gt;= #{beginTime}</if>" +
            "<if test='endTime != null'>AND created_at &lt;= #{endTime}</if>" +
            "</where>" +
            "</script>")
    Long countByCondition(
            @Param("orderNo") String orderNo,
            @Param("customerName") String customerName,
            @Param("status") Integer status,
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Select("SELECT * FROM orders WHERE id = #{id}")
    OrderVO selectById(String id);

    @Insert("INSERT INTO orders (id, user_id, customer_name, customer_phone, customer_address, total_amount, status, created_at, updated_at) " +
            "VALUES (#{id}, #{userId}, #{customerName}, #{customerPhone}, #{customerAddress}, #{totalAmount}, #{status}, #{createdAt}, #{updatedAt})")
    void insert(Order order);

    @Update("UPDATE orders SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") String id, @Param("status") Integer status);

    @Update("UPDATE orders SET payment_time = #{paymentTime}, status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updatePaymentTime(@Param("id") String id, @Param("paymentTime") LocalDateTime paymentTime, @Param("status") Integer status);

    @Update("UPDATE orders SET delivery_time = #{deliveryTime}, updated_at = NOW() WHERE id = #{id}")
    void updateDeliveryTime(@Param("id") String id, @Param("deliveryTime") LocalDateTime deliveryTime);

    @Select("<script>" +
            "SELECT * FROM orders WHERE user_id = #{userId} " +
            "<if test='status != null'>AND status = #{status}</if> " +
            "ORDER BY created_at DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<OrderVO> selectByUserCondition(@Param("userId") Long userId, @Param("status") Integer status, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM orders WHERE user_id = #{userId} " +
            "<if test='status != null'>AND status = #{status}</if>" +
            "</script>")
    Long countByUserCondition(@Param("userId") Long userId, @Param("status") Integer status);
}

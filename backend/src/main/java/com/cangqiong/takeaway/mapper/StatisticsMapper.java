package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.vo.DishStatisticsVO;
import com.cangqiong.takeaway.vo.LabelValueVO;
import com.cangqiong.takeaway.vo.PopularDishVO;
import com.cangqiong.takeaway.vo.StatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StatisticsMapper {

    @Select("SELECT CAST(payment_time AS DATE) AS date, SUM(total_amount) AS amount " +
            "FROM orders " +
            "WHERE status IN (1, 2, 3) " +
            "AND payment_time IS NOT NULL " +
            "AND CAST(payment_time AS DATE) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY CAST(payment_time AS DATE) " +
            "ORDER BY date")
    List<StatisticsVO> selectTurnoverByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT CAST(created_at AS DATE) AS date, COUNT(*) AS orderCount " +
            "FROM orders " +
            "WHERE CAST(created_at AS DATE) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY CAST(created_at AS DATE) " +
            "ORDER BY date")
    List<StatisticsVO> selectOrderCountByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT d.id AS dishId, d.name AS name, SUM(oi.quantity) AS saleCount, SUM(oi.total) AS saleAmount " +
            "FROM order_item oi " +
            "INNER JOIN orders o ON oi.order_id = o.id " +
            "INNER JOIN dish d ON oi.dish_id = d.id " +
            "WHERE o.status IN (1, 2, 3) " +
            "AND o.payment_time IS NOT NULL " +
            "AND CAST(o.payment_time AS DATE) BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY d.id, d.name " +
            "ORDER BY saleCount DESC")
    List<DishStatisticsVO> selectDishSaleByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status IN (1, 2, 3)")
    BigDecimal selectTotalSales();

    @Select("SELECT COUNT(*) FROM orders")
    Long selectTotalOrders();

    @Select("SELECT COUNT(*) FROM user")
    Long selectTotalUsers();

    @Select("SELECT COUNT(*) FROM user WHERE created_at BETWEEN #{startTime} AND #{endTime}")
    Long selectNewUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COUNT(DISTINCT user_id) FROM orders WHERE created_at BETWEEN #{startTime} AND #{endTime}")
    Long selectActiveUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM (" +
            "SELECT user_id FROM orders WHERE created_at BETWEEN #{startTime} AND #{endTime} GROUP BY user_id HAVING COUNT(*) > 1" +
            ") t")
    Long selectRepurchaseUsers(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status IN (1, 2, 3) AND created_at BETWEEN #{startTime} AND #{endTime}")
    BigDecimal selectSalesAmountBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COUNT(*) FROM orders WHERE created_at BETWEEN #{startTime} AND #{endTime}")
    Long selectOrderCountBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT CAST(created_at AS DATE) AS date, COUNT(*) AS orderCount " +
            "FROM orders " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY CAST(created_at AS DATE) " +
            "ORDER BY date")
    List<StatisticsVO> selectOrderTrendBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT CAST(created_at AS DATE) AS date, COALESCE(SUM(CASE WHEN status IN (1, 2, 3) THEN total_amount ELSE 0 END), 0) AS amount " +
            "FROM orders " +
            "WHERE created_at BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY CAST(created_at AS DATE) " +
            "ORDER BY date")
    List<StatisticsVO> selectSalesTrendBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "value", column = "item_value")
    })
    @Select("SELECT CASE status " +
            "WHEN 0 THEN '待支付' " +
            "WHEN 1 THEN '处理中' " +
            "WHEN 2 THEN '已配送' " +
            "WHEN 3 THEN '已完成' " +
            "WHEN 4 THEN '已取消' " +
            "ELSE '未知状态' END AS name, COUNT(*) AS item_value " +
            "FROM orders GROUP BY status ORDER BY status")
    List<LabelValueVO> selectOrderStatusDistribution();

    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "value", column = "item_value")
    })
    @Select("SELECT CASE " +
            "WHEN customer_address LIKE '北京市%' THEN '北京' " +
            "WHEN customer_address LIKE '上海市%' THEN '上海' " +
            "WHEN customer_address LIKE '广州市%' THEN '广州' " +
            "WHEN customer_address LIKE '深圳市%' THEN '深圳' " +
            "WHEN customer_address LIKE '杭州市%' THEN '杭州' " +
            "WHEN customer_address LIKE '成都市%' THEN '成都' " +
            "WHEN customer_address LIKE '武汉市%' THEN '武汉' " +
            "ELSE '其他' END AS name, COUNT(DISTINCT user_id) AS item_value " +
            "FROM orders GROUP BY name ORDER BY item_value DESC")
    List<LabelValueVO> selectUserRegionDistribution();

    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "value", column = "item_value")
    })
    @Select("SELECT CASE " +
            "WHEN EXTRACT(HOUR FROM created_at) BETWEEN 0 AND 2 THEN '00:00' " +
            "WHEN EXTRACT(HOUR FROM created_at) BETWEEN 3 AND 5 THEN '03:00' " +
            "WHEN EXTRACT(HOUR FROM created_at) BETWEEN 6 AND 8 THEN '06:00' " +
            "WHEN EXTRACT(HOUR FROM created_at) BETWEEN 9 AND 11 THEN '09:00' " +
            "WHEN EXTRACT(HOUR FROM created_at) BETWEEN 12 AND 14 THEN '12:00' " +
            "WHEN EXTRACT(HOUR FROM created_at) BETWEEN 15 AND 17 THEN '15:00' " +
            "WHEN EXTRACT(HOUR FROM created_at) BETWEEN 18 AND 20 THEN '18:00' " +
            "ELSE '21:00' END AS name, COUNT(*) AS item_value " +
            "FROM orders GROUP BY name ORDER BY name")
    List<LabelValueVO> selectSalesTimeDistribution();

    @Select("SELECT oi.name AS name, SUM(oi.quantity) AS sales " +
            "FROM order_item oi " +
            "INNER JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status IN (1, 2, 3) " +
            "AND o.created_at BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY oi.name " +
            "ORDER BY sales DESC " +
            "LIMIT #{top}")
    List<PopularDishVO> selectPopularDishes(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("top") Integer top);
}

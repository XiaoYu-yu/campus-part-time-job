package com.cangqiong.takeaway.service;

import com.cangqiong.takeaway.vo.ChartSeriesVO;
import com.cangqiong.takeaway.vo.DashboardStatisticsVO;
import com.cangqiong.takeaway.vo.DishStatisticsVO;
import com.cangqiong.takeaway.vo.PopularDishVO;
import com.cangqiong.takeaway.vo.SalesTrendVO;
import com.cangqiong.takeaway.vo.StatisticsVO;
import com.cangqiong.takeaway.vo.UserStatisticsDetailVO;

import java.util.List;

/**
 * 数据统计服务接口
 * 定义营业额、订单数、菜品销量等统计相关的业务逻辑操作
 */
public interface StatisticsService {

    /**
     * 获取营业额统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 营业额统计列表
     */
    List<StatisticsVO> getTurnoverStatistics(String startDate, String endDate);

    /**
     * 获取订单数统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单数统计列表
     */
    List<StatisticsVO> getOrderStatistics(String startDate, String endDate);

    /**
     * 获取菜品销量统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 菜品销量统计列表
     */
    List<DishStatisticsVO> getDishSaleStatistics(String startDate, String endDate);

    DashboardStatisticsVO getDashboardStatistics();

    SalesTrendVO getSalesTrend(String beginDate, String endDate);

    List<PopularDishVO> getPopularDishes(String beginDate, String endDate, Integer top);

    UserStatisticsDetailVO getUserStatistics(String beginDate, String endDate);
}

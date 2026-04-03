package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.service.StatisticsService;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.DashboardStatisticsVO;
import com.cangqiong.takeaway.vo.DishStatisticsVO;
import com.cangqiong.takeaway.vo.PopularDishVO;
import com.cangqiong.takeaway.vo.SalesTrendVO;
import com.cangqiong.takeaway.vo.StatisticsVO;
import com.cangqiong.takeaway.vo.UserStatisticsDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据统计控制器
 * 处理营业额、订单数、菜品销量等统计相关的 HTTP 请求
 */
@Slf4j
@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取营业额统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 营业额统计列表
     */
    @GetMapping("/api/statistics/turnover")
    public Result<List<StatisticsVO>> getTurnoverStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("营业额统计查询，开始日期：{}，结束日期：{}", startDate, endDate);

        List<StatisticsVO> list = statisticsService.getTurnoverStatistics(startDate, endDate);

        return Result.success(list);
    }

    /**
     * 获取订单数统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单数统计列表
     */
    @GetMapping("/api/statistics/orders")
    public Result<List<StatisticsVO>> getOrderStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("订单数统计查询，开始日期：{}，结束日期：{}", startDate, endDate);

        List<StatisticsVO> list = statisticsService.getOrderStatistics(startDate, endDate);

        return Result.success(list);
    }

    /**
     * 获取菜品销量统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 菜品销量统计列表
     */
    @GetMapping("/api/statistics/dishes")
    public Result<List<DishStatisticsVO>> getDishSaleStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("菜品销量统计查询，开始日期：{}，结束日期：{}", startDate, endDate);

        List<DishStatisticsVO> list = statisticsService.getDishSaleStatistics(startDate, endDate);

        return Result.success(list);
    }

    @GetMapping("/api/statistics/dashboard")
    public Result<DashboardStatisticsVO> getDashboardStatistics() {
        log.info("获取仪表盘统计数据");
        return Result.success(statisticsService.getDashboardStatistics());
    }

    @GetMapping("/api/statistics/sales")
    public Result<SalesTrendVO> getSalesTrend(
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        log.info("获取销售趋势，开始日期：{}，结束日期：{}", beginDate, endDate);
        return Result.success(statisticsService.getSalesTrend(beginDate, endDate));
    }

    @GetMapping("/api/statistics/popular-dishes")
    public Result<List<PopularDishVO>> getPopularDishes(
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer top) {
        log.info("获取热门菜品，开始日期：{}，结束日期：{}，top={}", beginDate, endDate, top);
        return Result.success(statisticsService.getPopularDishes(beginDate, endDate, top));
    }

    @GetMapping("/api/statistics/users")
    public Result<UserStatisticsDetailVO> getUserStatistics(
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        log.info("获取用户统计，开始日期：{}，结束日期：{}", beginDate, endDate);
        return Result.success(statisticsService.getUserStatistics(beginDate, endDate));
    }
}

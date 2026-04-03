package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.mapper.StatisticsMapper;
import com.cangqiong.takeaway.service.StatisticsService;
import com.cangqiong.takeaway.vo.ChartSeriesVO;
import com.cangqiong.takeaway.vo.DashboardStatisticsVO;
import com.cangqiong.takeaway.vo.DishStatisticsVO;
import com.cangqiong.takeaway.vo.LabelValueVO;
import com.cangqiong.takeaway.vo.PopularDishVO;
import com.cangqiong.takeaway.vo.SalesTrendVO;
import com.cangqiong.takeaway.vo.StatisticsVO;
import com.cangqiong.takeaway.vo.TimeSeriesVO;
import com.cangqiong.takeaway.vo.UserStatisticsDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public List<StatisticsVO> getTurnoverStatistics(String startDate, String endDate) {
        log.info("获取营业额统计，开始日期：{}，结束日期：{}", startDate, endDate);
        return statisticsMapper.selectTurnoverByDateRange(startDate, endDate);
    }

    @Override
    public List<StatisticsVO> getOrderStatistics(String startDate, String endDate) {
        log.info("获取订单数统计，开始日期：{}，结束日期：{}", startDate, endDate);
        return statisticsMapper.selectOrderCountByDateRange(startDate, endDate);
    }

    @Override
    public List<DishStatisticsVO> getDishSaleStatistics(String startDate, String endDate) {
        log.info("获取菜品销量统计，开始日期：{}，结束日期：{}", startDate, endDate);
        return statisticsMapper.selectDishSaleByDateRange(startDate, endDate);
    }

    @Override
    public DashboardStatisticsVO getDashboardStatistics() {
        LocalDate endDate = LocalDate.now();
        LocalDate beginDate = endDate.minusDays(6);
        LocalDate previousBegin = beginDate.minusDays(7);
        LocalDate previousEnd = beginDate.minusDays(1);

        LocalDateTime currentStart = beginDate.atStartOfDay();
        LocalDateTime currentEnd = endDate.atTime(LocalTime.MAX);
        LocalDateTime previousStart = previousBegin.atStartOfDay();
        LocalDateTime previousEndTime = previousEnd.atTime(LocalTime.MAX);

        BigDecimal totalSales = defaultDecimal(statisticsMapper.selectTotalSales());
        Long totalOrders = defaultLong(statisticsMapper.selectTotalOrders());
        Long totalUsers = defaultLong(statisticsMapper.selectTotalUsers());
        Long newUsers = defaultLong(statisticsMapper.selectNewUsers(currentStart, currentEnd));

        BigDecimal currentSales = defaultDecimal(statisticsMapper.selectSalesAmountBetween(currentStart, currentEnd));
        BigDecimal previousSales = defaultDecimal(statisticsMapper.selectSalesAmountBetween(previousStart, previousEndTime));
        Long currentOrders = defaultLong(statisticsMapper.selectOrderCountBetween(currentStart, currentEnd));
        Long previousOrders = defaultLong(statisticsMapper.selectOrderCountBetween(previousStart, previousEndTime));
        Long currentNewUsers = newUsers;
        Long previousNewUsers = defaultLong(statisticsMapper.selectNewUsers(previousStart, previousEndTime));
        BigDecimal currentAvg = calculateAverage(currentSales, currentOrders);
        BigDecimal previousAvg = calculateAverage(previousSales, previousOrders);
        Long activeUsers = defaultLong(statisticsMapper.selectActiveUsers(currentStart, currentEnd));
        Long repurchaseUsers = defaultLong(statisticsMapper.selectRepurchaseUsers(currentStart, currentEnd));

        DashboardStatisticsVO vo = new DashboardStatisticsVO();
        vo.setTotalSales(formatCurrency(totalSales));
        vo.setTotalOrders(totalOrders);
        vo.setTotalUsers(totalUsers);
        vo.setNewUsers(newUsers);
        vo.setAvgOrderValue(formatCurrency(calculateAverage(totalSales, totalOrders)));
        vo.setSalesChange(formatChange(currentSales, previousSales));
        vo.setSalesChangeType(changeType(currentSales, previousSales));
        vo.setOrdersChange(formatChange(BigDecimal.valueOf(currentOrders), BigDecimal.valueOf(previousOrders)));
        vo.setOrdersChangeType(changeType(BigDecimal.valueOf(currentOrders), BigDecimal.valueOf(previousOrders)));
        vo.setUsersChange(formatChange(BigDecimal.valueOf(currentNewUsers), BigDecimal.valueOf(previousNewUsers)));
        vo.setUsersChangeType(changeType(BigDecimal.valueOf(currentNewUsers), BigDecimal.valueOf(previousNewUsers)));
        vo.setAvgChange(formatChange(currentAvg, previousAvg));
        vo.setAvgChangeType(changeType(currentAvg, previousAvg));

        BigDecimal conversionRate = calculateRate(activeUsers, totalUsers);
        BigDecimal repurchaseRate = calculateRate(repurchaseUsers, activeUsers);
        vo.setConversionRate(formatPercent(conversionRate));
        vo.setConversionChange("+0.00%");
        vo.setConversionChangeType("up");
        vo.setRepurchaseRate(formatPercent(repurchaseRate));
        vo.setRepurchaseChange("+0.00%");
        vo.setRepurchaseChangeType("up");

        vo.setOrderTrend(buildSalesTrend(currentStart, currentEnd));
        vo.setPopularDishes(getPopularDishes(beginDate.format(DATE_FORMATTER), endDate.format(DATE_FORMATTER), 8));
        vo.setOrderStatus(toChartSeries(statisticsMapper.selectOrderStatusDistribution()));
        vo.setUserRegion(toChartSeries(statisticsMapper.selectUserRegionDistribution()));
        vo.setSalesTime(buildSalesTimeSeries(statisticsMapper.selectSalesTimeDistribution()));

        return vo;
    }

    @Override
    public SalesTrendVO getSalesTrend(String beginDate, String endDate) {
        LocalDate end = endDate == null || endDate.isBlank() ? LocalDate.now() : LocalDate.parse(endDate);
        LocalDate begin = beginDate == null || beginDate.isBlank() ? end.minusDays(6) : LocalDate.parse(beginDate);
        return buildSalesTrend(begin.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    @Override
    public List<PopularDishVO> getPopularDishes(String beginDate, String endDate, Integer top) {
        LocalDate end = endDate == null || endDate.isBlank() ? LocalDate.now() : LocalDate.parse(endDate);
        LocalDate begin = beginDate == null || beginDate.isBlank() ? end.minusDays(6) : LocalDate.parse(beginDate);
        Integer safeTop = top == null || top < 1 ? 10 : top;
        return statisticsMapper.selectPopularDishes(begin.atStartOfDay(), end.atTime(LocalTime.MAX), safeTop);
    }

    @Override
    public UserStatisticsDetailVO getUserStatistics(String beginDate, String endDate) {
        LocalDate end = endDate == null || endDate.isBlank() ? LocalDate.now() : LocalDate.parse(endDate);
        LocalDate begin = beginDate == null || beginDate.isBlank() ? end.minusDays(6) : LocalDate.parse(beginDate);
        LocalDateTime start = begin.atStartOfDay();
        LocalDateTime finish = end.atTime(LocalTime.MAX);

        Long totalUsers = defaultLong(statisticsMapper.selectTotalUsers());
        Long newUsers = defaultLong(statisticsMapper.selectNewUsers(start, finish));
        Long activeUsers = defaultLong(statisticsMapper.selectActiveUsers(start, finish));
        Long repurchaseUsers = defaultLong(statisticsMapper.selectRepurchaseUsers(start, finish));
        List<LabelValueVO> regions = statisticsMapper.selectUserRegionDistribution();

        UserStatisticsDetailVO vo = new UserStatisticsDetailVO();
        vo.setTotalUsers(totalUsers);
        vo.setNewUsers(newUsers);
        vo.setActiveUsers(activeUsers);
        vo.setConversionRate(formatPercent(calculateRate(activeUsers, totalUsers)));
        vo.setRepurchaseRate(formatPercent(calculateRate(repurchaseUsers, activeUsers)));
        vo.setRegions(regions.stream().map(LabelValueVO::getName).toList());
        vo.setRegionValues(regions.stream().map(item -> defaultLong(item.getValue())).toList());
        return vo;
    }

    private SalesTrendVO buildSalesTrend(LocalDateTime start, LocalDateTime end) {
        List<StatisticsVO> salesRows = statisticsMapper.selectSalesTrendBetween(start, end);
        List<StatisticsVO> orderRows = statisticsMapper.selectOrderTrendBetween(start, end);
        Map<String, BigDecimal> salesMap = salesRows.stream()
                .collect(Collectors.toMap(row -> row.getDate().format(DATE_FORMATTER), row -> defaultDecimal(row.getAmount()), (left, right) -> right, LinkedHashMap::new));
        Map<String, Long> orderMap = orderRows.stream()
                .collect(Collectors.toMap(row -> row.getDate().format(DATE_FORMATTER), row -> defaultLong(row.getOrderCount()), (left, right) -> right, LinkedHashMap::new));

        List<String> dates = new ArrayList<>();
        List<BigDecimal> sales = new ArrayList<>();
        List<Long> orders = new ArrayList<>();
        LocalDate current = start.toLocalDate();
        LocalDate last = end.toLocalDate();
        while (!current.isAfter(last)) {
            String date = current.format(DATE_FORMATTER);
            dates.add(date);
            sales.add(salesMap.getOrDefault(date, BigDecimal.ZERO));
            orders.add(orderMap.getOrDefault(date, 0L));
            current = current.plusDays(1);
        }

        SalesTrendVO trendVO = new SalesTrendVO();
        trendVO.setDates(dates);
        trendVO.setSales(sales);
        trendVO.setOrders(orders);
        return trendVO;
    }

    private ChartSeriesVO toChartSeries(List<LabelValueVO> list) {
        ChartSeriesVO vo = new ChartSeriesVO();
        vo.setNames(list.stream().map(LabelValueVO::getName).toList());
        vo.setValues(list.stream().map(item -> defaultLong(item.getValue())).toList());
        return vo;
    }

    private TimeSeriesVO buildSalesTimeSeries(List<LabelValueVO> source) {
        List<String> buckets = List.of("00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00");
        Map<String, Long> raw = source.stream()
                .sorted(Comparator.comparing(LabelValueVO::getName))
                .collect(Collectors.toMap(LabelValueVO::getName, item -> defaultLong(item.getValue()), (left, right) -> right, LinkedHashMap::new));
        long total = raw.values().stream().mapToLong(Long::longValue).sum();
        List<BigDecimal> values = new ArrayList<>();
        for (String bucket : buckets) {
            long count = raw.getOrDefault(bucket, 0L);
            BigDecimal percent = total == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP);
            values.add(percent);
        }

        TimeSeriesVO vo = new TimeSeriesVO();
        vo.setHours(buckets);
        vo.setValues(values);
        return vo;
    }

    private BigDecimal calculateAverage(BigDecimal totalAmount, Long totalCount) {
        if (totalCount == null || totalCount == 0) {
            return BigDecimal.ZERO;
        }
        return defaultDecimal(totalAmount).divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRate(Long numerator, Long denominator) {
        if (denominator == null || denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(defaultLong(numerator))
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

    private String formatCurrency(BigDecimal amount) {
        return "¥" + defaultDecimal(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private String formatPercent(BigDecimal amount) {
        return defaultDecimal(amount).setScale(2, RoundingMode.HALF_UP) + "%";
    }

    private String formatChange(BigDecimal currentValue, BigDecimal previousValue) {
        if (previousValue == null || previousValue.compareTo(BigDecimal.ZERO) == 0) {
            return currentValue.compareTo(BigDecimal.ZERO) > 0 ? "+100.00%" : "+0.00%";
        }
        BigDecimal change = currentValue.subtract(previousValue)
                .multiply(BigDecimal.valueOf(100))
                .divide(previousValue, 2, RoundingMode.HALF_UP);
        String prefix = change.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return prefix + change + "%";
    }

    private String changeType(BigDecimal currentValue, BigDecimal previousValue) {
        return currentValue.compareTo(previousValue) >= 0 ? "up" : "down";
    }

    private BigDecimal defaultDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Long defaultLong(Number value) {
        return value == null ? 0L : value.longValue();
    }
}

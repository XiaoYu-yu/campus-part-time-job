package com.cangqiong.takeaway;

import com.cangqiong.takeaway.controller.StatisticsController;
import com.cangqiong.takeaway.service.StatisticsService;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.ChartSeriesVO;
import com.cangqiong.takeaway.vo.DashboardStatisticsVO;
import com.cangqiong.takeaway.vo.SalesTrendVO;
import com.cangqiong.takeaway.vo.TimeSeriesVO;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatisticsControllerTest {

    @Test
    void dashboardEndpointShouldReturnAggregatedPayload() {
        StatisticsService statisticsService = mock(StatisticsService.class);
        StatisticsController controller = new StatisticsController();
        ReflectionTestUtils.setField(controller, "statisticsService", statisticsService);

        DashboardStatisticsVO vo = new DashboardStatisticsVO();
        vo.setTotalSales("¥100.00");
        vo.setTotalOrders(2L);
        vo.setTotalUsers(1L);
        vo.setAvgOrderValue("¥50.00");

        SalesTrendVO trendVO = new SalesTrendVO();
        trendVO.setDates(List.of("2026-04-01"));
        trendVO.setSales(List.of(new BigDecimal("100.00")));
        trendVO.setOrders(List.of(2L));
        vo.setOrderTrend(trendVO);

        ChartSeriesVO chartSeriesVO = new ChartSeriesVO();
        chartSeriesVO.setNames(List.of("已完成"));
        chartSeriesVO.setValues(List.of(2L));
        vo.setOrderStatus(chartSeriesVO);
        vo.setUserRegion(chartSeriesVO);

        TimeSeriesVO timeSeriesVO = new TimeSeriesVO();
        timeSeriesVO.setHours(List.of("12:00"));
        timeSeriesVO.setValues(List.of(new BigDecimal("100.00")));
        vo.setSalesTime(timeSeriesVO);

        when(statisticsService.getDashboardStatistics()).thenReturn(vo);

        Result<DashboardStatisticsVO> result = controller.getDashboardStatistics();

        assertEquals(Result.SUCCESS_CODE, result.getCode());
        assertNotNull(result.getData());
        assertEquals("¥100.00", result.getData().getTotalSales());
        assertEquals(2L, result.getData().getTotalOrders());
        verify(statisticsService).getDashboardStatistics();
    }
}

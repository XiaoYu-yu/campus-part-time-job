package com.cangqiong.takeaway;

import com.cangqiong.takeaway.controller.OrderController;
import com.cangqiong.takeaway.query.OrderQuery;
import com.cangqiong.takeaway.service.OrderService;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.OrderVO;
import com.cangqiong.takeaway.vo.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderControllerTest {

    @Test
    void pageQueryShouldReturnUnifiedContractResult() {
        OrderService orderService = mock(OrderService.class);
        OrderController controller = new OrderController();
        ReflectionTestUtils.setField(controller, "orderService", orderService);

        PageResult<OrderVO> pageResult = new PageResult<>();
        pageResult.setRecords(Collections.emptyList());
        pageResult.setTotal(0L);
        pageResult.setCurrent(1L);
        pageResult.setSize(10L);
        pageResult.setPages(0L);

        when(orderService.pageQuery(any(OrderQuery.class))).thenReturn(pageResult);

        OrderQuery query = new OrderQuery();
        query.setOrderNo("20260403");
        query.setCustomerName("张三");
        query.setStatus(1);
        query.setBeginTime(java.time.LocalDateTime.parse("2026-04-01T00:00:00"));
        query.setEndTime(java.time.LocalDateTime.parse("2026-04-03T23:59:59"));
        query.setPage(1);
        query.setPageSize(10);

        Result<PageResult<OrderVO>> result = controller.pageQuery(query);

        assertEquals(Result.SUCCESS_CODE, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getCurrent());
        assertEquals(10L, result.getData().getSize());
        verify(orderService).pageQuery(query);
    }
}

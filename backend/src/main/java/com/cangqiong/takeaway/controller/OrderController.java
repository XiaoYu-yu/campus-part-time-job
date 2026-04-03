package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.OrderDTO;
import com.cangqiong.takeaway.query.OrderQuery;
import com.cangqiong.takeaway.service.OrderService;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.OrderVO;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单管理控制器
 * 处理订单相关的 HTTP 请求，包括分页查询、创建、支付、取消等操作
 */
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 订单分页查询
     * @param query 查询参数（orderNo、customerName、status、beginTime、endTime、page、pageSize）
     * @return 订单分页列表
     */
    @GetMapping("/api/orders")
    public Result<PageResult<OrderVO>> pageQuery(OrderQuery query) {
        log.info("订单分页查询，参数：{}", query);

        PageResult<OrderVO> pageResult = orderService.pageQuery(query);

        return Result.success(pageResult);
    }

    /**
     * 根据 ID 获取订单详情
     * @param id 订单 ID
     * @return 订单详情
     */
    @GetMapping("/api/orders/{id}")
    public Result<OrderVO> getById(@PathVariable String id) {
        log.info("获取订单详情: {}", id);

        OrderVO orderVO = orderService.getById(id);

        return Result.success(orderVO);
    }

    /**
     * 创建订单
     * @param dto 订单信息
     * @return 创建的订单 ID
     */
    @PostMapping("/api/orders")
    public Result<String> create(@RequestBody OrderDTO dto) {
        log.info("创建订单");

        String orderId = orderService.create(dto);

        return Result.success(orderId);
    }

    /**
     * 更新订单状态
     * @param id 订单 ID
     * @param params 状态参数
     * @return 操作结果
     */
    @PutMapping("/api/orders/{id}/status")
    public Result<Void> updateStatus(@PathVariable String id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        log.info("更新订单状态: id={}, status={}", id, status);

        orderService.updateStatus(id, status);

        return Result.success();
    }

    /**
     * 支付订单
     * @param id 订单 ID
     * @return 操作结果
     */
    @PutMapping("/api/orders/{id}/pay")
    public Result<Void> pay(@PathVariable String id) {
        log.info("支付订单: {}", id);

        orderService.pay(id);

        return Result.success();
    }

    /**
     * 取消订单
     * @param id 订单 ID
     * @return 操作结果
     */
    @PutMapping("/api/orders/{id}/cancel")
    public Result<Void> cancel(@PathVariable String id) {
        log.info("取消订单: {}", id);

        orderService.cancel(id);

        return Result.success();
    }
}

package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusCourierDeliverDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierPickupDTO;
import com.cangqiong.takeaway.campus.query.CampusCourierAvailableOrderQuery;
import com.cangqiong.takeaway.campus.service.CampusRelayOrderService;
import com.cangqiong.takeaway.campus.vo.CampusCourierOrderVO;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/campus/courier/orders")
public class CampusCourierOrderController {

    @Autowired
    private CampusRelayOrderService campusRelayOrderService;

    @GetMapping("/available")
    public Result<PageResult<CampusCourierOrderVO>> pageAvailable(CampusCourierAvailableOrderQuery query) {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员分页查询可接订单: userId={}, query={}", courierUserId, query);
        return Result.success(campusRelayOrderService.pageAvailableForCourier(query, courierUserId));
    }

    @GetMapping("/{id}")
    public Result<CampusCourierOrderVO> getById(@PathVariable String id) {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员查看订单详情: userId={}, orderId={}", courierUserId, id);
        return Result.success(campusRelayOrderService.getCourierOrderById(id, courierUserId));
    }

    @PostMapping("/{id}/accept")
    public Result<Void> accept(@PathVariable String id) {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员接单: userId={}, orderId={}", courierUserId, id);
        campusRelayOrderService.acceptByCourier(id, courierUserId);
        return Result.success();
    }

    @PostMapping("/{id}/pickup")
    public Result<Void> pickup(@PathVariable String id, @RequestBody CampusCourierPickupDTO dto) {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员取餐: userId={}, orderId={}", courierUserId, id);
        campusRelayOrderService.pickupByCourier(id, dto, courierUserId);
        return Result.success();
    }

    @PostMapping("/{id}/deliver")
    public Result<Void> deliver(@PathVariable String id, @RequestBody(required = false) CampusCourierDeliverDTO dto) {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员推进配送状态: userId={}, orderId={}", courierUserId, id);
        campusRelayOrderService.deliverByCourier(id, dto, courierUserId);
        return Result.success();
    }
}

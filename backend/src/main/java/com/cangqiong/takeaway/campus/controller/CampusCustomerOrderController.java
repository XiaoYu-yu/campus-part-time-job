package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderCreateDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderAfterSaleDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderCancelDTO;
import com.cangqiong.takeaway.campus.query.CampusCustomerOrderQuery;
import com.cangqiong.takeaway.campus.service.CampusRelayOrderService;
import com.cangqiong.takeaway.campus.vo.CampusCustomerOrderVO;
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
@RequestMapping("/api/campus/customer/orders")
public class CampusCustomerOrderController {

    @Autowired
    private CampusRelayOrderService campusRelayOrderService;

    @PostMapping
    public Result<String> create(@RequestBody CampusCustomerOrderCreateDTO dto) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("校园用户创建代送单: userId={}", customerUserId);
        return Result.success(campusRelayOrderService.createByCustomer(dto, customerUserId));
    }

    @PostMapping("/{id}/mock-pay")
    public Result<Void> mockPay(@PathVariable String id) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("校园用户模拟支付代送单: userId={}, orderId={}", customerUserId, id);
        campusRelayOrderService.mockPayByCustomer(id, customerUserId);
        return Result.success();
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable String id) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("校园用户确认送达: userId={}, orderId={}", customerUserId, id);
        campusRelayOrderService.confirmByCustomer(id, customerUserId);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable String id, @RequestBody CampusCustomerOrderCancelDTO dto) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("校园用户取消代送单: userId={}, orderId={}", customerUserId, id);
        campusRelayOrderService.cancelByCustomer(id, dto, customerUserId);
        return Result.success();
    }

    @PostMapping("/{id}/after-sale")
    public Result<Void> openAfterSale(@PathVariable String id, @RequestBody CampusCustomerOrderAfterSaleDTO dto) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("校园用户发起售后: userId={}, orderId={}", customerUserId, id);
        campusRelayOrderService.openAfterSaleByCustomer(id, dto, customerUserId);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CampusCustomerOrderVO> getById(@PathVariable String id) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("校园用户查看代送单详情: userId={}, orderId={}", customerUserId, id);
        return Result.success(campusRelayOrderService.getCustomerOrderById(id, customerUserId));
    }

    @GetMapping
    public Result<PageResult<CampusCustomerOrderVO>> pageQuery(CampusCustomerOrderQuery query) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("校园用户分页查询代送单: userId={}, query={}", customerUserId, query);
        return Result.success(campusRelayOrderService.pageQueryByCustomer(query, customerUserId));
    }
}

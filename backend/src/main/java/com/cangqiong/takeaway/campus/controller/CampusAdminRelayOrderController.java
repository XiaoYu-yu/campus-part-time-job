package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.query.CampusRelayOrderQuery;
import com.cangqiong.takeaway.campus.service.CampusRelayOrderService;
import com.cangqiong.takeaway.campus.vo.CampusOrderTimelineVO;
import com.cangqiong.takeaway.campus.vo.CampusRelayOrderVO;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/campus/admin/orders")
public class CampusAdminRelayOrderController {

    @Autowired
    private CampusRelayOrderService campusRelayOrderService;

    @GetMapping
    public Result<PageResult<CampusRelayOrderVO>> pageQuery(CampusRelayOrderQuery query) {
        log.info("校园代送订单分页查询: {}", query);
        return Result.success(campusRelayOrderService.pageQuery(query));
    }

    @GetMapping("/{id}")
    public Result<CampusRelayOrderVO> getById(@PathVariable String id) {
        log.info("校园代送订单详情查询: {}", id);
        return Result.success(campusRelayOrderService.getById(id));
    }

    @GetMapping("/{id}/timeline")
    public Result<CampusOrderTimelineVO> getTimeline(@PathVariable String id) {
        log.info("校园代送订单时间线查询: {}", id);
        return Result.success(campusRelayOrderService.getTimelineByAdmin(id));
    }
}

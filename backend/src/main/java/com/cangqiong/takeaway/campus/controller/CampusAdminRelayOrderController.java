package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleDecisionDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleExecutionDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleHandleDTO;
import com.cangqiong.takeaway.campus.query.CampusAdminOrderLocationReportQuery;
import com.cangqiong.takeaway.campus.query.CampusAdminAfterSaleOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusRelayOrderQuery;
import com.cangqiong.takeaway.campus.service.CampusLocationReportService;
import com.cangqiong.takeaway.campus.service.CampusRelayOrderService;
import com.cangqiong.takeaway.campus.vo.CampusAdminAfterSaleOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusAdminAfterSaleResultVO;
import com.cangqiong.takeaway.campus.vo.CampusLocationReportVO;
import com.cangqiong.takeaway.campus.vo.CampusOrderExceptionSummaryVO;
import com.cangqiong.takeaway.campus.vo.CampusOrderTimelineVO;
import com.cangqiong.takeaway.campus.vo.CampusRelayOrderVO;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.PageResult;
import jakarta.validation.Valid;
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
@RequestMapping("/api/campus/admin/orders")
public class CampusAdminRelayOrderController {

    @Autowired
    private CampusRelayOrderService campusRelayOrderService;

    @Autowired
    private CampusLocationReportService campusLocationReportService;

    @GetMapping
    public Result<PageResult<CampusRelayOrderVO>> pageQuery(CampusRelayOrderQuery query) {
        log.info("校园代送订单分页查询: {}", query);
        return Result.success(campusRelayOrderService.pageQuery(query));
    }

    @GetMapping("/after-sale")
    public Result<PageResult<CampusAdminAfterSaleOrderVO>> pageAfterSale(CampusAdminAfterSaleOrderQuery query) {
        log.info("校园代送订单售后分页查询: {}", query);
        return Result.success(campusRelayOrderService.pageAfterSaleByAdmin(query));
    }

    @GetMapping("/{id}/after-sale-result")
    public Result<CampusAdminAfterSaleResultVO> getAfterSaleResult(@PathVariable String id) {
        log.info("校园代送订单售后结果汇总查询: {}", id);
        return Result.success(campusRelayOrderService.getAfterSaleResultByAdmin(id));
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

    @GetMapping("/{id}/location-reports")
    public Result<PageResult<CampusLocationReportVO>> pageLocationReports(
            @PathVariable String id,
            CampusAdminOrderLocationReportQuery query
    ) {
        log.info("管理员按订单查询校园位置记录: orderId={}, query={}", id, query);
        return Result.success(campusLocationReportService.pageByOrderForAdmin(id, query));
    }

    @GetMapping("/{id}/exception-summary")
    public Result<CampusOrderExceptionSummaryVO> getExceptionSummary(@PathVariable String id) {
        log.info("管理员按订单查询校园异常摘要: {}", id);
        return Result.success(campusRelayOrderService.getExceptionSummaryByOrderIdForAdmin(id));
    }

    @PostMapping("/{id}/after-sale-handle")
    public Result<Void> handleAfterSale(@PathVariable String id, @Valid @RequestBody CampusAdminAfterSaleHandleDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送订单售后处理: employeeId={}, orderId={}", employeeId, id);
        campusRelayOrderService.handleAfterSaleByAdmin(id, dto, employeeId);
        return Result.success();
    }

    @PostMapping("/{id}/after-sale-decision")
    public Result<Void> recordAfterSaleDecision(@PathVariable String id, @Valid @RequestBody CampusAdminAfterSaleDecisionDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送订单售后决策记录: employeeId={}, orderId={}", employeeId, id);
        campusRelayOrderService.recordAfterSaleDecisionByAdmin(id, dto, employeeId);
        return Result.success();
    }

    @PostMapping("/{id}/after-sale-execution")
    public Result<Void> recordAfterSaleExecution(@PathVariable String id, @Valid @RequestBody CampusAdminAfterSaleExecutionDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送订单售后执行结果记录: employeeId={}, orderId={}", employeeId, id);
        campusRelayOrderService.recordAfterSaleExecutionByAdmin(id, dto, employeeId);
        return Result.success();
    }
}

package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementConfirmDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementBatchPayoutDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementPayoutResultDTO;
import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementBatchPayoutResultVO;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileSummaryVO;
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
@RequestMapping("/api/campus/admin/settlements")
public class CampusAdminSettlementController {

    @Autowired
    private CampusSettlementRecordService campusSettlementRecordService;

    @GetMapping
    public Result<PageResult<CampusSettlementRecordVO>> pageQuery(CampusSettlementQuery query) {
        log.info("校园代送结算分页查询: {}", query);
        return Result.success(campusSettlementRecordService.pageQuery(query));
    }

    @GetMapping("/{id}")
    public Result<CampusSettlementRecordVO> getById(@PathVariable Long id) {
        log.info("校园代送结算详情查询: {}", id);
        return Result.success(campusSettlementRecordService.getById(id));
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id, @Valid @RequestBody CampusAdminSettlementConfirmDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送结算确认: employeeId={}, settlementId={}", employeeId, id);
        campusSettlementRecordService.confirmByAdmin(id, dto, employeeId);
        return Result.success();
    }

    @PostMapping("/{id}/payout-result")
    public Result<Void> recordPayoutResult(@PathVariable Long id, @Valid @RequestBody CampusAdminSettlementPayoutResultDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送结算打款结果记录: employeeId={}, settlementId={}", employeeId, id);
        campusSettlementRecordService.recordPayoutResultByAdmin(id, dto, employeeId);
        return Result.success();
    }

    @PostMapping("/batch-payout-result")
    public Result<CampusSettlementBatchPayoutResultVO> batchRecordPayoutResult(@Valid @RequestBody CampusAdminSettlementBatchPayoutDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送结算批量打款结果记录: employeeId={}, dto={}", employeeId, dto);
        return Result.success(campusSettlementRecordService.batchRecordPayoutResultByAdmin(dto, employeeId));
    }

    @GetMapping("/reconcile-summary")
    public Result<CampusSettlementReconcileSummaryVO> getReconcileSummary(CampusSettlementQuery query) {
        log.info("校园代送结算对账摘要查询: {}", query);
        return Result.success(campusSettlementRecordService.getReconcileSummary(query));
    }
}

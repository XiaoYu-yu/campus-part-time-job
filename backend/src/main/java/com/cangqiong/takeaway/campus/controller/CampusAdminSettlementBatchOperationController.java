package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementBatchOperationDTO;
import com.cangqiong.takeaway.campus.query.CampusSettlementBatchOperationQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementBatchOperationRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementBatchOperationRecordVO;
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
@RequestMapping("/api/campus/admin/settlements/payout-batches/{batchNo}")
public class CampusAdminSettlementBatchOperationController {

    @Autowired
    private CampusSettlementBatchOperationRecordService batchOperationRecordService;

    @GetMapping("/operations")
    public Result<PageResult<CampusSettlementBatchOperationRecordVO>> pageOperations(
            @PathVariable String batchNo,
            CampusSettlementBatchOperationQuery query
    ) {
        log.info("校园代送结算批次操作审计查询: batchNo={}, query={}", batchNo, query);
        return Result.success(batchOperationRecordService.pageByBatchNo(batchNo, query));
    }

    @PostMapping("/review")
    public Result<Void> review(@PathVariable String batchNo, @Valid @RequestBody CampusAdminSettlementBatchOperationDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送结算批次复核审计写入: employeeId={}, batchNo={}", employeeId, batchNo);
        batchOperationRecordService.recordReview(batchNo, dto, employeeId);
        return Result.success();
    }

    @PostMapping("/withdraw")
    public Result<Void> withdraw(@PathVariable String batchNo, @Valid @RequestBody CampusAdminSettlementBatchOperationDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送结算批次撤回审计写入: employeeId={}, batchNo={}", employeeId, batchNo);
        batchOperationRecordService.recordWithdraw(batchNo, dto, employeeId);
        return Result.success();
    }
}

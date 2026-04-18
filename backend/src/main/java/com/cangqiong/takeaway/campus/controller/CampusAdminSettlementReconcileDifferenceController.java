package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementReconcileDifferenceCreateDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementReconcileDifferenceResolveDTO;
import com.cangqiong.takeaway.campus.query.CampusSettlementReconcileDifferenceQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementReconcileDifferenceRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileDifferenceRecordVO;
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
@RequestMapping("/api/campus/admin/settlements/reconcile-differences")
public class CampusAdminSettlementReconcileDifferenceController {

    @Autowired
    private CampusSettlementReconcileDifferenceRecordService reconcileDifferenceRecordService;

    @GetMapping
    public Result<PageResult<CampusSettlementReconcileDifferenceRecordVO>> pageQuery(CampusSettlementReconcileDifferenceQuery query) {
        log.info("校园代送结算对账差异分页查询: {}", query);
        return Result.success(reconcileDifferenceRecordService.pageQuery(query));
    }

    @GetMapping("/{id}")
    public Result<CampusSettlementReconcileDifferenceRecordVO> getById(@PathVariable Long id) {
        log.info("校园代送结算对账差异详情查询: {}", id);
        return Result.success(reconcileDifferenceRecordService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CampusAdminSettlementReconcileDifferenceCreateDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送结算对账差异登记: employeeId={}, settlementRecordId={}", employeeId, dto.getSettlementRecordId());
        reconcileDifferenceRecordService.createByAdmin(dto, employeeId);
        return Result.success();
    }

    @PostMapping("/{id}/resolve")
    public Result<Void> resolve(@PathVariable Long id, @Valid @RequestBody CampusAdminSettlementReconcileDifferenceResolveDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园代送结算对账差异处理关闭: employeeId={}, differenceId={}", employeeId, id);
        reconcileDifferenceRecordService.resolveByAdmin(id, dto, employeeId);
        return Result.success();
    }
}

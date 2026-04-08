package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementConfirmDTO;
import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
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
}

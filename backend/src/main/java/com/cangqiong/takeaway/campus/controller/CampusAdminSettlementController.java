package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}

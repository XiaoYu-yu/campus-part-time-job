package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.query.CampusAfterSaleExecutionRecordQuery;
import com.cangqiong.takeaway.campus.service.CampusAfterSaleExecutionRecordService;
import com.cangqiong.takeaway.campus.vo.CampusAfterSaleExecutionRecordVO;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/campus/admin/after-sale-execution-records")
public class CampusAdminAfterSaleExecutionRecordController {

    @Autowired
    private CampusAfterSaleExecutionRecordService campusAfterSaleExecutionRecordService;

    @GetMapping
    public Result<PageResult<CampusAfterSaleExecutionRecordVO>> pageQuery(CampusAfterSaleExecutionRecordQuery query) {
        log.info("校园代送售后执行历史分页查询: {}", query);
        return Result.success(campusAfterSaleExecutionRecordService.pageQuery(query));
    }
}

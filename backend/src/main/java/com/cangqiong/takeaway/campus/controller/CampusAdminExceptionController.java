package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.query.CampusExceptionRecordQuery;
import com.cangqiong.takeaway.campus.service.CampusExceptionRecordService;
import com.cangqiong.takeaway.campus.vo.CampusExceptionRecordVO;
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
@RequestMapping("/api/campus/admin/exceptions")
public class CampusAdminExceptionController {

    @Autowired
    private CampusExceptionRecordService campusExceptionRecordService;

    @GetMapping
    public Result<PageResult<CampusExceptionRecordVO>> pageQuery(CampusExceptionRecordQuery query) {
        log.info("校园代送异常历史分页查询: {}", query);
        return Result.success(campusExceptionRecordService.pageQuery(query));
    }

    @GetMapping("/{id}")
    public Result<CampusExceptionRecordVO> getById(@PathVariable Long id) {
        log.info("校园代送异常历史详情查询: {}", id);
        return Result.success(campusExceptionRecordService.getById(id));
    }
}

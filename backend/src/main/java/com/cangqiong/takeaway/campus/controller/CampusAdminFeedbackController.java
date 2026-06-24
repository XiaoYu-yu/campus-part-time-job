package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusAdminFeedbackProcessDTO;
import com.cangqiong.takeaway.campus.query.CampusFeedbackQuery;
import com.cangqiong.takeaway.campus.service.CampusFeedbackService;
import com.cangqiong.takeaway.campus.vo.CampusFeedbackVO;
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
@RequestMapping("/api/campus/admin/feedback")
public class CampusAdminFeedbackController {

    @Autowired
    private CampusFeedbackService campusFeedbackService;

    @GetMapping
    public Result<PageResult<CampusFeedbackVO>> pageQuery(CampusFeedbackQuery query) {
        log.info("校园平台反馈分页查询: {}", query);
        return Result.success(campusFeedbackService.pageQuery(query));
    }

    @GetMapping("/{id}")
    public Result<CampusFeedbackVO> getById(@PathVariable Long id) {
        log.info("校园平台反馈详情查询: id={}", id);
        return Result.success(campusFeedbackService.getById(id));
    }

    @PostMapping("/{id}/process")
    public Result<Void> process(@PathVariable Long id, @Valid @RequestBody CampusAdminFeedbackProcessDTO dto) {
        Long employeeId = BaseContext.getCurrentUserId();
        log.info("校园平台反馈处理: employeeId={}, feedbackId={}, status={}", employeeId, id, dto.getStatus());
        campusFeedbackService.processByAdmin(id, dto, employeeId);
        return Result.success();
    }
}

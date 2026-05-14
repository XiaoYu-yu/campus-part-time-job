package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusFeedbackSubmitDTO;
import com.cangqiong.takeaway.campus.service.CampusFeedbackService;
import com.cangqiong.takeaway.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/campus/public/feedback")
public class CampusFeedbackController {

    @Autowired
    private CampusFeedbackService campusFeedbackService;

    @PostMapping
    public Result<Long> submit(@RequestBody CampusFeedbackSubmitDTO dto) {
        log.info("提交校园平台反馈: role={}, category={}", dto == null ? null : dto.getSubmitterRole(), dto == null ? null : dto.getCategory());
        return Result.success(campusFeedbackService.submit(dto));
    }
}

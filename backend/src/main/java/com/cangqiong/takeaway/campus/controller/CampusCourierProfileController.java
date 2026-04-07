package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusCourierProfileSubmitDTO;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.vo.CampusCourierProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierReviewStatusVO;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/campus/courier")
public class CampusCourierProfileController {

    @Autowired
    private CampusCourierProfileService campusCourierProfileService;

    @PostMapping("/profile")
    public Result<CampusCourierProfileVO> submitProfile(@RequestBody CampusCourierProfileSubmitDTO dto) {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员提交资料: userId={}", courierUserId);
        return Result.success(campusCourierProfileService.submitProfile(dto, courierUserId));
    }

    @GetMapping("/profile")
    public Result<CampusCourierProfileVO> getCurrentProfile() {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员查看资料: userId={}", courierUserId);
        return Result.success(campusCourierProfileService.getCurrentProfile(courierUserId));
    }

    @GetMapping("/review-status")
    public Result<CampusCourierReviewStatusVO> getReviewStatus() {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员查看审核状态: userId={}", courierUserId);
        return Result.success(campusCourierProfileService.getCurrentReviewStatus(courierUserId));
    }
}

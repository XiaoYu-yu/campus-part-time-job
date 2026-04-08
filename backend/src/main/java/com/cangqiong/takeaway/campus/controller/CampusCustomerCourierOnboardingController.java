package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusCustomerCourierOnboardingSubmitDTO;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.vo.CampusCourierTokenEligibilityVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerCourierOnboardingProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerCourierOnboardingReviewStatusVO;
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
@RequestMapping("/api/campus/customer/courier-onboarding")
public class CampusCustomerCourierOnboardingController {

    @Autowired
    private CampusCourierProfileService campusCourierProfileService;

    @PostMapping("/profile")
    public Result<CampusCustomerCourierOnboardingProfileVO> submitProfile(@RequestBody CampusCustomerCourierOnboardingSubmitDTO dto) {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("customer 侧提交 courier onboarding 资料: userId={}", customerUserId);
        return Result.success(campusCourierProfileService.submitOnboardingProfile(dto, customerUserId));
    }

    @GetMapping("/profile")
    public Result<CampusCustomerCourierOnboardingProfileVO> getProfile() {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("customer 侧查看 courier onboarding 资料: userId={}", customerUserId);
        return Result.success(campusCourierProfileService.getCurrentOnboardingProfile(customerUserId));
    }

    @GetMapping("/review-status")
    public Result<CampusCustomerCourierOnboardingReviewStatusVO> getReviewStatus() {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("customer 侧查看 courier onboarding 审核状态: userId={}", customerUserId);
        return Result.success(campusCourierProfileService.getCurrentOnboardingReviewStatus(customerUserId));
    }

    @GetMapping("/token-eligibility")
    public Result<CampusCourierTokenEligibilityVO> getTokenEligibility() {
        Long customerUserId = BaseContext.getCurrentUserId();
        log.info("customer 侧查看 courier token 资格: userId={}", customerUserId);
        return Result.success(campusCourierProfileService.getTokenEligibility(customerUserId));
    }
}

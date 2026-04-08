package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusCourierProfileSubmitDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierReviewDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerCourierOnboardingSubmitDTO;
import com.cangqiong.takeaway.campus.entity.CampusCourierProfile;
import com.cangqiong.takeaway.campus.query.CampusCourierQuery;
import com.cangqiong.takeaway.campus.vo.CampusCourierProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierReviewStatusVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierTokenEligibilityVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerCourierOnboardingProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerCourierOnboardingReviewStatusVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusCourierProfileService {

    PageResult<CampusCourierProfileVO> pageQuery(CampusCourierQuery query);

    CampusCourierProfileVO submitProfile(CampusCourierProfileSubmitDTO dto, Long courierUserId);

    CampusCourierProfileVO getCurrentProfile(Long courierUserId);

    CampusCourierReviewStatusVO getCurrentReviewStatus(Long courierUserId);

    CampusCustomerCourierOnboardingProfileVO submitOnboardingProfile(CampusCustomerCourierOnboardingSubmitDTO dto, Long customerUserId);

    CampusCustomerCourierOnboardingProfileVO getCurrentOnboardingProfile(Long customerUserId);

    CampusCustomerCourierOnboardingReviewStatusVO getCurrentOnboardingReviewStatus(Long customerUserId);

    CampusCourierTokenEligibilityVO getTokenEligibility(Long customerUserId);

    void reviewByAdmin(Long id, CampusCourierReviewDTO dto, Long employeeId);

    CampusCourierProfile requireApprovedEnabledProfile(Long courierUserId);
}

package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusAdminFeedbackProcessDTO;
import com.cangqiong.takeaway.campus.dto.CampusFeedbackSubmitDTO;
import com.cangqiong.takeaway.campus.query.CampusFeedbackQuery;
import com.cangqiong.takeaway.campus.vo.CampusFeedbackVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusFeedbackService {

    Long submit(CampusFeedbackSubmitDTO dto);

    PageResult<CampusFeedbackVO> pageQuery(CampusFeedbackQuery query);

    CampusFeedbackVO getById(Long id);

    void processByAdmin(Long id, CampusAdminFeedbackProcessDTO dto, Long employeeId);
}

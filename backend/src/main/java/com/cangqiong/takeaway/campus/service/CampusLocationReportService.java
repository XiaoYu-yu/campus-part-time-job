package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.query.CampusAdminCourierLocationReportQuery;
import com.cangqiong.takeaway.campus.query.CampusAdminOrderLocationReportQuery;
import com.cangqiong.takeaway.campus.dto.CampusCourierLocationReportDTO;
import com.cangqiong.takeaway.campus.vo.CampusLocationReportVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusLocationReportService {

    void createByCourier(CampusCourierLocationReportDTO dto, Long courierUserId);

    PageResult<CampusLocationReportVO> pageByCourierForAdmin(Long courierProfileId, CampusAdminCourierLocationReportQuery query);

    PageResult<CampusLocationReportVO> pageByOrderForAdmin(String relayOrderId, CampusAdminOrderLocationReportQuery query);
}

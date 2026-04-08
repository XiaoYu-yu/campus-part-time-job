package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusCourierLocationReportDTO;

public interface CampusLocationReportService {

    void createByCourier(CampusCourierLocationReportDTO dto, Long courierUserId);
}

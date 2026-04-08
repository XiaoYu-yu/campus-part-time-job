package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.dto.CampusCourierLocationReportDTO;
import com.cangqiong.takeaway.campus.service.CampusLocationReportService;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.utils.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/campus/courier/location-reports")
public class CampusCourierLocationController {

    @Autowired
    private CampusLocationReportService campusLocationReportService;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CampusCourierLocationReportDTO dto) {
        Long courierUserId = BaseContext.getCurrentUserId();
        log.info("校园配送员位置上报: userId={}, relayOrderId={}", courierUserId, dto.getRelayOrderId());
        campusLocationReportService.createByCourier(dto, courierUserId);
        return Result.success();
    }
}

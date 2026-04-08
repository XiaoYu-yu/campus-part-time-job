package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusCourierLocationReportDTO;
import com.cangqiong.takeaway.campus.entity.CampusCourierProfile;
import com.cangqiong.takeaway.campus.entity.CampusLocationReport;
import com.cangqiong.takeaway.campus.entity.CampusRelayOrder;
import com.cangqiong.takeaway.campus.enums.CampusRelayOrderStatus;
import com.cangqiong.takeaway.campus.mapper.CampusLocationReportMapper;
import com.cangqiong.takeaway.campus.mapper.CampusRelayOrderMapper;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.service.CampusLocationReportService;
import com.cangqiong.takeaway.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CampusLocationReportServiceImpl implements CampusLocationReportService {

    @Autowired
    private CampusLocationReportMapper campusLocationReportMapper;

    @Autowired
    private CampusRelayOrderMapper campusRelayOrderMapper;

    @Autowired
    private CampusCourierProfileService campusCourierProfileService;

    @Override
    @Transactional
    public void createByCourier(CampusCourierLocationReportDTO dto, Long courierUserId) {
        CampusCourierProfile courierProfile = campusCourierProfileService.requireApprovedEnabledProfile(courierUserId);
        if (dto == null) {
            throw new BusinessException("位置上报请求不能为空");
        }

        String relayOrderId = normalizeText(dto.getRelayOrderId());
        String source = normalizeText(dto.getSource());
        requireText(relayOrderId, "订单号不能为空");
        requireText(source, "位置来源不能为空");
        assertCoordinate(dto.getLatitude(), "纬度");
        assertCoordinate(dto.getLongitude(), "经度");
        assertLatitude(dto.getLatitude());
        assertLongitude(dto.getLongitude());

        CampusRelayOrder order = campusRelayOrderMapper.selectEntityById(relayOrderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getCourierProfileId() == null || !order.getCourierProfileId().equals(courierProfile.getId())) {
            throw new BusinessException(403, "当前订单不属于你");
        }
        assertLocationReportAllowed(order);

        LocalDateTime now = LocalDateTime.now();
        CampusLocationReport report = new CampusLocationReport();
        report.setRelayOrderId(relayOrderId);
        report.setCourierProfileId(courierProfile.getId());
        report.setLatitude(dto.getLatitude());
        report.setLongitude(dto.getLongitude());
        report.setSource(source);
        report.setNote(normalizeText(dto.getNote()));
        report.setReportedAt(now);
        report.setCreatedAt(now);
        campusLocationReportMapper.insert(report);
    }

    private void assertLocationReportAllowed(CampusRelayOrder order) {
        if (!CampusRelayOrderStatus.ACCEPTED.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.PICKED_UP.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.DELIVERING.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.AWAITING_CONFIRMATION.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可上报位置");
        }
    }

    private void assertCoordinate(BigDecimal coordinate, String coordinateName) {
        if (coordinate == null) {
            throw new BusinessException(coordinateName + "不能为空");
        }
    }

    private void assertLatitude(BigDecimal latitude) {
        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new BusinessException("纬度超出有效范围");
        }
    }

    private void assertLongitude(BigDecimal longitude) {
        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new BusinessException("经度超出有效范围");
        }
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
    }
}

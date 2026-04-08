package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusCourierLocationReportDTO;
import com.cangqiong.takeaway.campus.entity.CampusCourierProfile;
import com.cangqiong.takeaway.campus.entity.CampusLocationReport;
import com.cangqiong.takeaway.campus.entity.CampusRelayOrder;
import com.cangqiong.takeaway.campus.enums.CampusRelayOrderStatus;
import com.cangqiong.takeaway.campus.mapper.CampusLocationReportMapper;
import com.cangqiong.takeaway.campus.mapper.CampusRelayOrderMapper;
import com.cangqiong.takeaway.campus.query.CampusAdminCourierLocationReportQuery;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.service.CampusLocationReportService;
import com.cangqiong.takeaway.campus.vo.CampusLocationReportVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public PageResult<CampusLocationReportVO> pageByCourierForAdmin(Long courierProfileId, CampusAdminCourierLocationReportQuery query) {
        if (courierProfileId == null) {
            throw new BusinessException("配送员资料不能为空");
        }

        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String relayOrderId = normalizeText(query == null ? null : query.getRelayOrderId());

        List<CampusLocationReportVO> records = campusLocationReportMapper.selectByCourierProfileId(
                courierProfileId,
                relayOrderId,
                offset,
                pageSize
        );
        Long total = campusLocationReportMapper.countByCourierProfileId(courierProfileId, relayOrderId);

        PageResult<CampusLocationReportVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize((long) pageSize);
        pageResult.setCurrent((long) page);
        pageResult.setPages((total + pageSize - 1) / pageSize);
        return pageResult;
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

    private int safePositive(Integer value, int defaultValue) {
        return value == null || value < 1 ? defaultValue : value;
    }

    private int safePageSize(Integer pageSize, Integer size) {
        int resolved = size != null ? size : (pageSize != null ? pageSize : 10);
        resolved = resolved < 1 ? 10 : resolved;
        return Math.min(resolved, 100);
    }

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
    }
}

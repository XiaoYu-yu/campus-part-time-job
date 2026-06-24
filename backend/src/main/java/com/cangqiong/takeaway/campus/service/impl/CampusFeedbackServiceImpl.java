package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusAdminFeedbackProcessDTO;
import com.cangqiong.takeaway.campus.dto.CampusFeedbackSubmitDTO;
import com.cangqiong.takeaway.campus.entity.CampusFeedback;
import com.cangqiong.takeaway.campus.mapper.CampusFeedbackMapper;
import com.cangqiong.takeaway.campus.query.CampusFeedbackQuery;
import com.cangqiong.takeaway.campus.service.CampusFeedbackService;
import com.cangqiong.takeaway.campus.vo.CampusFeedbackVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class CampusFeedbackServiceImpl implements CampusFeedbackService {

    private static final int DUPLICATE_WINDOW_SECONDS = 60;
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_RESOLVED = "RESOLVED";
    private static final Set<String> SUPPORTED_ROLES = Set.of("USER", "PARTTIME");
    private static final Set<String> SUPPORTED_CATEGORIES = Set.of("BUG", "SUGGESTION", "ORDER", "ACCOUNT", "OTHER");
    private static final Set<String> SUPPORTED_STATUSES = Set.of(STATUS_PENDING, STATUS_IN_PROGRESS, STATUS_RESOLVED);
    private static final Set<String> ADMIN_TARGET_STATUSES = Set.of(STATUS_IN_PROGRESS, STATUS_RESOLVED);

    @Autowired
    private CampusFeedbackMapper campusFeedbackMapper;

    @Override
    public Long submit(CampusFeedbackSubmitDTO dto) {
        if (dto == null) {
            throw new BusinessException("反馈内容不能为空");
        }

        String role = normalizeEnum(dto.getSubmitterRole(), "USER", SUPPORTED_ROLES, "反馈角色仅支持 USER 或 PARTTIME");
        String category = normalizeEnum(dto.getCategory(), "OTHER", SUPPORTED_CATEGORIES, "反馈类型不支持");
        String content = requireText(dto.getContent(), "反馈内容不能为空", 1000);
        if (content.length() < 5) {
            throw new BusinessException("反馈内容至少需要 5 个字符");
        }
        String contact = normalizeText(dto.getContact(), 100);
        String page = normalizeText(dto.getPage(), 120);
        String orderId = normalizeText(dto.getOrderId(), 32);

        LocalDateTime now = LocalDateTime.now();
        Long duplicateCount = campusFeedbackMapper.countRecentDuplicate(
                role,
                category,
                content,
                contact,
                page,
                orderId,
                now.minusSeconds(DUPLICATE_WINDOW_SECONDS)
        );
        if (duplicateCount != null && duplicateCount > 0) {
            throw new BusinessException(429, "相同反馈刚刚已经提交，请稍后再试");
        }

        CampusFeedback feedback = new CampusFeedback();
        feedback.setSubmitterRole(role);
        feedback.setCategory(category);
        feedback.setContent(content);
        feedback.setContact(contact);
        feedback.setPage(page);
        feedback.setOrderId(orderId);
        feedback.setStatus(STATUS_PENDING);
        feedback.setCreatedAt(now);
        feedback.setUpdatedAt(now);
        campusFeedbackMapper.insert(feedback);
        return feedback.getId();
    }

    @Override
    public PageResult<CampusFeedbackVO> pageQuery(CampusFeedbackQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String role = normalizeOptionalEnum(query == null ? null : query.getSubmitterRole(), SUPPORTED_ROLES, "反馈角色仅支持 USER 或 PARTTIME");
        String category = normalizeOptionalEnum(query == null ? null : query.getCategory(), SUPPORTED_CATEGORIES, "反馈类型不支持");
        String status = normalizeOptionalEnum(query == null ? null : query.getStatus(), SUPPORTED_STATUSES, "反馈状态不支持");
        String orderId = normalizeText(query == null ? null : query.getOrderId(), 32);

        List<CampusFeedbackVO> records = campusFeedbackMapper.selectByCondition(
                role,
                category,
                status,
                orderId,
                offset,
                pageSize
        );
        Long total = campusFeedbackMapper.countByCondition(role, category, status, orderId);
        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    public CampusFeedbackVO getById(Long id) {
        if (id == null) {
            throw new BusinessException("反馈ID不能为空");
        }
        CampusFeedbackVO feedback = campusFeedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(404, "反馈记录不存在");
        }
        return feedback;
    }

    @Override
    @Transactional
    public void processByAdmin(Long id, CampusAdminFeedbackProcessDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (id == null) {
            throw new BusinessException("反馈ID不能为空");
        }
        if (dto == null) {
            throw new BusinessException("反馈处理请求不能为空");
        }

        String targetStatus = normalizeEnum(dto.getStatus(), null, ADMIN_TARGET_STATUSES, "处理状态仅支持 IN_PROGRESS 或 RESOLVED");
        String adminNote = requireText(dto.getAdminNote(), "处理备注不能为空", 500);
        CampusFeedbackVO existing = getById(id);
        if (STATUS_RESOLVED.equals(existing.getStatus())) {
            throw new BusinessException("反馈已处理完成，不能重复操作");
        }
        if (targetStatus.equals(existing.getStatus())) {
            throw new BusinessException("反馈已处于目标状态");
        }

        LocalDateTime now = LocalDateTime.now();
        int updated = campusFeedbackMapper.updateProcessStatus(id, targetStatus, employeeId, now, adminNote, now);
        if (updated == 0) {
            CampusFeedbackVO latest = campusFeedbackMapper.selectById(id);
            if (latest == null) {
                throw new BusinessException(404, "反馈记录不存在");
            }
            throw new BusinessException("反馈状态已变化，请刷新后重试");
        }
    }

    private PageResult<CampusFeedbackVO> buildPageResult(List<CampusFeedbackVO> records, Long total, int page, int pageSize) {
        long resolvedTotal = total == null ? 0L : total;
        PageResult<CampusFeedbackVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(resolvedTotal);
        pageResult.setSize((long) pageSize);
        pageResult.setCurrent((long) page);
        pageResult.setPages((resolvedTotal + pageSize - 1) / pageSize);
        return pageResult;
    }

    private int safePositive(Integer value, int defaultValue) {
        return value == null || value < 1 ? defaultValue : value;
    }

    private int safePageSize(Integer pageSize, Integer size) {
        int resolved = size != null ? size : (pageSize != null ? pageSize : 10);
        resolved = resolved < 1 ? 10 : resolved;
        return Math.min(resolved, 100);
    }

    private String normalizeEnum(String value, String defaultValue, Set<String> allowedValues, String errorMessage) {
        String normalized = StringUtils.hasText(value) ? value.trim().toUpperCase() : defaultValue;
        if (normalized == null || !allowedValues.contains(normalized)) {
            throw new BusinessException(errorMessage);
        }
        return normalized;
    }

    private String normalizeOptionalEnum(String value, Set<String> allowedValues, String errorMessage) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return normalizeEnum(value, null, allowedValues, errorMessage);
    }

    private String requireText(String value, String errorMessage, int maxLength) {
        String normalized = normalizeText(value, maxLength);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException(errorMessage);
        }
        return normalized;
    }

    private String normalizeText(String value, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new BusinessException("输入内容过长，最多允许 " + maxLength + " 个字符");
        }
        return normalized;
    }
}

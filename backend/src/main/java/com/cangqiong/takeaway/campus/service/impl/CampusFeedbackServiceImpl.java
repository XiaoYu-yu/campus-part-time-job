package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusFeedbackSubmitDTO;
import com.cangqiong.takeaway.campus.entity.CampusFeedback;
import com.cangqiong.takeaway.campus.mapper.CampusFeedbackMapper;
import com.cangqiong.takeaway.campus.service.CampusFeedbackService;
import com.cangqiong.takeaway.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class CampusFeedbackServiceImpl implements CampusFeedbackService {

    private static final String STATUS_PENDING = "PENDING";
    private static final Set<String> SUPPORTED_ROLES = Set.of("USER", "PARTTIME");
    private static final Set<String> SUPPORTED_CATEGORIES = Set.of("BUG", "SUGGESTION", "ORDER", "ACCOUNT", "OTHER");

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
        String contact = normalizeText(dto.getContact(), 100);
        String page = normalizeText(dto.getPage(), 120);
        String orderId = normalizeText(dto.getOrderId(), 32);

        LocalDateTime now = LocalDateTime.now();
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

    private String normalizeEnum(String value, String defaultValue, Set<String> allowedValues, String errorMessage) {
        String normalized = StringUtils.hasText(value) ? value.trim().toUpperCase() : defaultValue;
        if (!allowedValues.contains(normalized)) {
            throw new BusinessException(errorMessage);
        }
        return normalized;
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

package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusAdminExceptionResolveDTO;
import com.cangqiong.takeaway.campus.mapper.CampusExceptionRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusExceptionRecordQuery;
import com.cangqiong.takeaway.campus.service.CampusExceptionRecordService;
import com.cangqiong.takeaway.campus.vo.CampusExceptionRecordVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CampusExceptionRecordServiceImpl implements CampusExceptionRecordService {

    private static final String PROCESS_STATUS_REPORTED = "REPORTED";
    private static final String PROCESS_STATUS_RESOLVED = "RESOLVED";
    private static final String PROCESS_RESULT_HANDLED = "HANDLED";
    private static final String PROCESS_RESULT_MARKED_INVALID = "MARKED_INVALID";
    private static final String PROCESS_RESULT_FOLLOWED_UP = "FOLLOWED_UP";

    @Autowired
    private CampusExceptionRecordMapper campusExceptionRecordMapper;

    @Override
    public PageResult<CampusExceptionRecordVO> pageQuery(CampusExceptionRecordQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String relayOrderId = normalizeText(query == null ? null : query.getRelayOrderId());
        String processStatus = normalizeProcessStatus(query == null ? null : query.getProcessStatus());
        Long courierProfileId = query == null ? null : query.getCourierProfileId();

        List<CampusExceptionRecordVO> records = campusExceptionRecordMapper.selectByCondition(
                relayOrderId,
                courierProfileId,
                processStatus,
                offset,
                pageSize
        );
        Long total = campusExceptionRecordMapper.countByCondition(relayOrderId, courierProfileId, processStatus);
        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    public CampusExceptionRecordVO getById(Long id) {
        if (id == null) {
            throw new BusinessException("异常记录ID不能为空");
        }
        CampusExceptionRecordVO record = campusExceptionRecordMapper.selectDetailById(id);
        if (record == null) {
            throw new BusinessException(404, "异常记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void resolveByAdmin(Long id, CampusAdminExceptionResolveDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (id == null) {
            throw new BusinessException("异常记录ID不能为空");
        }
        if (dto == null) {
            throw new BusinessException("异常处理请求不能为空");
        }

        String processResult = normalizeProcessResult(dto.getProcessResult());
        String adminNote = normalizeText(dto.getAdminNote());
        if (!StringUtils.hasText(adminNote)) {
            throw new BusinessException("处理备注不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        int updated = campusExceptionRecordMapper.resolveByAdmin(id, processResult, adminNote, employeeId, now, now);
        if (updated > 0) {
            return;
        }

        CampusExceptionRecordVO existing = campusExceptionRecordMapper.selectDetailById(id);
        if (existing == null) {
            throw new BusinessException(404, "异常记录不存在");
        }
        if (PROCESS_STATUS_RESOLVED.equals(existing.getProcessStatus())) {
            throw new BusinessException("异常记录已处理，不能重复处理");
        }
        throw new BusinessException("当前异常记录状态不可处理");
    }

    private PageResult<CampusExceptionRecordVO> buildPageResult(List<CampusExceptionRecordVO> records, Long total, int page, int pageSize) {
        long resolvedTotal = total == null ? 0L : total;
        PageResult<CampusExceptionRecordVO> pageResult = new PageResult<>();
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

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeProcessStatus(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        String upper = normalized.toUpperCase();
        if (PROCESS_STATUS_REPORTED.equals(upper) || PROCESS_STATUS_RESOLVED.equals(upper)) {
            return upper;
        }
        throw new BusinessException("异常处理状态仅支持 REPORTED 或 RESOLVED");
    }

    private String normalizeProcessResult(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException("处理结果不能为空");
        }
        String upper = normalized.toUpperCase();
        if (PROCESS_RESULT_HANDLED.equals(upper)
                || PROCESS_RESULT_MARKED_INVALID.equals(upper)
                || PROCESS_RESULT_FOLLOWED_UP.equals(upper)) {
            return upper;
        }
        throw new BusinessException("处理结果仅支持 HANDLED、MARKED_INVALID 或 FOLLOWED_UP");
    }
}

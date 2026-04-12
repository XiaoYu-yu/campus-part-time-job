package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.mapper.CampusExceptionRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusExceptionRecordQuery;
import com.cangqiong.takeaway.campus.service.CampusExceptionRecordService;
import com.cangqiong.takeaway.campus.vo.CampusExceptionRecordVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CampusExceptionRecordServiceImpl implements CampusExceptionRecordService {

    private static final String PROCESS_STATUS_REPORTED = "REPORTED";
    private static final String PROCESS_STATUS_RESOLVED = "RESOLVED";

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
}

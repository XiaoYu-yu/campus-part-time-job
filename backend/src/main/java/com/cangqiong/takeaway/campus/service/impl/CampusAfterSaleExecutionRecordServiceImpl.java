package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.mapper.CampusAfterSaleExecutionRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusAfterSaleExecutionRecordQuery;
import com.cangqiong.takeaway.campus.service.CampusAfterSaleExecutionRecordService;
import com.cangqiong.takeaway.campus.vo.CampusAfterSaleExecutionRecordVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CampusAfterSaleExecutionRecordServiceImpl implements CampusAfterSaleExecutionRecordService {

    private static final String EXECUTION_STATUS_SUCCESS = "SUCCESS";
    private static final String EXECUTION_STATUS_FAILED = "FAILED";

    @Autowired
    private CampusAfterSaleExecutionRecordMapper campusAfterSaleExecutionRecordMapper;

    @Override
    public PageResult<CampusAfterSaleExecutionRecordVO> pageQuery(CampusAfterSaleExecutionRecordQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String relayOrderId = normalizeText(query == null ? null : query.getRelayOrderId());
        String executionStatus = normalizeExecutionStatus(query == null ? null : query.getExecutionStatus());
        Integer corrected = query == null || query.getCorrected() == null ? null : (query.getCorrected() ? 1 : 0);

        List<CampusAfterSaleExecutionRecordVO> records = campusAfterSaleExecutionRecordMapper.selectByCondition(
                relayOrderId,
                executionStatus,
                corrected,
                offset,
                pageSize
        );
        Long total = campusAfterSaleExecutionRecordMapper.countByCondition(relayOrderId, executionStatus, corrected);
        return buildPageResult(records, total, page, pageSize);
    }

    private PageResult<CampusAfterSaleExecutionRecordVO> buildPageResult(
            List<CampusAfterSaleExecutionRecordVO> records,
            Long total,
            int page,
            int pageSize
    ) {
        long resolvedTotal = total == null ? 0L : total;
        PageResult<CampusAfterSaleExecutionRecordVO> pageResult = new PageResult<>();
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

    private String normalizeExecutionStatus(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        String upper = normalized.toUpperCase();
        if (EXECUTION_STATUS_SUCCESS.equals(upper) || EXECUTION_STATUS_FAILED.equals(upper)) {
            return upper;
        }
        throw new BusinessException("售后执行历史状态仅支持 SUCCESS 或 FAILED");
    }
}

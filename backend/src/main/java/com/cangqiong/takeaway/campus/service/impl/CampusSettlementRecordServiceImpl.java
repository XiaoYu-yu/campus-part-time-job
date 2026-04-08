package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.mapper.CampusSettlementRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CampusSettlementRecordServiceImpl implements CampusSettlementRecordService {

    @Autowired
    private CampusSettlementRecordMapper campusSettlementRecordMapper;

    @Override
    public PageResult<CampusSettlementRecordVO> pageQuery(CampusSettlementQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;

        List<CampusSettlementRecordVO> records = campusSettlementRecordMapper.selectByCondition(
                normalizeText(query == null ? null : query.getSettlementStatus()),
                query == null ? null : query.getCourierProfileId(),
                normalizeText(query == null ? null : query.getRelayOrderId()),
                offset,
                pageSize
        );
        Long total = campusSettlementRecordMapper.countByCondition(
                normalizeText(query == null ? null : query.getSettlementStatus()),
                query == null ? null : query.getCourierProfileId(),
                normalizeText(query == null ? null : query.getRelayOrderId())
        );

        PageResult<CampusSettlementRecordVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize((long) pageSize);
        pageResult.setCurrent((long) page);
        pageResult.setPages((total + pageSize - 1) / pageSize);
        return pageResult;
    }

    private int safePositive(Integer value, int defaultValue) {
        return value == null || value < 1 ? defaultValue : value;
    }

    private int safePageSize(Integer pageSize, Integer size) {
        int resolved = pageSize != null ? pageSize : (size != null ? size : 10);
        resolved = resolved < 1 ? 10 : resolved;
        return Math.min(resolved, 100);
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}

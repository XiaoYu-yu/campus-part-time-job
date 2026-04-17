package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementBatchOperationDTO;
import com.cangqiong.takeaway.campus.entity.CampusSettlementBatchOperationRecord;
import com.cangqiong.takeaway.campus.mapper.CampusSettlementBatchOperationRecordMapper;
import com.cangqiong.takeaway.campus.mapper.CampusSettlementRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusSettlementBatchOperationQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementBatchOperationRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementBatchOperationRecordVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CampusSettlementBatchOperationRecordServiceImpl implements CampusSettlementBatchOperationRecordService {

    private static final String OPERATION_TYPE_REVIEW = "REVIEW";
    private static final String OPERATION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String OPERATION_RESULT_PASSED = "PASSED";
    private static final String OPERATION_RESULT_REJECTED = "REJECTED";
    private static final String OPERATION_RESULT_REQUESTED = "REQUESTED";
    private static final String OPERATION_RESULT_RECORDED = "RECORDED";

    @Autowired
    private CampusSettlementBatchOperationRecordMapper batchOperationRecordMapper;

    @Autowired
    private CampusSettlementRecordMapper settlementRecordMapper;

    @Override
    public PageResult<CampusSettlementBatchOperationRecordVO> pageByBatchNo(String batchNo, CampusSettlementBatchOperationQuery query) {
        String normalizedBatchNo = normalizeBatchNo(batchNo);
        assertPayoutBatchExists(normalizedBatchNo);

        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String operationType = normalizeOperationTypeFilter(query == null ? null : query.getOperationType());
        String operationResult = normalizeOperationResultFilter(query == null ? null : query.getOperationResult());

        List<CampusSettlementBatchOperationRecordVO> records = batchOperationRecordMapper.selectByBatchNo(
                normalizedBatchNo,
                operationType,
                operationResult,
                offset,
                pageSize
        );
        Long total = batchOperationRecordMapper.countByBatchNo(normalizedBatchNo, operationType, operationResult);
        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    @Transactional
    public void recordReview(String batchNo, CampusAdminSettlementBatchOperationDTO dto, Long employeeId) {
        String operationResult = normalizeReviewResult(dto == null ? null : dto.getOperationResult());
        recordOperation(batchNo, dto, employeeId, OPERATION_TYPE_REVIEW, operationResult);
    }

    @Override
    @Transactional
    public void recordWithdraw(String batchNo, CampusAdminSettlementBatchOperationDTO dto, Long employeeId) {
        String operationResult = normalizeWithdrawResult(dto == null ? null : dto.getOperationResult());
        recordOperation(batchNo, dto, employeeId, OPERATION_TYPE_WITHDRAW, operationResult);
    }

    private void recordOperation(
            String batchNo,
            CampusAdminSettlementBatchOperationDTO dto,
            Long employeeId,
            String operationType,
            String operationResult
    ) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("批次操作请求不能为空");
        }

        String normalizedBatchNo = normalizeBatchNo(batchNo);
        assertPayoutBatchExists(normalizedBatchNo);
        String operationRemark = normalizeText(dto.getOperationRemark());
        if (!StringUtils.hasText(operationRemark)) {
            throw new BusinessException("批次操作备注不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        CampusSettlementBatchOperationRecord record = new CampusSettlementBatchOperationRecord();
        record.setPayoutBatchNo(normalizedBatchNo);
        record.setOperationType(operationType);
        record.setOperationResult(operationResult);
        record.setOperationRemark(operationRemark);
        record.setOperatedByEmployeeId(employeeId);
        record.setOperatedAt(now);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        batchOperationRecordMapper.insert(record);
    }

    private void assertPayoutBatchExists(String batchNo) {
        if (settlementRecordMapper.selectPayoutBatchSummaryByBatchNo(batchNo) == null) {
            throw new BusinessException(404, "打款批次不存在");
        }
    }

    private PageResult<CampusSettlementBatchOperationRecordVO> buildPageResult(
            List<CampusSettlementBatchOperationRecordVO> records,
            Long total,
            int page,
            int pageSize
    ) {
        long resolvedTotal = total == null ? 0L : total;
        PageResult<CampusSettlementBatchOperationRecordVO> pageResult = new PageResult<>();
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

    private String normalizeBatchNo(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException("打款批次号不能为空");
        }
        return normalized;
    }

    private String normalizeText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalizeOperationTypeFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        String upper = normalized.toUpperCase();
        if (OPERATION_TYPE_REVIEW.equals(upper) || OPERATION_TYPE_WITHDRAW.equals(upper)) {
            return upper;
        }
        throw new BusinessException("批次操作类型仅支持 REVIEW 或 WITHDRAW");
    }

    private String normalizeOperationResultFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        String upper = normalized.toUpperCase();
        if (OPERATION_RESULT_PASSED.equals(upper)
                || OPERATION_RESULT_REJECTED.equals(upper)
                || OPERATION_RESULT_REQUESTED.equals(upper)
                || OPERATION_RESULT_RECORDED.equals(upper)) {
            return upper;
        }
        throw new BusinessException("批次操作结果仅支持 PASSED、REJECTED、REQUESTED 或 RECORDED");
    }

    private String normalizeReviewResult(String value) {
        String upper = requireOperationResult(value);
        if (OPERATION_RESULT_PASSED.equals(upper) || OPERATION_RESULT_REJECTED.equals(upper)) {
            return upper;
        }
        throw new BusinessException("批次复核结果仅支持 PASSED 或 REJECTED");
    }

    private String normalizeWithdrawResult(String value) {
        String upper = requireOperationResult(value);
        if (OPERATION_RESULT_REQUESTED.equals(upper) || OPERATION_RESULT_RECORDED.equals(upper)) {
            return upper;
        }
        throw new BusinessException("批次撤回结果仅支持 REQUESTED 或 RECORDED");
    }

    private String requireOperationResult(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException("批次操作结果不能为空");
        }
        return normalized.toUpperCase();
    }
}

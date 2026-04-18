package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementReconcileDifferenceCreateDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementReconcileDifferenceResolveDTO;
import com.cangqiong.takeaway.campus.entity.CampusSettlementReconcileDifferenceRecord;
import com.cangqiong.takeaway.campus.entity.CampusSettlementRecord;
import com.cangqiong.takeaway.campus.mapper.CampusSettlementReconcileDifferenceRecordMapper;
import com.cangqiong.takeaway.campus.mapper.CampusSettlementRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusSettlementReconcileDifferenceQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementReconcileDifferenceRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileDifferenceRecordVO;
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
public class CampusSettlementReconcileDifferenceRecordServiceImpl implements CampusSettlementReconcileDifferenceRecordService {

    private static final String PROCESS_STATUS_OPEN = "OPEN";
    private static final String PROCESS_STATUS_RESOLVED = "RESOLVED";
    private static final String PROCESS_RESULT_CONFIRMED = "CONFIRMED";
    private static final String PROCESS_RESULT_MARKED_INVALID = "MARKED_INVALID";
    private static final String PROCESS_RESULT_FOLLOWED_UP = "FOLLOWED_UP";
    private static final String DIFFERENCE_TYPE_AMOUNT_MISMATCH = "AMOUNT_MISMATCH";
    private static final String DIFFERENCE_TYPE_STATUS_MISMATCH = "STATUS_MISMATCH";
    private static final String DIFFERENCE_TYPE_UNVERIFIED_PAID = "UNVERIFIED_PAID";
    private static final String DIFFERENCE_TYPE_FAILED_NEEDS_RETRY = "FAILED_NEEDS_RETRY";
    private static final String DIFFERENCE_TYPE_MANUAL_NOTE = "MANUAL_NOTE";
    private static final String SOURCE_MANUAL_ADMIN = "MANUAL_ADMIN";
    private static final String SOURCE_SIMULATED_RECONCILE = "SIMULATED_RECONCILE";

    @Autowired
    private CampusSettlementReconcileDifferenceRecordMapper reconcileDifferenceRecordMapper;

    @Autowired
    private CampusSettlementRecordMapper settlementRecordMapper;

    @Override
    public PageResult<CampusSettlementReconcileDifferenceRecordVO> pageQuery(CampusSettlementReconcileDifferenceQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String payoutBatchNo = normalizeText(query == null ? null : query.getPayoutBatchNo());
        Long settlementRecordId = query == null ? null : query.getSettlementRecordId();
        String relayOrderId = normalizeText(query == null ? null : query.getRelayOrderId());
        String differenceType = normalizeDifferenceTypeFilter(query == null ? null : query.getDifferenceType());
        String processStatus = normalizeProcessStatus(query == null ? null : query.getProcessStatus());

        List<CampusSettlementReconcileDifferenceRecordVO> records = reconcileDifferenceRecordMapper.selectByCondition(
                payoutBatchNo,
                settlementRecordId,
                relayOrderId,
                differenceType,
                processStatus,
                offset,
                pageSize
        );
        Long total = reconcileDifferenceRecordMapper.countByCondition(
                payoutBatchNo,
                settlementRecordId,
                relayOrderId,
                differenceType,
                processStatus
        );
        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    public CampusSettlementReconcileDifferenceRecordVO getById(Long id) {
        if (id == null) {
            throw new BusinessException("对账差异记录ID不能为空");
        }
        CampusSettlementReconcileDifferenceRecordVO record = reconcileDifferenceRecordMapper.selectDetailById(id);
        if (record == null) {
            throw new BusinessException(404, "对账差异记录不存在");
        }
        return record;
    }

    @Override
    @Transactional
    public void createByAdmin(CampusAdminSettlementReconcileDifferenceCreateDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("对账差异请求不能为空");
        }
        if (dto.getSettlementRecordId() == null) {
            throw new BusinessException("结算记录ID不能为空");
        }

        CampusSettlementRecord settlementRecord = settlementRecordMapper.selectById(dto.getSettlementRecordId());
        if (settlementRecord == null) {
            throw new BusinessException(404, "结算记录不存在");
        }

        String payoutBatchNo = requireText(dto.getPayoutBatchNo(), "打款批次号不能为空");
        String currentBatchNo = normalizeText(settlementRecord.getPayoutBatchNo());
        if (!payoutBatchNo.equals(currentBatchNo)) {
            throw new BusinessException("打款批次号与结算记录不一致");
        }

        String differenceType = normalizeDifferenceTypeRequired(dto.getDifferenceType());
        String differenceRemark = requireText(dto.getDifferenceRemark(), "差异备注不能为空");
        String source = normalizeSource(dto.getSource());
        validateAmounts(differenceType, dto.getExpectedAmount(), dto.getActualAmount());

        LocalDateTime now = LocalDateTime.now();
        CampusSettlementReconcileDifferenceRecord record = new CampusSettlementReconcileDifferenceRecord();
        record.setPayoutBatchNo(payoutBatchNo);
        record.setSettlementRecordId(settlementRecord.getId());
        record.setRelayOrderId(settlementRecord.getRelayOrderId());
        record.setCourierProfileId(settlementRecord.getCourierProfileId());
        record.setDifferenceType(differenceType);
        record.setExpectedAmount(dto.getExpectedAmount());
        record.setActualAmount(dto.getActualAmount());
        record.setDifferenceRemark(differenceRemark);
        record.setProcessStatus(PROCESS_STATUS_OPEN);
        record.setReportedByEmployeeId(employeeId);
        record.setReportedAt(now);
        record.setSource(source);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        reconcileDifferenceRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void resolveByAdmin(Long id, CampusAdminSettlementReconcileDifferenceResolveDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (id == null) {
            throw new BusinessException("对账差异记录ID不能为空");
        }
        if (dto == null) {
            throw new BusinessException("对账差异处理请求不能为空");
        }

        String processResult = normalizeProcessResult(dto.getProcessResult());
        String processRemark = requireText(dto.getProcessRemark(), "处理备注不能为空");
        LocalDateTime now = LocalDateTime.now();
        int updated = reconcileDifferenceRecordMapper.resolveByAdmin(id, processResult, processRemark, employeeId, now, now);
        if (updated > 0) {
            return;
        }

        CampusSettlementReconcileDifferenceRecordVO existing = reconcileDifferenceRecordMapper.selectDetailById(id);
        if (existing == null) {
            throw new BusinessException(404, "对账差异记录不存在");
        }
        if (PROCESS_STATUS_RESOLVED.equals(existing.getProcessStatus())) {
            throw new BusinessException("对账差异记录已处理，不能重复处理");
        }
        throw new BusinessException("当前对账差异状态不可处理");
    }

    private PageResult<CampusSettlementReconcileDifferenceRecordVO> buildPageResult(
            List<CampusSettlementReconcileDifferenceRecordVO> records,
            Long total,
            int page,
            int pageSize
    ) {
        long resolvedTotal = total == null ? 0L : total;
        PageResult<CampusSettlementReconcileDifferenceRecordVO> pageResult = new PageResult<>();
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

    private String requireText(String value, String errorMessage) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            throw new BusinessException(errorMessage);
        }
        return normalized;
    }

    private String normalizeDifferenceTypeRequired(String value) {
        String normalized = requireText(value, "差异类型不能为空").toUpperCase();
        return assertDifferenceType(normalized);
    }

    private String normalizeDifferenceTypeFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        return assertDifferenceType(normalized.toUpperCase());
    }

    private String assertDifferenceType(String value) {
        if (DIFFERENCE_TYPE_AMOUNT_MISMATCH.equals(value)
                || DIFFERENCE_TYPE_STATUS_MISMATCH.equals(value)
                || DIFFERENCE_TYPE_UNVERIFIED_PAID.equals(value)
                || DIFFERENCE_TYPE_FAILED_NEEDS_RETRY.equals(value)
                || DIFFERENCE_TYPE_MANUAL_NOTE.equals(value)) {
            return value;
        }
        throw new BusinessException("对账差异类型仅支持 AMOUNT_MISMATCH、STATUS_MISMATCH、UNVERIFIED_PAID、FAILED_NEEDS_RETRY 或 MANUAL_NOTE");
    }

    private String normalizeProcessStatus(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        String upper = normalized.toUpperCase();
        if (PROCESS_STATUS_OPEN.equals(upper) || PROCESS_STATUS_RESOLVED.equals(upper)) {
            return upper;
        }
        throw new BusinessException("对账差异处理状态仅支持 OPEN 或 RESOLVED");
    }

    private String normalizeProcessResult(String value) {
        String normalized = requireText(value, "处理结果不能为空").toUpperCase();
        if (PROCESS_RESULT_CONFIRMED.equals(normalized)
                || PROCESS_RESULT_MARKED_INVALID.equals(normalized)
                || PROCESS_RESULT_FOLLOWED_UP.equals(normalized)) {
            return normalized;
        }
        throw new BusinessException("处理结果仅支持 CONFIRMED、MARKED_INVALID 或 FOLLOWED_UP");
    }

    private String normalizeSource(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return SOURCE_MANUAL_ADMIN;
        }
        String upper = normalized.toUpperCase();
        if (SOURCE_MANUAL_ADMIN.equals(upper) || SOURCE_SIMULATED_RECONCILE.equals(upper)) {
            return upper;
        }
        throw new BusinessException("对账差异来源仅支持 MANUAL_ADMIN 或 SIMULATED_RECONCILE");
    }

    private void validateAmounts(String differenceType, BigDecimal expectedAmount, BigDecimal actualAmount) {
        if (DIFFERENCE_TYPE_AMOUNT_MISMATCH.equals(differenceType)
                && (expectedAmount == null || actualAmount == null)) {
            throw new BusinessException("金额差异必须填写应有金额和实际金额");
        }
    }
}

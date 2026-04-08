package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementBatchPayoutDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementConfirmDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementPayoutResultDTO;
import com.cangqiong.takeaway.campus.entity.CampusSettlementRecord;
import com.cangqiong.takeaway.campus.enums.CampusPayoutStatus;
import com.cangqiong.takeaway.campus.enums.CampusSettlementStatus;
import com.cangqiong.takeaway.campus.mapper.CampusSettlementRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementBatchPayoutResultVO;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileSummaryVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CampusSettlementRecordServiceImpl implements CampusSettlementRecordService {

    private static final BigDecimal ZERO_AMOUNT = new BigDecimal("0.00");

    @Autowired
    private CampusSettlementRecordMapper campusSettlementRecordMapper;

    @Override
    public PageResult<CampusSettlementRecordVO> pageQuery(CampusSettlementQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String settlementStatus = resolveSettlementStatusFilter(query == null ? null : query.getSettlementStatus());
        String payoutStatus = normalizePayoutStatusFilter(query == null ? null : query.getPayoutStatus());
        String relayOrderId = normalizeText(query == null ? null : query.getRelayOrderId());

        List<CampusSettlementRecordVO> records = campusSettlementRecordMapper.selectByCondition(
                settlementStatus,
                payoutStatus,
                query == null ? null : query.getCourierProfileId(),
                relayOrderId,
                offset,
                pageSize
        );
        Long total = campusSettlementRecordMapper.countByCondition(
                settlementStatus,
                payoutStatus,
                query == null ? null : query.getCourierProfileId(),
                relayOrderId
        );

        PageResult<CampusSettlementRecordVO> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize((long) pageSize);
        pageResult.setCurrent((long) page);
        pageResult.setPages((total + pageSize - 1) / pageSize);
        return pageResult;
    }

    @Override
    public CampusSettlementRecordVO getById(Long id) {
        CampusSettlementRecord record = campusSettlementRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(404, "结算记录不存在");
        }
        CampusSettlementRecordVO vo = new CampusSettlementRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    @Override
    public void confirmByAdmin(Long id, CampusAdminSettlementConfirmDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("结算确认请求不能为空");
        }

        CampusSettlementRecord record = campusSettlementRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(404, "结算记录不存在");
        }
        if (!CampusSettlementStatus.PENDING.name().equals(record.getSettlementStatus())) {
            throw new BusinessException("当前结算记录状态不可重复确认");
        }

        String settleRemark = normalizeText(dto.getSettleRemark());
        if (!StringUtils.hasText(settleRemark)) {
            throw new BusinessException("结算备注不能为空");
        }

        String finalRemark = appendRemark(record.getRemark(), settleRemark);
        LocalDateTime now = LocalDateTime.now();
        int updated = campusSettlementRecordMapper.confirmByAdmin(
                id,
                CampusSettlementStatus.SETTLED.name(),
                now,
                CampusPayoutStatus.UNPAID.name(),
                finalRemark,
                now
        );
        if (updated == 0) {
            throw new BusinessException("结算记录状态已变化，无法确认结算");
        }
    }

    @Override
    @Transactional
    public void recordPayoutResultByAdmin(Long id, CampusAdminSettlementPayoutResultDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("打款结果请求不能为空");
        }

        CampusSettlementRecord record = campusSettlementRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(404, "结算记录不存在");
        }

        PayoutWritePayload payload = buildPayoutWritePayload(record, dto.getPayoutStatus(), dto.getPayoutRemark(), dto.getPayoutReferenceNo(), true);
        int updated = campusSettlementRecordMapper.recordPayoutResultByAdmin(
                id,
                payload.currentPayoutStatus,
                payload.targetPayoutStatus,
                payload.payoutRemark,
                payload.payoutReferenceNo,
                employeeId,
                payload.recordedAt,
                payload.recordedAt
        );
        if (updated == 0) {
            throw new BusinessException("结算记录状态已变化，无法记录打款结果");
        }
    }

    @Override
    @Transactional
    public CampusSettlementBatchPayoutResultVO batchRecordPayoutResultByAdmin(CampusAdminSettlementBatchPayoutDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("批量打款请求不能为空");
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();
        if (dto.getSettlementIds() != null) {
            for (Long settlementId : dto.getSettlementIds()) {
                if (settlementId != null) {
                    uniqueIds.add(settlementId);
                }
            }
        }
        if (uniqueIds.isEmpty()) {
            throw new BusinessException("结算记录ID列表不能为空");
        }

        String payoutStatus = normalizeRequiredPayoutStatus(dto.getPayoutStatus()).name();
        String payoutRemark = normalizeText(dto.getPayoutRemark());
        if (!StringUtils.hasText(payoutRemark)) {
            throw new BusinessException("批量打款备注不能为空");
        }
        String batchNo = normalizeOptionalReferenceNo(dto.getBatchNo());

        List<Long> skippedIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        int successCount = 0;

        for (Long settlementId : uniqueIds) {
            CampusSettlementRecord record = campusSettlementRecordMapper.selectById(settlementId);
            if (record == null) {
                skippedIds.add(settlementId);
                continue;
            }

            PayoutWritePayload payload;
            try {
                payload = buildPayoutWritePayload(record, payoutStatus, payoutRemark, batchNo, false);
            } catch (BusinessException ex) {
                skippedIds.add(settlementId);
                continue;
            }

            int updated = campusSettlementRecordMapper.recordPayoutResultByAdmin(
                    settlementId,
                    payload.currentPayoutStatus,
                    payload.targetPayoutStatus,
                    payload.payoutRemark,
                    payload.payoutReferenceNo,
                    employeeId,
                    payload.recordedAt,
                    payload.recordedAt
            );
            if (updated == 0) {
                failedIds.add(settlementId);
            } else {
                successCount++;
            }
        }

        CampusSettlementBatchPayoutResultVO resultVO = new CampusSettlementBatchPayoutResultVO();
        resultVO.setTotalRequested(uniqueIds.size());
        resultVO.setSuccessCount(successCount);
        resultVO.setSkippedCount(skippedIds.size());
        resultVO.setFailedCount(failedIds.size());
        resultVO.setSkippedIds(skippedIds);
        resultVO.setFailedIds(failedIds);
        return resultVO;
    }

    @Override
    public CampusSettlementReconcileSummaryVO getReconcileSummary(CampusSettlementQuery query) {
        String settlementStatus = resolveSettlementStatusFilter(query == null ? null : query.getSettlementStatus());
        String payoutStatus = normalizePayoutStatusFilter(query == null ? null : query.getPayoutStatus());
        CampusSettlementReconcileSummaryVO summaryVO = campusSettlementRecordMapper.selectReconcileSummary(
                settlementStatus,
                payoutStatus,
                query == null ? null : query.getCourierProfileId()
        );
        if (summaryVO == null) {
            summaryVO = new CampusSettlementReconcileSummaryVO();
        }
        summaryVO.setTotalCount(summaryVO.getTotalCount() == null ? 0L : summaryVO.getTotalCount());
        summaryVO.setPendingPayoutCount(summaryVO.getPendingPayoutCount() == null ? 0L : summaryVO.getPendingPayoutCount());
        summaryVO.setPaidCount(summaryVO.getPaidCount() == null ? 0L : summaryVO.getPaidCount());
        summaryVO.setFailedPayoutCount(summaryVO.getFailedPayoutCount() == null ? 0L : summaryVO.getFailedPayoutCount());
        summaryVO.setTotalPendingAmount(normalizeAmount(summaryVO.getTotalPendingAmount()));
        summaryVO.setTotalPaidAmount(normalizeAmount(summaryVO.getTotalPaidAmount()));
        summaryVO.setTotalFailedAmount(normalizeAmount(summaryVO.getTotalFailedAmount()));
        return summaryVO;
    }

    private PayoutWritePayload buildPayoutWritePayload(
            CampusSettlementRecord record,
            String payoutStatusText,
            String payoutRemarkText,
            String payoutReferenceNoText,
            boolean requireReferenceNoWhenPaid
    ) {
        if (record == null) {
            throw new BusinessException("结算记录不存在");
        }
        if (!CampusSettlementStatus.SETTLED.name().equals(record.getSettlementStatus())) {
            throw new BusinessException("当前结算记录尚未完成结算确认");
        }

        CampusPayoutStatus targetPayoutStatus = normalizeRequiredPayoutStatus(payoutStatusText);
        if (targetPayoutStatus != CampusPayoutStatus.PAID && targetPayoutStatus != CampusPayoutStatus.FAILED) {
            throw new BusinessException("打款结果仅支持 PAID 或 FAILED");
        }

        String payoutRemark = normalizeText(payoutRemarkText);
        if (!StringUtils.hasText(payoutRemark)) {
            throw new BusinessException("打款备注不能为空");
        }

        String payoutReferenceNo = normalizeOptionalReferenceNo(payoutReferenceNoText);
        if (requireReferenceNoWhenPaid
                && targetPayoutStatus == CampusPayoutStatus.PAID
                && !StringUtils.hasText(payoutReferenceNo)) {
            throw new BusinessException("打款成功时参考号不能为空");
        }

        CampusPayoutStatus currentPayoutStatus = resolveCurrentPayoutStatus(record);
        assertPayoutTransitionAllowed(currentPayoutStatus, targetPayoutStatus);

        PayoutWritePayload payload = new PayoutWritePayload();
        payload.currentPayoutStatus = currentPayoutStatus.name();
        payload.targetPayoutStatus = targetPayoutStatus.name();
        payload.payoutRemark = payoutRemark;
        payload.payoutReferenceNo = payoutReferenceNo;
        payload.recordedAt = LocalDateTime.now();
        return payload;
    }

    private CampusPayoutStatus resolveCurrentPayoutStatus(CampusSettlementRecord record) {
        if (StringUtils.hasText(record.getPayoutStatus())) {
            return normalizeRequiredPayoutStatus(record.getPayoutStatus());
        }
        return CampusPayoutStatus.UNPAID;
    }

    private void assertPayoutTransitionAllowed(CampusPayoutStatus currentStatus, CampusPayoutStatus targetStatus) {
        if (currentStatus == CampusPayoutStatus.PAID) {
            throw new BusinessException("当前结算记录已记录打款成功结果");
        }
        if (currentStatus == CampusPayoutStatus.FAILED && targetStatus == CampusPayoutStatus.FAILED) {
            throw new BusinessException("当前结算记录已记录打款失败结果");
        }
        if (currentStatus == CampusPayoutStatus.FAILED && targetStatus != CampusPayoutStatus.PAID) {
            throw new BusinessException("打款失败后仅允许人工纠正为 PAID");
        }
    }

    private CampusSettlementStatus resolveSettlementStatus(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        try {
            return CampusSettlementStatus.valueOf(normalized.toUpperCase());
        } catch (Exception ex) {
            throw new BusinessException("不支持的结算状态筛选值");
        }
    }

    private String resolveSettlementStatusFilter(String value) {
        CampusSettlementStatus status = resolveSettlementStatus(value);
        return status == null ? null : status.name();
    }

    private String normalizePayoutStatusFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        return normalizeRequiredPayoutStatus(normalized).name();
    }

    private CampusPayoutStatus normalizeRequiredPayoutStatus(String value) {
        try {
            return CampusPayoutStatus.valueOf(normalizeText(value).toUpperCase());
        } catch (Exception ex) {
            throw new BusinessException("不支持的打款结果");
        }
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? ZERO_AMOUNT : amount.setScale(2, RoundingMode.HALF_UP);
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

    private String normalizeOptionalReferenceNo(String value) {
        String normalized = normalizeText(value);
        return StringUtils.hasText(normalized) ? normalized : null;
    }

    private String appendRemark(String originalRemark, String settleRemark) {
        String normalizedOriginal = normalizeText(originalRemark);
        if (!StringUtils.hasText(normalizedOriginal)) {
            return settleRemark;
        }
        return normalizedOriginal + " | " + settleRemark;
    }

    private static class PayoutWritePayload {
        private String currentPayoutStatus;
        private String targetPayoutStatus;
        private String payoutRemark;
        private String payoutReferenceNo;
        private LocalDateTime recordedAt;
    }
}

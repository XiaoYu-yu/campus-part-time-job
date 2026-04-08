package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementConfirmDTO;
import com.cangqiong.takeaway.campus.entity.CampusSettlementRecord;
import com.cangqiong.takeaway.campus.enums.CampusSettlementStatus;
import com.cangqiong.takeaway.campus.mapper.CampusSettlementRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.service.CampusSettlementRecordService;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.vo.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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
                finalRemark,
                now
        );
        if (updated == 0) {
            throw new BusinessException("结算记录状态已变化，无法确认结算");
        }
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

    private String appendRemark(String originalRemark, String settleRemark) {
        String normalizedOriginal = normalizeText(originalRemark);
        if (!StringUtils.hasText(normalizedOriginal)) {
            return settleRemark;
        }
        return normalizedOriginal + " | " + settleRemark;
    }
}

package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementConfirmDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementBatchPayoutDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementPayoutResultDTO;
import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.vo.CampusSettlementBatchPayoutResultVO;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileSummaryVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusSettlementRecordService {

    PageResult<CampusSettlementRecordVO> pageQuery(CampusSettlementQuery query);

    CampusSettlementRecordVO getById(Long id);

    void confirmByAdmin(Long id, CampusAdminSettlementConfirmDTO dto, Long employeeId);

    void recordPayoutResultByAdmin(Long id, CampusAdminSettlementPayoutResultDTO dto, Long employeeId);

    CampusSettlementBatchPayoutResultVO batchRecordPayoutResultByAdmin(CampusAdminSettlementBatchPayoutDTO dto, Long employeeId);

    CampusSettlementReconcileSummaryVO getReconcileSummary(CampusSettlementQuery query);
}

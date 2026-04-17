package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementBatchOperationDTO;
import com.cangqiong.takeaway.campus.query.CampusSettlementBatchOperationQuery;
import com.cangqiong.takeaway.campus.vo.CampusSettlementBatchOperationRecordVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusSettlementBatchOperationRecordService {

    PageResult<CampusSettlementBatchOperationRecordVO> pageByBatchNo(String batchNo, CampusSettlementBatchOperationQuery query);

    void recordReview(String batchNo, CampusAdminSettlementBatchOperationDTO dto, Long employeeId);

    void recordWithdraw(String batchNo, CampusAdminSettlementBatchOperationDTO dto, Long employeeId);
}

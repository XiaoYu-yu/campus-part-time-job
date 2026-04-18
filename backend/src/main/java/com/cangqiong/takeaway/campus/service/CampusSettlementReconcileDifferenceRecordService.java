package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementReconcileDifferenceCreateDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminSettlementReconcileDifferenceResolveDTO;
import com.cangqiong.takeaway.campus.query.CampusSettlementReconcileDifferenceQuery;
import com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileDifferenceRecordVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusSettlementReconcileDifferenceRecordService {

    PageResult<CampusSettlementReconcileDifferenceRecordVO> pageQuery(CampusSettlementReconcileDifferenceQuery query);

    CampusSettlementReconcileDifferenceRecordVO getById(Long id);

    void createByAdmin(CampusAdminSettlementReconcileDifferenceCreateDTO dto, Long employeeId);

    void resolveByAdmin(Long id, CampusAdminSettlementReconcileDifferenceResolveDTO dto, Long employeeId);
}

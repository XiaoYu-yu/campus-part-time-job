package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.query.CampusSettlementQuery;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusSettlementRecordService {

    PageResult<CampusSettlementRecordVO> pageQuery(CampusSettlementQuery query);
}

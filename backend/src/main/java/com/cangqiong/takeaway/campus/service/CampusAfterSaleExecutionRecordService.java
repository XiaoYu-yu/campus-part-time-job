package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.query.CampusAfterSaleExecutionRecordQuery;
import com.cangqiong.takeaway.campus.vo.CampusAfterSaleExecutionRecordVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusAfterSaleExecutionRecordService {

    PageResult<CampusAfterSaleExecutionRecordVO> pageQuery(CampusAfterSaleExecutionRecordQuery query);
}

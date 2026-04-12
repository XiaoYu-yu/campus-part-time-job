package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.query.CampusExceptionRecordQuery;
import com.cangqiong.takeaway.campus.vo.CampusExceptionRecordVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusExceptionRecordService {

    PageResult<CampusExceptionRecordVO> pageQuery(CampusExceptionRecordQuery query);

    CampusExceptionRecordVO getById(Long id);
}

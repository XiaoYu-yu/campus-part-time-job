package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderCreateDTO;
import com.cangqiong.takeaway.campus.query.CampusCourierAvailableOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusCustomerOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusRelayOrderQuery;
import com.cangqiong.takeaway.campus.vo.CampusCourierOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusRelayOrderVO;
import com.cangqiong.takeaway.vo.PageResult;

public interface CampusRelayOrderService {

    PageResult<CampusRelayOrderVO> pageQuery(CampusRelayOrderQuery query);

    CampusRelayOrderVO getById(String id);

    String createByCustomer(CampusCustomerOrderCreateDTO dto, Long customerUserId);

    void mockPayByCustomer(String id, Long customerUserId);

    CampusCustomerOrderVO getCustomerOrderById(String id, Long customerUserId);

    PageResult<CampusCustomerOrderVO> pageQueryByCustomer(CampusCustomerOrderQuery query, Long customerUserId);

    PageResult<CampusCourierOrderVO> pageAvailableForCourier(CampusCourierAvailableOrderQuery query, Long courierUserId);

    CampusCourierOrderVO getCourierOrderById(String id, Long courierUserId);

    void acceptByCourier(String id, Long courierUserId);
}

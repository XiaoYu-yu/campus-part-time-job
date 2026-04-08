package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleDecisionDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleHandleDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierDeliverDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierExceptionReportDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierPickupDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderAfterSaleDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderCancelDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderCreateDTO;
import com.cangqiong.takeaway.campus.query.CampusAdminAfterSaleOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusCourierAvailableOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusCustomerOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusRelayOrderQuery;
import com.cangqiong.takeaway.campus.vo.CampusAdminAfterSaleOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierRecentExceptionVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusOrderTimelineVO;
import com.cangqiong.takeaway.campus.vo.CampusRelayOrderVO;
import com.cangqiong.takeaway.vo.PageResult;

import java.util.List;

public interface CampusRelayOrderService {

    PageResult<CampusRelayOrderVO> pageQuery(CampusRelayOrderQuery query);

    PageResult<CampusAdminAfterSaleOrderVO> pageAfterSaleByAdmin(CampusAdminAfterSaleOrderQuery query);

    CampusRelayOrderVO getById(String id);

    String createByCustomer(CampusCustomerOrderCreateDTO dto, Long customerUserId);

    void mockPayByCustomer(String id, Long customerUserId);

    void cancelByCustomer(String id, CampusCustomerOrderCancelDTO dto, Long customerUserId);

    void openAfterSaleByCustomer(String id, CampusCustomerOrderAfterSaleDTO dto, Long customerUserId);

    CampusCustomerOrderVO getCustomerOrderById(String id, Long customerUserId);

    PageResult<CampusCustomerOrderVO> pageQueryByCustomer(CampusCustomerOrderQuery query, Long customerUserId);

    PageResult<CampusCourierOrderVO> pageAvailableForCourier(CampusCourierAvailableOrderQuery query, Long courierUserId);

    CampusCourierOrderVO getCourierOrderById(String id, Long courierUserId);

    void acceptByCourier(String id, Long courierUserId);

    void pickupByCourier(String id, CampusCourierPickupDTO dto, Long courierUserId);

    void deliverByCourier(String id, CampusCourierDeliverDTO dto, Long courierUserId);

    void confirmByCustomer(String id, Long customerUserId);

    CampusOrderTimelineVO getTimelineByAdmin(String id);

    void handleAfterSaleByAdmin(String id, CampusAdminAfterSaleHandleDTO dto, Long employeeId);

    void recordAfterSaleDecisionByAdmin(String id, CampusAdminAfterSaleDecisionDTO dto, Long employeeId);

    void reportExceptionByCourier(String id, CampusCourierExceptionReportDTO dto, Long courierUserId);

    List<CampusCourierRecentExceptionVO> listRecentExceptionsByCourier(Long courierProfileId, Integer limit);
}

package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleDecisionDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleExecutionDTO;
import com.cangqiong.takeaway.campus.dto.CampusAdminAfterSaleHandleDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierDeliverDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierExceptionReportDTO;
import com.cangqiong.takeaway.campus.dto.CampusCourierPickupDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderAfterSaleDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderCancelDTO;
import com.cangqiong.takeaway.campus.dto.CampusCustomerOrderCreateDTO;
import com.cangqiong.takeaway.campus.entity.CampusExceptionRecord;
import com.cangqiong.takeaway.campus.enums.CampusAfterSaleDecisionType;
import com.cangqiong.takeaway.campus.enums.CampusAfterSaleExecutionStatus;
import com.cangqiong.takeaway.campus.enums.CampusAfterSaleHandleAction;
import com.cangqiong.takeaway.campus.entity.CampusCourierProfile;
import com.cangqiong.takeaway.campus.entity.CampusCustomerProfile;
import com.cangqiong.takeaway.campus.entity.CampusPickupPoint;
import com.cangqiong.takeaway.campus.entity.CampusRelayOrder;
import com.cangqiong.takeaway.campus.entity.CampusSettlementRecord;
import com.cangqiong.takeaway.campus.enums.CampusDeliveryTargetType;
import com.cangqiong.takeaway.campus.enums.CampusPaymentStatus;
import com.cangqiong.takeaway.campus.enums.CampusRelayOrderStatus;
import com.cangqiong.takeaway.campus.enums.CampusSettlementStatus;
import com.cangqiong.takeaway.campus.mapper.CampusPickupPointMapper;
import com.cangqiong.takeaway.campus.mapper.CampusLocationReportMapper;
import com.cangqiong.takeaway.campus.mapper.CampusExceptionRecordMapper;
import com.cangqiong.takeaway.campus.mapper.CampusRelayOrderMapper;
import com.cangqiong.takeaway.campus.mapper.CampusSettlementRecordMapper;
import com.cangqiong.takeaway.campus.query.CampusAdminAfterSaleExecutionQuery;
import com.cangqiong.takeaway.campus.query.CampusAdminAfterSaleOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusCourierAvailableOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusCustomerOrderQuery;
import com.cangqiong.takeaway.campus.query.CampusRelayOrderQuery;
import com.cangqiong.takeaway.campus.vo.CampusAdminAfterSaleExecutionVO;
import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.vo.CampusAdminAfterSaleOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusAdminAfterSaleResultVO;
import com.cangqiong.takeaway.campus.vo.CampusOrderTimelineItemVO;
import com.cangqiong.takeaway.campus.vo.CampusOrderTimelineVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierRecentExceptionVO;
import com.cangqiong.takeaway.campus.service.CampusCustomerProfileService;
import com.cangqiong.takeaway.campus.service.CampusRelayOrderService;
import com.cangqiong.takeaway.campus.support.CampusRuleCatalog;
import com.cangqiong.takeaway.campus.vo.CampusCustomerAfterSaleResultVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusOrderExceptionSummaryVO;
import com.cangqiong.takeaway.campus.vo.CampusRelayOrderVO;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CampusRelayOrderServiceImpl implements CampusRelayOrderService {

    private static final BigDecimal ZERO_AMOUNT = new BigDecimal("0.00");
    private static final BigDecimal MAX_DECIMAL_10_2 = new BigDecimal("99999999.99");
    private static final String SETTLEMENT_REMARK = "订单完成待结算";
    private static final int ENABLED = 1;
    private static final String EXCEPTION_PROCESS_STATUS_REPORTED = "REPORTED";
    private static final String EXCEPTION_SOURCE_COURIER = "COURIER";
    private static final DateTimeFormatter ORDER_ID_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Autowired
    private CampusRelayOrderMapper campusRelayOrderMapper;

    @Autowired
    private CampusPickupPointMapper campusPickupPointMapper;

    @Autowired
    private CampusCustomerProfileService campusCustomerProfileService;

    @Autowired
    private CampusCourierProfileService campusCourierProfileService;

    @Autowired
    private CampusSettlementRecordMapper campusSettlementRecordMapper;

    @Autowired
    private CampusLocationReportMapper campusLocationReportMapper;

    @Autowired
    private CampusExceptionRecordMapper campusExceptionRecordMapper;

    @Override
    public PageResult<CampusRelayOrderVO> pageQuery(CampusRelayOrderQuery query) {
        int page = safePositive(query.getPage(), 1);
        int pageSize = safePageSize(query.getPageSize(), query.getSize());
        int offset = (page - 1) * pageSize;
        String status = normalizeText(StringUtils.hasText(query.getStatus()) ? query.getStatus() : query.getOrderStatus());

        List<CampusRelayOrderVO> records = campusRelayOrderMapper.selectByCondition(
                normalizeText(query.getOrderNo()),
                normalizeText(query.getCustomerName()),
                normalizeText(query.getCourierName()),
                status,
                normalizeText(query.getPaymentStatus()),
                normalizeText(query.getDeliveryTargetType()),
                normalizeText(query.getDeliveryBuilding()),
                query.getPickupPointId(),
                query.getBeginTime(),
                query.getEndTime(),
                offset,
                pageSize
        );

        Long total = campusRelayOrderMapper.countByCondition(
                normalizeText(query.getOrderNo()),
                normalizeText(query.getCustomerName()),
                normalizeText(query.getCourierName()),
                status,
                normalizeText(query.getPaymentStatus()),
                normalizeText(query.getDeliveryTargetType()),
                normalizeText(query.getDeliveryBuilding()),
                query.getPickupPointId(),
                query.getBeginTime(),
                query.getEndTime()
        );

        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    public PageResult<CampusAdminAfterSaleOrderVO> pageAfterSaleByAdmin(CampusAdminAfterSaleOrderQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String orderStatus = normalizeAfterSaleOrderStatusFilter(query == null ? null : query.getOrderStatus());
        String afterSaleHandleAction = normalizeAfterSaleHandleActionFilter(query == null ? null : query.getAfterSaleHandleAction());
        String afterSaleDecisionType = normalizeAfterSaleDecisionTypeFilter(query == null ? null : query.getAfterSaleDecisionType());
        String afterSaleExecutionStatus = normalizeAfterSaleExecutionStatusFilter(query == null ? null : query.getAfterSaleExecutionStatus());

        List<CampusAdminAfterSaleOrderVO> records = campusRelayOrderMapper.selectAfterSaleByCondition(
                orderStatus,
                afterSaleHandleAction,
                afterSaleDecisionType,
                afterSaleExecutionStatus,
                query == null ? null : query.getCourierProfileId(),
                query == null ? null : query.getCustomerUserId(),
                normalizeText(query == null ? null : query.getRelayOrderId()),
                offset,
                pageSize
        );
        Long total = campusRelayOrderMapper.countAfterSaleByCondition(
                orderStatus,
                afterSaleHandleAction,
                afterSaleDecisionType,
                afterSaleExecutionStatus,
                query == null ? null : query.getCourierProfileId(),
                query == null ? null : query.getCustomerUserId(),
                normalizeText(query == null ? null : query.getRelayOrderId())
        );
        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    public CampusRelayOrderVO getById(String id) {
        CampusRelayOrderVO orderVO = campusRelayOrderMapper.selectById(id);
        if (orderVO == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return orderVO;
    }

    @Override
    @Transactional
    public String createByCustomer(CampusCustomerOrderCreateDTO dto, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        CampusCustomerProfile customerProfile = campusCustomerProfileService.getByUserId(customerUserId);
        if (customerProfile == null) {
            throw new BusinessException("校园用户资料不存在，请先完善资料");
        }

        validateCreateRequest(dto);

        CampusDeliveryTargetType targetType = resolveTargetType(dto.getTargetType());
        validateDeliveryBuilding(targetType, dto.getDeliveryBuilding());

        CampusPickupPoint pickupPoint = campusPickupPointMapper.selectById(dto.getPickupPointId());
        if (pickupPoint == null || pickupPoint.getEnabled() == null || pickupPoint.getEnabled() != ENABLED) {
            throw new BusinessException("取餐点不存在或已停用");
        }

        BigDecimal baseFee = CampusRuleCatalog.BASE_FEE.setScale(2, RoundingMode.HALF_UP);
        BigDecimal priorityFee = resolvePriorityFee(dto.getUrgentFlag());
        BigDecimal tipFee = resolveTipFee(dto.getTipFee());
        BigDecimal totalAmount = baseFee.add(priorityFee).add(tipFee).setScale(2, RoundingMode.HALF_UP);

        LocalDateTime now = LocalDateTime.now();
        CampusRelayOrder order = new CampusRelayOrder();
        order.setId(generateOrderId(now));
        order.setCustomerUserId(customerUserId);
        order.setPickupPointId(pickupPoint.getId());
        order.setDeliveryTargetType(targetType.name());
        order.setDeliveryBuilding(normalizeText(dto.getDeliveryBuilding()));
        order.setDeliveryDetail(normalizeText(dto.getDeliveryDetail()));
        order.setDeliveryContactName(normalizeText(dto.getContactName()));
        order.setDeliveryContactPhone(normalizeText(dto.getContactPhone()));
        order.setFoodDescription(normalizeText(dto.getFoodDescription()));
        order.setExternalPlatformName(normalizeText(dto.getExternalPlatformName()));
        order.setExternalOrderRef(normalizeText(dto.getExternalOrderRef()));
        order.setPickupCode(normalizeText(dto.getPickupCode()));
        order.setBaseFee(baseFee);
        order.setPriorityFee(priorityFee);
        order.setTipFee(tipFee);
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus(CampusPaymentStatus.UNPAID.name());
        order.setOrderStatus(CampusRelayOrderStatus.PENDING_PAYMENT.name());
        order.setPriorityDormitoryBuilding(targetType == CampusDeliveryTargetType.DORMITORY ? normalizeText(dto.getDeliveryBuilding()) : null);
        order.setCustomerRemark(normalizeText(dto.getRemark()));
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        campusRelayOrderMapper.insert(order);
        return order.getId();
    }

    @Override
    @Transactional
    public void mockPayByCustomer(String id, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        CampusRelayOrder order = getOwnedOrder(id, customerUserId);
        assertCustomerSideMutableBeforePickup(order);
        assertMockPayAllowed(order);

        LocalDateTime now = LocalDateTime.now();
        CampusRelayOrderStatus nextOrderStatus = resolvePostPayStatus(order);
        LocalDateTime priorityWindowDeadline = nextOrderStatus == CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING
                ? now.plusMinutes(CampusRuleCatalog.PRIORITY_WINDOW_MINUTES)
                : null;

        campusRelayOrderMapper.updateAfterMockPay(
                order.getId(),
                CampusPaymentStatus.PAID.name(),
                nextOrderStatus.name(),
                now,
                nextOrderStatus == CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING ? order.getDeliveryBuilding() : null,
                priorityWindowDeadline,
                now
        );
    }

    @Override
    @Transactional
    public void cancelByCustomer(String id, CampusCustomerOrderCancelDTO dto, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        CampusRelayOrder order = getOwnedOrder(id, customerUserId);
        String cancelReason = normalizeText(dto == null ? null : dto.getCancelReason());
        requireText(cancelReason, "取消原因不能为空");
        assertCustomerCancelAllowed(order);

        LocalDateTime now = LocalDateTime.now();
        int updated = campusRelayOrderMapper.cancelByCustomer(
                id,
                customerUserId,
                CampusRelayOrderStatus.CANCELLED.name(),
                cancelReason,
                now,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法取消");
        }
    }

    @Override
    @Transactional
    public void openAfterSaleByCustomer(String id, CampusCustomerOrderAfterSaleDTO dto, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        CampusRelayOrder order = getOwnedOrder(id, customerUserId);
        String afterSaleReason = normalizeText(dto == null ? null : dto.getAfterSaleReason());
        requireText(afterSaleReason, "售后说明不能为空");
        assertAfterSaleAllowed(order);

        LocalDateTime now = LocalDateTime.now();
        int updated = campusRelayOrderMapper.openAfterSaleByCustomer(
                id,
                customerUserId,
                CampusRelayOrderStatus.AFTER_SALE_OPEN.name(),
                afterSaleReason,
                now,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法发起售后");
        }
    }

    @Override
    public CampusCustomerOrderVO getCustomerOrderById(String id, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        CampusCustomerOrderVO orderVO = campusRelayOrderMapper.selectCustomerById(id, customerUserId);
        if (orderVO == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return orderVO;
    }

    @Override
    public CampusCustomerAfterSaleResultVO getAfterSaleResultByCustomer(String id, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        CampusRelayOrder order = getOwnedOrder(id, customerUserId);
        CampusRelayOrderVO orderVO = getById(order.getId());
        if (!isAfterSaleTracked(orderVO)) {
            throw new BusinessException("当前订单未进入售后流程");
        }

        CampusCustomerAfterSaleResultVO resultVO = new CampusCustomerAfterSaleResultVO();
        resultVO.setRelayOrderId(orderVO.getId());
        resultVO.setOrderStatus(orderVO.getStatus());
        resultVO.setAfterSaleAppliedAt(orderVO.getAfterSaleAppliedAt());
        resultVO.setAfterSaleReason(orderVO.getAfterSaleReason());
        resultVO.setAfterSaleHandleAction(orderVO.getAfterSaleHandleAction());
        resultVO.setAfterSaleHandleRemark(orderVO.getAfterSaleHandleRemark());
        resultVO.setDecisionType(orderVO.getAfterSaleDecisionType());
        resultVO.setDecisionAmount(orderVO.getAfterSaleDecisionAmount() == null
                ? null
                : orderVO.getAfterSaleDecisionAmount().setScale(2, RoundingMode.HALF_UP));
        resultVO.setAfterSaleExecutionStatus(orderVO.getAfterSaleExecutionStatus());
        resultVO.setCustomerReceiptStatus(resolveCustomerReceiptStatus(orderVO));
        resultVO.setCustomerReceiptTitle(resolveCustomerReceiptTitle(orderVO));
        resultVO.setCustomerReceiptMessage(resolveCustomerReceiptMessage(orderVO));
        resultVO.setLastUpdatedAt(resolveCustomerAfterSaleLastUpdatedAt(orderVO));
        return resultVO;
    }

    @Override
    public PageResult<CampusCustomerOrderVO> pageQueryByCustomer(CampusCustomerOrderQuery query, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        int page = safePositive(query.getPage(), 1);
        int pageSize = safePageSize(query.getPageSize(), query.getSize());
        int offset = (page - 1) * pageSize;

        List<CampusCustomerOrderVO> records = campusRelayOrderMapper.selectByCustomerCondition(
                customerUserId,
                normalizeText(query.getOrderNo()),
                normalizeText(query.getStatus()),
                normalizeText(query.getPaymentStatus()),
                normalizeText(query.getDeliveryTargetType()),
                offset,
                pageSize
        );

        Long total = campusRelayOrderMapper.countByCustomerCondition(
                customerUserId,
                normalizeText(query.getOrderNo()),
                normalizeText(query.getStatus()),
                normalizeText(query.getPaymentStatus()),
                normalizeText(query.getDeliveryTargetType())
        );

        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    public PageResult<CampusCourierOrderVO> pageAvailableForCourier(CampusCourierAvailableOrderQuery query, Long courierUserId) {
        CampusCourierProfile courierProfile = campusCourierProfileService.requireApprovedEnabledProfile(courierUserId);
        int page = safePositive(query.getPage(), 1);
        int pageSize = safePageSize(query.getPageSize(), query.getSize());
        int offset = (page - 1) * pageSize;
        LocalDateTime now = LocalDateTime.now();

        List<CampusCourierOrderVO> candidates = campusRelayOrderMapper.selectAvailableForCourier(
                normalizeText(query.getOrderNo()),
                normalizeText(query.getDeliveryTargetType()),
                normalizeText(query.getDeliveryBuilding()),
                query.getPickupPointId()
        );
        List<CampusCourierOrderVO> eligibleRecords = new ArrayList<>();
        for (CampusCourierOrderVO candidate : candidates) {
            candidate.setPriorityWindowActive(isPriorityWindowActive(candidate, now));
            if (canCourierViewOrder(candidate, courierProfile, now)) {
                eligibleRecords.add(candidate);
            }
        }

        int start = Math.min(offset, eligibleRecords.size());
        int end = Math.min(start + pageSize, eligibleRecords.size());
        List<CampusCourierOrderVO> pageRecords = eligibleRecords.subList(start, end);
        return buildPageResult(pageRecords, (long) eligibleRecords.size(), page, pageSize);
    }

    @Override
    public CampusCourierOrderVO getCourierOrderById(String id, Long courierUserId) {
        CampusCourierProfile courierProfile = campusCourierProfileService.requireApprovedEnabledProfile(courierUserId);
        CampusRelayOrder order = requireRelayOrder(id);

        if (order.getCourierProfileId() != null) {
            if (!order.getCourierProfileId().equals(courierProfile.getId())) {
                throw new BusinessException(403, "当前订单不可查看");
            }
        } else {
            assertOrderCanBeAcceptedByCourier(order, courierProfile, LocalDateTime.now());
        }

        CampusCourierOrderVO orderVO = campusRelayOrderMapper.selectCourierById(id);
        if (orderVO == null) {
            throw new BusinessException(404, "订单不存在");
        }
        orderVO.setPriorityWindowActive(isPriorityWindowActive(orderVO, LocalDateTime.now()));
        return orderVO;
    }

    @Override
    @Transactional
    public void acceptByCourier(String id, Long courierUserId) {
        CampusCourierProfile courierProfile = campusCourierProfileService.requireApprovedEnabledProfile(courierUserId);
        CampusRelayOrder order = requireRelayOrder(id);

        if (order.getCourierProfileId() != null) {
            if (order.getCourierProfileId().equals(courierProfile.getId())) {
                throw new BusinessException("当前订单已由你接单");
            }
            throw new BusinessException(403, "订单已被其他配送员接单");
        }

        LocalDateTime now = LocalDateTime.now();
        assertOrderCanBeAcceptedByCourier(order, courierProfile, now);

        int updated = campusRelayOrderMapper.acceptOrder(
                id,
                courierProfile.getId(),
                CampusRelayOrderStatus.ACCEPTED.name(),
                now,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单已被其他配送员接单或状态已变化");
        }
    }

    @Override
    @Transactional
    public void pickupByCourier(String id, CampusCourierPickupDTO dto, Long courierUserId) {
        CampusCourierProfile courierProfile = campusCourierProfileService.requireApprovedEnabledProfile(courierUserId);
        CampusRelayOrder order = requireRelayOrder(id);
        assertOrderOwnedByCourier(order, courierProfile);
        assertPickupAllowed(order);

        String pickupProofImageUrl = normalizeText(dto == null ? null : dto.getPickupProofImageUrl());
        if (!StringUtils.hasText(pickupProofImageUrl)) {
            throw new BusinessException("取餐凭证不能为空");
        }
        assertControlledFileReference(pickupProofImageUrl, "取餐凭证路径必须为受控文件路径");

        LocalDateTime now = LocalDateTime.now();
        int updated = campusRelayOrderMapper.pickupOrder(
                id,
                courierProfile.getId(),
                CampusRelayOrderStatus.PICKED_UP.name(),
                pickupProofImageUrl,
                normalizeText(dto == null ? null : dto.getCourierRemark()),
                now,
                now,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法执行取餐");
        }
    }

    @Override
    @Transactional
    public void deliverByCourier(String id, CampusCourierDeliverDTO dto, Long courierUserId) {
        CampusCourierProfile courierProfile = campusCourierProfileService.requireApprovedEnabledProfile(courierUserId);
        CampusRelayOrder order = requireRelayOrder(id);
        assertOrderOwnedByCourier(order, courierProfile);

        String courierRemark = normalizeText(dto == null ? null : dto.getCourierRemark());
        LocalDateTime now = LocalDateTime.now();
        int updated;

        if (CampusRelayOrderStatus.PICKED_UP.name().equals(order.getOrderStatus())) {
            updated = campusRelayOrderMapper.startDelivering(
                    id,
                    courierProfile.getId(),
                    CampusRelayOrderStatus.DELIVERING.name(),
                    courierRemark,
                    now
            );
            if (updated == 0) {
                throw new BusinessException("订单状态已变化，无法推进到配送中");
            }
            return;
        }

        if (CampusRelayOrderStatus.DELIVERING.name().equals(order.getOrderStatus())) {
            updated = campusRelayOrderMapper.markDelivered(
                    id,
                    courierProfile.getId(),
                    CampusRelayOrderStatus.AWAITING_CONFIRMATION.name(),
                    courierRemark,
                    now,
                    now
            );
            if (updated == 0) {
                throw new BusinessException("订单状态已变化，无法标记送达");
            }
            return;
        }

        throw new BusinessException("当前订单状态不可推进配送流程");
    }

    @Override
    @Transactional
    public void confirmByCustomer(String id, Long customerUserId) {
        if (customerUserId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        CampusRelayOrder order = getOwnedOrder(id, customerUserId);
        assertCustomerConfirmAllowed(order);

        LocalDateTime now = LocalDateTime.now();
        int updated = campusRelayOrderMapper.confirmDeliveredByCustomer(
                id,
                customerUserId,
                CampusRelayOrderStatus.COMPLETED.name(),
                now,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法确认送达");
        }
        syncSettlementRecordForCompletedOrder(order, now);
    }

    @Override
    @Transactional
    public void handleAfterSaleByAdmin(String id, CampusAdminAfterSaleHandleDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("处理请求不能为空");
        }

        CampusRelayOrder order = requireRelayOrder(id);
        if (!CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可处理售后");
        }

        CampusAfterSaleHandleAction action = resolveAfterSaleHandleAction(dto.getAction());
        String handleRemark = normalizeText(dto.getHandleRemark());
        requireText(handleRemark, "处理备注不能为空");

        CampusRelayOrderStatus nextStatus = action == CampusAfterSaleHandleAction.RESOLVED
                ? CampusRelayOrderStatus.AFTER_SALE_RESOLVED
                : CampusRelayOrderStatus.AFTER_SALE_REJECTED;
        LocalDateTime now = LocalDateTime.now();

        int updated = campusRelayOrderMapper.handleAfterSaleByAdmin(
                id,
                nextStatus.name(),
                action.name(),
                handleRemark,
                employeeId,
                now,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法处理售后");
        }
    }

    @Override
    @Transactional
    public void recordAfterSaleDecisionByAdmin(String id, CampusAdminAfterSaleDecisionDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("售后决策请求不能为空");
        }

        CampusRelayOrder order = requireRelayOrder(id);
        if (!CampusRelayOrderStatus.AFTER_SALE_RESOLVED.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可记录售后决策");
        }
        if (StringUtils.hasText(order.getAfterSaleDecisionType())) {
            throw new BusinessException("当前订单已记录售后决策");
        }

        CampusAfterSaleDecisionType decisionType = resolveAfterSaleDecisionType(dto.getDecisionType());
        String decisionRemark = normalizeText(dto.getDecisionRemark());
        requireText(decisionRemark, "售后决策备注不能为空");
        BigDecimal decisionAmount = normalizeDecisionAmount(dto.getDecisionAmount(), decisionType, order.getTotalAmount());
        CampusAfterSaleExecutionStatus initialExecutionStatus = decisionType == CampusAfterSaleDecisionType.NONE
                ? CampusAfterSaleExecutionStatus.NOT_REQUIRED
                : CampusAfterSaleExecutionStatus.PENDING;

        LocalDateTime now = LocalDateTime.now();
        int updated = campusRelayOrderMapper.recordAfterSaleDecisionByAdmin(
                id,
                decisionType.name(),
                decisionAmount,
                decisionRemark,
                employeeId,
                now,
                initialExecutionStatus.name(),
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法记录售后决策");
        }
    }

    @Override
    public CampusAdminAfterSaleResultVO getAfterSaleResultByAdmin(String id) {
        CampusRelayOrderVO order = getById(id);
        if (!isAfterSaleTracked(order)) {
            throw new BusinessException("当前订单未进入售后流程");
        }

        CampusAdminAfterSaleResultVO resultVO = new CampusAdminAfterSaleResultVO();
        BeanUtils.copyProperties(order, resultVO);
        resultVO.setRelayOrderId(order.getId());
        resultVO.setOrderStatus(order.getStatus());
        return resultVO;
    }

    @Override
    public PageResult<CampusAdminAfterSaleExecutionVO> pageAfterSaleExecutionsByAdmin(CampusAdminAfterSaleExecutionQuery query) {
        int page = safePositive(query == null ? null : query.getPage(), 1);
        int pageSize = safePageSize(query == null ? null : query.getPageSize(), query == null ? null : query.getSize());
        int offset = (page - 1) * pageSize;
        String executionStatus = normalizeAfterSaleExecutionStatusFilter(query == null ? null : query.getAfterSaleExecutionStatus());
        String decisionType = normalizeAfterSaleDecisionTypeFilter(query == null ? null : query.getDecisionType());

        List<CampusAdminAfterSaleExecutionVO> records = campusRelayOrderMapper.selectAfterSaleExecutionsByCondition(
                executionStatus,
                decisionType,
                query == null ? null : query.getCorrectedOnly(),
                query == null ? null : query.getCustomerUserId(),
                query == null ? null : query.getCourierProfileId(),
                normalizeText(query == null ? null : query.getRelayOrderId()),
                offset,
                pageSize
        );
        Long total = campusRelayOrderMapper.countAfterSaleExecutionsByCondition(
                executionStatus,
                decisionType,
                query == null ? null : query.getCorrectedOnly(),
                query == null ? null : query.getCustomerUserId(),
                query == null ? null : query.getCourierProfileId(),
                normalizeText(query == null ? null : query.getRelayOrderId())
        );
        return buildPageResult(records, total, page, pageSize);
    }

    @Override
    @Transactional
    public void recordAfterSaleExecutionByAdmin(String id, CampusAdminAfterSaleExecutionDTO dto, Long employeeId) {
        if (employeeId == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }
        if (dto == null) {
            throw new BusinessException("售后执行结果请求不能为空");
        }

        CampusRelayOrder order = requireRelayOrder(id);
        if (!CampusRelayOrderStatus.AFTER_SALE_RESOLVED.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可记录售后执行结果");
        }
        if (!StringUtils.hasText(order.getAfterSaleDecisionType())) {
            throw new BusinessException("当前订单尚未记录售后决策");
        }

        CampusAfterSaleDecisionType decisionType = resolveAfterSaleDecisionType(order.getAfterSaleDecisionType());
        if (decisionType == CampusAfterSaleDecisionType.NONE) {
            throw new BusinessException("NONE 决策类型的订单无需记录售后执行结果");
        }

        CampusAfterSaleExecutionStatus executionStatus = resolveAfterSaleExecutionStatus(dto.getExecutionStatus());
        if (executionStatus != CampusAfterSaleExecutionStatus.SUCCESS && executionStatus != CampusAfterSaleExecutionStatus.FAILED) {
            throw new BusinessException("售后执行结果仅支持 SUCCESS 或 FAILED");
        }

        String executionRemark = normalizeText(dto.getExecutionRemark());
        requireText(executionRemark, "售后执行备注不能为空");
        String executionReferenceNo = normalizeOptionalReferenceNo(dto.getExecutionReferenceNo());
        if (executionStatus == CampusAfterSaleExecutionStatus.SUCCESS
                && decisionType == CampusAfterSaleDecisionType.REFUND
                && !StringUtils.hasText(executionReferenceNo)) {
            throw new BusinessException("退款执行成功时参考号不能为空");
        }

        CampusAfterSaleExecutionStatus currentExecutionStatus = resolveCurrentAfterSaleExecutionStatus(order, decisionType);
        assertAfterSaleExecutionTransitionAllowed(currentExecutionStatus, executionStatus);

        LocalDateTime now = LocalDateTime.now();
        boolean corrected = currentExecutionStatus == CampusAfterSaleExecutionStatus.FAILED
                && executionStatus == CampusAfterSaleExecutionStatus.SUCCESS;
        int updated = campusRelayOrderMapper.recordAfterSaleExecutionByAdmin(
                id,
                currentExecutionStatus.name(),
                executionStatus.name(),
                executionRemark,
                executionReferenceNo,
                employeeId,
                now,
                corrected ? 1 : 0,
                corrected ? employeeId : null,
                corrected ? now : null,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法记录售后执行结果");
        }
    }

    @Override
    @Transactional
    public void reportExceptionByCourier(String id, CampusCourierExceptionReportDTO dto, Long courierUserId) {
        CampusCourierProfile courierProfile = campusCourierProfileService.requireApprovedEnabledProfile(courierUserId);
        CampusRelayOrder order = requireRelayOrder(id);
        assertOrderOwnedByCourier(order, courierProfile);
        assertExceptionReportAllowed(order);

        String exceptionType = normalizeText(dto == null ? null : dto.getExceptionType());
        String exceptionRemark = normalizeText(dto == null ? null : dto.getExceptionRemark());
        requireText(exceptionType, "异常类型不能为空");
        requireText(exceptionRemark, "异常说明不能为空");

        LocalDateTime now = LocalDateTime.now();
        int updated = campusRelayOrderMapper.reportExceptionByCourier(
                id,
                courierProfile.getId(),
                exceptionType,
                exceptionRemark,
                now,
                now
        );
        if (updated == 0) {
            throw new BusinessException("订单状态已变化，无法上报异常");
        }

        CampusExceptionRecord exceptionRecord = new CampusExceptionRecord();
        exceptionRecord.setRelayOrderId(id);
        exceptionRecord.setCourierProfileId(courierProfile.getId());
        exceptionRecord.setExceptionType(exceptionType);
        exceptionRecord.setExceptionRemark(exceptionRemark);
        exceptionRecord.setReportedAt(now);
        exceptionRecord.setProcessStatus(EXCEPTION_PROCESS_STATUS_REPORTED);
        exceptionRecord.setSource(EXCEPTION_SOURCE_COURIER);
        exceptionRecord.setCreatedAt(now);
        exceptionRecord.setUpdatedAt(now);
        campusExceptionRecordMapper.insert(exceptionRecord);
    }

    @Override
    public List<CampusCourierRecentExceptionVO> listRecentExceptionsByCourier(Long courierProfileId, Integer limit) {
        if (courierProfileId == null) {
            throw new BusinessException("配送员资料不能为空");
        }
        int resolvedLimit = limit == null ? 10 : limit;
        resolvedLimit = resolvedLimit < 1 ? 10 : resolvedLimit;
        resolvedLimit = Math.min(resolvedLimit, 50);
        return campusRelayOrderMapper.selectRecentExceptionsByCourier(courierProfileId, resolvedLimit);
    }

    @Override
    public CampusOrderExceptionSummaryVO getExceptionSummaryByOrderIdForAdmin(String id) {
        CampusRelayOrder order = requireRelayOrder(id);
        CampusOrderExceptionSummaryVO summaryVO = new CampusOrderExceptionSummaryVO();
        summaryVO.setRelayOrderId(order.getId());
        summaryVO.setOrderStatus(order.getOrderStatus());
        summaryVO.setCourierProfileId(order.getCourierProfileId());
        summaryVO.setLatestExceptionType(normalizeText(order.getExceptionType()));
        summaryVO.setLatestExceptionRemark(normalizeText(order.getExceptionRemark()));
        summaryVO.setLatestExceptionReportedAt(order.getExceptionReportedAt());
        summaryVO.setLatestLocationReportedAt(campusLocationReportMapper.selectLatestReportedAtByOrderId(id));
        Long locationReportCount = campusLocationReportMapper.countByOrderId(id);
        summaryVO.setLocationReportCount(locationReportCount == null ? 0L : locationReportCount);
        return summaryVO;
    }

    @Override
    public CampusOrderTimelineVO getTimelineByAdmin(String id) {
        CampusRelayOrderVO order = getById(id);
        CampusSettlementRecord settlementRecord = campusSettlementRecordMapper.selectByRelayOrderId(id);

        List<CampusOrderTimelineItemVO> items = new ArrayList<>();
        appendTimelineItem(items, "CREATED", "已下单", order.getCreatedAt(), buildCreatedRemark(order));
        appendTimelineItem(items, "PAID", "已支付", order.getPaidAt(), buildPaidRemark(order));
        appendTimelineItem(items, "ACCEPTED", "已接单", order.getAcceptedAt(), buildAcceptedRemark(order));
        appendTimelineItem(items, "PICKED_UP", "已取餐", order.getPickedUpAt(), buildPickupRemark(order));
        appendTimelineItem(items, "DELIVERED", "已送达", order.getDeliveredAt(), normalizeText(order.getCourierRemark()));
        appendTimelineItem(items, "CONFIRMED", "已确认送达", order.getAutoCompleteAt(), "客户已确认送达");
        appendTimelineItem(items, "CANCELLED", "已取消", order.getCancelledAt(), normalizeText(order.getCancelReason()));
        appendTimelineItem(items, "AFTER_SALE_OPENED", "已发起售后", order.getAfterSaleAppliedAt(), normalizeText(order.getAfterSaleReason()));
        appendTimelineItem(items, "EXCEPTION_REPORTED", "异常上报", order.getExceptionReportedAt(), buildExceptionRemark(order));
        appendTimelineItem(items, "AFTER_SALE_RESOLVED", "售后已解决",
                CampusAfterSaleHandleAction.RESOLVED.name().equals(order.getAfterSaleHandleAction()) ? order.getAfterSaleHandledAt() : null,
                buildAfterSaleHandleRemark(order));
        appendTimelineItem(items, "AFTER_SALE_REJECTED", "售后已驳回",
                CampusAfterSaleHandleAction.REJECTED.name().equals(order.getAfterSaleHandleAction()) ? order.getAfterSaleHandledAt() : null,
                buildAfterSaleHandleRemark(order));
        appendTimelineItem(items, "AFTER_SALE_DECISION_RECORDED", "售后决策已记录",
                order.getAfterSaleDecidedAt(),
                buildAfterSaleDecisionRemark(order));
        appendTimelineItem(items, "AFTER_SALE_EXECUTION_RECORDED", "售后执行结果已记录",
                order.getAfterSaleExecutedAt(),
                buildAfterSaleExecutionRemark(order));

        if (settlementRecord != null) {
            LocalDateTime eventTime = settlementRecord.getSettledAt() != null
                    ? settlementRecord.getSettledAt()
                    : settlementRecord.getCreatedAt();
            appendTimelineItem(
                    items,
                    CampusSettlementStatus.SETTLED.name().equals(settlementRecord.getSettlementStatus()) ? "SETTLED" : "SETTLEMENT_PENDING",
                    CampusSettlementStatus.SETTLED.name().equals(settlementRecord.getSettlementStatus()) ? "已结算" : "待结算",
                    eventTime,
                    normalizeText(settlementRecord.getRemark())
            );
        }

        items.sort(Comparator.comparing(CampusOrderTimelineItemVO::getEventTime));
        CampusOrderTimelineVO timelineVO = new CampusOrderTimelineVO();
        timelineVO.setOrderId(order.getId());
        timelineVO.setOrderStatus(order.getStatus());
        timelineVO.setPaymentStatus(order.getPaymentStatus());
        timelineVO.setSettlementStatus(settlementRecord == null ? null : settlementRecord.getSettlementStatus());
        timelineVO.setItems(items);
        return timelineVO;
    }

    private void validateCreateRequest(CampusCustomerOrderCreateDTO dto) {
        if (dto == null) {
            throw new BusinessException("请求体不能为空");
        }
        if (dto.getPickupPointId() == null) {
            throw new BusinessException("取餐点不能为空");
        }
        requireText(dto.getTargetType(), "配送目标类型不能为空");
        requireText(dto.getDeliveryBuilding(), "配送楼栋不能为空");
        requireText(dto.getDeliveryDetail(), "配送详情不能为空");
        requireText(dto.getContactName(), "联系人姓名不能为空");
        requireText(dto.getContactPhone(), "联系人电话不能为空");
        requireText(dto.getFoodDescription(), "外卖描述不能为空");
        requireText(dto.getExternalPlatformName(), "外部平台名称不能为空");
        requireText(dto.getExternalOrderRef(), "外部订单号不能为空");
        requireText(dto.getPickupCode(), "取餐码不能为空");
    }

    private CampusDeliveryTargetType resolveTargetType(String targetType) {
        try {
            return CampusDeliveryTargetType.valueOf(normalizeText(targetType).toUpperCase());
        } catch (Exception ex) {
            throw new BusinessException("不支持的配送目标类型");
        }
    }

    private void validateDeliveryBuilding(CampusDeliveryTargetType targetType, String deliveryBuilding) {
        String building = normalizeText(deliveryBuilding);
        if (targetType == CampusDeliveryTargetType.DORMITORY && !CampusRuleCatalog.DORMITORY_BUILDINGS.contains(building)) {
            throw new BusinessException("宿舍楼栋不在支持范围内");
        }
        if (targetType == CampusDeliveryTargetType.TEACHING_BUILDING && !CampusRuleCatalog.TEACHING_BUILDINGS.contains(building)) {
            throw new BusinessException("教学楼不在支持范围内");
        }
        if (targetType == CampusDeliveryTargetType.LIBRARY && !"图书馆".equals(building)) {
            throw new BusinessException("图书馆订单的配送楼栋必须为图书馆");
        }
    }

    private BigDecimal resolvePriorityFee(Integer urgentFlag) {
        int flag = urgentFlag == null ? 0 : urgentFlag;
        if (flag != 0 && flag != 1) {
            throw new BusinessException("加急标记仅支持 0 或 1");
        }
        return flag == 1 ? CampusRuleCatalog.PRIORITY_FEE_MIN.setScale(2, RoundingMode.HALF_UP) : ZERO_AMOUNT;
    }

    private BigDecimal resolveTipFee(BigDecimal tipFee) {
        BigDecimal resolved = tipFee == null ? ZERO_AMOUNT : tipFee.setScale(2, RoundingMode.HALF_UP);
        if (resolved.compareTo(ZERO_AMOUNT) < 0) {
            throw new BusinessException("打赏金额不能为负数");
        }
        if (resolved.compareTo(ZERO_AMOUNT) > 0) {
            if (resolved.compareTo(CampusRuleCatalog.TIP_FEE_MIN) < 0 || resolved.compareTo(CampusRuleCatalog.TIP_FEE_MAX) > 0) {
                throw new BusinessException("打赏金额必须在 1 到 10 元之间");
            }
        }
        return resolved;
    }

    private CampusRelayOrder getOwnedOrder(String id, Long customerUserId) {
        CampusRelayOrder order = campusRelayOrderMapper.selectEntityByIdAndCustomerUserId(id, customerUserId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private CampusRelayOrder requireRelayOrder(String id) {
        CampusRelayOrder order = campusRelayOrderMapper.selectEntityById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private void assertMockPayAllowed(CampusRelayOrder order) {
        if (!CampusPaymentStatus.UNPAID.name().equals(order.getPaymentStatus())) {
            throw new BusinessException("订单当前状态不可重复支付");
        }
        if (!CampusRelayOrderStatus.PENDING_PAYMENT.name().equals(order.getOrderStatus())) {
            throw new BusinessException("订单当前状态不可发起模拟支付");
        }
    }

    /**
     * Step 03A 还没有开放 customer 取消接口，但规则已经先落到状态校验里：
     * 一旦进入已取餐或之后的阶段，后续 customer 不允许直接取消或修改订单。
     */
    private void assertCustomerSideMutableBeforePickup(CampusRelayOrder order) {
        if (order.getPickedUpAt() != null) {
            throw new BusinessException("订单已取餐，当前阶段不可直接取消或修改");
        }

        if (CampusRelayOrderStatus.PICKED_UP.name().equals(order.getOrderStatus())
                || CampusRelayOrderStatus.DELIVERING.name().equals(order.getOrderStatus())
                || CampusRelayOrderStatus.AWAITING_CONFIRMATION.name().equals(order.getOrderStatus())
                || CampusRelayOrderStatus.COMPLETED.name().equals(order.getOrderStatus())
                || CampusRelayOrderStatus.CANCELLED.name().equals(order.getOrderStatus())
                || CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getOrderStatus())) {
            throw new BusinessException("订单已取餐，当前阶段不可直接取消或修改");
        }
    }

    private void assertCustomerCancelAllowed(CampusRelayOrder order) {
        if (CampusRelayOrderStatus.CANCELLED.name().equals(order.getOrderStatus())) {
            throw new BusinessException("订单已取消，无需重复操作");
        }
        if (CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getOrderStatus())) {
            throw new BusinessException("订单已进入售后处理，当前不可取消");
        }
        assertCustomerSideMutableBeforePickup(order);
    }

    private void assertAfterSaleAllowed(CampusRelayOrder order) {
        if (CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getOrderStatus())) {
            throw new BusinessException("订单已发起售后，请勿重复提交");
        }
        if (!CampusRelayOrderStatus.AWAITING_CONFIRMATION.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.COMPLETED.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可发起售后");
        }
    }

    private CampusRelayOrderStatus resolvePostPayStatus(CampusRelayOrder order) {
        if (CampusDeliveryTargetType.DORMITORY.name().equals(order.getDeliveryTargetType())) {
            return CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING;
        }
        return CampusRelayOrderStatus.WAITING_ACCEPT;
    }

    private void assertOrderCanBeAcceptedByCourier(CampusRelayOrder order, CampusCourierProfile courierProfile, LocalDateTime now) {
        if (!CampusPaymentStatus.PAID.name().equals(order.getPaymentStatus())) {
            throw new BusinessException("当前订单未支付，暂不可接单");
        }

        if (!CampusRelayOrderStatus.WAITING_ACCEPT.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可接单");
        }

        if (!CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING.name().equals(order.getOrderStatus())) {
            return;
        }

        if (!isBuildingPriorityWindowActive(order, now)) {
            return;
        }

        String courierDormitoryBuilding = normalizeText(courierProfile.getDormitoryBuilding());
        String priorityDormitoryBuilding = normalizeText(order.getPriorityDormitoryBuilding());
        if (!StringUtils.hasText(courierDormitoryBuilding)
                || !StringUtils.hasText(priorityDormitoryBuilding)
                || !priorityDormitoryBuilding.equals(courierDormitoryBuilding)) {
            throw new BusinessException(403, "当前宿舍单仍处于本楼栋优先窗口，暂不可接单");
        }
    }

    private void assertOrderOwnedByCourier(CampusRelayOrder order, CampusCourierProfile courierProfile) {
        if (order.getCourierProfileId() == null || !order.getCourierProfileId().equals(courierProfile.getId())) {
            throw new BusinessException(403, "当前订单不属于你");
        }
    }

    private void assertPickupAllowed(CampusRelayOrder order) {
        if (!CampusRelayOrderStatus.ACCEPTED.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可执行取餐");
        }
    }

    private void assertExceptionReportAllowed(CampusRelayOrder order) {
        if (!CampusRelayOrderStatus.ACCEPTED.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.PICKED_UP.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.DELIVERING.name().equals(order.getOrderStatus())
                && !CampusRelayOrderStatus.AWAITING_CONFIRMATION.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可上报异常");
        }
    }

    private void assertCustomerConfirmAllowed(CampusRelayOrder order) {
        if (!CampusRelayOrderStatus.AWAITING_CONFIRMATION.name().equals(order.getOrderStatus())) {
            throw new BusinessException("当前订单状态不可确认送达");
        }
    }

    private void syncSettlementRecordForCompletedOrder(CampusRelayOrder order, LocalDateTime now) {
        if (order.getCourierProfileId() == null) {
            return;
        }

        CampusSettlementRecord existing = campusSettlementRecordMapper.selectByRelayOrderId(order.getId());
        BigDecimal grossAmount = order.getTotalAmount() == null ? ZERO_AMOUNT : order.getTotalAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal platformCommission = ZERO_AMOUNT.setScale(2, RoundingMode.HALF_UP);
        BigDecimal pendingAmount = grossAmount.subtract(platformCommission).setScale(2, RoundingMode.HALF_UP);

        if (existing == null) {
            CampusSettlementRecord record = new CampusSettlementRecord();
            record.setRelayOrderId(order.getId());
            record.setCourierProfileId(order.getCourierProfileId());
            record.setGrossAmount(grossAmount);
            record.setPlatformCommission(platformCommission);
            record.setPendingAmount(pendingAmount);
            record.setSettlementStatus(CampusSettlementStatus.PENDING.name());
            record.setSettledAt(null);
            record.setRemark(SETTLEMENT_REMARK);
            record.setCreatedAt(now);
            record.setUpdatedAt(now);
            campusSettlementRecordMapper.insert(record);
            return;
        }

        campusSettlementRecordMapper.updateSnapshotForCompleted(
                order.getId(),
                order.getCourierProfileId(),
                grossAmount,
                platformCommission,
                pendingAmount,
                CampusSettlementStatus.PENDING.name(),
                SETTLEMENT_REMARK,
                now
        );
    }

    private void appendTimelineItem(List<CampusOrderTimelineItemVO> items, String nodeCode, String nodeLabel, LocalDateTime eventTime, String remark) {
        if (eventTime == null) {
            return;
        }
        CampusOrderTimelineItemVO item = new CampusOrderTimelineItemVO();
        item.setNodeCode(nodeCode);
        item.setNodeLabel(nodeLabel);
        item.setEventTime(eventTime);
        item.setRemark(remark);
        items.add(item);
    }

    private String buildCreatedRemark(CampusRelayOrderVO order) {
        return order.getPickupPointName() + " -> " + order.getDeliveryBuilding();
    }

    private String buildPaidRemark(CampusRelayOrderVO order) {
        return CampusPaymentStatus.PAID.name().equals(order.getPaymentStatus()) ? "模拟支付成功" : null;
    }

    private String buildAcceptedRemark(CampusRelayOrderVO order) {
        return StringUtils.hasText(order.getCourierName()) ? "接单配送员: " + order.getCourierName() : null;
    }

    private String buildPickupRemark(CampusRelayOrderVO order) {
        if (StringUtils.hasText(order.getPickupProofImageUrl())) {
            return order.getPickupProofImageUrl();
        }
        return normalizeText(order.getCourierRemark());
    }

    private String buildExceptionRemark(CampusRelayOrderVO order) {
        if (!StringUtils.hasText(order.getExceptionType()) && !StringUtils.hasText(order.getExceptionRemark())) {
            return null;
        }
        if (!StringUtils.hasText(order.getExceptionType())) {
            return normalizeText(order.getExceptionRemark());
        }
        if (!StringUtils.hasText(order.getExceptionRemark())) {
            return normalizeText(order.getExceptionType());
        }
        return order.getExceptionType() + ": " + order.getExceptionRemark();
    }

    private String buildAfterSaleHandleRemark(CampusRelayOrderVO order) {
        return normalizeText(order.getAfterSaleHandleRemark());
    }

    private String buildAfterSaleDecisionRemark(CampusRelayOrderVO order) {
        if (!StringUtils.hasText(order.getAfterSaleDecisionType())) {
            return null;
        }
        String amountText = order.getAfterSaleDecisionAmount() == null
                ? null
                : order.getAfterSaleDecisionAmount().setScale(2, RoundingMode.HALF_UP).toPlainString();
        String remark = normalizeText(order.getAfterSaleDecisionRemark());
        if (amountText == null && remark == null) {
            return order.getAfterSaleDecisionType();
        }
        if (amountText == null) {
            return order.getAfterSaleDecisionType() + ": " + remark;
        }
        if (remark == null) {
            return order.getAfterSaleDecisionType() + ": " + amountText;
        }
        return order.getAfterSaleDecisionType() + ": " + amountText + " | " + remark;
    }

    private String buildAfterSaleExecutionRemark(CampusRelayOrderVO order) {
        if (!StringUtils.hasText(order.getAfterSaleExecutionStatus())) {
            return null;
        }
        String referenceNo = normalizeText(order.getAfterSaleExecutionReferenceNo());
        String remark = normalizeText(order.getAfterSaleExecutionRemark());
        String correctedSuffix = order.getAfterSaleExecutionCorrected() != null && order.getAfterSaleExecutionCorrected() == 1
                ? " (已人工纠正)"
                : "";
        if (!StringUtils.hasText(referenceNo) && !StringUtils.hasText(remark)) {
            return order.getAfterSaleExecutionStatus() + correctedSuffix;
        }
        if (!StringUtils.hasText(referenceNo)) {
            return order.getAfterSaleExecutionStatus() + correctedSuffix + ": " + remark;
        }
        if (!StringUtils.hasText(remark)) {
            return order.getAfterSaleExecutionStatus() + correctedSuffix + ": " + referenceNo;
        }
        return order.getAfterSaleExecutionStatus() + correctedSuffix + ": " + referenceNo + " | " + remark;
    }

    private void assertControlledFileReference(String value, String message) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized) || !normalized.startsWith(CampusRuleCatalog.CONTROLLED_FILE_PREFIX)) {
            throw new BusinessException(message);
        }
    }

    private boolean isBuildingPriorityWindowActive(CampusRelayOrder order, LocalDateTime now) {
        return CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING.name().equals(order.getOrderStatus())
                && order.getPriorityWindowDeadline() != null
                && order.getPriorityWindowDeadline().isAfter(now);
    }

    private boolean isPriorityWindowActive(CampusCourierOrderVO order, LocalDateTime now) {
        return CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING.name().equals(order.getStatus())
                && order.getPriorityWindowDeadline() != null
                && order.getPriorityWindowDeadline().isAfter(now);
    }

    private boolean canCourierViewOrder(CampusCourierOrderVO order, CampusCourierProfile courierProfile, LocalDateTime now) {
        if (!CampusPaymentStatus.PAID.name().equals(order.getPaymentStatus())) {
            return false;
        }
        if (order.getCourierProfileId() != null) {
            return false;
        }
        if (CampusRelayOrderStatus.WAITING_ACCEPT.name().equals(order.getStatus())) {
            return true;
        }
        if (!CampusRelayOrderStatus.BUILDING_PRIORITY_PENDING.name().equals(order.getStatus())) {
            return false;
        }
        if (!isPriorityWindowActive(order, now)) {
            return true;
        }

        String courierDormitoryBuilding = normalizeText(courierProfile.getDormitoryBuilding());
        String priorityDormitoryBuilding = normalizeText(order.getPriorityDormitoryBuilding());
        return StringUtils.hasText(courierDormitoryBuilding)
                && StringUtils.hasText(priorityDormitoryBuilding)
                && priorityDormitoryBuilding.equals(courierDormitoryBuilding);
    }

    private String generateOrderId(LocalDateTime now) {
        return "CR" + now.format(ORDER_ID_TIME_FORMATTER) + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private String normalizeAfterSaleOrderStatusFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        String upper = normalized.toUpperCase();
        if (!CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(upper)
                && !CampusRelayOrderStatus.AFTER_SALE_RESOLVED.name().equals(upper)
                && !CampusRelayOrderStatus.AFTER_SALE_REJECTED.name().equals(upper)) {
            throw new BusinessException("售后查询状态仅支持 AFTER_SALE_OPEN、AFTER_SALE_RESOLVED、AFTER_SALE_REJECTED");
        }
        return upper;
    }

    private String normalizeAfterSaleHandleActionFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        return resolveAfterSaleHandleAction(normalized).name();
    }

    private String normalizeAfterSaleDecisionTypeFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        return resolveAfterSaleDecisionType(normalized).name();
    }

    private String normalizeAfterSaleExecutionStatusFilter(String value) {
        String normalized = normalizeText(value);
        if (!StringUtils.hasText(normalized)) {
            return null;
        }
        return resolveAfterSaleExecutionStatus(normalized).name();
    }

    private CampusAfterSaleHandleAction resolveAfterSaleHandleAction(String action) {
        try {
            return CampusAfterSaleHandleAction.valueOf(normalizeText(action).toUpperCase());
        } catch (Exception ex) {
            throw new BusinessException("不支持的售后处理动作");
        }
    }

    private CampusAfterSaleDecisionType resolveAfterSaleDecisionType(String decisionType) {
        try {
            return CampusAfterSaleDecisionType.valueOf(normalizeText(decisionType).toUpperCase());
        } catch (Exception ex) {
            throw new BusinessException("不支持的售后决策类型");
        }
    }

    private CampusAfterSaleExecutionStatus resolveAfterSaleExecutionStatus(String executionStatus) {
        try {
            return CampusAfterSaleExecutionStatus.valueOf(normalizeText(executionStatus).toUpperCase());
        } catch (Exception ex) {
            throw new BusinessException("不支持的售后执行状态");
        }
    }

    private BigDecimal normalizeDecisionAmount(BigDecimal decisionAmount, CampusAfterSaleDecisionType decisionType, BigDecimal totalAmount) {
        if (decisionType == CampusAfterSaleDecisionType.NONE) {
            if (decisionAmount != null) {
                throw new BusinessException("NONE 决策类型不允许传入金额");
            }
            return null;
        }
        if (decisionAmount == null) {
            throw new BusinessException("退款/补偿金额不能为空");
        }
        BigDecimal normalized = decisionAmount.setScale(2, RoundingMode.HALF_UP);
        if (normalized.compareTo(ZERO_AMOUNT) < 0) {
            throw new BusinessException("退款/补偿金额不能为负数");
        }
        assertWithinMoneyPrecision(normalized, "退款/补偿金额超出支持范围");
        if (decisionType == CampusAfterSaleDecisionType.REFUND) {
            BigDecimal normalizedTotalAmount = totalAmount == null
                    ? ZERO_AMOUNT
                    : totalAmount.setScale(2, RoundingMode.HALF_UP);
            if (normalized.compareTo(normalizedTotalAmount) > 0) {
                throw new BusinessException("退款金额不能超过订单总金额");
            }
        }
        return normalized;
    }

    private void assertWithinMoneyPrecision(BigDecimal amount, String message) {
        if (amount == null) {
            return;
        }
        if (amount.compareTo(MAX_DECIMAL_10_2) > 0) {
            throw new BusinessException(message);
        }
    }

    private CampusAfterSaleExecutionStatus resolveCurrentAfterSaleExecutionStatus(
            CampusRelayOrder order,
            CampusAfterSaleDecisionType decisionType
    ) {
        if (StringUtils.hasText(order.getAfterSaleExecutionStatus())) {
            return resolveAfterSaleExecutionStatus(order.getAfterSaleExecutionStatus());
        }
        return decisionType == CampusAfterSaleDecisionType.NONE
                ? CampusAfterSaleExecutionStatus.NOT_REQUIRED
                : CampusAfterSaleExecutionStatus.PENDING;
    }

    private void assertAfterSaleExecutionTransitionAllowed(
            CampusAfterSaleExecutionStatus currentStatus,
            CampusAfterSaleExecutionStatus targetStatus
    ) {
        if (currentStatus == CampusAfterSaleExecutionStatus.NOT_REQUIRED) {
            throw new BusinessException("当前订单无需记录售后执行结果");
        }
        if (currentStatus == CampusAfterSaleExecutionStatus.SUCCESS) {
            throw new BusinessException("当前订单已记录成功的售后执行结果");
        }
        if (currentStatus == CampusAfterSaleExecutionStatus.FAILED && targetStatus == CampusAfterSaleExecutionStatus.FAILED) {
            throw new BusinessException("当前订单已记录失败的售后执行结果");
        }
        if (currentStatus == CampusAfterSaleExecutionStatus.FAILED && targetStatus != CampusAfterSaleExecutionStatus.SUCCESS) {
            throw new BusinessException("失败的售后执行结果仅允许纠正为 SUCCESS");
        }
        if (currentStatus != CampusAfterSaleExecutionStatus.PENDING && currentStatus != CampusAfterSaleExecutionStatus.FAILED) {
            throw new BusinessException("当前订单售后执行状态不可更新");
        }
    }

    private String normalizeOptionalReferenceNo(String value) {
        String normalized = normalizeText(value);
        return StringUtils.hasText(normalized) ? normalized : null;
    }

    private boolean isAfterSaleTracked(CampusRelayOrderVO order) {
        if (order == null) {
            return false;
        }
        return CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getStatus())
                || CampusRelayOrderStatus.AFTER_SALE_RESOLVED.name().equals(order.getStatus())
                || CampusRelayOrderStatus.AFTER_SALE_REJECTED.name().equals(order.getStatus())
                || order.getAfterSaleAppliedAt() != null
                || StringUtils.hasText(order.getAfterSaleReason());
    }

    private boolean isAfterSaleTracked(CampusRelayOrder order) {
        if (order == null) {
            return false;
        }
        return CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getOrderStatus())
                || CampusRelayOrderStatus.AFTER_SALE_RESOLVED.name().equals(order.getOrderStatus())
                || CampusRelayOrderStatus.AFTER_SALE_REJECTED.name().equals(order.getOrderStatus())
                || order.getAfterSaleAppliedAt() != null
                || StringUtils.hasText(order.getAfterSaleReason());
    }

    private String resolveCustomerReceiptStatus(CampusRelayOrderVO order) {
        if (order == null) {
            return null;
        }
        if (CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getStatus())) {
            return "PROCESSING";
        }
        if (CampusRelayOrderStatus.AFTER_SALE_REJECTED.name().equals(order.getStatus())) {
            return "REJECTED";
        }
        if (!CampusRelayOrderStatus.AFTER_SALE_RESOLVED.name().equals(order.getStatus())) {
            throw new BusinessException("当前订单未进入售后结果阶段");
        }

        CampusAfterSaleExecutionStatus executionStatus = resolveCurrentAfterSaleExecutionStatusForView(order);
        return switch (executionStatus) {
            case NOT_REQUIRED, SUCCESS -> "COMPLETED";
            case PENDING -> "PROCESSING";
            case FAILED -> "NEED_MANUAL_FOLLOW_UP";
        };
    }

    private String resolveCustomerReceiptTitle(CampusRelayOrderVO order) {
        String receiptStatus = resolveCustomerReceiptStatus(order);
        return switch (receiptStatus) {
            case "PROCESSING" -> "售后处理中";
            case "REJECTED" -> "售后申请未通过";
            case "COMPLETED" -> "售后结果已确认";
            case "NEED_MANUAL_FOLLOW_UP" -> "售后处理需人工跟进";
            default -> "售后结果";
        };
    }

    private String resolveCustomerReceiptMessage(CampusRelayOrderVO order) {
        String receiptStatus = resolveCustomerReceiptStatus(order);
        if ("REJECTED".equals(receiptStatus)) {
            return StringUtils.hasText(order.getAfterSaleHandleRemark())
                    ? order.getAfterSaleHandleRemark()
                    : "售后申请未通过，请联系平台管理员";
        }
        if ("PROCESSING".equals(receiptStatus)) {
            if (CampusRelayOrderStatus.AFTER_SALE_OPEN.name().equals(order.getStatus())) {
                return "售后申请已提交，等待平台处理";
            }
            return "售后已处理，执行结果仍在跟进中";
        }
        if ("NEED_MANUAL_FOLLOW_UP".equals(receiptStatus)) {
            return StringUtils.hasText(order.getAfterSaleExecutionRemark())
                    ? order.getAfterSaleExecutionRemark()
                    : "售后执行失败，请等待平台人工跟进";
        }

        if (CampusAfterSaleDecisionType.NONE.name().equals(order.getAfterSaleDecisionType())) {
            return StringUtils.hasText(order.getAfterSaleDecisionRemark())
                    ? order.getAfterSaleDecisionRemark()
                    : "售后处理已完成，无需额外退款或补偿";
        }
        if (StringUtils.hasText(order.getAfterSaleExecutionRemark())) {
            return order.getAfterSaleExecutionRemark();
        }
        if (StringUtils.hasText(order.getAfterSaleDecisionRemark())) {
            return order.getAfterSaleDecisionRemark();
        }
        return "售后处理已完成";
    }

    private LocalDateTime resolveCustomerAfterSaleLastUpdatedAt(CampusRelayOrderVO order) {
        if (order == null) {
            return null;
        }
        if (order.getAfterSaleExecutionCorrectedAt() != null) {
            return order.getAfterSaleExecutionCorrectedAt();
        }
        if (order.getAfterSaleExecutedAt() != null) {
            return order.getAfterSaleExecutedAt();
        }
        if (order.getAfterSaleDecidedAt() != null) {
            return order.getAfterSaleDecidedAt();
        }
        if (order.getAfterSaleHandledAt() != null) {
            return order.getAfterSaleHandledAt();
        }
        if (order.getAfterSaleAppliedAt() != null) {
            return order.getAfterSaleAppliedAt();
        }
        return order.getUpdatedAt();
    }

    private CampusAfterSaleExecutionStatus resolveCurrentAfterSaleExecutionStatusForView(CampusRelayOrderVO order) {
        if (StringUtils.hasText(order.getAfterSaleExecutionStatus())) {
            return resolveAfterSaleExecutionStatus(order.getAfterSaleExecutionStatus());
        }
        if (CampusAfterSaleDecisionType.NONE.name().equals(order.getAfterSaleDecisionType())) {
            return CampusAfterSaleExecutionStatus.NOT_REQUIRED;
        }
        if (StringUtils.hasText(order.getAfterSaleDecisionType())) {
            return CampusAfterSaleExecutionStatus.PENDING;
        }
        return CampusAfterSaleExecutionStatus.PENDING;
    }

    private <T> PageResult<T> buildPageResult(List<T> records, Long total, int page, int pageSize) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setRecords(records);
        pageResult.setTotal(total);
        pageResult.setSize((long) pageSize);
        pageResult.setCurrent((long) page);
        pageResult.setPages((total + pageSize - 1) / pageSize);
        return pageResult;
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

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
    }
}

package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusRelayOrder;
import com.cangqiong.takeaway.campus.vo.CampusCourierOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusCustomerOrderVO;
import com.cangqiong.takeaway.campus.vo.CampusRelayOrderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CampusRelayOrderMapper {

    @Select("SELECT * FROM campus_relay_order WHERE id = #{id}")
    CampusRelayOrder selectEntityById(String id);

    @Select("SELECT * FROM campus_relay_order WHERE id = #{id} AND customer_user_id = #{customerUserId}")
    CampusRelayOrder selectEntityByIdAndCustomerUserId(@Param("id") String id, @Param("customerUserId") Long customerUserId);

    @Select("SELECT * FROM campus_relay_order WHERE id = #{id} AND courier_profile_id = #{courierProfileId}")
    CampusRelayOrder selectEntityByIdAndCourierProfileId(@Param("id") String id, @Param("courierProfileId") Long courierProfileId);

    @Select({
            "<script>",
            "SELECT",
            "  cro.id,",
            "  cro.customer_user_id,",
            "  cro.delivery_contact_name AS customer_name,",
            "  cro.delivery_contact_phone AS customer_phone,",
            "  cro.courier_profile_id,",
            "  cp.real_name AS courier_name,",
            "  cro.pickup_point_id,",
            "  pp.code AS pickup_point_code,",
            "  pp.name AS pickup_point_name,",
            "  cro.delivery_target_type,",
            "  cro.delivery_building,",
            "  cro.delivery_detail,",
            "  cro.food_description,",
            "  cro.external_platform_name,",
            "  cro.external_order_ref,",
            "  cro.pickup_code,",
            "  cro.base_fee,",
            "  cro.priority_fee,",
            "  cro.tip_fee,",
            "  cro.total_amount,",
            "  cro.payment_status,",
            "  cro.order_status AS status,",
            "  cro.priority_dormitory_building,",
            "  cro.pickup_proof_image_url,",
            "  cro.customer_remark,",
            "  cro.courier_remark,",
            "  cro.after_sale_reason,",
            "  cro.priority_window_deadline,",
            "  cro.accepted_at,",
            "  cro.cancel_locked_until,",
            "  cro.picked_up_at,",
            "  cro.delivered_at,",
            "  cro.auto_complete_at,",
            "  cro.created_at,",
            "  cro.updated_at",
            "FROM campus_relay_order cro",
            "LEFT JOIN campus_pickup_point pp ON cro.pickup_point_id = pp.id",
            "LEFT JOIN campus_courier_profile cp ON cro.courier_profile_id = cp.id",
            "WHERE cro.id = #{id}",
            "</script>"
    })
    CampusRelayOrderVO selectById(String id);

    @Select({
            "<script>",
            "SELECT",
            "  cro.id,",
            "  cro.customer_user_id,",
            "  cro.delivery_contact_name AS customer_name,",
            "  cro.delivery_contact_phone AS customer_phone,",
            "  cro.courier_profile_id,",
            "  cp.real_name AS courier_name,",
            "  cro.pickup_point_id,",
            "  pp.code AS pickup_point_code,",
            "  pp.name AS pickup_point_name,",
            "  cro.delivery_target_type,",
            "  cro.delivery_building,",
            "  cro.delivery_detail,",
            "  cro.food_description,",
            "  cro.external_platform_name,",
            "  cro.external_order_ref,",
            "  cro.pickup_code,",
            "  cro.base_fee,",
            "  cro.priority_fee,",
            "  cro.tip_fee,",
            "  cro.total_amount,",
            "  cro.payment_status,",
            "  cro.order_status AS status,",
            "  cro.priority_dormitory_building,",
            "  cro.priority_window_deadline,",
            "  cro.accepted_at,",
            "  cro.picked_up_at,",
            "  cro.delivered_at,",
            "  cro.auto_complete_at,",
            "  cro.created_at,",
            "  cro.updated_at",
            "FROM campus_relay_order cro",
            "LEFT JOIN campus_pickup_point pp ON cro.pickup_point_id = pp.id",
            "LEFT JOIN campus_customer_profile ccp ON cro.customer_user_id = ccp.user_id",
            "LEFT JOIN campus_courier_profile cp ON cro.courier_profile_id = cp.id",
            "<where>",
            "  <if test='orderNo != null and orderNo != \"\"'>AND cro.id LIKE CONCAT('%', #{orderNo}, '%')</if>",
            "  <if test='customerName != null and customerName != \"\"'>AND (cro.delivery_contact_name LIKE CONCAT('%', #{customerName}, '%') OR ccp.real_name LIKE CONCAT('%', #{customerName}, '%'))</if>",
            "  <if test='courierName != null and courierName != \"\"'>AND cp.real_name LIKE CONCAT('%', #{courierName}, '%')</if>",
            "  <if test='status != null and status != \"\"'>AND cro.order_status = #{status}</if>",
            "  <if test='paymentStatus != null and paymentStatus != \"\"'>AND cro.payment_status = #{paymentStatus}</if>",
            "  <if test='deliveryTargetType != null and deliveryTargetType != \"\"'>AND cro.delivery_target_type = #{deliveryTargetType}</if>",
            "  <if test='deliveryBuilding != null and deliveryBuilding != \"\"'>AND cro.delivery_building LIKE CONCAT('%', #{deliveryBuilding}, '%')</if>",
            "  <if test='pickupPointId != null'>AND cro.pickup_point_id = #{pickupPointId}</if>",
            "  <if test='beginTime != null'>AND cro.created_at &gt;= #{beginTime}</if>",
            "  <if test='endTime != null'>AND cro.created_at &lt;= #{endTime}</if>",
            "</where>",
            "ORDER BY cro.created_at DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusRelayOrderVO> selectByCondition(
            @Param("orderNo") String orderNo,
            @Param("customerName") String customerName,
            @Param("courierName") String courierName,
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("deliveryTargetType") String deliveryTargetType,
            @Param("deliveryBuilding") String deliveryBuilding,
            @Param("pickupPointId") Long pickupPointId,
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*)",
            "FROM campus_relay_order cro",
            "LEFT JOIN campus_customer_profile ccp ON cro.customer_user_id = ccp.user_id",
            "LEFT JOIN campus_courier_profile cp ON cro.courier_profile_id = cp.id",
            "<where>",
            "  <if test='orderNo != null and orderNo != \"\"'>AND cro.id LIKE CONCAT('%', #{orderNo}, '%')</if>",
            "  <if test='customerName != null and customerName != \"\"'>AND (cro.delivery_contact_name LIKE CONCAT('%', #{customerName}, '%') OR ccp.real_name LIKE CONCAT('%', #{customerName}, '%'))</if>",
            "  <if test='courierName != null and courierName != \"\"'>AND cp.real_name LIKE CONCAT('%', #{courierName}, '%')</if>",
            "  <if test='status != null and status != \"\"'>AND cro.order_status = #{status}</if>",
            "  <if test='paymentStatus != null and paymentStatus != \"\"'>AND cro.payment_status = #{paymentStatus}</if>",
            "  <if test='deliveryTargetType != null and deliveryTargetType != \"\"'>AND cro.delivery_target_type = #{deliveryTargetType}</if>",
            "  <if test='deliveryBuilding != null and deliveryBuilding != \"\"'>AND cro.delivery_building LIKE CONCAT('%', #{deliveryBuilding}, '%')</if>",
            "  <if test='pickupPointId != null'>AND cro.pickup_point_id = #{pickupPointId}</if>",
            "  <if test='beginTime != null'>AND cro.created_at &gt;= #{beginTime}</if>",
            "  <if test='endTime != null'>AND cro.created_at &lt;= #{endTime}</if>",
            "</where>",
            "</script>"
    })
    Long countByCondition(
            @Param("orderNo") String orderNo,
            @Param("customerName") String customerName,
            @Param("courierName") String courierName,
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("deliveryTargetType") String deliveryTargetType,
            @Param("deliveryBuilding") String deliveryBuilding,
            @Param("pickupPointId") Long pickupPointId,
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Select({
            "<script>",
            "SELECT",
            "  cro.id,",
            "  cro.pickup_point_id,",
            "  pp.code AS pickup_point_code,",
            "  pp.name AS pickup_point_name,",
            "  cro.delivery_target_type,",
            "  cro.delivery_building,",
            "  cro.delivery_detail,",
            "  cro.delivery_contact_name AS contact_name,",
            "  cro.delivery_contact_phone AS contact_phone,",
            "  cro.food_description,",
            "  cro.external_platform_name,",
            "  cro.external_order_ref,",
            "  cro.pickup_code,",
            "  cro.base_fee,",
            "  cro.priority_fee,",
            "  cro.tip_fee,",
            "  cro.total_amount,",
            "  cro.payment_status,",
            "  cro.order_status AS status,",
            "  cro.courier_profile_id,",
            "  cro.priority_dormitory_building,",
            "  cro.priority_window_deadline,",
            "  cro.accepted_at,",
            "  cro.created_at,",
            "  cro.updated_at",
            "FROM campus_relay_order cro",
            "LEFT JOIN campus_pickup_point pp ON cro.pickup_point_id = pp.id",
            "<where>",
            "  AND cro.payment_status = 'PAID'",
            "  AND cro.courier_profile_id IS NULL",
            "  AND cro.order_status IN ('WAITING_ACCEPT', 'BUILDING_PRIORITY_PENDING')",
            "  <if test='orderNo != null and orderNo != \"\"'>AND cro.id LIKE CONCAT('%', #{orderNo}, '%')</if>",
            "  <if test='deliveryTargetType != null and deliveryTargetType != \"\"'>AND cro.delivery_target_type = #{deliveryTargetType}</if>",
            "  <if test='deliveryBuilding != null and deliveryBuilding != \"\"'>AND cro.delivery_building LIKE CONCAT('%', #{deliveryBuilding}, '%')</if>",
            "  <if test='pickupPointId != null'>AND cro.pickup_point_id = #{pickupPointId}</if>",
            "</where>",
            "ORDER BY cro.created_at DESC",
            "</script>"
    })
    List<CampusCourierOrderVO> selectAvailableForCourier(
            @Param("orderNo") String orderNo,
            @Param("deliveryTargetType") String deliveryTargetType,
            @Param("deliveryBuilding") String deliveryBuilding,
            @Param("pickupPointId") Long pickupPointId
    );

    @Select({
            "SELECT",
            "  cro.id,",
            "  cro.pickup_point_id,",
            "  pp.code AS pickup_point_code,",
            "  pp.name AS pickup_point_name,",
            "  cro.delivery_target_type,",
            "  cro.delivery_building,",
            "  cro.delivery_detail,",
            "  cro.delivery_contact_name AS contact_name,",
            "  cro.delivery_contact_phone AS contact_phone,",
            "  cro.food_description,",
            "  cro.external_platform_name,",
            "  cro.external_order_ref,",
            "  cro.pickup_code,",
            "  cro.base_fee,",
            "  cro.priority_fee,",
            "  cro.tip_fee,",
            "  cro.total_amount,",
            "  cro.payment_status,",
            "  cro.order_status AS status,",
            "  cro.courier_profile_id,",
            "  cro.priority_dormitory_building,",
            "  cro.pickup_proof_image_url,",
            "  cro.customer_remark,",
            "  cro.courier_remark,",
            "  cro.priority_window_deadline,",
            "  cro.accepted_at,",
            "  cro.picked_up_at,",
            "  cro.delivered_at,",
            "  cro.auto_complete_at,",
            "  cro.created_at,",
            "  cro.updated_at",
            "FROM campus_relay_order cro",
            "LEFT JOIN campus_pickup_point pp ON cro.pickup_point_id = pp.id",
            "WHERE cro.id = #{id}"
    })
    CampusCourierOrderVO selectCourierById(String id);

    @Select({
            "<script>",
            "SELECT",
            "  cro.id,",
            "  cro.pickup_point_id,",
            "  pp.code AS pickup_point_code,",
            "  pp.name AS pickup_point_name,",
            "  cro.delivery_target_type,",
            "  cro.delivery_building,",
            "  cro.delivery_detail,",
            "  cro.delivery_contact_name AS contact_name,",
            "  cro.delivery_contact_phone AS contact_phone,",
            "  cro.food_description,",
            "  cro.external_platform_name,",
            "  cro.external_order_ref,",
            "  cro.pickup_code,",
            "  cro.customer_remark AS remark,",
            "  cro.base_fee,",
            "  cro.priority_fee,",
            "  cro.tip_fee,",
            "  cro.total_amount,",
            "  cro.payment_status,",
            "  cro.order_status AS status,",
            "  cro.courier_profile_id,",
            "  cp.real_name AS courier_name,",
            "  cro.priority_dormitory_building,",
            "  cro.pickup_proof_image_url,",
            "  cro.courier_remark,",
            "  cro.priority_window_deadline,",
            "  cro.accepted_at,",
            "  cro.picked_up_at,",
            "  cro.delivered_at,",
            "  cro.auto_complete_at,",
            "  cro.created_at,",
            "  cro.updated_at",
            "FROM campus_relay_order cro",
            "LEFT JOIN campus_pickup_point pp ON cro.pickup_point_id = pp.id",
            "LEFT JOIN campus_courier_profile cp ON cro.courier_profile_id = cp.id",
            "WHERE cro.id = #{id} AND cro.customer_user_id = #{customerUserId}",
            "</script>"
    })
    CampusCustomerOrderVO selectCustomerById(
            @Param("id") String id,
            @Param("customerUserId") Long customerUserId
    );

    @Select({
            "<script>",
            "SELECT",
            "  cro.id,",
            "  cro.pickup_point_id,",
            "  pp.code AS pickup_point_code,",
            "  pp.name AS pickup_point_name,",
            "  cro.delivery_target_type,",
            "  cro.delivery_building,",
            "  cro.delivery_detail,",
            "  cro.delivery_contact_name AS contact_name,",
            "  cro.delivery_contact_phone AS contact_phone,",
            "  cro.food_description,",
            "  cro.external_platform_name,",
            "  cro.external_order_ref,",
            "  cro.pickup_code,",
            "  cro.customer_remark AS remark,",
            "  cro.base_fee,",
            "  cro.priority_fee,",
            "  cro.tip_fee,",
            "  cro.total_amount,",
            "  cro.payment_status,",
            "  cro.order_status AS status,",
            "  cro.courier_profile_id,",
            "  cp.real_name AS courier_name,",
            "  cro.priority_dormitory_building,",
            "  cro.pickup_proof_image_url,",
            "  cro.courier_remark,",
            "  cro.priority_window_deadline,",
            "  cro.accepted_at,",
            "  cro.picked_up_at,",
            "  cro.delivered_at,",
            "  cro.auto_complete_at,",
            "  cro.created_at,",
            "  cro.updated_at",
            "FROM campus_relay_order cro",
            "LEFT JOIN campus_pickup_point pp ON cro.pickup_point_id = pp.id",
            "LEFT JOIN campus_courier_profile cp ON cro.courier_profile_id = cp.id",
            "<where>",
            "  AND cro.customer_user_id = #{customerUserId}",
            "  <if test='orderNo != null and orderNo != \"\"'>AND cro.id LIKE CONCAT('%', #{orderNo}, '%')</if>",
            "  <if test='status != null and status != \"\"'>AND cro.order_status = #{status}</if>",
            "  <if test='paymentStatus != null and paymentStatus != \"\"'>AND cro.payment_status = #{paymentStatus}</if>",
            "  <if test='deliveryTargetType != null and deliveryTargetType != \"\"'>AND cro.delivery_target_type = #{deliveryTargetType}</if>",
            "</where>",
            "ORDER BY cro.created_at DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusCustomerOrderVO> selectByCustomerCondition(
            @Param("customerUserId") Long customerUserId,
            @Param("orderNo") String orderNo,
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("deliveryTargetType") String deliveryTargetType,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*)",
            "FROM campus_relay_order cro",
            "<where>",
            "  AND cro.customer_user_id = #{customerUserId}",
            "  <if test='orderNo != null and orderNo != \"\"'>AND cro.id LIKE CONCAT('%', #{orderNo}, '%')</if>",
            "  <if test='status != null and status != \"\"'>AND cro.order_status = #{status}</if>",
            "  <if test='paymentStatus != null and paymentStatus != \"\"'>AND cro.payment_status = #{paymentStatus}</if>",
            "  <if test='deliveryTargetType != null and deliveryTargetType != \"\"'>AND cro.delivery_target_type = #{deliveryTargetType}</if>",
            "</where>",
            "</script>"
    })
    Long countByCustomerCondition(
            @Param("customerUserId") Long customerUserId,
            @Param("orderNo") String orderNo,
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("deliveryTargetType") String deliveryTargetType
    );

    @Insert("INSERT INTO campus_relay_order (" +
            "id, customer_user_id, courier_profile_id, pickup_point_id, delivery_target_type, delivery_building, delivery_detail, " +
            "delivery_contact_name, delivery_contact_phone, food_description, external_platform_name, external_order_ref, pickup_code, " +
            "base_fee, priority_fee, tip_fee, total_amount, payment_status, order_status, priority_dormitory_building, " +
            "priority_window_deadline, accepted_at, cancel_locked_until, picked_up_at, delivered_at, auto_complete_at, " +
            "pickup_proof_image_url, customer_remark, courier_remark, after_sale_reason, created_at, updated_at" +
            ") VALUES (" +
            "#{id}, #{customerUserId}, #{courierProfileId}, #{pickupPointId}, #{deliveryTargetType}, #{deliveryBuilding}, #{deliveryDetail}, " +
            "#{deliveryContactName}, #{deliveryContactPhone}, #{foodDescription}, #{externalPlatformName}, #{externalOrderRef}, #{pickupCode}, " +
            "#{baseFee}, #{priorityFee}, #{tipFee}, #{totalAmount}, #{paymentStatus}, #{orderStatus}, #{priorityDormitoryBuilding}, " +
            "#{priorityWindowDeadline}, #{acceptedAt}, #{cancelLockedUntil}, #{pickedUpAt}, #{deliveredAt}, #{autoCompleteAt}, " +
            "#{pickupProofImageUrl}, #{customerRemark}, #{courierRemark}, #{afterSaleReason}, #{createdAt}, #{updatedAt}" +
            ")")
    void insert(CampusRelayOrder order);

    @Update("UPDATE campus_relay_order SET " +
            "payment_status = #{paymentStatus}, " +
            "order_status = #{orderStatus}, " +
            "priority_dormitory_building = #{priorityDormitoryBuilding}, " +
            "priority_window_deadline = #{priorityWindowDeadline}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateAfterMockPay(
            @Param("id") String id,
            @Param("paymentStatus") String paymentStatus,
            @Param("orderStatus") String orderStatus,
            @Param("priorityDormitoryBuilding") String priorityDormitoryBuilding,
            @Param("priorityWindowDeadline") LocalDateTime priorityWindowDeadline,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_relay_order SET " +
            "courier_profile_id = #{courierProfileId}, " +
            "order_status = #{orderStatus}, " +
            "accepted_at = #{acceptedAt}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} " +
            "AND payment_status = 'PAID' " +
            "AND courier_profile_id IS NULL " +
            "AND order_status IN ('WAITING_ACCEPT', 'BUILDING_PRIORITY_PENDING')")
    int acceptOrder(
            @Param("id") String id,
            @Param("courierProfileId") Long courierProfileId,
            @Param("orderStatus") String orderStatus,
            @Param("acceptedAt") LocalDateTime acceptedAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_relay_order SET " +
            "order_status = #{orderStatus}, " +
            "pickup_proof_image_url = #{pickupProofImageUrl}, " +
            "courier_remark = #{courierRemark}, " +
            "picked_up_at = #{pickedUpAt}, " +
            "cancel_locked_until = #{cancelLockedUntil}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} " +
            "AND courier_profile_id = #{courierProfileId} " +
            "AND order_status = 'ACCEPTED'")
    int pickupOrder(
            @Param("id") String id,
            @Param("courierProfileId") Long courierProfileId,
            @Param("orderStatus") String orderStatus,
            @Param("pickupProofImageUrl") String pickupProofImageUrl,
            @Param("courierRemark") String courierRemark,
            @Param("pickedUpAt") LocalDateTime pickedUpAt,
            @Param("cancelLockedUntil") LocalDateTime cancelLockedUntil,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_relay_order SET " +
            "order_status = #{orderStatus}, " +
            "courier_remark = #{courierRemark}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} " +
            "AND courier_profile_id = #{courierProfileId} " +
            "AND order_status = 'PICKED_UP'")
    int startDelivering(
            @Param("id") String id,
            @Param("courierProfileId") Long courierProfileId,
            @Param("orderStatus") String orderStatus,
            @Param("courierRemark") String courierRemark,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_relay_order SET " +
            "order_status = #{orderStatus}, " +
            "courier_remark = #{courierRemark}, " +
            "delivered_at = #{deliveredAt}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} " +
            "AND courier_profile_id = #{courierProfileId} " +
            "AND order_status = 'DELIVERING'")
    int markDelivered(
            @Param("id") String id,
            @Param("courierProfileId") Long courierProfileId,
            @Param("orderStatus") String orderStatus,
            @Param("courierRemark") String courierRemark,
            @Param("deliveredAt") LocalDateTime deliveredAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_relay_order SET " +
            "order_status = #{orderStatus}, " +
            "auto_complete_at = #{autoCompleteAt}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} " +
            "AND customer_user_id = #{customerUserId} " +
            "AND order_status = 'AWAITING_CONFIRMATION'")
    int confirmDeliveredByCustomer(
            @Param("id") String id,
            @Param("customerUserId") Long customerUserId,
            @Param("orderStatus") String orderStatus,
            @Param("autoCompleteAt") LocalDateTime autoCompleteAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}

package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusSettlementRecord;
import com.cangqiong.takeaway.campus.vo.CampusSettlementPayoutBatchVO;
import com.cangqiong.takeaway.campus.vo.CampusSettlementRecordVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CampusSettlementRecordMapper {

    @Select("SELECT * FROM campus_settlement_record WHERE id = #{id}")
    CampusSettlementRecord selectById(Long id);

    @Select("SELECT * FROM campus_settlement_record WHERE relay_order_id = #{relayOrderId}")
    CampusSettlementRecord selectByRelayOrderId(String relayOrderId);

    @Insert("INSERT INTO campus_settlement_record (" +
            "relay_order_id, courier_profile_id, gross_amount, platform_commission, pending_amount, settlement_status, " +
            "payout_status, payout_remark, payout_reference_no, payout_recorded_by_employee_id, payout_recorded_at, " +
            "settled_at, remark, created_at, updated_at" +
            ") VALUES (" +
            "#{relayOrderId}, #{courierProfileId}, #{grossAmount}, #{platformCommission}, #{pendingAmount}, #{settlementStatus}, " +
            "#{payoutStatus}, #{payoutRemark}, #{payoutReferenceNo}, #{payoutRecordedByEmployeeId}, #{payoutRecordedAt}, " +
            "#{settledAt}, #{remark}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusSettlementRecord record);

    @Update("UPDATE campus_settlement_record SET " +
            "courier_profile_id = #{courierProfileId}, " +
            "gross_amount = #{grossAmount}, " +
            "platform_commission = #{platformCommission}, " +
            "pending_amount = #{pendingAmount}, " +
            "remark = #{remark}, " +
            "updated_at = #{updatedAt}, " +
            "settlement_status = CASE WHEN settlement_status = 'SETTLED' THEN settlement_status ELSE #{settlementStatus} END, " +
            "settled_at = CASE WHEN settlement_status = 'SETTLED' THEN settled_at ELSE NULL END " +
            "WHERE relay_order_id = #{relayOrderId}")
    int updateSnapshotForCompleted(
            @Param("relayOrderId") String relayOrderId,
            @Param("courierProfileId") Long courierProfileId,
            @Param("grossAmount") BigDecimal grossAmount,
            @Param("platformCommission") BigDecimal platformCommission,
            @Param("pendingAmount") BigDecimal pendingAmount,
            @Param("settlementStatus") String settlementStatus,
            @Param("remark") String remark,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_settlement_record SET " +
            "settlement_status = #{settlementStatus}, " +
            "settled_at = #{settledAt}, " +
            "payout_status = #{payoutStatus}, " +
            "payout_batch_no = NULL, " +
            "payout_remark = NULL, " +
            "payout_reference_no = NULL, " +
            "payout_recorded_by_employee_id = NULL, " +
            "payout_recorded_at = NULL, " +
            "payout_verified = 0, " +
            "payout_verified_by_employee_id = NULL, " +
            "payout_verified_at = NULL, " +
            "payout_verify_remark = NULL, " +
            "remark = #{remark}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND settlement_status = 'PENDING'")
    int confirmByAdmin(
            @Param("id") Long id,
            @Param("settlementStatus") String settlementStatus,
            @Param("settledAt") LocalDateTime settledAt,
            @Param("payoutStatus") String payoutStatus,
            @Param("remark") String remark,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_settlement_record SET " +
            "payout_status = #{payoutStatus}, " +
            "payout_batch_no = #{payoutBatchNo}, " +
            "payout_remark = #{payoutRemark}, " +
            "payout_reference_no = #{payoutReferenceNo}, " +
            "payout_recorded_by_employee_id = #{payoutRecordedByEmployeeId}, " +
            "payout_recorded_at = #{payoutRecordedAt}, " +
            "payout_verified = 0, " +
            "payout_verified_by_employee_id = NULL, " +
            "payout_verified_at = NULL, " +
            "payout_verify_remark = NULL, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} " +
            "AND settlement_status = 'SETTLED' " +
            "AND (payout_status = #{currentPayoutStatus} OR (#{currentPayoutStatus} = 'UNPAID' AND payout_status IS NULL))")
    int recordPayoutResultByAdmin(
            @Param("id") Long id,
            @Param("currentPayoutStatus") String currentPayoutStatus,
            @Param("payoutStatus") String payoutStatus,
            @Param("payoutBatchNo") String payoutBatchNo,
            @Param("payoutRemark") String payoutRemark,
            @Param("payoutReferenceNo") String payoutReferenceNo,
            @Param("payoutRecordedByEmployeeId") Long payoutRecordedByEmployeeId,
            @Param("payoutRecordedAt") LocalDateTime payoutRecordedAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Update("UPDATE campus_settlement_record SET " +
            "payout_verified = 1, " +
            "payout_verified_by_employee_id = #{payoutVerifiedByEmployeeId}, " +
            "payout_verified_at = #{payoutVerifiedAt}, " +
            "payout_verify_remark = #{payoutVerifyRemark}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} " +
            "AND settlement_status = 'SETTLED' " +
            "AND payout_status = 'PAID' " +
            "AND (payout_verified = 0 OR payout_verified IS NULL)")
    int verifyPayoutByAdmin(
            @Param("id") Long id,
            @Param("payoutVerifiedByEmployeeId") Long payoutVerifiedByEmployeeId,
            @Param("payoutVerifiedAt") LocalDateTime payoutVerifiedAt,
            @Param("payoutVerifyRemark") String payoutVerifyRemark,
            @Param("updatedAt") LocalDateTime updatedAt
    );

    @Select({
            "<script>",
            "SELECT id, relay_order_id, courier_profile_id, gross_amount, platform_commission, pending_amount,",
            "settlement_status, payout_status, payout_batch_no, payout_remark, payout_reference_no, payout_recorded_by_employee_id, payout_recorded_at,",
            "payout_verified, payout_verified_by_employee_id, payout_verified_at, payout_verify_remark,",
            "settled_at, remark, created_at, updated_at",
            "FROM campus_settlement_record",
            "<where>",
            "  <if test='settlementStatus != null and settlementStatus != \"\"'>AND settlement_status = #{settlementStatus}</if>",
            "  <if test='payoutStatus != null and payoutStatus != \"\"'>AND payout_status = #{payoutStatus}</if>",
            "  <if test='payoutVerified != null'>AND payout_verified = #{payoutVerified}</if>",
            "  <if test='payoutBatchNo != null and payoutBatchNo != \"\"'>AND payout_batch_no = #{payoutBatchNo}</if>",
            "  <if test='courierProfileId != null'>AND courier_profile_id = #{courierProfileId}</if>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND relay_order_id = #{relayOrderId}</if>",
            "</where>",
            "ORDER BY created_at DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusSettlementRecordVO> selectByCondition(
            @Param("settlementStatus") String settlementStatus,
            @Param("payoutStatus") String payoutStatus,
            @Param("payoutVerified") Integer payoutVerified,
            @Param("payoutBatchNo") String payoutBatchNo,
            @Param("courierProfileId") Long courierProfileId,
            @Param("relayOrderId") String relayOrderId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM campus_settlement_record",
            "<where>",
            "  <if test='settlementStatus != null and settlementStatus != \"\"'>AND settlement_status = #{settlementStatus}</if>",
            "  <if test='payoutStatus != null and payoutStatus != \"\"'>AND payout_status = #{payoutStatus}</if>",
            "  <if test='payoutVerified != null'>AND payout_verified = #{payoutVerified}</if>",
            "  <if test='payoutBatchNo != null and payoutBatchNo != \"\"'>AND payout_batch_no = #{payoutBatchNo}</if>",
            "  <if test='courierProfileId != null'>AND courier_profile_id = #{courierProfileId}</if>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND relay_order_id = #{relayOrderId}</if>",
            "</where>",
            "</script>"
    })
    Long countByCondition(
            @Param("settlementStatus") String settlementStatus,
            @Param("payoutStatus") String payoutStatus,
            @Param("payoutVerified") Integer payoutVerified,
            @Param("payoutBatchNo") String payoutBatchNo,
            @Param("courierProfileId") Long courierProfileId,
            @Param("relayOrderId") String relayOrderId
    );

    @Select({
            "<script>",
            "SELECT",
            "  COUNT(*) AS total_count,",
            "  SUM(CASE WHEN payout_status = 'UNPAID' THEN 1 ELSE 0 END) AS pending_payout_count,",
            "  SUM(CASE WHEN payout_status = 'PAID' THEN 1 ELSE 0 END) AS paid_count,",
            "  SUM(CASE WHEN payout_status = 'FAILED' THEN 1 ELSE 0 END) AS failed_payout_count,",
            "  COALESCE(SUM(CASE WHEN payout_status = 'UNPAID' THEN pending_amount ELSE 0 END), 0) AS total_pending_amount,",
            "  COALESCE(SUM(CASE WHEN payout_status = 'PAID' THEN pending_amount ELSE 0 END), 0) AS total_paid_amount,",
            "  COALESCE(SUM(CASE WHEN payout_status = 'FAILED' THEN pending_amount ELSE 0 END), 0) AS total_failed_amount",
            "FROM campus_settlement_record",
            "<where>",
            "  <if test='settlementStatus != null and settlementStatus != \"\"'>AND settlement_status = #{settlementStatus}</if>",
            "  <if test='payoutStatus != null and payoutStatus != \"\"'>AND payout_status = #{payoutStatus}</if>",
            "  <if test='courierProfileId != null'>AND courier_profile_id = #{courierProfileId}</if>",
            "</where>",
            "</script>"
    })
    com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileSummaryVO selectReconcileSummary(
            @Param("settlementStatus") String settlementStatus,
            @Param("payoutStatus") String payoutStatus,
            @Param("courierProfileId") Long courierProfileId
    );

    @Select({
            "<script>",
            "SELECT",
            "  payout_batch_no,",
            "  COUNT(*) AS total_count,",
            "  SUM(CASE WHEN payout_status = 'PAID' THEN 1 ELSE 0 END) AS paid_count,",
            "  SUM(CASE WHEN payout_status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count,",
            "  SUM(CASE WHEN payout_verified = 1 THEN 1 ELSE 0 END) AS verified_count,",
            "  SUM(CASE WHEN payout_verified = 1 THEN 0 ELSE 1 END) AS unverified_count,",
            "  COALESCE(SUM(pending_amount), 0) AS total_pending_amount,",
            "  MIN(payout_recorded_at) AS first_recorded_at,",
            "  MAX(payout_recorded_at) AS last_recorded_at",
            "FROM campus_settlement_record",
            "<where>",
            "  AND payout_batch_no IS NOT NULL",
            "  <if test='payoutStatus != null and payoutStatus != \"\"'>AND payout_status = #{payoutStatus}</if>",
            "  <if test='payoutVerified != null'>AND payout_verified = #{payoutVerified}</if>",
            "  <if test='courierProfileId != null'>AND courier_profile_id = #{courierProfileId}</if>",
            "</where>",
            "GROUP BY payout_batch_no",
            "ORDER BY MAX(payout_recorded_at) DESC, payout_batch_no DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusSettlementPayoutBatchVO> selectPayoutBatchPage(
            @Param("payoutStatus") String payoutStatus,
            @Param("payoutVerified") Integer payoutVerified,
            @Param("courierProfileId") Long courierProfileId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM (",
            "  SELECT payout_batch_no",
            "  FROM campus_settlement_record",
            "  <where>",
            "    AND payout_batch_no IS NOT NULL",
            "    <if test='payoutStatus != null and payoutStatus != \"\"'>AND payout_status = #{payoutStatus}</if>",
            "    <if test='payoutVerified != null'>AND payout_verified = #{payoutVerified}</if>",
            "    <if test='courierProfileId != null'>AND courier_profile_id = #{courierProfileId}</if>",
            "  </where>",
            "  GROUP BY payout_batch_no",
            ") t",
            "</script>"
    })
    Long countPayoutBatchPage(
            @Param("payoutStatus") String payoutStatus,
            @Param("payoutVerified") Integer payoutVerified,
            @Param("courierProfileId") Long courierProfileId
    );

    @Select({
            "SELECT",
            "  payout_batch_no,",
            "  COUNT(*) AS total_count,",
            "  SUM(CASE WHEN payout_status = 'PAID' THEN 1 ELSE 0 END) AS paid_count,",
            "  SUM(CASE WHEN payout_status = 'FAILED' THEN 1 ELSE 0 END) AS failed_count,",
            "  SUM(CASE WHEN payout_verified = 1 THEN 1 ELSE 0 END) AS verified_count,",
            "  SUM(CASE WHEN payout_verified = 1 THEN 0 ELSE 1 END) AS unverified_count,",
            "  COALESCE(SUM(pending_amount), 0) AS total_pending_amount,",
            "  MIN(payout_recorded_at) AS first_recorded_at,",
            "  MAX(payout_recorded_at) AS last_recorded_at",
            "FROM campus_settlement_record",
            "WHERE payout_batch_no = #{payoutBatchNo}",
            "GROUP BY payout_batch_no"
    })
    CampusSettlementPayoutBatchVO selectPayoutBatchSummaryByBatchNo(@Param("payoutBatchNo") String payoutBatchNo);

    @Select({
            "SELECT id, relay_order_id, courier_profile_id, gross_amount, platform_commission, pending_amount,",
            "settlement_status, payout_status, payout_batch_no, payout_remark, payout_reference_no, payout_recorded_by_employee_id, payout_recorded_at,",
            "payout_verified, payout_verified_by_employee_id, payout_verified_at, payout_verify_remark,",
            "settled_at, remark, created_at, updated_at",
            "FROM campus_settlement_record",
            "WHERE payout_batch_no = #{payoutBatchNo}",
            "ORDER BY payout_recorded_at DESC, id DESC"
    })
    List<CampusSettlementRecordVO> selectByPayoutBatchNo(@Param("payoutBatchNo") String payoutBatchNo);
}

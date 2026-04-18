package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusSettlementReconcileDifferenceRecord;
import com.cangqiong.takeaway.campus.vo.CampusSettlementReconcileDifferenceRecordVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CampusSettlementReconcileDifferenceRecordMapper {

    @Insert("INSERT INTO campus_settlement_reconcile_difference_record (" +
            "payout_batch_no, settlement_record_id, relay_order_id, courier_profile_id, difference_type, " +
            "expected_amount, actual_amount, difference_remark, process_status, process_result, process_remark, " +
            "reported_by_employee_id, reported_at, processed_by_employee_id, processed_at, source, created_at, updated_at" +
            ") VALUES (" +
            "#{payoutBatchNo}, #{settlementRecordId}, #{relayOrderId}, #{courierProfileId}, #{differenceType}, " +
            "#{expectedAmount}, #{actualAmount}, #{differenceRemark}, #{processStatus}, #{processResult}, #{processRemark}, " +
            "#{reportedByEmployeeId}, #{reportedAt}, #{processedByEmployeeId}, #{processedAt}, #{source}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusSettlementReconcileDifferenceRecord record);

    @Select({
            "<script>",
            "SELECT",
            "  csrd.id,",
            "  csrd.payout_batch_no,",
            "  csrd.settlement_record_id,",
            "  csrd.relay_order_id,",
            "  csrd.courier_profile_id,",
            "  csrd.difference_type,",
            "  csrd.expected_amount,",
            "  csrd.actual_amount,",
            "  csrd.difference_remark,",
            "  csrd.process_status,",
            "  csrd.process_result,",
            "  csrd.process_remark,",
            "  csrd.reported_by_employee_id,",
            "  csrd.reported_at,",
            "  csrd.processed_by_employee_id,",
            "  csrd.processed_at,",
            "  csrd.source,",
            "  csr.settlement_status,",
            "  csr.payout_status,",
            "  csr.payout_batch_no AS current_payout_batch_no,",
            "  csr.payout_remark,",
            "  csr.payout_reference_no,",
            "  csr.payout_verified,",
            "  csr.payout_verify_remark,",
            "  csr.pending_amount,",
            "  csr.payout_recorded_at,",
            "  csr.payout_verified_at,",
            "  csrd.created_at,",
            "  csrd.updated_at",
            "FROM campus_settlement_reconcile_difference_record csrd",
            "LEFT JOIN campus_settlement_record csr ON csrd.settlement_record_id = csr.id",
            "<where>",
            "  <if test='payoutBatchNo != null and payoutBatchNo != \"\"'>AND csrd.payout_batch_no = #{payoutBatchNo}</if>",
            "  <if test='settlementRecordId != null'>AND csrd.settlement_record_id = #{settlementRecordId}</if>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND csrd.relay_order_id = #{relayOrderId}</if>",
            "  <if test='differenceType != null and differenceType != \"\"'>AND csrd.difference_type = #{differenceType}</if>",
            "  <if test='processStatus != null and processStatus != \"\"'>AND csrd.process_status = #{processStatus}</if>",
            "</where>",
            "ORDER BY csrd.reported_at DESC, csrd.id DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusSettlementReconcileDifferenceRecordVO> selectByCondition(
            @Param("payoutBatchNo") String payoutBatchNo,
            @Param("settlementRecordId") Long settlementRecordId,
            @Param("relayOrderId") String relayOrderId,
            @Param("differenceType") String differenceType,
            @Param("processStatus") String processStatus,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM campus_settlement_reconcile_difference_record csrd",
            "<where>",
            "  <if test='payoutBatchNo != null and payoutBatchNo != \"\"'>AND csrd.payout_batch_no = #{payoutBatchNo}</if>",
            "  <if test='settlementRecordId != null'>AND csrd.settlement_record_id = #{settlementRecordId}</if>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND csrd.relay_order_id = #{relayOrderId}</if>",
            "  <if test='differenceType != null and differenceType != \"\"'>AND csrd.difference_type = #{differenceType}</if>",
            "  <if test='processStatus != null and processStatus != \"\"'>AND csrd.process_status = #{processStatus}</if>",
            "</where>",
            "</script>"
    })
    Long countByCondition(
            @Param("payoutBatchNo") String payoutBatchNo,
            @Param("settlementRecordId") Long settlementRecordId,
            @Param("relayOrderId") String relayOrderId,
            @Param("differenceType") String differenceType,
            @Param("processStatus") String processStatus
    );

    @Select({
            "SELECT",
            "  csrd.id,",
            "  csrd.payout_batch_no,",
            "  csrd.settlement_record_id,",
            "  csrd.relay_order_id,",
            "  csrd.courier_profile_id,",
            "  csrd.difference_type,",
            "  csrd.expected_amount,",
            "  csrd.actual_amount,",
            "  csrd.difference_remark,",
            "  csrd.process_status,",
            "  csrd.process_result,",
            "  csrd.process_remark,",
            "  csrd.reported_by_employee_id,",
            "  csrd.reported_at,",
            "  csrd.processed_by_employee_id,",
            "  csrd.processed_at,",
            "  csrd.source,",
            "  csr.settlement_status,",
            "  csr.payout_status,",
            "  csr.payout_batch_no AS current_payout_batch_no,",
            "  csr.payout_remark,",
            "  csr.payout_reference_no,",
            "  csr.payout_verified,",
            "  csr.payout_verify_remark,",
            "  csr.pending_amount,",
            "  csr.payout_recorded_at,",
            "  csr.payout_verified_at,",
            "  csrd.created_at,",
            "  csrd.updated_at",
            "FROM campus_settlement_reconcile_difference_record csrd",
            "LEFT JOIN campus_settlement_record csr ON csrd.settlement_record_id = csr.id",
            "WHERE csrd.id = #{id}"
    })
    CampusSettlementReconcileDifferenceRecordVO selectDetailById(Long id);

    @Update("UPDATE campus_settlement_reconcile_difference_record SET " +
            "process_status = 'RESOLVED', " +
            "process_result = #{processResult}, " +
            "process_remark = #{processRemark}, " +
            "processed_by_employee_id = #{processedByEmployeeId}, " +
            "processed_at = #{processedAt}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND process_status = 'OPEN'")
    int resolveByAdmin(
            @Param("id") Long id,
            @Param("processResult") String processResult,
            @Param("processRemark") String processRemark,
            @Param("processedByEmployeeId") Long processedByEmployeeId,
            @Param("processedAt") LocalDateTime processedAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}

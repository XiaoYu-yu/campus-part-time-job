package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusSettlementBatchOperationRecord;
import com.cangqiong.takeaway.campus.vo.CampusSettlementBatchOperationRecordVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CampusSettlementBatchOperationRecordMapper {

    @Insert("INSERT INTO campus_settlement_batch_operation_record (" +
            "payout_batch_no, operation_type, operation_result, operation_remark, operated_by_employee_id, operated_at, created_at, updated_at" +
            ") VALUES (" +
            "#{payoutBatchNo}, #{operationType}, #{operationResult}, #{operationRemark}, #{operatedByEmployeeId}, #{operatedAt}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusSettlementBatchOperationRecord record);

    @Select({
            "<script>",
            "SELECT",
            "  id,",
            "  payout_batch_no,",
            "  operation_type,",
            "  operation_result,",
            "  operation_remark,",
            "  operated_by_employee_id,",
            "  operated_at,",
            "  created_at,",
            "  updated_at",
            "FROM campus_settlement_batch_operation_record",
            "<where>",
            "  payout_batch_no = #{payoutBatchNo}",
            "  <if test='operationType != null and operationType != \"\"'>AND operation_type = #{operationType}</if>",
            "  <if test='operationResult != null and operationResult != \"\"'>AND operation_result = #{operationResult}</if>",
            "</where>",
            "ORDER BY operated_at DESC, id DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusSettlementBatchOperationRecordVO> selectByBatchNo(
            @Param("payoutBatchNo") String payoutBatchNo,
            @Param("operationType") String operationType,
            @Param("operationResult") String operationResult,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM campus_settlement_batch_operation_record",
            "<where>",
            "  payout_batch_no = #{payoutBatchNo}",
            "  <if test='operationType != null and operationType != \"\"'>AND operation_type = #{operationType}</if>",
            "  <if test='operationResult != null and operationResult != \"\"'>AND operation_result = #{operationResult}</if>",
            "</where>",
            "</script>"
    })
    Long countByBatchNo(
            @Param("payoutBatchNo") String payoutBatchNo,
            @Param("operationType") String operationType,
            @Param("operationResult") String operationResult
    );
}

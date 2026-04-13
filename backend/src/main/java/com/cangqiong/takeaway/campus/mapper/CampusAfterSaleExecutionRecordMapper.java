package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusAfterSaleExecutionRecord;
import com.cangqiong.takeaway.campus.vo.CampusAfterSaleExecutionRecordVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CampusAfterSaleExecutionRecordMapper {

    @Insert("INSERT INTO campus_after_sale_execution_record (" +
            "relay_order_id, decision_type, decision_amount, previous_execution_status, execution_status, " +
            "execution_remark, execution_reference_no, executed_by_employee_id, executed_at, corrected, created_at, updated_at" +
            ") VALUES (" +
            "#{relayOrderId}, #{decisionType}, #{decisionAmount}, #{previousExecutionStatus}, #{executionStatus}, " +
            "#{executionRemark}, #{executionReferenceNo}, #{executedByEmployeeId}, #{executedAt}, #{corrected}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusAfterSaleExecutionRecord record);

    @Select({
            "<script>",
            "SELECT",
            "  caer.id,",
            "  caer.relay_order_id,",
            "  cro.order_status,",
            "  cro.customer_user_id,",
            "  cro.courier_profile_id,",
            "  caer.decision_type,",
            "  caer.decision_amount,",
            "  caer.previous_execution_status,",
            "  caer.execution_status,",
            "  caer.execution_remark,",
            "  caer.execution_reference_no,",
            "  caer.executed_by_employee_id,",
            "  caer.executed_at,",
            "  caer.corrected,",
            "  cro.after_sale_execution_status AS current_execution_status,",
            "  cro.after_sale_execution_remark AS current_execution_remark,",
            "  cro.after_sale_execution_reference_no AS current_execution_reference_no,",
            "  cro.after_sale_execution_corrected AS current_execution_corrected,",
            "  cro.after_sale_execution_corrected_at AS current_execution_corrected_at,",
            "  caer.created_at,",
            "  caer.updated_at",
            "FROM campus_after_sale_execution_record caer",
            "LEFT JOIN campus_relay_order cro ON caer.relay_order_id = cro.id",
            "<where>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND caer.relay_order_id = #{relayOrderId}</if>",
            "  <if test='executionStatus != null and executionStatus != \"\"'>AND caer.execution_status = #{executionStatus}</if>",
            "  <if test='corrected != null'>AND caer.corrected = #{corrected}</if>",
            "</where>",
            "ORDER BY caer.executed_at DESC, caer.id DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusAfterSaleExecutionRecordVO> selectByCondition(
            @Param("relayOrderId") String relayOrderId,
            @Param("executionStatus") String executionStatus,
            @Param("corrected") Integer corrected,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM campus_after_sale_execution_record caer",
            "<where>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND caer.relay_order_id = #{relayOrderId}</if>",
            "  <if test='executionStatus != null and executionStatus != \"\"'>AND caer.execution_status = #{executionStatus}</if>",
            "  <if test='corrected != null'>AND caer.corrected = #{corrected}</if>",
            "</where>",
            "</script>"
    })
    Long countByCondition(
            @Param("relayOrderId") String relayOrderId,
            @Param("executionStatus") String executionStatus,
            @Param("corrected") Integer corrected
    );
}

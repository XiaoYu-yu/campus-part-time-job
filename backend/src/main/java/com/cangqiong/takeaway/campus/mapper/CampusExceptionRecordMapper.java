package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusExceptionRecord;
import com.cangqiong.takeaway.campus.vo.CampusExceptionRecordVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CampusExceptionRecordMapper {

    @Insert("INSERT INTO campus_exception_record (" +
            "relay_order_id, courier_profile_id, exception_type, exception_remark, reported_at, " +
            "process_status, process_result, processed_by_employee_id, processed_at, admin_note, source, created_at, updated_at" +
            ") VALUES (" +
            "#{relayOrderId}, #{courierProfileId}, #{exceptionType}, #{exceptionRemark}, #{reportedAt}, " +
            "#{processStatus}, #{processResult}, #{processedByEmployeeId}, #{processedAt}, #{adminNote}, #{source}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusExceptionRecord record);

    @Select({
            "<script>",
            "SELECT",
            "  cer.id,",
            "  cer.relay_order_id,",
            "  cro.order_status,",
            "  cro.customer_user_id,",
            "  cer.courier_profile_id,",
            "  cp.real_name AS courier_name,",
            "  cer.exception_type,",
            "  cer.exception_remark,",
            "  cer.reported_at,",
            "  cer.process_status,",
            "  cer.process_result,",
            "  cer.processed_by_employee_id,",
            "  cer.processed_at,",
            "  cer.admin_note,",
            "  cer.source,",
            "  cro.exception_type AS latest_exception_type,",
            "  cro.exception_remark AS latest_exception_remark,",
            "  cro.exception_reported_at AS latest_exception_reported_at,",
            "  cer.created_at,",
            "  cer.updated_at",
            "FROM campus_exception_record cer",
            "LEFT JOIN campus_relay_order cro ON cer.relay_order_id = cro.id",
            "LEFT JOIN campus_courier_profile cp ON cer.courier_profile_id = cp.id",
            "<where>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND cer.relay_order_id = #{relayOrderId}</if>",
            "  <if test='courierProfileId != null'>AND cer.courier_profile_id = #{courierProfileId}</if>",
            "  <if test='processStatus != null and processStatus != \"\"'>AND cer.process_status = #{processStatus}</if>",
            "</where>",
            "ORDER BY cer.reported_at DESC, cer.id DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusExceptionRecordVO> selectByCondition(
            @Param("relayOrderId") String relayOrderId,
            @Param("courierProfileId") Long courierProfileId,
            @Param("processStatus") String processStatus,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM campus_exception_record cer",
            "<where>",
            "  <if test='relayOrderId != null and relayOrderId != \"\"'>AND cer.relay_order_id = #{relayOrderId}</if>",
            "  <if test='courierProfileId != null'>AND cer.courier_profile_id = #{courierProfileId}</if>",
            "  <if test='processStatus != null and processStatus != \"\"'>AND cer.process_status = #{processStatus}</if>",
            "</where>",
            "</script>"
    })
    Long countByCondition(
            @Param("relayOrderId") String relayOrderId,
            @Param("courierProfileId") Long courierProfileId,
            @Param("processStatus") String processStatus
    );

    @Select({
            "SELECT",
            "  cer.id,",
            "  cer.relay_order_id,",
            "  cro.order_status,",
            "  cro.customer_user_id,",
            "  cer.courier_profile_id,",
            "  cp.real_name AS courier_name,",
            "  cer.exception_type,",
            "  cer.exception_remark,",
            "  cer.reported_at,",
            "  cer.process_status,",
            "  cer.process_result,",
            "  cer.processed_by_employee_id,",
            "  cer.processed_at,",
            "  cer.admin_note,",
            "  cer.source,",
            "  cro.exception_type AS latest_exception_type,",
            "  cro.exception_remark AS latest_exception_remark,",
            "  cro.exception_reported_at AS latest_exception_reported_at,",
            "  cer.created_at,",
            "  cer.updated_at",
            "FROM campus_exception_record cer",
            "LEFT JOIN campus_relay_order cro ON cer.relay_order_id = cro.id",
            "LEFT JOIN campus_courier_profile cp ON cer.courier_profile_id = cp.id",
            "WHERE cer.id = #{id}"
    })
    CampusExceptionRecordVO selectDetailById(Long id);

    @Update("UPDATE campus_exception_record SET " +
            "process_status = 'RESOLVED', " +
            "process_result = #{processResult}, " +
            "processed_by_employee_id = #{processedByEmployeeId}, " +
            "processed_at = #{processedAt}, " +
            "admin_note = #{adminNote}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND process_status = 'REPORTED'")
    int resolveByAdmin(
            @Param("id") Long id,
            @Param("processResult") String processResult,
            @Param("adminNote") String adminNote,
            @Param("processedByEmployeeId") Long processedByEmployeeId,
            @Param("processedAt") LocalDateTime processedAt,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}

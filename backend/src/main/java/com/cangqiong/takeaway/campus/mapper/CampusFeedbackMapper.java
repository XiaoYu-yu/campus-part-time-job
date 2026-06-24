package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusFeedback;
import com.cangqiong.takeaway.campus.vo.CampusFeedbackVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CampusFeedbackMapper {

    @Insert("INSERT INTO campus_feedback (" +
            "submitter_role, category, content, contact, page_path, order_id, status, created_at, updated_at" +
            ") VALUES (" +
            "#{submitterRole}, #{category}, #{content}, #{contact}, #{page}, #{orderId}, #{status}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusFeedback feedback);

    @Select("SELECT COUNT(*) FROM campus_feedback WHERE " +
            "submitter_role = #{submitterRole} AND category = #{category} AND content = #{content} " +
            "AND (contact = #{contact} OR (contact IS NULL AND #{contact} IS NULL)) " +
            "AND (page_path = #{page} OR (page_path IS NULL AND #{page} IS NULL)) " +
            "AND (order_id = #{orderId} OR (order_id IS NULL AND #{orderId} IS NULL)) " +
            "AND created_at >= #{createdAfter}")
    Long countRecentDuplicate(
            @Param("submitterRole") String submitterRole,
            @Param("category") String category,
            @Param("content") String content,
            @Param("contact") String contact,
            @Param("page") String page,
            @Param("orderId") String orderId,
            @Param("createdAfter") LocalDateTime createdAfter
    );

    @Select({
            "<script>",
            "SELECT",
            "  id, submitter_role, category, content, contact, page_path AS page, order_id, status,",
            "  processed_by_employee_id, processed_at, admin_note, created_at, updated_at",
            "FROM campus_feedback",
            "<where>",
            "  <if test='submitterRole != null and submitterRole != \"\"'>AND submitter_role = #{submitterRole}</if>",
            "  <if test='category != null and category != \"\"'>AND category = #{category}</if>",
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>",
            "  <if test='orderId != null and orderId != \"\"'>AND order_id = #{orderId}</if>",
            "</where>",
            "ORDER BY created_at DESC, id DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusFeedbackVO> selectByCondition(
            @Param("submitterRole") String submitterRole,
            @Param("category") String category,
            @Param("status") String status,
            @Param("orderId") String orderId,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM campus_feedback",
            "<where>",
            "  <if test='submitterRole != null and submitterRole != \"\"'>AND submitter_role = #{submitterRole}</if>",
            "  <if test='category != null and category != \"\"'>AND category = #{category}</if>",
            "  <if test='status != null and status != \"\"'>AND status = #{status}</if>",
            "  <if test='orderId != null and orderId != \"\"'>AND order_id = #{orderId}</if>",
            "</where>",
            "</script>"
    })
    Long countByCondition(
            @Param("submitterRole") String submitterRole,
            @Param("category") String category,
            @Param("status") String status,
            @Param("orderId") String orderId
    );

    @Select("SELECT id, submitter_role, category, content, contact, page_path AS page, order_id, status, " +
            "processed_by_employee_id, processed_at, admin_note, created_at, updated_at " +
            "FROM campus_feedback WHERE id = #{id}")
    CampusFeedbackVO selectById(Long id);

    @Update("UPDATE campus_feedback SET " +
            "status = #{status}, processed_by_employee_id = #{employeeId}, processed_at = #{processedAt}, " +
            "admin_note = #{adminNote}, updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND status <> 'RESOLVED'")
    int updateProcessStatus(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("employeeId") Long employeeId,
            @Param("processedAt") LocalDateTime processedAt,
            @Param("adminNote") String adminNote,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}

package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusCourierProfile;
import com.cangqiong.takeaway.campus.vo.CampusCourierProfileVO;
import com.cangqiong.takeaway.campus.vo.CampusCourierReviewStatusVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CampusCourierProfileMapper {

    @Select("SELECT * FROM campus_courier_profile WHERE id = #{id}")
    CampusCourierProfile selectById(Long id);

    @Select("SELECT * FROM campus_courier_profile WHERE user_id = #{userId}")
    CampusCourierProfile selectByUserId(Long userId);

    @Select("SELECT * FROM campus_courier_profile WHERE user_id = #{userId} AND review_status = 'APPROVED' AND enabled = 1")
    CampusCourierProfile selectApprovedEnabledByUserId(Long userId);

    @Select({
            "SELECT",
            "  cp.id,",
            "  cp.user_id,",
            "  cp.real_name,",
            "  u.phone,",
            "  cp.gender,",
            "  cp.campus_zone,",
            "  cp.student_no,",
            "  cp.college,",
            "  cp.major,",
            "  cp.class_name,",
            "  cp.dormitory_building,",
            "  cp.dormitory_room,",
            "  cp.enabled_work_in_own_building,",
            "  cp.applicant_remark,",
            "  cp.id_card_last4,",
            "  cp.emergency_contact_name,",
            "  cp.emergency_contact_phone,",
            "  cp.verification_photo_url,",
            "  cp.schedule_attachment_url,",
            "  cp.review_status,",
            "  cp.review_comment,",
            "  cp.enabled,",
            "  cp.reviewed_by_employee_id,",
            "  e.name AS reviewed_by_name,",
            "  cp.reviewed_at,",
            "  cp.created_at,",
            "  cp.updated_at",
            "FROM campus_courier_profile cp",
            "LEFT JOIN user u ON cp.user_id = u.id",
            "LEFT JOIN employee e ON cp.reviewed_by_employee_id = e.id",
            "WHERE cp.user_id = #{userId}"
    })
    CampusCourierProfileVO selectDetailByUserId(Long userId);

    @Select({
            "SELECT",
            "  cp.id,",
            "  cp.user_id,",
            "  cp.real_name,",
            "  u.phone,",
            "  cp.gender,",
            "  cp.campus_zone,",
            "  cp.student_no,",
            "  cp.college,",
            "  cp.major,",
            "  cp.class_name,",
            "  cp.dormitory_building,",
            "  cp.dormitory_room,",
            "  cp.enabled_work_in_own_building,",
            "  cp.applicant_remark,",
            "  cp.id_card_last4,",
            "  cp.emergency_contact_name,",
            "  cp.emergency_contact_phone,",
            "  cp.verification_photo_url,",
            "  cp.schedule_attachment_url,",
            "  cp.review_status,",
            "  cp.review_comment,",
            "  cp.enabled,",
            "  cp.reviewed_by_employee_id,",
            "  e.name AS reviewed_by_name,",
            "  cp.reviewed_at,",
            "  cp.created_at,",
            "  cp.updated_at",
            "FROM campus_courier_profile cp",
            "LEFT JOIN user u ON cp.user_id = u.id",
            "LEFT JOIN employee e ON cp.reviewed_by_employee_id = e.id",
            "WHERE cp.id = #{id}"
    })
    CampusCourierProfileVO selectDetailById(Long id);

    @Select({
            "SELECT",
            "  cp.id AS profile_id,",
            "  cp.review_status,",
            "  cp.review_comment,",
            "  cp.enabled,",
            "  cp.reviewed_by_employee_id,",
            "  e.name AS reviewed_by_name,",
            "  cp.reviewed_at,",
            "  cp.updated_at",
            "FROM campus_courier_profile cp",
            "LEFT JOIN employee e ON cp.reviewed_by_employee_id = e.id",
            "WHERE cp.user_id = #{userId}"
    })
    CampusCourierReviewStatusVO selectReviewStatusByUserId(Long userId);

    @Select({
            "<script>",
            "SELECT",
            "  cp.id,",
            "  cp.user_id,",
            "  cp.real_name,",
            "  u.phone,",
            "  cp.gender,",
            "  cp.campus_zone,",
            "  cp.student_no,",
            "  cp.college,",
            "  cp.major,",
            "  cp.class_name,",
            "  cp.dormitory_building,",
            "  cp.dormitory_room,",
            "  cp.enabled_work_in_own_building,",
            "  cp.applicant_remark,",
            "  cp.review_status,",
            "  cp.review_comment,",
            "  cp.enabled,",
            "  cp.reviewed_by_employee_id,",
            "  e.name AS reviewed_by_name,",
            "  cp.reviewed_at,",
            "  cp.created_at,",
            "  cp.updated_at",
            "FROM campus_courier_profile cp",
            "LEFT JOIN user u ON cp.user_id = u.id",
            "LEFT JOIN employee e ON cp.reviewed_by_employee_id = e.id",
            "<where>",
            "  <if test='realName != null and realName != \"\"'>AND cp.real_name LIKE CONCAT('%', #{realName}, '%')</if>",
            "  <if test='phone != null and phone != \"\"'>AND u.phone LIKE CONCAT('%', #{phone}, '%')</if>",
            "  <if test='studentNo != null and studentNo != \"\"'>AND cp.student_no LIKE CONCAT('%', #{studentNo}, '%')</if>",
            "  <if test='college != null and college != \"\"'>AND cp.college LIKE CONCAT('%', #{college}, '%')</if>",
            "  <if test='reviewStatus != null and reviewStatus != \"\"'>AND cp.review_status = #{reviewStatus}</if>",
            "  <if test='dormitoryBuilding != null and dormitoryBuilding != \"\"'>AND cp.dormitory_building = #{dormitoryBuilding}</if>",
            "  <if test='enabled != null'>AND cp.enabled = #{enabled}</if>",
            "</where>",
            "ORDER BY cp.created_at DESC",
            "LIMIT #{pageSize} OFFSET #{offset}",
            "</script>"
    })
    List<CampusCourierProfileVO> selectByCondition(
            @Param("realName") String realName,
            @Param("phone") String phone,
            @Param("studentNo") String studentNo,
            @Param("college") String college,
            @Param("reviewStatus") String reviewStatus,
            @Param("dormitoryBuilding") String dormitoryBuilding,
            @Param("enabled") Integer enabled,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    @Select({
            "<script>",
            "SELECT COUNT(*)",
            "FROM campus_courier_profile cp",
            "LEFT JOIN user u ON cp.user_id = u.id",
            "<where>",
            "  <if test='realName != null and realName != \"\"'>AND cp.real_name LIKE CONCAT('%', #{realName}, '%')</if>",
            "  <if test='phone != null and phone != \"\"'>AND u.phone LIKE CONCAT('%', #{phone}, '%')</if>",
            "  <if test='studentNo != null and studentNo != \"\"'>AND cp.student_no LIKE CONCAT('%', #{studentNo}, '%')</if>",
            "  <if test='college != null and college != \"\"'>AND cp.college LIKE CONCAT('%', #{college}, '%')</if>",
            "  <if test='reviewStatus != null and reviewStatus != \"\"'>AND cp.review_status = #{reviewStatus}</if>",
            "  <if test='dormitoryBuilding != null and dormitoryBuilding != \"\"'>AND cp.dormitory_building = #{dormitoryBuilding}</if>",
            "  <if test='enabled != null'>AND cp.enabled = #{enabled}</if>",
            "</where>",
            "</script>"
    })
    Long countByCondition(
            @Param("realName") String realName,
            @Param("phone") String phone,
            @Param("studentNo") String studentNo,
            @Param("college") String college,
            @Param("reviewStatus") String reviewStatus,
            @Param("dormitoryBuilding") String dormitoryBuilding,
            @Param("enabled") Integer enabled
    );

    @Insert("INSERT INTO campus_courier_profile (" +
            "user_id, real_name, gender, campus_zone, student_no, college, major, class_name, dormitory_building, dormitory_room, " +
            "enabled_work_in_own_building, applicant_remark, id_card_last4, emergency_contact_name, emergency_contact_phone, verification_photo_url, schedule_attachment_url, " +
            "review_status, review_comment, reviewed_by_employee_id, reviewed_at, enabled, created_at, updated_at" +
            ") VALUES (" +
            "#{userId}, #{realName}, #{gender}, #{campusZone}, #{studentNo}, #{college}, #{major}, #{className}, #{dormitoryBuilding}, #{dormitoryRoom}, " +
            "#{enabledWorkInOwnBuilding}, #{applicantRemark}, #{idCardLast4}, #{emergencyContactName}, #{emergencyContactPhone}, #{verificationPhotoUrl}, #{scheduleAttachmentUrl}, " +
            "#{reviewStatus}, #{reviewComment}, #{reviewedByEmployeeId}, #{reviewedAt}, #{enabled}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusCourierProfile profile);

    @Update("UPDATE campus_courier_profile SET " +
            "real_name = #{realName}, " +
            "gender = #{gender}, " +
            "campus_zone = #{campusZone}, " +
            "student_no = #{studentNo}, " +
            "college = #{college}, " +
            "major = #{major}, " +
            "class_name = #{className}, " +
            "dormitory_building = #{dormitoryBuilding}, " +
            "dormitory_room = #{dormitoryRoom}, " +
            "enabled_work_in_own_building = #{enabledWorkInOwnBuilding}, " +
            "applicant_remark = #{applicantRemark}, " +
            "id_card_last4 = #{idCardLast4}, " +
            "emergency_contact_name = #{emergencyContactName}, " +
            "emergency_contact_phone = #{emergencyContactPhone}, " +
            "verification_photo_url = #{verificationPhotoUrl}, " +
            "schedule_attachment_url = #{scheduleAttachmentUrl}, " +
            "review_status = #{reviewStatus}, " +
            "review_comment = #{reviewComment}, " +
            "reviewed_by_employee_id = #{reviewedByEmployeeId}, " +
            "reviewed_at = #{reviewedAt}, " +
            "enabled = #{enabled}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateSubmittedProfile(CampusCourierProfile profile);

    @Update("UPDATE campus_courier_profile SET " +
            "review_status = #{reviewStatus}, " +
            "review_comment = #{reviewComment}, " +
            "reviewed_by_employee_id = #{reviewedByEmployeeId}, " +
            "reviewed_at = #{reviewedAt}, " +
            "enabled = #{enabled}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int updateReviewDecision(
            @Param("id") Long id,
            @Param("reviewStatus") String reviewStatus,
            @Param("reviewComment") String reviewComment,
            @Param("reviewedByEmployeeId") Long reviewedByEmployeeId,
            @Param("reviewedAt") LocalDateTime reviewedAt,
            @Param("enabled") Integer enabled,
            @Param("updatedAt") LocalDateTime updatedAt
    );
}

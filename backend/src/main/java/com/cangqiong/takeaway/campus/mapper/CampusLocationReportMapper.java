package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusLocationReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CampusLocationReportMapper {

    @Select("SELECT * FROM campus_location_report WHERE relay_order_id = #{relayOrderId} ORDER BY reported_at DESC LIMIT 1")
    CampusLocationReport selectLatestByOrderId(String relayOrderId);

    @Select("SELECT COUNT(*) FROM campus_location_report WHERE relay_order_id = #{relayOrderId} AND courier_profile_id = #{courierProfileId}")
    Long countByOrderIdAndCourierProfileId(@Param("relayOrderId") String relayOrderId, @Param("courierProfileId") Long courierProfileId);

    @Insert("INSERT INTO campus_location_report (" +
            "relay_order_id, courier_profile_id, latitude, longitude, source, note, reported_at, created_at" +
            ") VALUES (" +
            "#{relayOrderId}, #{courierProfileId}, #{latitude}, #{longitude}, #{source}, #{note}, #{reportedAt}, #{createdAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusLocationReport report);
}

package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusFeedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface CampusFeedbackMapper {

    @Insert("INSERT INTO campus_feedback (" +
            "submitter_role, category, content, contact, page_path, order_id, status, created_at, updated_at" +
            ") VALUES (" +
            "#{submitterRole}, #{category}, #{content}, #{contact}, #{page}, #{orderId}, #{status}, #{createdAt}, #{updatedAt}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusFeedback feedback);
}

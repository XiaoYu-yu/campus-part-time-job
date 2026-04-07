package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusCustomerProfile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CampusCustomerProfileMapper {

    @Select("SELECT * FROM campus_customer_profile WHERE user_id = #{userId}")
    CampusCustomerProfile selectByUserId(Long userId);

    @Insert("INSERT INTO campus_customer_profile (user_id, real_name, identity_type, identity_no, created_at, updated_at) " +
            "VALUES (#{userId}, #{realName}, #{identityType}, #{identityNo}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CampusCustomerProfile profile);
}

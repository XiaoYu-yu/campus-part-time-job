package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectByPhone(String phone);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Update("UPDATE user SET password = #{password}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}

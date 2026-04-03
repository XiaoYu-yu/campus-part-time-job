package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {

    @Select("SELECT * FROM address WHERE user_id = #{userId} ORDER BY is_default DESC, updated_at DESC")
    List<Address> selectByUserId(Long userId);

    @Select("SELECT * FROM address WHERE id = #{id} AND user_id = #{userId}")
    Address selectById(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT * FROM address WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    Address selectDefaultByUserId(Long userId);

    @Insert("INSERT INTO address (user_id, consignee, phone, province, city, district, detail, is_default, created_at, updated_at) VALUES " +
            "(#{userId}, #{consignee}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Address address);

    @Update("UPDATE address SET consignee = #{consignee}, phone = #{phone}, province = #{province}, city = #{city}, district = #{district}, detail = #{detail}, is_default = #{isDefault}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND user_id = #{userId}")
    void update(Address address);

    @Delete("DELETE FROM address WHERE id = #{id} AND user_id = #{userId}")
    void deleteById(@Param("id") Long id, @Param("userId") Long userId);

    @Update("UPDATE address SET is_default = 0 WHERE user_id = #{userId}")
    void clearDefault(Long userId);

    @Update("UPDATE address SET is_default = 1, updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND user_id = #{userId}")
    void setDefault(@Param("id") Long id, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM address WHERE user_id = #{userId}")
    Long countByUserId(Long userId);
}

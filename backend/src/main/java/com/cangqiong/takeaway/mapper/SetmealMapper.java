package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.Setmeal;
import com.cangqiong.takeaway.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("<script>" +
            "SELECT s.*, c.name as category_name FROM setmeal s " +
            "LEFT JOIN category c ON s.category_id = c.id " +
            "<where>" +
            "<if test='name != null and name != \"\"'>AND s.name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='categoryId != null'>AND s.category_id = #{categoryId}</if>" +
            "</where>" +
            "ORDER BY s.created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<SetmealVO> selectByCondition(@Param("name") String name, @Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("size") int size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM setmeal " +
            "<where>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
            "</where>" +
            "</script>")
    Long countByCondition(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Select("SELECT s.*, c.name as category_name FROM setmeal s " +
            "LEFT JOIN category c ON s.category_id = c.id " +
            "WHERE s.id = #{id}")
    SetmealVO selectById(Long id);

    @Insert("INSERT INTO setmeal (name, category_id, price, description, image, status, created_at, updated_at) " +
            "VALUES (#{name}, #{categoryId}, #{price}, #{description}, #{image}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Setmeal setmeal);

    @Update("UPDATE setmeal SET " +
            "name = #{name}, " +
            "category_id = #{categoryId}, " +
            "price = #{price}, " +
            "description = #{description}, " +
            "image = #{image}, " +
            "status = #{status}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(Setmeal setmeal);

    @Delete("DELETE FROM setmeal WHERE id = #{id}")
    void deleteById(Long id);

    @Update("UPDATE setmeal SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("<script>" +
            "UPDATE setmeal SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    void batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}

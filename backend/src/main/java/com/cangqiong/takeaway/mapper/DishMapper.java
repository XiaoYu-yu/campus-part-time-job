package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.Dish;
import com.cangqiong.takeaway.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    @Select("<script>" +
            "SELECT d.*, c.name as category_name FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.id " +
            "<where>" +
            "<if test='name != null and name != \"\"'>AND d.name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='categoryId != null'>AND d.category_id = #{categoryId}</if>" +
            "</where>" +
            "ORDER BY d.created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<DishVO> selectByCondition(@Param("name") String name, @Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("size") int size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM dish " +
            "<where>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='categoryId != null'>AND category_id = #{categoryId}</if>" +
            "</where>" +
            "</script>")
    Long countByCondition(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Select("SELECT d.*, c.name as category_name FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.id " +
            "WHERE d.id = #{id}")
    DishVO selectById(Long id);

    @Insert("INSERT INTO dish (name, category_id, price, description, image, status, created_at, updated_at) " +
            "VALUES (#{name}, #{categoryId}, #{price}, #{description}, #{image}, #{status}, #{createdAt}, #{updatedAt})")
    void insert(Dish dish);

    @Update("UPDATE dish SET " +
            "name = #{name}, " +
            "category_id = #{categoryId}, " +
            "price = #{price}, " +
            "description = #{description}, " +
            "image = #{image}, " +
            "status = #{status}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(Dish dish);

    @Delete("DELETE FROM dish WHERE id = #{id}")
    void deleteById(Long id);

    @Update("UPDATE dish SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("<script>" +
            "UPDATE dish SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    void batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM dish WHERE category_id = #{categoryId}")
    Long countByCategoryId(Long categoryId);

    @Select("<script>" +
            "SELECT d.*, c.name as category_name FROM dish d " +
            "LEFT JOIN category c ON d.category_id = c.id " +
            "<where>" +
            "AND d.status = 1 " +
            "<if test='name != null and name != \"\"'>AND d.name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='categoryId != null'>AND d.category_id = #{categoryId}</if>" +
            "</where>" +
            "ORDER BY d.created_at DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<DishVO> selectEnabledByCondition(@Param("name") String name, @Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM dish d " +
            "<where>" +
            "AND d.status = 1 " +
            "<if test='name != null and name != \"\"'>AND d.name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='categoryId != null'>AND d.category_id = #{categoryId}</if>" +
            "</where>" +
            "</script>")
    Long countEnabledByCondition(@Param("name") String name, @Param("categoryId") Long categoryId);

    @Select("SELECT d.*, c.name as category_name FROM dish d LEFT JOIN category c ON d.category_id = c.id WHERE d.id = #{id} AND d.status = 1")
    DishVO selectEnabledById(Long id);
}

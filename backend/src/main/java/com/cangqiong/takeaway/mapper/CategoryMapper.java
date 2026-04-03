package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM category ORDER BY sort ASC, created_at DESC")
    List<Category> selectAll();

    @Select("SELECT * FROM category WHERE status = 1 ORDER BY sort ASC, created_at DESC")
    List<Category> selectEnabled();

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category selectById(Long id);

    @Insert("INSERT INTO category (name, sort, status, created_at, updated_at) " +
            "VALUES (#{name}, #{sort}, #{status}, #{createdAt}, #{updatedAt})")
    void insert(Category category);

    @Update("UPDATE category SET " +
            "name = #{name}, " +
            "sort = #{sort}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    void deleteById(Long id);

    @Update("UPDATE category SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE category SET sort = #{sort}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateSort(@Param("id") Long id, @Param("sort") Integer sort);

    @Select("SELECT COUNT(*) FROM category WHERE name = #{name}")
    Long countByName(String name);
}

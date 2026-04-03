package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE phone = #{phone}")
    Employee selectByPhone(String phone);

    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee selectById(Long id);

    @Select("<script>" +
            "SELECT * FROM employee " +
            "<where>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'>AND phone LIKE CONCAT('%', #{phone}, '%')</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<Employee> selectByCondition(@Param("name") String name, @Param("phone") String phone, @Param("offset") int offset, @Param("size") int size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM employee " +
            "<where>" +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'>AND phone LIKE CONCAT('%', #{phone}, '%')</if>" +
            "</where>" +
            "</script>")
    Long countByCondition(@Param("name") String name, @Param("phone") String phone);

    @Insert("INSERT INTO employee (name, phone, password, position, department, entry_date, status, created_at, updated_at) " +
            "VALUES (#{name}, #{phone}, #{password}, #{position}, #{department}, #{entryDate}, #{status}, #{createdAt}, #{updatedAt})")
    void insert(Employee employee);

    @Update("UPDATE employee SET " +
            "name = #{name}, " +
            "phone = #{phone}, " +
            "position = #{position}, " +
            "department = #{department}, " +
            "entry_date = #{entryDate}, " +
            "status = #{status}, " +
            "updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(Employee employee);

    @Delete("DELETE FROM employee WHERE id = #{id}")
    void deleteById(Long id);

    @Update("UPDATE employee SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE employee SET password = #{password}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}

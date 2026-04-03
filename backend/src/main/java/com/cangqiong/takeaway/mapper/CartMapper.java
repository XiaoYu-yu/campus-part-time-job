package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.Cart;
import com.cangqiong.takeaway.vo.CartItemVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("<script>" +
            "SELECT * FROM cart WHERE user_id = #{userId} " +
            "<choose>" +
            "<when test='dishId != null'>AND dish_id = #{dishId} AND setmeal_id IS NULL</when>" +
            "<otherwise>AND dish_id IS NULL AND setmeal_id = #{setmealId}</otherwise>" +
            "</choose> " +
            "LIMIT 1" +
            "</script>")
    Cart selectExisting(@Param("userId") Long userId, @Param("dishId") Long dishId, @Param("setmealId") Long setmealId);

    @Select("SELECT c.id, c.dish_id, c.setmeal_id, c.quantity, c.checked, " +
            "COALESCE(d.name, s.name) AS name, COALESCE(d.description, s.description) AS description, " +
            "COALESCE(d.image, s.image) AS image, COALESCE(d.price, s.price) AS price, " +
            "CASE WHEN c.dish_id IS NOT NULL THEN 'dish' ELSE 'setmeal' END AS type " +
            "FROM cart c " +
            "LEFT JOIN dish d ON c.dish_id = d.id " +
            "LEFT JOIN setmeal s ON c.setmeal_id = s.id " +
            "WHERE c.user_id = #{userId} ORDER BY c.updated_at DESC")
    List<CartItemVO> selectByUserId(Long userId);

    @Select("<script>" +
            "SELECT c.id, c.dish_id, c.setmeal_id, c.quantity, c.checked, " +
            "COALESCE(d.name, s.name) AS name, COALESCE(d.description, s.description) AS description, " +
            "COALESCE(d.image, s.image) AS image, COALESCE(d.price, s.price) AS price, " +
            "CASE WHEN c.dish_id IS NOT NULL THEN 'dish' ELSE 'setmeal' END AS type " +
            "FROM cart c " +
            "LEFT JOIN dish d ON c.dish_id = d.id " +
            "LEFT JOIN setmeal s ON c.setmeal_id = s.id " +
            "WHERE c.user_id = #{userId} AND c.id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            " ORDER BY c.updated_at DESC" +
            "</script>")
    List<CartItemVO> selectByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    @Insert("INSERT INTO cart (user_id, dish_id, setmeal_id, quantity, checked, created_at, updated_at) VALUES " +
            "(#{userId}, #{dishId}, #{setmealId}, #{quantity}, #{checked}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Cart cart);

    @Update("UPDATE cart SET quantity = #{quantity}, checked = #{checked}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND user_id = #{userId}")
    void update(Cart cart);

    @Update("UPDATE cart SET quantity = quantity + #{delta}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void increaseQuantity(@Param("id") Long id, @Param("delta") Integer delta);

    @Delete("DELETE FROM cart WHERE id = #{id} AND user_id = #{userId}")
    void deleteById(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    @Delete("<script>" +
            "DELETE FROM cart WHERE user_id = #{userId} AND id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    void deleteByIds(@Param("userId") Long userId, @Param("ids") List<Long> ids);

    @Select("SELECT COUNT(*) FROM cart WHERE user_id = #{userId}")
    Long countByUserId(Long userId);
}

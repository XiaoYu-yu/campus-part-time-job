package com.cangqiong.takeaway.mapper;

import com.cangqiong.takeaway.entity.ShopBusinessHour;
import com.cangqiong.takeaway.entity.ShopConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShopConfigMapper {

    @Select("SELECT * FROM shop_config WHERE id = 1")
    ShopConfig selectConfig();

    @Select("SELECT * FROM shop_business_hour ORDER BY sort ASC")
    List<ShopBusinessHour> selectBusinessHours();

    @Update("UPDATE shop_config SET is_open = #{isOpen}, rest_notice = #{restNotice}, last_updated = CURRENT_TIMESTAMP WHERE id = 1")
    void updateConfig(ShopConfig config);

    @Update("UPDATE shop_business_hour SET day_name = #{dayName}, is_open = #{isOpen}, open_time = #{openTime}, close_time = #{closeTime} WHERE day_key = #{dayKey}")
    void updateBusinessHour(ShopBusinessHour hour);
}

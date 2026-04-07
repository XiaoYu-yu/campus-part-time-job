package com.cangqiong.takeaway.campus.mapper;

import com.cangqiong.takeaway.campus.entity.CampusPickupPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CampusPickupPointMapper {

    @Select("SELECT * FROM campus_pickup_point WHERE enabled = 1 ORDER BY sort ASC, id ASC")
    List<CampusPickupPoint> selectEnabledList();

    @Select("SELECT * FROM campus_pickup_point ORDER BY sort ASC, id ASC")
    List<CampusPickupPoint> selectAll();

    @Select("SELECT * FROM campus_pickup_point WHERE id = #{id}")
    CampusPickupPoint selectById(Long id);
}

package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.vo.CampusPickupPointVO;

import java.util.List;

public interface CampusPickupPointService {

    List<CampusPickupPointVO> listEnabledPickupPoints();
}

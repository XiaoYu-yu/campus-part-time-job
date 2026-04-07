package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.entity.CampusPickupPoint;
import com.cangqiong.takeaway.campus.mapper.CampusPickupPointMapper;
import com.cangqiong.takeaway.campus.service.CampusPickupPointService;
import com.cangqiong.takeaway.campus.vo.CampusPickupPointVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CampusPickupPointServiceImpl implements CampusPickupPointService {

    @Autowired
    private CampusPickupPointMapper campusPickupPointMapper;

    @Override
    public List<CampusPickupPointVO> listEnabledPickupPoints() {
        return campusPickupPointMapper.selectEnabledList().stream()
                .map(this::toVO)
                .toList();
    }

    private CampusPickupPointVO toVO(CampusPickupPoint entity) {
        CampusPickupPointVO vo = new CampusPickupPointVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}

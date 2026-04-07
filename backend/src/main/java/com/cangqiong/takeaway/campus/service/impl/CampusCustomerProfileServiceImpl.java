package com.cangqiong.takeaway.campus.service.impl;

import com.cangqiong.takeaway.campus.entity.CampusCustomerProfile;
import com.cangqiong.takeaway.campus.mapper.CampusCustomerProfileMapper;
import com.cangqiong.takeaway.campus.service.CampusCustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CampusCustomerProfileServiceImpl implements CampusCustomerProfileService {

    @Autowired
    private CampusCustomerProfileMapper campusCustomerProfileMapper;

    @Override
    public CampusCustomerProfile getByUserId(Long userId) {
        return campusCustomerProfileMapper.selectByUserId(userId);
    }
}

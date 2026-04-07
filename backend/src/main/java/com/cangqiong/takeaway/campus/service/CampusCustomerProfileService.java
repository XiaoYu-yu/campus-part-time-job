package com.cangqiong.takeaway.campus.service;

import com.cangqiong.takeaway.campus.entity.CampusCustomerProfile;

public interface CampusCustomerProfileService {

    CampusCustomerProfile getByUserId(Long userId);
}

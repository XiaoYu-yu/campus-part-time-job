package com.cangqiong.takeaway.service;

import com.cangqiong.takeaway.entity.User;

public interface UserService {

    User login(String phone, String password);

    User getById(Long id);
}

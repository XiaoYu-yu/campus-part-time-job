package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.entity.User;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.mapper.UserMapper;
import com.cangqiong.takeaway.service.PasswordService;
import com.cangqiong.takeaway.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordService passwordService;

    @Override
    @Transactional
    public User login(String phone, String password) {
        User user = userMapper.selectByPhone(phone);
        if (user == null || !passwordService.matches(password, user.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("当前账号已被禁用");
        }

        upgradePasswordIfNeeded(user.getId(), password, user.getPassword());
        return userMapper.selectById(user.getId());
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    private void upgradePasswordIfNeeded(Long userId, String rawPassword, String storedPassword) {
        if (passwordService.shouldUpgrade(storedPassword)) {
            userMapper.updatePassword(userId, passwordService.encode(rawPassword));
        }
    }
}

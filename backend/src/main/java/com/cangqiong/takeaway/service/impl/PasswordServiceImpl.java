package com.cangqiong.takeaway.service.impl;

import com.cangqiong.takeaway.service.PasswordService;
import com.cangqiong.takeaway.utils.MD5Util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (MD5Util.isLegacyHash(storedPassword)) {
            return MD5Util.encrypt(rawPassword).equalsIgnoreCase(storedPassword);
        }

        return passwordEncoder.matches(rawPassword, storedPassword);
    }

    @Override
    public boolean shouldUpgrade(String storedPassword) {
        return MD5Util.isLegacyHash(storedPassword);
    }
}

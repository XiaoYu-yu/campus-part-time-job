package com.cangqiong.takeaway.service;

public interface PasswordService {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String storedPassword);

    boolean shouldUpgrade(String storedPassword);
}

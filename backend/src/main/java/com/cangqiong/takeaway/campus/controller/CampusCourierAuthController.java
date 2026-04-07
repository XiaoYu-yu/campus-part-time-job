package com.cangqiong.takeaway.campus.controller;

import com.cangqiong.takeaway.campus.service.CampusCourierProfileService;
import com.cangqiong.takeaway.campus.vo.CampusCourierProfileVO;
import com.cangqiong.takeaway.dto.UserLoginDTO;
import com.cangqiong.takeaway.entity.User;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.service.UserService;
import com.cangqiong.takeaway.utils.JwtUtil;
import com.cangqiong.takeaway.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/campus/courier/auth")
public class CampusCourierAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CampusCourierProfileService campusCourierProfileService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/token")
    public Result<Map<String, Object>> createToken(@RequestBody UserLoginDTO loginDTO) {
        if (loginDTO == null || !StringUtils.hasText(loginDTO.getPhone()) || !StringUtils.hasText(loginDTO.getPassword())) {
            throw new BusinessException("手机号和密码不能为空");
        }
        log.info("校园配送员申请 token: phone={}", loginDTO.getPhone());

        User user = userService.login(loginDTO.getPhone(), loginDTO.getPassword());
        campusCourierProfileService.requireApprovedEnabledProfile(user.getId());
        CampusCourierProfileVO courierProfile = campusCourierProfileService.getCurrentProfile(user.getId());
        String token = jwtUtil.generateToken(user.getId(), user.getName(), "courier");

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("courierProfile", courierProfile);
        return Result.success(result);
    }
}

package com.cangqiong.takeaway.controller;

import com.cangqiong.takeaway.dto.UserLoginDTO;
import com.cangqiong.takeaway.entity.User;
import com.cangqiong.takeaway.exception.BusinessException;
import com.cangqiong.takeaway.interceptor.BaseContext;
import com.cangqiong.takeaway.service.UserService;
import com.cangqiong.takeaway.utils.JwtUtil;
import com.cangqiong.takeaway.utils.Result;
import com.cangqiong.takeaway.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/api/users/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginDTO loginDTO) {
        User user = userService.login(loginDTO.getPhone(), loginDTO.getPassword());

        String token = jwtUtil.generateToken(user.getId(), user.getName(), "customer");
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userVO);
        return Result.success(result);
    }

    @GetMapping("/api/users/info")
    public Result<UserVO> info() {
        Long userId = BaseContext.getCurrentUserId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }

    @PostMapping("/api/users/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}

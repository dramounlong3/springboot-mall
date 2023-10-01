package com.kyleguo.springbootmall.service;

import com.kyleguo.springbootmall.dto.UserRegisterRequest;
import com.kyleguo.springbootmall.model.User;

public interface UserService {
    User getUserById(Integer userId);
    Integer register(UserRegisterRequest userRegisterRequest);
}

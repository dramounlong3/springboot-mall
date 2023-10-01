package com.kyleguo.springbootmall.dao;

import com.kyleguo.springbootmall.dto.UserRegisterRequest;
import com.kyleguo.springbootmall.model.User;
import com.kyleguo.springbootmall.service.UserService;

public interface UserDao {
    User getUserById(Integer userId);

    User getUserByEmail(String email);
    Integer createUser(UserRegisterRequest userRegisterRequest);
}

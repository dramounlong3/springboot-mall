package com.kyleguo.springbootmall.dao;

import com.kyleguo.springbootmall.dto.UserRegisterRequest;
import com.kyleguo.springbootmall.model.User;

public interface UserDao {
    User getUserById(Integer userId);
    Integer createUser(UserRegisterRequest userRegisterRequest);
}

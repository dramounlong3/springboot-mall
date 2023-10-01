package com.kyleguo.springbootmall.service.impl;

import com.kyleguo.springbootmall.dao.UserDao;
import com.kyleguo.springbootmall.dto.UserRegisterRequest;
import com.kyleguo.springbootmall.model.User;
import com.kyleguo.springbootmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        return userDao.createUser(userRegisterRequest);
    }
}

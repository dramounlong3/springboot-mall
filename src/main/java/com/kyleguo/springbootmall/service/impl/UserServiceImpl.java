package com.kyleguo.springbootmall.service.impl;

import com.kyleguo.springbootmall.dao.UserDao;
import com.kyleguo.springbootmall.dto.UserRegisterRequest;
import com.kyleguo.springbootmall.model.User;
import com.kyleguo.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    // log變數
    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        //檢查註冊的email
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        if (user != null) {
            // {}會依序對應到後面的變數
            log.warn("該 email {} 已經被 {} 註冊", userRegisterRequest.getEmail(), "其他使用者");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); //400
        }

        //創建帳號
        return userDao.createUser(userRegisterRequest);
    }
}

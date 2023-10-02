package com.kyleguo.springbootmall.service.impl;

import com.kyleguo.springbootmall.dao.UserDao;
import com.kyleguo.springbootmall.dto.UserLoginRequest;
import com.kyleguo.springbootmall.dto.UserRegisterRequest;
import com.kyleguo.springbootmall.model.User;
import com.kyleguo.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
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

        // 使用 MD5 執行hash雜湊運算, 介紹如下註解
        userRegisterRequest.setPassword(DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes()));

        //創建帳號
        return userDao.createUser(userRegisterRequest);

        // MD5 執行hash雜湊運算, 雜湊值無法由加密後的值推回原本的明碼
        // 雜湊值可能會發生碰撞(上述無法回推的原因)
        // MD5 因為無法回推(解密), 故無嚴格定義上 不被當做"加密", 僅是雜湊
        // 不同人使用同樣的密碼就會發生碰撞, 所以可以用Salt處理, 降低collision
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        //檢查email
        User user = userDao.getUserByEmail(userLoginRequest.getEmail());
        if(user == null){
            log.warn("該 email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); //400
        }


        // 將使用者輸入的登入密碼轉成MD5後再與資料庫的密碼比較
        // java比較請見下方註解說明
        if(user.getPassword().equals(DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes()))){
            return user;
        } else {
            log.warn("email {} 密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); //400
        }

        // == vs equals   ==會比較記憶體位置  equals只比較內容
        // ex1:
        // String a1 = new String("abc");
        // String a2 = new String("abc");
        // a1 == a2      ===> false  (因為記憶體位置不同)
        // a1.equals(a2) ===> true   (只比較內容)

        // 但若當例子為
        // String a1 = "abc";
        // String a2 = "abc";
        // a1 == a2      ===> true (因為Java為了節省空間，第二次宣告的相同內容會直接將位址指向第一次宣告的地方)
        // a1.equals(a2) ===> true (仍然是只比較內容)
    }
}

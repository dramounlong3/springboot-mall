package com.kyleguo.springbootmall.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    Integer userId;
    String email;
    String password;
    Date createdDate;
    Date lastModifiedDate;
}

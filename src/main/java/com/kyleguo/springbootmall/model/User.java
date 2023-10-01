package com.kyleguo.springbootmall.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    Integer userId;

    // 將email回傳給前端時, 欄位變成是e_mail
    // @JsonProperty("e_mail")
    String email;

    //忽略這個值, 不傳給前端
    @JsonIgnore
    String password;

    Date createdDate;
    Date lastModifiedDate;
}

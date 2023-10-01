package com.kyleguo.springbootmall.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterRequest {

    // @NotBlank不能是NULL, 也不能是空
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}

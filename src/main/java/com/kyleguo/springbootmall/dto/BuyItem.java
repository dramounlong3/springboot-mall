package com.kyleguo.springbootmall.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BuyItem {
    @NotNull
    private Integer productId;
    @NotNull
    private Integer quantity;
}

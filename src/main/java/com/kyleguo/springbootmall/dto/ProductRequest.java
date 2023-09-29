package com.kyleguo.springbootmall.dto;

import com.kyleguo.springbootmall.constant.ProductCategory;
import lombok.Data;

import javax.validation.constraints.NotNull;


// 用做規範前端錢地參數的model，避免原本與資料庫處理的model有過多的限制在其中
@Data
public class ProductRequest {

    @NotNull
    private String productName;
    @NotNull
    private ProductCategory category;
    @NotNull
    private String imageUrl;
    @NotNull
    private Integer price;
    @NotNull
    private Integer stock;
    private String description;

}

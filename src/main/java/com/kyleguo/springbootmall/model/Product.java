package com.kyleguo.springbootmall.model;

import com.kyleguo.springbootmall.constant.ProductCategory;
import lombok.Data;

import java.util.Date;

@Data
public class Product {
    private Integer productId;
    private String productName;
    private ProductCategory category;
    private String imageUrl;
    private Integer price;
    private Integer stock;
    private String description;

    // Date類型的變數 預設使用GMT+0
    private Date createdDate;
    private Date lastModifiedDate;
}

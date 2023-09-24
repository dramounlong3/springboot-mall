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

    // UTC = GMT+0 英國(GMT+0) 2023/3/1 13:00:00 = 台灣(GMT+8) 2023/3/1 21::00:00
    // 英國和台灣同一個時間點下的時間戳(Timestamp)會相同 可用System.currentTimeMillis()取得
    // timestamp => 整數，計算從UTC 1970/1/1 00:00:00 到現在為止的總秒數
}

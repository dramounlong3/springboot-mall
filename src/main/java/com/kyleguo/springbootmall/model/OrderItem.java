package com.kyleguo.springbootmall.model;

import lombok.Data;

@Data
// 因為取得OrderItem時join，所以此model有含product的兩個欄位
public class OrderItem {

    // order_item
    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private Integer amount;

    //product 因為取order_item時，只有id，所以join product的資訊
    private String productName;
    private String imageUrl;
}

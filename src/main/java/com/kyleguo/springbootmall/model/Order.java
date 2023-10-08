package com.kyleguo.springbootmall.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Order {

    //order
    private Integer orderId;
    private Integer userId;
    private Integer totalAmount;
    private Date createdDate;
    private Date lastModifiedDate;

    // order_item
    private List<OrderItem> orderItemList;
}

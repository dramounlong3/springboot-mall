package com.kyleguo.springbootmall.service;

import com.kyleguo.springbootmall.dto.CreateOrderRequest;
import com.kyleguo.springbootmall.model.Order;

public interface OrderService {

    Order getOrderById(Integer orderId);

    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);
}

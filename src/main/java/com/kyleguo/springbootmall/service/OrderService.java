package com.kyleguo.springbootmall.service;

import com.kyleguo.springbootmall.dto.CreateOrderRequest;
import com.kyleguo.springbootmall.dto.OrderQueryParams;
import com.kyleguo.springbootmall.model.Order;

import java.util.List;

public interface OrderService {
    Integer countOrder(OrderQueryParams orderQueryParams);
    List<Order> getOrders(OrderQueryParams orderQueryParams);
    Order getOrderById(Integer orderId);
    Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest);
}

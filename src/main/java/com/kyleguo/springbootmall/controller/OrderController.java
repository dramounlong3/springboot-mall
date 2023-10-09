package com.kyleguo.springbootmall.controller;

import com.kyleguo.springbootmall.dto.CreateOrderRequest;
import com.kyleguo.springbootmall.dto.OrderQueryParams;
import com.kyleguo.springbootmall.model.Order;
import com.kyleguo.springbootmall.service.OrderService;
import com.kyleguo.springbootmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<Page<Order>> getOrders(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") @Max(1000) @Min(0)  Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset
            ) {
        OrderQueryParams orderQueryParams = new OrderQueryParams();
        orderQueryParams.setUserId(userId);
        orderQueryParams.setLimit(limit);
        orderQueryParams.setOffset(offset);

        //取得 order list
        List<Order> orderList = orderService.getOrders(orderQueryParams);

        //取得 order 總數
        Integer orderCount = orderService.countOrder(orderQueryParams);

        //分頁
        Page<Order> orderPage = new Page<>();
        orderPage.setLimit(limit);
        orderPage.setOffset(offset);
        orderPage.setTotal(orderCount);
        orderPage.setResults(orderList);

        return ResponseEntity.status(HttpStatus.OK).body(orderPage);
    }


    // 使用者必須先有帳號才能創建訂單, 所以path則以users開頭
    @PostMapping("/users/{userId}/orders")
    public ResponseEntity<Order> createOrder(@PathVariable Integer userId,
                                         @RequestBody @Valid CreateOrderRequest createOrderRequest) {

        Integer orderId = orderService.createOrder(userId, createOrderRequest);

        Order order = orderService.getOrderById(orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}

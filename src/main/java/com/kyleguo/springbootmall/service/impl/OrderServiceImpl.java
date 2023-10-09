package com.kyleguo.springbootmall.service.impl;

import com.kyleguo.springbootmall.dao.OrderDao;
import com.kyleguo.springbootmall.dao.ProductDao;
import com.kyleguo.springbootmall.dao.UserDao;
import com.kyleguo.springbootmall.dto.BuyItem;
import com.kyleguo.springbootmall.dto.CreateOrderRequest;
import com.kyleguo.springbootmall.dto.OrderQueryParams;
import com.kyleguo.springbootmall.model.Order;
import com.kyleguo.springbootmall.model.OrderItem;
import com.kyleguo.springbootmall.model.Product;
import com.kyleguo.springbootmall.model.User;
import com.kyleguo.springbootmall.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private UserDao userDao;

    private final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public Integer countOrder(OrderQueryParams orderQueryParams) {
        return orderDao.countOrder(orderQueryParams);
    }

    @Override
    public List<Order> getOrders(OrderQueryParams orderQueryParams) {
        List<Order> orderList = orderDao.getOrders(orderQueryParams);

        for(Order order : orderList) {
            List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(order.getOrderId());

            order.setOrderItemList(orderItemList);
        }

        return  orderList;
    }

    @Override
    public Order getOrderById(Integer orderId) {

        //主檔
        Order order = orderDao.getOrderById(orderId);

        //明細檔
        List<OrderItem> orderItemList = orderDao.getOrderItemsByOrderId(orderId);

        // 將主檔內新增一個含明細檔的變數, 並設定到主檔, 一起回傳主明細檔給前端
        order.setOrderItemList(orderItemList);

        return order;
    }

    @Override
    @Transactional //因為table: order和order_item 兩張table的變更需同時成功或失敗
    public Integer createOrder(Integer userId, CreateOrderRequest createOrderRequest) {

        //檢查user是否存在
        User user = userDao.getUserById(userId);
        if(user == null) {
            log.warn("該 userId {} 不存在", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        //累積使用者此訂單的所有商品總價格
        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        //由前端的buyItem, 取得productId, 再由productDao 取得每一項商品的資訊
        for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {
            Product product = productDao.getProductById(buyItem.getProductId());

            //檢查 product是否存在、庫存是否足夠
            if (product == null) {
                log.warn("商品 {} 不存在", buyItem.getProductId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            if(buyItem.getQuantity() > product.getStock()) {
                log.warn("商品 {} 庫存數量不足，無法購買。剩餘庫存 {}，欲購買數量 {}", buyItem.getProductId(), product.getStock(), buyItem.getQuantity());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            //扣除商品庫存
            productDao.updateStock(product.getProductId(), product.getStock() - buyItem.getQuantity());

            //計算總價錢, 藉由使用者買的數量和從資料庫查出來的價格, 計算後得出該單一品項的總價格
            //totalAmount紀錄所有品項的總價格
            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;

            //轉換BuyItem to OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(buyItem.getProductId());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);

            orderItemList.add(orderItem);
        }

        //創建訂單
        Integer orderId = orderDao.createOrder(userId, totalAmount);

        orderDao.createOrderItems(orderId, orderItemList);

        return orderId;
    }
}

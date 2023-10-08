package com.kyleguo.springbootmall.service.impl;

import com.kyleguo.springbootmall.dao.OrderDao;
import com.kyleguo.springbootmall.dao.ProductDao;
import com.kyleguo.springbootmall.dto.BuyItem;
import com.kyleguo.springbootmall.dto.CreateOrderRequest;
import com.kyleguo.springbootmall.model.Order;
import com.kyleguo.springbootmall.model.OrderItem;
import com.kyleguo.springbootmall.model.Product;
import com.kyleguo.springbootmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ProductDao productDao;

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
        //累積使用者此訂單的所有商品總價格
        int totalAmount = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        //由前端的buyItem, 取得productId, 再由productDao 取得每一項商品的資訊
        for (BuyItem buyItem : createOrderRequest.getBuyItemList()) {
            Product product = productDao.getProductById(buyItem.getProductId());

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

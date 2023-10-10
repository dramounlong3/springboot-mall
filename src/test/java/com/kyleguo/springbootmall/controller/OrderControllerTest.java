package com.kyleguo.springbootmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyleguo.springbootmall.dto.BuyItem;
import com.kyleguo.springbootmall.dto.CreateOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 創建訂單
    @Test
    @Transactional
    public void createOrder_success() throws Exception {

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();

        // 第一個購買品項
        BuyItem buyItem1 =new BuyItem();
        buyItem1.setProductId(1);
        buyItem1.setQuantity(5);
        buyItemList.add(buyItem1);

        // 第二個購買品項
        BuyItem buyItem2 =new BuyItem();
        buyItem2.setProductId(2);
        buyItem2.setQuantity(2);
        buyItemList.add(buyItem2);

        // 每個品項的productId和數量
        createOrderRequest.setBuyItemList(buyItemList);

        String json = objectMapper.writeValueAsString(createOrderRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.userId", equalTo(1)))
                .andExpect(jsonPath("$.totalAmount", equalTo(750)))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()))
                .andExpect(jsonPath("$.orderItemList", hasSize(2)));
    }

    @Test
    @Transactional
    public void createOrder_illegalArgument_emptyBuyItemList() throws Exception{
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();
        //empty list
        createOrderRequest.setBuyItemList(buyItemList);

        String json = objectMapper.writeValueAsString(createOrderRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest()); // = status().is(400)
    }

    @Test
    @Transactional
    public void createOrder_userNotExist() throws Exception {
        CreateOrderRequest createOrderRequest =new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();
        BuyItem buyItem = new BuyItem();
        buyItem.setProductId(1);
        buyItem.setQuantity(1);
        buyItemList.add(buyItem);

        createOrderRequest.setBuyItemList(buyItemList);

        String json = objectMapper.writeValueAsString(createOrderRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders", 100) //UserNotExist
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void createOrder_productNotExist() throws Exception {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();

        BuyItem buyItem = new BuyItem();
        buyItem.setProductId(100); //ProductNotExist
        buyItem.setQuantity(1);
        buyItemList.add(buyItem);

        createOrderRequest.setBuyItemList(buyItemList);

        String json = objectMapper.writeValueAsString(createOrderRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    @Test
    @Transactional
    public void createOrder_stockNotEnough() throws Exception {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        List<BuyItem> buyItemList = new ArrayList<>();
        BuyItem buyItem = new BuyItem();

        buyItem.setProductId(1);
        buyItem.setQuantity(10000);
        buyItemList.add(buyItem);

        createOrderRequest.setBuyItemList(buyItemList);

        String json = objectMapper.writeValueAsString(createOrderRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/users/{userId}/orders",1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(400));
    }

    // 查詢訂單列表

    @Test
    public void getOrders_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 1);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.limit", equalTo(10)))
                .andExpect(jsonPath("$.offset", equalTo(0)))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[0].orderId", equalTo(3)))
                .andExpect(jsonPath("$.results[0].userId", equalTo(1)))
                .andExpect(jsonPath("$.results[0].totalAmount", equalTo(100000)))
                .andExpect(jsonPath("$.results[0].orderItemList", hasSize(0)))
                .andExpect(jsonPath("$.results[0].createdDate", notNullValue()))
                .andExpect(jsonPath("$.results[0].lastModifiedDate", notNullValue()))
                .andExpect(jsonPath("$.results[1].orderId", equalTo(2)))
                .andExpect(jsonPath("$.results[1].userId", equalTo(1)))
                .andExpect(jsonPath("$.results[1].totalAmount", equalTo(500690)))
                .andExpect(jsonPath("$.results[1].orderItemList", hasSize(1)))
                .andExpect(jsonPath("$.results[1].createdDate", notNullValue()))
                .andExpect(jsonPath("$.results[1].lastModifiedDate", notNullValue()));
    }

    @Test
    public void getOrders_pagination() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders",1)
                .param("limit","2")
                .param("offset", "2");

        mockMvc.perform(requestBuilder)
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.limit", equalTo(2)))
                .andExpect(jsonPath("$.offset", equalTo(2)))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(0)));
    }

    @Test
    public void getOrders_userHasNoOrder() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders", 2);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit",equalTo(10)))
                .andExpect(jsonPath("$.offset",equalTo(0)))
                .andExpect(jsonPath("$.total",equalTo(0)))
                .andExpect(jsonPath("$.results",hasSize(0)));
    }

    @Test
    public void getOrders_userNotExist() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/users/{userId}/orders",100);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limit",equalTo(10)))
                .andExpect(jsonPath("$.offset",equalTo(0)))
                .andExpect(jsonPath("$.total",equalTo(0)))
                .andExpect(jsonPath("$.results",hasSize(0)));
    }



}
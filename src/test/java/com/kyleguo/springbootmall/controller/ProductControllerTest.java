package com.kyleguo.springbootmall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyleguo.springbootmall.constant.ProductCategory;
import com.kyleguo.springbootmall.dto.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 查詢商品
    @Test
    public void getProduct_success() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}", 1)
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName", equalTo("蘋果（澳洲）")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl",notNullValue()))
                .andExpect(jsonPath("$.price",notNullValue()))
                .andExpect(jsonPath("$.stock",notNullValue()))
                .andExpect(jsonPath("$.description",notNullValue()))
                .andExpect(jsonPath("$.createdDate",notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate",notNullValue()));
    }

    @Test
    public void getProduct_notFound() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products/{productId}", 500000);

        mockMvc.perform(requestBuilder)
                // 兩種寫法都可以表示404
                .andExpect(status().isNotFound())
                .andExpect(status().is(404));
    }

    // 創建商品
    @Test
    @Transactional
    public void createProduct_success() throws Exception {
        // 測資
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(100);
        productRequest.setStock(2);

        // 將測資轉格式為json的字串
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.productName", equalTo("test food product")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.price", equalTo(100)))
                .andExpect(jsonPath("$.stock", equalTo(2)))
                .andExpect(jsonPath("$.description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    @Test
    @Transactional
    public void createProduct_illegalArgument() throws  Exception{
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product"); //只設定名稱

        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest()); // 400
    }

    // 更新商品
    @Test
    @Transactional
    public void updateProduct() throws Exception {
        // 測資
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(100);
        productRequest.setStock(2);

        // 將測資轉格式為json的字串
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.productName", equalTo("test food product")))
                .andExpect(jsonPath("$.category", equalTo("FOOD")))
                .andExpect(jsonPath("$.imageUrl", equalTo("http://test.com")))
                .andExpect(jsonPath("$.price", equalTo(100)))
                .andExpect(jsonPath("$.stock", equalTo(2)))
                .andExpect(jsonPath("$.description", nullValue()))
                .andExpect(jsonPath("$.createdDate", notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate", notNullValue()));
    }

    @Test
    @Transactional
    public void updateProduct_illArgument() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product"); //只設定名稱

        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest()); // 400
    }

    @Test
    @Transactional
    public void updateProduct_productNotFound() throws Exception {
        // 測資
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("test food product");
        productRequest.setCategory(ProductCategory.FOOD);
        productRequest.setImageUrl("http://test.com");
        productRequest.setPrice(100);
        productRequest.setStock(2);

        // 將測資轉格式為json的字串
        String json = objectMapper.writeValueAsString(productRequest);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/products/{productId}", 500000) // id不存在
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound()); // 404
    }

    // 刪除商品
    @Test
    @Transactional
    public void deleteProduct_success() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}", 5);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent()); // 204
    }

    @Test
    @Transactional
    public void deleteProduct_deleteNonExistingProduct() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/products/{productId}", 500000); //id不存在

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent()); // 204
    }

    // 查詢商品列表
    @Test
    public void getProducts() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk()) //200
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(5))); //array length, 因為limit預設的限制只有5
    }

    @Test
    public void getProducts_filtering() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("search", "B")
                .param("category","CAR");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()) //200
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(2)));
    }

    @Test
    public void getProducts_sorting() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("orderBy", "price")
                .param("sort","desc");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk()) //200
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.limit").isNumber()) //是否為數字
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(5)))
                .andExpect(jsonPath("$.results").isArray()) //是否為array
                .andExpect(jsonPath("$.results[0].productId", equalTo(6)))
                .andExpect(jsonPath("$.results[1].productId", equalTo(5)))
                .andExpect(jsonPath("$.results[2].productId", equalTo(7)))
                .andExpect(jsonPath("$.results[3].productId", equalTo(4)))
                .andExpect(jsonPath("$.results[4].productId", equalTo(2)));
    }

    @Test
    public void getProducts_pagination() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/products")
                .param("limit", "2")
                .param("offset","2")
                .accept(MediaType.APPLICATION_JSON_UTF8);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk()) //200
                .andExpect(jsonPath("$.limit", notNullValue()))
                .andExpect(jsonPath("$.limit").isNumber()) //是否為數字
                .andExpect(jsonPath("$.offset", notNullValue()))
                .andExpect(jsonPath("$.total", notNullValue()))
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results").isArray()) //是否為array
                .andExpect(jsonPath("$.results[0].productId", equalTo(5)))
                .andExpect(jsonPath("$.results[0].productName", equalTo("BMW")))
                .andExpect(jsonPath("$.results[1].productId", equalTo(4)))
                .andExpect(jsonPath("$.results[1].productName", equalTo("Toyota")));
    }
}
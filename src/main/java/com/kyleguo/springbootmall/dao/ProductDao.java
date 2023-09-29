package com.kyleguo.springbootmall.dao;

import com.kyleguo.springbootmall.dto.ProductRequest;
import com.kyleguo.springbootmall.model.Product;

public interface ProductDao {
    Product getProductById(Integer productId);
    Integer createProduct(ProductRequest productRequest);

    void updateProduct(Integer productId, ProductRequest productRequest);
}

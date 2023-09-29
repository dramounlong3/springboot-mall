package com.kyleguo.springbootmall.service;

import com.kyleguo.springbootmall.dto.ProductRequest;
import com.kyleguo.springbootmall.model.Product;

public interface ProductService {
    Product getProductById(Integer productId);
    Integer createProduct(ProductRequest productRequest);
    void updateProduct(Integer productId, ProductRequest productRequest);
    void deleteProductById(Integer productId);
}

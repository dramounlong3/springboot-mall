package com.kyleguo.springbootmall.dao;

import com.kyleguo.springbootmall.model.Product;

public interface ProductDao {
    Product getProductById(Integer productId);
}
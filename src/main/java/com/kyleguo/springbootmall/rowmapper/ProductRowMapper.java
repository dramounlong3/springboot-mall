package com.kyleguo.springbootmall.rowmapper;

import com.kyleguo.springbootmall.constant.ProductCategory;
import com.kyleguo.springbootmall.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));

//        // 從DB撈出category字串
//        String categoryStr = rs.getString("category");
//        // 由字串內容透過valueOf轉Enum類型
//        ProductCategory category = ProductCategory.valueOf(categoryStr);
//        // 設定到enum變數上
//        product.setCategory(category);

        // 寫一行取代上三行
        product.setCategory(ProductCategory.valueOf(rs.getString("category")));



        product.setImageUrl(rs.getString("image_url"));
        product.setPrice(rs.getInt("price"));
        product.setStock(rs.getInt("stock"));
        product.setDescription(rs.getString("description"));
        product.setCreatedDate(rs.getTimestamp("created_date"));
        product.setLastModifiedDate(rs.getTimestamp("last_modified_date"));

        return product;
    }
}

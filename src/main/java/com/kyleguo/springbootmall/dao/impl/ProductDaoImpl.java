package com.kyleguo.springbootmall.dao.impl;

import com.kyleguo.springbootmall.dao.ProductDao;
import com.kyleguo.springbootmall.dto.ProductQueryParams;
import com.kyleguo.springbootmall.dto.ProductRequest;
import com.kyleguo.springbootmall.model.Product;
import com.kyleguo.springbootmall.rowmapper.ProductRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductDaoImpl implements ProductDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Integer countProduct(ProductQueryParams productQueryParams) {
        String sql = "SELECT COUNT(*) FROM product WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
//        if(productQueryParams.getCategory() != null) {
//            sql += " AND category = :category";
//            map.put("category", productQueryParams.getCategory().name());
//        }
//
//        if(productQueryParams.getSearch() != null) {
//            sql += " AND product_name LIKE :search";
//            map.put("search", "%" + productQueryParams.getSearch() + "%");
//        }

        //將上述註解的段落改以方法執行
        sql = addFilteringSql(sql, map, productQueryParams);


        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;

    }

    @Override
    public List<Product> getProducts(ProductQueryParams productQueryParams) {
        String sql = "SELECT product_id, product_name, category, image_url, price, stock, description, " +
                 "created_date, last_modified_date " +
                 "FROM product " +
                 "WHERE 1=1";

        Map<String,Object> map = new HashMap<>();

        // 查詢條件
//        if(productQueryParams.getCategory() != null) {
//           //補上空白 跟上面的WHERE 條件隔開
//           sql += " AND category = :category";
//           map.put("category", productQueryParams.getCategory().name());
//        }
//
//        if(productQueryParams.getSearch() != null) {
//            //LIKE的百分比只能加在map, 並且需要用+號隔開接字串, 此為springboot的限制
//            sql += " AND product_name LIKE :search";
//            map.put("search", "%" + productQueryParams.getSearch() + "%");
//        }

        //將上述註解的段落改以方法執行
        sql = addFilteringSql(sql, map, productQueryParams);

        // 因為spring boot的限制, order by只能用字串拼接的方式執行, 無法用 :...
        // 預設要提供最新的商品給客戶看, 所以不用增基判斷語句
        // 排序
        sql += " ORDER BY " + productQueryParams.getOrderBy() + " " + productQueryParams.getSort();

        // 分頁
        sql += " LIMIT :limit OFFSET :offset";
        map.put("limit",productQueryParams.getLimit());
        map.put("offset",productQueryParams.getOffset());

       //List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        //合併上一段程式碼, 省掉宣告
       return namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

    }

    @Override
    public Product getProductById(Integer productId) {
        String sql = "SELECT product_id, product_name, category, image_url, price, stock, description, " +
                "created_date, last_modified_date " +
                "FROM product WHERE product_id = :productId";

        Map<String, Object> map = new HashMap<>();
        map.put("productId",productId);

        List<Product> productList = namedParameterJdbcTemplate.query(sql, map, new ProductRowMapper());

        if(!productList.isEmpty()) {
            return productList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createProduct(ProductRequest productRequest) {
        String sql = "INSERT INTO product(product_name, category, image_url, price, stock, description, created_date, last_modified_date)" +
                     "VALUES(:product_name, :category, :image_url, :price, :stock, :description, :created_date, :last_modified_date)";

        Map<String, Object> map = new HashMap<>();
        map.put("product_name", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("image_url", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());

        Date now = new Date();
        map.put("created_date", now);
        map.put("last_modified_date", now);


        KeyHolder keyHolder = new  GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        //int productId = keyHolder.getKey().intValue();
        //改寫, 省略宣告和避免NullPointerException
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    @Override
    public void updateProduct(Integer productId, ProductRequest productRequest) {
        String sql = "UPDATE product " +
                     "SET product_name = :product_name, " +
                     "category = :category," +
                     "image_url = :image_url," +
                     "price = :price," +
                     "stock = :stock," +
                     "description = :description," +
                     "last_modified_date = :last_modified_date " +
                     "WHERE product_id = :product_id";

        Map<String, Object> map = new HashMap<>();
        map.put("product_id", productId);
        map.put("product_name", productRequest.getProductName());
        map.put("category", productRequest.getCategory().toString());
        map.put("image_url", productRequest.getImageUrl());
        map.put("price", productRequest.getPrice());
        map.put("stock", productRequest.getStock());
        map.put("description", productRequest.getDescription());
        map.put("last_modified_date", new Date());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void updateStock(Integer productId, Integer stock) {
        String sql = "UPDATE product SET stock = :stock, last_modified_date = :lastModifiedDate " +
                     "WHERE product_id = :productId";
        Map<String, Object> map = new HashMap<>();
        map.put("stock", stock);
        map.put("productId", productId);
        map.put("lastModifiedDate", new Date());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteProductById(Integer productId) {
        String sql = "DELETE FROM product WHERE product_id = :product_id";

        Map<String, Object> map = new HashMap<>();
        map.put("product_id", productId);

        namedParameterJdbcTemplate.update(sql, map);
    }

    // 查詢條件拼接SQL語句
    // 方法只有在這個class被用到, 優先使用private, 若設為public, 則必須確認其他class是否有使用到此方法
    private String addFilteringSql(String sql, Map<String, Object> map, ProductQueryParams productQueryParams) {
        if(productQueryParams.getCategory() != null) {
            sql += " AND category = :category";
            map.put("category", productQueryParams.getCategory().name());
        }

        if(productQueryParams.getSearch() != null) {
            sql += " AND product_name LIKE :search";
            map.put("search", "%" + productQueryParams.getSearch() + "%");
        }

        return sql;
    }
}

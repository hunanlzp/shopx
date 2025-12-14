package com.shopx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopx.entity.Product;
import com.shopx.util.ResponseUtil;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 获取商品列表（分页）
     */
    ResponseUtil.PageResult<Product> getProducts(int page, int size, String keyword, String category);
    
    /**
     * 根据ID获取商品详情
     */
    Product getProductById(Long id);
    
    /**
     * 创建商品
     */
    Product createProduct(Product product);
    
    /**
     * 更新商品
     */
    Product updateProduct(Long id, Product product);
    
    /**
     * 删除商品
     */
    boolean deleteProduct(Long id);
    
    /**
     * 批量删除商品
     */
    boolean batchDeleteProducts(List<Long> ids);
    
    /**
     * 更新商品库存
     */
    boolean updateStock(Long id, Integer quantity);
    
    /**
     * 检查商品库存
     */
    boolean checkStock(Long id, Integer quantity);
    
    /**
     * 获取热门商品
     */
    List<Product> getHotProducts(int limit);
    
    /**
     * 获取推荐商品
     */
    List<Product> getRecommendedProducts(Long userId, int limit);
    
    /**
     * 搜索商品
     */
    ResponseUtil.PageResult<Product> searchProducts(String keyword, int page, int size);
    
    /**
     * 根据分类获取商品
     */
    ResponseUtil.PageResult<Product> getProductsByCategory(String category, int page, int size);
}
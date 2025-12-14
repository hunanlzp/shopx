package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.cache.CacheManager;
import com.shopx.constant.Constants;
import com.shopx.entity.Product;
import com.shopx.exception.BusinessException;
import com.shopx.mapper.ProductMapper;
import com.shopx.service.ProductService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import com.shopx.validation.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Override
    public ResponseUtil.PageResult<Product> getProducts(int page, int size, String keyword, String category) {
        log.info("获取商品列表: page={}, size={}, keyword={}, category={}", page, size, keyword, category);
        
        // 参数校验
        ValidationUtils.validPageParams(page, size, "分页参数错误");
        
        // 构建查询条件
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("name", keyword)
                       .or()
                       .like("description", keyword);
        }
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        // 只查询启用的商品
        queryWrapper.eq("enabled", true);
        queryWrapper.orderByDesc("create_time");
        
        // 分页查询
        Page<Product> pageParam = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<Product>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public Product getProductById(Long id) {
        log.info("获取商品详情: id={}", id);
        
        ValidationUtils.validId(id, "商品ID不能为空");
        
        // 先从缓存获取
        Product product = cacheManager.getProductCache(id);
        if (product != null) {
            log.debug("从缓存获取商品: id={}", id);
            return product;
        }
        
        // 从数据库获取
        product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 设置缓存
        cacheManager.setProductCache(id, product);
        
        return product;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product createProduct(Product product) {
        log.info("创建商品: name={}", product.getName());
        
        // 参数校验
        ValidationUtils.notBlank(product.getName(), "商品名称不能为空");
        ValidationUtils.validPositive(product.getPrice(), "商品价格必须大于0");
        ValidationUtils.validNonNegative(product.getStock(), "商品库存不能为负数");
        
        // 设置创建者
        Long currentUserId = SaTokenUtil.getCurrentUserId();
        if (currentUserId != null) {
            product.setCreateBy(currentUserId);
        }
        
        // 设置默认值
        if (product.getEnabled() == null) {
            product.setEnabled(true);
        }
        if (product.getLikeCount() == null) {
            product.setLikeCount(0);
        }
        if (product.getShareCount() == null) {
            product.setShareCount(0);
        }
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        
        // 保存商品
        productMapper.insert(product);
        
        // 清除相关缓存
        cacheManager.delete(Constants.CacheKey.PRODUCT_PREFIX + "list:*");
        
        log.info("商品创建成功: id={}, name={}", product.getId(), product.getName());
        return product;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product updateProduct(Long id, Product product) {
        log.info("更新商品: id={}", id);
        
        ValidationUtils.validId(id, "商品ID不能为空");
        
        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 检查权限：普通用户只能更新自己创建的商品
        if (!SaTokenUtil.isAdmin() && !SaTokenUtil.isSeller()) {
            Long currentUserId = SaTokenUtil.getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(existingProduct.getCreateBy())) {
                throw new BusinessException(403, "只能更新自己创建的商品");
            }
        }
        
        // 更新商品信息
        product.setId(id);
        productMapper.updateById(product);
        
        // 清除缓存
        cacheManager.deleteProductCache(id);
        cacheManager.delete(Constants.CacheKey.PRODUCT_PREFIX + "list:*");
        
        log.info("商品更新成功: id={}", id);
        return product;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long id) {
        log.info("删除商品: id={}", id);
        
        ValidationUtils.validId(id, "商品ID不能为空");
        
        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(id);
        if (existingProduct == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        // 检查权限：普通用户只能删除自己创建的商品
        if (!SaTokenUtil.isAdmin() && !SaTokenUtil.isSeller()) {
            Long currentUserId = SaTokenUtil.getCurrentUserId();
            if (currentUserId == null || !currentUserId.equals(existingProduct.getCreateBy())) {
                throw new BusinessException(403, "只能删除自己创建的商品");
            }
        }
        
        // 软删除：设置enabled为false
        existingProduct.setEnabled(false);
        productMapper.updateById(existingProduct);
        
        // 清除缓存
        cacheManager.deleteProductCache(id);
        cacheManager.delete(Constants.CacheKey.PRODUCT_PREFIX + "list:*");
        
        log.info("商品删除成功: id={}", id);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteProducts(List<Long> ids) {
        log.info("批量删除商品: ids={}", ids);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "商品ID列表不能为空");
        }
        
        // 检查权限
        if (!SaTokenUtil.isAdmin() && !SaTokenUtil.isSeller()) {
            throw new BusinessException(403, "批量删除需要管理员或商家权限");
        }
        
        // 批量软删除
        for (Long id : ids) {
            Product product = productMapper.selectById(id);
            if (product != null) {
                product.setEnabled(false);
                productMapper.updateById(product);
                cacheManager.deleteProductCache(id);
            }
        }
        
        cacheManager.delete(Constants.CacheKey.PRODUCT_PREFIX + "list:*");
        
        log.info("批量删除商品成功: count={}", ids.size());
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStock(Long id, Integer quantity) {
        log.info("更新商品库存: id={}, quantity={}", id, quantity);
        
        ValidationUtils.validId(id, "商品ID不能为空");
        ValidationUtils.validNonNegative(quantity, "库存数量不能为负数");
        
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(404, "商品不存在");
        }
        
        product.setStock(quantity);
        productMapper.updateById(product);
        
        // 清除缓存
        cacheManager.deleteProductCache(id);
        
        log.info("商品库存更新成功: id={}, newStock={}", id, quantity);
        return true;
    }
    
    @Override
    public boolean checkStock(Long id, Integer quantity) {
        log.debug("检查商品库存: id={}, quantity={}", id, quantity);
        
        Product product = getProductById(id);
        return product.getStock() >= quantity;
    }
    
    @Override
    public List<Product> getHotProducts(int limit) {
        log.info("获取热门商品: limit={}", limit);
        
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", true)
                   .orderByDesc("view_count")
                   .orderByDesc("like_count")
                   .last("LIMIT " + limit);
        
        return productMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<Product> getRecommendedProducts(Long userId, int limit) {
        log.info("获取推荐商品: userId={}, limit={}", userId, limit);
        
        // 这里可以实现更复杂的推荐算法
        // 目前简单实现：获取热门商品
        return getHotProducts(limit);
    }
    
    @Override
    public ResponseUtil.PageResult<Product> searchProducts(String keyword, int page, int size) {
        log.info("搜索商品: keyword={}, page={}, size={}", keyword, page, size);
        
        ValidationUtils.notBlank(keyword, "搜索关键词不能为空");
        ValidationUtils.validPageParams(page, size, "分页参数错误");
        
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", keyword)
                   .or()
                   .like("description", keyword)
                   .or()
                   .like("lifestyle_tags", keyword);
        queryWrapper.eq("enabled", true);
        queryWrapper.orderByDesc("create_time");
        
        Page<Product> pageParam = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<Product>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public ResponseUtil.PageResult<Product> getProductsByCategory(String category, int page, int size) {
        log.info("根据分类获取商品: category={}, page={}, size={}", category, page, size);
        
        ValidationUtils.notBlank(category, "商品分类不能为空");
        ValidationUtils.validPageParams(page, size, "分页参数错误");
        
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category", category)
                   .eq("enabled", true)
                   .orderByDesc("create_time");
        
        Page<Product> pageParam = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<Product>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
}

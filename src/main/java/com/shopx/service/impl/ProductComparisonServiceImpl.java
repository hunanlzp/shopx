package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.Product;
import com.shopx.entity.ProductComparison;
import com.shopx.exception.BusinessException;
import com.shopx.mapper.ProductComparisonMapper;
import com.shopx.mapper.ProductMapper;
import com.shopx.service.ProductComparisonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品对比服务实现类
 */
@Slf4j
@Service
public class ProductComparisonServiceImpl extends ServiceImpl<ProductComparisonMapper, ProductComparison> implements ProductComparisonService {
    
    @Autowired
    private ProductComparisonMapper comparisonMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    private static final int MAX_COMPARISON_PRODUCTS = 5;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductComparison createComparison(Long userId, String comparisonName, List<Long> productIds, Boolean isPublic) {
        log.info("创建商品对比: userId={}, comparisonName={}, productIds={}", userId, comparisonName, productIds);
        
        if (productIds == null || productIds.isEmpty()) {
            throw new BusinessException(400, "商品列表不能为空");
        }
        
        if (productIds.size() > MAX_COMPARISON_PRODUCTS) {
            throw new BusinessException(400, "最多只能对比" + MAX_COMPARISON_PRODUCTS + "个商品");
        }
        
        // 检查商品是否存在
        List<Product> products = productMapper.selectBatchIds(productIds);
        if (products.size() != productIds.size()) {
            throw new BusinessException(400, "部分商品不存在");
        }
        
        // 创建对比列表
        ProductComparison comparison = new ProductComparison();
        comparison.setUserId(userId);
        comparison.setComparisonName(comparisonName != null ? comparisonName : "商品对比");
        comparison.setProductIds(convertToJsonArray(productIds));
        comparison.setIsPublic(isPublic != null ? isPublic : false);
        
        // 生成分享链接
        if (Boolean.TRUE.equals(isPublic)) {
            String shareLink = UUID.randomUUID().toString();
            comparison.setShareLink(shareLink);
        }
        
        comparisonMapper.insert(comparison);
        return comparison;
    }
    
    @Override
    public com.shopx.dto.ComparisonTableDTO getComparisonDetails(Long comparisonId) {
        ProductComparison comparison = comparisonMapper.selectById(comparisonId);
        if (comparison == null) {
            throw new BusinessException(404, "对比列表不存在");
        }
        
        // 解析商品ID列表
        List<Long> productIds = parseJsonArray(comparison.getProductIds());
        
        // 生成对比表格
        return generateComparisonTable(productIds);
    }
    
    @Override
    public List<ProductComparison> getUserComparisons(Long userId) {
        QueryWrapper<ProductComparison> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        return comparisonMapper.selectList(queryWrapper);
    }
    
    @Override
    public ProductComparison getComparisonByShareLink(String shareLink) {
        QueryWrapper<ProductComparison> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("share_link", shareLink)
                   .eq("is_public", true);
        return comparisonMapper.selectOne(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductComparison updateComparison(Long comparisonId, Long userId, List<Long> productIds) {
        ProductComparison comparison = comparisonMapper.selectById(comparisonId);
        if (comparison == null || !comparison.getUserId().equals(userId)) {
            throw new BusinessException(404, "对比列表不存在或无权限");
        }
        
        if (productIds != null) {
            if (productIds.size() > MAX_COMPARISON_PRODUCTS) {
                throw new BusinessException(400, "最多只能对比" + MAX_COMPARISON_PRODUCTS + "个商品");
            }
            comparison.setProductIds(convertToJsonArray(productIds));
        }
        
        comparisonMapper.updateById(comparison);
        return comparison;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComparison(Long comparisonId, Long userId) {
        ProductComparison comparison = comparisonMapper.selectById(comparisonId);
        if (comparison == null || !comparison.getUserId().equals(userId)) {
            return false;
        }
        
        comparisonMapper.deleteById(comparisonId);
        return true;
    }
    
    @Override
    public com.shopx.dto.ComparisonTableDTO generateComparisonTable(List<Long> productIds) {
        List<Product> products = productMapper.selectBatchIds(productIds);
        
        com.shopx.dto.ComparisonTableDTO table = new com.shopx.dto.ComparisonTableDTO();
        
        // 商品列表
        List<com.shopx.dto.ComparisonTableDTO.ProductComparisonItemDTO> productItems = products.stream()
            .map(product -> {
                com.shopx.dto.ComparisonTableDTO.ProductComparisonItemDTO item = 
                    new com.shopx.dto.ComparisonTableDTO.ProductComparisonItemDTO();
                item.setId(product.getId());
                item.setName(product.getName());
                java.util.Map<String, Object> properties = new java.util.HashMap<>();
                properties.put("price", product.getPrice());
                properties.put("category", product.getCategory());
                properties.put("stock", product.getStock());
                properties.put("viewCount", product.getViewCount());
                properties.put("likeCount", product.getLikeCount());
                properties.put("has3dPreview", product.getHas3dPreview());
                properties.put("isRecyclable", product.getIsRecyclable());
                properties.put("isRentable", product.getIsRentable());
                item.setProperties(properties);
                return item;
            })
            .collect(Collectors.toList());
        
        table.setProducts(productItems);
        
        // 属性对比
        List<com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO> attributes = new ArrayList<>();
        
        // 商品名称
        com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO nameAttr = 
            new com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO();
        nameAttr.setName("商品名称");
        nameAttr.setValues(products.stream().map(Product::getName).collect(Collectors.toList()));
        attributes.add(nameAttr);
        
        // 价格
        com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO priceAttr = 
            new com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO();
        priceAttr.setName("价格");
        priceAttr.setValues(products.stream().map(Product::getPrice).collect(Collectors.toList()));
        attributes.add(priceAttr);
        
        // 分类
        com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO categoryAttr = 
            new com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO();
        categoryAttr.setName("分类");
        categoryAttr.setValues(products.stream().map(Product::getCategory).collect(Collectors.toList()));
        attributes.add(categoryAttr);
        
        // 库存
        com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO stockAttr = 
            new com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO();
        stockAttr.setName("库存");
        stockAttr.setValues(products.stream().map(Product::getStock).collect(Collectors.toList()));
        attributes.add(stockAttr);
        
        // 3D预览
        com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO previewAttr = 
            new com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO();
        previewAttr.setName("3D预览");
        previewAttr.setValues(products.stream()
            .map(p -> p.getHas3dPreview() != null && p.getHas3dPreview())
            .collect(Collectors.toList()));
        attributes.add(previewAttr);
        
        // 可回收
        com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO recycleAttr = 
            new com.shopx.dto.ComparisonTableDTO.AttributeComparisonDTO();
        recycleAttr.setName("可回收");
        recycleAttr.setValues(products.stream()
            .map(p -> p.getIsRecyclable() != null && p.getIsRecyclable())
            .collect(Collectors.toList()));
        attributes.add(recycleAttr);
        
        table.setAttributes(attributes);
        
        return table;
    }
    
    private String convertToJsonArray(List<Long> ids) {
        return "[" + ids.stream().map(String::valueOf).collect(Collectors.joining(",")) + "]";
    }
    
    private List<Long> parseJsonArray(String json) {
        if (json == null || json.trim().isEmpty() || !json.startsWith("[")) {
            return new ArrayList<>();
        }
        
        String content = json.substring(1, json.length() - 1);
        if (content.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(content.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}


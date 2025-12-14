package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.config.ProductAuditConfig;
import com.shopx.constant.Constants;
import com.shopx.entity.Product;
import com.shopx.entity.ProductAudit;
import com.shopx.mapper.ProductAuditMapper;
import com.shopx.mapper.ProductMapper;
import com.shopx.service.ProductAuditService;
import com.shopx.service.ProductImageService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品审核服务实现类
 */
@Slf4j
@Service
public class ProductAuditServiceImpl extends ServiceImpl<ProductAuditMapper, ProductAudit> implements ProductAuditService {
    
    @Autowired
    private ProductAuditMapper auditMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductImageService productImageService;
    
    @Autowired
    private ProductAuditConfig auditConfig;
    
    @Override
    public int calculateCompletenessScore(Product product) {
        ProductAuditConfig.ScoreWeights weights = auditConfig.getScoreWeights();
        int score = 0;
        
        // 基本信息（40分）
        if (StringUtils.hasText(product.getName())) score += weights.getName();
        if (StringUtils.hasText(product.getDescription())) score += weights.getDescription();
        if (product.getPrice() != null && product.getPrice().doubleValue() > 0) score += weights.getPrice();
        if (StringUtils.hasText(product.getCategory())) score += weights.getCategory();
        
        // 图片（20分）- 至少3张图片
        if (productImageService.checkImageCount(product.getId(), weights.getMinImageCount())) {
            score += weights.getImages();
        } else {
            List<com.shopx.entity.ProductImage> images = productImageService.getProductImages(product.getId());
            score += Math.min(images.size() * weights.getImageScorePerItem(), weights.getImages());
        }
        
        // 详细属性（20分）
        if (StringUtils.hasText(product.getSuitableScenarios())) score += weights.getScenarios();
        if (StringUtils.hasText(product.getLifestyleTags())) score += weights.getLifestyleTags();
        if (product.getStock() != null) score += weights.getStock();
        if (product.getHas3dPreview() != null && product.getHas3dPreview()) score += weights.getHas3dPreview();
        
        // AR/VR体验（10分）
        if (StringUtils.hasText(product.getArModelUrl()) || StringUtils.hasText(product.getVrExperienceUrl())) {
            score += weights.getArvr();
        }
        
        // 价值循环信息（10分）
        if (product.getIsRecyclable() != null || product.getRecycleValue() != null) {
            score += weights.getRecycle();
        }
        
        return Math.min(score, weights.getMaxScore());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductAudit submitForAudit(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 计算完整度评分
        int score = calculateCompletenessScore(product);
        product.setCompletenessScore(score);
        productMapper.updateById(product);
        
        // 创建审核记录
        ProductAudit audit = new ProductAudit();
        audit.setProductId(productId);
        audit.setStatus(Constants.ProductAuditStatus.PENDING);
        audit.setCompletenessScore(score);
        
        auditMapper.insert(audit);
        return audit;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductAudit auditProduct(Long productId, Long auditorId, String status, String comment) {
        QueryWrapper<ProductAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                   .eq("status", Constants.ProductAuditStatus.PENDING)
                   .orderByDesc("create_time")
                   .last("LIMIT 1");
        
        ProductAudit audit = auditMapper.selectOne(queryWrapper);
        if (audit == null) {
            throw new RuntimeException("未找到待审核记录");
        }
        
        audit.setStatus(status);
        audit.setAuditorId(auditorId);
        audit.setComment(comment);
        audit.setAuditTime(LocalDateTime.now());
        
        auditMapper.updateById(audit);
        
        // 更新商品状态
        Product product = productMapper.selectById(productId);
        if (product != null) {
            if (Constants.ProductAuditStatus.APPROVED.equals(status)) {
                product.setEnabled(true);
            } else if (Constants.ProductAuditStatus.REJECTED.equals(status)) {
                product.setEnabled(false);
            }
            productMapper.updateById(product);
        }
        
        return audit;
    }
    
    @Override
    public ResponseUtil.PageResult<Product> getPendingAuditProducts(int page, int size) {
        // 查询待审核的商品
        QueryWrapper<ProductAudit> auditWrapper = new QueryWrapper<>();
        auditWrapper.eq("status", Constants.ProductAuditStatus.PENDING);
        List<ProductAudit> audits = auditMapper.selectList(auditWrapper);
        
        // 获取对应的商品ID列表
        List<Long> productIds = audits.stream()
                .map(ProductAudit::getProductId)
                .distinct()
                .toList();
        
        if (productIds.isEmpty()) {
            return ResponseUtil.PageResult.<Product>builder()
                    .data(java.util.Collections.emptyList())
                    .total(0L)
                    .page(page)
                    .size(size)
                    .totalPages(0)
                    .build();
        }
        
        QueryWrapper<Product> productWrapper = new QueryWrapper<>();
        productWrapper.in("id", productIds);
        
        Page<Product> pageParam = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(pageParam, productWrapper);
        
        return ResponseUtil.PageResult.<Product>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public java.util.List<ProductAudit> getProductAuditHistory(Long productId) {
        QueryWrapper<ProductAudit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                   .orderByDesc("create_time");
        return auditMapper.selectList(queryWrapper);
    }
}


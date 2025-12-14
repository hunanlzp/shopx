package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.ProductImage;
import com.shopx.mapper.ProductImageMapper;
import com.shopx.service.ProductImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品图片服务实现类
 */
@Slf4j
@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {
    
    @Autowired
    private ProductImageMapper imageMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductImage addProductImage(Long productId, String imageUrl, String imageType, Integer sortOrder) {
        ProductImage image = new ProductImage();
        image.setProductId(productId);
        image.setImageUrl(imageUrl);
        image.setImageType(imageType != null ? imageType : "MAIN");
        image.setSortOrder(sortOrder != null ? sortOrder : 0);
        
        imageMapper.insert(image);
        return image;
    }
    
    @Override
    public List<ProductImage> getProductImages(Long productId) {
        QueryWrapper<ProductImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                   .orderByAsc("sort_order")
                   .orderByDesc("create_time");
        return imageMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProductImage(Long imageId) {
        return imageMapper.deleteById(imageId) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateImageOrder(Long imageId, Integer sortOrder) {
        ProductImage image = imageMapper.selectById(imageId);
        if (image == null) {
            return false;
        }
        
        image.setSortOrder(sortOrder);
        imageMapper.updateById(image);
        return true;
    }
    
    @Override
    public boolean checkImageCount(Long productId, int minCount) {
        List<ProductImage> images = getProductImages(productId);
        return images.size() >= minCount;
    }
}


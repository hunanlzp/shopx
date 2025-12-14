package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体 - 支持情境化推荐和AR/VR体验
 */
@Data
@TableName("t_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String description;
    private String image;          // 商品图片
    private BigDecimal price;
    private BigDecimal shippingFee; // 运费
    private BigDecimal taxRate;    // 税率（百分比）
    private Integer stock;
    private String category;
    
    // 创建者信息
    private Long createBy;         // 创建者ID
    private Boolean enabled;       // 是否启用
    
    // 情境化属性
    private String suitableScenarios; // 适用场景
    private String seasonality;       // 季节性
    private String lifestyleTags;     // 生活方式标签
    
    // AR/VR相关
    private String arModelUrl;        // AR模型URL
    private String vrExperienceUrl;   // VR体验URL
    private Boolean has3dPreview;     // 是否有3D预览
    
    // 价值循环属性
    private Boolean isRecyclable;     // 是否可回收
    private Boolean isRentable;       // 是否可租赁
    private BigDecimal recycleValue;  // 回收价值
    
    // 社交属性
    private Integer likeCount;
    private Integer shareCount;
    private Integer viewCount;
    
    // 完整度评分（0-100）
    private Integer completenessScore;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

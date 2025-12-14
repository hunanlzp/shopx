package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 社区帖子实体
 */
@Data
@TableName("t_community_post")
public class CommunityPost {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String title;
    private String content;
    private String postType; // GENERAL, RECYCLE, ACTIVITY, SHARE
    private String category;
    
    private String imageUrls; // JSON格式
    private String tags;
    
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Integer viewCount;
    
    private Boolean isPinned;
    private Boolean isFeatured;
    private String status; // PUBLISHED, DRAFT, DELETED
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}


package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 社区评论实体
 */
@Data
@TableName("t_community_comment")
public class CommunityComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long postId;
    private Long userId;
    private Long parentId; // 父评论ID
    private String content;
    
    private Integer likeCount;
    private Integer replyCount;
    private String status; // PUBLISHED, DELETED
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}


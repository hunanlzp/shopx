package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AR/VR体验记录实体
 */
@Data
@TableName("t_experience_record")
public class ExperienceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long productId;
    private String experienceType; // AR/VR
    private Integer durationSeconds;
    private String interactionData; // JSON格式
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}


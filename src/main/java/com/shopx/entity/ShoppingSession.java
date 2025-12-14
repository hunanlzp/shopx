package com.shopx.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 协作购物会话实体 - 支持多人实时购物
 */
@Data
@TableName("t_shopping_session")
public class ShoppingSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sessionId;        // 会话唯一ID
    private Long hostUserId;         // 主持人用户ID
    private String participantIds;   // 参与者ID列表(JSON)
    private Long productId;          // 正在浏览的商品ID
    private String sessionStatus;    // 会话状态: ACTIVE, ENDED
    
    // 实时协作数据
    private String chatHistory;      // 聊天记录(JSON)
    private String annotations;      // 标注信息(JSON)
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}


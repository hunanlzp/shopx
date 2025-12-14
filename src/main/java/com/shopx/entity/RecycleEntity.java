package com.shopx.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 回收订单实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecycleOrder {
    
    /**
     * 回收订单ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片
     */
    private String productImage;
    
    /**
     * 回收数量
     */
    private Integer quantity;
    
    /**
     * 回收价格
     */
    private BigDecimal recyclePrice;
    
    /**
     * 回收状态（PENDING/APPROVED/REJECTED/COMPLETED）
     */
    private String status;
    
    /**
     * 回收原因
     */
    private String reason;
    
    /**
     * 回收地址
     */
    private String address;
    
    /**
     * 联系方式
     */
    private String contact;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

/**
 * 价值循环统计实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecycleStats {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 总回收次数
     */
    private Integer totalRecycles;
    
    /**
     * 总回收价值
     */
    private BigDecimal totalValue;
    
    /**
     * 本月回收次数
     */
    private Integer monthlyRecycles;
    
    /**
     * 本月回收价值
     */
    private BigDecimal monthlyValue;
    
    /**
     * 环保积分
     */
    private Integer ecoPoints;
    
    /**
     * 环保等级
     */
    private String ecoLevel;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdate;
}

/**
 * 环保活动实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoActivity {
    
    /**
     * 活动ID
     */
    private Long id;
    
    /**
     * 活动名称
     */
    private String name;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 活动类型
     */
    private String type;
    
    /**
     * 活动状态
     */
    private String status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 参与人数
     */
    private Integer participantCount;
    
    /**
     * 奖励积分
     */
    private Integer rewardPoints;
    
    /**
     * 活动图片
     */
    private String imageUrl;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

/**
 * 用户环保记录实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEcoRecord {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 活动ID
     */
    private Long activityId;
    
    /**
     * 记录类型
     */
    private String recordType;
    
    /**
     * 记录内容
     */
    private String content;
    
    /**
     * 获得积分
     */
    private Integer points;
    
    /**
     * 记录时间
     */
    private LocalDateTime recordTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

/**
 * 价值循环页面数据实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecyclePageData {
    
    /**
     * 用户回收统计
     */
    private RecycleStats userStats;
    
    /**
     * 回收订单列表
     */
    private List<RecycleOrder> recycleOrders;
    
    /**
     * 环保活动列表
     */
    private List<EcoActivity> ecoActivities;
    
    /**
     * 用户环保记录
     */
    private List<UserEcoRecord> ecoRecords;
    
    /**
     * 可回收商品列表
     */
    private List<Product> recyclableProducts;
    
    /**
     * 环保积分排行榜
     */
    private List<UserEcoRanking> ecoRanking;
}

/**
 * 用户环保排行榜实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEcoRanking {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 环保积分
     */
    private Integer ecoPoints;
    
    /**
     * 排名
     */
    private Integer ranking;
    
    /**
     * 环保等级
     */
    private String ecoLevel;
}

package com.shopx.service.impl;

import com.shopx.entity.*;
import com.shopx.service.RecycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 价值循环服务实现类
 */
@Slf4j
@Service
public class RecycleServiceImpl implements RecycleService {
    
    // 模拟数据存储
    private final Map<Long, RecycleOrder> recycleOrders = new HashMap<>();
    private final Map<Long, RecycleStats> recycleStats = new HashMap<>();
    private final Map<Long, EcoActivity> ecoActivities = new HashMap<>();
    private final Map<Long, UserEcoRecord> ecoRecords = new HashMap<>();
    private final Map<Long, UserEcoRanking> ecoRankings = new HashMap<>();
    
    private Long nextOrderId = 1L;
    private Long nextActivityId = 1L;
    private Long nextRecordId = 1L;
    
    @Override
    public RecycleOrder createRecycleOrder(RecycleOrder recycleOrder) {
        log.info("创建回收订单: userId={}, productId={}", recycleOrder.getUserId(), recycleOrder.getProductId());
        
        recycleOrder.setId(nextOrderId++);
        recycleOrder.setStatus("PENDING");
        recycleOrder.setCreateTime(LocalDateTime.now());
        recycleOrder.setUpdateTime(LocalDateTime.now());
        
        recycleOrders.put(recycleOrder.getId(), recycleOrder);
        
        return recycleOrder;
    }
    
    @Override
    public List<RecycleOrder> getUserRecycleOrders(Long userId, String status) {
        log.info("获取用户回收订单: userId={}, status={}", userId, status);
        
        return recycleOrders.values().stream()
                .filter(order -> order.getUserId().equals(userId))
                .filter(order -> status == null || order.getStatus().equals(status))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public RecycleOrder getRecycleOrderById(Long id) {
        log.info("获取回收订单详情: orderId={}", id);
        
        return recycleOrders.get(id);
    }
    
    @Override
    public RecycleOrder updateRecycleOrderStatus(Long id, String status) {
        log.info("更新回收订单状态: orderId={}, status={}", id, status);
        
        RecycleOrder order = recycleOrders.get(id);
        if (order != null) {
            order.setStatus(status);
            order.setUpdateTime(LocalDateTime.now());
            
            if ("APPROVED".equals(status)) {
                order.setReviewTime(LocalDateTime.now());
            } else if ("COMPLETED".equals(status)) {
                order.setCompleteTime(LocalDateTime.now());
            }
            
            recycleOrders.put(id, order);
        }
        
        return order;
    }
    
    @Override
    public RecycleStats getUserRecycleStats(Long userId) {
        log.info("获取用户回收统计: userId={}", userId);
        
        RecycleStats stats = recycleStats.get(userId);
        if (stats == null) {
            // 计算统计数据
            List<RecycleOrder> userOrders = getUserRecycleOrders(userId, null);
            
            int totalRecycles = userOrders.size();
            BigDecimal totalValue = userOrders.stream()
                    .map(RecycleOrder::getRecyclePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算本月数据
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            
            int monthlyRecycles = (int) userOrders.stream()
                    .filter(order -> order.getCreateTime().isAfter(monthStart))
                    .count();
            
            BigDecimal monthlyValue = userOrders.stream()
                    .filter(order -> order.getCreateTime().isAfter(monthStart))
                    .map(RecycleOrder::getRecyclePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 计算环保积分
            int ecoPoints = totalRecycles * 10 + monthlyRecycles * 5;
            
            // 计算环保等级
            String ecoLevel = calculateEcoLevel(ecoPoints);
            
            stats = RecycleStats.builder()
                    .userId(userId)
                    .totalRecycles(totalRecycles)
                    .totalValue(totalValue)
                    .monthlyRecycles(monthlyRecycles)
                    .monthlyValue(monthlyValue)
                    .ecoPoints(ecoPoints)
                    .ecoLevel(ecoLevel)
                    .lastUpdate(LocalDateTime.now())
                    .build();
            
            recycleStats.put(userId, stats);
        }
        
        return stats;
    }
    
    @Override
    public List<EcoActivity> getEcoActivities(String status) {
        log.info("获取环保活动列表: status={}", status);
        
        // 初始化一些示例活动
        if (ecoActivities.isEmpty()) {
            initializeEcoActivities();
        }
        
        return ecoActivities.values().stream()
                .filter(activity -> status == null || activity.getStatus().equals(status))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public UserEcoRecord joinEcoActivity(Long activityId, Long userId) {
        log.info("参与环保活动: activityId={}, userId={}", activityId, userId);
        
        EcoActivity activity = ecoActivities.get(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        
        UserEcoRecord record = UserEcoRecord.builder()
                .id(nextRecordId++)
                .userId(userId)
                .activityId(activityId)
                .recordType("ACTIVITY_JOIN")
                .content("参与活动: " + activity.getName())
                .points(activity.getRewardPoints())
                .recordTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .build();
        
        ecoRecords.put(record.getId(), record);
        
        // 更新活动参与人数
        activity.setParticipantCount(activity.getParticipantCount() + 1);
        ecoActivities.put(activityId, activity);
        
        return record;
    }
    
    @Override
    public RecyclePageData getRecyclePageData(Long userId) {
        log.info("获取价值循环页面数据: userId={}", userId);
        
        RecycleStats userStats = getUserRecycleStats(userId);
        List<RecycleOrder> recycleOrders = getUserRecycleOrders(userId, null);
        List<EcoActivity> ecoActivities = getEcoActivities("ACTIVE");
        List<UserEcoRecord> ecoRecords = getUserEcoRecords(userId);
        List<Product> recyclableProducts = getRecyclableProducts();
        List<UserEcoRanking> ecoRanking = getEcoRanking(10);
        
        return RecyclePageData.builder()
                .userStats(userStats)
                .recycleOrders(recycleOrders)
                .ecoActivities(ecoActivities)
                .ecoRecords(ecoRecords)
                .recyclableProducts(recyclableProducts)
                .ecoRanking(ecoRanking)
                .build();
    }
    
    @Override
    public List<UserEcoRanking> getEcoRanking(Integer limit) {
        log.info("获取环保积分排行榜: limit={}", limit);
        
        // 初始化排行榜数据
        if (ecoRankings.isEmpty()) {
            initializeEcoRankings();
        }
        
        return ecoRankings.values().stream()
                .sorted((a, b) -> b.getEcoPoints().compareTo(a.getEcoPoints()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 初始化环保活动
     */
    private void initializeEcoActivities() {
        EcoActivity activity1 = EcoActivity.builder()
                .id(nextActivityId++)
                .name("绿色生活挑战")
                .description("30天绿色生活挑战，减少塑料使用")
                .type("CHALLENGE")
                .status("ACTIVE")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(30))
                .participantCount(156)
                .rewardPoints(100)
                .imageUrl("https://picsum.photos/400/200?random=1")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        EcoActivity activity2 = EcoActivity.builder()
                .id(nextActivityId++)
                .name("旧物改造大赛")
                .description("将废旧物品改造成有用物品的创意大赛")
                .type("CONTEST")
                .status("ACTIVE")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusDays(15))
                .participantCount(89)
                .rewardPoints(200)
                .imageUrl("https://picsum.photos/400/200?random=2")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        
        ecoActivities.put(activity1.getId(), activity1);
        ecoActivities.put(activity2.getId(), activity2);
    }
    
    /**
     * 初始化排行榜数据
     */
    private void initializeEcoRankings() {
        String[] names = {"Alice Wang", "Bob Li", "Carol Zhang", "David Chen", "Eve Liu", "Frank Wu", "Grace Zhou", "Henry Sun", "Ivy Ma", "Jack Liu"};
        
        for (int i = 0; i < names.length; i++) {
            UserEcoRanking ranking = UserEcoRanking.builder()
                    .userId((long) (i + 1))
                    .username(names[i])
                    .avatar("https://picsum.photos/100/100?random=" + (i + 1))
                    .ecoPoints(1000 - i * 50)
                    .ranking(i + 1)
                    .ecoLevel(calculateEcoLevel(1000 - i * 50))
                    .build();
            
            ecoRankings.put(ranking.getUserId(), ranking);
        }
    }
    
    /**
     * 获取用户环保记录
     */
    private List<UserEcoRecord> getUserEcoRecords(Long userId) {
        return ecoRecords.values().stream()
                .filter(record -> record.getUserId().equals(userId))
                .sorted((a, b) -> b.getRecordTime().compareTo(a.getRecordTime()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取可回收商品列表
     */
    private List<Product> getRecyclableProducts() {
        // 模拟可回收商品数据
        List<Product> products = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            Product product = Product.builder()
                    .id((long) i)
                    .name("可回收商品 " + i)
                    .description("这是一个可回收的商品")
                    .price(new BigDecimal("99.99"))
                    .isRecyclable(true)
                    .recycleValue(new BigDecimal("20.00"))
                    .build();
            
            products.add(product);
        }
        
        return products;
    }
    
    /**
     * 计算环保等级
     */
    private String calculateEcoLevel(int ecoPoints) {
        if (ecoPoints >= 1000) {
            return "环保大师";
        } else if (ecoPoints >= 500) {
            return "环保达人";
        } else if (ecoPoints >= 200) {
            return "环保新手";
        } else {
            return "环保初学者";
        }
    }
}

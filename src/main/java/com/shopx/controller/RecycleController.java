package com.shopx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.shopx.annotation.ApiVersion;
import com.shopx.constant.Constants;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.RecycleOrder;
import com.shopx.service.RecycleOrderService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 价值循环控制器
 * 提供回收和环保相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/recycle")
@ApiVersion("v1")
@Tag(name = "价值循环", description = "回收和环保相关API")
public class RecycleController {

    @Autowired
    private RecycleOrderService recycleOrderService;
    
    @Autowired(required = false)
    private com.shopx.service.CommunityService communityService;
    
    @Autowired(required = false)
    private ProductService productService;

    // 模拟环保活动数据
    private final Map<Long, Map<String, Object>> ecoActivities = new HashMap<>();

    public RecycleController() {
        initEcoActivities();
    }

    /**
     * 创建回收订单
     */
    @Operation(summary = "创建回收订单", description = "创建新的回收订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @SaCheckLogin
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<RecycleOrder>> createRecycleOrder(
            @Parameter(description = "回收订单信息", required = true) @RequestBody Map<String, Object> orderData) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            // 创建回收订单
            RecycleOrder order = new RecycleOrder();
            order.setUserId(userId);
            order.setProductName((String) orderData.get("productName"));
            order.setQuantity((Integer) orderData.get("quantity"));
            order.setEstimatedValue(((Number) orderData.get("estimatedValue")).doubleValue());
            order.setStatus("PENDING");
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            if (orderData.containsKey("pickupDate")) {
                order.setPickupDate(LocalDateTime.parse((String) orderData.get("pickupDate")));
            }
            
            if (orderData.containsKey("notes")) {
                order.setNotes((String) orderData.get("notes"));
            }

            // 保存订单
            RecycleOrder savedOrder = recycleOrderService.save(order);

            log.info("用户 {} 创建回收订单: {}", userId, savedOrder.getId());

            return ResponseUtil.success("回收订单创建成功", savedOrder);
        } catch (Exception e) {
            log.error("创建回收订单失败", e);
            return ResponseUtil.error("创建回收订单失败，请稍后重试");
        }
    }

    /**
     * 获取回收订单详情
     */
    @Operation(summary = "获取回收订单详情", description = "根据订单ID获取回收订单的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @SaCheckLogin
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecycleOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId) {

        try {
            RecycleOrder order = recycleOrderService.getById(orderId);
            if (order == null) {
                return ResponseUtil.notFound("回收订单不存在");
            }

            // 构建完整的订单信息
            Map<String, Object> orderDetail = new HashMap<>();
            orderDetail.put("id", order.getId());
            orderDetail.put("userId", order.getUserId());
            orderDetail.put("productName", order.getProductName());
            orderDetail.put("quantity", order.getQuantity());
            orderDetail.put("estimatedValue", order.getEstimatedValue());
            orderDetail.put("actualValue", order.getActualValue());
            orderDetail.put("status", order.getStatus());
            orderDetail.put("recycleType", order.getRecycleType());
            orderDetail.put("pickupDate", order.getPickupDate());
            orderDetail.put("notes", order.getNotes());
            orderDetail.put("createTime", order.getCreateTime());
            orderDetail.put("updateTime", order.getUpdateTime());
            orderDetail.put("completionDate", order.getCompletionDate());

            // 如果有商品ID，获取商品信息
            if (order.getProductId() != null) {
                try {
                    Product product = productService.getProductById(order.getProductId());
                    if (product != null) {
                        Map<String, Object> productInfo = new HashMap<>();
                        productInfo.put("id", product.getId());
                        productInfo.put("name", product.getName());
                        productInfo.put("price", product.getPrice());
                        productInfo.put("image", product.getImage());
                        productInfo.put("category", product.getCategory());
                        productInfo.put("isRecyclable", product.getIsRecyclable());
                        productInfo.put("recycleValue", product.getRecycleValue());
                        orderDetail.put("productInfo", productInfo);
                    }
                } catch (Exception e) {
                    log.warn("获取商品信息失败: productId={}", order.getProductId());
                }
            }

            // 环境影响数据
            if (order.getEnvironmentalImpact() != null && !order.getEnvironmentalImpact().isEmpty()) {
                try {
                    Map<String, Object> envImpact = com.alibaba.fastjson2.JSON.parseObject(
                        order.getEnvironmentalImpact(), 
                        Map.class
                    );
                    orderDetail.put("environmentalImpact", envImpact);
                } catch (Exception e) {
                    log.warn("解析环境影响数据失败", e);
                }
            }

            // 检测结果
            if (order.getInspectionResult() != null && !order.getInspectionResult().isEmpty()) {
                try {
                    Map<String, Object> inspection = com.alibaba.fastjson2.JSON.parseObject(
                        order.getInspectionResult(),
                        Map.class
                    );
                    orderDetail.put("inspectionResult", inspection);
                } catch (Exception e) {
                    log.warn("解析检测结果失败", e);
                }
            }

            return ResponseUtil.success("获取回收订单详情成功", orderDetail);
        } catch (Exception e) {
            log.error("获取回收订单详情失败", e);
            return ResponseUtil.error("获取回收订单详情失败，请稍后重试");
        }
    }

    /**
     * 获取用户回收订单
     */
    @Operation(summary = "获取用户回收订单", description = "获取用户的回收订单列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<RecycleOrder>>> getUserRecycleOrders(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        try {
            List<RecycleOrder> orders = recycleOrderService.getByUserId(userId);

            return ResponseUtil.success("获取回收订单成功", orders);
        } catch (Exception e) {
            log.error("获取回收订单失败", e);
            return ResponseUtil.error("获取回收订单失败，请稍后重试");
        }
    }

    /**
     * 更新回收订单状态
     */
    @Operation(summary = "更新回收订单状态", description = "更新回收订单的状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @SaCheckPermission("recycle:manage")
    @PutMapping("/order/{orderId}/status")
    public ResponseEntity<ApiResponse<Void>> updateRecycleOrderStatus(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId,
            @Parameter(description = "新状态", required = true) @RequestParam String status) {

        try {
            RecycleOrder order = recycleOrderService.getById(orderId);
            if (order == null) {
                return ResponseUtil.notFound("回收订单不存在");
            }

            order.setStatus(status);
            order.setUpdateTime(LocalDateTime.now());
            
            if ("COMPLETED".equals(status)) {
                order.setCompletionDate(LocalDateTime.now());
                // 设置实际价值（模拟）
                order.setActualValue(order.getEstimatedValue() * (0.8 + Math.random() * 0.4));
            }

            recycleOrderService.updateById(order);

            log.info("回收订单 {} 状态更新为: {}", orderId, status);

            return ResponseUtil.success("订单状态更新成功", null);
        } catch (Exception e) {
            log.error("更新回收订单状态失败", e);
            return ResponseUtil.error("更新回收订单状态失败，请稍后重试");
        }
    }

    /**
     * 获取环保活动列表
     */
    @Operation(summary = "获取环保活动列表", description = "获取可参与的环保活动列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEcoActivities() {

        try {
            List<Map<String, Object>> activities = new ArrayList<>(ecoActivities.values());

            return ResponseUtil.success("获取环保活动成功", activities);
        } catch (Exception e) {
            log.error("获取环保活动失败", e);
            return ResponseUtil.error("获取环保活动失败，请稍后重试");
        }
    }

    /**
     * 参加环保活动
     */
    @Operation(summary = "参加环保活动", description = "用户参加环保活动")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "参加成功"),
            @ApiResponse(responseCode = "404", description = "活动不存在")
    })
    @SaCheckLogin
    @PostMapping("/activity/{activityId}/join")
    public ResponseEntity<ApiResponse<Void>> joinEcoActivity(
            @Parameter(description = "活动ID", required = true) @PathVariable Long activityId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        try {
            Map<String, Object> activity = ecoActivities.get(activityId);
            if (activity == null) {
                return ResponseUtil.notFound("环保活动不存在");
            }

            // 检查活动状态
            if (!"ONGOING".equals(activity.get("status"))) {
                return ResponseUtil.error("活动已结束或未开始");
            }

            // 检查参与人数限制
            Integer maxParticipants = (Integer) activity.get("maxParticipants");
            @SuppressWarnings("unchecked")
            List<Long> participants = (List<Long>) activity.get("participants");
            
            if (maxParticipants != null && participants.size() >= maxParticipants) {
                return ResponseUtil.error("活动参与人数已满");
            }

            // 添加参与者
            if (!participants.contains(userId)) {
                participants.add(userId);
                activity.put("participants", participants);
            }

            log.info("用户 {} 参加环保活动 {}", userId, activityId);

            return ResponseUtil.success("参加活动成功", null);
        } catch (Exception e) {
            log.error("参加环保活动失败", e);
            return ResponseUtil.error("参加环保活动失败，请稍后重试");
        }
    }

    /**
     * 获取用户回收统计
     */
    @Operation(summary = "获取用户回收统计", description = "获取用户的回收统计数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserRecycleStats(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取用户回收订单统计
            List<RecycleOrder> orders = recycleOrderService.getByUserId(userId);
            
            int totalOrders = orders.size();
            double totalValue = orders.stream()
                    .filter(order -> order.getActualValue() != null)
                    .mapToDouble(RecycleOrder::getActualValue)
                    .sum();
            
            // 计算环保分数
            int sustainabilityScore = calculateSustainabilityScore(orders);
            
            // 确定环保等级
            String ecoLevel = determineEcoLevel(sustainabilityScore);
            
            // 计算环境影响
            Map<String, Double> environmentalImpact = calculateEnvironmentalImpact(orders);
            
            stats.put("totalOrders", totalOrders);
            stats.put("totalValue", totalValue);
            stats.put("sustainabilityScore", sustainabilityScore);
            stats.put("ecoLevel", ecoLevel);
            stats.put("carbonSaved", environmentalImpact.get("carbonSaved"));
            stats.put("treesPlanted", environmentalImpact.get("treesPlanted"));
            stats.put("waterSaved", environmentalImpact.get("waterSaved"));
            stats.put("energySaved", environmentalImpact.get("energySaved"));
            stats.put("monthlyTrend", generateMonthlyTrend(orders));

            return ResponseUtil.success("获取回收统计成功", stats);
        } catch (Exception e) {
            log.error("获取回收统计失败", e);
            return ResponseUtil.error("获取回收统计失败，请稍后重试");
        }
    }

    /**
     * 获取回收订单详情
     */
    @Operation(summary = "获取回收订单详情", description = "获取回收订单的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "订单不存在")
    })
    @SaCheckLogin
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<RecycleOrder>> getRecycleOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId) {

        try {
            RecycleOrder order = recycleOrderService.getById(orderId);
            if (order == null) {
                return ResponseUtil.notFound("回收订单不存在");
            }

            return ResponseUtil.success("获取回收订单成功", order);
        } catch (Exception e) {
            log.error("获取回收订单失败", e);
            return ResponseUtil.error("获取回收订单失败，请稍后重试");
        }
    }

    /**
     * 计算环保分数
     */
    private int calculateSustainabilityScore(List<RecycleOrder> orders) {
        int score = 0;
        
        for (RecycleOrder order : orders) {
            if ("COMPLETED".equals(order.getStatus())) {
                score += 10; // 每个完成的回收订单得10分
                
                if (order.getActualValue() != null && order.getActualValue() > order.getEstimatedValue()) {
                    score += 5; // 实际价值超过预估价值额外得5分
                }
            }
        }
        
        return Math.min(score, 1000); // 最高1000分
    }

    /**
     * 确定环保等级
     */
    private String determineEcoLevel(int score) {
        if (score >= 800) return "Diamond";
        if (score >= 600) return "Platinum";
        if (score >= 400) return "Gold";
        if (score >= 200) return "Silver";
        return "Bronze";
    }

    /**
     * 计算环境影响
     */
    private Map<String, Double> calculateEnvironmentalImpact(List<RecycleOrder> orders) {
        Map<String, Double> impact = new HashMap<>();
        
        int completedOrders = (int) orders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .count();
        
        impact.put("carbonSaved", completedOrders * 2.5); // 每个订单减少2.5kg碳排放
        impact.put("treesPlanted", completedOrders * 0.1); // 每个订单相当于种植0.1棵树
        impact.put("waterSaved", completedOrders * 50.0); // 每个订单节约50L水
        impact.put("energySaved", completedOrders * 5.0); // 每个订单节约5kWh能源
        
        return impact;
    }

    /**
     * 生成月度趋势
     */
    private List<Map<String, Object>> generateMonthlyTrend(List<RecycleOrder> orders) {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        for (int i = 11; i >= 0; i--) {
            LocalDateTime monthStart = LocalDateTime.now().minusMonths(i).withDayOfMonth(1);
            LocalDateTime monthEnd = monthStart.plusMonths(1);
            
            int monthOrders = (int) orders.stream()
                    .filter(order -> order.getCreateTime().isAfter(monthStart) && 
                                   order.getCreateTime().isBefore(monthEnd))
                    .count();
            
            double monthValue = orders.stream()
                    .filter(order -> order.getCreateTime().isAfter(monthStart) && 
                                   order.getCreateTime().isBefore(monthEnd) &&
                                   order.getActualValue() != null)
                    .mapToDouble(RecycleOrder::getActualValue)
                    .sum();
            
            Map<String, Object> month = new HashMap<>();
            month.put("month", monthStart.getMonth().toString());
            month.put("orders", monthOrders);
            month.put("value", monthValue);
            month.put("score", monthOrders * 10);
            trend.add(month);
        }
        
        return trend;
    }

    /**
     * 初始化环保活动
     */
    private void initEcoActivities() {
        // 环保挑战活动
        Map<String, Object> challenge1 = new HashMap<>();
        challenge1.put("id", 1L);
        challenge1.put("title", "30天无塑料挑战");
        challenge1.put("description", "挑战30天不使用一次性塑料制品，为环保贡献力量");
        challenge1.put("type", "CHALLENGE");
        challenge1.put("status", "ONGOING");
        challenge1.put("startDate", LocalDateTime.now().minusDays(10));
        challenge1.put("endDate", LocalDateTime.now().plusDays(20));
        challenge1.put("participants", new ArrayList<Long>());
        challenge1.put("maxParticipants", 100);
        challenge1.put("points", 100);
        challenge1.put("difficulty", "MEDIUM");
        challenge1.put("category", "环保挑战");
        ecoActivities.put(1L, challenge1);

        // 环保教育活动
        Map<String, Object> education1 = new HashMap<>();
        education1.put("id", 2L);
        education1.put("title", "垃圾分类知识讲座");
        education1.put("description", "学习正确的垃圾分类方法，提高环保意识");
        education1.put("type", "EDUCATION");
        education1.put("status", "UPCOMING");
        education1.put("startDate", LocalDateTime.now().plusDays(5));
        education1.put("endDate", LocalDateTime.now().plusDays(5).plusHours(2));
        education1.put("participants", new ArrayList<Long>());
        education1.put("maxParticipants", 50);
        education1.put("points", 50);
        education1.put("difficulty", "EASY");
        education1.put("category", "环保教育");
        ecoActivities.put(2L, education1);

        // 环保活动
        Map<String, Object> event1 = new HashMap<>();
        event1.put("id", 3L);
        event1.put("title", "社区清洁活动");
        event1.put("description", "参与社区清洁活动，美化环境");
        event1.put("type", "EVENT");
        event1.put("status", "ONGOING");
        event1.put("startDate", LocalDateTime.now().minusDays(1));
        event1.put("endDate", LocalDateTime.now().plusDays(6));
        event1.put("participants", new ArrayList<Long>());
        event1.put("maxParticipants", 200);
        event1.put("points", 80);
        event1.put("difficulty", "EASY");
        event1.put("category", "环保活动");
        ecoActivities.put(3L, event1);
    }
}
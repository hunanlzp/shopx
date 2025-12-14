package com.shopx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.shopx.annotation.ApiVersion;
import com.shopx.constant.Constants;
import com.shopx.dto.ARVRStatsDTO;
import com.shopx.dto.FavoriteProductDTO;
import com.shopx.dto.RecentExperienceDTO;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.Product;
import com.shopx.service.ProductService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * AR/VR体验控制器
 * 提供AR/VR体验相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/ar-vr")
@ApiVersion("v1")
@Tag(name = "AR/VR体验", description = "AR/VR体验相关API")
public class ARVRController {

    @Autowired
    private ProductService productService;
    
    @Autowired(required = false)
    private com.shopx.mapper.ExperienceRecordMapper experienceRecordMapper;

    /**
     * 获取AR体验URL
     */
    @Operation(summary = "获取AR体验URL", description = "根据商品ID获取AR体验链接")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @SaCheckLogin
    @GetMapping("/ar/{productId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getARExperience(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {

        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                return ResponseUtil.notFound("商品不存在");
            }

            // 生成AR体验URL
            String arUrl = generateARUrl(productId);
            
            Map<String, String> result = new HashMap<>();
            result.put("arUrl", arUrl);
            result.put("productId", productId.toString());
            result.put("productName", product.getName());
            result.put("modelUrl", product.getArModelUrl());

            // 记录用户行为
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId != null) {
                // 这里可以调用用户行为记录服务
                log.info("用户 {} 访问商品 {} 的AR体验", userId, productId);
            }

            return ResponseUtil.success("获取AR体验URL成功", result);
        } catch (Exception e) {
            log.error("获取AR体验URL失败", e);
            return ResponseUtil.error("获取AR体验URL失败，请稍后重试");
        }
    }

    /**
     * 获取VR体验URL
     */
    @Operation(summary = "获取VR体验URL", description = "根据商品ID获取VR体验链接")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @SaCheckLogin
    @GetMapping("/vr/{productId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getVRExperience(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {

        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                return ResponseUtil.notFound("商品不存在");
            }

            // 生成VR体验URL
            String vrUrl = generateVRUrl(productId);
            
            Map<String, String> result = new HashMap<>();
            result.put("vrUrl", vrUrl);
            result.put("productId", productId.toString());
            result.put("productName", product.getName());
            result.put("modelUrl", product.getVrExperienceUrl());

            // 记录用户行为
            Long userId = SaTokenUtil.getCurrentUserId();
            if (userId != null) {
                log.info("用户 {} 访问商品 {} 的VR体验", userId, productId);
            }

            return ResponseUtil.success("获取VR体验URL成功", result);
        } catch (Exception e) {
            log.error("获取VR体验URL失败", e);
            return ResponseUtil.error("获取VR体验URL失败，请稍后重试");
        }
    }

    /**
     * 获取3D模型信息
     */
    @Operation(summary = "获取3D模型信息", description = "根据商品ID获取3D模型详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @SaCheckLogin
    @GetMapping("/model/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getModelInfo(
            @Parameter(description = "商品ID", required = true) @PathVariable Long productId) {

        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                return ResponseUtil.notFound("商品不存在");
            }

            Map<String, Object> modelInfo = new HashMap<>();
            modelInfo.put("productId", productId);
            modelInfo.put("productName", product.getName());
            modelInfo.put("arModelUrl", product.getArModelUrl());
            modelInfo.put("vrExperienceUrl", product.getVrExperienceUrl());
            modelInfo.put("has3dPreview", product.getHas3dPreview());
            
            // 3D模型配置
            Map<String, Object> modelConfig = getModelConfig(productId);
            modelInfo.put("modelConfig", modelConfig);

            return ResponseUtil.success("获取3D模型信息成功", modelInfo);
        } catch (Exception e) {
            log.error("获取3D模型信息失败", e);
            return ResponseUtil.error("获取3D模型信息失败，请稍后重试");
        }
    }

    /**
     * 记录AR/VR交互行为
     */
    @Operation(summary = "记录AR/VR交互行为", description = "记录用户在AR/VR体验中的交互行为")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "记录成功")
    })
    @SaCheckLogin
    @PostMapping("/interaction")
    public ResponseEntity<ApiResponse<Void>> recordInteraction(
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "交互类型", required = true) @RequestParam String interactionType,
            @Parameter(description = "交互数据", required = false) @RequestBody(required = false) Map<String, Object> interactionData) {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            // 记录交互行为
            log.info("用户 {} 在商品 {} 上执行了 {} 交互", userId, productId, interactionType);
            
            // 这里可以调用用户行为分析服务
            // userBehaviorService.recordInteraction(userId, productId, interactionType, interactionData);

            return ResponseUtil.success("交互行为记录成功", null);
        } catch (Exception e) {
            log.error("记录交互行为失败", e);
            return ResponseUtil.error("记录交互行为失败");
        }
    }

    /**
     * 获取AR/VR体验统计
     */
    @Operation(summary = "获取AR/VR体验统计", description = "获取用户的AR/VR体验统计数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<ARVRStatsDTO>> getExperienceStats() {

        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            
            ARVRStatsDTO stats = new ARVRStatsDTO();
            
            if (experienceRecordMapper != null) {
                // 查询AR体验次数
                long totalARViews = experienceRecordMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.shopx.entity.ExperienceRecord>()
                        .eq(com.shopx.entity.ExperienceRecord::getUserId, userId)
                        .eq(com.shopx.entity.ExperienceRecord::getExperienceType, "AR")
                );
                
                // 查询VR体验次数
                long totalVRViews = experienceRecordMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.shopx.entity.ExperienceRecord>()
                        .eq(com.shopx.entity.ExperienceRecord::getUserId, userId)
                        .eq(com.shopx.entity.ExperienceRecord::getExperienceType, "VR")
                );
                
                // 总交互次数
                long totalInteractions = experienceRecordMapper.selectCount(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.shopx.entity.ExperienceRecord>()
                        .eq(com.shopx.entity.ExperienceRecord::getUserId, userId)
                );
                
                // 获取最近体验的商品（最近10条）
                List<com.shopx.entity.ExperienceRecord> recentRecords = experienceRecordMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.shopx.entity.ExperienceRecord>()
                        .eq(com.shopx.entity.ExperienceRecord::getUserId, userId)
                        .orderByDesc(com.shopx.entity.ExperienceRecord::getCreateTime)
                        .last("LIMIT 10")
                );
                
                List<RecentExperienceDTO> recentExperiences = recentRecords.stream()
                    .map(record -> {
                        RecentExperienceDTO exp = new RecentExperienceDTO();
                        exp.setProductId(record.getProductId());
                        exp.setExperienceType(record.getExperienceType());
                        exp.setDurationSeconds(record.getDurationSeconds());
                        exp.setExperienceTime(record.getCreateTime());
                        try {
                            Product product = productService.getProductById(record.getProductId());
                            if (product != null) {
                                exp.setProductName(product.getName());
                            }
                        } catch (Exception e) {
                            log.warn("获取商品信息失败: productId={}", record.getProductId());
                        }
                        return exp;
                    })
                    .collect(java.util.stream.Collectors.toList());
                
                // 获取最喜欢的商品（体验次数最多的商品）
                List<com.shopx.entity.ExperienceRecord> allRecords = experienceRecordMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.shopx.entity.ExperienceRecord>()
                        .eq(com.shopx.entity.ExperienceRecord::getUserId, userId)
                );
                
                Map<Long, Long> productCounts = allRecords.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                        com.shopx.entity.ExperienceRecord::getProductId,
                        java.util.stream.Collectors.counting()
                    ));
                
                List<FavoriteProductDTO> favoriteProducts = productCounts.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .map(entry -> {
                        FavoriteProductDTO fav = new FavoriteProductDTO();
                        fav.setProductId(entry.getKey());
                        fav.setExperienceCount(entry.getValue());
                        try {
                            Product product = productService.getProductById(entry.getKey());
                            if (product != null) {
                                fav.setProductName(product.getName());
                            }
                        } catch (Exception e) {
                            log.warn("获取商品信息失败: productId={}", entry.getKey());
                        }
                        return fav;
                    })
                    .collect(java.util.stream.Collectors.toList());
                
                stats.setTotalARViews(totalARViews);
                stats.setTotalVRViews(totalVRViews);
                stats.setTotalInteractions(totalInteractions);
                stats.setFavoriteProducts(favoriteProducts);
                stats.setRecentExperiences(recentExperiences);
            } else {
                // 降级：返回默认值
                stats.setTotalARViews(0L);
                stats.setTotalVRViews(0L);
                stats.setTotalInteractions(0L);
                stats.setFavoriteProducts(new ArrayList<>());
                stats.setRecentExperiences(new ArrayList<>());
            }

            return ResponseUtil.success("获取体验统计成功", stats);
        } catch (Exception e) {
            log.error("获取体验统计失败", e);
            return ResponseUtil.error("获取体验统计失败，请稍后重试");
        }
    }

    /**
     * 生成AR体验URL
     */
    private String generateARUrl(Long productId) {
        try {
            Product product = productService.getProductById(productId);
            if (product != null && product.getArModelUrl() != null && !product.getArModelUrl().isEmpty()) {
                // 如果商品配置了AR模型URL，直接返回
                return product.getArModelUrl();
            }
            
            // 如果没有配置，生成动态URL
            // 支持WebXR、AR.js等AR框架
            String baseUrl = System.getProperty("shopx.ar.base.url", "/ar-experience");
            return String.format("%s/%d?type=ar&framework=webxr", baseUrl, productId);
        } catch (Exception e) {
            log.error("生成AR URL失败", e);
            return String.format("/ar-experience/%d", productId);
        }
    }

    /**
     * 生成VR体验URL
     */
    private String generateVRUrl(Long productId) {
        try {
            Product product = productService.getProductById(productId);
            if (product != null && product.getVrExperienceUrl() != null && !product.getVrExperienceUrl().isEmpty()) {
                // 如果商品配置了VR体验URL，直接返回
                return product.getVrExperienceUrl();
            }
            
            // 如果没有配置，生成动态URL
            // 支持A-Frame、Three.js等VR框架
            String baseUrl = System.getProperty("shopx.vr.base.url", "/vr-experience");
            return String.format("%s/%d?type=vr&framework=aframe", baseUrl, productId);
        } catch (Exception e) {
            log.error("生成VR URL失败", e);
            return String.format("/vr-experience/%d", productId);
        }
    }

    /**
     * 获取3D模型配置
     */
    private Map<String, Object> getModelConfig(Long productId) {
        Map<String, Object> config = new HashMap<>();
        
        // 根据商品ID返回不同的3D模型配置
        switch (productId.intValue()) {
            case 1: // 智能运动手环
                config.put("geometry", "box");
                config.put("size", new double[]{2.0, 0.3, 1.0});
                config.put("color", "#1890ff");
                config.put("material", "metal");
                break;
            case 2: // 时尚连衣裙
                config.put("geometry", "cylinder");
                config.put("size", new double[]{1.0, 1.2, 3.0});
                config.put("color", "#eb2f96");
                config.put("material", "fabric");
                break;
            case 3: // 无线蓝牙耳机
                config.put("geometry", "sphere");
                config.put("size", new double[]{0.8, 0.8, 0.8});
                config.put("color", "#52c41a");
                config.put("material", "plastic");
                break;
            default:
                config.put("geometry", "box");
                config.put("size", new double[]{1.0, 1.0, 1.0});
                config.put("color", "#666666");
                config.put("material", "default");
                break;
        }
        
        return config;
    }
}

package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.dto.ComparisonTableDTO;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.ProductComparison;
import com.shopx.service.ProductComparisonService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品对比控制器
 */
@Slf4j
@RestController
@RequestMapping("/comparison")
@ApiVersion("v1")
@Tag(name = "商品对比", description = "商品对比相关API")
public class ComparisonController {
    
    @Autowired
    private ProductComparisonService comparisonService;
    
    /**
     * 创建商品对比列表
     */
    @Operation(summary = "创建商品对比", description = "创建商品对比列表（最多5个商品）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductComparison>> createComparison(
            @Parameter(description = "对比列表名称", required = false) @RequestParam(required = false) String comparisonName,
            @Parameter(description = "商品ID列表", required = true) @RequestBody List<Long> productIds,
            @Parameter(description = "是否公开", required = false) @RequestParam(required = false) Boolean isPublic) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ProductComparison comparison = comparisonService.createComparison(userId, comparisonName, productIds, isPublic);
            return ResponseUtil.success("创建成功", comparison);
        } catch (Exception e) {
            log.error("创建商品对比失败", e);
            return ResponseUtil.error("创建商品对比失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取商品对比详情
     */
    @Operation(summary = "获取对比详情", description = "获取商品对比的详细信息和对比表格")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/{comparisonId}")
    public ResponseEntity<ApiResponse<ComparisonTableDTO>> getComparisonDetails(
            @Parameter(description = "对比ID", required = true) @PathVariable Long comparisonId) {
        
        try {
            ComparisonTableDTO details = comparisonService.getComparisonDetails(comparisonId);
            return ResponseUtil.success("查询成功", details);
        } catch (Exception e) {
            log.error("获取对比详情失败", e);
            return ResponseUtil.error("获取对比详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的对比列表
     */
    @Operation(summary = "获取对比列表", description = "获取当前用户的所有商品对比列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductComparison>>> getUserComparisons() {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            List<ProductComparison> comparisons = comparisonService.getUserComparisons(userId);
            return ResponseUtil.success("查询成功", comparisons);
        } catch (Exception e) {
            log.error("获取对比列表失败", e);
            return ResponseUtil.error("获取对比列表失败，请稍后重试");
        }
    }
    
    /**
     * 通过分享链接获取对比列表
     */
    @Operation(summary = "通过分享链接获取对比", description = "通过分享链接访问公开的对比列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/share/{shareLink}")
    public ResponseEntity<ApiResponse<ComparisonTableDTO>> getComparisonByShareLink(
            @Parameter(description = "分享链接", required = true) @PathVariable String shareLink) {
        
        try {
            ProductComparison comparison = comparisonService.getComparisonByShareLink(shareLink);
            if (comparison == null) {
                return ResponseUtil.error("分享链接无效或已失效");
            }
            
            ComparisonTableDTO details = comparisonService.getComparisonDetails(comparison.getId());
            return ResponseUtil.success("查询成功", details);
        } catch (Exception e) {
            log.error("通过分享链接获取对比失败", e);
            return ResponseUtil.error("获取对比失败，请稍后重试");
        }
    }
    
    /**
     * 更新对比列表
     */
    @Operation(summary = "更新对比列表", description = "更新商品对比列表中的商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功")
    })
    @PutMapping("/{comparisonId}")
    public ResponseEntity<ApiResponse<ProductComparison>> updateComparison(
            @Parameter(description = "对比ID", required = true) @PathVariable Long comparisonId,
            @Parameter(description = "商品ID列表", required = true) @RequestBody List<Long> productIds) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ProductComparison comparison = comparisonService.updateComparison(comparisonId, userId, productIds);
            return ResponseUtil.success("更新成功", comparison);
        } catch (Exception e) {
            log.error("更新对比列表失败", e);
            return ResponseUtil.error("更新对比列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除对比列表
     */
    @Operation(summary = "删除对比列表", description = "删除指定的商品对比列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功")
    })
    @DeleteMapping("/{comparisonId}")
    public ResponseEntity<ApiResponse<Void>> deleteComparison(
            @Parameter(description = "对比ID", required = true) @PathVariable Long comparisonId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = comparisonService.deleteComparison(comparisonId, userId);
            if (success) {
                return ResponseUtil.success("删除成功", null);
            } else {
                return ResponseUtil.error("删除失败，对比列表不存在或无权限");
            }
        } catch (Exception e) {
            log.error("删除对比列表失败", e);
            return ResponseUtil.error("删除对比列表失败，请稍后重试");
        }
    }
    
    /**
     * 生成对比表格
     */
    @Operation(summary = "生成对比表格", description = "根据商品ID列表生成对比表格数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功")
    })
    @PostMapping("/table")
    public ResponseEntity<ApiResponse<ComparisonTableDTO>> generateComparisonTable(
            @Parameter(description = "商品ID列表", required = true) @RequestBody List<Long> productIds) {
        
        try {
            ComparisonTableDTO table = comparisonService.generateComparisonTable(productIds);
            return ResponseUtil.success("生成成功", table);
        } catch (Exception e) {
            log.error("生成对比表格失败", e);
            return ResponseUtil.error("生成对比表格失败，请稍后重试");
        }
    }
}


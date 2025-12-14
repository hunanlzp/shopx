package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.ReturnOrder;
import com.shopx.service.ReturnService;
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

/**
 * 退货控制器
 */
@Slf4j
@RestController
@RequestMapping("/return")
@ApiVersion("v1")
@Tag(name = "退货管理", description = "退货退款相关API")
public class ReturnController {
    
    @Autowired
    private ReturnService returnService;
    
    /**
     * 一键退货申请
     */
    @Operation(summary = "申请退货", description = "一键申请退货")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "申请成功")
    })
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<ReturnOrder>> applyReturn(
            @Parameter(description = "订单ID", required = true) @RequestParam Long orderId,
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId,
            @Parameter(description = "退货数量", required = true) @RequestParam Integer quantity,
            @Parameter(description = "退货原因", required = true) @RequestParam String reason,
            @Parameter(description = "退货说明", required = false) @RequestParam(required = false) String description) {
        
        try {
            ReturnOrder returnOrder = returnService.createReturnOrder(orderId, productId, quantity, reason, description);
            return ResponseUtil.success("退货申请提交成功", returnOrder);
        } catch (Exception e) {
            log.error("申请退货失败", e);
            return ResponseUtil.error("申请退货失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户退货列表
     */
    @Operation(summary = "获取退货列表", description = "获取当前用户的退货订单列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<ReturnOrder>>> getUserReturnOrders(
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ResponseUtil.PageResult<ReturnOrder> result = returnService.getUserReturnOrders(userId, page, size);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取退货列表失败", e);
            return ResponseUtil.error("获取退货列表失败，请稍后重试");
        }
    }
    
    /**
     * 获取退货详情
     */
    @Operation(summary = "获取退货详情", description = "根据退货订单ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/{returnOrderId}")
    public ResponseEntity<ApiResponse<ReturnOrder>> getReturnOrderById(
            @Parameter(description = "退货订单ID", required = true) @PathVariable Long returnOrderId) {
        
        try {
            ReturnOrder returnOrder = returnService.getReturnOrderById(returnOrderId);
            if (returnOrder == null) {
                return ResponseUtil.error("退货订单不存在");
            }
            return ResponseUtil.success("查询成功", returnOrder);
        } catch (Exception e) {
            log.error("获取退货详情失败", e);
            return ResponseUtil.error("获取退货详情失败，请稍后重试");
        }
    }
    
    /**
     * 取消退货申请
     */
    @Operation(summary = "取消退货申请", description = "取消待审核的退货申请")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消成功")
    })
    @PostMapping("/{returnOrderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelReturnOrder(
            @Parameter(description = "退货订单ID", required = true) @PathVariable Long returnOrderId) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            boolean success = returnService.cancelReturnOrder(returnOrderId, userId);
            if (success) {
                return ResponseUtil.success("取消成功", null);
            } else {
                return ResponseUtil.error("取消失败，退货订单不存在或无权限");
            }
        } catch (Exception e) {
            log.error("取消退货申请失败", e);
            return ResponseUtil.error("取消退货申请失败：" + e.getMessage());
        }
    }
}


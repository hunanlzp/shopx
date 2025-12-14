package com.shopx.controller;

import com.shopx.entity.ApiResponse;
import com.shopx.entity.ShoppingSession;
import com.shopx.service.CollaborativeShoppingService;
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
 * 协作购物控制器
 */
@Slf4j
@RestController
@RequestMapping("/collaboration")
@Tag(name = "协作购物", description = "协作购物相关API")
public class CollaborativeShoppingController {
    
    @Autowired
    private CollaborativeShoppingService collaborativeService;
    
    /**
     * 创建协作购物会话
     */
    @Operation(summary = "创建协作购物会话", description = "创建新的协作购物会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "会话创建成功"),
            @ApiResponse(responseCode = "400", description = "创建失败")
    })
    @PostMapping("/session")
    public ResponseEntity<ApiResponse<CollaborationSessionResponse>> createSession(
            @Parameter(description = "主持人用户ID", required = true) @RequestParam Long hostUserId,
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId) {
        
        log.info("创建协作购物会话: hostUserId={}, productId={}", hostUserId, productId);
        
        try {
            String sessionId = collaborativeService.createSession(hostUserId, productId);
            
            CollaborationSessionResponse response = CollaborationSessionResponse.builder()
                    .sessionId(sessionId)
                    .hostUserId(hostUserId)
                    .productId(productId)
                    .status("ACTIVE")
                    .message("会话创建成功")
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("会话创建成功", response));
        } catch (Exception e) {
            log.error("创建协作购物会话失败", e);
            return ResponseEntity.ok(ApiResponse.error("创建会话失败，请稍后重试"));
        }
    }
    
    /**
     * 加入会话
     */
    @Operation(summary = "加入协作购物会话", description = "用户加入现有的协作购物会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "加入成功"),
            @ApiResponse(responseCode = "400", description = "加入失败")
    })
    @PostMapping("/session/{sessionId}/join")
    public ResponseEntity<ApiResponse<Void>> joinSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        
        log.info("用户加入会话: sessionId={}, userId={}", sessionId, userId);
        
        try {
            boolean success = collaborativeService.joinSession(sessionId, userId);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("加入会话成功", null));
            } else {
                return ResponseEntity.ok(ApiResponse.badRequest("加入会话失败"));
            }
        } catch (Exception e) {
            log.error("加入协作购物会话失败", e);
            return ResponseEntity.ok(ApiResponse.error("加入会话失败，请稍后重试"));
        }
    }
    
    /**
     * 获取会话信息
     */
    @Operation(summary = "获取协作购物会话信息", description = "获取指定会话的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<ShoppingSession>> getSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {
        
        log.info("获取会话信息: sessionId={}", sessionId);
        
        try {
            ShoppingSession session = collaborativeService.getSession(sessionId);
            
            if (session != null) {
                return ResponseEntity.ok(ApiResponse.success("获取会话信息成功", session));
            } else {
                return ResponseEntity.ok(ApiResponse.notFound("会话不存在"));
            }
        } catch (Exception e) {
            log.error("获取协作购物会话失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取会话信息失败，请稍后重试"));
        }
    }
    
    /**
     * 结束会话
     */
    @Operation(summary = "结束协作购物会话", description = "结束指定的协作购物会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "会话已结束")
    })
    @PostMapping("/session/{sessionId}/end")
    public ResponseEntity<ApiResponse<Void>> endSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {
        
        log.info("结束会话: sessionId={}", sessionId);
        
        try {
            collaborativeService.endSession(sessionId);
            return ResponseEntity.ok(ApiResponse.success("会话已结束", null));
        } catch (Exception e) {
            log.error("结束协作购物会话失败", e);
            return ResponseEntity.ok(ApiResponse.error("结束会话失败，请稍后重试"));
        }
    }
    
    /**
     * 协作购物会话响应实体
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CollaborationSessionResponse {
        private String sessionId;
        private Long hostUserId;
        private Long productId;
        private String status;
        private String message;
    }
}


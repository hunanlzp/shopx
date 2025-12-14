package com.shopx.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.shopx.annotation.ApiVersion;
import com.shopx.constant.Constants;
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

import java.time.LocalDateTime;
import java.util.*;

/**
 * 协作购物控制器
 * 提供协作购物相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/collaboration")
@ApiVersion("v1")
@Tag(name = "协作购物", description = "协作购物相关API")
public class CollaborationController {

    @Autowired
    private ProductService productService;

    // 模拟协作会话存储
    private final Map<String, Map<String, Object>> collaborationSessions = new HashMap<>();

    /**
     * 创建协作会话
     */
    @Operation(summary = "创建协作会话", description = "创建新的协作购物会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @SaCheckLogin
    @PostMapping("/session")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCollaborationSession(
            @Parameter(description = "主持人用户ID", required = true) @RequestParam Long hostUserId,
            @Parameter(description = "商品ID", required = true) @RequestParam Long productId) {

        try {
            // 验证商品是否存在
            Product product = productService.getProductById(productId);
            if (product == null) {
                return ResponseUtil.notFound("商品不存在");
            }

            // 生成会话ID
            String sessionId = generateSessionId();
            
            // 创建协作会话
            Map<String, Object> session = new HashMap<>();
            session.put("id", sessionId);
            session.put("hostUserId", hostUserId);
            session.put("productId", productId);
            session.put("productName", product.getName());
            session.put("productPrice", product.getPrice());
            session.put("participants", Arrays.asList(hostUserId));
            session.put("status", "ACTIVE");
            session.put("createTime", LocalDateTime.now());
            session.put("updateTime", LocalDateTime.now());
            session.put("chatHistory", new ArrayList<>());
            session.put("annotations", new ArrayList<>());

            // 存储会话
            collaborationSessions.put(sessionId, session);

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("session", session);

            log.info("用户 {} 创建协作会话 {}，商品ID: {}", hostUserId, sessionId, productId);

            return ResponseUtil.success("协作会话创建成功", result);
        } catch (Exception e) {
            log.error("创建协作会话失败", e);
            return ResponseUtil.error("创建协作会话失败，请稍后重试");
        }
    }

    /**
     * 加入协作会话
     */
    @Operation(summary = "加入协作会话", description = "加入现有的协作购物会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "加入成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SaCheckLogin
    @PostMapping("/session/{sessionId}/join")
    public ResponseEntity<ApiResponse<Void>> joinCollaborationSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        try {
            Map<String, Object> session = collaborationSessions.get(sessionId);
            if (session == null) {
                return ResponseUtil.notFound("协作会话不存在");
            }

            // 检查会话状态
            if (!"ACTIVE".equals(session.get("status"))) {
                return ResponseUtil.error("协作会话已结束");
            }

            // 添加参与者
            @SuppressWarnings("unchecked")
            List<Long> participants = (List<Long>) session.get("participants");
            if (!participants.contains(userId)) {
                participants.add(userId);
                session.put("participants", participants);
                session.put("updateTime", LocalDateTime.now());

                // 添加加入消息
                addChatMessage(sessionId, userId, "加入了协作会话", "system");
            }

            log.info("用户 {} 加入协作会话 {}", userId, sessionId);

            return ResponseUtil.success("加入协作会话成功", null);
        } catch (Exception e) {
            log.error("加入协作会话失败", e);
            return ResponseUtil.error("加入协作会话失败，请稍后重试");
        }
    }

    /**
     * 获取协作会话
     */
    @Operation(summary = "获取协作会话", description = "获取协作会话的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SaCheckLogin
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCollaborationSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {

        try {
            Map<String, Object> session = collaborationSessions.get(sessionId);
            if (session == null) {
                return ResponseUtil.notFound("协作会话不存在");
            }

            return ResponseUtil.success("获取协作会话成功", session);
        } catch (Exception e) {
            log.error("获取协作会话失败", e);
            return ResponseUtil.error("获取协作会话失败，请稍后重试");
        }
    }

    /**
     * 结束协作会话
     */
    @Operation(summary = "结束协作会话", description = "结束协作购物会话")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "结束成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SaCheckLogin
    @PostMapping("/session/{sessionId}/end")
    public ResponseEntity<ApiResponse<Void>> endCollaborationSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {

        try {
            Map<String, Object> session = collaborationSessions.get(sessionId);
            if (session == null) {
                return ResponseUtil.notFound("协作会话不存在");
            }

            // 更新会话状态
            session.put("status", "ENDED");
            session.put("endTime", LocalDateTime.now());
            session.put("updateTime", LocalDateTime.now());

            log.info("协作会话 {} 已结束", sessionId);

            return ResponseUtil.success("协作会话结束成功", null);
        } catch (Exception e) {
            log.error("结束协作会话失败", e);
            return ResponseUtil.error("结束协作会话失败，请稍后重试");
        }
    }

    /**
     * 发送协作消息
     */
    @Operation(summary = "发送协作消息", description = "在协作会话中发送消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "发送成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SaCheckLogin
    @PostMapping("/session/{sessionId}/message")
    public ResponseEntity<ApiResponse<Void>> sendCollaborationMessage(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "消息内容", required = true) @RequestParam String message,
            @Parameter(description = "消息类型", required = false) @RequestParam(defaultValue = "text") String messageType) {

        try {
            Map<String, Object> session = collaborationSessions.get(sessionId);
            if (session == null) {
                return ResponseUtil.notFound("协作会话不存在");
            }

            // 检查会话状态
            if (!"ACTIVE".equals(session.get("status"))) {
                return ResponseUtil.error("协作会话已结束");
            }

            // 添加消息
            addChatMessage(sessionId, userId, message, messageType);

            log.info("用户 {} 在协作会话 {} 中发送消息: {}", userId, sessionId, message);

            return ResponseUtil.success("消息发送成功", null);
        } catch (Exception e) {
            log.error("发送协作消息失败", e);
            return ResponseUtil.error("发送协作消息失败，请稍后重试");
        }
    }

    /**
     * 添加商品标注
     */
    @Operation(summary = "添加商品标注", description = "在协作会话中添加商品标注")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SaCheckLogin
    @PostMapping("/session/{sessionId}/annotation")
    public ResponseEntity<ApiResponse<Void>> addAnnotation(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "标注内容", required = true) @RequestParam String content,
            @Parameter(description = "X坐标", required = true) @RequestParam Double x,
            @Parameter(description = "Y坐标", required = true) @RequestParam Double y) {

        try {
            Map<String, Object> session = collaborationSessions.get(sessionId);
            if (session == null) {
                return ResponseUtil.notFound("协作会话不存在");
            }

            // 添加标注
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> annotations = (List<Map<String, Object>>) session.get("annotations");
            
            Map<String, Object> annotation = new HashMap<>();
            annotation.put("id", generateAnnotationId());
            annotation.put("userId", userId);
            annotation.put("content", content);
            annotation.put("x", x);
            annotation.put("y", y);
            annotation.put("timestamp", LocalDateTime.now());
            
            annotations.add(annotation);
            session.put("annotations", annotations);
            session.put("updateTime", LocalDateTime.now());

            log.info("用户 {} 在协作会话 {} 中添加标注: {}", userId, sessionId, content);

            return ResponseUtil.success("标注添加成功", null);
        } catch (Exception e) {
            log.error("添加标注失败", e);
            return ResponseUtil.error("添加标注失败，请稍后重试");
        }
    }

    /**
     * 获取协作会话详情
     */
    @Operation(summary = "获取协作会话详情", description = "根据会话ID获取协作会话的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "会话不存在")
    })
    @SaCheckLogin
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCollaborationSession(
            @Parameter(description = "会话ID", required = true) @PathVariable String sessionId) {

        try {
            Map<String, Object> session = collaborationSessions.get(sessionId);
            if (session == null) {
                return ResponseUtil.notFound("协作会话不存在");
            }

            // 获取商品信息
            Long productId = ((Number) session.get("productId")).longValue();
            Product product = productService.getProductById(productId);
            
            // 构建完整的会话信息
            Map<String, Object> sessionDetail = new HashMap<>(session);
            if (product != null) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("id", product.getId());
                productInfo.put("name", product.getName());
                productInfo.put("price", product.getPrice());
                productInfo.put("description", product.getDescription());
                productInfo.put("image", product.getImage());
                sessionDetail.put("productInfo", productInfo);
            }

            return ResponseUtil.success("获取协作会话详情成功", sessionDetail);
        } catch (Exception e) {
            log.error("获取协作会话详情失败", e);
            return ResponseUtil.error("获取协作会话详情失败，请稍后重试");
        }
    }

    /**
     * 获取用户协作会话列表
     */
    @Operation(summary = "获取用户协作会话列表", description = "获取用户参与的协作会话列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    @SaCheckLogin
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserCollaborationSessions(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {

        try {
            List<Map<String, Object>> userSessions = new ArrayList<>();
            
            // 查找用户参与的会话
            for (Map<String, Object> session : collaborationSessions.values()) {
                @SuppressWarnings("unchecked")
                List<Long> participants = (List<Long>) session.get("participants");
                if (participants.contains(userId)) {
                    userSessions.add(session);
                }
            }

            return ResponseUtil.success("获取协作会话列表成功", userSessions);
        } catch (Exception e) {
            log.error("获取协作会话列表失败", e);
            return ResponseUtil.error("获取协作会话列表失败，请稍后重试");
        }
    }

    /**
     * 添加聊天消息
     */
    private void addChatMessage(String sessionId, Long userId, String message, String messageType) {
        Map<String, Object> session = collaborationSessions.get(sessionId);
        if (session != null) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> chatHistory = (List<Map<String, Object>>) session.get("chatHistory");
            
            Map<String, Object> chatMessage = new HashMap<>();
            chatMessage.put("id", generateMessageId());
            chatMessage.put("userId", userId);
            chatMessage.put("message", message);
            chatMessage.put("type", messageType);
            chatMessage.put("timestamp", LocalDateTime.now());
            
            chatHistory.add(chatMessage);
            session.put("chatHistory", chatHistory);
            session.put("updateTime", LocalDateTime.now());
        }
    }

    /**
     * 生成会话ID
     */
    private String generateSessionId() {
        return "collab_" + System.currentTimeMillis();
    }

    /**
     * 生成消息ID
     */
    private String generateMessageId() {
        return "msg_" + System.currentTimeMillis();
    }

    /**
     * 生成标注ID
     */
    private String generateAnnotationId() {
        return "anno_" + System.currentTimeMillis();
    }
}

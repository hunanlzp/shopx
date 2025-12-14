package com.shopx.controller;

import com.shopx.annotation.ApiVersion;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.CustomerServiceTicket;
import com.shopx.entity.FAQ;
import com.shopx.service.CustomerServiceService;
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
import java.util.Map;

/**
 * 客服控制器
 */
@Slf4j
@RestController
@RequestMapping("/customer-service")
@ApiVersion("v1")
@Tag(name = "客服管理", description = "客服工单和FAQ相关API")
public class CustomerServiceController {
    
    @Autowired
    private CustomerServiceService customerServiceService;
    
    /**
     * 创建客服工单
     */
    @Operation(summary = "创建工单", description = "创建新的客服工单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功")
    })
    @PostMapping("/ticket")
    public ResponseEntity<ApiResponse<CustomerServiceTicket>> createTicket(
            @Parameter(description = "工单类型", required = true) @RequestParam String ticketType,
            @Parameter(description = "标题", required = true) @RequestParam String title,
            @Parameter(description = "内容", required = true) @RequestParam String content,
            @Parameter(description = "优先级", required = false) @RequestParam(required = false) String priority) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            CustomerServiceTicket ticket = customerServiceService.createTicket(userId, ticketType, title, content, priority);
            return ResponseUtil.success("工单创建成功", ticket);
        } catch (Exception e) {
            log.error("创建工单失败", e);
            return ResponseUtil.error("创建工单失败，请稍后重试");
        }
    }
    
    /**
     * 获取用户工单列表
     */
    @Operation(summary = "获取工单列表", description = "获取当前用户的所有工单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<CustomerServiceTicket>>> getUserTickets(
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            ResponseUtil.PageResult<CustomerServiceTicket> result = customerServiceService.getUserTickets(userId, page, size);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取工单列表失败", e);
            return ResponseUtil.error("获取工单列表失败，请稍后重试");
        }
    }
    
    /**
     * 获取工单详情
     */
    @Operation(summary = "获取工单详情", description = "根据工单ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<ApiResponse<CustomerServiceTicket>> getTicketById(
            @Parameter(description = "工单ID", required = true) @PathVariable Long ticketId) {
        
        try {
            CustomerServiceTicket ticket = customerServiceService.getTicketById(ticketId);
            if (ticket == null) {
                return ResponseUtil.error("工单不存在");
            }
            return ResponseUtil.success("查询成功", ticket);
        } catch (Exception e) {
            log.error("获取工单详情失败", e);
            return ResponseUtil.error("获取工单详情失败，请稍后重试");
        }
    }
    
    /**
     * 获取常见问题列表
     */
    @Operation(summary = "获取常见问题", description = "获取常见问题列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/faq")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<FAQ>>> getFAQs(
            @Parameter(description = "分类", required = false) @RequestParam(required = false) String category,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            ResponseUtil.PageResult<FAQ> result = customerServiceService.getFAQs(category, page, size);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取常见问题失败", e);
            return ResponseUtil.error("获取常见问题失败，请稍后重试");
        }
    }
    
    /**
     * 搜索常见问题
     */
    @Operation(summary = "搜索常见问题", description = "根据关键词搜索常见问题")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/faq/search")
    public ResponseEntity<ApiResponse<List<FAQ>>> searchFAQs(
            @Parameter(description = "关键词", required = true) @RequestParam String keyword) {
        
        try {
            List<FAQ> faqs = customerServiceService.searchFAQs(keyword);
            return ResponseUtil.success("查询成功", faqs);
        } catch (Exception e) {
            log.error("搜索常见问题失败", e);
            return ResponseUtil.error("搜索常见问题失败，请稍后重试");
        }
    }
    
    /**
     * 标记FAQ有用
     */
    @Operation(summary = "标记FAQ有用", description = "标记常见问题为有用")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "标记成功")
    })
    @PostMapping("/faq/{faqId}/helpful")
    public ResponseEntity<ApiResponse<Void>> markFAQHelpful(
            @Parameter(description = "FAQ ID", required = true) @PathVariable Long faqId) {
        
        try {
            boolean success = customerServiceService.markFAQHelpful(faqId);
            if (success) {
                return ResponseUtil.success("标记成功", null);
            } else {
                return ResponseUtil.error("标记失败，FAQ不存在");
            }
        } catch (Exception e) {
            log.error("标记FAQ有用失败", e);
            return ResponseUtil.error("标记失败，请稍后重试");
        }
    }
}


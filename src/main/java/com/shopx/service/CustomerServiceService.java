package com.shopx.service;

import com.shopx.entity.CustomerServiceTicket;
import com.shopx.entity.FAQ;
import com.shopx.util.ResponseUtil;

import java.util.List;
import java.util.Map;

/**
 * 客服服务接口
 */
public interface CustomerServiceService {
    
    /**
     * 创建客服工单
     */
    CustomerServiceTicket createTicket(Long userId, String ticketType, String title, String content, String priority);
    
    /**
     * 获取用户工单列表
     */
    ResponseUtil.PageResult<CustomerServiceTicket> getUserTickets(Long userId, int page, int size);
    
    /**
     * 获取工单详情
     */
    CustomerServiceTicket getTicketById(Long ticketId);
    
    /**
     * 客服回复工单
     */
    boolean replyTicket(Long ticketId, Long serviceStaffId, String reply);
    
    /**
     * 更新工单状态
     */
    boolean updateTicketStatus(Long ticketId, String status);
    
    /**
     * 获取常见问题列表
     */
    ResponseUtil.PageResult<FAQ> getFAQs(String category, int page, int size);
    
    /**
     * 搜索常见问题
     */
    List<FAQ> searchFAQs(String keyword);
    
    /**
     * 记录FAQ有用性
     */
    boolean markFAQHelpful(Long faqId);
    
    /**
     * 获取客服响应时间统计
     */
    Map<String, Object> getResponseTimeStats(Long serviceStaffId);
}


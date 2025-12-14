package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopx.entity.CustomerServiceTicket;
import com.shopx.entity.FAQ;
import com.shopx.mapper.CustomerServiceTicketMapper;
import com.shopx.mapper.FAQMapper;
import com.shopx.service.CustomerServiceService;
import com.shopx.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客服服务实现类
 */
@Slf4j
@Service
public class CustomerServiceServiceImpl extends ServiceImpl<CustomerServiceTicketMapper, CustomerServiceTicket> implements CustomerServiceService {
    
    @Autowired
    private CustomerServiceTicketMapper ticketMapper;
    
    @Autowired
    private FAQMapper faqMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerServiceTicket createTicket(Long userId, String ticketType, String title, String content, String priority) {
        log.info("创建客服工单: userId={}, ticketType={}, title={}", userId, ticketType, title);
        
        // 生成工单号
        String ticketNo = "T" + System.currentTimeMillis();
        
        CustomerServiceTicket ticket = new CustomerServiceTicket();
        ticket.setUserId(userId);
        ticket.setTicketNo(ticketNo);
        ticket.setTicketType(ticketType);
        ticket.setTitle(title);
        ticket.setContent(content);
        ticket.setPriority(priority != null ? priority : Constants.TicketPriority.MEDIUM);
        ticket.setStatus(Constants.TicketStatus.OPEN);
        
        ticketMapper.insert(ticket);
        return ticket;
    }
    
    @Override
    public ResponseUtil.PageResult<CustomerServiceTicket> getUserTickets(Long userId, int page, int size) {
        QueryWrapper<CustomerServiceTicket> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        
        Page<CustomerServiceTicket> pageParam = new Page<>(page, size);
        Page<CustomerServiceTicket> result = ticketMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<CustomerServiceTicket>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public CustomerServiceTicket getTicketById(Long ticketId) {
        return ticketMapper.selectById(ticketId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replyTicket(Long ticketId, Long serviceStaffId, String reply) {
        CustomerServiceTicket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            return false;
        }
        
        ticket.setServiceStaffId(serviceStaffId);
        ticket.setReply(reply);
        ticket.setReplyTime(LocalDateTime.now());
        ticket.setStatus("PROCESSING");
        
        ticketMapper.updateById(ticket);
        return true;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTicketStatus(Long ticketId, String status) {
        CustomerServiceTicket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            return false;
        }
        
        ticket.setStatus(status);
        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            ticket.setResolvedTime(LocalDateTime.now());
        }
        
        ticketMapper.updateById(ticket);
        return true;
    }
    
    @Override
    public ResponseUtil.PageResult<FAQ> getFAQs(String category, int page, int size) {
        QueryWrapper<FAQ> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", true);
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq("category", category);
        }
        
        queryWrapper.orderByAsc("sort_order")
                   .orderByDesc("helpful_count");
        
        Page<FAQ> pageParam = new Page<>(page, size);
        Page<FAQ> result = faqMapper.selectPage(pageParam, queryWrapper);
        
        return ResponseUtil.PageResult.<FAQ>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public List<FAQ> searchFAQs(String keyword) {
        QueryWrapper<FAQ> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", true)
                   .and(wrapper -> wrapper
                       .like("question", keyword)
                       .or()
                       .like("answer", keyword)
                   )
                   .orderByDesc("helpful_count")
                   .last("LIMIT 10");
        
        return faqMapper.selectList(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markFAQHelpful(Long faqId) {
        FAQ faq = faqMapper.selectById(faqId);
        if (faq == null) {
            return false;
        }
        
        faq.setHelpfulCount(faq.getHelpfulCount() + 1);
        faqMapper.updateById(faq);
        return true;
    }
    
    @Override
    public Map<String, Object> getResponseTimeStats(Long serviceStaffId) {
        QueryWrapper<CustomerServiceTicket> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_staff_id", serviceStaffId)
                   .isNotNull("reply_time");
        
        List<CustomerServiceTicket> tickets = ticketMapper.selectList(queryWrapper);
        
        Map<String, Object> stats = new HashMap<>();
        
        if (!tickets.isEmpty()) {
            // 计算平均响应时间
            List<Long> responseTimes = tickets.stream()
                    .filter(t -> t.getCreateTime() != null && t.getReplyTime() != null)
                    .map(t -> Duration.between(t.getCreateTime(), t.getReplyTime()).toMinutes())
                    .collect(Collectors.toList());
            
            if (!responseTimes.isEmpty()) {
                double avgResponseTime = responseTimes.stream()
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0.0);
                
                stats.put("averageResponseTime", avgResponseTime);
                stats.put("totalTickets", tickets.size());
                stats.put("resolvedTickets", tickets.stream()
                        .filter(t -> "RESOLVED".equals(t.getStatus()))
                        .count());
            }
        } else {
            stats.put("averageResponseTime", 0.0);
            stats.put("totalTickets", 0);
            stats.put("resolvedTickets", 0);
        }
        
        return stats;
    }
}


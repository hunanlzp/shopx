package com.shopx.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单响应DTO
 */
@Data
public class TicketResponseDTO {
    private Long id;
    private Long userId;
    private String ticketType;
    private String title;
    private String content;
    private String priority;
    private String status;
    private List<String> attachments;
    private List<TicketReplyDTO> replies;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Data
    public static class TicketReplyDTO {
        private Long id;
        private Long userId;
        private String content;
        private Boolean isStaff;
        private LocalDateTime createTime;
    }
}


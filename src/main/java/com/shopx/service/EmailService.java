package com.shopx.service;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送简单文本邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendSimpleEmail(String to, String subject, String content);
    
    /**
     * 发送HTML格式邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML内容
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);
    
    /**
     * 发送带附件的邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param attachmentPath 附件路径
     * @param attachmentName 附件名称
     */
    void sendEmailWithAttachment(String to, String subject, String content, 
                                 String attachmentPath, String attachmentName);
}


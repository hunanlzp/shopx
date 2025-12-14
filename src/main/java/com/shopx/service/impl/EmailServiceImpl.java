package com.shopx.service.impl;

import com.shopx.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮件服务实现类
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;
    
    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("简单邮件发送成功: {} -> {}", from, to);
        } catch (Exception e) {
            log.error("发送简单邮件失败: {} -> {}", from, to, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
    
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML邮件发送成功: {} -> {}", from, to);
        } catch (Exception e) {
            log.error("发送HTML邮件失败: {} -> {}", from, to, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
    
    @Override
    public void sendEmailWithAttachment(String to, String subject, String content, 
                                       String attachmentPath, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            
            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(attachmentName, file);
            
            mailSender.send(message);
            log.info("带附件邮件发送成功: {} -> {}, 附件: {}", from, to, attachmentName);
        } catch (Exception e) {
            log.error("发送带附件邮件失败: {} -> {}", from, to, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}


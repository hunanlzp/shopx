package com.shopx.controller;

import com.shopx.entity.ApiResponse;
import com.shopx.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 邮件发送控制器
 */
@Slf4j
@RestController
@RequestMapping("/emails")
@Tag(name = "邮件服务", description = "发送文本、HTML、附件邮件接口")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @Operation(summary = "发送文本邮件")
    @PostMapping("/text")
    public ResponseEntity<ApiResponse<Void>> sendText(@RequestParam String to,
                                                      @RequestParam String subject,
                                                      @RequestParam String content) {
        emailService.sendSimpleEmail(to, subject, content);
        return ResponseEntity.ok(ApiResponse.success("邮件发送成功", null));
    }

    @Operation(summary = "发送HTML邮件")
    @PostMapping("/html")
    public ResponseEntity<ApiResponse<Void>> sendHtml(@RequestParam String to,
                                                      @RequestParam String subject,
                                                      @RequestParam String htmlContent) {
        emailService.sendHtmlEmail(to, subject, htmlContent);
        return ResponseEntity.ok(ApiResponse.success("邮件发送成功", null));
    }

    @Operation(summary = "发送带附件的邮件")
    @PostMapping("/attachment")
    public ResponseEntity<ApiResponse<Void>> sendWithAttachment(@RequestParam String to,
                                                                @RequestParam String subject,
                                                                @RequestParam String content,
                                                                @RequestParam("file") MultipartFile attachment) {
        try {
            String safeName = attachment.getOriginalFilename() == null ? "attachment" : attachment.getOriginalFilename();
            Path tempFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve(safeName);
            attachment.transferTo(tempFile.toFile());
            emailService.sendEmailWithAttachment(to, subject, content, tempFile.toString(), safeName);
            Files.deleteIfExists(tempFile);
        } catch (Exception e) {
            log.error("发送带附件邮件失败", e);
            return ResponseEntity.ok(ApiResponse.error("邮件发送失败"));
        }
        return ResponseEntity.ok(ApiResponse.success("邮件发送成功", null));
    }
}


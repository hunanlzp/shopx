package com.shopx.controller;

import com.shopx.config.ShopXConfig;
import com.shopx.entity.ApiResponse;
import com.shopx.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传与下载控制器
 */
@Slf4j
@RestController
@RequestMapping("/files")
@Tag(name = "文件管理", description = "文件上传、删除、类型校验等接口")
@RequiredArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;
    private final ShopXConfig shopXConfig;

    @Operation(summary = "上传单个文件")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> upload(@RequestParam("file") MultipartFile file) {
        String path = fileUploadService.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.success("上传成功", path));
    }

    @Operation(summary = "批量上传文件")
    @PostMapping("/upload/batch")
    public ResponseEntity<ApiResponse<List<String>>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        List<String> paths = fileUploadService.uploadFiles(files);
        return ResponseEntity.ok(ApiResponse.success("上传成功", paths));
    }

    @Operation(summary = "上传图片并生成缩略图")
    @PostMapping("/upload/image")
    public ResponseEntity<ApiResponse<FileUploadService.ImageUploadResult>> uploadImage(@RequestParam("file") MultipartFile file) {
        FileUploadService.ImageUploadResult result = fileUploadService.uploadImageWithThumbnail(file);
        return ResponseEntity.ok(ApiResponse.success("上传成功", result));
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> delete(@RequestParam("path") String path) {
        boolean deleted = fileUploadService.deleteFile(path);
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("删除成功", true));
        }
        return ResponseEntity.ok(ApiResponse.error("文件不存在或删除失败"));
    }

    @Operation(summary = "获取允许的文件类型")
    @GetMapping("/allowed-types")
    public ResponseEntity<ApiResponse<List<String>>> allowedTypes() {
        List<String> types = Arrays.asList(shopXConfig.getFileUpload().getAllowedTypes());
        return ResponseEntity.ok(ApiResponse.success(types));
    }
}









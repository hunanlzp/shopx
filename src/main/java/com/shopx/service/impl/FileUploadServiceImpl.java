package com.shopx.service.impl;

import com.shopx.config.ShopXConfig;
import com.shopx.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    @Autowired
    private ShopXConfig shopXConfig;
    
    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;
    
    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件类型
        if (!isAllowedFileType(file.getOriginalFilename())) {
            throw new IllegalArgumentException("不支持的文件类型");
        }
        
        // 检查文件大小
        if (file.getSize() > shopXConfig.getFileUpload().getMaxFileSize()) {
            throw new IllegalArgumentException("文件大小超过限制");
        }
        
        try {
            Path basePath = resolveBasePath();
            // 生成文件路径
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String safeName = sanitizeFilename(file.getOriginalFilename());
            String relativePath = dateDir + "/" + UUID.randomUUID().toString() + "_" + safeName;
            Path fullPath = basePath.resolve(relativePath);
            
            // 创建目录
            Files.createDirectories(fullPath.getParent());
            
            // 保存文件
            file.transferTo(fullPath.toFile());
            
            log.info("文件上传成功: {}", relativePath);
            return "/uploads/" + relativePath;
        } catch (IOException e) {
            log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    
    @Override
    public List<String> uploadFiles(MultipartFile[] files) {
        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            paths.add(uploadFile(file));
        }
        return paths;
    }
    
    @Override
    public ImageUploadResult uploadImageWithThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isImageFile(originalFilename)) {
            throw new IllegalArgumentException("不是有效的图片文件");
        }
        
        try {
            // 上传原图
            String originalPath = uploadFile(file);
            
            // 生成缩略图
            String thumbnailPath = generateThumbnail(file, originalPath);
            
            return new ImageUploadResult(originalPath, thumbnailPath);
        } catch (Exception e) {
            log.error("图片上传失败: {}", originalFilename, e);
            throw new RuntimeException("图片上传失败", e);
        }
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        try {
            Path basePath = resolveBasePath();
            Path path = basePath.resolve(filePath.replace("/uploads/", ""));
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }
    
    @Override
    public boolean isAllowedFileType(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        String[] allowedTypes = shopXConfig.getFileUpload().getAllowedTypes();
        
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return "jpg".equals(extension) || "jpeg".equals(extension) 
            || "png".equals(extension) || "gif".equals(extension);
    }
    
    /**
     * 生成缩略图
     */
    private String generateThumbnail(MultipartFile file, String originalPath) throws IOException {
        Path basePath = resolveBasePath();
        String relativePath = originalPath.replace("/uploads/", "");
        Path originalFilePath = basePath.resolve(relativePath);
        
        // 生成缩略图路径
        String thumbnailRelativePath = relativePath.replace(".", "_thumb.");
        Path thumbnailPath = basePath.resolve(thumbnailRelativePath);
        
        // 创建缩略图目录
        Files.createDirectories(thumbnailPath.getParent());
        
        // 生成缩略图
        Thumbnails.of(originalFilePath.toFile())
            .size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
            .keepAspectRatio(true)
            .toFile(thumbnailPath.toFile());
        
        log.info("缩略图生成成功: {}", thumbnailRelativePath);
        return "/uploads/" + thumbnailRelativePath;
    }

    /**
     * 解析并创建上传根目录
     */
    private Path resolveBasePath() throws IOException {
        Path basePath = Paths.get(shopXConfig.getFileUpload().getUploadPath()).toAbsolutePath().normalize();
        Files.createDirectories(basePath);
        return basePath;
    }

    /**
     * 仅保留文件名，去除路径片段
     */
    private String sanitizeFilename(String originalName) {
        if (originalName == null) {
            return UUID.randomUUID().toString();
        }
        return Paths.get(originalName).getFileName().toString();
    }
}


package com.shopx.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 上传单个文件
     * @param file 文件
     * @return 文件访问路径
     */
    String uploadFile(MultipartFile file);
    
    /**
     * 上传多个文件
     * @param files 文件列表
     * @return 文件访问路径列表
     */
    List<String> uploadFiles(MultipartFile[] files);
    
    /**
     * 上传图片并生成缩略图
     * @param file 图片文件
     * @return 包含原图和缩略图路径的对象
     */
    ImageUploadResult uploadImageWithThumbnail(MultipartFile file);
    
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 检查文件类型是否允许
     * @param filename 文件名
     * @return 是否允许
     */
    boolean isAllowedFileType(String filename);
    
    /**
     * 图片上传结果
     */
    class ImageUploadResult {
        private String originalPath;
        private String thumbnailPath;
        
        public ImageUploadResult(String originalPath, String thumbnailPath) {
            this.originalPath = originalPath;
            this.thumbnailPath = thumbnailPath;
        }
        
        public String getOriginalPath() {
            return originalPath;
        }
        
        public String getThumbnailPath() {
            return thumbnailPath;
        }
    }
}


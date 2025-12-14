package com.shopx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 扩展配置
 * 用于暴露本地上传目录为静态资源
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ShopXConfig shopXConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadBase = shopXConfig.getFileUpload().getUploadPath();
        Path uploadPath = Paths.get(uploadBase).toAbsolutePath();
        String uploadLocation = uploadPath.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}









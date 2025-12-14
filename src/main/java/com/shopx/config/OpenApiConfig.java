package com.shopx.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger配置
 * 提供完整的API文档和交互式测试界面
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShopX创新电商平台API")
                        .description("基于创新电商设计理念的颠覆性购物平台API文档\n\n" +
                                "## 核心特性\n" +
                                "- 🎯 **情境化推荐**：基于用户生活场景的智能推荐\n" +
                                "- 👥 **协作购物**：多人实时购物体验\n" +
                                "- 🤖 **AI助手**：个性化购物建议\n" +
                                "- 🔄 **价值循环**：产品回收与再利用\n" +
                                "- 🥽 **AR/VR体验**：沉浸式购物体验\n\n" +
                                "## 技术栈\n" +
                                "- Spring Boot 3.2.0\n" +
                                "- MyBatis-Plus 3.5.5\n" +
                                "- Redis/Redisson\n" +
                                "- MySQL 8.0")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("ShopX开发团队")
                                .email("dev@shopx.com")
                                .url("https://www.shopx.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("本地开发环境"),
                        new Server()
                                .url("https://api.shopx.com")
                                .description("生产环境")
                ));
    }
}

package com.shopx.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.shopx.annotation.ApiVersion;
import com.shopx.constant.Constants;
import com.shopx.entity.ApiResponse;
import com.shopx.entity.Product;
import com.shopx.entity.SavedFilter;
import com.shopx.entity.SearchHistory;
import com.shopx.service.ProductService;
import com.shopx.service.SearchService;
import com.shopx.util.ResponseUtil;
import com.shopx.validation.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * 提供商品的基础CRUD操作和情境化查询功能
 */
@Slf4j
@RestController
@RequestMapping("/products")
@ApiVersion("v1")
@Tag(name = "商品管理", description = "商品相关API，包括商品查询、添加等功能")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private SearchService searchService;
    
    /**
     * 获取所有商品
     * 返回系统中所有可用的商品列表，支持分页和筛选
     */
    @Operation(summary = "获取商品列表", description = "获取系统中所有商品，支持分页查询")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<Product>>> getAllProducts(
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词", required = false) @RequestParam(required = false) String keyword,
            @Parameter(description = "分类", required = false) @RequestParam(required = false) String category) {
        
        try {
            ResponseUtil.PageResult<Product> result = productService.getProducts(page, size, keyword, category);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            return ResponseUtil.error("获取商品列表失败，请稍后重试");
        }
    }
    
    /**
     * 根据ID获取商品详情
     * 获取指定商品的详细信息，包括AR/VR体验链接等
     */
    @Operation(summary = "获取商品详情", description = "根据商品ID获取详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id) {
        
        try {
            Product product = productService.getProductById(id);
            return ResponseUtil.success("查询成功", product);
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return ResponseUtil.error("获取商品详情失败，请稍后重试");
        }
    }
    
    /**
     * 添加新商品
     * 创建新的商品记录，支持情境化属性和AR/VR体验配置
     */
    @Operation(summary = "添加商品", description = "创建新的商品记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @SaCheckPermission(Constants.Permission.PRODUCT_ADD)
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> addProduct(
            @Parameter(description = "商品信息", required = true) @RequestBody Product product) {
        
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseUtil.success("添加成功", createdProduct);
        } catch (Exception e) {
            log.error("添加商品失败", e);
            return ResponseUtil.error("添加商品失败，请稍后重试");
        }
    }
    
    /**
     * 更新商品信息
     */
    @Operation(summary = "更新商品", description = "更新现有商品信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @SaCheckPermission(Constants.Permission.PRODUCT_UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Parameter(description = "商品信息", required = true) @RequestBody Product product) {
        
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseUtil.success("更新成功", updatedProduct);
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return ResponseUtil.error("更新商品失败，请稍后重试");
        }
    }
    
    /**
     * 删除商品
     */
    @Operation(summary = "删除商品", description = "删除指定商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @SaCheckPermission(Constants.Permission.PRODUCT_DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id) {
        
        try {
            boolean success = productService.deleteProduct(id);
            return ResponseUtil.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return ResponseUtil.error("删除商品失败，请稍后重试");
        }
    }
    
    /**
     * 批量删除商品
     */
    @Operation(summary = "批量删除商品", description = "批量删除多个商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @SaCheckPermission(Constants.Permission.PRODUCT_DELETE)
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteProducts(
            @Parameter(description = "商品ID列表", required = true) @RequestBody List<Long> ids) {
        
        try {
            boolean success = productService.batchDeleteProducts(ids);
            return ResponseUtil.success("批量删除成功", null);
        } catch (Exception e) {
            log.error("批量删除商品失败", e);
            return ResponseUtil.error("批量删除商品失败，请稍后重试");
        }
    }
    
    /**
     * 更新商品库存
     */
    @Operation(summary = "更新商品库存", description = "更新指定商品的库存数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Void>> updateStock(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Parameter(description = "库存数量", required = true) @RequestParam Integer quantity) {
        
        try {
            boolean success = productService.updateStock(id, quantity);
            return ResponseUtil.success("库存更新成功", null);
        } catch (Exception e) {
            log.error("更新商品库存失败", e);
            return ResponseUtil.error("更新商品库存失败，请稍后重试");
        }
    }
    
    /**
     * 获取热门商品
     */
    @Operation(summary = "获取热门商品", description = "获取浏览量最高的商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<Product>>> getHotProducts(
            @Parameter(description = "数量限制", required = false) @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<Product> products = productService.getHotProducts(limit);
            return ResponseUtil.success("查询成功", products);
        } catch (Exception e) {
            log.error("获取热门商品失败", e);
            return ResponseUtil.error("获取热门商品失败，请稍后重试");
        }
    }
    
    /**
     * 搜索商品
     */
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<Product>>> searchProducts(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            ResponseUtil.PageResult<Product> result = productService.searchProducts(keyword, page, size);
            return ResponseUtil.success("搜索成功", result);
        } catch (Exception e) {
            log.error("搜索商品失败", e);
            return ResponseUtil.error("搜索商品失败，请稍后重试");
        }
    }
    
    /**
     * 根据分类获取商品
     */
    @Operation(summary = "根据分类获取商品", description = "获取指定分类的商品列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<Product>>> getProductsByCategory(
            @Parameter(description = "商品分类", required = true) @PathVariable String category,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            ResponseUtil.PageResult<Product> result = productService.getProductsByCategory(category, page, size);
            return ResponseUtil.success("查询成功", result);
        } catch (Exception e) {
            log.error("根据分类获取商品失败", e);
            return ResponseUtil.error("根据分类获取商品失败，请稍后重试");
        }
    }
    
    /**
     * 高级搜索
     */
    @Operation(summary = "高级搜索", description = "支持多维度筛选的商品搜索")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功")
    })
    @PostMapping("/search/advanced")
    public ResponseEntity<ApiResponse<ResponseUtil.PageResult<Product>>> advancedSearch(
            @Parameter(description = "搜索关键词", required = false) @RequestParam(required = false) String keyword,
            @Parameter(description = "筛选条件", required = false) @RequestBody(required = false) Map<String, Object> filters,
            @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", required = false) @RequestParam(defaultValue = "20") int size) {
        
        try {
            ResponseUtil.PageResult<Product> result = searchService.advancedSearch(keyword, filters, page, size);
            return ResponseUtil.success("搜索成功", result);
        } catch (Exception e) {
            log.error("高级搜索失败", e);
            return ResponseUtil.error("高级搜索失败，请稍后重试");
        }
    }
    
    /**
     * 获取搜索建议
     */
    @Operation(summary = "获取搜索建议", description = "根据关键词前缀获取搜索建议")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/search/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(
            @Parameter(description = "关键词前缀", required = true) @RequestParam String keyword,
            @Parameter(description = "数量限制", required = false) @RequestParam(defaultValue = "10") int limit) {
        
        try {
            List<String> suggestions = searchService.getSearchSuggestions(keyword, limit);
            return ResponseUtil.success("查询成功", suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议失败", e);
            return ResponseUtil.error("获取搜索建议失败，请稍后重试");
        }
    }
    
    /**
     * 获取搜索历史
     */
    @Operation(summary = "获取搜索历史", description = "获取当前用户的搜索历史")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/search/history")
    public ResponseEntity<ApiResponse<List<SearchHistory>>> getSearchHistory(
            @Parameter(description = "数量限制", required = false) @RequestParam(defaultValue = "20") int limit) {
        
        try {
            Long userId = com.shopx.util.SaTokenUtil.getCurrentUserId();
            List<SearchHistory> history = searchService.getUserSearchHistory(userId, limit);
            return ResponseUtil.success("查询成功", history);
        } catch (Exception e) {
            log.error("获取搜索历史失败", e);
            return ResponseUtil.error("获取搜索历史失败，请稍后重试");
        }
    }
    
    /**
     * 保存筛选条件
     */
    @Operation(summary = "保存筛选条件", description = "保存用户自定义的筛选条件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功")
    })
    @PostMapping("/search/filters")
    public ResponseEntity<ApiResponse<SavedFilter>> saveFilter(
            @Parameter(description = "筛选条件名称", required = true) @RequestParam String filterName,
            @Parameter(description = "筛选条件", required = true) @RequestBody Map<String, Object> filterConditions,
            @Parameter(description = "是否默认", required = false) @RequestParam(required = false) Boolean isDefault) {
        
        try {
            Long userId = com.shopx.util.SaTokenUtil.getCurrentUserId();
            String filterJson = convertFiltersToJson(filterConditions);
            SavedFilter filter = searchService.saveFilter(userId, filterName, filterJson, isDefault);
            return ResponseUtil.success("保存成功", filter);
        } catch (Exception e) {
            log.error("保存筛选条件失败", e);
            return ResponseUtil.error("保存筛选条件失败，请稍后重试");
        }
    }
    
    /**
     * 获取保存的筛选条件
     */
    @Operation(summary = "获取保存的筛选条件", description = "获取当前用户保存的所有筛选条件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    @GetMapping("/search/filters")
    public ResponseEntity<ApiResponse<List<SavedFilter>>> getSavedFilters() {
        
        try {
            Long userId = com.shopx.util.SaTokenUtil.getCurrentUserId();
            List<SavedFilter> filters = searchService.getUserSavedFilters(userId);
            return ResponseUtil.success("查询成功", filters);
        } catch (Exception e) {
            log.error("获取保存的筛选条件失败", e);
            return ResponseUtil.error("获取保存的筛选条件失败，请稍后重试");
        }
    }
    
    /**
     * 删除保存的筛选条件
     */
    @Operation(summary = "删除保存的筛选条件", description = "删除指定的筛选条件")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功")
    })
    @DeleteMapping("/search/filters/{filterId}")
    public ResponseEntity<ApiResponse<Void>> deleteSavedFilter(
            @Parameter(description = "筛选条件ID", required = true) @PathVariable Long filterId) {
        
        try {
            Long userId = com.shopx.util.SaTokenUtil.getCurrentUserId();
            boolean success = searchService.deleteSavedFilter(filterId, userId);
            if (success) {
                return ResponseUtil.success("删除成功", null);
            } else {
                return ResponseUtil.error("删除失败，筛选条件不存在或无权限");
            }
        } catch (Exception e) {
            log.error("删除保存的筛选条件失败", e);
            return ResponseUtil.error("删除保存的筛选条件失败，请稍后重试");
        }
    }
    
    /**
     * 将筛选条件Map转换为JSON字符串
     */
    private String convertFiltersToJson(Map<String, Object> filters) {
        try {
            // 简单的JSON转换，实际可以使用Jackson或Gson
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof String) {
                    json.append("\"").append(entry.getValue()).append("\"");
                } else {
                    json.append(entry.getValue());
                }
                first = false;
            }
            json.append("}");
            return json.toString();
        } catch (Exception e) {
            log.warn("转换筛选条件为JSON失败", e);
            return null;
        }
    }
}

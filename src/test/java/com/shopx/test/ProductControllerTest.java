package com.shopx.test;

import com.shopx.controller.ProductController;
import com.shopx.entity.Product;
import com.shopx.service.ProductService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import com.shopx.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 商品控制器测试类
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private SaTokenUtil saTokenUtil;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setDescription("这是一个测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory("电子产品");
        testProduct.setImage("https://example.com/image.jpg");
        testProduct.setStock(100);
        testProduct.setStatus("ACTIVE");
        testProduct.setViewCount(0);
        testProduct.setLikeCount(0);
        testProduct.setShareCount(0);
        testProduct.setHas3dPreview(true);
        testProduct.setArModelUrl("https://example.com/ar/model");
        testProduct.setVrExperienceUrl("https://example.com/vr/experience");
        testProduct.setCreateTime(LocalDateTime.now());
        testProduct.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testGetAllProducts_Success() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProducts(1, 20, null, null)).thenReturn(products);

        // When
        ResponseEntity<?> response = productController.getAllProducts(1, 20, null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).getProducts(1, 20, null, null);
    }

    @Test
    void testGetProductById_Success() {
        // Given
        Long productId = 1L;
        when(productService.getProductById(productId)).thenReturn(testProduct);

        // When
        ResponseEntity<?> response = productController.getProductById(productId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).getProductById(productId);
    }

    @Test
    void testGetProductById_NotFound() {
        // Given
        Long productId = 999L;
        when(productService.getProductById(productId)).thenThrow(new BusinessException("商品不存在"));

        // When
        ResponseEntity<?> response = productController.getProductById(productId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).getProductById(productId);
    }

    @Test
    void testAddProduct_Success() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("新商品");
        newProduct.setDescription("新商品描述");
        newProduct.setPrice(new BigDecimal("199.99"));
        newProduct.setCategory("服装");
        newProduct.setStock(50);
        newProduct.setStatus("ACTIVE");

        when(productService.createProduct(any(Product.class))).thenReturn(newProduct);
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.addProduct(newProduct);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void testAddProduct_Unauthorized() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("新商品");
        when(saTokenUtil.getCurrentUserId()).thenReturn(null);

        // When
        ResponseEntity<?> response = productController.addProduct(newProduct);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        Long productId = 1L;
        Product updateProduct = new Product();
        updateProduct.setName("更新后的商品");
        updateProduct.setPrice(new BigDecimal("149.99"));

        when(productService.updateProduct(productId, updateProduct)).thenReturn(updateProduct);
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.updateProduct(productId, updateProduct);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).updateProduct(productId, updateProduct);
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Given
        Long productId = 999L;
        Product updateProduct = new Product();
        updateProduct.setName("更新后的商品");

        when(productService.updateProduct(productId, updateProduct))
                .thenThrow(new BusinessException("商品不存在"));
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.updateProduct(productId, updateProduct);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).updateProduct(productId, updateProduct);
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        Long productId = 1L;
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.deleteProduct(productId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).deleteProduct(productId);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Given
        Long productId = 999L;
        when(productService.deleteProduct(productId))
                .thenThrow(new BusinessException("商品不存在"));
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.deleteProduct(productId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).deleteProduct(productId);
    }

    @Test
    void testBatchDeleteProducts_Success() {
        // Given
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.batchDeleteProducts(productIds);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).batchDeleteProducts(productIds);
    }

    @Test
    void testUpdateStock_Success() {
        // Given
        Long productId = 1L;
        Integer newStock = 150;
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.updateStock(productId, newStock);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).updateStock(productId, newStock);
    }

    @Test
    void testGetHotProducts_Success() {
        // Given
        List<Product> hotProducts = Arrays.asList(testProduct);
        when(productService.getHotProducts(10)).thenReturn(hotProducts);

        // When
        ResponseEntity<?> response = productController.getHotProducts(10);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).getHotProducts(10);
    }

    @Test
    void testSearchProducts_Success() {
        // Given
        String keyword = "测试";
        List<Product> searchResults = Arrays.asList(testProduct);
        when(productService.searchProducts(keyword, 1, 20)).thenReturn(searchResults);

        // When
        ResponseEntity<?> response = productController.searchProducts(keyword, 1, 20);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).searchProducts(keyword, 1, 20);
    }

    @Test
    void testGetProductsByCategory_Success() {
        // Given
        String category = "电子产品";
        List<Product> categoryProducts = Arrays.asList(testProduct);
        when(productService.getProductsByCategory(category, 1, 20)).thenReturn(categoryProducts);

        // When
        ResponseEntity<?> response = productController.getProductsByCategory(category, 1, 20);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).getProductsByCategory(category, 1, 20);
    }

    @Test
    void testGetAllProducts_InvalidParameters() {
        // Given
        when(productService.getProducts(-1, 0, null, null))
                .thenThrow(new BusinessException("参数错误"));

        // When
        ResponseEntity<?> response = productController.getAllProducts(-1, 0, null, null);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productService).getProducts(-1, 0, null, null);
    }

    @Test
    void testAddProduct_ValidationError() {
        // Given
        Product invalidProduct = new Product();
        // 缺少必填字段
        when(productService.createProduct(any(Product.class)))
                .thenThrow(new BusinessException("商品名称不能为空"));
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        ResponseEntity<?> response = productController.addProduct(invalidProduct);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productService).createProduct(any(Product.class));
    }
}

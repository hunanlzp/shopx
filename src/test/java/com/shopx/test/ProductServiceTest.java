package com.shopx.test;

import com.shopx.entity.Product;
import com.shopx.service.ProductService;
import com.shopx.service.impl.ProductServiceImpl;
import com.shopx.mapper.ProductMapper;
import com.shopx.cache.CacheManager;
import com.shopx.util.SaTokenUtil;
import com.shopx.validation.ValidationUtils;
import com.shopx.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 商品服务测试类
 */
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private SaTokenUtil saTokenUtil;

    @InjectMocks
    private ProductServiceImpl productService;

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
    void testGetProductById_Success() {
        // Given
        Long productId = 1L;
        when(productMapper.selectById(productId)).thenReturn(testProduct);

        // When
        Product result = productService.getProductById(productId);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getPrice(), result.getPrice());
        verify(productMapper).selectById(productId);
    }

    @Test
    void testGetProductById_NotFound() {
        // Given
        Long productId = 999L;
        when(productMapper.selectById(productId)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            productService.getProductById(productId);
        });
        verify(productMapper).selectById(productId);
    }

    @Test
    void testCreateProduct_Success() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("新商品");
        newProduct.setDescription("新商品描述");
        newProduct.setPrice(new BigDecimal("199.99"));
        newProduct.setCategory("服装");
        newProduct.setStock(50);
        newProduct.setStatus("ACTIVE");

        when(productMapper.insert(any(Product.class))).thenReturn(1);
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        Product result = productService.createProduct(newProduct);

        // Then
        assertNotNull(result);
        assertEquals(newProduct.getName(), result.getName());
        assertEquals(newProduct.getPrice(), result.getPrice());
        verify(productMapper).insert(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        Long productId = 1L;
        Product updateProduct = new Product();
        updateProduct.setName("更新后的商品");
        updateProduct.setPrice(new BigDecimal("149.99"));

        when(productMapper.selectById(productId)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        Product result = productService.updateProduct(productId, updateProduct);

        // Then
        assertNotNull(result);
        assertEquals(updateProduct.getName(), result.getName());
        assertEquals(updateProduct.getPrice(), result.getPrice());
        verify(productMapper).selectById(productId);
        verify(productMapper).updateById(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Given
        Long productId = 999L;
        Product updateProduct = new Product();
        updateProduct.setName("更新后的商品");

        when(productMapper.selectById(productId)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            productService.updateProduct(productId, updateProduct);
        });
        verify(productMapper).selectById(productId);
        verify(productMapper, never()).updateById(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        Long productId = 1L;
        when(productMapper.selectById(productId)).thenReturn(testProduct);
        when(productMapper.deleteById(productId)).thenReturn(1);
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        productService.deleteProduct(productId);

        // Then
        verify(productMapper).selectById(productId);
        verify(productMapper).deleteById(productId);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Given
        Long productId = 999L;
        when(productMapper.selectById(productId)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            productService.deleteProduct(productId);
        });
        verify(productMapper).selectById(productId);
        verify(productMapper, never()).deleteById(productId);
    }

    @Test
    void testGetHotProducts_Success() {
        // Given
        List<Product> hotProducts = Arrays.asList(testProduct);
        when(productMapper.selectHotProducts(anyString(), anyInt())).thenReturn(hotProducts);

        // When
        List<Product> result = productService.getHotProducts(10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productMapper).selectHotProducts(anyString(), anyInt());
    }

    @Test
    void testSearchProducts_Success() {
        // Given
        String keyword = "测试";
        List<Product> searchResults = Arrays.asList(testProduct);
        when(productMapper.searchProducts(keyword)).thenReturn(searchResults);

        // When
        List<Product> result = productService.searchProducts(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productMapper).searchProducts(keyword);
    }

    @Test
    void testGetProductsByCategory_Success() {
        // Given
        String category = "电子产品";
        List<Product> categoryProducts = Arrays.asList(testProduct);
        when(productMapper.selectList(any())).thenReturn(categoryProducts);

        // When
        List<Product> result = productService.getProductsByCategory(category, 1, 20);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productMapper).selectList(any());
    }

    @Test
    void testUpdateStock_Success() {
        // Given
        Long productId = 1L;
        Integer newStock = 150;
        when(productMapper.selectById(productId)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        // When
        productService.updateStock(productId, newStock);

        // Then
        verify(productMapper).selectById(productId);
        verify(productMapper).updateById(any(Product.class));
    }

    @Test
    void testUpdateStock_NotFound() {
        // Given
        Long productId = 999L;
        Integer newStock = 150;
        when(productMapper.selectById(productId)).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            productService.updateStock(productId, newStock);
        });
        verify(productMapper).selectById(productId);
        verify(productMapper, never()).updateById(any(Product.class));
    }

    @Test
    void testBatchDeleteProducts_Success() {
        // Given
        List<Long> productIds = Arrays.asList(1L, 2L, 3L);
        when(productMapper.deleteBatchIds(productIds)).thenReturn(3);
        when(saTokenUtil.getCurrentUserId()).thenReturn(1L);

        // When
        productService.batchDeleteProducts(productIds);

        // Then
        verify(productMapper).deleteBatchIds(productIds);
    }

    @Test
    void testBatchDeleteProducts_EmptyList() {
        // Given
        List<Long> productIds = Arrays.asList();

        // When & Then
        assertThrows(BusinessException.class, () -> {
            productService.batchDeleteProducts(productIds);
        });
        verify(productMapper, never()).deleteBatchIds(any());
    }
}

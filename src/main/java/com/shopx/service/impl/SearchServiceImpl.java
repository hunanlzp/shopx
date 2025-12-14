package com.shopx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopx.entity.Product;
import com.shopx.entity.SavedFilter;
import com.shopx.entity.SearchHistory;
import com.shopx.mapper.ProductMapper;
import com.shopx.mapper.SavedFilterMapper;
import com.shopx.mapper.SearchHistoryMapper;
import com.shopx.service.SearchService;
import com.shopx.util.ResponseUtil;
import com.shopx.util.SaTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    
    @Autowired
    private SavedFilterMapper savedFilterMapper;
    
    @Override
    public ResponseUtil.PageResult<Product> advancedSearch(String keyword, Map<String, Object> filters, int page, int size) {
        log.info("高级搜索: keyword={}, filters={}, page={}, size={}", keyword, filters, page, size);
        
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("name", keyword)
                .or()
                .like("description", keyword)
                .or()
                .like("lifestyle_tags", keyword)
            );
        }
        
        // 价格区间筛选
        if (filters != null) {
            Object minPrice = filters.get("minPrice");
            Object maxPrice = filters.get("maxPrice");
            if (minPrice != null || maxPrice != null) {
                if (minPrice != null && maxPrice != null) {
                    queryWrapper.between("price", 
                        new BigDecimal(minPrice.toString()), 
                        new BigDecimal(maxPrice.toString()));
                } else if (minPrice != null) {
                    queryWrapper.ge("price", new BigDecimal(minPrice.toString()));
                } else if (maxPrice != null) {
                    queryWrapper.le("price", new BigDecimal(maxPrice.toString()));
                }
            }
            
            // 分类筛选
            Object category = filters.get("category");
            if (category != null && StringUtils.hasText(category.toString())) {
                queryWrapper.eq("category", category.toString());
            }
            
            // 库存状态筛选
            Object stockStatus = filters.get("stockStatus");
            if (stockStatus != null) {
                String status = stockStatus.toString();
                if ("inStock".equals(status)) {
                    queryWrapper.gt("stock", 0);
                } else if ("outOfStock".equals(status)) {
                    queryWrapper.eq("stock", 0);
                }
            }
            
            // 是否有3D预览
            Object has3dPreview = filters.get("has3dPreview");
            if (has3dPreview != null && Boolean.parseBoolean(has3dPreview.toString())) {
                queryWrapper.eq("has_3d_preview", true);
            }
            
            // 是否可回收
            Object isRecyclable = filters.get("isRecyclable");
            if (isRecyclable != null && Boolean.parseBoolean(isRecyclable.toString())) {
                queryWrapper.eq("is_recyclable", true);
            }
            
            // 排序
            Object sortBy = filters.get("sortBy");
            if (sortBy != null) {
                String sort = sortBy.toString();
                String order = filters.get("sortOrder") != null ? filters.get("sortOrder").toString() : "desc";
                if ("price".equals(sort)) {
                    if ("asc".equalsIgnoreCase(order)) {
                        queryWrapper.orderByAsc("price");
                    } else {
                        queryWrapper.orderByDesc("price");
                    }
                } else if ("popularity".equals(sort)) {
                    queryWrapper.orderByDesc("view_count");
                    queryWrapper.orderByDesc("like_count");
                } else if ("newest".equals(sort)) {
                    queryWrapper.orderByDesc("create_time");
                }
            }
        }
        
        // 只查询启用的商品
        queryWrapper.eq("enabled", true);
        
        // 如果没有指定排序，默认按创建时间倒序
        if (filters == null || filters.get("sortBy") == null) {
            queryWrapper.orderByDesc("create_time");
        }
        
        // 分页查询
        Page<Product> pageParam = new Page<>(page, size);
        Page<Product> result = productMapper.selectPage(pageParam, queryWrapper);
        
        // 保存搜索历史
        try {
            Long userId = SaTokenUtil.getCurrentUserId();
            String filterJson = filters != null ? convertFiltersToJson(filters) : null;
            saveSearchHistory(userId, keyword, "ADVANCED", filterJson, (int) result.getTotal());
        } catch (Exception e) {
            log.warn("保存搜索历史失败", e);
        }
        
        return ResponseUtil.PageResult.<Product>builder()
                .data(result.getRecords())
                .total(result.getTotal())
                .page(page)
                .size(size)
                .totalPages((int) result.getPages())
                .build();
    }
    
    @Override
    public List<String> getSearchSuggestions(String keyword, int limit) {
        log.info("获取搜索建议: keyword={}, limit={}", keyword, limit);
        
        if (!StringUtils.hasText(keyword) || keyword.length() < 2) {
            return Collections.emptyList();
        }
        
        // 从商品名称中提取建议
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT name")
                   .like("name", keyword)
                   .eq("enabled", true)
                   .last("LIMIT " + limit);
        
        List<Product> products = productMapper.selectList(queryWrapper);
        return products.stream()
                .map(Product::getName)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public void saveSearchHistory(Long userId, String keyword, String searchType, String filterConditions, Integer resultCount) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }
        
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setKeyword(keyword);
        history.setSearchType(searchType != null ? searchType : "BASIC");
        history.setFilterConditions(filterConditions);
        history.setResultCount(resultCount != null ? resultCount : 0);
        
        searchHistoryMapper.insert(history);
        
        // 限制每个用户的搜索历史数量（保留最近100条）
        if (userId != null) {
            QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .orderByDesc("create_time")
                       .last("LIMIT 100, 1");
            List<SearchHistory> oldHistories = searchHistoryMapper.selectList(queryWrapper);
            if (!oldHistories.isEmpty()) {
                Long oldestId = oldHistories.get(0).getId();
                QueryWrapper<SearchHistory> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq("user_id", userId)
                            .lt("id", oldestId);
                searchHistoryMapper.delete(deleteWrapper);
            }
        }
    }
    
    @Override
    public List<SearchHistory> getUserSearchHistory(Long userId, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
        QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time")
                   .last("LIMIT " + limit);
        
        return searchHistoryMapper.selectList(queryWrapper);
    }
    
    @Override
    public SavedFilter saveFilter(Long userId, String filterName, String filterConditions, Boolean isDefault) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        SavedFilter filter = new SavedFilter();
        filter.setUserId(userId);
        filter.setFilterName(filterName);
        filter.setFilterConditions(filterConditions);
        filter.setIsDefault(isDefault != null ? isDefault : false);
        
        // 如果设置为默认，取消其他默认筛选条件
        if (Boolean.TRUE.equals(isDefault)) {
            QueryWrapper<SavedFilter> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .eq("is_default", true);
            List<SavedFilter> defaultFilters = savedFilterMapper.selectList(queryWrapper);
            for (SavedFilter df : defaultFilters) {
                df.setIsDefault(false);
                savedFilterMapper.updateById(df);
            }
        }
        
        savedFilterMapper.insert(filter);
        return filter;
    }
    
    @Override
    public List<SavedFilter> getUserSavedFilters(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
        QueryWrapper<SavedFilter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("is_default")
                   .orderByDesc("update_time");
        
        return savedFilterMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean deleteSavedFilter(Long filterId, Long userId) {
        if (filterId == null || userId == null) {
            return false;
        }
        
        SavedFilter filter = savedFilterMapper.selectById(filterId);
        if (filter == null || !filter.getUserId().equals(userId)) {
            return false;
        }
        
        savedFilterMapper.deleteById(filterId);
        return true;
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


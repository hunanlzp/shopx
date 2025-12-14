package com.shopx.service;

import com.shopx.entity.Product;
import com.shopx.entity.SavedFilter;
import com.shopx.entity.SearchHistory;
import com.shopx.util.ResponseUtil;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 */
public interface SearchService {
    
    /**
     * 高级搜索
     * @param keyword 关键词
     * @param filters 筛选条件
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    ResponseUtil.PageResult<Product> advancedSearch(String keyword, Map<String, Object> filters, int page, int size);
    
    /**
     * 获取搜索建议（自动补全）
     * @param keyword 关键词前缀
     * @param limit 返回数量限制
     * @return 搜索建议列表
     */
    List<String> getSearchSuggestions(String keyword, int limit);
    
    /**
     * 保存搜索历史
     * @param userId 用户ID（可为空）
     * @param keyword 关键词
     * @param searchType 搜索类型
     * @param filterConditions 筛选条件
     * @param resultCount 结果数量
     */
    void saveSearchHistory(Long userId, String keyword, String searchType, String filterConditions, Integer resultCount);
    
    /**
     * 获取用户搜索历史
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 搜索历史列表
     */
    List<SearchHistory> getUserSearchHistory(Long userId, int limit);
    
    /**
     * 保存筛选条件
     * @param userId 用户ID
     * @param filterName 筛选条件名称
     * @param filterConditions 筛选条件（JSON）
     * @param isDefault 是否默认
     * @return 保存的筛选条件
     */
    SavedFilter saveFilter(Long userId, String filterName, String filterConditions, Boolean isDefault);
    
    /**
     * 获取用户保存的筛选条件
     * @param userId 用户ID
     * @return 筛选条件列表
     */
    List<SavedFilter> getUserSavedFilters(Long userId);
    
    /**
     * 删除保存的筛选条件
     * @param filterId 筛选条件ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteSavedFilter(Long filterId, Long userId);
}


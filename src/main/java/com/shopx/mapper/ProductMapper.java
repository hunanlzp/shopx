package com.shopx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopx.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品Mapper接口
 * 提供商品数据访问功能
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 分页查询商品（带条件）
     */
    IPage<Product> selectProductPage(Page<Product> page, 
                                   @Param("keyword") String keyword,
                                   @Param("category") String category,
                                   @Param("minPrice") Double minPrice,
                                   @Param("maxPrice") Double maxPrice,
                                   @Param("enabled") Boolean enabled);
    
    /**
     * 获取热门商品
     */
    @Select("SELECT * FROM t_product WHERE enabled = 1 ORDER BY view_count DESC, like_count DESC LIMIT #{limit}")
    List<Product> selectHotProducts(@Param("limit") int limit);
    
    /**
     * 获取推荐商品（基于用户偏好）
     */
    @Select("SELECT * FROM t_product WHERE enabled = 1 AND lifestyle_tags LIKE CONCAT('%', #{lifestyle}, '%') ORDER BY create_time DESC LIMIT #{limit}")
    List<Product> selectRecommendedProducts(@Param("lifestyle") String lifestyle, @Param("limit") int limit);
    
    /**
     * 更新商品浏览量
     */
    @Update("UPDATE t_product SET view_count = view_count + 1 WHERE id = #{id}")
    int updateViewCount(@Param("id") Long id);
    
    /**
     * 更新商品点赞数
     */
    @Update("UPDATE t_product SET like_count = like_count + #{increment} WHERE id = #{id}")
    int updateLikeCount(@Param("id") Long id, @Param("increment") int increment);
    
    /**
     * 更新商品分享数
     */
    @Update("UPDATE t_product SET share_count = share_count + #{increment} WHERE id = #{id}")
    int updateShareCount(@Param("id") Long id, @Param("increment") int increment);
    
    /**
     * 批量更新商品状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("enabled") Boolean enabled);
    
    /**
     * 获取商品统计信息
     */
    @Select("SELECT COUNT(*) as total, " +
            "COUNT(CASE WHEN enabled = 1 THEN 1 END) as active, " +
            "COUNT(CASE WHEN enabled = 0 THEN 1 END) as inactive, " +
            "AVG(price) as avgPrice, " +
            "SUM(stock) as totalStock " +
            "FROM t_product")
    Object getProductStatistics();
}